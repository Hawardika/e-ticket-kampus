package app.presentation.routes

import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.http.HttpStatusCode
import app.domain.services.PaymentService
import app.domain.models.*

fun Route.paymentRoutes() {

    val service = PaymentService()

    route("/payments") {
        post {
            try {
                val req = call.receive<PaymentCreateRequest>()

                val result = service.create(req)

                call.respond(HttpStatusCode.Created, result)
            } catch (e: Exception) {

                call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "Invalid Data")))
            }
        }

        post("/{id}/capture") {
            val id = call.parameters["id"]?.toLongOrNull()

            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@post
            }

            val updatedPayment = service.capture(id)

            if (updatedPayment != null) {
                call.respond(updatedPayment)
            } else {
                call.respond(HttpStatusCode.NotFound, "Payment not found")
            }
        }
    }
}