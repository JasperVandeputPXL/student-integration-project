package be.openint.pxltraining;

import be.openint.pxltraining.generated.PurchaseAcceptedResponse;
import be.openint.pxltraining.generated.PurchaseRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.*;
import org.apache.camel.Exchange;
import org.apache.camel.ValidationException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoField;
import java.util.UUID;

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

    @ConfigProperty(name = "ticket-purchase.openapi.filename")
    private String openApiFilename;

    @Inject
    ObjectMapper mapper;

    static final Logger LOG = Logger.getLogger(TicketPurchaseAPIRoute.class);

    @Override
    public void configure() {
        restConfiguration()
                .apiContextPath("/api-doc")
                .bindingMode(RestBindingMode.json);

        rest()
            .openApi(openApiFilename).getOpenApi().setMissingOperation("ignore");

        onException(IllegalArgumentException.class)
            .handled(true)
            .transform().simple("invalid input: ${body}")
            .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400));

        // https://camel.apache.org/components/4.4.x/scheduler-component.html
        from("direct:purchaseTicket")
            .routeId(getClass().getSimpleName())
            .log("body of ticket purchase\n${body}")
            // https://camel.apache.org/components/4.4.x/log-component.html
            .to("kafka:" + topicName + "?clientId=" + clientId + "&saslJaasConfig=" + saslJaasConfig);
    }
}
