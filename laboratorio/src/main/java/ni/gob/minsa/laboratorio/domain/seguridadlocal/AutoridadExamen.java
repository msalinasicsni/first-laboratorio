package ni.gob.minsa.laboratorio.domain.seguridadlocal;

import ni.gob.minsa.laboratorio.domain.audit.Auditable;
import ni.gob.minsa.laboratorio.domain.examen.Area;
import ni.gob.minsa.laboratorio.domain.examen.CatalogoExamenes;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by FIRSTICT on 3/25/2015.
 * V1.0
 */
@Entity
@Table(name = "autoridad_examen", schema = "laboratorio")
public class AutoridadExamen implements Auditable {
    Integer idAutoridadExamen;
    AutoridadArea autoridadArea;
    CatalogoExamenes examen;
    User usuarioRegistro;
    Date fechaRegistro;
    Boolean pasivo;

    @Id
    @GeneratedValue(strategy= GenerationType.TABLE)
    @Column(name = "ID_AUTORIDAD_EXAMEM", nullable = false, insertable = true, updatable = true)
    public Integer getIdAutoridadExamen() {
        return idAutoridadExamen;
    }

    public void setIdAutoridadExamen(Integer idAutoridadExamen) {
        this.idAutoridadExamen = idAutoridadExamen;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name="ID_AUTORIDAD_AREA", referencedColumnName = "ID_AUTORIDAD_AREA", insertable = true, updatable = false)
    @ForeignKey(name = "AUTORIDADEXA_AUTOAREA_FK")
    public AutoridadArea getAutoridadArea() {
        return autoridadArea;
    }

    public void setAutoridadArea(AutoridadArea autoridadArea) {
        this.autoridadArea = autoridadArea;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_EXAMEN", referencedColumnName = "ID_EXAMEN",nullable = false)
    @ForeignKey(name="AUTORIDADEXA_EXAMEN_FK")
    public CatalogoExamenes getExamen() {
        return examen;
    }

    public void setExamen(CatalogoExamenes examen) {
        this.examen = examen;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "USUARIO_REGISTRO", referencedColumnName = "USERNAME",nullable = false)
    @ForeignKey(name="AUTORIDADEXA_USUARIOREG_FK")
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
                "idAutoridadExamen=" + idAutoridadExamen +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AutoridadExamen)) return false;

        AutoridadExamen that = (AutoridadExamen) o;

        if (!idAutoridadExamen.equals(that.idAutoridadExamen)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return idAutoridadExamen.hashCode();
    }
}
