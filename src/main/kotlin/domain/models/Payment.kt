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

// Domain model (tanpa @Serializable karena ada LocalDateTime)
data class Payment(
    val id : Long,
    val orderId : Long,
    val amount : Int,
    val method : PayMethod,
    val paidAt : LocalDateTime?,
    val status : PayStatus,
    val snapToken : String? = null,
    val snapRedirectUrl : String? = null
)

@Serializable
data class PaymentCreateRequest(
    val orderId : Long,
    val amount : Int,
    val method : PayMethod,
    val customerName: String? = null,
    val customerEmail: String? = null,
    val customerPhone: String? = null
)

// Response model (serializable, convert LocalDateTime jadi String)
@Serializable
data class PaymentResponse(
    val id : Long,
    val orderId : Long,
    val amount : Int,
    val method : PayMethod,
    val paidAt : String?,
    val status : PayStatus,
    val snapToken : String? = null,      // Token untuk popup Midtrans
    val redirectUrl : String? = null     // URL redirect Midtrans
)