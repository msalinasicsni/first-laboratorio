package ni.gob.minsa.laboratorio.domain.examen;

import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;

/**
 * Created by FIRSTICT on 12/2/2014.
 */
@Entity
@Table(name = "catalogo_departamento", schema = "alerta")
public class Departamento {

    Integer idDepartamento;
    String nombre;
    Direccion direccion;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "ID_DEPARTAMENTO", nullable = false, insertable = true, updatable = true, precision = 0)
    public Integer getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(Integer idDepartamento) {
        this.idDepartamento = idDepartamento;
    }

    @Basic
    @Column(name = "NOMBRE", nullable = false, insertable = true, updatable = true, length = 100)
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_DIRECCION",referencedColumnName = "ID_DIRECCION", nullable = false)
    @ForeignKey(name="DEPART_DIRECCION_FK")
    public Direccion getDireccion() {
        return direccion;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }
}
