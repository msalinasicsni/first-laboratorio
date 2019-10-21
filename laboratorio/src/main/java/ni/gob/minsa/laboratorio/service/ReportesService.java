package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.concepto.Catalogo_Lista;
import ni.gob.minsa.laboratorio.domain.muestra.DaSolicitudDx;
import ni.gob.minsa.laboratorio.domain.muestra.DaSolicitudEstudio;
import ni.gob.minsa.laboratorio.domain.muestra.FiltroMx;
import ni.gob.minsa.laboratorio.domain.muestra.RecepcionMx;
import ni.gob.minsa.laboratorio.domain.muestra.traslado.HistoricoEnvioMx;
import ni.gob.minsa.laboratorio.domain.muestra.traslado.TrasladoMx;
import ni.gob.minsa.laboratorio.domain.resultados.DetalleResultadoFinal;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.AutoridadLaboratorio;
import ni.gob.minsa.laboratorio.utilities.DateUtil;
import ni.gob.minsa.laboratorio.utilities.FiltrosReporte;
import ni.gob.minsa.laboratorio.utilities.enumeration.HealthUnitType;
import ni.gob.minsa.laboratorio.utilities.reportes.ResultadoVigilancia;
import ni.gob.minsa.laboratorio.utilities.reportes.TiemposProcesamiento;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.*;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by souyen-ics.
 */
@Service("reportesService")
@Transactional
public class ReportesService {

    private Logger logger = LoggerFactory.getLogger(ReportesService.class);

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;

    @Resource(name="resultadoFinalService")
    private ResultadoFinalService resultadoFinalService;

    @Resource(name = "respuestasExamenService")
    private RespuestasExamenService respuestasExamenService;

    private static final String sqlRutina = " and dx.codDx.idDiagnostico = :idDx ";
    private static final String sqlEstudio = " and dx.tipoEstudio.idEstudio = :idDx ";
    private static final String sqlFechasProcRut = " and dx.idSolicitudDx in (select r.solicitudDx.idSolicitudDx  from DetalleResultadoFinal r where r.pasivo = false and r.fechahRegistro between :fechaInicio and :fechaFin) "; //" and mx.fechaHTomaMx between :fechaInicio and :fechaFin ";
    private static final String sqlFechasAproRut =  " and dx.fechaAprobacion between :fechaInicio and :fechaFin ";
    private static final String sqlLab = " and dx.labProcesa.codigo = :codigoLab ";
    private static final String sqlFIS = " and noti.fechaInicioSintomas between :fechaInicio and :fechaFin ";

    @SuppressWarnings("unchecked")
    public List<RecepcionMx> getReceivedSamplesByFiltro(FiltroMx filtro) throws UnsupportedEncodingException {
        Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(RecepcionMx.class, "recepcion");
        crit.createAlias("recepcion.tomaMx", "toma");
        crit.createAlias("toma.idNotificacion", "notifi");


        if(filtro.getNombreSolicitud()!= null){
            filtro.setNombreSolicitud(URLDecoder.decode(filtro.getNombreSolicitud(), "utf-8"));
        }


        //se filtra por SILAIS
        if (filtro.getCodSilais()!=null){
            crit.createAlias("notifi.codSilaisAtencion","silais");
            crit.add( Restrictions.and(
                            Restrictions.eq("silais.codigo", Long.valueOf(filtro.getCodSilais())))
            );
        }
        //se filtra por unidad de salud
        if (filtro.getCodUnidadSalud()!=null){
            crit.createAlias("notifi.codUnidadAtencion","unidadS");
            crit.add( Restrictions.and(
                            Restrictions.eq("unidadS.codigo", Long.valueOf(filtro.getCodUnidadSalud())))
            );
        }
        //Se filtra por rango de fecha de recepcion
        if (filtro.getFechaInicioRecep()!=null && filtro.getFechaFinRecep()!=null){
            crit.add( Restrictions.and(
                            Restrictions.between("fechaHoraRecepcion", filtro.getFechaInicioRecep(),filtro.getFechaFinRecep()))
            );
        }
        // se filtra por tipo de muestra
        if (filtro.getCodTipoMx()!=null){
            crit.createAlias("toma.codTipoMx", "tipoMx");
            crit.add( Restrictions.and(

                            Restrictions.eq("tipoMx.idTipoMx", Integer.valueOf(filtro.getCodTipoMx())))
            );
        }

        //se filtra por tipo de solicitud
        if(filtro.getCodTipoSolicitud()!=null){
            if(filtro.getCodTipoSolicitud().equals("Estudio")){
                crit.add(Subqueries.propertyIn("toma.idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                        .add(Restrictions.eq("anulado",false))
                        .createAlias("idTomaMx", "idTomaMx")
                        .setProjection(Property.forName("idTomaMx.idTomaMx"))));
            }else{
                crit.add(Subqueries.propertyIn("toma.idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                        .add(Restrictions.eq("anulado",false))
                        .createAlias("idTomaMx", "idTomaMx")
                        .setProjection(Property.forName("idTomaMx.idTomaMx"))));
            }

        }

        //nombre solicitud
        if (filtro.getNombreSolicitud() != null) {
            if (filtro.getCodTipoSolicitud() != null) {
                if (filtro.getCodTipoSolicitud().equals("Estudio")) {
                    crit.add(Subqueries.propertyIn("toma.idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                            .add(Restrictions.eq("anulado",false))
                            .createAlias("tipoEstudio", "estudio")
                            .add(Restrictions.ilike("estudio.nombre", "%" + filtro.getNombreSolicitud() + "%"))
                            .setProjection(Property.forName("idTomaMx.idTomaMx"))));
                } else {
                    crit.add(Subqueries.propertyIn("toma.idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                            .add(Restrictions.eq("anulado",false))
                            .createAlias("codDx", "dx")
                            .add(Restrictions.ilike("dx.nombre", "%" + filtro.getNombreSolicitud() + "%"))
                            .setProjection(Property.forName("idTomaMx.idTomaMx"))));
                }
            } else {

                Junction conditGroup = Restrictions.disjunction();
                conditGroup.add(Subqueries.propertyIn("toma.idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                        .add(Restrictions.eq("anulado",false))
                        .createAlias("tipoEstudio", "estudio")
                        .add(Restrictions.ilike("estudio.nombre", "%" + filtro.getNombreSolicitud() + "%"))
                        .setProjection(Property.forName("idTomaMx.idTomaMx"))))
                        .add(Subqueries.propertyIn("toma.idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                                .add(Restrictions.eq("anulado",false))
                                .createAlias("codDx", "dx")
                                .add(Restrictions.ilike("dx.nombre", "%" + filtro.getNombreSolicitud() + "%"))
                                .setProjection(Property.forName("idTomaMx.idTomaMx"))));

                crit.add(conditGroup);
            }
        }
        //se filtra que usuario tenga autorizado laboratorio al que se envio la muestra desde ALERTA
        if (filtro.getNombreUsuario()!=null) {
            /*crit.createAlias("tomaMx.envio","envioMx");
            crit.add(Subqueries.propertyIn("envioMx.laboratorioDestino.codigo", DetachedCriteria.forClass(AutoridadLaboratorio.class)
                    .createAlias("laboratorio", "labautorizado")
                    .createAlias("user", "usuario")
                    .add(Restrictions.eq("pasivo",false)) //autoridad laboratorio activa
                    .add(Restrictions.and(Restrictions.eq("usuario.username",filtro.getNombreUsuario()))) //usuario
                    .setProjection(Property.forName("labautorizado.codigo"))));*/
            //filtro que las rutinas pertenezcan al laboratorio del usuario que consulta
            crit.createAlias("recepcion.labRecepcion","labRecep");
            crit.add(Subqueries.propertyIn("labRecep.codigo", DetachedCriteria.forClass(AutoridadLaboratorio.class)
                    .createAlias("laboratorio", "labautorizado")
                    .createAlias("user", "usuario")
                    .add(Restrictions.eq("pasivo",false)) //autoridad laboratorio activa
                    .add(Restrictions.and(Restrictions.eq("usuario.username",filtro.getNombreUsuario()))) //usuario
                    .setProjection(Property.forName("labautorizado.codigo"))));
        }

            crit.addOrder(Order.asc("recepcion.fechaHoraRecepcion"));

       return crit.list();
    }


    @SuppressWarnings("unchecked")
    public List<DaSolicitudDx> getPositiveRoutineRequestByFilter(FiltroMx filtro) throws UnsupportedEncodingException {
        Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(DaSolicitudDx.class, "rutina");
        crit.createAlias("rutina.idTomaMx", "toma");
        crit.createAlias("toma.idNotificacion", "notif");
        crit.createAlias("rutina.codDx", "dx");

        if(filtro.getNombreSolicitud()!= null){
            filtro.setNombreSolicitud(URLDecoder.decode(filtro.getNombreSolicitud(), "utf-8"));
        }
        //se filtra por SILAIS
        if (filtro.getCodSilais()!=null){
            crit.createAlias("notif.codSilaisAtencion","silais");
            crit.add( Restrictions.and(
                            Restrictions.eq("silais.codigo", Long.valueOf(filtro.getCodSilais())))
            );
        }
        //se filtra por unidad de salud
        if (filtro.getCodUnidadSalud()!=null){
            crit.createAlias("notif.codUnidadAtencion","unidadS");
            crit.add( Restrictions.and(
                            Restrictions.eq("unidadS.codigo", Long.valueOf(filtro.getCodUnidadSalud())))
            );
        }
        //Se filtra por rango de fecha de aprobacion
        if (filtro.getFechaInicioAprob()!=null && filtro.getFechaFinAprob()!=null){
            crit.add( Restrictions.and(
                            Restrictions.between("fechaAprobacion", filtro.getFechaInicioAprob(),filtro.getFechaFinAprob()))
            );
        }

        //nombre solicitud
        if (filtro.getNombreSolicitud() != null) {
            crit.add(Restrictions.ilike("dx.nombre", "%" + filtro.getNombreSolicitud() + "%"));
        }

        //filtro de resultados finales aprobados
        crit.add(Restrictions.and(
                        Restrictions.eq("rutina.aprobada", true))
        );
        //filtro de solicitud que no sea control de calidad
        crit.add(Restrictions.and(
                        Restrictions.eq("rutina.controlCalidad", false))
        );

        //filtro de resultado final positivo
        crit.add(Subqueries.propertyIn("rutina.idSolicitudDx", DetachedCriteria.forClass(DetalleResultadoFinal.class)
        .setProjection(Property.forName("solicitudDx.idSolicitudDx"))));

        crit.addOrder(Order.asc("fechaAprobacion"));

        if (!filtro.isNivelCentral()) {//si no es nivel central, filtrar solo el laboratorio al que pertenece el usuario o seleccionado en pantalla
            crit.createAlias("rutina.labProcesa", "labProcesa");
            if (!filtro.getCodLaboratio().equalsIgnoreCase("ALL")) {
                crit.add(Restrictions.and(
                        Restrictions.eq("labProcesa.codigo", filtro.getCodLaboratio())));
            }else {
                crit.add(Subqueries.propertyIn("labProcesa.codigo", DetachedCriteria.forClass(AutoridadLaboratorio.class)
                        .createAlias("laboratorio", "labautorizado")
                        .createAlias("user", "usuario")
                        .add(Restrictions.eq("pasivo", false)) //autoridad laboratorio activa
                        .add(Restrictions.and(Restrictions.eq("usuario.username", filtro.getNombreUsuario()))) //usuario
                        .setProjection(Property.forName("labautorizado.codigo"))));
            }
        }else {
            //se filtra por laboratorio que procesa
            if (!filtro.getCodLaboratio().equalsIgnoreCase("ALL")) {
                // and dx.labProcesa.codigo = :codigoLab
                crit.createAlias("rutina.labProcesa", "labProcesa");
                crit.add(Restrictions.and(
                        Restrictions.eq("labProcesa.codigo", filtro.getCodLaboratio())));
            }
        }

        //filtro x area
        if(filtro.getArea() != null){
            crit.createAlias("dx.area","area");
            crit.add( Restrictions.and(
                    Restrictions.eq("area.nombre",(filtro.getArea()))));
        }
        return crit.list();
    }

    @SuppressWarnings("unchecked")
    public List<DaSolicitudEstudio> getPositiveStudyRequestByFilter(FiltroMx filtro) throws UnsupportedEncodingException {
        Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(DaSolicitudEstudio.class, "estudio");
        crit.createAlias("estudio.idTomaMx", "toma");
        crit.createAlias("toma.idNotificacion", "notif");
        crit.createAlias("estudio.tipoEstudio", "tEstudio");
        crit.add(Restrictions.eq("estudio.anulado", false));
        if(filtro.getNombreSolicitud()!= null){
            filtro.setNombreSolicitud(URLDecoder.decode(filtro.getNombreSolicitud(), "utf-8"));
        }

        //se filtra por SILAIS
        if (filtro.getCodSilais()!=null){
            crit.createAlias("notif.codSilaisAtencion","silais");
            crit.add( Restrictions.and(
                            Restrictions.eq("silais.codigo", Long.valueOf(filtro.getCodSilais())))
            );
        }
        //se filtra por unidad de salud
        if (filtro.getCodUnidadSalud()!=null){
            crit.createAlias("notif.codUnidadAtencion","unidadS");
            crit.add( Restrictions.and(
                            Restrictions.eq("unidadS.codigo", Long.valueOf(filtro.getCodUnidadSalud())))
            );
        }
        //Se filtra por rango de fecha de aprobacion
        if (filtro.getFechaInicioAprob()!=null && filtro.getFechaFinAprob()!=null){
            crit.add( Restrictions.and(
                            Restrictions.between("fechaAprobacion", filtro.getFechaInicioAprob(),filtro.getFechaFinAprob()))
            );
        }

        //nombre solicitud
        if (filtro.getNombreSolicitud() != null) {
            //nombre solicitud
            if (filtro.getNombreSolicitud() != null) {
                crit.add(Restrictions.ilike("tEstudio.nombre", "%" + filtro.getNombreSolicitud() + "%"));
            }
        }
        if (!filtro.isNivelCentral()) {//si no es nivel central, filtrar solo el laboratorio al que pertenece el usuario
            //se filtra que usuario tenga autorizado laboratorio al que se envio la muestra desde ALERTA
            if (filtro.getNombreUsuario() != null) {
                crit.createAlias("toma.envio", "envioMx");
            /*crit.add(Subqueries.propertyIn("envioMx.laboratorioDestino.codigo", DetachedCriteria.forClass(AutoridadLaboratorio.class)
                    .createAlias("laboratorio", "labautorizado")
                    .createAlias("user", "usuario")
                    .add(Restrictions.eq("pasivo",false)) //autoridad laboratorio activa
                    .add(Restrictions.and(Restrictions.eq("usuario.username",filtro.getNombreUsuario()))) //usuario
                    .setProjection(Property.forName("labautorizado.codigo"))));*/

                //se filtra que laboratorio destino o si es traslado haya historico de envio al laboratario este autorizado al usuario
                Junction conditGroup = Restrictions.disjunction();
                conditGroup.add(Subqueries.propertyIn("envioMx.laboratorioDestino.codigo", DetachedCriteria.forClass(AutoridadLaboratorio.class)
                        .createAlias("laboratorio", "labautorizado")
                        .createAlias("user", "usuario")
                        .add(Restrictions.eq("pasivo", false)) //autoridad laboratorio activa
                        .add(Restrictions.and(Restrictions.eq("usuario.username", filtro.getNombreUsuario()))) //usuario
                        .setProjection(Property.forName("labautorizado.codigo"))))
                        .add(Restrictions.or(Subqueries.propertyIn("idTomaMx", DetachedCriteria.forClass(HistoricoEnvioMx.class)
                                .createAlias("tomaMx", "toma")
                                .createAlias("envioMx", "envio")
                                .createAlias("envio.laboratorioDestino", "destino")
                                .add(Subqueries.propertyIn("destino.codigo", DetachedCriteria.forClass(AutoridadLaboratorio.class)
                                        .add(Restrictions.eq("pasivo", false)) //autoridad lab activa
                                        .createAlias("laboratorio", "labautorizadoEnv")
                                        .createAlias("user", "usuario")
                                        .add(Restrictions.and(Restrictions.eq("usuario.username", filtro.getNombreUsuario()))) //usuario
                                        .setProjection(Property.forName("labautorizadoEnv.codigo"))))
                                .setProjection(Property.forName("toma.idTomaMx")))));
                crit.add(conditGroup);
            }
        }

        //filtro de resultados finales aprobados
        crit.add(Restrictions.and(
                        Restrictions.eq("estudio.aprobada", true))
        );

        //filtro de resultado final positivo
        crit.add(Subqueries.propertyIn("estudio.idSolicitudEstudio", DetachedCriteria.forClass(DetalleResultadoFinal.class)
                .setProjection(Property.forName("solicitudEstudio.idSolicitudEstudio"))));

        crit.addOrder(Order.asc("fechaAprobacion"));

        //filtro x area
        if(filtro.getArea() != null){
            crit.createAlias("tEstudio.area","area");
            crit.add( Restrictions.and(
                    Restrictions.eq("area.nombre",(filtro.getArea()))));
        }

        return crit.list();
    }

    @SuppressWarnings("unchecked")
    public List<DaSolicitudDx> getQCRoutineRequestByFilter(FiltroMx filtro) throws UnsupportedEncodingException {
        Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(DaSolicitudDx.class, "rutina");
        crit.add(Restrictions.eq("anulado",false));
        crit.createAlias("rutina.idTomaMx", "toma");
        crit.createAlias("toma.idNotificacion", "notif");
        crit.createAlias("rutina.codDx", "dx");

        if(filtro.getNombreSolicitud()!= null){
            filtro.setNombreSolicitud(URLDecoder.decode(filtro.getNombreSolicitud(), "utf-8"));
        }

        //se filtra por SILAIS
        if (filtro.getCodSilais()!=null){
            crit.createAlias("notif.codSilaisAtencion","silais");
            crit.add( Restrictions.and(
                            Restrictions.eq("silais.codigo", Long.valueOf(filtro.getCodSilais())))
            );
        }
        //se filtra por unidad de salud
        if (filtro.getCodUnidadSalud()!=null){
            crit.createAlias("notif.codUnidadAtencion","unidadS");
            crit.add( Restrictions.and(
                            Restrictions.eq("unidadS.codigo", Long.valueOf(filtro.getCodUnidadSalud())))
            );
        }
        //Se filtra por rango de fecha de aprobacion
        if (filtro.getFechaInicioAprob()!=null && filtro.getFechaFinAprob()!=null){
            crit.add( Restrictions.and(
                            Restrictions.between("fechaAprobacion", filtro.getFechaInicioAprob(),filtro.getFechaFinAprob()))
            );
        }

        //se filtra por tipo de solicitud
        if(filtro.getCodTipoSolicitud()!=null){
            if(filtro.getCodTipoSolicitud().equals("Rutina")){
                crit.add(Subqueries.propertyIn("toma.idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                        .createAlias("idTomaMx", "idTomaMx")
                        .setProjection(Property.forName("idTomaMx.idTomaMx"))));
            }

        }

        //nombre solicitud
        if (filtro.getNombreSolicitud() != null) {
            if (filtro.getCodTipoSolicitud() != null) {
                if (filtro.getCodTipoSolicitud().equals("Rutina")) {
                    crit.add(Subqueries.propertyIn("toma.idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                            .createAlias("codDx", "dx")
                            .add(Restrictions.ilike("dx.nombre", "%" + filtro.getNombreSolicitud() + "%"))
                            .setProjection(Property.forName("idTomaMx.idTomaMx"))));
                }
            }
        }
        //se filtra que usuario tenga autorizado laboratorio al que se envio la muestra desde ALERTA
        if (filtro.getNombreUsuario()!=null) {
            crit.createAlias("toma.envio","envioMx");
            crit.add(Subqueries.propertyIn("envioMx.laboratorioDestino.codigo", DetachedCriteria.forClass(AutoridadLaboratorio.class)
                    .createAlias("laboratorio", "labautorizado")
                    .createAlias("user", "usuario")
                    .add(Restrictions.eq("pasivo",false)) //autoridad laboratorio activa
                    .add(Restrictions.and(Restrictions.eq("usuario.username",filtro.getNombreUsuario()))) //usuario
                    .setProjection(Property.forName("labautorizado.codigo"))));
        }

        crit.add(Restrictions.and(
                        Restrictions.eq("rutina.controlCalidad", true))
        );

        //filtro de resultados finales aprobados
        crit.add(Restrictions.and(
                        Restrictions.eq("rutina.aprobada", true))
        );

        // se filtra por tipo de muestra
        if (filtro.getCodTipoMx()!=null){
            crit.createAlias("toma.codTipoMx", "tipoMx");
            crit.add( Restrictions.and(

                            Restrictions.eq("tipoMx.idTipoMx", Integer.valueOf(filtro.getCodTipoMx())))
            );
        }
        //filtro de resultado final positivo
        /*crit.add(Subqueries.propertyIn("rutina.idSolicitudDx", DetachedCriteria.forClass(DetalleResultadoFinal.class)
                .setProjection(Property.forName("solicitudDx.idSolicitudDx"))));*/

        //crit.addOrder(Order.asc("fechaAprobacion"));

        if (filtro.getCodLaboratio()!=null){
            crit.add(Subqueries.propertyIn("toma.idTomaMx", DetachedCriteria.forClass(TrasladoMx.class)
                    .createAlias("tomaMx", "tomaMx")
                    .add(Restrictions.eq("laboratorioOrigen.codigo",filtro.getCodLaboratio()))
                    .setProjection(Property.forName("tomaMx.idTomaMx"))));
            //crit.add(Restrictions.eq("labProcesa.codigo",filtro.getCodLaboratio()));
        }

        /*crit.add(Subqueries.propertyIn("labProcesa.codigo", DetachedCriteria.forClass(AutoridadLaboratorio.class)
                .createAlias("laboratorio", "labautorizado")
                .createAlias("user", "usuario")
                .add(Restrictions.eq("pasivo",false)) //autoridad laboratorio activa
                .add(Restrictions.and(Restrictions.eq("usuario.username",filtro.getNombreUsuario()))) //usuario
                .setProjection(Property.forName("labautorizado.codigo"))));*/

        //filtro x area
        /*if(filtro.getArea() != null){
            crit.createAlias("dx.area","area");
            crit.add( Restrictions.and(
                    Restrictions.eq("area.nombre",(filtro.getArea()))));
        }*/

        return crit.list();
    }

