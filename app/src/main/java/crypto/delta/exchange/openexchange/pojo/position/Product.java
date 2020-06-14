
package crypto.delta.exchange.openexchange.pojo.position;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Product {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("symbol")
    @Expose
    private String symbol;
    @SerializedName("short_description")
    @Expose
    private Object shortDescription;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("settlement_time")
    @Expose
    private Object settlementTime;
    @SerializedName("product_type")
    @Expose
    private String productType;
    @SerializedName("pricing_source")
    @Expose
    private String pricingSource;
    @SerializedName("impact_size")
    @Expose
    private Integer impactSize;
    @SerializedName("initial_margin")
    @Expose
    private Integer initialMargin;
    @SerializedName("maintenance_margin")
    @Expose
    private String maintenanceMargin;
    @SerializedName("contract_value")
    @Expose
    private String contractValue;
    @SerializedName("contract_unit_currency")
    @Expose
    private String contractUnitCurrency;
    @SerializedName("tick_size")
    @Expose
    private String tickSize;
    @SerializedName("trading_status")
    @Expose
    private String tradingStatus;
    @SerializedName("max_leverage_notional")
    @Expose
    private String maxLeverageNotional;
    @SerializedName("default_leverage")
    @Expose
    private String defaultLeverage;
    @SerializedName("leverage_slider_values")
    @Expose
    private List<Integer> leverageSliderValues = null;
    @SerializedName("initial_margin_scaling_factor")
    @Expose
    private String initialMarginScalingFactor;
    @SerializedName("maintenance_margin_scaling_factor")
    @Expose
    private String maintenanceMarginScalingFactor;
    @SerializedName("commission_rate")
    @Expose
    private String commissionRate;
    @SerializedName("maker_commission_rate")
    @Expose
    private String makerCommissionRate;
    @SerializedName("liquidation_penalty_factor")
    @Expose
    private String liquidationPenaltyFactor;
    @SerializedName("sort_priority")
    @Expose
    private Integer sortPriority;
    @SerializedName("contract_type")
    @Expose
    private String contractType;
    @SerializedName("position_size_limit")
    @Expose
    private Integer positionSizeLimit;
    @SerializedName("basis_factor_max_limit")
    @Expose
    private String basisFactorMaxLimit;
    @SerializedName("strike_price")
    @Expose
    private Object strikePrice;
    @SerializedName("is_quanto")
    @Expose
    private Boolean isQuanto;
    @SerializedName("funding_method")
    @Expose
    private String fundingMethod;
    @SerializedName("annualized_funding")
    @Expose
    private String annualizedFunding;
    @SerializedName("price_clubbing_values")
    @Expose
    private List<Double> priceClubbingValues = null;
    @SerializedName("price_band")
    @Expose
    private String priceBand;
    @SerializedName("ui_config")
    @Expose
    private UiConfig uiConfig;
    @SerializedName("launch_time")
    @Expose
    private Object launchTime;
    @SerializedName("auction_start_time")
    @Expose
    private Object auctionStartTime;
    @SerializedName("auction_finish_time")
    @Expose
    private Object auctionFinishTime;
    @SerializedName("settlement_price")
    @Expose
    private Object settlementPrice;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("product_specs")
    @Expose
    private ProductSpecs productSpecs;
    @SerializedName("insurance_fund_margin_contribution")
    @Expose
    private Object insuranceFundMarginContribution;
    @SerializedName("underlying_asset")
    @Expose
    private UnderlyingAsset underlyingAsset;
    @SerializedName("quoting_asset")
    @Expose
    private QuotingAsset quotingAsset;
    @SerializedName("settling_asset")
    @Expose
    private SettlingAsset settlingAsset;
    @SerializedName("spot_index")
    @Expose
    private SpotIndex spotIndex;

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

    public Object getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(Object shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Object getSettlementTime() {
        return settlementTime;
    }

    public void setSettlementTime(Object settlementTime) {
        this.settlementTime = settlementTime;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getPricingSource() {
        return pricingSource;
    }

    public void setPricingSource(String pricingSource) {
        this.pricingSource = pricingSource;
    }

    public Integer getImpactSize() {
        return impactSize;
    }

    public void setImpactSize(Integer impactSize) {
        this.impactSize = impactSize;
    }

    public Integer getInitialMargin() {
        return initialMargin;
    }

    public void setInitialMargin(Integer initialMargin) {
        this.initialMargin = initialMargin;
    }

    public String getMaintenanceMargin() {
        return maintenanceMargin;
    }

    public void setMaintenanceMargin(String maintenanceMargin) {
        this.maintenanceMargin = maintenanceMargin;
    }

    public String getContractValue() {
        return contractValue;
    }

    public void setContractValue(String contractValue) {
        this.contractValue = contractValue;
    }

    public String getContractUnitCurrency() {
        return contractUnitCurrency;
    }

    public void setContractUnitCurrency(String contractUnitCurrency) {
        this.contractUnitCurrency = contractUnitCurrency;
    }

    public String getTickSize() {
        return tickSize;
    }

    public void setTickSize(String tickSize) {
        this.tickSize = tickSize;
    }

    public String getTradingStatus() {
        return tradingStatus;
    }

    public void setTradingStatus(String tradingStatus) {
        this.tradingStatus = tradingStatus;
    }

    public String getMaxLeverageNotional() {
        return maxLeverageNotional;
    }

    public void setMaxLeverageNotional(String maxLeverageNotional) {
        this.maxLeverageNotional = maxLeverageNotional;
    }

    public String getDefaultLeverage() {
        return defaultLeverage;
    }

    public void setDefaultLeverage(String defaultLeverage) {
        this.defaultLeverage = defaultLeverage;
    }

    public List<Integer> getLeverageSliderValues() {
        return leverageSliderValues;
    }

    public void setLeverageSliderValues(List<Integer> leverageSliderValues) {
        this.leverageSliderValues = leverageSliderValues;
    }

    public String getInitialMarginScalingFactor() {
        return initialMarginScalingFactor;
    }

    public void setInitialMarginScalingFactor(String initialMarginScalingFactor) {
        this.initialMarginScalingFactor = initialMarginScalingFactor;
    }

    public String getMaintenanceMarginScalingFactor() {
        return maintenanceMarginScalingFactor;
    }

    public void setMaintenanceMarginScalingFactor(String maintenanceMarginScalingFactor) {
        this.maintenanceMarginScalingFactor = maintenanceMarginScalingFactor;
    }

    public String getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(String commissionRate) {
        this.commissionRate = commissionRate;
    }

    public String getMakerCommissionRate() {
        return makerCommissionRate;
    }

    public void setMakerCommissionRate(String makerCommissionRate) {
        this.makerCommissionRate = makerCommissionRate;
    }

    public String getLiquidationPenaltyFactor() {
        return liquidationPenaltyFactor;
    }

    public void setLiquidationPenaltyFactor(String liquidationPenaltyFactor) {
        this.liquidationPenaltyFactor = liquidationPenaltyFactor;
    }

    public Integer getSortPriority() {
        return sortPriority;
    }

    public void setSortPriority(Integer sortPriority) {
        this.sortPriority = sortPriority;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public Integer getPositionSizeLimit() {
        return positionSizeLimit;
    }

    public void setPositionSizeLimit(Integer positionSizeLimit) {
        this.positionSizeLimit = positionSizeLimit;
    }

    public String getBasisFactorMaxLimit() {
        return basisFactorMaxLimit;
    }

    public void setBasisFactorMaxLimit(String basisFactorMaxLimit) {
        this.basisFactorMaxLimit = basisFactorMaxLimit;
    }

    public Object getStrikePrice() {
        return strikePrice;
    }

    public void setStrikePrice(Object strikePrice) {
        this.strikePrice = strikePrice;
    }

    public Boolean getIsQuanto() {
        return isQuanto;
    }

    public void setIsQuanto(Boolean isQuanto) {
        this.isQuanto = isQuanto;
    }

    public String getFundingMethod() {
        return fundingMethod;
    }

    public void setFundingMethod(String fundingMethod) {
        this.fundingMethod = fundingMethod;
    }

    public String getAnnualizedFunding() {
        return annualizedFunding;
    }

    public void setAnnualizedFunding(String annualizedFunding) {
        this.annualizedFunding = annualizedFunding;
    }

    public List<Double> getPriceClubbingValues() {
        return priceClubbingValues;
    }

    public void setPriceClubbingValues(List<Double> priceClubbingValues) {
        this.priceClubbingValues = priceClubbingValues;
    }

    public String getPriceBand() {
        return priceBand;
    }

    public void setPriceBand(String priceBand) {
        this.priceBand = priceBand;
    }

    public UiConfig getUiConfig() {
        return uiConfig;
    }

    public void setUiConfig(UiConfig uiConfig) {
        this.uiConfig = uiConfig;
    }

    public Object getLaunchTime() {
        return launchTime;
    }

    public void setLaunchTime(Object launchTime) {
        this.launchTime = launchTime;
    }

    public Object getAuctionStartTime() {
        return auctionStartTime;
    }

    public void setAuctionStartTime(Object auctionStartTime) {
        this.auctionStartTime = auctionStartTime;
    }

    public Object getAuctionFinishTime() {
        return auctionFinishTime;
    }

    public void setAuctionFinishTime(Object auctionFinishTime) {
        this.auctionFinishTime = auctionFinishTime;
    }

    public Object getSettlementPrice() {
        return settlementPrice;
    }

    public void setSettlementPrice(Object settlementPrice) {
        this.settlementPrice = settlementPrice;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public ProductSpecs getProductSpecs() {
        return productSpecs;
    }

    public void setProductSpecs(ProductSpecs productSpecs) {
        this.productSpecs = productSpecs;
    }

    public Object getInsuranceFundMarginContribution() {
        return insuranceFundMarginContribution;
    }

    public void setInsuranceFundMarginContribution(Object insuranceFundMarginContribution) {
        this.insuranceFundMarginContribution = insuranceFundMarginContribution;
    }

    public UnderlyingAsset getUnderlyingAsset() {
        return underlyingAsset;
    }

    public void setUnderlyingAsset(UnderlyingAsset underlyingAsset) {
        this.underlyingAsset = underlyingAsset;
    }

    public QuotingAsset getQuotingAsset() {
        return quotingAsset;
    }

    public void setQuotingAsset(QuotingAsset quotingAsset) {
        this.quotingAsset = quotingAsset;
    }

    public SettlingAsset getSettlingAsset() {
        return settlingAsset;
    }

    public void setSettlingAsset(SettlingAsset settlingAsset) {
        this.settlingAsset = settlingAsset;
    }

    public SpotIndex getSpotIndex() {
        return spotIndex;
    }

    public void setSpotIndex(SpotIndex spotIndex) {
        this.spotIndex = spotIndex;
    }

}
