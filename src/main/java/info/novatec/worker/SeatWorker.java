package info.novatec.worker;

import info.novatec.service.SeatService;
import info.novatec.process.Variables;
import info.novatec.service.TicketService;
import info.novatec.micronaut.zeebe.client.feature.ZeebeWorker;
import info.novatec.process.VariableHandler;
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
        List<String> seats = VariableHandler.getSeats(job);
        if (!seats.isEmpty()) {
            boolean available = seatService.seatsAvailable(seats);
            Map<String, Boolean> variables = Collections.singletonMap(Variables.SEATS_AVAILABLE.getName(), available);
            client.newCompleteCommand(job.getKey()).variables(variables).send().join();
        } else {
            client.newFailCommand(job.getKey()).retries(0).errorMessage("no seats found").send();
        }
    }

    @ZeebeWorker(type = "reserve-seats")
    public void reserveSeats(final JobClient client, final ActivatedJob job) {
        logger.info("reserving seats");
        List<String> seats = VariableHandler.getSeats(job);
        if (!seats.isEmpty()) {
            seatService.reserveSeats(seats);
            int ticketPrice = ticketService.getTicketPrice(seats);
            Map<String, Object> variables = VariableHandler.empty().withTicketPrice(ticketPrice).build();
            client.newCompleteCommand(job.getKey()).variables(variables).send().join();
        } else {
            client.newFailCommand(job.getKey()).retries(0).errorMessage("no seats found").send().join();
        }
    }

    @ZeebeWorker(type = "alt-seats")
    public void alternativeSeats(final JobClient client, final ActivatedJob job) {
        logger.info("getting alternative seats");
        List<String> seats = VariableHandler.getSeats(job);
        if (!seats.isEmpty()) {
            List<String> alternativeSeats = seatService.getAlternativeSeats(seats);
            offerAltSeats(alternativeSeats, VariableHandler.getReservationId(job));
            Map<String, Object> variables = VariableHandler.of(job.getVariablesAsMap()).withSeats(alternativeSeats).build();
            client.newCompleteCommand(job.getKey()).variables(variables).send().join();
        } else {
            client.newFailCommand(job.getKey()).retries(0).errorMessage("no seats found").send().join();
        }
    }

    @ZeebeWorker(type = "release-seats")
    public void releaseSeats(final JobClient client, final ActivatedJob job) {
        logger.info("releasing seats");
        List<String> seats = VariableHandler.getSeats(job);
        if (!seats.isEmpty()) {
            seatService.releaseSeats(seats);
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
