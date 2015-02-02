package ni.gob.minsa.laboratorio.domain.resultados;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;

import javax.persistence.*;


@NamedQueries({
        @NamedQuery(
                name = "getTipoDato",
                query = "select tpdato from TipoDatoCatalogo tpdato where tpdato.codigo = :pCodigo"
        )
})

/**
 * Created by souyen-ics.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue(value = "TPDATO")
public class TipoDatoCatalogo extends Catalogo {

    private static final long serialVersionUID = 6222112588070443487L;

    public TipoDatoCatalogo() {
    }
}
