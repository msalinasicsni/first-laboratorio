package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.muestra.Laboratorio;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by FIRSTICT on 12/11/2014.
 */
@Service("laboratoriosService")
@Transactional
public class LaboratoriosService {
    @Resource(name = "sessionFactory")
    private SessionFactory sessionFactory;

    public List<Laboratorio> getLaboratoriosInternos(){
        String query = "from Laboratorio where codTipo =:codTipo order by nombre";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("codTipo","INT");
        return q.list();
    }

    public List<Laboratorio> getLaboratoriosRegionales(){
        String query = "from Laboratorio where codTipo =:codTipo order by nombre";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("codTipo","REG");
        return q.list();
    }

    public Laboratorio getLaboratorioByCodigo(String codLaboratorio){
        String query = "from Laboratorio where codigo =:codLaboratorio order by nombre";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("codLaboratorio",codLaboratorio);
        return (Laboratorio)q.uniqueResult();
    }
}
