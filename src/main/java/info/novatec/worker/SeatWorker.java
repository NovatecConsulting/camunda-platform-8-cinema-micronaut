package info.novatec.worker;

import info.novatec.model.Reservation;
import info.novatec.service.SeatService;
import info.novatec.process.ProcessVariables;
import info.novatec.service.TicketService;
import info.novatec.micronaut.zeebe.client.feature.ZeebeWorker;
import info.novatec.process.ProcessVariableHandler;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.micronaut.runtime.server.EmbeddedServer;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Singleton
public class SeatWorker {

    private final Logger logger = LoggerFactory.getLogger(SeatWorker.class);
    private final SeatService seatService;
    private final TicketService ticketService;
    private final int port;

    public SeatWorker(SeatService seatService, TicketService ticketService, EmbeddedServer embeddedServer) {
        this.seatService = seatService;
        this.ticketService = ticketService;
        this.port = embeddedServer.getPort();
    }

    @ZeebeWorker(type = "check-seats")
    public void checkSeats(final JobClient client, final ActivatedJob job) {
        logger.info("checking seat availability");
        List<String> seats = ProcessVariableHandler.getSeats(job);
        if (seats != null && !seats.isEmpty()) {
            boolean available = seatService.seatsAvailable(seats);
            Map<String, Boolean> variables = Collections.singletonMap(ProcessVariables.SEATS_AVAILABLE.getName(), available);
            client.newCompleteCommand(job.getKey()).variables(variables).send().join();
        } else {
            client.newFailCommand(job.getKey()).retries(0).errorMessage("no seats found").send();
        }
    }

    @ZeebeWorker(type = "reserve-seats")
    public void reserveSeats(final JobClient client, final ActivatedJob job) {
        logger.info("reserving seats");
        Reservation reservation = ProcessVariableHandler.getReservation(job);
        if (reservation != null) {
            seatService.reserveSeats(reservation.getSeats());
            long ticketPrice = ticketService.getTicketPrice(reservation);
            reservation.setPrice(ticketPrice);
            client.newCompleteCommand(job.getKey()).variables(reservation).send().join();
        } else {
            client.newFailCommand(job.getKey()).retries(0).errorMessage("no seats found").send().join();
        }
    }

    @ZeebeWorker(type = "alt-seats")
    public void alternativeSeats(final JobClient client, final ActivatedJob job) {
        logger.info("getting alternative seats");
        Reservation reservation = ProcessVariableHandler.getReservation(job);
        if (reservation != null) {
            List<String> alternativeSeats = seatService.getAlternativeSeats(reservation.getSeats());
            reservation.setSeats(alternativeSeats);
            offerAltSeats(alternativeSeats, reservation.getReservationId());
            client.newCompleteCommand(job.getKey()).variables(reservation).send().join();
        } else {
            client.newFailCommand(job.getKey()).retries(0).errorMessage("no seats found").send().join();
        }
    }

    @ZeebeWorker(type = "release-seats")
    public void releaseSeats(final JobClient client, final ActivatedJob job) {
        logger.info("releasing seats");
        Reservation reservation = ProcessVariableHandler.getReservation(job);
        if (reservation != null) {
            seatService.releaseSeats(reservation.getSeats());
            client.newCompleteCommand(job.getKey()).send().join();
        } else {
            client.newFailCommand(job.getKey()).retries(0).errorMessage("no seats found").send().join();
        }
    }

    public void offerAltSeats(List<String> seats, String reservationId) {
        logger.info("The seats you selected are not available. Alternative seats are {}", seats);
        logger.info("To accept these seats, click the following link: http://localhost:{}/offer/{}", port, reservationId);
    }
}
