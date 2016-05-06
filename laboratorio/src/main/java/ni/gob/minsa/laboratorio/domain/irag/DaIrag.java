package ni.gob.minsa.laboratorio.domain.irag;

import ni.gob.minsa.laboratorio.domain.audit.Auditable;
import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;
import ni.gob.minsa.laboratorio.domain.estructura.Cie10;
import ni.gob.minsa.laboratorio.domain.notificacion.DaNotificacion;
import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
import ni.gob.minsa.laboratorio.domain.estructura.Procedencia;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;


/**
 * Created by souyen-ics
 */
@Entity
@Table(name = "da_irag", schema = "alerta")
public class DaIrag implements Serializable, Auditable {

    private static final long serialVersionUID = 4700369684537438371L;
    private DaNotificacion idNotificacion;
    private Date fechaConsulta;
    private Date fechaPrimeraConsulta;
    private String codExpediente;
    private Clasificacion codClasificacion;
    private String nombreMadreTutor;
    private Procedencia codProcedencia;
    private Captacion codCaptacion;
    private Cie10 diagnostico;
    private Integer tarjetaVacuna;
    private Respuesta codAntbUlSem;
    private Integer cantidadAntib;
    private String nombreAntibiotico;
    private Date fechaPrimDosisAntib;
    private Date fechaUltDosisAntib;
    private Integer noDosisAntib;
    private ViaAntibiotico codViaAntb;
    private Respuesta usoAntivirales;
    private String nombreAntiviral;
    private Date fechaPrimDosisAntiviral;
    private Date fechaUltDosisAntiviral;
    private Integer noDosisAntiviral;
    private String codResRadiologia;
    private String otroResultadoRadiologia;
    private Integer uci;
    private Integer noDiasHospitalizado;
    private Integer ventilacionAsistida;
    private Cie10 diagnostico1Egreso;
    private Cie10 diagnostico2Egreso;
    private Date fechaEgreso;
    private CondicionEgreso codCondEgreso;
    private String codClasFCaso;
    private ClasificacionFinalNB codClasFDetalleNB;
    private ClasificacionFinalNV codClasFDetalleNV;
    private String agenteBacteriano;
    private String serotipificacion;
    private String agenteViral;
    private String agenteEtiologico;
    private String manifestaciones;
    private String otraManifestacion;
    private String condiciones;
    private Integer semanasEmbarazo;
    private String otraCondicion;
    private Timestamp fechaRegistro;
    private Usuarios usuario;
    private String id;

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

    @Basic
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column(name = "FECHA_CONSULTA", nullable = true, insertable = true, updatable = true)
    public Date getFechaConsulta() {
        return fechaConsulta;
    }

    public void setFechaConsulta(Date fechaConsulta) {
        this.fechaConsulta = fechaConsulta;
    }

    @Basic
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column(name = "FECHA_PRIMERA_CONSULTA", nullable = true, insertable = true, updatable = true)
    public Date getFechaPrimeraConsulta() {
        return fechaPrimeraConsulta;
    }

    public void setFechaPrimeraConsulta(Date fechaPrimeraConsulta) {
        this.fechaPrimeraConsulta = fechaPrimeraConsulta;
    }

    @Basic
    @Column(name = "COD_EXPEDIENTE", nullable = true, insertable = true, updatable = true, length = 30)
    public String getCodExpediente() {
        return codExpediente;
    }

