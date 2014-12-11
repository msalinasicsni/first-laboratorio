package ni.gob.minsa.laboratorio.domain.irag;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;

import javax.persistence.*;

/**
 * Created by souyen-ics on 10-10-14.
 */

@NamedQueries({
        @NamedQuery(name ="getVacunaByCodigo",
                query = "select vac from Vacuna vac where vac.codigo = :pCodigo")})

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue(value = "VAC")
public class Vacuna extends Catalogo {

    private static final long serialVersionUID = -7878479654020764306L;

    public Vacuna() {
    }
}
