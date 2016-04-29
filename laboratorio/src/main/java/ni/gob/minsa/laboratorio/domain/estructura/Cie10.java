package ni.gob.minsa.laboratorio.domain.estructura;


import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@NamedQueries({
	@NamedQuery(
		name = "cie10PorCodigoCie10",
		query = "select pat from Cie10 pat where pat.codigoCie10 = :pCodigo"
	)
})

@Entity
@Table(name="sim_cie10",schema="ALERTA")
public class Cie10  implements Serializable {

     /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long idCie10;
	private String codigoCie10;
	private String nombreCie10;
	private Boolean notificacionInmediata;
	private Boolean activo;
	private Date fechaRegistro;
	private Long eliminado;
	private Boolean notificacionEspacial;

    public Cie10() {
    }

	
    public Cie10(long idCie10, String codigoCie10, String nombreCie10, boolean activo, Date fechaRegistro) {
        this.idCie10 = idCie10;
        this.codigoCie10 = codigoCie10;
        this.nombreCie10 = nombreCie10;
        this.activo = activo;
        this.fechaRegistro = fechaRegistro;
    }
    public Cie10(long idCie10, String codigoCie10, String nombreCie10, Boolean notificacionInmediata, boolean activo, Date fechaRegistro, Long eliminado) {
       this.idCie10 = idCie10;
       this.codigoCie10 = codigoCie10;
       this.nombreCie10 = nombreCie10;
       this.notificacionInmediata = notificacionInmediata;
       this.activo = activo;
       this.fechaRegistro = fechaRegistro;
       this.eliminado = eliminado;
    }
   
    
    @Id 
    @Column(name="ID_CIE10", length=10)
    public long getIdCie10() {
        return this.idCie10;
    }
    
    public void setIdCie10(long idCie10) {
        this.idCie10 = idCie10;
    }

    @Column(name="CODIGO_CIE10", nullable=false, length=10)
    public String getCodigoCie10() {
        return this.codigoCie10;
    }
    
    public void setCodigoCie10(String codigoCie10) {
        this.codigoCie10 = codigoCie10;
    }
    
    @Column(name="NOMBRE_CIE10", nullable=false, length=500)
    public String getNombreCie10() {
        return this.nombreCie10;
    }
    
    public void setNombreCie10(String nombreCie10) {
        this.nombreCie10 = nombreCie10;
    }
    
    @Column(name="NOTIFICACION_INMEDIATA", length=1)
    public Boolean getNotificacionInmediata() {
        return this.notificacionInmediata;
    }
    
    public void setNotificacionInmediata(Boolean notificacionInmediata) {
        this.notificacionInmediata = notificacionInmediata;
    }
    
    @Column(name="NOTIFICACION_ESPACIAL", length=1)
    public Boolean getNotificacionEspacial() {
		return notificacionEspacial;
	}

	public void setNotificacionEspacial(Boolean notificacionEspacial) {
		this.notificacionEspacial = notificacionEspacial;
	}

	@Column(name="ACTIVO", nullable=false, length=1)
    public boolean isActivo() {
        return this.activo;
    }
    
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="FECHA_REGISTRO")
    public Date getFechaRegistro() {
        return this.fechaRegistro;
    }
    
    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
    
    @Column(name="ELIMINADO", length=1)
    public Long getEliminado() {
        return this.eliminado;
    }
    
    public void setEliminado(Long eliminado) {
        this.eliminado = eliminado;
    }

    @Override
    public String toString() {
        return "Cie10{" +
                "idCie10=" + idCie10 +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cie10)) return false;

        Cie10 cie10 = (Cie10) o;

        if (idCie10 != cie10.idCie10) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (idCie10 ^ (idCie10 >>> 32));
    }
}
