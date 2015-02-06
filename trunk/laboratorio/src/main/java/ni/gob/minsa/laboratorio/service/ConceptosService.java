package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.muestra.FiltroMx;
import ni.gob.minsa.laboratorio.domain.muestra.RecepcionMx;
import ni.gob.minsa.laboratorio.domain.resultados.Catalogo_Lista;
import ni.gob.minsa.laboratorio.domain.resultados.Conceptos;
import ni.gob.minsa.laboratorio.domain.resultados.TipoDato;
import org.apache.commons.codec.language.Soundex;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Restrictions;
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
@Service("conceptosService")
@Transactional
public class ConceptosService {

    private Logger logger = LoggerFactory.getLogger(ConceptosService.class);

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;

    public ConceptosService(){}

    /**
     * Agrega una Registro de Recepción de muestra
     *
     * @param dto Objeto a agregar
     * @throws Exception
     */
    public void addConcept(Conceptos dto) throws Exception {
        //String idMaestro;
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                //idMaestro = (String)
                session.save(dto);
            }
            else
                throw new Exception("Objeto Concepto es NULL");
        }catch (Exception ex){
            logger.error("Error al agregar Concepto",ex);
            throw ex;
        }
    }

    /**
     * Actualiza una Registro de Recepción de muestra
     *
     * @param dto Objeto a actualizar
     * @throws Exception
     */
    public void updateConcept(Conceptos dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.update(dto);
            }
            else
                throw new Exception("Objeto Concepto es NULL");
        }catch (Exception ex){
            logger.error("Error al actualizar Concepto",ex);
            throw ex;
        }
    }

    public List<Conceptos> getConceptosByExamen(Integer idExamen){
        String query = "from Conceptos as a where a.idExamen.idExamen = :idExamen order by orden asc";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idExamen", idExamen);
        return q.list();
    }

    public List<Conceptos> getConceptosActivosByExamen(Integer idExamen){
        String query = "from Conceptos as a where a.idExamen.idExamen = :idExamen and pasivo = false order by orden asc";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idExamen", idExamen);
        return q.list();
    }

    public Conceptos getConceptoById(Integer idConcepto){
        String query = "from Conceptos as a where idConcepto =:idConcepto";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idConcepto", idConcepto);
        return (Conceptos)q.uniqueResult();
    }

    public List<Catalogo_Lista> getCatalogoListaConceptoByIdExamen(Integer idExamen) throws Exception {
        String query = "Select a from Catalogo_Lista as a inner join a.idTipoDato tdl , Conceptos as c inner join c.tipoDato tdc " +
                "where a.pasivo = false and tdl.idTipoDato = tdc.idTipoDato and c.idExamen.idExamen =:idExamen" +
                " order by  a.valor";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idExamen",idExamen);

        return q.list();
    }

}
