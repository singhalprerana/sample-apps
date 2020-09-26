
--------------------------------------
Build the project 
--------------------------------------

-- Run from sample-apps/grpc folder

./gradlew clean build


--------------------------------------
Steps to run multi-JVM app:
--------------------------------------

Step 1: Run Downstream GRPC Server

java -jar "grpc/build/libs/sample-grpc-server.jar" 50052 -1

Step 2: Run Upstream GRPC Server

java -jar "grpc/build/libs/sample-grpc-server.jar" 50051 50052

Step 3: Run the upstream client

java -jar "grpc/build/libs/sample-grpc-client.jar" 50051


--------------------------------------
Steps to run single-JVM app:
--------------------------------------

-- Run the sample App with embedded client, upstream and downstream

java -jar "grpc/build/libs/sample-grpc-app.jar" 50051 50052

