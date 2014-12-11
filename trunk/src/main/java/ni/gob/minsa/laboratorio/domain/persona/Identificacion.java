package ni.gob.minsa.laboratorio.domain.persona;

import javax.persistence.*;
import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;

@NamedQueries({
@NamedQuery(
	name = "obtenerIdentificacionPorCodigo",
	query = "select cat from Identificacion cat where cat.codigo = :pCodigo"
	)
})
@Entity
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorValue(value="TPOID")
public class Identificacion extends Catalogo {
   
    private static final long serialVersionUID = 1L;

    public Identificacion() {
    
    }
    
}


