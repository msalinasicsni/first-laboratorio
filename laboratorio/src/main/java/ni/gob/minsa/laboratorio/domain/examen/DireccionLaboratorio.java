package ni.gob.minsa.laboratorio.domain.examen;

import ni.gob.minsa.laboratorio.domain.muestra.Catalogo_Dx;
import ni.gob.minsa.laboratorio.domain.muestra.Laboratorio;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.User;
import org.hibernate.annotations.ForeignKey;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by FIRSTICT on 12/2/2014.
 */
@Entity
@Table(name = "direccion_laboratorio", schema = "laboratorio")
public class DireccionLaboratorio {

    Integer idDireccionLab;
    Laboratorio laboratorio;
    Direccion direccion;
    private boolean pasivo;
    Date fechaRegistro;
    User usuarioRegistro;

    @Id
    @GeneratedValue(strategy= GenerationType.TABLE)
    @Column(name = "ID_DIRECCION_LAB", nullable = false, insertable = true, updatable = true)
    public Integer getIdDireccionLab() {
        return idDireccionLab;
    }

    public void setIdDireccionLab(Integer idDireccionLab) {
        this.idDireccionLab = idDireccionLab;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "CODIGO_LAB", referencedColumnName = "CODIGO",nullable = false)
    @ForeignKey(name="DIRECCIONLAB_LAB_FK")
    public Laboratorio getLaboratorio() {
        return laboratorio;
    }

    public void setLaboratorio(Laboratorio laboratorio) {
        this.laboratorio = laboratorio;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_DIRECCION", referencedColumnName = "ID_DIRECCION",nullable = false)
    @ForeignKey(name="DIRECCIONLAB_DIRECCION_FK")
    public Direccion getDireccion() {
        return direccion;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
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
    @ForeignKey(name = "DIRECCIONLAB_USUARIO_FK")
    public User getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(User usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }


}
