package info.novatec.service;

import info.novatec.model.Ticket;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.UUID;

@Singleton
public class TicketService {

    public static final int TICKET_PRICE = 12;

    public Ticket generateTickets() {
        return new Ticket(UUID.randomUUID().toString());
    }

    public int getTicketPrice(List<String> seats) {
        return seats.size() * TICKET_PRICE;
    }

}
