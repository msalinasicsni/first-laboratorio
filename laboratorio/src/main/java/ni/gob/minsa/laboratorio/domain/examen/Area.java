package ni.gob.minsa.laboratorio.domain.examen;

import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;

/**
 * Created by FIRSTICT on 12/2/2014.
 */
@Entity
@Table(name = "catalogo_area", schema = "alerta")
public class Area {

    Integer idArea;
    String nombre;
    Departamento departamento;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
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

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_DEPARTAMENTO", referencedColumnName = "ID_DEPARTAMENTO",nullable = false)
    @ForeignKey(name="AREA_DEPARTAMENTO_FK")
    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }
}
