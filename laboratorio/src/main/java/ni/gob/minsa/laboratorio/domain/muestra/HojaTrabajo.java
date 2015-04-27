package ni.gob.minsa.laboratorio.domain.muestra;

import ni.gob.minsa.laboratorio.domain.seguridadlocal.User;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.context.annotation.Lazy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by FIRSTICT on 4/17/2015.
 * V1.0
 */
@Entity
@Table(name = "hoja_trabajo", schema = "laboratorio")
public class HojaTrabajo {

    private String idHojaTrabajo;
    private int numero;
    private Date fechaRegistro;
    private User usuarioRegistro;
    private Laboratorio laboratorio;


    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "ID_HOJA", nullable = false, insertable = true, updatable = true, length = 36)
    public String getIdHojaTrabajo() {
        return idHojaTrabajo;
    }

    public void setIdHojaTrabajo(String idHojaTrabajo) {
        this.idHojaTrabajo = idHojaTrabajo;
    }

    @Column(name = "NUMERO", nullable = false)
    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column(name = "FECHA_REGISTRO", nullable = false)
    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="USUARIO_REGISTRO", referencedColumnName="username", nullable=false)
    @ForeignKey(name = "fk_hojatrabajo_usuarioregistro")
    public User getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(User usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }

    @ManyToOne(optional = true)
    @JoinColumn(name="CODIGO_LAB", referencedColumnName="CODIGO", nullable=true)
    @ForeignKey(name = "fk_hojatrabajo_laboratorio")
    public Laboratorio getLaboratorio() {
        return laboratorio;
    }

    public void setLaboratorio(Laboratorio laboratorio) {
        this.laboratorio = laboratorio;
    }
}
