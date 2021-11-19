package info.novatec.service;

import info.novatec.process.Variables;
import io.camunda.zeebe.client.ZeebeClient;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

/**
 * @author Stefan Schultz
 *
 * rest controller to start instances and interact with service tasks
 */
@Controller
public class CinemaController {

    private final Logger logger = LoggerFactory.getLogger(CinemaController.class);
    public static final String VERIFIED_MESSAGE = "seatsVerifiedByCustomer";
    private final ZeebeClient zeebeClient;

    public CinemaController(ZeebeClient zeebeClient) {
        this.zeebeClient = zeebeClient;
    }

    @Post("/reservation/movie/{movieId}")
    public HttpResponse<String> reserveSeat(String movieId, @QueryValue String seats, @QueryValue String userId) {
        String reservationId = "res-" + UUID.randomUUID();
        Map<String, Object> variables = Variables.empty()
                .withSeats(Arrays.asList(seats.split(",")))
                .withMovieId(movieId)
                .withUserId(userId)
                .withReservationId(reservationId)
                .get();
        zeebeClient.newCreateInstanceCommand().bpmnProcessId("bpmn-cinema-ticket-reservation")
                .latestVersion()
                .variables(variables)
                .send()
                .exceptionally( throwable -> { throw new RuntimeException("Could not start new instance", throwable); });
        logger.info("Reservation issued: " + reservationId);
        return HttpResponse.created("Reservation issued: " + reservationId);
    }

    @Get("/offer/{id}")
    public HttpResponse<String> acceptOffer(@PathVariable String id) {
        zeebeClient.newPublishMessageCommand()
                .messageName(VERIFIED_MESSAGE)
                .correlationKey(id)
                .send()
                .exceptionally( throwable -> { throw new RuntimeException("Could not send message", throwable); });
        logger.info("The offer for reservation {} was accepted", id);
        return HttpResponse.accepted().body("Reservation change accepted");
    }

}
