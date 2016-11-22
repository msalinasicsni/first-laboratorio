package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.muestra.Catalogo_Dx;
import ni.gob.minsa.laboratorio.domain.muestra.Catalogo_Estudio;
import ni.gob.minsa.laboratorio.domain.concepto.Catalogo_Lista;
import ni.gob.minsa.laboratorio.domain.resultados.RespuestaSolicitud;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

/**
 * Created by souyen-ics.
 */
@Service("respuestasSolicitudService")
@Transactional
public class RespuestasSolicitudService {

    private Logger logger = LoggerFactory.getLogger(RespuestasSolicitudService.class);

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;

    public RespuestasSolicitudService() {
    }

    public List<Catalogo_Dx> getDxByFiltro(String nombreDx) throws UnsupportedEncodingException {
        Session session = sessionFactory.getCurrentSession();
        nombreDx = URLDecoder.decode(nombreDx, "utf-8");
        StringBuilder sQuery = new StringBuilder("select dx " +
                "from Catalogo_Dx as dx");
        if (!nombreDx.isEmpty()) sQuery.append(" where lower(dx.nombre) like '%").append(nombreDx.toLowerCase()).append("%'");

        Query q = session.createQuery(sQuery.toString());

        List<Catalogo_Dx> dxs= q.list();
        return dxs;
    }

    public List<Catalogo_Estudio> getEstudioByFiltro(String nombre) throws UnsupportedEncodingException {
        Session session = sessionFactory.getCurrentSession();
        nombre = URLDecoder.decode(nombre, "utf-8");
        StringBuilder sQuery = new StringBuilder("select e " +
                "from Catalogo_Estudio as e");
        if (!nombre.isEmpty()) sQuery.append(" where lower(e.nombre) like '%").append(nombre.toLowerCase()).append("%'");

        Query q = session.createQuery(sQuery.toString());

        List<Catalogo_Estudio> estudios= q.list();
        return estudios;
    }

    public List<RespuestaSolicitud> getRespuestasByDx(Integer idDx){
        String query = "from RespuestaSolicitud as a where a.diagnostico.idDiagnostico = :idDx order by orden asc";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idDx", idDx);
        return q.list();
    }

    public List<RespuestaSolicitud> getRespuestasByEstudio(Integer id){
        String query = "from RespuestaSolicitud as a where a.estudio.idEstudio = :id order by orden asc";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("id", id);
        return q.list();
    }

    public RespuestaSolicitud getRespuestaDxById(Integer idRespuesta){
        String query = "from RespuestaSolicitud as a where idRespuesta =:idRespuesta";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idRespuesta", idRespuesta);
        return (RespuestaSolicitud)q.uniqueResult();
    }

    /**
     * Agrega o Actualiza un Registro de RespuestaDx
     * @param dto Objeto a actualizar
     * @throws Exception
     */
    public void saveOrUpdateResponse(RespuestaSolicitud dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.saveOrUpdate(dto);
            }
            else
                throw new Exception("Objeto Respuesta es NULL");
        }catch (Exception ex){
            logger.error("Error al actualizar Respuesta",ex);
            throw ex;
        }
    }

    public List<Catalogo_Lista> getCatalogoListaConceptoByIdDx(Integer idDx) throws Exception {
        String query = "Select distinct a from Catalogo_Lista as a inner join a.idConcepto tdl , RespuestaSolicitud as r inner join r.concepto tdc " +
                "where a.pasivo = false and tdl.idConcepto = tdc.idConcepto and r.diagnostico.idDiagnostico =:idDx" +
                " order by  a.valor";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idDx",idDx);
        return q.list();
    }

    public List<Catalogo_Lista> getCatalogoListaConceptoByIdEstudio(Integer idEstudio) throws Exception {
        String query = "Select distinct a from Catalogo_Lista as a inner join a.idConcepto tdl , RespuestaSolicitud as r inner join r.concepto tdc " +
                "where a.pasivo = false and tdl.idConcepto = tdc.idConcepto and r.estudio.idEstudio =:idEstudio" +
                " order by  a.valor";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idEstudio",idEstudio);
        return q.list();
    }

    public List<RespuestaSolicitud> getRespuestasActivasByDx(Integer idDx){
        String query = "from RespuestaSolicitud as a where a.diagnostico.idDiagnostico = :idDx and pasivo = false order by orden asc";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idDx", idDx);
        return q.list();
    }

    public List<RespuestaSolicitud> getRespuestasActivasByEstudio(Integer idEstudio){
        String query = "from RespuestaSolicitud as a where a.estudio.idEstudio = :idEstudio and pasivo = false order by orden asc";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idEstudio", idEstudio);
        return q.list();
    }

    public RespuestaSolicitud getRespuestaDefectoMxInadecuada(){
        String query = "select a from RespuestaSolicitud as a, Parametro as p where p.nombre = :paramMxInadecuada and pasivo = false and a.idRespuesta = to_number(p.valor)";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("paramMxInadecuada", "RES_MX_INADECUADA");
        return (RespuestaSolicitud) q.uniqueResult();
    }
}
