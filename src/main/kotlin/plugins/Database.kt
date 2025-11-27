package app.plugins

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import app.data.tables.UsersTable
import app.data.tables.EventsTable

object DatabaseFactory {
    fun init() {
        val cfg = HikariConfig().apply {
            jdbcUrl = System.getenv("DB_URL") ?: "jdbc:mysql://localhost:3306/eticketing"
            username = System.getenv("DB_USER") ?: "root"
            password = System.getenv("DB_PASS") ?: "root"
            maximumPoolSize = 5
            driverClassName = "com.mysql.cj.jdbc.Driver"
        }
        val ds = HikariDataSource(cfg)
        Database.connect(ds)
        transaction { SchemaUtils.create(UsersTable, EventsTable) }
    }
}

fun configureDatabase() = DatabaseFactory.init()
