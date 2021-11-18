package info.novatec.process;

public enum Variables {

    SEATS_AVAILABLE("seatsAvailable"),
    TICKET_ID("ticketId"),
    QR("qrCode"),
    RESERVATION_ID("reservationId"),
    USER_ID("userId"),
    SEATS("seats"),
    MOVIE_ID("movieId"),
    PRICE("ticketPrice");

    private final String name;

    Variables(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
