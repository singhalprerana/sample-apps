plugins {
    `java-library`
    id("com.github.johnrengelman.shadow")
}

repositories {
    mavenCentral()
}

val okhttpVersion = "3.14.9"

dependencies {
    implementation("com.squareup.okhttp3:okhttp:${okhttpVersion}")
    implementation("com.squareup.okhttp3:logging-interceptor:${okhttpVersion}")
    implementation("com.google.code.gson:gson:2.7")
}

tasks.register<Jar>("generate-client-jar") {
    manifest {
        attributes(
                "Implementation-Title" to "SampleOkHttpClient",
                "Implementation-Version" to "1.0",
                "Main-Class" to "prer.sample.app.client.okhttp.SampleOkHttpClient")
    }
    from(configurations.runtimeClasspath.get().map({ if (it.isDirectory) it else zipTree(it) }))
    baseName = "sample-okhttp-client"
    with(tasks.jar.get() as CopySpec)
}

tasks {
    "build"{
        dependsOn("generate-client-jar")
    }
}