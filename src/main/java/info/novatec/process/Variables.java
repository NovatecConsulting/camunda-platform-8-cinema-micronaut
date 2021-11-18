package info.novatec.process;

import io.camunda.zeebe.client.api.response.ActivatedJob;

import java.util.*;

/**
 * variable handler based on a builder pattern to easily create maps of variables
 * for process instances
 */
public class Variables {

    private final Map<String, Object> variables;

    private enum VariableName {

        SEATS_AVAILABLE("seatsAvailable"),
        TICKET_ID("ticketId"),
        QR("qrCode"),
        RESERVATION_ID("reservationId"),
        USER_ID("userId"),
        SEATS("seats"),
        MOVIE_ID("movieId"),
        PRICE("ticketPrice");

        private final String name;

        VariableName(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    /**
     * create an empty variable handler to be used to add new variables
     * @return a variable handler without any variables set
     */
    public static Variables empty() {
        return new Variables(new HashMap<>());
    }

    /**
     * create a handler with an existing set of variables
     * @param variables existing variables
     * @return an instance of the builder
     */
    public static Variables of(Map<String, Object> variables) {
        return new Variables(variables);
    }

    /**
     * private constructor to prevent instantiation for builder pattern
     * @param variables map with variables
     */
    private Variables(Map<String, Object> variables) {
        this.variables = variables;
    }

    /**
     * return the constructed variables for further use
     * @return all process variables
     */
    public Map<String, Object> get() {
        return this.variables;
    }

    public static Integer getTicketPrice(ActivatedJob job) {
        return (Integer) job.getVariablesAsMap().get(VariableName.PRICE.getName());
    }

    public static String getTicketCode(ActivatedJob job) {
        return (String) job.getVariablesAsMap().get(VariableName.TICKET_ID.getName());
    }

    public static String getUserId(ActivatedJob job) {
        return (String) job.getVariablesAsMap().get(VariableName.USER_ID.getName());
    }

    public static String getMovieId(ActivatedJob job) {
        return (String) job.getVariablesAsMap().get(VariableName.MOVIE_ID.getName());
    }

    public static String getReservationId(ActivatedJob job) {
        return (String) job.getVariablesAsMap().get(VariableName.RESERVATION_ID.getName());
    }

    public static List<String> getSeats(ActivatedJob job) {
        String seats = (String) job.getVariablesAsMap().get(VariableName.SEATS.getName());
        if (seats != null) {
            return Arrays.asList(seats.split(","));
        } else {
            return new ArrayList<>();
        }
    }

    public static String getQrCode(ActivatedJob job) {
        return (String) job.getVariablesAsMap().get(VariableName.QR.getName());
    }


    /*
    * setter with builder pattern
    */
    public Variables withTicketPrice(int ticketPrice) {
        variables.put(VariableName.PRICE.getName(), ticketPrice);
        return this;
    }

    public Variables withSeats(List<String> alternativeSeats) {
        variables.put(VariableName.SEATS.getName(), String.join(",", alternativeSeats));
        return this;
    }

    public Variables withTicketId(String ticketId) {
        variables.put(VariableName.TICKET_ID.getName(), ticketId);
        return this;
    }

    public Variables withMovieId(String movieId) {
        variables.put(VariableName.MOVIE_ID.getName(), movieId);
        return this;
    }

    public Variables withUserId(String userId) {
        variables.put(VariableName.USER_ID.getName(), userId);
        return this;
    }

    public Variables withReservationId(String reservationId) {
        variables.put(VariableName.RESERVATION_ID.getName(), reservationId);
        return this;
    }

    public Variables withQrCode(String qrCode) {
        variables.put(VariableName.QR.getName(), qrCode);
        return this;
    }

    public Variables withSeatAvailable(boolean available) {
        variables.put(VariableName.SEATS_AVAILABLE.getName(), available);
        return this;
    }
}
