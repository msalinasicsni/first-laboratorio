package ni.gob.minsa.laboratorio.api;

/**
 * Created by Miguel Salinas on 16/05/2019.
 * V1.0
 */
public class RegistroSolicitud {

    private String codTipoNoti;
    private String idSilais;
    private String idUnidadSalud;
    private String fechaInicioSintomas;
    private String urgente;
    private String embarazada;
    private String semanasEmbarazo;
    private String codExpediente;
    private String codExpedienteUnico;
    private String codigoVIH;

    private String idTipoMx;
    private String fechaTomaMx;
    private String horaTomaMx;
    private String volumen;
    private String seguimiento;

    private String diagnosticos;
    private String codigoLab;

    private String idUsuario;

    private DatosVIH datosVIH;

    private DatosTB datosTB;

    public DatosTB getDatosTB() {
        return datosTB;
    }

    public void setDatosTB(DatosTB datosTB) {
        this.datosTB = datosTB;
    }

    public DatosVIH getDatosVIH() {
        return datosVIH;
    }

    public void setDatosVIH(DatosVIH datosVIH) {
        this.datosVIH = datosVIH;
    }

    public String getCodTipoNoti() {
        return codTipoNoti;
    }

    public void setCodTipoNoti(String codTipoNoti) {
        this.codTipoNoti = codTipoNoti;
    }

    public String getIdSilais() {
        return idSilais;
    }

    public void setIdSilais(String idSilais) {
        this.idSilais = idSilais;
    }

    public String getIdUnidadSalud() {
        return idUnidadSalud;
    }

    public void setIdUnidadSalud(String idUnidadSalud) {
        this.idUnidadSalud = idUnidadSalud;
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

    public String getCodigoVIH() {
        return codigoVIH;
    }

    public void setCodigoVIH(String codigoVIH) {
        this.codigoVIH = codigoVIH;
    }

    public String getIdTipoMx() {
        return idTipoMx;
    }

    public void setIdTipoMx(String idTipoMx) {
        this.idTipoMx = idTipoMx;
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
