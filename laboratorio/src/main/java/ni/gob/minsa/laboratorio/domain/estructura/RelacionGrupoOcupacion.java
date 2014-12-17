package ni.gob.minsa.laboratorio.domain.estructura;


import java.sql.Timestamp;
import javax.persistence.*;

import ni.gob.minsa.laboratorio.domain.persona.GrupoOcupacion;
import ni.gob.minsa.laboratorio.domain.persona.Ocupacion;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;


@Entity
@Table(name="relacion_grupos_ocupaciones"
    ,schema="alerta"
)
public class RelacionGrupoOcupacion  implements java.io.Serializable {

    private static final long serialVersionUID = 7627001819740899582L;
	
    private long grupoocupacionId;
    private Ocupacion ocupacion;
    private GrupoOcupacion gruposOcupacion;
    private String usuarioRegistro;
    private Timestamp fechaRegistro;

    public RelacionGrupoOcupacion() {
    }
    
    public RelacionGrupoOcupacion(long grupoocupacionId, 
            Ocupacion ocupacion, 
            GrupoOcupacion gruposOcupacion, 
            String usuarioRegistro,
            Timestamp fechaRegistro) {
        
       this.grupoocupacionId = grupoocupacionId;
       this.ocupacion = ocupacion;
       this.gruposOcupacion = gruposOcupacion;
       this.usuarioRegistro = usuarioRegistro;
       this.fechaRegistro = fechaRegistro;
    }
   
    @Id 
    @Column(name="GRUPOOCUPACION_ID", nullable=false, precision=10, scale=0)
    public long getGrupoocupacionId() {
        return this.grupoocupacionId;
    }
    
    public void setGrupoocupacionId(long grupoocupacionId) {
        this.grupoocupacionId = grupoocupacionId;
    }

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToOne(targetEntity=Ocupacion.class,fetch= FetchType.LAZY)
    @JoinColumn(name="CODIGO_OCUPACION",referencedColumnName="CODIGO",nullable=false)
    public Ocupacion getOcupacion() {
        return this.ocupacion;
    }
    
    public void setOcupacion(Ocupacion ocupacion) {
        this.ocupacion = ocupacion;
    }
    
    @ManyToOne(targetEntity=GrupoOcupacion.class,fetch= FetchType.LAZY)
    @JoinColumn(name="CODIGO_CIUO",referencedColumnName="CODIGO_CIUO",nullable=false)
    public GrupoOcupacion getGrupoOcupacion() {
        return this.gruposOcupacion;
    }
    
    public void setGrupoOcupacion(GrupoOcupacion gruposOcupacion) {
        this.gruposOcupacion = gruposOcupacion;
    }
    
    @Column(name="USUARIO_REGISTRO", nullable=false, length=400)
    public String getUsuarioRegistro() {
        return this.usuarioRegistro;
    }
    
    public void setUsuarioRegistro(String usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }
    
    @Column(name="FECHA_REGISTRO", nullable=false)
    public Timestamp getFechaRegistro() {
        return this.fechaRegistro;
    }
    
    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }


}


