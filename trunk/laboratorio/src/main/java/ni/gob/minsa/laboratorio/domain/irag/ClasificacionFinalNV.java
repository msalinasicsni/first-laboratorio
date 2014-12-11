package ni.gob.minsa.laboratorio.domain.irag;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;

import javax.persistence.*;

/**
 * Created by souyen-ics on 11-07-14.
 */

@NamedQueries({
        @NamedQuery(
                name = "getClasificacionFinalNVByCodigo",
                query = "select cla from ClasificacionFinalNV cla where cla.codigo = :pCodigo"
        )
})

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue(value = "CLASFNV")
public class ClasificacionFinalNV extends Catalogo {
    private static final long serialVersionUID = -3108098032437177502L;

    public ClasificacionFinalNV() {
    }
}
