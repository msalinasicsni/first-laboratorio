package ni.gob.minsa.laboratorio.utilities.reportes;

/**
 * Created by miguel on 17/3/2021.
 */
public class ExcelVigilanciaRequest {
    String codes;
    String languaje;
    String strB64Response;

    public String getCodes() {
        return codes;
    }

    public void setCodes(String codes) {
        this.codes = codes;
    }

    public String getLanguaje() {
        return languaje;
    }

    public void setLanguaje(String languaje) {
        this.languaje = languaje;
    }

    public String getStrB64Response() {
        return strB64Response;
    }

    public void setStrB64Response(String strB64Response) {
        this.strB64Response = strB64Response;
    }
}
