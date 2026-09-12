plugins {
    kotlin("jvm") version "2.3.10"
    id("io.ktor.plugin") version "3.5.1"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.3.10"
}

application{
    mainClass.set("notification.ApplicationKt")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val ktorVersion = "3.5.1"

dependencies {
    testImplementation(kotlin("test"))
    // Ktor Server Core & WebSockets (3.5.1)
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-websockets-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")

    // RabbitMQ AMQP Client
    implementation("com.rabbitmq:amqp-client:5.21.0")

    // Coroutines & Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.5.13")
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}