package info.novatec.service;

import info.novatec.Configuration;
import info.novatec.exception.PaymentException;
import jakarta.inject.Singleton;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Singleton
public class PaymentService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Optional<Boolean> paymentFailureActive;

    PaymentService(Configuration configuration) {
        this.paymentFailureActive = configuration.getPaymentFailureActive();
    }

    public void issueMoney(long ticketPrice, String iban, String bic) throws PaymentException {
        if (paymentFailureActive.isPresent() && paymentFailureActive.get() && Math.random() > 0.25) {
            logger.error("There was an issue with the payment");
            throw new PaymentException("Bank declined the transaction. Code: " + generateRandomBankReference());
        } else {
            logger.info("Getting {} Euro from IBAN {}, BIC {}", ticketPrice, iban, bic);
        }
    }

    private String generateRandomBankReference() {
        return RandomStringUtils.random(12, true, true).toUpperCase();
    }

    public void giveMoneyBack(int ticketPrice, String iban, String bic) {
        logger.info("Sending {} Euro to IBAN {}, BIC {}", ticketPrice, iban, bic);
    }
}
