package ni.gob.minsa.laboratorio.domain.muestra.traslado;

import ni.gob.minsa.laboratorio.domain.muestra.DaEnvioMx;
import ni.gob.minsa.laboratorio.domain.muestra.DaTomaMx;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.User;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by FIRSTICT on 4/30/2015.
 * V1.0
 */
@Entity
@Table(name = "historio_envio_mx", schema = "laboratorio")
public class HistoricoEnvioMx {

    String idHistorico;
    DaTomaMx tomaMx;
    DaEnvioMx envioMx;
    User usuarioRegistro;
    Timestamp fechaHoraRegistro;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "ID_HISTORICO", nullable = false, insertable = true, updatable = true, length = 36)
    public String getIdHistorico() {
        return idHistorico;
    }

    public void setIdHistorico(String idHistorico) {
        this.idHistorico = idHistorico;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_TOMAMX", referencedColumnName = "ID_TOMAMX")
    @ForeignKey(name = "ID_TOMAMX_HISTOENVIO_FK")
    public DaTomaMx getTomaMx() {
        return tomaMx;
    }

    public void setTomaMx(DaTomaMx tomaMx) {
        this.tomaMx = tomaMx;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_ENVIO", referencedColumnName = "ID_ENVIO")
    @ForeignKey(name = "ID_ENVIO_HISTOENVIO_FK")
    public DaEnvioMx getEnvioMx() {
        return envioMx;
    }

    public void setEnvioMx(DaEnvioMx envioMx) {
        this.envioMx = envioMx;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "USUARIO_REGISTRO", referencedColumnName = "username")
    @ForeignKey(name = "USUARIO_HISTOENVIO_FK")
    public User getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(User usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }

    @Basic
    @Column(name = "FECHAH_REGISTRO", nullable = false, insertable = true, updatable = true)
    public Timestamp getFechaHoraRegistro() {
        return fechaHoraRegistro;
    }

    public void setFechaHoraRegistro(Timestamp fechaHoraRegistro) {
        this.fechaHoraRegistro = fechaHoraRegistro;
    }
}
