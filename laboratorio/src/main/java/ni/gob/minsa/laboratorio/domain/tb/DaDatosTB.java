package ni.gob.minsa.laboratorio.domain.tb;

import ni.gob.minsa.laboratorio.domain.audit.Auditable;
import ni.gob.minsa.laboratorio.domain.notificacion.DaNotificacion;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by miguel on 22/1/2020.
 */
@Entity
@Table(name = "da_datos_tb", schema = "alerta", uniqueConstraints = @UniqueConstraint(columnNames = {"ID_NOTIFICACION"}))
public class DaDatosTB implements Serializable, Auditable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String idDatosTB;
    private DaNotificacion idNotificacion;

    private String poblacion;
    private String comorbilidades;
    private String categoria;
    private String localizacion;

    public DaDatosTB() {
        super();
    }

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "ID_DATOS_TB", nullable = false, insertable = true, updatable = true, length = 36)
    public String getIdDatosTB() {
        return idDatosTB;
    }

    public void setIdDatosTB(String idDatosTB) {
        this.idDatosTB = idDatosTB;
    }

    @OneToOne(targetEntity=DaNotificacion.class)
    @JoinColumn(name = "ID_NOTIFICACION", referencedColumnName = "ID_NOTIFICACION")
    public DaNotificacion getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(DaNotificacion idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    @Column(name = "POBLACION_RIESGO", nullable = true, length = 100)
    public String getPoblacion() {
        return poblacion;
    }

    public void setPoblacion(String poblacion) {
        this.poblacion = poblacion;
    }

    @Column(name = "ENFERMEDADES_CRONICAS", nullable = true, length = 500)
    public String getComorbilidades() {
        return comorbilidades;
    }

    public void setComorbilidades(String comorbilidades) {
        this.comorbilidades = comorbilidades;
    }

    @Column(name = "CATEGORIA_PACIENTE", nullable = true, length = 100)
    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    @Column(name = "LOCALIZACION_INFECCION", nullable = true, length = 100)
    public String getLocalizacion() {
        return localizacion;
    }

    public void setLocalizacion(String localizacion) {
        this.localizacion = localizacion;
    }


    @Override
    public boolean isFieldAuditable(String fieldname) {
        if (fieldname.matches("idDatosTB") || fieldname.matches("idNotificacion"))
            return  false;
        else
            return true;
    }

    @Override
    public String toString() {
        return idDatosTB;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DaDatosTB daDatosTB = (DaDatosTB) o;

        if (!idDatosTB.equals(daDatosTB.idDatosTB)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return idDatosTB.hashCode();
    }
}
