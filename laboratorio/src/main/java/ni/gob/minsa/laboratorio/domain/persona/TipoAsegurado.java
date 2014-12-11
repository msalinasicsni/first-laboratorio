package ni.gob.minsa.laboratorio.domain.persona;

import javax.persistence.*;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;

@NamedQueries({
@NamedQuery(
	name = "obtenerTipoAseguradoPorCodigo",
	query = "select cat from TipoAsegurado cat where cat.codigo = :pCodigo"
	)
})
@Entity
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorValue(value="TPOAS")
public class TipoAsegurado  extends Catalogo {
   
	private static final long serialVersionUID = 182816082930676722L;
	    
    public TipoAsegurado() {
    
    }

}


