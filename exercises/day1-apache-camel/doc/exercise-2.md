# Exercise 2

## Introduction

In this exercise you are going to poll on scheduled time the energy prices in Belgium and put it on a Kafka topic.
We will use the Elia Opendata API: https://help.opendatasoft.com/apis/ods-explore-v2/  

We will use the _Imbalance prices per minute (Near real-time)_ API: https://opendata.elia.be/explore/dataset/ods161/api/  

## Schedule the route every 60 seconds

1. Create new class with the name GetImbalancedPriceRoute next to your fist route.  
   The class has to extend the abstract RouteBuilder class from camel (org.apache.camel.builder.RouteBuilder).  
   You are required to implement the method configure.  
   Add the _@Component_ annotation on your class (org.springframework.stereotype.Component). Without that, your route will not be discovered by Camel.

2. We need to get the price real time. It is available every minute.  
   Let's start to configure the route in the GetImbalancedPriceRoute class with scheduler that will trigger it every minute.  
   In the configure method start to write your route:   
   ```java
   from("scheduler:checkImbalancedPrice?delay=60000")
   .setBody(constant(">>>>>>>>> hello world! <<<<<<<<<<"));
   ```
   
## Query the API

To query the API we will use the Camel ['rest' component](https://camel.apache.org/components/4.4.x/rest-component.htm)  

1. Configure the rest base URL to query Elias.  
   At the beginning of your confirgure method in your route class, add the folowwing configuration:  
   ```java
   restConfiguration()
       //the base url for the API calls
       .host("https://opendata.elia.be/api/explore/v2.1/catalog/datasets/ods161");   
   ``` 

2. query the Elia API with the 'rest' component with an HTTP GET on the _records_ sub path with the _limit=1_ query parameter.  
   The _limit=1_ limit the response to the lastest result.  
   Replace _setBody(...)_ and _to(...)_ parts of your route with: 
   ```
   //HTTP GET query on https://opendata.elia.be/api/explore/v2.1/catalog/datasets/ods161/records?limit=1
   .to("rest:get:records?limit=1")   
   ```   

3. the result is a Json array with one element. To extrat the only element of the array, we will use the _split_ integration pattern as we did in the exercise 1 step 3.  
   Add this after your _to("rest:...)_ element in your route:  
   ```java
   .split().jsonpath("$.results[*]", List.class)
       .marshal().json(JsonLibrary.Jackson)
       .log(">>>>>>>>>>>>> Imbalanced price date time ${jsonpath($.datetime)} <<<<<<<<<<<<<<<<<")
   ```

## Serialize the price with Avro

1. load the Avro schema. Add in the beginning of your configure method the loading of the schema:  
   ```java
   ClassPathResource avroSchema = new ClassPathResource("schema/schema-imbalancedPrice.avsc", this.getClass().getClassLoader());
   InputStream avroSchemaIS = avroSchema.getInputStream();
   Schema schema = new Schema.Parser().parse(avroSchemaIS);
   ```

2. serialize the price in binary fromat with Avro.
   In your route, after _log("${body}"), add the processor that will perform the seralization logic:  
   ```java
   .process(e -> {
       // Deserialize the JSON string into an Avro GenericRecord
       Decoder decoder = DecoderFactory.get().jsonDecoder(schema, e.getIn().getBody(String.class));
       DatumReader<GenericRecord> reader = new GenericDatumReader<>(schema);
       GenericRecord result = reader.read(null, decoder);
       
       log.info("receiving imbalanced price for date time " + result.get("datetime"));
       
       // Serialize the Avro GenericRecord to bytes
       ByteArrayOutputStream baos = new ByteArrayOutputStream();
       Encoder encoder = EncoderFactory.get().jsonEncoder(schema, baos);
       DatumWriter<GenericRecord> writer = new GenericDatumWriter<>(schema);
       writer.write(result, encoder);
       encoder.flush();
       baos.close();
       
       e.getIn().setBody(baos.toByteArray());
   })
   ```
## Send the latest price to the Kafka topic

1. configure the specific parts for this connection in the application.properties.  
   Ask your credentials for the topic ID_PRODUCE_PRICING.  
   Add the following entries and replace [YOUR-FIRSTNAME], [USER] and [PASSWORD].
   ```properties
   kafka.energy.imbalanced.price.topic=ID_PRODUCE_PRICING
   kafka.energy.imbalanced.price.client.id=[YOUR-FIRSTNAME]
   kafka.energy.imbalanced.price.sasl-jaas-config=org.apache.kafka.common.security.plain.PlainLoginModule required username="[USER]" password="[PASSWORD]";

   ```
   
2. In your GetImbalancedPriceRoute class add the fields to collect the topic name, the client id and the SALS JASS credentials configured in your application.properties:  
   ```java
    @Value("${kafka.energy.imbalanced.price.topic}")
    private String topicName;

    @Value("${kafka.energy.imbalanced.price.client.id}")
    private String clientId;

    @Value("${kafka.energy.imbalanced.price.sasl-jaas-config}")
    private String saslJaasConfig;
   ```
   
3. You cannot have 2 Kafka producers with the same configuration to 2 different topics.  
   You need another client id and for our Kafka setup on IBM, you need separate credentials.  
   At the end of your route, add the Kafka producer:  

   ```
   .to("kafka:" + topicName + "?clientId=[CLIENT_ID]2&saslJaasConfig=" + saslJaasConfig);
   ```

4. run your application locally to verify that it works correctly.  
   At this stage, it will send the price to the IBM Kafka already configured in the previous exercice.
   
## upload your jar to your cloud VM

Use the instructions in exercise 1 step 5 to copy and run your application on your VM in the cloud.
You should first kill the application currently running and delete the 'nohup.out' file containing the logs.
