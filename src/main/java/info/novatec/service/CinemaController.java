package info.novatec.service;

import info.novatec.process.ProcessMessage;
import info.novatec.process.Variables;
import io.camunda.zeebe.client.ZeebeClient;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@Controller
public class CinemaController {

    private final Logger logger = LoggerFactory.getLogger(CinemaController.class);
    private final ZeebeClient zeebeClient;

    public CinemaController(ZeebeClient zeebeClient) {
        this.zeebeClient = zeebeClient;
    }

    @Post("/reservation/movie/{movieId}")
    public HttpResponse<String> reserveSeat(String movieId, @QueryValue String seats, @QueryValue String userId) {
        String reservationId = "RESERVATION-" + UUID.randomUUID();
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
                .join();
        logger.info("Reservation issued: " + reservationId);
        return HttpResponse.accepted().body("Reservation issued: " + reservationId);
    }

    @Get("/offer/{id}")
    public HttpResponse<String> acceptOffer(@PathVariable String id) {
        zeebeClient.newPublishMessageCommand()
                .messageName(ProcessMessage.SEATS_VERIFIED.getName())
                .correlationKey(id)
                .send()
                .join();
        logger.info("The offer for reservation {} was accepted", id);
        return HttpResponse.ok("Reservation change accepted");
    }

}
