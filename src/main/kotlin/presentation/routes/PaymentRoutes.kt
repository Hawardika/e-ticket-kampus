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
            val req = call.receive<PaymentCreateRequest>()
            val result = service.create(req)
            call.respond(HttpStatusCode.Created, result)
        }

        get {
            val result = service.list()
            call.respond(result)
        }

        // Check Midtrans transaction status
        get("/status/{orderId}") {
            val orderId = call.parameters["orderId"]

            if (orderId.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid Order ID"))
                return@get
            }

            val status = service.checkStatus(orderId)

            if (status != null) {
                call.respond(status)
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Transaction not found"))
            }
        }

        // Capture payment manually (untuk testing)
        post("/{id}/capture") {
            val id = call.parameters["id"]?.toLongOrNull()

            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ID"))
                return@post
            }

            val updatedPayment = service.capture(id)

            if (updatedPayment != null) {
                call.respond(updatedPayment)
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Payment not found"))
            }
        }
    }
}