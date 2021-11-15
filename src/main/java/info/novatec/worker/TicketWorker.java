package info.novatec.worker;

import info.novatec.model.Ticket;
import info.novatec.service.TicketService;
import info.novatec.micronaut.zeebe.client.feature.ZeebeWorker;
import info.novatec.process.ProcessVariableHandler;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        Ticket ticket = ticketService.generateTickets(ProcessVariableHandler.getReservation(job));
        client.newCompleteCommand(job.getKey()).variables(ticket).send().join();
    }

    @ZeebeWorker(type = "send-ticket")
    public void sendTicket(final JobClient client, final ActivatedJob job) {
        Ticket ticket = ProcessVariableHandler.getTicket(job);
        logger.info("sending ticket {} to customer", ticket.getCode());
        client.newCompleteCommand(job.getKey()).send().join();
        logger.info(ticket.getInfo());
    }
}