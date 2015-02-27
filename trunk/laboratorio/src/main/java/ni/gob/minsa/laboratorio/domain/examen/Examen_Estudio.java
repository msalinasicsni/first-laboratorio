package ni.gob.minsa.laboratorio.domain.examen;

import ni.gob.minsa.laboratorio.domain.muestra.Catalogo_Dx;
import ni.gob.minsa.laboratorio.domain.muestra.Catalogo_Estudio;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;

/**
 * Created by FIRSTICT on 12/2/2014.
 */
@Entity
@Table(name = "examen_estudio", schema = "laboratorio", uniqueConstraints = @UniqueConstraint(columnNames = {"ID_ESTUDIO","ID_EXAMEN"}))
public class Examen_Estudio {

    Integer idExamen_Dx;
    Catalogo_Estudio estudio;
    CatalogoExamenes examen;
    private boolean pasivo;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "ID_EXAMEN_EST", nullable = false, insertable = true, updatable = true)
    public Integer getIdExamen_Dx() {
        return idExamen_Dx;
    }

    public void setIdExamen_Dx(Integer idExamen_Dx) {
        this.idExamen_Dx = idExamen_Dx;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_ESTUDIO", referencedColumnName = "ID_ESTUDIO",nullable = false)
    @ForeignKey(name="EXAMENDX_ESTUDIO_FK")
    public Catalogo_Estudio getEstudio() {
        return estudio;
    }

    public void setEstudio(Catalogo_Estudio diagnostico) {
        this.estudio = diagnostico;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_EXAMEN", referencedColumnName = "ID_EXAMEN",nullable = false)
    @ForeignKey(name="EXAMENDX_EXAMEN_FK")
    public CatalogoExamenes getExamen() {
        return examen;
    }

    public void setExamen(CatalogoExamenes examen) {
        this.examen = examen;
    }

    @Basic
    @Column(name = "PASIVO", nullable = false, insertable = true, updatable = true)
    public boolean isPasivo() {
        return pasivo;
    }

    public void setPasivo(boolean pasivo) {
        this.pasivo = pasivo;
    }
}
