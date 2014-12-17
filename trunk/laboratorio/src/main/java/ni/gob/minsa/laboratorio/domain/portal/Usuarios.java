package ni.gob.minsa.laboratorio.domain.portal;

import org.springframework.context.annotation.Lazy;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by Herrold on 27/05/14 12:39
 */
@Entity
@Table(name = "usuarios", schema = "alerta")
@Lazy(value = false)
public class Usuarios {

    private int usuarioId;
    private String nombre;
    private String username;
    private String clave;
    private String claveTemporal;
    private Timestamp fechaFin;
    private Timestamp fechaRegistro;
    private Timestamp ultimaSesion;
    private Timestamp ultimaModificacion;
    private String pasivo;
    private String email;
    private String referencia;

    @Id
    @Column(name = "USUARIO_ID", nullable = false, insertable = true, updatable = true, precision = 0)
    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    @Basic
    @Column(name = "NOMBRE", nullable = false, insertable = true, updatable = true, length = 100)
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Basic
    @Column(name = "USERNAME", nullable = false, insertable = true, updatable = true, length = 50)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Basic
    @Column(name = "CLAVE", nullable = false, insertable = true, updatable = true, length = 100)
    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    @Basic
    @Column(name = "CLAVE_TEMPORAL", nullable = false, insertable = true, updatable = true, length = 1)
    public String getClaveTemporal() {
        return claveTemporal;
    }

    public void setClaveTemporal(String claveTemporal) {
        this.claveTemporal = claveTemporal;
    }

    @Basic
    @Column(name = "FECHA_FIN", nullable = true, insertable = true, updatable = true)
    public Timestamp getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Timestamp fechaFin) {
        this.fechaFin = fechaFin;
    }

    @Basic
    @Column(name = "FECHA_REGISTRO", nullable = false, insertable = true, updatable = true)
    public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @Basic
    @Column(name = "ULTIMA_SESION", nullable = true, insertable = true, updatable = true)
    public Timestamp getUltimaSesion() {
        return ultimaSesion;
    }

    public void setUltimaSesion(Timestamp ultimaSesion) {
        this.ultimaSesion = ultimaSesion;
    }

    @Basic
    @Column(name = "ULTIMA_MODIFICACION", nullable = false, insertable = true, updatable = true)
    public Timestamp getUltimaModificacion() {
        return ultimaModificacion;
    }

    public void setUltimaModificacion(Timestamp ultimaModificacion) {
        this.ultimaModificacion = ultimaModificacion;
    }

    @Basic
    @Column(name = "PASIVO", nullable = false, insertable = true, updatable = true, length = 1)
    public String getPasivo() {
        return pasivo;
    }

    public void setPasivo(String pasivo) {
        this.pasivo = pasivo;
    }

    @Basic
    @Column(name = "EMAIL", nullable = true, insertable = true, updatable = true, length = 50)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Basic
    @Column(name = "REFERENCIA", nullable = true, insertable = true, updatable = true, length = 200)
    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Usuarios usuarios = (Usuarios) o;

        if (usuarioId != usuarios.usuarioId) return false;
        if (clave != null ? !clave.equals(usuarios.clave) : usuarios.clave != null) return false;
        if (claveTemporal != null ? !claveTemporal.equals(usuarios.claveTemporal) : usuarios.claveTemporal != null)
            return false;
        if (email != null ? !email.equals(usuarios.email) : usuarios.email != null) return false;
        if (fechaFin != null ? !fechaFin.equals(usuarios.fechaFin) : usuarios.fechaFin != null) return false;
        if (fechaRegistro != null ? !fechaRegistro.equals(usuarios.fechaRegistro) : usuarios.fechaRegistro != null)
            return false;
        if (nombre != null ? !nombre.equals(usuarios.nombre) : usuarios.nombre != null) return false;
        if (pasivo != null ? !pasivo.equals(usuarios.pasivo) : usuarios.pasivo != null) return false;
        if (referencia != null ? !referencia.equals(usuarios.referencia) : usuarios.referencia != null) return false;
        if (ultimaModificacion != null ? !ultimaModificacion.equals(usuarios.ultimaModificacion) : usuarios.ultimaModificacion != null)
            return false;
        if (ultimaSesion != null ? !ultimaSesion.equals(usuarios.ultimaSesion) : usuarios.ultimaSesion != null)
            return false;
        if (username != null ? !username.equals(usuarios.username) : usuarios.username != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = usuarioId;
        result = 31 * result + (nombre != null ? nombre.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (clave != null ? clave.hashCode() : 0);
        result = 31 * result + (claveTemporal != null ? claveTemporal.hashCode() : 0);
        result = 31 * result + (fechaFin != null ? fechaFin.hashCode() : 0);
        result = 31 * result + (fechaRegistro != null ? fechaRegistro.hashCode() : 0);
        result = 31 * result + (ultimaSesion != null ? ultimaSesion.hashCode() : 0);
        result = 31 * result + (ultimaModificacion != null ? ultimaModificacion.hashCode() : 0);
        result = 31 * result + (pasivo != null ? pasivo.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (referencia != null ? referencia.hashCode() : 0);
        return result;
    }

}
