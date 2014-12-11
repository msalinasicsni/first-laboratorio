package ni.gob.minsa.laboratorio.domain.irag;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;

import javax.persistence.*;

/**
 * Created by souyen-ics on 10-10-14.
 */
@NamedQueries({
        @NamedQuery(name ="getResRadiologiaByCodigo",
                query = "select resRad from ResultadoRadiologia resRad where resRad.codigo = :pCodigo")})

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue(value = "RESRAD")
public class ResultadoRadiologia extends Catalogo {

    private static final long serialVersionUID = -4651847893346241580L;

    public ResultadoRadiologia() {
    }
}
