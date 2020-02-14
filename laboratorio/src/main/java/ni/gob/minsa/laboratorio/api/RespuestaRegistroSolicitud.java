package ni.gob.minsa.laboratorio.api;

import java.util.List;

/**
 * Created by Miguel Salinas on 17/05/2019.
 * V1.0
 */
public class RespuestaRegistroSolicitud {

    private String status;
    private String error;
    private String message;
    private List<DatosSolicitud> solicitudes;

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

    public List<DatosSolicitud> getSolicitudes() {
        return solicitudes;
    }

    public void setSolicitudes(List<DatosSolicitud> solicitudes) {
        this.solicitudes = solicitudes;
    }
}
