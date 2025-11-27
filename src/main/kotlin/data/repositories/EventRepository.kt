package app.data.repositories

import app.data.tables.*
import app.domain.models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class EventRepository {
    fun insert(req: EventCreateRequest): EventResponse = transaction {
        val id = EventsTable.insertAndGetId {
            it[title] = req.title; it[organizer] = req.organizer
            it[date] = java.time.LocalDate.parse(req.date); it[venue] = req.venue
        }.value
        EventResponse(id, req.title, req.organizer, req.date, req.venue, EventStatus.DRAFT.name)
    }

    fun publish(id: Long): EventResponse = transaction {
        EventsTable.update({ EventsTable.id eq id }) { it[status] = EventStatus.PUBLISHED.name }
        val row = EventsTable.select { EventsTable.id eq id }.single()
        EventResponse(
            row[EventsTable.id].value,
            row[EventsTable.title],
            row[EventsTable.organizer],
            row[EventsTable.date].toString(),
            row[EventsTable.venue],
            row[EventsTable.status]
        )
    }

    fun listPublished(): List<EventResponse> = transaction {
        EventsTable.select { EventsTable.status eq EventStatus.PUBLISHED.name }.map {
            EventResponse(
                it[EventsTable.id].value,
                it[EventsTable.title],
                it[EventsTable.organizer],
                it[EventsTable.date].toString(),
                it[EventsTable.venue],
                it[EventsTable.status]
            )
        }
    }
}
