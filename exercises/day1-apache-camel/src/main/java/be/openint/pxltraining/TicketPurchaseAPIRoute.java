package be.openint.pxltraining;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Exercise 1 start route
 */
@ApplicationScoped
public class TicketPurchaseAPIRoute extends RouteBuilder {

    @ConfigProperty(name = "kafka.festival.purchases.topic")
    private String topicName;

    @ConfigProperty(name = "kafka.festival.purchases.client.id")
    private String clientId;

    @ConfigProperty(name = "kafka.festival.purchases.sasl-jaas-config")
    private String saslJaasConfig;

    @Override
    public void configure() {
        // https://camel.apache.org/components/4.4.x/scheduler-component.html
        from("scheduler:runOnceForPXLTrainingBase?delay=1000&repeatCount=1")
            .routeId(getClass().getSimpleName())
            .setBody(constant(">>>>>>>>> hello world! <<<<<<<<<<"))
            // https://camel.apache.org/components/4.4.x/log-component.html
            .to("log:be.openint.pxltraining");
    }
}
