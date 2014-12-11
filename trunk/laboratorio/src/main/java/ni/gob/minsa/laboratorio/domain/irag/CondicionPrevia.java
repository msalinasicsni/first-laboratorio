package ni.gob.minsa.laboratorio.domain.irag;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;

import javax.persistence.*;

/**
 * Created by souyen-ics on 10-10-14.
 */
@NamedQueries({
        @NamedQuery(
                name = "getCondicionPreviaByCodigo",
                query = "select cond from CondicionPrevia cond where cond.codigo = :pCodigo"
        )
})

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue(value = "CONDPRE")
public class CondicionPrevia extends Catalogo {

    private static final long serialVersionUID = 489584601282274296L;

    public CondicionPrevia() {
    }
}
