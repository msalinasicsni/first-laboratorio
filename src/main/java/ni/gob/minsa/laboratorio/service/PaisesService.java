package ni.gob.minsa.laboratorio.service;

import java.util.List;

import javax.annotation.Resource;


import ni.gob.minsa.laboratorio.domain.poblacion.Paises;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("paisesService")
@Transactional
public class PaisesService {
	
	@Resource(name="sessionFactory")
	private SessionFactory sessionFactory;


	@SuppressWarnings("unchecked")
	public List<Paises> getPaises() {
		// Retrieve session from Hibernate
		Session session = sessionFactory.getCurrentSession();
		// Create a Hibernate query (HQL)
		Query query = session.createQuery("FROM Paises");
		// Retrieve all
		return  query.list();
	}
}
