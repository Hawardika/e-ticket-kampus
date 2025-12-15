package app

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import app.plugins.*

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    embeddedServer(Netty, host = "0.0.0.0", port = port) {
        module()
    }.start(wait = true)
}

fun Application.module() {
    configureCORS() // ← TAMBAHKAN INI PALING ATAS
    configureSerialization()
    configureErrorHandling()

    // Guard DB biar tidak crash saat dev/CI
    val enableDb = (System.getenv("DB_ENABLED") ?: "false").equals("true", ignoreCase = true)
    if (enableDb) {
        runCatching { configureDatabase() }
            .onFailure { cause ->
                log.warn("Database init failed: ${cause.message}. Server jalan tanpa DB.", cause)
            }
    } else {
        log.info("DB_DISABLED: Lewati init database (set DB_ENABLED=true untuk mengaktifkan).")
    }

    configureRouting()
}