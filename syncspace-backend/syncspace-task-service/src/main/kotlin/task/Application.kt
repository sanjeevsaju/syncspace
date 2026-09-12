package task

import com.expediagroup.graphql.server.ktor.GraphQL
import com.expediagroup.graphql.server.ktor.graphQLPostRoute
import com.expediagroup.graphql.server.ktor.graphQLSDLRoute
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.routing
import task.graphql.TaskMutation
import task.graphql.TaskQuery

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        module()
    }.start(wait = true)
}

fun Application.module() {
    // 1. Install GraphQL Server plugin
    install(GraphQL) {
        schema {
            packages = listOf("task")
            queries = listOf(TaskQuery())
            mutations = listOf(TaskMutation())
        }
    }

    // 2. Expose standard GraphQL routes
    routing {
        // Handles POST request to /graphql
        graphQLPostRoute("/api/v1/graphql/")

        // Optional: Exposes the schema definition at /sdl for tools like Postman/Altair
        graphQLSDLRoute("/api/v1/graphql/sdl")
    }
}