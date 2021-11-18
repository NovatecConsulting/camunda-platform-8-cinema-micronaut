package info.novatec.model;

import io.micronaut.core.annotation.Introspected;

import java.util.List;

@Introspected
public class Reservation {

    private String reservationId;
    private List<String> seats;
    private long price;
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public List<String> getSeats() {
        return seats;
    }

    public void setSeats(List<String> seats) {
        this.seats = seats;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Seats " + String.join(", ", seats);
    }
}
