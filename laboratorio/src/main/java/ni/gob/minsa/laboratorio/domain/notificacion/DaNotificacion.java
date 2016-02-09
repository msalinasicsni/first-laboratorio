package ni.gob.minsa.laboratorio.domain.notificacion;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;
import ni.gob.minsa.laboratorio.domain.estructura.EntidadesAdtvas;
import ni.gob.minsa.laboratorio.domain.estructura.Unidades;
import ni.gob.minsa.laboratorio.domain.irag.Respuesta;
import ni.gob.minsa.laboratorio.domain.persona.SisPersona;
import ni.gob.minsa.laboratorio.domain.poblacion.Comunidades;
import ni.gob.minsa.laboratorio.domain.poblacion.Divisionpolitica;
import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
import ni.gob.minsa.laboratorio.domain.solicitante.Solicitante;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;


/**
 * Created by souyen-ics on 11-17-14.
 */
@Entity
@Table(name = "da_notificacion", schema = "alerta")
public class DaNotificacion {

    private String idNotificacion;
    private SisPersona persona;
    private TipoNotificacion codTipoNotificacion;
    private boolean pasivo;
    private EntidadesAdtvas codSilaisAtencion;
    private Unidades codUnidadAtencion;
    private Usuarios usuarioRegistro;
    private Timestamp fechaAnulacion;
    private Timestamp fechaRegistro;
    private Divisionpolitica municipioResidencia;
    private String direccionResidencia;
    private Date fechaInicioSintomas;
    private Respuesta urgente;
    private Solicitante solicitante;
	private boolean completa;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "ID_NOTIFICACION", nullable = false, insertable = true, updatable = true, length = 36)
    public String getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(String idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    @ManyToOne(optional = true)
    @JoinColumn(name = "PERSONA_ID", referencedColumnName = "PERSONA_ID")
    @ForeignKey(name = "PERSONA_ID_NOT_FK")
    public SisPersona getPersona() {
        return persona;
    }

    public void setPersona(SisPersona persona) {
        this.persona = persona;
    }

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Catalogo.class, optional = true)
    @JoinColumn(name = "COD_TIPO_NOTIFICACION", referencedColumnName = "CODIGO", nullable = true)
    @ForeignKey(name = "TIPO_NOTIF_FK")
    public TipoNotificacion getCodTipoNotificacion() {
        return codTipoNotificacion;
    }

    public void setCodTipoNotificacion(TipoNotificacion codTipoNotificacion) {
        this.codTipoNotificacion = codTipoNotificacion;
    }

    @Basic
    @Column(name = "PASIVO", nullable = true, insertable = true, updatable = true)
    public boolean isPasivo() {
        return pasivo;
    }

    public void setPasivo(boolean pasivo) {
        this.pasivo = pasivo;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "USUARIO_ID", referencedColumnName = "USUARIO_ID")
    @ForeignKey(name = "USUARIO_NOTI_FK")
    public Usuarios getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(Usuarios usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }

    @Basic
    @Column(name = "FECHA_REGISTRO", nullable = false, insertable = true, updatable = true)
    public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }


    @ManyToOne(optional = true)
    @JoinColumn(name = "COD_SILAIS_ATENCION", referencedColumnName = "CODIGO", nullable = true)
    @ForeignKey(name = "COD_SILAIS_FK")
    public EntidadesAdtvas getCodSilaisAtencion() {
        return codSilaisAtencion;
    }

    public void setCodSilaisAtencion(EntidadesAdtvas codSilaisAtencion) {
        this.codSilaisAtencion = codSilaisAtencion;
    }

    @ManyToOne(optional = true)
    @JoinColumn(name = "COD_UNIDAD_ATENCION", referencedColumnName = "CODIGO", nullable = true)
    @ForeignKey(name = "COD_UNIDAD_FK")
    public Unidades getCodUnidadAtencion() {
        return codUnidadAtencion;
    }

    public void setCodUnidadAtencion(Unidades codUnidadAtencion) {
        this.codUnidadAtencion = codUnidadAtencion;
    }

    @Basic
    @Column(name = "FECHA_ANULACION", nullable = true, insertable = true, updatable = true)
    public Timestamp getFechaAnulacion() {
        return fechaAnulacion;
    }

    public void setFechaAnulacion(Timestamp fechaAnulacion) {
        this.fechaAnulacion = fechaAnulacion;
    }

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Divisionpolitica.class)
    @JoinColumn(name = "CODIGO_MUNIC_RESIDENCIA", referencedColumnName = "CODIGO_NACIONAL", nullable = true)
    public Divisionpolitica getMunicipioResidencia() { return municipioResidencia; }

    public void setMunicipioResidencia(Divisionpolitica municipioResidencia) { this.municipioResidencia = municipioResidencia; }


    @Column(name="DIRECCION_RESIDENCIA", length=100)
    public String getDireccionResidencia() {
        return direccionResidencia;
    }

    public void setDireccionResidencia(String direccionResidencia) {
        this.direccionResidencia = direccionResidencia;
    }

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column(name = "FECHA_INICIO_SINTOMAS", nullable = true)
    public Date getFechaInicioSintomas() {
        return fechaInicioSintomas;
    }

    public void setFechaInicioSintomas(Date fechaInicioSintomas) {
        this.fechaInicioSintomas = fechaInicioSintomas;
    }

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Catalogo.class, optional = true)
    @JoinColumn(name = "URGENTE", referencedColumnName = "CODIGO", nullable = true)
    @ForeignKey(name = "SF_URGENTE_FK")
    public Respuesta getUrgente() { return urgente; }

    public void setUrgente(Respuesta urgente) { this.urgente = urgente; }

    @ManyToOne(optional = true)
    @JoinColumn(name = "ID_SOLICITANTE", referencedColumnName = "ID_SOLICITANTE")
    @ForeignKey(name = "SOLICITANTE_NOT_FK")
    public Solicitante getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(Solicitante solicitante) {
        this.solicitante = solicitante;
    }
	
	@Basic
    @Column(name = "COMPLETA", nullable = true, insertable = true, updatable = true)
    public boolean isCompleta() { return completa; }

    public void setCompleta(boolean completa) {  this.completa = completa; }
}


