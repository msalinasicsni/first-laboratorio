package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.irag.DaIrag;
import ni.gob.minsa.laboratorio.domain.muestra.DaOrdenExamen;
import ni.gob.minsa.laboratorio.domain.muestra.DaTomaMx;
import ni.gob.minsa.laboratorio.domain.muestra.FiltroMx;
import ni.gob.minsa.laboratorio.domain.vigilanciaSindFebril.DaSindFebril;
import org.apache.commons.codec.language.Soundex;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Created by FIRSTICT on 11/21/2014.
 */
@Service("ordenExamenMxService")
@Transactional
public class OrdenExamenMxService {

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;

    public void updateOrdenExamen(DaOrdenExamen dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.update(dto);
            }
            else
                throw new Exception("Objeto Orden Examen es NULL");
        }catch (Exception ex){
            ex.printStackTrace();
            throw ex;
        }
    }

    public  DaOrdenExamen getOrdenExamenById(String idOrdenExamen){
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery("from DaOrdenExamen where idOrdenExamen =:idOrdenExamen");
        q.setParameter("idOrdenExamen",idOrdenExamen);
        return (DaOrdenExamen)q.uniqueResult();
    }

}
