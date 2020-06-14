
package crypto.delta.exchange.openexchange.pojo.position;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UiConfig {

    @SerializedName("sort_priority")
    @Expose
    private Integer sortPriority;
    @SerializedName("show_bracket_orders")
    @Expose
    private Boolean showBracketOrders;
    @SerializedName("price_clubbing_values")
    @Expose
    private List<Double> priceClubbingValues = null;
    @SerializedName("leverage_slider_values")
    @Expose
    private List<Integer> leverageSliderValues = null;
    @SerializedName("default_trading_view_candle")
    @Expose
    private String defaultTradingViewCandle;

    public Integer getSortPriority() {
        return sortPriority;
    }

    public void setSortPriority(Integer sortPriority) {
        this.sortPriority = sortPriority;
    }

    public Boolean getShowBracketOrders() {
        return showBracketOrders;
    }

    public void setShowBracketOrders(Boolean showBracketOrders) {
        this.showBracketOrders = showBracketOrders;
    }

    public List<Double> getPriceClubbingValues() {
        return priceClubbingValues;
    }

    public void setPriceClubbingValues(List<Double> priceClubbingValues) {
        this.priceClubbingValues = priceClubbingValues;
    }

    public List<Integer> getLeverageSliderValues() {
        return leverageSliderValues;
    }

    public void setLeverageSliderValues(List<Integer> leverageSliderValues) {
        this.leverageSliderValues = leverageSliderValues;
    }

    public String getDefaultTradingViewCandle() {
        return defaultTradingViewCandle;
    }

    public void setDefaultTradingViewCandle(String defaultTradingViewCandle) {
        this.defaultTradingViewCandle = defaultTradingViewCandle;
    }

}
