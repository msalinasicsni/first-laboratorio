package ni.gob.minsa.laboratorio.utilities.reportes;

import java.util.Date;

/**
 * Created by miguel on 7/5/2021.
 */
public class RecepcionViajeros {

    private String fechaRecepcion;
    private long total;
    private long enLinea;
    private long enRecepcion;

    public String getFechaRecepcion() {
        return fechaRecepcion;
    }

    public void setFechaRecepcion(String fechaRecepcion) {
        this.fechaRecepcion = fechaRecepcion;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getEnLinea() {
        return enLinea;
    }

    public void setEnLinea(long enLinea) {
        this.enLinea = enLinea;
    }

    public long getEnRecepcion() {
        return enRecepcion;
    }

    public void setEnRecepcion(long enRecepcion) {
        this.enRecepcion = enRecepcion;
    }
}
