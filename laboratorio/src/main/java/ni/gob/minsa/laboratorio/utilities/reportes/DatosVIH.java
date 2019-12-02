package ni.gob.minsa.laboratorio.utilities.reportes;

import java.sql.Timestamp;

/**
 * Created by Miguel Salinas on 26/11/2019.
 * V1.0
 */
public class DatosVIH {

    private String codigoVIH;
    private String expediente;
    private String resA1;
    private String resA2;
    private Timestamp fechaDxVIH;
    private String embarazo;
    private String estadoPx;
    private String infOport;
    private String estaTx;
    private Timestamp fechaTAR;
    private String exposicionPeri;
    private String cesarea;
    private String ocupacion;

    public String getCodigoVIH() {
        return codigoVIH;
    }

    public void setCodigoVIH(String codigoVIH) {
        this.codigoVIH = codigoVIH;
    }

    public String getExpediente() {
        return expediente;
    }

    public void setExpediente(String expediente) {
        this.expediente = expediente;
    }

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

    public Timestamp getFechaDxVIH() {
        return fechaDxVIH;
    }

    public void setFechaDxVIH(Timestamp fechaDxVIH) {
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

    public Timestamp getFechaTAR() {
        return fechaTAR;
    }

    public void setFechaTAR(Timestamp fechaTAR) {
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

    public String getOcupacion() {
        return ocupacion;
    }

    public void setOcupacion(String ocupacion) {
        this.ocupacion = ocupacion;
    }
}
