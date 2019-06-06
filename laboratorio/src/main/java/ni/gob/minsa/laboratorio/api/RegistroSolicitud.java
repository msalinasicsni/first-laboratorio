package ni.gob.minsa.laboratorio.api;

import java.util.Date;

/**
 * Created by Miguel Salinas on 16/05/2019.
 * V1.0
 */
public class RegistroSolicitud {

    private String codTipoNoti;
    private String codSilais;
    private String codUnidadSalud;
    private String fechaInicioSintomas;
    private String urgente;
    private String embarazada;
    private String semanasEmbarazo;
    private String codExpediente;
    private String codExpedienteUnico;

    private String codTipoMx;
    private String fechaTomaMx;
    private String horaTomaMx;
    private String volumen;
    private String seguimiento;

    private String diagnosticos;
    private String codigoLab;

    private String idUsuario;

    public String getCodTipoNoti() {
        return codTipoNoti;
    }

    public void setCodTipoNoti(String codTipoNoti) {
        this.codTipoNoti = codTipoNoti;
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

    public String getFechaInicioSintomas() {
        return fechaInicioSintomas;
    }

    public void setFechaInicioSintomas(String fechaInicioSintomas) {
        this.fechaInicioSintomas = fechaInicioSintomas;
    }

    public String getUrgente() {
        return urgente;
    }

    public void setUrgente(String urgente) {
        this.urgente = urgente;
    }

    public String getEmbarazada() {
        return embarazada;
    }

    public void setEmbarazada(String embarazada) {
        this.embarazada = embarazada;
    }

    public String getSemanasEmbarazo() {
        return semanasEmbarazo;
    }

    public void setSemanasEmbarazo(String semanasEmbarazo) {
        this.semanasEmbarazo = semanasEmbarazo;
    }

    public String getCodExpediente() {
        return codExpediente;
    }

    public void setCodExpediente(String codExpediente) {
        this.codExpediente = codExpediente;
    }

    public String getCodExpedienteUnico() {
        return codExpedienteUnico;
    }

    public void setCodExpedienteUnico(String codExpedienteUnico) {
        this.codExpedienteUnico = codExpedienteUnico;
    }

    public String getCodTipoMx() {
        return codTipoMx;
    }

    public void setCodTipoMx(String codTipoMx) {
        this.codTipoMx = codTipoMx;
    }

    public String getFechaTomaMx() {
        return fechaTomaMx;
    }

    public void setFechaTomaMx(String fechaTomaMx) {
        this.fechaTomaMx = fechaTomaMx;
    }

    public String getHoraTomaMx() {
        return horaTomaMx;
    }

    public void setHoraTomaMx(String horaTomaMx) {
        this.horaTomaMx = horaTomaMx;
    }

    public String getVolumen() {
        return volumen;
    }

    public void setVolumen(String volumen) {
        this.volumen = volumen;
    }

    public String getSeguimiento() {
        return seguimiento;
    }

    public void setSeguimiento(String seguimiento) {
        this.seguimiento = seguimiento;
    }

    public String getDiagnosticos() {
        return diagnosticos;
    }

    public void setDiagnosticos(String diagnosticos) {
        this.diagnosticos = diagnosticos;
    }

    public String getCodigoLab() {
        return codigoLab;
    }

    public void setCodigoLab(String codigoLab) {
        this.codigoLab = codigoLab;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }
}
