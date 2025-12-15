package app.domain.services

import app.data.repositories.TicketTypeRepository
import app.domain.models.*

class TicketService(
    private val repo: TicketTypeRepository = TicketTypeRepository()
) {

    fun create(req: TicketCreateRequest): TicketResponse {
        val ticket = repo.insert(req)
        return TicketResponse(
            id = ticket.id,
            eventId = ticket.eventId,
            name = ticket.name,
            price = ticket.price.toDouble(),
            allocation = ticket.quota,
            available = ticket.available
        )
    }

    fun listByEvent(eventId: Long): List<TicketResponse> {
        return repo.listByEvent(eventId).map {
            TicketResponse(
                id = it.id,
                eventId = it.eventId,
                name = it.name,
                price = it.price.toDouble(),
                allocation = it.quota,
                available = it.available
            )
        }
    }
}