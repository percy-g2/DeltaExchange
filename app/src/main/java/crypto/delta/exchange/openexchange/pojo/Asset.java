package crypto.delta.exchange.openexchange.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Asset {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("symbol")
    @Expose
    private String symbol;
    @SerializedName("precision")
    @Expose
    private Integer precision;
    @SerializedName("minimum_precision")
    @Expose
    private Integer minimumPrecision;
    @SerializedName("sort_priority")
    @Expose
    private Object sortPriority;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("withdrawal_status")
    @Expose
    private Object withdrawalStatus;
    @SerializedName("interest_slabs")
    @Expose
    private Object interestSlabs;
    @SerializedName("interest_credit")
    @Expose
    private Boolean interestCredit;

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

    public Integer getPrecision() {
        return precision;
    }

    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    public Integer getMinimumPrecision() {
        return minimumPrecision;
    }

    public void setMinimumPrecision(Integer minimumPrecision) {
        this.minimumPrecision = minimumPrecision;
    }

    public Object getSortPriority() {
        return sortPriority;
    }

    public void setSortPriority(Object sortPriority) {
        this.sortPriority = sortPriority;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getWithdrawalStatus() {
        return withdrawalStatus;
    }

    public void setWithdrawalStatus(Object withdrawalStatus) {
        this.withdrawalStatus = withdrawalStatus;
    }

    public Object getInterestSlabs() {
        return interestSlabs;
    }

    public void setInterestSlabs(Object interestSlabs) {
        this.interestSlabs = interestSlabs;
    }

    public Boolean getInterestCredit() {
        return interestCredit;
    }

    public void setInterestCredit(Boolean interestCredit) {
        this.interestCredit = interestCredit;
    }

}
