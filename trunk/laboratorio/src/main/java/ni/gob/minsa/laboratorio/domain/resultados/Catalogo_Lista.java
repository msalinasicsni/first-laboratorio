package ni.gob.minsa.laboratorio.domain.resultados;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;
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
@Table(name = "catalogo_lista", schema = "laboratorio")
public class Catalogo_Lista implements Serializable {

   Integer idCatalogoLista;
   String valor;
   TipoDato idTipoDato;
   boolean pasivo;
   Usuarios usarioRegistro;
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
    @Column(name= "NOMBRE", nullable = false, insertable = true, updatable = true, length = 50)
    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToOne(optional = false)
    @JoinColumn(name="ID_TIPO_DATO", referencedColumnName = "ID_TIPO_DATO", nullable = false)
    @ForeignKey(name = "IDTIPOD_FK")
    public TipoDato getIdTipoDato() {
        return idTipoDato;
    }

    public void setIdTipoDato(TipoDato idTipoDato) {
        this.idTipoDato = idTipoDato;
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
    @JoinColumn(name = "USUARIO_REGISTRO", referencedColumnName = "USUARIO_ID")
    @ForeignKey(name = "USUARIO_REG_FK")
    public Usuarios getUsarioRegistro() {
        return usarioRegistro;
    }

    public void setUsarioRegistro(Usuarios usarioRegistro) {
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
