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
    implementation(project(":http:servlets:servlet30x"))
    implementation("org.eclipse.jetty:jetty-server:${jettyVersion}")
    implementation("org.eclipse.jetty:jetty-servlet:${jettyVersion}")
    implementation("com.google.code.gson:gson:2.7")
}

tasks.register<Jar>("generate-server-jar") {
    archiveClassifier.set("")
    manifest {
        attributes(
                "Implementation-Title" to "SampleJettyServer",
                "Implementation-Version" to "1.0",
                "Main-Class" to "prer.sample.app.server.jetty.SampleJettyServer")
        exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
    }
    from(configurations.runtimeClasspath.get().map({ if (it.isDirectory) it else zipTree(it) }))
    baseName = "sample-jetty-server"
    with(tasks.jar.get() as CopySpec)
}

tasks.register<Jar>("generate-app-jar") {
    archiveClassifier.set("")
    manifest {
        attributes(
                "Implementation-Title" to "SampleJettyApp",
                "Implementation-Version" to "1.0",
                "Main-Class" to "prer.sample.app.server.jetty.SampleJettyApp")
        exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
    }
    from(configurations.runtimeClasspath.get().map({ if (it.isDirectory) it else zipTree(it) }))
    baseName = "sample-jetty-app"
    with(tasks.jar.get() as CopySpec)
}

tasks {
    "build"{
        dependsOn("generate-server-jar")
        dependsOn("generate-app-jar")
    }
}