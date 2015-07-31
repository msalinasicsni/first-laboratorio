package ni.gob.minsa.laboratorio.domain.muestra;

import ni.gob.minsa.laboratorio.domain.seguridadlocal.User;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by souyen-ics.
 */
@Entity
@Table(name = "dato_solicitud_detalle", schema = "laboratorio")
public class DatoSolicitudDetalle implements Serializable{

    String idDetalle;
    String valor;
    DaSolicitudDx solicitudDx;
    DatoSolicitud datoSolicitud;
    User usuarioRegistro;
    Timestamp fechahRegistro;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "ID_DETALLE", nullable = false, insertable = true, updatable = true, length = 36)
    public String getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(String idDetalle) {
        this.idDetalle = idDetalle;
    }

    @Basic
    @Column(name = "VALOR", nullable = false, insertable = true, updatable = true, length = 500)
    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    @Basic
    @Column(name = "FECHAH_REGISTRO", nullable = false, insertable = true, updatable = false)
    public Timestamp getFechahRegistro() {
        return fechahRegistro;
    }

    public void setFechahRegistro(Timestamp fechahRegistro) {
        this.fechahRegistro = fechahRegistro;
    }

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToOne(optional = true)
    @JoinColumn(name = "ID_SOLICITUD_DX", referencedColumnName = "ID_SOLICITUD_DX", nullable = true)
    @ForeignKey(name = "DET_CON_SOLIC_SOLIDX_FK")
    public DaSolicitudDx getSolicitudDx() { return solicitudDx; }

    public void setSolicitudDx(DaSolicitudDx solicitud) { this.solicitudDx = solicitud; }

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToOne(optional = true)
    @JoinColumn(name = "ID_CONCEPTO_SOL", referencedColumnName = "ID_CONCEPTO_SOL", nullable = true)
    @ForeignKey(name = "DET_CON_SOLIC_CONSOLIC_FK")
    public DatoSolicitud getDatoSolicitud() {
        return datoSolicitud;
    }

    public void setDatoSolicitud(DatoSolicitud datoSolicitud) {
        this.datoSolicitud = datoSolicitud;
    }

    @ManyToOne(optional = true)
    @JoinColumn(name = "USUARIO_REGISTRO", referencedColumnName = "username")
    @ForeignKey(name = "DET_CON_SOLIC_USUARIO_REG_FK")
    public User getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(User usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }
}
