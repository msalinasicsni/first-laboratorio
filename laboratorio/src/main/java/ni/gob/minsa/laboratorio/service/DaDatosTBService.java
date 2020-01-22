package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.tb.DaDatosTB;
import ni.gob.minsa.laboratorio.utilities.reportes.DatosTB;
import ni.gob.minsa.laboratorio.utilities.reportes.DatosVIH;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by miguel on 22/1/2020.
 */
@Service("daDatosTBService")
@Transactional
public class DaDatosTBService {

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    public List<DaDatosTB> getDaDatosTBPersona(long idPerson){
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("From DaDatosTB dtb where dtb.idNotificacion.persona.personaId =:idPerson");
        query.setParameter("idPerson", idPerson);
        return query.list();
    }

    public DaDatosTB getDaDatosTB(String idNotificacion){
        Session session = sessionFactory.getCurrentSession();
        return (DaDatosTB) session.createCriteria(DaDatosTB.class)
                .add(Restrictions.eq("idNotificacion.idNotificacion", idNotificacion)).uniqueResult();

    }

    public DatosTB getDatosTB(String idNotificacion){
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("select tb.idNotificacion.idNotificacion as idNotificacion, tb.idNotificacion.persona.personaId as idPersona, " +
                " tb.poblacion as poblacion, tb.comorbilidades as comorbilidades, tb.categoria as categoria, tb.localizacion as localizacion " +
                "FROM DaDatosTB tb where tb.idNotificacion.idNotificacion = '" + idNotificacion + "'");
        query.setResultTransformer(Transformers.aliasToBean(DatosTB.class));
        return (DatosTB) query.uniqueResult();

    }

    public void saveDaDatosTB(DaDatosTB daDatosVIH) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(daDatosVIH);
    }
}
