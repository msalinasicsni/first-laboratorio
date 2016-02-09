package ni.gob.minsa.laboratorio.domain.irag;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;
import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
import org.hibernate.annotations.ForeignKey;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by souyen-ics
 */
@Entity
@Table(name = "da_vacunas_irag", schema = "alerta")
public class DaVacunasIrag implements Serializable {



    private Integer idVacuna;
    private DaIrag idNotificacion;
    private Vacuna codVacuna;
    private String codTipoVacuna;
    private Integer dosis;
    private Date fechaUltimaDosis;
    private Timestamp fechaRegistro;
    private boolean pasivo;
    private Usuarios usuario;


    @Id
    @GeneratedValue(strategy=GenerationType.TABLE)
    @Column(name = "ID_VACUNA", nullable = false, updatable = true, insertable = true, precision = 0)
    public Integer getIdVacuna() {
        return idVacuna;
    }
    public void setIdVacuna(Integer idVacuna) {
        this.idVacuna = idVacuna;
    }


    @ManyToOne()
    @JoinColumn(name="ID_NOTIFICACION", referencedColumnName = "ID_NOTIFICACION")
    @ForeignKey(name = "ID_NOTI_VAC_FK")
    public DaIrag getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(DaIrag idNotificacion) {
        this.idNotificacion = idNotificacion;
    }


    @ManyToOne(fetch=FetchType.LAZY,targetEntity=Catalogo.class)
    @JoinColumn(name="COD_VACUNA", referencedColumnName = "CODIGO", nullable = false)
    @ForeignKey(name = "COD_VACUNA_FK")
    public Vacuna getCodVacuna() { return codVacuna; }

    public void setCodVacuna(Vacuna codVacuna) { this.codVacuna = codVacuna; }


    @Basic
    @Column(name = "COD_TIPO_VACUNA", nullable = true, insertable = true, updatable = true, length = 250)

    public String getCodTipoVacuna() { return codTipoVacuna; }

    public void setCodTipoVacuna(String codTipoVacuna) { this.codTipoVacuna = codTipoVacuna; }


    @Basic
    @Column(name = "DOSIS", nullable = true, updatable = true, insertable = true, precision = 0)
    public Integer getDosis() {
        return dosis;
    }

    public void setDosis(Integer dosis) {
        this.dosis = dosis;
    }

    @Basic
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_ULTIMA_DOSIS", nullable = true, insertable = true, updatable = true)
    public Date getFechaUltimaDosis() {
        return fechaUltimaDosis;
    }

    public void setFechaUltimaDosis(Date fechaUltimaDosis) {
        this.fechaUltimaDosis = fechaUltimaDosis;
    }

    @Column(name = "FECHA_REGISTRO", nullable = false, insertable = true, updatable = true)
    public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @Basic
    @Column(name = "PASIVO", nullable = true, insertable = true, updatable = true)
    public boolean isPasivo() { return pasivo; }

    public void setPasivo(boolean pasivo) { this.pasivo = pasivo; }

    @ManyToOne(optional=false)
    @JoinColumn(name="USUARIO_ID", referencedColumnName = "USUARIO_ID")
    @ForeignKey(name = "USUARIO_VAC_FK")
    public Usuarios getUsuario() { return usuario; }

    public void setUsuario(Usuarios usuario) { this.usuario = usuario; }

}
