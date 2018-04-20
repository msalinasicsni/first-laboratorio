package ni.gob.minsa.laboratorio.domain.catalogos;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;

import javax.persistence.*;

@NamedQueries({
@NamedQuery(
	name = "obtenerSemanasPorCodigo",
	query = "select cat from Semanas cat where cat.codigo = :pCodigo"
	)
})
@Entity
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorValue(value="SEMANASEPI")
public class Semanas extends Catalogo{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Semanas() {
		
	}

}
