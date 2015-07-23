package ni.gob.minsa.laboratorio.domain.examen;

import ni.gob.minsa.laboratorio.domain.seguridadlocal.User;
import org.hibernate.annotations.ForeignKey;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by FIRSTICT on 12/2/2014.
 */
@Entity
@Table(name = "area_departamento", schema = "laboratorio")
public class AreaDepartamento {

    Integer idAreaDepartamento;
    DepartamentoDireccion depDireccion;
    Area area;
    private boolean pasivo;
    Date fechaRegistro;
    User usuarioRegistro;

    @Id
    @GeneratedValue(strategy= GenerationType.TABLE)
    @Column(name = "ID_AREA_DEPART", nullable = false, insertable = true, updatable = true)
    public Integer getIdAreaDepartamento() {
        return idAreaDepartamento;
    }

    public void setIdAreaDepartamento(Integer idAreaDepartamento) {
        this.idAreaDepartamento = idAreaDepartamento;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_AREA", referencedColumnName = "ID_AREA",nullable = false)
    @ForeignKey(name="AREADEPARTAMENTO_AREA_FK")
    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }



    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_DEPART_DIRECCION", referencedColumnName = "ID_DEPART_DIRECCION",nullable = false)
    @ForeignKey(name="AREADEPARTAMENTO_DEPA_FK")
    public DepartamentoDireccion getDepDireccion() { return depDireccion; }

    public void setDepDireccion(DepartamentoDireccion depDireccion) { this.depDireccion = depDireccion; }

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
    @ForeignKey(name = "AREADEPARTAMENTO_USUARIO_FK")
    public User getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(User usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }


}
