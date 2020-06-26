package crypto.delta.exchange.openexchange.interfaces

import crypto.delta.exchange.openexchange.pojo.order.CreateOrderResponse

interface OrdersInterface {
    fun onCancelOrder(createOrderResponse: CreateOrderResponse)
}