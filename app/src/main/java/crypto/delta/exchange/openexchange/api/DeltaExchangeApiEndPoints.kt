package crypto.delta.exchange.openexchange.api

import crypto.delta.exchange.openexchange.pojo.DeltaExchangeChartHistoryResponse
import crypto.delta.exchange.openexchange.pojo.DeltaExchangeTickerResponse
import crypto.delta.exchange.openexchange.pojo.OrderBookResponse
import crypto.delta.exchange.openexchange.pojo.order.ChangeOrderLeverageBody
import crypto.delta.exchange.openexchange.pojo.order.CreateOrderRequest
import crypto.delta.exchange.openexchange.pojo.order.CreateOrderResponse
import crypto.delta.exchange.openexchange.pojo.order.OrderLeverageResponse
import io.reactivex.Observable
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


    @POST("orders")
    fun createOrder(
        @Header("api-key") apiKey: String,
        @Header("timestamp") timestamp: String,
        @Header("signature") signature: String,
        @Body createOrderRequest: CreateOrderRequest
    ): Observable<CreateOrderResponse>

    @GET("/orders/leverage")
    fun getOrderLeverage(
        @Header("api-key") apiKey: String,
        @Header("timestamp") timestamp: String,
        @Header("signature") signature: String,
        @Query("product_id") productId: String?
    ): Observable<OrderLeverageResponse>

    @POST("/orders/leverage")
    fun setOrderLeverage(
        @Header("api-key") apiKey: String,
        @Header("timestamp") timestamp: String,
        @Header("signature") signature: String,
        @Body changeOrderLeverageBody: ChangeOrderLeverageBody
    ): Observable<OrderLeverageResponse>

    @POST("orders")
    fun getOrders(
        @Header("api-key") apiKey: String,
        @Header("timestamp") timestamp: String,
        @Header("signature") signature: String,
        @Query("product_id") productId: String,
        @Query("state") state: String
    ): Observable<CreateOrderResponse>
}