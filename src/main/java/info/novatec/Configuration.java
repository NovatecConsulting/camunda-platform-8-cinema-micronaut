package info.novatec;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Context;

import java.util.Optional;

/**
 * @author Stefan Schultz
 *
 * configuration class with properties for the application
 */
@Context
@ConfigurationProperties("zeebe.cinema")
public interface Configuration {

    Optional<Boolean> getPaymentFailureActive();

}
