package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.irag.DaIrag;
import ni.gob.minsa.laboratorio.domain.muestra.*;
import ni.gob.minsa.laboratorio.domain.vigilanciaSindFebril.DaSindFebril;
import org.apache.commons.codec.language.Soundex;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
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
            crit.add(Restrictions.and(
                    Restrictions.eq("estado.codigo", filtro.getCodEstado()).ignoreCase()));
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

        return crit.list();
    }

    /**
     * Obtiene fecha de Inicio de síntomas, según id de notificación.. Si se agregan nuevas fichas, se debe agregar la consulta a dicha ficha
     * @param strIdNotificacion id de la notificación a filtrar
     * @return Date
     */
    public Date getFechaInicioSintomas(String strIdNotificacion){
        Date fecInicioSintomas = null;
        String query = "from DaIrag where idNotificacion.idNotificacion = :idNotificacion";
        String query2 = "from DaSindFebril where idNotificacion.idNotificacion = :idNotificacion";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idNotificacion", strIdNotificacion);
        Query q2 = session.createQuery(query2);
        q2.setParameter("idNotificacion", strIdNotificacion);

        DaIrag iragNoti= (DaIrag)q.uniqueResult();
        DaSindFebril sinFebNoti= (DaSindFebril)q2.uniqueResult();
        if(iragNoti!=null)
            fecInicioSintomas = iragNoti.getFechaInicioSintomas();
        else if(sinFebNoti!=null)
            fecInicioSintomas = sinFebNoti.getFechaInicioSintomas();

        return fecInicioSintomas;
    }

    public List<DaSolicitudDx> getSolicitudesDxByMx(String idTomaMx){
        String query = "from DaSolicitudDx where idTomaMx.idTomaMx = :idTomaMx ORDER BY fechaHSolicitud";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idTomaMx",idTomaMx);
        return q.list();
    }

    public DaSolicitudDx getSolicitudesDxByMxDx(String idTomaMx,  Integer idDiagnostico){
        String query = "from DaSolicitudDx where idTomaMx.idTomaMx = :idTomaMx " +
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

    /**
     *Retorna una lista de dx segun tipoMx y tipo Notificacion
     * @param codMx tipo de Mx
     * @param tipoNoti tipo Notificacion
     *
     */
    @SuppressWarnings("unchecked")
    public List<Dx_TipoMx_TipoNoti> getDx(String codMx, String tipoNoti) throws Exception {
        String query = "select dx from Dx_TipoMx_TipoNoti dx where dx.tipoMx_tipoNotificacion.tipoMx.idTipoMx = :codMx and dx.tipoMx_tipoNotificacion.tipoNotificacion.codigo = :tipoNoti" ;
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("codMx", codMx);
        q.setString("tipoNoti", tipoNoti);
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
}
