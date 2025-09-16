# Exercise 1 step 3

## Define your Json Schema

The Json format of the request body is defined in the resources/schema/PXL_EANConsumptions_API.json file.  
We will use it to validate the imput.  

1. In the EANConsumptionRoute class, in your route, after the _.log(...)_ call the [json-validator](https://camel.apache.org/components/4.4.x/json-validator-component.html) component:  
   ```
   .to("json-validator:schema/PXL_EANConsumptions_API.json")   
   ``` 
   
## loop on each element of request body

The request body is an array of EAN Consumptions.  
We want to send 1 event per EAN Consumption to the Kafka topic.  
Camel offers the [split](https://camel.apache.org/components/4.4.x/eips/split-eip.html) integration pattern to fulfill this.  
Camel offers as well a Json Path language. It's a way to walk Json documents. You can use it in combination with split to itterate over each Json array element.  

1. In the EANConsumptionRoute class, in your route after the _json-validator_ added in the previous point, add the split:  
   ```java
   .split().jsonpath("$[*]", List.class )
   ```
2. after that you have to serialize (= marshal) each element again in order to have it a string.  
   Add the marshalling as the 1st step after the split:  
   ```java
   .marshal().json(JsonLibrary.Jackson)
   ```
3. eventually add some logging to understand what element of the array you are processing:
   ```java
   .log(">>>>>>>>>>>>> EANConsumption index ${exchangeProperty.CamelSplitIndex} <<<<<<<<<<<<<<<<<")
   .log("${body}")             
   ```

4. test that you are sending something to Kafa in an integration test using TestContainer framework.  
   Open the test class _EANConsumptionRouteITest_.  
   Verify that the producerTemplate.sendBody(...) is set to the same value as your from(...) in you business route _CamelRoute_.  
   For your information, when a container is started, it choose a random port available to expose it on your machine.  
   In the _'runtimeConfiguration(...)'_ method, the server to connect is dynamically configured to the current local URI.

   Start your docker server. The integration test will use it.
   Run the integration test. If it succeed, it means that:
   - a Kafka container was started
   - an event was sent on it on a specific topic
   - exactly one event was consumed from it from that specific topic
   - the consumed event content is exactly the same as what was sent

   Take some time to read the test class and the comments to understand how this is working.

## Define your Avro Schema

To create your Avro Schema from the avro definition:
1. In the EANConsumptionRoute class, in your Route configuration method, read you definition and load it:  
   ```java
   ClassPathResource avroSchema = new ClassPathResource("schema/schema-dailyEnergy.avsc", this.getClass().getClassLoader());
   InputStream avroSchemaIS = avroSchema.getInputStream();
   Schema schema = new Schema.Parser().parse(avroSchemaIS);
   ```
2. Use the schema in your route to deserialize the request body and transform it in a binary Avro output ready for Kafka.  
   Between the to(...) and the from of your route, add a processor that will handle that logic:  
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
3. configure the Kafka url and topic to use in your application.properties  
   ```properties
   camel.component.kafka.brokers=localhost:9092
   kafka.energy.info.topic=testtopic
   ```
4. define the topic name in your route and use it

5. run your application with the kafka in docker to check that you receive the event inside kafka.
   Becarefull, the body that you send has conform to the Avro definition otherwise you'll get an error because the input is invalid.
   
    [to step 4](exercise-1-step-4) 