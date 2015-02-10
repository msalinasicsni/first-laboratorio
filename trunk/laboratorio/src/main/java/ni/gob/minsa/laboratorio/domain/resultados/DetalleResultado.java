package ni.gob.minsa.laboratorio.domain.resultados;

import ni.gob.minsa.laboratorio.domain.muestra.AlicuotaRegistro;
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
@Table(name = "detalle_resultado", schema = "laboratorio")
public class DetalleResultado implements Serializable {

    String idDetalle;
    String valor;
    AlicuotaRegistro alicuotaRegistro;
    RespuestaExamen concepto;
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
    public String getRazonAnulacion() {
        return razonAnulacion;
    }

    public void setRazonAnulacion(String razonAnulacion) {
        this.razonAnulacion = razonAnulacion;
    }

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_ALICUOTA", referencedColumnName = "ID_ALICUOTA", nullable = false)
    @ForeignKey(name = "ID_EXAMEN_FK")
    public AlicuotaRegistro getAlicuotaRegistro() {
        return alicuotaRegistro;
    }

    public void setAlicuotaRegistro(AlicuotaRegistro alicuotaRegistro) {
        this.alicuotaRegistro = alicuotaRegistro;
    }

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_CONCEPTO", referencedColumnName = "ID_CONCEPTO", nullable = false)
    @ForeignKey(name = "CONCEPTO_DR_FK")
    public RespuestaExamen getConcepto() {
        return concepto;
    }

    public void setConcepto(RespuestaExamen concepto) {
        this.concepto = concepto;
    }

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
