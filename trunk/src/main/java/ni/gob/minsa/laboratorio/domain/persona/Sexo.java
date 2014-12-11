package ni.gob.minsa.laboratorio.domain.persona;

import javax.persistence.*;
import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;


@NamedQueries({
@NamedQuery(
	name = "obtenerSexoPorCodigo",
	query = "select cat from Sexo cat where cat.codigo = :pCodigo"
	)
})
@Entity
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorValue(value="SEXO")
public class Sexo extends Catalogo {

    private static final long serialVersionUID = 1L;
	    
    public Sexo() {
    }

}


