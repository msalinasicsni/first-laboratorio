package ni.gob.minsa.laboratorio.domain.muestra;

import ni.gob.minsa.laboratorio.domain.examen.CatalogoExamenes;
import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by souyen-ics on 01-06-2015.
 */
@Entity
@Table(name = "alicuotas_registro", schema = "laboratorio")
public class AlicuotaRegistro implements Serializable {
    String idAlicuota;
    Alicuota alicuotaCatalogo;
    Float volumen;
    DaTomaMx codUnicoMx;
    Timestamp fechaHoraRegistro;
    OrdenExamen idOrden;
    Usuarios usuarioRegistro;
    boolean pasivo;

    @Id
    @Column(name = "ID_ALICUOTA", nullable = false, updatable = true, insertable = true, length = 16)
    public String getIdAlicuota() {
        return idAlicuota;
    }

    public void setIdAlicuota(String idAlicuota) {
        this.idAlicuota = idAlicuota;
    }

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToOne(optional = false)
    @JoinColumn(name = "ALICUOTA_CATALOGO", referencedColumnName = "ID_ALICUOTA")
    @ForeignKey(name = "ID_ALIC_FK")
     public Alicuota getAlicuotaCatalogo() {
         return alicuotaCatalogo;
     }

    public void setAlicuotaCatalogo(Alicuota alicuotaCatalogo) {
        this.alicuotaCatalogo = alicuotaCatalogo;
    }

    @Column(name = "VOLUMEN", nullable = false)
    public Float getVolumen() {
        return volumen;
    }
    public void setVolumen(Float volumen) {
        this.volumen = volumen;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "CODUNICOMX", referencedColumnName = "CODUNICOMX")
    @ForeignKey(name = "CODUNICOMX_FK")
    public DaTomaMx getCodUnicoMx() {
        return codUnicoMx;
    }

    public void setCodUnicoMx(DaTomaMx codUnicoMx) {
        this.codUnicoMx = codUnicoMx;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_ORDEN_EXAMEN", referencedColumnName = "ID_ORDEN_EXAMEN")
    @ForeignKey(name = "ID_ORDENEX_FK")
    public OrdenExamen getIdOrden() {
        return idOrden;
    }

    public void setIdOrden(OrdenExamen idOrden) {
        this.idOrden = idOrden;
    }

    @Basic
    @Column(name = "FECHAH_REGISTRO", nullable = false, insertable = true, updatable = false)
    public Timestamp getFechaHoraRegistro() {
        return fechaHoraRegistro;
    }

    public void setFechaHoraRegistro(Timestamp fechaHoraRegistro) {
        this.fechaHoraRegistro = fechaHoraRegistro;
    }

    @ManyToOne(optional = true)
    @JoinColumn(name = "USUARIO_REGISTRO", referencedColumnName = "USUARIO_ID")
    @ForeignKey(name = "USUARIO_REG_FK")
    public Usuarios getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(Usuarios usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }

    @Basic
    @Column(name = "PASIVO", nullable = true, insertable = true, updatable = true)
    public boolean isPasivo() { return pasivo; }

    public void setPasivo(boolean pasivo) { this.pasivo = pasivo; }
}
