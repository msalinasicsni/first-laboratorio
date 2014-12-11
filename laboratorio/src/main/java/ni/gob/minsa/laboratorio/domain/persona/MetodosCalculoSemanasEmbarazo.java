package ni.gob.minsa.laboratorio.domain.persona;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;

import javax.persistence.*;

/**
 * Created by JMPS on 29/10/2014.
 */
@NamedQueries({
        @NamedQuery(
                name = "obtenerMetodCalcSeEmbPorCodigo",
                query = "select cat from MetodosCalculoSemanasEmbarazo cat where cat.codigo = :pCodigo"
        )
})
@Entity
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorValue(value="MSEGEST")
public class MetodosCalculoSemanasEmbarazo extends Catalogo {

    private static final long serialVersionUID = 123L;
    public MetodosCalculoSemanasEmbarazo() {
    }
}
