package ni.gob.minsa.laboratorio.api;

import java.io.Serializable;

/**
 * Created by miguel on 13/2/2020.
 */
public class DatosSolicitud implements Serializable {

    private String idSolicitud;
    private String nombre;

    public DatosSolicitud(String idSolicitud, String nombre){
        this.idSolicitud = idSolicitud;
        this.nombre = nombre;
    }

    public String getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(String idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
