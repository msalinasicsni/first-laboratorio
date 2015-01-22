package ni.gob.minsa.laboratorio.domain.muestra;

import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by souyen-ics on 11-20-14.
 */
@Entity
@Table(name = "da_solicitud_dx", schema = "alerta")
public class DaSolicitudDx {

    private String idSolicitudDx;
    private DaTomaMx idTomaMx;
    private Timestamp fechaHSolicitud;
    private Catalogo_Dx codDx;
    private Usuarios usarioRegistro;


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
}
