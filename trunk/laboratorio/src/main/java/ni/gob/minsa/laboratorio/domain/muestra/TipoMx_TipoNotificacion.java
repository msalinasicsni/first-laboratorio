package ni.gob.minsa.laboratorio.domain.muestra;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;
import ni.gob.minsa.laboratorio.domain.notificacion.TipoNotificacion;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;

/**
 * Created by FIRSTICT on 12/15/2014.
 */
@Entity
@Table(name = "tipomx_tiponotifi", schema = "alerta",uniqueConstraints=@UniqueConstraint(columnNames={"COD_TIPONOTI", "ID_TIPOMX"}))
public class TipoMx_TipoNotificacion {
    Integer id;
    TipoNotificacion tipoNotificacion;
    TipoMx tipoMx;
    boolean pasivo;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_TIPOMX_NOTIFI", nullable = false, updatable = true, insertable = true, precision = 0)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Catalogo.class, optional = false)
    @JoinColumn(name="COD_TIPONOTI", referencedColumnName = "CODIGO", nullable = false)
    @ForeignKey(name = "TMTN_TIPONOTI_FK")
    public TipoNotificacion getTipoNotificacion() {
        return tipoNotificacion;
    }

    public void setTipoNotificacion(TipoNotificacion tipoNotificacion) {
        this.tipoNotificacion = tipoNotificacion;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name="ID_TIPOMX", referencedColumnName = "ID_TIPOMX", nullable = false)
    @ForeignKey(name = "TMTN_TIPOMX_FK")
    public TipoMx getTipoMx() {
        return tipoMx;
    }

    public void setTipoMx(TipoMx tipoMx) {
        this.tipoMx = tipoMx;
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
