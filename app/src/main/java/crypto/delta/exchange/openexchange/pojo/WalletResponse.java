package crypto.delta.exchange.openexchange.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WalletResponse {

    @SerializedName("balance")
    @Expose
    private String balance;
    @SerializedName("order_margin")
    @Expose
    private String orderMargin;
    @SerializedName("position_margin")
    @Expose
    private String positionMargin;
    @SerializedName("commission")
    @Expose
    private String commission;
    @SerializedName("available_balance")
    @Expose
    private String availableBalance;
    @SerializedName("trading_fee_credit")
    @Expose
    private String tradingFeeCredit;
    @SerializedName("asset")
    @Expose
    private Asset asset;
    @SerializedName("interest_credit")
    @Expose
    private String interestCredit;

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getOrderMargin() {
        return orderMargin;
    }

    public void setOrderMargin(String orderMargin) {
        this.orderMargin = orderMargin;
    }

    public String getPositionMargin() {
        return positionMargin;
    }

    public void setPositionMargin(String positionMargin) {
        this.positionMargin = positionMargin;
    }

    public String getCommission() {
        return commission;
    }

    public void setCommission(String commission) {
        this.commission = commission;
    }

    public String getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(String availableBalance) {
        this.availableBalance = availableBalance;
    }

    public String getTradingFeeCredit() {
        return tradingFeeCredit;
    }

    public void setTradingFeeCredit(String tradingFeeCredit) {
        this.tradingFeeCredit = tradingFeeCredit;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public String getInterestCredit() {
        return interestCredit;
    }

    public void setInterestCredit(String interestCredit) {
        this.interestCredit = interestCredit;
    }

}
