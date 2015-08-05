package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.notificacion.DaNotificacion;
import org.apache.commons.codec.language.Soundex;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

/**
 * Created by souyen-ics on 11-18-14.
 */

@Service("daNotificacionService")
@Transactional
public class DaNotificacionService {

    static final Logger logger = LoggerFactory.getLogger(DaNotificacion.class);

    @Resource(name ="sessionFactory")
    public SessionFactory sessionFactory;


    /**
     * Agrega Notificacion
     */
    public void addNotification(DaNotificacion noti) {
        Session session = sessionFactory.getCurrentSession();
        session.save(noti);
    }

    /**
     * Retorna notificacion
     * @param idNotificacion
     */
    public DaNotificacion getNotifById(String idNotificacion) {

        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery("FROM DaNotificacion noti where noti.idNotificacion = '" + idNotificacion + "'");
        return (DaNotificacion) query.uniqueResult();

    }

    @SuppressWarnings("unchecked")
    public List<DaNotificacion> getNoticesByPerson(long idPersona, String tipoNotificacion){
        Session session = sessionFactory.getCurrentSession();
        //todas las notificaciones tipo CASO ESPECIAL registradas para la persona seleccionada
            return session.createCriteria(DaNotificacion.class, "noti")
                    .createAlias("noti.persona", "persona")
                    .add(Restrictions.and(
                                    Restrictions.eq("persona.personaId", idPersona),
                                    Restrictions.eq("codTipoNotificacion.codigo", tipoNotificacion))
                    )
                    .list();

    }


    public void updateNotificacion(DaNotificacion dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.update(dto);
            }
            else
                throw new Exception("Objeto DaNotificacion es NULL");
        }catch (Exception ex){
            ex.printStackTrace();
            throw ex;
        }
    }

    /******************************************/
    /*******************Otras Muestras***********************/
    /******************************************/

    @SuppressWarnings("unchecked")
    public List<DaNotificacion> getNoticesByApplicant(String idSolicitante, String tipoNotificacion){
        Session session = sessionFactory.getCurrentSession();
        //todas las notificaciones tipo CASO ESPECIAL registradas para la persona seleccionada
        return session.createCriteria(DaNotificacion.class, "noti")
                .createAlias("noti.solicitante", "solicitante")
                .add(Restrictions.and(
                                Restrictions.eq("solicitante.idSolicitante", idSolicitante),
                                Restrictions.eq("codTipoNotificacion.codigo", tipoNotificacion))
                )
                .list();

    }

}
