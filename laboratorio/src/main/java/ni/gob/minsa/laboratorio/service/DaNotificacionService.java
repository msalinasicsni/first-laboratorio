package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.irag.DaIrag;
import ni.gob.minsa.laboratorio.domain.muestra.DaTomaMx;
import ni.gob.minsa.laboratorio.domain.muestra.FiltroMx;
import ni.gob.minsa.laboratorio.domain.notificacion.DaNotificacion;
import ni.gob.minsa.laboratorio.domain.persona.SisPersona;
import ni.gob.minsa.laboratorio.domain.solicitante.Solicitante;
import ni.gob.minsa.laboratorio.domain.vigilanciaSindFebril.DaSindFebril;
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
import java.util.List;

/**
 * Created by souyen-ics on 11-18-14.
 */

@Service("daNotificacionService")
@Transactional
public class DaNotificacionService {

    static final Logger logger = LoggerFactory.getLogger(DaNotificacion.class);

    @Resource(name ="sessionFactory")
    public SessionFactory sessionFactory;


    /**
     * Agrega Notificacion
     */
    public void addNotification(DaNotificacion noti) {
        Session session = sessionFactory.getCurrentSession();
        session.save(noti);
    }

    /**
     * Retorna notificacion
     * @param idNotificacion
     */
    public DaNotificacion getNotifById(String idNotificacion) {

        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery("FROM DaNotificacion noti where noti.idNotificacion = '" + idNotificacion + "'");
        return (DaNotificacion) query.uniqueResult();

    }

    @SuppressWarnings("unchecked")
    public List<DaNotificacion> getNoticesByPerson(long idPersona, String tipoNotificacion){
        Session session = sessionFactory.getCurrentSession();
        //todas las notificaciones tipo CASO ESPECIAL registradas para la persona seleccionada
            return session.createCriteria(DaNotificacion.class, "noti")
                    .createAlias("noti.persona", "persona")
                    .add(Restrictions.and(
                                    Restrictions.eq("persona.personaId", idPersona),
                                    Restrictions.eq("codTipoNotificacion.codigo", tipoNotificacion))
                    )
                    .list();

    }

