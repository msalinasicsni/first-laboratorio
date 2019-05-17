package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.concepto.Catalogo_Lista;
import ni.gob.minsa.laboratorio.domain.muestra.DatoSolicitud;
import ni.gob.minsa.laboratorio.domain.muestra.DatoSolicitudDetalle;
import ni.gob.minsa.laboratorio.domain.resultados.DetalleResultadoFinal;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by souyen-ics.
 */
@Service("datosSolicitudService")
@Transactional
public class DatosSolicitudService {

    private Logger logger = LoggerFactory.getLogger(DatosSolicitudService.class);

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;

    public DatosSolicitudService() {
    }


    public List<DatoSolicitud> getDatosRecepcionDxByIdSolicitud(Integer idSolicitud){
        String query = "from DatoSolicitud as a where a.diagnostico.idDiagnostico = :idSolicitud order by orden asc";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idSolicitud", idSolicitud);
        return q.list();
    }

    public DatoSolicitud getDatoRecepcionSolicitudById(Integer idConceptoSol){
        String query = "from DatoSolicitud as a where idConceptoSol =:idConceptoSol";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idConceptoSol", idConceptoSol);
        return (DatoSolicitud)q.uniqueResult();
    }

    /**
     * Agrega o Actualiza un Registro de DatoSolicitud
     * @param dto Objeto a actualizar
     * @throws Exception
     */
    public void saveOrUpdateDatoRecepcion(DatoSolicitud dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.saveOrUpdate(dto);
            }
            else
                throw new Exception("Objeto DatoSolicitud es NULL");
        }catch (Exception ex){
            logger.error("Error al actualizar o agregar DatoSolicitud",ex);
            throw ex;
        }
    }

    /**
     * Agrega o Actualiza un Registro de ConceptoSolicitud
     * @param dto Objeto a actualizar
     * @throws Exception
     */
    public void saveOrUpdateDetalleDatoRecepcion(DatoSolicitudDetalle dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.saveOrUpdate(dto);
            }
            else
                throw new Exception("Objeto DatoSolicitudDetalle es NULL");
        }catch (Exception ex){
            logger.error("Error al actualizar o agregar DatoSolicitudDetalle",ex);
            throw ex;
        }
    }

    public Integer deleteDetallesDatosRecepcionByTomaMx(String idTomaMx) {
        // Retrieve session from Hibernate
        Session s = sessionFactory.openSession();
        Transaction tx = s.beginTransaction();

        String hqlDelete = "delete DatoSolicitudDetalle dato where dato.solicitudDx in (from DaSolicitudDx where idTomaMx.idTomaMx = :idTomaMx)";
        int deletedEntities = s.createQuery( hqlDelete )
                .setString("idTomaMx", idTomaMx)
                .executeUpdate();
        tx.commit();
        s.close();
        return deletedEntities;
    }

    public List<Catalogo_Lista> getCatalogoListaConceptoByIdDx(Integer idDx) throws Exception {
        String query = "Select a from Catalogo_Lista as a inner join a.idConcepto tdl , DatoSolicitud as r inner join r.concepto tdc " +
                "where a.pasivo = false and tdl.idConcepto = tdc.idConcepto and r.diagnostico.idDiagnostico =:idDx" +
                " order by  a.valor";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idDx",idDx);
        return q.list();
    }

    public List<DatoSolicitud> getDatosRecepcionActivosDxByIdSolicitud(Integer idSolicitud){
        String query = "from DatoSolicitud as a where a.diagnostico.idDiagnostico = :idSolicitud and pasivo = false order by orden asc";

        Session session = sessionFactory.getCurrentSession();

        Query q = session.createQuery(query);
        q.setParameter("idSolicitud", idSolicitud);
        return q.list();
    }

    public List<DatoSolicitudDetalle> getDatosSolicitudDetalleBySolicitud(String idSolicitud){
        Session session = sessionFactory.getCurrentSession();
        String query = "select a from DatoSolicitudDetalle as a inner join a.solicitudDx as r where r.idSolicitudDx = :idSolicitud ";
        Query q = session.createQuery(query);
        q.setParameter("idSolicitud", idSolicitud);
        return q.list();
    }

    public List<DatoSolicitud> getDatosRecepcionActivosDxByIdSolicitudes(String idSolicitudes){
        String query = "from DatoSolicitud as a where a.diagnostico.idDiagnostico in ("+idSolicitudes+") and pasivo = false order by orden asc";

        Session session = sessionFactory.getCurrentSession();

        Query q = session.createQuery(query);
        return q.list();
    }
}
