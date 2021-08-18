--------------------------------------
Build the project 
--------------------------------------

-- Run from sample-apps/grpc folder

./gradlew clean build


--------------------------------------
Steps to run multi-JVM app:
--------------------------------------

Step 1: Run Downstream GRPC Server (update the jar and config file paths)

java -agentlib:jdwp=transport=dt_socket,server=y,address=50152,suspend=n -javaagent:/Users/prerana.singhal/traceable/repos/agent/javaagent/build/libs/javaagent-0.1.143-SNAPSHOT.jar=traceableConfigFile=/Users/prerana.singhal/traceable/sample-apps/resources/agent-configs/config.json,traceableServiceName=SampleGrpcDownstream -jar "grpc/build/libs/sample-grpc-server.jar" 50052 -1

Step 2: Run Upstream GRPC Server (update the jar and config file paths)

java -agentlib:jdwp=transport=dt_socket,server=y,address=50151,suspend=n -javaagent:/Users/prerana.singhal/traceable/repos/agent/javaagent/build/libs/javaagent-0.1.143-SNAPSHOT.jar=traceableConfigFile=/Users/prerana.singhal/traceable/sample-apps/resources/agent-configs/config.json,traceableServiceName=SampleGrpcUpstream -jar "grpc/build/libs/sample-grpc-server.jar" 50051 50052

Step 3: Run the upstream client

java -jar "grpc/build/libs/sample-grpc-client.jar" 50051


--------------------------------------
Steps to run single-JVM app:
--------------------------------------

-- Run the sample App with embedded client, upstream and downstream (update the jar and config file paths)

java -agentlib:jdwp=transport=dt_socket,server=y,address=50150,suspend=n -javaagent:/Users/prerana.singhal/traceable/repos/agent/javaagent/build/libs/javaagent-0.1.147-SNAPSHOT.jar=traceableConfigFile=/Users/prerana.singhal/traceable/sample-apps/resources/agent-configs/config.json,traceableServiceName=SampleGrpcApp -jar "grpc/build/libs/sample-grpc-app.jar" 

