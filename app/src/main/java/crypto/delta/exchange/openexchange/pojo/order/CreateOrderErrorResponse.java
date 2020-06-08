package crypto.delta.exchange.openexchange.pojo.order;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateOrderErrorResponse {

    @SerializedName("error")
    @Expose
    private String error;
    @SerializedName("message")
    @Expose
    private String message;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
