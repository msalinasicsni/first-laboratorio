package ni.gob.minsa.laboratorio.domain.vih;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;

import javax.persistence.*;

/**
 * Created by JMPS on 27/10/2014.
 */

@NamedQueries({
        @NamedQuery(
                name = "getTipoEdadVihByCodigo",
                query = "select tedad from TipoEdadVih tedad where tedad.codigo = :pCodigo"
        )
})

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue(value = "TEDAD")
public class TipoEdadVih extends Catalogo{

    private static final long serialVersionUID = -8537799539873362151L;

    public TipoEdadVih(){}
}
