/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ni.gob.minsa.laboratorio.domain.persona;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;

/**
 *
 * @author mdeltrus
 */
@NamedQueries({
@NamedQuery(
	name = "ObtenerParentescoPorCodigo",
	query = "select cat from Parentesco cat where cat.codigo = :pCodigo"
	)
})
@Entity
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorValue(value="PARENTESCO")
public class Parentesco extends Catalogo {
    
    private static final long serialVersionUID = 1L;

    public Parentesco() {
    }
     
     
    
}
