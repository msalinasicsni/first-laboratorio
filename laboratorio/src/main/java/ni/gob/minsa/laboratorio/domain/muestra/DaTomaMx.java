package ni.gob.minsa.laboratorio.domain.muestra;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;
import ni.gob.minsa.laboratorio.domain.estructura.EntidadesAdtvas;
import ni.gob.minsa.laboratorio.domain.estructura.Unidades;
import ni.gob.minsa.laboratorio.domain.notificacion.DaNotificacion;
import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by souyen-ics on 11-05-14.
 */
@Entity
@Table(name = "da_tomamx", schema = "alerta", uniqueConstraints = @UniqueConstraint(columnNames = "CODUNICOMX"))
public class DaTomaMx implements Serializable {

    private String idTomaMx;
    private DaNotificacion idNotificacion;
    private TipoMx codTipoMx;
    private Timestamp fechaHTomaMx;//solo tomar en cuenta la fecha
    private String horaRefrigeracion;
    private Integer canTubos;
    private Float volumen;
    private Boolean mxSeparada;
    private EstadoMx estadoMx;
    private Usuarios usuario;
    private Timestamp fechaRegistro;
    private boolean anulada;
    private Timestamp fechaAnulacion;
    private String codigoUnicoMx;
    private DaEnvioMx envio;
    private CategoriaMx categoriaMx;
    private String codigoLab;
    private EntidadesAdtvas codSilaisAtencion;
    private Unidades codUnidadAtencion;
    private String horaTomaMx;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "ID_TOMAMX", nullable = false, insertable = true, updatable = true, length = 36)
    public String getIdTomaMx() {
        return idTomaMx;
    }

    public void setIdTomaMx(String idTomaMx) {
        this.idTomaMx = idTomaMx;
    }


    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_NOTIFICACION", referencedColumnName = "ID_NOTIFICACION", nullable = false)
    @ForeignKey(name = "IDNOTI_FK")
    public DaNotificacion getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(DaNotificacion idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "COD_TIPOMX", referencedColumnName = "ID_TIPOMX", nullable = false)
    @ForeignKey(name = "COD_TIPOMX_FK")
    public TipoMx getCodTipoMx() {
        return codTipoMx;
    }

    public void setCodTipoMx(TipoMx codTipoMx) {
        this.codTipoMx = codTipoMx;
    }

    @Basic
    @Column(name = "FECHAH_TOMAMX", nullable = false, insertable = true, updatable = true)
    public Timestamp getFechaHTomaMx() {
        return fechaHTomaMx;
    }

    public void setFechaHTomaMx(Timestamp fechaHTomaMx) {
        this.fechaHTomaMx = fechaHTomaMx;
    }

    @Basic
    @Column(name = "CANT_TUBOS", nullable = true, insertable = true, updatable = true, precision = 0)
    public Integer getCanTubos() {
        return canTubos;
    }

    public void setCanTubos(Integer canTubos) {
        this.canTubos = canTubos;
    }

    @Column(name = "VOLUMEN", nullable = true)
    public Float getVolumen() {
        return volumen;
    }

    public void setVolumen(Float volumen) {
        this.volumen = volumen;
    }

    @Basic
    @Column(name = "MXSEPARADA", nullable = true, insertable = true, updatable = true)
    public Boolean getMxSeparada() {
        return mxSeparada;
    }

    public void setMxSeparada(Boolean mxSeparada) {
        this.mxSeparada = mxSeparada;
    }


    @ManyToOne(optional = false)
    @JoinColumn(name = "USUARIO_ID", referencedColumnName = "USUARIO_ID")
    @ForeignKey(name = "USUARIO_FK")
    public Usuarios getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuarios usuario) {
        this.usuario = usuario;
    }

    @Basic
    @Column(name = "FECHA_REGISTRO", nullable = false, insertable = true, updatable = true)
    public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @Basic
    @Column(name = "ANULADA", nullable = true, insertable = true, updatable = true)
    public boolean isAnulada() {
        return anulada;
    }

    public void setAnulada(boolean anulada) {
        this.anulada = anulada;
    }

    @Basic
    @Column(name = "FECHA_ANULACION", nullable = true, insertable = true, updatable = true)
    public Timestamp getFechaAnulacion() {
        return fechaAnulacion;
    }

    public void setFechaAnulacion(Timestamp fechaAnulacion) {
        this.fechaAnulacion = fechaAnulacion;
    }


    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Catalogo.class, optional = false)
    @JoinColumn(name = "COD_ESTADOMX", referencedColumnName = "CODIGO", nullable = false)
    @ForeignKey(name = "COD_ESTADOMX_FK")
    public EstadoMx getEstadoMx() {
        return estadoMx;
    }

    public void setEstadoMx(EstadoMx estadoMx) {
        this.estadoMx = estadoMx;
    }

    @Basic
    @Column(name = "HORA_REFRIGERACION", nullable = true, insertable = true, updatable = true, length = 50)
    public String getHoraRefrigeracion() {
        return horaRefrigeracion;
    }

    public void setHoraRefrigeracion(String horaRefrigeracion) {
        this.horaRefrigeracion = horaRefrigeracion;
    }

    @Basic
    @Column(name = "CODUNICOMX", nullable = true, insertable = true, updatable = true, length = 12)
    public String getCodigoUnicoMx() {
        return codigoUnicoMx;
    }

    public void setCodigoUnicoMx(String codigoUnicoMx) {
        this.codigoUnicoMx = codigoUnicoMx;
    }

    @ManyToOne(optional = true)
    @JoinColumn(name = "ID_ENVIO", referencedColumnName = "ID_ENVIO")
    @ForeignKey(name = "ENVIOMX_MX_FK")
    public DaEnvioMx getEnvio() {
        return envio;
    }

    public void setEnvio(DaEnvioMx envio) {
        this.envio = envio;
    }

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Catalogo.class, optional = true)
    @JoinColumn(name = "COD_CATEGORIA", referencedColumnName = "CODIGO",nullable = true)
    @ForeignKey(name = "CATEGORIAMX_MX_FK")
    public CategoriaMx getCategoriaMx() {
        return categoriaMx;
    }

    public void setCategoriaMx(CategoriaMx categoriaMx) {
        this.categoriaMx = categoriaMx;
    }

    @Basic
    @Column(name = "CODIGO_LAB", nullable = true, insertable = true, updatable = true, length = 16, unique=true)
    public String getCodigoLab() {
        return codigoLab;
    }

    public void setCodigoLab(String codigoLab) {
        this.codigoLab = codigoLab;
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
    @Column(name = "HORA_TOMAMX", nullable = true, insertable = true, updatable = true, length = 8)
    public String getHoraTomaMx() {
        return horaTomaMx;
    }

    public void setHoraTomaMx(String horaTomaMx) {
        this.horaTomaMx = horaTomaMx;
    }
}
