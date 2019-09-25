package ni.gob.minsa.laboratorio.utilities.reportes;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by Miguel Salinas on 11/06/2019.
 * V1.0
 */
public class ResultadoExamen {

    private String idDetalle;
    private String respuesta;
    private String tipo;
    private Date fechahProcesa;
    private String valor;

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
}
