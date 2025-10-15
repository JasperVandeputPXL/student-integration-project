## Create the required objects for the PXL festival servcice
The next step is to create all the required topics for the festival service, secure them and expose these topics to the public world to be used.

### Create Topics
Keep in mind to use your student prefix when creating objects (ST01, ST02,...)
1. Create following topics using the same steps as you just created 'ST00-MY.FIRST.TOPIC':
	* ST00-TICKET.INVENTORY.UPDATED
	* ST00-TICKET.PAYMENT.STATUS.UPDATED
	* ST00-TICKET.PURCHASE.REQUESTED

### Connect your EEM with the ES cluster
When exposing topics to the outside (extern your team, domain, enterprise), you will want to do it secured and in a self-managed way. To do this, we will use Event Endpoint Management (EEM).
This is as sort of app store for data streaming applications.

	- Login to [IBM Event Endpoint Management] (https://eem-demo-mgr-ibm-eem-manager-tools.apps.itz-c8kjj5.infra01-lb.fra02.techzone.ibm.com)
	- Verify if you are already connected to a ES cluster 
	- Go to Manage -> clusters	
![ManageClusters](Tab_Clusters.PNG)
	- If the list contains a cluster, you are good to go and you can continue with the step "Add your topics to EEM"
	- If the list is empty, you will need to add the cluster to  your environment
	


1. In the Azure portal, go to your API Management instance and click on **APIs** in the left-hand menu.
2. Click on the **Festival Ticket Sales API xx** API.
3. Click on the **Settings** tab.
4. Change the value of the **Backend URL** to the URL of the Camel API created during day 1. The URL should resemble `http://<your-instance-name>-.eu-central-1.compute.amazonaws.com:8080/api/v1`. Replace `<your-instance-name>` with the values from the previous exercise in Camel. If you don't have a working API from day 1 then you can use the following mock API URL: `http://ec2-user@ec2-3-72-47-60.eu-central-1.compute.amazonaws.com:8080/v1/`.
5. Press **Save**.

  ![APIM Change backend](../../assets/images/apim-change-backend.png)

### Add backend API security
The backend API that was created during the first day should require a valid OAuth token to access the API. For this exercise, we will assume that the API requires a valid OAuth token in the `Authorization` header. We will configure Azure API Management to request a new OAuth token from the idenntity provider for each request and forward the token to the backend service in the `Authorization` header.

1. Click on the **Festival Ticket Sales API xx** API.
2. Click on the operation **All operations**.
3. Go to the **Inbound processing** section and open **policy code editor**.
 ![APIM policy editor](../../assets/images/apim-policy-editor.png)
 4. The XML editor will open. Add the following policy to the `<inbound>` section of the policy after the `<base />` line:
```xml
      <set-variable name="tokenEndpoint" value="https://login.microsoftonline.com/09385aae-477d-4c3c-bb3d-36f75a52cdc3/oauth2/v2.0/token" />
        <set-variable name="clientId" value="<replace with backend clientId>" />
        <set-variable name="clientSecret" value="<replace with backend secret>" />
        <set-variable name="scope" value="<replace with backend scope>" />
        <!-- Call Azure Active Directory to retriev a token -->
        <send-request mode="new" response-variable-name="tokenResponse" timeout="20" ignore-error="false">
            <set-url>@((string)context.Variables["tokenEndpoint"])</set-url>
            <set-method>POST</set-method>
            <set-header name="Content-Type" exists-action="override">
                <value>application/x-www-form-urlencoded</value>
            </set-header>
            <set-body>@{
            return "client_id=" + context.Variables["clientId"] + 
                   "&scope=" + context.Variables["scope"] + 
                   "&client_secret=" + context.Variables["clientSecret"] + 
                   "&grant_type=client_credentials";
        }</set-body>
        </send-request>
        <!-- Store statuscode in context variable -->
        <set-variable name="STSResponseCode" value="@(((IResponse)context.Variables["tokenResponse"]).StatusCode)" />
        <!-- Add token to Authorization header -->
        <set-header name="Authorization" exists-action="override">
            <value>@("Bearer " + ((IResponse)context.Variables["tokenResponse"]).Body.As&lt;JObject&gt;()["access_token"])</value>
        </set-header>
 ```
 5. Replace the placeholders with the values from the backend service. Request the values from the instructor.
 6. Press **Save**.


### Test the API
In order to test the API, we will use Postman to send a request to the API and view the response.

1. Open Postman.
2. Create a new request.
3. Set the request type to **POST**.
4. Enter the URL of the API. The URL should resemble `https://<your-apim-instance-name>.azure-api.net//api/festivalticketsales00/tickets/{ticketId}/purchase`.
5. Replace `<your-apim-instance-name>` with the name of your API Management instance and `{ticketId}` with a valid ticket ID (e.g., `1`).
6. Go to the **Headers** tab and add a new header with the key `Ocp-Apim-Subscription-Key` and the value of the subscription key you copied earlier.
7. Go to the **Body** tab and enter the following JSON object:

```json
{
  "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "quantity": 1,
  "ticketType": "normal"
}
```
8. Send the request.
9. You should receive a response with a status code of 200 OK and a JSON object containing the response from the backend service.
![APIM policy editor](../../assets/images/api-response.png)