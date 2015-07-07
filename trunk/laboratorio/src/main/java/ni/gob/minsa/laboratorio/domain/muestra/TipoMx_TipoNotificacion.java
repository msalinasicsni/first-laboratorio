package ni.gob.minsa.laboratorio.domain.muestra;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;
import ni.gob.minsa.laboratorio.domain.notificacion.TipoNotificacion;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.User;
import org.hibernate.annotations.ForeignKey;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by FIRSTICT on 12/15/2014.
 */
@Entity
@Table(name = "tipomx_tiponotifi", schema = "alerta")
public class TipoMx_TipoNotificacion {
    Integer id;
    TipoNotificacion tipoNotificacion;
    TipoMx tipoMx;
    boolean pasivo;
    Date fechaRegistro;
    User usuarioRegistro;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "ID_TIPOMX_NOTIFI", nullable = false, updatable = true, insertable = true, precision = 0)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Catalogo.class, optional = false)
    @JoinColumn(name="COD_TIPONOTI", referencedColumnName = "CODIGO", nullable = false)
    @ForeignKey(name = "TMTN_TIPONOTI_FK")
    public TipoNotificacion getTipoNotificacion() {
        return tipoNotificacion;
    }

    public void setTipoNotificacion(TipoNotificacion tipoNotificacion) {
        this.tipoNotificacion = tipoNotificacion;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name="ID_TIPOMX", referencedColumnName = "ID_TIPOMX", nullable = false)
    @ForeignKey(name = "TMTN_TIPOMX_FK")
    public TipoMx getTipoMx() {
        return tipoMx;
    }

    public void setTipoMx(TipoMx tipoMx) {
        this.tipoMx = tipoMx;
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
    @ForeignKey(name = "fk_TMxNoti_usuario")
    public User getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(User usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }
}
