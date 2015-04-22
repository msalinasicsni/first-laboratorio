package ni.gob.minsa.laboratorio.domain.muestra;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.context.annotation.Lazy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by FIRSTICT on 4/17/2015.
 * V1.0
 */
@Entity
@Table(name = "mx_hoja_trabajo", schema = "laboratorio")
public class Mx_HojaTrabajo implements Serializable {
    private String idMxHoja;
    private DaTomaMx tomaMx;
    private HojaTrabajo hojaTrabajo;
    private Date fechaRegistro;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "ID_MX_HOJA", nullable = false, insertable = true, updatable = true, length = 36)
    public String getIdMxHoja() {
        return idMxHoja;
    }

    public void setIdMxHoja(String idMxHoja) {
        this.idMxHoja = idMxHoja;
    }


    @ManyToOne(optional =  false)
    @JoinColumn(name="ID_TOMAMX", referencedColumnName="ID_TOMAMX", nullable=false)
    public DaTomaMx getTomaMx() {
        return tomaMx;
    }

    public void setTomaMx(DaTomaMx tomaMx) {
        this.tomaMx = tomaMx;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name="ID_HOJA", referencedColumnName="ID_HOJA", nullable=false)
    public HojaTrabajo getHojaTrabajo() {
        return hojaTrabajo;
    }

    public void setHojaTrabajo(HojaTrabajo hojaTrabajo) {
        this.hojaTrabajo = hojaTrabajo;
    }

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column(name = "FECHA_REGISTRO", nullable = false)
    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
