package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.examen.CatalogoExamenes;
import ni.gob.minsa.laboratorio.domain.examen.Examen_Dx;
import ni.gob.minsa.laboratorio.domain.examen.Examen_Estudio;
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
@Service("examenesSolicitudService")
@Transactional
public class ExamenesSolicitudService {

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;

    private Logger logger = LoggerFactory.getLogger(ExamenesSolicitudService.class);


    public ExamenesSolicitudService() {
    }

    /**
     * Obtiene una lista de examenes asociados a una rutina
     * @param id id
     */
    @SuppressWarnings("unchecked")
    public List<Examen_Dx> getTestsDxByIdDx(Integer id){
        Session session = sessionFactory.getCurrentSession();
        Criteria cr = session.createCriteria(Examen_Dx.class, "exaDx");
        cr.createAlias("exaDx.diagnostico", "dx");
        cr.createAlias("exaDx.examen", "examen");
        cr.add(Restrictions.eq("dx.idDiagnostico", id));
        cr.add(Restrictions.eq("exaDx.pasivo", false));
        cr.add(Restrictions.eq("examen.pasivo", false));
        return cr.list();
    }

    /**
     * Obtiene una lista de examenes asociados a un estudio
     * @param id id
     */
    @SuppressWarnings("unchecked")
    public List<Examen_Estudio> getTestsEstByIdDx(Integer id){
        Session session = sessionFactory.getCurrentSession();
        Criteria cr = session.createCriteria(Examen_Estudio.class, "exaEst");
        cr.createAlias("exaEst.estudio", "est");
        cr.createAlias("exaEst.examen", "examen");
        cr.add(Restrictions.eq("est.idEstudio", id));
        cr.add(Restrictions.eq("exaEst.pasivo", false));
        cr.add(Restrictions.eq("examen.pasivo", false));
        return cr.list();
    }

    /**
     * Obtiene una lista de examenes asociados a un estudio
     * @param id id
     */
    @SuppressWarnings("unchecked")
    public List<CatalogoExamenes> getTestsByIdArea(Integer id){
        Session session = sessionFactory.getCurrentSession();
        Criteria cr = session.createCriteria(CatalogoExamenes.class, "exa");
        cr.createAlias("exa.area", "area");
        cr.add(Restrictions.eq("area.idArea", id));
        cr.add(Restrictions.eq("exa.pasivo", false));
        return cr.list();
    }

    public Examen_Dx getDxTestRecord(Integer idDx, Integer idExamen){
        String query = "from Examen_Dx dx where dx.diagnostico.id = :idDx and dx.examen.id = :idExamen and dx.pasivo = false";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setInteger("idDx", idDx);
        q.setInteger("idExamen", idExamen);
        return (Examen_Dx) q.uniqueResult();
    }

    public Examen_Estudio getStudyTestRecord(Integer idEstudio, Integer idExamen){
        String query = "from Examen_Estudio est where est.estudio.id = :idEstudio and est.examen.id = :idExamen and est.pasivo = false";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setInteger("idEstudio", idEstudio);
        q.setInteger("idExamen", idExamen);
        return (Examen_Estudio) q.uniqueResult();
    }

    /**
     * Actualiza o agrega una asociacion de examen
     *
     * @param dto Objeto a actualizar o agregar
     * @throws Exception
     */
    public void addOrUpdateTest(Examen_Dx dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.saveOrUpdate(dto);
            }
            else
                throw new Exception("Objeto NULL");
        }catch (Exception ex){
            logger.error("Error al agregar o actualizar asociacion examen",ex);
            throw ex;
        }
    }

    /**
     * Actualiza o agrega una asociacion de examen
     *
     * @param dto Objeto a actualizar o agregar
     * @throws Exception
     */
    public void addOrUpdateTestE(Examen_Estudio dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.saveOrUpdate(dto);
            }
            else
                throw new Exception("Objeto NULL");
        }catch (Exception ex){
            logger.error("Error al agregar o actualizar asociacion examen",ex);
            throw ex;
        }
    }

    public Examen_Dx getRoutineTestById(Integer id){
        String query = "from Examen_Dx dx where dx.idExamen_Dx = :id and dx.pasivo = false";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setInteger("id", id);
        return (Examen_Dx) q.uniqueResult();
    }

    public Examen_Estudio getStudyTestById(Integer id){
        String query = "from Examen_Estudio est where est.idExamen_Estudio = :id and est.pasivo = false";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setInteger("id", id);
        return (Examen_Estudio) q.uniqueResult();
    }
}
