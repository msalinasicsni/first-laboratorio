package ni.gob.minsa.laboratorio.domain.vigilanciaSindFebril;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import ni.gob.minsa.laboratorio.domain.audit.Auditable;
import org.hibernate.annotations.ForeignKey;
import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;
import ni.gob.minsa.laboratorio.domain.irag.Respuesta;
import ni.gob.minsa.laboratorio.domain.notificacion.DaNotificacion;
import ni.gob.minsa.laboratorio.domain.estructura.Procedencia;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "da_ficha_sindfeb", schema = "alerta")
public class DaSindFebril implements Serializable, Auditable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private DaNotificacion idNotificacion;

    private String codExpediente;
    private String numFicha;
    private String idLab;
    private Date fechaFicha;

    private Integer semanaEpi;
    private Integer mesEpi;
    private Integer anioEpi;

    private String nombPadre;
    private Procedencia codProcedencia;
    private Respuesta viaje;
    private String dondeViaje;
    private Respuesta embarazo;
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

    private Respuesta hosp;
    private Date fechaIngreso;

    private Respuesta fallecido;
    private Date fechaFallecido;

    private String dxPresuntivo;
    private String dxFinal;

    private String nombreLlenoFicha;

    private String id;

    public DaSindFebril() {
        super();
    }

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "ID_FICHA", nullable = false, insertable = true, updatable = true, length = 36)
    public String getId(){return this.id;}

    public void setId(String id){this.id = id;}

    @OneToOne(targetEntity=DaNotificacion.class)
    @JoinColumn(name = "ID_NOTIFICACION", referencedColumnName = "ID_NOTIFICACION")
    public DaNotificacion getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(DaNotificacion idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    @Column(name = "EXPEDIENTE", nullable = true, length = 50)
    public String getCodExpediente() {
        return codExpediente;
    }

    public void setCodExpediente(String codExpediente) {
        this.codExpediente = codExpediente;
    }

    @Column(name = "NUM_FICHA", nullable = true, length = 10)
    public String getNumFicha() {
        return numFicha;
    }

    public void setNumFicha(String numFicha) {
        this.numFicha = numFicha;
    }

    @Column(name = "ID_LAB", nullable = true, length = 15)
    public String getIdLab() {
        return idLab;
    }

    public void setIdLab(String idLab) {
        this.idLab = idLab;
    }

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column(name = "FECHA_FICHA", nullable = false)
    public Date getFechaFicha() {
        return fechaFicha;
    }

    public void setFechaFicha(Date fechaFicha) {
        this.fechaFicha = fechaFicha;
    }

    @Column(name = "SEMANA", nullable = true)
    public Integer getSemanaEpi() {
        return semanaEpi;
    }

    public void setSemanaEpi(Integer semanaEpi) {
        this.semanaEpi = semanaEpi;
    }

    @Column(name = "MES", nullable = true)
    public Integer getMesEpi() {
        return mesEpi;
    }

    public void setMesEpi(Integer mesEpi) {
        this.mesEpi = mesEpi;
    }

    @Column(name = "ANIO", nullable = true)
    public Integer getAnioEpi() {
        return anioEpi;
    }

    public void setAnioEpi(Integer anioEpi) {
        this.anioEpi = anioEpi;
    }

    @Column(name = "NOMB_PADRE", nullable = true, length = 150)
    public String getNombPadre() {
        return nombPadre;
    }

    public void setNombPadre(String nombPadre) {
        this.nombPadre = nombPadre;
    }

    @ManyToOne(fetch=FetchType.LAZY,targetEntity=Catalogo.class)
    @JoinColumn(name="COD_PROCEDENCIA",referencedColumnName="CODIGO", nullable=true)
    @ForeignKey(name = "SF_PROC_FK")
    public Procedencia getCodProcedencia() {
        return codProcedencia;
    }

    public void setCodProcedencia(Procedencia codProcedencia) {
        this.codProcedencia = codProcedencia;
    }

    @ManyToOne(fetch=FetchType.LAZY,targetEntity=Catalogo.class, optional = true)
    @JoinColumn(name="COD_VIAJO", referencedColumnName = "CODIGO", nullable = true)
    @ForeignKey(name = "SF_VIAJE_FK")
    public Respuesta getViaje() {
        return viaje;
    }

    public void setViaje(Respuesta viaje) {
        this.viaje = viaje;
    }

    @Column(name = "DONDE_VIAJO", nullable = true, length = 250)
    public String getDondeViaje() {
        return dondeViaje;
    }

    public void setDondeViaje(String dondeViaje) {
        this.dondeViaje = dondeViaje;
    }

    @ManyToOne(fetch=FetchType.LAZY,targetEntity=Catalogo.class, optional = true)
    @JoinColumn(name="EMBARAZO", referencedColumnName = "CODIGO", nullable = true)
    @ForeignKey(name = "SF_EMBARAZO_FK")
    public Respuesta getEmbarazo() {
        return embarazo;
    }

    public void setEmbarazo(Respuesta embarazo) {
        this.embarazo = embarazo;
    }

    @Column(name = "MESES_EMBARAZO")
    public int getMesesEmbarazo() {
        return mesesEmbarazo;
    }

    public void setMesesEmbarazo(int mesesEmbarazo) {
        this.mesesEmbarazo = mesesEmbarazo;
    }

    @Column(name = "ENFERMEDAD_CRONICA", nullable = true, length = 250)
    public String getEnfCronica() {
        return enfCronica;
    }

    public void setEnfCronica(String enfCronica) {
        this.enfCronica = enfCronica;
    }

    @Column(name = "OTRA_ENFERMEDAD_CRONICA", nullable = true, length = 250)
    public String getOtraCronica() {
        return otraCronica;
    }

    public void setOtraCronica(String otraCronica) {
        this.otraCronica = otraCronica;
    }

    @Column(name = "ENFERMEDAD_AGUDA", nullable = true, length = 250)
    public String getEnfAgudaAdicional() {
        return enfAgudaAdicional;
    }

    public void setEnfAgudaAdicional(String enfAgudaAdicional) {
        this.enfAgudaAdicional = enfAgudaAdicional;
    }

    @Column(name = "OTRA_ENFERMEDAD_AGUDA", nullable = true, length = 250)
    public String getOtraAgudaAdicional() {
        return otraAgudaAdicional;
    }

    public void setOtraAgudaAdicional(String otraAgudaAdicional) {
        this.otraAgudaAdicional = otraAgudaAdicional;
    }

    @Column(name = "FUENTE_AGUA", nullable = true, length = 250)
    public String getFuenteAgua() {
        return fuenteAgua;
    }

    public void setFuenteAgua(String fuenteAgua) {
        this.fuenteAgua = fuenteAgua;
    }

    @Column(name = "OTRA_FUENTE_AGUA", nullable = true, length = 250)
    public String getOtraFuenteAgua() {
        return otraFuenteAgua;
    }

    public void setOtraFuenteAgua(String otraFuenteAgua) {
        this.otraFuenteAgua = otraFuenteAgua;
    }

    @Column(name = "ANIMALES", nullable = true, length = 250)
    public String getAnimales() {
        return animales;
    }

    public void setAnimales(String animales) {
        this.animales = animales;
    }

    @Column(name = "OTROS_ANIMALES", nullable = true, length = 250)
    public String getOtrosAnimales() {
        return otrosAnimales;
    }

    public void setOtrosAnimales(String otrosAnimales) {
        this.otrosAnimales = otrosAnimales;
    }

    @Column(name = "FECHA_TOMA_MUESTRA", nullable = true)
    public Date getFechaTomaMuestra() {
        return fechaTomaMuestra;
    }

    public void setFechaTomaMuestra(Date fechaTomaMuestra) {
        this.fechaTomaMuestra = fechaTomaMuestra;
    }

    @Column(name = "TEMPERATURA", nullable = true)
    public Float getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(Float temperatura) {
        this.temperatura = temperatura;
    }

    @Column(name = "PRESION_ARTERIAL_SISTOLICA", nullable = true)
    public Integer getPas() {
        return pas;
    }

    public void setPas(Integer pas) {
        this.pas = pas;
    }

    @Column(name = "PRESION_ARTERIAL_DIASTOLICA", nullable = true)
    public Integer getPad() {
        return pad;
    }

    public void setPad(Integer pad) {
        this.pad = pad;
    }

    @Column(name = "SIGNOS_DENGUE_SIN_ALARMA", nullable = true, length = 250)
    public String getSsDSA() {
        return ssDSA;
    }

    public void setSsDSA(String ssDSA) {
        this.ssDSA = ssDSA;
    }

    @Column(name = "SIGNOS_DENGUE_CON_ALARMA", nullable = true, length = 250)
    public String getSsDCA() {
        return ssDCA;
    }

    public void setSsDCA(String ssDCA) {
        this.ssDCA = ssDCA;
    }

    @Column(name = "SIGNOS_DENGUE_SEVERO", nullable = true, length = 250)
    public String getSsDS() {
        return ssDS;
    }

    public void setSsDS(String ssDS) {
        this.ssDS = ssDS;
    }

    @Column(name = "SIGNOS_LEPTOSPIROSIS", nullable = true, length = 250)
    public String getSsLepto() {
        return ssLepto;
    }

    public void setSsLepto(String ssLepto) {
        this.ssLepto = ssLepto;
    }

    @Column(name = "SIGNOS_HANTAVIRUS", nullable = true, length = 250)
    public String getSsHV() {
        return ssHV;
    }

    public void setSsHV(String ssHV) {
        this.ssHV = ssHV;
    }

    @Column(name = "SIGNOS_CHIKUNGUNYA", nullable = true, length = 250)
    public String getSsCK() {
        return ssCK;
    }

    public void setSsCK(String ssCK) {
        this.ssCK = ssCK;
    }

    @ManyToOne(fetch=FetchType.LAZY,targetEntity=Catalogo.class, optional = true)
    @JoinColumn(name="HOSPITALIZADO", referencedColumnName = "CODIGO", nullable = true)
    @ForeignKey(name = "SF_HOSPITALIZADO_FK")
    public Respuesta getHosp() {
        return hosp;
    }

    public void setHosp(Respuesta hosp) {
        this.hosp = hosp;
    }

    @Column(name = "FECHA_HOSPITALIZACION", nullable = true)
    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    @ManyToOne(fetch=FetchType.LAZY,targetEntity=Catalogo.class, optional = true)
    @JoinColumn(name="FALLECIDO", referencedColumnName = "CODIGO", nullable = true)
    @ForeignKey(name = "SF_FALLECIDO_FK")
    public Respuesta getFallecido() {
        return fallecido;
    }

    public void setFallecido(Respuesta fallecido) {
        this.fallecido = fallecido;
    }

    @Column(name = "FECHA_FALLECIMIENTO", nullable = true)
    public Date getFechaFallecido() {
        return fechaFallecido;
    }

    public void setFechaFallecido(Date fechaFallecido) {
        this.fechaFallecido = fechaFallecido;
    }

    @Column(name = "DIAG_PRESUNTIVO", nullable = true, length = 250)
    public String getDxPresuntivo() {
        return dxPresuntivo;
    }

    public void setDxPresuntivo(String dxPresuntivo) {
        this.dxPresuntivo = dxPresuntivo;
    }

    @Column(name = "DIAGNOSTICO_FINAL", nullable = true)
    public String getDxFinal() {
        return dxFinal;
    }

    public void setDxFinal(String dxFinal) {
        this.dxFinal = dxFinal;
    }

    @Column(name = "NOMBRE_COMPLETA_FICHA", nullable = true)
    public String getNombreLlenoFicha() {
        return nombreLlenoFicha;
    }

    public void setNombreLlenoFicha(String nombreLlenoFicha) {
        this.nombreLlenoFicha = nombreLlenoFicha;
    }


    @Override
    public boolean isFieldAuditable(String fieldname) {
        if (fieldname.matches("idNotificacion"))
            return  false;
        else
            return true;
    }
    @Override
    public String toString() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DaSindFebril)) return false;

        DaSindFebril that = (DaSindFebril) o;

        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
