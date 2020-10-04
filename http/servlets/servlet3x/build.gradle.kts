import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
    id("com.github.johnrengelman.shadow")
}

repositories {
    mavenCentral()
}

val jettyVersion = "9.4.31.v20200723"

dependencies {
    implementation(project(":http:clients:okhttp"))
    implementation(project(":http:clients:grizzly"))
    implementation("org.eclipse.jetty:jetty-server:${jettyVersion}")
    implementation("org.eclipse.jetty:jetty-servlet:${jettyVersion}")
    implementation("com.google.code.gson:gson:2.7")
}

tasks.register<ShadowJar>("generate-servlet-jar") {
    archiveClassifier.set("")
    manifest {
        attributes(
                "Implementation-Title" to "SampleServlet",
                "Implementation-Version" to "1.0")
        exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
    }
    baseName = "sample-servlet"
}

tasks {
    "build"{
        dependsOn("generate-servlet-jar")
    }
}