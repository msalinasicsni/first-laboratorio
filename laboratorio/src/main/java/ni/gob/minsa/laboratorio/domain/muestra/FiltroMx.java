package ni.gob.minsa.laboratorio.domain.muestra;

import java.util.Date;

/**
 * Created by FIRSTICT on 11/21/2014.
 */
public class FiltroMx {
    String nombreApellido;
    Date fechaInicioTomaMx;
    Date fechaFinTomaMx;
    String codSilais;
    String codUnidadSalud;
    String codTipoMx;
    String codEstado;
    String idAreaProcesa;
    Date fechaInicioRecep;
    Date fechaFinRecep;
    Date fechaInicioRecepLab;
    Date fechaFinRecepLab;
    Boolean incluirMxInadecuada;
    String codigoUnicoMx;

    public String getNombreApellido() {
        return nombreApellido;
    }

    public void setNombreApellido(String nombreApellido) {
        this.nombreApellido = nombreApellido;
    }

    public Date getFechaInicioTomaMx() {
        return fechaInicioTomaMx;
    }

    public void setFechaInicioTomaMx(Date fechaInicioTomaMx) {
        this.fechaInicioTomaMx = fechaInicioTomaMx;
    }

    public Date getFechaFinTomaMx() {
        return fechaFinTomaMx;
    }

    public void setFechaFinTomaMx(Date fechaFinTomaMx) {
        this.fechaFinTomaMx = fechaFinTomaMx;
    }

    public String getCodSilais() {
        return codSilais;
    }

    public void setCodSilais(String codSilais) {
        this.codSilais = codSilais;
    }

    public String getCodUnidadSalud() {
        return codUnidadSalud;
    }

    public void setCodUnidadSalud(String codUnidadSalud) {
        this.codUnidadSalud = codUnidadSalud;
    }

    public String getCodTipoMx() {
        return codTipoMx;
    }

    public void setCodTipoMx(String codTipoMx) {
        this.codTipoMx = codTipoMx;
    }

    public String getCodEstado() {
        return codEstado;
    }

    public void setCodEstado(String codEstado) {
        this.codEstado = codEstado;
    }

    public String getIdAreaProcesa() {
        return idAreaProcesa;
    }

    public void setIdAreaProcesa(String idAreaProcesa) {
        this.idAreaProcesa = idAreaProcesa;
    }

    public Date getFechaInicioRecep() {
        return fechaInicioRecep;
    }

    public void setFechaInicioRecep(Date fechaInicioRecep) {
        this.fechaInicioRecep = fechaInicioRecep;
    }

    public Date getFechaFinRecep() {
        return fechaFinRecep;
    }

    public void setFechaFinRecep(Date fechaFinRecep) {
        this.fechaFinRecep = fechaFinRecep;
    }

    public Date getFechaInicioRecepLab() { return fechaInicioRecepLab;  }

    public void setFechaInicioRecepLab(Date fechaInicioRecepLab) { this.fechaInicioRecepLab = fechaInicioRecepLab; }

    public Date getFechaFinRecepLab() { return fechaFinRecepLab; }

    public void setFechaFinRecepLab(Date fechaFinRecepLab) { this.fechaFinRecepLab = fechaFinRecepLab; }

    public Boolean getIncluirMxInadecuada() { return incluirMxInadecuada; }

    public void setIncluirMxInadecuada(Boolean incluirMxInadecuada) { this.incluirMxInadecuada = incluirMxInadecuada; }

    public String getCodigoUnicoMx() {
        return codigoUnicoMx;
    }

    public void setCodigoUnicoMx(String codigoUnicoMx) {
        this.codigoUnicoMx = codigoUnicoMx;
    }
}
