
package crypto.delta.exchange.openexchange.pojo.position;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SpotIndex {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("symbol")
    @Expose
    private String symbol;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("constituent_exchanges")
    @Expose
    private List<ConstituentExchange> constituentExchanges = null;
    @SerializedName("price_method")
    @Expose
    private String priceMethod;
    @SerializedName("health_interval")
    @Expose
    private Integer healthInterval;
    @SerializedName("impact_size")
    @Expose
    private String impactSize;
    @SerializedName("is_composite")
    @Expose
    private Boolean isComposite;
    @SerializedName("constituent_indices")
    @Expose
    private Object constituentIndices;
    @SerializedName("index_type")
    @Expose
    private String indexType;
    @SerializedName("underlying_asset")
    @Expose
    private UnderlyingAsset_ underlyingAsset;
    @SerializedName("quoting_asset")
    @Expose
    private QuotingAsset_ quotingAsset;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ConstituentExchange> getConstituentExchanges() {
        return constituentExchanges;
    }

    public void setConstituentExchanges(List<ConstituentExchange> constituentExchanges) {
        this.constituentExchanges = constituentExchanges;
    }

    public String getPriceMethod() {
        return priceMethod;
    }

    public void setPriceMethod(String priceMethod) {
        this.priceMethod = priceMethod;
    }

    public Integer getHealthInterval() {
        return healthInterval;
    }

    public void setHealthInterval(Integer healthInterval) {
        this.healthInterval = healthInterval;
    }

    public String getImpactSize() {
        return impactSize;
    }

    public void setImpactSize(String impactSize) {
        this.impactSize = impactSize;
    }

    public Boolean getIsComposite() {
        return isComposite;
    }

    public void setIsComposite(Boolean isComposite) {
        this.isComposite = isComposite;
    }

    public Object getConstituentIndices() {
        return constituentIndices;
    }

    public void setConstituentIndices(Object constituentIndices) {
        this.constituentIndices = constituentIndices;
    }

    public String getIndexType() {
        return indexType;
    }

    public void setIndexType(String indexType) {
        this.indexType = indexType;
    }

    public UnderlyingAsset_ getUnderlyingAsset() {
        return underlyingAsset;
    }

    public void setUnderlyingAsset(UnderlyingAsset_ underlyingAsset) {
        this.underlyingAsset = underlyingAsset;
    }

    public QuotingAsset_ getQuotingAsset() {
        return quotingAsset;
    }

    public void setQuotingAsset(QuotingAsset_ quotingAsset) {
        this.quotingAsset = quotingAsset;
    }

}
