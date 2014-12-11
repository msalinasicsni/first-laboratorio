package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.muestra.Alicuota;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by FIRSTICT on 12/10/2014.
 */
@Service("alicuotaService")
@Transactional
public class AlicuotaService {

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;

    public AlicuotaService(){}

    public List<Alicuota> getAlicuotas() throws Exception {
        String query = "from Alicuota order by alicuota";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        return q.list();
    }

    public Alicuota getAlicuota(int idAlicuota ) throws Exception {
        String query = "from Alicuota where idAlicuota = :idAlicuota order by alicuota";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idAlicuota",idAlicuota);
        return (Alicuota)q.uniqueResult();
    }
}
