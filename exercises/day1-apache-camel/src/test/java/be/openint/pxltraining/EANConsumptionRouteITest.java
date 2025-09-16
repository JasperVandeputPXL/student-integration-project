package be.openint.pxltraining;

import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * an integration test using a kafka container.
 * The "sendBody(...)" triggers the route and is expecting that the route consumer (from) has the uri: "direct:camelRoute".
 * The test checks that exactly one event was sent to the topic and that the body of the event is the same as the one sent.
 */
@SpringBootTest
@CamelSpringBootTest
@EnableAutoConfiguration
public class EANConsumptionRouteITest {

  @Autowired
  private CamelContext context;

  @Container
  private static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"));

  //a utility to send messages on a route
  @Autowired
  ProducerTemplate producerTemplate;

  //a utility to mock an Camel consumer component
  @EndpointInject("mock:consumeKafkaTopic")
  private MockEndpoint messageConsumer;

  @Value("${kafka.festival.purchases.topic}")
  private String topicName;

  @DynamicPropertySource
  public static void runtimeConfiguration(DynamicPropertyRegistry registry) {
    kafka.start();
    //dynamically configures the url of the bootstrap server for the Camel Kafka producer.
    registry.add("camel.component.kafka.brokers", kafka::getBootstrapServers);
  }

  @Test
  public void testKafkaProduced() throws Exception {

    //Dynamically add a route to consume the kafka topic where the event are sent
    context.addRoutes(new RouteBuilder() {
      @Override
      public void configure() throws Exception {
        //seekTo=BEGINNING ensure that all the events on the topic are read.
        //This ensures that if this route is started after that the event was sent it will still be consumed
        from("kafka:" + topicName + "?seekTo=BEGINNING")
                .log("unmarshalling ===")
                .to(messageConsumer);
      }
    });

    //defining what is expected to happen on the mock
    messageConsumer.expectedMessageCount(2);
    //send the event to the route to test
    producerTemplate.sendBody("direct:addEANConsumptions", readRequestBodyAsAFile());

    //verifies that the expectation were fulfilled
    messageConsumer.assertIsSatisfied();

    //verifying one by one that each element in the initial request body have been consumed.
    //Loading the JSON body in a JSON aware array.
    JSONArray jsonArray = new JSONArray(readRequestBodyAsAFile());
    //Loop over each message received and compare it JSON aware with the corresponding element in the original body
    int i = 0;
    for (Exchange exchange: messageConsumer.getExchanges()) {
      String body = exchange.getIn().getBody(String.class);
      JsonAssertions.assertThatJson(body).isEqualTo(jsonArray.get(i).toString());
      i++;
    }
  }

  private static String readRequestBodyAsAFile() throws IOException, URISyntaxException {
    URL jsonPayloadFile = TicketPurchaseAPIRoute.class.getResource("/samples/eanConsumptionsBody.json");
    return Files.readString(Paths.get(jsonPayloadFile.toURI()));
  }
}