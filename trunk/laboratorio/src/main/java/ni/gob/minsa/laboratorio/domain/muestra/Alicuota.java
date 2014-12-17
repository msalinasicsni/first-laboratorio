package ni.gob.minsa.laboratorio.domain.muestra;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;
import ni.gob.minsa.laboratorio.domain.notificacion.TipoNotificacion;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;

/**
 * Created by FIRSTICT on 12/9/2014.
 */
@Entity
@Table(name = "alicuotas_mx", schema = "laboratorio")
public class Alicuota {
    int idAlicuota;
    String alicuota;
    Float volumen;
    String etiquetaPara;
    int cantidad;
    int dia;
    TipoMx tipoMuestra;
    TipoRecepcionMx tipoRecepcionMx;
    TipoNotificacion tipoNotificacion;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "ID_ALICUOTA", nullable = false, updatable = true, insertable = true, precision = 0)
    public int getIdAlicuota() {
        return idAlicuota;
    }

    public void setIdAlicuota(int idAlicuota) {
        this.idAlicuota = idAlicuota;
    }

    @Basic
    @Column(name = "ALICUOTA", nullable = false, insertable = true, updatable = true, length = 24)
    public String getAlicuota() {
        return alicuota;
    }

    public void setAlicuota(String alicuota) {
        this.alicuota = alicuota;
    }

    @Column(name = "VOLUMEN", nullable = true)
    public Float getVolumen() {
        return volumen;
    }

    public void setVolumen(Float volumen) {
        this.volumen = volumen;
    }

    @Basic
    @Column(name = "ETIQUETA_PARA", nullable = false, insertable = true, updatable = true, length = 100)
    public String getEtiquetaPara() {
        return etiquetaPara;
    }

    public void setEtiquetaPara(String etiquetaPara) {
        this.etiquetaPara = etiquetaPara;
    }

    @Column(name = "CANTIDAD", nullable = false, length = 2)
    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    @Column(name = "DIA", nullable = true, length = 2)
    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Catalogo.class, optional = false)
    @JoinColumn(name = "TIPO_MUESTRA", referencedColumnName = "CODIGO", nullable = false)
    @ForeignKey(name = "ALICUOTA_TIPOMX_FK")
    public TipoMx getTipoMuestra() {
        return tipoMuestra;
    }

    public void setTipoMuestra(TipoMx tipoMuestra) {
        this.tipoMuestra = tipoMuestra;
    }

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Catalogo.class, optional = true)
    @JoinColumn(name = "TIPO_RECEPCION", referencedColumnName = "CODIGO", nullable = false)
    @ForeignKey(name = "ALICUOTA_TIPORECEP_FK")
    public TipoRecepcionMx getTipoRecepcionMx() {
        return tipoRecepcionMx;
    }

    public void setTipoRecepcionMx(TipoRecepcionMx tipoRecepcionMx) {
        this.tipoRecepcionMx = tipoRecepcionMx;
    }

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Catalogo.class, optional = true)
    @JoinColumn(name = "TIPO_NOTIFICACION", referencedColumnName = "CODIGO", nullable = false)
    @ForeignKey(name = "ALICUOTA_TIPONOTI_FK")
    public TipoNotificacion getTipoNotificacion() {
        return tipoNotificacion;
    }

    public void setTipoNotificacion(TipoNotificacion tipoNotificacion) {
        this.tipoNotificacion = tipoNotificacion;
    }
}
