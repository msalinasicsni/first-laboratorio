package ni.gob.minsa.laboratorio.utilities.reportes;

import java.util.Date;

/**
 * Created by Miguel Salinas on 12/06/2019.
 * V1.0
 */
public class DatosRecepcionMx {

    private String idRecepcion;
    private String tomaMx;
    private Date fechaHoraRecepcion;
    private String tipoRecepcionMx;
    private String usuarioRecepcion;
    private String tipoTubo;
    private String calidadMx;
    boolean cantidadTubosCk;
    boolean tipoMxCk;
    private String causaRechazo;
    private String labRecepcion;
    private String condicionMx;
    private Date fechaRecibido;
    private String horaRecibido;

    public String getIdRecepcion() {
        return idRecepcion;
    }

    public void setIdRecepcion(String idRecepcion) {
        this.idRecepcion = idRecepcion;
    }

    public String getTomaMx() {
        return tomaMx;
    }

    public void setTomaMx(String tomaMx) {
        this.tomaMx = tomaMx;
    }

    public Date getFechaHoraRecepcion() {
        return fechaHoraRecepcion;
    }

    public void setFechaHoraRecepcion(Date fechaHoraRecepcion) {
        this.fechaHoraRecepcion = fechaHoraRecepcion;
    }

    public String getTipoRecepcionMx() {
        return tipoRecepcionMx;
    }

    public void setTipoRecepcionMx(String tipoRecepcionMx) {
        this.tipoRecepcionMx = tipoRecepcionMx;
    }

    public String getUsuarioRecepcion() {
        return usuarioRecepcion;
    }

    public void setUsuarioRecepcion(String usuarioRecepcion) {
        this.usuarioRecepcion = usuarioRecepcion;
    }

    public String getTipoTubo() {
        return tipoTubo;
    }

    public void setTipoTubo(String tipoTubo) {
        this.tipoTubo = tipoTubo;
    }

    public String getCalidadMx() {
        return calidadMx;
    }

    public void setCalidadMx(String calidadMx) {
        this.calidadMx = calidadMx;
    }

    public boolean isCantidadTubosCk() {
        return cantidadTubosCk;
    }

    public void setCantidadTubosCk(boolean cantidadTubosCk) {
        this.cantidadTubosCk = cantidadTubosCk;
    }

    public boolean isTipoMxCk() {
        return tipoMxCk;
    }

    public void setTipoMxCk(boolean tipoMxCk) {
        this.tipoMxCk = tipoMxCk;
    }

    public String getCausaRechazo() {
        return causaRechazo;
    }

    public void setCausaRechazo(String causaRechazo) {
        this.causaRechazo = causaRechazo;
    }

    public String getLabRecepcion() {
        return labRecepcion;
    }

    public void setLabRecepcion(String labRecepcion) {
        this.labRecepcion = labRecepcion;
    }

    public String getCondicionMx() {
        return condicionMx;
    }

    public void setCondicionMx(String condicionMx) {
        this.condicionMx = condicionMx;
    }

    public Date getFechaRecibido() {
        return fechaRecibido;
    }

    public void setFechaRecibido(Date fechaRecibido) {
        this.fechaRecibido = fechaRecibido;
    }

    public String getHoraRecibido() {
        return horaRecibido;
    }

    public void setHoraRecibido(String horaRecibido) {
        this.horaRecibido = horaRecibido;
    }
}
