package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.vigilanciaSindFebril.DaSindFebril;
import ni.gob.minsa.laboratorio.utilities.reportes.DatosDaSindFebril;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service("sindFebrilService")
@Transactional
public class SindFebrilService {
	
	@Resource(name="sessionFactory")
	private SessionFactory sessionFactory;
	
	@SuppressWarnings("unchecked")
	public List<DaSindFebril> getDaSindFebrilesPersona(long idPerson){
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("From DaSindFebril sf where sf.idNotificacion.persona.personaId =:idPerson");
		query.setParameter("idPerson", idPerson);
		return query.list();
	}
	
	public DaSindFebril getDaSindFebril(String idNotificacion){
		Session session = sessionFactory.getCurrentSession();
		return (DaSindFebril) session.createCriteria(DaSindFebril.class)
					.add(Restrictions.eq("idNotificacion.idNotificacion", idNotificacion)).uniqueResult();
				   
	}
	
	public void saveSindFebril(DaSindFebril daSindFebril) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(daSindFebril.getIdNotificacion());
		session.saveOrUpdate(daSindFebril);
	}


    public DatosDaSindFebril getDaSindFebrilV2(String idNotificacion){
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(" select sf.nombPadre as nombPadre, sf.fechaFicha as fechaFicha,  " +
                "coalesce((select c.valor from Procedencia c where c.codigo = sf.codProcedencia.codigo), null) as codProcedencia, " +
                "coalesce((select c.valor from Respuesta c where c.codigo = sf.hosp.codigo), null) as hosp, " +
                "coalesce((select c.valor from Respuesta c where c.codigo = sf.fallecido.codigo), null) as fallecido, " +
                "sf.fechaIngreso as fechaIngreso, sf.fechaFallecido as fechaFallecido,  sf.dxPresuntivo as dxPresuntivo "+
                "from DaSindFebril sf where sf.idNotificacion.idNotificacion = :idNotificacion ");
        query.setParameter("idNotificacion",idNotificacion);
        query.setResultTransformer(Transformers.aliasToBean(DatosDaSindFebril.class));
        return (DatosDaSindFebril) query.uniqueResult();
}

    public void deleteDaSindFebril(DaSindFebril dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.delete(dto);
            }
            else
                throw new Exception("Objeto DaSindFebril es NULL");
        }catch (Exception ex){
            ex.printStackTrace();
            throw ex;
        }

    }
}