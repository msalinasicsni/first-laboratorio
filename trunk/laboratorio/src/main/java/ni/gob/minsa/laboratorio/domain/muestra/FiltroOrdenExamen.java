package ni.gob.minsa.laboratorio.domain.muestra;

import java.util.Date;

/**
 * Created by FIRSTICT on 11/21/2014.
 */
public class FiltroOrdenExamen {
    String nombreApellido;
    Date fechaInicio;
    Date fechaFin;
    String codSilais;
    String codUnidadSalud;
    String codTipoMx;
    String codEstado;
    String idAreaProcesa;

    public String getNombreApellido() {
        return nombreApellido;
    }

    public void setNombreApellido(String nombreApellido) {
        this.nombreApellido = nombreApellido;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
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
}
