# Exercise 1 step 4

## send your event to the real kafka

The real kafka is using enterprise requirements to connect.  
To be allowed to send events on it, you have to authenticate and to encrypt the connection.

1. Ask for the broker url, credentials en pem certificate to get access to it:  
   Add that in the configuration of your application.properties:  
     
	 Replace [BROKER-URL], [YOUR-FIRSTNAME], [USER], [PASSWORD] and [PEM-FILENAME] with the corresponding values.  
	 Be careful: reuse the existing properties, _camel.component.kafka.brokers_, and _kafka.meter.consumption.info.sasl-jaas-config_ instead of defining it twice!  
     Indeed, those properties are already defined in your configuration and need to be adapted accordingly.  
     
	 ```properties
     #kafka producer client configuration
     camel.component.kafka.brokers=[BROKER-URL]
     camel.component.kafka.ssl-truststore-location=[PEM-FILENAME]
     camel.component.kafka.value-serializer=org.apache.kafka.common.serialization.ByteArraySerializer
     camel.component.kafka.security-protocol=SASL_SSL
     camel.component.kafka.sasl-mechanism=PLAIN
     camel.component.kafka.ssl-truststore-type=PEM
     
     kafka.festival.purchases.topic=ID_PRODUCE_PURCHASES
     kafka.festival.purchases.client.id=[YOUR_FIRST_NAME_HERE]
     kafka.festival.purchases.sasl-jaas-config=org.apache.kafka.common.security.plain.PlainLoginModule required username="[USER]" password="[PASSWORD]";
     ```
2. Run you application and send a valid request body to your API. Check (or ask to check if you don't have access) on the EEM dashboard if your event is present on the topic.  
   You'll find a valid dummy body your test resources of the project at src/test/resources/samples/ticketPurchaseBody.json.  
   This time, the 'quarkus dev' command will not start a Kafka instance for you. It does that only if not kafka configuration is set.
   
    [to step 6](exercise-1-step-6) 