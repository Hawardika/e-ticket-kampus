package app.data.repositories

import app.data.tables.OrdersTable
import app.domain.models.Order
import app.domain.models.OrderCreateRequest
import app.domain.models.OrderStatus
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class OrderRepository {

    private fun toOrder(row: ResultRow): Order {
        return Order(
            id = row[OrdersTable.id].value,
            userId = row[OrdersTable.userId],
            eventId = row[OrdersTable.eventId],
            ticketTypeId = row[OrdersTable.ticketTypeId],
            qty = row[OrdersTable.qty],
            orderTotal = row[OrdersTable.orderTotal],
            status = row[OrdersTable.status]
        )
    }

    fun insert(req: OrderCreateRequest, totalAmount: Int): Order = transaction {
        val newId = OrdersTable.insertAndGetId {
            it[userId] = 1 // TODO: Nanti ambil dari req.userId atau Context
            it[eventId] = 1 // TODO: Harusnya req.eventId ada di Request
            it[ticketTypeId] = req.ticketTypeId
            it[qty] = req.qty
            it[orderTotal] = totalAmount
            it[status] = OrderStatus.PENDING
        }

        Order(
            id = newId.value,
            userId = 1,
            eventId = 1,
            ticketTypeId = req.ticketTypeId,
            qty = req.qty,
            orderTotal = totalAmount,
            status = OrderStatus.PENDING
        )
    }

    fun findById(id: Long): Order? = transaction {
        OrdersTable.select { OrdersTable.id eq id }
            .singleOrNull()
            ?.let { toOrder(it) }
    }

    fun updateStatus(id: Long, newStatus: OrderStatus) = transaction {
        OrdersTable.update({ OrdersTable.id eq id }) {
            it[status] = newStatus
        }
    }

    fun list(): List<Order> = transaction {
        OrdersTable.selectAll()
            .map { toOrder(it) }
    }
}