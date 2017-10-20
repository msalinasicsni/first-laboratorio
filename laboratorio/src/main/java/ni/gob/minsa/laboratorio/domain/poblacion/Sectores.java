package ni.gob.minsa.laboratorio.domain.poblacion;


import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import ni.gob.minsa.laboratorio.domain.estructura.Unidades;

import org.hibernate.annotations.ForeignKey;


@Entity
@Table(name = "sectores", schema = "general")
public class Sectores implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer sectorId;
	private String nombre;
	private String referencias;
	private Unidades unidad;
	private Divisionpolitica municipio;
	private String codigo;
	private char sede = 0;
	private char pasivo = 0;
	private Date fechaRegistro;
	private String usuarioRegistro;
	

	public Sectores() {
	}

	
	@Id
	@Column(name = "SECTOR_ID", nullable = false)
	public Integer getSectorId() {
		return this.sectorId;
	}

	public void setSectorId(Integer sectorId) {
		this.sectorId = sectorId;
	}

	@Column(name = "NOMBRE", nullable = false, length = 100)
	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Column(name = "REFERENCIAS", nullable = true, length = 500)
	public String getReferencias() {
		return this.referencias;
	}

	public void setReferencias(String referencias) {
		this.referencias = referencias;
	}

	@ManyToOne(optional=true)
	@JoinColumn(name="UNIDAD",referencedColumnName="CODIGO", nullable=true)
	@ForeignKey(name = "SECTOR_UNIDAD_FK")
	public Unidades getUnidad() {
		return unidad;
	}

	public void setUnidad(Unidades unidad) {
		this.unidad = unidad;
	}

	@ManyToOne(optional=true)
	@JoinColumn(name="MUNICIPIO",referencedColumnName="CODIGO_NACIONAL", nullable=true)
	@ForeignKey(name = "SECTOR_MUNICIPIO_FK")
	public Divisionpolitica getMunicipio() {
		return municipio;
	}


	public void setMunicipio(Divisionpolitica municipio) {
		this.municipio = municipio;
	}

	@Column(name = "CODIGO", nullable = false, length = 7)
	public String getCodigo() {
		return this.codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	
	@Column(name = "SEDE", nullable = false, length = 1)
	public char getSede() {
		return sede;
	}


	public void setSede(char sede) {
		this.sede = sede;
	}


	@Column(name = "PASIVO", nullable = false, length = 1)
	public char getPasivo() {
		return this.pasivo;
	}

	public void setPasivo(char pasivo) {
		this.pasivo = pasivo;
	}

	@Column(name = "USUARIO_REGISTRO", nullable = false, length = 100)
	public String getUsuarioRegistro() {
		return this.usuarioRegistro;
	}

	public void setUsuarioRegistro(String usuarioRegistro) {
		this.usuarioRegistro = usuarioRegistro;
	}

	@Column(name="FECHA_REGISTRO")
	public Date getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

}
