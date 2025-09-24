# Exercise 1 step 3

## Send the response back to the requestor

The Json format of the request response is defined in the openapi/Festival_Ticket_Sales_API.json file.  
We will use the PurchaseAcceptedResponse bean generated from the specification to create the response.  

1. In the TicketPurchaseAPIRoute class, at the end of your route, after _.to("kafka:" + ...)_ add:
   ```java
   .process(exchange -> {
       String requestUrl = exchange.getIn().getHeader(Exchange.HTTP_URL, String.class);
       URI uri = URI.create(requestUrl);
       String basePath = uri.getScheme() + "://" + uri.getAuthority() + "/v1";
       UUID purchaseId = exchange.getProperty("purchaseId", UUID.class);
       String statusUrl = basePath + "/purchases/" + purchaseId;
       PurchaseAcceptedResponse acceptedResponse = new PurchaseAcceptedResponse();
       acceptedResponse.setPurchaseId(purchaseId);
       acceptedResponse.setStatusUrl(statusUrl);
       exchange.getIn().setBody(acceptedResponse);
   });
   ``` 
   This code do :
   - build the status URL deriving it from the request URL scheme and dns (=authority)
   - populate a PurchaseAcceptedResponse with the information of the request just created
   - set it as the response = exchange body
   
2. test that you are receiving the response with postman after starting the application with quarkus dev.

    [to step 5](exercise-1-step-5) 