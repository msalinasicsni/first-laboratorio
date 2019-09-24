package ni.gob.minsa.laboratorio.utilities.reportes;

import java.util.Date;

/**
 * Created by Miguel Salinas on 12/06/2019.
 * V1.0
 */
public class DatosDaSindFebril {

    private String idNotificacion;

    private String codExpediente;
    private String numFicha;
    private String idLab;
    private Date fechaFicha;

    private Integer semanaEpi;
    private Integer mesEpi;
    private Integer anioEpi;

    private String nombPadre;
    private String codProcedencia;
    private String viaje;
    private String dondeViaje;
    private String embarazo;
    private int mesesEmbarazo=0;
    private String enfCronica;
    private String otraCronica;
    private String enfAgudaAdicional;
    private String otraAgudaAdicional;
    private String fuenteAgua;
    private String otraFuenteAgua;
    private String animales;
    private String otrosAnimales;

    private Date fechaTomaMuestra;
    private Float temperatura;
    private Integer pas;
    private Integer pad;

    private String ssDSA;
    private String ssDCA;
    private String ssDS;

    private String ssLepto;
    private String ssHV;
    private String ssCK;

    private String hosp;
    private Date fechaIngreso;

    private String fallecido;
    private Date fechaFallecido;

    private String dxPresuntivo;
    private String dxFinal;

    private String nombreLlenoFicha;

    private String id;

    public String getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(String idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    public String getCodExpediente() {
        return codExpediente;
    }

    public void setCodExpediente(String codExpediente) {
        this.codExpediente = codExpediente;
    }

    public String getNumFicha() {
        return numFicha;
    }

    public void setNumFicha(String numFicha) {
        this.numFicha = numFicha;
    }

    public String getIdLab() {
        return idLab;
    }

    public void setIdLab(String idLab) {
        this.idLab = idLab;
    }

    public Date getFechaFicha() {
        return fechaFicha;
    }

    public void setFechaFicha(Date fechaFicha) {
        this.fechaFicha = fechaFicha;
    }

    public Integer getSemanaEpi() {
        return semanaEpi;
    }

    public void setSemanaEpi(Integer semanaEpi) {
        this.semanaEpi = semanaEpi;
    }

    public Integer getMesEpi() {
        return mesEpi;
    }

    public void setMesEpi(Integer mesEpi) {
        this.mesEpi = mesEpi;
    }

    public Integer getAnioEpi() {
        return anioEpi;
    }

    public void setAnioEpi(Integer anioEpi) {
        this.anioEpi = anioEpi;
    }

    public String getNombPadre() {
        return nombPadre;
    }

    public void setNombPadre(String nombPadre) {
        this.nombPadre = nombPadre;
    }

    public String getCodProcedencia() {
        return codProcedencia;
    }

    public void setCodProcedencia(String codProcedencia) {
        this.codProcedencia = codProcedencia;
    }

    public String getViaje() {
        return viaje;
    }

    public void setViaje(String viaje) {
        this.viaje = viaje;
    }

    public String getDondeViaje() {
        return dondeViaje;
    }

    public void setDondeViaje(String dondeViaje) {
        this.dondeViaje = dondeViaje;
    }

    public String getEmbarazo() {
        return embarazo;
    }

    public void setEmbarazo(String embarazo) {
        this.embarazo = embarazo;
    }

    public int getMesesEmbarazo() {
        return mesesEmbarazo;
    }

    public void setMesesEmbarazo(int mesesEmbarazo) {
        this.mesesEmbarazo = mesesEmbarazo;
    }

    public String getEnfCronica() {
        return enfCronica;
    }

    public void setEnfCronica(String enfCronica) {
        this.enfCronica = enfCronica;
    }

    public String getOtraCronica() {
        return otraCronica;
    }

    public void setOtraCronica(String otraCronica) {
        this.otraCronica = otraCronica;
    }

    public String getEnfAgudaAdicional() {
        return enfAgudaAdicional;
    }

    public void setEnfAgudaAdicional(String enfAgudaAdicional) {
        this.enfAgudaAdicional = enfAgudaAdicional;
    }

    public String getOtraAgudaAdicional() {
        return otraAgudaAdicional;
    }

    public void setOtraAgudaAdicional(String otraAgudaAdicional) {
        this.otraAgudaAdicional = otraAgudaAdicional;
    }

    public String getFuenteAgua() {
        return fuenteAgua;
    }

    public void setFuenteAgua(String fuenteAgua) {
        this.fuenteAgua = fuenteAgua;
    }

    public String getOtraFuenteAgua() {
        return otraFuenteAgua;
    }

    public void setOtraFuenteAgua(String otraFuenteAgua) {
        this.otraFuenteAgua = otraFuenteAgua;
    }

    public String getAnimales() {
        return animales;
    }

    public void setAnimales(String animales) {
        this.animales = animales;
    }

    public String getOtrosAnimales() {
        return otrosAnimales;
    }

    public void setOtrosAnimales(String otrosAnimales) {
        this.otrosAnimales = otrosAnimales;
    }

    public Date getFechaTomaMuestra() {
        return fechaTomaMuestra;
    }

    public void setFechaTomaMuestra(Date fechaTomaMuestra) {
        this.fechaTomaMuestra = fechaTomaMuestra;
    }

    public Float getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(Float temperatura) {
        this.temperatura = temperatura;
    }

    public Integer getPas() {
        return pas;
    }

    public void setPas(Integer pas) {
        this.pas = pas;
    }

    public Integer getPad() {
        return pad;
    }

    public void setPad(Integer pad) {
        this.pad = pad;
    }

    public String getSsDSA() {
        return ssDSA;
    }

    public void setSsDSA(String ssDSA) {
        this.ssDSA = ssDSA;
    }

    public String getSsDCA() {
        return ssDCA;
    }

    public void setSsDCA(String ssDCA) {
        this.ssDCA = ssDCA;
    }

    public String getSsDS() {
        return ssDS;
    }

    public void setSsDS(String ssDS) {
        this.ssDS = ssDS;
    }

    public String getSsLepto() {
        return ssLepto;
    }

    public void setSsLepto(String ssLepto) {
        this.ssLepto = ssLepto;
    }

    public String getSsHV() {
        return ssHV;
    }

    public void setSsHV(String ssHV) {
        this.ssHV = ssHV;
    }

    public String getSsCK() {
        return ssCK;
    }

    public void setSsCK(String ssCK) {
        this.ssCK = ssCK;
    }

    public String getHosp() {
        return hosp;
    }

    public void setHosp(String hosp) {
        this.hosp = hosp;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public String getFallecido() {
        return fallecido;
    }

    public void setFallecido(String fallecido) {
        this.fallecido = fallecido;
    }

    public Date getFechaFallecido() {
        return fechaFallecido;
    }

    public void setFechaFallecido(Date fechaFallecido) {
        this.fechaFallecido = fechaFallecido;
    }

    public String getDxPresuntivo() {
        return dxPresuntivo;
    }

    public void setDxPresuntivo(String dxPresuntivo) {
        this.dxPresuntivo = dxPresuntivo;
    }

    public String getDxFinal() {
        return dxFinal;
    }

    public void setDxFinal(String dxFinal) {
        this.dxFinal = dxFinal;
    }

    public String getNombreLlenoFicha() {
        return nombreLlenoFicha;
    }

    public void setNombreLlenoFicha(String nombreLlenoFicha) {
        this.nombreLlenoFicha = nombreLlenoFicha;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
