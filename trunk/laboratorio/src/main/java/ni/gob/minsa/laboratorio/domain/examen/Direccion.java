package ni.gob.minsa.laboratorio.domain.examen;

import ni.gob.minsa.laboratorio.domain.muestra.Laboratorio;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;

/**
 * Created by FIRSTICT on 12/2/2014.
 */
@Entity
@Table(name = "catalogo_direccion", schema = "alerta")
public class Direccion {

    Integer idDireccion;
    String nombre;
    Laboratorio laboratorio;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "ID_DIRECCION", nullable = false, insertable = true, updatable = true)
    public Integer getIdDireccion() {
        return idDireccion;
    }

    public void setIdDireccion(Integer idDireccion) {
        this.idDireccion = idDireccion;
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
    @JoinColumn(name = "CODIGO_LAB", referencedColumnName = "CODIGO", nullable = false, insertable = true, updatable = true)
    @ForeignKey(name = "DIRECCION_LABPERTENECE_FK")
    public Laboratorio getLaboratorio() {
        return laboratorio;
    }

    public void setLaboratorio(Laboratorio laboratorio) {
        this.laboratorio = laboratorio;
    }
}
