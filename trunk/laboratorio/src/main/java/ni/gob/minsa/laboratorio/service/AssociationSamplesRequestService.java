package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.muestra.*;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by souyen-ics.
 */
@Service("associationSR")
@Transactional
public class AssociationSamplesRequestService {

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;

    private Logger logger = LoggerFactory.getLogger(AssociationSamplesRequestService.class);

    public AssociationSamplesRequestService() {
    }

    /**
     * Obtiene una lista de
     * @param code codigoNotificacion
     */
    @SuppressWarnings("unchecked")
    public List<TipoMx_TipoNotificacion> getMxNoti(String code){
        Session session = sessionFactory.getCurrentSession();
        Criteria cr = session.createCriteria(TipoMx_TipoNotificacion.class, "mxNoti");
        cr.createAlias("mxNoti.tipoNotificacion", "noti");
        cr.add(Restrictions.eq("noti.codigo", code));
        cr.add(Restrictions.eq("mxNoti.pasivo", false));
        return cr.list();
    }

    /**
     * Obtiene una lista de dx asociados a tipoMxNoti
     * @param idTipoMxNoti
     */
    @SuppressWarnings("unchecked")
    public List<Dx_TipoMx_TipoNoti> getRoutines(Integer idTipoMxNoti){
        Session session = sessionFactory.getCurrentSession();
        Criteria cr = session.createCriteria(Dx_TipoMx_TipoNoti.class, "dx");
        cr.add(Restrictions.eq("dx.pasivo", false));
        cr.createAlias("dx.tipoMx_tipoNotificacion", "tipoMxNoti");
        cr.add(Restrictions.eq("tipoMxNoti.pasivo", false));
        cr.add(Restrictions.eq("tipoMxNoti.id", idTipoMxNoti));
        return cr.list();
    }

    /**
     * Obtiene una lista de estudios asociados a tipoMxNoti
     * @param idTipoMxNoti
     */
    @SuppressWarnings("unchecked")
    public List<Estudio_TipoMx_TipoNoti> getStudies(Integer idTipoMxNoti){
        Session session = sessionFactory.getCurrentSession();
        Criteria cr = session.createCriteria(Estudio_TipoMx_TipoNoti.class, "est");
        cr.add(Restrictions.eq("est.pasivo", false));
        cr.createAlias("est.tipoMx_tipoNotificacion", "tipoMxNoti");
        cr.add(Restrictions.eq("tipoMxNoti.pasivo", false));
        cr.add(Restrictions.eq("tipoMxNoti.id", idTipoMxNoti));
        return cr.list();
    }

    @SuppressWarnings("unchecked")
    public List<Catalogo_Dx> getDxs() throws Exception {
        String query = "from Catalogo_Dx dx where pasivo = false " ;
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        return q.list();
    }

