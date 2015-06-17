package ni.gob.minsa.laboratorio.domain.muestra;

import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
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
@Table(name = "rechazo_resultado_solicitud", schema = "laboratorio")
public class RechazoResultadoFinalSolicitud {

    private String idRechazo;
    private DaSolicitudDx solicitudDx;
    private Timestamp fechaHRechazo;
    private User usarioRechazo;
    private DaSolicitudEstudio solicitudEstudio;
    private String causaRechazo;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "ID_RECHAZO", nullable = false, insertable = true, updatable = true, length = 36)
    public String getIdRechazo() {
        return idRechazo;
    }

    public void setIdRechazo(String idOrdenExamen) {
        this.idRechazo = idOrdenExamen;
    }

    @ManyToOne(optional = true)
    @JoinColumn(name = "ID_SOLICITUD_DX", referencedColumnName = "ID_SOLICITUD_DX")
    @ForeignKey(name = "SOLICITUD_DX_RECHA_FK")
    public DaSolicitudDx getSolicitudDx() {
        return solicitudDx;
    }

    public void setSolicitudDx(DaSolicitudDx solicitudDx) {
        this.solicitudDx = solicitudDx;
    }

    @Basic
    @Column(name = "FECHAH_RECHAZO", nullable = false, insertable = true, updatable = true)
    public Timestamp getFechaHRechazo() {
        return fechaHRechazo;
    }

    public void setFechaHRechazo(Timestamp fechaHOrden) {
        this.fechaHRechazo = fechaHOrden;
    }
    @ManyToOne(optional = false)
    @JoinColumn(name = "USUARIO", referencedColumnName = "username")
    @ForeignKey(name = "USUARIO_RECHAZO_FK")
    public User getUsarioRechazo() {
        return usarioRechazo;
    }

    public void setUsarioRechazo(User usarioRegistro) {
        this.usarioRechazo = usarioRegistro;
    }

    @ManyToOne(optional = true)
    @JoinColumn(name = "ID_SOLICITUD_EST", referencedColumnName = "ID_SOLICITUD_EST")
    @ForeignKey(name = "SOLICITUD_EST_RECHA_FK")
    public DaSolicitudEstudio getSolicitudEstudio() {
        return solicitudEstudio;
    }

    public void setSolicitudEstudio(DaSolicitudEstudio solicitudEstudio) {
        this.solicitudEstudio = solicitudEstudio;
    }

    @Basic
    @Column(name = "CAUSA_RECHAZO", nullable = false, insertable = true, updatable = true, length = 512)
    public String getCausaRechazo() {
        return causaRechazo;
    }

    public void setCausaRechazo(String causaRechazo) {
        this.causaRechazo = causaRechazo;
    }
}
