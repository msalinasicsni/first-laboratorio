package ni.gob.minsa.laboratorio.api;

/**
 * Clase que representa los datos de VIH que se solicitan por el servicio /api/v1/crearSolicitudDx/save
 * para luego guardar en la tabla alerta.da_datos_vih
 */
public class DatosVIH {
    private String resA1;
    private String resA2;
    private String fechaDxVIH;
    private String embarazo;
    private String estadoPx;
    private String infOport;
    private String estaTx;
    private String fechaTAR;
    private String exposicionPeri;
    private String cesarea;

    public String getResA1() {
        return resA1;
    }

    public void setResA1(String resA1) {
        this.resA1 = resA1;
    }

    public String getResA2() {
        return resA2;
    }

    public void setResA2(String resA2) {
        this.resA2 = resA2;
    }

    public String getFechaDxVIH() {
        return fechaDxVIH;
    }

    public void setFechaDxVIH(String fechaDxVIH) {
        this.fechaDxVIH = fechaDxVIH;
    }

    public String getEmbarazo() {
        return embarazo;
    }

    public void setEmbarazo(String embarazo) {
        this.embarazo = embarazo;
    }

    public String getEstadoPx() {
        return estadoPx;
    }

    public void setEstadoPx(String estadoPx) {
        this.estadoPx = estadoPx;
    }

    public String getInfOport() {
        return infOport;
    }

    public void setInfOport(String infOport) {
        this.infOport = infOport;
    }

    public String getEstaTx() {
        return estaTx;
    }

    public void setEstaTx(String estaTx) {
        this.estaTx = estaTx;
    }

    public String getFechaTAR() {
        return fechaTAR;
    }

    public void setFechaTAR(String fechaTAR) {
        this.fechaTAR = fechaTAR;
    }

    public String getExposicionPeri() {
        return exposicionPeri;
    }

    public void setExposicionPeri(String exposicionPeri) {
        this.exposicionPeri = exposicionPeri;
    }

    public String getCesarea() {
        return cesarea;
    }

    public void setCesarea(String cesarea) {
        this.cesarea = cesarea;
    }
}
