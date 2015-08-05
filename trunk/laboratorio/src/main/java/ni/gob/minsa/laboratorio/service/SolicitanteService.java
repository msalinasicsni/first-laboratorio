package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.persona.SisPersona;
import ni.gob.minsa.laboratorio.domain.solicitante.Solicitante;
import org.apache.commons.codec.language.Soundex;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

/**
 * Created by FIRSTICT on 8/4/2015.
 * V1.0
 */
@Service("solicitanteService")
@Transactional
public class SolicitanteService {
    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;

    public void saveSolicitante(Solicitante solicitante){
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(solicitante);
    }

    public List<Solicitante> getSolicitantes(){
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery("from Solicitante ");
        return q.list();
    }

    public Solicitante getSolicitanteById(String idSolicitante){
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery("from Solicitante where idSolicitante = :idSolicitante");
        q.setParameter("idSolicitante",idSolicitante);
        return (Solicitante)q.uniqueResult();
    }

    public List<Solicitante> getSolicitantes(String filtro){
        try {
            filtro = URLDecoder.decode(filtro, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Session session = sessionFactory.getCurrentSession();
        if(filtro.matches("[0-9]*")){
            return session.createCriteria(Solicitante.class)
                    .add( Restrictions.or(
                                    Restrictions.eq("telefonoContacto", filtro),
                                    Restrictions.eq("telefono", filtro))
                    )
                    .list();
        }else {
            Criteria crit = session.createCriteria(Solicitante.class);
            String[] partes = filtro.split(" ");
            for(String parte : partes){
                Junction conditionGroup = Restrictions.disjunction();
                conditionGroup.add(Restrictions.ilike("nombre" , "%"+parte+"%" ));
                crit.add(conditionGroup);
            }

            return crit.list();
        }
    }
}
