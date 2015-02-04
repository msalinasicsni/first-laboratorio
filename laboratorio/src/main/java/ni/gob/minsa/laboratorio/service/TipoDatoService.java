package ni.gob.minsa.laboratorio.service;

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
     * Actualiza una Registro de Tipo de Dato
     * @param dto Objeto a actualizar
     * @throws Exception
     *//*
    public void updateDataType(TipoDato dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.update(dto);
            } else
                throw new Exception("Objeto TipoDato es NULL");
        } catch (Exception ex) {
            logger.error("Error al actualizar TipoDato", ex);
            throw ex;
        }
    }*/


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


}
