# Exercise 1 step 2

## Send an event to a local test Kafka

1. In the EANConsumptionRoute class, change the producer of your route to one sending to kafka: https://camel.apache.org/components/4.4.x/kafka-component.html.
   ```java
   ...
   .to("kafka:" + topicName + "?clientId=" + clientId + "&saslJaasConfig=" + saslJaasConfig);
   ```
   'topicName' is already available and its value is defined in you configuration (resources/application.properties).  
   In the same configuration you'll find the spring boot autoconfiguration ready for the use of a kafka client: _camel.component.kafka.brokers_.
   The value is the URL to connect to kafka.
   The other parameter of the kafka producer 'clientId' and 'saslJaasConfig' are already there for the next steps and will be explained then.  
   In the mean time change the value of the property _'kafka.meter.consumption.info.client.id'_ in the application.properties.
   Change the value _[YOUR_FIRST_NAME_HERE]_ with your firstname.

2. change the setBody(...) part of your route with a log to verify that you are passing inside it:  
   ```java
   .log(">>>>>>>>>>>>> in my route! <<<<<<<<<<<<<<<<<")
   ```

## Send an event to a local Kafka

Now that you know that you have managed to send an event on a Kafka Test container, lets send an event on local Kafka container.

1. start a local kafka container and expose his port on you host machine.
   In your git bash shell: _docker run -d -p 9092:9092 --name broker apache/kafka:latest_  
2. prepare a consumer in the container that will consume the events you are sending on Kafka.
   Continue in you shell with these commands to login the container and start the consumer.
   The queue name to use is define in the application.properties with the key: _kafka.energy.info.topic_.  
   Use that topic name in you consumer.
   - _docker exec -it broker sh_
   - _/opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic **ID_PRODUCE_READINGS**_
3. start you application
4. send a request on you API using Postman.
5. you should see the log of your route
6. you should see the content of your request printed on the command line by your consumer in your container
   
    [to step 3](exercise-1-step-3) 