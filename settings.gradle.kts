rootProject.name = "sample-apps"

buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        classpath("com.github.jengelman.gradle.plugins:shadow:6.0.0")
    }
}

include(":grpc")

include(":http:clients:grizzly")
include(":http:clients:okhttp")

include(":http:servers:jetty:jetty-servlet2x")
include(":http:servers:jetty:jetty-servlet30x")
include(":http:servers:jetty:jetty-servlet31x")
include(":http:servers:tomcat")
include(":http:servers:spark")

include(":http:servlets:servlet2x")
include(":http:servlets:servlet30x")
include(":http:servlets:servlet31x")
include(":http:servlets:servlet5x")

