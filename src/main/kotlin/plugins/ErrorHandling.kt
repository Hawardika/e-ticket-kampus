package app.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureErrorHandling() {
    install(StatusPages) {
        exception<ValidationException> { call, ex ->
            call.respond(HttpStatusCode.UnprocessableEntity, mapOf("errors" to ex.errors))
        }
        exception<ConflictException> { call, ex ->
            call.respond(HttpStatusCode.Conflict, mapOf("error" to ex.message))
        }
        exception<NotFoundException> { call, ex ->
            call.respond(HttpStatusCode.NotFound, mapOf("error" to ex.message))
        }
        exception<Throwable> { call, ex ->
            // Log error detail
            call.application.log.error("Unhandled exception", ex)

            // Response ke client dengan detail error (untuk development)
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf(
                    "error" to "internal_error",
                    "message" to (ex.message ?: "Unknown error"),
                    "type" to ex::class.simpleName
                )
            )
        }
    }
}