package ni.gob.minsa.laboratorio.utilities.dto;

/**
 * Created by miguel on 2/2/2021.
 */
public class DatosCovidViajeroDTO {

    String lugarDondeViaja;
    String numeroFactura;
    String identificacion;

    public String getLugarDondeViaja() {
        return lugarDondeViaja;
    }

    public void setLugarDondeViaja(String lugarDondeViaja) {
        this.lugarDondeViaja = lugarDondeViaja;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }
}
