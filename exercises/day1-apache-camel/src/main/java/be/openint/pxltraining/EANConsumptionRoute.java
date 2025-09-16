package be.openint.pxltraining;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Exercise 1 start route
 */
@Component
public class EANConsumptionRoute extends RouteBuilder {

    @Value("${kafka.meter.consumption.info.topic}")
    private String topicName;

    @Value("${kafka.meter.consumption.info.client.id}")
    private String clientId;

    @Value("${kafka.meter.consumption.info.sasl-jaas-config}")
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
