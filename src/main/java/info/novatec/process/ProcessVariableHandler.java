package info.novatec.process;

import io.camunda.zeebe.client.api.response.ActivatedJob;

import java.util.*;
import java.util.stream.Collectors;

public class ProcessVariableHandler {

    private Map<String, Object> variables;

    public ProcessVariableHandler(ActivatedJob job) {
        this.variables = job.getVariablesAsMap();
    }

    public ProcessVariableHandler(Map<String, Object> variables) {
        this.variables = variables;
    }

    public ProcessVariableHandler() {
        this.variables = new HashMap<>();
    }

    public Map<String, Object> build() {
        return this.variables;
    }

    public static Integer getTicketPrice(ActivatedJob job) {
        return (Integer) job.getVariablesAsMap().get(ProcessVariables.PRICE.getName());
    }

    public static String getTicketCode(ActivatedJob job) {
        return (String) job.getVariablesAsMap().get(ProcessVariables.TICKET_ID.getName());
    }

    public static String getUserId(ActivatedJob job) {
        return (String) job.getVariablesAsMap().get(ProcessVariables.USER_ID.getName());
    }

    public static String getMovieId(ActivatedJob job) {
        return (String) job.getVariablesAsMap().get(ProcessVariables.MOVIE_ID.getName());
    }

    public static String getReservationId(ActivatedJob job) {
        return (String) job.getVariablesAsMap().get(ProcessVariables.RESERVATION_ID.getName());
    }

    public static List<String> getSeats(ActivatedJob job) {
        String seats = (String) job.getVariablesAsMap().get(ProcessVariables.SEATS.getName());
        if (seats != null) {
            return Arrays.asList(seats.split(","));
        } else {
            return new ArrayList<>();
        }
    }

    public static String getQrCode(ActivatedJob job) {
        return (String) job.getVariablesAsMap().get(ProcessVariables.QR.getName());
    }


    /*
    * setter with builder pattern
    */
    public ProcessVariableHandler withTicketPrice(int ticketPrice) {
        variables.put(ProcessVariables.PRICE.getName(), ticketPrice);
        return this;
    }

    public ProcessVariableHandler withSeats(List<String> alternativeSeats) {
        variables.put(ProcessVariables.SEATS.getName(), String.join(",", alternativeSeats));
        return this;
    }

    public ProcessVariableHandler withTicketId(String ticketId) {
        variables.put(ProcessVariables.TICKET_ID.getName(), ticketId);
        return this;
    }

    public ProcessVariableHandler withMovieId(String movieId) {
        variables.put(ProcessVariables.MOVIE_ID.getName(), movieId);
        return this;
    }

    public ProcessVariableHandler withUserId(String userId) {
        variables.put(ProcessVariables.USER_ID.getName(), userId);
        return this;
    }

    public ProcessVariableHandler withReservationId(String reservationId) {
        variables.put(ProcessVariables.RESERVATION_ID.getName(), reservationId);
        return this;
    }

    public ProcessVariableHandler withQrCode(String qrCode) {
        variables.put(ProcessVariables.QR.getName(), qrCode);
        return this;
    }
}
