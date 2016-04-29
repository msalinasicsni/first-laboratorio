package ni.gob.minsa.laboratorio.domain.examen;

import ni.gob.minsa.laboratorio.domain.audit.Auditable;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.User;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by FIRSTICT on 1/5/2016.
 * V1.0
 */
@Entity
@Table(name = "regla_examen", schema = "laboratorio")
public class ReglaExamen implements Auditable {

    String idRegla;
    CatalogoExamenes examen;
    String descripcion;
    private boolean pasivo;
    Date fechaRegistro;
    User usuarioRegistro;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "ID_REGLA", nullable = false, insertable = true, updatable = true, length = 36)
    public String getIdRegla() {
        return idRegla;
    }

    public void setIdRegla(String idRegla) {
        this.idRegla = idRegla;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_EXAMEN", referencedColumnName = "ID_EXAMEN",nullable = false)
    @ForeignKey(name="REGLA_EXAMEN_FK")
    public CatalogoExamenes getExamen() {
        return examen;
    }

    public void setExamen(CatalogoExamenes examen) {
        this.examen = examen;
    }

    @Basic
    @Column(name = "DESCRIPCION", nullable = false, insertable = true, updatable = true, length = 250)
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Basic
    @Column(name = "PASIVO", nullable = false, insertable = true, updatable = true)
    public boolean isPasivo() {
        return pasivo;
    }

    public void setPasivo(boolean pasivo) {
        this.pasivo = pasivo;
    }

    @Basic
    @Column(name = "FECHA_REGISTRO", nullable = false, insertable = true, updatable = true)
    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @ManyToOne()
    @JoinColumn(name="USUARIO_REGISTRO", referencedColumnName="username", nullable=false)
    @ForeignKey(name = "REGLA_USUARIO_FK")
    public User getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(User usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }

    @Override
    public boolean isFieldAuditable(String fieldname) {
        if (fieldname.matches("fechaRegistro") || fieldname.matches("usuarioRegistro")) return false;
        return  true;
    }

    @Override
    public String toString() {
        return "{" +
                "idRegla='" + idRegla + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReglaExamen)) return false;

        ReglaExamen that = (ReglaExamen) o;

        if (!idRegla.equals(that.idRegla)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return idRegla.hashCode();
    }
}
