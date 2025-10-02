## deploy you application in the cloud

1. open git bash shell and go to the directory of the exercise solution: `cd /path/to/git/directory/exercises/day1-apache-camel/PXLExercice2025Solution`  
   From now do everything from that shell instance.
2. in the shell set you AWS VM DNS in a variable.  
   The DNS is the file exercises/day1-apache-camel/assets/student-vms.json next your student number. Copy it and put in a variable.  
   In this example replace "ec2-123-123-123-123.eu-central-1.compute.amazonaws.com" with the DNS name you have found:  
   `MY_AWS_DNS=ec2-123-123-123-123.eu-central-1.compute.amazonaws.com`
3. install java on your VM.  
   a. login on your remote VM. In git bash: `ssh -i ../assets/PXL-key.pem ec2-user@$MY_AWS_DNS`  
   b. now you are on the remote VM  
   c. Install java on the VM: `sudo dnf install java-21-amazon-corretto-devel -y`  
   d. run the `exit` command to return on you locale machine in the git bash shell  
4. Configure your Kafka topic logins in the application.properties file located at: exercises/day1-apache-camel/PXLExercice2025Solution/src/main/resources/application.properties  
   Update the property values using the credentials provided for your student number (replace XX with your actual student number).  
   The topic names must remain unchanged.  
   You’ll find your credentials in the directory: exercises/day3-event-automation/Assets/TopicCredentials/  
   Each file will follow this naming format:  
   - StudentXX_ticket.purchase.requested_prod.json
   - StudentXX_ticket.payment.status_cons.json
   - StudentXX_ticket.inventory.updated_cons.json
   ```properties
   # Purchase Requested Producer
   kafka.festival.purchases.client.id=<value of "id" from purchase.requested_prod.json>
   kafka.festival.purchases.sasl-jaas-config=org.apache.kafka.common.security.plain.PlainLoginModule required username="<username from purchase.requested_prod.json>" password="<password from purchase.requested_prod.json>";
   
   # Payment Status Consumer
   kafka.festival.purchases.status.client.id=<value of "id" from payment.status_cons.json>
   kafka.festival.purchases.status.sasl-jaas-config=org.apache.kafka.common.security.plain.PlainLoginModule required username="<username from payment.status_cons.json>" password="<password from payment.status_cons.json>";
   
   # Inventory Updated Consumer
   kafka.festival.purchases.inventory.client.id=<value of "id" from inventory.updated_cons.json>
   kafka.festival.purchases.inventory.sasl-jaas-config=org.apache.kafka.common.security.plain.PlainLoginModule required username="<username from inventory.updated_cons.json>" password="<password from inventory.updated_cons.json>";
   ```
5. build your application in one bit fat executable jar containing everything together.  
   Run: `mvn clean package -DskipTests -Dquarkus.package.jar.type=uber-jar`  
6. copy the resulting jar and the kafka pem to your VM:   
   `scp -i ../assets/PXL-key.pem target/pxl-training-exercise1-solution-1.0-SNAPSHOT-runner.jar src/main/resources/purchases.kafka.pem ec2-user@$MY_AWS_DNS:~`
7. run your application from the remote vm:  
   a. login on your remote VM. In git bash: `ssh -i ../assets/PXL-key.pem ec2-user@$MY_AWS_DNS`  
   b. now you are on the remote VM  
   c. run your application in the background: `nohup java -jar pxl-training-exercise1-solution-1.0-SNAPSHOT-runner.jar 2>&1 &`  
   d. You'll find a 'nohup.out' file in the same directory. It collects the output logs of your application.  
   f You can use it to follow what is going on by tailing it: `tail -f nohup.out`  
     To stop tailing the logs it hit: ctrl + c  
   g. you can exit the remote vm, the application will keep running: `exit`  
8. test your application. In postman, change localhost with http://[YOUR-VM-DNS]:8080  
   The dns is the value of the $MY_AWS_DNS variable. You can read the value with the command: `echo $MY_AWS_DNS` or  
   in the exercises/day1-apache-camel/assets/student-vms.json next your student number.  
   Send a request with a valid body. There is body example in src/test/resources/samples/ticketPurchaseBody.json.
9. optionally, if you want to stop your java application you have to kill it of login on the remote vm.  
   First log on your vm: `ssh -i ../assets/PXL-key.pem ec2-user@$MY_AWS_DNS`  
   Find the PID (process id). This is an example on a random VM:  
   ```shell
   [ec2-user@ip-172-31-21-86 ~]$ ps -ef | grep java
   ec2-user  474523  474498 98 10:50 pts/0    00:00:03 java -jar /home/ec2-user/pxl-training-base-1.0-SNAPSHOT-runner.jar
   ec2-user  474538  474498  0 10:50 pts/0    00:00:00 grep --color=auto java
   ```
   The PID here is 474523. Use it to kill your java process:
   ```shell
   kill -9 474523
   ```