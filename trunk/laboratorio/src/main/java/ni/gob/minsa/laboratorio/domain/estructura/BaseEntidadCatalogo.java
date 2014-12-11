package ni.gob.minsa.laboratorio.domain.estructura;

import javax.persistence.*;

@MappedSuperclass
public class BaseEntidadCatalogo extends BaseEntidadCreacion {
	@Id
	@Column(name="CATALOGO_ID", updatable=false)
	private long catalogoId;

	@Column(name="CODIGO",nullable=true,length=50)
	private String codigo;
	
	@Column(name="VALOR",length=100,nullable=false)
	private String valor;
	
	@Column(name="PASIVO",nullable=false)
	private boolean pasivo;

	@Column(name="FINAL",nullable=false)
	private boolean eFinal;

	@Column(name="ORDEN",nullable=false)
    private Integer orden;
	
	@Column(name="DESCRIPCION",length=200,nullable=true)
    private String descripcion;
    
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="DEPENDENCIA",
				updatable=false,
				nullable=true,
				insertable=false,
				referencedColumnName="CODIGO")
	private Catalogo nodoPadre;
	
	public long getCatalogoId() {
		return this.catalogoId;
	}

	public void setCatalogoId(long catalogoId) {
		this.catalogoId = catalogoId;
	}

	public String getCodigo() {
		return this.codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getValor() {
		return this.valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public boolean getPasivo() {
		return this.pasivo;
	}

	public void setPasivo(boolean pasivo) {
		this.pasivo = pasivo;
	}
	
	// esta asignación es importante
	@Override
	public String toString() {
		return valor;
	}

	public void setOrden(Integer orden) {
		this.orden = orden;
	}

	public Integer getOrden() {
		return orden;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setNodoPadre(Catalogo nodoPadre) {
		this.nodoPadre = nodoPadre;
	}

	public Catalogo getNodoPadre() {
		return nodoPadre;
	}

	public void seteFinal(boolean eFinal) {
		this.eFinal = eFinal;
	}

	public boolean iseFinal() {
		return eFinal;
	}

}
