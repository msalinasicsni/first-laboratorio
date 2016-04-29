package ni.gob.minsa.laboratorio.domain.examen;

import ni.gob.minsa.laboratorio.domain.audit.Auditable;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.User;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by souyen-ics on 11-27-14.
 */
@Entity
@Table(name = "catalogo_examenes", schema = "laboratorio")
public class CatalogoExamenes implements Auditable {

    private Integer idExamen;
    private String nombre;
    private Float precio;
    private boolean pasivo;
    private Timestamp fechaRegistro;
    private User usuarioRegistro;
    private Area area;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "ID_EXAMEN", nullable = false, updatable = true, insertable = true, precision = 0)
    public Integer getIdExamen() {
        return idExamen;
    }

    public void setIdExamen(Integer idExamen) {
        this.idExamen = idExamen;
    }

    @Basic
    @Column(name = "NOMBRE", nullable = false, insertable = true, updatable = true, length = 100, unique = true)
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Column(name = "PRECIO", nullable = true)
    public Float getPrecio() {
        return precio;
    }

    public void setPrecio(Float precio) {
        this.precio = precio;
    }

    @Basic
    @Column(name = "PASIVO", nullable = true, insertable = true, updatable = true)
    public boolean isPasivo() {
        return pasivo;
    }

    public void setPasivo(boolean pasivo) {
        this.pasivo = pasivo;
    }

    @Basic
    @Column(name = "FECHA_REGISTRO", nullable = false, insertable = true, updatable = true)
    public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "USUARIO_REGISTRO", referencedColumnName = "username")
    @ForeignKey(name = "CE_USUARIO_REG_FK")
    public User getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(User usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name="ID_AREA", referencedColumnName = "ID_AREA", nullable = false)
    @ForeignKey(name = "EXAM_AREA_FK")
    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    @Override
    public boolean isFieldAuditable(String fieldname) {
        if (fieldname.matches("fechaRegistro") || fieldname.matches("usuarioRegistro")) return false;
        return  true;
    }

    @Override
    public String toString() {
        return "{" +
                "idExamen=" + idExamen +
                ", nombre='" + nombre + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CatalogoExamenes)) return false;

        CatalogoExamenes examenes = (CatalogoExamenes) o;

        if (!idExamen.equals(examenes.idExamen)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return idExamen.hashCode();
    }
}
