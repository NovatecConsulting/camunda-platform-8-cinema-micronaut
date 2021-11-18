package info.novatec.worker;

import info.novatec.micronaut.zeebe.client.feature.ZeebeWorker;
import info.novatec.model.Ticket;
import info.novatec.process.Variables;
import info.novatec.service.TicketService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

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
        Ticket ticket = ticketService.generateTickets();
        Map<String, Object> variables = Variables.empty().withTicketId(ticket.getCode()).get();
        client.newCompleteCommand(job.getKey()).variables(variables).send().join();
    }

    @ZeebeWorker(type = "send-ticket")
    public void sendTicket(final JobClient client, final ActivatedJob job) {
        String ticket = Variables.getTicketCode(job);
        String qrCode = Variables.getQrCode(job);
        String userId = Variables.getUserId(job);
        List<String> seats = Variables.getSeats(job);
        String movieId = Variables.getMovieId(job);
        String message = Ticket.getMessage(userId, movieId, String.join(", ", seats), ticket, qrCode);
        logger.info("sending ticket {} to customer", ticket);
        client.newCompleteCommand(job.getKey()).send().join();
        logger.info(message);
    }
}