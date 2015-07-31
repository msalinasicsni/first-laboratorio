package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;
import ni.gob.minsa.laboratorio.domain.estructura.Procedencia;
import ni.gob.minsa.laboratorio.domain.irag.*;
import ni.gob.minsa.laboratorio.domain.muestra.*;
import ni.gob.minsa.laboratorio.domain.notificacion.TipoNotificacion;
import ni.gob.minsa.laboratorio.domain.persona.*;
import ni.gob.minsa.laboratorio.domain.concepto.TipoDatoCatalogo;
import ni.gob.minsa.laboratorio.domain.vih.*;
import ni.gob.minsa.laboratorio.domain.vigilanciaSindFebril.Animales;
import ni.gob.minsa.laboratorio.domain.vigilanciaSindFebril.EnfAgudas;
import ni.gob.minsa.laboratorio.domain.vigilanciaSindFebril.EnfCronicas;
import ni.gob.minsa.laboratorio.domain.vigilanciaSindFebril.FuenteAgua;
import ni.gob.minsa.laboratorio.domain.vigilanciaSindFebril.SintomasCHIK;
import ni.gob.minsa.laboratorio.domain.vigilanciaSindFebril.SintomasDCSA;
import ni.gob.minsa.laboratorio.domain.vigilanciaSindFebril.SintomasDGRA;
import ni.gob.minsa.laboratorio.domain.vigilanciaSindFebril.SintomasDSSA;
import ni.gob.minsa.laboratorio.domain.vigilanciaSindFebril.SintomasHANT;
import ni.gob.minsa.laboratorio.domain.vigilanciaSindFebril.SintomasLEPT;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Servicio para el objeto de Catalogos
 *
 * @author Miguel Salinas
 */

@Service("catalogosService")
@Transactional
public class CatalogoService {

    private Logger logger = LoggerFactory.getLogger(CatalogoService.class);

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;

    public CatalogoService() {

    }

    public SessionFactory getSessionFactory(){
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory){
        if(this.sessionFactory == null){
            this.sessionFactory = sessionFactory;
        }
    }

    public List<Catalogo> ElementosCatalogos(String discriminador) throws Exception {
        String query = "from Catalogo";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        return q.list();
    }
    
