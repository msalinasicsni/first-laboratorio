package ni.gob.minsa.laboratorio.domain.irag;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;

import javax.persistence.*;

/**
 * Created by souyen-ics
 */
@NamedQueries({
        @NamedQuery(
                name = "getClasificacionByCodigo",
                query = "select cata from Clasificacion cata where cata.codigo = :pCodigo"
        )
})

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue(value = "CLASIFVI")
public class Clasificacion extends Catalogo {

    private static final long serialVersionUID = -8537799539873362151L;

    public Clasificacion() {
    }
}
