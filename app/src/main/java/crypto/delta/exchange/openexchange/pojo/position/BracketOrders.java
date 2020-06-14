
package crypto.delta.exchange.openexchange.pojo.position;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BracketOrders {

    @SerializedName("stop_loss_order")
    @Expose
    private Object stopLossOrder;
    @SerializedName("take_profit_order")
    @Expose
    private Object takeProfitOrder;

    public Object getStopLossOrder() {
        return stopLossOrder;
    }

    public void setStopLossOrder(Object stopLossOrder) {
        this.stopLossOrder = stopLossOrder;
    }

    public Object getTakeProfitOrder() {
        return takeProfitOrder;
    }

    public void setTakeProfitOrder(Object takeProfitOrder) {
        this.takeProfitOrder = takeProfitOrder;
    }

}
