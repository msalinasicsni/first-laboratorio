package ni.gob.minsa.laboratorio.domain.muestra;

import ni.gob.minsa.laboratorio.domain.notificacion.TipoNotificacion;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;

/**
 * Created by FIRSTICT on 12/2/2014.
 */
@Entity
@Table(name = "CATALOGO_LABORATORIOS", schema = "ALERTA")
public class Laboratorio {

    String codigo;
    String nombre;

    @Id
    @Column(name = "CODIGO", nullable = false, insertable = true, updatable = true, length = 10)
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
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
