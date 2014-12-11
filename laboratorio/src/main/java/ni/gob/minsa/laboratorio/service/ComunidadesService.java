package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.poblacion.Comunidades;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Servicio para el objeto Comunidades
 *
 * @author Miguel Salinas
 */
@Service("comunidadesService")
@Transactional
public class ComunidadesService {

    @Resource(name = "sessionFactory")
    public SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     *
     *
     * @param idMunicipio
     * @return
     * @throws Exception
     */
    public List<Comunidades> getComunidades(String idMunicipio) throws Exception {
            String query = "select a from Comunidades as a, Sectores as s " +
                    "where a.pasivo = :pasivo and a.sector = s.codigo and s.municipio = :municipio  order by a.nombre";
            Session session = sessionFactory.getCurrentSession();
            Query q = session.createQuery(query);
            q.setString("municipio", idMunicipio);
            q.setParameter("pasivo",'0');
            return q.list();
    }

    public Comunidades getComunidad(String codigo) throws Exception {
        String query = "from Comunidades where codigo = :codigo ";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("codigo", codigo);
        return (Comunidades) q.uniqueResult();
    }

    public List<Comunidades> getComunidadesBySector(String codSector) throws Exception {
        String query = "select a from Comunidades as a, Sectores as s " +
                "where a.pasivo = :pasivo and a.sector = s.codigo and s.codigo = :codSector order by a.nombre";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("codSector", codSector);
        q.setParameter("pasivo",'0');
        return q.list();
    }
}