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
     * Agrega un Registro de detalle de resultado según alicuota y concepto
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
     * Obtiene un detalle de resultado según el id indicado
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
}
