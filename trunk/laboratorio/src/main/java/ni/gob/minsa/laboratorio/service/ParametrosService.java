package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.parametros.Parametro;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


@Service("parametrosService")
@Transactional
public class ParametrosService {
	
	@Resource(name="sessionFactory")
	private SessionFactory sessionFactory;


	@SuppressWarnings("unchecked")
	public List<Parametro> getParametros() {
		// Retrieve session from Hibernate
		Session session = sessionFactory.getCurrentSession();
		// Create a Hibernate query (HQL)
		Query query = session.createQuery("FROM Parametro ");
		// Retrieve all
		return  query.list();
	}

    @SuppressWarnings("unchecked")
    public Parametro getParametroByName(String nombre) {
        // Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        // Create a Hibernate query (HQL)
        Query query = session.createQuery("FROM Parametro where nombre =:nombre ");
        query.setParameter("nombre",nombre);
        // Retrieve all
        return  (Parametro)query.uniqueResult();
    }
}
