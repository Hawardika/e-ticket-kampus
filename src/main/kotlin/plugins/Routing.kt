package app.plugins
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import app.presentation.routes.userRoutes
import app.presentation.routes.eventRoutes

fun Application.configureRouting() {
    routing {
        get("/") { call.respondText("API running") }
        userRoutes()
        eventRoutes()
    }
}
