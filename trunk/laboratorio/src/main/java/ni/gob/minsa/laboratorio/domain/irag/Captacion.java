package ni.gob.minsa.laboratorio.domain.irag;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;

import javax.persistence.*;


/**
 * Created by souyen-ics.
 */
@NamedQueries({
        @NamedQuery(name ="getCaptacionByCodigo",
                    query = "select capta from Captacion capta where capta.codigo = :pCodigo")})
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue(value = "CAPTAC")
public class Captacion extends Catalogo{


    private static final long serialVersionUID = 1516215284759621120L;

    public Captacion() {
    }
}
