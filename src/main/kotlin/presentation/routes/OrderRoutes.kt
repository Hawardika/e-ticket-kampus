package app.presentation.routes

import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import app.domain.services.OrderService
import app.domain.models.*

fun Route.orderRoutes() {
    val service = OrderService()
    route("/orders") {
        post {
            val req = call.receive<OrderCreateRequest>()
            call.respond(service.create(req))
        }

        get {
            call.respond(service.list())
        }

        route("/{id}") {
            post("/cancel") {
                val id = call.parameters["id"]!!.toLong()
                call.respond(service.cancel(id))
            }

            post("/pay") {
                val id = call.parameters["id"]!!.toLong()
                call.respond(service.markPaid(id))
            }
        }
    }
}