package ni.gob.minsa.laboratorio.domain.irag;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;

import javax.persistence.*;

/**
 * Created by souyen-ics on 10-10-14.
 */

@NamedQueries({
        @NamedQuery(
                name = "getClasificacionFinalByCodigo",
                query = "select clas from ClasificacionFinal clas where clas.codigo = :pCodigo"
        )
})

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue(value = "CLASFI")
public class ClasificacionFinal extends Catalogo {

    private static final long serialVersionUID = 5091358381320252870L;

    public ClasificacionFinal() {
    }
}
