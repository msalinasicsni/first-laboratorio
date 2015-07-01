package ni.gob.minsa.laboratorio.domain.muestra;

import ni.gob.minsa.laboratorio.domain.seguridadlocal.User;
import org.hibernate.annotations.ForeignKey;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by souyen-ics.
 */
@Entity
@Table(name = "estudio_tipomx_tiponoti", schema = "alerta")
public class Estudio_TipoMx_TipoNoti {

    Integer idEstTipoMxNt;
    Catalogo_Estudio estudio;
    TipoMx_TipoNotificacion tipoMx_tipoNotificacion;
    Boolean pasivo;
    Date fechaRegistro;
    User usuarioRegistro;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "ID_EST_TPMX_NOTI", nullable = false, insertable = true, updatable = false)
    public Integer getIdEstTipoMxNt() {
        return idEstTipoMxNt;
    }

    public void setIdEstTipoMxNt(Integer idDxTipoMxNt) {
        this.idEstTipoMxNt = idDxTipoMxNt;
    }


    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_ESTUDIO", referencedColumnName = "ID_ESTUDIO", nullable = false)
    @ForeignKey(name = "ESTTMXNOTI_ESTUDIO_FK")
    public Catalogo_Estudio getEstudio() {
        return estudio;
    }

    public void setEstudio(Catalogo_Estudio estudio) {
        this.estudio = estudio;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_TIPOMX_NOTIFI", referencedColumnName = "ID_TIPOMX_NOTIFI", nullable = false)
    @ForeignKey(name = "ESTTMXNOTI_TIPOMXTIPONOTI_FK")
    public TipoMx_TipoNotificacion getTipoMx_tipoNotificacion() {
        return tipoMx_tipoNotificacion;
    }

    public void setTipoMx_tipoNotificacion(TipoMx_TipoNotificacion tipoMx_tipoNotificacion) {
        this.tipoMx_tipoNotificacion = tipoMx_tipoNotificacion;
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
    @JoinColumn(name="USUARIO_REGISTRO", referencedColumnName="username", nullable=false)
    @ForeignKey(name = "fk_estTMxNoti_usuario")
    public User getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(User usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }

}

