package ni.gob.minsa.laboratorio.domain.muestra;

import ni.gob.minsa.laboratorio.domain.examen.CatalogoExamenes;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;

/**
 * Created by FIRSTICT on 12/15/2014.
 */
@Entity
@Table(name = "examen_tipomx_tiponoti", schema = "alerta",uniqueConstraints=@UniqueConstraint(columnNames={"ID_EXAMEN", "ID_TIPOMX_NOTIFI"}))
public class Examen_TipoMxTipoNoti {
    Integer idExamenTipoMxNt;
    CatalogoExamenes examen;
    TipoMx_TipoNotificacion tipoMxTipoNotificacion;
    boolean pasivo;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_EXAMEN_TIPOMXNT", nullable = false, updatable = true, insertable = true, precision = 0)
    public Integer getIdExamenTipoMxNt() {
        return idExamenTipoMxNt;
    }

    public void setIdExamenTipoMxNt(Integer idExamenTipoMxNt) {
        this.idExamenTipoMxNt = idExamenTipoMxNt;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name="ID_EXAMEN", referencedColumnName = "ID_EXAMEN", nullable = false)
    @ForeignKey(name = "EXTMX_EXAMEN_FK")
    public CatalogoExamenes getExamen() {
        return examen;
    }

    public void setExamen(CatalogoExamenes examen) {
        this.examen = examen;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name="ID_TIPOMX_NOTIFI", referencedColumnName = "ID_TIPOMX_NOTIFI", nullable = false)
    @ForeignKey(name = "EXTMX_TIPOMXTIPONOTI_FK")
    public TipoMx_TipoNotificacion getTipoMxTipoNotificacion() {
        return tipoMxTipoNotificacion;
    }

    public void setTipoMxTipoNotificacion(TipoMx_TipoNotificacion tipoMx) {
        this.tipoMxTipoNotificacion = tipoMx;
    }

    @Basic
    @Column(name = "PASIVO", nullable = true, insertable = true, updatable = true)
    public boolean isPasivo() {
        return pasivo;
    }

    public void setPasivo(boolean pasivo) {
        this.pasivo = pasivo;
    }
}
