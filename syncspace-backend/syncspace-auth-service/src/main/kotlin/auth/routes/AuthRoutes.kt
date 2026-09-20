package org.example.auth.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import java.util.UUID
import kotlinx.serialization.Serializable
import org.example.auth.db.DatabaseFactory.dbQuery
import org.example.auth.db.UsersTable
import org.example.auth.security.Security
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

@Serializable
data class RegisterRequest(val email: String, val username: String, val password: String)

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class AuthResponse(val userId: String, val username: String, val token: String)

fun Route.authRoutes() {
    route("/health") {
        get {
            call.respondText("OK")
        }
    }

    route("/register") {
        post {
            val req = call.receive<RegisterRequest>()
            val newId = UUID.randomUUID().toString()
            val hash = Security.hashPassword(password = req.password)

            val success = dbQuery {
                try {
                    UsersTable.insert {
                        it[id] = newId
                        it[email] = req.email.lowercase()
                        it[username] = req.username
                        it[passwordHash] = hash
                    }
                    true
                } catch (e: Exception) {
                    false
                }
            }

            if (!success) {
                call.respond(HttpStatusCode.Conflict, mapOf("error" to "Email or username already exists"))
                return@post
            }

            val token = Security.generateToken(newId, req.username)
            call.respond(HttpStatusCode.Created, AuthResponse(newId, req.username, token))
        }
    }

    route("/login") {
        post {
            val req = call.receive<LoginRequest>()
            val userRow = dbQuery {
                UsersTable.select { UsersTable.email eq req.email.lowercase() }.singleOrNull()
            }

            if (userRow == null || !Security.verifyPassword(req.password, userRow[UsersTable.passwordHash])) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid credentials"))
                return@post
            }

            val userId = userRow[UsersTable.id]
            val username = userRow[UsersTable.username]
            val token = Security.generateToken(userId, username)
            call.respond(HttpStatusCode.OK, AuthResponse(userId, username, token))
        }
    }
}
