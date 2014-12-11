package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.persona.Ocupacion;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Servicio para el objeto Ocupacion
 *
 * @author William Aviles
 */
@Service("ocupacionService")
@Transactional
public class OcupacionService {

    @Resource(name = "sessionFactory")
    SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
	public List<Ocupacion> getAllOcupaciones() {
        String query = "from Ocupacion where pasivo = :pasivo order by nombre asc";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("pasivo",false);
        return q.list();
    }

    /**
     * @param codigo Id para obtener un objeto en especifico del tipo <code>Ocupacion</code>
     * @return retorna un objeto filtrado del tipo <code>Ocupacion</code>
     * 
     */
    public Ocupacion getOcupacionByCodigo(String codigo) {
        String query = "from Ocupacion where codigo= :codigo order by nombre asc";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("codigo",codigo);
        return  (Ocupacion)q.uniqueResult();
    }
}