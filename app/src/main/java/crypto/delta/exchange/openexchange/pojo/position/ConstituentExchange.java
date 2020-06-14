
package crypto.delta.exchange.openexchange.pojo.position;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ConstituentExchange {

    @SerializedName("weight")
    @Expose
    private Integer weight;
    @SerializedName("exchange")
    @Expose
    private String exchange;
    @SerializedName("health_interval")
    @Expose
    private Integer healthInterval;

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public Integer getHealthInterval() {
        return healthInterval;
    }

    public void setHealthInterval(Integer healthInterval) {
        this.healthInterval = healthInterval;
    }

}
