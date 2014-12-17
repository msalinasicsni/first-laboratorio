package ni.gob.minsa.laboratorio.domain.vih;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;
import ni.gob.minsa.laboratorio.domain.estructura.EntidadesAdtvas;
import ni.gob.minsa.laboratorio.domain.estructura.Unidades;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by USER on 13/10/2014.
 */
@Entity
@Table(name = "sive_vih_ficha", schema = "alerta")
public class VihFicha {

    private Integer id_ficha_vih;
    private String codigo_usuario_vih;
    private Timestamp fecha;
    private EntidadesAdtvas entidadesAdtva;
    private Unidades unidadSalud;
    private Integer id_municipio;
    private TipoAseguradovih id_categoria_afiliacion;
    private String responsable_ficha;
    private Timestamp fechaAlta;
    private String usuarioAlta;
    private Timestamp fechaBaja;
    private String usuarioBaja;
    private Integer id_metodo_captacion;

    @Id
    @Column(name = "id_ficha_vih", nullable = false, insertable = true, updatable = true, precision = 0)
    public Integer getId_ficha_vih() {
        return id_ficha_vih;
    }

    public void setId_ficha_vih(Integer id) {
        this.id_ficha_vih = id;
    }

    public String getCodigo_usuario_vih() {
        return codigo_usuario_vih;
    }

    public void setCodigo_usuario_vih(String codigo_usuario_vih) {
        this.codigo_usuario_vih = codigo_usuario_vih;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    @ManyToOne(optional=false)
    @JoinColumn(name="ID_SILAIS", referencedColumnName = "CODIGO")
    @ForeignKey(name = "VIHFICHA_ENTIDAD_FK")
    public EntidadesAdtvas getEntidadesAdtva() {
        return entidadesAdtva;
    }

    public void setEntidadesAdtva(EntidadesAdtvas entidadesAdtva) {
        this.entidadesAdtva = entidadesAdtva;
    }

    public Integer getId_municipio() {
        return id_municipio;
    }

    public void setId_municipio(Integer id_municipio) {
        this.id_municipio = id_municipio;
    }

    @ManyToOne(optional=false)
    @JoinColumn(name="ID_UNIDAD_SALUD", referencedColumnName = "CODIGO")
    @ForeignKey(name = "VIHFICHA_UNIDADES_FK")
    public Unidades getUnidadSalud() {
        return unidadSalud;
    }

    public void setUnidadSalud(Unidades unidadSalud) {
        this.unidadSalud = unidadSalud;
    }

    @ManyToOne(fetch=FetchType.LAZY,targetEntity=Catalogo.class)
    @JoinColumn(name="id_categoria_afiliacion", referencedColumnName = "CODIGO")
    @ForeignKey(name = "COD_TIPOAS_FK")
    public TipoAseguradovih getId_categoria_afiliacion() {
        return this.id_categoria_afiliacion;
    }

    public void setId_categoria_afiliacion(TipoAseguradovih id_categoria_afiliacion) {
        this.id_categoria_afiliacion = id_categoria_afiliacion;
    }

    public String getResponsable_ficha() {
        return responsable_ficha;
    }

    public void setResponsable_ficha(String responsable_ficha) {
        this.responsable_ficha = responsable_ficha;
    }

    public Timestamp getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Timestamp fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public String getUsuarioAlta() {
        return usuarioAlta;
    }

    public void setUsuarioAlta(String usuarioAlta) {
        this.usuarioAlta = usuarioAlta;
    }

    public Timestamp getFechaBaja() {
        return fechaBaja;
    }

    public void setFechaBaja(Timestamp fechaBaja) {
        this.fechaBaja = fechaBaja;
    }

    public String getUsuarioBaja() {
        return usuarioBaja;
    }

    public void setUsuarioBaja(String usuarioBaja) {
        this.usuarioBaja = usuarioBaja;
    }

    public Integer getId_metodo_captacion() {
        return id_metodo_captacion;
    }

    public void setId_metodo_captacion(Integer id_metodo_captacion) {
        this.id_metodo_captacion = id_metodo_captacion;
    }





}
