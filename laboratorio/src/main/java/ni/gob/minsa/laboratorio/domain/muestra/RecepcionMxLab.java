package ni.gob.minsa.laboratorio.domain.muestra;

import ni.gob.minsa.laboratorio.domain.audit.Auditable;
import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;
import ni.gob.minsa.laboratorio.domain.examen.Area;
import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.User;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by FIRSTICT on 12/9/2014.
 */
@Entity
@Table(name = "recepcion_mx_lab", schema = "laboratorio")
public class RecepcionMxLab {

    String idRecepcion;
    RecepcionMx recepcionMx;
    Timestamp fechaHoraRecepcion;
    User usuarioRecepcion;
    Area area;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "ID_RECEPCION_LAB", nullable = false, insertable = true, updatable = true, length = 36)
    public String getIdRecepcion() {
        return idRecepcion;
    }

    public void setIdRecepcion(String idRecepcion) {
        this.idRecepcion = idRecepcion;
    }

    @Basic
    @Column(name = "FECHAHORA_RECEPCION", nullable = false, insertable = true, updatable = false)
    public Timestamp getFechaHoraRecepcion() {
        return fechaHoraRecepcion;
    }

    public void setFechaHoraRecepcion(Timestamp fechaHoraRecepcion) {
        this.fechaHoraRecepcion = fechaHoraRecepcion;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "USUARIO_RECEPCION", referencedColumnName = "username")
    @ForeignKey(name = "RECEPCION_LAB_USUARIO_FK")
    public User getUsuarioRecepcion() {
        return usuarioRecepcion;
    }

    public void setUsuarioRecepcion(User usuarioRecepcion) {
        this.usuarioRecepcion = usuarioRecepcion;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_RECEPCION", referencedColumnName = "ID_RECEPCION")
    @ForeignKey(name = "RECEPCION_LAB_RECEPGRALMX_FK")
    public RecepcionMx getRecepcionMx() {
        return recepcionMx;
    }

    public void setRecepcionMx(RecepcionMx recepcionMx) {
        this.recepcionMx = recepcionMx;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_AREA", referencedColumnName = "ID_AREA")
    @ForeignKey(name = "RECEPCION_LAB_AREA_FK")
    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    @Override
    public String toString() {
        return "idRecepcion='" + idRecepcion + '\'';
    }
}
