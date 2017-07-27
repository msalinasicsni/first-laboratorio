package ni.gob.minsa.laboratorio.domain.muestra;

import ni.gob.minsa.laboratorio.domain.audit.Auditable;
import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.User;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by souyen-ics on 11-20-14.
 */
@Entity
@Table(name = "da_solicitud_estudio", schema = "alerta")
public class DaSolicitudEstudio implements Serializable, Auditable {

    private String idSolicitudEstudio;
    private DaTomaMx idTomaMx;
    private Timestamp fechaHSolicitud;
    private Catalogo_Estudio tipoEstudio;
    private Usuarios usarioRegistro;
    private Date fechaAprobacion;
    private User usuarioAprobacion;
    private Boolean aprobada;
    private boolean anulado = false;
    private User usuarioAnulacion;
    private String causaAnulacion;
    private Date fechaAnulacion;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "ID_SOLICITUD_EST", nullable = false, insertable = true, updatable = true, length = 36)
    public String getIdSolicitudEstudio() {
        return idSolicitudEstudio;
    }

    public void setIdSolicitudEstudio(String idSolicitudDx) {
        this.idSolicitudEstudio = idSolicitudDx;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_TOMAMX", referencedColumnName = "ID_TOMAMX")
    @ForeignKey(name = "SOLEST_TOMAMX_FK")
    public DaTomaMx getIdTomaMx() {
        return idTomaMx;
    }

    public void setIdTomaMx(DaTomaMx idTomaMx) {
        this.idTomaMx = idTomaMx;
    }


    @Basic
    @Column(name = "FECHAH_SOLICITUD", nullable = false, insertable = true, updatable = true)
    public Timestamp getFechaHSolicitud() {
        return fechaHSolicitud;
    }

    public void setFechaHSolicitud(Timestamp fechaHSolicitud) {
        this.fechaHSolicitud = fechaHSolicitud;
    }


    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_ESTUDIO", referencedColumnName = "ID_ESTUDIO")
    @ForeignKey(name = "SOLEST_ESTUDIO_FK")
    public Catalogo_Estudio getTipoEstudio() {
        return tipoEstudio;
    }

    public void setTipoEstudio(Catalogo_Estudio codDx) {
        this.tipoEstudio = codDx;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "USUARIO", referencedColumnName = "USUARIO_ID")
    @ForeignKey(name = "USUARIO_FK")
    public Usuarios getUsarioRegistro() {
        return usarioRegistro;
    }

    public void setUsarioRegistro(Usuarios usarioRegistro) {
        this.usarioRegistro = usarioRegistro;
    }

    @Basic
    @Column(name = "FECHA_APROBACION", nullable = true, insertable = true, updatable = true)
    public Date getFechaAprobacion() {
        return fechaAprobacion;
    }

    public void setFechaAprobacion(Date fechaAprobacion) {
        this.fechaAprobacion = fechaAprobacion;
    }

    @ManyToOne(optional = true)
    @JoinColumn(name = "USUARIO_APROBACION", referencedColumnName = "username")
    @ForeignKey(name = "USUARIO_APROBACION_EST_FK")
    public User getUsuarioAprobacion() {
        return usuarioAprobacion;
    }

    public void setUsuarioAprobacion(User usuarioAprobacion) {
        this.usuarioAprobacion = usuarioAprobacion;
    }

    @Basic
    @Column(name = "APROBADA", nullable = true, insertable = true, updatable = true)
    public Boolean getAprobada() {
        return aprobada;
    }

    public void setAprobada(Boolean aprobada) {
        this.aprobada = aprobada;
    }

    @Basic
    @Column(name = "ANULADO", nullable = true, insertable = true, updatable = true)
    public boolean isAnulado() {
        return anulado;
    }

    public void setAnulado(boolean anulado) {
        this.anulado = anulado;
    }

    @ManyToOne(optional = true)
    @JoinColumn(name = "USUARIO_ANULACION", referencedColumnName = "username")
    @ForeignKey(name = "SE_USUARIO_ANUL_FK")
    public User getUsuarioAnulacion() {
        return usuarioAnulacion;
    }

    public void setUsuarioAnulacion(User usuarioAnulacion) {
        this.usuarioAnulacion = usuarioAnulacion;
    }

    @Basic
    @Column(name = "CAUSA_ANULACION", nullable = true, insertable = true, updatable = true, length = 255)
    public String getCausaAnulacion() {
        return causaAnulacion;
    }

    public void setCausaAnulacion(String causaAnulacion) {
        this.causaAnulacion = causaAnulacion;
    }

    @Basic
    @Column(name = "FECHA_ANULACION", nullable = true, insertable = true, updatable = true)
    public Date getFechaAnulacion() {
        return fechaAnulacion;
    }

    public void setFechaAnulacion(Date fechaAnulacion) {
        this.fechaAnulacion = fechaAnulacion;
    }

    @Override
    public boolean isFieldAuditable(String fieldname) {
        return false;
    }

    @Override
    public String toString() {
        return "idSolicitudEstudio='" + idSolicitudEstudio + '\''+
                ", " + idTomaMx +
                ", tipoEstudio=" + tipoEstudio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DaSolicitudEstudio)) return false;

        DaSolicitudEstudio that = (DaSolicitudEstudio) o;

        if (idSolicitudEstudio != null ? !idSolicitudEstudio.equals(that.idSolicitudEstudio) : that.idSolicitudEstudio != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return idSolicitudEstudio != null ? idSolicitudEstudio.hashCode() : 0;
    }
}
