package info.novatec.worker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;

import java.util.Map;

public class Worker {

    void completeJob(JobClient client, ActivatedJob job) {
        client.newCompleteCommand(job.getKey())
                .send()
                .exceptionally(throwable -> {
                    throw new RuntimeException("Could not complete job " + job, throwable);
                });
    }

    void completeJob(JobClient client, ActivatedJob job, Map<String, Object> variables) {
        client.newCompleteCommand(job.getKey())
                .variables(variables)
                .send()
                .exceptionally(throwable -> {
                    throw new RuntimeException("Could not complete job " + job, throwable);
                });
    }

    void failJob(JobClient client, ActivatedJob job, String errorMessage) {
        client.newFailCommand(job.getKey())
                .retries(0)
                .errorMessage(errorMessage)
                .send()
                .exceptionally(throwable -> {
                    throw new RuntimeException("Could not fail job " + job, throwable);
                });
    }

    void throwBusinessError(JobClient client, ActivatedJob job, String errorCode, String errorMessage) {
        client.newThrowErrorCommand(job.getKey())
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .send()
                .exceptionally(throwable -> {
                    throw new RuntimeException("Could not throw error command for job " + job, throwable);
                });
    }

}
