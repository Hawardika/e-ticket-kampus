package app.domain.services

import app.data.repositories.PaymentRepository
import app.domain.models.*
import com.midtrans.Config
import com.midtrans.httpclient.SnapApi
import java.time.LocalDateTime

class PaymentService(
    private val repo: PaymentRepository = PaymentRepository()
) {

    private val midtransEnabled: Boolean
    private val midtransConfig: Config?

    init {
        val serverKey = System.getenv("MIDTRANS_SERVER_KEY")
        val clientKey = System.getenv("MIDTRANS_CLIENT_KEY")
        val isProduction = System.getenv("MIDTRANS_IS_PRODUCTION")?.toBoolean() ?: false

        midtransEnabled = serverKey != null && clientKey != null

        midtransConfig = if (midtransEnabled) {
            Config(serverKey, clientKey, isProduction)
        } else {
            println("⚠️ Midtrans disabled: MIDTRANS_SERVER_KEY or MIDTRANS_CLIENT_KEY not set")
            null
        }
    }

    fun create(req: PaymentCreateRequest): PaymentResponse {
        val payment = repo.insert(req)

        if (midtransEnabled && midtransConfig != null) {
            try {
                val params = hashMapOf<String, Any>(
                    "transaction_details" to hashMapOf(
                        "order_id" to payment.id.toString(),
                        "gross_amount" to payment.amount
                    ),
                    "credit_card" to hashMapOf(
                        "secure" to true
                    )
                )

                val snapToken = SnapApi.createTransactionToken(params, midtransConfig)
                println("✅ Midtrans Token Created: $snapToken")

            } catch (e: Exception) {
                println("❌ Midtrans Error: ${e.message}")
                e.printStackTrace()
            }
        } else {
            println("ℹ️ Payment created without Midtrans integration (ID: ${payment.id})")
        }

        return PaymentResponse(
            id = payment.id,
            orderId = payment.orderId,
            amount = payment.amount,
            method = payment.method,
            paidAt = payment.paidAt?.toString(),
            status = payment.status
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
                status = it.status
            )
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
                status = it.status
            )
        }
    }
}