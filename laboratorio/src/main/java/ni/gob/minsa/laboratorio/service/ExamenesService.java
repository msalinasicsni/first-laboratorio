package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.examen.CatalogoExamenes;
import ni.gob.minsa.laboratorio.domain.examen.Examen_Dx;
import ni.gob.minsa.laboratorio.domain.examen.ReglaExamen;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

/**
 * Created by FIRSTICT on 11/21/2014.
 */
@Service("examenesService")
@Transactional
public class ExamenesService {

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;

    public void saveExamen(CatalogoExamenes examen) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(examen);
    }

    /**
     *
     * @param idExamen
     * @return
     */
    public CatalogoExamenes getExamenById(int idExamen){
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery("from CatalogoExamenes where idExamen =:idExamen");
        q.setInteger("idExamen", idExamen);
        return (CatalogoExamenes)q.uniqueResult();
    }

    /**
     * Obtiene una lista de examenes por diagnóstico según ids de examenes enviados
     * @param idExamenes id de los examenes a filtrar, separados por coma Ejm: 1,2,3
     * @return List<Examen_Dx>
     */
    public List<Examen_Dx> getExamenesDxByIdsExamenes(String idExamenes){
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery("select edx from Examen_Dx as edx inner join edx.examen as ex inner join edx.diagnostico as dx " +
                " where ex.idExamen in("+ idExamenes +")");
        return q.list();
    }

    /**
     * Obtiene lista de examenes asociados a un dx específico
     * @param idDx id del diagnóstico a filtrar
     * @return List<CatalogoExamenes>
     */
    public List<CatalogoExamenes> getExamenesByIdDx(int idDx){
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery("select ex from Examen_Dx as edx inner join edx.examen as ex inner join edx.diagnostico as dx " +
                "where dx.idDiagnostico = :idDx and edx.pasivo = false and ex.pasivo = false");
        q.setParameter("idDx",idDx);
        return q.list();
    }

    /**
     * Obtiene una lista de examenes asociados a un dx específico, y además tiene que estar en un rango determinado de ids de examenes
     * @param idDx id del diagnóstico a filtrar
     * @param idExamenes String con los ids de los examenes a filtrar, separados por coma Ejm: 1,2,3
     * @return List<Examen_Dx>
     */
    public List<Examen_Dx> getExamenesByIdDxAndIdsEx(int idDx, String idExamenes, String username){
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery("select edx from Examen_Dx as edx inner join edx.examen as ex inner join edx.diagnostico as dx, AutoridadExamen as ae " +
                "where ae.examen.idExamen = ex.idExamen and ae.pasivo = false and ae.autoridadArea.pasivo = false and ae.autoridadArea.user.username = :username and dx.idDiagnostico = :idDx "+
                " and ex.idExamen in("+ idExamenes +") and edx.pasivo = false and ex.pasivo = false ");
        q.setParameter("idDx",idDx);
        q.setParameter("username", username);
        return q.list();
    }

    /**
     * Obtiene una lista de examenes según ids de examenes enviados
     * @param idExamenes id de los examenes a filtrar, separados por coma Ejm: 1,2,3
     * @return List<CatalogoExamenes>
     */
    public List<CatalogoExamenes> getExamenesByIdsExamenes(String idExamenes){
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery("from CatalogoExamenes as ex" +
                " where ex.idExamen in("+ idExamenes +") and ex.pasivo = false ");
        return q.list();
    }

    public List<Object[]> getExamenesByFiltro(String idDx, String codTipoNoti, String nombreExamen){
        Session session = sessionFactory.getCurrentSession();
        StringBuilder sQuery = new StringBuilder("select ex.idExamen, dx.idDiagnostico, noti.codigo, ex.nombre, noti.valor, dx.nombre, are.nombre " +
                "from Examen_Dx as edx inner join edx.examen as ex inner join edx.diagnostico as dx inner join ex.area as are, " +
                "Dx_TipoMx_TipoNoti dxmxnt inner join dxmxnt.tipoMx_tipoNotificacion.tipoNotificacion noti " +
                "where edx.diagnostico.idDiagnostico = dxmxnt.diagnostico.idDiagnostico ");
        if (!idDx.isEmpty()) sQuery.append(" and dx.idDiagnostico = :idDx");
        if (!codTipoNoti.isEmpty()) sQuery.append(" and noti.codigo = :codTipoNoti");
        if (!nombreExamen.isEmpty()) sQuery.append(" and lower(ex.nombre) like '%"+nombreExamen.toLowerCase()+"%'");

        Query q = session.createQuery(sQuery.toString());

        if (!idDx.isEmpty()) q.setParameter("idDx",Integer.valueOf(idDx));
        if (!codTipoNoti.isEmpty()) q.setParameter("codTipoNoti",codTipoNoti);

        List<Object[]> examenes= (List<Object[]>)q.list();
        return examenes;
    }

    public List<CatalogoExamenes> getExamenesByFiltro(String nombreExamen) throws UnsupportedEncodingException{
        Session session = sessionFactory.getCurrentSession();
        nombreExamen = URLDecoder.decode(nombreExamen, "utf-8");
        StringBuilder sQuery = new StringBuilder("select ex " +
                "from CatalogoExamenes as ex");
        if (!nombreExamen.isEmpty()) sQuery.append(" where lower(ex.nombre) like '%").append(nombreExamen.toLowerCase()).append("%'");

        Query q = session.createQuery(sQuery.toString());

        List<CatalogoExamenes> examenes= q.list();
        return examenes;
    }

    public List<CatalogoExamenes> getExamenesByIdEstudio(int idEstudio){
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery("select ex from Examen_Estudio as eex inner join eex.examen as ex inner join eex.estudio as dx " +
                "where dx.idEstudio = :idEstudio and eex.pasivo =false and ex.pasivo = false");
        q.setParameter("idEstudio",idEstudio);
        return q.list();
    }

    public List<CatalogoExamenes> getExamenesDisponiblesUser(String userName){
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery("select ex from CatalogoExamenes as ex, AutoridadArea as aa " +
                "where ex.area.idArea = aa.area.idArea and aa.pasivo = false and ex.pasivo = false " +
                "and aa.user.username = :userName and ex.idExamen not in (select aex.examen.idExamen from AutoridadExamen as aex " +
                "where aex.pasivo = false and aex.autoridadArea.pasivo = false and aex.autoridadArea.user.username = :userName) " +
                "order by ex.nombre");
        q.setParameter("userName",userName);
        return q.list();
    }

    /**
     * Obtiene lista de todos los examenes registrados
     * @return List<CatalogoExamenes>
     */
    public List<CatalogoExamenes> getExamenes(){
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery("select ex from CatalogoExamenes ex");
        return q.list();
    }

    /***REGLAS DE EXAMEN****/

    public void guardarReglaExamen(ReglaExamen regla) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(regla);
    }

    public ReglaExamen getReglaById(String idRegla){
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery("from ReglaExamen where idRegla = :idRegla");
        q.setParameter("idRegla",idRegla);
        return (ReglaExamen)q.uniqueResult();
    }

    public List<ReglaExamen> getReglasByExamen(Integer idExamen){
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery("select regla from ReglaExamen regla  where regla.examen.idExamen = :idExamen and regla.pasivo = false ");
        q.setParameter("idExamen",idExamen);
        return q.list();
    }

    public List<ReglaExamen> getReglasByExamenes(String idExamenes){
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery("select regla from ReglaExamen regla  where regla.examen.idExamen in ("+idExamenes+") and regla.pasivo = false" +
                " order by regla.examen.nombre");
        return q.list();
    }
}
