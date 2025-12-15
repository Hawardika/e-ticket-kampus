package app.domain.models

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
enum class PayMethod {
    VA,
    EWALLET,
    TRANSFER
}

@Serializable
enum class PayStatus {
    INIT,
    SUCCESS,
    FAILED
}

data class Payment(
    val id : Long,
    val orderId : Long,
    val amount : Int,
    val method : PayMethod,
    val paidAt : LocalDateTime?,
    val status : PayStatus
)
@Serializable
data class PaymentCreateRequest(
    val orderId : Long,
    val amount : Int,
    val method : PayMethod
)
@Serializable
data class PaymentResponse(
    val id : Long,
    val orderId : Long,
    val amount : Int,
    val method : PayMethod,
    val paidAt : LocalDateTime?,
    val status : PayStatus
)
