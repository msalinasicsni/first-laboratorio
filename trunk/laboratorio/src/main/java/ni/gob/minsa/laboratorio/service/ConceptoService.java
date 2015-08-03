package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.concepto.Catalogo_Lista;
import ni.gob.minsa.laboratorio.domain.concepto.Concepto;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by souyen-ics.
 */
@Service("conceptoService")
@Transactional
public class ConceptoService {

    private Logger logger = LoggerFactory.getLogger(ConceptoService.class);

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;

   public ConceptoService(){}

    /**
     * Obtiene una lista de los conceptos
     */

    public List<Concepto> getConceptsList(boolean excluirFechas) throws Exception {
        String query = "from Concepto as a where a.pasivo = false ";
        if (excluirFechas){ //excluir conceptos con tipo dato fecha
            query += " and a.tipo.codigo != 'TPDATO|FCH'";
        }
        query += " order by a.fechahRegistro";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);

        return q.list();
    }

    /**
     * Obtiene un registro de Concepto
     * @param id  IdConcepto
     */
    @SuppressWarnings("unchecked")
    public Concepto getConceptById(Integer id){
        Session session = sessionFactory.getCurrentSession();
        Criteria cr = session.createCriteria(Concepto.class, "reg");
        cr.add(Restrictions.eq("reg.idConcepto", id));
        return (Concepto) cr.uniqueResult();
    }

    /**
     * Actualiza o agrega una Registro de Concepto
     *
     * @param dto Objeto a actualizar o agregar
     * @throws Exception
     */
    public void addOrUpdateConcept(Concepto dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.saveOrUpdate(dto);
            }
            else
                throw new Exception("Objeto Concepto es NULL");
        }catch (Exception ex){
            logger.error("Error al agregar o actualizar concepto",ex);
            throw ex;
        }
    }


    /**
     * Obtiene valores de una lista segun idConcepto
     * @param id  IdConcepto
     */
    @SuppressWarnings("unchecked")

    public List<Catalogo_Lista> getValuesByIdConcepto(Integer id) throws Exception {
        String query = "from Catalogo_Lista as cat where cat.pasivo = false and cat.idConcepto = :id order by cat.fechaHRegistro";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setInteger("id", id);
        return q.list();
    }

    /**
     * Obtiene un registro de Catalogo_Lista
     * @param id  IdCatagoLista
     */
    @SuppressWarnings("unchecked")
    public Catalogo_Lista getCatalogoListaById(Integer id){
        Session session = sessionFactory.getCurrentSession();
        Criteria cr = session.createCriteria(Catalogo_Lista.class, "cat");
        cr.add(Restrictions.eq("cat.idCatalogoLista", id));
        return (Catalogo_Lista) cr.uniqueResult();
    }


    /**
     * Actualiza o agrega una registro de catalogo_lista
     *
     * @param dto Objeto a actualizar o agregar
     * @throws Exception
     */
    public void addOrUpdateValue(Catalogo_Lista dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.saveOrUpdate(dto);
            }
            else
                throw new Exception("Objeto Catalogo_lista es NULL");
        }catch (Exception ex){
            logger.error("Error al agregar o actualizar catalogo_lista",ex);
            throw ex;
        }
    }



}
