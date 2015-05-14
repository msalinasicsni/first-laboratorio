package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.muestra.*;
import ni.gob.minsa.laboratorio.domain.muestra.traslado.HistoricoEnvioMx;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.AutoridadLaboratorio;
import org.apache.commons.codec.language.Soundex;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by souyen-ics on 11-05-14.
 */
@Service("tomaMxService")
@Transactional
public class TomaMxService {

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;


    public void updateTomaMx(DaTomaMx dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.update(dto);
            }
            else
                throw new Exception("Objeto toma Mx es NULL");
        }catch (Exception ex){
            ex.printStackTrace();
            throw ex;
        }
    }

    /**
     * Agrega Registro de Toma de Muestra
     */
    public void addTomaMx(DaTomaMx toma) {
        Session session = sessionFactory.getCurrentSession();
        session.save(toma);
    }

    /**
     * Retorna toma de muestra
     * @param id
     */
    public DaTomaMx getTomaMxById(String id){
        String query = "from DaTomaMx where idTomaMx = :id";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("id", id);
        return (DaTomaMx)q.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<DaTomaMx> getTomaMxByFiltro(FiltroMx filtro){
        Session session = sessionFactory.getCurrentSession();
        Soundex varSoundex = new Soundex();
        Criteria crit = session.createCriteria(DaTomaMx.class, "tomaMx");
        crit.createAlias("tomaMx.estadoMx","estado");
        crit.createAlias("tomaMx.idNotificacion", "notifi");
        //siempre se tomam las muestras que no estan anuladas
        crit.add( Restrictions.and(
                        Restrictions.eq("tomaMx.anulada", false))
        );//y las ordenes en estado según filtro
        if (filtro.getCodEstado()!=null) {
            if (filtro.getIncluirTraslados()){
                crit.add(Restrictions.or(
                        Restrictions.eq("estado.codigo", filtro.getCodEstado()).ignoreCase()).
                        add(Restrictions.or(
                                Restrictions.eq("estado.codigo", "ESTDMX|TRAS"))));
            }else {
                crit.add(Restrictions.and(
                        Restrictions.eq("estado.codigo", filtro.getCodEstado()).ignoreCase()));
            }
        }
        // se filtra por nombre y apellido persona
        if (filtro.getNombreApellido()!=null) {
            crit.createAlias("notifi.persona", "person");
            String[] partes = filtro.getNombreApellido().split(" ");
            String[] partesSnd = filtro.getNombreApellido().split(" ");
            for (int i = 0; i < partes.length; i++) {
                try {
                    partesSnd[i] = varSoundex.encode(partes[i]);
                } catch (IllegalArgumentException e) {
                    partesSnd[i] = "0000";
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < partes.length; i++) {
                Junction conditionGroup = Restrictions.disjunction();
                conditionGroup.add(Restrictions.ilike("person.primerNombre", "%" + partes[i] + "%"))
                        .add(Restrictions.ilike("person.primerApellido", "%" + partes[i] + "%"))
                        .add(Restrictions.ilike("person.segundoNombre", "%" + partes[i] + "%"))
                        .add(Restrictions.ilike("person.segundoApellido", "%" + partes[i] + "%"))
                        .add(Restrictions.ilike("person.sndNombre", "%" + partesSnd[i] + "%"));
                crit.add(conditionGroup);
            }
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
        //Se filtra por rango de fecha de toma de muestra
        if (filtro.getFechaInicioTomaMx()!=null && filtro.getFechaFinTomaMx()!=null){
            crit.add( Restrictions.and(
                            Restrictions.between("tomaMx.fechaRegistro", filtro.getFechaInicioTomaMx(),filtro.getFechaFinTomaMx()))
            );
        }
        // se filtra por tipo de muestra
        if (filtro.getCodTipoMx()!=null){
            crit.add( Restrictions.and(
                            Restrictions.eq("tomaMx.codTipoMx.idTipoMx", Integer.valueOf(filtro.getCodTipoMx())))
            );
        }

        //se filtra por tipo de solicitud
        if(filtro.getCodTipoSolicitud()!=null){
            if(filtro.getCodTipoSolicitud().equals("Estudio")){
                crit.add(Subqueries.propertyIn("idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                        .createAlias("idTomaMx", "idTomaMx")
                        .setProjection(Property.forName("idTomaMx.idTomaMx"))));
            }else{
                crit.add(Subqueries.propertyIn("idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                        .createAlias("idTomaMx", "idTomaMx")
                        .setProjection(Property.forName("idTomaMx.idTomaMx"))));
            }

        }

        //nombre solicitud
        if (filtro.getNombreSolicitud() != null) {
            if (filtro.getCodTipoSolicitud() != null) {
                if (filtro.getCodTipoSolicitud().equals("Estudio")) {
                    crit.add(Subqueries.propertyIn("idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                            .createAlias("tipoEstudio", "estudio")
                            .add(Restrictions.ilike("estudio.nombre", "%" + filtro.getNombreSolicitud() + "%"))
                            .setProjection(Property.forName("idTomaMx.idTomaMx"))));
                } else {
                    crit.add(Subqueries.propertyIn("idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                            .createAlias("codDx", "dx")
                            .add(Restrictions.ilike("dx.nombre", "%" + filtro.getNombreSolicitud() + "%"))
                            .setProjection(Property.forName("idTomaMx.idTomaMx"))));
                }
            } else {

                Junction conditGroup = Restrictions.disjunction();
                conditGroup.add(Subqueries.propertyIn("idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                        .createAlias("tipoEstudio", "estudio")
                        .add(Restrictions.ilike("estudio.nombre", "%" + filtro.getNombreSolicitud() + "%"))
                        .setProjection(Property.forName("idTomaMx.idTomaMx"))))
                        .add(Subqueries.propertyIn("idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
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
                    .setProjection(Property.forName("labautorizado.codigo"))));
            */
            Junction conditGroup = Restrictions.disjunction();

            //usuario tiene autorizado envio actual, o alguno que este en histórico para la muestra
            conditGroup.add(Subqueries.propertyIn("tomaMx.envio.idEnvio", DetachedCriteria.forClass(DaEnvioMx.class)
                    .createAlias("laboratorioDestino", "destino")
                    .add(Subqueries.propertyIn("destino.codigo", DetachedCriteria.forClass(AutoridadLaboratorio.class)
                            .createAlias("laboratorio", "labautorizado")
                            .add(Restrictions.eq("pasivo", false)) //autoridad area activa
                            .add(Restrictions.and(Restrictions.eq("user.username", filtro.getNombreUsuario()))) //usuario
                            .setProjection(Property.forName("labautorizado.codigo"))))
                    .setProjection(Property.forName("idEnvio"))))
                    .add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(HistoricoEnvioMx.class)
                            .createAlias("envioMx", "envio")
                            .add(Subqueries.propertyIn("envio.laboratorioDestino.codigo", DetachedCriteria.forClass(AutoridadLaboratorio.class)
                                    .createAlias("laboratorio", "labautorizado")
                                    .add(Restrictions.eq("pasivo", false)) //autoridad area activa
                                    .add(Restrictions.and(Restrictions.eq("user.username", filtro.getNombreUsuario()))) //usuario
                                    .setProjection(Property.forName("labautorizado.codigo"))))
                            .createAlias("tomaMx", "toma")
                            .setProjection(Property.forName("toma.idTomaMx"))));

            crit.add(conditGroup);
        }

        //filtro para solicitudes aprobadas
        if (filtro.getSolicitudAprobada() != null) {
           Junction conditGroup = Restrictions.disjunction();
            conditGroup.add(Subqueries.propertyIn("idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                    .add(Restrictions.eq("aprobada", filtro.getSolicitudAprobada()))
                    .createAlias("idTomaMx", "toma")
                    .setProjection(Property.forName("toma.idTomaMx"))))

                    .add(Subqueries.propertyIn("idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                            .add(Restrictions.eq("aprobada", filtro.getSolicitudAprobada()))
                            .createAlias("idTomaMx", "toma")
                            .setProjection(Property.forName("toma.idTomaMx"))));


            crit.add(conditGroup);

        }
        //filtro sólo control calidad en el laboratio del usuario
        if (filtro.getControlCalidad()!=null) {
            crit.add(Subqueries.propertyIn("idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                    .add(Restrictions.eq("controlCalidad", filtro.getControlCalidad()))
                    .createAlias("idTomaMx", "toma")
                    //.createAlias("labProcesa","labProcesa")
                    .add(Subqueries.propertyIn("labProcesa.codigo", DetachedCriteria.forClass(AutoridadLaboratorio.class)
                            .createAlias("laboratorio", "labautorizado")
                            .createAlias("user", "usuario")
                            .add(Restrictions.eq("pasivo",false)) //autoridad laboratorio activa
                            .add(Restrictions.and(Restrictions.eq("usuario.username",filtro.getNombreUsuario()))) //usuario
                            .setProjection(Property.forName("labautorizado.codigo"))))
                    .setProjection(Property.forName("toma.idTomaMx"))));
        }

        return crit.list();
    }

    public List<DaSolicitudDx> getSolicitudesDxByIdToma(String idTomaMx, String codigoLab){
        /*String query = "select sdx from DaSolicitudDx sdx inner join sdx.idTomaMx mx inner join mx.envio en " +
                "where mx.idTomaMx = :idTomaMx and en.laboratorioDestino.codigo = :codigoLab " +
                "and sdx.labProcesa.codigo = :codigoLab ORDER BY sdx.fechaHSolicitud";*/
        String query = "select sdx from DaSolicitudDx sdx inner join sdx.idTomaMx mx " +
                "where mx.idTomaMx = :idTomaMx " +
                "and sdx.labProcesa.codigo = :codigoLab ORDER BY sdx.fechaHSolicitud";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idTomaMx",idTomaMx);
        q.setParameter("codigoLab",codigoLab);
        return q.list();
    }

    public List<DaSolicitudEstudio> getSolicitudesEstudioByMx(String idTomaMx){
        String query = "from DaSolicitudEstudio where idTomaMx.idTomaMx = :idTomaMx ORDER BY fechaHSolicitud";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idTomaMx",idTomaMx);
        return q.list();
    }

    public DaSolicitudDx getSolicitudesDxByMxDx(String idTomaMx,  Integer idDiagnostico){
        String query = "from DaSolicitudDx where idTomaMx.idTomaMx = :idTomaMx and idTomaMx.envio.laboratorioDestino.codigo = labProcesa.codigo " +
                "and codDx.idDiagnostico = :idDiagnostico ORDER BY fechaHSolicitud";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idTomaMx",idTomaMx);
        q.setParameter("idDiagnostico",idDiagnostico);
        return (DaSolicitudDx)q.uniqueResult();
    }

    public DaSolicitudDx getSolicitudDxByIdSolicitud(String idSolicitud){
        String query = "from DaSolicitudDx where idSolicitudDx = :idSolicitud";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idSolicitud",idSolicitud);
        return (DaSolicitudDx)q.uniqueResult();
    }

    public DaSolicitudEstudio getEstudioByIdSolicitud(String idSolicitud){
        String query = "from DaSolicitudEstudio where idSolicitudEstudio = :idSolicitud";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idSolicitud",idSolicitud);
        return (DaSolicitudEstudio)q.uniqueResult();
    }

    /**
     *Retorna una lista de dx segun tipoMx y tipo Notificacion
     * @param codMx tipo de Mx
     * @param tipoNoti tipo Notificacion
     *
     */
    @SuppressWarnings("unchecked")
    public List<Dx_TipoMx_TipoNoti> getDx(String codMx, String tipoNoti, String userName) throws Exception {
        String query = "select dx from Dx_TipoMx_TipoNoti dx " +
                "where dx.tipoMx_tipoNotificacion.tipoMx.idTipoMx = :codMx " +
                "and dx.tipoMx_tipoNotificacion.tipoNotificacion.codigo = :tipoNoti ";
        if (userName!=null) {
          query +=  "and dx.diagnostico.area.idArea in (select a.idArea from AutoridadArea as aa inner join aa.area as a where aa.user.username = :userName)";
        }
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("codMx", codMx);
        q.setString("tipoNoti", tipoNoti);
        if (userName!=null) {
            q.setString("userName", userName);
        }
        return q.list();
    }

    public List<Catalogo_Dx> getDxsByTipoNoti(String tipoNoti) throws Exception {
        String query = "select dx from Dx_TipoMx_TipoNoti dxrel inner join dxrel.diagnostico dx where dxrel.tipoMx_tipoNotificacion.tipoNotificacion.codigo = :tipoNoti" ;
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("tipoNoti", tipoNoti);
        return q.list();
    }

    public DaTomaMx getTomaMxByCodUnicoMx(String codigoUnicoMx){
        String query = "from DaTomaMx as a where codigoUnicoMx= :codigoUnicoMx";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("codigoUnicoMx", codigoUnicoMx);
        return  (DaTomaMx)q.uniqueResult();
    }

    public Catalogo_Dx getDxsById(Integer idDx) throws Exception {
        String query = "from Catalogo_Dx where idDiagnostico = :idDx" ;
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idDx", idDx);
        return (Catalogo_Dx)q.uniqueResult();
    }

    public Catalogo_Estudio getEstudioById(Integer idEstudio) throws Exception {
        String query = "from Catalogo_Estudio where idEstudio= :idEstudio" ;
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idEstudio", idEstudio);
        return (Catalogo_Estudio)q.uniqueResult();
    }

    /**
     * actualizar solicitud de diagnostico o rutina
     * @param solicitud objeto a actualizar
     */
    public void updateSolicitudDx(DaSolicitudDx solicitud) {
        Session session = sessionFactory.getCurrentSession();
        session.update(solicitud);
    }

/****************************************************************
 * MUESTRAS DE ESTUDIOS
******************************************************************/
    public List<DaSolicitudEstudio> getSolicitudesEstudioByIdTomaMx(String idTomaMx){
        String query = "from DaSolicitudEstudio where idTomaMx.idTomaMx = :idTomaMx ORDER BY fechaHSolicitud";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idTomaMx",idTomaMx);
        return q.list();
    }

    /**
     *Retorna una lista de estudios segun tipoMx y tipo Notificacion
     * @param codTipoMx código del tipo de Mx
     * @param codTipoNoti código del tipo Notificacion
     *
     */
    @SuppressWarnings("unchecked")
    public List<Estudio_TipoMx_TipoNoti> getEstudiosByTipoMxTipoNoti(String codTipoMx, String codTipoNoti, String idTomaMx) throws Exception {
        String query = "select est from Estudio_TipoMx_TipoNoti est, DaSolicitudEstudio as dse inner join dse.tipoEstudio as tes " +
                "where est.tipoMx_tipoNotificacion.tipoMx.idTipoMx = :codTipoMx " +
                "and est.tipoMx_tipoNotificacion.tipoNotificacion.codigo = :codTipoNoti " +
                "and tes.idEstudio = est.estudio.idEstudio " +
                "and dse.idTomaMx.idTomaMx = :idTomaMx" ;
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("codTipoMx", codTipoMx);
        q.setString("codTipoNoti", codTipoNoti);
        q.setParameter("idTomaMx",idTomaMx);

        return q.list();
    }

    public DaSolicitudEstudio getSolicitudesEstudioByMxEst(String idTomaMx, Integer idEstudio){
        String query = "from DaSolicitudEstudio where idTomaMx.idTomaMx = :idTomaMx " +
                "and tipoEstudio.idEstudio= :idEstudio ORDER BY fechaHSolicitud";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idTomaMx",idTomaMx);
        q.setParameter("idEstudio",idEstudio);
        return (DaSolicitudEstudio)q.uniqueResult();
    }

    public List<Catalogo_Estudio> getEstudiossByTipoNoti(String tipoNoti) throws Exception {
        String query = "select dx from Estudio_TipoMx_TipoNoti dxrel inner join dxrel.estudio dx " +
                "where dxrel.tipoMx_tipoNotificacion.tipoNotificacion.codigo = :tipoNoti" ;
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("tipoNoti", tipoNoti);
        return q.list();
    }

    public DaSolicitudEstudio getSolicitudEstByIdSolicitud(String idSolicitud){
        String query = "from DaSolicitudEstudio where idSolicitudEstudio = :idSolicitud";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idSolicitud",idSolicitud);
        return (DaSolicitudEstudio)q.uniqueResult();
    }

    /**
     * actualizar solicitud de estudio
     * @param solicitud objeto a actualizar
     */
    public void updateSolicitudEstudio(DaSolicitudEstudio solicitud) {
        Session session = sessionFactory.getCurrentSession();
        session.update(solicitud);
    }

    @SuppressWarnings("unchecked")
    public List<DaSolicitudDx> getSoliDxAprobByIdToma(String idTomaMx){
        String query = "from DaSolicitudDx where idTomaMx.idTomaMx = :idTomaMx and  aprobada = true ORDER BY fechaHSolicitud";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idTomaMx",idTomaMx);
        return q.list();
    }

    @SuppressWarnings("unchecked")
    public List<DaSolicitudEstudio> getSoliEstudioAprobByIdTomaMx(String idTomaMx){
        String query = "from DaSolicitudEstudio where idTomaMx.idTomaMx = :idTomaMx and aprobada = true ORDER BY fechaHSolicitud";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idTomaMx",idTomaMx);
        return q.list();
    }

    @SuppressWarnings("unchecked")
    public List<DaSolicitudEstudio> getSoliEAprobByIdTomaMxOrderCodigo(String idTomaMx){
        String query = "from DaSolicitudEstudio where idTomaMx.idTomaMx = :idTomaMx and aprobada = true ORDER BY  idTomaMx.codigoUnicoMx";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idTomaMx",idTomaMx);
        return q.list();
    }

    @SuppressWarnings("unchecked")
    public List<DaSolicitudEstudio> getSoliEAprobByCodigo(String codigoUnico){
        String query = "from DaSolicitudEstudio where idTomaMx.codigoUnicoMx like :codigoUnico and aprobada = true ORDER BY  idTomaMx.codigoUnicoMx";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("codigoUnico",codigoUnico + "%");
        return q.list();
    }

    @SuppressWarnings("unchecked")
     public DaSolicitudEstudio getSoliEstByCodigo(String codigoUnico){
        String query = "from DaSolicitudEstudio where idTomaMx.codigoUnicoMx like :codigoUnico ORDER BY  idTomaMx.codigoUnicoMx";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("codigoUnico",codigoUnico);
        return (DaSolicitudEstudio) q.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public DaSolicitudDx getSoliDxByCodigo(String codigoUnico, String userName){
        String query = "select sdx from DaSolicitudDx sdx, AutoridadLaboratorio al " +
                "where sdx.labProcesa.codigo = al.laboratorio.codigo and al.user.username = :userName and sdx.idTomaMx.codigoUnicoMx like :codigoUnico ORDER BY  sdx.idTomaMx.codigoUnicoMx";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("codigoUnico",codigoUnico);
        q.setParameter("userName",userName);
        return (DaSolicitudDx) q.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<DaSolicitudDx> getSolicitudesDxCodigo(String codigo, String userName){
        String query = " select sdx from DaSolicitudDx sdx, AutoridadLaboratorio al " +
                "where sdx.labProcesa.codigo = al.laboratorio.codigo and al.user.username =:userName and sdx.idTomaMx.codigoUnicoMx like :codigo ORDER BY sdx.fechaHSolicitud";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("codigo",codigo);
        q.setParameter("userName",userName);
        return q.list();
    }

    @SuppressWarnings("unchecked")
    public List<DaSolicitudDx> getSoliDxAprobByTomaAndUser(String idToma, String userName){
        String query = " select sdx from DaSolicitudDx sdx, AutoridadLaboratorio al " +
                "where sdx.labProcesa.codigo = al.laboratorio.codigo and al.user.username =:userName and sdx.idTomaMx.idTomaMx like :idToma and sdx.aprobada = true ORDER BY sdx.fechaHSolicitud";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idToma",idToma);
        q.setParameter("userName",userName);
        return q.list();
    }


    public DaSolicitudDx getSolicitudDxByIdSolicitudUser(String idSolicitud, String userName){
        String query = " select sdx from DaSolicitudDx sdx, AutoridadLaboratorio al " +
                "where sdx.labProcesa.codigo = al.laboratorio.codigo and al.user.username =:userName and sdx.idSolicitudDx like :idSolicitud ORDER BY sdx.fechaHSolicitud";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idSolicitud",idSolicitud);
        q.setParameter("userName",userName);
        return (DaSolicitudDx)q.uniqueResult();
    }


    /************************************************************/
    /***************TOMA MX VIP**********************************/
    /************************************************************/
    @SuppressWarnings("unchecked")
    public List<TipoMx_TipoNotificacion> getTipoMxByTipoNoti(String codigo){
        //Retrieve session Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM TipoMx_TipoNotificacion tmx where tmx.tipoNotificacion = :codigo and tmx.pasivo= false");
        query.setString("codigo", codigo);
        //retrieve all
        return query.list();

    }

    /**
     * Retorna tipoMx
     * @param id
     */
    public TipoMx getTipoMxById(String id){
        String query = "from TipoMx where idTipoMx = :id";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("id", id);
        return (TipoMx)q.uniqueResult();
    }

    /**
     * Agrega solicitud rutina
     */
    public void addSolicitudDx(DaSolicitudDx orden) {
        Session session = sessionFactory.getCurrentSession();
        session.save(orden);
    }

    public void addEnvioOrden(DaEnvioMx dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.save(dto);
            }
            else
                throw new Exception("Objeto Envio Orden es NULL");
        }catch (Exception ex){
            ex.printStackTrace();
            throw ex;
        }
    }
    /**
     * Retorna rutina
     * @param id
     */
    public Catalogo_Dx getDxById(String id){
        String query = "from Catalogo_Dx where idDiagnostico = :id";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("id", id);
        return (Catalogo_Dx)q.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<DaTomaMx> getTomaMxByIdNoti(String idNotificacion){
        //Retrieve session Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM DaTomaMx tmx where tmx.idNotificacion = :idNotificacion");
        query.setString("idNotificacion", idNotificacion);
        //retrieve all
        return query.list();

    }

    public List<DaSolicitudDx> getSolicitudesDxPrioridadByIdToma(String idTomaMx){
        String query = "select sdx from DaSolicitudDx as sdx inner join sdx.codDx dx where sdx.idTomaMx.idTomaMx = :idTomaMx ORDER BY dx.prioridad asc";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idTomaMx",idTomaMx);
        return q.list();
    }

    /**
     * Se toma las solicitudes dx cuya área no se encuentra en la tabla de traslados para esa mx
     * @param idTomaMx
     * @return
     */
    public List<DaSolicitudDx> getSolicitudesDxSinTrasladoByIdToma(String idTomaMx){
        String query = "select sdx from DaSolicitudDx sdx inner join sdx.idTomaMx t inner join sdx.codDx dx, RecepcionMx r " +
                "where r.tomaMx.idTomaMx = t.idTomaMx and t.idTomaMx = :idTomaMx " +
                "and dx.area.idArea not in ( " +
                "           select rl.area.idArea from RecepcionMxLab rl where rl.recepcionMx.idRecepcion = r.idRecepcion) " +
                "ORDER BY dx.prioridad asc";

        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idTomaMx",idTomaMx);
        return q.list();
    }

    public List<DaSolicitudDx> getSolicitudesDxByIdTomaArea(String idTomaMx, int idArea){
        String query = "from DaSolicitudDx where idTomaMx.idTomaMx = :idTomaMx " +
                "and codDx.area.idArea = :idArea ORDER BY fechaHSolicitud";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idTomaMx",idTomaMx);
        q.setParameter("idArea",idArea);
        return q.list();
    }
}
