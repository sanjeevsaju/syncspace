package org.example.auth.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database.Companion.connect
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object UsersTable : Table("users") {
    val id = varchar("id", 36)
    val email = varchar("email", 36).uniqueIndex()
    val username = varchar("username", 64).uniqueIndex()
    val passwordHash = varchar("password_hash", 128)
    override val primaryKey = PrimaryKey(id)
}

object DatabaseFactory {
    fun init() {
        val config = HikariConfig().apply {
            jdbcUrl = System.getenv("DB_URL") ?: "jdbc:postgresql://postgres:5432/auth_db"
            username = System.getenv("DB_USER") ?: "syncspace_admin"
            password = System.getenv("DB_PASSWORD") ?: "root_super_secret_password"
            driverClassName = "org.postgresql.Driver"
            maximumPoolSize = 5
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        }

        val database = connect(HikariDataSource(config))
        transaction(database) {
            SchemaUtils.create(UsersTable)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}
