package crypto.delta.exchange.openexchange.api

import crypto.delta.exchange.openexchange.pojo.DeleteOrderRequest
import crypto.delta.exchange.openexchange.pojo.DeltaExchangeChartHistoryResponse
import crypto.delta.exchange.openexchange.pojo.DeltaExchangeTickerResponse
import crypto.delta.exchange.openexchange.pojo.OrderBookResponse
import crypto.delta.exchange.openexchange.pojo.order.ChangeOrderLeverageBody
import crypto.delta.exchange.openexchange.pojo.order.CreateOrderRequest
import crypto.delta.exchange.openexchange.pojo.order.CreateOrderResponse
import crypto.delta.exchange.openexchange.pojo.position.OpenPositionResponse
import crypto.delta.exchange.openexchange.pojo.products.ProductsResponse
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface DeltaExchangeApiEndPoints {
    @GET("products/ticker/24hr")
    fun getTickers24Hrs(
        @Query("symbol") symbol: String?
    ): Call<DeltaExchangeTickerResponse>

    @GET("chart/history")
    fun getChartHistory(
        @Query("symbol") symbol: String?,
        @Query("resolution") resolution: String?,
        @Query("from") from: String?,
        @Query("to") to: String?
    ): Call<DeltaExchangeChartHistoryResponse>

    @GET("orderbook/{product_id}/l2")
    fun getOrderBook(
        @Path("product_id") product_id: String?
    ): Call<OrderBookResponse>

    @GET("products")
    fun getProducts(): Observable<List<ProductsResponse>>


    @POST("/orders")
    fun createOrder(
        @Header("api-key") apiKey: String,
        @Header("timestamp") timestamp: String,
        @Header("signature") signature: String,
        @Body createOrderRequest: CreateOrderRequest
    ): Call<ResponseBody>

    @GET("/orders/leverage")
    fun getOrderLeverage(
        @Header("api-key") apiKey: String,
        @Header("timestamp") timestamp: String,
        @Header("signature") signature: String,
        @Query("product_id") productId: String?
    ): Call<ResponseBody>

    @POST("/orders/leverage")
    fun setOrderLeverage(
        @Header("api-key") apiKey: String,
        @Header("timestamp") timestamp: String,
        @Header("signature") signature: String,
        @Body changeOrderLeverageBody: ChangeOrderLeverageBody
    ): Call<ResponseBody>

    @GET("/orders")
    fun getOrders(
        @Header("api-key") apiKey: String,
        @Header("timestamp") timestamp: String,
        @Header("signature") signature: String,
        @Query("state") state: String
    ): Call<List<CreateOrderResponse>>

    @GET("/orders")
    fun getStopOrders(
        @Header("api-key") apiKey: String,
        @Header("timestamp") timestamp: String,
        @Header("signature") signature: String,
        @Query("state") state: String,
        @Query("stop_order_type") stopOrderType: String
    ): Call<List<CreateOrderResponse>>

    @GET("/positions")
    fun getOpenPositions(
        @Header("api-key") apiKey: String,
        @Header("timestamp") timestamp: String,
        @Header("signature") signature: String
    ): Call<List<OpenPositionResponse>>


    @GET("/wallet/balances")
    fun getWallet(
        @Header("api-key") apiKey: String,
        @Header("timestamp") timestamp: String,
        @Header("signature") signature: String
    ): Call<ResponseBody>

    @HTTP(method = "DELETE", path = "/orders", hasBody = true)
    fun cancelOrder(
        @Header("api-key") apiKey: String,
        @Header("timestamp") timestamp: String,
        @Header("signature") signature: String,
        @Body deleteOrderRequest: DeleteOrderRequest
    ): Call<ResponseBody>
}