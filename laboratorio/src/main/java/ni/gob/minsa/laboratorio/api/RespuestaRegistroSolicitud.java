package ni.gob.minsa.laboratorio.api;

/**
 * Created by Miguel Salinas on 17/05/2019.
 * V1.0
 */
public class RespuestaRegistroSolicitud {

    private String status;
    private String error;
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

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
