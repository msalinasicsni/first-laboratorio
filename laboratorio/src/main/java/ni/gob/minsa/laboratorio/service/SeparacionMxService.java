package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.muestra.Alicuota;
import ni.gob.minsa.laboratorio.domain.muestra.AlicuotaRegistro;
import org.hibernate.*;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by souyen-ics.
 */
@Service("separacionMxService")
@Transactional
public class SeparacionMxService {

    private Logger logger = LoggerFactory.getLogger(SeparacionMxService.class);

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;

    public SeparacionMxService(){}

    /**
     * Obtiene una lista de Registros de Alicuotas segun tipo de Recepcion, solicitud y tipo recepcion
     *
     * @param solicitud
     * @param tipoMx
     * @param tipoRecepcion
     */

    public List<Alicuota> getAlicuotasByTRecSoliTMx(Integer solicitud, Integer tipoMx, String tipoRecepcion) throws Exception {
        Session session = sessionFactory.getCurrentSession();
        List<Alicuota> aliquotList = new ArrayList<Alicuota>();
        String query = "from Alicuota as a where a.tipoRecepcionMx= :tipoRecepcion and a.tipoMuestra = :tipoMx and a.estudio= :solicitud";
        Query q = session.createQuery(query);
        q.setString("tipoRecepcion", tipoRecepcion);
        q.setInteger("tipoMx", tipoMx);
        q.setInteger("solicitud", solicitud);
        aliquotList = q.list();

        //se toman las de rutina
        String query1 = "from Alicuota as a where a.tipoRecepcionMx= :tipoRecepcion and a.tipoMuestra = :tipoMx and a.diagnostico= :solicitud";
        Query q1 = session.createQuery(query1);
        q1.setString("tipoRecepcion", tipoRecepcion);
        q1.setInteger("tipoMx", tipoMx);
        q1.setInteger("solicitud", solicitud);
        aliquotList.addAll(q1.list());
        return  aliquotList;

    }


    /**
     * Agrega una Registro de Alicuota
     * @param dto Objeto a agregar
     * @throws Exception
     */
    public String addAliquot(AlicuotaRegistro dto) throws Exception {
        String idAlicuota;
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                idAlicuota = (String)session.save(dto);
            }
            else
                throw new Exception("Objeto Alicuota es NULL");
        }catch (Exception ex){
            logger.error("Error al agregar alicuota",ex);
            throw ex;
        }
        return idAlicuota;
    }

    public Long cantidadAlicuotas(String idAlicuota) {
        Session session = sessionFactory.getCurrentSession();
        Long result;
        Criteria cr = session.createCriteria(AlicuotaRegistro.class, "reg");
        cr.add(Restrictions.like("reg.idAlicuota", idAlicuota + "%"));
       return (Long) cr.setProjection(Projections.rowCount()).uniqueResult();
    }


    /**
     * Obtiene una lista de Registros de Alicuota por IdAlicuota
     * @param id  IdAlicuota a buscar
     */
    @SuppressWarnings("unchecked")
    public List<AlicuotaRegistro> getAliquotsById(String id){
        Session session = sessionFactory.getCurrentSession();
        Criteria cr = session.createCriteria(AlicuotaRegistro.class, "reg");
        //cr.add(Restrictions.like("reg.idAlicuota", id + "%"));
        cr.add(Restrictions.eq("reg.codUnicoMx.codigoUnicoMx", id));
        cr.addOrder(Order.asc("reg.fechaHoraRegistro"));
        cr.add(Restrictions.eq("reg.pasivo", false));
        return cr.list();
    }

    /**
     * Obtiene una lista de Registros de Alicuota por codigoUnicoMx
     * @param codigoUnicoMx  IdAlicuota a buscar
     */
    @SuppressWarnings("unchecked")
    public List<AlicuotaRegistro> getAliquotsRecordsByCodigoUnicoMx(String codigoUnicoMx){
        Session session = sessionFactory.getCurrentSession();
        Criteria cr = session.createCriteria(AlicuotaRegistro.class, "reg");
        cr.createAlias("reg.codUnicoMx", "toma");
        cr.add(Restrictions.like("toma.codigoUnicoMx",codigoUnicoMx));
        cr.addOrder(Order.asc("reg.fechaHoraRegistro"));
        cr.add(Restrictions.eq("reg.pasivo", false));
        return cr.list();
    }

    /**
     * Obtiene un registro de Alicuota por IdAlicuota
     * @param id  IdAlicuota
     */
    @SuppressWarnings("unchecked")
    public AlicuotaRegistro getAliquotById(String id){
        Session session = sessionFactory.getCurrentSession();
        Criteria cr = session.createCriteria(AlicuotaRegistro.class, "reg");
        cr.add(Restrictions.eq("reg.idAlicuota", id));
        return (AlicuotaRegistro) cr.uniqueResult();
    }

    /**
     * Obtiene un registro de Alicuota Catalogo x idTomaMx, idEstudio, etiqueta
     * @param idTipoMx
     * @param  idEstudio
     * @param  etiqueta
     */
    @SuppressWarnings("unchecked")
    public Alicuota getAliquotCatByTipoMxEstudioEtiqueta(Integer idTipoMx, Integer idEstudio, String etiqueta){
        Session session = sessionFactory.getCurrentSession();
        Criteria cr = session.createCriteria(Alicuota.class, "catAlic");
        cr.createAlias("catAlic.tipoMuestra", "tipo" );
        cr.createAlias("catAlic.estudio", "estudio");
        cr.add(Restrictions.eq("tipo.idTipoMx", idTipoMx));
        cr.add(Restrictions.eq("estudio.idEstudio", idEstudio));
        cr.add(Restrictions.eq("catAlic.etiquetaPara", etiqueta));
        cr.add(Restrictions.eq("catAlic.pasivo", false));
        return (Alicuota) cr.uniqueResult();
    }

    /**
     * Obtiene volumen de catalogo alicuota
     * @param id  IdAlicuota
     */
    @SuppressWarnings("unchecked")
    public String getAliquotVolumeById(Integer id){
        String query = " select a.volumen from Alicuota a where a.idAlicuota = :id ";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("id",id);
        return (String) q.uniqueResult();
    }

    /**
     * Actualiza una Registro de Alicuotas
     *
     * @param dto Objeto a actualizar
     * @throws Exception
     */
    public void updateAlicuotaReg(AlicuotaRegistro dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.update(dto);
            }
            else
                throw new Exception("Objeto AlicuotaRegistro es NULL");
        }catch (Exception ex){
            logger.error("Error al actualizar AlicuotaRegistro",ex);
            throw ex;
        }
    }


    /**
     * Obtiene un registro de Alicuota Catalogo x idTomaMx, idDx, etiqueta
     * @param idTipoMx
     * @param  idDx
     * @param  etiqueta
     */
    @SuppressWarnings("unchecked")
    public Alicuota getAliquotCatByTipoMxDxEtiqueta(Integer idTipoMx, Integer idDx, String etiqueta){
        Session session = sessionFactory.getCurrentSession();
        Criteria cr = session.createCriteria(Alicuota.class, "catAlic");
        cr.createAlias("catAlic.tipoMuestra", "tipo" );
        cr.createAlias("catAlic.diagnostico", "dx");
        cr.add(Restrictions.eq("tipo.idTipoMx", idTipoMx));
        cr.add(Restrictions.eq("dx.idDiagnostico", idDx));
        cr.add(Restrictions.eq("catAlic.etiquetaPara", etiqueta));
        cr.add(Restrictions.eq("catAlic.pasivo", false));
        return (Alicuota) cr.uniqueResult();
    }

}
