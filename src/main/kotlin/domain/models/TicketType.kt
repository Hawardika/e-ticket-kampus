package app.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class TicketCreateRequest(
    val eventId: Long,
    val name: String,
    val price: Double,
    val allocation: Int
)

@Serializable
data class TicketResponse(
    val id: Long,
    val eventId: Long,
    val name: String,
    val price: Double,
    val allocation: Int,
    val available: Int
)

// Domain model (tidak perlu @Serializable karena cuma internal)
data class TicketType(
    val id: Long,
    val eventId: Long,
    val name: String,
    val price: Int,
    val quota: Int,
    val available: Int
)