package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.poblacion.Divisionpolitica;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Servicio para el objeto Division politica
 *
 * @author Miguel Salinas
 */

@Service("divisionPoliticaService")
@Transactional
public class DivisionPoliticaService {

    @Resource(name = "sessionFactory")
    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory(){
        return
                sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        if (this.sessionFactory == null) {
            this.sessionFactory = sessionFactory;
        }
    }

    /**
     * @return
     * @throws Exception
     */
    public List<Divisionpolitica> getAllDepartamentos() throws Exception {
        String query = "from Divisionpolitica where pasivo = :pasivo and dependencia is null order by nombre asc";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("pasivo",'0');
        return  q.list();
    }

    @Deprecated
    public List<Divisionpolitica> getAllMunicipios() throws Exception {
        String query = "from Divisionpolitica where pasivo = :pasivo and dependencia is not null order by nombre asc";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("pasivo",'0');
        return q.list();
    }

    public List<Divisionpolitica> getMunicipiosFromDepartamento(String codigoNacional) throws Exception {
        String query = "select muni from Divisionpolitica as muni, Divisionpolitica as depa " +
                "where muni.pasivo = :pasivo and muni.dependencia is not null and muni.dependencia.codigoNacional =depa.codigoNacional and depa.codigoNacional =:codigoNacional order by muni.nombre asc";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("codigoNacional", codigoNacional);
        q.setParameter("pasivo",'0');
        return q.list();
    }


    public Divisionpolitica getDepartamentoByMunicipi(String codNac){
        String query = "select muni.dependencia from Divisionpolitica as muni " +
                "where muni.dependencia is not null and muni.codigoNacional =:codigoNacional";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("codigoNacional", codNac);
        return (Divisionpolitica)q.uniqueResult();
    }

    public List<Divisionpolitica> getMunicipiosBySilais(long idSilais){
        String query = "select distinct muni from Divisionpolitica as muni, Unidades as uni " +
                "where muni.pasivo = :pasivo and  uni.entidadAdtva = :idSilais and uni.municipio = muni.codigoNacional order by muni.nombre"; // muni.dependenciaSilais =:idSilas";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setLong("idSilais", idSilais);
        q.setParameter("pasivo",'0');
        return q.list();
    }

    public Divisionpolitica getMunicipiosByUnidadSalud(String municipio){
        String query = "select muni from Divisionpolitica as muni, Unidades as uni " +
                "where muni.pasivo = :pasivo and  uni.municipio = :municipio and uni.municipio = muni.codigoNacional";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("municipio", municipio);
        q.setParameter("pasivo",'0');
        return (Divisionpolitica)q.uniqueResult();
    }

    public Divisionpolitica getDivisionPolitiacaByCodNacional(String codNac){
        String query = "from Divisionpolitica where codigoNacional =:codigoNacional";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("codigoNacional", codNac);
        return (Divisionpolitica)q.uniqueResult();
    }
}