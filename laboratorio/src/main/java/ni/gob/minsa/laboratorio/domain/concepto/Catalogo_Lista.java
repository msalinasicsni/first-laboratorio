package ni.gob.minsa.laboratorio.domain.concepto;

import ni.gob.minsa.laboratorio.domain.audit.Auditable;
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
@Table(name = "catalogo_lista", schema = "laboratorio", uniqueConstraints = @UniqueConstraint(columnNames = {"VALOR","ID_CONCEPTO"},name = "CATLISTA_VALOR_IDCONCEPTO_UQ"))
public class Catalogo_Lista implements Serializable, Auditable {

    Integer idCatalogoLista;
    String valor;
    String etiqueta;
    Concepto idConcepto;
    boolean pasivo;
    User usarioRegistro;
    Timestamp fechaHRegistro;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "ID_CATALOGO_LISTA", nullable = false, insertable = true, updatable = false)
    public Integer getIdCatalogoLista() {
        return idCatalogoLista;
    }

    public void setIdCatalogoLista(Integer idCatalogoLista) {
        this.idCatalogoLista = idCatalogoLista;
    }

    @Basic
    @Column(name= "VALOR", nullable = false, insertable = true, updatable = true, length = 50)
    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    @Basic
    @Column(name= "ETIQUETA", nullable = true, insertable = true, updatable = true, length = 150)
    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToOne(optional = false)
    @JoinColumn(name="ID_CONCEPTO", referencedColumnName = "ID_CONCEPTO", nullable = false)
    @ForeignKey(name = "IDCONCEPTO_FK")
    public Concepto getIdConcepto() {
        return idConcepto;
    }

    public void setIdConcepto(Concepto idConcepto) {
        this.idConcepto = idConcepto;
    }

    @Basic
    @Column(name= "PASIVO", nullable = true, insertable = true, updatable = true)
    public boolean isPasivo() {
        return pasivo;
    }

    public void setPasivo(boolean pasivo) {
        this.pasivo = pasivo;
    }

    @ManyToOne(optional = true)
    @JoinColumn(name = "USUARIO_REGISTRO", referencedColumnName = "username")
    @ForeignKey(name = "CL_USUARIO_REG_FK")
    public User getUsarioRegistro() {
        return usarioRegistro;
    }

    public void setUsarioRegistro(User usarioRegistro) {
        this.usarioRegistro = usarioRegistro;
    }

    @Basic
    @Column(name = "FECHAH_REGISTRO", nullable = false, insertable = true, updatable = false)
    public Timestamp getFechaHRegistro() {
        return fechaHRegistro;
    }

    public void setFechaHRegistro(Timestamp fechaHRegistro) {
        this.fechaHRegistro = fechaHRegistro;
    }

    @Override
    public boolean isFieldAuditable(String fieldname) {
        if (fieldname.matches("fechaHRegistro") || fieldname.matches("usarioRegistro"))
            return false;
        else
            return true;
    }

    @Override
    public String toString() {
        return "{" +
                "idCatalogoLista=" + idCatalogoLista +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Catalogo_Lista)) return false;

        Catalogo_Lista that = (Catalogo_Lista) o;

        if (!idCatalogoLista.equals(that.idCatalogoLista)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return idCatalogoLista.hashCode();
    }
}
