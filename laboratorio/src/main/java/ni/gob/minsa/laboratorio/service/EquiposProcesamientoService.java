package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.examen.EquiposProcesamiento;
import ni.gob.minsa.laboratorio.domain.examen.Examen_Equipo;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Miguel Salinas on 25/07/2019.
 * V1.0
 */
@Service("equiposProcesamientoService")
@Transactional
public class EquiposProcesamientoService {

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;

    public void saveEquipo(EquiposProcesamiento equipo) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(equipo);
    }

    public void saveExamenEquipo(Examen_Equipo examenEquipo) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(examenEquipo);
    }

    public List<EquiposProcesamiento> getEquipos(){
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("from EquiposProcesamiento");
        return query.list();
    }

    public EquiposProcesamiento getEquipo(int idEquipo){
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("from EquiposProcesamiento where idEquipo = :idEquipo ");
        query.setParameter("idEquipo", idEquipo);
        return (EquiposProcesamiento)query.uniqueResult();
    }

    public List<EquiposProcesamiento> getExamenesEquipo(int idEquipo){
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("select ee from Examen_Equipo ee inner join ee.equipo e " +
                "where ee.pasivo = false and e.pasivo = false and e.idEquipo = :idEquipo ");
        query.setParameter("idEquipo", idEquipo);
        return query.list();
    }
}