    public List<Object[]> getResumenRecepcionMuestrasSILAIS(String laboratorio, Date fechaInicio, Date fechaFin, boolean nivelCentral){
        Session session = sessionFactory.getCurrentSession();
        String sQuery = "select coalesce(count(r.idRecepcion),0) as total, sa.entidadAdtvaId, sa.nombre " +
                "from RecepcionMx as r inner join r.tomaMx as mx left join mx.codSilaisAtencion as sa " +
                "where r.fechaHoraRecepcion between :fechaInicio and :fechaFin ";
                if (!nivelCentral){
                    sQuery += "and r.labRecepcion.codigo = :laboratorio ";
                }
        sQuery += "group by sa.entidadAdtvaId, sa.nombre";

        Query q = session.createQuery(sQuery);
        if (!nivelCentral) {
            q.setParameter("laboratorio", laboratorio);
        }
        q.setParameter("fechaInicio", fechaInicio);
        q.setParameter("fechaFin", fechaFin);

        List<Object[]> resumenMxSilais= (List<Object[]>)q.list();
        return resumenMxSilais;
    }

    /**
     * Consolida las recepciones de muestras por municipios de un SILAIS
     * @param laboratorio
     * @param fechaInicio
     * @param fechaFin
     * @return List<Object[]>
     */
    public List<Object[]> getResumenRecepcionMuestrasMunSILAIS(String laboratorio, Date fechaInicio, Date fechaFin, Long codSilais){
        Session session = sessionFactory.getCurrentSession();
        String sQuery = "select distinct " +
                "coalesce((select count(distinct r.idRecepcion) " +
                "from RecepcionMx as r inner join r.tomaMx as mx inner join mx.idNotificacion as noti " +
                "where r.labRecepcion.codigo = :laboratorio and noti.codUnidadAtencion.municipio.divisionpoliticaId = divi.divisionpoliticaId " +
                "and r.fechaHoraRecepcion between :fechaInicio and :fechaFin " +
                "group by noti.codUnidadAtencion.municipio.divisionpoliticaId), 0), divi.codigoNacional, divi.nombre " +
                "from Divisionpolitica as divi, Unidades as uni " +
                "where divi.pasivo = '0' and uni.pasivo='0' " +
                "and uni.municipio.codigoNacional = divi.codigoNacional and uni.entidadAdtva.entidadAdtvaId = :codSilais ";

        Query q = session.createQuery(sQuery);

        q.setParameter("laboratorio",laboratorio);
        q.setParameter("fechaInicio", fechaInicio);
        q.setParameter("fechaFin", fechaFin);
        q.setParameter("codSilais", codSilais);

        List<Object[]> resumenMxSilais= (List<Object[]>)q.list();
        return resumenMxSilais;
    }


    public List<Object[]> getResumenRecepcionMuestrasSolicitud(String laboratorio, Date fechaInicio, Date fechaFin){
        Session session = sessionFactory.getCurrentSession();
        String sQuery = "select count(r.idRecepcion) as total, dx.idDiagnostico, dx.nombre " +
                "from RecepcionMx as r, DaSolicitudDx as sdx inner join sdx.idTomaMx as mx " +
                "inner join sdx.codDx as dx " +
                "where r.tomaMx.idTomaMx = mx.idTomaMx and sdx.anulado = false and sdx.inicial = true and sdx.labProcesa.codigo = :laboratorio " +
                " and r.fechaHoraRecepcion between :fechaInicio and :fechaFin " +
                "group by dx.idDiagnostico, dx.nombre";

        String sQuery2 = "select count(r.idRecepcion) as total, es.idEstudio, es.nombre " +
                "from RecepcionMx as r, DaSolicitudEstudio as sde inner join sde.idTomaMx as mx " +
                "inner join sde.tipoEstudio as es " +
                "where r.tomaMx.idTomaMx = mx.idTomaMx and sde.anulado = false and mx.envio.laboratorioDestino.codigo = :laboratorio " +
                " and r.fechaHoraRecepcion between :fechaInicio and :fechaFin " +
                "group by es.idEstudio, es.nombre";


        Query q = session.createQuery(sQuery);

        q.setParameter("laboratorio",laboratorio);
        q.setParameter("fechaInicio", fechaInicio);
        q.setParameter("fechaFin", fechaFin);

        List<Object[]> resumenMxSolicitud= (List<Object[]>)q.list();

        q = session.createQuery(sQuery2);
        q.setParameter("laboratorio",laboratorio);
        q.setParameter("fechaInicio", fechaInicio);
        q.setParameter("fechaFin", fechaFin);

        resumenMxSolicitud.addAll(q.list());

        return resumenMxSolicitud;
    }

    public List<DaSolicitudDx> getDiagnosticosAprobadosByFiltro(FiltrosReporte filtro){
        Session session = sessionFactory.getCurrentSession();
        Query queryNotiDx = null;
        if (filtro.getCodArea().equals("AREAREP|PAIS")) {
            queryNotiDx = session.createQuery(" select dx from DaSolicitudDx dx inner join dx.idTomaMx mx inner join mx.idNotificacion noti " +
                    "where noti.pasivo = false and dx.anulado = false and mx.anulada = false and dx.aprobada = true and dx.controlCalidad = false "+ sqlLab + sqlRutina + (filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut));
        }else if (filtro.getCodArea().equals("AREAREP|SILAIS")) {
            queryNotiDx = session.createQuery(" select dx from DaSolicitudDx dx inner join dx.idTomaMx mx inner join mx.idNotificacion noti " +
                    "where noti.pasivo = false and dx.anulado = false and mx.anulada = false and dx.aprobada = true and dx.controlCalidad = false "+ sqlLab + sqlRutina + (filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) +
            "  and noti.codSilaisAtencion.entidadAdtvaId =:codSilais ");
            queryNotiDx.setParameter("codSilais", filtro.getCodSilais());
        } else if (filtro.getCodArea().equals("AREAREP|UNI")) {
            queryNotiDx = session.createQuery(" select dx from DaSolicitudDx dx inner join dx.idTomaMx mx inner join mx.idNotificacion noti " +
                    "where noti.pasivo = false and dx.anulado = false and mx.anulada = false and dx.aprobada = true and dx.controlCalidad = false "+ sqlLab + sqlRutina + (filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) +
            "  and noti.codUnidadAtencion.unidadId =:codUnidad ");
            queryNotiDx.setParameter("codUnidad", filtro.getCodUnidad());
        }

        queryNotiDx.setParameter("codigoLab", filtro.getCodLaboratio());
        queryNotiDx.setParameter("idDx", filtro.getIdDx());
        queryNotiDx.setParameter("fechaInicio", filtro.getFechaInicio());
        queryNotiDx.setParameter("fechaFin", filtro.getFechaFin());
        return queryNotiDx.list();
    }

    public List<ResultadoVigilancia> getDiagnosticosAprobadosByFiltroV2(FiltrosReporte filtro){
        Session session = sessionFactory.getCurrentSession();
        Query queryNotiDx = null;
        if (filtro.getCodArea().equals("AREAREP|PAIS")) {
            queryNotiDx = session.createQuery(" select cast(p.personaId as string) as codigoExpUnico, p.primerNombre as primerNombre, p.segundoNombre as segundoNombre, p.primerApellido as primerApellido, p.segundoApellido as segundoApellido, p.fechaNacimiento as fechaNacimiento, p.sexo.codigo as sexo, " +
                    " p.direccionResidencia as direccionResidencia, p.telefonoResidencia as telefonoResidencia, p.telefonoMovil as telefonoMovil, coalesce((select co.nombre from  Comunidades co where co.codigo=p.comunidadResidencia.codigo), null) as comunidadResidencia, " +
                    " noti.idNotificacion as idNotificacion, noti.semanasEmbarazo as semanasEmbarazo, noti.fechaInicioSintomas as fechaInicioSintomas, coalesce((select r.valor from Respuesta r where r.codigo = noti.urgente.codigo), null) as urgente,  coalesce((select r.valor from Respuesta r where r.codigo = noti.embarazada.codigo), null) as embarazada, " +
                    " coalesce((select ea.codigo from EntidadesAdtvas ea where ea.codigo = noti.codSilaisAtencion.codigo ), null) as codigoSilaisNoti, coalesce((select ea.nombre from EntidadesAdtvas ea where ea.codigo = noti.codSilaisAtencion.codigo ), null) as nombreSilaisNoti, " +
                    " coalesce((select u.codigo from Unidades u where u.codigo = noti.codUnidadAtencion.codigo), null) as codigoUnidadNoti, coalesce((select u.nombre from Unidades u where u.codigo = noti.codUnidadAtencion.codigo), null) as nombreUnidadNoti, " +
                    " coalesce((select u.codigoNacional from Divisionpolitica u where u.codigoNacional = noti.codUnidadAtencion.municipio.codigoNacional), null) as codigoMuniNoti, coalesce((select u.nombre from Divisionpolitica u where u.codigoNacional = noti.codUnidadAtencion.municipio.codigoNacional), null) as nombreMuniNoti, " +
                    " coalesce((select u.codigoNacional from Divisionpolitica u where u.codigoNacional = noti.municipioResidencia.codigoNacional), null) as codigoMuniResid, coalesce((select u.nombre from Divisionpolitica u where u.codigoNacional = noti.municipioResidencia.codigoNacional), null) as nombreMuniResid, " +
                    " coalesce((select ea.codigo from EntidadesAdtvas ea where ea.codigo = noti.municipioResidencia.dependenciaSilais.codigo ), null) as codigoSilaisResid, coalesce((select ea.nombre from EntidadesAdtvas ea where ea.codigo = noti.municipioResidencia.dependenciaSilais.codigo ), null) as nombreSilaisResid, " +
                    " coalesce((select ea.codigo from EntidadesAdtvas ea where ea.codigo = mx.codSilaisAtencion.codigo ), null) as codigoSilaisMx, coalesce((select ea.nombre from EntidadesAdtvas ea where ea.codigo = mx.codSilaisAtencion.codigo ), null) as nombreSilaisMx, " +
                    " coalesce((select u.codigo from Unidades u where u.codigo = mx.codUnidadAtencion.codigo), null) as codigoUnidadMx, coalesce((select u.nombre from Unidades u where u.codigo = mx.codUnidadAtencion.codigo), null) as nombreUnidadMx, " +
                    " coalesce((select u.codigoNacional from Divisionpolitica u where u.codigoNacional = mx.codUnidadAtencion.municipio.codigoNacional), null) as codigoMuniMx, coalesce((select u.nombre from Divisionpolitica u where u.codigoNacional = mx.codUnidadAtencion.municipio.codigoNacional), null) as nombreMuniMx, " +
                    " mx.idTomaMx as idTomaMx, mx.fechaHTomaMx as fechaTomaMx, mx.codigoLab as codigoMx, mx.codigoUnicoMx as codUnicoMx, mx.codTipoMx.idTipoMx as idTipoMx, mx.codTipoMx.nombre as nombreTipoMx, dx.idSolicitudDx as idSolicitud, dx.fechaAprobacion as fechaAprobacion  " +
                    " from DaSolicitudDx dx inner join dx.idTomaMx mx inner join mx.idNotificacion noti inner join noti.persona p  " +
                    " where noti.pasivo = false and dx.anulado = false and mx.anulada = false and dx.aprobada = true and dx.controlCalidad = false "+ sqlLab + sqlRutina + (filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut));

        }else if (filtro.getCodArea().equals("AREAREP|SILAIS")) {
            queryNotiDx = session.createQuery(" select cast(p.personaId as string) as codigoExpUnico, p.primerNombre as primerNombre, p.segundoNombre as segundoNombre, p.primerApellido as primerApellido, p.segundoApellido as segundoApellido, p.fechaNacimiento as fechaNacimiento, p.sexo.codigo as sexo, " +
                    " p.direccionResidencia as direccionResidencia, p.telefonoResidencia as telefonoResidencia, p.telefonoMovil as telefonoMovil, coalesce((select co.nombre from  Comunidades co where co.codigo=p.comunidadResidencia.codigo), null) as comunidadResidencia, " +
                    " noti.idNotificacion as idNotificacion, noti.semanasEmbarazo as semanasEmbarazo, noti.fechaInicioSintomas as fechaInicioSintomas, coalesce((select r.valor from Respuesta r where r.codigo = noti.urgente.codigo), null) as urgente,  coalesce((select r.valor from Respuesta r where r.codigo = noti.embarazada.codigo), null) as embarazada, " +
                    " coalesce((select ea.codigo from EntidadesAdtvas ea where ea.codigo = noti.codSilaisAtencion.codigo ), null) as codigoSilaisNoti, coalesce((select ea.nombre from EntidadesAdtvas ea where ea.codigo = noti.codSilaisAtencion.codigo ), null) as nombreSilaisNoti, " +
                    " coalesce((select u.codigo from Unidades u where u.codigo = noti.codUnidadAtencion.codigo), null) as codigoUnidadNoti, coalesce((select u.nombre from Unidades u where u.codigo = noti.codUnidadAtencion.codigo), null) as nombreUnidadNoti, " +
                    " coalesce((select u.codigoNacional from Divisionpolitica u where u.codigoNacional = noti.codUnidadAtencion.municipio.codigoNacional), null) as codigoMuniNoti, coalesce((select u.nombre from Divisionpolitica u where u.codigoNacional = noti.codUnidadAtencion.municipio.codigoNacional), null) as nombreMuniNoti, " +
                    " coalesce((select u.codigoNacional from Divisionpolitica u where u.codigoNacional = noti.municipioResidencia.codigoNacional), null) as codigoMuniResid, coalesce((select u.nombre from Divisionpolitica u where u.codigoNacional = noti.municipioResidencia.codigoNacional), null) as nombreMuniResid, " +
                    " coalesce((select ea.codigo from EntidadesAdtvas ea where ea.codigo = noti.municipioResidencia.dependenciaSilais.codigo ), null) as codigoSilaisResid, coalesce((select ea.nombre from EntidadesAdtvas ea where ea.codigo = noti.municipioResidencia.dependenciaSilais.codigo ), null) as nombreSilaisResid, " +
                    " coalesce((select ea.codigo from EntidadesAdtvas ea where ea.codigo = mx.codSilaisAtencion.codigo ), null) as codigoSilaisMx, coalesce((select ea.nombre from EntidadesAdtvas ea where ea.codigo = mx.codSilaisAtencion.codigo ), null) as nombreSilaisMx, " +
                    " coalesce((select u.codigo from Unidades u where u.codigo = mx.codUnidadAtencion.codigo), null) as codigoUnidadMx, coalesce((select u.nombre from Unidades u where u.codigo = mx.codUnidadAtencion.codigo), null) as nombreUnidadMx, " +
                    " coalesce((select u.codigoNacional from Divisionpolitica u where u.codigoNacional = mx.codUnidadAtencion.municipio.codigoNacional), null) as codigoMuniMx, coalesce((select u.nombre from Divisionpolitica u where u.codigoNacional = mx.codUnidadAtencion.municipio.codigoNacional), null) as nombreMuniMx, " +
                    " mx.idTomaMx as idTomaMx, mx.fechaHTomaMx as fechaTomaMx, mx.codigoLab as codigoMx, mx.codigoUnicoMx as codUnicoMx, mx.codTipoMx.idTipoMx as idTipoMx, mx.codTipoMx.nombre as nombreTipoMx, dx.idSolicitudDx as idSolicitud, dx.fechaAprobacion as fechaAprobacion  " +
                    " from DaSolicitudDx dx inner join dx.idTomaMx mx inner join mx.idNotificacion noti inner join noti.persona p " +
                    " where noti.pasivo = false and dx.anulado = false and mx.anulada = false and dx.aprobada = true and dx.controlCalidad = false "+ sqlLab + sqlRutina + (filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) +
                    " and noti.codSilaisAtencion.entidadAdtvaId =:codSilais ");
            queryNotiDx.setParameter("codSilais", filtro.getCodSilais());

        } else if (filtro.getCodArea().equals("AREAREP|UNI")) {
            queryNotiDx = session.createQuery(" select cast(p.personaId as string) as codigoExpUnico, p.primerNombre as primerNombre, p.segundoNombre as segundoNombre, p.primerApellido as primerApellido, p.segundoApellido as segundoApellido, p.fechaNacimiento as fechaNacimiento, p.sexo.codigo as sexo, " +
                    " p.direccionResidencia as direccionResidencia, p.telefonoResidencia as telefonoResidencia, p.telefonoMovil as telefonoMovil, coalesce((select co.nombre from  Comunidades co where co.codigo=p.comunidadResidencia.codigo), null) as comunidadResidencia, " +
                    " noti.idNotificacion as idNotificacion, noti.semanasEmbarazo as semanasEmbarazo, noti.fechaInicioSintomas as fechaInicioSintomas, coalesce((select r.valor from Respuesta r where r.codigo = noti.urgente.codigo), null) as urgente,  coalesce((select r.valor from Respuesta r where r.codigo = noti.embarazada.codigo), null) as embarazada, " +
                    " coalesce((select ea.codigo from EntidadesAdtvas ea where ea.codigo = noti.codSilaisAtencion.codigo ), null) as codigoSilaisNoti, coalesce((select ea.nombre from EntidadesAdtvas ea where ea.codigo = noti.codSilaisAtencion.codigo ), null) as nombreSilaisNoti, " +
                    " coalesce((select u.codigo from Unidades u where u.codigo = noti.codUnidadAtencion.codigo), null) as codigoUnidadNoti, coalesce((select u.nombre from Unidades u where u.codigo = noti.codUnidadAtencion.codigo), null) as nombreUnidadNoti, " +
                    " coalesce((select u.codigoNacional from Divisionpolitica u where u.codigoNacional = noti.codUnidadAtencion.municipio.codigoNacional), null) as codigoMuniNoti, coalesce((select u.nombre from Divisionpolitica u where u.codigoNacional = noti.codUnidadAtencion.municipio.codigoNacional), null) as nombreMuniNoti, " +
                    " coalesce((select u.codigoNacional from Divisionpolitica u where u.codigoNacional = noti.municipioResidencia.codigoNacional), null) as codigoMuniResid, coalesce((select u.nombre from Divisionpolitica u where u.codigoNacional = noti.municipioResidencia.codigoNacional), null) as nombreMuniResid, " +
                    " coalesce((select ea.codigo from EntidadesAdtvas ea where ea.codigo = noti.municipioResidencia.dependenciaSilais.codigo ), null) as codigoSilaisResid, coalesce((select ea.nombre from EntidadesAdtvas ea where ea.codigo = noti.municipioResidencia.dependenciaSilais.codigo ), null) as nombreSilaisResid, " +
                    " coalesce((select ea.codigo from EntidadesAdtvas ea where ea.codigo = mx.codSilaisAtencion.codigo ), null) as codigoSilaisMx, coalesce((select ea.nombre from EntidadesAdtvas ea where ea.codigo = mx.codSilaisAtencion.codigo ), null) as nombreSilaisMx, " +
                    " coalesce((select u.codigo from Unidades u where u.codigo = mx.codUnidadAtencion.codigo), null) as codigoUnidadMx, coalesce((select u.nombre from Unidades u where u.codigo = mx.codUnidadAtencion.codigo), null) as nombreUnidadMx, " +
                    " coalesce((select u.codigoNacional from Divisionpolitica u where u.codigoNacional = mx.codUnidadAtencion.municipio.codigoNacional), null) as codigoMuniMx, coalesce((select u.nombre from Divisionpolitica u where u.codigoNacional = mx.codUnidadAtencion.municipio.codigoNacional), null) as nombreMuniMx, " +
                    " mx.idTomaMx as idTomaMx, mx.fechaHTomaMx as fechaTomaMx, mx.codigoLab as codigoMx, mx.codigoUnicoMx as codUnicoMx, mx.codTipoMx.idTipoMx as idTipoMx, mx.codTipoMx.nombre as nombreTipoMx, dx.idSolicitudDx as idSolicitud, dx.fechaAprobacion as fechaAprobacion  " +
                    " from DaSolicitudDx dx inner join dx.idTomaMx mx inner join mx.idNotificacion noti inner join noti.persona p " +
                    " where noti.pasivo = false and dx.anulado = false and mx.anulada = false and dx.aprobada = true and dx.controlCalidad = false "+ sqlLab + sqlRutina + (filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) +
                    " and noti.codUnidadAtencion.unidadId =:codUnidad ");
            queryNotiDx.setParameter("codUnidad", filtro.getCodUnidad());
        }

        queryNotiDx.setParameter("codigoLab", filtro.getCodLaboratio());
        queryNotiDx.setParameter("idDx", filtro.getIdDx());
        queryNotiDx.setParameter("fechaInicio", filtro.getFechaInicio());
        queryNotiDx.setParameter("fechaFin", filtro.getFechaFin());

        queryNotiDx.setResultTransformer(Transformers.aliasToBean(ResultadoVigilancia.class));
        return queryNotiDx.list();
    }

