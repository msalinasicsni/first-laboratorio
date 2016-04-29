package ni.gob.minsa.laboratorio.domain.seguridadlocal;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


import ni.gob.minsa.laboratorio.domain.audit.Auditable;
import org.hibernate.annotations.ForeignKey;

import java.io.Serializable;

/**
 * Simple objeto de dominio que representa un rol
 * 
 * @author William Aviles
 **/

@Entity
@Table(name = "usuarios_roles", schema = "laboratorio")
public class Authority implements Serializable, Auditable {
	
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

    @Override
    public boolean isFieldAuditable(String fieldname) {
        return false;
    }

    @Override
    public String toString() {
        return "Authority{" +
                "authId=" + authId +
                ", user=" + user +
                ", rol=" + rol +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Authority)) return false;

        Authority authority = (Authority) o;

        if (!authId.equals(authority.authId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return authId.hashCode();
    }
}
