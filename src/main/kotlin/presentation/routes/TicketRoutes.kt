package app.presentation.routes

import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.http.HttpStatusCode
import app.domain.services.TicketService
import app.domain.models.TicketCreateRequest

fun Route.ticketRoutes() {

    val service = TicketService()

    route("/tickets") {

        post {
            try {
                val req = call.receive<TicketCreateRequest>()

                val result = service.create(req)

                call.respond(HttpStatusCode.Created, result)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid Data")
            }
        }

        get("/event/{eventId}") {
            val eventId = call.parameters["eventId"]?.toLongOrNull()

            if (eventId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid Event ID")
                return@get
            }

            val result = service.listByEvent(eventId)
            call.respond(result)
        }
    }
}