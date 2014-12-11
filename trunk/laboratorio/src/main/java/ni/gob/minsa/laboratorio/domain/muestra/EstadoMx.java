package ni.gob.minsa.laboratorio.domain.muestra;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;

import javax.persistence.*;

/**
 * Created by souyen-ics on 11-13-14.
 */

@javax.persistence.NamedQueries({
        @NamedQuery(
                name = "getEstadoMxByCodigo",
                query = "select est from EstadoMx est where est.codigo = :pCodigo"
        )
})


@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue(value = "ESTDMX")
public class EstadoMx extends Catalogo {

    private static final long serialVersionUID = -2125376946497990948L;

    public EstadoMx() {
    }
}
