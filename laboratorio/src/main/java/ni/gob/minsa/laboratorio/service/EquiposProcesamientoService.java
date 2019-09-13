package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.examen.CatalogoExamenes;
import ni.gob.minsa.laboratorio.domain.examen.EquiposProcesamiento;
import ni.gob.minsa.laboratorio.domain.examen.Examen_Equipo;
import org.apache.poi.util.StringUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.security.web.util.TextEscapeUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
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

    public int anularExamenEquipo(int idExamenEquipo){
        Session session = sessionFactory.getCurrentSession();
        int afectados = session.createQuery("update Examen_Equipo set pasivo = true where idExamenEquipo = :idExamenEquipo ")
                .setParameter("idExamenEquipo", idExamenEquipo)
                .executeUpdate();
        return afectados;
    }

    public List<Examen_Equipo> getExamenesEquipo(int idEquipo){
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("select ee from Examen_Equipo ee inner join ee.equipo e " +
                "where ee.pasivo = false and e.pasivo = false and e.idEquipo = :idEquipo ");
        query.setParameter("idEquipo", idEquipo);
        return query.list();
    }

    public List<CatalogoExamenes> getExamenesDisponiblesEquipo(int idEquipo){
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("select ex from CatalogoExamenes ex where pasivo = false  and idExamen not in (select ee.examen.idExamen from Examen_Equipo ee inner join ee.equipo e " +
                "where ee.pasivo = false and e.pasivo = false and e.idEquipo = :idEquipo )");
        query.setParameter("idEquipo", idEquipo);
        return query.list();
    }

    public List<EquiposProcesamiento> getEquiposExamen(int idExamen){
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("select ee.equipo from Examen_Equipo ee inner join ee.examen ex where ex.idExamen = :idExamen ");
        query.setParameter("idExamen", idExamen);
        return query.list();
    }

    public boolean examenIsProcessedInfinity(int idExamen){
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("select eq from Examen_Equipo ee inner join ee.examen ex inner join ee.equipo eq " +
                "where ex.idExamen = :idExamen and  upper(eq.nombre) like '%INFINITY%' and eq.pasivo = false  and ee.pasivo = false ");
        query.setParameter("idExamen", idExamen);
        return query.list().size()>0;
    }

    public String ordersProcessedInInfinity(String idTomaMx){
        Session session = sessionFactory.getCurrentSession();
        List<Long> nse = new ArrayList<Long>();
        //se toman las que son de diagnóstico.
        Query q = session.createQuery("select exs.idExamen from Examen_Equipo ee inner join ee.examen ex inner join ee.equipo eq, " +
                "OrdenExamen as oe inner join oe.solicitudDx dx inner join dx.idTomaMx as mx inner join oe.codExamen exs " +
                "where exs.idExamen = ex.idExamen and mx.idTomaMx =:idTomaMx and dx.anulado = false and oe.anulado = false and ee.pasivo = false ");
        q.setParameter("idTomaMx",idTomaMx);
        nse = q.list();
        //se toman las que son de estudio
        Query q2 = session.createQuery("select exs.idExamen from Examen_Equipo ee inner join ee.examen ex inner join ee.equipo eq, " +
                "OrdenExamen as oe inner join oe.solicitudEstudio es inner join es.idTomaMx as mx inner join oe.codExamen exs " +
                "where exs.idExamen = ex.idExamen and mx.idTomaMx =:idTomaMx and es.anulado = false and oe.anulado = false and ee.pasivo = false ");
        q2.setParameter("idTomaMx",idTomaMx);
        nse.addAll(q2.list());

        if (nse.size()>0)
            return StringUtil.join(nse.toArray(), ",");
        else
            return null;
    }
}
