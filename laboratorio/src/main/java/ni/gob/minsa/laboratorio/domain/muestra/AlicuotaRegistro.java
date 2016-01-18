package ni.gob.minsa.laboratorio.domain.muestra;

import ni.gob.minsa.laboratorio.domain.seguridadlocal.User;
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
    DaSolicitudEstudio solicitudEstudio;
    DaSolicitudDx solicitudDx;
    User usuarioRegistro;
    boolean pasivo;

    @Id
    @Column(name = "ID_ALICUOTA", nullable = false, updatable = true, insertable = true, length = 24)
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

    @Column(name = "VOLUMEN", nullable = true)
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

    @ManyToOne(optional = true)
    @JoinColumn(name = "ID_SOLICITUD_EST", referencedColumnName = "ID_SOLICITUD_EST")
    @ForeignKey(name = "ID_SOLI_EST_FK")
    public DaSolicitudEstudio getSolicitudEstudio() {
        return solicitudEstudio;
    }

    public void setSolicitudEstudio(DaSolicitudEstudio solicitudEstudio) {
        this.solicitudEstudio = solicitudEstudio;
    }

    @ManyToOne(optional = true)
    @JoinColumn(name = "ID_SOLICITUD_DX", referencedColumnName = "ID_SOLICITUD_DX")
    @ForeignKey(name = "ID_SOLI_EST_FK")
    public DaSolicitudDx getSolicitudDx() {
        return solicitudDx;
    }

    public void setSolicitudDx(DaSolicitudDx solicitudDx) {
        this.solicitudDx = solicitudDx;
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
    @JoinColumn(name = "USUARIO_REGISTRO", referencedColumnName = "username")
    @ForeignKey(name = "AR_USUARIO_REG_FK")
    public User getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(User usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }

    @Basic
    @Column(name = "PASIVO", nullable = true, insertable = true, updatable = true)
    public boolean isPasivo() { return pasivo; }

    public void setPasivo(boolean pasivo) { this.pasivo = pasivo; }
}
