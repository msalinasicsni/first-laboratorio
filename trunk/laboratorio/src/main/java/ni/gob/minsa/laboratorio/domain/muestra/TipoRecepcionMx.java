package ni.gob.minsa.laboratorio.domain.muestra;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;
import org.hibernate.annotations.NamedQueries;

import javax.persistence.*;

/**
 * Created by souyen-ics on 11-13-14.
 */

@javax.persistence.NamedQueries({
        @NamedQuery(
                name = "getTipoRecepcionMxByCodigo",
                query = "select tprecpmx from TipoRecepcionMx tprecpmx where tprecpmx.codigo = :pCodigo"
        )
})

    @Entity
    @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
    @DiscriminatorValue(value = "TPRECPMX")
    public class TipoRecepcionMx extends Catalogo {

    public TipoRecepcionMx() {
    }
}


