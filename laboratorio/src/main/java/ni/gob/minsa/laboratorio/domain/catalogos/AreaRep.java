package ni.gob.minsa.laboratorio.domain.catalogos;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;

import javax.persistence.*;

@NamedQueries({
@NamedQuery(
	name = "obtenerAreaRepPorCodigo",
	query = "select cat from AreaRep cat where cat.codigo = :pCodigo"
	)
})
@Entity
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorValue(value="AREAREP")
public class AreaRep extends Catalogo{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AreaRep() {
		
	}

}
