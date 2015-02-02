package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.muestra.OrdenExamen;
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
 * Created by FIRSTICT on 11/21/2014.
 */
@Service("ordenExamenMxService")
@Transactional
public class OrdenExamenMxService {

    private Logger logger = LoggerFactory.getLogger(OrdenExamenMxService.class);

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;


    /**
     * Agrega una Registro de orden de examen
     *
     * @param dto Objeto a agregar
     * @throws Exception
     */
    public String addOrdenExamen(OrdenExamen dto) throws Exception {
        String idMaestro;
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                idMaestro = (String)session.save(dto);
            }
            else
                throw new Exception("Objeto Orden examen es NULL");
        }catch (Exception ex){
            logger.error("Error al agregar orden de examen",ex);
            throw ex;
        }
        return idMaestro;
    }

    /**
     * Actualiza una orden de examen
     *
     * @param dto Objeto a actualizar
     * @throws Exception
     */
    public void updateOrdenExamen(OrdenExamen dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.update(dto);
            }
            else
                throw new Exception("Objeto Orden Examen es NULL");
        }catch (Exception ex){
            logger.error("Error al actualizar recepción de muestra",ex);
            throw ex;
        }
    }

    public OrdenExamen getOrdenExamenById(String idOrdenExamen){
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery("from OrdenExamen where idOrdenExamen =:idOrdenExamen");
        q.setParameter("idOrdenExamen",idOrdenExamen);
        return (OrdenExamen)q.uniqueResult();
    }

    public List<OrdenExamen> getOrdenesExamenByIdMx(String idTomaMx){
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery("select oe from OrdenExamen as oe inner join oe.solicitudDx.idTomaMx as mx where mx.idTomaMx =:idTomaMx ");
        q.setParameter("idTomaMx",idTomaMx);
        return q.list();
    }

    public List<OrdenExamen> getOrdenesExamenNoAnuladasByCodigoUnico(String codigoUnico){
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery("select oe from OrdenExamen as oe inner join oe.solicitudDx.idTomaMx as mx where mx.codigoUnicoMx =:codigoUnico and oe.anulado = false ");
        q.setParameter("codigoUnico",codigoUnico);
        return q.list();
    }

    public List<OrdenExamen> getOrdenesExamenNoAnuladasByIdMx(String idTomaMx){
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery("select oe from OrdenExamen as oe inner join oe.solicitudDx.idTomaMx as mx where mx.idTomaMx =:idTomaMx and oe.anulado = false ");
        q.setParameter("idTomaMx",idTomaMx);
        return q.list();
    }

    public List<OrdenExamen> getOrdExamenNoAnulByIdMxIdDxIdExamen(String idTomaMx, int idDx, int idExamen){
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery("select oe from OrdenExamen as oe inner join oe.solicitudDx as sdx inner join sdx.idTomaMx as mx where mx.idTomaMx =:idTomaMx " +
                "and sdx.codDx.idDiagnostico = :idDx and oe.codExamen.idExamen = :idExamen and oe.anulado = false ");
        q.setParameter("idTomaMx",idTomaMx);
        q.setParameter("idDx",idDx);
        q.setParameter("idExamen",idExamen);
        return q.list();
    }

}
