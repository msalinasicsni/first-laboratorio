package ni.gob.minsa.laboratorio.domain.seguridadlocal;

import ni.gob.minsa.laboratorio.domain.audit.Auditable;
import ni.gob.minsa.laboratorio.domain.examen.Departamento;
import ni.gob.minsa.laboratorio.domain.muestra.Laboratorio;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by FIRSTICT on 3/25/2015.
 * V1.0
 */
@Entity
@Table(name = "autoridad_departamento", schema = "laboratorio")
public class AutoridadDepartamento implements Auditable {
    Integer idAutoridadDepa;
    User user;
    Departamento departamento;
    User usuarioRegistro;
    Date fechaRegistro;
    Boolean pasivo;

    @Id
    @GeneratedValue(strategy= GenerationType.TABLE)
    @Column(name = "ID_AUTORIDAD_DEPA", nullable = false, insertable = true, updatable = true)
    public Integer getIdAutoridadDepa() {
        return idAutoridadDepa;
    }

    public void setIdAutoridadDepa(Integer idAutoridadDepa) {
        this.idAutoridadDepa = idAutoridadDepa;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name="USUARIO", insertable = true, updatable = false)
    @ForeignKey(name = "AUTORIDADDEPA_USUARIO_FK")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_DEPARTAMENTO", referencedColumnName = "ID_DEPARTAMENTO",nullable = false)
    @ForeignKey(name="AUTORIDADDEPA_DEPARTAMENTO_FK")
    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "USUARIO_REGISTRO", referencedColumnName = "USERNAME",nullable = false)
    @ForeignKey(name="AUTORIDADDEPA_USUARIOREG_FK")
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
                "idAutoridadDepa=" + idAutoridadDepa +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AutoridadDepartamento)) return false;

        AutoridadDepartamento that = (AutoridadDepartamento) o;

        if (!idAutoridadDepa.equals(that.idAutoridadDepa)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return idAutoridadDepa.hashCode();
    }
}
