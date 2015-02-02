package ni.gob.minsa.laboratorio.domain.resultados;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;

import javax.persistence.*;


@NamedQueries({
        @NamedQuery(
                name = "getCatalogoLista",
                query = "select cat from CatalogoLista cat where cat.codigo = :pCodigo"
        )
})

/**
 * Created by souyen-ics.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue(value = "TDATLIS")
public class CatalogoLista extends Catalogo {

    private static final long serialVersionUID = 215694335378247572L;

    public CatalogoLista() {
    }
}
