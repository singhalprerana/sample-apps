
--------------------------------------
Build the project 
--------------------------------------

-- Run from sample-apps/http/servers/servlet/servlet2x folder

./gradlew clean build


--------------------------------------
Steps to run multi-JVM app:
--------------------------------------

Step 1: Run Downstream Jetty Server (update the jar and config file paths)

java -agentlib:jdwp=transport=dt_socket,server=y,address=7122,suspend=n -javaagent:/Users/prerana.singhal/traceable/repos/agent/javaagent/build/libs/javaagent-0.1.143-SNAPSHOT.jar=traceableConfigFile=/Users/prerana.singhal/traceable/sample-apps/resources/agent-configs/config.json,traceableServiceName=SampleServlet2xDownstream -jar "http/servers/servlet/servlet2x/build/libs/sample-jetty-server.jar" 7022 -1

Step 2: Run Upstream Jetty Server (update the jar and config file paths)

java -agentlib:jdwp=transport=dt_socket,server=y,address=7122,suspend=n -javaagent:/Users/prerana.singhal/traceable/repos/agent/javaagent/build/libs/javaagent-0.1.143-SNAPSHOT.jar=traceableConfigFile=/Users/prerana.singhal/traceable/sample-apps/resources/agent-configs/config.json,traceableServiceName=SampleServlet2xUpstream -jar "http/servers/servlet/servlet2x/build/libs/sample-grpc-server.jar" 7021 7022

Step 3: Build and Run the upstream client

-- Run from sample-apps/http/clients/okhttp folder

./gradlew clean build

java -jar "http/clients/okhttp/build/libs/sample-okhttp-client.jar" 7021


--------------------------------------
Steps to run single-JVM app:
--------------------------------------

-- Run the sample App with embedded client, upstream and downstream (update the jar and config file paths)

java -agentlib:jdwp=transport=dt_socket,server=y,address=7120,suspend=n -javaagent:/Users/prerana.singhal/traceable/repos/agent/javaagent/build/libs/javaagent-0.1.143-SNAPSHOT.jar=traceableConfigFile=/Users/prerana.singhal/traceable/sample-apps/resources/agent-configs/config.json,traceableServiceName=SampleServlet2xApp -jar "http/servers/servlet/servlet2x/build/libs/sample-servlet-app.jar" 7021 7022

