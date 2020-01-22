package ni.gob.minsa.laboratorio.api;

/**
 * Clase que representa los datos de TB que se solicitan por el servicio /api/v1/crearSolicitudDx/save
 * para luego guardar en la tabla alerta.da_datos_tb
 */
public class DatosTB {
    private String poblacion;
    private String comorbilidades;
    private String categoria;
    private String localizacion;

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
