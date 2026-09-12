import com.google.protobuf.gradle.id

plugins {
    kotlin("jvm") version "2.3.10"
    id("io.ktor.plugin") version "3.5.1"
    id("com.google.protobuf") version "0.10.0"
    application
}

application {
    mainClass.set("profile.ApplicationKt")
}

val grpcVersion = "1.82.0"
val grpcKotlinVersion = "1.5.0"
val protobufVersion = "4.29.3"
val exposedVersion = "1.0.0"

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    // gRPC & Protobuf for Kotlin Coroutines
    implementation("io.grpc:grpc-netty-shaded:$grpcVersion")
    implementation("io.grpc:grpc-protobuf:$grpcVersion")
    implementation("io.grpc:grpc-stub:$grpcVersion")
    implementation("io.grpc:grpc-kotlin-stub:$grpcKotlinVersion")
    implementation("com.google.protobuf:protobuf-kotlin:$protobufVersion")

    // Exposed ORM & Postgres (Connecting to profile_db)
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("com.zaxxer:HikariCP:5.1.0")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.5.6")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
        }
        id("grpckotlin") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.4.1:jdk8@jar"
        }
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

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}