package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.muestra.RecepcionMx;
import ni.gob.minsa.laboratorio.domain.muestra.RechazoResultadoFinalSolicitud;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by FIRSTICT on 3/18/2015.
 * V1.0
 */
@Service("rechazoResultadoSolicitudService")
@Transactional
public class RechazoResultadoSolicitudService {
    private Logger logger = LoggerFactory.getLogger(RechazoResultadoSolicitudService.class);
    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;

    /**
     * Agrega una Registro de RechazoResultadoFinalSolicitud
     *
     * @param dto Objeto a agregar
     * @throws Exception
     */
    public void addRechazoResultadoSolicitud(RechazoResultadoFinalSolicitud dto) throws Exception {
        String idMaestro;
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.save(dto);
            }
            else
                throw new Exception("Objeto Rechazo Resultado Final es NULL");
        }catch (Exception ex){
            logger.error("Error al agregar rechazo de resultado final de solicitud",ex);
            throw new Exception(ex);
        }
    }
}
