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
	
			