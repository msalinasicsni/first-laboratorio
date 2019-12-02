package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.vih.DaDatosVIH;

import ni.gob.minsa.laboratorio.utilities.reportes.DatosDaIrag;
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

    public DatosVIH getDatosVIH(String idNotificacion){
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("select vi.idNotificacion.codigoPacienteVIH as codigoVIH, vi.idNotificacion.codExpediente as expediente, " +
                " vi.resA1 as resA1, vi.resA2 as resA2, vi.fechaDxVIH as fechaDxVIH, vi.embarazo as embarazo, vi.estadoPx as estadoPx, vi.infOport as infOport, " +
                "vi.estaTx as estaTx, vi.fechaTAR as fechaTAR, vi.exposicionPeri as exposicionPeri, vi.cesarea as cesarea," +
                "coalesce((select c.nombre from Ocupacion c where c.codigo = vi.idNotificacion.persona.ocupacion.codigo), null) as ocupacion " +
                "FROM DaDatosVIH vi where vi.idNotificacion.idNotificacion = '" + idNotificacion + "'");
        query.setResultTransformer(Transformers.aliasToBean(DatosVIH.class));
        return (DatosVIH) query.uniqueResult();

    }
	
	public void saveDaDatosVIH(DaDatosVIH daDatosVIH) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(daDatosVIH.getIdNotificacion());
		session.saveOrUpdate(daDatosVIH);
	}
}