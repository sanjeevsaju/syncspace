import com.google.protobuf.gradle.id

val ktorVersion = "3.5.1"
val grpcVersion = "1.82.0"
val grpcKotlinVersion = "1.5.0"
val protobufVersion = "4.29.3"
val graphqlKotlinVersion = "8.3.0"

plugins {
    kotlin("jvm") version "2.3.10"
    id("io.ktor.plugin") version "3.5.1"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.3.10"
    id("com.google.protobuf") version "0.10.0"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
    application
}

ktlint {
    filter {
        exclude { element ->
            element.file.path.contains("/build/generated/")
        }
    }
}

application {
    mainClass.set("task.ApplicationKt")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    // Ktor Server Core
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")

    // Expedia Group GraphQL Kotlin (Replaces KGraphQL)
    implementation("com.expediagroup:graphql-kotlin-ktor-server:$graphqlKotlinVersion")

    // MongoDB Official Kotlin Coroutines Driver
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:5.1.0")
//    implementation("org.mongodb:bson-kotlinx:5.8.0")

    // gRPC Client & Protobuf
    implementation("io.grpc:grpc-netty-shaded:$grpcVersion")
    implementation("io.grpc:grpc-protobuf:$grpcVersion")
    implementation("io.grpc:grpc-stub:$grpcVersion")
    implementation("io.grpc:grpc-kotlin-stub:$grpcKotlinVersion")
    implementation("com.google.protobuf:protobuf-kotlin:$protobufVersion")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.5.6")

    // RabbitMQ
    implementation("com.rabbitmq:amqp-client:5.21.0")
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}

protobuf {
    protoc { artifact = "com.google.protobuf:protoc:$protobufVersion" }
    plugins {
        id("grpc") { artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion" }
        id("grpckotlin") { artifact = "io.grpc:protoc-gen-grpc-kotlin:1.4.1:jdk8@jar" }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
                id("grpckotlin")
            }
        }
    }
}
