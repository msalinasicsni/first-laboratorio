package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.irag.DaIrag;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by souyen-ics
 */
@Service("daIragService")
@Transactional
public class DaIragService {

    static final Logger logger = LoggerFactory.getLogger(DaIragService.class);

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;


    /**
     * Retorna Ficha de Vigilancia Integrada
     * @param id
     */
    public DaIrag getFormById(String id) {

        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery("FROM DaIrag vi where vi.idNotificacion.idNotificacion = '" + id + "'");
        return (DaIrag) query.uniqueResult();

    }

    /**
     * Guarda o actualiza una notificacion irag
     */
    public void saveOrUpdateIrag(DaIrag irag) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(irag.getIdNotificacion());
        session.saveOrUpdate(irag);
    }

}
