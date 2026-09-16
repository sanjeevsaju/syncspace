package org.example.auth.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date
import org.mindrot.jbcrypt.BCrypt

object Security {
    private val secret = System.getenv("JWT_SECRET") ?: "super_secret_jwt_key_syncspace"
    private val issuer = "syncspace-auth-service"
    private val algorithm = Algorithm.HMAC256(secret)

    fun hashPassword(password: String): String =
        BCrypt.hashpw(password, BCrypt.gensalt(12))

    fun verifyPassword(password: String, hash: String): Boolean =
        BCrypt.checkpw(password, hash)

    fun generateToken(userId: String, username: String): String {
        val expirationTime = System.currentTimeMillis() + (86400000 * 7) // 7 days
        return JWT.create()
            .withSubject(userId)
            .withIssuer(issuer)
            .withClaim("username", username)
            .withIssuedAt(Date())
            .withExpiresAt(Date(expirationTime))
            .sign(algorithm)
    }
}
