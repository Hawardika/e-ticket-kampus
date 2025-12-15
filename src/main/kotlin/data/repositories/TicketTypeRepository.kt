package app.data.repositories

import app.data.tables.TicketTypesTable

import app.domain.models.TicketType
import app.domain.models.TicketCreateRequest

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.minus
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.transactions.transaction

class TicketTypeRepository {

    private fun toTicketType(row: ResultRow): TicketType {
        return TicketType(
            id = row[TicketTypesTable.id].value,
            eventId = row[TicketTypesTable.eventId],
            name = row[TicketTypesTable.name],
            price = row[TicketTypesTable.price],
            quota = row[TicketTypesTable.quota],
            available = row[TicketTypesTable.available]
        )
    }

    fun insert(req: TicketCreateRequest): TicketType = transaction {
        val newId = TicketTypesTable.insertAndGetId {
            it[TicketTypesTable.eventId] = req.eventId
            it[TicketTypesTable.name] = req.name
            it[TicketTypesTable.price] = req.price.toInt()
            it[TicketTypesTable.quota] = req.allocation
            it[TicketTypesTable.available] = req.allocation
        }

        TicketType(
            id = newId.value,
            eventId = req.eventId,
            name = req.name,
            price = req.price.toInt(),
            quota = req.allocation,
            available = req.allocation
        )
    }

    fun findById(id: Long): TicketType? = transaction {
        TicketTypesTable.select { TicketTypesTable.id eq id }
            .singleOrNull()
            ?.let { toTicketType(it) }
    }

    fun listByEvent(eventId: Long): List<TicketType> = transaction {
        TicketTypesTable.select { TicketTypesTable.eventId eq eventId }
            .map { toTicketType(it) }
    }

    fun reserve(ticketTypeId: Long, qty: Int): Boolean = transaction {
        val updatedRows = TicketTypesTable.update({
            (TicketTypesTable.id eq ticketTypeId) and (TicketTypesTable.available greaterEq qty)
        }) {
            // Update stok (Explicit reference)
            it[TicketTypesTable.available] = TicketTypesTable.available - qty
        }
        updatedRows > 0
    }

    fun release(ticketTypeId: Long, qty: Int) = transaction {
        TicketTypesTable.update({ TicketTypesTable.id eq ticketTypeId }) {
            // Kembalikan stok
            it[TicketTypesTable.available] = TicketTypesTable.available + qty
        }
    }
}