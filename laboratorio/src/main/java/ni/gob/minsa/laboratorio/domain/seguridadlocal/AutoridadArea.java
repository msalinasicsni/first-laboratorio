package ni.gob.minsa.laboratorio.domain.seguridadlocal;

import ni.gob.minsa.laboratorio.domain.audit.Auditable;
import ni.gob.minsa.laboratorio.domain.examen.Area;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by FIRSTICT on 3/25/2015.
 * V1.0
 */
@Entity
@Table(name = "autoridad_area", schema = "laboratorio")
public class AutoridadArea implements Auditable {
    Integer idAutoridadArea;
    User user;
    Area area;
    User usuarioRegistro;
    Date fechaRegistro;
    Boolean pasivo;

    @Id
    @GeneratedValue(strategy= GenerationType.TABLE)
    @Column(name = "ID_AUTORIDAD_AREA", nullable = false, insertable = true, updatable = true)
    public Integer getIdAutoridadArea() {
        return idAutoridadArea;
    }

    public void setIdAutoridadArea(Integer idAutoridadArea) {
        this.idAutoridadArea = idAutoridadArea;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name="USUARIO", insertable = true, updatable = false)
    @ForeignKey(name = "AUTORIDADAREA_USUARIO_FK")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_AREA", referencedColumnName = "ID_AREA",nullable = false)
    @ForeignKey(name="AUTORIDADAREA_AREA_FK")
    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "USUARIO_REGISTRO", referencedColumnName = "USERNAME",nullable = false)
    @ForeignKey(name="AUTORIDADAREA_USUARIOREG_FK")
    public User getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(User usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }

    @Basic
    @Column(name = "FECHA_REGISTRO", nullable = false, insertable = true, updatable = false)
    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @Basic
    @Column(name = "PASIVO", nullable = false, insertable = true, updatable = true)
    public Boolean getPasivo() {
        return pasivo;
    }

    public void setPasivo(Boolean pasivo) {
        this.pasivo = pasivo;
    }

    @Override
    public boolean isFieldAuditable(String fieldname) {
        if (fieldname.matches("fechaRegistro") || fieldname.matches("usuarioRegistro")) return false;
        return  true;
    }

    @Override
    public String toString() {
        return "{" +
                "idAutoridadArea=" + idAutoridadArea +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AutoridadArea)) return false;

        AutoridadArea that = (AutoridadArea) o;

        if (!idAutoridadArea.equals(that.idAutoridadArea)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return idAutoridadArea.hashCode();
    }
}
