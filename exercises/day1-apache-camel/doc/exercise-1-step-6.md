# Exercise 1 step 5

## deploy you application in the cloud

1. ask the DNS of your VM in the cloud, the private key to access it is available in the assets directory of this exercise
2. install java on your VM.
   In git bash: 
   a. Open a shell on your cloud VM: _ssh -i [PATH-TO-PEM-KEY] ec2-user@[YOUR-VM-DNS]_
   b. Install java: _sudo dnf install java-21-amazon-corretto-devel -y_
3. update the kafka path the the pem file to purchases.kafka.pem 
3. build your application with the option to create an executable jar in git bash.  
   From the root of your application run: _mvn clean package -DskipTests -Dquarkus.package.jar.type=uber-jar_  
4. copy the resulting jar to your VM:   
   ```shell
   scp -i [PATH-TO-PEM-KEY] target/pxl-training-base-1.0-SNAPSHOT-runner.jar ec2-user@[YOUR-VM-DNS]:~
   ```
5. copy your kafka pem certificate received in step 4 to your VM:  
   _scp path/to/kafka.pem i [PATH-TO-PEM-KEY] ec2-user@[YOUR-VM-DNS]:~/purchases.kafka.pem_
   Set the value of the configuration **camel.component.kafka.ssl-truststore-location** to purchases.kafka.pem. 
6. run your application:  
   ```shell
   nohup java -jar pxl-training-base-1.0-SNAPSHOT-runner.jar 2>&1 &  
   ```
   You'll find a 'nohup.out' file in the same directory. It collects the output logs of your application.
   You can use it to follow what is going on by tailing it:  
   ```shell
   tail -f nohup.out
   ```
   To stop tailing the logs it hit: ctrl + c
7. test your application. In postman, change localhost with http://[YOUR-VM-DNS]:8080. 
   Send a request with a valid body.
8. optionally, if you want to stop your java application you have to kill it.  
   Find the PID (process id). This is an example on a random VM:
   ```shell
   [ec2-user@ip-172-31-21-86 ~]$ ps -ef | grep java
   ec2-user  474523  474498 98 10:50 pts/0    00:00:03 java -jar /home/ec2-user/pxl-training-base-1.0-SNAPSHOT-runner.jar
   ec2-user  474538  474498  0 10:50 pts/0    00:00:00 grep --color=auto java
   ```
   The PID here is 474523. Use it to kill your java process:
   ```shell
   kill 474523
   ```

    [to step 7](exercise-1-step-7) 