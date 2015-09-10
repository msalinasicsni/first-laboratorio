package ni.gob.minsa.laboratorio.service;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by FIRSTICT on 8/7/2015.
 * V1.0
 */
@Service("homeService")
@Transactional
public class HomeService {
    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;

    public List<Object[]> getResumenMuestrasSILAIS(String laboratorio){
        Session session = sessionFactory.getCurrentSession();
        String sQuery = "select count(mx.idTomaMx) as total, sa.entidadAdtvaId, sa.nombre " +
                "from DaTomaMx as mx left join mx.codSilaisAtencion as sa " +
                "where mx.envio.laboratorioDestino.codigo = :laboratorio and (mx.estadoMx.codigo = 'ESTDMX|ENV' or mx.estadoMx.codigo = 'ESTDMX|TRAS')" +
                "group by sa.entidadAdtvaId, sa.nombre";


        Query q = session.createQuery(sQuery);

        q.setParameter("laboratorio",laboratorio);

        List<Object[]> resumenMxSilais= (List<Object[]>)q.list();
        return resumenMxSilais;
    }

    public List<Object[]> getResumenMuestrasSolicitud(String laboratorio){
        Session session = sessionFactory.getCurrentSession();
        String sQuery = "select count(mx.idTomaMx) as total, dx.idDiagnostico, dx.nombre " +
                "from DaSolicitudDx as sdx inner join sdx.idTomaMx as mx " +
                "inner join sdx.codDx as dx " +
                "where sdx.labProcesa.codigo = :laboratorio and (mx.estadoMx.codigo = 'ESTDMX|ENV' or mx.estadoMx.codigo = 'ESTDMX|TRAS')" +
                "group by dx.idDiagnostico, dx.nombre";

        String sQuery2 = "select count(mx.idTomaMx) as total, es.idEstudio, es.nombre " +
                "from DaSolicitudEstudio as sde inner join sde.idTomaMx as mx " +
                "inner join sde.tipoEstudio as es " +
                "where mx.envio.laboratorioDestino.codigo = :laboratorio and mx.estadoMx.codigo = 'ESTDMX|ENV'" +
                "group by es.idEstudio, es.nombre";


        Query q = session.createQuery(sQuery);

        q.setParameter("laboratorio",laboratorio);

        List<Object[]> resumenMxSolicitud= (List<Object[]>)q.list();

        q = session.createQuery(sQuery2);
        q.setParameter("laboratorio",laboratorio);

        resumenMxSolicitud.addAll(q.list());

        return resumenMxSolicitud;
    }
}
