package ni.gob.minsa.laboratorio.utilities.reportes;

import com.google.common.base.MoreObjects;

/**
 * Created by Miguel Salinas on 3/23/2018.
 * V1.0
 * Representa un registro para datos mostrar en el reporte de consolidado por dx, silais, semana y exámen(técnica)
 */
public class ConsolidadoExamen {
    Integer idDiagnostico;
    Integer idExamen;
    Long codigoSilais;
    Integer noSemana;
    Integer noMes;
    String idOrdenExamen;
    String resultado;
    String serotipo;

    public ConsolidadoExamen(){

    }
    public ConsolidadoExamen(Integer idDiagnostico,
                             Integer idExamen,
                             Long codigoSilais,
                             Integer noSemana,
                             Integer noMes,
                             String idOrdenExamen){
        this.idDiagnostico = idDiagnostico;
        this.idExamen = idExamen;
        this.codigoSilais = codigoSilais;
        this.noSemana = noSemana;
        this.noMes = noMes;
        this.idOrdenExamen = idOrdenExamen;
    }

    public Integer getIdDiagnostico() {
        return idDiagnostico;
    }

    public void setIdDiagnostico(Integer idDiagnostico) {
        this.idDiagnostico = idDiagnostico;
    }

    public Integer getIdExamen() {
        return idExamen;
    }

    public void setIdExamen(Integer idExamen) {
        this.idExamen = idExamen;
    }

    public Long getCodigoSilais() {
        return codigoSilais;
    }

    public void setCodigoSilais(Long codigoSilais) {
        this.codigoSilais = codigoSilais;
    }

    public Integer getNoSemana() {
        return noSemana;
    }

    public void setNoSemana(Integer noSemana) {
        this.noSemana = noSemana;
    }

    public Integer getNoMes() {
        return noMes;
    }

    public void setNoMes(Integer noMes) {
        this.noMes = noMes;
    }

    public String getIdOrdenExamen() {
        return idOrdenExamen;
    }

    public void setIdOrdenExamen(String idOrdenExamen) {
        this.idOrdenExamen = idOrdenExamen;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public String getSerotipo() {
        return serotipo;
    }

    public void setSerotipo(String serotipo) {
        this.serotipo = serotipo;
    }

    @Override
    public String toString() {

        return MoreObjects.toStringHelper(ConsolidadoExamen.class)
                .add("idDiagnostico", idDiagnostico)
                .add("idExamen", idExamen)
                .add("codigoSilais", codigoSilais)
                .add("noSemana", noSemana)
                .add("noMes", noMes)
                .add("idOrdenExamen", idOrdenExamen)
                .add("resultado", resultado)
                .add("serotipo", serotipo)
                .toString();
    }
}
