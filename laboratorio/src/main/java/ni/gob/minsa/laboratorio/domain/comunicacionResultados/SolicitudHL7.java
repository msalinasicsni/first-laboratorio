package ni.gob.minsa.laboratorio.domain.comunicacionResultados;

import ni.gob.minsa.laboratorio.domain.examen.EquiposProcesamiento;
import ni.gob.minsa.laboratorio.domain.muestra.DaTomaMx;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.User;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "solicitud_hl7", schema = "laboratorio")
public class SolicitudHL7 {

    private String idSolicitud;
    private String idMuestraSecundario;
    private DaTomaMx muestra;
    private EquiposProcesamiento equipo;
    private String examenes;
    private String trama;
    private User usuarioRegistro;
    private Date fechaRegistro;
    private Boolean anulado;
    private User usuarioAnulacion;
    private String causaAnulacion;
    private Date fechaAnulacion;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "ID_SOLICITUD", nullable = false, updatable = false, length = 36)
    public String getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(String idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    @Column(name = "ID_MUESTRA_SECUD", nullable = false, updatable = false, length = 36)
    public String getIdMuestraSecundario() {
        return idMuestraSecundario;
    }

    public void setIdMuestraSecundario(String idMuestraSecundario) {
        this.idMuestraSecundario = idMuestraSecundario;
    }

    @ManyToOne
    @JoinColumn(name = "ID_TOMAMX", referencedColumnName = "ID_TOMAMX")
    @ForeignKey(name = "SOLICITUDHL7_TOMAMX_FK")
    public DaTomaMx getMuestra() {
        return muestra;
    }

    public void setMuestra(DaTomaMx muestra) {
        this.muestra = muestra;
    }

    @ManyToOne
    @JoinColumn(name = "ID_EQUIPO", referencedColumnName = "ID_EQUIPO")
    @ForeignKey(name = "SOLICITUDHL7_EQUIPOPROC_FK")
    public EquiposProcesamiento getEquipo() {
        return equipo;
    }

    public void setEquipo(EquiposProcesamiento equipo) {
        this.equipo = equipo;
    }

    @Column(name = "EXAMENES", length = 100)
    public String getExamenes() {
        return examenes;
    }

    public void setExamenes(String examenes) {
        this.examenes = examenes;
    }

    @Column(name = "TRAMA", length = 1500)
    public String getTrama() {
        return trama;
    }

    public void setTrama(String trama) {
        this.trama = trama;
    }

    @ManyToOne
    @JoinColumn(name = "USUARIO_REGISTRO", referencedColumnName = "username")
    @ForeignKey(name = "SOLICITUDHL7_USUARIO_FK")
    public User getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(User usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }

    @Basic
    @Column(name = "FECHA_REGISTRO")
    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @Column(name = "ANULADO")
    public Boolean isAnulado() {
        return anulado;
    }

    public void setAnulado(Boolean anulado) {
        this.anulado = anulado;
    }

    @ManyToOne
    @JoinColumn(name = "USUARIO_ANULACION", referencedColumnName = "username")
    @ForeignKey(name = "SOLICITUDHL7_USUARIO2_FK")
    public User getUsuarioAnulacion() {
        return usuarioAnulacion;
    }

    public void setUsuarioAnulacion(User usuarioAnulacion) {
        this.usuarioAnulacion = usuarioAnulacion;
    }

    @Column(name = "CAUSA_ANULACION", length = 250)
    public String getCausaAnulacion() {
        return causaAnulacion;
    }

    public void setCausaAnulacion(String causaAnulacion) {
        this.causaAnulacion = causaAnulacion;
    }

    @Basic
    @Column(name = "FECHA_ANULACION")
    public Date getFechaAnulacion() {
        return fechaAnulacion;
    }

    public void setFechaAnulacion(Date fechaAnulacion) {
        this.fechaAnulacion = fechaAnulacion;
    }
}
