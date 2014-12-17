package ni.gob.minsa.laboratorio.domain.persona;

import java.sql.Timestamp;
import java.util.Set;
import javax.persistence.*;

import ni.gob.minsa.laboratorio.domain.estructura.RelacionGrupoOcupacion;


@Entity
@Table(name="grupos_ocupaciones"
    ,schema="alerta"
    , uniqueConstraints = @UniqueConstraint(columnNames="CODIGO_CIUO") 
)
public class GrupoOcupacion  implements java.io.Serializable {

    private static final long serialVersionUID = -1249935011495559589L;
	
    private long grupoOcupacionId;
    private String tituloSp;
    private String codigoCiuo;
    private String observacion;
    private GrupoOcupacion granGrupo;
    private boolean final_;
    private String usuarioRegistro;
    private Timestamp fechaRegistro;
    private boolean pasivo;
     
    //bi-directional many-to-one association to RelacionGruposOcupaciones
    private Set<RelacionGrupoOcupacion> relacionGrupoOcupacion;
    
    public GrupoOcupacion() {
    }

	
    public GrupoOcupacion(long grupoOcupacionId, 
            String tituloSp, 
            String codigoCiuo, 
            boolean final_, 
            String usuarioRegistro, 
            Timestamp fechaRegistro, 
            boolean pasivo) {
        
        this.grupoOcupacionId = grupoOcupacionId;
        this.tituloSp = tituloSp;
        this.codigoCiuo = codigoCiuo;
        this.final_ = final_;
        this.usuarioRegistro = usuarioRegistro;
        this.fechaRegistro = fechaRegistro;
        this.pasivo = pasivo;
    }
    
    public GrupoOcupacion(long grupoOcupacionId, 
            String tituloSp,
            String codigoCiuo,
            String observacion,
            GrupoOcupacion granGrupo,
            boolean final_, 
            String usuarioRegistro, 
            Timestamp fechaRegistro, 
            boolean pasivo) {
        
       this.grupoOcupacionId = grupoOcupacionId;
       this.tituloSp = tituloSp;
       this.codigoCiuo = codigoCiuo;
       this.observacion = observacion;
       this.granGrupo = granGrupo;
       this.final_ = final_;
       this.usuarioRegistro = usuarioRegistro;
       this.fechaRegistro = fechaRegistro;
       this.pasivo = pasivo;
    }
   
    @Id 
    @Column(name="GRUPO_OCUPACION_ID", nullable=false, precision=10, scale=0)
    public long getGrupoOcupacionId() {
        return this.grupoOcupacionId;
    }
    
    public void setGrupoOcupacionId(long grupoOcupacionId) {
        this.grupoOcupacionId = grupoOcupacionId;
    }
    
    @Column(name="TITULO_SP", nullable=false, length=800)
    public String getTituloSp() {
        return this.tituloSp;
    }
    
    public void setTituloSp(String tituloSp) {
        this.tituloSp = tituloSp;
    }
    
    @Column(name="CODIGO_CIUO", nullable=false, length=40)
    public String getCodigoCiuo() {
        return this.codigoCiuo;
    }
    
    public void setCodigoCiuo(String codigoCiuo) {
        this.codigoCiuo = codigoCiuo;
    }
    
    @Column(name="OBSERVACION", length=200)
    public String getObservacion() {
        return this.observacion;
    }
    
    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
    
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="DEPENDENCIA",
				updatable=false,
				nullable=true,
				insertable=false,
				referencedColumnName="CODIGO_CIUO")
    public GrupoOcupacion getGranGrupo() {
        return this.granGrupo;
    }
    
    public void setGranGrupo(GrupoOcupacion granGrupo) {
        this.granGrupo = granGrupo;
    }
    
    @Column(name="FINAL", nullable=false, precision=1, scale=0)
    public boolean isFinal_() {
        return this.final_;
    }
    
    public void setFinal_(boolean final_) {
        this.final_ = final_;
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
    
    @Column(name="PASIVO", nullable=false, precision=1, scale=0)
    public boolean isPasivo() {
        return this.pasivo;
    }
    
    public void setPasivo(boolean pasivo) {
        this.pasivo = pasivo;
    }
    
    //bi-directional many-to-one association to RelacionGruposOcupaciones
    @OneToMany(mappedBy="grupoOcupacion",targetEntity=RelacionGrupoOcupacion.class, fetch=FetchType.LAZY)
    public Set<RelacionGrupoOcupacion> getRelacionGrupoOcupacion() {
        return relacionGrupoOcupacion;
    }

    public void setRelacionGrupoOcupacion(Set<RelacionGrupoOcupacion> relacionGrupoOcupacion) {
        this.relacionGrupoOcupacion = relacionGrupoOcupacion;
    }
}


