package app.domain.services

import app.data.repositories.EventRepository
import app.domain.models.*

class EventService(private val repo: EventRepository = EventRepository()) {
    fun create(req: EventCreateRequest) = repo.insert(req)
    fun publish(id: Long) = repo.publish(id)
    fun listPublished() = repo.listPublished()
}

