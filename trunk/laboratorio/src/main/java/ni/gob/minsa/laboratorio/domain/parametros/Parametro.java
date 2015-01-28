package ni.gob.minsa.laboratorio.domain.parametros;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by FIRSTICT on 1/23/2015.
 */
@Entity
@Table(name = "parametros", schema = "alerta", uniqueConstraints = @UniqueConstraint(columnNames = "NOMBRE"))
public class Parametro implements Serializable {
    private Integer id;
    private String nombre;
    private String valor;
    private String descripcion;

    @Id
    @Column(name = "ID_PARAMETRO", nullable = false, insertable = true, updatable = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "NOMBRE", nullable = false, insertable = true, updatable = true, length = 50)
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Basic
    @Column(name = "DESCRIPCION", nullable = false, insertable = true, updatable = true, length = 200)
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Basic
    @Column(name = "VALOR", nullable = false, insertable = true, updatable = true, length = 100)
    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}
