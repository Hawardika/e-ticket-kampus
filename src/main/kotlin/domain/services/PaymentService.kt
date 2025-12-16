package app.domain.services

import app.data.repositories.PaymentRepository
import app.domain.models.*
import app.infrastructure.payment.*
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime

class PaymentService(
    private val repo: PaymentRepository = PaymentRepository()
) {

    private val midtransEnabled: Boolean
    private val midtransClient: MidtransClient?

    init {
        val serverKey = System.getenv("MIDTRANS_SERVER_KEY")
        val isProduction = System.getenv("MIDTRANS_IS_PRODUCTION")?.toBoolean() ?: false

        midtransEnabled = !serverKey.isNullOrBlank()

        midtransClient = if (midtransEnabled) {
            println("✅ Midtrans enabled (${if (isProduction) "PRODUCTION" else "SANDBOX"})")
            MidtransClient(serverKey!!, isProduction)
        } else {
            println("⚠️ Midtrans disabled: MIDTRANS_SERVER_KEY not set")
            null
        }
    }

    fun create(req: PaymentCreateRequest): PaymentResponse {
        var snapToken: String? = null
        var redirectUrl: String? = null

        // Create Midtrans Snap Token
        if (midtransEnabled && midtransClient != null) {
            try {
                val snapRequest = SnapTokenRequest(
                    transaction_details = TransactionDetails(
                        order_id = "ORDER-${System.currentTimeMillis()}",
                        gross_amount = req.amount
                    ),
                    customer_details = if (req.customerEmail != null) {
                        CustomerDetails(
                            first_name = req.customerName ?: "Customer",
                            email = req.customerEmail,
                            phone = req.customerPhone ?: ""
                        )
                    } else null
                )

                val snapResponse = runBlocking {
                    midtransClient.createSnapToken(snapRequest)
                }

                snapToken = snapResponse.token
                redirectUrl = snapResponse.redirect_url

                println("✅ Midtrans Snap Token: $snapToken")

            } catch (e: Exception) {
                println("❌ Midtrans Error: ${e.message}")
                e.printStackTrace()
            }
        } else {
            println("ℹ️ Payment created without Midtrans (Midtrans disabled)")
        }

        // Save to database
        val payment = repo.insert(req, snapToken, redirectUrl)

        return PaymentResponse(
            id = payment.id,
            orderId = payment.orderId,
            amount = payment.amount,
            method = payment.method,
            paidAt = payment.paidAt?.toString(),
            status = payment.status,
            snapToken = snapToken,
            redirectUrl = redirectUrl
        )
    }

    fun capture(paymentId: Long): PaymentResponse? {
        val payment = repo.updateStatus(paymentId, PayStatus.SUCCESS, LocalDateTime.now())
        return payment?.let {
            PaymentResponse(
                id = it.id,
                orderId = it.orderId,
                amount = it.amount,
                method = it.method,
                paidAt = it.paidAt?.toString(),
                status = it.status,
                snapToken = it.snapToken,
                redirectUrl = it.snapRedirectUrl
            )
        }
    }

    fun checkStatus(orderId: String): TransactionStatusResponse? {
        if (!midtransEnabled || midtransClient == null) {
            println("⚠️ Cannot check status: Midtrans disabled")
            return null
        }

        return try {
            runBlocking {
                midtransClient.getTransactionStatus(orderId)
            }
        } catch (e: Exception) {
            println("❌ Error checking status: ${e.message}")
            null
        }
    }

    fun list(): List<PaymentResponse> {
        return repo.list().map {
            PaymentResponse(
                id = it.id,
                orderId = it.orderId,
                amount = it.amount,
                method = it.method,
                paidAt = it.paidAt?.toString(),
                status = it.status,
                snapToken = it.snapToken,
                redirectUrl = it.snapRedirectUrl
            )
        }
    }
}