package app.infrastructure.payment

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.Base64

@Serializable
data class SnapTokenRequest(
    val transaction_details: TransactionDetails,
    val credit_card: CreditCard = CreditCard(),
    val customer_details: CustomerDetails? = null
)

@Serializable
data class TransactionDetails(
    val order_id: String,
    val gross_amount: Int
)

@Serializable
data class CreditCard(
    val secure: Boolean = true
)

@Serializable
data class CustomerDetails(
    val first_name: String,
    val email: String,
    val phone: String
)

@Serializable
data class SnapTokenResponse(
    val token: String,
    val redirect_url: String
)

@Serializable
data class TransactionStatusResponse(
    val transaction_id: String,
    val order_id: String,
    val transaction_status: String,
    val fraud_status: String? = null,
    val gross_amount: String,
    val payment_type: String
)

class MidtransClient(
    private val serverKey: String,
    private val isProduction: Boolean = false
) {
    private val baseUrl = if (isProduction) {
        "https://app.midtrans.com"
    } else {
        "https://app.sandbox.midtrans.com"
    }

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
    }

    private fun getAuthHeader(): String {
        val credentials = "$serverKey:"
        return "Basic ${Base64.getEncoder().encodeToString(credentials.toByteArray())}"
    }

    suspend fun createSnapToken(request: SnapTokenRequest): SnapTokenResponse {
        return client.post("$baseUrl/snap/v1/transactions") {
            contentType(ContentType.Application.Json)
            header("Authorization", getAuthHeader())
            setBody(request)
        }.body()
    }

    suspend fun getTransactionStatus(orderId: String): TransactionStatusResponse {
        return client.get("$baseUrl/v2/$orderId/status") {
            header("Authorization", getAuthHeader())
        }.body()
    }

    suspend fun cancelTransaction(orderId: String): TransactionStatusResponse {
        return client.post("$baseUrl/v2/$orderId/cancel") {
            header("Authorization", getAuthHeader())
        }.body()
    }

    fun close() {
        client.close()
    }
}