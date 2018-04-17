package ni.gob.minsa.laboratorio.utilities.reportes;

import com.google.common.base.MoreObjects;

/**
 * Created by Miguel Salinas on 3/23/2018.
 * V1.0
 * Representa un registro para datos mostrar en el reporte de consolidado por dx, silais, semana y exámen(técnica)
 */
public class ConsolidadoExamenRespuesta {
    String idOrdenExamen;
    String valor;
    Integer idRespuesta;
    Integer idConcepto;
    String tipoConcepto;

    public ConsolidadoExamenRespuesta(){

    }
    public ConsolidadoExamenRespuesta(
                                      String idOrdenExamen,
                                      Integer idRespuesta,
                                      Integer idConcepto,
                                      String tipoConcepto,
                                      String valor){
        this.idOrdenExamen = idOrdenExamen;
        this.valor = valor;
        this.idRespuesta = idRespuesta;
        this.idConcepto = idConcepto;
        this.tipoConcepto = tipoConcepto;

    }
    public String getIdOrdenExamen() {
        return idOrdenExamen;
    }

    public void setIdOrdenExamen(String idOrdenExamen) {
        this.idOrdenExamen = idOrdenExamen;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public Integer getIdRespuesta() {
        return idRespuesta;
    }

    public void setIdRespuesta(Integer idRespuesta) {
        this.idRespuesta = idRespuesta;
    }

    public Integer getIdConcepto() {
        return idConcepto;
    }

    public void setIdConcepto(Integer idConcepto) {
        this.idConcepto = idConcepto;
    }

    public String getTipoConcepto() {
        return tipoConcepto;
    }

    public void setTipoConcepto(String tipoConcepto) {
        this.tipoConcepto = tipoConcepto;
    }

    @Override
    public String toString() {

        return MoreObjects.toStringHelper(ConsolidadoExamenRespuesta.class)
                .add("idOrdenExamen", idOrdenExamen)
                .add("valor", valor)
                .add("idRespuesta", idRespuesta)
                .add("idConcepto", idConcepto)
                .add("tipoConcepto", tipoConcepto)
                .toString();
    }
}
