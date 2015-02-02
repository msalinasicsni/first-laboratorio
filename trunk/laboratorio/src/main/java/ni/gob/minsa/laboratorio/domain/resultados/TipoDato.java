package ni.gob.minsa.laboratorio.domain.resultados;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;
import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
import org.hibernate.annotations.ForeignKey;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by souyen-ics.
 */
@Entity
@Table(name = "tipodato", schema = "laboratorio")
public class TipoDato implements Serializable {

    Integer idTipoDato;
    String nombre;
    boolean pasivo;
    TipoDatoCatalogo tipo;
    CatalogoLista catalogoLista;
    Usuarios usuarioRegistro;
    DateTime fechahRegistro;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_TIPO_DATO", nullable = false, insertable = true, updatable = false)
    public Integer getIdTipoDato() {
        return idTipoDato;
    }

    public void setIdTipoDato(Integer idTipoDato) {
        this.idTipoDato = idTipoDato;
    }

    @Basic
    @Column(name= "NOMBRE", nullable = false, insertable = true, updatable = true, length = 50)
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    @Basic
    @Column(name= "PASIVO", nullable = true, insertable = true, updatable = true)
    public boolean isPasivo() {
        return pasivo;
    }

    public void setPasivo(boolean pasivo) {
        this.pasivo = pasivo;
    }


    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Catalogo.class, optional = true)
    @JoinColumn(name = "COD_TIPO_DATO", referencedColumnName = "CODIGO", nullable = true)
    @ForeignKey(name = "COD_DATO_FK")
    public TipoDatoCatalogo getTipo() {
        return tipo;
    }

    public void setTipo(TipoDatoCatalogo tipo) {
        this.tipo = tipo;
    }


    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Catalogo.class, optional = true)
    @JoinColumn(name = "COD_LISTA", referencedColumnName = "CODIGO", nullable = true)
    @ForeignKey(name = "COD_LISTA_FK")
    public CatalogoLista getCatalogoLista() {
        return catalogoLista;
    }

    public void setCatalogoLista(CatalogoLista catalogoLista) {
        this.catalogoLista = catalogoLista;
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
    public DateTime getFechahRegistro() {
        return fechahRegistro;
    }

    public void setFechahRegistro(DateTime fechahRegistro) {
        this.fechahRegistro = fechahRegistro;
    }
}
