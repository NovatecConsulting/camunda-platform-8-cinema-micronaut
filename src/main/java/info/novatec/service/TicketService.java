package info.novatec.service;

import info.novatec.model.Reservation;
import info.novatec.model.Ticket;
import jakarta.inject.Singleton;

import java.util.UUID;

@Singleton
public class TicketService {

    public static final long TICKET_PRICE = 12L;

    public Ticket generateTickets(Reservation reservation) {
        Ticket ticket = new Ticket(UUID.randomUUID().toString());
        ticket.setInfo(reservation.getUserId(), reservation.toString(), ticket.getCode());
        return ticket;
    }

    public long getTicketPrice(Reservation reservation) {
        return reservation.getSeats().size() * TICKET_PRICE;
    }

}
