package ni.gob.minsa.laboratorio.domain.vigilanciaSindFebril;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;

import javax.persistence.*;

/**
 * Created by souyen-ics
 */
@NamedQueries({
        @NamedQuery(name ="getSintomasDSSAByCodigo",
                query = "select cat from SintomasDSSA cat where cat.codigo = :pCodigo")})

@Entity
@Inheritance (strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue(value = "DSSA")
public class SintomasDSSA extends Catalogo {

    private static final long serialVersionUID = -6665592634170716248L;

    public SintomasDSSA() {
    	
    }
}
