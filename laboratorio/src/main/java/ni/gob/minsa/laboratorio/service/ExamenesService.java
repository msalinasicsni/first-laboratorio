package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.examen.CatalogoExamenes;
import ni.gob.minsa.laboratorio.domain.examen.Examen_Dx;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
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
     * Obtiene una lista de examenes por diagn�stico seg�n ids de examenes enviados
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
     * Obtiene lista de examenes asociados a un dx espec�fico
     * @param idDx id del diagn�stico a filtrar
     * @return List<CatalogoExamenes>
     */
    public List<CatalogoExamenes> getExamenesByIdDx(int idDx){
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery("select ex from Examen_Dx as edx inner join edx.examen as ex inner join edx.diagnostico as dx " +
                "where dx.idDiagnostico = :idDx");
        q.setParameter("idDx",idDx);
        return q.list();
    }

    /**
     * Obtiene una lista de examenes asociados a un dx espec�fico, y adem�s tiene que estar en un rango determinado de ids de examenes
     * @param idDx id del diagn�stico a filtrar
     * @param idExamenes String con los ids de los examenes a filtrar, separados por coma Ejm: 1,2,3
     * @return List<Examen_Dx>
     */
    public List<Examen_Dx> getExamenesByIdDxAndIdsEx(int idDx, String idExamenes){
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery("select edx from Examen_Dx as edx inner join edx.examen as ex inner join edx.diagnostico as dx " +
                "where dx.idDiagnostico = :idDx "+
                " and ex.idExamen in("+ idExamenes +")");
        q.setParameter("idDx",idDx);
        return q.list();
    }

    /**
     * Obtiene una lista de examenes seg�n ids de examenes enviados
     * @param idExamenes id de los examenes a filtrar, separados por coma Ejm: 1,2,3
     * @return List<CatalogoExamenes>
     */
    public List<CatalogoExamenes> getExamenesByIdsExamenes(String idExamenes){
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery("from CatalogoExamenes as ex" +
                " where ex.idExamen in("+ idExamenes +")");
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
                "where dx.idEstudio = :idEstudio");
        q.setParameter("idEstudio",idEstudio);
        return q.list();
    }
}