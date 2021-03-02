package ni.gob.minsa.laboratorio.restServices.ServiciosEnLinea;

/**
 * Created by miguel on 18/2/2021.
 */
public class ListaPreRegistroRequest {
    private String fechainicial;
    private String fechafinal;
    private String identificacion;
    private Long preregistroid;
    private String hknhjx;

    public ListaPreRegistroRequest() {
    }

    public ListaPreRegistroRequest(Long preregistroid) {
        this.preregistroid = preregistroid;
    }

    public ListaPreRegistroRequest(String identificacion) {
        this.identificacion = identificacion;
    }

    public ListaPreRegistroRequest(String fechainicial, String fechafinal) {
        this.fechainicial = fechainicial;
        this.fechafinal = fechafinal;
    }

    public String getFechainicial() {
        return fechainicial;
    }

    public void setFechainicial(String fechainicial) {
        this.fechainicial = fechainicial;
    }

    public String getFechafinal() {
        return fechafinal;
    }

    public void setFechafinal(String fechafinal) {
        this.fechafinal = fechafinal;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public Long getPreregistroid() {
        return preregistroid;
    }

    public void setPreregistroid(Long preregistroid) {
        this.preregistroid = preregistroid;
    }

    public String getHknhjx() {
        return hknhjx;
    }

    public void setHknhjx(String hknhjx) {
        this.hknhjx = hknhjx;
    }
}
