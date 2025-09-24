# Exercise 1 step 3

## Define your Json Schema

The Json format of the request body is defined in the openapi/Festival_Ticket_Sales_API.json file.  
We will use it to validate the input.  


1. In the TicketPurchaseAPIRoute class, in your route, after the _.log(...)_ call the [json-validator](https://camel.apache.org/components/4.4.x/json-validator-component.html) component:
   Change
   ```java
   .routeId(getClass().getSimpleName())
   .log("body of ticket purchase\n${body}")
   // https://camel.apache.org/components/4.4.x/log-component.html
   .to("kafka:" + topicName + "?clientId=" + clientId + "&saslJaasConfig=" + saslJaasConfig);
   ```
   into
   ```java
   .inputType(be.openint.pxltraining.generated.PurchaseRequest.class)
   .routeId(getClass().getSimpleName())
   .log("body of ticket purchase\n${body}")
   .setProperty("BODY_POJO", simple("${body}"))
   .marshal().json(JsonLibrary.Jackson)
   .to("json-validator:classpath:" + openApiFilename)
   // https://camel.apache.org/components/4.4.x/log-component.html
   .to("kafka:" + topicName + "?clientId=" + clientId + "&saslJaasConfig=" + saslJaasConfig);
   ``` 
   This code do more than validating the json body, it:
   - deserialize the JSON body in a PurchaseRequest bean thanks to the JSON binding mode
   - save the PurchaseRequest object in a property called 'BODY_POJO' for later use
   - serialize (= marshal) the PurchaseRequest back to a JSON string for the json-validator component that only accepts JSON strings
   
2. test that you are sending something to Kafka using the quarkus dev service for kafka.
   Start your application with 'quarkus dev' and send a ticket purchase request to your application with postman.
   Check on the quarkus dev dashboard that the request was sent on the kafka topic at http://localhost:8080/q/dev-ui/quarkus-kafka-client/topics

## Define your Avro Schema

To create your Avro Schema from the avro definition:
1. In the TicketPurchaseAPIRoute class, in your Route configuration method, read you definition and load it.  
   Put this before `from("direct:purchaseTicket")`
   ```java
   InputStream avroSchemaIS = getClass().getResourceAsStream("/schema/schema-ticketPurchase.avsc");
   Schema schema = new Schema.Parser().parse(avroSchemaIS);
   ```
2. Use the schema in your route to serialize the request body and transform it in a binary Avro output ready for Kafka.  
   This piece of code, take the PurchaseRequest bean from the request and create a new JSON object from it and add a purchaseId and a timestamp to fulfill the Avro schema contract.  
   That JSON object is then serialized with the avro schema to be sent to kafka.
   After `.to("json-validator:classpath:" + openApiFilename)` add a processor that will handle that logic:  
   ```java
      .process(exchange -> {
          //TODO
          UUID purchaseId = UUID.randomUUID();
          exchange.setProperty("purchaseId", purchaseId);
          PurchaseRequest purchaseRequest = exchange.getProperty("BODY_POJO", PurchaseRequest.class);

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
   ```  
   To help you to select the correct classes to import from Avro here is the list of the one you need:
   ```java
   import org.apache.avro.Schema;
   import org.apache.avro.generic.GenericDatumReader;
   import org.apache.avro.generic.GenericDatumWriter;
   import org.apache.avro.generic.GenericRecord;
   import org.apache.avro.io.*;
   ```
3. Run the application with 'quarkus dev' to verify it's working with the avro schema serialization
4. create the topic on the dev ui with the name from your configuration
5. Send a POST request with a body conform to the OpenAPI specification otherwise you'll get an error because the input is invalid.
6. Check that you see the log "receiving ticket purchase request for userId 3fa85f64-5717-4562-b3fc-2c963f66afa6" with your id and that the ticket is in Kafka
   
    [to step 4](exercise-1-step-4) 