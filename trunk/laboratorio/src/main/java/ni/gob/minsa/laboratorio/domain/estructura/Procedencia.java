package ni.gob.minsa.laboratorio.domain.estructura;

import javax.persistence.*;

/**
 * Created by FIRSTICT on 10/7/2014.
 */
@NamedQueries({
        @NamedQuery(
                name = "obtenerProcedenciaPorCodigo",
                query = "select cat from Procedencia cat where cat.codigo = :pCodigo"
        )
})
@Entity
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorValue(value= "PROCDNCIA")
public class Procedencia extends Catalogo {
    private static final long serialVersionUID = 1L;

    public Procedencia() {
    }
}
