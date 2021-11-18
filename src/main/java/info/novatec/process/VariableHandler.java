package info.novatec.process;

import io.camunda.zeebe.client.api.response.ActivatedJob;

import java.util.*;

public class VariableHandler {

    private final Map<String, Object> variables;

    public static VariableHandler empty() {
        return new VariableHandler(new HashMap<>());
    }

    public static VariableHandler of(Map<String, Object> variables) {
        return new VariableHandler(variables);
    }

    private VariableHandler(Map<String, Object> variables) {
        this.variables = variables;
    }

    public Map<String, Object> build() {
        return this.variables;
    }

    public static Integer getTicketPrice(ActivatedJob job) {
        return (Integer) job.getVariablesAsMap().get(Variables.PRICE.getName());
    }

    public static String getTicketCode(ActivatedJob job) {
        return (String) job.getVariablesAsMap().get(Variables.TICKET_ID.getName());
    }

    public static String getUserId(ActivatedJob job) {
        return (String) job.getVariablesAsMap().get(Variables.USER_ID.getName());
    }

    public static String getMovieId(ActivatedJob job) {
        return (String) job.getVariablesAsMap().get(Variables.MOVIE_ID.getName());
    }

    public static String getReservationId(ActivatedJob job) {
        return (String) job.getVariablesAsMap().get(Variables.RESERVATION_ID.getName());
    }

    public static List<String> getSeats(ActivatedJob job) {
        String seats = (String) job.getVariablesAsMap().get(Variables.SEATS.getName());
        if (seats != null) {
            return Arrays.asList(seats.split(","));
        } else {
            return new ArrayList<>();
        }
    }

    public static String getQrCode(ActivatedJob job) {
        return (String) job.getVariablesAsMap().get(Variables.QR.getName());
    }


    /*
    * setter with builder pattern
    */
    public VariableHandler withTicketPrice(int ticketPrice) {
        variables.put(Variables.PRICE.getName(), ticketPrice);
        return this;
    }

    public VariableHandler withSeats(List<String> alternativeSeats) {
        variables.put(Variables.SEATS.getName(), String.join(",", alternativeSeats));
        return this;
    }

    public VariableHandler withTicketId(String ticketId) {
        variables.put(Variables.TICKET_ID.getName(), ticketId);
        return this;
    }

    public VariableHandler withMovieId(String movieId) {
        variables.put(Variables.MOVIE_ID.getName(), movieId);
        return this;
    }

    public VariableHandler withUserId(String userId) {
        variables.put(Variables.USER_ID.getName(), userId);
        return this;
    }

    public VariableHandler withReservationId(String reservationId) {
        variables.put(Variables.RESERVATION_ID.getName(), reservationId);
        return this;
    }

    public VariableHandler withQrCode(String qrCode) {
        variables.put(Variables.QR.getName(), qrCode);
        return this;
    }
}
