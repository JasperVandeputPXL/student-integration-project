# Update the inventory

In this lab, you will be challenged with an extra complex Flink mapping. You will take in account the new purchases and 
update the inventory topic with the latest state.

## Create initial inventory

	- Login to 
[IBM Event Streams](https://es-demo-ibm-es-ui-tools.apps.itz-c8kjj5.infra01-lb.fra02.techzone.ibm.com)
	
	- Create new credentials for your topic STXX-TICKET.INVENTORY.UPDATED so you can reuse the jar you downloaded in the first lab.
	- Download these credentials and update the jar
	- Define a new ticket type and provide an initial inventory.
		Example given: [VIP_TICKET : 100],  [STANDARD_TICKET : 1000]
	- Use the app to publish your new inventory (Keep in mind, the app does not do event validation!! Incorrect events will be pushed!)
	
## Create the required credentials
	- Login to 
[IBM Event Endpoint Management](https://eem-demo-mgr-ibm-eem-manager-tools.apps.itz-c8kjj5.infra01-lb.fra02.techzone.ibm.com)
	
	- Create consuming and producting credentials for your topic STXX-TICKET.INVENTORY.UPDATED
		Tip: keep in mind the naming strategy
	- Provide policies (approval, schema enforcement)
	- Download your credentials
	- Know which one is needed by Flink and which by Camel
	
## Create a Flink integration 

	- Login into 
[EP](https://ep-demo-ibm-ep-rt-tools.apps.itz-c8kjj5.infra01-lb.fra02.techzone.ibm.com)

	- Combine the your topics  STXX-TICKET.INVENTORY.UPDATED and STXX-TICKET.PURCHASE.REQUESTED into one flow
	- You will need to combine the streams by using a join 
		- Tip: only use the latest version of the correct event on the correct topic
		- Tip: do plenty of real-time testing before producing data to your new destination topic!
	- Map the results so it matches the contract of the destination topic
	- Check your results in EP and in ES
	
## Update the camel integration so it can provide inventory updates to the API consumer

	
