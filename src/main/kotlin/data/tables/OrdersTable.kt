package app.infrastructure.database

import app.data.tables.UsersTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.javatime.date

object Orders : Table("orders") {
    val id = long("id").autoIncrement()
    val userId = long("user_id").references(UsersTable.id)
    val totalAmount = double("total_amount")
    val status = varchar("status", 20)
    val createdAt = datetime("created_at")

    override val primaryKey = PrimaryKey(id)
}