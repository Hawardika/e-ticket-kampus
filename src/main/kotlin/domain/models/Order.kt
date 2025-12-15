package app.domain.models

import kotlinx.serialization.Serializable

@Serializable
enum class OrderStatus {PENDING, PAID, CANCELLED}

@Serializable
data class Order(
    val id : Long,
    val userId : Long,
    val eventId : Long,
    val ticketTypeId : Long,
    val qty : Int,
    val orderTotal : Int,
    val status : OrderStatus
)

@Serializable
data class OrderCreateRequest(
    val ticketTypeId : Long,
    val qty : Int
)

@Serializable
data class OrderResponse(
    val id : Long,
    val userId : Long,
    val eventId : Long,
    val ticketTypeId : Long,
    val qty : Int,
    val orderTotal : Int,
    val status : OrderStatus
)