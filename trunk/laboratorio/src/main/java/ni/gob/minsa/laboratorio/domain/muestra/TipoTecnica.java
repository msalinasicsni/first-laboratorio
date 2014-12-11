package ni.gob.minsa.laboratorio.domain.muestra;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;
import org.hibernate.annotations.NamedQueries;

import javax.persistence.*;

/**
 * Created by souyen-ics on 11-13-14.
 */

@javax.persistence.NamedQueries({
        @NamedQuery(
                name = "getTipoTecnicaByCodigo",
                query = "select TPTECNC from TipoTecnica TPTECNC where TPTECNC.codigo = :pCodigo"
        )
})

    @Entity
    @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
    @DiscriminatorValue(value = "TPTECNC")
    public class TipoTecnica extends Catalogo {

    public TipoTecnica() {
    }
}


