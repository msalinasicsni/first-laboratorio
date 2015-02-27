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
@Table(name = "recepcion_mx", schema = "laboratorio")
public class RecepcionMx {

    String idRecepcion;
    DaTomaMx tomaMx;
    Timestamp fechaHoraRecepcion;
    TipoRecepcionMx tipoRecepcionMx;
    Usuarios usuarioRecepcion;
    TecnicaxLaboratorio tecnicaxLaboratorio;
    TipoTubo tipoTubo;
    CalidadMx calidadMx;
    boolean cantidadTubosCk;
    boolean tipoMxCk;
    String causaRechazo;
    Timestamp fechaHoraRecepcionLab;
    Usuarios usuarioRecepcionLab;

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

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Catalogo.class, optional = false)
    @JoinColumn(name = "TIPO_RECEPCION", referencedColumnName = "CODIGO")
    @ForeignKey(name = "RECEPCION_TIPORECEP_FK")
    public TipoRecepcionMx getTipoRecepcionMx() {
        return tipoRecepcionMx;
    }

    public void setTipoRecepcionMx(TipoRecepcionMx tipoRecepcionMx) {
        this.tipoRecepcionMx = tipoRecepcionMx;
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

    @ManyToOne(optional = false)
    @JoinColumn(name = "CODUNICOMX", referencedColumnName = "CODUNICOMX")
    @ForeignKey(name = "RECEPCION_TOMAMX_FK")
    public DaTomaMx getTomaMx() {
        return tomaMx;
    }

    public void setTomaMx(DaTomaMx tomaMx) {
        this.tomaMx = tomaMx;
    }

    @ManyToOne(optional = true)
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
    @Column(name = "CAUSA", nullable = true, insertable = true, updatable = true, length = 200)
    public String getCausaRechazo() {
        return causaRechazo;
    }

    public void setCausaRechazo(String causaRechazo) {
        this.causaRechazo = causaRechazo;
    }

    @Basic
    @Column(name = "FECHAH_RECEPCION_LAB", nullable = true, insertable = true, updatable = true)
    public Timestamp getFechaHoraRecepcionLab() {
        return fechaHoraRecepcionLab;
    }

    public void setFechaHoraRecepcionLab(Timestamp fechaHoraRecepcionLab) {
        this.fechaHoraRecepcionLab = fechaHoraRecepcionLab;
    }

    @ManyToOne(optional = true)
    @JoinColumn(name = "USUARIO_RECEPCIONLAB", referencedColumnName = "USUARIO_ID")
    @ForeignKey(name = "RECEPCIONLAB_USUARIO_FK")
    public Usuarios getUsuarioRecepcionLab() {
        return usuarioRecepcionLab;
    }

    public void setUsuarioRecepcionLab(Usuarios usuarioRecepcionLab) {
        this.usuarioRecepcionLab = usuarioRecepcionLab;
    }
}
