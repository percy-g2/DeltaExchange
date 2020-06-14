
package crypto.delta.exchange.openexchange.pojo.position;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OpenPositionResponse {

    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("size")
    @Expose
    private Integer size;
    @SerializedName("entry_price")
    @Expose
    private String entryPrice;
    @SerializedName("margin")
    @Expose
    private String margin;
    @SerializedName("liquidation_price")
    @Expose
    private String liquidationPrice;
    @SerializedName("bankruptcy_price")
    @Expose
    private String bankruptcyPrice;
    @SerializedName("adl_level")
    @Expose
    private Integer adlLevel;
    @SerializedName("auto_topup")
    @Expose
    private Boolean autoTopup;
    @SerializedName("bracket_orders")
    @Expose
    private BracketOrders bracketOrders;
    @SerializedName("realized_pnl")
    @Expose
    private String realizedPnl;
    @SerializedName("realized_funding")
    @Expose
    private String realizedFunding;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("product")
    @Expose
    private Product product;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getEntryPrice() {
        return entryPrice;
    }

    public void setEntryPrice(String entryPrice) {
        this.entryPrice = entryPrice;
    }

    public String getMargin() {
        return margin;
    }

    public void setMargin(String margin) {
        this.margin = margin;
    }

    public String getLiquidationPrice() {
        return liquidationPrice;
    }

    public void setLiquidationPrice(String liquidationPrice) {
        this.liquidationPrice = liquidationPrice;
    }

    public String getBankruptcyPrice() {
        return bankruptcyPrice;
    }

    public void setBankruptcyPrice(String bankruptcyPrice) {
        this.bankruptcyPrice = bankruptcyPrice;
    }

    public Integer getAdlLevel() {
        return adlLevel;
    }

    public void setAdlLevel(Integer adlLevel) {
        this.adlLevel = adlLevel;
    }

    public Boolean getAutoTopup() {
        return autoTopup;
    }

    public void setAutoTopup(Boolean autoTopup) {
        this.autoTopup = autoTopup;
    }

    public BracketOrders getBracketOrders() {
        return bracketOrders;
    }

    public void setBracketOrders(BracketOrders bracketOrders) {
        this.bracketOrders = bracketOrders;
    }

    public String getRealizedPnl() {
        return realizedPnl;
    }

    public void setRealizedPnl(String realizedPnl) {
        this.realizedPnl = realizedPnl;
    }

    public String getRealizedFunding() {
        return realizedFunding;
    }

    public void setRealizedFunding(String realizedFunding) {
        this.realizedFunding = realizedFunding;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

}
