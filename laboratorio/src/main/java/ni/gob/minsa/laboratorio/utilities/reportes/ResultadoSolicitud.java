package ni.gob.minsa.laboratorio.utilities.reportes;

import java.util.Date;

/**
 * Created by Miguel Salinas on 12/06/2019.
 * V1.0
 */
public class ResultadoSolicitud {

    private String idDetalle;
    private String respuesta;
    private String tipo;
    private Date fechahProcesa;
    private String valor;
    private String respuestaExamen;
    private String tipoExamen;
    private Date fechahProcesaExamen;

    public String getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(String idDetalle) {
        this.idDetalle = idDetalle;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Date getFechahProcesa() {
        return fechahProcesa;
    }

    public void setFechahProcesa(Date fechahProcesa) {
        this.fechahProcesa = fechahProcesa;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getRespuestaExamen() {
        return respuestaExamen;
    }

    public void setRespuestaExamen(String respuestaExamen) {
        this.respuestaExamen = respuestaExamen;
    }

    public String getTipoExamen() {
        return tipoExamen;
    }

    public void setTipoExamen(String tipoExamen) {
        this.tipoExamen = tipoExamen;
    }

    public Date getFechahProcesaExamen() {
        return fechahProcesaExamen;
    }

    public void setFechahProcesaExamen(Date fechahProcesaExamen) {
        this.fechahProcesaExamen = fechahProcesaExamen;
    }
}
