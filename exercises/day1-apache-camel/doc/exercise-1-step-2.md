# Exercise 1 step 2

## Send an event to a local test Kafka

1. In the TicketPurchaseAPIRoute class, change the producer of your route to one sending to kafka: https://camel.apache.org/components/4.4.x/kafka-component.html.  
   Overwrite the line `.to("log:be.openint.pxltraining");` with:  
   ```java
   ...
   .to("kafka:" + topicName + "?clientId=" + clientId + "&saslJaasConfig=" + saslJaasConfig);
   ```
   For you information for later steps, you configure the 'topicName' value in you configuration (resources/application.properties)  
   by setting the 'kafka.festival.purchases.topic' property value.
   In the same configuration you'll find the spring boot autoconfiguration ready for the use of a kafka client: _camel.component.kafka.brokers_.
   The value is the URL to connect to kafka.
   The other parameter of the kafka producer 'clientId' and 'saslJaasConfig' are already there for the next steps and will be explained then.  
   In the mean time change the value of the property _'kafka.meter.consumption.info.client.id'_ in the application.properties.
   Change the value _[YOUR_FIRST_NAME_HERE]_ with your firstname.

2. The current state of the route overwrites the message body to ">>>>>>>>> hello world! <<<<<<<<<<".  
   We want to keep the body we have received from the REST endpoint. Fix that by changing the line
   '.setBody(constant(">>>>>>>>> hello world! <<<<<<<<<<"))' of your route with a log to verify that you are passing inside it:  
   ```java
   .log("body of ticket purchase\n${body}")
   ```

3. Run the integration test 'TicketPurchaseAPIRouteITest' from the test sources to run the route.  
   That test is using the TestContainer technology to create a Kafka container and connect to it.  
   It will use the configuration in application.properties in the test resources in the src/test/resources directory.
   The test has to succeed and somewhere in the logs you should find you log "body of ticket purchase" with the body on the nex lines.

## Send an event to a local Kafka (optional but nice to run Kafka and play with your application locally)

Now that you know that you have managed to send an event on a Kafka Test container, lets send an event on local Kafka container.

1. start a local kafka container and expose his port on you host machine.
   In your git bash shell: _docker run -d -p 9092:9092 --name broker apache/kafka:latest_  
2. prepare a consumer in the container that will consume the events you are sending on Kafka.
   Continue in your shell with these commands to login the container and start the consumer.
   The queue name to use is define in the application.properties with the key: _kafka.energy.info.topic_.  
   Use that topic name in you consumer.
   - _docker exec -it broker sh_
   - _/opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic **ID_PRODUCE_READINGS**_
3. start you application
4. send a request on you API using Postman.
5. you should see the log of your route
6. you should see the content of your request printed on the command line by your consumer in your container
   
    [to step 3](exercise-1-step-3) 