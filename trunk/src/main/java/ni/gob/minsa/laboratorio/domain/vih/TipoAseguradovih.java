package ni.gob.minsa.laboratorio.domain.vih;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;

import javax.persistence.*;

/**
 * Created by JMPS on 21/10/2014.
 */

@NamedQueries({
        @NamedQuery(
                name = "getTipoAseguradoByCodigo",
                query = "select tipoas from TipoAseguradovih tipoas where tipoas.codigo = :pCodigo"
        )
})

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue(value = "TPOAS")
public class TipoAseguradovih extends Catalogo {

    private static final long serialVersionUID = -8537799539873362151L;

    public TipoAseguradovih(){}
}
