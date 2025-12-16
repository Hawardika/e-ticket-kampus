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
    val customer_details: CustomerDetails? = null,
    val item_details: List<ItemDetail>? = null
)

@Serializable
data class TransactionDetails(
    val order_id: String,
    val gross_amount: Int
)

@Serializable
data class ItemDetail(
    val id: String,
    val price: Int,
    val quantity: Int,
    val name: String
)

@Serializable
data class CreditCard(
    val secure: Boolean = true
)

@Serializable
data class CustomerDetails(
    val first_name: String,
    val last_name: String = "",
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
    val transaction_id: String? = null,
    val order_id: String,
    val transaction_status: String,
    val fraud_status: String? = null,
    val gross_amount: String,
    val payment_type: String? = null
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

    private val apiBaseUrl = if (isProduction) {
        "https://api.midtrans.com"
    } else {
        "https://api.sandbox.midtrans.com"
    }

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

    private fun getAuthHeader(): String {
        val credentials = "$serverKey:"
        return "Basic ${Base64.getEncoder().encodeToString(credentials.toByteArray())}"
    }

    suspend fun createSnapToken(request: SnapTokenRequest): SnapTokenResponse {
        try {
            println("🔵 Creating Snap Token...")
            println("🔵 Request: $request")

            val response: SnapTokenResponse = client.post("$baseUrl/snap/v1/transactions") {
                contentType(ContentType.Application.Json)
                header("Authorization", getAuthHeader())
                header("Accept", "application/json")
                setBody(request)
            }.body()

            println("✅ Snap Token created: ${response.token}")
            return response
        } catch (e: Exception) {
            println("❌ Error creating Snap Token: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    suspend fun getTransactionStatus(orderId: String): TransactionStatusResponse {
        try {
            println("🔵 Checking transaction status for: $orderId")

            val response: TransactionStatusResponse = client.get("$apiBaseUrl/v2/$orderId/status") {
                header("Authorization", getAuthHeader())
                header("Accept", "application/json")
            }.body()

            println("✅ Transaction status: ${response.transaction_status}")
            return response
        } catch (e: Exception) {
            println("❌ Error checking status: ${e.message}")
            throw e
        }
    }

    suspend fun cancelTransaction(orderId: String): TransactionStatusResponse {
        try {
            println("🔵 Cancelling transaction: $orderId")

            val response: TransactionStatusResponse = client.post("$apiBaseUrl/v2/$orderId/cancel") {
                header("Authorization", getAuthHeader())
                header("Accept", "application/json")
            }.body()

            println("✅ Transaction cancelled")
            return response
        } catch (e: Exception) {
            println("❌ Error cancelling transaction: ${e.message}")
            throw e
        }
    }

    fun close() {
        client.close()
    }
}