package ni.gob.minsa.laboratorio.domain.muestra;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;
import ni.gob.minsa.laboratorio.domain.estructura.Unidades;
import ni.gob.minsa.laboratorio.domain.notificacion.DaNotificacion;
import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by FIRSTICT on 12/9/2014.
 */
@Entity
@Table(name = "RECEPCION_MX", schema = "LABORATORIO")
public class RecepcionMx {

    String idRecepcion;
    String codigoUnicoMx;
    DaOrdenExamen ordenExamen;
    Timestamp fechaHoraRecepcion;
    DaNotificacion notificacion;
    Unidades unidadSalud;
    Laboratorio laboratorioEnvio;
    TipoRecepcionMx tipoRecepcionMx;
    String diagSolicitado;
    Usuarios usuarioRecepcion;
    TipoEstudio tipoEstudio;
    TecnicaxLaboratorio tecnicaxLaboratorio;
    TipoTubo tipoTubo;
    CalidadMx calidadMx;
    boolean cantidadTubosCk;
    boolean tipoMxCk;
    String causaRechazo;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "ID_RECEPCION", nullable = false, insertable = true, updatable = true, length = 36)
    public String getIdRecepcion() {
        return idRecepcion;
    }

    public void setIdRecepcion(String idRecepcion) {
        this.idRecepcion = idRecepcion;
    }

    @Basic
    @Column(name = "FECHAHORA_RECEPCION", nullable = false, insertable = true, updatable = false)
    public Timestamp getFechaHoraRecepcion() {
        return fechaHoraRecepcion;
    }

    public void setFechaHoraRecepcion(Timestamp fechaHoraRecepcion) {
        this.fechaHoraRecepcion = fechaHoraRecepcion;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_NOTIFICACION", referencedColumnName = "ID_NOTIFICACION")
    @ForeignKey(name = "RECEPCION_NOTIFICACION_FK")
    public DaNotificacion getNotificacion() {
        return notificacion;
    }

    public void setNotificacion(DaNotificacion notificacion) {
        this.notificacion = notificacion;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "UNIDAD_SALUD", referencedColumnName = "CODIGO")
    @ForeignKey(name = "RECEPCION_UNIDADS_FK")
    public Unidades getUnidadSalud() {
        return unidadSalud;
    }

    public void setUnidadSalud(Unidades unidadSalud) {
        this.unidadSalud = unidadSalud;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "LABORATORIO", referencedColumnName = "CODIGO")
    @ForeignKey(name = "RECEPCION_LAB_FK")
    public Laboratorio getLaboratorioEnvio() {
        return laboratorioEnvio;
    }

    public void setLaboratorioEnvio(Laboratorio laboratorioEnvio) {
        this.laboratorioEnvio = laboratorioEnvio;
    }

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Catalogo.class, optional = false)
    @JoinColumn(name = "TIPO_RECEPCION", referencedColumnName = "CODIGO")
    @ForeignKey(name = "RECEPCION_TIPORECEP_FK")
    public TipoRecepcionMx getTipoRecepcionMx() {
        return tipoRecepcionMx;
    }

    public void setTipoRecepcionMx(TipoRecepcionMx tipoRecepcionMx) {
        this.tipoRecepcionMx = tipoRecepcionMx;
    }

    @Basic
    @Column(name = "DIAG_SOLICITADO", nullable = true, insertable = true, updatable = true, length = 1000)
    public String getDiagSolicitado() {
        return diagSolicitado;
    }

    public void setDiagSolicitado(String diagSolicitado) {
        this.diagSolicitado = diagSolicitado;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "USUARIO_RECEPCION", referencedColumnName = "USUARIO_ID")
    @ForeignKey(name = "RECEPCION_USUARIO_FK")
    public Usuarios getUsuarioRecepcion() {
        return usuarioRecepcion;
    }

    public void setUsuarioRecepcion(Usuarios usuarioRecepcion) {
        this.usuarioRecepcion = usuarioRecepcion;
    }

    @Basic
    @Column(name = "CODUNICOMX", nullable = false, insertable = true, updatable = true, length = 12)
    public String getCodigoUnicoMx() {
        return codigoUnicoMx;
    }

    public void setCodigoUnicoMx(String codigoUnicoMx) {
        this.codigoUnicoMx = codigoUnicoMx;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_ORDEN_EXAMEN", referencedColumnName = "ID_ORDEN_EXAMEN")
    @ForeignKey(name = "RECEPCION_EXAMEN_FK")
    public DaOrdenExamen getOrdenExamen() {
        return ordenExamen;
    }

    public void setOrdenExamen(DaOrdenExamen ordenExamen) {
        this.ordenExamen = ordenExamen;
    }

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Catalogo.class, optional = true)
    @JoinColumn(name = "COD_TIPO_EST", referencedColumnName = "CODIGO")
    @ForeignKey(name = "RECEPCION_ESTUDIO_FK")
    public TipoEstudio getTipoEstudio() {
        return tipoEstudio;
    }

    public void setTipoEstudio(TipoEstudio tipoEstudio) {
        this.tipoEstudio = tipoEstudio;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "TECNICA_LAB", referencedColumnName = "ID_TECNICALAB")
    @ForeignKey(name = "RECEPCION_TECLAB_FK")
    public TecnicaxLaboratorio getTecnicaxLaboratorio() {
        return tecnicaxLaboratorio;
    }

    public void setTecnicaxLaboratorio(TecnicaxLaboratorio tecnicaxLaboratorio) {
        this.tecnicaxLaboratorio = tecnicaxLaboratorio;
    }

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Catalogo.class, optional = true)
    @JoinColumn(name = "COD_TIPO_TUBO", referencedColumnName = "CODIGO")
    @ForeignKey(name = "RECEPCION_TPTUBO_FK")
    public TipoTubo getTipoTubo() {
        return tipoTubo;
    }

    public void setTipoTubo(TipoTubo tipoTubo) {
        this.tipoTubo = tipoTubo;
    }

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Catalogo.class, optional = true)
    @JoinColumn(name = "COD_CALIDADMX", referencedColumnName = "CODIGO")
    @ForeignKey(name = "RECEPCION_CALIDADMX_FK")
    public CalidadMx getCalidadMx() {
        return calidadMx;
    }

    public void setCalidadMx(CalidadMx calidadMx) {
        this.calidadMx = calidadMx;
    }

    @Basic
    @Column(name = "CANTUBOS_CK", nullable = true, insertable = true, updatable = true)
    public boolean isCantidadTubosCk() {
        return cantidadTubosCk;
    }

    public void setCantidadTubosCk(boolean cantidadTubosCk) {
        this.cantidadTubosCk = cantidadTubosCk;
    }

    @Basic
    @Column(name = "TIPOMX_CK", nullable = true, insertable = true, updatable = true)
    public boolean isTipoMxCk() {
        return tipoMxCk;
    }

    public void setTipoMxCk(boolean tipoMxCk) {
        this.tipoMxCk = tipoMxCk;
    }

    @Basic
    @Column(name = "CAUSA", nullable = false, insertable = true, updatable = true, length = 200)
    public String getCausaRechazo() {
        return causaRechazo;
    }

    public void setCausaRechazo(String causaRechazo) {
        this.causaRechazo = causaRechazo;
    }
}
