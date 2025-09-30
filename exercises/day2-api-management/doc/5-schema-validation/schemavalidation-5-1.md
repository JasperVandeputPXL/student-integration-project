## Inbound JSON validation based on JSON schema

In this lab, we will implement JSON schema validation in Azure API Management using the `validate-content` policy. This policy allows you to validate the incoming request body against a predefined JSON schema.

### Add JSON schema validation policy
In this exercise, we will add a JSON schema validation policy to the `Initiates a new ticket purchase.` operation of the `Festival Ticket Sales API xx` API. The policy will validate the incoming request body against a predefined JSON schema. This allows us to ensure that the incoming request body is in the correct format and contains all the required fields. 

1. Click on the **Festival Ticket Sales API xx** API.
2. Click on the operation **Initiates a new ticket purchase.**.
3. Go to the **Inbound processing** section and open **policy code editor**.
4. The XML editor will open. Add the following policy to the `<inbound>` section of the policy after the `<base />` tag:
```xml
      <!-- Validate JSON content against schema -->
        <validate-content unspecified-content-type-action="detect" max-size="102400" size-exceeded-action="ignore" errors-variable-name="requestBodyValidation">
            <content type="application/json" validate-as="json" action="prevent" schema-id="CreateTicketRequestSchema" allow-additional-properties="false" />
        </validate-content>
```
5. Press **Save**.

The above policy will validate the incoming request body against the JSON schema with the ID `CreateTicketRequestSchema`. If the request body does not conform to the schema, the request will be rejected with a `400 Bad Request` response. For this excercise the schema is already created and associated with the API Management service instance. In a real-world scenario, you would need to create the schema first and then associate it with the API Management service instance. View the schema by navigating to the **Schemas** tab under the **APIs** section in the Azure portal or in the assets folder part of this lab on location `exercises/day2-api-management/assets/schema/CreateTicketRequestSchema.json`.

### Test the JSON schema validation policy
In order to test the JSON schema validation policy, we will use Postman to send requests to the API with different request bodies and view the response.

1. Open Postman.
2. Reuse the request you created in the previous exercise.
3. Modify the request body to test different scenarios:
   - Valid request body:
```json
{
  "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "quantity": 1,
  "ticketType": "normal"
}
```
   - Invalid request body (missing required field):
```json
{
  "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "quantity": 1
}
```
   - Invalid request body (wrong data type):
```json
{
  "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "quantity": "one",
  "ticketType": "normal"
}
```
4. Send the request and observe the response.