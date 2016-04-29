package ni.gob.minsa.laboratorio.domain.muestra;

import ni.gob.minsa.laboratorio.domain.audit.Auditable;
import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.web.servlet.tags.EditorAwareTag;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by souyen-ics on 11-20-14.
 */
@Entity
@Table(name = "da_solicitud_dx", schema = "alerta")
public class DaSolicitudDx implements Auditable {

    private String idSolicitudDx;
    private DaTomaMx idTomaMx;
    private Timestamp fechaHSolicitud;
    private Catalogo_Dx codDx;
    private Usuarios usarioRegistro;
    private Date fechaAprobacion;
    private Usuarios usuarioAprobacion;
    private Boolean aprobada;
    private Boolean controlCalidad;
    private Laboratorio labProcesa;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "ID_SOLICITUD_DX", nullable = false, insertable = true, updatable = true, length = 36)
    public String getIdSolicitudDx() {
        return idSolicitudDx;
    }

    public void setIdSolicitudDx(String idSolicitudDx) {
        this.idSolicitudDx = idSolicitudDx;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_TOMAMX", referencedColumnName = "ID_TOMAMX")
    @ForeignKey(name = "ID_TOMAMX_FK")
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
    @JoinColumn(name = "ID_DIAGNOSTICO", referencedColumnName = "ID_DIAGNOSTICO")
    @ForeignKey(name = "ID_DX_FK")
    public Catalogo_Dx getCodDx() {
        return codDx;
    }

    public void setCodDx(Catalogo_Dx codDx) {
        this.codDx = codDx;
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
    @JoinColumn(name = "USUARIO_APROBACION", referencedColumnName = "USUARIO_ID")
    @ForeignKey(name = "USUARIO_APROBACION_FK")
    public Usuarios getUsuarioAprobacion() {
        return usuarioAprobacion;
    }

    public void setUsuarioAprobacion(Usuarios usuarioAprobacion) {
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
    @Column(name = "CONTROL_CALIDAD", columnDefinition = "number(1,0) default 0", nullable = true, insertable = true, updatable = true)
    public Boolean getControlCalidad() {
        return controlCalidad;
    }

    public void setControlCalidad(Boolean controlCalidad) {
        this.controlCalidad = controlCalidad;
    }

    @ManyToOne(optional = true)
    @JoinColumn(name = "LABORATORIO_PRC", referencedColumnName = "CODIGO")
    @ForeignKey(name = "SOLIC_DX_LABORATORIO_FK")
    public Laboratorio getLabProcesa() {
        return labProcesa;
    }

    public void setLabProcesa(Laboratorio labProcesa) {
        this.labProcesa = labProcesa;
    }

    @Override
    public boolean isFieldAuditable(String fieldname) {
        return true;
    }

    @Override
    public String toString() {
        return "idSolicitudDx='" + idSolicitudDx + '\'' +
                ", " + idTomaMx +
                ", codDx=" + codDx ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DaSolicitudDx)) return false;

        DaSolicitudDx that = (DaSolicitudDx) o;

        if (idSolicitudDx != null ? !idSolicitudDx.equals(that.idSolicitudDx) : that.idSolicitudDx != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return idSolicitudDx != null ? idSolicitudDx.hashCode() : 0;
    }
}
