package ni.gob.minsa.laboratorio.domain.irag;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;

import javax.persistence.*;

/**
 * Created by souyen-ics
 */
@NamedQueries({
        @NamedQuery(name ="getRespuestaByCodigo",
                query = "select res from Respuesta res where res.codigo = :pCodigo")})

@Entity
@Inheritance (strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue(value = "RESP")
public class Respuesta extends Catalogo {

    private static final long serialVersionUID = -6665592634170716248L;

    public Respuesta() {
    }
}
