
package crypto.delta.exchange.openexchange.pojo.position;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UnderlyingAsset_ {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("symbol")
    @Expose
    private String symbol;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("precision")
    @Expose
    private Integer precision;
    @SerializedName("minimum_precision")
    @Expose
    private Integer minimumPrecision;
    @SerializedName("sort_priority")
    @Expose
    private Integer sortPriority;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Integer getSortPriority() {
        return sortPriority;
    }

    public void setSortPriority(Integer sortPriority) {
        this.sortPriority = sortPriority;
    }

}