    public Catalogo_Dx getDx(Integer id){
        String query = "from Catalogo_Dx dx where dx.id = :id ";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setInteger("id", id);
        return (Catalogo_Dx) q.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<Catalogo_Estudio> getStudies() throws Exception {
        String query = "from Catalogo_Estudio where pasivo = false" ;
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        return q.list();
    }

    public Catalogo_Estudio getEstudio(Integer id){
        String query = "from Catalogo_Estudio est where est.id = :id ";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setInteger("id", id);
        return (Catalogo_Estudio) q.uniqueResult();
    }

    public TipoMx_TipoNotificacion getTipoMxTipoNoti(Integer id){
        String query = "from TipoMx_TipoNotificacion tp where tp.id = :id ";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setInteger("id", id);
        return (TipoMx_TipoNotificacion) q.uniqueResult();
    }

    public Dx_TipoMx_TipoNoti getDxByIdAndDx(Integer id, Integer codDx){
        String query = "from Dx_TipoMx_TipoNoti dx where dx.tipoMx_tipoNotificacion.id = :id and dx.diagnostico.idDiagnostico = :codDx and dx.pasivo = false";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setInteger("id", id);
        q.setInteger("codDx", codDx);
        return (Dx_TipoMx_TipoNoti) q.uniqueResult();
    }

    public Estudio_TipoMx_TipoNoti getEstudioByIdAndEst(Integer id, Integer codEstudio){
        String query = "from Estudio_TipoMx_TipoNoti est where est.tipoMx_tipoNotificacion.id = :id and est.estudio.idEstudio = :codEstudio and est.pasivo = false ";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setInteger("id", id);
        q.setInteger("codEstudio", codEstudio);
        return (Estudio_TipoMx_TipoNoti) q.uniqueResult();
    }

    /**
     * Actualiza o agrega una asociacion de solicitud de rutina
     *
     * @param dto Objeto a actualizar o agregar
     * @throws Exception
     */
    public void addOrUpdateRequestRoutine(Dx_TipoMx_TipoNoti dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.saveOrUpdate(dto);
            }
            else
                throw new Exception("Objeto NULL");
        }catch (Exception ex){
            logger.error("Error al agregar o actualizar solicitud rutina",ex);
            throw ex;
        }
    }

    /**
     * Actualiza o agrega una asociacion de solicitud de estudio
     *
     * @param dto Objeto a actualizar o agregar
     * @throws Exception
     */
    public void addOrUpdateRequestStudy(Estudio_TipoMx_TipoNoti dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.saveOrUpdate(dto);
            }
            else
                throw new Exception("Objeto NULL");
        }catch (Exception ex){
            logger.error("Error al agregar o actualizar solicitud estudio",ex);
            throw ex;
        }
    }


    public Dx_TipoMx_TipoNoti getDxByidDxMxNoti(Integer id){
        String query = "from Dx_TipoMx_TipoNoti dx where dx.idDxTipoMxNt = :id and dx.pasivo = false";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setInteger("id", id);
        return (Dx_TipoMx_TipoNoti) q.uniqueResult();
    }

    public Estudio_TipoMx_TipoNoti getEstByidEstMxNoti(Integer id){
        String query = "from Estudio_TipoMx_TipoNoti est where est.idEstTipoMxNt = :id and est.pasivo = false";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setInteger("id", id);
        return (Estudio_TipoMx_TipoNoti) q.uniqueResult();
    }

    public TipoMx_TipoNotificacion getTMxNotiByIdAndNoti(Integer idMx, String noti){
        String query = "from TipoMx_TipoNotificacion tMxNoti where tMxNoti.tipoMx.id = :idMx and tMxNoti.tipoNotificacion = :noti and tMxNoti.pasivo = false ";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setInteger("idMx", idMx);
        q.setString("noti", noti);
        return (TipoMx_TipoNotificacion) q.uniqueResult();
    }

    /**
     * Actualiza o agrega una asociacion tipo de Muestra y Notificacion
     *
     * @param dto Objeto a actualizar o agregar
     * @throws Exception
     */
    public void addOrUpdateTMxNoti(TipoMx_TipoNotificacion dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.saveOrUpdate(dto);
            }
            else
                throw new Exception("Objeto NULL");
        }catch (Exception ex){
            logger.error("Error al agregar o actualizar asociacion de Tipo de Mx y Notificacion",ex);
            throw ex;
        }
    }


    public TipoMx_TipoNotificacion getTipoMxNotiById(Integer id){
        String query = "from TipoMx_TipoNotificacion mxNoti where mxNoti.id = :id and mxNoti.pasivo = false";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setInteger("id", id);
        return (TipoMx_TipoNotificacion) q.uniqueResult();


    }

    public Integer overrideRequestsByidMxNoti(Integer idMxNoti) {
        // Retrieve session from Hibernate
        Session s = sessionFactory.openSession();
        Transaction tx = s.beginTransaction();

        String hql = "update Dx_TipoMx_TipoNoti set pasivo=true where tipoMx_tipoNotificacion.id= :idMxNoti and pasivo = false ";
        int updateEntities = s.createQuery( hql )
                .setParameter("idMxNoti", idMxNoti)
                .executeUpdate();
        tx.commit();
        s.close();
        return updateEntities;
    }

    public Integer overrideStudiesByidMxNoti(Integer idMxNoti) {
        // Retrieve session from Hibernate
        Session s = sessionFactory.openSession();
        Transaction tx = s.beginTransaction();

        String hql = "update Estudio_TipoMx_TipoNoti set pasivo=true where tipoMx_tipoNotificacion.id= :idMxNoti and pasivo = false ";
        int updateEntities = s.createQuery( hql )
                .setParameter("idMxNoti", idMxNoti)
                .executeUpdate();
        tx.commit();
        s.close();
        return updateEntities;
    }

}
