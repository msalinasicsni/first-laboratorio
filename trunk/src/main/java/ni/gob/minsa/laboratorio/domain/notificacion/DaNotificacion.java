package ni.gob.minsa.laboratorio.domain.notificacion;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;
import ni.gob.minsa.laboratorio.domain.estructura.EntidadesAdtvas;
import ni.gob.minsa.laboratorio.domain.estructura.Unidades;
import ni.gob.minsa.laboratorio.domain.persona.SisPersona;
import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * Created by souyen-ics on 11-17-14.
 */
@Entity
@Table(name = "DA_NOTIFICACION", schema = "ALERTA")
public class DaNotificacion {

    private String idNotificacion;
    private SisPersona persona;
    private TipoNotificacion codTipoNotificacion;
    private boolean pasivo;
    private EntidadesAdtvas codSilaisAtencion;
    private Unidades codUnidadAtencion;
    private Usuarios usuarioRegistro;
    private Timestamp fechaRegistro;


    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "ID_NOTIFICACION", nullable = false, insertable = true, updatable = true, length = 36)
    public String getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(String idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    @ManyToOne(optional = true)
    @JoinColumn(name = "PERSONA_ID", referencedColumnName = "PERSONA_ID")
    @ForeignKey(name = "PERSONA_ID_NOT_FK")
    public SisPersona getPersona() {
        return persona;
    }

    public void setPersona(SisPersona persona) {
        this.persona = persona;
    }

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Catalogo.class, optional = true)
    @JoinColumn(name = "COD_TIPO_NOTIFICACION", referencedColumnName = "CODIGO", nullable = true)
    @ForeignKey(name = "TIPO_NOTIF_FK")
    public TipoNotificacion getCodTipoNotificacion() {
        return codTipoNotificacion;
    }

    public void setCodTipoNotificacion(TipoNotificacion codTipoNotificacion) {
        this.codTipoNotificacion = codTipoNotificacion;
    }

    @Basic
    @Column(name = "PASIVO", nullable = true, insertable = true, updatable = true)
    public boolean isPasivo() {
        return pasivo;
    }

    public void setPasivo(boolean pasivo) {
        this.pasivo = pasivo;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "USUARIO_ID", referencedColumnName = "USUARIO_ID")
    @ForeignKey(name = "USUARIO_NOTI_FK")
    public Usuarios getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(Usuarios usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }

    @Basic
    @Column(name = "FECHA_REGISTRO", nullable = false, insertable = true, updatable = true)
    public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }


    @ManyToOne(optional = false)
    @JoinColumn(name = "COD_SILAIS_ATENCION", referencedColumnName = "CODIGO")
    @ForeignKey(name = "COD_SILAIS_FK")
    public EntidadesAdtvas getCodSilaisAtencion() {
        return codSilaisAtencion;
    }

    public void setCodSilaisAtencion(EntidadesAdtvas codSilaisAtencion) {
        this.codSilaisAtencion = codSilaisAtencion;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "COD_UNIDAD_ATENCION", referencedColumnName = "CODIGO")
    @ForeignKey(name = "COD_UNIDAD_FK")
    public Unidades getCodUnidadAtencion() {
        return codUnidadAtencion;
    }

    public void setCodUnidadAtencion(Unidades codUnidadAtencion) {
        this.codUnidadAtencion = codUnidadAtencion;
    }

}
