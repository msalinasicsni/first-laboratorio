package ni.gob.minsa.laboratorio.domain.examen;

import ni.gob.minsa.laboratorio.domain.audit.Auditable;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.User;
import org.hibernate.annotations.ForeignKey;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Miguel Salinas on 24/07/2019.
 * V1.0
 */
@Entity
@Table(name = "equipos_procesamiento", schema = "laboratorio")
public class EquiposProcesamiento implements Auditable {

    private int idEquipo;
    private String nombre;
    private String marca;
    private String modelo;
    private String descripcion;
    private boolean pasivo;
    private Date fechaRegistro;
    private User usuarioRegistro;

    @Id
    @GeneratedValue(strategy= GenerationType.TABLE)
    @Column(name = "ID_EQUIPO", nullable = false, insertable = true, updatable = true)
    public int getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(int idEquipo) {
        this.idEquipo = idEquipo;
    }

    @Column(name = "NOMBRE", nullable = false, insertable = true, updatable = true, length = 100)
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Column(name = "MARCA", nullable = true, insertable = true, updatable = true, length = 100)
    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    @Column(name = "MODELO", nullable = true, insertable = true, updatable = true, length = 100)
    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    @Column(name = "DESCRIPCION", nullable = true, insertable = true, updatable = true, length = 500)
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Basic
    @Column(name = "PASIVO", nullable = false, insertable = true, updatable = true)
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
    @ForeignKey(name = "EQUIPO_USUARIO_FK")
    public User getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(User usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }

    @Override
    public boolean isFieldAuditable(String fieldname) {
        return false;
    }
}
