package ni.gob.minsa.laboratorio.domain.seguridadlocal;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;


@Embeddable
public class AuthorityId implements Serializable {
	/**
	 * Objeto que representa la clave unica de un rol
	 * 
	 * @author William Aviles
	 **/
	
	private static final long serialVersionUID = 1L;
	private String username;
	private String authority;
	
	public AuthorityId(){
		
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof AuthorityId))
			return false;
		AuthorityId castOther = (AuthorityId) other;

		return (this.getUsername() == castOther.getUsername())
				&& (this.getAuthority() == castOther.getAuthority());
	}

	public int hashCode() {
		int result = 17;
		result = 37 * 3;
		return result;	
	}
	
	@Column(name = "username", nullable = false, length =50)
	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}

	@Column(name = "authority", nullable = false, length =50)
	public String getAuthority() {
		return authority;
	}


	public void setAuthority(String authority) {
		this.authority = authority;
	}
	

}