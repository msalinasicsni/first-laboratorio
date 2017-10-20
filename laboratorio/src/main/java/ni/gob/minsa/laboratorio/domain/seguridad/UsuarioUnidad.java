// -----------------------------------------------
// UsuarioUnidad.java
// -----------------------------------------------
package ni.gob.minsa.laboratorio.domain.seguridad;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import ni.gob.minsa.laboratorio.domain.estructura.Unidades;
import ni.gob.minsa.laboratorio.domain.portal.Sistema;
import ni.gob.minsa.laboratorio.domain.portal.Usuarios;

@Entity
@Table(name="usuarios_unidades", schema="general")
@NamedQueries({
	@NamedQuery(
			name="unidadesPorUsuario",
			query="select tuu from UsuarioUnidad tuu " +
					"where tuu.usuario.usuarioId=:pUsuarioId and " +
					       "tuu.unidad.pasivo='0' " +
					"order by tuu.unidad.nombre")
})
/**
 * La clase persistente para la tabla USUARIOS_UNIDADES 
 * de la base de datos en el esquema GENERAL
 * 
 */
public class UsuarioUnidad implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="USUARIO_UNIDAD_ID", insertable=true, updatable=false, nullable=false, precision=10)
    private long usuarioUnidadId;

    @Temporal( TemporalType.DATE)
    @Column(name="FECHA_REGISTRO", updatable=false, nullable=false)
    private Date fechaRegistro;

    @Column(name="USUARIO_REGISTRO", nullable=false, length=100)
    private String usuarioRegistro;

    @ManyToOne(targetEntity=Sistema.class,fetch=FetchType.LAZY)
    @JoinColumn(name="SISTEMA", unique=false, nullable=false, updatable=false)
    private Sistema sistema;

    @ManyToOne(targetEntity=Unidades.class,fetch=FetchType.LAZY)
    @JoinColumn(name="UNIDAD", unique=false, nullable=false, updatable=false)
    private Unidades unidad;

    @ManyToOne(targetEntity=Usuarios.class,fetch=FetchType.LAZY)
    @JoinColumn(name="USUARIO", nullable=false,updatable=false)
    private Usuarios usuario;

    public UsuarioUnidad() {
    }

	public long getUsuarioUnidadId() {
		return this.usuarioUnidadId;
	}

	public void setUsuarioUnidadId(long usuarioUnidadId) {
		this.usuarioUnidadId = usuarioUnidadId;
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

	public Unidades getUnidad() {
		return this.unidad;
	}

	public void setUnidad(Unidades unidad) {
		this.unidad = unidad;
	}
	
	public Usuarios getUsuario() {
		return this.usuario;
	}

	public void setUsuario(Usuarios usuario) {
		this.usuario = usuario;
	}

	public void setSistema(Sistema sistema) {
		this.sistema = sistema;
	}

	public Sistema getSistema() {
		return sistema;
	}
	
}