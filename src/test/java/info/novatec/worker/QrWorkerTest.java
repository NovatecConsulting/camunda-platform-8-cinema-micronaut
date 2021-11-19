package info.novatec.worker;

import info.novatec.process.Variables;
import info.novatec.service.QRCodeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class QrWorkerTest extends WorkerTest {

    @Mock
    QRCodeService service;

    @Test
    void qr_worker_generate_qr_completes_job_with_qr_variable() throws Exception {
        // given
        QrWorker worker = new QrWorker(service);
        String expectedString = "qrCodeBase64String";
        given(service.generateQRCode(anyString())).willReturn(expectedString);
        given_job_with_key(123456L);
        given_job_will_return_single_variable(Variables.VariableName.TICKET_ID, "ticketId");

        // when
        worker.generateTicket(jobClient, activatedJob);

        // then
        then_new_complete_command_step_with_key(123456L);
        then_complete_command_step_called_with_single_variable(Variables.VariableName.QR_CODE, expectedString);
        then_complete_command_step_sent_and_joined();
    }

}