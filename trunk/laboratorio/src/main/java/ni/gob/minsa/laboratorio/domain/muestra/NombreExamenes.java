package ni.gob.minsa.laboratorio.domain.muestra;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;

import javax.persistence.*;

/**
 * Created by souyen-ics on 11-27-14.
 */

@javax.persistence.NamedQueries({
        @NamedQuery(
                name = "getExamenByCodigo",
                query = "select exa from NombreExamenes exa where exa.codigo = :pCodigo"
        )
})

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue(value = "NOMEXAM")
public class NombreExamenes extends Catalogo {

    private static final long serialVersionUID = -7903011662357691768L;

    public NombreExamenes() {
    }
}
