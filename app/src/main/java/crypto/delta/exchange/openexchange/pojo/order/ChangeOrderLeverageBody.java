package crypto.delta.exchange.openexchange.pojo.order;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChangeOrderLeverageBody {

    @SerializedName("product_id")
    @Expose
    private Integer productId;
    @SerializedName("leverage")
    @Expose
    private String leverage;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getLeverage() {
        return leverage;
    }

    public void setLeverage(String leverage) {
        this.leverage = leverage;
    }

}
