package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.estructura.Unidades;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para el objeto de Unidades
 *
 * @author Miguel Salinas
 */
@Service("unidadesService")
@Transactional
public class UnidadesService {

    @Resource(name="sessionFactory")
    public SessionFactory sessionFactory;

    public SessionFactory getSessionFactory(){
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory){
        if(this.sessionFactory == null){
            this.sessionFactory = sessionFactory;
        }
    }

    /**
     * Retorna todos los datos del Objeto <code> Unidades </code>
     *
     * @return una lista de  Unidades
     * @throws Exception
     */
    @Deprecated
    public List<Unidades> getAllUnidades() throws Exception {
        String query = "from Unidades where pasivo = :pasivo order by nombre asc";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("pasivo", '0');
        return q.list();
    }

    public List<Unidades> getUnidadesFromEntidades(int idEntidad) throws Exception {
        Session session = sessionFactory.getCurrentSession();
        String query = "from Unidades where pasivo = :pasivo and entidadAdtva =:idEntidad order by nombre asc";
        Query q = session.createQuery(query);
        q.setInteger("idEntidad",idEntidad);
        q.setParameter("pasivo", '0');
        return q.list();
    }

    /**
     * @param codUnidad Id para obtener un objeto en especifico del tipo <code>Unidades</code>
     * @return retorna un objeto filtrado del tipo <code>Unidades</code>
     * @throws Exception
     */
    public Unidades getUnidadByCodigo(Integer codUnidad) throws Exception {
        Session session = sessionFactory.getCurrentSession();
        String query = "from Unidades as a where codigo=:idUnidad order by nombre asc";
        Query q = session.createQuery(query);
        q.setInteger("idUnidad", codUnidad);
        return  (Unidades)q.uniqueResult();
    }

    public List<Unidades> getPrimaryUnitsByMunicipio_Silais(String codMunicipio, long codSilais, String[] codTiposUnidades) throws Exception {
        List<Unidades> res = new ArrayList<Unidades>();
        try {
            Session session = sessionFactory.getCurrentSession();
            Criteria criteria = session.createCriteria(Unidades.class);
            //criteria.createAlias("Unidades.municipio", "Municipio");
            criteria.add(Restrictions.eq("municipio.codigoNacional",codMunicipio));
            criteria.add(Restrictions.eq("entidadAdtva.codigo",codSilais));
            criteria.add(Restrictions.eq("pasivo",'0'));
            Long[] dataTipUnidades = new Long[codTiposUnidades.length];
            for(int i=0; i < codTiposUnidades.length; i++){
                dataTipUnidades[i] = Long.parseLong(codTiposUnidades[i]);
            }
            criteria.add(Restrictions.in("tipoUnidad", dataTipUnidades));
            criteria.addOrder(Order.asc("nombre"));
            res = criteria.list();

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return res;
    }

    public List<Unidades> getPrimaryUnitsBySilais(long codSilais, String[] codTiposUnidades) throws Exception {
        List<Unidades> res = new ArrayList<Unidades>();
        try {
            Session session = sessionFactory.getCurrentSession();
            Criteria criteria = session.createCriteria(Unidades.class);
            criteria.add(Restrictions.eq("entidadAdtva.codigo",codSilais));
            criteria.add(Restrictions.eq("pasivo",'0'));
            Long[] dataTipUnidades = new Long[codTiposUnidades.length];
            for(int i=0; i < codTiposUnidades.length; i++){
                dataTipUnidades[i] = Long.parseLong(codTiposUnidades[i]);
            }
            criteria.add(Restrictions.in("tipoUnidad", dataTipUnidades));
            criteria.addOrder(Order.asc("nombre"));
            res = criteria.list();

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return res;
    }

    public List<Unidades> getPUnitsHospByMuniAndSilais(String codMunicipio, String[] codTiposUnidades, long codSilais) throws Exception {
        List<Unidades> res = new ArrayList<Unidades>();
        try {
            Session session = sessionFactory.getCurrentSession();
            Criteria criteria = session.createCriteria(Unidades.class);
            criteria.add(Restrictions.eq("municipio.codigoNacional",codMunicipio));
            criteria.add(Restrictions.eq("entidadAdtva.codigo",codSilais));
            Long[] dataTipUnidades = new Long[codTiposUnidades.length];
            for(int i=0; i < codTiposUnidades.length; i++){
                dataTipUnidades[i] = Long.parseLong(codTiposUnidades[i]);
            }
            criteria.add(Restrictions.in("tipoUnidad", dataTipUnidades));
            criteria.addOrder(Order.asc("nombre"));
            res = criteria.list();

        }catch (Exception ex){
            ex.printStackTrace();
        }

        return res;
    }
}