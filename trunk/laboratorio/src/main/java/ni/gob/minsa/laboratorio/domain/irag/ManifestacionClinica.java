package ni.gob.minsa.laboratorio.domain.irag;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;

import javax.persistence.*;

/**
 * Created by souyen-ics on 10-10-14.
 */
@NamedQueries({
        @NamedQuery(name ="getManifestacionClinicaByCodigo",
                query = "select mani from ManifestacionClinica mani where mani.codigo = :pCodigo")})

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue(value = "MANCLIN")
public class ManifestacionClinica extends Catalogo {

    private static final long serialVersionUID = -2882402578513466090L;

    public ManifestacionClinica() {
    }
}
