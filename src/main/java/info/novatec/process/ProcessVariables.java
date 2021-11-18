package info.novatec.process;

public enum ProcessVariables {

    SEATS_AVAILABLE("seatsAvailable"),
    TICKET_ID("ticketId"),
    QR("qrCode"),
    RESERVATION_ID("reservationId"),
    USER_ID("userId"),
    SEATS("seats"),
    MOVIE_ID("movieId"),
    PRICE("ticketPrice");

    private final String name;

    ProcessVariables(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
