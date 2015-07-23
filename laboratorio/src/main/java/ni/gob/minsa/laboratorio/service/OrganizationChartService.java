package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.examen.DepartamentoDireccion;
import ni.gob.minsa.laboratorio.domain.examen.DireccionLaboratorio;
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
@Service("organizationChartService")
@Transactional
public class OrganizationChartService {

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;

    private Logger logger = LoggerFactory.getLogger(OrganizationChartService.class);


    public OrganizationChartService() {
    }


    /**
     * Obtiene una lista de las direcciones asociadas a un labpratorio
     * @param codLab
     */

    @SuppressWarnings("unchecked")
    public List<DireccionLaboratorio> getManagmentLabAssociated(String codLab){
        Session session = sessionFactory.getCurrentSession();
        Criteria cr = session.createCriteria(DireccionLaboratorio.class, "direcciones");
        cr.add(Restrictions.eq("direcciones.pasivo", false));
        cr.createAlias("direcciones.laboratorio", "lab");
       // cr.add(Restrictions.eq("lab.pasivo", false));
        cr.add(Restrictions.eq("lab.codigo", codLab));
        return cr.list();
    }

    /**
     * Obtiene un registro de DireccionLaboratorio
     * @param codLab
     * @param idManagment
     */

    public DireccionLaboratorio getManagmentLabRecord(String codLab, Integer idManagment){
        String query = "from DireccionLaboratorio manLab where manLab.laboratorio.codigo = :codLab and manLab.direccion.idDireccion = :idManagment and manLab.pasivo = false";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("codLab", codLab);
        q.setInteger("idManagment", idManagment);
        return (DireccionLaboratorio) q.uniqueResult();
    }


    /**
     * Actualiza o agrega una asociación DireccionLaboratorio
     *
     * @param dto Objeto a actualizar o agregar
     * @throws Exception
     */
    public void addOrUpdateManagmentLab(DireccionLaboratorio dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.saveOrUpdate(dto);
            }
            else
                throw new Exception("Objeto NULL");
        }catch (Exception ex){
            logger.error("Error al agregar o actualizar una asociación DireccionLaboratorio",ex);
            throw ex;
        }
    }

    /**
     * Obtiene un registro de DireccionLaboratorio por id
     * @param id
     */

    public DireccionLaboratorio getManagmentLabById(Integer id){
        String query = "from DireccionLaboratorio manLab where manLab.idDireccionLab = :id";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setInteger("id", id);
        return (DireccionLaboratorio) q.uniqueResult();
    }

    /**
     * Obtiene una lista de los departamentos asociados a una direccion-lab
     *  @param id
     */

    @SuppressWarnings("unchecked")
    public List<DepartamentoDireccion> getDepartmentAssociated(Integer id){
        Session session = sessionFactory.getCurrentSession();
        Criteria cr = session.createCriteria(DepartamentoDireccion.class, "dep");
        cr.add(Restrictions.eq("dep.pasivo", false));
        cr.createAlias("dep.direccionLab", "dirLab");
        // cr.add(Restrictions.eq("lab.pasivo", false));
        cr.add(Restrictions.eq("dirLab.idDireccionLab", id));
        return cr.list();
    }

}
