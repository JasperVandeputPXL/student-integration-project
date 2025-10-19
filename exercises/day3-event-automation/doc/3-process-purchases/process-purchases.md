# Lets process our events
In this exercise, we will create a simple flink flow to process the purchases and confirm the payment status. This will be a simplified
solution. 

## EEM: create new topics
We will start by creating new topics in IBM Event Endpoint Management to allow us to consume and produce events by using Event Processing (EP)

	- Create a consuming topic (STXX-TICKET.PURCHASE.REQUESTED_CONS) with an approval policy for Flink
	- Request access and download credentials for EP
	
### TICKET.PAYMENT.STATUS.UPDATED

	- Create a new contract you will use for your topic: TICKET.PAYMENT.STATUS.UPDATED
	- result message must look like:	
		{
		  "purchaseId": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
		  "timestamp": 1678886400000,
		  "status": "SUCCESS"
		}
	- Now create a consuming and producing topic in EEM for the ES topic: TICKET.PAYMENT.STATUS.UPDATED (keep in mind the naming strategy)
	- consuming: 
		- policy = approval
		- create credentials for your camel solution to consume the events
	- producing: policy = approval + schema enforcement
		- create credentials for your future flink solution to produce new events
	
	
## ES: Real time processing

	- Login into 
[EP](https://ep-demo-ibm-ep-rt-tools.apps.itz-c8kjj5.infra01-lb.fra02.techzone.ibm.com)
	- Create a new Flow
	- Add an event source to the canvas
	- Add the bootstrap server (tip, EEM -> catalog)
	- Add your credentials as PLAIN
	- Select your topic
	- Message format = JSON
	- Click Run flow with historical data (if you have already pushed data from your camel application)
	- Stop when done testing
	
	- Add a transformation
	- you will need to add a property (to make the exercise easy -> hardcode a CAST to accepted
	- In the output, match the properties to keep to the contract of ticket.payment.status_prod
		(tip: you only need 3 properties)
	- Click Run flow with historical data (if you have already pushed data from your camel application)
	- Stop when done testing
		
	- Add an event destination 
	- Add the bootstrap server (tip, EEM -> catalog)
	- Add your credentials as PLAIN
	- Select your topic
	- Click Run flow with historical data (if you have already pushed data from your camel application)
	- close your flow but keep it running
	- Go to EP and check your incoming result
	
	
### Extra: schema enforcement
	
	
	- You can play with the schema enforcementin EEM and see how EP reacts