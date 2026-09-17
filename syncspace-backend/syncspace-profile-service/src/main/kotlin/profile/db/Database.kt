package profile.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

// 1. Database Table schema
object ProfilesTable : Table("profiles") {
    val userId = varchar("user_id", 36)
    val username = varchar("username", 64)
    val displayName = varchar("display_name", 100).default("")
    val avatarUrl = varchar("avatar_url", 255).default("")
    val bio = varchar("bio", 255).default("")
    override val primaryKey = PrimaryKey(userId)
}

object DatabaseFactory {
    fun init() {
        val config =
            HikariConfig().apply {
                jdbcUrl = System.getenv("DB_URL") ?: "jdbc:postgresql://postgres:5432/profile_db"
                username = System.getenv("DB_USER") ?: "syncspace_admin"
                password = System.getenv("DB_PASSWORD") ?: "root_super_secret_password"
                driverClassName = "org.postgresql.Driver"
                maximumPoolSize = 5
                isAutoCommit = false
                transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            }

        val database = Database.connect(HikariDataSource(config))
        transaction(database) {
            SchemaUtils.create(ProfilesTable)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction(Dispatchers.IO) { block() }
}
