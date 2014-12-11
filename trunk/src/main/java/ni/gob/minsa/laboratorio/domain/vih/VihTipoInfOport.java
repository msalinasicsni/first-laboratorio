package ni.gob.minsa.laboratorio.domain.vih;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by USER on 22/09/2014.
 */
@Entity
@Table(name = "SIVE_VIH_TIPO_INF_OPORT", schema = "ALERTA")
public class VihTipoInfOport {
    private Integer id;
    private String nombre;
    private String descripcion;
    private Timestamp fechaAlta;
    private String usuarioAlta;
    private Timestamp fechaBaja;
    private String usuarioBaja;


    @Id
    @Column(name = "ID", nullable = false, insertable = true, updatable = true, precision = 0)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "NOMBRE", nullable = true, insertable = true, updatable = true, length = 100)
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Basic
    @Column(name = "DESCRIPCION", nullable = true, insertable = true, updatable = true, length = 200)
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Basic
    @Column(name = "FECHA_ALTA", nullable = true, insertable = true, updatable = true)
    public Timestamp getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Timestamp fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    @Basic
    @Column(name = "USUARIO_ALTA", nullable = true, insertable = true, updatable = true, length = 20)
    public String getUsuarioAlta() {
        return usuarioAlta;
    }

    public void setUsuarioAlta(String usuarioAlta) {
        this.usuarioAlta = usuarioAlta;
    }

    @Basic
    @Column(name = "FECHA_BAJA", nullable = true, insertable = true, updatable = true)
    public Timestamp getFechaBaja() {
        return fechaBaja;
    }

    public void setFechaBaja(Timestamp fechaBaja) {
        this.fechaBaja = fechaBaja;
    }

    @Basic
    @Column(name = "USUARIO_BAJA", nullable = true, insertable = true, updatable = true, length = 20)
    public String getUsuarioBaja() {
        return usuarioBaja;
    }

    public void setUsuarioBaja(String usuarioBaja) {
        this.usuarioBaja = usuarioBaja;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VihTipoInfOport that = (VihTipoInfOport) o;

        if (descripcion != null ? !descripcion.equals(that.descripcion) : that.descripcion != null) return false;
        if (fechaAlta != null ? !fechaAlta.equals(that.fechaAlta) : that.fechaAlta != null) return false;
        if (fechaBaja != null ? !fechaBaja.equals(that.fechaBaja) : that.fechaBaja != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (nombre != null ? !nombre.equals(that.nombre) : that.nombre != null) return false;
        if (usuarioAlta != null ? !usuarioAlta.equals(that.usuarioAlta) : that.usuarioAlta != null) return false;
        if (usuarioBaja != null ? !usuarioBaja.equals(that.usuarioBaja) : that.usuarioBaja != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (nombre != null ? nombre.hashCode() : 0);
        result = 31 * result + (descripcion != null ? descripcion.hashCode() : 0);
        result = 31 * result + (fechaAlta != null ? fechaAlta.hashCode() : 0);
        result = 31 * result + (usuarioAlta != null ? usuarioAlta.hashCode() : 0);
        result = 31 * result + (fechaBaja != null ? fechaBaja.hashCode() : 0);
        result = 31 * result + (usuarioBaja != null ? usuarioBaja.hashCode() : 0);
        return result;
    }
}
