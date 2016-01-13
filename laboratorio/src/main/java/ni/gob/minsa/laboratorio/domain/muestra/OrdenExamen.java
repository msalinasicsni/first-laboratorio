package ni.gob.minsa.laboratorio.domain.muestra;

import ni.gob.minsa.laboratorio.domain.examen.CatalogoExamenes;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.User;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Miguel Salinas
 * V 1.0
 */
@Entity
@Table(name = "orden_examen", schema = "laboratorio")
public class OrdenExamen {

    private String idOrdenExamen;
    private DaSolicitudDx solicitudDx;
    private Timestamp fechaHOrden;
    private CatalogoExamenes codExamen;
    private User usuarioRegistro;
    private boolean anulado;
    private DaSolicitudEstudio solicitudEstudio;
    private Laboratorio labProcesa;
    private User usuarioAnulacion;
    private String causaAnulacion;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "ID_ORDEN_EXAMEN", nullable = false, insertable = true, updatable = true, length = 36)
    public String getIdOrdenExamen() {
        return idOrdenExamen;
    }

    public void setIdOrdenExamen(String idOrdenExamen) {
        this.idOrdenExamen = idOrdenExamen;
    }

    @ManyToOne(optional = true)
    @JoinColumn(name = "ID_SOLICITUD_DX", referencedColumnName = "ID_SOLICITUD_DX")
    @ForeignKey(name = "SOLICITUD_DX_EX_FK")
    public DaSolicitudDx getSolicitudDx() {
        return solicitudDx;
    }

    public void setSolicitudDx(DaSolicitudDx solicitudDx) {
        this.solicitudDx = solicitudDx;
    }

    @Basic
    @Column(name = "FECHAH_ORDEN", nullable = false, insertable = true, updatable = true)
    public Timestamp getFechaHOrden() {
        return fechaHOrden;
    }

    public void setFechaHOrden(Timestamp fechaHOrden) {
        this.fechaHOrden = fechaHOrden;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_EXAMEN", referencedColumnName = "ID_EXAMEN")
    @ForeignKey(name = "ID_EXA_FK")
    public CatalogoExamenes getCodExamen() {
        return codExamen;
    }

    public void setCodExamen(CatalogoExamenes codExamen) {
        this.codExamen = codExamen;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "USUARIO", referencedColumnName = "username")
    @ForeignKey(name = "USUARIO_FK")
    public User getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(User usarioRegistro) {
        this.usuarioRegistro = usarioRegistro;
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
    @JoinColumn(name = "ID_SOLICITUD_EST", referencedColumnName = "ID_SOLICITUD_EST")
    @ForeignKey(name = "SOLICITUD_EST_EX_FK")
    public DaSolicitudEstudio getSolicitudEstudio() {
        return solicitudEstudio;
    }

    public void setSolicitudEstudio(DaSolicitudEstudio solicitudEstudio) {
        this.solicitudEstudio = solicitudEstudio;
    }

    @ManyToOne(optional = true)
    @JoinColumn(name = "LABORATORIO_PRC", referencedColumnName = "CODIGO")
    @ForeignKey(name = "LABORATORIO_ORDEX_FK")
    public Laboratorio getLabProcesa() {
        return labProcesa;
    }

    public void setLabProcesa(Laboratorio labProcesa) {
        this.labProcesa = labProcesa;
    }

    @ManyToOne(optional = true)
    @JoinColumn(name = "USUARIO_ANUL", referencedColumnName = "username")
    @ForeignKey(name = "USUARIO_ANUL_FK")
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
}
