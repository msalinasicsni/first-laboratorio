package ni.gob.minsa.laboratorio.domain.muestra;

import ni.gob.minsa.laboratorio.domain.audit.Auditable;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.User;
import org.hibernate.annotations.ForeignKey;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by souyen-ics on 11-13-14.
 */

@Entity
@Table(name = "tipo_muestra", schema = "alerta")
    public class TipoMx implements Serializable, Auditable {

    private static final long serialVersionUID = 6373407114599760842L;
    Integer idTipoMx;
    String nombre;
    boolean pasivo;
    private Date fechaRegistro;
    private User usuarioRegistro;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "ID_TIPOMX", nullable = false, insertable = true, updatable = true)
    public Integer getIdTipoMx() {
        return idTipoMx;
    }

    public void setIdTipoMx(Integer idTipoMx) {
        this.idTipoMx = idTipoMx;
    }

    @Basic
    @Column(name = "NOMBRE", nullable = false, insertable = true, updatable = true, length = 200)
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Basic
    @Column(name = "PASIVO", nullable = true, insertable = true, updatable = true)
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
    @ForeignKey(name = "fk_tipoMx_usuarioregistro")
    public User getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(User usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }

    @Override
    public boolean isFieldAuditable(String fieldname) {
        if (fieldname.matches("idTipoMx") || fieldname.matches("fechaRegistro") || fieldname.matches("usuarioRegistro"))
            return  false;
        else
            return true;
    }

    @Override
    public String toString() {
        return "TipoMx{" +
                "idTipoMx=" + idTipoMx +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TipoMx)) return false;

        TipoMx tipoMx = (TipoMx) o;

        if (!idTipoMx.equals(tipoMx.idTipoMx)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return idTipoMx.hashCode();
    }
}


