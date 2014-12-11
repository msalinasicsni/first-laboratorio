package ni.gob.minsa.laboratorio.domain.vih;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;

import javax.persistence.*;

/**
 * Created by JMPS on 29/10/2014.
 */
@NamedQueries({
        @NamedQuery(
                name = "obtenerPeriodoPruebaVihEmbPorCodigo",
                query = "select cat from PeriodoPruebaVihEmb cat where cat.codigo = :pCodigo"
        )
})
@Entity
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorValue(value="PERVIHEMB")
public class PeriodoPruebaVihEmb extends Catalogo{

    private static final long serialVersionUID = 12433L;

    public PeriodoPruebaVihEmb() {
    }
}
