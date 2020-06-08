package crypto.delta.exchange.openexchange.pojo.order;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderLeverageResponse {

    @SerializedName("leverage")
    @Expose
    private String leverage;
    @SerializedName("order_margin")
    @Expose
    private String orderMargin;
    @SerializedName("product_id")
    @Expose
    private String productId;

    public String getLeverage() {
        return leverage;
    }

    public void setLeverage(String leverage) {
        this.leverage = leverage;
    }

    public String getOrderMargin() {
        return orderMargin;
    }

    public void setOrderMargin(String orderMargin) {
        this.orderMargin = orderMargin;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

}
