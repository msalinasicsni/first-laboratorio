package ni.gob.minsa.laboratorio.domain.concepto;

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
@Table(name = "catalogo_lista", schema = "laboratorio")
public class Catalogo_Lista implements Serializable {

   Integer idCatalogoLista;
   String valor;
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
}
