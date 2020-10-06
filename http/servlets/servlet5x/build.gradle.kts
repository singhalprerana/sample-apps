plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":http:clients:okhttp"))
    implementation(project(":http:clients:grizzly"))
    implementation("jakarta.servlet:jakarta.servlet-api:5.0.0")
    implementation("com.google.code.gson:gson:2.7")
}

tasks.register<Jar>("generate-servlet-jar") {
    archiveClassifier.set("")
    manifest {
        attributes(
                "Implementation-Title" to "SampleServlet",
                "Implementation-Version" to "1.0")
        exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
    }
    from(configurations.runtimeClasspath.get().map({ if (it.isDirectory) it else zipTree(it) }))
    baseName = "sample-servlet"
    with(tasks.jar.get() as CopySpec)
}

tasks {
    "build"{
        dependsOn("generate-servlet-jar")
    }
}