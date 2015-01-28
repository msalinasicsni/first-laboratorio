package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.examen.CatalogoExamenes;
import ni.gob.minsa.laboratorio.domain.examen.Examen_Dx;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by FIRSTICT on 11/21/2014.
 */
@Service("examenesService")
@Transactional
public class ExamenesService {

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;

    /**
     *
     * @param idExamen
     * @return
     */
    public CatalogoExamenes getExamenesById(int idExamen){
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery("from CatalogoExamenes where idExamen =:idExamen");
        q.setInteger("idExamen", idExamen);
        return (CatalogoExamenes)q.uniqueResult();
    }

    /**
     * Obtiene una lista de examenes por diagnóstico según ids de examenes enviados
     * @param idExamenes id de los examenes a filtrar, separados por coma Ejm: 1,2,3
     * @return List<Examen_Dx>
     */
    public List<Examen_Dx> getExamenesByIds(String idExamenes){
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery("select edx from Examen_Dx as edx inner join edx.examen as ex inner join edx.diagnostico as dx " +
                " where ex.idExamen in("+ idExamenes +")");
        return q.list();
    }
}
