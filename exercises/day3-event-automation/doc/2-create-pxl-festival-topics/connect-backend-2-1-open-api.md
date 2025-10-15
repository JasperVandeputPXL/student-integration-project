## Create the required objects for the PXL festival servcice
The next step is to create all the required topics for the festival service, secure them and expose these topics to the public world to be used.

### Create Topics
Keep in mind to use your student prefix when creating objects (ST01, ST02,...)
1. Create following topics using the same steps as you just created 'ST00-MY.FIRST.TOPIC':
	* ST00-TICKET.INVENTORY.UPDATED
	* ST00-TICKET.PAYMENT.STATUS.UPDATED
	* ST00-TICKET.PURCHASE.REQUESTED

### Connect your EEM with the ES cluster
When exposing topics to the outside (extern your team, domain, enterprise), you will want to do it secured and in a 
self-managed way. To do this, we will use Event Endpoint Management (EEM). This is as sort of app store for data streaming applications.

	- Login to [IBM Event Endpoint Management] (https://eem-demo-mgr-ibm-eem-manager-tools.apps.itz-c8kjj5.infra01-lb.fra02.techzone.ibm.com)
	- Verify if you are already connected to a ES cluster 
	- Go to Manage -> clusters	
![ManageClusters](images/Tab_Clusters.PNG)

	- If the list contains a cluster, you are good to go and you can continue with the step "Add your topics to EEM"
	- If the list is empty, you will need to add the cluster to  your environment
	
#### Add a new Cluster
##### IBM Event Streams
	- Get the cluster address from IBM Event Streams (tip: paste it in notepadd as you will also need to get the credentials 
	-> these can only be copied once!!!)
![GetClusterInfo](images/Get_ClusterInfo.PNG)
		
	- Generate Credentials
![GenerateCredentails](images/GenerateCredentials.PNG)
		
	- Produce messages, consume messages and create topics and schemas
	- All Topics
	- All Consumer Groups
	- All transactional IDs
	- Save your credentials
![SaveCredentials](images/CopyCredentials.PNG)
		
		
##### IBM Event Endpoint Management
In EEM, click add cluster and provide the saved information

	- Click "Add cluster"
	- Give the cluster a logical name: st00-my-es-cluster
	- If all is configure correctly, you should see something like:
	
![ConfiguredCluster](images/ClusterConfigured.PNG)
	
### Add your topic to EEM
A topic in IBM Event Endpoint Management (EEM) has a different role and purpose as in IBM Event Streams(ES). 

	- In ES:
		* We refer to the core capability of event storage and messaging unit.
		* The main purpose is to provide a highly available and scalable log of events for producer to write 
		to and consumers to read from
	- In EEM:
		* We use topics to describe, catalog, secure and govern access to the kafka topics provided in ES
		* We enable self-service for developers
		* We provide AsyncAPI specs for developers
		* We allows an admin to define access, policies and plans
		
Also in EEM we make a difference between consuming events from a topic and producing events to a topic. Each type will always contain dedicated credentials!
Tip, store these credentials because you won't be able to request them again. When the credentials are lost, you will need to regenerate these credentials.