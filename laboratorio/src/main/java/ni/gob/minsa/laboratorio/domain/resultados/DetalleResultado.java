package ni.gob.minsa.laboratorio.domain.resultados;

import ni.gob.minsa.laboratorio.domain.audit.Auditable;
import ni.gob.minsa.laboratorio.domain.muestra.OrdenExamen;
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
@Table(name = "detalle_resultado", schema = "laboratorio")
public class DetalleResultado implements Serializable, Auditable {

    String idDetalle;
    String valor;
    OrdenExamen examen;
    RespuestaExamen respuesta;
    User usuarioRegistro;
    Timestamp fechahProcesa;
    boolean pasivo;
    String razonAnulacion;
    User usuarioAnulacion;
    Timestamp fechahAnulacion;
    Timestamp fechahoraRegistro;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "ID_DETALLE", nullable = false, insertable = true, updatable = false, length = 36)
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
    @Column(name = "PASIVO", nullable = false, insertable = true, updatable = true)
    public boolean isPasivo() {
        return pasivo;
    }

    public void setPasivo(boolean pasivo) {
        this.pasivo = pasivo;
    }

    @Basic
    @Column(name = "FECHAH_REGISTRO", nullable = false, insertable = true, updatable = true)
    public Timestamp getFechahProcesa() {
        return fechahProcesa;
    }

    public void setFechahProcesa(Timestamp fechahRegistro) {
        this.fechahProcesa = fechahRegistro;
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
    public String getRazonAnulacion() {
        return razonAnulacion;
    }

    public void setRazonAnulacion(String razonAnulacion) {
        this.razonAnulacion = razonAnulacion;
    }

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_ORDEN_EXAMEN", referencedColumnName = "ID_ORDEN_EXAMEN", nullable = false)
    @ForeignKey(name = "ID_ORDEN_EXAMEN_FK")
    public OrdenExamen getExamen() {
        return examen;
    }

    public void setExamen(OrdenExamen alicuotaRegistro) {
        this.examen = alicuotaRegistro;
    }

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_RESPUESTA", referencedColumnName = "ID_RESPUESTA", nullable = false)
    @ForeignKey(name = "CONCEPTO_DR_FK")
    public RespuestaExamen getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(RespuestaExamen respuesta) {
        this.respuesta = respuesta;
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

    @Basic
    @Column(name = "FECHA_REG_SISTEMA", nullable = true, insertable = true, updatable = false)
    public Timestamp getFechahoraRegistro() {
        return fechahoraRegistro;
    }

    public void setFechahoraRegistro(Timestamp fechahoraRegistro) {
        this.fechahoraRegistro = fechahoraRegistro;
    }

    @Override
    public boolean isFieldAuditable(String fieldname) {
        return true;
    }

    @Override
    public String toString() {
        return "DetalleResultado{" + idDetalle + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DetalleResultado)) return false;

        DetalleResultado that = (DetalleResultado) o;

        if (!idDetalle.equals(that.idDetalle)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return idDetalle.hashCode();
    }
}
