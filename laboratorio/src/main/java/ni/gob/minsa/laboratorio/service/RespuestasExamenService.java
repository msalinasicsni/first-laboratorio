package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.concepto.Catalogo_Lista;
import ni.gob.minsa.laboratorio.domain.resultados.RespuestaExamen;
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
 * Created by FIRSTICT on 12/10/2014.
 * V1.0
 */
@Service("respuestasExamenService")
@Transactional
public class RespuestasExamenService {

    private Logger logger = LoggerFactory.getLogger(RespuestasExamenService.class);

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;

    public RespuestasExamenService(){}

    /**
     * Agrega una Registro de Recepción de muestra
     *
     * @param dto Objeto a agregar
     * @throws Exception
     */
    public void addResponse(RespuestaExamen dto) throws Exception {
        //String idMaestro;
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                //idMaestro = (String)
                session.save(dto);
            }
            else
                throw new Exception("Objeto Respuesta es NULL");
        }catch (Exception ex){
            logger.error("Error al agregar Respuesta",ex);
            throw ex;
        }
    }

    /**
     * Actualiza una Registro de Recepción de muestra
     *
     * @param dto Objeto a actualizar
     * @throws Exception
     */
    public void updateResponse(RespuestaExamen dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.update(dto);
            }
            else
                throw new Exception("Objeto Respuesta es NULL");
        }catch (Exception ex){
            logger.error("Error al actualizar Respuesta",ex);
            throw ex;
        }
    }

    public List<RespuestaExamen> getRespuestasByExamen(Integer idExamen){
        String query = "from RespuestaExamen as a where a.idExamen.idExamen = :idExamen order by orden asc";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idExamen", idExamen);
        return q.list();
    }

    public List<RespuestaExamen> getRespuestasActivasByExamen(Integer idExamen){
        String query = "from RespuestaExamen as a where a.idExamen.idExamen = :idExamen and pasivo = false order by orden asc";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idExamen", idExamen);
        return q.list();
    }

    public RespuestaExamen getRespuestaById(Integer idRespuesta){
        String query = "from RespuestaExamen as a where idRespuesta =:idRespuesta";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idRespuesta", idRespuesta);
        return (RespuestaExamen)q.uniqueResult();
    }

    public List<Catalogo_Lista> getCatalogoListaConceptoByIdExamen(Integer idExamen) throws Exception {
        String query = "Select distinct a from Catalogo_Lista as a inner join a.idConcepto tdl , RespuestaExamen as r inner join r.concepto tdc " +
                "where a.pasivo = false and tdl.idConcepto = tdc.idConcepto and r.idExamen.idExamen =:idExamen" +
                " order by  a.valor";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idExamen",idExamen);

        return q.list();
    }

    public Catalogo_Lista getCatalogoListaConceptoByIdLista(Integer idLista) throws Exception {
        String query = "Select c from Catalogo_Lista as c where c.pasivo = false and c.idCatalogoLista =:idLista";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idLista",idLista);
        return (Catalogo_Lista) q.uniqueResult();
    }

    public RespuestaExamen getRespuestaByNombre(String nombreRespuesta){
        String query = "from RespuestaExamen as a where nombre =:nombreRespuesta";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("nombreRespuesta", nombreRespuesta);
        return (RespuestaExamen)q.uniqueResult();
    }
}
