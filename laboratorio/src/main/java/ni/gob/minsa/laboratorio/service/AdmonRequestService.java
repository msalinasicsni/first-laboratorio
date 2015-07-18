package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.muestra.Catalogo_Dx;
import ni.gob.minsa.laboratorio.domain.muestra.Catalogo_Estudio;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by souyen-ics.
 */
@Service("admonRequestService")
@Transactional
public class AdmonRequestService {

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;

    private Logger logger = LoggerFactory.getLogger(AdmonRequestService.class);


    public AdmonRequestService() {
    }


    public Catalogo_Dx getDxRecord(String nombre, Integer idArea){
        String query = "from Catalogo_Dx dx where dx.nombre = :nombre and dx.area.id = :idArea and dx.pasivo = false";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("nombre", nombre);
        q.setInteger("idArea", idArea);
        return (Catalogo_Dx) q.uniqueResult();
    }

    public Catalogo_Estudio getStudyRecord(String nombre, Integer idArea){
        String query = "from Catalogo_Estudio est where est.nombre = :nombre and est.area.id = :idArea and est.pasivo = false";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("nombre", nombre);
        q.setInteger("idArea", idArea);
        return (Catalogo_Estudio) q.uniqueResult();
    }

    /**
     * Actualiza o agrega un dx
     *
     * @param dto Objeto a actualizar o agregar
     * @throws Exception
     */
    public void addOrUpdateDx(Catalogo_Dx dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.saveOrUpdate(dto);
            }
            else
                throw new Exception("Objeto NULL");
        }catch (Exception ex){
            logger.error("Error al agregar o actualizar un dx",ex);
            throw ex;
        }
    }

    /**
     * Actualiza o agrega un dx
     *
     * @param dto Objeto a actualizar o agregar
     * @throws Exception
     */
    public void addOrUpdateStudy(Catalogo_Estudio dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.saveOrUpdate(dto);
            }
            else
                throw new Exception("Objeto NULL");
        }catch (Exception ex){
            logger.error("Error al agregar o actualizar un estudio",ex);
            throw ex;
        }
    }

    public Catalogo_Dx getDxRecordById(Integer id){
        String query = "from Catalogo_Dx dx where  dx.id = :id ";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setInteger("id", id);
        return (Catalogo_Dx) q.uniqueResult();
    }

    public Catalogo_Estudio getStudyRecordById(Integer id){
        String query = "from Catalogo_Estudio est where  est.id = :id ";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setInteger("id", id);
        return (Catalogo_Estudio) q.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<Catalogo_Estudio> getAllStudies() throws Exception {
        String query = "from Catalogo_Estudio" ;
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        return q.list();
    }

    @SuppressWarnings("unchecked")
    public List<Catalogo_Dx> getAllDxs() throws Exception {
        String query = "from Catalogo_Dx dx " ;
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        return q.list();
    }
}
