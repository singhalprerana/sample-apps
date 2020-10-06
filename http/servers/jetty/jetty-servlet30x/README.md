
--------------------------------------
Build the project 
--------------------------------------

-- Run from sample-apps/http/servers/jetty/jetty-servlet30x folder

./gradlew clean build


--------------------------------------
Steps to run multi-JVM app:
--------------------------------------

Step 1: Run Downstream Jetty Server (update the jar and config file paths)

java -agentlib:jdwp=transport=dt_socket,server=y,address=6302,suspend=n -javaagent:/Users/prerana.singhal/traceable/repos/agent/javaagent/build/libs/javaagent-0.1.144-SNAPSHOT.jar=traceableConfigFile=/Users/prerana.singhal/traceable/sample-apps/resources/agent-configs/config.json,traceableServiceName=SampleJettyServlet30xDownstream -jar "http/servers/jetty/jetty-servlet30x/build/libs/sample-jetty-server.jar" 7302 -1

Step 2: Run Upstream Jetty Server (update the jar and config file paths)

java -agentlib:jdwp=transport=dt_socket,server=y,address=6301,suspend=n -javaagent:/Users/prerana.singhal/traceable/repos/agent/javaagent/build/libs/javaagent-0.1.144-SNAPSHOT.jar=traceableConfigFile=/Users/prerana.singhal/traceable/sample-apps/resources/agent-configs/config.json,traceableServiceName=SampleJettyServlet30xUpstream -jar "http/servers/jetty/jetty-servlet30x/build/libs/sample-jetty-server.jar" 7301 7302

Step 3: Build and Run the upstream client

-- Run from sample-apps/http/clients/okhttp folder

./gradlew clean build

java -jar "http/clients/okhttp/build/libs/sample-okhttp-client.jar" 7011


--------------------------------------
Steps to run single-JVM app:
--------------------------------------

-- Run the sample App with embedded client, upstream and downstream (update the jar and config file paths)

java -agentlib:jdwp=transport=dt_socket,server=y,address=6300,suspend=n -javaagent:/Users/prerana.singhal/traceable/repos/agent/javaagent/build/libs/javaagent-0.1.144-SNAPSHOT.jar=traceableConfigFile=/Users/prerana.singhal/traceable/sample-apps/resources/agent-configs/config.json,traceableServiceName=SampleJettyServlet30xApp -jar "http/servers/jetty/jetty-servlet30x/build/libs/sample-jetty-app.jar" 7301 7302

