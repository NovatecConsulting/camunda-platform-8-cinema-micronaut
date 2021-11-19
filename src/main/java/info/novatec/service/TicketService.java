package info.novatec.service;

import jakarta.inject.Singleton;

import java.util.List;
import java.util.UUID;

/**
 * @author Stefan Schultz
 *
 * pseudo service to handle ticket prices
 */
@Singleton
public class TicketService {

    public static final int TICKET_PRICE = 12;

    public String generateTicketId() {
        return UUID.randomUUID().toString();
    }

    public int calculateTicketPrice(List<String> seats) {
        return seats.size() * TICKET_PRICE;
    }

}
