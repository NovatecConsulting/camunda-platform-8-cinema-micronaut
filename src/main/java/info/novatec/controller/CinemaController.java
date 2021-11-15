package info.novatec.controller;

import info.novatec.model.Reservation;
import info.novatec.process.ProcessMessage;
import io.camunda.zeebe.client.ZeebeClient;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Controller
public class CinemaController {

    private final Logger logger = LoggerFactory.getLogger(CinemaController.class);
    private final ZeebeClient zeebeClient;

    public CinemaController(ZeebeClient zeebeClient) {
        this.zeebeClient = zeebeClient;
    }

    @Post("/reservation")
    public HttpResponse<String> reserveSeat(@RequestBean Reservation reservation) {
        String reservationId = "RESERVATION-" + UUID.randomUUID();
        reservation.setReservationId(reservationId);
        zeebeClient.newCreateInstanceCommand().bpmnProcessId("bpmn-cinema-ticket-reservation")
                .latestVersion()
                .variables(reservation)
                .send()
                .join();
        logger.info("Reservation issued: " + reservationId);
        return HttpResponse.accepted().body("Reservation issued: " + reservationId);
    }

    @Get("/offer/{id}")
    public HttpResponse<String> acceptOffer(@PathVariable String id) {
        zeebeClient.newPublishMessageCommand().messageName(ProcessMessage.SEATS_VERIFIED.getName()).correlationKey(id).send().join();
        logger.info("The offer for reservation {} was accepted", id);
        return HttpResponse.ok("Reservation change accepted");
    }

}
