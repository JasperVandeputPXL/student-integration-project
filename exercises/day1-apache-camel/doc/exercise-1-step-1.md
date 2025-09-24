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
For your information, some classes (PurchaseAcceptedResponse, PurchaseRequest, ...) will appear in red (=error) because they are not found. This is normal, they will be generated for the OpenApi specification file after the 1st compilation of the code.  
You can ignore those errors until you have compiled your code for the 1st time.

1. In the TicketPurchaseAPIRoute class, define a REST route with the REST DSL Contract first approach: https://camel.apache.org/manual/rest-dsl-openapi.html  
   Pass it the OpenApi filename (it is set to "Festival_Ticket_Sales_API.yaml" in the configuration) of the file containing the OpenApi specification.  
   Paste this a the begin of the existing `public void configure()` method:
   
   ```java
   rest()
      .openApi(openApiFilename).getOpenApi().setMissingOperation("ignore");
   ```
   
   Thanks to the contract first approach, Camel will expect to have a route with a specific "direct:" consumer name create by convention from the OpenAPI spec operationId.
2. Create the route that will receive the API request based on the operationId of the ticket purchase request in the OpenApi specification.  
   Replace the from("scheduler:...") with the expected route name based on the operationId 'purchaseTicket' of the purchase request in the specification:  
   ```java
   from("direct:purchaseTicket")
   ```

3. run the application ('quarkus dev' command from the directory of the application) and test it with postman.  
   In postman, create an HTTP POST request to http://localhost:8080/v1/tickets/:ticketId/purchase.  
   In the "Params" tab, set a value for the "Path Variables" "tickerId". 123 for example.
   Set the request header 'Content-Type' to 'application/json'.  
   Define a random Json body and send the request.  
   Example:
   ```json
   {
     "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
     "quantity": 1,
     "ticketType": "normal"
   }
   ```
   The result in Postman should be to receive the '>>>>>>>>> hello world! <<<<<<<<<<' text as a response.  
   
4. expose the OpenSpecs. Use the rest configuration to define an api context: https://github.com/apache/camel/blob/camel-4.2.x/components/camel-openapi-java/src/main/docs/openapi-java.adoc#using-openapi-in-rest-dsl.  
   `.apiContextPath("/api-doc")` exposes the specs at the "/api-doc" path, `.bindingMode(RestBindingMode.json)` converts Json bodies to they corresponding Java Bean (pojo) 
   Paste this a the begin of the existing `public void configure()` method:

   ```java
     restConfiguration()
       .apiContextPath("/api-doc")
       .bindingMode(RestBindingMode.json);
   ```
   Run your application an navigate to http://localhost:8080/api-doc to see the result.
   
    [to step 2](exercise-1-step-2) 