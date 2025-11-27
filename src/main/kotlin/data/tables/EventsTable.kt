package app.data.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.timestamp

enum class EventStatus { DRAFT, PUBLISHED, DONE }
object EventsTable : LongIdTable("events") {
    val title = varchar("title", 200)
    val organizer = varchar("organizer", 150)
    val date = date("date")
    val venue = varchar("venue", 150)
    val status = varchar("status", 12).default(EventStatus.DRAFT.name)
    val createdAt = timestamp("created_at").clientDefault { java.time.Instant.now() }
}
