package ni.gob.minsa.laboratorio.restServices.ServiciosEnLinea;

/**
 * Created by miguel on 18/2/2021.
 */
public class ServiciosEnLineaResponse {
    private String status;
    private String message;
    private String data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
