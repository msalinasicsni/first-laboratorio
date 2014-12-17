package ni.gob.minsa.laboratorio.domain.examen;

import javax.persistence.*;

/**
 * Created by FIRSTICT on 12/2/2014.
 */
@Entity
@Table(name = "catalogo_direccion", schema = "alerta")
public class Direccion {

    Integer idDireccion;
    String nombre;

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

}
