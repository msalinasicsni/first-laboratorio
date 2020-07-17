package ni.gob.minsa.laboratorio.utilities.reportes;

import java.util.Date;
import java.util.List;

public class DatosSolicitud {
    private String idSolicitud;
    private String nombre;
    private String codigoMx;
    private String estadoMx;
    private String tipoMx;
    private String fechaSolicitud;
    private String estadoSolicitud;
    private Boolean aprobada;
    private String fechaAprobacion;
    private List<ValorResultado> resultado;
    private String usuarioAprobacion;

    public String getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(String idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigoMx() {
        return codigoMx;
    }

    public void setCodigoMx(String codigoMx) {
        this.codigoMx = codigoMx;
    }

    public String getEstadoMx() {
        return estadoMx;
    }

    public void setEstadoMx(String estadoMx) {
        this.estadoMx = estadoMx;
    }

    public String getTipoMx() {
        return tipoMx;
    }

    public void setTipoMx(String tipoMx) {
        this.tipoMx = tipoMx;
    }

    public String getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(String fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public String getEstadoSolicitud() {
        return estadoSolicitud;
    }

    public void setEstadoSolicitud(String estadoSolicitud) {
        this.estadoSolicitud = estadoSolicitud;
    }

    public Boolean getAprobada() {
        return aprobada;
    }

    public void setAprobada(Boolean aprobada) {
        this.aprobada = aprobada;
    }

    public String getFechaAprobacion() {
        return fechaAprobacion;
    }

    public void setFechaAprobacion(String fechaAprobacion) {
        this.fechaAprobacion = fechaAprobacion;
    }

    public List<ValorResultado> getResultado() {
        return resultado;
    }

    public void setResultado(List<ValorResultado> resultado) {
        this.resultado = resultado;
    }

    public String getUsuarioAprobacion() {
        return usuarioAprobacion;
    }

    public void setUsuarioAprobacion(String usuarioAprobacion) {
        this.usuarioAprobacion = usuarioAprobacion;
    }
}
