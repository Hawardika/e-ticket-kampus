package app.domain.services

import app.data.repositories.PaymentRepository
import app.domain.models.*
import com.midtrans.Config
import com.midtrans.httpclient.SnapApi
import java.time.LocalDateTime

class PaymentService(
    private val repo: PaymentRepository = PaymentRepository()
) {

    private val midtransConfig: Config = Config(
         System.getenv("MIDTRANS_SERVER_KEY")
            ?: error("MIDTRANS_SERVER_KEY is not set"),
         System.getenv("MIDTRANS_CLIENT_KEY")
            ?: error("MIDTRANS_CLIENT_KEY is not set"),
         System.getenv("MIDTRANS_IS_PRODUCTION")?.toBoolean() ?: false
    )

    fun create(req: PaymentCreateRequest): Payment {
        val payment = repo.insert(req)

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

            println("Midtrans Token: $snapToken")

        } catch (e: Exception) {
            println("Midtrans Error: ${e.message}")
            e.printStackTrace()
        }

        return payment
    }

    fun capture(paymentId: Long): Payment? {
        return repo.updateStatus(paymentId, PayStatus.SUCCESS, LocalDateTime.now())
    }

    fun list(): List<Payment> {
        return repo.list()
    }
}
