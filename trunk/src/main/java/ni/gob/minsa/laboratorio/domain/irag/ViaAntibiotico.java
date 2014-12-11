package ni.gob.minsa.laboratorio.domain.irag;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;

import javax.persistence.*;

/**
 * Created by souyen-ics on 10-10-14.
 */
@NamedQueries({
        @NamedQuery(name ="getViaAntibioticoByCodigo",
                query = "select via from ViaAntibiotico via where via.codigo = :pCodigo")})
@Entity
@Inheritance (strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue(value = "VIA")
public class ViaAntibiotico extends Catalogo {

    private static final long serialVersionUID = 9054924612240708036L;

    public ViaAntibiotico() {
    }
}
