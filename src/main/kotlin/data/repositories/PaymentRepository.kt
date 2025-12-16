package app.data.repositories

import app.domain.models.Payment
import app.domain.models.PaymentCreateRequest
import app.domain.models.PayStatus
import app.domain.models.PayMethod

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

class PaymentRepository {

    private fun toPayment(row: ResultRow): Payment {
        return Payment(
            id = row[PaymentsTable.id].value,
            orderId = row[PaymentsTable.orderId],
            amount = row[PaymentsTable.amount],
            method = row[PaymentsTable.method],
            paidAt = row[PaymentsTable.paidAt],
            status = row[PaymentsTable.status],
            snapToken = row[PaymentsTable.snapToken],
            snapRedirectUrl = row[PaymentsTable.snapRedirectUrl]
        )
    }

    fun insert(req: PaymentCreateRequest, snapToken: String? = null, redirectUrl: String? = null): Payment = transaction {
        val newId = PaymentsTable.insertAndGetId {
            it[orderId] = req.orderId
            it[amount] = req.amount
            it[method] = req.method
            it[status] = PayStatus.INIT
            it[paidAt] = null
            it[PaymentsTable.snapToken] = snapToken
            it[snapRedirectUrl] = redirectUrl
        }

        Payment(
            id = newId.value,
            orderId = req.orderId,
            amount = req.amount,
            method = req.method,
            paidAt = null,
            status = PayStatus.INIT,
            snapToken = snapToken,
            snapRedirectUrl = redirectUrl
        )
    }

    fun findById(id: Long): Payment? = transaction {
        PaymentsTable.select { PaymentsTable.id eq id }
            .singleOrNull()
            ?.let { toPayment(it) }
    }

    fun updateStatus(id: Long, status: PayStatus, paidAt: LocalDateTime): Payment? = transaction {
        PaymentsTable.update({ PaymentsTable.id eq id }) {
            it[PaymentsTable.status] = status
            it[PaymentsTable.paidAt] = paidAt
        }

        PaymentsTable.select { PaymentsTable.id eq id }
            .singleOrNull()
            ?.let { toPayment(it) }
    }

    fun list(): List<Payment> = transaction {
        PaymentsTable.selectAll()
            .map { toPayment(it) }
    }
}

object PaymentsTable : LongIdTable("payments") {
    val orderId = long("order_id")
    val amount = integer("amount")
    val method = enumerationByName("method", 20, PayMethod::class)
    val status = enumerationByName("status", 20, PayStatus::class).default(PayStatus.INIT)
    val paidAt = datetime("paid_at").nullable()
    val snapToken = varchar("snap_token", 255).nullable()
    val snapRedirectUrl = varchar("snap_redirect_url", 500).nullable()
}