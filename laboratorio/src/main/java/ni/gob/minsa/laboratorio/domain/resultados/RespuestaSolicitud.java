package ni.gob.minsa.laboratorio.domain.resultados;

import ni.gob.minsa.laboratorio.domain.audit.Auditable;
import ni.gob.minsa.laboratorio.domain.concepto.Concepto;
import ni.gob.minsa.laboratorio.domain.muestra.Catalogo_Dx;
import ni.gob.minsa.laboratorio.domain.muestra.Catalogo_Estudio;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.User;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by souyen-ics.
 */
@Entity
@Table(name = "respuesta_solicitud", schema = "laboratorio")
public class RespuestaSolicitud implements Serializable, Auditable{

    Integer idRespuesta;
    String nombre;
    Catalogo_Dx diagnostico;
    Catalogo_Estudio estudio;
    Concepto concepto;
    Integer orden;
    boolean requerido;
    boolean pasivo;
    Integer minimo;
    Integer maximo;
    User usuarioRegistro;
    Timestamp fechahRegistro;
    String descripcion;


    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "ID_RESPUESTA", nullable = false, insertable = true, updatable = false)
    public Integer getIdRespuesta() {
        return idRespuesta;
    }

    public void setIdRespuesta(Integer idRespuesta) {
        this.idRespuesta = idRespuesta;
    }

    @Basic
    @Column(name = "NOMBRE", nullable = false, insertable = true, updatable = true, length = 50)
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToOne(optional = true)
    @JoinColumn(name = "DIAGNOSTICO", referencedColumnName = "ID_DIAGNOSTICO", nullable = true)
    @ForeignKey(name = "ID_DX_FK")
    public Catalogo_Dx getDiagnostico() { return diagnostico; }

    public void setDiagnostico(Catalogo_Dx diagnostico) { this.diagnostico = diagnostico; }

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToOne(optional = true)
    @JoinColumn(name = "ESTUDIO", referencedColumnName = "ID_ESTUDIO", nullable = true)
    @ForeignKey(name = "ID_EST_FK")
    public Catalogo_Estudio getEstudio() { return estudio; }

    public void setEstudio(Catalogo_Estudio estudio) { this.estudio = estudio; }

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_CONCEPTO", referencedColumnName = "ID_CONCEPTO", nullable = false)
    @ForeignKey(name = "CONCEPT_FK")
    public Concepto getConcepto() {
        return concepto;
    }

    public void setConcepto(Concepto concepto) {
        this.concepto = concepto;
    }

    @Basic
    @Column(name = "ORDEN", nullable = false, insertable = true)
    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    @Basic
    @Column(name = "REQUERIDO", nullable = false, insertable = true)
    public boolean isRequerido() {
        return requerido;
    }

    public void setRequerido(boolean requerido) {
        this.requerido = requerido;
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
    @Column(name = "MINIMO", nullable = true, insertable = true)
    public Integer getMinimo() {
        return minimo;
    }

    public void setMinimo(Integer minimo) {
        this.minimo = minimo;
    }

    @Basic
    @Column(name = "MAXIMO", nullable = true, insertable = true)
    public Integer getMaximo() {
        return maximo;
    }

    public void setMaximo(Integer maximo) {
        this.maximo = maximo;
    }

    @ManyToOne(optional = true)
    @JoinColumn(name = "USUARIO_REGISTRO", referencedColumnName = "username")
    @ForeignKey(name = "RS_USUARIO_REG_FK")
    public User getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(User usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
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
    @Column(name = "DESCRIPCION", nullable = true, insertable = true, updatable = true, length = 500)
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public boolean isFieldAuditable(String fieldname) {
        if (fieldname.matches("fechahRegistro") || fieldname.matches("usuarioRegistro")) return false;
        return  true;
    }

    @Override
    public String toString() {
        return "{" +
                "idRespuestaSol=" + idRespuesta +
                ", nombre='" + nombre + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RespuestaSolicitud)) return false;

        RespuestaSolicitud that = (RespuestaSolicitud) o;

        if (!idRespuesta.equals(that.idRespuesta)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return idRespuesta.hashCode();
    }
}
