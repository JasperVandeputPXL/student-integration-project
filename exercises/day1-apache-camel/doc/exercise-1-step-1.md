# Exercise 1 step 1

## Get to know a camel route

1. clone the base project: git@github.com:I8C/pxl-integrationproject.git  
2. take checkout the 'feature/camel' branch  
3. open the project in Intellij  
4. run the application:  
	- in Intellij, got to the App class and run it  
	- or in git bash, go to the root of your project and run: mvn spring-boot:run  
5. you should see a log written with:  
   Exchange[ExchangePattern: InOnly, BodyType: String, Body: >>>>>>>>> hello world! <<<<<<<<<<]  

## Expose a REST API

A Camel route is the configuration of Camel behaviour with Java.  
This means that once you are running your application and put a breakpoint in the route, when sending a message in it, th breakpoint will never be hit.  
Keep this in mind when you configure/program a route.  

1. In the EANConsumptionRoute class, define a REST route with the REST DSL Contract first approach: https://camel.apache.org/manual/rest-dsl-openapi.html  
   Pass it the "PXL_EANConsumptions_API.json" file in the openApi configuration:  
   
   ```java
   rest()
	.openApi("schema/PXL_EANConsumptions_API.json");
   ```
   
   Thanks to the contract first approach, Camel will expect to have a route with a specific "direct:" consumer name create by convention from the OpenAPI spec.  
   Run your application. Camel will complain that he cannot find a route with name "direct:...".  
   Copy that name.  
   
2. Create the route that will receive the API request.  
   Replace the from("scheduler:...") with the value of the direct route from the previous point. Example:  
   ```java
   from("direct:addEANConsumptions")...
   ```

3. run the application and test it with postman.  
   In postman, create an HTTP POST request to http://localhost:8080/api/v1/ean-consumptions [this could be different].  
   To be sure about the URI to use check what URIs you find with Spring actuoator mapping capibility. In you browser go to http://localhost:8080/actuator/mappings.  
   Find the correct URI of your API in the response. 
   Set the resuest header 'Content-Type' to 'application/json'.  
   Define a random Json body and send the request.  
   The result in Posman sould be to receive the 'hello world' text as a response.  
   
4. expose the OpenSpecs. Use the rest configuration to define an api context: https://github.com/apache/camel/blob/camel-4.2.x/components/camel-openapi-java/src/main/docs/openapi-java.adoc#using-openapi-in-rest-dsl   
   ```java
   restConfiguration()
     .apiContextPath("/api-doc");
   ```
   Run your application an navigate to http://localhost:8080/api-doc to see the result.
   
    [to step 2](exercise-1-step-2) 