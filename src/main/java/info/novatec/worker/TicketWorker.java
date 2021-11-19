package info.novatec.worker;

import info.novatec.micronaut.zeebe.client.feature.ZeebeWorker;
import info.novatec.process.Variables;
import info.novatec.service.TicketService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Stefan Schultz
 *
 * worker to handle ticket related stuff
 */
@Singleton
public class TicketWorker {

    Logger logger = LoggerFactory.getLogger(TicketWorker.class);

    private final TicketService ticketService;

    public TicketWorker(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @ZeebeWorker(type = "generate-ticket")
    public void generateTicket(final JobClient client, final ActivatedJob job) {
        logger.info("generating ticket");
        String ticketId = ticketService.generateTicketId();
        Map<String, Object> variables = Variables.empty().withTicketId(ticketId).get();
        client.newCompleteCommand(job.getKey()).variables(variables).send().join();
    }

    @ZeebeWorker(type = "send-ticket")
    public void sendTicket(final JobClient client, final ActivatedJob job) {
        String ticket = Variables.getTicketCode(job);
        String qrCode = Variables.getQrCode(job);
        String userId = Variables.getUserId(job);
        List<String> seats = Variables.getSeats(job);
        String movieId = Variables.getMovieId(job);
        String message = generateMessage(userId, movieId, String.join(", ", seats), ticket, qrCode);
        logger.info("sending ticket {} to customer", ticket);
        client.newCompleteCommand(job.getKey()).send().join();
        logger.info(message);
    }

    private String generateMessage(String user, String movieId, String seats, String ticketId, String qrCode) {
        String now = new SimpleDateFormat("dd.MM.yy HH:mm:ss").format(new Date());
        String vorstellung = new SimpleDateFormat("dd.MM.yy").format(new Date());
        return String.format("Hello %s, " +
                        "Thanks for your reservation at %s.\n" +
                        "Here is your reservation at a glance:\n" +
                        "Movie: %s, %s 21:00 Uhr\n" +
                        "Reservation: %s\n" +
                        "Ticket id: %s (qr code in attachment)\n" +
                        "Have fun at the movies\n\n" +
                        "your Cinema Team!" +
                        "\n\n" +
                        "Attachment (1):\n" +
                        "Code: %s",
                user, now, movieId, vorstellung, seats, ticketId, qrCode);
    }
}