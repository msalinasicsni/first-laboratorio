package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.parametros.Imagen;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by FIRSTICT on 12/16/2015.
 * V1.0
 */
@Service("imagenesService")
@Transactional
public class ImagenesService {
    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;

    public List<Imagen> getImagenes(){
// Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        // Create a Hibernate query (HQL)
        Query query = session.createQuery("FROM Imagen ");
        // Retrieve all
        return  query.list();
    }

    public Imagen getImagenByName(String nombre){
// Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        // Create a Hibernate query (HQL)
        Query query = session.createQuery("FROM Imagen where nombre =:nombre ");
        query.setParameter("nombre",nombre);
        // Retrieve all
        return  (Imagen)query.uniqueResult();
    }

    public void saveOrUpdateImagen(Imagen imagen){
        Session session = sessionFactory.getCurrentSession();
        try {
            session.saveOrUpdate(imagen);
        }catch (Exception ex){
            ex.printStackTrace();
            throw ex;
        }
    }
}
