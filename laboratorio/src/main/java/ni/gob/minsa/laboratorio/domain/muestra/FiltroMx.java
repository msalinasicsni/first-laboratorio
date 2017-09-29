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
    Date fechaInicioRecep;
    Date fechaFinRecep;
    Date fechaInicioRecepLab;
    Date fechaFinRecepLab;
    Boolean incluirMxInadecuada;
    String codigoUnicoMx;
    String codTipoSolicitud;
    String nombreSolicitud;
    String resultado;
    Boolean solicitudAprobada;
    String nombreUsuario;
    Integer nivelLaboratorio;
    Date fechaInicioAprob;
    Date fechaFinAprob;
    Boolean incluirTraslados;
    Boolean controlCalidad;
    String area;
    String resultadoFinal;
    String codLaboratio;
    Date fechaInicioProcesamiento;
    Date fechaFinProcesamiento;
    Date fechaInicioRechazo;
    Date fechaFinRechazo;


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

    public String getCodTipoSolicitud() { return codTipoSolicitud; }

    public void setCodTipoSolicitud(String codTipoSolicitud) { this.codTipoSolicitud = codTipoSolicitud; }

    public String getNombreSolicitud() { return nombreSolicitud; }

    public void setNombreSolicitud(String nombreSolicitud) { this.nombreSolicitud = nombreSolicitud; }

    public String getResultado() { return resultado; }

    public void setResultado(String resultado) { this.resultado = resultado; }

    public Boolean getSolicitudAprobada() {
        return solicitudAprobada;
    }

    public void setSolicitudAprobada(Boolean solicitudAprobada) {
        this.solicitudAprobada = solicitudAprobada;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public Date getFechaInicioAprob() { return fechaInicioAprob; }

    public void setFechaInicioAprob(Date fechaInicioAprob) { this.fechaInicioAprob = fechaInicioAprob; }

    public Date getFechaFinAprob() { return fechaFinAprob; }

    public void setFechaFinAprob(Date fechaFinAprob) { this.fechaFinAprob = fechaFinAprob; }

    /**
     * Los niveles Son: 1=Analista, 2=Jefe Departamento, 3=Director
     * @return Integer
     */
    public Integer getNivelLaboratorio() {
        return nivelLaboratorio;
    }

    public void setNivelLaboratorio(Integer nivelLaboratorio) {
        this.nivelLaboratorio = nivelLaboratorio;
    }

    public Boolean getIncluirTraslados() {
        return incluirTraslados;
    }

    public void setIncluirTraslados(Boolean incluirTraslados) {
        this.incluirTraslados = incluirTraslados;
    }

    public Boolean getControlCalidad() {
        return controlCalidad;
    }

    public void setControlCalidad(Boolean controlCalidad) {
        this.controlCalidad = controlCalidad;
    }

    public String getArea() { return area; }

    public void setArea(String area) { this.area = area; }

    public String getCodLaboratio() {
        return codLaboratio;
    }

    public void setCodLaboratio(String codLaboratio) {
        this.codLaboratio = codLaboratio;
    }

    public String getResultadoFinal() { return resultadoFinal; }

    public void setResultadoFinal(String resultadoFinal) { this.resultadoFinal = resultadoFinal; }

    public Date getFechaInicioProcesamiento() {
        return fechaInicioProcesamiento;
    }

    public void setFechaInicioProcesamiento(Date fechaInicioProcesamiento) {
        this.fechaInicioProcesamiento = fechaInicioProcesamiento;
    }

    public Date getFechaFinProcesamiento() {
        return fechaFinProcesamiento;
    }

    public void setFechaFinProcesamiento(Date fechaFinProcesamiento) {
        this.fechaFinProcesamiento = fechaFinProcesamiento;
    }

    public Date getFechaInicioRechazo() {
        return fechaInicioRechazo;
    }

    public void setFechaInicioRechazo(Date fechaInicioRechazo) {
        this.fechaInicioRechazo = fechaInicioRechazo;
    }

    public Date getFechaFinRechazo() {
        return fechaFinRechazo;
    }

    public void setFechaFinRechazo(Date fechaFinRechazo) {
        this.fechaFinRechazo = fechaFinRechazo;
    }
}
