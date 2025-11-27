package app.presentation.routes
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import app.domain.services.EventService
import app.domain.models.*

fun Route.eventRoutes() {
    val service = EventService()
    route("/events") {
        post { val req = call.receive<EventCreateRequest>(); call.respond(service.create(req)) }
        post("{id}/publish") { val id = call.parameters["id"]!!.toLong(); call.respond(service.publish(id)) }
        get { call.respond(service.listPublished()) }
    }
}
