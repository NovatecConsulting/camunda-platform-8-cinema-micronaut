package info.novatec.worker;

import info.novatec.micronaut.zeebe.client.feature.ZeebeWorker;
import info.novatec.process.Variables;
import info.novatec.service.SeatService;
import info.novatec.service.TicketService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.micronaut.runtime.server.EmbeddedServer;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @author Stefan Schultz
 *
 * worker to handle seat related stuff
 */
@Singleton
public class SeatWorker extends Worker {

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
        List<String> seats = Variables.getSeats(job);
        if (!seats.isEmpty()) {
            boolean available = seatService.seatsAvailable(seats);
            Map<String, Object> variables = Variables.empty().withSeatAvailable(available).get();
            completeJob(client, job, variables);
        } else {
            failJob(client, job, "no seats found");
        }
    }

    @ZeebeWorker(type = "reserve-seats")
    public void reserveSeats(final JobClient client, final ActivatedJob job) {
        logger.info("reserving seats");
        List<String> seats = Variables.getSeats(job);
        if (!seats.isEmpty()) {
            seatService.reserveSeats(seats);
            int ticketPrice = ticketService.calculateTicketPrice(seats);
            Map<String, Object> variables = Variables.empty().withTicketPrice(ticketPrice).get();
            completeJob(client, job, variables);
        } else {
            failJob(client, job, "no seats found");
        }
    }

    @ZeebeWorker(type = "alt-seats")
    public void alternativeSeats(final JobClient client, final ActivatedJob job) {
        logger.info("getting alternative seats");
        List<String> seats = Variables.getSeats(job);
        if (!seats.isEmpty()) {
            List<String> alternativeSeats = seatService.getAlternativeSeats(seats);
            offerAltSeats(alternativeSeats, Variables.getReservationId(job));
            Map<String, Object> variables = Variables.empty().withSeats(alternativeSeats).get();
            completeJob(client, job, variables);
        } else {
            failJob(client, job, "no seats found");
        }
    }

    @ZeebeWorker(type = "release-seats")
    public void releaseSeats(final JobClient client, final ActivatedJob job) {
        logger.info("releasing seats");
        List<String> seats = Variables.getSeats(job);
        if (!seats.isEmpty()) {
            seatService.releaseSeats(seats);
            completeJob(client, job);
        } else {
            failJob(client, job, "no seats found");
        }
    }

    public void offerAltSeats(List<String> seats, String reservationId) {
        logger.info("The seats you selected are not available. Alternative seats are {}", seats);
        logger.info("To accept these seats, click the following link: http://localhost:{}/offer/{}", port, reservationId);
    }
}
