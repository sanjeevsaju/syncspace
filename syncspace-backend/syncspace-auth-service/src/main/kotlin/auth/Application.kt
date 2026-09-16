package org.example.auth

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import org.example.auth.db.DatabaseFactory
import org.example.auth.routes.authRoutes

fun main() {
    embeddedServer(Netty, 8080, "0.0.0.0") {
        module()
    }.start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    DatabaseFactory.init()

    routing {
        authRoutes()
    }
}
