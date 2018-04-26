package ni.gob.minsa.laboratorio.domain.catalogos;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;

import javax.persistence.*;

@NamedQueries({
@NamedQuery(
	name = "obtenerAniosPorCodigo",
	query = "select cat from Anios cat where cat.codigo = :pCodigo"
	)
})
@Entity
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorValue(value="ANIOSEPI")
public class Anios extends Catalogo{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Anios() {
		
	}

}
