package ni.gob.minsa.laboratorio.utilities.dto;

/**
 * Created by miguel on 2/2/2021.
 */
public class DatosCovidViajeroDTO {

    String lugarDondeViaja;
    String numeroFactura;
    String identificacion;
    String idioma;
    String modalidad;

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

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public String getModalidad() {
        return modalidad;
    }

    public void setModalidad(String modalidad) {
        this.modalidad = modalidad;
    }
}
