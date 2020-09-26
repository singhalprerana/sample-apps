
--------------------------------------
Build the project 
--------------------------------------

-- Run from sample-apps/http/servers/servlet/servlet2x folder

./gradlew clean build


--------------------------------------
Steps to run multi-JVM app:
--------------------------------------

Step 1: Run Downstream Jetty Server (update the jar and config file paths)

java -jar "http/servers/servlet/servlet2x/build/libs/sample-jetty-server.jar" 7012 -1

Step 2: Run Upstream Jetty Server (update the jar and config file paths)

java -jar "http/servers/servlet/servlet2x/build/libs/sample-grpc-server.jar" 7011 7012

Step 3: Build and Run the upstream client

-- Run from sample-apps/http/clients/okhttp folder

./gradlew clean build

java -jar "http/clients/okhttp/build/libs/sample-okhttp-client.jar" 7011


--------------------------------------
Steps to run single-JVM app:
--------------------------------------

-- Run the sample App with embedded client, upstream and downstream (update the jar and config file paths)

java -jar "http/servers/servlet/servlet2x/build/libs/sample-servlet-app.jar" 7011 7012

