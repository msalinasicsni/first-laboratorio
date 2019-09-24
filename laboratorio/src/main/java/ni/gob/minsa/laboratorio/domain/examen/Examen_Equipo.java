package ni.gob.minsa.laboratorio.domain.examen;

import ni.gob.minsa.laboratorio.domain.audit.Auditable;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.User;
import org.hibernate.annotations.ForeignKey;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Miguel Salinas on 25/07/2019.
 * V1.0
 */
@Entity
@Table(name = "examen_equipo", schema = "laboratorio")
public class Examen_Equipo implements Auditable {
    private Integer idExamenEquipo;
    private CatalogoExamenes examen;
    private EquiposProcesamiento equipo;
    private boolean pasivo;
    private Date fechaRegistro;
    private User usuarioRegistro;

    @Id
    @GeneratedValue(strategy= GenerationType.TABLE)
    @Column(name = "ID_EXAMEN_DX", nullable = false, insertable = true, updatable = true)
    public Integer getIdExamenEquipo() {
        return idExamenEquipo;
    }

    public void setIdExamenEquipo(Integer idExamenEquipo) {
        this.idExamenEquipo = idExamenEquipo;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_EXAMEN", referencedColumnName = "ID_EXAMEN",nullable = false)
    @ForeignKey(name="EXAMENEQ_EXAMEN_FK")
    public CatalogoExamenes getExamen() {
        return examen;
    }

    public void setExamen(CatalogoExamenes examen) {
        this.examen = examen;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_EQUIPO", referencedColumnName = "ID_EQUIPO",nullable = false)
    @ForeignKey(name="EXAMENEQ_EQUIPO_FK")
    public EquiposProcesamiento getEquipo() {
        return equipo;
    }

    public void setEquipo(EquiposProcesamiento equipo) {
        this.equipo = equipo;
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
    @ForeignKey(name = "EXAMENEQ_USUARIO_FK")
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
        return "Examen_Equipo{" +
                "idExamenEquipo=" + idExamenEquipo +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Examen_Equipo that = (Examen_Equipo) o;

        if (!idExamenEquipo.equals(that.idExamenEquipo)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return idExamenEquipo.hashCode();
    }
}
