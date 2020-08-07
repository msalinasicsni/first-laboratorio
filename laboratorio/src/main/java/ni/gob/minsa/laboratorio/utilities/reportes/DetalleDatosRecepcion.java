package ni.gob.minsa.laboratorio.utilities.reportes;

/**
 * Created by miguel on 6/8/2020.
 */
public class DetalleDatosRecepcion {
    String idDetalle;
    String nombre;
    String valor;
    String solicitudDx;
    Integer datoSolicitud;
    String tipoConcepto;

    public String getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(String idDetalle) {
        this.idDetalle = idDetalle;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getSolicitudDx() {
        return solicitudDx;
    }

    public void setSolicitudDx(String solicitudDx) {
        this.solicitudDx = solicitudDx;
    }

    public Integer getDatoSolicitud() {
        return datoSolicitud;
    }

    public void setDatoSolicitud(Integer datoSolicitud) {
        this.datoSolicitud = datoSolicitud;
    }

    public String getTipoConcepto() {
        return tipoConcepto;
    }

    public void setTipoConcepto(String tipoConcepto) {
        this.tipoConcepto = tipoConcepto;
    }
}
