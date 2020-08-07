package ni.gob.minsa.laboratorio.utilities.reportes;

import java.util.Date;

/**
 * Created by Miguel Salinas on 05/06/2019.
 * V1.0
 */
public class ResultadoVigilancia {

    private String idNotificacion;
    private String codigoTipoNoti;
    private String idTomaMx;
    private String idSolicitud;
    private String codigoMx;
    private String codUnicoMx;
    private String codigoVIH;
    private String expediente;

    private String codigoExpUnico;
    private Date fechaNacimiento;
    private String sexo;
    private String primerNombre;
    private String primerApellido;
    private String segundoNombre;
    private String segundoApellido;
    private String direccionResidencia;
    private String telefonoResidencia;
    private String telefonoMovil;
    private String comunidadResidencia;
    private String identificacion;

    private Integer idTipoMx;
    private String nombreTipoMx;
    private Long codigoSilaisNoti;
    private String nombreSilaisNoti;
    private Long codigoSilaisMx;
    private String nombreSilaisMx;
    private Long codigoUnidadNoti;
    private String nombreUnidadNoti;
    private Long codigoUnidadMx;
    private String nombreUnidadMx;
    private String codigoMuniNoti;
    private String nombreMuniNoti;
    private String codigoMuniMx;
    private String nombreMuniMx;
    private Date fechaInicioSintomas;
    private Date fechaTomaMx;
    private Date fechaAprobacion;
    private String resultadoFinal;
    private String resultadoFinalSecun;

    private Long codigoSilaisResid;
    private String nombreSilaisResid;
    private String codigoMuniResid;
    private String nombreMuniResid;

    private String embarazada;
    private Integer semanasEmbarazo;
    private String urgente;

