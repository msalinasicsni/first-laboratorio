package ni.gob.minsa.laboratorio.utilities.dto;

import java.io.Serializable;

/**
 * Created by miguel on 14/8/2020.
 */
public class UsuarioDTO implements Serializable{

    private String username;
    private Boolean enabled;
    private Boolean nivelCentral;
    private String roles;
    private String laboratorio;
    private String descripcion;
    private String email;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getNivelCentral() {
        return nivelCentral;
    }

    public void setNivelCentral(Boolean nivelCentral) {
        this.nivelCentral = nivelCentral;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getLaboratorio() {
        return laboratorio;
    }

    public void setLaboratorio(String laboratorio) {
        this.laboratorio = laboratorio;
    }
}
