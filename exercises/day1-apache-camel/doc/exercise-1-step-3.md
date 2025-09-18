# Exercise 1 step 3

## Define your Json Schema

The Json format of the request body is defined in the resources/schema/Festival_Ticket_Sales_API.json file.  
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
   .routeId(getClass().getSimpleName())
   .log("body of ticket purchase\n${body}")
   .to("json-validator:schema/Festival_Ticket_Sales_API.json")
   // https://camel.apache.org/components/4.4.x/log-component.html
   .to("kafka:" + topicName + "?clientId=" + clientId + "&saslJaasConfig=" + saslJaasConfig);
   ``` 
2. test that you are sending something to Kafa in an integration test using TestContainer framework.  
   Open the test class _TicketPurchaseAPIRouteITest_.  
   Verify that the producerTemplate.sendBody(...) is set to the same value as your from(...) in you business route _CamelRoute_.  
   It should be "direct:purchaseTicket" and thus you should write _producerTemplate.sendBody("direct:purchaseTicket")_ in your test class.    
   For your information, when a container is started, it choose a random port available to expose it on your machine.  
   In the _'runtimeConfiguration(...)'_ method, the server to connect is dynamically configured to the current local URI.

   Start your docker server. The integration test will use it.
   Run the integration test. If it succeeds, it means that:
   - a Kafka container was started
   - an event was sent on it on a specific topic
   - exactly one event was consumed from it from that specific topic
   - the consumed event content is exactly the same as what was sent

   Take some time to read the test class and the comments to understand how this is working.

## Define your Avro Schema

To create your Avro Schema from the avro definition:
1. In the TicketPurchaseAPIRoute class, in your Route configuration method, read you definition and load it.  
   Put this before `from("direct:purchaseTicket")`
   ```java
   ClassPathResource avroSchema = new ClassPathResource("schema/schema-ticketPurchase.avsc", this.getClass().getClassLoader());
   InputStream avroSchemaIS = avroSchema.getInputStream();
   Schema schema = new Schema.Parser().parse(avroSchemaIS);
   ```
2. Use the schema in your route to deserialize the request body and transform it in a binary Avro output ready for Kafka.  
   After `.to("json-validator:schema/Festival_Ticket_Sales_API.json")` add a processor that will handle that logic:  
   ```java
   .process(e -> {
		// Deserialize the JSON string into an Avro GenericRecord
		Decoder decoder = DecoderFactory.get().jsonDecoder(schema, e.getIn().getBody(String.class));
		DatumReader<GenericRecord> reader = new GenericDatumReader<>(schema);
		GenericRecord result = reader.read(null, decoder);

		log.info("receiving energy consumption for eanNumber " + result.get("eanNumber"));

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
   To help you to select the correct classes to import from Avro here is the list of the one you need:
   ```java
   import org.apache.avro.Schema;
   import org.apache.avro.generic.GenericDatumReader;
   import org.apache.avro.generic.GenericDatumWriter;
   import org.apache.avro.generic.GenericRecord;
   import org.apache.avro.io.*;
   ```
3. run the test TicketPurchaseAPIRouteITest and check that you see the log "receiving ticket purchase request for userId 3fa85f64-5717-4562-b3fc-2c963f66afa6".
4. Run it with 'quarkus dev'
5. create the topic on the dev ui with the name from your configuration
6. Send a POST request with a body conform to the OpenAPI specification otherwise you'll get an error because the input is invalid.
7. Check that you see the log "receiving ticket purchase request for userId 3fa85f64-5717-4562-b3fc-2c963f66afa6" with your id and that the ticket is in Kafka
   
    [to step 4](exercise-1-step-4) 