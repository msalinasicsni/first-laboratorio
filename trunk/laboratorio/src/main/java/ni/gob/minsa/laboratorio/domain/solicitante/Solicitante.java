package ni.gob.minsa.laboratorio.domain.solicitante;

import ni.gob.minsa.laboratorio.domain.seguridadlocal.User;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by FIRSTICT on 8/3/2015.
 * V1.0
 */
@Entity
@Table(name = "solicitante", schema = "laboratorio", uniqueConstraints = @UniqueConstraint(columnNames = "NOMBRE"))
public class Solicitante {

    private String idSolicitante;
    private String nombre;
    private String direccion;
    private String telefono;
    private String nombreContacto;
    private String telefonoContacto;
    private String correoContacto;
    private boolean pasivo;
    private User usuarioRegistro;
    private Timestamp fechahRegistro;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "ID_SOLICITANTE", nullable = false, insertable = true, updatable = true, length = 36)
    public String getIdSolicitante() {
        return idSolicitante;
    }

    public void setIdSolicitante(String idSolicitante) {
        this.idSolicitante = idSolicitante;
    }

    @Basic
    @Column(name = "NOMBRE", nullable = false, insertable = true, updatable = true, length = 160)
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Basic
    @Column(name = "DIRECCION", nullable = true, insertable = true, updatable = true, length = 256)
    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    @Basic
    @Column(name = "TELEFONO", nullable = false, insertable = true, updatable = true, length = 32)
    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    @Basic
    @Column(name = "NOMBRE_CONTACTO", nullable = false, insertable = true, updatable = true, length = 160)
    public String getNombreContacto() {
        return nombreContacto;
    }

    public void setNombreContacto(String nombreContacto) {
        this.nombreContacto = nombreContacto;
    }

    @Basic
    @Column(name = "TELEFONO_CONTACTO", nullable = false, insertable = true, updatable = true, length = 32)
    public String getTelefonoContacto() {
        return telefonoContacto;
    }

    public void setTelefonoContacto(String telefonoContacto) {
        this.telefonoContacto = telefonoContacto;
    }

    @Basic
    @Column(name = "CORREO_CONTACTO", nullable = true, insertable = true, updatable = true, length = 64)
    public String getCorreoContacto() {
        return correoContacto;
    }

    public void setCorreoContacto(String correoContacto) {
        this.correoContacto = correoContacto;
    }

    @Basic
    @Column(name = "PASIVO", nullable = false, insertable = true)
    public boolean isPasivo() {
        return pasivo;
    }

    public void setPasivo(boolean pasivo) {
        this.pasivo = pasivo;
    }

    @ManyToOne(optional = true)
    @JoinColumn(name = "USUARIO_REGISTRO", referencedColumnName = "username")
    @ForeignKey(name = "USUARIO_REG_SOLICITANTE_FK")
    public User getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(User usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }

    @Basic
    @Column(name = "FECHAH_REGISTRO", nullable = false, insertable = true, updatable = false)
    public Timestamp getFechahRegistro() {
        return fechahRegistro;
    }

    public void setFechahRegistro(Timestamp fechahRegistro) {
        this.fechahRegistro = fechahRegistro;
    }
}
