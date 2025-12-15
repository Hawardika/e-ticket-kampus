package app.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*


fun Application.configureErrorHandling() {
    install(StatusPages) {
        // Karena satu package (app.plugins), dia otomatis kenal ValidationException dari file sebelah
        exception<ValidationException> { call, ex ->
            call.respond(HttpStatusCode.UnprocessableEntity, mapOf("errors" to ex.errors))
        }
        exception<ConflictException> { call, ex ->
            call.respond(HttpStatusCode.Conflict, mapOf("error" to ex.message))
        }
        exception<Throwable> { call, _ ->
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "internal_error"))
        }
    }
}