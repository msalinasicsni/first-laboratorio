package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.examen.CatalogoExamenes;
import ni.gob.minsa.laboratorio.domain.muestra.DaOrdenExamen;
import ni.gob.minsa.laboratorio.domain.muestra.DaTomaMx;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by souyen-ics on 11-05-14.
 */
@Service("tomaMxService")
@Transactional
public class TomaMxService {

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;


    public DaOrdenExamen getOrdenExamenById(String idOrden){
        String query = "from DaOrdenExamen where idOrdenExamen = :idOrden ";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("idOrden", idOrden);
        return (DaOrdenExamen)q.uniqueResult();
    }

    public void updateOrdenExamen(DaOrdenExamen dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.update(dto);
            }
            else
                throw new Exception("Objeto Orden Examen es NULL");
        }catch (Exception ex){
            ex.printStackTrace();
            throw ex;
        }
    }

    /**
     *
     *
     * @param codMx
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public List<CatalogoExamenes> getExamenes(String codMx, String tipoNoti) throws Exception {
        String query = "select ca from CatalogoExamenes as ca where ca.tipoNotificacion = :tipoNoti and ca.codTipoMx like :codMx" ;
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
         q.setString("codMx", "%" +codMx + "%");
        q.setString("tipoNoti", tipoNoti);
        return q.list();
    }

    @SuppressWarnings("unchecked")
    public List<CatalogoExamenes> getCatalogoExamenes(){
        //Retrieve session Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM CatalogoExamenes cat where cat.pasivo = false");
        //retrieve all
        return query.list();

    }

    /**
     * Agrega Registro de Toma de Muestra
     */
    public void addTomaMx(DaTomaMx toma) {
        Session session = sessionFactory.getCurrentSession();
        session.save(toma);
    }

    /**
     * Agrega Orden
     */
    public void addOrdenExamen(DaOrdenExamen orden) {
        Session session = sessionFactory.getCurrentSession();
        session.save(orden);
    }


    /**
     * Retorna examen
     * @param id
     */
    public CatalogoExamenes getExamById(String id){
        String query = "from CatalogoExamenes where idExamen = :id";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("id", id);
        return (CatalogoExamenes)q.uniqueResult();
    }

    /**
     * Retorna toma de muestra
     * @param id
     */
    public DaTomaMx getTomaMxById(String id){
        String query = "from DaTomaMx where idTomaMx = :id";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("id", id);
        return (DaTomaMx)q.uniqueResult();
    }

}
