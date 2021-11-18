package info.novatec.worker;

import info.novatec.micronaut.zeebe.client.feature.ZeebeWorker;
import info.novatec.model.Ticket;
import info.novatec.process.ProcessVariableHandler;
import info.novatec.process.ProcessVariables;
import info.novatec.service.QRCodeService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class QrWorker {

    Logger logger = LoggerFactory.getLogger(QrWorker.class);

    private final QRCodeService qrCodeService;

    public QrWorker(QRCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }

    @ZeebeWorker(type = "generate-qr")
    public void generateTicket(final JobClient client, final ActivatedJob job) throws IOException {
        logger.info("generating qr code");
        String ticketCode = ProcessVariableHandler.getTicketCode(job);
        String qrCode = qrCodeService.generateQRCode(ticketCode);
        Map<String, Object> variables = new ProcessVariableHandler().withQrCode(qrCode).build();
        client.newCompleteCommand(job.getKey()).variables(variables).send().join();
    }
}