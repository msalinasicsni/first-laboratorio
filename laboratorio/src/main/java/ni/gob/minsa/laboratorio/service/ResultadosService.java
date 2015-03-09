package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.muestra.FiltroMx;
import ni.gob.minsa.laboratorio.domain.muestra.RecepcionMx;
import ni.gob.minsa.laboratorio.domain.resultados.DetalleResultado;
import org.apache.commons.codec.language.Soundex;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by FIRSTICT on 12/10/2014.
 * V 1.0
 */
@Service("resultadosService")
@Transactional
public class ResultadosService {

    private Logger logger = LoggerFactory.getLogger(ResultadosService.class);

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;

    public ResultadosService(){}

    /**
     * Agrega un Registro de detalle de resultado seg�n alicuota y concepto
     *
     * @param dto Objeto a agregar
     * @throws Exception
     */
    public String addDetalleResultado(DetalleResultado dto) throws Exception {
        String idMaestro;
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                idMaestro = (String)session.save(dto);
            }
            else
                throw new Exception("Objeto DetalleResultado es NULL");
        }catch (Exception ex){
            logger.error("Error al agregar DetalleResultado",ex);
            throw ex;
        }
        return idMaestro;
    }

    /**
     * Actualiza un registro de detalle de resultado
     *
     * @param dto Objeto a actualizar
     * @throws Exception
     */
    public void updateDetalleResultado(DetalleResultado dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.update(dto);
            }
            else
                throw new Exception("Objeto DetalleResultado es NULL");
        }catch (Exception ex){
            logger.error("Error al actualizar DetalleResultado",ex);
            throw ex;
        }
    }

    /**
     * Obtiene un detalle de resultado seg�n el id indicado
     * @param idDetalle del Detalle a recuperar
     * @return DetalleResultado
     */
    public DetalleResultado getDetalleResultado(String idDetalle){
        String query = "from DetalleResultado as a where idDetalle= :idDetalle";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("idDetalle", idDetalle);
        return  (DetalleResultado)q.uniqueResult();
    }

    /**
     * Obtiene una lista de detalles de resultados registrados para el la orden de examen indicada
     * @param idOrdenExamen id de la orden a recuperar resultados
     * @return List<DetalleResultado>
     */
    public List<DetalleResultado> getDetallesResultadoByExamen(String idOrdenExamen){
        String query = "select a from DetalleResultado as a inner join a.examen as r where r.idOrdenExamen = :idOrdenExamen ";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idOrdenExamen", idOrdenExamen);
        return  q.list();
    }

    public List<DetalleResultado> getDetallesResultadoActivosByExamen(String idOrdenExamen){
        String query = "select a from DetalleResultado as a inner join a.examen as r where a.pasivo = false and r.idOrdenExamen = :idOrdenExamen ";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idOrdenExamen", idOrdenExamen);
        return  q.list();
    }

    /**
     * Verifica si existe registrado un resultado para la respuesta y orden de examen indicado, siempre y cuando el registro este activo (pasivo = false)
     * @param idOrdenExamen orden a verificar
     * @param idRespuesta respuesta a verificar
     * @return DetalleResultado
     */
    public DetalleResultado getDetalleResultadoByOrdenExamanAndRespuesta(String idOrdenExamen, int idRespuesta){
        String query = "Select a from DetalleResultado as a inner join a.examen as ex inner join a.respuesta as re " +
                "where ex.idOrdenExamen = :idOrdenExamen and re.idRespuesta = :idRespuesta and a.pasivo = false ";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idOrdenExamen", idOrdenExamen);
        q.setParameter("idRespuesta", idRespuesta);
        return  (DetalleResultado)q.uniqueResult();
    }
}