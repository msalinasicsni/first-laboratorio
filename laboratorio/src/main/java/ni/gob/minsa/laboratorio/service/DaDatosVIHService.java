package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.vih.DaDatosVIH;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service("daDatosVIHService")
@Transactional
public class DaDatosVIHService {
	
	@Resource(name="sessionFactory")
	private SessionFactory sessionFactory;
	
	@SuppressWarnings("unchecked")
	public List<DaDatosVIH> getDaDatosVIHPersona(long idPerson){
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("From DaDatosVIH dvih where dvih.idNotificacion.persona.personaId =:idPerson");
		query.setParameter("idPerson", idPerson);
		return query.list();
	}
	
	public DaDatosVIH getDaDatosVIH(String idNotificacion){
		Session session = sessionFactory.getCurrentSession();
		return (DaDatosVIH) session.createCriteria(DaDatosVIH.class)
					.add(Restrictions.eq("idNotificacion.idNotificacion", idNotificacion)).uniqueResult();
				   
	}
	
	public void saveDaDatosVIH(DaDatosVIH daDatosVIH) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(daDatosVIH.getIdNotificacion());
		session.saveOrUpdate(daDatosVIH);
	}
}