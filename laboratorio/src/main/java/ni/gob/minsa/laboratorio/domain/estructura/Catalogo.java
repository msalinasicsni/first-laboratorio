// -----------------------------------------------
// Catalogo.java
// -----------------------------------------------

package ni.gob.minsa.laboratorio.domain.estructura;
import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.DiscriminatorOptions;


@Entity
@Table(name="catalogos", schema="ALERTA")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="DEPENDENCIA",discriminatorType = DiscriminatorType.STRING)
@DiscriminatorOptions(force = true)
public class Catalogo extends BaseEntidadCatalogo implements Serializable {
	private static final long serialVersionUID = 1L;

    public Catalogo() {

    }

    @Override
	public boolean equals(Object other) {
		
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof Catalogo))
			return false;
		
		Catalogo castOther = (Catalogo) other;
		
		return (this.getCatalogoId() == castOther.getCatalogoId())
				&& (this.getCodigo().equals(castOther.getCodigo())) 
						&& (this.getValor().equals(castOther.getValor()));
	}
}