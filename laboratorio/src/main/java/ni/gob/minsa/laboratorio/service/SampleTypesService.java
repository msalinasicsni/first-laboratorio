package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.muestra.TipoMx;
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
@Service("sampleTypesService")
@Transactional
public class SampleTypesService {

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;

    private Logger logger = LoggerFactory.getLogger(SampleTypesService.class);

    public SampleTypesService() {}

    /**
     * Obtiene una lista completa de los tipos de Mx
     */

    @SuppressWarnings("unchecked")
    public List<TipoMx> getAllSamplesList() throws Exception {
        String query = "from TipoMx as t order by t.nombre";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);

        return q.list();
    }

    /**
     * Obtiene una lista de tipos de Mx
     */

    @SuppressWarnings("unchecked")
    public List<TipoMx> getSamplesList() throws Exception {
        String query = "from TipoMx as t where t.pasivo = false order by t.nombre";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);

        return q.list();
    }

    /**
     * Obtiene un registro de Tipo Mx
     * @param id  IdConcepto
     */
    @SuppressWarnings("unchecked")
    public TipoMx getTipoMxById(Integer id){
        Session session = sessionFactory.getCurrentSession();
        Criteria cr = session.createCriteria(TipoMx.class, "tipoMx");
        cr.add(Restrictions.eq("tipoMx.idTipoMx", id));
        return (TipoMx) cr.uniqueResult();
    }

    /**
     * Actualiza o agrega un tipo de Mx
     *
     * @param dto Objeto a actualizar o agregar
     * @throws Exception
     */
    public void addOrUpdateSampleTypes(TipoMx dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.saveOrUpdate(dto);
            }
            else
                throw new Exception("Objeto NULL");
        }catch (Exception ex){
            logger.error("Error al agregar o actualizar tipo mx",ex);
            throw ex;
        }
    }
}
