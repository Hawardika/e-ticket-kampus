package app.domain.services

import app.data.repositories.OrderRepository
import app.data.repositories.TicketTypeRepository
import app.domain.models.*
import app.plugins.ValidationException
import app.plugins.NotFoundException

class OrderService(
    private val orderRepo: OrderRepository = OrderRepository(),
    private val ticketRepo: TicketTypeRepository = TicketTypeRepository()
) {

    private fun toResponse(order: Order): OrderResponse {
        return OrderResponse(
            id = order.id,
            userId = order.userId,
            eventId = order.eventId,
            ticketTypeId = order.ticketTypeId,
            qty = order.qty,
            orderTotal = order.orderTotal,
            status = order.status
        )
    }

    fun create(req: OrderCreateRequest): OrderResponse {
        if (req.qty <= 0) {
            throw ValidationException(mapOf("qty" to "Minimal beli 1 tiket"))
        }

        val ticket = ticketRepo.findById(req.ticketTypeId)
            ?: throw NotFoundException("Tiket tidak ditemukan")

        val success = ticketRepo.reserve(req.ticketTypeId, req.qty)
        if (!success) {
            throw ValidationException(mapOf("quota" to "Stok tiket habis atau tidak cukup"))
        }

        val totalAmount = ticket.price * req.qty

        val savedOrder = orderRepo.insert(req, totalAmount)

        return toResponse(savedOrder)
    }


    fun cancel(orderId: Long) {
        val order = orderRepo.findById(orderId)
            ?: throw NotFoundException("Order tidak ditemukan")

        if (order.status != OrderStatus.PENDING) {
            throw ValidationException(mapOf("status" to "Order tidak bisa dibatalkan karena sudah ${order.status}"))
        }

        orderRepo.updateStatus(orderId, OrderStatus.CANCELLED)

        ticketRepo.release(order.ticketTypeId, order.qty)
    }

    fun markPaid(orderId: Long) {
        val order = orderRepo.findById(orderId)
            ?: throw NotFoundException("Order tidak ditemukan")

        if (order.status == OrderStatus.PAID) {
            return
        }

        orderRepo.updateStatus(orderId, OrderStatus.PAID)
    }

    fun list(): List<OrderResponse> {

        val orders = orderRepo.list()

        return orders.map { toResponse(it) }
    }
}