    @SuppressWarnings("unchecked")
    public List<DaNotificacion> getNoticesByPerson(long idPersona){
        Session session = sessionFactory.getCurrentSession();
        //todas las notificaciones tipo CASO ESPECIAL registradas para la persona seleccionada
        return session.createCriteria(DaNotificacion.class, "noti")
                .createAlias("noti.persona", "persona")
                .add(Restrictions.and(
                                Restrictions.eq("persona.personaId", idPersona))
                ).addOrder(Order.desc("noti.fechaRegistro"))
                .list();

    }
    public void updateNotificacion(DaNotificacion dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.update(dto);
            }
            else
                throw new Exception("Objeto DaNotificacion es NULL");
        }catch (Exception ex){
            ex.printStackTrace();
            throw ex;
        }
    }

    /******************************************/
    /*******************Otras Muestras***********************/
    /******************************************/

    @SuppressWarnings("unchecked")
    public List<DaNotificacion> getNoticesByApplicant(String idSolicitante, String tipoNotificacion){
        Session session = sessionFactory.getCurrentSession();
        //todas las notificaciones tipo CASO ESPECIAL registradas para la persona seleccionada
        return session.createCriteria(DaNotificacion.class, "noti")
                .createAlias("noti.solicitante", "solicitante")
                .add(Restrictions.and(
                                Restrictions.eq("solicitante.idSolicitante", idSolicitante),
                                Restrictions.eq("codTipoNotificacion.codigo", tipoNotificacion))
                )
                .list();

    }

    public String getNumExpediente(String strIdNotificacion){
        String numExpediente = "";
        Session session = sessionFactory.getCurrentSession();
        //IRAG
        String query = "from DaIrag where idNotificacion.idNotificacion = :idNotificacion";
        Query q = session.createQuery(query);
        q.setParameter("idNotificacion", strIdNotificacion);

        //SINDROMES FEBRILES
        String query2 = "from DaSindFebril where idNotificacion.idNotificacion = :idNotificacion";
        Query q2 = session.createQuery(query2);
        q2.setParameter("idNotificacion", strIdNotificacion);

        DaIrag iragNoti= (DaIrag)q.uniqueResult();
        if(iragNoti!=null && iragNoti.getCodExpediente()!=null){
            numExpediente = iragNoti.getCodExpediente();
        }
        else {
            DaSindFebril sinFebNoti= (DaSindFebril)q2.uniqueResult();
            if (sinFebNoti!=null && sinFebNoti.getCodExpediente()!=null)
                numExpediente = sinFebNoti.getCodExpediente();
        }
        return numExpediente;
    }

    public List<DaNotificacion> getNoticesByFilro(FiltroMx filtro){
        Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(DaNotificacion.class, "notifi");
        Soundex varSoundex = new Soundex();
        //siempre se tomam las notificaciones activas
        crit.add( Restrictions.and(
                        Restrictions.eq("notifi.pasivo", false))
        );
        //no mostrar las muestras de notificaciones 'OTRAS MUESTRAS' pues son de laboratorio, ni CASOS ESPECIALES
        crit.add( Restrictions.and(
                Restrictions.ne("notifi.codTipoNotificacion.codigo", "TPNOTI|OMX").ignoreCase()));
        crit.add( Restrictions.and(
                Restrictions.ne("notifi.codTipoNotificacion.codigo", "TPNOTI|CAESP").ignoreCase()));
        // se filtra por nombre y apellido persona
        if (filtro.getNombreApellido()!=null) {
            //crit.createAlias("notifi.persona", "person");
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
                Junction conditGroup = Restrictions.disjunction();
                conditGroup.add(Subqueries.propertyIn("notifi.persona.personaId", DetachedCriteria.forClass(SisPersona.class, "person")
                        .add(Restrictions.or(Restrictions.ilike("person.primerNombre", "%" + partes[i] + "%"))
                                .add(Restrictions.or(Restrictions.ilike("person.primerApellido", "%" + partes[i] + "%"))
                                        .add(Restrictions.or(Restrictions.ilike("person.segundoNombre", "%" + partes[i] + "%"))
                                                .add(Restrictions.or(Restrictions.ilike("person.segundoApellido", "%" + partes[i] + "%"))
                                                        .add(Restrictions.or(Restrictions.ilike("person.sndNombre", "%" + partesSnd[i] + "%")))))))
                        .setProjection(Property.forName("personaId"))))
                        .add(Subqueries.propertyIn("notifi.solicitante.idSolicitante", DetachedCriteria.forClass(Solicitante.class,"solicitante")
                                .add(Restrictions.ilike("solicitante.nombre", "%" + partes[i] + "%"))
                                .setProjection(Property.forName("idSolicitante"))));

                crit.add(conditGroup);
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
        //Se filtra por rango de fecha de registro notificación
        if (filtro.getFechaInicioNotificacion()!=null && filtro.getFechaFinNotificacion()!=null){
            crit.add( Restrictions.and(
                            Restrictions.between("notifi.fechaRegistro", filtro.getFechaInicioNotificacion(),filtro.getFechaFinNotificacion()))
            );
        }

        if (filtro.getTipoNotificacion()!=null){
            crit.createAlias("notifi.codTipoNotificacion","tipoNoti");
            crit.add( Restrictions.and(
                            Restrictions.eq("tipoNoti.codigo", filtro.getTipoNotificacion()))
            );
        }

        if (filtro.getCodigoUnicoMx()!=null){
            crit.add(Subqueries.propertyIn("notifi.idNotificacion", DetachedCriteria.forClass(DaTomaMx.class)
                    .createAlias("idNotificacion", "notifiSub")
                    .add(Restrictions.eq("anulada",false))
                    .add(Restrictions.or(
                                    Restrictions.eq("codigoUnicoMx", filtro.getCodigoUnicoMx()))
                                    .add(Restrictions.or(Restrictions.eq("codigoLab", filtro.getCodigoUnicoMx())))
                    )
                    .setProjection(Property.forName("notifiSub.idNotificacion"))));
        }
        return crit.list();
    }
}