    public Catalogo getElementoByCodigo(String codigo) throws Exception {
        String query = "from Catalogo as a where pasivo = false and codigo= :codigo order by orden";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("codigo", codigo);
        return  (Catalogo)q.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<Procedencia> getProcedencia() {
        // Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        // Create a Hibernate query (HQL)
        Query query = session.createQuery("FROM Procedencia where pasivo = :pasivo order by orden");
        query.setParameter("pasivo", false);
        // Retrieve all
        return  query.list();
    }

    @SuppressWarnings("unchecked")
    public List<Captacion> getCaptacion(){
        //Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM Captacion capta where capta.pasivo = false order by orden");
       //retrieve all
        return query.list();
    }

    @SuppressWarnings("unchecked")
    public List<Clasificacion> getClasificacion(){
        //Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM Clasificacion clas where clas.pasivo = false order by orden");
        //retrieve all
        return query.list();
    }

    @SuppressWarnings("unchecked")
    public List<Respuesta> getRespuesta(){
        //Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM Respuesta res where res.pasivo = false  order by orden");
        //retrieve all
        return query.list();
    }

    @SuppressWarnings("unchecked")
    public List<ViaAntibiotico> getViaAntibiotico(){
        //Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM ViaAntibiotico via where via.pasivo = false order by orden");
        //retrieve all
        return query.list();
    }

    @SuppressWarnings("unchecked")
    public List<ResultadoRadiologia> getResultadoRadiologia(){
        //Retrieve session Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM ResultadoRadiologia resRad where resRad.pasivo = false order by orden");
        //retrieve all
        return query.list();

    }

    @SuppressWarnings("unchecked")
    public List<CondicionEgreso> getCondicionEgreso(){
        //Retrieve session Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM CondicionEgreso cond where cond.pasivo = false order by orden");
        //retrieve all
        return query.list();
    }

    @SuppressWarnings("unchecked")
    public List<ClasificacionFinal> getClasificacionFinal(){
        //Retrieve session Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM ClasificacionFinal clas where clas.pasivo = false order by orden");
        //retrieve all
        return query.list();

    }

    @SuppressWarnings("unchecked")
    public List<ClasificacionFinalNV> getClasificacionFinalNV(){
        //Retrieve session Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM ClasificacionFinalNV cla where cla.pasivo = false order by orden");
        //retrieve all
        return query.list();

    }

    @SuppressWarnings("unchecked")
    public List<ClasificacionFinalNB> getClasificacionFinalNB(){
        //Retrieve session Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM ClasificacionFinalNB clasi where clasi.pasivo = false order by orden");
        //retrieve all
        return query.list();

    }

    @SuppressWarnings("unchecked")
    public List<Vacuna> getVacuna(){
        //Retrieve session Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM Vacuna vac where vac.pasivo = false order by orden");
        //retrieve all
        return query.list();

    }

    @SuppressWarnings("unchecked")
    public List<TipoVacuna> getTipoVacuna(){
        //Retrieve session Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM TipoVacuna tvac where tvac.pasivo = false order by orden");
        //retrieve all
        return query.list();

    }

    public List<TipoVacuna> getTipoVacunaHib(){
        //Retrieve session Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM TipoVacuna tvac where tvac.pasivo = false and tvac.codigo like :codigo order by orden");
        query.setString("codigo","TVAC|HIB1");
        //retrieve all
        return query.list();
    }

    public List<TipoVacuna> getTipoVacunaMeningococica(){
        //Retrieve session Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM TipoVacuna tvac where tvac.pasivo = false and tvac.codigo like :codigo order by orden");
        query.setString("codigo","%"+"TVAC|MENING"+"%");
        //retrieve all
        return query.list();
    }

    public List<TipoVacuna> getTipoVacunaNeumococica(){
        //Retrieve session Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM TipoVacuna tvac where tvac.pasivo = false and tvac.codigo like :codigo order by orden");
        query.setString("codigo","%"+"TVAC|NEUMO"+"%");
        //retrieve all
        return query.list();
    }

    public List<TipoVacuna> getTipoVacunaFlu(){
        //Retrieve session Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM TipoVacuna tvac where tvac.pasivo = false and tvac.codigo like :codigo order by orden");
        query.setString("codigo","%"+"TVAC|FLU"+"%");
        //retrieve all
        return query.list();
    }

    @SuppressWarnings("unchecked")
    public List<CondicionPrevia> getCondicionPrevia(){
        //Retrieve session Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM CondicionPrevia cond where cond.pasivo = false order by orden");
        //retrieve all
        return query.list();

    }

    @SuppressWarnings("unchecked")
    public List<ManifestacionClinica> getManifestacionClinica(){
        //Retrieve session Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM ManifestacionClinica mani where mani.pasivo = false order by orden");
        //retrieve all
        return query.list();

    }

    @SuppressWarnings("unchecked")
    public List<TipoMx> getTipoMuestra(){
        //Retrieve session Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("select distinct tpmx.tipoMx FROM TipoMx_TipoNotificacion tpmx where tpmx.pasivo = false order by tpmx.tipoMx.nombre");
        //retrieve all
        return query.list();

    }

    @SuppressWarnings("unchecked")
    public List<TipoMx> getEstadoMx(){
        //Retrieve session Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM EstadoMx est where est.pasivo = false order by orden");
        //retrieve all
        return query.list();

    }

    @SuppressWarnings("unchecked")
    public List<TipoNotificacion> getTipoNotificacion(){
        //Retrieve session Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM TipoNotificacion noti where noti.pasivo = false order by orden");
        //retrieve all
        return query.list();

    }

    @SuppressWarnings("unchecked")
    public List<EstadoOrdenEx> getEstadoOrdenEx(){
        //Retrieve session Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM EstadoOrdenEx est where est.pasivo = false order by orden");
        //retrieve all
        return query.list();

    }

    @SuppressWarnings("unchecked")
    public List<TipoDatoCatalogo> getTipoDatoCatalogo(){
        //Retrieve session Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM TipoDatoCatalogo cat where cat.pasivo = false order by orden");
        //Retrieve all
        return query.list();
    }

    public Procedencia getProcedencia(String procedencia) {
        // Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        // Create a Hibernate query (HQL)
        Query query = session.getNamedQuery("obtenerProcedenciaPorCodigo").setString("pCodigo", procedencia);
        // Retrieve all
        return  (Procedencia) query.uniqueResult();
    }

    public Captacion getCaptacion(String captacion){
        //Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a Hibernate query (HQL)
        Query query = session.getNamedQuery("getCaptacionByCodigo").setString("pCodigo", captacion);
        //Retrieve all
        return (Captacion) query.uniqueResult();
    }

    public Clasificacion getClasificacion(String clasificacion){
        //Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a Hibernate query (HQL)
        Query query = session.getNamedQuery("getClasificacionByCodigo").setString("pCodigo", clasificacion);
        //Retrieve all
        return (Clasificacion) query.uniqueResult();
    }

    public Respuesta getRespuesta(String respuesta){
        //Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a Hibernate query (HQL)
        Query query = session.getNamedQuery("getRespuestaByCodigo").setString("pCodigo", respuesta);
        //Retrieve all
        return (Respuesta) query.uniqueResult();
    }

    public ViaAntibiotico getViaAntibiotico(String via){
        //Retrieve session from hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a Hibernate query (HQL)
        Query query = session.getNamedQuery("getViaAntibioticoByCodigo").setString("pCodigo", via);
        //Retrieve all
        return (ViaAntibiotico) query.uniqueResult();
    }

    public ResultadoRadiologia getResultadoRadiologia (String res){
        //Retrieve session from hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.getNamedQuery("getResRadiologiaByCodigo").setString("pCodigo", res);
        //Retrieve all
        return (ResultadoRadiologia) query.uniqueResult();
    }

    public CondicionEgreso getCondicionEgreso (String cond){
        //Retrieve session from hibernated
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.getNamedQuery("getCondicionEgresoByCodigo").setString("pCodigo", cond);
        //Retrieve all
        return (CondicionEgreso) query.uniqueResult();
    }

    public ClasificacionFinal getClasificacionFinal (String clas){
        //Retrieve session from hibernated
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.getNamedQuery("getClasificacionFinalByCodigo").setString("pCodigo", clas);
        //Retrieve all
        return (ClasificacionFinal) query.uniqueResult();
    }

    public ClasificacionFinalNV getClasificacionFinalNV (String cla){
        //Retrieve session from hibernated
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.getNamedQuery("getClasificacionFinalNVByCodigo").setString("pCodigo", cla);
        //Retrieve all
        return (ClasificacionFinalNV) query.uniqueResult();
    }

    public ClasificacionFinalNB getClasificacionFinalNB (String clasi){
        //Retrieve session from hibernated
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.getNamedQuery("getClasificacionFinalNBByCodigo").setString("pCodigo", clasi);
        //Retrieve all
        return (ClasificacionFinalNB) query.uniqueResult();
    }

    public Vacuna getVacuna (String vac){
        //Retrieve session from hibernated
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.getNamedQuery("getVacunaByCodigo").setString("pCodigo", vac);
        //Retrieve all
        return (Vacuna) query.uniqueResult();
    }

    public TipoVacuna getTipoVacuna (String tvac){
        //Retrieve session from hibernated
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.getNamedQuery("getTipoVacunaByCodigo").setString("pCodigo", tvac);
        //Retrieve all
        return (TipoVacuna) query.uniqueResult();
    }

    public CondicionPrevia getCondicionPrevia (String cond){
        //Retrieve session from hibernated
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.getNamedQuery("getCondicionPreviaByCodigo").setString("pCodigo", cond);
        //Retrieve all
        return (CondicionPrevia) query.uniqueResult();
    }


    public ManifestacionClinica getManifestacionClinica(String mani){
        //Retrieve session from hibernated
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.getNamedQuery("getManifestacionClinicaByCodigo").setString("pCodigo", mani);
        //Retrieve all
        return (ManifestacionClinica) query.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<TipoAsegurado> getTiposAsegurados(){
        //Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM TipoAsegurado tipoas where tipoas.pasivo = false order by orden");
        //retrieve all
        return query.list();
    }

    public TipoAsegurado getTipoAsegurado(String tipoas){
        //Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a Hibernate query (HQL)
        Query query = session.getNamedQuery("obtenerTipoAseguradoPorCodigo").setString("pCodigo", tipoas);
        //Retrieve all
        return (TipoAsegurado) query.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<TipoAseguradovih> getListaTiposAseguradosVih(){
        //Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM TipoAseguradovih tipoas where tipoas.pasivo = false order by orden");
        //retrieve all
        return query.list();
    }

    public TipoAseguradovih getTipoAseguradoVih(String tipoas){
        //Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a Hibernate query (HQL)
        Query query = session.getNamedQuery("getTipoAseguradoByCodigo").setString("pCodigo", tipoas);
        //Retrieve all
        return (TipoAseguradovih) query.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<TipoEdadVih> getTiposEdad(){
        //Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM TipoEdadVih tedad where tedad.pasivo = false order by orden");
        //retrieve all
        return query.list();
    }

    public TipoEdadVih getTipoEdad(String tipoedad){
        //Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a Hibernate query (HQL)
        Query query = session.getNamedQuery("getTipoEdadVihByCodigo").setString("pCodigo", tipoedad);
        //Retrieve all
        return (TipoEdadVih) query.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<Sexo> getListaSexo(){
        //Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM Sexo catsexo where catsexo.pasivo = false order by orden");
        //retrieve all
        return query.list();
    }

    public Sexo getSexo(String sex){
        //Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a Hibernate query (HQL)
        Query query = session.getNamedQuery("obtenerSexoPorCodigo").setString("pCodigo", sex);
        //Retrieve all
        return (Sexo) query.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<Etnia> getListaEtnia(){
        //Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM Etnia catetnia where catetnia.pasivo = false order by orden");
        //retrieve all
        return query.list();
    }

    public Etnia getEtnia(String etnia){
        //Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a Hibernate query (HQL)
        Query query = session.getNamedQuery("obtenerEtniaPorCodigo").setString("pCodigo", etnia);
        //Retrieve all
        return (Etnia) query.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<EstadoCivil> getListaEstadoCivil(){
        //Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM EstadoCivil catestadocivil where catestadocivil.pasivo = false order by orden");
        //retrieve all
        return query.list();
    }

    public EstadoCivil getEstadoCivil(String estado){
        //Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a Hibernate query (HQL)
        Query query = session.getNamedQuery("obtenerEstadoCivilPorCodigo").setString("pCodigo", estado);
        //Retrieve all
        return (EstadoCivil) query.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<Escolaridad> getListaEscolaridad(){
        //Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM Escolaridad esc where esc.pasivo = false order by orden");
        //retrieve all
        return query.list();
    }

    public Escolaridad getEscolaridad(String esc){
        //Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a Hibernate query (HQL)
        Query query = session.getNamedQuery("obtenerEscolaridadPorCodigo").setString("pCodigo", esc);
        //Retrieve all
        return (Escolaridad) query.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<Ocupacion> getListaOcupacion(){
        //Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM Ocupacion ocup where ocup.pasivo = false order by nombre");
        //retrieve all
        return query.list();
    }

    public Ocupacion getOcupacion(String ocup){
        //Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a Hibernate query (HQL)
        Query query = session.getNamedQuery("obtenerOcupacionPorCodigo").setString("pCodigo", ocup);
        //Retrieve all
        return (Ocupacion) query.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<MetodosCalculoSemanasEmbarazo> getListaMetodosCalcSeEmb(){
        //Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM MetodosCalculoSemanasEmbarazo metodos where metodos.pasivo = false order by orden");
        //retrieve all
        return query.list();
    }

    public MetodosCalculoSemanasEmbarazo getMetodoCalcSeEmb(String metodo){
        //Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a Hibernate query (HQL)
        Query query = session.getNamedQuery("obtenerMetodCalcSeEmbPorCodigo").setString("pCodigo", metodo);
        //Retrieve all
        return (MetodosCalculoSemanasEmbarazo) query.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<PeriodoPruebaVihEmb> getListaPeriodoPruebaVihEmb(){
        //Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM PeriodoPruebaVihEmb periodo where periodo.pasivo = false order by orden");
        //retrieve all
        return query.list();
    }

    public PeriodoPruebaVihEmb getPeriodoPruebaVihEmb(String periodo){
        //Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a Hibernate query (HQL)
        Query query = session.getNamedQuery("obtenerPeriodoPruebaVihEmbPorCodigo").setString("pCodigo", periodo);
        //Retrieve all
        return (PeriodoPruebaVihEmb) query.uniqueResult();
    }
    
    @SuppressWarnings("unchecked")
    public List<Animales> getAnimales(){
        //Retrieve session Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM Animales where pasivo = false order by orden");
        //retrieve all
        return query.list();
    }
    
    public Animales getAnimal(String codigo){
        //Retrieve session from hibernated
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.getNamedQuery("getAnimalesByCodigo").setString("pCodigo", codigo);
        //Retrieve all
        return (Animales) query.uniqueResult();
    }
    
    @SuppressWarnings("unchecked")
    public List<EnfAgudas> getEnfAgudas(){
        //Retrieve session Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM EnfAgudas where pasivo = false order by orden");
        //retrieve all
        return query.list();
    }
    
    public EnfAgudas getEnfAgudas(String codigo){
        //Retrieve session from hibernated
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.getNamedQuery("getEnfAgudasByCodigo").setString("pCodigo", codigo);
        //Retrieve all
        return (EnfAgudas) query.uniqueResult();
    }
    
    @SuppressWarnings("unchecked")
    public List<EnfCronicas> getEnfCronicas(){
        //Retrieve session Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM EnfCronicas where pasivo = false order by orden");
        //retrieve all
        return query.list();
    }
    
    public EnfCronicas getEnfCronicas(String codigo){
        //Retrieve session from hibernated
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.getNamedQuery("getEnfCronicasByCodigo").setString("pCodigo", codigo);
        //Retrieve all
        return (EnfCronicas) query.uniqueResult();
    }
    
    @SuppressWarnings("unchecked")
    public List<FuenteAgua> getFuenteAgua(){
        //Retrieve session Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM FuenteAgua where pasivo = false order by orden");
        //retrieve all
        return query.list();
    }
    
    public FuenteAgua getFuenteAgua(String codigo){
        //Retrieve session from hibernated
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.getNamedQuery("getFuenteAguaByCodigo").setString("pCodigo", codigo);
        //Retrieve all
        return (FuenteAgua) query.uniqueResult();
    }
    
    @SuppressWarnings("unchecked")
    public List<SintomasCHIK> getSintomasCHIK(){
        //Retrieve session Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM SintomasCHIK where pasivo = false order by orden");
        //retrieve all
        return query.list();
    }
    
    public SintomasCHIK getSintomasCHIK(String codigo){
        //Retrieve session from hibernated
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.getNamedQuery("getSintomasCHIKByCodigo").setString("pCodigo", codigo);
        //Retrieve all
        return (SintomasCHIK) query.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<SintomasDCSA> getSintomasDCSA(){
        //Retrieve session Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM SintomasDCSA where pasivo = false order by orden");
        //retrieve all
        return query.list();
    }
    
    public SintomasDCSA getSintomasDCSA(String codigo){
        //Retrieve session from hibernated
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.getNamedQuery("getSintomasDCSAByCodigo").setString("pCodigo", codigo);
        //Retrieve all
        return (SintomasDCSA) query.uniqueResult();
    }
    
    @SuppressWarnings("unchecked")
    public List<SintomasDGRA> getSintomasDGRA(){
        //Retrieve session Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM SintomasDGRA where pasivo = false order by orden");
        //retrieve all
        return query.list();
    }
    
    public SintomasDGRA getSintomasDGRA(String codigo){
        //Retrieve session from hibernated
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.getNamedQuery("getSintomasDGRAByCodigo").setString("pCodigo", codigo);
        //Retrieve all
        return (SintomasDGRA) query.uniqueResult();
    }
    
    @SuppressWarnings("unchecked")
    public List<SintomasDSSA> getSintomasDSSA(){
        //Retrieve session Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM SintomasDSSA where pasivo = false order by orden");
        //retrieve all
        return query.list();
    }
    
    public SintomasDSSA getSintomasDSSA(String codigo){
        //Retrieve session from hibernated
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.getNamedQuery("getSintomasDSSAByCodigo").setString("pCodigo", codigo);
        //Retrieve all
        return (SintomasDSSA) query.uniqueResult();
    }
    
    
    @SuppressWarnings("unchecked")
    public List<SintomasHANT> getSintomasHANT(){
        //Retrieve session Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM SintomasHANT where pasivo = false order by orden");
        //retrieve all
        return query.list();
    }
    
    public SintomasHANT getSintomasHANT(String codigo){
        //Retrieve session from hibernated
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.getNamedQuery("getSintomasHANTByCodigo").setString("pCodigo", codigo);
        //Retrieve all
        return (SintomasHANT) query.uniqueResult();
    }
    
    @SuppressWarnings("unchecked")
    public List<SintomasLEPT> getSintomasLEPT(){
        //Retrieve session Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM SintomasLEPT where pasivo = false order by orden");
        //retrieve all
        return query.list();
    }
    
    public SintomasLEPT getSintomasLEPT(String codigo){
        //Retrieve session from hibernated
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.getNamedQuery("getSintomasLEPTByCodigo").setString("pCodigo", codigo);
        //Retrieve all
        return (SintomasLEPT) query.uniqueResult();
    }

    public List<Identificacion> getListaTipoIdentificacion(){
        //Retrieve session Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM Identificacion where pasivo = false order by orden");
        //retrieve all
        return query.list();
    }

    public EstadoMx getEstadoMx (String tmx){
        //Retrieve session from hibernated
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.getNamedQuery("getEstadoMxByCodigo").setString("pCodigo", tmx);
        //Retrieve all
        return (EstadoMx) query.uniqueResult();
    }

    public TipoNotificacion getTipoNotificacion (String tpNoti){
        //Retrieve session from hibernated
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.getNamedQuery("getTipoNotifCodigo").setString("pCodigo", tpNoti);
        //Retrieve all
        return (TipoNotificacion) query.uniqueResult();
    }


    public EstadoOrdenEx getEstadoOrdenEx(String est){
        //Retrieve session from hibernated
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.getNamedQuery("getEstadoOrdenExByCodigo").setString("pCodigo", est);
        //Retrieve all
        return (EstadoOrdenEx) query.uniqueResult();
    }

    //LABORATORIO
    public List<TipoRecepcionMx> getTipoRecepcionesMx(){
        //Retrieve session Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM TipoRecepcionMx where pasivo = false order by orden");
        //retrieve all
        return query.list();
    }

    public TipoRecepcionMx getTipoRecepcionMx(String codigo){
        //Retrieve session from hibernated
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.getNamedQuery("getTipoRecepcionMxByCodigo").setString("pCodigo", codigo);
        //Retrieve all
        return (TipoRecepcionMx) query.uniqueResult();
    }

    public List<TipoEstudio> getTiposEstudios(){
        //Retrieve session Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM TipoEstudio where pasivo = false order by orden");
        //retrieve all
        return query.list();
    }

    public TipoEstudio getTipoEstudio(String codigo){
        //Retrieve session from hibernated
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.getNamedQuery("getTipoEstudioByCodigo").setString("pCodigo", codigo);
        //Retrieve all
        return (TipoEstudio) query.uniqueResult();
    }

    public List<TipoTecnica> getTipoTecnicas(){
        //Retrieve session Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM TipoTecnica where pasivo = false order by orden");
        //retrieve all
        return query.list();
    }

    public TipoTecnica getTipoTecnica(String codigo){
        //Retrieve session from hibernated
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.getNamedQuery("getTipoTecnicaByCodigo").setString("pCodigo", codigo);
        //Retrieve all
        return (TipoTecnica) query.uniqueResult();
    }

    public List<TipoTubo> getTipoTubos(){
        //Retrieve session Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM TipoTubo where pasivo = false order by orden");
        //retrieve all
        return query.list();
    }

    public TipoTubo getTipoTubo(String codigo){
        //Retrieve session from hibernated
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.getNamedQuery("getTipoTuboByCodigo").setString("pCodigo", codigo);
        //Retrieve all
        return (TipoTubo) query.uniqueResult();
    }

    public List<CalidadMx> getCalidadesMx(){
        //Retrieve session Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM CalidadMx where pasivo = false order by orden");
        //retrieve all
        return query.list();
    }

    public CalidadMx getCalidadMx(String codigo){
        //Retrieve session from hibernated
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.getNamedQuery("getCalidadMxByCodigo").setString("pCodigo", codigo);
        //Retrieve all
        return (CalidadMx) query.uniqueResult();
    }


    public TipoDatoCatalogo getTipoDatoCatalogo(String codigo){
        //Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.getNamedQuery("getTipoDatoCatalogo").setString("pCodigo", codigo);
        //Retrieve all
        return  (TipoDatoCatalogo) query.uniqueResult();
    }

    public List<CondicionMx> getCondicionesMx(){
        //Retrieve session Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM CondicionMx where pasivo = false order by orden");
        //retrieve all
        return query.list();
    }

    public CondicionMx getCondicionMx(String codigo){
        //Retrieve session from hibernated
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.getNamedQuery("getCondicionMxByCodigo").setString("pCodigo", codigo);
        //Retrieve all
        return (CondicionMx) query.uniqueResult();
    }

    public CausaRechazoMx getCausaRechazoMx(String codigo){
        //Retrieve session from hibernated
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.getNamedQuery("getCausaRechazoMxByCodigo").setString("pCodigo", codigo);
        //Retrieve all
        return (CausaRechazoMx) query.uniqueResult();
    }

    public List<CausaRechazoMx> getCausaRechazoMxRecepGeneral(){
        //Retrieve session from hibernated
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.getNamedQuery("getCausaRechazoMxRecepGeneral");
        //Retrieve all
        return query.list();
    }

    public List<CausaRechazoMx> getCausaRechazoMxRecepLab(){
        //Retrieve session from hibernated
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.getNamedQuery("getCausaRechazoMxRecepLab");
        //Retrieve all
        return query.list();
    }
}