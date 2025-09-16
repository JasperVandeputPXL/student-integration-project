# Exercise 3

## Introduction

In this exercise we will read a kafka topic that contains results calculated on the backed based on the data sent on the other topics.  
Each calculated data received will then be sent as an alert.

## Start consuming from the calculated prices topic

1. Create new class with the name CalculatedConsumptionRoute next to your fist route.  
   The class has to extend the abstract RouteBuilder class from camel (org.apache.camel.builder.RouteBuilder).  
   You are required to implement the method configure.  
   Add the _@Component_ annotation on your class (org.springframework.stereotype.Component). Without that, your route will not be discovered by Camel.

2. configure the specific parts for this connection in the application.properties.  
   Ask your credentials for the topic ID_CONSUME_CALCULATED.  
   Add the following entries and replace [YOUR-FIRSTNAME], [USER] and [PASSWORD].
   ```properties
   kafka.consumption.calculated.topic=ID_CONSUME_CALCULATED
   kafka.consumption.calculated.client.id=[YOUR-FIRSTNAME]
   kafka.consumption.calculated.sasl-jaas-config=org.apache.kafka.common.security.plain.PlainLoginModule required username="[USER]" password="[PASSWORD]";
   ```
3. In your CalculatedConsumptionRoute class add the fields to collect the topic name, the client id and the SALS JASS credentials configured in your application.properties:
   ```java
    @Value("${kafka.consumption.calculated.topic}")
    private String topicName;

    @Value("${kafka.consumption.calculated.sasl-jaas-config}")
    private String saslJaasConfig;

    @Value("${kafka.consumption.calculated.client.id")
    private String clientId;
   ```

3. start to configure your route in the _configure()_ method.  
   Your route has to consume from a kafka topic. Write your consumer node from your route:  
   ```java
   from("kafka:" + topicName + "?clientId=" + clientId + "&saslJaasConfig=" + saslJaasConfig + "&seekTo=BEGINNING")
   .routeId(getClass().getSimpleName())
   .log("Received calculated consumption from topic '" + topicName + "' with body\n${body}");
   ```
   This kafka consumer has an extra configuration: _seekTo=BEGINNING_.  
   It as the consumer to read all the event back from the beginning of what was sent on the topic. Not only the new events arriving.  

## Add the parsing of the event body with the corresponding Avro schema

1. We have agreed on specific format for the events.  
This is enforced with an Avro schema. Here we are verifying that the event fulfill the schema requirements.  
We first need to load the Avro schema. Add this to the beginning of your configure() method:  
```java
ClassPathResource avroSchema = new ClassPathResource("schema/schema-calculatedPrices.avsc", this.getClass().getClassLoader());
InputStream avroSchemaIS = avroSchema.getInputStream();
Schema schema = new Schema.Parser().parse(avroSchemaIS);
```

2. We are doing that in a processor to have full control on how Avro will be used.  
The code is reading the schema, decode the input against it and returns it as a Json string.  
Add this at the end of your route:  
```java
.process(e -> {
    GenericDatumReader<GenericRecord> reader = new GenericDatumReader<>(schema);
    JsonDecoder jsonDecoder = DecoderFactory.get().jsonDecoder(schema, "");
    jsonDecoder.configure(e.getIn().getBody(InputStream.class));
    e.getIn().setBody(reader.read(null, jsonDecoder));
})
.log("Avro message read:\n${body}")
```

## Sent an alert from the consumed event

We are going to simulate. It means that we don't have means to send mails, chat messages or SMS available.  
Instead we will create a piece of code that can be extended in the future to really send an event out.  

1. Create a new route class with the name PriceAlertRoute next to your other routes.  
The class has to extend the abstract RouteBuilder class from camel (org.apache.camel.builder.RouteBuilder).  
You are required to implement the method configure.  
Add the _@Component_ annotation on your class (org.springframework.stereotype.Component). Without that, your route will not be discovered by Camel.

2. Create a field to old the producer URL or you consumer node fo test purpose:  
```java
public static final String FROM = "direct:priceAlert";
```

3. configure your route in the _configure()_ method as such:  
```java
from(FROM)
.routeId(getClass().getSimpleName())
//here you use any technology that you like instead of the log to warn the client about the price calculation
//if you know who and how to contact him
.log("${body}");
```

## use your Alert capability in your CalculatedConsumptionRoute route

Now that you have some code to alert on a pricing event, let's use it.  

1. In end the route you have defined in CalculatedConsumptionRoute with:  
```java
.to(PriceAlertRoute.FROM);
```

## Test your application with the Integration test ready for you

There is an integration test based on the same TestContainer as the other one.  
1. In the test class CalculatedConsumptionRouteITest uncomment:  
```java
//    @EndpointInject("mock:" + PriceAlertRoute.FROM)
```
and  
```java
//        AdviceWith.adviceWith(context, CalculatedConsumptionRoute.class.getSimpleName(), a ->
//                a.weaveByToUri(PriceAlertRoute.FROM).replace().to(priceAlertMockEndpoint));
```

2. run the integration test

## Run your application locally

run your application locally to verify that it works correctly.  
At this stage, it will send the price to the IBM Kafka already configured in the previous exercise.

## upload your jar to your cloud VM

Use the instructions in exercise 1 step 5 to copy and run your application on your VM in the cloud.
You should first kill the application currently running and delete the 'nohup.out' file containing the logs.