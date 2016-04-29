package ni.gob.minsa.laboratorio.domain.examen;

import ni.gob.minsa.laboratorio.domain.audit.Auditable;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.User;
import org.hibernate.annotations.ForeignKey;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by FIRSTICT on 12/2/2014.
 */
@Entity
@Table(name = "catalogo_area", schema = "laboratorio")
public class Area implements Auditable {

    Integer idArea;
    String nombre;
    private boolean pasivo;
    Date fechaRegistro;
    User usuarioRegistro;

    @Id
    @GeneratedValue(strategy= GenerationType.TABLE)
    @Column(name = "ID_AREA", nullable = false, insertable = true, updatable = true)
    public Integer getIdArea() {
        return idArea;
    }

    public void setIdArea(Integer idArea) {
        this.idArea = idArea;
    }

    @Basic
    @Column(name = "NOMBRE", nullable = false, insertable = true, updatable = true, length = 100)
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Basic
    @Column(name = "PASIVO", nullable = false, insertable = true, updatable = true)
    public boolean isPasivo() {
        return pasivo;
    }

    public void setPasivo(boolean pasivo) {
        this.pasivo = pasivo;
    }

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column(name = "FECHA_REGISTRO", nullable = false)
    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @ManyToOne()
    @JoinColumn(name="USUARIO_REGISTRO", referencedColumnName="username", nullable=false)
    @ForeignKey(name = "area_usuario_fk")
    public User getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(User usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }

    @Override
    public boolean isFieldAuditable(String fieldname) {
        if (fieldname.matches("nombre") || fieldname.matches("pasivo"))
            return true;
        else
            return false;
    }

    @Override
    public String toString() {
        return "{" +
                "idArea=" + idArea +
                ", nombre='" + nombre + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Area)) return false;

        Area area = (Area) o;

        if (!idArea.equals(area.idArea)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return idArea.hashCode();
    }
}
