package ni.gob.minsa.laboratorio.domain.resultados;

import ni.gob.minsa.laboratorio.domain.muestra.DaSolicitudDx;
import ni.gob.minsa.laboratorio.domain.muestra.OrdenExamen;
import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
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
    DaSolicitudDx solicitud;
    RespuestaDx respuesta;
    Usuarios usuarioRegistro;
    Timestamp fechahRegistro;
    boolean pasivo;
    String razonAnulacion;
    Usuarios usuarioAnulacion;
    Timestamp fechahAnulacion;

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
    @ManyToOne(optional = false)
    @JoinColumn(name = "SOLICITUD", referencedColumnName = "ID_SOLICITUD_DX", nullable = false)
    @ForeignKey(name = "SOLI_FK")
    public DaSolicitudDx getSolicitud() { return solicitud; }

    public void setSolicitud(DaSolicitudDx solicitud) { this.solicitud = solicitud; }

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToOne(optional = false)
    @JoinColumn(name = "RESPUESTA", referencedColumnName = "ID_RESPUESTA", nullable = false)
    @ForeignKey(name = "RESP_FK")
    public RespuestaDx getRespuesta() { return respuesta; }

    public void setRespuesta(RespuestaDx respuesta) { this.respuesta = respuesta; }

    @ManyToOne(optional = true)
    @JoinColumn(name = "USUARIO_REGISTRO", referencedColumnName = "USUARIO_ID")
    @ForeignKey(name = "USUARIO_REG_DR_FK")
    public Usuarios getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(Usuarios usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }

    @ManyToOne(optional = true)
    @JoinColumn(name = "USUARIO_ANULACION", referencedColumnName = "USUARIO_ID")
    @ForeignKey(name = "USUARIO_ANUL_DR_FK")
    public Usuarios getUsuarioAnulacion() {
        return usuarioAnulacion;
    }

    public void setUsuarioAnulacion(Usuarios usuarioAnulacion) {
        this.usuarioAnulacion = usuarioAnulacion;
    }
}