package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.muestra.AlicuotaRegistro;
import ni.gob.minsa.laboratorio.domain.muestra.DaTomaMx;
import ni.gob.minsa.laboratorio.domain.muestra.FiltroMx;
import ni.gob.minsa.laboratorio.domain.muestra.OrdenExamen;
import ni.gob.minsa.laboratorio.domain.resultados.DetalleResultado;
import org.apache.commons.codec.language.Soundex;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by FIRSTICT on 11/21/2014.
 */
@Service("ordenExamenMxService")
@Transactional
public class OrdenExamenMxService {

    private Logger logger = LoggerFactory.getLogger(OrdenExamenMxService.class);

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;


    /**
     * Agrega una Registro de orden de examen
     *
     * @param dto Objeto a agregar
     * @throws Exception
     */
    public String addOrdenExamen(OrdenExamen dto) throws Exception {
        String idMaestro;
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                idMaestro = (String)session.save(dto);
            }
            else
                throw new Exception("Objeto Orden examen es NULL");
        }catch (Exception ex){
            logger.error("Error al agregar orden de examen",ex);
            throw ex;
        }
        return idMaestro;
    }

    /**
     * Actualiza una orden de examen
     *
     * @param dto Objeto a actualizar
     * @throws Exception
     */
    public void updateOrdenExamen(OrdenExamen dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.update(dto);
            }
            else
                throw new Exception("Objeto Orden Examen es NULL");
        }catch (Exception ex){
            logger.error("Error al actualizar recepci�n de muestra",ex);
            throw ex;
        }
    }

    public OrdenExamen getOrdenExamenById(String idOrdenExamen){
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery("from OrdenExamen where idOrdenExamen =:idOrdenExamen");
        q.setParameter("idOrdenExamen",idOrdenExamen);
        return (OrdenExamen)q.uniqueResult();
    }

    public List<OrdenExamen> getOrdenesExamenByIdMx(String idTomaMx){
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery("select oe from OrdenExamen as oe inner join oe.solicitudDx.idTomaMx as mx where mx.idTomaMx =:idTomaMx ");
        q.setParameter("idTomaMx",idTomaMx);
        return q.list();
    }

    public List<OrdenExamen> getOrdenesExamenNoAnuladasByCodigoUnico(String codigoUnico){
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery("select oe from OrdenExamen as oe inner join oe.solicitudDx.idTomaMx as mx where mx.codigoUnicoMx =:codigoUnico and oe.anulado = false ");
        q.setParameter("codigoUnico",codigoUnico);
        return q.list();
    }

    /**
     * Obtiene las ordenes de examen no anuladas para la muestra. Una toma no puede tener de dx y de estudios, es uno u otro.
     * @param idTomaMx id de la toma a consultar
     * @return List<OrdenExamen>
     */
    public List<OrdenExamen> getOrdenesExamenNoAnuladasByIdMx(String idTomaMx){
        Session session = sessionFactory.getCurrentSession();
        List<OrdenExamen> ordenExamenList = new ArrayList<OrdenExamen>();
        //se toman las que son de diagn�stico.
        Query q = session.createQuery("select oe from OrdenExamen as oe inner join oe.solicitudDx.idTomaMx as mx where mx.idTomaMx =:idTomaMx and oe.anulado = false ");
        q.setParameter("idTomaMx",idTomaMx);
        ordenExamenList = q.list();
        //se toman las que son de estudio
        Query q2 = session.createQuery("select oe from OrdenExamen as oe inner join oe.solicitudEstudio.idTomaMx as mx where mx.idTomaMx =:idTomaMx and oe.anulado = false ");
        q2.setParameter("idTomaMx",idTomaMx);
        ordenExamenList.addAll(q2.list());
        return ordenExamenList;
    }

    public List<OrdenExamen> getOrdExamenNoAnulByIdMxIdDxIdExamen(String idTomaMx, int idDx, int idExamen){
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery("select oe from OrdenExamen as oe inner join oe.solicitudDx as sdx inner join sdx.idTomaMx as mx where mx.idTomaMx =:idTomaMx " +
                "and sdx.codDx.idDiagnostico = :idDx and oe.codExamen.idExamen = :idExamen and oe.anulado = false ");
        q.setParameter("idTomaMx",idTomaMx);
        q.setParameter("idDx",idDx);
        q.setParameter("idExamen",idExamen);
        return q.list();
    }

    public List<OrdenExamen> getOrdenesExamenDxByFiltro(FiltroMx filtro){
        Session session = sessionFactory.getCurrentSession();
        Soundex varSoundex = new Soundex();
        Criteria crit = session.createCriteria(OrdenExamen.class, "ordenEx");
        crit.createAlias("ordenEx.solicitudDx","solicitudDx");
        crit.createAlias("solicitudDx.idTomaMx","tomaMx");
        crit.createAlias("tomaMx.estadoMx","estado");
        crit.createAlias("tomaMx.idNotificacion", "notifi");
        //siempre se tomam las muestras que no estan anuladas
        crit.add( Restrictions.and(
                        Restrictions.eq("tomaMx.anulada", false))
        );
        //siempre se tomam las ordenes que no estan anuladas
        crit.add( Restrictions.and(
                        Restrictions.eq("ordenEx.anulado", false))
        );
        //y las ordenes en estado seg�n filtro
        if (filtro.getCodEstado()!=null) {
            crit.add(Restrictions.and(
                    Restrictions.eq("estado.codigo", filtro.getCodEstado()).ignoreCase()));
        }
        // se filtra por nombre y apellido persona
        if (filtro.getNombreApellido()!=null) {
            crit.createAlias("notifi.persona", "person");
            String[] partes = filtro.getNombreApellido().split(" ");
            String[] partesSnd = filtro.getNombreApellido().split(" ");
            for (int i = 0; i < partes.length; i++) {
                try {
                    partesSnd[i] = varSoundex.encode(partes[i]);
                } catch (IllegalArgumentException e) {
                    partesSnd[i] = "0000";
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < partes.length; i++) {
                Junction conditionGroup = Restrictions.disjunction();
                conditionGroup.add(Restrictions.ilike("person.primerNombre", "%" + partes[i] + "%"))
                        .add(Restrictions.ilike("person.primerApellido", "%" + partes[i] + "%"))
                        .add(Restrictions.ilike("person.segundoNombre", "%" + partes[i] + "%"))
                        .add(Restrictions.ilike("person.segundoApellido", "%" + partes[i] + "%"))
                        .add(Restrictions.ilike("person.sndNombre", "%" + partesSnd[i] + "%"));
                crit.add(conditionGroup);
            }
        }
        //se filtra por SILAIS
        if (filtro.getCodSilais()!=null){
            crit.createAlias("notifi.codSilaisAtencion","silais");
            crit.add( Restrictions.and(
                            Restrictions.eq("silais.codigo", Long.valueOf(filtro.getCodSilais())))
            );
        }
        //se filtra por unidad de salud
        if (filtro.getCodUnidadSalud()!=null){
            crit.createAlias("notifi.codUnidadAtencion","unidadS");
            crit.add( Restrictions.and(
                            Restrictions.eq("unidadS.codigo", Long.valueOf(filtro.getCodUnidadSalud())))
            );
        }
        //Se filtra por rango de fecha de toma de muestra
        if (filtro.getFechaInicioTomaMx()!=null && filtro.getFechaFinTomaMx()!=null){
            crit.add( Restrictions.and(
                            Restrictions.between("tomaMx.fechaRegistro", filtro.getFechaInicioTomaMx(),filtro.getFechaFinTomaMx()))
            );
        }
        // se filtra por tipo de muestra
        if (filtro.getCodTipoMx()!=null){
            crit.add( Restrictions.and(
                            Restrictions.eq("tomaMx.codTipoMx.idTipoMx", Integer.valueOf(filtro.getCodTipoMx())))
            );
        }
        /*crit.add( Subqueries.propertyNotIn("idAlicuota", DetachedCriteria.forClass(DetalleResultado.class)
                .createAlias("alicuotaRegistro", "resultado").add(Restrictions.eq("pasivo", false))
                .setProjection(Property.forName("resultado.idAlicuota"))));*/
        return crit.list();
    }

    public List<OrdenExamen> getOrdenesExamenEstudioByFiltro(FiltroMx filtro){
        Session session = sessionFactory.getCurrentSession();
        Soundex varSoundex = new Soundex();
        Criteria crit = session.createCriteria(OrdenExamen.class, "ordenEx");
        crit.createAlias("ordenEx.solicitudEstudio","solicitudEstudio");
        crit.createAlias("solicitudEstudio.idTomaMx","tomaMx");
        crit.createAlias("tomaMx.estadoMx","estado");
        crit.createAlias("tomaMx.idNotificacion", "notifi");
        //siempre se tomam las muestras que no estan anuladas
        crit.add( Restrictions.and(
                        Restrictions.eq("tomaMx.anulada", false))
        );
        //siempre se tomam las ordenes que no estan anuladas
        crit.add( Restrictions.and(
                        Restrictions.eq("ordenEx.anulado", false))
        );
        //y las ordenes en estado seg�n filtro
        if (filtro.getCodEstado()!=null) {
            crit.add(Restrictions.and(
                    Restrictions.eq("estado.codigo", filtro.getCodEstado()).ignoreCase()));
        }
        // se filtra por nombre y apellido persona
        if (filtro.getNombreApellido()!=null) {
            crit.createAlias("notifi.persona", "person");
            String[] partes = filtro.getNombreApellido().split(" ");
            String[] partesSnd = filtro.getNombreApellido().split(" ");
            for (int i = 0; i < partes.length; i++) {
                try {
                    partesSnd[i] = varSoundex.encode(partes[i]);
                } catch (IllegalArgumentException e) {
                    partesSnd[i] = "0000";
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < partes.length; i++) {
                Junction conditionGroup = Restrictions.disjunction();
                conditionGroup.add(Restrictions.ilike("person.primerNombre", "%" + partes[i] + "%"))
                        .add(Restrictions.ilike("person.primerApellido", "%" + partes[i] + "%"))
                        .add(Restrictions.ilike("person.segundoNombre", "%" + partes[i] + "%"))
                        .add(Restrictions.ilike("person.segundoApellido", "%" + partes[i] + "%"))
                        .add(Restrictions.ilike("person.sndNombre", "%" + partesSnd[i] + "%"));
                crit.add(conditionGroup);
            }
        }
        //se filtra por SILAIS
        if (filtro.getCodSilais()!=null){
            crit.createAlias("notifi.codSilaisAtencion","silais");
            crit.add( Restrictions.and(
                            Restrictions.eq("silais.codigo", Long.valueOf(filtro.getCodSilais())))
            );
        }
        //se filtra por unidad de salud
        if (filtro.getCodUnidadSalud()!=null){
            crit.createAlias("notifi.codUnidadAtencion","unidadS");
            crit.add( Restrictions.and(
                            Restrictions.eq("unidadS.codigo", Long.valueOf(filtro.getCodUnidadSalud())))
            );
        }
        //Se filtra por rango de fecha de toma de muestra
        if (filtro.getFechaInicioTomaMx()!=null && filtro.getFechaFinTomaMx()!=null){
            crit.add( Restrictions.and(
                            Restrictions.between("tomaMx.fechaRegistro", filtro.getFechaInicioTomaMx(),filtro.getFechaFinTomaMx()))
            );
        }
        // se filtra por tipo de muestra
        if (filtro.getCodTipoMx()!=null){
            crit.add( Restrictions.and(
                            Restrictions.eq("tomaMx.codTipoMx.idTipoMx", Integer.valueOf(filtro.getCodTipoMx())))
            );
        }
        /*crit.add( Subqueries.propertyNotIn("idAlicuota", DetachedCriteria.forClass(DetalleResultado.class)
                .createAlias("alicuotaRegistro", "resultado").add(Restrictions.eq("pasivo", false))
                .setProjection(Property.forName("resultado.idAlicuota"))));*/
        return crit.list();
    }

    public List<OrdenExamen> getOrdExamenNoAnulByIdMxIdEstIdExamen(String idTomaMx, int idEstudio, int idExamen){
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery("select oe from OrdenExamen as oe inner join oe.solicitudEstudio as sdx inner join sdx.idTomaMx as mx where mx.idTomaMx =:idTomaMx " +
                "and sdx.tipoEstudio.idEstudio = :idEstudio and oe.codExamen.idExamen = :idExamen and oe.anulado = false ");
        q.setParameter("idTomaMx",idTomaMx);
        q.setParameter("idEstudio",idEstudio);
        q.setParameter("idExamen",idExamen);
        return q.list();
    }
}