    public void setCodExpediente(String codExpediente) {
        this.codExpediente = codExpediente;
    }


    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Catalogo.class, optional = true)
    @JoinColumn(name = "COD_CLASIFICACION", referencedColumnName = "CODIGO", nullable = true)
    @ForeignKey(name = "COD_CLASIF_FK")
    public Clasificacion getCodClasificacion() {
        return codClasificacion;
    }

    public void setCodClasificacion(Clasificacion codClasificacion) {
        this.codClasificacion = codClasificacion;
    }


    @Basic
    @Column(name = "NOMBRE_MADRE_TUTOR", nullable = true, insertable = true, updatable = true, length = 100)
    public String getNombreMadreTutor() {
        return nombreMadreTutor;
    }

    public void setNombreMadreTutor(String nombreMadreTutor) {
        this.nombreMadreTutor = nombreMadreTutor;
    }


    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Catalogo.class)
    @JoinColumn(name = "COD_PROCEDENCIA", referencedColumnName = "CODIGO", nullable = true)
    @ForeignKey(name = "COD_PROC_FK")
    public Procedencia getCodProcedencia() {
        return codProcedencia;
    }

    public void setCodProcedencia(Procedencia codProcedencia) {
        this.codProcedencia = codProcedencia;
    }

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Catalogo.class)
    @JoinColumn(name = "COD_CAPTACION", referencedColumnName = "CODIGO")
    @ForeignKey(name = "COD_CAPT_FK")
    public Captacion getCodCaptacion() {
        return codCaptacion;
    }

    public void setCodCaptacion(Captacion codCaptacion) {
        this.codCaptacion = codCaptacion;
    }


    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Cie10.class)
    @JoinColumn(name = "DIAGNOSTICO", referencedColumnName = "CODIGO_CIE10")
    @ForeignKey(name = "COD_CIE10_FK")
    public Cie10 getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(Cie10 diagnostico) {
        this.diagnostico = diagnostico;
    }

    @Basic
    @Column(name = "TARJETA_VACUNA", nullable = true, insertable = true, updatable = true, length = 4)
    public Integer getTarjetaVacuna() {
        return tarjetaVacuna;
    }

    public void setTarjetaVacuna(Integer tarjetaVacuna) {
        this.tarjetaVacuna = tarjetaVacuna;
    }


    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Catalogo.class, optional = true)
    @JoinColumn(name = "COD_ANTB_ULSEM", referencedColumnName = "CODIGO", nullable = true)
    @ForeignKey(name = "COD_ANTB_ULSEM_FK")
    public Respuesta getCodAntbUlSem() {
        return codAntbUlSem;
    }

    public void setCodAntbUlSem(Respuesta codAntbUlSem) {
        this.codAntbUlSem = codAntbUlSem;
    }

    @Basic
    @Column(name = "CANT_ANTIBIOTICOS", nullable = true, insertable = true, updatable = true, precision = 0)
    public Integer getCantidadAntib() {
        return cantidadAntib;
    }

    public void setCantidadAntib(Integer cantidadAntib) {
        this.cantidadAntib = cantidadAntib;
    }


    @Basic
    @Column(name = "NOMBRE_ANTIBIOTICO", nullable = true, insertable = true, updatable = true, length = 100)
    public String getNombreAntibiotico() {
        return nombreAntibiotico;
    }

    public void setNombreAntibiotico(String nombreAntibiotico) {
        this.nombreAntibiotico = nombreAntibiotico;
    }

    @Basic
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column(name = "FECHA_PRIM_DOSIS_ANTIB", nullable = true, insertable = true, updatable = true)
    public Date getFechaPrimDosisAntib() {
        return fechaPrimDosisAntib;
    }

    public void setFechaPrimDosisAntib(Date fechaPrimDosisAntib) {
        this.fechaPrimDosisAntib = fechaPrimDosisAntib;
    }

    @Basic
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column(name = "FECHA_ULT_DOSIS_ANTIB", nullable = true, insertable = true, updatable = true)
    public Date getFechaUltDosisAntib() {
        return fechaUltDosisAntib;
    }

    public void setFechaUltDosisAntib(Date fechaUltDosisAntib) {
        this.fechaUltDosisAntib = fechaUltDosisAntib;
    }

    @Basic
    @Column(name = "NO_DOSIS_ANTIB", nullable = true, insertable = true, updatable = true, precision = 0)
    public Integer getNoDosisAntib() {
        return noDosisAntib;
    }

    public void setNoDosisAntib(Integer noDosisAntib) {
        this.noDosisAntib = noDosisAntib;
    }


    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Catalogo.class, optional = true)
    @JoinColumn(name = "COD_VIA_ANTB", referencedColumnName = "CODIGO", nullable = true)
    @ForeignKey(name = "COD_VIA_ANTB_FK")
    public ViaAntibiotico getCodViaAntb() {
        return codViaAntb;
    }

    public void setCodViaAntb(ViaAntibiotico codViaAntb) {
        this.codViaAntb = codViaAntb;
    }


    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Catalogo.class, optional = true)
    @JoinColumn(name = "USO_ANTIVIRALES", referencedColumnName = "CODIGO", nullable = true)
    @ForeignKey(name = "USO_ANTIV_FK")
    public Respuesta getUsoAntivirales() {
        return usoAntivirales;
    }

    public void setUsoAntivirales(Respuesta usoAntivirales) {
        this.usoAntivirales = usoAntivirales;
    }

    @Basic
    @Column(name = "NOMBRE_ANTIVIRAL", nullable = true, insertable = true, updatable = true, length = 100)
    public String getNombreAntiviral() {
        return nombreAntiviral;
    }

    public void setNombreAntiviral(String nombreAntiviral) {
        this.nombreAntiviral = nombreAntiviral;
    }

    @Basic
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column(name = "FECHA_PRIM_DOSIS_ANTIV", nullable = true, insertable = true, updatable = true)
    public Date getFechaPrimDosisAntiviral() {
        return fechaPrimDosisAntiviral;
    }

    public void setFechaPrimDosisAntiviral(Date fechaPrimDosisAntiviral) {
        this.fechaPrimDosisAntiviral = fechaPrimDosisAntiviral;
    }

    @Basic
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column(name = "FECHA_ULT_DOSIS_ANTIV", nullable = true, insertable = true, updatable = true)
    public Date getFechaUltDosisAntiviral() {
        return fechaUltDosisAntiviral;
    }

    public void setFechaUltDosisAntiviral(Date fechaUltDosisAntiviral) {
        this.fechaUltDosisAntiviral = fechaUltDosisAntiviral;
    }

    @Basic
    @Column(name = "NO_DOSIS_ANTIV", nullable = true, insertable = true, updatable = true, precision = 0)
    public Integer getNoDosisAntiviral() {
        return noDosisAntiviral;
    }

    public void setNoDosisAntiviral(Integer noDosisAntiviral) {
        this.noDosisAntiviral = noDosisAntiviral;
    }

    @Basic
    @Column(name = "COD_RES_RADIOLOGIA", nullable = true, insertable = true, updatable = true, length = 100)
    public String getCodResRadiologia() {
        return codResRadiologia;
    }

    public void setCodResRadiologia(String codResRadiologia) {
        this.codResRadiologia = codResRadiologia;
    }


    @Basic
    @Column(name = "OTRO_RES_RADIOLOGIA", nullable = true, insertable = true, updatable = true, length = 50)
    public String getOtroResultadoRadiologia() {
        return otroResultadoRadiologia;
    }

    public void setOtroResultadoRadiologia(String otroResultadoRadiologia) {
        this.otroResultadoRadiologia = otroResultadoRadiologia;
    }

    @Basic
    @Column(name = "UCI", nullable = true, insertable = true, updatable = true, length = 4)
    public Integer getUci() {
        return uci;
    }

    public void setUci(Integer uci) {
        this.uci = uci;
    }

    @Basic
    @Column(name = "NO_DIAS_HOSP", nullable = true, insertable = true, updatable = true, precision = 0)
    public Integer getNoDiasHospitalizado() {
        return noDiasHospitalizado;
    }

    public void setNoDiasHospitalizado(Integer noDiasHospitalizado) {
        this.noDiasHospitalizado = noDiasHospitalizado;
    }


    @Basic
    @Column(name = "VENTILACION_ASISTIDA", nullable = true, insertable = true, updatable = true, length = 4)
    public Integer getVentilacionAsistida() {
        return ventilacionAsistida;
    }

    public void setVentilacionAsistida(Integer ventilacionAsistida) {
        this.ventilacionAsistida = ventilacionAsistida;
    }

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Cie10.class)
    @JoinColumn(name = "DIAG1_EGRESO", referencedColumnName = "CODIGO_CIE10")
    @ForeignKey(name = "COD_CIE10_FK")
    public Cie10 getDiagnostico1Egreso() {
        return diagnostico1Egreso;
    }

    public void setDiagnostico1Egreso(Cie10 diagnostico1Egreso) {
        this.diagnostico1Egreso = diagnostico1Egreso;
    }

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Cie10.class)
    @JoinColumn(name = "DIAG2_EGRESO", referencedColumnName = "CODIGO_CIE10")
    @ForeignKey(name = "COD_CIE10_FK")
    public Cie10 getDiagnostico2Egreso() {
        return diagnostico2Egreso;
    }

    public void setDiagnostico2Egreso(Cie10 diagnostico2Egreso) {
        this.diagnostico2Egreso = diagnostico2Egreso;
    }

    @Basic
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column(name = "FECHA_EGRESO", nullable = true, insertable = true, updatable = true)
    public Date getFechaEgreso() {
        return fechaEgreso;
    }

    public void setFechaEgreso(Date fechaEgreso) {
        this.fechaEgreso = fechaEgreso;
    }

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Catalogo.class, optional = true)
    @JoinColumn(name = "COD_COND_EGRESO", referencedColumnName = "CODIGO", nullable = true)
    @ForeignKey(name = "COD_CONDEGRESO_FK")
    public CondicionEgreso getCodCondEgreso() {
        return codCondEgreso;
    }

    public void setCodCondEgreso(CondicionEgreso codCondEgreso) {
        this.codCondEgreso = codCondEgreso;
    }

    @Basic
    @Column(name = "COD_CLASFINAL", nullable = true, insertable = true, updatable = true, length = 100)
    public String getCodClasFCaso() {
        return codClasFCaso;
    }

    public void setCodClasFCaso(String codClasFCaso) {
        this.codClasFCaso = codClasFCaso;
    }


    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Catalogo.class, optional = true)
    @JoinColumn(name = "COD_CLASF_NB", referencedColumnName = "CODIGO", nullable = true)
    @ForeignKey(name = "COD_CLASFNB_FK")
    public ClasificacionFinalNB getCodClasFDetalleNB() {
        return codClasFDetalleNB;
    }

    public void setCodClasFDetalleNB(ClasificacionFinalNB codClasFDetalleNB) {
        this.codClasFDetalleNB = codClasFDetalleNB;
    }


    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Catalogo.class, optional = true)
    @JoinColumn(name = "COD_CLASF_NV", referencedColumnName = "CODIGO", nullable = true)
    @ForeignKey(name = "COD_CLASFNV_FK")

    public ClasificacionFinalNV getCodClasFDetalleNV() {
        return codClasFDetalleNV;
    }

    public void setCodClasFDetalleNV(ClasificacionFinalNV codClasFDetalleNV) {
        this.codClasFDetalleNV = codClasFDetalleNV;
    }


    @Basic
    @Column(name = "AGENTE_BACTERIANO", nullable = true, insertable = true, updatable = true, length = 50)
    public String getAgenteBacteriano() {
        return agenteBacteriano;
    }

    public void setAgenteBacteriano(String agenteBacteriano) {
        this.agenteBacteriano = agenteBacteriano;
    }

    @Basic
    @Column(name = "SEROTIPIFICACION", nullable = true, insertable = true, updatable = true, length = 50)
    public String getSerotipificacion() {
        return serotipificacion;
    }

    public void setSerotipificacion(String serotipificacion) {
        this.serotipificacion = serotipificacion;
    }

    @Basic
    @Column(name = "AGENTE_VIRAL", nullable = true, insertable = true, updatable = true, length = 50)
    public String getAgenteViral() {
        return agenteViral;
    }

    public void setAgenteViral(String agenteViral) {
        this.agenteViral = agenteViral;
    }

    @Basic
    @Column(name = "AGENTE_ETIOLOGICO", nullable = true, insertable = true, updatable = true, length = 50)
    public String getAgenteEtiologico() {
        return agenteEtiologico;
    }

    public void setAgenteEtiologico(String agenteEtiologico) {
        this.agenteEtiologico = agenteEtiologico;
    }

    @Basic
    @Column(name = "FECHA_REGISTRO", nullable = false, insertable = true, updatable = true)
    public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "USUARIO_ID", referencedColumnName = "USUARIO_ID")
    @ForeignKey(name = "USUARIO_FICHAI_FK")
    public Usuarios getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuarios usuario) {
        this.usuario = usuario;
    }

    @Basic
    @Column(name = "MANIFESTACIONES", nullable = true, insertable = true, updatable = true, length = 100)
    public String getManifestaciones() {
        return manifestaciones;
    }

    public void setManifestaciones(String manifestaciones) {
        this.manifestaciones = manifestaciones;
    }

    @Basic
    @Column(name = "OTRA_MANIFESTACION", nullable = true, insertable = true, updatable = true, length = 50)
    public String getOtraManifestacion() {
        return otraManifestacion;
    }

    public void setOtraManifestacion(String otraManifestacion) {
        this.otraManifestacion = otraManifestacion;
    }


    @Basic
    @Column(name = "CONDICIONES", nullable = true, insertable = true, updatable = true, length = 100)
    public String getCondiciones() {
        return condiciones;
    }

    public void setCondiciones(String condiciones) {
        this.condiciones = condiciones;
    }

    @Basic
    @Column(name = "SEMANAS_EMBARAZO", nullable = true, insertable = true, updatable = true, length = 2)
    public Integer getSemanasEmbarazo() {
        return semanasEmbarazo;
    }

    public void setSemanasEmbarazo(Integer semanasEmbarazo) {
        this.semanasEmbarazo = semanasEmbarazo;
    }

    @Basic
    @Column(name = "OTRA_CONDICION", nullable = true, insertable = true, updatable = true, length = 100)
    public String getOtraCondicion() {
        return otraCondicion;
    }

    public void setOtraCondicion(String otraCondicion) {
        this.otraCondicion = otraCondicion;
    }

    @Override
    public boolean isFieldAuditable(String fieldname) {
        if (fieldname.matches("idNotificacion") || fieldname.matches("usuario") || fieldname.matches("fechaRegistro")) return false;
        else
            return true;
    }

    @Override
    public String toString() {
        return "{" +
                "idDaIrag=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DaIrag)) return false;

        DaIrag daIrag = (DaIrag) o;

        if (id != null ? !id.equals(daIrag.id) : daIrag.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
