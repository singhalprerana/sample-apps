import com.google.protobuf.gradle.*

plugins {
    `java-library`
    id("com.google.protobuf") version "0.8.8"
    id("com.github.johnrengelman.shadow")
}

repositories {
    mavenCentral()
}

val PROTOBUF_VERSION = "3.6.1"

dependencies {
    implementation("io.grpc:grpc-all:1.19.0")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation("com.google.protobuf:protobuf-java:${PROTOBUF_VERSION}")
}


protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${PROTOBUF_VERSION}"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.19.0"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                // Apply the "grpc" plugin whose spec is defined above, without options.
                id("grpc")
            }
        }
    }
    generatedFilesBaseDir = "$projectDir/generated"
    tasks.clean {
        delete("$projectDir/generated")
    }
}

sourceSets {
    main {
        java {
            srcDirs("src/main/java", "generated/main/grpc", "$projectDir/generated/main/java")
        }
        proto {
            srcDir("$projectDir/src/main/proto")
        }
    }
}

tasks.register<Jar>("generate-server-jar") {
    manifest {
        attributes(
                "Implementation-Title" to "SampleGrpcServer",
                "Implementation-Version" to "1.0",
                "Main-Class" to "prer.sample.app.grpc.SampleGrpcServer")
    }
    from(configurations.runtimeClasspath.get().map({ if (it.isDirectory) it else zipTree(it) }))
    baseName = "sample-grpc-server"
    with(tasks.jar.get() as CopySpec)
}

tasks.register<Jar>("generate-client-jar") {
    manifest {
        attributes(
                "Implementation-Title" to "SampleGrpcClient",
                "Implementation-Version" to "1.0",
                "Main-Class" to "prer.sample.app.grpc.SampleGrpcClient")
    }
    from(configurations.runtimeClasspath.get().map({ if (it.isDirectory) it else zipTree(it) }))
    baseName = "sample-grpc-client"
    with(tasks.jar.get() as CopySpec)
}

tasks.register<Jar>("generate-app-jar") {
    manifest {
        attributes(
                "Implementation-Title" to "SampleGrpcApp",
                "Implementation-Version" to "1.0",
                "Main-Class" to "prer.sample.app.grpc.SampleGrpcApp")
    }
    from(configurations.runtimeClasspath.get().map({ if (it.isDirectory) it else zipTree(it) }))
    baseName = "sample-grpc-app"
    with(tasks.jar.get() as CopySpec)
}

tasks {
    "build"{
        dependsOn("generate-server-jar")
        dependsOn("generate-client-jar")
        dependsOn("generate-app-jar")
    }
}