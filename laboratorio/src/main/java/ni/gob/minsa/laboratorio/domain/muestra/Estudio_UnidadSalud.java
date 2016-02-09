package ni.gob.minsa.laboratorio.domain.muestra;

import ni.gob.minsa.laboratorio.domain.estructura.Unidades;
import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
import org.hibernate.annotations.ForeignKey;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by souyen-ics.
 */
@Entity
@Table(name = "estudio_unidad", schema = "alerta")
public class Estudio_UnidadSalud {

    Integer idEstudioUnidad;
    Catalogo_Estudio estudio;
    Unidades unidad;
    Boolean pasivo;
    Date fechaRegistro;
    Usuarios usuarioRegistro;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "ID_EST_UNIDAD", nullable = false, insertable = true, updatable = false)
    public Integer getIdEstudioUnidad() { return idEstudioUnidad; }

    public void setIdEstudioUnidad(Integer idEstudioUnidad) { this.idEstudioUnidad = idEstudioUnidad; }

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_ESTUDIO", referencedColumnName = "ID_ESTUDIO", nullable = false)
    @ForeignKey(name = "ESTUNIDAD_ESTUDIO_FK")
    public Catalogo_Estudio getEstudio() {
        return estudio;
    }

    public void setEstudio(Catalogo_Estudio estudio) {
        this.estudio = estudio;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "UNIDAD_ID", referencedColumnName = "UNIDAD_ID", nullable = false)
    @ForeignKey(name = "ESTUNIDAD_UNIDAD_FK")
    public Unidades getUnidad() {
        return unidad;
    }

    public void setUnidad(Unidades unidad) {
        this.unidad = unidad;
    }

    @Basic
    @Column(name = "PASIVO", nullable = true, insertable = true, updatable = true)

    public Boolean getPasivo() {
        return pasivo;
    }

    public void setPasivo(Boolean pasivo) {
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
    @JoinColumn(name="USUARIO_REGISTRO", referencedColumnName="USUARIO_ID", nullable=false)
    @ForeignKey(name = "ESTUNIDAD_USUARIO_FK")
    public Usuarios getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(Usuarios usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }

}

