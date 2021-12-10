package info.novatec.worker;

import info.novatec.process.Variables;
import info.novatec.service.QRCodeService;
import org.assertj.core.api.Assertions;
import org.camunda.community.zeebe.testutils.stubs.ActivatedJobStub;
import org.camunda.community.zeebe.testutils.stubs.JobClientStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.camunda.community.zeebe.testutils.ZeebeWorkerAssertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QrWorkerTest {

    @Mock
    QRCodeService service;

    private QrWorker worker;
    private JobClientStub jobClient;
    private ActivatedJobStub activatedJob;

    @BeforeEach
    void setup() {
        worker = new QrWorker(service);
        jobClient = new JobClientStub();
        activatedJob = jobClient.createActivatedJob();
    }

    @Test
    void test_worker_completes_job_and_sets_it_as_var_when_qr_code_is_generated() throws Exception {
        // given
        when(service.generateQRCode(any())).thenReturn("myQrCodeBase64");

        // when
        worker.generateTicket(jobClient, activatedJob);

        // then
        assertThat(activatedJob).completed();
        assertThat(activatedJob.getOutputVariables())
                .containsExactly(
                        entry(Variables.VariableName.QR_CODE.getName(), "myQrCodeBase64")
                );
    }

    @Test
    void test_worker_fails_job_when_qr_code_generation_fails() throws Exception {
        // given
        when(service.generateQRCode(any())).thenThrow(new IOException("error generating qr code"));

        // when
        worker.generateTicket(jobClient, activatedJob);

        // then
        assertThat(activatedJob).failed()
                .hasErrorMessage("error generating qr code")
                .hasRetries(0);
    }

}