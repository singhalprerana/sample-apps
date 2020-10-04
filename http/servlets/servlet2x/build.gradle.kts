plugins {
    `java-library`
    id("com.github.johnrengelman.shadow")
}

repositories {
    mavenCentral()
}

val jettyVersion = "7.6.20.v20160902"

dependencies {
    implementation(project(":http:clients:okhttp"))
    implementation(project(":http:clients:grizzly"))
    implementation("org.eclipse.jetty:jetty-server:${jettyVersion}")
    implementation("org.eclipse.jetty:jetty-servlet:${jettyVersion}")
    implementation("com.google.code.gson:gson:2.7")
}


tasks.register<Jar>("generate-servlet-jar") {
    manifest {
        attributes(
                "Implementation-Title" to "SampleServlet",
                "Implementation-Version" to "1.0")
        exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
    }
    from(configurations.runtimeClasspath.get().map({ if (it.isDirectory) it else zipTree(it) }))
    baseName = "sample-servlet-2x"
    with(tasks.jar.get() as CopySpec)
}

tasks {
    "build"{
        dependsOn("generate-servlet-jar")
    }
}