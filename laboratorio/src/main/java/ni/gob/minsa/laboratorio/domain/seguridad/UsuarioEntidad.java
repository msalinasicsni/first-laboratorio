// -----------------------------------------------
// UsuarioEntidad.java
// -----------------------------------------------
package ni.gob.minsa.laboratorio.domain.seguridad;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import ni.gob.minsa.laboratorio.domain.estructura.EntidadesAdtvas;
import ni.gob.minsa.laboratorio.domain.portal.Sistema;
import ni.gob.minsa.laboratorio.domain.portal.Usuarios;

@Entity
@Table(name="USUARIOS_ENTIDADES", schema="ALERTA")
@NamedQueries({
	@NamedQuery(
			name="entidadesPorUsuario",
			query="select tue from UsuarioEntidad tue " +
					"where tue.usuario.usuarioId=:pUsuarioId " +
					"order by tue.entidadAdtva.nombre")
})
/**
 * La clase persistente para la tabla USUARIOS_ENTIDADES 
 * de la base de datos en el esquema GENERAL
 * <p>
 * @author Marlon Arr�liga
 * @author <a href=mailto:marrolig@hotmail.com>marrolig@hotmail.com</a>
 * @version 1.0, &nbsp; 08/05/2012
 * @since jdk1.6.0_21
 */

public class UsuarioEntidad implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="USUARIO_ENTIDAD_ID", insertable=true, updatable=false, nullable=false, precision=10)
	private long usuarioEntidadId;

    @Temporal( TemporalType.TIMESTAMP)
	@Column(name="FECHA_REGISTRO", updatable=false, nullable=false)
	private Date fechaRegistro;

	@Column(name="USUARIO_REGISTRO", nullable=false, length=100)
	private String usuarioRegistro;

	@ManyToOne(targetEntity=Sistema.class,fetch=FetchType.LAZY)
	@JoinColumn(name="SISTEMA", unique=false, nullable=false, updatable=false)
	private Sistema sistema;

	@ManyToOne(targetEntity=EntidadesAdtvas.class,fetch=FetchType.LAZY)
	@JoinColumn(name="ENTIDAD_ADTVA", nullable=false,updatable=false)
	private EntidadesAdtvas entidadAdtva;

	@ManyToOne(targetEntity=Usuarios.class,fetch=FetchType.LAZY)
	@JoinColumn(name="USUARIO", nullable=false,updatable=false)
	private Usuarios usuario;

    public UsuarioEntidad() {
    }

	public long getUsuarioEntidadId() {
		return this.usuarioEntidadId;
	}

	public void setUsuarioEntidadId(long usuarioEntidadId) {
		this.usuarioEntidadId = usuarioEntidadId;
	}

	public Date getFechaRegistro() {
		return this.fechaRegistro;
	}

	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	public String getUsuarioRegistro() {
		return this.usuarioRegistro;
	}

	public void setUsuarioRegistro(String usuarioRegistro) {
		this.usuarioRegistro = usuarioRegistro;
	}

	public EntidadesAdtvas getEntidadAdtva() {
		return this.entidadAdtva;
	}

	public void setEntidadAdtva(EntidadesAdtvas entidadAdtva) {
		this.entidadAdtva = entidadAdtva;
	}
	
	public Usuarios getUsuario() {
		return this.usuario;
	}

	public void setUsuario(Usuarios usuario) {
		this.usuario = usuario;
	}

	public Sistema getSistema() {
		return this.sistema;
	}

	public void setSistema(Sistema sistema) {
		this.sistema = sistema;
	}
}