    public String getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(String idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    public String getCodigoTipoNoti() {
        return codigoTipoNoti;
    }

    public void setCodigoTipoNoti(String codigoTipoNoti) {
        this.codigoTipoNoti = codigoTipoNoti;
    }

    public String getIdTomaMx() {
        return idTomaMx;
    }

    public void setIdTomaMx(String idTomaMx) {
        this.idTomaMx = idTomaMx;
    }

    public String getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(String idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    public String getCodigoMx() {
        return codigoMx;
    }

    public void setCodigoMx(String codigoMx) {
        this.codigoMx = codigoMx;
    }

    public String getCodUnicoMx() {
        return codUnicoMx;
    }

    public void setCodUnicoMx(String codUnicoMx) {
        this.codUnicoMx = codUnicoMx;
    }

    public String getCodigoExpUnico() {
        return codigoExpUnico;
    }

    public void setCodigoExpUnico(String codigoExpUnico) {
        this.codigoExpUnico = codigoExpUnico;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getPrimerNombre() {
        return primerNombre;
    }

    public void setPrimerNombre(String primerNombre) {
        this.primerNombre = primerNombre;
    }

    public String getPrimerApellido() {
        return primerApellido;
    }

    public void setPrimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }

    public String getSegundoNombre() {
        return segundoNombre;
    }

    public void setSegundoNombre(String segundoNombre) {
        this.segundoNombre = segundoNombre;
    }

    public String getSegundoApellido() {
        return segundoApellido;
    }

    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }

    public String getDireccionResidencia() {
        return direccionResidencia;
    }

    public void setDireccionResidencia(String direccionResidencia) {
        this.direccionResidencia = direccionResidencia;
    }

    public String getTelefonoResidencia() {
        return telefonoResidencia;
    }

    public void setTelefonoResidencia(String telefonoResidencia) {
        this.telefonoResidencia = telefonoResidencia;
    }

    public String getTelefonoMovil() {
        return telefonoMovil;
    }

    public void setTelefonoMovil(String telefonoMovil) {
        this.telefonoMovil = telefonoMovil;
    }

    public String getComunidadResidencia() {
        return comunidadResidencia;
    }

    public void setComunidadResidencia(String comunidadResidencia) {
        this.comunidadResidencia = comunidadResidencia;
    }

    public Integer getIdTipoMx() {
        return idTipoMx;
    }

    public void setIdTipoMx(Integer idTipoMx) {
        this.idTipoMx = idTipoMx;
    }

    public String getNombreTipoMx() {
        return nombreTipoMx;
    }

    public void setNombreTipoMx(String nombreTipoMx) {
        this.nombreTipoMx = nombreTipoMx;
    }

    public Long getCodigoSilaisNoti() {
        return codigoSilaisNoti;
    }

    public void setCodigoSilaisNoti(Long codigoSilaisNoti) {
        this.codigoSilaisNoti = codigoSilaisNoti;
    }

    public String getNombreSilaisNoti() {
        return nombreSilaisNoti;
    }

    public void setNombreSilaisNoti(String nombreSilaisNoti) {
        this.nombreSilaisNoti = nombreSilaisNoti;
    }

    public Long getCodigoSilaisMx() {
        return codigoSilaisMx;
    }

    public void setCodigoSilaisMx(Long codigoSilaisMx) {
        this.codigoSilaisMx = codigoSilaisMx;
    }

    public String getNombreSilaisMx() {
        return nombreSilaisMx;
    }

    public void setNombreSilaisMx(String nombreSilaisMx) {
        this.nombreSilaisMx = nombreSilaisMx;
    }

    public Long getCodigoUnidadNoti() {
        return codigoUnidadNoti;
    }

    public void setCodigoUnidadNoti(Long codigoUnidadNoti) {
        this.codigoUnidadNoti = codigoUnidadNoti;
    }

    public String getNombreUnidadNoti() {
        return nombreUnidadNoti;
    }

    public void setNombreUnidadNoti(String nombreUnidadNoti) {
        this.nombreUnidadNoti = nombreUnidadNoti;
    }

    public Long getCodigoUnidadMx() {
        return codigoUnidadMx;
    }

    public void setCodigoUnidadMx(Long codigoUnidadMx) {
        this.codigoUnidadMx = codigoUnidadMx;
    }

    public String getNombreUnidadMx() {
        return nombreUnidadMx;
    }

    public void setNombreUnidadMx(String nombreUnidadMx) {
        this.nombreUnidadMx = nombreUnidadMx;
    }

    public String getCodigoMuniNoti() {
        return codigoMuniNoti;
    }

    public void setCodigoMuniNoti(String codigoMuniNoti) {
        this.codigoMuniNoti = codigoMuniNoti;
    }

    public String getNombreMuniNoti() {
        return nombreMuniNoti;
    }

    public void setNombreMuniNoti(String nombreMuniNoti) {
        this.nombreMuniNoti = nombreMuniNoti;
    }

    public String getCodigoMuniMx() {
        return codigoMuniMx;
    }

    public void setCodigoMuniMx(String codigoMuniMx) {
        this.codigoMuniMx = codigoMuniMx;
    }

    public String getNombreMuniMx() {
        return nombreMuniMx;
    }

    public void setNombreMuniMx(String nombreMuniMx) {
        this.nombreMuniMx = nombreMuniMx;
    }

    public Date getFechaInicioSintomas() {
        return fechaInicioSintomas;
    }

    public void setFechaInicioSintomas(Date fechaInicioSintomas) {
        this.fechaInicioSintomas = fechaInicioSintomas;
    }

    public Date getFechaTomaMx() {
        return fechaTomaMx;
    }

    public void setFechaTomaMx(Date fechaTomaMx) {
        this.fechaTomaMx = fechaTomaMx;
    }

    public Date getFechaAprobacion() {
        return fechaAprobacion;
    }

    public void setFechaAprobacion(Date fechaAprobacion) {
        this.fechaAprobacion = fechaAprobacion;
    }

    public String getResultadoFinal() {
        return resultadoFinal;
    }

    public void setResultadoFinal(String resultadoFinal) {
        this.resultadoFinal = resultadoFinal;
    }

    public String getResultadoFinalSecun() {
        return resultadoFinalSecun;
    }

    public void setResultadoFinalSecun(String resultadoFinalSecun) {
        this.resultadoFinalSecun = resultadoFinalSecun;
    }

    public Long getCodigoSilaisResid() {
        return codigoSilaisResid;
    }

    public void setCodigoSilaisResid(Long codigoSilaisResid) {
        this.codigoSilaisResid = codigoSilaisResid;
    }

    public String getNombreSilaisResid() {
        return nombreSilaisResid;
    }

    public void setNombreSilaisResid(String nombreSilaisResid) {
        this.nombreSilaisResid = nombreSilaisResid;
    }

    public String getCodigoMuniResid() {
        return codigoMuniResid;
    }

    public void setCodigoMuniResid(String codigoMuniResid) {
        this.codigoMuniResid = codigoMuniResid;
    }

    public String getNombreMuniResid() {
        return nombreMuniResid;
    }

    public void setNombreMuniResid(String nombreMuniResid) {
        this.nombreMuniResid = nombreMuniResid;
    }

    public String getEmbarazada() {
        return embarazada;
    }

    public void setEmbarazada(String embarazada) {
        this.embarazada = embarazada;
    }

    public Integer getSemanasEmbarazo() {
        return semanasEmbarazo;
    }

    public void setSemanasEmbarazo(Integer semanasEmbarazo) {
        this.semanasEmbarazo = semanasEmbarazo;
    }

    public String getUrgente() {
        return urgente;
    }

    public void setUrgente(String urgente) {
        this.urgente = urgente;
    }

    public String getCodigoVIH() {
        return codigoVIH;
    }

    public void setCodigoVIH(String codigoVIH) {
        this.codigoVIH = codigoVIH;
    }

    public String getExpediente() {
        return expediente;
    }

    public void setExpediente(String expediente) {
        this.expediente = expediente;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }
}
