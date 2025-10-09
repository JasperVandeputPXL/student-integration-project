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

    @Inject
    ObjectMapper mapper;

    static final Logger LOG = Logger.getLogger(TicketPurchaseAPIRoute.class);

    @Override
    public void configure() throws IOException {

        onException(IllegalArgumentException.class)
            .handled(true)
            .transform().simple("invalid input: ${body}")
            .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400));

        InputStream avroSchemaIS = getClass().getResourceAsStream("/schema/schema-ticketPurchase.avsc");
        Schema schema = new Schema.Parser().parse(avroSchemaIS);

        // https://camel.apache.org/components/4.4.x/scheduler-component.html
        from("direct:purchaseTicket")
            .inputType(be.openint.pxltraining.generated.PurchaseRequest.class)
            .routeId(getClass().getSimpleName())
            .log("body of ticket purchase\n${body}")
            // https://camel.apache.org/components/4.4.x/log-component.html
            .process(exchange -> {
                //TODO
                UUID purchaseId = UUID.randomUUID();
                exchange.setProperty("purchaseId", purchaseId);
                PurchaseRequest purchaseRequest = exchange.getIn().getBody(PurchaseRequest.class);

                ObjectNode ticketPurchaseJson = mapper.createObjectNode();
                ticketPurchaseJson.put("purchaseId", purchaseId.toString());
                ticketPurchaseJson.put("userId", purchaseRequest.getUserId().toString());
                ticketPurchaseJson.put("ticketType", purchaseRequest.getTicketType().getValue());
                ticketPurchaseJson.put("quantity", purchaseRequest.getQuantity());
                ticketPurchaseJson.put("timestamp", Instant.now().getLong(ChronoField.INSTANT_SECONDS));
                // Deserialize the JSON string into an Avro GenericRecord
                Decoder decoder = DecoderFactory.get().jsonDecoder(schema, mapper.writeValueAsString(ticketPurchaseJson));
                DatumReader<GenericRecord> reader = new GenericDatumReader<>(schema);
                GenericRecord result = reader.read(null, decoder);

                LOG.infof("receiving ticket purchase request for userId %s", purchaseRequest.getUserId().toString());

                // Serialize the Avro GenericRecord to bytes
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Encoder encoder = EncoderFactory.get().jsonEncoder(schema, baos);
                DatumWriter<GenericRecord> writer = new GenericDatumWriter<>(schema);
                writer.write(result, encoder);
                encoder.flush();
                baos.close();

                exchange.getIn().setBody(baos.toByteArray());
            })
            .to("kafka:" + topicName + "?clientId=" + clientId + "&saslJaasConfig=" + saslJaasConfig)
            .process(exchange -> {
                String requestUrl = exchange.getIn().getHeader(Exchange.HTTP_URL, String.class);
                UUID purchaseId = exchange.getProperty("purchaseId", UUID.class);
                URI uri = URI.create(requestUrl);

                String basePath = uri.getScheme() + "://" + uri.getAuthority() + "/v1";
                String statusUrl = basePath + "/purchases/" + purchaseId;

                PurchaseAcceptedResponse acceptedResponse = new PurchaseAcceptedResponse();
                acceptedResponse.setPurchaseId(purchaseId);
                acceptedResponse.setStatusUrl(statusUrl);

                exchange.getIn().setBody(acceptedResponse);
            });
    }
}
