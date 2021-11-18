package info.novatec.service;

import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Singleton
public class SeatService {

    private final Logger logger = LoggerFactory.getLogger(SeatService.class);
    public static final double AVAILABILITY_RATE = 0.15;
    private static final int MAX_SEATS_IN_ROW = 20;
    private static final int MAX_ROWS = 25; // a-z or a+25 = z

    public void reserveSeats(List<String> seats) {
        logSeatWithMessage(seats, "Seat reserved: {}");
    }

    public void releaseSeats(List<String> seats) {
        logSeatWithMessage(seats, "Seat released: {}");
    }

    public boolean seatsAvailable(List<String> seats) {
        return seats.stream().allMatch(seat -> seatAvailable());
    }

    public List<String> getAlternativeSeats(List<String> seats) {
        List<String> alternativeSeats = new ArrayList<>();
        final int seat = getRandomStartingSeat(seats.size());
        String row = getRandomRow();
        IntStream.range(seat, seat + seats.size()).forEach(i -> alternativeSeats.add(row + i));
        return alternativeSeats;
    }

    private int getRandomStartingSeat(int buffer) {
        return Math.max(1, new Random().nextInt(MAX_SEATS_IN_ROW - buffer));
    }

    private String getRandomRow() {
        char randomChar = (char) ('a' + new Random().nextInt(MAX_ROWS));
        return String.valueOf(randomChar).toUpperCase();
    }

    private void logSeatWithMessage(List<String> seats, String s) {
        seats.forEach(seat -> logger.info(s, seat));
    }

    private boolean seatAvailable() {
        return Math.random() > AVAILABILITY_RATE;
    }
}
