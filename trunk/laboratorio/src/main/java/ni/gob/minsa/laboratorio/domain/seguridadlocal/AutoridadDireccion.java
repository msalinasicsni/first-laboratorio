package ni.gob.minsa.laboratorio.domain.seguridadlocal;

import ni.gob.minsa.laboratorio.domain.examen.Direccion;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by FIRSTICT on 3/25/2015.
 * V1.0
 */
@Entity
@Table(name = "autoridad_direccion", schema = "laboratorio")
public class AutoridadDireccion {
    Integer idAutoridadDirec;
    User user;
    Direccion direccion;
    User usuarioRegistro;
    Date fechaRegistro;
    Boolean pasivo;

    @Id
    @GeneratedValue(strategy= GenerationType.TABLE)
    @Column(name = "ID_AUTORIDAD_DIR", nullable = false, insertable = true, updatable = true)
    public Integer getIdAutoridadDirec() {
        return idAutoridadDirec;
    }

    public void setIdAutoridadDirec(Integer idAutoridadDirec) {
        this.idAutoridadDirec = idAutoridadDirec;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name="USUARIO", insertable = true, updatable = false)
    @ForeignKey(name = "AUTORIDADDIREC_USUARIO_FK")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_DIRECCION", referencedColumnName = "ID_DIRECCION",nullable = false)
    @ForeignKey(name="AUTORIDADDIREC_DIRECCION_FK")
    public Direccion getDireccion() {
        return direccion;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "USUARIO_REGISTRO", referencedColumnName = "USERNAME",nullable = false)
    @ForeignKey(name="AUTORIDADDIREC_USUARIOREG_FK")
    public User getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(User usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }

    @Basic
    @Column(name = "FECHA_REGISTRO", nullable = false, insertable = true, updatable = false)
    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @Basic
    @Column(name = "PASIVO", nullable = false, insertable = true, updatable = true)
    public Boolean getPasivo() {
        return pasivo;
    }

    public void setPasivo(Boolean pasivo) {
        this.pasivo = pasivo;
    }
}
