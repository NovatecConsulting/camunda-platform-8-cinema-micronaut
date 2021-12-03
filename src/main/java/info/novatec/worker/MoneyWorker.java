package info.novatec.worker;

import info.novatec.micronaut.zeebe.client.feature.ZeebeWorker;
import info.novatec.process.Variables;
import info.novatec.service.PaymentService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Stefan Schultz
 * <p>
 * worker to handle money transactions
 */
@Singleton
public class MoneyWorker extends Worker {

    private final String ERROR_CODE = "Transaction_Error";
    private final Logger logger = LoggerFactory.getLogger(MoneyWorker.class);
    private final PaymentService paymentService;

    public MoneyWorker(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @ZeebeWorker(type = "get-money")
    public void getMoney(final JobClient client, final ActivatedJob job) throws InterruptedException {
        logger.info("withdrawing money");
        Thread.sleep(2000);
        Integer price = Variables.getTicketPrice(job);
        if (price != null) {
            try {
                paymentService.issueMoney(price, "DE12345678901234", "VOBA123456XX");
                completeJob(client, job);
            } catch (Exception e) {
                throwBusinessError(client, job, ERROR_CODE, e.getMessage());
            }
        } else {
            failJob(client, job, "no ticket price set");
        }
    }
}