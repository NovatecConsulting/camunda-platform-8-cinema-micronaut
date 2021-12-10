package info.novatec.worker;

import info.novatec.process.Variables;
import io.camunda.zeebe.client.api.ZeebeFuture;
import io.camunda.zeebe.client.api.command.CompleteJobCommandStep1;
import io.camunda.zeebe.client.api.command.FailJobCommandStep1;
import io.camunda.zeebe.client.api.command.ThrowErrorCommandStep1;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.CompleteJobResponse;
import io.camunda.zeebe.client.api.worker.JobClient;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.lenient;

public class WorkerTest {

    @Mock
    ActivatedJob activatedJob;

    @Mock
    public JobClient jobClient;

    @Mock
    public CompleteJobCommandStep1 completeJobCommandStep1;

    @Mock
    public FailJobCommandStep1 failJobCommandStep1;

    @Mock
    public ThrowErrorCommandStep1 throwErrorCommandStep1;

    @Mock
    public ZeebeFuture<CompleteJobResponse> completeResponseFuture;

    @BeforeEach
    void before() {
        mockJobClient();
        mockCompleteCommandStep1();
    }

    private void mockCompleteCommandStep1() {
        lenient().when(completeJobCommandStep1.variables(anyMap())).thenReturn(completeJobCommandStep1);
        lenient().when(completeJobCommandStep1.variables(anyString())).thenReturn(completeJobCommandStep1);
        lenient().when(completeJobCommandStep1.variables(any(InputStream.class))).thenReturn(completeJobCommandStep1);

        lenient().when(completeJobCommandStep1.send()).thenReturn(completeResponseFuture);
    }

    private void mockJobClient() {
        lenient().when(jobClient.newCompleteCommand(anyLong())).thenReturn(completeJobCommandStep1);
        lenient().when(jobClient.newFailCommand(anyLong())).thenReturn(failJobCommandStep1);
        lenient().when(jobClient.newThrowErrorCommand(anyLong())).thenReturn(throwErrorCommandStep1);
    }

    protected void then_complete_command_step_sent_and_exceptionally() {
        then(completeJobCommandStep1).should().send();
        then(completeResponseFuture).should().exceptionally(any());
    }

    protected void then_complete_command_step_called_with_single_variable(Variables.VariableName variableName, String value) {
        ArgumentCaptor<Map> argumentCaptor = ArgumentCaptor.forClass(HashMap.class);
        then(completeJobCommandStep1).should().variables(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().get(variableName.getName())).isEqualTo(value);
    }

    protected void then_complete_step_called_with_variables(Map<String, Object> variables) {
        ArgumentCaptor<Map> argumentCaptor = ArgumentCaptor.forClass(HashMap.class);
        then(completeJobCommandStep1).should().variables(argumentCaptor.capture());
        Map<String, Object> capturedVariables = argumentCaptor.getValue();
        assertThat(capturedVariables).containsAllEntriesOf(variables);
    }

    protected void then_new_complete_command_step_with_key(long jobKey) {
        then(jobClient).should().newCompleteCommand(jobKey);
    }

    protected void given_job_with_key(long key) {
        given(activatedJob.getKey()).willReturn(key);
    }

    protected void given_job_will_return_single_variable(Variables.VariableName variableName, String value) {
        given(activatedJob.getVariablesAsMap()).willReturn(Map.of(variableName.getName(), value));
    }

    protected void given_job_will_return_variables(Map<String, Object> variables) {
        given(activatedJob.getVariablesAsMap()).willReturn(variables);
    }
}
