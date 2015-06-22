package ni.gob.minsa.laboratorio.domain.resultados;

import ni.gob.minsa.laboratorio.domain.muestra.DaSolicitudDx;
import ni.gob.minsa.laboratorio.domain.muestra.DaSolicitudEstudio;
import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
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
@Table(name = "detalle_resultado_final", schema = "laboratorio")
public class DetalleResultadoFinal implements Serializable{

    String idDetalle;
    String valor;
    DaSolicitudDx solicitudDx;
    RespuestaSolicitud respuesta;
    RespuestaExamen respuestaExamen;
    User usuarioRegistro;
    Timestamp fechahRegistro;
    boolean pasivo;
    String razonAnulacion;
    User usuarioAnulacion;
    Timestamp fechahAnulacion;
    DaSolicitudEstudio solicitudEstudio;


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
    @Column(name = "PASIVO", nullable = false, insertable = true)
    public boolean isPasivo() {
        return pasivo;
    }

    public void setPasivo(boolean pasivo) {
        this.pasivo = pasivo;
    }

    @Basic
    @Column(name = "FECHAH_REGISTRO", nullable = false, insertable = true, updatable = false)
    public Timestamp getFechahRegistro() {
        return fechahRegistro;
    }

    public void setFechahRegistro(Timestamp fechahRegistro) {
        this.fechahRegistro = fechahRegistro;
    }

    @Basic
    @Column(name = "FECHAH_ANULACION", nullable = true, insertable = true, updatable = true)
    public Timestamp getFechahAnulacion() {
        return fechahAnulacion;
    }

    public void setFechahAnulacion(Timestamp fechahAnulacion) {
        this.fechahAnulacion = fechahAnulacion;
    }

    @Basic
    @Column(name = "RAZON_ANULACION", nullable = true, insertable = true, updatable = true, length = 500)
    public String getRazonAnulacion() { return razonAnulacion;  }

    public void setRazonAnulacion(String razonAnulacion) { this.razonAnulacion = razonAnulacion; }

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToOne(optional = true)
    @JoinColumn(name = "SOLICITUD_DX", referencedColumnName = "ID_SOLICITUD_DX", nullable = true)
    @ForeignKey(name = "SOLI_FK")
    public DaSolicitudDx getSolicitudDx() { return solicitudDx; }

    public void setSolicitudDx(DaSolicitudDx solicitud) { this.solicitudDx = solicitud; }

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToOne(optional = true)
    @JoinColumn(name = "RESPUESTA", referencedColumnName = "ID_RESPUESTA", nullable = true)
    @ForeignKey(name = "RESP_FK")
    public RespuestaSolicitud getRespuesta() { return respuesta; }

    public void setRespuesta(RespuestaSolicitud respuesta) { this.respuesta = respuesta; }


    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToOne(optional = true)
    @JoinColumn(name = "RESPUESTA_EXAMEN", referencedColumnName = "ID_RESPUESTA", nullable = true)
    @ForeignKey(name = "RESPEX_FK")
    public RespuestaExamen getRespuestaExamen() {
        return respuestaExamen;
    }

    public void setRespuestaExamen(RespuestaExamen respuestaExamen) {
        this.respuestaExamen = respuestaExamen;
    }

    @ManyToOne(optional = true)
    @JoinColumn(name = "USUARIO_REGISTRO", referencedColumnName = "username")
    @ForeignKey(name = "USUARIO_REG_DR_FK")
    public User getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(User usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }

    @ManyToOne(optional = true)
    @JoinColumn(name = "USUARIO_ANULACION", referencedColumnName = "username")
    @ForeignKey(name = "USUARIO_ANUL_DR_FK")
    public User getUsuarioAnulacion() {
        return usuarioAnulacion;
    }

    public void setUsuarioAnulacion(User usuarioAnulacion) {
        this.usuarioAnulacion = usuarioAnulacion;
    }

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToOne(optional = true)
    @JoinColumn(name = "SOLICITUD_EST", referencedColumnName = "ID_SOLICITUD_EST", nullable = true)
    @ForeignKey(name = "RF_SOLI_EST_FK")
    public DaSolicitudEstudio getSolicitudEstudio() {
        return solicitudEstudio;
    }

    public void setSolicitudEstudio(DaSolicitudEstudio solicitudEstudio) {
        this.solicitudEstudio = solicitudEstudio;
    }
}
