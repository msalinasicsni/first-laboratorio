package ni.gob.minsa.laboratorio.domain.muestra;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;

/**
 * Created by FIRSTICT on 12/10/2014.
 */
@Entity
@Table(name = "TECNICA_LABORATORIO", schema = "LABORATORIO")
public class TecnicaxLaboratorio {
    int idTecnicaLab;
    Laboratorio laboratorio;
    TipoTecnica tecnica;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "ID_TECNICALAB", nullable = false, updatable = true, insertable = true, precision = 0)
    public int getIdTecnicaLab() {
        return idTecnicaLab;
    }

    public void setIdTecnicaLab(int idTecnicaLab) {
        this.idTecnicaLab = idTecnicaLab;
    }

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Catalogo.class, optional = false)
    @JoinColumn(name = "COD_LABORATORIO", referencedColumnName = "CODIGO", nullable = false)
    @ForeignKey(name = "TECLAB_LABORATORIO_FK")
    public Laboratorio getLaboratorio() {
        return laboratorio;
    }

    public void setLaboratorio(Laboratorio laboratorio) {
        this.laboratorio = laboratorio;
    }

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Catalogo.class,optional = false)
    @JoinColumn(name = "COD_TECNICA", referencedColumnName = "CODIGO", nullable = false)
    @ForeignKey(name = "TECLAB_TECNICA_FK")
    public TipoTecnica getTecnica() {
        return tecnica;
    }

    public void setTecnica(TipoTecnica tecnica) {
        this.tecnica = tecnica;
    }
}
