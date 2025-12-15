package app.domain.services

import app.domain.models.*

class TicketService {

    private val ticketDatabase = mutableListOf<TicketType>()

    fun create(req: TicketCreateRequest): TicketResponse {
        val newTicket = TicketType(
            id = System.currentTimeMillis(),
            eventId = req.eventId,
            name = req.name,
            price = req.price.toInt(),
            quota = req.allocation,
            available = req.allocation
        )

        ticketDatabase.add(newTicket)
        println("Ticket Created: $newTicket")

        return TicketResponse(
            newTicket.id, newTicket.eventId, newTicket.name,
            newTicket.price.toDouble(), newTicket.quota, newTicket.available
        )
    }

    fun listByEvent(eventId: Long): List<TicketResponse> {
        return ticketDatabase
            .filter { it.eventId == eventId }
            .map {
                TicketResponse(it.id, it.eventId, it.name, it.price.toDouble(), it.quota, it.available)
            }
    }


    fun reserve(ticketTypeId: Long, qty: Int): Boolean {
        val ticketIndex = ticketDatabase.indexOfFirst { it.id == ticketTypeId }

        if (ticketIndex != -1) {
            val ticket = ticketDatabase[ticketIndex]

            if (ticket.available >= qty) {
                val updatedTicket = ticket.copy(available = ticket.available - qty)
                ticketDatabase[ticketIndex] = updatedTicket
                println("Ticket Reserved. Remaining: ${updatedTicket.available}")
                return true
            }
        }
        println("Ticket Reserve Failed: Not enough quota or ticket not found")
        return false
    }

    fun release(ticketTypeId: Long, qty: Int) {
        val ticketIndex = ticketDatabase.indexOfFirst { it.id == ticketTypeId }

        if (ticketIndex != -1) {
            val ticket = ticketDatabase[ticketIndex]

            val updatedTicket = ticket.copy(available = ticket.available + qty)
            ticketDatabase[ticketIndex] = updatedTicket
            println("Ticket Released. Current: ${updatedTicket.available}")
        }
    }
}