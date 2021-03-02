package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.persona.SisPersona;
import ni.gob.minsa.laboratorio.domain.serviciosEnLinea.ResultadoViajero;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by miguel on 1/2/2021.
 */
@Service("serviciosEnLineaService")
@Transactional
public class ServiciosEnLineaService {

    private Logger logger = LoggerFactory.getLogger(ServiciosEnLineaService.class);

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;

    @Autowired
    @Qualifier(value = "tomaMxService")
    private TomaMxService tomaMxService;

    /****
     * Guadar ResultadoViajero
     * @param resultado ResultadoViajero a guardar
     */
    public void saveOrUpdateResultadoViajero(ResultadoViajero resultado){
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(resultado);
    }

    /***
     * Recuperar ResultadoViajero por codigo de muestra
     * @param codigoMx a filtrar
     * @return List<ResultadoViajero>
     */
    public List<ResultadoViajero> getResultadoViajeroByCodigoMx(String codigoMx){
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("select r from ResultadoViajero r where r.codigoMx = :codigoMx");
        query.setParameter("codigoMx", codigoMx);
        return (List<ResultadoViajero>) query.list();
    }

    /***
     * Valida y actualiza ResultadoViajero por idNotificacion
     * @param idNotificacion a obtener resultados que se deben actualizar
     * @param persona Datos de la persana
     */
    public void updatePersonaResultadosViajeroNotificacion(String idNotificacion, SisPersona persona) throws Exception{
        List<String> mxViajeroNotifi = tomaMxService.getCodigosMxViajerosByIdNoti(idNotificacion);
        for(String codigoMx : mxViajeroNotifi){
            List<ResultadoViajero> resultadoViajeros = getResultadoViajeroByCodigoMx(codigoMx);
            for(ResultadoViajero resultado : resultadoViajeros){
                resultado.setIdentificacion(persona.getIdentificacion());
                resultado.setDocumentoViaje(persona.getIdentificacion());
                resultado.setFechaNacimiento(persona.getFechaNacimiento());
                String nombres = persona.getPrimerNombre();
                if (persona.getSegundoNombre() != null)
                    nombres = nombres + " " + persona.getSegundoNombre();
                String apellidos = persona.getPrimerApellido();
                if (persona.getSegundoApellido() != null)
                    apellidos = apellidos+ " " + persona.getSegundoApellido();
                resultado.setNombres(nombres);
                resultado.setApellidos(apellidos);
                saveOrUpdateResultadoViajero(resultado);
            }
        }
    }
}
