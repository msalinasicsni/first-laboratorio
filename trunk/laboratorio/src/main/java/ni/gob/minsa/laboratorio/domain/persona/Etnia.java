package ni.gob.minsa.laboratorio.domain.persona;

import javax.persistence.*;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;

@NamedQueries({
@NamedQuery(
	name = "obtenerEtniaPorCodigo",
	query = "select cat from Etnia cat where cat.codigo = :pCodigo"
	)
})
@Entity
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorValue(value="ETNIA")
public class Etnia extends Catalogo {

    private static final long serialVersionUID = 1L;
	
    public Etnia() {
    }

}


