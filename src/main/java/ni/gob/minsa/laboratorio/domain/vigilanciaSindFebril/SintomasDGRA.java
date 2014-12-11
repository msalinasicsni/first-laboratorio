package ni.gob.minsa.laboratorio.domain.vigilanciaSindFebril;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;

import javax.persistence.*;

/**
 * Created by souyen-ics
 */
@NamedQueries({
        @NamedQuery(name ="getSintomasDGRAByCodigo",
                query = "select cat from SintomasDGRA cat where cat.codigo = :pCodigo")})

@Entity
@Inheritance (strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue(value = "DGRA")
public class SintomasDGRA extends Catalogo {

    private static final long serialVersionUID = -6665592634170716248L;

    public SintomasDGRA() {
    	
    }
}
