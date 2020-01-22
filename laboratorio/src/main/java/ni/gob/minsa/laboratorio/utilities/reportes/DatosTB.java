package ni.gob.minsa.laboratorio.utilities.reportes;

/**
 * Created by Miguel Salinas on 22/01/2020.
 * V1.0
 */
public class DatosTB {
    private String idNotificacion;
    private long idPersona;
    private String poblacion;
    private String comorbilidades;
    private String categoria;
    private String localizacion;

    public String getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(String idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    public long getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(long idPersona) {
        this.idPersona = idPersona;
    }

    public String getPoblacion() {
        return poblacion;
    }

    public void setPoblacion(String poblacion) {
        this.poblacion = poblacion;
    }

    public String getComorbilidades() {
        return comorbilidades;
    }

    public void setComorbilidades(String comorbilidades) {
        this.comorbilidades = comorbilidades;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getLocalizacion() {
        return localizacion;
    }

    public void setLocalizacion(String localizacion) {
        this.localizacion = localizacion;
    }
}
