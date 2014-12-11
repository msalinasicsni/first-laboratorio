package ni.gob.minsa.laboratorio.domain.muestra;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;

import javax.persistence.*;

/**
 * Created by souyen-ics on 11-20-14.
 */


@javax.persistence.NamedQueries({
        @NamedQuery(
                name = "getEstadoOrdenExByCodigo",
                query = "select est from EstadoOrdenEx est where est.codigo = :pCodigo"
        )
})
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue(value = "ESTORDEN")
public class EstadoOrdenEx extends Catalogo {

    private static final long serialVersionUID = -3844363255569516837L;

    public EstadoOrdenEx() {
    }
}
