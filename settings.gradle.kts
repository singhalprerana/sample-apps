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
include(":http:servers:jetty")
include(":http:servers:tomcat")
include(":http:servlets:servlet2x")
include(":http:servlets:servlet3x")
