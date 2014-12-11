package ni.gob.minsa.laboratorio.domain.muestra;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;
import org.hibernate.annotations.NamedQueries;

import javax.persistence.*;

/**
 * Created by souyen-ics on 11-13-14.
 */

@javax.persistence.NamedQueries({
        @NamedQuery(
                name = "getTipoEstudioByCodigo",
                query = "select TPESTUD from TipoEstudio TPESTUD where TPESTUD.codigo = :pCodigo"
        )
})

    @Entity
    @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
    @DiscriminatorValue(value = "TPESTUD")
    public class TipoEstudio extends Catalogo {

    public TipoEstudio() {
    }
}


