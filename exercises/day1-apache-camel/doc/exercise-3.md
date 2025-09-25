# Exercise 3

## Introduction

In this exercise you are going get ticket inventory update events from a Kafka topic and keep it in memory.
All the inventory updates will be reloaded at startup hence it is not required to persist them locally.

## Manage ticket inventory update events

Configure your kafka consumer with the credential received from the teacher in the configurations with prefix: "kafka.festival.purchases.inventory.".

Wat you need to achieve this exercise and that already provided to you is:
1. listening to ticket inventory update events from kafka topic
   This is done in the class TicketInventoryEventsRoute.
   It contains a route using the kafka component of camel listening to a topic.
   Each Event is deserialized based on the schema-inventoryUpdate.avsc avro schema.
   The event is then transformed in a TicketInventory bean.
2. store that event in a cache
   This is done in the class TicketInventoryEventsRoute. The TicketInventory bean is stored in the TicketInventoryCache.
   The TicketInventoryCache is holding a hash map that stores the ticket inventory update events.
3. provide the ticket inventory over an HTTP endpoint
   That is done in the TicketInventoryAPIRoute which expose a route listening the ticket inventory requests.
   When a request comes in, it checks if the requested ticket type exists in the cache and return it if found.
4. reload the cache with all the ticket inventory updates at every start
   This is done in the class TicketInventoryEventsRoute.
   The kafka consumer is configured with 'seekTo=BEGINNING'.
   That ensure that when the application is fully restarted (not hot restarted has in dev mode) the topic is read from the beginning again.
   If the topic is read from the beginning, all the ticket inventory updates will be received again leading to filling the cache again

**Play a bit with this part of the application to get used to it.
You can add events on the kafka topic of the dev service with name ID_INVENTORY (if you did not change the configuration).
Example of purchase status event are available in src/tst/resources/samples/inventoryTicketType.json**

## upload your jar to your cloud VM

Use the instructions in exercise 1 step 6 to copy and run your application on your VM in the cloud.
You should first kill the application currently running and delete the 'nohup.out' file containing the logs.
