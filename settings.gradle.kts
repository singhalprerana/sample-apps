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
