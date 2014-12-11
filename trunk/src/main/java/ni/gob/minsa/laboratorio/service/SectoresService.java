package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.poblacion.Sectores;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


@Service("sectoresService")
@Transactional
public class SectoresService {
	
	@Resource(name="sessionFactory")
	private SessionFactory sessionFactory;


	@SuppressWarnings("unchecked")
	public List<Sectores> getSectores() {
		// Retrieve session from Hibernate
		Session session = sessionFactory.getCurrentSession();
		// Create a Hibernate query (HQL)
		Query query = session.createQuery("FROM Sectores");
		// Retrieve all
		return  query.list();
	}

    public List<Sectores> getSectoresByMunicipio(String codMunicipio) {
        // Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        // Create a Hibernate query (HQL)
        Query query = session.createQuery("FROM Sectores where municipio.codigoNacional = :codMunicipio and pasivo = :pasivo");
        query.setParameter("codMunicipio",codMunicipio);
        query.setParameter("pasivo",'0');
        // Retrieve all
        return  query.list();
    }

    public List<Sectores> getSectoresByUnidad(long codUnidad) {
        // Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        // Create a Hibernate query (HQL)
        Query query = session.createQuery("FROM Sectores where unidad.codigo = :codUnidad and pasivo = :pasivo");
        query.setParameter("codUnidad",codUnidad);
        query.setParameter("pasivo",'0');
        // Retrieve all
        return  query.list();
    }
}
