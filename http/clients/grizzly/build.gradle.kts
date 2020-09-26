plugins {
    `java-library`
    id("com.github.johnrengelman.shadow")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.glassfish.grizzly:grizzly-http-client:1.16")
    implementation("com.google.code.gson:gson:2.7")
}

tasks.register<Jar>("generate-client-jar") {
    manifest {
        attributes(
                "Implementation-Title" to "SampleGrizzlyClient",
                "Implementation-Version" to "1.0",
                "Main-Class" to "prer.sample.app.client.grizzly.SampleGrizzlyClient")
    }
    from(configurations.runtimeClasspath.get().map({ if (it.isDirectory) it else zipTree(it) }))
    baseName = "sample-grizzly-client"
    with(tasks.jar.get() as CopySpec)
}

tasks {
    "build"{
        dependsOn("generate-client-jar")
    }
}