    /**
     * M?todo que retornar la informaci?n para generar reporte y gr?fico de notificaciones por tipo de resultado (positivo, negativo, sin resultado y % positividad)
     * 04-09-2018 Andrea solocita que sea por fecha de aprobaci�n que se filtre el reporte
     * @param filtro indicando el nivel (pais, silais, departamento, municipio, unidad salud), tipo notificaci?n, rango de fechas, factor tasas de poblaci?n
     * @return Lista de objetos a mostrar
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> getDataDxResultReport(FiltrosReporte filtro, String nombreDx, int cantidadColumnas) {
        // Retrieve session from Hibernate
        List<Object[]> resTemp1 = new ArrayList<Object[]>();
        List<Object[]> resTemp2 = new ArrayList<Object[]>();

        List<Object[]> resFinal = new ArrayList<Object[]>();
        Session session = sessionFactory.getCurrentSession();
        Query queryNotiDx = null;
        Query queryIdNoti = null;

        if (filtro.getCodArea().equals("AREAREP|PAIS")) {

            if (filtro.isPorSilais()) {
                queryNotiDx = session.createQuery(" select ent.codigo, ent.nombre, " + //TOTAL RUTINAS
                        " (select coalesce(sum(count(noti.idNotificacion)),0) from DaNotificacion noti, DaTomaMx mx, DaSolicitudDx dx " +
                        " where noti.idNotificacion = mx.idNotificacion.idNotificacion and noti.codSilaisAtencion.codigo = ent.codigo " +
                        " and mx.idTomaMx = dx.idTomaMx.idTomaMx and noti.pasivo = false and dx.anulado = false and mx.anulada = false and dx.controlCalidad = false " +
                        sqlRutina + (filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) + (!filtro.getCodLaboratio().equalsIgnoreCase("ALL")?sqlLab:"") +
                        " group by noti.codSilaisAtencion.codigo) as dx, " +
                        " coalesce( " + //TOTAL RUTINAS CON RESULTADO
                        " (select sum(case dx.aprobada when true then 1 else 0 end) " +
                        " from DaNotificacion noti, DaTomaMx mx, DaSolicitudDx dx" +
                        " where noti.idNotificacion = mx.idNotificacion.idNotificacion " +
                        " and mx.idTomaMx = dx.idTomaMx.idTomaMx " +
                        " and  noti.codSilaisAtencion.codigo = ent.codigo " +
                        sqlRutina + (filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) + (!filtro.getCodLaboratio().equalsIgnoreCase("ALL")?sqlLab:"") +
                        " and noti.pasivo = false and dx.anulado = false and dx.controlCalidad = false " +
                        " and mx.anulada = false),0) as conresultado, " +
                        " coalesce( " + //TOTAL RUTINAS SIN RESULTADO
                        " (select  sum(case dx.aprobada when false then 1 else 0 end) " +
                        " from DaNotificacion noti, DaTomaMx mx, DaSolicitudDx dx " +
                        " where noti.idNotificacion = mx.idNotificacion.idNotificacion " +
                        " and mx.idTomaMx = dx.idTomaMx.idTomaMx " +
                        sqlRutina + (filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) + (!filtro.getCodLaboratio().equalsIgnoreCase("ALL")?sqlLab:"") +
                        " and  noti.codSilaisAtencion.codigo = ent.codigo " +
                        " and noti.pasivo = false and dx.anulado = false and dx.controlCalidad = false " +
                        " and mx.anulada = false),0) as sinresultado " +
                        " from EntidadesAdtvas ent " + (!filtro.isNivelCentral()?", EntidadAdtvaLaboratorio entlab ":"") +
                        " where ent.pasivo = 0 " + (!filtro.isNivelCentral()?" and ent.codigo = entlab.entidadAdtva.codigo and entlab.laboratorio.codigo = :laboratorio ":"") +
                        " order by ent.codigo ");

                queryIdNoti = session.createQuery(" select noti.codSilaisAtencion.codigo, dx.idSolicitudDx, r.valor " +
                        ", coalesce((select rr.concepto.tipo.codigo from RespuestaSolicitud rr where rr.idRespuesta = r.respuesta.idRespuesta),'NULL')"+
                        ", coalesce((select rr.concepto.tipo.codigo from RespuestaExamen rr where rr.idRespuesta = r.respuestaExamen.idRespuesta),'NULL') "+
                        " from DaNotificacion noti, DaTomaMx mx, DaSolicitudDx dx, DetalleResultadoFinal r " +
                        " where noti.idNotificacion = mx.idNotificacion " +
                        sqlRutina + (filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) + (!filtro.getCodLaboratio().equalsIgnoreCase("ALL")?sqlLab:"") +
                        " and mx.idTomaMx = dx.idTomaMx.idTomaMx " +
                        " and dx.idSolicitudDx = r.solicitudDx.idSolicitudDx and r.pasivo = false " +
                        " and noti.pasivo = false and dx.anulado = false " +
                        " and mx.anulada = false " +
                        " and dx.aprobada = true and dx.controlCalidad = false " +
                        " and noti.codSilaisAtencion is not null " +
                        " order by noti.codSilaisAtencion.codigo");

            }else{
                queryNotiDx = session.createQuery(" select div.divisionpoliticaId, div.nombre, " +
                        " (select coalesce(sum(count(noti.idNotificacion)),0) from DaNotificacion noti, DaTomaMx mx, DaSolicitudDx dx" +
                        " where noti.idNotificacion = mx.idNotificacion.idNotificacion" +
                        " and noti.codUnidadAtencion.municipio.dependencia.divisionpoliticaId = div.divisionpoliticaId " +
                        " and mx.idTomaMx = dx.idTomaMx.idTomaMx and noti.pasivo = false and dx.anulado = false and dx.controlCalidad = false and mx.anulada = false " +
                        sqlRutina + (filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) + (!filtro.getCodLaboratio().equalsIgnoreCase("ALL")?sqlLab:"") +
                        " group by noti.codUnidadAtencion.municipio.dependencia.divisionpoliticaId) as dx, " +
                        " coalesce( " +
                        " (select sum(case dx.aprobada when true then 1 else 0 end) " +
                        " from DaNotificacion noti, DaTomaMx mx, DaSolicitudDx dx " +
                        " where noti.idNotificacion = mx.idNotificacion.idNotificacion " +
                        " and mx.idTomaMx = dx.idTomaMx.idTomaMx " +
                        " and  noti.codUnidadAtencion.municipio.dependencia.divisionpoliticaId = div.divisionpoliticaId " +
                        sqlRutina + (filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) + (!filtro.getCodLaboratio().equalsIgnoreCase("ALL")?sqlLab:"") +
                        " and noti.pasivo = false and dx.anulado = false and dx.controlCalidad = false " +
                        " and mx.anulada = false),0) as conresultado, " +
                        " coalesce( " +
                        " (select  sum(case dx.aprobada when false then 1 else 0 end) " +
                        " from DaNotificacion noti, DaTomaMx mx, DaSolicitudDx dx " +
                        " where noti.idNotificacion = mx.idNotificacion.idNotificacion " +
                        " and mx.idTomaMx = dx.idTomaMx.idTomaMx " +
                        sqlRutina + (filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) + (!filtro.getCodLaboratio().equalsIgnoreCase("ALL")?sqlLab:"") +
                        " and  noti.codUnidadAtencion.municipio.dependencia.divisionpoliticaId = div.divisionpoliticaId " +
                        " and noti.pasivo = false and dx.anulado = false and dx.controlCalidad = false " +
                        " and mx.anulada = false),0) as sinresultado " +
                        " from Divisionpolitica div " +
                        "where div.dependencia is null and div.pasivo = '0'" +
                        " order by div.divisionpoliticaId ");

                queryIdNoti = session.createQuery(" select noti.codUnidadAtencion.municipio.dependencia.divisionpoliticaId, dx.idSolicitudDx, r.valor " +
                        ", coalesce((select rr.concepto.tipo.codigo from RespuestaSolicitud rr where rr.idRespuesta = r.respuesta.idRespuesta),'NULL')"+
                        ", coalesce((select rr.concepto.tipo.codigo from RespuestaExamen rr where rr.idRespuesta = r.respuestaExamen.idRespuesta),'NULL') "+
                        " from DaNotificacion noti, DaTomaMx mx, DaSolicitudDx dx, DetalleResultadoFinal r " +
                        " where noti.idNotificacion = mx.idNotificacion " +
                        sqlRutina + (filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) + (!filtro.getCodLaboratio().equalsIgnoreCase("ALL")?sqlLab:"") +
                        " and mx.idTomaMx = dx.idTomaMx.idTomaMx " +
                        " and dx.idSolicitudDx = r.solicitudDx.idSolicitudDx and r.pasivo = false " +
                        " and noti.pasivo = false and dx.anulado = false and dx.controlCalidad = false " +
                        " and mx.anulada = false " +
                        " and dx.aprobada = true " +
                        " and noti.codUnidadAtencion is not null " +
                        " order by noti.codUnidadAtencion.municipio.dependencia.divisionpoliticaId");

            }

            //rutinas
            queryIdNoti.setParameter("idDx", filtro.getIdDx());
            queryIdNoti.setParameter("fechaInicio", filtro.getFechaInicio());
            queryIdNoti.setParameter("fechaFin", filtro.getFechaFin());
            //if (!filtro.isNivelCentral()) {
            if (!filtro.getCodLaboratio().equalsIgnoreCase("ALL")) {
                queryNotiDx.setParameter("codigoLab", filtro.getCodLaboratio());
                queryIdNoti.setParameter("codigoLab", filtro.getCodLaboratio());
            }
            if (!filtro.isNivelCentral()){
                queryNotiDx.setParameter("laboratorio", filtro.getCodLaboratio());
            }
            resTemp2.addAll(queryIdNoti.list());

        } else if (filtro.getCodArea().equals("AREAREP|SILAIS")) {

            queryNotiDx = session.createQuery(" select distinct div.divisionpoliticaId, div.nombre, " +
                    " (select coalesce(sum(count(noti.idNotificacion)),0) from DaNotificacion noti, DaTomaMx mx, DaSolicitudDx dx " +
                    " where noti.idNotificacion = mx.idNotificacion.idNotificacion" +
                    " and noti.codUnidadAtencion.municipio.divisionpoliticaId = div.divisionpoliticaId " +
                    " and mx.idTomaMx = dx.idTomaMx.idTomaMx and noti.pasivo = false and dx.anulado = false and dx.controlCalidad = false and mx.anulada = false " +
                    sqlRutina + (filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) + (!filtro.getCodLaboratio().equalsIgnoreCase("ALL")?sqlLab:"") +
                    " group by noti.codSilaisAtencion.entidadAdtvaId), " +
                    " coalesce( " +
                    " (select sum(case dx.aprobada when true then 1 else 0 end) " +
                    " from DaNotificacion noti, DaTomaMx mx, DaSolicitudDx dx " +
                    " where noti.idNotificacion = mx.idNotificacion.idNotificacion " +
                    " and mx.idTomaMx = dx.idTomaMx.idTomaMx " +
                    " and noti.codUnidadAtencion.municipio.divisionpoliticaId = div.divisionpoliticaId " +
                    sqlRutina +(filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) + (!filtro.getCodLaboratio().equalsIgnoreCase("ALL")?sqlLab:"") +
                    " and noti.pasivo = false and dx.anulado = false and dx.controlCalidad = false " +
                    " and mx.anulada = false),0) as conresultado, " +
                    " coalesce( " +
                    " (select  sum(case dx.aprobada when false then 1 else 0 end) " +
                    " from DaNotificacion noti, DaTomaMx mx, DaSolicitudDx dx " +
                    " where noti.idNotificacion = mx.idNotificacion.idNotificacion " +
                    " and mx.idTomaMx = dx.idTomaMx.idTomaMx " +
                    " and noti.codUnidadAtencion.municipio.divisionpoliticaId = div.divisionpoliticaId " +
                    sqlRutina +(filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) + (!filtro.getCodLaboratio().equalsIgnoreCase("ALL")?sqlLab:"") +
                    " and noti.pasivo = false and dx.anulado = false and dx.controlCalidad = false " +
                    " and mx.anulada = false),0) as sinresultado " +
                    " from Divisionpolitica div, Unidades as uni " +
                    " where div.pasivo = '0' and uni.pasivo='0' " +
                    " and uni.municipio.codigoNacional = div.codigoNacional and uni.entidadAdtva.codigo = :codSilais " +
                    " order by div.divisionpoliticaId ");

            queryIdNoti = session.createQuery(" select noti.codUnidadAtencion.municipio.divisionpoliticaId, dx.idSolicitudDx, r.valor " +
                    ", coalesce((select rr.concepto.tipo.codigo from RespuestaSolicitud rr where rr.idRespuesta = r.respuesta.idRespuesta),'NULL')"+
                    ", coalesce((select rr.concepto.tipo.codigo from RespuestaExamen rr where rr.idRespuesta = r.respuestaExamen.idRespuesta),'NULL') "+
                    " from DaNotificacion noti, DaTomaMx mx, DaSolicitudDx dx, DetalleResultadoFinal r " +
                    " where noti.idNotificacion = mx.idNotificacion " +
                    sqlRutina +(filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) + (!filtro.getCodLaboratio().equalsIgnoreCase("ALL")?sqlLab:"") +
                    " and mx.idTomaMx = dx.idTomaMx.idTomaMx " +
                    " and dx.idSolicitudDx = r.solicitudDx.idSolicitudDx and r.pasivo = false " +
                    " and noti.pasivo = false and dx.anulado = false " +
                    " and mx.anulada = false " +
                    " and dx.aprobada = true and dx.controlCalidad = false " +
                    " and noti.codUnidadAtencion.entidadAdtva.codigo = :codSilais " +
                    " order by noti.codUnidadAtencion.municipio.divisionpoliticaId ");

            queryNotiDx.setParameter("codSilais", filtro.getCodSilais());

            //rutinas
            queryIdNoti.setParameter("idDx", filtro.getIdDx());
            queryIdNoti.setParameter("fechaInicio", filtro.getFechaInicio());
            queryIdNoti.setParameter("fechaFin", filtro.getFechaFin());
            queryIdNoti.setParameter("codSilais", filtro.getCodSilais());
            if (!filtro.getCodLaboratio().equalsIgnoreCase("ALL")) {
                queryNotiDx.setParameter("codigoLab", filtro.getCodLaboratio());
                queryIdNoti.setParameter("codigoLab", filtro.getCodLaboratio());
            }
            resTemp2.addAll(queryIdNoti.list());

        } else if (filtro.getCodArea().equals("AREAREP|MUNI")) {
            queryNotiDx = session.createQuery(" select uni.unidadId, uni.nombre, " +
                    " (select coalesce(sum(count(noti.idNotificacion)),0) from DaNotificacion noti, DaTomaMx mx, DaSolicitudDx dx " +
                    " where noti.idNotificacion = mx.idNotificacion.idNotificacion" +
                    " and noti.codUnidadAtencion.codigo =  uni.codigo " +
                    " and mx.idTomaMx = dx.idTomaMx.idTomaMx and noti.pasivo = false and dx.anulado = false and mx.anulada = false and dx.controlCalidad = false " +
                    sqlRutina +(filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) + (!filtro.getCodLaboratio().equalsIgnoreCase("ALL")?sqlLab:"") +
                    " group by  noti.codUnidadAtencion.unidadId) as dx, " +
                    " coalesce( " +
                    " (select sum(case dx.aprobada when true then 1 else 0 end) " +
                    " from DaNotificacion noti, DaTomaMx mx, DaSolicitudDx dx " +
                    " where noti.idNotificacion = mx.idNotificacion.idNotificacion " +
                    " and mx.idTomaMx = dx.idTomaMx.idTomaMx " +
                    " and  noti.codUnidadAtencion.codigo =  uni.codigo " +
                    sqlRutina +(filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) + (!filtro.getCodLaboratio().equalsIgnoreCase("ALL")?sqlLab:"") +
                    " and noti.pasivo = false and dx.anulado = false and dx.controlCalidad = false " +
                    " and mx.anulada = false),0) as conresultado, " +
                    " coalesce( " +
                    " (select  sum(case dx.aprobada when false then 1 else 0 end) " +
                    " from DaNotificacion noti, DaTomaMx mx, DaSolicitudDx dx " +
                    " where noti.idNotificacion = mx.idNotificacion.idNotificacion " +
                    " and mx.idTomaMx = dx.idTomaMx.idTomaMx " +
                    sqlRutina +(filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) + (!filtro.getCodLaboratio().equalsIgnoreCase("ALL")?sqlLab:"") +
                    " and  noti.codUnidadAtencion.codigo =  uni.codigo " +
                    " and noti.pasivo = false and dx.anulado = false and dx.controlCalidad = false " +
                    " and mx.anulada = false),0) as sinresultado " +
                    "FROM Unidades uni " +
                    "where uni.municipio.codigoNacional = :codMunicipio" +
                    " and uni.entidadAdtva.codigo = :codSilais" +
                    " and uni.tipoUnidad in ("+ HealthUnitType.UnidadesPrimHosp.getDiscriminator()+") " +
                    " order by uni.unidadId ");


            queryIdNoti = session.createQuery(" select noti.codUnidadAtencion.unidadId, dx.idSolicitudDx, r.valor " +
                    ", coalesce((select rr.concepto.tipo.codigo from RespuestaSolicitud rr where rr.idRespuesta = r.respuesta.idRespuesta),'NULL')"+
                    ", coalesce((select rr.concepto.tipo.codigo from RespuestaExamen rr where rr.idRespuesta = r.respuestaExamen.idRespuesta),'NULL') "+
                    " from DaNotificacion noti, DaTomaMx mx, DaSolicitudDx dx, DetalleResultadoFinal r  " +
                    " where noti.idNotificacion = mx.idNotificacion " +
                    sqlRutina +(filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) + (!filtro.getCodLaboratio().equalsIgnoreCase("ALL")?sqlLab:"") +
                    " and mx.idTomaMx = dx.idTomaMx.idTomaMx " +
                    " and dx.idSolicitudDx = r.solicitudDx.idSolicitudDx and r.pasivo = false " +
                    " and noti.pasivo = false and dx.anulado = false " +
                    " and mx.anulada = false " +
                    " and dx.aprobada = true and dx.controlCalidad = false " +
                    " and noti.codUnidadAtencion.municipio.codigoNacional = :codMunicipio " +
                    " and noti.codUnidadAtencion.entidadAdtva.codigo = :codSilais " +
                    " order by noti.codUnidadAtencion.unidadId ");

            queryNotiDx.setParameter("codMunicipio", String.valueOf(filtro.getCodMunicipio()));
            queryNotiDx.setParameter("codSilais", filtro.getCodSilais());

            //rutinas
            queryIdNoti.setParameter("idDx", filtro.getIdDx());
            queryIdNoti.setParameter("fechaInicio", filtro.getFechaInicio());
            queryIdNoti.setParameter("fechaFin", filtro.getFechaFin());
            queryIdNoti.setParameter("codMunicipio", String.valueOf(filtro.getCodMunicipio()));
            queryIdNoti.setParameter("codSilais", filtro.getCodSilais());
            if (!filtro.getCodLaboratio().equalsIgnoreCase("ALL")) {
                queryNotiDx.setParameter("codigoLab", filtro.getCodLaboratio());
                queryIdNoti.setParameter("codigoLab", filtro.getCodLaboratio());
            }
            resTemp2.addAll(queryIdNoti.list());

        } else if (filtro.getCodArea().equals("AREAREP|UNI")) {

            if(filtro.isSubunidades()){
                queryNotiDx = session.createQuery(" select uni.unidadId, uni.nombre, " +
                        " (select coalesce(sum(count(noti.idNotificacion)),0) from DaNotificacion noti, DaTomaMx mx, DaSolicitudDx dx" +
                        " where noti.idNotificacion = mx.idNotificacion.idNotificacion" +
                        " and noti.codUnidadAtencion.codigo =  uni.codigo " +
                        " and mx.idTomaMx = dx.idTomaMx.idTomaMx and noti.pasivo = false and dx.anulado = false and dx.controlCalidad = false and mx.anulada = false " +
                        sqlRutina +(filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) + (!filtro.getCodLaboratio().equalsIgnoreCase("ALL")?sqlLab:"") +
                        " group by  noti.codUnidadAtencion.unidadId) as dx, " +
                        " coalesce( " +
                        " (select sum(case dx.aprobada when true then 1 else 0 end) " +
                        " from DaNotificacion noti, DaTomaMx mx, DaSolicitudDx dx " +
                        " where noti.idNotificacion = mx.idNotificacion.idNotificacion " +
                        " and mx.idTomaMx = dx.idTomaMx.idTomaMx " +
                        " and  noti.codUnidadAtencion.codigo =  uni.codigo " +
                        sqlRutina +(filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) + (!filtro.getCodLaboratio().equalsIgnoreCase("ALL")?sqlLab:"") +
                        " and noti.pasivo = false and dx.anulado = false and dx.controlCalidad = false " +
                        " and mx.anulada = false),0) as conresultado, " +
                        " coalesce( " +
                        " (select  sum(case dx.aprobada when false then 1 else 0 end) " +
                        " from DaNotificacion noti, DaTomaMx mx, DaSolicitudDx dx " +
                        " where noti.idNotificacion = mx.idNotificacion.idNotificacion " +
                        " and mx.idTomaMx = dx.idTomaMx.idTomaMx " +
                        sqlRutina +(filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) + (!filtro.getCodLaboratio().equalsIgnoreCase("ALL")?sqlLab:"") +
                        " and  noti.codUnidadAtencion.codigo =  uni.codigo " +
                        " and noti.pasivo = false and dx.anulado = false and dx.controlCalidad = false " +
                        " and mx.anulada = false),0) as sinresultado " +
                        "FROM Unidades uni " +
                        "where (uni.unidadId = :codUnidad" +
                        " or uni.unidadAdtva in (select u.codigo from Unidades u where u.unidadId = :codUnidad )) " +
                        " and uni.tipoUnidad in ("+ HealthUnitType.UnidadesPrimHosp.getDiscriminator()+") " +
                        " order by uni.unidadId ");

                queryIdNoti = session.createQuery(" select noti.codUnidadAtencion.unidadId, dx.idSolicitudDx, r.valor " +
                        ", coalesce((select rr.concepto.tipo.codigo from RespuestaSolicitud rr where rr.idRespuesta = r.respuesta.idRespuesta),'NULL')"+
                        ", coalesce((select rr.concepto.tipo.codigo from RespuestaExamen rr where rr.idRespuesta = r.respuestaExamen.idRespuesta),'NULL') "+
                        " from DaNotificacion noti, DaTomaMx mx, DaSolicitudDx dx, DetalleResultadoFinal r " +
                        " where noti.idNotificacion = mx.idNotificacion " +
                        sqlRutina + (filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) + (!filtro.getCodLaboratio().equalsIgnoreCase("ALL")?sqlLab:"") +
                        " and mx.idTomaMx = dx.idTomaMx.idTomaMx " +
                        " and dx.idSolicitudDx = r.solicitudDx.idSolicitudDx and r.pasivo = false " +
                        " and noti.pasivo = false and dx.anulado = false " +
                        " and mx.anulada = false " +
                        " and dx.aprobada = true and dx.controlCalidad = false " +
                        " and (noti.codUnidadAtencion.unidadId = :codUnidad " +
                        " or noti.codUnidadAtencion.unidadAdtva in (select u.codigo from Unidades u where u.unidadId = :codUnidad )) " +
                        " order by noti.codUnidadAtencion.unidadId ");

            }else{
                queryNotiDx = session.createQuery(" select uni.unidadId, uni.nombre, " +
                        " (select coalesce(sum(count(noti.idNotificacion)),0) from DaNotificacion noti, DaTomaMx mx, DaSolicitudDx dx " +
                        " where noti.idNotificacion = mx.idNotificacion.idNotificacion" +
                        " and noti.codUnidadAtencion.codigo =  uni.codigo " +
                        " and mx.idTomaMx = dx.idTomaMx.idTomaMx and noti.pasivo = false and dx.anulado = false and dx.controlCalidad = false and mx.anulada = false " +
                        sqlRutina +(filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) + (!filtro.getCodLaboratio().equalsIgnoreCase("ALL")?sqlLab:"") +
                        " group by  noti.codUnidadAtencion.unidadId) as dx, " +
                        " coalesce( " +
                        " (select sum(case dx.aprobada when true then 1 else 0 end) " +
                        " from DaNotificacion noti, DaTomaMx mx, DaSolicitudDx dx" +
                        " where noti.idNotificacion = mx.idNotificacion.idNotificacion " +
                        " and mx.idTomaMx = dx.idTomaMx.idTomaMx " +
                        " and  noti.codUnidadAtencion.codigo =  uni.codigo " +
                        sqlRutina +(filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) + (!filtro.getCodLaboratio().equalsIgnoreCase("ALL")?sqlLab:"") +
                        " and noti.pasivo = false and dx.anulado = false and dx.controlCalidad = false " +
                        " and mx.anulada = false),0) as conresultado, " +
                        " coalesce( " +
                        " (select  sum(case dx.aprobada when false then 1 else 0 end) " +
                        " from DaNotificacion noti, DaTomaMx mx, DaSolicitudDx dx" +
                        " where noti.idNotificacion = mx.idNotificacion.idNotificacion " +
                        " and mx.idTomaMx = dx.idTomaMx.idTomaMx " +
                        sqlRutina +(filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) + (!filtro.getCodLaboratio().equalsIgnoreCase("ALL")?sqlLab:"") +
                        " and  noti.codUnidadAtencion.codigo =  uni.codigo " +
                        " and noti.pasivo = false and dx.anulado = false and dx.controlCalidad = false " +
                        " and mx.anulada = false),0) as sinresultado " +
                        "FROM Unidades uni " +
                        "where uni.unidadId = :codUnidad" +
                        " and uni.tipoUnidad in ("+ HealthUnitType.UnidadesPrimHosp.getDiscriminator()+") " +
                        " order by uni.unidadId ");

                queryIdNoti = session.createQuery(" select noti.codUnidadAtencion.unidadId, dx.idSolicitudDx, r.valor " +
                        ", coalesce((select rr.concepto.tipo.codigo from RespuestaSolicitud rr where rr.idRespuesta = r.respuesta.idRespuesta),'NULL')"+
                        ", coalesce((select rr.concepto.tipo.codigo from RespuestaExamen rr where rr.idRespuesta = r.respuestaExamen.idRespuesta),'NULL') "+
                        " from DaNotificacion noti, DaTomaMx mx, DaSolicitudDx dx, DetalleResultadoFinal r " +
                        " where noti.idNotificacion = mx.idNotificacion " +
                        sqlRutina +(filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) + (!filtro.getCodLaboratio().equalsIgnoreCase("ALL")?sqlLab:"") +
                        " and mx.idTomaMx = dx.idTomaMx.idTomaMx " +
                        " and dx.idSolicitudDx = r.solicitudDx.idSolicitudDx and r.pasivo = false " +
                        " and noti.pasivo = false and dx.anulado = false " +
                        " and mx.anulada = false " +
                        " and dx.aprobada = true and dx.controlCalidad = false " +
                        " and noti.codUnidadAtencion.unidadId = :codUnidad " +
                        " order by noti.codUnidadAtencion.unidadId ");
            }

            queryNotiDx.setParameter("codUnidad", filtro.getCodUnidad());

            //rutinas
            queryIdNoti.setParameter("idDx", filtro.getIdDx());
            queryIdNoti.setParameter("fechaInicio", filtro.getFechaInicio());
            queryIdNoti.setParameter("fechaFin", filtro.getFechaFin());
            queryIdNoti.setParameter("codUnidad", filtro.getCodUnidad());
            if (!filtro.getCodLaboratio().equalsIgnoreCase("ALL")) {
                queryNotiDx.setParameter("codigoLab", filtro.getCodLaboratio());
                queryIdNoti.setParameter("codigoLab", filtro.getCodLaboratio());
            }
            resTemp2.addAll(queryIdNoti.list());

        }

        queryNotiDx.setParameter("idDx", filtro.getIdDx());
        queryNotiDx.setParameter("fechaInicio", filtro.getFechaInicio());
        queryNotiDx.setParameter("fechaFin", filtro.getFechaFin());
        resTemp1.addAll(queryNotiDx.list());
        for (Object[] reg : resTemp1) {
            Object[] reg1 = new Object[cantidadColumnas];
            reg1[0] = reg[1]; //Nombre Silais
            //reg1[1] = reg[2]; //Cantidad Notificaciones (NO SE USA)
            reg1[1] = (Long) reg[2]; //Cantidad Dx //24072019
            if (!filtro.getCodArea().equals("AREAREP|MUNI") || (filtro.getCodArea().equals("AREAREP|MUNI") && (Long) reg[2]>0)) {
                //para dengue, chik y zika
                int pos = 0;
                int neg = 0;
                //para ifi
                int flua = 0, flub = 0, flursv = 0, fluadv = 0, flupiv1 = 0, flupiv2 = 0, flupiv3 = 0, flumpv = 0;
                int inadecuada = 0;
                int noProc = 0;
                String idSolicitud = "";
                for (Object[] sol : resTemp2) {
                    //identidad
                    if (sol[0].equals(reg[0]) && !sol[0].equals(idSolicitud)) {

                        if (!sol[3].toString().equalsIgnoreCase("NULL")) {
                            if (sol[3].toString().equalsIgnoreCase("TPDATO|LIST")) {
                                Integer idLista = Integer.valueOf(sol[2].toString());
                                Catalogo_Lista valor = null;
                                try {
                                    valor = respuestasExamenService.getCatalogoListaConceptoByIdLista(idLista);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (valor != null) {
                                    if (nombreDx.toLowerCase().contains("dengue") || nombreDx.toLowerCase().contains("chikun") || nombreDx.toLowerCase().contains("zika") ||
                                            (nombreDx.toLowerCase().contains("leptospi") && nombreDx.toLowerCase().contains("molec"))
                                            || (nombreDx.toLowerCase().contains("molecular virus respiratorio") || nombreDx.toLowerCase().contains("influenza"))) {
                                        if (valor.getValor().trim().toLowerCase().contains("negativo")
                                                /*|| valor.getValor().trim().toLowerCase().contains("no reactor")
                                                || valor.getValor().trim().toLowerCase().contains("no detectado")
                                                || valor.getValor().trim().toUpperCase().contains("MTB-ND")*/) {
                                            neg++;
                                            idSolicitud = sol[1].toString();
                                        } else if (valor.getValor().trim().toLowerCase().equals("mx inadecuada")) {
                                            inadecuada++;
                                            idSolicitud = sol[1].toString();
                                        } else if (valor.getValor().trim().toLowerCase().equals("no procesado")) {
                                            noProc++;
                                            idSolicitud = sol[1].toString();
                                        } else if (valor.getValor().trim().toLowerCase().contains("positivo")
                                                /*|| valor.getValor().trim().toLowerCase().contains("reactor")
                                                || valor.getValor().trim().toLowerCase().contains("detectado")
                                                || valor.getValor().trim().toUpperCase().contains("MTB-DET")*/
                                                || (!valor.getValor().trim().toLowerCase().contains("negativo") && !valor.getValor().trim().toLowerCase().contains("indetermin"))) {
                                            pos++;
                                            idSolicitud = sol[1].toString();
                                        }
                                    }else if (nombreDx.toLowerCase().contains("ifi virus respiratorio")){
                                        if (valor.getValor().trim().toLowerCase().contains("negativo")) {
                                            neg++;
                                            idSolicitud = sol[1].toString();
                                        } else if (valor.getValor().trim().toLowerCase().contains("influenza a") || valor.getValor().trim().toLowerCase().contains("flu a")){
                                            flua++;
                                            idSolicitud = sol[1].toString();
                                        } else if (valor.getValor().trim().toLowerCase().contains("influenza b") || valor.getValor().trim().toLowerCase().contains("flu b")){
                                            flub++;
                                            idSolicitud = sol[1].toString();
                                        } else if (valor.getValor().trim().toLowerCase().contains("rsv") || valor.getValor().trim().toLowerCase().contains("sincitial")){
                                            flursv++;
                                            idSolicitud = sol[1].toString();
                                        } else if (valor.getValor().trim().toLowerCase().contains("adenovirus") || valor.getValor().trim().toLowerCase().contains("adv")){
                                            fluadv++;
                                            idSolicitud = sol[1].toString();
                                        } else if (valor.getValor().trim().toLowerCase().contains("influenza 1") || valor.getValor().trim().toLowerCase().contains("influenza1")){
                                            flupiv1++;
                                            idSolicitud = sol[1].toString();
                                        } else if (valor.getValor().trim().toLowerCase().contains("influenza 2") || valor.getValor().trim().toLowerCase().contains("influenza2")){
                                            flupiv2++;
                                            idSolicitud = sol[1].toString();
                                        } else if (valor.getValor().trim().toLowerCase().contains("influenza 3") || valor.getValor().trim().toLowerCase().contains("influenza3")){
                                            flupiv3++;
                                            idSolicitud = sol[1].toString();
                                        } else if (valor.getValor().trim().toLowerCase().contains("metapneumovirus") || valor.getValor().trim().toLowerCase().contains("mpv")){
                                            flumpv++;
                                            idSolicitud = sol[1].toString();
                                        } else if (valor.getValor().trim().toLowerCase().contains("inadecuada") || valor.getValor().trim().toLowerCase().contains("inadecuado")) {
                                            inadecuada++;
                                            idSolicitud = sol[1].toString();
                                        }else if (valor.getValor().trim().toLowerCase().equals("no procesado")) {
                                            noProc++;
                                            idSolicitud = sol[1].toString();
                                        }
                                    } else if (nombreDx.toLowerCase().contains("leptospi") && nombreDx.toLowerCase().contains("serolog")){
                                        if (valor.getValor().trim().toLowerCase().contains("negativo")
                                                || valor.getValor().trim().toLowerCase().contains("no reactor")) {
                                            neg++;
                                            idSolicitud = sol[1].toString();
                                        } else if (valor.getValor().trim().toLowerCase().equals("mx inadecuada")) {
                                            inadecuada++;
                                            idSolicitud = sol[1].toString();
                                        } else if (valor.getValor().trim().toLowerCase().equals("no procesado")) {
                                            noProc++;
                                            idSolicitud = sol[1].toString();
                                        } else if (valor.getValor().trim().toLowerCase().contains("positivo")
                                                || valor.getValor().trim().toLowerCase().contains("reactor")
                                                || (!valor.getValor().trim().toLowerCase().contains("negativo") && !valor.getValor().trim().toLowerCase().contains("indetermin"))) {
                                            pos++;
                                            idSolicitud = sol[1].toString();
                                        }
                                    } else if (nombreDx.toLowerCase().contains("mycobacterium") && (nombreDx.toLowerCase().contains("tuberculosis") || nombreDx.contains("tb"))) {
                                        if (valor.getValor().trim().toUpperCase().contains("MTB-ND")) {
                                            neg++;
                                            idSolicitud = sol[1].toString();
                                        } else if (valor.getValor().trim().toLowerCase().equals("mx inadecuada")) {
                                            inadecuada++;
                                            idSolicitud = sol[1].toString();
                                        } else if (valor.getValor().trim().toLowerCase().equals("no procesado")) {
                                            noProc++;
                                            idSolicitud = sol[1].toString();
                                        } else if (valor.getValor().trim().toUpperCase().contains("MTB-DET")
                                                || (!valor.getValor().trim().toLowerCase().contains("negativo") && !valor.getValor().trim().toLowerCase().contains("indetermin"))) {
                                            pos++;
                                            idSolicitud = sol[1].toString();
                                        }
                                    }
                                }

                            } else if (sol[3].toString().equalsIgnoreCase("TPDATO|TXT")) {
                                if (nombreDx.toLowerCase().contains("dengue") || nombreDx.toLowerCase().contains("chikun") || nombreDx.toLowerCase().contains("zika") ||
                                        (nombreDx.toLowerCase().contains("leptospi") && nombreDx.toLowerCase().contains("molec"))
                                        || (nombreDx.toLowerCase().contains("molecular virus respiratorio") || nombreDx.toLowerCase().contains("influenza"))) {
                                    if (sol[2].toString().trim().toLowerCase().contains("negativo")
                                            /*|| sol[2].toString().trim().toLowerCase().contains("no reactor")
                                            || sol[2].toString().trim().toLowerCase().contains("no detectado")
                                            || sol[2].toString().trim().toUpperCase().contains("MTB-ND")*/) {
                                        neg++;
                                        idSolicitud = sol[1].toString();
                                    } else if (sol[2].toString().trim().toLowerCase().contains("mx inadecuada")) {
                                        inadecuada++;
                                        idSolicitud = sol[1].toString();
                                    } else if (sol[2].toString().trim().toLowerCase().equals("no procesado")) {
                                        noProc++;
                                        idSolicitud = sol[1].toString();
                                    } else if (sol[2].toString().trim().toLowerCase().contains("positivo")
                                            /*|| sol[2].toString().trim().toLowerCase().contains("reactor")
                                            || sol[2].toString().trim().toLowerCase().contains("detectado")
                                            || sol[2].toString().trim().toUpperCase().contains("MTB-DET")
                                            */|| (!sol[2].toString().trim().toLowerCase().contains("negativo") && !sol[2].toString().trim().toLowerCase().contains("indetermin"))) {
                                        pos++;
                                        idSolicitud = sol[1].toString();
                                    }
                                } else if (nombreDx.toLowerCase().contains("ifi virus respiratorio")){
                                    if (sol[2].toString().trim().toLowerCase().contains("negativo")) {
                                        neg++;
                                        idSolicitud = sol[1].toString();
                                    } else if (sol[2].toString().trim().toLowerCase().contains("influenza a") || sol[2].toString().trim().toLowerCase().contains("flu a")){
                                        flua++;
                                        idSolicitud = sol[1].toString();
                                    } else if (sol[2].toString().trim().toLowerCase().contains("influenza b") || sol[2].toString().trim().toLowerCase().contains("flu b")){
                                        flub++;
                                        idSolicitud = sol[1].toString();
                                    } else if (sol[2].toString().trim().toLowerCase().contains("rsv") || sol[2].toString().trim().toLowerCase().contains("sincitial")){
                                        flursv++;
                                        idSolicitud = sol[1].toString();
                                    } else if (sol[2].toString().trim().toLowerCase().contains("adenovirus") || sol[2].toString().trim().toLowerCase().contains("adv")){
                                        fluadv++;
                                        idSolicitud = sol[1].toString();
                                    } else if (sol[2].toString().trim().toLowerCase().contains("influenza 1") || sol[2].toString().trim().toLowerCase().contains("influenza1")){
                                        flupiv1++;
                                        idSolicitud = sol[1].toString();
                                    } else if (sol[2].toString().trim().toLowerCase().contains("influenza 2") || sol[2].toString().trim().toLowerCase().contains("influenza2")){
                                        flupiv2++;
                                        idSolicitud = sol[1].toString();
                                    } else if (sol[2].toString().trim().toLowerCase().contains("influenza 3") || sol[2].toString().trim().toLowerCase().contains("influenza3")){
                                        flupiv3++;
                                        idSolicitud = sol[1].toString();
                                    } else if (sol[2].toString().trim().toLowerCase().contains("metapneumovirus") || sol[2].toString().trim().toLowerCase().contains("mpv")){
                                        flumpv++;
                                        idSolicitud = sol[1].toString();
                                    } else if (sol[2].toString().trim().toLowerCase().contains("inadecuada") || sol[2].toString().trim().toLowerCase().contains("inadecuado")) {
                                        inadecuada++;
                                        idSolicitud = sol[1].toString();
                                    }else if (sol[2].toString().trim().toLowerCase().equals("no procesado")) {
                                        noProc++;
                                        idSolicitud = sol[1].toString();
                                    }
                                } else if (nombreDx.toLowerCase().contains("leptospi") && nombreDx.toLowerCase().contains("serolog")){
                                    if (sol[2].toString().trim().toLowerCase().contains("negativo")
                                            || sol[2].toString().trim().toLowerCase().contains("no reactor")) {
                                        neg++;
                                        idSolicitud = sol[1].toString();
                                    } else if (sol[2].toString().trim().toLowerCase().equals("mx inadecuada")) {
                                        inadecuada++;
                                        idSolicitud = sol[1].toString();
                                    } else if (sol[2].toString().trim().toLowerCase().equals("no procesado")) {
                                        noProc++;
                                        idSolicitud = sol[1].toString();
                                    } else if (sol[2].toString().trim().toLowerCase().contains("positivo")
                                            || sol[2].toString().trim().toLowerCase().contains("reactor")
                                            || (!sol[2].toString().trim().toLowerCase().contains("negativo") && !sol[2].toString().trim().toLowerCase().contains("indetermin"))) {
                                        pos++;
                                        idSolicitud = sol[1].toString();
                                    }
                                } else if (nombreDx.toLowerCase().contains("mycobacterium") && (nombreDx.toLowerCase().contains("tuberculosis") || nombreDx.contains("tb"))) {
                                    if (sol[2].toString().trim().toUpperCase().contains("MTB-ND")) {
                                        neg++;
                                        idSolicitud = sol[1].toString();
                                    } else if (sol[2].toString().trim().toLowerCase().equals("mx inadecuada")) {
                                        inadecuada++;
                                        idSolicitud = sol[1].toString();
                                    } else if (sol[2].toString().trim().toLowerCase().equals("no procesado")) {
                                        noProc++;
                                        idSolicitud = sol[1].toString();
                                    } else if (sol[2].toString().trim().toUpperCase().contains("MTB-DET")
                                            || (!sol[2].toString().trim().toLowerCase().contains("negativo") && !sol[2].toString().trim().toLowerCase().contains("indetermin"))) {
                                        pos++;
                                        idSolicitud = sol[1].toString();
                                    }
                                }
                            }
                        } else if (!sol[4].toString().equalsIgnoreCase("NULL")) {
                            if (sol[4].toString().equalsIgnoreCase("TPDATO|LIST")) {
                                Integer idLista = Integer.valueOf(sol[2].toString());
                                Catalogo_Lista valor = null;
                                try {
                                    valor = respuestasExamenService.getCatalogoListaConceptoByIdLista(idLista);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                if (valor != null) {
                                    if (nombreDx.toLowerCase().contains("dengue") || nombreDx.toLowerCase().contains("chikun") || nombreDx.toLowerCase().contains("zika") ||
                                            (nombreDx.toLowerCase().contains("leptospi") && nombreDx.toLowerCase().contains("molec"))
                                            || (nombreDx.toLowerCase().contains("molecular virus respiratorio") || nombreDx.toLowerCase().contains("influenza"))) {
                                        if (valor.getValor().trim().toLowerCase().contains("negativo")
                                                /*|| valor.getValor().trim().toLowerCase().contains("no reactor")
                                                || valor.getValor().trim().toLowerCase().contains("no detectado")
                                                || valor.getValor().trim().toUpperCase().contains("MTB-ND")*/) {
                                            neg++;
                                            idSolicitud = sol[1].toString();
                                        } else if (valor.getValor().trim().toLowerCase().equals("mx inadecuada")) {
                                            inadecuada++;
                                            idSolicitud = sol[1].toString();
                                        } else if (valor.getValor().trim().toLowerCase().equals("no procesado")) {
                                            noProc++;
                                            idSolicitud = sol[1].toString();
                                        } else if (valor.getValor().trim().toLowerCase().contains("positivo")
                                                /*|| valor.getValor().trim().toLowerCase().contains("reactor")
                                                || valor.getValor().trim().toLowerCase().contains("detectado")
                                                || valor.getValor().trim().toUpperCase().contains("MTB-DET")
                                                */|| (!valor.getValor().trim().toLowerCase().contains("negativo") && !valor.getValor().trim().toLowerCase().contains("indetermin"))) {
                                            pos++;
                                            idSolicitud = sol[1].toString();
                                        }
                                    } else if (nombreDx.toLowerCase().contains("ifi virus respiratorio")){
                                        if (valor.getValor().trim().toLowerCase().contains("negativo")) {
                                            neg++;
                                            idSolicitud = sol[1].toString();
                                        } else if (valor.getValor().trim().toLowerCase().contains("influenza a") || valor.getValor().trim().toLowerCase().contains("flu a")){
                                            flua++;
                                            idSolicitud = sol[1].toString();
                                        } else if (valor.getValor().trim().toLowerCase().contains("influenza b") || valor.getValor().trim().toLowerCase().contains("flu b")){
                                            flub++;
                                            idSolicitud = sol[1].toString();
                                        } else if (valor.getValor().trim().toLowerCase().contains("rsv") || valor.getValor().trim().toLowerCase().contains("sincitial")){
                                            flursv++;
                                            idSolicitud = sol[1].toString();
                                        } else if (valor.getValor().trim().toLowerCase().contains("adenovirus") || valor.getValor().trim().toLowerCase().contains("adv")){
                                            fluadv++;
                                            idSolicitud = sol[1].toString();
                                        } else if (valor.getValor().trim().toLowerCase().contains("influenza 1") || valor.getValor().trim().toLowerCase().contains("influenza1")){
                                            flupiv1++;
                                            idSolicitud = sol[1].toString();
                                        } else if (valor.getValor().trim().toLowerCase().contains("influenza 2") || valor.getValor().trim().toLowerCase().contains("influenza2")){
                                            flupiv2++;
                                            idSolicitud = sol[1].toString();
                                        } else if (valor.getValor().trim().toLowerCase().contains("influenza 3") || valor.getValor().trim().toLowerCase().contains("influenza3")){
                                            flupiv3++;
                                            idSolicitud = sol[1].toString();
                                        } else if (valor.getValor().trim().toLowerCase().contains("metapneumovirus") || valor.getValor().trim().toLowerCase().contains("mpv")){
                                            flumpv++;
                                            idSolicitud = sol[1].toString();
                                        } else if (valor.getValor().trim().toLowerCase().contains("inadecuada") || valor.getValor().trim().toLowerCase().contains("inadecuado")) {
                                            inadecuada++;
                                            idSolicitud = sol[1].toString();
                                        }else if (valor.getValor().trim().toLowerCase().equals("no procesado")) {
                                            noProc++;
                                            idSolicitud = sol[1].toString();
                                        }
                                    } else if (nombreDx.toLowerCase().contains("leptospi") && nombreDx.toLowerCase().contains("serolog")) {
                                        if (valor.getValor().trim().toLowerCase().contains("negativo")
                                                || valor.getValor().trim().toLowerCase().contains("no reactor")) {
                                            neg++;
                                            idSolicitud = sol[1].toString();
                                        } else if (valor.getValor().trim().toLowerCase().equals("mx inadecuada")) {
                                            inadecuada++;
                                            idSolicitud = sol[1].toString();
                                        } else if (valor.getValor().trim().toLowerCase().equals("no procesado")) {
                                            noProc++;
                                            idSolicitud = sol[1].toString();
                                        } else if (valor.getValor().trim().toLowerCase().contains("positivo")
                                                || valor.getValor().trim().toLowerCase().contains("reactor")
                                                || (!valor.getValor().trim().toLowerCase().contains("negativo") && !valor.getValor().trim().toLowerCase().contains("indetermin"))) {
                                            pos++;
                                            idSolicitud = sol[1].toString();
                                        }
                                    } else if (nombreDx.toLowerCase().contains("mycobacterium") && (nombreDx.toLowerCase().contains("tuberculosis") || nombreDx.contains("tb"))) {
                                        if (valor.getValor().trim().toUpperCase().contains("MTB-ND")) {
                                            neg++;
                                            idSolicitud = sol[1].toString();
                                        } else if (valor.getValor().trim().toLowerCase().equals("mx inadecuada")) {
                                            inadecuada++;
                                            idSolicitud = sol[1].toString();
                                        } else if (valor.getValor().trim().toLowerCase().equals("no procesado")) {
                                            noProc++;
                                            idSolicitud = sol[1].toString();
                                        } else if (valor.getValor().trim().toUpperCase().contains("MTB-DET")
                                                || (!valor.getValor().trim().toLowerCase().contains("negativo") && !valor.getValor().trim().toLowerCase().contains("indetermin"))) {
                                            pos++;
                                            idSolicitud = sol[1].toString();
                                        }
                                    }
                                }


                            } else if (sol[4].toString().equalsIgnoreCase("TPDATO|TXT")) {
                                if (nombreDx.toLowerCase().contains("dengue") || nombreDx.toLowerCase().contains("chikun") || nombreDx.toLowerCase().contains("zika") ||
                                        (nombreDx.toLowerCase().contains("leptospi") && nombreDx.toLowerCase().contains("molec"))
                                        || (nombreDx.toLowerCase().contains("molecular virus respiratorio") || nombreDx.toLowerCase().contains("influenza"))) {
                                    if (sol[2].toString().trim().toLowerCase().contains("negativo")
                                            /*|| sol[2].toString().trim().toLowerCase().contains("no reactor")
                                            || sol[2].toString().trim().toLowerCase().contains("no detectado")
                                            || sol[2].toString().trim().toUpperCase().contains("MTB-ND")*/) {
                                        neg++;
                                        idSolicitud = sol[1].toString();
                                    } else if (sol[2].toString().trim().toLowerCase().contains("mx inadecuada")) {
                                        inadecuada++;
                                        idSolicitud = sol[1].toString();
                                    } else if (sol[2].toString().trim().toLowerCase().equals("no procesado")) {
                                        noProc++;
                                        idSolicitud = sol[1].toString();
                                    } else if (sol[2].toString().trim().toLowerCase().contains("positivo")
                                            /*|| sol[2].toString().trim().toLowerCase().contains("reactor")
                                            || sol[2].toString().trim().toLowerCase().contains("detectado")
                                            || sol[2].toString().trim().toUpperCase().contains("MTB-DET")
                                            */|| (!sol[2].toString().trim().toLowerCase().contains("negativo") && !sol[2].toString().trim().toLowerCase().contains("indetermin"))) {
                                        pos++;
                                        idSolicitud = sol[1].toString();
                                    }
                                } else if (nombreDx.toLowerCase().contains("ifi virus respiratorio")){
                                    if (sol[2].toString().trim().toLowerCase().contains("negativo")) {
                                        neg++;
                                        idSolicitud = sol[1].toString();
                                    } else if (sol[2].toString().trim().toLowerCase().contains("influenza a") || sol[2].toString().trim().toLowerCase().contains("flu a")){
                                        flua++;
                                        idSolicitud = sol[1].toString();
                                    } else if (sol[2].toString().trim().toLowerCase().contains("influenza b") || sol[2].toString().trim().toLowerCase().contains("flu b")){
                                        flub++;
                                        idSolicitud = sol[1].toString();
                                    } else if (sol[2].toString().trim().toLowerCase().contains("rsv") || sol[2].toString().trim().toLowerCase().contains("sincitial")){
                                        flursv++;
                                        idSolicitud = sol[1].toString();
                                    } else if (sol[2].toString().trim().toLowerCase().contains("adenovirus") || sol[2].toString().trim().toLowerCase().contains("adv")){
                                        fluadv++;
                                        idSolicitud = sol[1].toString();
                                    } else if (sol[2].toString().trim().toLowerCase().contains("influenza 1") || sol[2].toString().trim().toLowerCase().contains("influenza1")){
                                        flupiv1++;
                                        idSolicitud = sol[1].toString();
                                    } else if (sol[2].toString().trim().toLowerCase().contains("influenza 2") || sol[2].toString().trim().toLowerCase().contains("influenza2")){
                                        flupiv2++;
                                        idSolicitud = sol[1].toString();
                                    } else if (sol[2].toString().trim().toLowerCase().contains("influenza 3") || sol[2].toString().trim().toLowerCase().contains("influenza3")){
                                        flupiv3++;
                                        idSolicitud = sol[1].toString();
                                    } else if (sol[2].toString().trim().toLowerCase().contains("metapneumovirus") || sol[2].toString().trim().toLowerCase().contains("mpv")){
                                        flumpv++;
                                        idSolicitud = sol[1].toString();
                                    } else if (sol[2].toString().trim().toLowerCase().contains("inadecuada") || sol[2].toString().trim().toLowerCase().contains("inadecuado")) {
                                        inadecuada++;
                                        idSolicitud = sol[1].toString();
                                    }else if (sol[2].toString().trim().toLowerCase().equals("no procesado")) {
                                        noProc++;
                                        idSolicitud = sol[1].toString();
                                    }
                                } else if (nombreDx.toLowerCase().contains("leptospi") && nombreDx.toLowerCase().contains("serolog")){
                                    if (sol[2].toString().trim().toLowerCase().contains("negativo")
                                            || sol[2].toString().trim().toLowerCase().contains("no reactor")) {
                                        neg++;
                                        idSolicitud = sol[1].toString();
                                    } else if (sol[2].toString().trim().toLowerCase().equals("mx inadecuada")) {
                                        inadecuada++;
                                        idSolicitud = sol[1].toString();
                                    } else if (sol[2].toString().trim().toLowerCase().equals("no procesado")) {
                                        noProc++;
                                        idSolicitud = sol[1].toString();
                                    } else if (sol[2].toString().trim().toLowerCase().contains("positivo")
                                            || sol[2].toString().trim().toLowerCase().contains("reactor")
                                            || (!sol[2].toString().trim().toLowerCase().contains("negativo") && !sol[2].toString().trim().toLowerCase().contains("indetermin"))) {
                                        pos++;
                                        idSolicitud = sol[1].toString();
                                    }
                                } else if (nombreDx.toLowerCase().contains("mycobacterium") && (nombreDx.toLowerCase().contains("tuberculosis") || nombreDx.contains("tb"))) {
                                    if (sol[2].toString().trim().toUpperCase().contains("MTB-ND")) {
                                        neg++;
                                        idSolicitud = sol[1].toString();
                                    } else if (sol[2].toString().trim().toLowerCase().equals("mx inadecuada")) {
                                        inadecuada++;
                                        idSolicitud = sol[1].toString();
                                    } else if (sol[2].toString().trim().toLowerCase().equals("no procesado")) {
                                        noProc++;
                                        idSolicitud = sol[1].toString();
                                    } else if (sol[2].toString().trim().toUpperCase().contains("MTB-DET")
                                            || (!sol[2].toString().trim().toLowerCase().contains("negativo") && !sol[2].toString().trim().toLowerCase().contains("indetermin"))) {
                                        pos++;
                                        idSolicitud = sol[1].toString();
                                    }
                                }
                            }
                        }
                    }
                }
                if (nombreDx.toLowerCase().contains("dengue") || nombreDx.toLowerCase().contains("chikun") || nombreDx.toLowerCase().contains("zika") ||
                        (nombreDx.toLowerCase().contains("leptospi") && nombreDx.toLowerCase().contains("molec"))
                        || (nombreDx.toLowerCase().contains("molecular virus respiratorio") || nombreDx.toLowerCase().contains("influenza"))) {
                    reg1[2] = pos; // Positivo
                    reg1[3] = neg; // Negativo
                    reg1[4] = (Long) reg[4]; // Sin Resultado dx
                    Long totalConySinResultado = (long) pos + (long) neg;//solo tomar en cuenta positivos y negativos. Andrea 24072019 //(Long) reg1[2];
                    reg1[7] = (totalConySinResultado != 0 ? (double) Math.round(Integer.valueOf(reg1[2].toString()).doubleValue() / totalConySinResultado * 100 * 100) / 100 : 0);
                    reg1[5] = inadecuada; //muestras inadecuadas
                    reg1[6] = noProc; //muestras con resultado no procesados
                    resFinal.add(reg1);
                }else if (nombreDx.toLowerCase().contains("ifi virus respiratorio")){
                    reg1[2] = flua; // flu a
                    reg1[3] = flub; // flu b
                    reg1[4] = flursv; // flu sincitial
                    reg1[5] = fluadv; // flu adenovirus
                    reg1[6] = flupiv1; // flu 1
                    reg1[7] = flupiv2; // flua 2
                    reg1[8] = flupiv3; // flua 3
                    reg1[9] = flumpv; // flua meta
                    reg1[10] = neg; // negativos
                    reg1[11] = (Long) reg[4]; // Sin Resultado dx
                    reg1[12] = inadecuada; //muestras inadecuadas
                    reg1[13] = noProc; //muestras con resultado no procesados
                    Long positivos = (long) flua + (long) flub + (long) flursv + (long) fluadv + (long) flupiv1 + (long) flupiv2 + (long) flupiv3 + (long) flumpv;
                    Long totalPosNeg = positivos + (long) neg;//solo tomar en cuenta positivos y negativos. Andrea 24072019 //(Long) reg1[2];
                    reg1[14] = (totalPosNeg != 0 ? (double) Math.round(positivos.doubleValue() / totalPosNeg * 100 * 100) / 100 : 0);
                    resFinal.add(reg1);
                }else if (nombreDx.toLowerCase().contains("molecular virus respiratorio")){ //TODO
                    reg1[2] = flua; // flua a
                    reg1[3] = flub; // flua b
                    reg1[4] = neg; // Negativo
                    reg1[5] = (Long) reg[4]; // Sin Resultado dx
                    reg1[6] = inadecuada; //muestras inadecuadas
                    reg1[7] = noProc; //muestras con resultado no procesados
                    Long positivos = (long) flua + (long) flub;
                    Long totalPosNeg = positivos + (long) neg;//solo tomar en cuenta positivos y negativos. Andrea 24072019 //(Long) reg1[2];
                    reg1[8] = (totalPosNeg != 0 ? (double) Math.round(positivos.doubleValue() / totalPosNeg * 100 * 100) / 100 : 0);
                    resFinal.add(reg1);
                }else if (nombreDx.toLowerCase().contains("leptospi") && nombreDx.toLowerCase().contains("serolog")){
                    reg1[2] = pos; // Reactor
                    reg1[3] = neg; // No Reactor
                    reg1[4] = (Long) reg[4]; // Sin Resultado dx
                    Long totalConySinResultado = (long) pos + (long) neg;//solo tomar en cuenta positivos y negativos. Andrea 24072019 //(Long) reg1[2];
                    reg1[7] = (totalConySinResultado != 0 ? (double) Math.round(Integer.valueOf(reg1[2].toString()).doubleValue() / totalConySinResultado * 100 * 100) / 100 : 0);
                    reg1[5] = inadecuada; //muestras inadecuadas
                    reg1[6] = noProc; //muestras con resultado no procesados
                    resFinal.add(reg1);
                } else if (nombreDx.toLowerCase().contains("mycobacterium") && (nombreDx.toLowerCase().contains("tuberculosis") || nombreDx.contains("tb"))) {
                    reg1[2] = pos; // MTB-DET
                    reg1[3] = neg; // MTB-ND
                    reg1[4] = (Long) reg[4]; // Sin Resultado dx
                    Long totalConySinResultado = (long) pos + (long) neg;//solo tomar en cuenta positivos y negativos. Andrea 24072019 //(Long) reg1[2];
                    reg1[7] = (totalConySinResultado != 0 ? (double) Math.round(Integer.valueOf(reg1[2].toString()).doubleValue() / totalConySinResultado * 100 * 100) / 100 : 0);
                    reg1[5] = inadecuada; //muestras inadecuadas
                    reg1[6] = noProc; //muestras con resultado no procesados
                    resFinal.add(reg1);
                }else {
                    reg1[2] = pos; // pos
                    reg1[3] = neg; // neg
                    reg1[4] = (Long) reg[4]; // Sin Resultado dx
                    Long totalConySinResultado = (long) pos + (long) neg;//solo tomar en cuenta positivos y negativos. Andrea 24072019 //(Long) reg1[2];
                    reg1[7] = (totalConySinResultado != 0 ? (double) Math.round(Integer.valueOf(reg1[2].toString()).doubleValue() / totalConySinResultado * 100 * 100) / 100 : 0);
                    reg1[5] = inadecuada; //muestras inadecuadas
                    reg1[6] = noProc; //muestras con resultado no procesados
                    resFinal.add(reg1);
                }
            }
        }
        return resFinal;
    }

    /**
     * M?todo que retornar la informaci?n para generar reporte y gr?fico de notificaciones por tipo de resultado (positivo, negativo, sin resultado y % positividad)
     * 04-09-2018 Andrea solocita que sea por fecha de aprobaci�n que se filtre el reporte
     * @param filtro indicando el nivel (pais, silais, departamento, municipio, unidad salud), tipo notificaci?n, rango de fechas, factor tasas de poblaci?n
     * @return Lista de objetos a mostrar
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> getDataEstResultReport(FiltrosReporte filtro) {
        // Retrieve session from Hibernate
        List<Object[]> resTemp1 = new ArrayList<Object[]>();
        List<Object[]> resTemp2 = new ArrayList<Object[]>();

        List<Object[]> resFinal = new ArrayList<Object[]>();
        Session session = sessionFactory.getCurrentSession();
        Query queryNotiDx = null;
        Query queryIdNoti = null;

        if (filtro.getCodArea().equals("AREAREP|PAIS")) {

            if (filtro.isPorSilais()) {
                queryNotiDx = session.createQuery(" select ent.codigo, ent.nombre, " + //TOTAL RUTINAS
                        " (select coalesce(sum(count(noti.idNotificacion)),0) from DaNotificacion noti, DaTomaMx mx, DaSolicitudEstudio dx " +
                        " where noti.idNotificacion = mx.idNotificacion.idNotificacion and noti.codSilaisAtencion.codigo = ent.codigo " +
                        " and mx.idTomaMx = dx.idTomaMx.idTomaMx and noti.pasivo = false and dx.anulado = false and mx.anulada = false " +
                        sqlEstudio + (filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) +
                        " group by noti.codSilaisAtencion.codigo) as dx, " +
                        " coalesce( " + //TOTAL RUTINAS CON RESULTADO
                        " (select sum(case dx.aprobada when true then 1 else 0 end) " +
                        " from DaNotificacion noti, DaTomaMx mx, DaSolicitudEstudio dx" +
                        " where noti.idNotificacion = mx.idNotificacion.idNotificacion " +
                        " and mx.idTomaMx = dx.idTomaMx.idTomaMx " +
                        " and  noti.codSilaisAtencion.codigo = ent.codigo " +
                        sqlEstudio + (filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) +
                        " and noti.pasivo = false and dx.anulado = false " +
                        " and mx.anulada = false),0) as conresultado, " +
                        " coalesce( " + //TOTAL RUTINAS SIN RESULTADO
                        " (select  sum(case dx.aprobada when false then 1 else 0 end) " +
                        " from DaNotificacion noti, DaTomaMx mx, DaSolicitudEstudio dx " +
                        " where noti.idNotificacion = mx.idNotificacion.idNotificacion " +
                        " and mx.idTomaMx = dx.idTomaMx.idTomaMx " +
                        sqlEstudio + (filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) +
                        " and  noti.codSilaisAtencion.codigo = ent.codigo " +
                        " and noti.pasivo = false and dx.anulado = false " +
                        " and mx.anulada = false),0) as sinresultado " +
                        " from EntidadesAdtvas ent " + (!filtro.isNivelCentral()?", EntidadAdtvaLaboratorio entlab ":"") +
                        " where ent.pasivo = 0 " + (!filtro.isNivelCentral()?" and ent.codigo = entlab.entidadAdtva.codigo and entlab.laboratorio.codigo = :laboratorio ":"") +
                        " order by ent.codigo ");

                queryIdNoti = session.createQuery(" select noti.codSilaisAtencion.codigo, dx.idSolicitudEstudio, r.valor " +
                        ", coalesce((select rr.concepto.tipo.codigo from RespuestaSolicitud rr where rr.idRespuesta = r.respuesta.idRespuesta),'NULL')"+
                        ", coalesce((select rr.concepto.tipo.codigo from RespuestaExamen rr where rr.idRespuesta = r.respuestaExamen.idRespuesta),'NULL') "+
                        " from DaNotificacion noti, DaTomaMx mx, DaSolicitudEstudio dx, DetalleResultadoFinal r " +
                        " where noti.idNotificacion = mx.idNotificacion " +
                        sqlEstudio + (filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) +
                        " and mx.idTomaMx = dx.idTomaMx.idTomaMx " +
                        " and dx.idSolicitudEstudio = r.solicitudEstudio.idSolicitudEstudio and r.pasivo = false " +
                        " and noti.pasivo = false and dx.anulado = false " +
                        " and mx.anulada = false " +
                        " and dx.aprobada = true " +
                        " and noti.codSilaisAtencion is not null " +
                        " order by noti.codSilaisAtencion.codigo");

            }else{
                queryNotiDx = session.createQuery(" select div.divisionpoliticaId, div.nombre, " +
                        " (select coalesce(sum(count(noti.idNotificacion)),0) from DaNotificacion noti, DaTomaMx mx, DaSolicitudEstudio dx" +
                        " where noti.idNotificacion = mx.idNotificacion.idNotificacion" +
                        " and noti.codUnidadAtencion.municipio.dependencia.divisionpoliticaId = div.divisionpoliticaId " +
                        " and mx.idTomaMx = dx.idTomaMx.idTomaMx and noti.pasivo = false and dx.anulado = false and mx.anulada = false " +
                        sqlEstudio + (filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) +
                        " group by noti.codUnidadAtencion.municipio.dependencia.divisionpoliticaId) as dx, " +
                        " coalesce( " +
                        " (select sum(case dx.aprobada when true then 1 else 0 end) " +
                        " from DaNotificacion noti, DaTomaMx mx, DaSolicitudEstudio dx " +
                        " where noti.idNotificacion = mx.idNotificacion.idNotificacion " +
                        " and mx.idTomaMx = dx.idTomaMx.idTomaMx " +
                        " and  noti.codUnidadAtencion.municipio.dependencia.divisionpoliticaId = div.divisionpoliticaId " +
                        sqlEstudio + (filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) +
                        " and noti.pasivo = false and dx.anulado = false " +
                        " and mx.anulada = false),0) as conresultado, " +
                        " coalesce( " +
                        " (select  sum(case dx.aprobada when false then 1 else 0 end) " +
                        " from DaNotificacion noti, DaTomaMx mx, DaSolicitudEstudio dx " +
                        " where noti.idNotificacion = mx.idNotificacion.idNotificacion " +
                        " and mx.idTomaMx = dx.idTomaMx.idTomaMx " +
                        sqlEstudio + (filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) +
                        " and  noti.codUnidadAtencion.municipio.dependencia.divisionpoliticaId = div.divisionpoliticaId " +
                        " and noti.pasivo = false and dx.anulado = false " +
                        " and mx.anulada = false),0) as sinresultado " +
                        " from Divisionpolitica div " +
                        "where div.dependencia is null and div.pasivo = '0'" +
                        " order by div.divisionpoliticaId ");

                queryIdNoti = session.createQuery(" select noti.codUnidadAtencion.municipio.dependencia.divisionpoliticaId, dx.idSolicitudEstudio, r.valor " +
                        ", coalesce((select rr.concepto.tipo.codigo from RespuestaSolicitud rr where rr.idRespuesta = r.respuesta.idRespuesta),'NULL')"+
                        ", coalesce((select rr.concepto.tipo.codigo from RespuestaExamen rr where rr.idRespuesta = r.respuestaExamen.idRespuesta),'NULL') "+
                        " from DaNotificacion noti, DaTomaMx mx, DaSolicitudEstudio dx, DetalleResultadoFinal r " +
                        " where noti.idNotificacion = mx.idNotificacion " +
                        sqlEstudio + (filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) +
                        " and mx.idTomaMx = dx.idTomaMx.idTomaMx " +
                        " and dx.idSolicitudEstudio = r.solicitudEstudio.idSolicitudEstudio and r.pasivo = false " +
                        " and noti.pasivo = false and dx.anulado = false " +
                        " and mx.anulada = false " +
                        " and dx.aprobada = true " +
                        " and noti.codUnidadAtencion is not null " +
                        " order by noti.codUnidadAtencion.municipio.dependencia.divisionpoliticaId");

            }

            //rutinas
            queryIdNoti.setParameter("idDx", filtro.getIdEstudio());
            queryIdNoti.setParameter("fechaInicio", filtro.getFechaInicio());
            queryIdNoti.setParameter("fechaFin", filtro.getFechaFin());

            resTemp2.addAll(queryIdNoti.list());

        } else if (filtro.getCodArea().equals("AREAREP|SILAIS")) {

            queryNotiDx = session.createQuery(" select distinct div.divisionpoliticaId, div.nombre, " +
                    " (select coalesce(sum(count(noti.idNotificacion)),0) from DaNotificacion noti, DaTomaMx mx, DaSolicitudEstudio dx " +
                    " where noti.idNotificacion = mx.idNotificacion.idNotificacion" +
                    " and noti.codUnidadAtencion.municipio.divisionpoliticaId = div.divisionpoliticaId " +
                    " and mx.idTomaMx = dx.idTomaMx.idTomaMx and noti.pasivo = false and dx.anulado = false and mx.anulada = false " +
                    sqlEstudio + (filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) +
                    " group by noti.codSilaisAtencion.entidadAdtvaId), " +
                    " coalesce( " +
                    " (select sum(case dx.aprobada when true then 1 else 0 end) " +
                    " from DaNotificacion noti, DaTomaMx mx, DaSolicitudEstudio dx " +
                    " where noti.idNotificacion = mx.idNotificacion.idNotificacion " +
                    " and mx.idTomaMx = dx.idTomaMx.idTomaMx " +
                    " and noti.codUnidadAtencion.municipio.divisionpoliticaId = div.divisionpoliticaId " +
                    sqlEstudio +(filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) +
                    " and noti.pasivo = false and dx.anulado = false " +
                    " and mx.anulada = false),0) as conresultado, " +
                    " coalesce( " +
                    " (select  sum(case dx.aprobada when false then 1 else 0 end) " +
                    " from DaNotificacion noti, DaTomaMx mx, DaSolicitudEstudio dx " +
                    " where noti.idNotificacion = mx.idNotificacion.idNotificacion " +
                    " and mx.idTomaMx = dx.idTomaMx.idTomaMx " +
                    " and noti.codUnidadAtencion.municipio.divisionpoliticaId = div.divisionpoliticaId " +
                    sqlEstudio +(filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) +
                    " and noti.pasivo = false and dx.anulado = false " +
                    " and mx.anulada = false),0) as sinresultado " +
                    " from Divisionpolitica div, Unidades as uni " +
                    " where div.pasivo = '0' and uni.pasivo='0' " +
                    " and uni.municipio.codigoNacional = div.codigoNacional and uni.entidadAdtva.codigo = :codSilais " +
                    " order by div.divisionpoliticaId ");

            queryIdNoti = session.createQuery(" select noti.codUnidadAtencion.municipio.divisionpoliticaId, dx.idSolicitudEstudio, r.valor " +
                    ", coalesce((select rr.concepto.tipo.codigo from RespuestaSolicitud rr where rr.idRespuesta = r.respuesta.idRespuesta),'NULL')"+
                    ", coalesce((select rr.concepto.tipo.codigo from RespuestaExamen rr where rr.idRespuesta = r.respuestaExamen.idRespuesta),'NULL') "+
                    " from DaNotificacion noti, DaTomaMx mx, DaSolicitudEstudio dx, DetalleResultadoFinal r " +
                    " where noti.idNotificacion = mx.idNotificacion " +
                    sqlEstudio +(filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) +
                    " and mx.idTomaMx = dx.idTomaMx.idTomaMx " +
                    " and dx.idSolicitudEstudio = r.solicitudEstudio.idSolicitudEstudio and r.pasivo = false " +
                    " and noti.pasivo = false and dx.anulado = false " +
                    " and mx.anulada = false " +
                    " and dx.aprobada = true " +
                    " and noti.codUnidadAtencion.entidadAdtva.codigo = :codSilais " +
                    " order by noti.codUnidadAtencion.municipio.divisionpoliticaId ");

            queryNotiDx.setParameter("codSilais", filtro.getCodSilais());

            //rutinas
            queryIdNoti.setParameter("idDx", filtro.getIdEstudio());
            queryIdNoti.setParameter("fechaInicio", filtro.getFechaInicio());
            queryIdNoti.setParameter("fechaFin", filtro.getFechaFin());
            queryIdNoti.setParameter("codSilais", filtro.getCodSilais());

            resTemp2.addAll(queryIdNoti.list());

        } else if (filtro.getCodArea().equals("AREAREP|MUNI")) {
            queryNotiDx = session.createQuery(" select uni.unidadId, uni.nombre, " +
                    " (select coalesce(sum(count(noti.idNotificacion)),0) from DaNotificacion noti, DaTomaMx mx, DaSolicitudEstudio dx " +
                    " where noti.idNotificacion = mx.idNotificacion.idNotificacion" +
                    " and noti.codUnidadAtencion.codigo =  uni.codigo " +
                    " and mx.idTomaMx = dx.idTomaMx.idTomaMx and noti.pasivo = false and dx.anulado = false and mx.anulada = false " +
                    sqlEstudio +(filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) +
                    " group by  noti.codUnidadAtencion.unidadId) as dx, " +
                    " coalesce( " +
                    " (select sum(case dx.aprobada when true then 1 else 0 end) " +
                    " from DaNotificacion noti, DaTomaMx mx, DaSolicitudEstudio dx " +
                    " where noti.idNotificacion = mx.idNotificacion.idNotificacion " +
                    " and mx.idTomaMx = dx.idTomaMx.idTomaMx " +
                    " and  noti.codUnidadAtencion.codigo =  uni.codigo " +
                    sqlEstudio +(filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) +
                    " and noti.pasivo = false and dx.anulado = false " +
                    " and mx.anulada = false),0) as conresultado, " +
                    " coalesce( " +
                    " (select  sum(case dx.aprobada when false then 1 else 0 end) " +
                    " from DaNotificacion noti, DaTomaMx mx, DaSolicitudEstudio dx " +
                    " where noti.idNotificacion = mx.idNotificacion.idNotificacion " +
                    " and mx.idTomaMx = dx.idTomaMx.idTomaMx " +
                    sqlEstudio +(filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) +
                    " and  noti.codUnidadAtencion.codigo =  uni.codigo " +
                    " and noti.pasivo = false and dx.anulado = false " +
                    " and mx.anulada = false),0) as sinresultado " +
                    "FROM Unidades uni " +
                    "where uni.municipio.codigoNacional = :codMunicipio" +
                    " and uni.entidadAdtva.codigo = :codSilais" +
                    " and uni.tipoUnidad in ("+ HealthUnitType.UnidadesPrimHosp.getDiscriminator()+") " +
                    " order by uni.unidadId ");


            queryIdNoti = session.createQuery(" select noti.codUnidadAtencion.unidadId, dx.idSolicitudEstudio, r.valor " +
                    ", coalesce((select rr.concepto.tipo.codigo from RespuestaSolicitud rr where rr.idRespuesta = r.respuesta.idRespuesta),'NULL')"+
                    ", coalesce((select rr.concepto.tipo.codigo from RespuestaExamen rr where rr.idRespuesta = r.respuestaExamen.idRespuesta),'NULL') "+
                    " from DaNotificacion noti, DaTomaMx mx, DaSolicitudEstudio dx, DetalleResultadoFinal r  " +
                    " where noti.idNotificacion = mx.idNotificacion " +
                    sqlEstudio +(filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) +
                    " and mx.idTomaMx = dx.idTomaMx.idTomaMx " +
                    " and dx.idSolicitudEstudio = r.solicitudEstudio.idSolicitudEstudio and r.pasivo = false " +
                    " and noti.pasivo = false and dx.anulado = false " +
                    " and mx.anulada = false " +
                    " and dx.aprobada = true " +
                    " and noti.codUnidadAtencion.municipio.codigoNacional = :codMunicipio " +
                    " and noti.codUnidadAtencion.entidadAdtva.codigo = :codSilais " +
                    " order by noti.codUnidadAtencion.unidadId ");

            queryNotiDx.setParameter("codMunicipio", String.valueOf(filtro.getCodMunicipio()));
            queryNotiDx.setParameter("codSilais", filtro.getCodSilais());

            //rutinas
            queryIdNoti.setParameter("idDx", filtro.getIdEstudio());
            queryIdNoti.setParameter("fechaInicio", filtro.getFechaInicio());
            queryIdNoti.setParameter("fechaFin", filtro.getFechaFin());
            queryIdNoti.setParameter("codMunicipio", String.valueOf(filtro.getCodMunicipio()));
            queryIdNoti.setParameter("codSilais", filtro.getCodSilais());

            resTemp2.addAll(queryIdNoti.list());

        } else if (filtro.getCodArea().equals("AREAREP|UNI")) {

            if(filtro.isSubunidades()){
                queryNotiDx = session.createQuery(" select uni.unidadId, uni.nombre, " +
                        " (select coalesce(sum(count(noti.idNotificacion)),0) from DaNotificacion noti, DaTomaMx mx, DaSolicitudEstudio dx" +
                        " where noti.idNotificacion = mx.idNotificacion.idNotificacion" +
                        " and noti.codUnidadAtencion.codigo =  uni.codigo " +
                        " and mx.idTomaMx = dx.idTomaMx.idTomaMx and noti.pasivo = false and dx.anulado = false and mx.anulada = false " +
                        sqlEstudio +(filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) +
                        " group by  noti.codUnidadAtencion.unidadId) as dx, " +
                        " coalesce( " +
                        " (select sum(case dx.aprobada when true then 1 else 0 end) " +
                        " from DaNotificacion noti, DaTomaMx mx, DaSolicitudEstudio dx " +
                        " where noti.idNotificacion = mx.idNotificacion.idNotificacion " +
                        " and mx.idTomaMx = dx.idTomaMx.idTomaMx " +
                        " and  noti.codUnidadAtencion.codigo =  uni.codigo " +
                        sqlEstudio +(filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) +
                        " and noti.pasivo = false and dx.anulado = false " +
                        " and mx.anulada = false),0) as conresultado, " +
                        " coalesce( " +
                        " (select  sum(case dx.aprobada when false then 1 else 0 end) " +
                        " from DaNotificacion noti, DaTomaMx mx, DaSolicitudEstudio dx " +
                        " where noti.idNotificacion = mx.idNotificacion.idNotificacion " +
                        " and mx.idTomaMx = dx.idTomaMx.idTomaMx " +
                        sqlEstudio +(filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) +
                        " and  noti.codUnidadAtencion.codigo =  uni.codigo " +
                        " and noti.pasivo = false and dx.anulado = false " +
                        " and mx.anulada = false),0) as sinresultado " +
                        "FROM Unidades uni " +
                        "where (uni.unidadId = :codUnidad" +
                        " or uni.unidadAdtva in (select u.codigo from Unidades u where u.unidadId = :codUnidad )) " +
                        " and uni.tipoUnidad in ("+ HealthUnitType.UnidadesPrimHosp.getDiscriminator()+") " +
                        " order by uni.unidadId ");

                queryIdNoti = session.createQuery(" select noti.codUnidadAtencion.unidadId, dx.idSolicitudEstudio, r.valor " +
                        ", coalesce((select rr.concepto.tipo.codigo from RespuestaSolicitud rr where rr.idRespuesta = r.respuesta.idRespuesta),'NULL')"+
                        ", coalesce((select rr.concepto.tipo.codigo from RespuestaExamen rr where rr.idRespuesta = r.respuestaExamen.idRespuesta),'NULL') "+
                        " from DaNotificacion noti, DaTomaMx mx, DaSolicitudEstudio dx, DetalleResultadoFinal r " +
                        " where noti.idNotificacion = mx.idNotificacion " +
                        sqlEstudio + (filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) +
                        " and mx.idTomaMx = dx.idTomaMx.idTomaMx " +
                        " and dx.idSolicitudEstudio = r.solicitudEstudio.idSolicitudEstudio and r.pasivo = false " +
                        " and noti.pasivo = false and dx.anulado = false " +
                        " and mx.anulada = false " +
                        " and dx.aprobada = true " +
                        " and (noti.codUnidadAtencion.unidadId = :codUnidad " +
                        " or noti.codUnidadAtencion.unidadAdtva in (select u.codigo from Unidades u where u.unidadId = :codUnidad )) " +
                        " order by noti.codUnidadAtencion.unidadId ");

            }else{
                queryNotiDx = session.createQuery(" select uni.unidadId, uni.nombre, " +
                        " (select coalesce(sum(count(noti.idNotificacion)),0) from DaNotificacion noti, DaTomaMx mx, DaSolicitudEstudio dx " +
                        " where noti.idNotificacion = mx.idNotificacion.idNotificacion" +
                        " and noti.codUnidadAtencion.codigo =  uni.codigo " +
                        " and mx.idTomaMx = dx.idTomaMx.idTomaMx and noti.pasivo = false and dx.anulado = false and mx.anulada = false " +
                        sqlEstudio +(filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) +
                        " group by  noti.codUnidadAtencion.unidadId) as dx, " +
                        " coalesce( " +
                        " (select sum(case dx.aprobada when true then 1 else 0 end) " +
                        " from DaNotificacion noti, DaTomaMx mx, DaSolicitudEstudio dx" +
                        " where noti.idNotificacion = mx.idNotificacion.idNotificacion " +
                        " and mx.idTomaMx = dx.idTomaMx.idTomaMx " +
                        " and  noti.codUnidadAtencion.codigo =  uni.codigo " +
                        sqlEstudio +(filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) +
                        " and noti.pasivo = false and dx.anulado = false " +
                        " and mx.anulada = false),0) as conresultado, " +
                        " coalesce( " +
                        " (select  sum(case dx.aprobada when false then 1 else 0 end) " +
                        " from DaNotificacion noti, DaTomaMx mx, DaSolicitudEstudio dx" +
                        " where noti.idNotificacion = mx.idNotificacion.idNotificacion " +
                        " and mx.idTomaMx = dx.idTomaMx.idTomaMx " +
                        sqlEstudio +(filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) +
                        " and  noti.codUnidadAtencion.codigo =  uni.codigo " +
                        " and noti.pasivo = false and dx.anulado = false " +
                        " and mx.anulada = false),0) as sinresultado " +
                        "FROM Unidades uni " +
                        "where uni.unidadId = :codUnidad" +
                        " and uni.tipoUnidad in ("+ HealthUnitType.UnidadesPrimHosp.getDiscriminator()+") " +
                        " order by uni.unidadId ");

                queryIdNoti = session.createQuery(" select noti.codUnidadAtencion.unidadId, dx.idSolicitudEstudio, r.valor " +
                        ", coalesce((select rr.concepto.tipo.codigo from RespuestaSolicitud rr where rr.idRespuesta = r.respuesta.idRespuesta),'NULL')"+
                        ", coalesce((select rr.concepto.tipo.codigo from RespuestaExamen rr where rr.idRespuesta = r.respuestaExamen.idRespuesta),'NULL') "+
                        " from DaNotificacion noti, DaTomaMx mx, DaSolicitudEstudio dx, DetalleResultadoFinal r " +
                        " where noti.idNotificacion = mx.idNotificacion " +
                        sqlEstudio +(filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) +
                        " and mx.idTomaMx = dx.idTomaMx.idTomaMx " +
                        " and dx.idSolicitudEstudio = r.solicitudEstudio.idSolicitudEstudio and r.pasivo = false " +
                        " and noti.pasivo = false and dx.anulado = false " +
                        " and mx.anulada = false " +
                        " and dx.aprobada = true " +
                        " and noti.codUnidadAtencion.unidadId = :codUnidad " +
                        " order by noti.codUnidadAtencion.unidadId ");
            }

            queryNotiDx.setParameter("codUnidad", filtro.getCodUnidad());

            //rutinas
            queryIdNoti.setParameter("idDx", filtro.getIdEstudio());
            queryIdNoti.setParameter("fechaInicio", filtro.getFechaInicio());
            queryIdNoti.setParameter("fechaFin", filtro.getFechaFin());
            queryIdNoti.setParameter("codUnidad", filtro.getCodUnidad());

            resTemp2.addAll(queryIdNoti.list());

        }

        queryNotiDx.setParameter("idDx", filtro.getIdEstudio());
        queryNotiDx.setParameter("fechaInicio", filtro.getFechaInicio());
        queryNotiDx.setParameter("fechaFin", filtro.getFechaFin());
        resTemp1.addAll(queryNotiDx.list());
        for (Object[] reg : resTemp1) {
            Object[] reg1 = new Object[8];
            reg1[0] = reg[1]; //Nombre Silais
            reg1[1] = reg[2]; //Cantidad Notificaciones (NO SE USA)
            reg1[2] = (Long) reg[2]; //Cantidad Dx
            if (!filtro.getCodArea().equals("AREAREP|MUNI") || (filtro.getCodArea().equals("AREAREP|MUNI") && (Long) reg[2]>0)) {
                int pos = 0;
                int neg = 0;
                int inadecuada = 0;
                String idSolicitud = "";
                for (Object[] sol : resTemp2) {
                    //identidad
                    if (sol[0].equals(reg[0]) && !sol[0].equals(idSolicitud)) {

                        if (!sol[3].toString().equalsIgnoreCase("NULL")) {
                            if (sol[3].toString().equalsIgnoreCase("TPDATO|LIST")) {
                                Integer idLista = Integer.valueOf(sol[2].toString());
                                Catalogo_Lista valor = null;
                                try {
                                    valor = respuestasExamenService.getCatalogoListaConceptoByIdLista(idLista);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (valor != null) {
                                    if (valor.getValor().trim().toLowerCase().contains("negativo")
                                            || valor.getValor().trim().toLowerCase().contains("no reactor")
                                            || valor.getValor().trim().toLowerCase().contains("no detectado")
                                            || valor.getValor().trim().toUpperCase().contains("MTB-ND")) {
                                        neg++;
                                        idSolicitud = sol[1].toString();
                                    } else if (valor.getValor().trim().toLowerCase().equals("mx inadecuada")){
                                        inadecuada++;
                                        idSolicitud = sol[1].toString();
                                    } else if (valor.getValor().trim().toLowerCase().contains("positivo")
                                            || valor.getValor().trim().toLowerCase().contains("reactor")
                                            || valor.getValor().trim().toLowerCase().contains("detectado")
                                            || valor.getValor().trim().toUpperCase().contains("MTB-DET")
                                            || (!valor.getValor().trim().toLowerCase().contains("negativo") && !valor.getValor().trim().toLowerCase().contains("indetermin"))) {
                                        pos++;
                                        idSolicitud = sol[1].toString();
                                    }
                                }

                            } else if (sol[3].toString().equalsIgnoreCase("TPDATO|TXT")) {
                                if (sol[2].toString().trim().toLowerCase().contains("negativo")
                                        || sol[2].toString().trim().toLowerCase().contains("no reactor")
                                        || sol[2].toString().trim().toLowerCase().contains("no detectado")
                                        || sol[2].toString().trim().toUpperCase().contains("MTB-ND")) {
                                    neg++;
                                    idSolicitud = sol[1].toString();
                                } else if (sol[2].toString().trim().toLowerCase().contains("mx inadecuada")) {
                                    inadecuada++;
                                    idSolicitud = sol[1].toString();
                                } else if (sol[2].toString().trim().toLowerCase().contains("positivo")
                                        || sol[2].toString().trim().toLowerCase().contains("reactor")
                                        || sol[2].toString().trim().toLowerCase().contains("detectado")
                                        || sol[2].toString().trim().toUpperCase().contains("MTB-DET")
                                        || (!sol[2].toString().trim().toLowerCase().contains("negativo") && !sol[2].toString().trim().toLowerCase().contains("indetermin"))) {
                                    pos++;
                                    idSolicitud = sol[1].toString();
                                }
                            }
                        } else if (!sol[4].toString().equalsIgnoreCase("NULL")) {
                            if (sol[4].toString().equalsIgnoreCase("TPDATO|LIST")) {
                                Integer idLista = Integer.valueOf(sol[2].toString());
                                Catalogo_Lista valor = null;
                                try {
                                    valor = respuestasExamenService.getCatalogoListaConceptoByIdLista(idLista);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                if (valor != null) {
                                    if (valor.getValor().trim().toLowerCase().contains("negativo")
                                            || valor.getValor().trim().toLowerCase().contains("no reactor")
                                            || valor.getValor().trim().toLowerCase().contains("no detectado")
                                            || valor.getValor().trim().toUpperCase().contains("MTB-ND")) {
                                        neg++;
                                        idSolicitud = sol[1].toString();
                                    } else if (valor.getValor().trim().toLowerCase().equals("mx inadecuada")){
                                        inadecuada++;
                                        idSolicitud = sol[1].toString();
                                    } else if (valor.getValor().trim().toLowerCase().contains("positivo")
                                            || valor.getValor().trim().toLowerCase().contains("reactor")
                                            || valor.getValor().trim().toLowerCase().contains("detectado")
                                            || valor.getValor().trim().toUpperCase().contains("MTB-DET")
                                            || (!valor.getValor().trim().toLowerCase().contains("negativo") && !valor.getValor().trim().toLowerCase().contains("indetermin"))) {
                                        pos++;
                                        idSolicitud = sol[1].toString();
                                    }
                                }


                            } else if (sol[4].toString().equalsIgnoreCase("TPDATO|TXT")) {
                                if (sol[2].toString().trim().toLowerCase().contains("negativo")
                                        || sol[2].toString().trim().toLowerCase().contains("no reactor")
                                        || sol[2].toString().trim().toLowerCase().contains("no detectado")
                                        || sol[2].toString().trim().toUpperCase().contains("MTB-ND")) {
                                    neg++;
                                    idSolicitud = sol[1].toString();
                                } else if (sol[2].toString().trim().toLowerCase().contains("mx inadecuada")) {
                                    inadecuada++;
                                    idSolicitud = sol[1].toString();
                                } else if (sol[2].toString().trim().toLowerCase().contains("positivo")
                                        || sol[2].toString().trim().toLowerCase().contains("reactor")
                                        || sol[2].toString().trim().toLowerCase().contains("detectado")
                                        || sol[2].toString().trim().toUpperCase().contains("MTB-DET")
                                        || (!sol[2].toString().trim().toLowerCase().contains("negativo") && !sol[2].toString().trim().toLowerCase().contains("indetermin"))) {
                                    pos++;
                                    idSolicitud = sol[1].toString();
                                }

                            }
                        }
                    }
                }

                reg1[3] = pos; // Positivo
                reg1[4] = neg; // Negativo
                reg1[5] = (Long) reg[4]; // Sin Resultado dx
                Long totalConySinResultado = (Long) reg1[2];
                reg1[6] = (totalConySinResultado != 0 ? (double) Math.round(Integer.valueOf(reg1[3].toString()).doubleValue() / totalConySinResultado * 100 * 100) / 100 : 0);
                reg1[7] = inadecuada; //muestras inadecuadas
                resFinal.add(reg1);
            }
        }
        return resFinal;
    }

    public List<ResultadoVigilancia> getEstudiosAprobadosByFiltroV2(FiltrosReporte filtro){
        Session session = sessionFactory.getCurrentSession();
        Query queryNotiDx = null;
        if (filtro.getCodArea().equals("AREAREP|PAIS")) {
            queryNotiDx = session.createQuery(" select cast(p.personaId as string) as codigoExpUnico, p.primerNombre as primerNombre, p.segundoNombre as segundoNombre, p.primerApellido as primerApellido, p.segundoApellido as segundoApellido, p.fechaNacimiento as fechaNacimiento, p.sexo.codigo as sexo, " +
                    " p.direccionResidencia as direccionResidencia, p.telefonoResidencia as telefonoResidencia, p.telefonoMovil as telefonoMovil, coalesce((select co.nombre from  Comunidades co where co.codigo=p.comunidadResidencia.codigo), null) as comunidadResidencia, " +
                    " noti.idNotificacion as idNotificacion, noti.semanasEmbarazo as semanasEmbarazo, noti.fechaInicioSintomas as fechaInicioSintomas, coalesce((select r.valor from Respuesta r where r.codigo = noti.urgente.codigo), null) as urgente,  coalesce((select r.valor from Respuesta r where r.codigo = noti.embarazada.codigo), null) as embarazada, " +
                    " coalesce((select ea.codigo from EntidadesAdtvas ea where ea.codigo = noti.codSilaisAtencion.codigo ), null) as codigoSilaisNoti, coalesce((select ea.nombre from EntidadesAdtvas ea where ea.codigo = noti.codSilaisAtencion.codigo ), null) as nombreSilaisNoti, " +
                    " coalesce((select u.codigo from Unidades u where u.codigo = noti.codUnidadAtencion.codigo), null) as codigoUnidadNoti, coalesce((select u.nombre from Unidades u where u.codigo = noti.codUnidadAtencion.codigo), null) as nombreUnidadNoti, " +
                    " coalesce((select u.codigoNacional from Divisionpolitica u where u.codigoNacional = noti.codUnidadAtencion.municipio.codigoNacional), null) as codigoMuniNoti, coalesce((select u.nombre from Divisionpolitica u where u.codigoNacional = noti.codUnidadAtencion.municipio.codigoNacional), null) as nombreMuniNoti, " +
                    " coalesce((select u.codigoNacional from Divisionpolitica u where u.codigoNacional = noti.municipioResidencia.codigoNacional), null) as codigoMuniResid, coalesce((select u.nombre from Divisionpolitica u where u.codigoNacional = noti.municipioResidencia.codigoNacional), null) as nombreMuniResid, " +
                    " coalesce((select ea.codigo from EntidadesAdtvas ea where ea.codigo = noti.municipioResidencia.dependenciaSilais.codigo ), null) as codigoSilaisResid, coalesce((select ea.nombre from EntidadesAdtvas ea where ea.codigo = noti.municipioResidencia.dependenciaSilais.codigo ), null) as nombreSilaisResid, " +
                    " coalesce((select ea.codigo from EntidadesAdtvas ea where ea.codigo = mx.codSilaisAtencion.codigo ), null) as codigoSilaisMx, coalesce((select ea.nombre from EntidadesAdtvas ea where ea.codigo = mx.codSilaisAtencion.codigo ), null) as nombreSilaisMx, " +
                    " coalesce((select u.codigo from Unidades u where u.codigo = mx.codUnidadAtencion.codigo), null) as codigoUnidadMx, coalesce((select u.nombre from Unidades u where u.codigo = mx.codUnidadAtencion.codigo), null) as nombreUnidadMx, " +
                    " coalesce((select u.codigoNacional from Divisionpolitica u where u.codigoNacional = mx.codUnidadAtencion.municipio.codigoNacional), null) as codigoMuniMx, coalesce((select u.nombre from Divisionpolitica u where u.codigoNacional = mx.codUnidadAtencion.municipio.codigoNacional), null) as nombreMuniMx, " +
                    " mx.idTomaMx as idTomaMx, mx.fechaHTomaMx as fechaTomaMx, mx.codigoUnicoMx as codigoMx, mx.codigoUnicoMx as codUnicoMx, mx.codTipoMx.idTipoMx as idTipoMx, mx.codTipoMx.nombre as nombreTipoMx, dx.idSolicitudEstudio as idSolicitud, dx.fechaAprobacion as fechaAprobacion  " +
                    " from DaSolicitudEstudio dx inner join dx.idTomaMx mx inner join mx.idNotificacion noti inner join noti.persona p  " +
                    " where noti.pasivo = false and dx.anulado = false and mx.anulada = false and dx.aprobada = true " + sqlEstudio + (filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut));

        }else if (filtro.getCodArea().equals("AREAREP|SILAIS")) {
            queryNotiDx = session.createQuery(" select cast(p.personaId as string) as codigoExpUnico, p.primerNombre as primerNombre, p.segundoNombre as segundoNombre, p.primerApellido as primerApellido, p.segundoApellido as segundoApellido, p.fechaNacimiento as fechaNacimiento, p.sexo.codigo as sexo, " +
                    " p.direccionResidencia as direccionResidencia, p.telefonoResidencia as telefonoResidencia, p.telefonoMovil as telefonoMovil, coalesce((select co.nombre from  Comunidades co where co.codigo=p.comunidadResidencia.codigo), null) as comunidadResidencia, " +
                    " noti.idNotificacion as idNotificacion, noti.semanasEmbarazo as semanasEmbarazo, noti.fechaInicioSintomas as fechaInicioSintomas, coalesce((select r.valor from Respuesta r where r.codigo = noti.urgente.codigo), null) as urgente,  coalesce((select r.valor from Respuesta r where r.codigo = noti.embarazada.codigo), null) as embarazada, " +
                    " coalesce((select ea.codigo from EntidadesAdtvas ea where ea.codigo = noti.codSilaisAtencion.codigo ), null) as codigoSilaisNoti, coalesce((select ea.nombre from EntidadesAdtvas ea where ea.codigo = noti.codSilaisAtencion.codigo ), null) as nombreSilaisNoti, " +
                    " coalesce((select u.codigo from Unidades u where u.codigo = noti.codUnidadAtencion.codigo), null) as codigoUnidadNoti, coalesce((select u.nombre from Unidades u where u.codigo = noti.codUnidadAtencion.codigo), null) as nombreUnidadNoti, " +
                    " coalesce((select u.codigoNacional from Divisionpolitica u where u.codigoNacional = noti.codUnidadAtencion.municipio.codigoNacional), null) as codigoMuniNoti, coalesce((select u.nombre from Divisionpolitica u where u.codigoNacional = noti.codUnidadAtencion.municipio.codigoNacional), null) as nombreMuniNoti, " +
                    " coalesce((select u.codigoNacional from Divisionpolitica u where u.codigoNacional = noti.municipioResidencia.codigoNacional), null) as codigoMuniResid, coalesce((select u.nombre from Divisionpolitica u where u.codigoNacional = noti.municipioResidencia.codigoNacional), null) as nombreMuniResid, " +
                    " coalesce((select ea.codigo from EntidadesAdtvas ea where ea.codigo = noti.municipioResidencia.dependenciaSilais.codigo ), null) as codigoSilaisResid, coalesce((select ea.nombre from EntidadesAdtvas ea where ea.codigo = noti.municipioResidencia.dependenciaSilais.codigo ), null) as nombreSilaisResid, " +
                    " coalesce((select ea.codigo from EntidadesAdtvas ea where ea.codigo = mx.codSilaisAtencion.codigo ), null) as codigoSilaisMx, coalesce((select ea.nombre from EntidadesAdtvas ea where ea.codigo = mx.codSilaisAtencion.codigo ), null) as nombreSilaisMx, " +
                    " coalesce((select u.codigo from Unidades u where u.codigo = mx.codUnidadAtencion.codigo), null) as codigoUnidadMx, coalesce((select u.nombre from Unidades u where u.codigo = mx.codUnidadAtencion.codigo), null) as nombreUnidadMx, " +
                    " coalesce((select u.codigoNacional from Divisionpolitica u where u.codigoNacional = mx.codUnidadAtencion.municipio.codigoNacional), null) as codigoMuniMx, coalesce((select u.nombre from Divisionpolitica u where u.codigoNacional = mx.codUnidadAtencion.municipio.codigoNacional), null) as nombreMuniMx, " +
                    " mx.idTomaMx as idTomaMx, mx.fechaHTomaMx as fechaTomaMx, mx.codigoUnicoMx as codigoMx, mx.codigoUnicoMx as codUnicoMx, mx.codTipoMx.idTipoMx as idTipoMx, mx.codTipoMx.nombre as nombreTipoMx, dx.idSolicitudEstudio as idSolicitud, dx.fechaAprobacion as fechaAprobacion  " +
                    " from DaSolicitudEstudio dx inner join dx.idTomaMx mx inner join mx.idNotificacion noti inner join noti.persona p " +
                    " where noti.pasivo = false and dx.anulado = false and mx.anulada = false and dx.aprobada = true " + sqlEstudio + (filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) +
                    " and noti.codSilaisAtencion.entidadAdtvaId =:codSilais ");
            queryNotiDx.setParameter("codSilais", filtro.getCodSilais());

        } else if (filtro.getCodArea().equals("AREAREP|UNI")) {
            queryNotiDx = session.createQuery(" select cast(p.personaId as string) as codigoExpUnico, p.primerNombre as primerNombre, p.segundoNombre as segundoNombre, p.primerApellido as primerApellido, p.segundoApellido as segundoApellido, p.fechaNacimiento as fechaNacimiento, p.sexo.codigo as sexo, " +
                    " p.direccionResidencia as direccionResidencia, p.telefonoResidencia as telefonoResidencia, p.telefonoMovil as telefonoMovil, coalesce((select co.nombre from  Comunidades co where co.codigo=p.comunidadResidencia.codigo), null) as comunidadResidencia, " +
                    " noti.idNotificacion as idNotificacion, noti.semanasEmbarazo as semanasEmbarazo, noti.fechaInicioSintomas as fechaInicioSintomas, coalesce((select r.valor from Respuesta r where r.codigo = noti.urgente.codigo), null) as urgente,  coalesce((select r.valor from Respuesta r where r.codigo = noti.embarazada.codigo), null) as embarazada, " +
                    " coalesce((select ea.codigo from EntidadesAdtvas ea where ea.codigo = noti.codSilaisAtencion.codigo ), null) as codigoSilaisNoti, coalesce((select ea.nombre from EntidadesAdtvas ea where ea.codigo = noti.codSilaisAtencion.codigo ), null) as nombreSilaisNoti, " +
                    " coalesce((select u.codigo from Unidades u where u.codigo = noti.codUnidadAtencion.codigo), null) as codigoUnidadNoti, coalesce((select u.nombre from Unidades u where u.codigo = noti.codUnidadAtencion.codigo), null) as nombreUnidadNoti, " +
                    " coalesce((select u.codigoNacional from Divisionpolitica u where u.codigoNacional = noti.codUnidadAtencion.municipio.codigoNacional), null) as codigoMuniNoti, coalesce((select u.nombre from Divisionpolitica u where u.codigoNacional = noti.codUnidadAtencion.municipio.codigoNacional), null) as nombreMuniNoti, " +
                    " coalesce((select u.codigoNacional from Divisionpolitica u where u.codigoNacional = noti.municipioResidencia.codigoNacional), null) as codigoMuniResid, coalesce((select u.nombre from Divisionpolitica u where u.codigoNacional = noti.municipioResidencia.codigoNacional), null) as nombreMuniResid, " +
                    " coalesce((select ea.codigo from EntidadesAdtvas ea where ea.codigo = noti.municipioResidencia.dependenciaSilais.codigo ), null) as codigoSilaisResid, coalesce((select ea.nombre from EntidadesAdtvas ea where ea.codigo = noti.municipioResidencia.dependenciaSilais.codigo ), null) as nombreSilaisResid, " +
                    " coalesce((select ea.codigo from EntidadesAdtvas ea where ea.codigo = mx.codSilaisAtencion.codigo ), null) as codigoSilaisMx, coalesce((select ea.nombre from EntidadesAdtvas ea where ea.codigo = mx.codSilaisAtencion.codigo ), null) as nombreSilaisMx, " +
                    " coalesce((select u.codigo from Unidades u where u.codigo = mx.codUnidadAtencion.codigo), null) as codigoUnidadMx, coalesce((select u.nombre from Unidades u where u.codigo = mx.codUnidadAtencion.codigo), null) as nombreUnidadMx, " +
                    " coalesce((select u.codigoNacional from Divisionpolitica u where u.codigoNacional = mx.codUnidadAtencion.municipio.codigoNacional), null) as codigoMuniMx, coalesce((select u.nombre from Divisionpolitica u where u.codigoNacional = mx.codUnidadAtencion.municipio.codigoNacional), null) as nombreMuniMx, " +
                    " mx.idTomaMx as idTomaMx, mx.fechaHTomaMx as fechaTomaMx, mx.codigoUnicoMx as codigoMx, mx.codigoUnicoMx as codUnicoMx, mx.codTipoMx.idTipoMx as idTipoMx, mx.codTipoMx.nombre as nombreTipoMx, dx.idSolicitudEstudio as idSolicitud, dx.fechaAprobacion as fechaAprobacion  " +
                    " from DaSolicitudEstudio dx inner join dx.idTomaMx mx inner join mx.idNotificacion noti inner join noti.persona p " +
                    " where noti.pasivo = false and dx.anulado = false and mx.anulada = false and dx.aprobada = true " + sqlEstudio + (filtro.getConsolidarPor().equalsIgnoreCase("FIS")? sqlFIS : sqlFechasAproRut) +
                    " and noti.codUnidadAtencion.unidadId =:codUnidad ");
            queryNotiDx.setParameter("codUnidad", filtro.getCodUnidad());
        }

        //queryNotiDx.setParameter("codigoLab", filtro.getCodLaboratio());
        queryNotiDx.setParameter("idDx", filtro.getIdEstudio());
        queryNotiDx.setParameter("fechaInicio", filtro.getFechaInicio());
        queryNotiDx.setParameter("fechaFin", filtro.getFechaFin());

        queryNotiDx.setResultTransformer(Transformers.aliasToBean(ResultadoVigilancia.class));
        return queryNotiDx.list();
    }

    public List<Object[]> getFechasDiagnosticosAprobadosByFiltro(FiltrosReporte filtro) throws ParseException {
        Session session = sessionFactory.getCurrentSession();
        String strFiltro = " and mx.fechah_tomamx between :fechaInicio and :fechaFin and dx.laboratorio_prc = :codigoLab and dx.id_diagnostico = :idDx ";
        String strFiltro2 = " and mx.fechah_tomamx between :fechaInicio and :fechaFin and dx.id_diagnostico = :idDx "; //sin laboratorio, es decir todos
        Query queryNotiDx = null;
        if (filtro.getCodArea().equals("AREAREP|PAIS")) {
            queryNotiDx = session.createSQLQuery("select mx.id_tomamx as IDTOMAMX, mx.codigo_lab as CODIGOMX, dx.id_solicitud_dx as IDSOLICITUDDX, c.nombre AS NOMBREDX, mx.fechah_tomamx as FECHATOMAMX, mx.hora_tomamx as HORATOMAMX, " +
                    "(select min(r.fechahora_recepcion) from recepcion_mx r where r.codunicomx = mx.codunicomx) as FECHARECEPCIONGRAL, " +
                    "(select min(r.fecha_recibido) from recepcion_mx r where r.codunicomx = mx.codunicomx) as FECHARECIBIDOGRAL, " +
                    "(select min(r.hora_recibido) from recepcion_mx r where r.codunicomx = mx.codunicomx) as HORARECIBIDOGRAL, " +
                    "(select min(rr.fechahora_recepcion) from recepcion_mx_lab rr, recepcion_mx r where r.codunicomx = mx.codunicomx and rr.id_recepcion = r.id_recepcion and rr.id_area = c.id_area) as FECHARECEPCIONLAB, " +
                    "(select min(dr.fechah_registro) from detalle_resultado dr where dr.id_orden_examen = oe.id_orden_examen ) as FECHAPROCESAMIENTO, " +
                    "dx.fecha_aprobacion as FECHAAPROBACION " +
                    "from LABORATORIO.orden_examen oe inner join ALERTA.da_solicitud_dx dx on oe.id_solicitud_dx = dx.id_solicitud_dx " +
                    "inner join LABORATORIO.catalogo_dx c on c.id_diagnostico = dx.id_diagnostico " +
                    "inner join ALERTA.da_tomamx mx on dx.id_tomamx = mx.id_tomamx inner join ALERTA.da_notificacion noti on mx.id_notificacion = noti.id_notificacion " +
                    "where oe.anulado = '0' and dx.anulado = '0' and dx.aprobada = '1' and mx.anulada = '0' and noti.pasivo = '0' and dx.control_calidad = '0' " +
                    (filtro.getCodLaboratio().equalsIgnoreCase("ALL")? strFiltro2 : strFiltro) +
                    " order by mx.fechah_tomamx");
        }else if (filtro.getCodArea().equals("AREAREP|SILAIS")) {
            queryNotiDx = session.createSQLQuery("select mx.id_tomamx as IDTOMAMX, mx.codigo_lab as CODIGOMX, dx.id_solicitud_dx as IDSOLICITUDDX, c.nombre AS NOMBREDX, mx.fechah_tomamx as FECHATOMAMX, mx.hora_tomamx as HORATOMAMX, " +
                    "(select min(r.fechahora_recepcion) from recepcion_mx r where r.codunicomx = mx.codunicomx) as FECHARECEPCIONGRAL, " +
                    "(select min(r.fecha_recibido) from recepcion_mx r where r.codunicomx = mx.codunicomx) as FECHARECIBIDOGRAL, " +
                    "(select min(r.hora_recibido) from recepcion_mx r where r.codunicomx = mx.codunicomx) as HORARECIBIDOGRAL, " +
                    "(select min(rr.fechahora_recepcion) from recepcion_mx_lab rr, recepcion_mx r where r.codunicomx = mx.codunicomx and rr.id_recepcion = r.id_recepcion and rr.id_area = c.id_area) as FECHARECEPCIONLAB, " +
                    "(select min(dr.fechah_registro) from detalle_resultado dr where dr.id_orden_examen = oe.id_orden_examen ) as FECHAPROCESAMIENTO, " +
                    "dx.fecha_aprobacion as FECHAAPROBACION " +
                    "from LABORATORIO.orden_examen oe inner join ALERTA.da_solicitud_dx dx on oe.id_solicitud_dx = dx.id_solicitud_dx " +
                    "inner join LABORATORIO.catalogo_dx c on c.id_diagnostico = dx.id_diagnostico " +
                    "inner join ALERTA.da_tomamx mx on dx.id_tomamx = mx.id_tomamx inner join ALERTA.da_notificacion noti on mx.id_notificacion = noti.id_notificacion " +
                    "where oe.anulado = '0' and dx.anulado = '0' and dx.aprobada = '1' and mx.anulada = '0' and noti.pasivo = '0' and dx.control_calidad = '0' " +
                    (filtro.getCodLaboratio().equalsIgnoreCase("ALL")? strFiltro2 : strFiltro) +
                    " and noti.cod_silais_atencion = :codSilais " +
                    " order by mx.fechah_tomamx");
            queryNotiDx.setParameter("codSilais", filtro.getCodSilais());

        } else if (filtro.getCodArea().equals("AREAREP|UNI")) {
            queryNotiDx = session.createSQLQuery("select mx.id_tomamx as IDTOMAMX, mx.codigo_lab as CODIGOMX, dx.id_solicitud_dx as IDSOLICITUDDX, c.nombre AS NOMBREDX, mx.fechah_tomamx as FECHATOMAMX, mx.hora_tomamx as HORATOMAMX, " +
                    "(select min(r.fechahora_recepcion) from recepcion_mx r where r.codunicomx = mx.codunicomx) as FECHARECEPCIONGRAL, " +
                    "(select min(r.fecha_recibido) from recepcion_mx r where r.codunicomx = mx.codunicomx) as FECHARECIBIDOGRAL, " +
                    "(select min(r.hora_recibido) from recepcion_mx r where r.codunicomx = mx.codunicomx) as HORARECIBIDOGRAL, " +
                    "(select min(rr.fechahora_recepcion) from recepcion_mx_lab rr, recepcion_mx r where r.codunicomx = mx.codunicomx and rr.id_recepcion = r.id_recepcion and rr.id_area = c.id_area) as FECHARECEPCIONLAB, " +
                    "(select min(dr.fechah_registro) from detalle_resultado dr where dr.id_orden_examen = oe.id_orden_examen ) as FECHAPROCESAMIENTO, " +
                    "dx.fecha_aprobacion as FECHAAPROBACION " +
                    "from LABORATORIO.orden_examen oe inner join ALERTA.da_solicitud_dx dx on oe.id_solicitud_dx = dx.id_solicitud_dx " +
                    "inner join LABORATORIO.catalogo_dx c on c.id_diagnostico = dx.id_diagnostico " +
                    "inner join ALERTA.da_tomamx mx on dx.id_tomamx = mx.id_tomamx inner join ALERTA.da_notificacion noti on mx.id_notificacion = noti.id_notificacion " +
                    "where oe.anulado = '0' and dx.anulado = '0' and dx.aprobada = '1' and mx.anulada = '0' and noti.pasivo = '0' and dx.control_calidad = '0' " +
                    (filtro.getCodLaboratio().equalsIgnoreCase("ALL")? strFiltro2 : strFiltro) +
                    " and noti.cod_unidad_atencion = :codUnidad " +
                    " order by mx.fechah_tomamx");
            queryNotiDx.setParameter("codUnidad", filtro.getCodUnidad());
        }
        if (!filtro.getCodLaboratio().equalsIgnoreCase("ALL")) {
            queryNotiDx.setParameter("codigoLab", filtro.getCodLaboratio());
        }
        queryNotiDx.setParameter("idDx", filtro.getIdDx());
        queryNotiDx.setParameter("fechaInicio", filtro.getFechaInicio());
        queryNotiDx.setParameter("fechaFin", filtro.getFechaFin());

        queryNotiDx.setResultTransformer(Transformers.aliasToBean(TiemposProcesamiento.class));
        List<TiemposProcesamiento> fechasDx = queryNotiDx.list();
        List<Object[]> resFinal = new ArrayList<Object[]>();
        int rowCount=1;
        for (TiemposProcesamiento tmp : fechasDx) {
            Object[] reg1 = new Object[12];
            reg1[0] = rowCount++;
            reg1[1] = tmp.getCODIGOMX(); //Nombre Silais
            //reg1[2] = tmp.getNOMBREDX();
            Date fechaTomaCompuesta = null;
            if (tmp.getHORATOMAMX()!=null){
                fechaTomaCompuesta = DateUtil.StringToDate(DateUtil.DateToString(tmp.getFECHATOMAMX(), "dd/MM/yyyy")+ " "+tmp.getHORATOMAMX(), "dd/MM/yyyy hh:mm a");

                reg1[2] = fechaTomaCompuesta;
                reg1[4] = DateUtil.getDiferenciaDiasHorasMinSegEntreFechas(fechaTomaCompuesta, tmp.getFECHARECEPCIONGRAL());
            }else {
                reg1[2] = tmp.getFECHATOMAMX();
                reg1[4] = DateUtil.getDiferenciaDiasHorasMinSegEntreFechas(tmp.getFECHATOMAMX(), tmp.getFECHARECEPCIONGRAL());
            }
            reg1[3] = tmp.getFECHARECEPCIONGRAL();
            reg1[5] = tmp.getFECHARECEPCIONLAB();
            reg1[6] = DateUtil.getDiferenciaDiasHorasMinSegEntreFechas(tmp.getFECHARECEPCIONGRAL(), tmp.getFECHARECEPCIONLAB());
            reg1[7] = tmp.getFECHAPROCESAMIENTO();
            reg1[8] = DateUtil.getDiferenciaDiasHorasMinSegEntreFechas(tmp.getFECHARECEPCIONLAB(), tmp.getFECHAPROCESAMIENTO());
            reg1[9] = tmp.getFECHAAPROBACION();
            reg1[10] = DateUtil.getDiferenciaDiasHorasMinSegEntreFechas(tmp.getFECHAPROCESAMIENTO(), tmp.getFECHAAPROBACION());
            reg1[11] = DateUtil.getDiferenciaDiasHorasMinSegEntreFechas(fechaTomaCompuesta == null ? tmp.getFECHATOMAMX(): fechaTomaCompuesta, tmp.getFECHAAPROBACION());
            resFinal.add(reg1);
        }
        return resFinal;
    }

    public List<Object[]> getFechasEstudiosAprobadosByFiltro(FiltrosReporte filtro) throws ParseException {
        Session session = sessionFactory.getCurrentSession();
        String strFiltro = " and mx.fechah_tomamx between :fechaInicio and :fechaFin and dx.id_estudio = :idDx ";
        Query queryNotiDx = null;
        if (filtro.getCodArea().equals("AREAREP|PAIS")) {
            queryNotiDx = session.createSQLQuery("select mx.id_tomamx as IDTOMAMX, mx.codunicomx as CODIGOMX, dx.id_solicitud_est as IDSOLICITUDDX, c.nombre AS NOMBREDX, mx.fechah_tomamx as FECHATOMAMX, mx.hora_tomamx as HORATOMAMX, " +
                    "(select min(r.fechahora_recepcion) from recepcion_mx r where r.codunicomx = mx.codunicomx) as FECHARECEPCIONGRAL, " +
                    "(select min(r.fecha_recibido) from recepcion_mx r where r.codunicomx = mx.codunicomx) as FECHARECIBIDOGRAL, " +
                    "(select min(r.hora_recibido) from recepcion_mx r where r.codunicomx = mx.codunicomx) as HORARECIBIDOGRAL, " +
                    "(select min(rr.fechahora_recepcion) from recepcion_mx_lab rr, recepcion_mx r where r.codunicomx = mx.codunicomx and rr.id_recepcion = r.id_recepcion and rr.id_area = c.id_area) as FECHARECEPCIONLAB, " +
                    "(select min(dr.fechah_registro) from detalle_resultado dr where dr.id_orden_examen = oe.id_orden_examen ) as FECHAPROCESAMIENTO, " +
                    "dx.fecha_aprobacion as FECHAAPROBACION " +
                    "from LABORATORIO.orden_examen oe inner join ALERTA.da_solicitud_estudio dx on oe.id_solicitud_est = dx.id_solicitud_est " +
                    "inner join LABORATORIO.catalogo_estudio c on c.id_estudio = dx.id_estudio " +
                    "inner join ALERTA.da_tomamx mx on dx.id_tomamx = mx.id_tomamx inner join ALERTA.da_notificacion noti on mx.id_notificacion = noti.id_notificacion " +
                    "where oe.anulado = '0' and dx.anulado = '0' and dx.aprobada = '1' and mx.anulada = '0' and noti.pasivo = '0' " +
                    strFiltro +
                    " order by mx.fechah_tomamx");
        }else if (filtro.getCodArea().equals("AREAREP|SILAIS")) {
            queryNotiDx = session.createSQLQuery("select mx.id_tomamx as IDTOMAMX, mx.codunicomx as CODIGOMX, dx.id_solicitud_est as IDSOLICITUDDX, c.nombre AS NOMBREDX, mx.fechah_tomamx as FECHATOMAMX, mx.hora_tomamx as HORATOMAMX, " +
                    "(select min(r.fechahora_recepcion) from recepcion_mx r where r.codunicomx = mx.codunicomx) as FECHARECEPCIONGRAL, " +
                    "(select min(r.fecha_recibido) from recepcion_mx r where r.codunicomx = mx.codunicomx) as FECHARECIBIDOGRAL, " +
                    "(select min(r.hora_recibido) from recepcion_mx r where r.codunicomx = mx.codunicomx) as HORARECIBIDOGRAL, " +
                    "(select min(rr.fechahora_recepcion) from recepcion_mx_lab rr, recepcion_mx r where r.codunicomx = mx.codunicomx and rr.id_recepcion = r.id_recepcion and rr.id_area = c.id_area) as FECHARECEPCIONLAB, " +
                    "(select min(dr.fechah_registro) from detalle_resultado dr where dr.id_orden_examen = oe.id_orden_examen ) as FECHAPROCESAMIENTO, " +
                    "dx.fecha_aprobacion as FECHAAPROBACION " +
                    "from LABORATORIO.orden_examen oe inner join ALERTA.da_solicitud_estudio dx on oe.id_solicitud_est = dx.id_solicitud_est " +
                    "inner join LABORATORIO.catalogo_estudio c on c.id_estudio = dx.id_estudio " +
                    "inner join ALERTA.da_tomamx mx on dx.id_tomamx = mx.id_tomamx inner join ALERTA.da_notificacion noti on mx.id_notificacion = noti.id_notificacion " +
                    "where oe.anulado = '0' and dx.anulado = '0' and dx.aprobada = '1' and mx.anulada = '0' and noti.pasivo = '0' " +
                    strFiltro +
                    " and noti.cod_silais_atencion = :codSilais " +
                    " order by mx.fechah_tomamx");
            queryNotiDx.setParameter("codSilais", filtro.getCodSilais());

        } else if (filtro.getCodArea().equals("AREAREP|UNI")) {
            queryNotiDx = session.createSQLQuery("select mx.id_tomamx as IDTOMAMX, mx.codunicomx as CODIGOMX, dx.id_solicitud_est as IDSOLICITUDDX, c.nombre AS NOMBREDX, mx.fechah_tomamx as FECHATOMAMX, mx.hora_tomamx as HORATOMAMX, " +
                    "(select min(r.fechahora_recepcion) from recepcion_mx r where r.codunicomx = mx.codunicomx) as FECHARECEPCIONGRAL, " +
                    "(select min(r.fecha_recibido) from recepcion_mx r where r.codunicomx = mx.codunicomx) as FECHARECIBIDOGRAL, " +
                    "(select min(r.hora_recibido) from recepcion_mx r where r.codunicomx = mx.codunicomx) as HORARECIBIDOGRAL, " +
                    "(select min(rr.fechahora_recepcion) from recepcion_mx_lab rr, recepcion_mx r where r.codunicomx = mx.codunicomx and rr.id_recepcion = r.id_recepcion and rr.id_area = c.id_area) as FECHARECEPCIONLAB, " +
                    "(select min(dr.fechah_registro) from detalle_resultado dr where dr.id_orden_examen = oe.id_orden_examen ) as FECHAPROCESAMIENTO, " +
                    "dx.fecha_aprobacion as FECHAAPROBACION " +
                    "from LABORATORIO.orden_examen oe inner join ALERTA.da_solicitud_estudio dx on oe.id_solicitud_est = dx.id_solicitud_est " +
                    "inner join LABORATORIO.catalogo_estudio c on c.id_estudio = dx.id_estudio " +
                    "inner join ALERTA.da_tomamx mx on dx.id_tomamx = mx.id_tomamx inner join ALERTA.da_notificacion noti on mx.id_notificacion = noti.id_notificacion " +
                    "where oe.anulado = '0' and dx.anulado = '0' and dx.aprobada = '1' and mx.anulada = '0' and noti.pasivo = '0' " +
                    strFiltro +
                    " and noti.cod_unidad_atencion = :codUnidad " +
                    " order by mx.fechah_tomamx");
            queryNotiDx.setParameter("codUnidad", filtro.getCodUnidad());
        }

        queryNotiDx.setParameter("idDx", filtro.getIdEstudio());
        queryNotiDx.setParameter("fechaInicio", filtro.getFechaInicio());
        queryNotiDx.setParameter("fechaFin", filtro.getFechaFin());

        queryNotiDx.setResultTransformer(Transformers.aliasToBean(TiemposProcesamiento.class));
        List<TiemposProcesamiento> fechasDx = queryNotiDx.list();
        List<Object[]> resFinal = new ArrayList<Object[]>();
        int rowCount=1;
        for (TiemposProcesamiento tmp : fechasDx) {
            Object[] reg1 = new Object[12];
            reg1[0] = rowCount++;
            reg1[1] = tmp.getCODIGOMX(); //Nombre Silais
            //reg1[2] = tmp.getNOMBREDX();
            Date fechaTomaCompuesta = null;
            if (tmp.getHORATOMAMX()!=null){
                fechaTomaCompuesta = DateUtil.StringToDate(DateUtil.DateToString(tmp.getFECHATOMAMX(), "dd/MM/yyyy")+ " "+tmp.getHORATOMAMX(), "dd/MM/yyyy hh:mm a");

                reg1[2] = fechaTomaCompuesta;
                reg1[4] = DateUtil.getDiferenciaDiasHorasMinSegEntreFechas(fechaTomaCompuesta, tmp.getFECHARECEPCIONGRAL());
            }else {
                reg1[2] = tmp.getFECHATOMAMX();
                reg1[4] = DateUtil.getDiferenciaDiasHorasMinSegEntreFechas(tmp.getFECHATOMAMX(), tmp.getFECHARECEPCIONGRAL());
            }
            reg1[3] = tmp.getFECHARECEPCIONGRAL();
            reg1[5] = tmp.getFECHARECEPCIONLAB();
            reg1[6] = DateUtil.getDiferenciaDiasHorasMinSegEntreFechas(tmp.getFECHARECEPCIONGRAL(), tmp.getFECHARECEPCIONLAB());
            reg1[7] = tmp.getFECHAPROCESAMIENTO();
            reg1[8] = DateUtil.getDiferenciaDiasHorasMinSegEntreFechas(tmp.getFECHARECEPCIONLAB(), tmp.getFECHAPROCESAMIENTO());
            reg1[9] = tmp.getFECHAAPROBACION();
            reg1[10] = DateUtil.getDiferenciaDiasHorasMinSegEntreFechas(tmp.getFECHAPROCESAMIENTO(), tmp.getFECHAAPROBACION());
            reg1[11] = DateUtil.getDiferenciaDiasHorasMinSegEntreFechas(fechaTomaCompuesta == null ? tmp.getFECHATOMAMX(): fechaTomaCompuesta, tmp.getFECHAAPROBACION());
            resFinal.add(reg1);
        }
        return resFinal;
    }

}
