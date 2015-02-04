package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.muestra.Alicuota;
import ni.gob.minsa.laboratorio.domain.muestra.AlicuotaRegistro;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
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
@Service("generacionAlicuotaService")
@Transactional
public class GeneracionAlicuotaService {

    private Logger logger = LoggerFactory.getLogger(GeneracionAlicuotaService.class);

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;

    public GeneracionAlicuotaService(){}

    /**
     * Obtiene una lista de Registros de Alicuotas segun tipo de Notficacion y tipo de Mx
     *
     * @param tipoNoti Tipo de Notificacion
     * @param tipoMx   Tipo de Mx
     */

    public List<Alicuota> getAlicuotasByTipoNoti(String tipoNoti, Integer tipoMx) throws Exception {
        String query = "from Alicuota as a where tipoNotificacion= :tipoNoti and tipoMuestra = :tipoMx";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("tipoNoti", tipoNoti);
        q.setInteger("tipoMx", tipoMx);
        return q.list();
    }

    /**
     * Agrega una Registro de Alicuota
     * @param dto Objeto a agregar
     * @throws Exception
     */
    public String addAliquot(AlicuotaRegistro dto) throws Exception {
        String idAlicuota;
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                idAlicuota = (String)session.save(dto);
            }
            else
                throw new Exception("Objeto Alicuota es NULL");
        }catch (Exception ex){
            logger.error("Error al agregar alicuota",ex);
            throw ex;
        }
        return idAlicuota;
    }

    public Long cantidadAlicuotas(String idAlicuota) {
        Session session = sessionFactory.getCurrentSession();
        Long result;
        Criteria cr = session.createCriteria(AlicuotaRegistro.class, "reg");
        cr.add(Restrictions.like("reg.idAlicuota", idAlicuota + "%"));
       return (Long) cr.setProjection(Projections.rowCount()).uniqueResult();
    }


    /**
     * Obtiene una lista de Registros de Alicuota por IdAlicuota
     * @param id  IdAlicuota a buscar
     */
    @SuppressWarnings("unchecked")
    public List<AlicuotaRegistro> getAliquotsById(String id){
        Session session = sessionFactory.getCurrentSession();
        Criteria cr = session.createCriteria(AlicuotaRegistro.class, "reg");
        cr.add(Restrictions.like("reg.idAlicuota", id + "%"));
        cr.addOrder(Order.asc("reg.fechaHoraRegistro"));
        cr.add(Restrictions.eq("reg.pasivo", false));
        return cr.list();
    }

    /**
     * Obtiene un registro de Alicuota por IdAlicuota
     * @param id  IdAlicuota
     */
    @SuppressWarnings("unchecked")
    public AlicuotaRegistro getAliquotById(String id){
        Session session = sessionFactory.getCurrentSession();
        Criteria cr = session.createCriteria(AlicuotaRegistro.class, "reg");
        cr.add(Restrictions.eq("reg.idAlicuota", id));
        return (AlicuotaRegistro) cr.uniqueResult();
    }

    /**
     * Actualiza una Registro de Alicuotas
     *
     * @param dto Objeto a actualizar
     * @throws Exception
     */
    public void updateAlicuotaReg(AlicuotaRegistro dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.update(dto);
            }
            else
                throw new Exception("Objeto AlicuotaRegistro es NULL");
        }catch (Exception ex){
            logger.error("Error al actualizar AlicuotaRegistro",ex);
            throw ex;
        }
    }



}
