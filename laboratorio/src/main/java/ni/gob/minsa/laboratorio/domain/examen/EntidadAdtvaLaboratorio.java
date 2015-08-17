package ni.gob.minsa.laboratorio.domain.examen;

import ni.gob.minsa.laboratorio.domain.estructura.EntidadesAdtvas;
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
@Table(name = "entidad_laboratorio", schema = "laboratorio")
public class EntidadAdtvaLaboratorio {

    Integer idEntidadAdtvaLab;
    Laboratorio laboratorio;
    EntidadesAdtvas entidadAdtva;
    private boolean pasivo;
    Date fechaRegistro;
    User usuarioRegistro;

    @Id
    @GeneratedValue(strategy= GenerationType.TABLE)
    @Column(name = "ID_ENTIDAD_LAB", nullable = false, insertable = true, updatable = true)
    public Integer getIdEntidadAdtvaLab() {
        return idEntidadAdtvaLab;
    }

    public void setIdEntidadAdtvaLab(Integer idEntidadAdtvaLab) {
        this.idEntidadAdtvaLab = idEntidadAdtvaLab;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "CODIGO_LAB", referencedColumnName = "CODIGO",nullable = false)
    @ForeignKey(name="ENTIDADLAB_LAB_FK")
    public Laboratorio getLaboratorio() {
        return laboratorio;
    }

    public void setLaboratorio(Laboratorio laboratorio) {
        this.laboratorio = laboratorio;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "CODIGO_ENTIDAD", referencedColumnName = "CODIGO",nullable = false)
    @ForeignKey(name="ENTIDADLAB_ENTIDADADTVA_FK")
    public EntidadesAdtvas getEntidadAdtva() {
        return entidadAdtva;
    }

    public void setEntidadAdtva(EntidadesAdtvas entidadAdtva) {
        this.entidadAdtva = entidadAdtva;
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
    @ForeignKey(name = "ENTIDADLAB_USUARIO_FK")
    public User getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(User usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }


}
