package ni.gob.minsa.laboratorio.domain.serviciosEnLinea;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * Clase que representa un resultado para dx viajero covid19
 * Created by miguel on 1/2/2021.
 */
@Entity
@Table(name = "SE_RESULTADOS_VIAJEROS", schema = "serviciosenlinea")
public class ResultadoViajero {

    private String idResultado;
    String codigoMx;
    String identificacion;
    String nombres;
    String apellidos;
    Date fechaNacimiento;
    String silais;
    String municipio;
    String unidadSalud;
    String tipoMuestra;
    Date fechaTomaMuestra;
    String examen;
    Date fechaProcesamiento;
    String resultado;
    String procesadoPor;
    String aprobadoPor;
    String codigoValidacion;
    String documentoViaje;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "RESULTADO_ID", nullable = false, insertable = true, updatable = false, length = 36)
    public String getIdResultado() {
        return idResultado;
    }

    public void setIdResultado(String idResultado) {
        this.idResultado = idResultado;
    }

    @Basic
    @Column(name = "CODIGO_MX", nullable = false, insertable = true, updatable = true, length = 16)
    public String getCodigoMx() {
        return codigoMx;
    }

    public void setCodigoMx(String codigoMx) {
        this.codigoMx = codigoMx;
    }

    @Basic
    @Column(name = "IDENTIFICACION", nullable = false, length = 20)
    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    @Basic
    @Column(name = "NOMBRES", nullable = false, length = 50)
    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    @Basic
    @Column(name = "APELLIDOS", nullable = false, length = 50)
    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_NACIMIENTO", nullable = false)
    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    @Basic
    @Column(name = "SILAIS", nullable = false, length = 400)
    public String getSilais() {
        return silais;
    }

    public void setSilais(String silais) {
        this.silais = silais;
    }

    @Basic
    @Column(name = "MUNICIPIO", nullable = false, length = 100)
    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    @Basic
    @Column(name = "UNIDAD_SALUD", nullable = false, length = 400)
    public String getUnidadSalud() {
        return unidadSalud;
    }

    public void setUnidadSalud(String unidadSalud) {
        this.unidadSalud = unidadSalud;
    }

    @Basic
    @Column(name = "TIPO_MUESTRA", nullable = false, length =200)
    public String getTipoMuestra() {
        return tipoMuestra;
    }

    public void setTipoMuestra(String tipoMuestra) {
        this.tipoMuestra = tipoMuestra;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_TOMA_MUESTRA", nullable = false)
    public Date getFechaTomaMuestra() {
        return fechaTomaMuestra;
    }

    public void setFechaTomaMuestra(Date fechaTomaMuestra) {
        this.fechaTomaMuestra = fechaTomaMuestra;
    }

    @Basic
    @Column(name = "EXAMEN", nullable = false, length = 100)
    public String getExamen() {
        return examen;
    }

    public void setExamen(String examen) {
        this.examen = examen;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_PROCESAMIENTO", nullable = false)
    public Date getFechaProcesamiento() {
        return fechaProcesamiento;
    }

    public void setFechaProcesamiento(Date fechaProcesamiento) {
        this.fechaProcesamiento = fechaProcesamiento;
    }

    @Basic
    @Column(name = "RESULTADO", nullable = false, length = 200)
    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    @Basic
    @Column(name = "PROCESADO_POR", nullable = false, length = 100)
    public String getProcesadoPor() {
        return procesadoPor;
    }

    public void setProcesadoPor(String procesadoPor) {
        this.procesadoPor = procesadoPor;
    }

    @Basic
    @Column(name = "APROBADO_POR", nullable = false, length = 100)
    public String getAprobadoPor() {
        return aprobadoPor;
    }

    public void setAprobadoPor(String aprobadoPor) {
        this.aprobadoPor = aprobadoPor;
    }

    @Basic
    @Column(name = "CODIGO_VALIDACION", nullable = false, length = 50)
    public String getCodigoValidacion() {
        return codigoValidacion;
    }

    public void setCodigoValidacion(String codigoValidacion) {
        this.codigoValidacion = codigoValidacion;
    }

    @Basic
    @Column(name = "DOCUMENTO_VIAJE", nullable = true, length = 20)
    public String getDocumentoViaje() {
        return documentoViaje;
    }

    public void setDocumentoViaje(String documentoViaje) {
        this.documentoViaje = documentoViaje;
    }
}
