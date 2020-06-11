package crypto.delta.exchange.openexchange.pojo.products

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ProductsResponse {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("symbol")
    @Expose
    var symbol: String? = null

    @SerializedName("short_description")
    @Expose
    var shortDescription: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("settlement_time")
    @Expose
    var settlementTime: String? = null

    @SerializedName("product_type")
    @Expose
    var productType: String? = null

    @SerializedName("pricing_source")
    @Expose
    var pricingSource: String? = null

    @SerializedName("impact_size")
    @Expose
    var impactSize: Int? = null

    @SerializedName("initial_margin")
    @Expose
    var initialMargin: Int? = null

    @SerializedName("maintenance_margin")
    @Expose
    var maintenanceMargin: String? = null

    @SerializedName("contract_value")
    @Expose
    var contractValue: String? = null

    @SerializedName("contract_unit_currency")
    @Expose
    var contractUnitCurrency: String? = null

    @SerializedName("tick_size")
    @Expose
    var tickSize: String? = null

    @SerializedName("trading_status")
    @Expose
    var tradingStatus: String? = null

    @SerializedName("max_leverage_notional")
    @Expose
    var maxLeverageNotional: String? = null

    @SerializedName("default_leverage")
    @Expose
    var defaultLeverage: String? = null

    @SerializedName("leverage_slider_values")
    @Expose
    var leverageSliderValues: List<Int>? = null

    @SerializedName("initial_margin_scaling_factor")
    @Expose
    var initialMarginScalingFactor: String? = null

    @SerializedName("maintenance_margin_scaling_factor")
    @Expose
    var maintenanceMarginScalingFactor: String? = null

    @SerializedName("commission_rate")
    @Expose
    var commissionRate: String? = null

    @SerializedName("maker_commission_rate")
    @Expose
    var makerCommissionRate: String? = null

    @SerializedName("liquidation_penalty_factor")
    @Expose
    var liquidationPenaltyFactor: String? = null

    @SerializedName("sort_priority")
    @Expose
    var sortPriority: Int? = null

    @SerializedName("contract_type")
    @Expose
    var contractType: String? = null

    @SerializedName("position_size_limit")
    @Expose
    var positionSizeLimit: Int? = null

    @SerializedName("basis_factor_max_limit")
    @Expose
    var basisFactorMaxLimit: String? = null

    @SerializedName("strike_price")
    @Expose
    var strikePrice: Any? = null

    @SerializedName("is_quanto")
    @Expose
    var isQuanto: Boolean? = null

    @SerializedName("funding_method")
    @Expose
    var fundingMethod: String? = null

    @SerializedName("annualized_funding")
    @Expose
    var annualizedFunding: String? = null

    @SerializedName("price_clubbing_values")
    @Expose
    var priceClubbingValues: List<Double>? = null

    @SerializedName("price_band")
    @Expose
    var priceBand: String? = null

    @SerializedName("ui_config")
    @Expose
    var uiConfig: UiConfig? = null

    @SerializedName("launch_time")
    @Expose
    var launchTime: String? = null

    @SerializedName("auction_start_time")
    @Expose
    var auctionStartTime: Any? = null

    @SerializedName("auction_finish_time")
    @Expose
    var auctionFinishTime: Any? = null

    @SerializedName("settlement_price")
    @Expose
    var settlementPrice: Any? = null

    @SerializedName("state")
    @Expose
    var state: String? = null

    @SerializedName("product_specs")
    @Expose
    var productSpecs: ProductSpecs? = null

    @SerializedName("insurance_fund_margin_contribution")
    @Expose
    var insuranceFundMarginContribution: Int? = null

    @SerializedName("underlying_asset")
    @Expose
    var underlyingAsset: UnderlyingAsset? =
        null

    @SerializedName("quoting_asset")
    @Expose
    var quotingAsset: QuotingAsset? = null

    @SerializedName("settling_asset")
    @Expose
    var settlingAsset: SettlingAsset? =
        null

    @SerializedName("spot_index")
    @Expose
    var spotIndex: SpotIndex? = null

}