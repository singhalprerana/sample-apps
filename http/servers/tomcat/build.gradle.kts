plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":http:clients:okhttp"))
    implementation(project(":http:clients:grizzly"))
    implementation(project(":http:servlets:servlet31x"))
    implementation("org.apache.tomcat.embed:tomcat-embed-core:9.0.0.M6")
    implementation("org.apache.tomcat.embed:tomcat-embed-jasper:9.0.0.M6")
    implementation("org.apache.tomcat.embed:tomcat-embed-logging-juli:9.0.0.M6")
    implementation("com.google.code.gson:gson:2.7")
}

tasks.register<Jar>("generate-server-jar") {
    manifest {
        attributes(
                "Implementation-Title" to "SampleTomcatServer",
                "Implementation-Version" to "1.0",
                "Main-Class" to "prer.sample.app.server.tomcat.SampleTomcatServer")
        exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
    }
    from(configurations.runtimeClasspath.get().map({ if (it.isDirectory) it else zipTree(it) }))
    baseName = "sample-tomcat-server"
    with(tasks.jar.get() as CopySpec)
}

tasks.register<Jar>("generate-app-jar") {
    manifest {
        attributes(
                "Implementation-Title" to "SampleTomcatApp",
                "Implementation-Version" to "1.0",
                "Main-Class" to "prer.sample.app.server.tomcat.SampleTomcatApp")
        exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
    }
    from(configurations.runtimeClasspath.get().map({ if (it.isDirectory) it else zipTree(it) }))
    baseName = "sample-tomcat-app"
    with(tasks.jar.get() as CopySpec)
}

tasks {
    "build"{
        dependsOn("generate-server-jar")
        dependsOn("generate-app-jar")
    }
}