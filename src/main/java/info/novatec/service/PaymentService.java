package info.novatec.service;

import info.novatec.Configuration;
import jakarta.inject.Singleton;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class PaymentService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final boolean paymentFailureActive;

    PaymentService(Configuration configuration) {
        this.paymentFailureActive = configuration.getPaymentFailureActive().orElse(false);
    }

    public void issueMoney(long ticketPrice, String iban, String bic) {
        if (paymentFailureActive && Math.random() < 0.25) {
            logger.error("There was an issue with the payment");
            throw new RuntimeException("Bank declined the transaction. Code: " + generateRandomBankReference());
        } else {
            logger.info("Getting {} Euro from IBAN {}, BIC {}", ticketPrice, iban, bic);
        }
    }

    private String generateRandomBankReference() {
        return RandomStringUtils.random(12, true, true).toUpperCase();
    }
}
