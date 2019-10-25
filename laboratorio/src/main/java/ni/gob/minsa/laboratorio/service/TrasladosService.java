package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.muestra.*;
import ni.gob.minsa.laboratorio.domain.muestra.traslado.HistoricoEnvioMx;
import ni.gob.minsa.laboratorio.domain.muestra.traslado.TrasladoMx;
import ni.gob.minsa.laboratorio.domain.persona.SisPersona;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.AutoridadArea;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.AutoridadLaboratorio;
import ni.gob.minsa.laboratorio.domain.solicitante.Solicitante;
import org.apache.commons.codec.language.Soundex;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by FIRSTICT on 4/27/2015.
 * V1.0
 */
@Service("trasladosService")
@Transactional
public class TrasladosService {

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;

    /**
     * Agrega Registro de Traslado de Muestra
     */
    public void saveTrasladoMx(TrasladoMx trasladoMx) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(trasladoMx);
    }

    public void saveHistoricoEnvioMx(HistoricoEnvioMx historicoEnvioMx) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(historicoEnvioMx);
    }

    public TrasladoMx getTrasladoActivoMx(String idTomaMx){
        String query = "select t from TrasladoMx t inner join t.tomaMx mx " +
                "where mx.idTomaMx = :idTomaMx and t.recepcionado = false" ;
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idTomaMx",idTomaMx);
        return (TrasladoMx)q.uniqueResult();
    }

    /**
     * utilizado en la recepción, para filtrar si se debe mostrar deacuerdo al traslado activo en el momento de la búsqueda
     * @param idTomaMx
     * @return
     */
    public TrasladoMx getTrasladoActivoMxRecepcion(String idTomaMx, boolean enRecepGral){
        String query = "select t from TrasladoMx t inner join t.tomaMx mx " +
                "where mx.idTomaMx = :idTomaMx and t.recepcionado = false and (t.trasladoExterno = :enRecepGral or t.controlCalidad = :enRecepGral) order by t.prioridad asc" ;
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idTomaMx",idTomaMx);
        q.setParameter("enRecepGral",enRecepGral);
        List<TrasladoMx> trasladoMxList = q.list();
        if (trasladoMxList!=null && trasladoMxList.size() > 0)
            return trasladoMxList.get(0);
        else
            return null;
    }

    /**
     * utilizado en la recepción, para filtrar si se debe mostrar deacuerdo al traslado activo en el momento de la búsqueda
     * @param idTomaMx
     * @return
     */
    public TrasladoMx getTrasladoInternoActivoMxRecepcion(String idTomaMx){
        String query = "select t from TrasladoMx t inner join t.tomaMx mx " +
                "where mx.idTomaMx = :idTomaMx and t.recepcionado = false and t.trasladoInterno = true " +
                "order by t.prioridad asc" ;
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idTomaMx",idTomaMx);
        List<TrasladoMx> trasladoMxList = q.list();
        if (trasladoMxList!=null && trasladoMxList.size() > 0)
            return trasladoMxList.get(0);
        else
            return null;
    }

    public List<Catalogo_Dx> getRutinas() throws Exception {
        String query = "select distinct dx from Dx_TipoMx_TipoNoti dxrel inner join dxrel.diagnostico dx " +
                "where dxrel.pasivo = false" ;
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        return q.list();
    }

    @SuppressWarnings("unchecked")
    public List<DaTomaMx> getTomaMxByFiltro(FiltroMx filtro){
        Session session = sessionFactory.getCurrentSession();
        Soundex varSoundex = new Soundex();
        Criteria crit = session.createCriteria(DaTomaMx.class, "tomaMx");
        crit.createAlias("tomaMx.estadoMx","estado");
        crit.createAlias("tomaMx.idNotificacion", "notifi");
        //siempre se tomam las muestras que no estan anuladas
        crit.add( Restrictions.and(
                        Restrictions.eq("tomaMx.anulada", false))
        );//y las ordenes en estado según filtro
        if (filtro.getCodEstado()!=null) {
            crit.add(Restrictions.and(
                    Restrictions.eq("estado.codigo", filtro.getCodEstado()).ignoreCase()));
        }
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
                conditGroup.add(Subqueries.propertyIn("notifi.persona.personaId", DetachedCriteria.forClass(SisPersona.class,"person")
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
        //Se filtra por rango de fecha de toma de muestra
        if (filtro.getFechaInicioTomaMx()!=null && filtro.getFechaFinTomaMx()!=null){
            crit.add( Restrictions.and(
                            Restrictions.between("tomaMx.fechaHTomaMx", filtro.getFechaInicioTomaMx(),filtro.getFechaFinTomaMx()))
            );
        }
        // se filtra por tipo de muestra
        if (filtro.getCodTipoMx()!=null){
            crit.add( Restrictions.and(
                            Restrictions.eq("tomaMx.codTipoMx.idTipoMx", Integer.valueOf(filtro.getCodTipoMx())))
            );
        }

        //se filtran sólo las muestras de rutina
        crit.add(Subqueries.propertyIn("idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                .add(Restrictions.eq("anulado", false))
                .createAlias("idTomaMx", "idTomaMx")
                .setProjection(Property.forName("idTomaMx.idTomaMx"))));

        if(filtro.getCodigoUnicoMx()!=null){
            crit.add(Restrictions.or(
                            Restrictions.eq("tomaMx.codigoUnicoMx", filtro.getCodigoUnicoMx())).add(Restrictions.or(Restrictions.eq("tomaMx.codigoLab", filtro.getCodigoUnicoMx())))
            );
        }

        //Se filtra por rango de fecha de recepción en laboratorio
        if (filtro.getFechaInicioRecep()!=null && filtro.getFechaFinRecep()!=null){
            crit.add(Subqueries.propertyIn("idTomaMx", DetachedCriteria.forClass(RecepcionMx.class)
                    .createAlias("tomaMx", "toma")
                    .add(Subqueries.propertyIn("idRecepcion", DetachedCriteria.forClass(RecepcionMxLab.class)
                                    .createAlias("recepcionMx","recGen")
                                    .add(Restrictions.between("fechaHoraRecepcion", filtro.getFechaInicioRecep(),filtro.getFechaFinRecep()))
                                    .setProjection(Property.forName("recGen.idRecepcion")))
                    )
                    .setProjection(Property.forName("toma.idTomaMx"))));

        }
        //nombre solicitud de rutina
        if (filtro.getNombreSolicitud() != null) {
            crit.add(Subqueries.propertyIn("idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                    .add(Restrictions.eq("anulado", false))
                    .createAlias("codDx", "dx")
                    .add(Restrictions.ilike("dx.nombre", "%" + filtro.getNombreSolicitud() + "%"))
                    .setProjection(Property.forName("idTomaMx.idTomaMx"))));
        }

        //se filtra que usuario tenga autorizado laboratorio al que se envio la muestra desde ALERTA
        if (filtro.getNombreUsuario()!=null) {
            crit.createAlias("tomaMx.envio","envioMx");
            crit.add(Subqueries.propertyIn("envioMx.laboratorioDestino.codigo", DetachedCriteria.forClass(AutoridadLaboratorio.class)
                    .createAlias("laboratorio", "labautorizado")
                    .createAlias("user", "usuario")
                    .add(Restrictions.eq("pasivo",false)) //autoridad laboratorio activa
                    .add(Restrictions.and(Restrictions.eq("usuario.username",filtro.getNombreUsuario()))) //usuario
                    .setProjection(Property.forName("labautorizado.codigo"))));

            crit.add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(RecepcionMxLab.class)
                    .createAlias("recepcionMx","rmx")
                    .createAlias("area","area")
                    .add(Subqueries.propertyIn("area.idArea", DetachedCriteria.forClass(AutoridadArea.class)
                            .add(Restrictions.eq("pasivo", false)) //autoridad area activa
                            .add(Restrictions.and(Restrictions.eq("user.username", filtro.getNombreUsuario()))) //usuario
                            .setProjection(Property.forName("area.idArea"))))
                    .createAlias("rmx.tomaMx", "tomarmx")
                    .setProjection(Property.forName("tomarmx.idTomaMx"))));
        }

        //filtro para solicitudes aprobadas
        if (filtro.getSolicitudAprobada() != null) {
            Junction conditGroup = Restrictions.disjunction();
            conditGroup.add(Subqueries.propertyIn("idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                    .add(Restrictions.eq("anulado", false))
                    .add(Restrictions.eq("aprobada", filtro.getSolicitudAprobada()))
                    .createAlias("idTomaMx", "toma")
                    .setProjection(Property.forName("toma.idTomaMx"))))

                    .add(Subqueries.propertyIn("idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                            .add(Restrictions.eq("anulado", false))
                            .add(Restrictions.eq("aprobada", filtro.getSolicitudAprobada()))
                            .createAlias("idTomaMx", "toma")
                            .setProjection(Property.forName("toma.idTomaMx"))));

            crit.add(conditGroup);
        }

        return crit.list();
    }

    public List<DaTomaMx> getTomaMxCCByFiltro(FiltroMx filtro){
        Session session = sessionFactory.getCurrentSession();
        Soundex varSoundex = new Soundex();
        Criteria crit = session.createCriteria(DaTomaMx.class, "tomaMx");
        crit.createAlias("tomaMx.estadoMx","estado");
        crit.createAlias("tomaMx.idNotificacion", "notifi");
        //siempre se tomam las muestras que no estan anuladas
        crit.add( Restrictions.and(
                        Restrictions.eq("tomaMx.anulada", false))
        );//y las ordenes en estado según filtro
        if (filtro.getCodEstado()!=null) {
            crit.add(Restrictions.and(
                    Restrictions.eq("estado.codigo", filtro.getCodEstado()).ignoreCase()));
        }
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
                /*Junction conditionGroup = Restrictions.disjunction();
                conditionGroup.add(Restrictions.ilike("person.primerNombre", "%" + partes[i] + "%"))
                        .add(Restrictions.ilike("person.primerApellido", "%" + partes[i] + "%"))
                        .add(Restrictions.ilike("person.segundoNombre", "%" + partes[i] + "%"))
                        .add(Restrictions.ilike("person.segundoApellido", "%" + partes[i] + "%"))
                        .add(Restrictions.ilike("person.sndNombre", "%" + partesSnd[i] + "%"));
                crit.add(conditionGroup);*/
                Junction conditGroup = Restrictions.disjunction();
                conditGroup.add(Subqueries.propertyIn("notifi.persona.personaId", DetachedCriteria.forClass(SisPersona.class,"person")
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
        //Se filtra por rango de fecha de toma de muestra
        if (filtro.getFechaInicioTomaMx()!=null && filtro.getFechaFinTomaMx()!=null){
            crit.add( Restrictions.and(
                            Restrictions.between("tomaMx.fechaHTomaMx", filtro.getFechaInicioTomaMx(),filtro.getFechaFinTomaMx()))
            );
        }
        // se filtra por tipo de muestra
        if (filtro.getCodTipoMx()!=null){
            crit.add( Restrictions.and(
                            Restrictions.eq("tomaMx.codTipoMx.idTipoMx", Integer.valueOf(filtro.getCodTipoMx())))
            );
        }

        //se filtran sólo las muestras de rutina
        crit.add(Subqueries.propertyIn("idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                .add(Restrictions.eq("anulado", false))
                .createAlias("idTomaMx", "idTomaMx")
                .setProjection(Property.forName("idTomaMx.idTomaMx"))));

        if(filtro.getCodigoUnicoMx()!=null){
            crit.add( Restrictions.and(
                            Restrictions.eq("tomaMx.codigoLab", filtro.getCodigoUnicoMx()))
            );
        }
        //nombre solicitud de rutina
        if (filtro.getNombreSolicitud() != null) {
            crit.add(Subqueries.propertyIn("idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                    .add(Restrictions.eq("anulado", false))
                    .createAlias("codDx", "dx")
                    .add(Restrictions.ilike("dx.nombre", "%" + filtro.getNombreSolicitud() + "%"))
                    .setProjection(Property.forName("idTomaMx.idTomaMx"))));
        }

        //se filtra que usuario tenga autorizado laboratorio al que se envio la muestra desde ALERTA
        if (filtro.getNombreUsuario()!=null) {
            crit.createAlias("tomaMx.envio","envioMx");
            crit.add(Subqueries.propertyIn("envioMx.laboratorioDestino.codigo", DetachedCriteria.forClass(AutoridadLaboratorio.class)
                    .createAlias("laboratorio", "labautorizado")
                    .createAlias("user", "usuario")
                    .add(Restrictions.eq("pasivo",false)) //autoridad laboratorio activa
                    .add(Restrictions.and(Restrictions.eq("usuario.username",filtro.getNombreUsuario()))) //usuario
                    .setProjection(Property.forName("labautorizado.codigo"))));
        }

        //filtro para solicitudes aprobadas
        if (filtro.getSolicitudAprobada() != null) {
            Junction conditGroup = Restrictions.disjunction();
            conditGroup.add(Subqueries.propertyIn("idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                    .add(Restrictions.eq("anulado", false))
                    .add(Restrictions.eq("aprobada", filtro.getSolicitudAprobada()))
                    .createAlias("idTomaMx", "toma")
                    .setProjection(Property.forName("toma.idTomaMx"))))

                    .add(Subqueries.propertyIn("idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                            .add(Restrictions.eq("anulado", false))
                            .add(Restrictions.eq("aprobada", filtro.getSolicitudAprobada()))
                            .createAlias("idTomaMx", "toma")
                            .setProjection(Property.forName("toma.idTomaMx"))));

            crit.add(conditGroup);
        }

        //Se filtra por rango de fecha de aprobacion
        if (filtro.getFechaInicioAprob()!=null && filtro.getFechaFinAprob()!=null){
            Junction conditGroup = Restrictions.disjunction();
            conditGroup.add(Subqueries.propertyIn("idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                    .add(Restrictions.and(
                            Restrictions.between("fechaAprobacion", filtro.getFechaInicioAprob(),filtro.getFechaFinAprob())))
                    .createAlias("idTomaMx", "toma")
                    .setProjection(Property.forName("toma.idTomaMx"))))

                    .add(Subqueries.propertyIn("idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                            .add(Restrictions.and(
                                    Restrictions.between("fechaAprobacion", filtro.getFechaInicioAprob(),filtro.getFechaFinAprob())))
                            .createAlias("idTomaMx", "toma")
                            .setProjection(Property.forName("toma.idTomaMx"))));

            crit.add(conditGroup);
        }

        return crit.list();
    }

    /**
     * utilizado en la recepción, para filtrar si se debe mostrar deacuerdo al traslado activo en el momento de la búsqueda
     * @param idTomaMx
     * @return
     */
    public TrasladoMx getTrasladoCCMx(String idTomaMx){
        String query = "select t from TrasladoMx t inner join t.tomaMx mx " +
                "where mx.idTomaMx = :idTomaMx and t.controlCalidad = true order by t.fechaHoraRegistro asc" ;
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idTomaMx",idTomaMx);
        List<TrasladoMx> trasladoMxList = q.list();
        if (trasladoMxList!=null && trasladoMxList.size() > 0)
            return trasladoMxList.get(0);
        else
            return null;
    }
}
