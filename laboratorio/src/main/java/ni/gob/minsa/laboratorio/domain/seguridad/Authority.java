package ni.gob.minsa.laboratorio.domain.seguridad;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;



import org.hibernate.annotations.ForeignKey;

/**
 * Simple objeto de dominio que representa un rol
 * 
 * @author William Aviles
 **/

@Entity
@Table(name = "usuarios_roles", schema = "laboratorio")
public class Authority {
	
	private AuthorityId authId;
	private User user;
	private Rol rol;
	
	
	@Id
	public AuthorityId getAuthId() {
		return authId;
	}
	public void setAuthId(AuthorityId authId) {
		this.authId = authId;
	}
	
	@ManyToOne
	@JoinColumn(name="username", insertable = false, updatable = false)
	@ForeignKey(name = "fk_authorities_users")
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	@ManyToOne
	@JoinColumn(name="authority", insertable = false, updatable = false)
	@ForeignKey(name = "fk_authorities_roles")
	public Rol getRol() {
		return rol;
	}
	public void setRol(Rol rol) {
		this.rol = rol;
	}

}
