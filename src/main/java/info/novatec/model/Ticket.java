package info.novatec.model;

import io.micronaut.core.annotation.Introspected;

import java.text.SimpleDateFormat;
import java.util.Date;

@Introspected
public class Ticket {

    private final String code;

    public Ticket(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static String getMessage(String user, String movieId, String seats, String ticketId, String qrCode) {
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
