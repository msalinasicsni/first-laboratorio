package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.examen.Area;
import ni.gob.minsa.laboratorio.domain.examen.AreaDepartamento;
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

    public void saveArea(Area area) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(area);
    }

    public List<Area> getAreas(){
        String query = "from Area order by nombre";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        return  q.list();
    }

    public List<Area> getAreasActivas(){
        String query = "from Area where pasivo = false order by nombre";
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

    public List<Area> getAreasDisponiblesUser(String userName){
        String query = "select aa from Area as aa, AreaDepartamento ad, DepartamentoDireccion dd, DireccionLaboratorio dl, AutoridadLaboratorio al " +
                "where aa.idArea not in (select a.area.idArea from AutoridadArea as a where a.pasivo = false and a.user.username = :userName) " +
                "and aa.idArea = ad.area.idArea " +
                "and ad.depDireccion.idDepartDireccion = dd.idDepartDireccion " +
                "and dd.direccionLab.idDireccionLab = dl.idDireccionLab " +
                "and dl.laboratorio.codigo = al.laboratorio.codigo " +
                "and al.user.username = :userName " +
                "and aa.pasivo = false and ad.pasivo = false and dd.pasivo = false and dl.pasivo = false and al.pasivo = false " +
                "order by aa.nombre";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("userName",userName);
        return  q.list();
    }

    public List<Area> getAreasByDepartamento(Integer idDepartamento){
        String query = "select aa from Area aa, AreaDepartamento ad " +
                "where aa.idArea = ad.area.idArea and ad.depDireccion.departamento.idDepartamento = :idDepartamento " +
                "order by aa.nombre";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idDepartamento",idDepartamento);
        return  q.list();
    }

    public AreaDepartamento getAreaDepartamento(Integer idAreaDepartamento){
        String query = "from AreaDepartamento as a where idAreaDepartamento= :idAreaDepartamento";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idAreaDepartamento", idAreaDepartamento);
        return  (AreaDepartamento)q.uniqueResult();
    }
}
