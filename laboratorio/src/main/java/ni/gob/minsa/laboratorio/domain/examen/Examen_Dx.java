package ni.gob.minsa.laboratorio.domain.examen;

import ni.gob.minsa.laboratorio.domain.muestra.Catalogo_Dx;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;

/**
 * Created by FIRSTICT on 12/2/2014.
 */
@Entity
@Table(name = "examen_dx", schema = "laboratorio", uniqueConstraints = @UniqueConstraint(columnNames = {"ID_DIAGNOSTICO","ID_EXAMEN"}))
public class Examen_Dx {

    Integer idExamen_Dx;
    Catalogo_Dx diagnostico;
    CatalogoExamenes examen;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "ID_EXAMEN_DX", nullable = false, insertable = true, updatable = true)
    public Integer getIdExamen_Dx() {
        return idExamen_Dx;
    }

    public void setIdExamen_Dx(Integer idExamen_Dx) {
        this.idExamen_Dx = idExamen_Dx;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_DIAGNOSTICO", referencedColumnName = "ID_DIAGNOSTICO",nullable = false)
    @ForeignKey(name="EXAMENDX_DX_FK")
    public Catalogo_Dx getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(Catalogo_Dx diagnostico) {
        this.diagnostico = diagnostico;
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

}
