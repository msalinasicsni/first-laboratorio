package ni.gob.minsa.laboratorio.domain.irag;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;

import javax.persistence.*;

/**
 * Created by souyen-ics on 10-10-14.
 */


@NamedQueries({
        @NamedQuery(name ="getCondicionEgresoByCodigo",
                query = "select cond from CondicionEgreso cond where cond.codigo = :pCodigo")})

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue(value = "CONEGRE")
public class CondicionEgreso extends Catalogo {

    private static final long serialVersionUID = 5192985363889113567L;

    public CondicionEgreso() {
    }
}
