package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.utilities.reportes.DatosSolicitud;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service("solicitudService")
@Transactional
public class SolicitudService {

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;

    public List<DatosSolicitud> getSolicitudesVIHTB(boolean incluirVIH, boolean incluirTB, String idPersona){
        Session session = sessionFactory.getCurrentSession();
        String sQuery = "select t1.idSolicitudDx as idSolicitud , t1.codDx.nombre as nombre, t2.codigoLab as codigoMx, t2.estadoMx.valor as estadoMx, " +
                " t2.codTipoMx.nombre as tipoMx, to_char(t1.fechaHSolicitud, 'DD/MM/YYYY HH24:mi:ss') as fechaSolicitud, t1.aprobada as aprobada, to_char(t1.fechaAprobacion, 'DD/MM/YYYY HH24:mi:ss') as fechaAprobacion " +
                "from DaSolicitudDx t1, DaTomaMx t2, DaNotificacion t3 " +
                "where t1.idTomaMx = t2.idTomaMx and t2.idNotificacion = t3.idNotificacion " +
                " and t3.persona.personaId = :idPersona ";
        if (incluirVIH && incluirTB){
            sQuery += "and t3.codTipoNotificacion.codigo in ('TPNOTI|VIH','TPNOTI|TB')";
        }else if (incluirVIH){
            sQuery += "and t3.codTipoNotificacion.codigo = 'TPNOTI|VIH' ";
        }else if (incluirTB){
            sQuery += "and t3.codTipoNotificacion.codigo = 'TPNOTI|TB' ";
        }
        sQuery += "order by t1.fechaHSolicitud desc";

        Query query = session.createQuery(sQuery);
        query.setParameter("idPersona",Long.valueOf(idPersona));
        query.setResultTransformer(Transformers.aliasToBean(DatosSolicitud.class));
        return query.list();
    }

    public List<DatosSolicitud> getSolicitudesByIdPersonTipoNoti(String idPersona, String tipoNoti){
        Session session = sessionFactory.getCurrentSession();
        String sQuery = "select t1.idSolicitudDx as idSolicitud , t1.codDx.nombre as nombre, t2.codigoLab as codigoMx, t2.estadoMx.valor as estadoMx, " +
                " t2.codTipoMx.nombre as tipoMx, to_char(t1.fechaHSolicitud, 'DD/MM/YYYY HH24:mi:ss') as fechaSolicitud, t1.aprobada as aprobada, to_char(t1.fechaAprobacion, 'DD/MM/YYYY HH24:mi:ss') as fechaAprobacion " +
                "from DaSolicitudDx t1, DaTomaMx t2, DaNotificacion t3 " +
                "where t1.idTomaMx = t2.idTomaMx and t2.idNotificacion = t3.idNotificacion " +
                " and t3.persona.personaId = :idPersona and t3.codTipoNotificacion.codigo = :tipoNoti " +
                " order by t1.fechaHSolicitud desc";

        Query query = session.createQuery(sQuery);
        query.setParameter("idPersona",Long.valueOf(idPersona));
        query.setParameter("tipoNoti", tipoNoti);
        query.setResultTransformer(Transformers.aliasToBean(DatosSolicitud.class));
        return query.list();
    }

    public DatosSolicitud getSolicitudesByIdSolicitud(String idSolicitud){
        Session session = sessionFactory.getCurrentSession();
        String sQuery = "select t1.idSolicitudDx as idSolicitud , t1.codDx.nombre as nombre, t2.codigoLab as codigoMx, t2.estadoMx.valor as estadoMx, " +
                " t2.codTipoMx.nombre as tipoMx, to_char(t1.fechaHSolicitud, 'DD/MM/YYYY HH24:mi:ss') as fechaSolicitud, t1.aprobada as aprobada, to_char(t1.fechaAprobacion, 'DD/MM/YYYY HH24:mi:ss') as fechaAprobacion " +
                "from DaSolicitudDx t1, DaTomaMx t2, DaNotificacion t3 " +
                "where t1.idTomaMx = t2.idTomaMx and t2.idNotificacion = t3.idNotificacion " +
                " and t1.idSolicitudDx = :idSolicitud " +
                " order by t1.fechaHSolicitud desc";

        Query query = session.createQuery(sQuery);
        query.setParameter("idSolicitud", idSolicitud);
        query.setResultTransformer(Transformers.aliasToBean(DatosSolicitud.class));
        return (DatosSolicitud)query.uniqueResult();
    }
}
