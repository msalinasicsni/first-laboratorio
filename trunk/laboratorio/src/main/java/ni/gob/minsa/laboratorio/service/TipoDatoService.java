package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.resultados.Catalogo_Lista;
import ni.gob.minsa.laboratorio.domain.resultados.TipoDato;
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
@Service("tipoDatoService")
@Transactional
public class TipoDatoService {

    private Logger logger = LoggerFactory.getLogger(TipoDatoService.class);

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;

   public TipoDatoService(){}

    /**
     * Obtiene una lista de los Tipos de Datos
     */

    public List<TipoDato> getDataTypeList() throws Exception {
        String query = "from TipoDato as a where a.pasivo = false order by a.fechahRegistro";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);

        return q.list();
    }

    /**
     * Obtiene un registro de Tipo de Dato
     * @param id  IdTipoDato
     */
    @SuppressWarnings("unchecked")
    public TipoDato getDataTypeById(Integer id){
        Session session = sessionFactory.getCurrentSession();
        Criteria cr = session.createCriteria(TipoDato.class, "reg");
        cr.add(Restrictions.eq("reg.idTipoDato", id));
        return (TipoDato) cr.uniqueResult();
    }

    /**
     * Actualiza o agrega una Registro de Tipo de Dato
     *
     * @param dto Objeto a actualizar o agregar
     * @throws Exception
     */
    public void addOrUpdateDataType(TipoDato dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.saveOrUpdate(dto);
            }
            else
                throw new Exception("Objeto Tipo Dato es NULL");
        }catch (Exception ex){
            logger.error("Error al agregar o actualizar tipo de dato",ex);
            throw ex;
        }
    }


    /**
     * Obtiene valores de una lista segun idTipoDato
     * @param id  IdTipoDato
     */
    @SuppressWarnings("unchecked")

    public List<Catalogo_Lista> getValuesByIdTipoDato(Integer id) throws Exception {
        String query = "from Catalogo_Lista as cat where cat.pasivo = false and cat.idTipoDato = :id order by cat.fechaHRegistro";
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
