package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;
import ni.gob.minsa.laboratorio.domain.muestra.RecepcionMx;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by FIRSTICT on 12/10/2014.
 */
@Service("recepcionMxService")
@Transactional
public class RecepcionMxService {

    private Logger logger = LoggerFactory.getLogger(CatalogoService.class);

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;

    public RecepcionMxService(){}

    /**
     * Agrega una Registro de encuesta al maestro y al detalle
     *
     * @param dto
     * @throws Exception
     */
    public String addDaMaeEncuesta(RecepcionMx dto) throws Exception {
        String idMaestro;
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                idMaestro = (String)session.save(dto);
            }
            else
                throw new Exception("Objeto Recepción Muestra es NULL");
        }catch (Exception ex){
            logger.error("Error al agregar recepción de muestra",ex);
            throw ex;
        }
        return idMaestro;
    }

    public RecepcionMx getRecepcionMx(String idRecepcion){
        String query = "from RecepcionMx as a where idRecepcion= :idRecepcion";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("idRecepcion", idRecepcion);
        return  (RecepcionMx)q.uniqueResult();
    }

}
