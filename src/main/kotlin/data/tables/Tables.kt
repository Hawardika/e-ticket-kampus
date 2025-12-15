package app.data.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import app.domain.models.OrderStatus



object TicketTypesTable : LongIdTable("ticket_types") {
    val eventId = long("event_id")
    val name = varchar("name", 100)
    val price = integer("price")
    val quota = integer("quota")
    val available = integer("available")
}

object OrdersTable : LongIdTable("orders") {
    val userId = long("user_id")
    val eventId = long("event_id")
    val ticketTypeId = long("ticket_type_id")

    val qty = integer("qty")
    val orderTotal = integer("order_total")

    val status = enumerationByName("status", 20, OrderStatus::class)
        .default(OrderStatus.PENDING)

    val createdAt = datetime("created_at").nullable()
}