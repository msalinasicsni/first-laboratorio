package ni.gob.minsa.laboratorio.domain.resultados;

import ni.gob.minsa.laboratorio.domain.examen.CatalogoExamenes;
import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
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
@Table(name = "conceptos", schema = "laboratorio")
public class Conceptos implements Serializable {

    Integer idConcepto;
    String nombre;
    CatalogoExamenes idExamen;
    TipoDato tipoDato;
    Integer orden;
    boolean requerido;
    boolean pasivo;
    Integer minimo;
    Integer maximo;
    Usuarios usuarioRegistro;
    Timestamp fechahRegistro;


    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "ID_CONCEPTO", nullable = false, insertable = true, updatable = false)
    public Integer getIdConcepto() {
        return idConcepto;
    }

    public void setIdConcepto(Integer idConcepto) {
        this.idConcepto = idConcepto;
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
    @JoinColumn(name = "TIPODATO", referencedColumnName = "ID_TIPO_DATO", nullable = false)
    @ForeignKey(name = "TIPODATO_FK")
    public TipoDato getTipoDato() {
        return tipoDato;
    }

    public void setTipoDato(TipoDato tipoDato) {
        this.tipoDato = tipoDato;
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
    @JoinColumn(name = "USUARIO_REGISTRO", referencedColumnName = "USUARIO_ID")
    @ForeignKey(name = "USUARIO_REG_FK")
    public Usuarios getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(Usuarios usuarioRegistro) {
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
}
