# Exercise 2

## Introduction

In this exercise you are going get payment status events from a Kafka topic and keep it in memory.
All the payment status will be reloaded at startup hence it is not required to persist the status locally.

## Manage purchase status events

Configure your kafka consumer with the credential received from the teacher in the configurations with prefix: "kafka.festival.purchases.status.".

Wat you need to achieve this exercise and that already provided to you is:
1. listening to payment status events from a kafka topic
   This is done in the class TicketPurchaseStatusEventsRoute.
   It contains a route using the kafka component of camel listening to a topic.
   Each Event is deserialized based on the schema-paymentStatusUpdate.avsc avro schema.
   The event is then transformed in a PurchaseStatus bean. 
2. store that event in a cache
   This is done in the class TicketPurchaseStatusEventsRoute. The PurchaseStatus bean is stored in the TicketStatusCache.
   The TicketStatusCache is holding a hash map that stores the payment status events.
3. provide the payment status over an HTTP endpoint
   That is done in the TicketPaymentStatusAPIRoute which exposes a route listening the purchase status requests.
   When a request comes in, it checks it the requested payment id exists in the cache and return it if found.
4. reload the cache with all payment status at every start
   This is done in the class TicketPurchaseStatusEventsRoute.
   The kafka consumer is configured with 'seekTo=BEGINNING'.
   That ensure that when the application is fully restarted (not hot restarted has in dev mode) the topic is read from the beginning again.
   If the topic is read from the beginning, all the purchase statuses will be received again leading to filling the cache again 

**Play a bit with this part of the application to get used to it.
You can add events on the kafka topic of the dev service with name ID_PURCHASES_STATUS (if you did not change the configuration).
Example of purchase status event are available in src/tst/resources/samples/ticketPurchaseStatusSucceeded.json and ticketPurchaseStatusFailed.json**

## upload your jar to your cloud VM

Use the instructions in exercise 1 step 6 to copy and run your application on your VM in the cloud.
You should first kill the application currently running and delete the 'nohup.out' file containing the logs.
