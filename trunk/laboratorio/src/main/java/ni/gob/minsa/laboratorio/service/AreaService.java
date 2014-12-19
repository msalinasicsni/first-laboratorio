package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.examen.Area;
import ni.gob.minsa.laboratorio.domain.muestra.RecepcionMx;
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
 * Created by FIRSTICT on 12/18/2014.
 */
@Service("areaService")
@Transactional
public class AreaService {
    private Logger logger = LoggerFactory.getLogger(CatalogoService.class);

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;

    public List<Area> getAreas(){
        String query = "from Area order by nombre";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        return  q.list();
    }

    public Area getArea(Integer idArea){
        String query = "from Area as a where idArea= :idArea";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idArea", idArea);
        return  (Area)q.uniqueResult();
    }
}
