// -----------------------------------------------
// Sistema.java
// -----------------------------------------------
package ni.gob.minsa.laboratorio.domain.portal;

import java.io.Serializable;
import javax.persistence.*;

/**
 * La clase persistente para la tabla SISTEMAS de la base
 * de datos
 * <p>
 * @author Marlon Arróliga 
 * @author <a href=mailto:marrolig@hotmail.com>marrolig@hotmail.com</a>
 * @version 1.0, &nbsp; 08/05/2012
 * @since jdk1.6.0_21
 */
@Entity
@Table(name="sistemas",schema = "portal")
public class Sistema implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	//@SequenceGenerator(name="SISTEMAS_SISTEMAID_GENERATOR", sequenceName="PORTAL.SISTEMAS_SEQ",allocationSize=1)
	//@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SISTEMAS_SISTEMAID_GENERATOR")
	@Column(name="SISTEMA_ID", updatable=false)
	private long sistemaId;

	@Column(name="BLOQUEADO",length=1,nullable=false)
	private char bloqueado;

	@Column(name="CODIGO",length=10,nullable=false,unique=true)
	private String codigo;

	@Column(name="NOMBRE",length=50,nullable=false)
	private String nombre;

	@Column(name="URL_IMAGEN",length=500,nullable=false)
	private String urlImagen;

	@Column(name="URL_INICIO",length=1000,nullable=false)
	private String urlInicio;

	@Column(name="URL_INICIO_INTRANET")
	private String urlInicioIntranet;

    public Sistema() {
    }

    /**
     * Obtiene el identificador del objeto sistema
     */
	public long getSistemaId() {
		return this.sistemaId;
	}

	/**
	 * Establece el valor para el identificador del objeto sistema
	 */
	public void setSistemaId(long sistemaId) {
		this.sistemaId = sistemaId;
	}

	/**
	 * Obtiene el estado de bloqueo del sistema.  Un sistema bloqueado
	 * no puede ser servido al usuario en el portal de aplicaciones.<br>
	 * Retorna <code>"1"</code> si se encuentra bloqueado, 
	 * <code>"0"</code> caso contrario.
	 */
	public char getBloqueado() {
		return this.bloqueado;
	}

	/**
	 * Establece el valor del estado de bloqueo del sistema. Un sistema
	 * bloqueado no puede ser servido al usuario en el portal de
	 * aplicaciones.<br>
	 * <code>"1"</code> si se encuentra bloqueado, 
	 * <code>"0"</code> caso contrario. 
	 */
	public void setBloqueado(char bloqueado) {
		this.bloqueado = bloqueado;
	}

	/**
	 * Obtiene el codigo asignado al sistema en el portafolio
	 * de aplicaciones de la institución.
	 */
	public String getCodigo() {
		return this.codigo;
	}

	/**
	 * Establece el c�digo asignado a la aplicaci�n o sistema.
	 */
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	/**
	 * Obtiene el nombre del sistema
	 */
	public String getNombre() {
		return this.nombre;
	}

	/**
	 * Establece el nombre del sistema
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * Obtiene una cadena con la url de la imagen que
	 * representa al sistema.
	 */
	public String getUrlImagen() {
		return this.urlImagen;
	}

	/**
	 * Establece la url de la imagen que representa
	 * al sistema en el portal de aplicaciones
	 */
	public void setUrlImagen(String urlImagen) {
		this.urlImagen = urlImagen;
	}

	/**
	 * Obtiene la url de la pagina inicial del sistema a la cual
	 * se dirige la acci�n desde el portal de aplicaciones
	 */
	public String getUrlInicio() {
		return this.urlInicio;
	}

	/**
	 * Establece el valor de la url de la p�gina inicial del sistema
	 * para dirigir la acci�n cuando se solicita ingresar al sistema
	 * desde el portal de aplicaciones
	 */
	public void setUrlInicio(String urlInicio) {
		this.urlInicio = urlInicio;
	}

	/**
	 * Obtiene la url de la pagina inicial del sistema a la cual
	 * se dirige la acci�n desde el portal de aplicaciones cuando
	 * es llamada desde la intranet
	 * 
	 * @return Cadena con la url de la p�gina inicial de la aplicaci�n
	 */
	public String getUrlInicioIntranet() {
		return this.urlInicioIntranet;
	}

	/**
	 * Establece el valor de la url de la p�gina inicial del sistema
	 * para dirigir la acci�n cuando se solicita ingresar al sistema
	 * desde el portal de aplicaciones desde la intranet de la instituci�n
	 * 
	 * @param urlInicioIntranet Cadena con la url de la p�gina inicial de la aplicaci�n
	 */
	public void setUrlInicioIntranet(String urlInicioIntranet) {
		this.urlInicioIntranet = urlInicioIntranet;
	}
		
}