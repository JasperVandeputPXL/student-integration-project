package be.openint.pxltraining;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Exercise 1 start route
 */
@Component
public class TicketPurchaseAPIRoute extends RouteBuilder {

    @Value("${kafka.festival.purchases.topic}")
    private String topicName;

    @Value("${kafka.festival.purchases.client.id}")
    private String clientId;

    @Value("${kafka.festival.purchases.sasl-jaas-config}")
    private String saslJaasConfig;

    @Override
    public void configure() {
        restConfiguration()
            .apiContextPath("/api-doc");

        rest()
            .openApi("schema/Festival_Ticket_Sales_API.yaml").getOpenApi().setMissingOperation("ignore");

        // https://camel.apache.org/components/4.4.x/scheduler-component.html
        from("direct:purchaseTicket")
            .routeId(getClass().getSimpleName())
            .setBody(constant(">>>>>>>>> hello world! <<<<<<<<<<"))
            // https://camel.apache.org/components/4.4.x/log-component.html
            .to("log:be.openint.pxltraining");
    }
}
