package info.novatec;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Context;

import java.util.Optional;

@Context
@ConfigurationProperties("zeebe.cinema")
public interface Configuration {

    Optional<Boolean> getPaymentFailureActive();

}
