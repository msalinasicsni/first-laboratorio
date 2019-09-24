package ni.gob.minsa.laboratorio.domain.comunicacionResultados;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "respuesta_hl7", schema = "laboratorio")
public class RespuestaHL7 {
    private String idRespuesta;
    private String idMuestra; //id de muestra que interpreta el equipo. NO es el de da_tomamx
    private String equipo;
    private String trama;
    private Date fechaRegistro;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "ID_RESPUESTA", nullable = false, updatable = false, length = 36)
    public String getIdRespuesta() {
        return idRespuesta;
    }

    public void setIdRespuesta(String idRespuesta) {
        this.idRespuesta = idRespuesta;
    }

    @Column(name = "ID_MUESTRA", nullable = false, length = 36)
    public String getIdMuestra() {
        return idMuestra;
    }

    public void setIdMuestra(String idMuestra) {
        this.idMuestra = idMuestra;
    }

    @Column(name = "EQUIPO", nullable = false, length = 100)
    public String getEquipo() {
        return equipo;
    }

    public void setEquipo(String equipo) {
        this.equipo = equipo;
    }

    @Column(name = "TRAMA", nullable = false, length = 2500)
    public String getTrama() {
        return trama;
    }

    public void setTrama(String trama) {
        this.trama = trama;
    }

    @Basic
    @Column(name = "FECHA_REGISTRO")
    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
