package ni.gob.minsa.laboratorio.domain.resultados;

import ni.gob.minsa.laboratorio.domain.audit.Auditable;
import ni.gob.minsa.laboratorio.domain.concepto.Concepto;
import ni.gob.minsa.laboratorio.domain.examen.CatalogoExamenes;
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
@Table(name = "respuesta_examen", schema = "laboratorio")
public class RespuestaExamen implements Serializable, Auditable {

    Integer idRespuesta;
    String nombre;
    CatalogoExamenes idExamen;
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
    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_EXAMEN", referencedColumnName = "ID_EXAMEN", nullable = false)
    @ForeignKey(name = "ID_EXAMEN_FK")
    public CatalogoExamenes getIdExamen() {
        return idExamen;
    }

    public void setIdExamen(CatalogoExamenes idExamen) {
        this.idExamen = idExamen;
    }

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
    @ForeignKey(name = "RE_USUARIO_REG_FK")
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
                "idRespuestaEx=" + idRespuesta +
                ", nombre='" + nombre + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RespuestaExamen)) return false;

        RespuestaExamen that = (RespuestaExamen) o;

        if (!idRespuesta.equals(that.idRespuesta)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return idRespuesta.hashCode();
    }
}
