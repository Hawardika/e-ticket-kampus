package app

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import app.plugins.*

fun main() {
    embeddedServer(Netty, host = "0.0.0.0", port = 8080) {
        module()
    }.start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureErrorHandling()

    // Guard DB biar tidak crash saat dev
    val enableDb = (System.getenv("DB_ENABLED") ?: "false").equals("true", ignoreCase = true)
    if (enableDb) {
        runCatching { configureDatabase() }
            .onFailure { cause ->
                // jangan bunuh server; cukup log
                log.warn("Database init failed: ${cause.message}. Server tetap jalan tanpa DB.")
            }
    } else {
        log.info("DB_DISABLED: Lewati init database (set DB_ENABLED=true untuk mengaktifkan).")
    }

    configureRouting()
}
