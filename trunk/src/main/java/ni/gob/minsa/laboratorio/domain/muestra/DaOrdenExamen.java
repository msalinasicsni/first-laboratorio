package ni.gob.minsa.laboratorio.domain.muestra;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;
import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by souyen-ics on 11-20-14.
 */
@Entity
@Table(name = "DA_ORDEN_EXAMEN", schema = "ALERTA")
public class DaOrdenExamen {

    private String idOrdenExamen;
    private EstadoOrdenEx codEstado;
    private DaTomaMx idTomaMx;
    private Timestamp fechaHOrden;
    private CatalogoExamenes codExamen;
    private DaEnvioOrden envio;
    private Usuarios usarioRegistro;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "ID_ORDEN_EXAMEN", nullable = false, insertable = true, updatable = true, length = 36)
    public String getIdOrdenExamen() {
        return idOrdenExamen;
    }

    public void setIdOrdenExamen(String idOrdenExamen) {
        this.idOrdenExamen = idOrdenExamen;
    }

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Catalogo.class, optional = false)
    @JoinColumn(name = "COD_ESTADO", referencedColumnName = "CODIGO", nullable = false)
    @ForeignKey(name = "COD_ESTADO_FK")
    public EstadoOrdenEx getCodEstado() {
        return codEstado;
    }

    public void setCodEstado(EstadoOrdenEx codEstado) {
        this.codEstado = codEstado;
    }


    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_TOMAMX", referencedColumnName = "ID_TOMAMX")
    @ForeignKey(name = "ID_TOMAMX_FK")
    public DaTomaMx getIdTomaMx() {
        return idTomaMx;
    }

    public void setIdTomaMx(DaTomaMx idTomaMx) {
        this.idTomaMx = idTomaMx;
    }

    @Basic
    @Column(name = "FECHAH_ORDEN", nullable = false, insertable = true, updatable = true)
    public Timestamp getFechaHOrden() {
        return fechaHOrden;
    }

    public void setFechaHOrden(Timestamp fechaHOrden) {
        this.fechaHOrden = fechaHOrden;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_EXAMEN", referencedColumnName = "ID_EXAMEN")
    @ForeignKey(name = "ID_EXA_FK")
    public CatalogoExamenes getCodExamen() {
        return codExamen;
    }

    public void setCodExamen(CatalogoExamenes codExamen) {
        this.codExamen = codExamen;
    }


    @ManyToOne(optional = true)
    @JoinColumn(name = "ID_ORDEN_ENVIO", referencedColumnName = "ID_ENVIO_ORDEN")
    @ForeignKey(name = "ENVIORDEN_FK")
    public DaEnvioOrden getEnvio() {
        return envio;
    }

    public void setEnvio(DaEnvioOrden envio) {
        this.envio = envio;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "USUARIO", referencedColumnName = "USUARIO_ID")
    @ForeignKey(name = "USUARIO_FK")
    public Usuarios getUsarioRegistro() {
        return usarioRegistro;
    }

    public void setUsarioRegistro(Usuarios usarioRegistro) {
        this.usarioRegistro = usarioRegistro;
    }
}
