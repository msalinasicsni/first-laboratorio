package ni.gob.minsa.laboratorio.api;

/**
 * Created by FIRSTICT on 6/19/2015.
 * V1.0
 */
public class ResultadoELISA {
    private String codigoMx;
    private String idDx;
    private String idExamen;
    private String nombreRespuesta;
    private String valor;
    private String username;

    public String getCodigoMx() {
        return codigoMx;
    }

    public void setCodigoMx(String codigoMx) {
        this.codigoMx = codigoMx;
    }

    public String getIdDx() {
        return idDx;
    }

    public void setIdDx(String idDx) {
        this.idDx = idDx;
    }

    public String getIdExamen() {
        return idExamen;
    }

    public void setIdExamen(String idExamen) {
        this.idExamen = idExamen;
    }

    public String getNombreRespuesta() {
        return nombreRespuesta;
    }

    public void setNombreRespuesta(String nombreRespuesta) {
        this.nombreRespuesta = nombreRespuesta;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
