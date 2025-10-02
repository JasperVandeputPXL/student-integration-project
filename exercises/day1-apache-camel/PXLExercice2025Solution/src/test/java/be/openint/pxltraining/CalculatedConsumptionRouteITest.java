//package be.openint.pxltraining;
//
//import org.apache.avro.Schema;
//import org.apache.avro.util.RandomData;
//import org.apache.camel.CamelContext;
//import org.apache.camel.EndpointInject;
//import org.apache.camel.ProducerTemplate;
//import org.apache.camel.builder.AdviceWith;
//import org.apache.camel.builder.RouteBuilder;
//import org.apache.camel.component.mock.MockEndpoint;
//import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.testcontainers.containers.KafkaContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.utility.DockerImageName;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URISyntaxException;
//import java.net.URL;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.concurrent.TimeUnit;
//
///**
// * an integration test using a kafka container.
// * The "sendBody(...)" triggers the special route created to send events to the kafka topic.
// * The test checks that exactly 1 event was consumed from the topic and that the body is same as the original one sent.
// * the original ones.
// */
//@SpringBootTest
//@CamelSpringBootTest
//@EnableAutoConfiguration
//public class CalculatedConsumptionRouteITest {
//
//    @Autowired
//    private CamelContext context;
//
//    @Container
//    private static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"));
//
//    //a utility to send messages on a route
//    @Autowired
//    ProducerTemplate producerTemplate;
//
//    //a utility to mock a Camel consumer component
//    //explain how this works
//    @EndpointInject("mock:" + PriceAlertRoute.FROM)
//    private MockEndpoint priceAlertMockEndpoint;
//
//    @Value("${kafka.consumption.calculated.topic}")
//    private String topicName;
//
//    @DynamicPropertySource
//    public static void runtimeConfiguration(DynamicPropertyRegistry registry) {
//        kafka.start();
//        //dynamically configures the url of the bootstrap server for the Camel Kafka producer.
//        registry.add("camel.component.kafka.brokers", kafka::getBootstrapServers);
//    }
//
//    @Test
//    public void testKafkaProduced() throws Exception {
//
//        String kafkaTestEventProducer = "direct:sendPricesToKafka";
//
//        //Dynamically add a route to send events to the kafka topic from where the route under test will consume the events
//        context.addRoutes(new RouteBuilder() {
//            @Override
//            public void configure() throws Exception {
//                from(kafkaTestEventProducer)
//                        .log("unmarshalling ===")
//                        .to("kafka:" + topicName + "?seekTo=BEGINNING");
//            }
//        });
//
//        //replace the from(PriceAlertRoute.FROM) in the PriceAlertRoute with from(priceAlertMockEndpoint)
//        AdviceWith.adviceWith(context, CalculatedConsumptionRoute.class.getSimpleName(), a ->
//                a.weaveByToUri(PriceAlertRoute.FROM).replace().to(priceAlertMockEndpoint));
//
//        String priceAlertBody = readRequestBodyFileAsString();
//
//        //defining what is expected to happen on the mock
//        priceAlertMockEndpoint.expectedMessageCount(1);
//        priceAlertMockEndpoint.expectedBodiesReceived(priceAlertBody);
//
//        //send the event to the kafka topic
//        producerTemplate.sendBody(kafkaTestEventProducer, priceAlertBody);
//
//        //verifies that the expectation were fulfilled, waiting max 5 seconds to allow enough time for all the producer
//        //and consumer kafka route to be started and performing their work
//        priceAlertMockEndpoint.assertIsSatisfied(5000, TimeUnit.MILLISECONDS);
//    }
//
//    private String readRequestBodyFileAsString() throws IOException, URISyntaxException {
//        URL jsonPayloadFile = TicketPurchaseAPIRoute.class.getResource("/samples/priceCalculation.json");
//        return Files.readString(Paths.get(jsonPayloadFile.toURI()));
//    }
//
//    public static void main(String[] args) throws IOException {
//        ClassLoader classLoader = CalculatedConsumptionRoute.class.getClassLoader();
//        InputStream is = classLoader.getResourceAsStream("schema/PaymentStatusUpdate.avsc");
//        Schema schema = new Schema.Parser().parse(is);
//        System.out.println(new RandomData(schema, 1).iterator().next());
//    }
//}