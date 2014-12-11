package ni.gob.minsa.laboratorio.domain.muestra;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;
import ni.gob.minsa.laboratorio.domain.notificacion.DaNotificacion;
import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by souyen-ics on 11-05-14.
 */
@Entity
@Table(name = "DA_TOMAMX", schema = "ALERTA")
public class DaTomaMx {

    private String idTomaMx;
    private DaNotificacion idNotificacion;
    private TipoMx codTipoMx;
    private String examenes;
    private Timestamp fechaHTomaMx;
    private String horaRefrigeracion;
    private Integer canTubos;
    private Float volumen;
    private Boolean mxSeparada;
    private EstadoMx estadoMx;
    private Usuarios usuario;
    private Timestamp fechaRegistro;
    private boolean anulada;
    private Timestamp fechaAnulacion;

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

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Catalogo.class, optional = false)
    @JoinColumn(name = "COD_TIPOMX", referencedColumnName = "CODIGO", nullable = false)
    @ForeignKey(name = "COD_TIPOMX_FK")
    public TipoMx getCodTipoMx() {
        return codTipoMx;
    }

    public void setCodTipoMx(TipoMx codTipoMx) {
        this.codTipoMx = codTipoMx;
    }

    @Basic
    @Column(name = "EXAMENES", nullable = false, insertable = true, updatable = true, length = 100)
    public String getExamenes() {
        return examenes;
    }

    public void setExamenes(String examenes) {
        this.examenes = examenes;
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
}
