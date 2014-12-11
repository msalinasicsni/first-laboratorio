package ni.gob.minsa.laboratorio.domain.irag;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;

import javax.persistence.*;

/**
 * Created by souyen-ics on 10-10-14.
 */

@NamedQueries({
        @NamedQuery(name ="getTipoVacunaByCodigo",
                query = "select tvac from TipoVacuna tvac where tvac.codigo = :pCodigo")})

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue(value = "TVAC")
public class TipoVacuna extends Catalogo {

    private static final long serialVersionUID = 6865861644689481531L;

    public TipoVacuna() {
    }
}
