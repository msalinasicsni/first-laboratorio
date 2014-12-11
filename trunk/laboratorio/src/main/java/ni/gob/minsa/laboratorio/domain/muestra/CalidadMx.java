package ni.gob.minsa.laboratorio.domain.muestra;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;
import org.hibernate.annotations.NamedQueries;

import javax.persistence.*;

/**
 * Created by souyen-ics on 11-13-14.
 */

@javax.persistence.NamedQueries({
        @NamedQuery(
                name = "getCalidadMxByCodigo",
                query = "select calidmx from CalidadMx calidmx where calidmx.codigo = :pCodigo"
        )
})

    @Entity
    @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
    @DiscriminatorValue(value = "CALIDMX")
    public class CalidadMx extends Catalogo {

    public CalidadMx() {
    }
}


