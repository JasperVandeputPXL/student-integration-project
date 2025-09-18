# Exercise 1 step 1

## Get to know a camel route

1. clone the base project: git@github.com:I8C/student-integration-project.git  
2. the code lies under the exercises/day1-apache-camel directory in the main branch  
3. open the project subdirectory in Intellij  
4. run the application:  
	- in Git-bash, go to your application directory: cd /c/path/to/your/repository/**exercises/day1-apache-camel**  
	- run: quarkus dev  
      This will start your application in dev mode which comes with the handy dashboard that you can find from http://localhost:8080 in your browser.
      It comes with automatic reload of your code as well.
5. when the application runs, somewhere at the end of the logs in the console you should see a log entry with the text ">>>>>>>>> hello world! <<<<<<<<<<"  

## Expose a REST API

A Camel route is the configuration of Camel behaviour with Java.  
This means that once you are running your application and put a breakpoint in the route, when sending a message in it, the breakpoint will never be hit.  
Keep this in mind when you configure/program a route.  

1. In the TicketPurchaseAPIRoute class, define a REST route with the REST DSL Contract first approach: https://camel.apache.org/manual/rest-dsl-openapi.html  
   Pass it the "Festival_Ticket_Sales_API.yaml" file in the openApi configuration.  
   Paste this a the begin of the existing `public void configure()` method:
   
   ```java
   rest()
	.openApi("schema/Festival_Ticket_Sales_API.json");
   ```
   
   Thanks to the contract first approach, Camel will expect to have a route with a specific "direct:" consumer name create by convention from the OpenAPI spec operationId.  
   Run your application. Camel will complain that he cannot find a route with name "direct:...".  
   Copy that name.  
   
2. Create the route that will receive the API request.  
   Replace the from("scheduler:...") with the value of the direct route from the previous point. Example:  
   ```java
   from("direct:purchaseTicket")...
   ```

3. run the application (quarkus dev command from the directory of the application) and test it with postman.  
   In postman, create an HTTP POST request to http://localhost:8080/v1/tickets/:ticketId/purchase [this could be different].  
   To be sure about the URI to use check what URIs you find with Spring actuator mapping capability. In you browser go to http://localhost:8080/actuator/mappings.  
   Find the correct URI of your API in the response by searching on the "purchase" word in the page. 
   Set the request header 'Content-Type' to 'application/json'.  
   Define a random Json body and send the request.  
   The result in Postman should be to receive the '>>>>>>>>> hello world! <<<<<<<<<<' text as a response.  
   
4. expose the OpenSpecs. Use the rest configuration to define an api context: https://github.com/apache/camel/blob/camel-4.2.x/components/camel-openapi-java/src/main/docs/openapi-java.adoc#using-openapi-in-rest-dsl.  
   Paste this a the begin of the existing `public void configure()` method:

   ```java
   restConfiguration()
     .apiContextPath("/api-doc");
   ```
   Run your application an navigate to http://localhost:8080/api-doc to see the result.
   
    [to step 2](exercise-1-step-2) 