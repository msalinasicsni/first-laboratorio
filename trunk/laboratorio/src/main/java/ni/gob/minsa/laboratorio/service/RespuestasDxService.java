package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.muestra.Catalogo_Dx;
import ni.gob.minsa.laboratorio.domain.resultados.Catalogo_Lista;
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
@Service("respuestasDxService")
@Transactional
public class RespuestasDxService {

    private Logger logger = LoggerFactory.getLogger(RespuestasDxService.class);

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;

    public RespuestasDxService() {
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

    public List<RespuestaSolicitud> getRespuestasByDx(Integer idDx){
        String query = "from RespuestaSolicitud as a where a.diagnostico.idDiagnostico = :idDx order by orden asc";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idDx", idDx);
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
        String query = "Select a from Catalogo_Lista as a inner join a.idConcepto tdl , RespuestaSolicitud as r inner join r.concepto tdc " +
                "where a.pasivo = false and tdl.idConcepto = tdc.idConcepto and r.diagnostico.idDiagnostico =:idDx" +
                " order by  a.valor";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idDx",idDx);
        return q.list();
    }

    public List<RespuestaSolicitud> getRespuestasActivasByDx(Integer idDx){
        String query = "from RespuestaSolicitud as a where a.diagnostico.idDiagnostico = :idDx and pasivo = false order by orden asc";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idDx", idDx);
        return q.list();
    }
}
