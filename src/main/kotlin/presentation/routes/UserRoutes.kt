package app.presentation.routes
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import app.domain.services.UserService
import app.domain.models.*

fun Route.userRoutes() {
    val service = UserService()
    route("/users") {
        post { val req = call.receive<UserCreateRequest>(); call.respond(service.create(req)) }
        get { call.respond(service.list()) }
    }
}
