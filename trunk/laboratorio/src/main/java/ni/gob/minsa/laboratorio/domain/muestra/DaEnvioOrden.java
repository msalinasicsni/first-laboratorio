package ni.gob.minsa.laboratorio.domain.muestra;

import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by FIRSTICT on 11/21/2014.
 */
@Entity
@Table(name = "DA_ENVIO_ORDEN_EXAMEN", schema = "ALERTA")
public class DaEnvioOrden {
    private String idEnvioOrden;
    private String nombreTransporta;
    private Float temperaturaTermo;
    private Timestamp fechaHoraEnvio;
    private long tiempoEspera;
    private Laboratorio laboratorioProcedencia;
    private Usuarios usarioRegistro;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "ID_ENVIO_ORDEN", nullable = false, insertable = true, updatable = true, length = 36)
    public String getIdEnvioOrden() {
        return idEnvioOrden;
    }

    public void setIdEnvioOrden(String idEnvioOrden) {
        this.idEnvioOrden = idEnvioOrden;
    }

    @Basic
    @Column(name = "NOMBRE_TRANSPORTA", nullable = true, insertable = true, updatable = true, length = 100)
    public String getNombreTransporta() {
        return nombreTransporta;
    }

    public void setNombreTransporta(String nombreTransporta) {
        this.nombreTransporta = nombreTransporta;
    }

    @Basic
    @Column(name = "FECHAHORA_ENVIO", nullable = false, insertable = true, updatable = false)
    public Timestamp getFechaHoraEnvio() {
        return fechaHoraEnvio;
    }

    public void setFechaHoraEnvio(Timestamp fechaHoraEnvio) {
        this.fechaHoraEnvio = fechaHoraEnvio;
    }

    @Column(name = "TEMPERATURA", nullable = true)
    public Float getTemperaturaTermo() {
        return temperaturaTermo;
    }

    public void setTemperaturaTermo(Float temperaturaTermo) {
        this.temperaturaTermo = temperaturaTermo;
    }

    @Column(name = "TIEMPO_ESPERA", nullable = true)
    public long getTiempoEspera() {
        return tiempoEspera;
    }

    public void setTiempoEspera(long tiempoEspera) {
        this.tiempoEspera = tiempoEspera;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "LABORATORIO_PROC", referencedColumnName = "CODIGO")
    @ForeignKey(name = "ENVIO_ORDEN_LABORATORIO_FK")
    public Laboratorio getLaboratorioProcedencia() {
        return laboratorioProcedencia;
    }

    public void setLaboratorioProcedencia(Laboratorio laboratorioProcedencia) {
        this.laboratorioProcedencia = laboratorioProcedencia;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "USUARIO", referencedColumnName = "USUARIO_ID")
    @ForeignKey(name = "ENVIO_ORDEN_USUARIO_FK")
    public Usuarios getUsarioRegistro() {
        return usarioRegistro;
    }

    public void setUsarioRegistro(Usuarios usarioRegistro) {
        this.usarioRegistro = usarioRegistro;
    }
}
