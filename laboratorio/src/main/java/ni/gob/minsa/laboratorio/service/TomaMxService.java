package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.examen.Area;
import ni.gob.minsa.laboratorio.domain.muestra.*;
import ni.gob.minsa.laboratorio.domain.muestra.traslado.HistoricoEnvioMx;
import ni.gob.minsa.laboratorio.domain.persona.SisPersona;
import ni.gob.minsa.laboratorio.domain.resultados.DetalleResultadoFinal;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.AutoridadLaboratorio;
import ni.gob.minsa.laboratorio.domain.solicitante.Solicitante;
import ni.gob.minsa.laboratorio.utilities.DateUtil;
import ni.gob.minsa.laboratorio.utilities.reportes.DatosSolicitud;
import ni.gob.minsa.laboratorio.utilities.reportes.ResultadoVigilancia;
import ni.gob.minsa.laboratorio.utilities.reportes.Solicitud;
import ni.gob.minsa.laboratorio.utilities.StringUtil;
import org.apache.commons.codec.language.Soundex;
import org.hibernate.*;
import org.hibernate.criterion.*;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by souyen-ics on 11-05-14.
 */
@Service("tomaMxService")
@Transactional
public class TomaMxService {

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;


    public void updateTomaMx(DaTomaMx dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.update(dto);
            }
            else
                throw new Exception("Objeto toma Mx es NULL");
        }catch (Exception ex){
            ex.printStackTrace();
            throw ex;
        }
    }

    /**
     * Agrega Registro de Toma de Muestra
     */
    public void addTomaMx(DaTomaMx toma) {
        Session session = sessionFactory.getCurrentSession();
        session.save(toma);
    }

    /**
     * Eliminar tomaMx
     * @param toma
     */
    public void deleteTomaMx(DaTomaMx toma) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(toma);
    }

    public Integer anularTomasMxByIdNotificacion(String idNotificacion, Timestamp fechaAnulacion) {
        // Retrieve session from Hibernate
        Session s = sessionFactory.openSession();
        Transaction tx = s.beginTransaction();

        String hqlUpdate = "update DaTomaMx mx set mx.anulada = true, mx.fechaAnulacion = :fechaAnulacion where mx.idNotificacion.idNotificacion = :idNotificacion";
        int updatedEntities = s.createQuery( hqlUpdate )
                .setParameter("fechaAnulacion",fechaAnulacion)
                .setString("idNotificacion", idNotificacion)
                .executeUpdate();
        tx.commit();
        s.close();
        return updatedEntities;
    }

    /**
     * Retorna toma de muestra
     * @param id
     */
    public DaTomaMx getTomaMxById(String id){
        String query = "from DaTomaMx where idTomaMx = :id";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("id", id);
        return (DaTomaMx)q.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<DaTomaMx> getTomaMxByFiltro(FiltroMx filtro){
        Session session = sessionFactory.getCurrentSession();
        Soundex varSoundex = new Soundex();
        Criteria crit = session.createCriteria(DaTomaMx.class, "tomaMx");
        crit.createAlias("tomaMx.estadoMx","estado");
        crit.createAlias("tomaMx.idNotificacion", "notifi");
        if (!filtro.getIncluirAnuladas()) {
            //siempre se tomam las muestras que no estan anuladas
            crit.add(Restrictions.and(
                            Restrictions.eq("tomaMx.anulada", false))
            );
        }
        //y las ordenes en estado según filtro
        if (filtro.getCodEstado()!=null) {
            if (filtro.getIncluirTraslados()){
                crit.add(Restrictions.or(
                        Restrictions.eq("estado.codigo", filtro.getCodEstado()).ignoreCase()).
                        add(Restrictions.or(
                                Restrictions.eq("estado.codigo", "ESTDMX|TRAS"))));
            }else {
                crit.add(Restrictions.and(
                        Restrictions.eq("estado.codigo", filtro.getCodEstado()).ignoreCase()));
            }
        }
        if(filtro.getCodigoUnicoMx()!=null){
            crit.add(Restrictions.or(
                            Restrictions.eq("tomaMx.codigoUnicoMx", filtro.getCodigoUnicoMx())).add(Restrictions.or(Restrictions.eq("tomaMx.codigoLab", filtro.getCodigoUnicoMx())))
            );
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
        //Se filtra por rango de fecha de notificación
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
        // se filtra por tipo de muestra
        if (filtro.getCodTipoMx()!=null){
            crit.add( Restrictions.and(
                            Restrictions.eq("tomaMx.codTipoMx.idTipoMx", Integer.valueOf(filtro.getCodTipoMx())))
            );
        }
        
        // se filtra por codigo VIH
        if (filtro.getCodigoVIH()!=null){
            crit.add( Restrictions.and(
                            Restrictions.eq("notifi.codigoPacienteVIH", filtro.getCodigoVIH()))
            );
        }

        //Se filtra por rango de fecha de recepción
        if (filtro.getFechaInicioRecep()!=null && filtro.getFechaFinRecep()!=null){
            crit.add(Subqueries.propertyIn("idTomaMx", DetachedCriteria.forClass(RecepcionMx.class)
                    .createAlias("tomaMx", "toma").add(Restrictions.between("fechaHoraRecepcion", filtro.getFechaInicioRecep(),filtro.getFechaFinRecep()))
                    .setProjection(Property.forName("toma.idTomaMx"))));

        }

        //se filtra por tipo de solicitud
        if(filtro.getCodTipoSolicitud()!=null){
            if(filtro.getCodTipoSolicitud().equals("Estudio")){
                crit.add(Subqueries.propertyIn("idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                        .add(Restrictions.eq("anulado",false))
                        .createAlias("idTomaMx", "idTomaMx")
                        .setProjection(Property.forName("idTomaMx.idTomaMx"))));
            }else{
                crit.add(Subqueries.propertyIn("idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                        .add(Restrictions.eq("anulado",false))
                        .createAlias("idTomaMx", "idTomaMx")
                        .setProjection(Property.forName("idTomaMx.idTomaMx"))));
            }

        }

        //nombre solicitud
        if (filtro.getNombreSolicitud() != null) {
            if (filtro.getCodTipoSolicitud() != null) {
                if (filtro.getCodTipoSolicitud().equals("Estudio")) {
                    crit.add(Subqueries.propertyIn("idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                            .add(Restrictions.eq("anulado",false))
                            .createAlias("tipoEstudio", "estudio")
                            .add(Restrictions.ilike("estudio.nombre", "%" + filtro.getNombreSolicitud() + "%"))
                            .setProjection(Property.forName("idTomaMx.idTomaMx"))));
                } else {
                    crit.add(Subqueries.propertyIn("idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                            .add(Restrictions.eq("anulado",false))
                            .createAlias("codDx", "dx")
                            .add(Restrictions.ilike("dx.nombre", "%" + filtro.getNombreSolicitud() + "%"))
                            .setProjection(Property.forName("idTomaMx.idTomaMx"))));
                }
            } else {

                Junction conditGroup = Restrictions.disjunction();
                conditGroup.add(Subqueries.propertyIn("idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                        .add(Restrictions.eq("anulado",false))
                        .createAlias("tipoEstudio", "estudio")
                        .add(Restrictions.ilike("estudio.nombre", "%" + filtro.getNombreSolicitud() + "%"))
                        .setProjection(Property.forName("idTomaMx.idTomaMx"))))
                        .add(Subqueries.propertyIn("idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                                .add(Restrictions.eq("anulado",false))
                                .createAlias("codDx", "dx")
                                .add(Restrictions.ilike("dx.nombre", "%" + filtro.getNombreSolicitud() + "%"))
                                .setProjection(Property.forName("idTomaMx.idTomaMx"))));

                crit.add(conditGroup);
            }
        }
        //se filtra que usuario tenga autorizado laboratorio al que se envio la muestra desde ALERTA
        if (filtro.getNombreUsuario()!=null) {
            /*crit.createAlias("tomaMx.envio","envioMx");
            crit.add(Subqueries.propertyIn("envioMx.laboratorioDestino.codigo", DetachedCriteria.forClass(AutoridadLaboratorio.class)
                    .createAlias("laboratorio", "labautorizado")
                    .createAlias("user", "usuario")
                    .add(Restrictions.eq("pasivo",false)) //autoridad laboratorio activa
                    .add(Restrictions.and(Restrictions.eq("usuario.username",filtro.getNombreUsuario()))) //usuario
                    .setProjection(Property.forName("labautorizado.codigo"))));
            */
            Junction conditGroup = Restrictions.disjunction();

            //usuario tiene autorizado envio actual, o alguno que este en histórico para la muestra
            conditGroup.add(Subqueries.propertyIn("tomaMx.envio.idEnvio", DetachedCriteria.forClass(DaEnvioMx.class)
                    .createAlias("laboratorioDestino", "destino")
                    .add(Subqueries.propertyIn("destino.codigo", DetachedCriteria.forClass(AutoridadLaboratorio.class)
                            .createAlias("laboratorio", "labautorizado")
                            .add(Restrictions.eq("pasivo", false)) //autoridad area activa
                            .add(Restrictions.and(Restrictions.eq("user.username", filtro.getNombreUsuario()))) //usuario
                            .setProjection(Property.forName("labautorizado.codigo"))))
                    .setProjection(Property.forName("idEnvio"))))
                    .add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(HistoricoEnvioMx.class)
                            .createAlias("envioMx", "envio")
                            .add(Subqueries.propertyIn("envio.laboratorioDestino.codigo", DetachedCriteria.forClass(AutoridadLaboratorio.class)
                                    .createAlias("laboratorio", "labautorizado")
                                    .add(Restrictions.eq("pasivo", false)) //autoridad area activa
                                    .add(Restrictions.and(Restrictions.eq("user.username", filtro.getNombreUsuario()))) //usuario
                                    .setProjection(Property.forName("labautorizado.codigo"))))
                            .createAlias("tomaMx", "toma")
                            .setProjection(Property.forName("toma.idTomaMx"))));

            crit.add(conditGroup);
        }

        //filtro para solicitudes aprobadas
        if (filtro.getSolicitudAprobada() != null) {

            //filtro por fecha de procesamiento de dx
            if (filtro.getFechaInicioProcesamiento()!=null && filtro.getFechaFinProcesamiento()!=null){
                Junction conditGroup = Restrictions.disjunction();
                conditGroup.add(Subqueries.propertyIn("idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                        .add(Restrictions.eq("aprobada", filtro.getSolicitudAprobada()))
                        .add(Restrictions.eq("anulado",false))
                        .add(Subqueries.propertyIn("idSolicitudEstudio", DetachedCriteria.forClass(DetalleResultadoFinal.class)
                                .createAlias("solicitudEstudio", "estudio").add(Restrictions.eq("pasivo",false))
                                .add(Restrictions.between("fechahRegistro", filtro.getFechaInicioProcesamiento(),filtro.getFechaFinProcesamiento()))
                                .setProjection(Property.forName("estudio.idSolicitudEstudio"))))
                        .createAlias("idTomaMx", "toma")
                        .setProjection(Property.forName("toma.idTomaMx"))))
                        .add(Subqueries.propertyIn("idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                                .add(Restrictions.eq("aprobada", filtro.getSolicitudAprobada()))
                                .add(Restrictions.eq("anulado",false))
                                .add(Subqueries.propertyIn("idSolicitudDx", DetachedCriteria.forClass(DetalleResultadoFinal.class)
                                        .createAlias("solicitudDx", "dx").add(Restrictions.eq("pasivo",false))
                                        .add(Restrictions.between("fechahRegistro", filtro.getFechaInicioProcesamiento(),filtro.getFechaFinProcesamiento()))
                                        .setProjection(Property.forName("dx.idSolicitudDx"))))
                                .createAlias("idTomaMx", "toma")
                                .setProjection(Property.forName("toma.idTomaMx"))));
                crit.add(conditGroup);
            } else  if (filtro.getFechaInicioAprob()!=null && filtro.getFechaFinAprob()!=null){
                Junction conditGroup = Restrictions.disjunction();
                conditGroup.add(Subqueries.propertyIn("idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                        .add(Restrictions.eq("aprobada", filtro.getSolicitudAprobada()))
                        .add(Restrictions.eq("anulado",false))
                        .add(Restrictions.between("fechaAprobacion", filtro.getFechaInicioAprob(),filtro.getFechaFinAprob()))
                        .createAlias("idTomaMx", "toma")
                        .setProjection(Property.forName("toma.idTomaMx"))))
                        .add(Subqueries.propertyIn("idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                                .add(Restrictions.eq("aprobada", filtro.getSolicitudAprobada()))
                                .add(Restrictions.eq("anulado",false))
                                .add(Restrictions.between("fechaAprobacion", filtro.getFechaInicioAprob(),filtro.getFechaFinAprob()))
                                .createAlias("idTomaMx", "toma")
                                .setProjection(Property.forName("toma.idTomaMx"))));
                crit.add(conditGroup);
            } else{
                Junction conditGroup = Restrictions.disjunction();
                conditGroup.add(Subqueries.propertyIn("idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                        .add(Restrictions.eq("aprobada", filtro.getSolicitudAprobada()))
                        .add(Restrictions.eq("anulado",false))
                        .createAlias("idTomaMx", "toma")
                        .setProjection(Property.forName("toma.idTomaMx"))))

                        .add(Subqueries.propertyIn("idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                                .add(Restrictions.eq("aprobada", filtro.getSolicitudAprobada()))
                                .add(Restrictions.eq("anulado",false))
                                .createAlias("idTomaMx", "toma")
                                .setProjection(Property.forName("toma.idTomaMx"))));
                crit.add(conditGroup);
            }

        }

        //filtro sólo control calidad en el laboratio del usuario
        if (filtro.getControlCalidad()!=null) {
            if (filtro.getControlCalidad()){  //si hay filtro por control de calidad y es "Si", sólo incluir rutinas
                crit.add(Subqueries.propertyIn("idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                        .add(Restrictions.eq("anulado",false))
                        .add(Restrictions.eq("controlCalidad", filtro.getControlCalidad()))
                        .createAlias("idTomaMx", "toma")
                                //.createAlias("labProcesa","labProcesa")
                        .add(Subqueries.propertyIn("labProcesa.codigo", DetachedCriteria.forClass(AutoridadLaboratorio.class)
                                .createAlias("laboratorio", "labautorizado")
                                .createAlias("user", "usuario")
                                .add(Restrictions.eq("pasivo",false)) //autoridad laboratorio activa
                                .add(Restrictions.and(Restrictions.eq("usuario.username",filtro.getNombreUsuario()))) //usuario
                                .setProjection(Property.forName("labautorizado.codigo"))))
                        .setProjection(Property.forName("toma.idTomaMx"))));
            }else { //si hay filtro por control de calidad y es "No", siempre incluir los estudios
                Junction conditGroup = Restrictions.disjunction();
                conditGroup.add(Subqueries.propertyIn("idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                        .add(Restrictions.eq("anulado",false))
                        .add(Restrictions.eq("controlCalidad", filtro.getControlCalidad()))
                        .createAlias("idTomaMx", "toma")
                                //.createAlias("labProcesa","labProcesa")
                        .add(Subqueries.propertyIn("labProcesa.codigo", DetachedCriteria.forClass(AutoridadLaboratorio.class)
                                .createAlias("laboratorio", "labautorizado")
                                .createAlias("user", "usuario")
                                .add(Restrictions.eq("pasivo", false)) //autoridad laboratorio activa
                                .add(Restrictions.and(Restrictions.eq("usuario.username", filtro.getNombreUsuario()))) //usuario
                                .setProjection(Property.forName("labautorizado.codigo"))))
                        .setProjection(Property.forName("toma.idTomaMx"))))
                        .add(Restrictions.or(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                                .add(Restrictions.eq("anulado",false))
                                .createAlias("idTomaMx", "idTomaMx")
                                .setProjection(Property.forName("idTomaMx.idTomaMx")))));
                crit.add(conditGroup);
            }
        }

        return crit.list();
    }

    public List<DaSolicitudDx> getSolicitudesDxByIdToma(String idTomaMx, String codigoLab){
        String query = "select distinct sdx from DaSolicitudDx sdx inner join sdx.idTomaMx mx " +
                "where sdx.anulado = false and mx.idTomaMx = :idTomaMx " +
                "and (sdx.labProcesa.codigo = :codigoLab" +
                " or sdx.idSolicitudDx in (select oe.solicitudDx.idSolicitudDx " +
                "                   from OrdenExamen oe where oe.solicitudDx.idSolicitudDx = sdx.idSolicitudDx and oe.labProcesa.codigo = :codigoLab )) " +
                "ORDER BY sdx.fechaHSolicitud ";

        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idTomaMx",idTomaMx);
        q.setParameter("codigoLab",codigoLab);
        return q.list();
    }

    public List<Solicitud> getSolicitudesDxByIdTomaV2(String idTomaMx, String codigoLab){
        String query = "select distinct sdx.codDx.idDiagnostico as idSolicitud, sdx.idSolicitudDx as idSolicitudDx, sdx.codDx.nombre as nombre, sdx.aprobada as aprobada, sdx.codDx.area.idArea as idArea, sdx.fechaHSolicitud as fechaSolicitud " +
                "from DaSolicitudDx sdx inner join sdx.idTomaMx mx " +
                "where sdx.anulado = false and mx.idTomaMx = :idTomaMx " +
                "and (sdx.labProcesa.codigo = :codigoLab" +
                " or sdx.idSolicitudDx in (select oe.solicitudDx.idSolicitudDx " +
                "                   from OrdenExamen oe where oe.solicitudDx.idSolicitudDx = sdx.idSolicitudDx and oe.labProcesa.codigo = :codigoLab )" +
                " or mx.idTomaMx = (select r.tomaMx.idTomaMx from RecepcionMx r where r.tomaMx.idTomaMx = :idTomaMx and r.trasladoViajero = true )) ";

        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idTomaMx",idTomaMx);
        q.setParameter("codigoLab",codigoLab);
        q.setResultTransformer(Transformers.aliasToBean(Solicitud.class));
        return q.list();
    }

    public List<DaSolicitudDx> getSolicitudesDxByIdTomaAreaLabUser(String idTomaMx, String username){
        String query = "select distinct sdx from DaSolicitudDx sdx inner join sdx.idTomaMx mx ," +
                "AutoridadLaboratorio al, AutoridadArea aa " +
                "where al.pasivo = false and sdx.anulado = false and mx.idTomaMx = :idTomaMx " +
                "and al.user.username = :username and aa.user.username = :username " +
                "and (sdx.labProcesa.codigo = al.laboratorio.codigo" +
                " or sdx.idSolicitudDx in (select oe.solicitudDx.idSolicitudDx " +
                "                   from OrdenExamen oe where oe.solicitudDx.idSolicitudDx = sdx.idSolicitudDx and oe.labProcesa.codigo = al.laboratorio.codigo )) " +
                "and sdx.codDx.area.idArea = aa.area.idArea " +
                "ORDER BY sdx.fechaHSolicitud ";

        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idTomaMx",idTomaMx);
        q.setParameter("username",username);
        return q.list();
    }

    public List<DaSolicitudEstudio> getSolicitudesEstudioByIdMxUser(String idTomaMx, String username){
        String query = "select sde from DaSolicitudEstudio sde, AutoridadArea aa " +
                "where sde.anulado = false and sde.idTomaMx.idTomaMx = :idTomaMx " +
                "and aa.user.username = :username and aa.area.idArea = sde.tipoEstudio.area.idArea " +
                "ORDER BY sde.fechaHSolicitud";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idTomaMx",idTomaMx);
        q.setParameter("username",username);
        return q.list();
    }

    public List<DaSolicitudEstudio> getSolicitudesEstudioByIdTomaArea(String idTomaMx, int idArea, String username){
        String query = "select sdx from DaSolicitudEstudio sdx, AutoridadArea al where al.pasivo = false and sdx.anulado = false and sdx.idTomaMx.idTomaMx = :idTomaMx " +
                "and sdx.tipoEstudio.area.idArea = al.area.idArea and sdx.tipoEstudio.area.idArea = :idArea and al.user.username = :username " +
                "ORDER BY sdx.fechaHSolicitud";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idTomaMx",idTomaMx);
        q.setParameter("idArea",idArea);
        q.setParameter("username",username);
        return q.list();
    }

    public DaSolicitudDx getSolicitudesDxByMxDx(String idTomaMx,  Integer idDiagnostico){
        String query = "from DaSolicitudDx where anulado = false and idTomaMx.idTomaMx = :idTomaMx and idTomaMx.envio.laboratorioDestino.codigo = labProcesa.codigo " +
                "and codDx.idDiagnostico = :idDiagnostico ORDER BY fechaHSolicitud";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idTomaMx",idTomaMx);
        q.setParameter("idDiagnostico",idDiagnostico);
        return (DaSolicitudDx)q.uniqueResult();
    }

    public DaSolicitudDx getSolicitudDxByIdSolicitud(String idSolicitud){
        String query = "from DaSolicitudDx where idSolicitudDx = :idSolicitud";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idSolicitud",idSolicitud);
        return (DaSolicitudDx)q.uniqueResult();
    }

    public DaSolicitudEstudio getEstudioByIdSolicitud(String idSolicitud){
        String query = "from DaSolicitudEstudio where idSolicitudEstudio = :idSolicitud";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idSolicitud",idSolicitud);
        return (DaSolicitudEstudio)q.uniqueResult();
    }

    /**
     *Retorna una lista de dx segun tipoMx y tipo Notificacion
     * @param codMx tipo de Mx
     * @param tipoNoti tipo Notificacion
     *
     */
    @SuppressWarnings("unchecked")
    public List<Dx_TipoMx_TipoNoti> getDx(String codMx, String tipoNoti, String userName, String idTomaMx) throws Exception {
        String query = "select dx from Dx_TipoMx_TipoNoti dx " +
                "where dx.tipoMx_tipoNotificacion.tipoMx.idTipoMx = :codMx " +
                "and dx.tipoMx_tipoNotificacion.tipoNotificacion.codigo = :tipoNoti and dx.diagnostico.pasivo = false";
        if (userName!=null) {
          query +=  " and dx.diagnostico.area.idArea in (select a.idArea from AutoridadArea as aa inner join aa.area as a where aa.pasivo = false and aa.user.username = :userName)";
        }
        if (idTomaMx!=null) {
            query += " and dx.diagnostico.idDiagnostico in (select sdx.codDx.idDiagnostico from DaSolicitudDx sdx where sdx.idTomaMx.idTomaMx = :idTomaMx )";
        }
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("codMx", codMx);
        q.setString("tipoNoti", tipoNoti);
        if (userName!=null) {
            q.setString("userName", userName);
        }
        if (idTomaMx!=null) {
            q.setString("idTomaMx", idTomaMx);
        }
        return q.list();
    }

    public List<Catalogo_Dx> getDxsByTipoNoti(String tipoNoti) throws Exception {
        String query = "select dx from Dx_TipoMx_TipoNoti dxrel inner join dxrel.diagnostico dx where dxrel.tipoMx_tipoNotificacion.tipoNotificacion.codigo = :tipoNoti" ;
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("tipoNoti", tipoNoti);
        return q.list();
    }

    public DaTomaMx getTomaMxByCodUnicoMx(String codigoUnicoMx){
        String query = "from DaTomaMx as a where codigoUnicoMx= :codigoUnicoMx";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("codigoUnicoMx", codigoUnicoMx);
        return  (DaTomaMx)q.uniqueResult();
    }

    public DaTomaMx getTomaMxByCodLab(String codigoLab){
        String query = "from DaTomaMx as a where codigoLab= :codigoLab or codigoUnicoMx = :codigoLab";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("codigoLab", codigoLab);
        return  (DaTomaMx)q.uniqueResult();
    }

    public Catalogo_Dx getDxsById(Integer idDx) throws Exception {
        String query = "from Catalogo_Dx where idDiagnostico = :idDx" ;
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idDx", idDx);
        return (Catalogo_Dx)q.uniqueResult();
    }

    public Catalogo_Estudio getEstudioById(Integer idEstudio) throws Exception {
        String query = "from Catalogo_Estudio where idEstudio= :idEstudio" ;
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idEstudio", idEstudio);
        return (Catalogo_Estudio)q.uniqueResult();
    }

    /**
     * actualizar solicitud de diagnostico o rutina
     * @param solicitud objeto a actualizar
     */
    public void updateSolicitudDx(DaSolicitudDx solicitud) {
        Session session = sessionFactory.getCurrentSession();
        session.update(solicitud);
    }

    public Integer bajaSolicitudDx(String userName, String idSolicitud, String causa) {
        // Retrieve session from Hibernate
        Session s = sessionFactory.openSession();
        Transaction tx = s.beginTransaction();
        int updateEntities = 0,updateEntities2=0,updateEntities3=0,updateEntities4=0;
        try {

            String hqlBajaResExam = "update DetalleResultado o set pasivo=true, fechahAnulacion = current_date, usuarioAnulacion.username = :usuario, razonAnulacion = :causa " +
                    "where examen.idOrdenExamen in (select idOrdenExamen from OrdenExamen where solicitudDx.idSolicitudDx = :idSolicitud)";
            updateEntities3 = s.createQuery(hqlBajaResExam)
                    .setString("usuario", userName)
                    .setString("causa",causa)
                    .setString("idSolicitud", idSolicitud).executeUpdate();

            String hqlBajaExam = "update OrdenExamen ex set anulado=true, fechaAnulacion = current_date, usuarioAnulacion.username = :usuario, causaAnulacion = :causa where solicitudDx.idSolicitudDx = :idSolicitud and anulado = false ";
            updateEntities = s.createQuery(hqlBajaExam)
                    .setString("usuario", userName)
                    .setString("causa",causa)
                    .setString("idSolicitud", idSolicitud)
                    .executeUpdate();

            String hqlBajaResDx = "update DetalleResultadoFinal ex set pasivo=true, fechahAnulacion = current_date, usuarioAnulacion.username = :usuario, razonAnulacion = :causa where solicitudDx.idSolicitudDx = :idSolicitud and pasivo = false ";
            updateEntities4 = s.createQuery(hqlBajaResDx)
                    .setString("usuario", userName)
                    .setString("causa",causa)
                    .setString("idSolicitud", idSolicitud)
                    .executeUpdate();

            String hqlBajaDx = "update DaSolicitudDx ex set anulado=true, fechaAnulacion = current_date, aprobada = false, usuarioAprobacion = null, fechaAprobacion = null, usuarioAnulacion.username = :usuario, causaAnulacion = :causa where idSolicitudDx = :idSolicitud";
            updateEntities2 = s.createQuery(hqlBajaDx)
                    .setString("usuario", userName)
                    .setString("causa",causa)
                    .setString("idSolicitud", idSolicitud)
                    .executeUpdate();

            tx.commit();
        }catch (Exception ex){
            tx.rollback();
            ex.printStackTrace();
            throw ex;
        }finally {
            s.close();
        }
        return updateEntities+updateEntities2+updateEntities3+updateEntities4;
    }

    public Integer bajaSolicitudEstudio(String userName, String idSolicitud, String causa) {
        // Retrieve session from Hibernate
        Session s = sessionFactory.openSession();
        Transaction tx = s.beginTransaction();
        int updateEntities = 0,updateEntities2=0,updateEntities3=0,updateEntities4=0;
        try {

            String hqlBajaResExam = "update DetalleResultado o set pasivo=true, fechahAnulacion = current_date, usuarioAnulacion.username = :usuario, razonAnulacion = :causa " +
                    "where examen.idOrdenExamen in (select idOrdenExamen from OrdenExamen where solicitudEstudio.idSolicitudEstudio = :idSolicitud)";
            updateEntities3 = s.createQuery(hqlBajaResExam)
                    .setString("usuario", userName)
                    .setString("causa",causa)
                    .setString("idSolicitud", idSolicitud)
                    .executeUpdate();

            String hqlBajaExam = "update OrdenExamen ex set anulado=true, fechaAnulacion = current_date, usuarioAnulacion.username = :usuario, causaAnulacion = :causa where solicitudEstudio.idSolicitudEstudio = :idSolicitud";
            updateEntities2 = s.createQuery(hqlBajaExam)
                    .setString("usuario", userName)
                    .setString("causa",causa)
                    .setString("idSolicitud", idSolicitud)
                    .executeUpdate();

            String hqlBajaResEst = "update DetalleResultadoFinal ex set pasivo=true, fechahAnulacion = current_date, usuarioAnulacion.username = :usuario, razonAnulacion = :causa where solicitudEstudio.idSolicitudEstudio = :idSolicitud";
            updateEntities4 = s.createQuery(hqlBajaResEst)
                    .setString("usuario", userName)
                    .setString("causa",causa)
                    .setString("idSolicitud", idSolicitud)
                    .executeUpdate();

            String hqlBajaEst = "update DaSolicitudEstudio ex set anulado=true, fechaAnulacion = current_date, aprobada = false, usuarioAprobacion = null, fechaAprobacion = null, usuarioAnulacion.username = :usuario, causaAnulacion = :causa where idSolicitudEstudio = :idSolicitud";
            updateEntities2 = s.createQuery(hqlBajaEst)
                    .setString("usuario", userName)
                    .setString("causa",causa)
                    .setString("idSolicitud", idSolicitud)
                    .executeUpdate();

            tx.commit();
        }catch (Exception ex){
            tx.rollback();
            throw ex;
        }finally {
            s.close();
        }
        return updateEntities+updateEntities2+updateEntities3+updateEntities4;
    }

/****************************************************************
 * MUESTRAS DE ESTUDIOS
******************************************************************/
    public List<DaSolicitudEstudio> getSolicitudesEstudioByIdTomaMx(String idTomaMx){
        String query = "from DaSolicitudEstudio where anulado = false and idTomaMx.idTomaMx = :idTomaMx ORDER BY fechaHSolicitud";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idTomaMx",idTomaMx);
        return q.list();
    }

    public boolean tieneEstudiosByIdTomaMx(String idTomaMx){
        String query = "select est.idSolicitudEstudio from DaSolicitudEstudio est where est.anulado = false and est.idTomaMx.idTomaMx = :idTomaMx ORDER BY est.fechaHSolicitud";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idTomaMx",idTomaMx);
        return q.list().size()>0;
    }
    /**
     *Retorna una lista de estudios segun tipoMx y tipo Notificacion
     * @param codTipoMx código del tipo de Mx
     * @param codTipoNoti código del tipo Notificacion
     *
     */
    @SuppressWarnings("unchecked")
    public List<Estudio_TipoMx_TipoNoti> getEstudiosByTipoMxTipoNoti(String codTipoMx, String codTipoNoti, String idTomaMx) {
        String query = "select est from Estudio_TipoMx_TipoNoti est " +
                "where est.tipoMx_tipoNotificacion.tipoMx.idTipoMx = :codTipoMx " +
                "and est.tipoMx_tipoNotificacion.tipoNotificacion.codigo = :codTipoNoti " ;

        if (idTomaMx!= null){
            query += " and est.estudio.idEstudio in ( select tipoEstudio.idEstudio from DaSolicitudEstudio se where se.idTomaMx.idTomaMx = :idTomaMx )";
        }
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("codTipoMx", codTipoMx);
        q.setString("codTipoNoti", codTipoNoti);
        if (idTomaMx!= null){
            q.setString("idTomaMx", idTomaMx);
        }

        return q.list();
    }

    public DaSolicitudEstudio getSolicitudesEstudioByMxEst(String idTomaMx, Integer idEstudio){
        String query = "from DaSolicitudEstudio where anulado = false and idTomaMx.idTomaMx = :idTomaMx " +
                "and tipoEstudio.idEstudio= :idEstudio ORDER BY fechaHSolicitud";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idTomaMx",idTomaMx);
        q.setParameter("idEstudio",idEstudio);
        return (DaSolicitudEstudio)q.uniqueResult();
    }

    public List<Catalogo_Estudio> getEstudiossByTipoNoti(String tipoNoti) throws Exception {
        String query = "select dx from Estudio_TipoMx_TipoNoti dxrel inner join dxrel.estudio dx " +
                "where dxrel.tipoMx_tipoNotificacion.tipoNotificacion.codigo = :tipoNoti" ;
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("tipoNoti", tipoNoti);
        return q.list();
    }

    public DaSolicitudEstudio getSolicitudEstByIdSolicitud(String idSolicitud){
        String query = "from DaSolicitudEstudio where idSolicitudEstudio = :idSolicitud";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idSolicitud",idSolicitud);
        return (DaSolicitudEstudio)q.uniqueResult();
    }

    /**
     * actualizar solicitud de estudio
     * @param solicitud objeto a actualizar
     */
    public void updateSolicitudEstudio(DaSolicitudEstudio solicitud) {
        Session session = sessionFactory.getCurrentSession();
        session.update(solicitud);
    }

    @SuppressWarnings("unchecked")
    public List<DaSolicitudDx> getSoliDxAprobByIdToma(String idTomaMx){
        String query = "from DaSolicitudDx where idTomaMx.idTomaMx = :idTomaMx and anulado = false and  aprobada = true ORDER BY fechaHSolicitud";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idTomaMx",idTomaMx);
        return q.list();
    }

    @SuppressWarnings("unchecked")
    public List<DaSolicitudEstudio> getSoliEstudioAprobByIdTomaMx_Area(String idTomaMx){
        String query = "from DaSolicitudEstudio where idTomaMx.idTomaMx = :idTomaMx and anulado = false and aprobada = true ORDER BY fechaHSolicitud";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idTomaMx",idTomaMx);
        return q.list();
    }

    @SuppressWarnings("unchecked")
    public List<DaSolicitudEstudio> getSoliEAprobByIdTomaMxOrderCodigo(String idTomaMx){
        String query = "from DaSolicitudEstudio where idTomaMx.idTomaMx = :idTomaMx and anulado = false and aprobada = true ORDER BY  idTomaMx.codigoUnicoMx";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idTomaMx",idTomaMx);
        return q.list();
    }

    @SuppressWarnings("unchecked")
    public List<DaSolicitudEstudio> getSoliEAprobByCodigo(String codigoUnico){
        String query = "from DaSolicitudEstudio where idTomaMx.codigoUnicoMx = :codigoUnico and anulado = false and aprobada = true ORDER BY  idTomaMx.codigoUnicoMx";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("codigoUnico",codigoUnico + "%");
        return q.list();
    }

    @SuppressWarnings("unchecked")
     public DaSolicitudEstudio getSoliEstByCodigo(String codigoUnico){
        String query = "from DaSolicitudEstudio where anulado = false and idTomaMx.codigoUnicoMx = :codigoUnico ORDER BY  idTomaMx.codigoUnicoMx";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("codigoUnico",codigoUnico);
        return (DaSolicitudEstudio) q.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public DaSolicitudDx getSoliDxByCodigo(String codigoUnico, String userName){
        String query = "select sdx from DaSolicitudDx sdx, AutoridadLaboratorio al " +
                "where al.pasivo = false and sdx.anulado = false and sdx.labProcesa.codigo = al.laboratorio.codigo and al.user.username = :userName and sdx.idTomaMx.codigoUnicoMx = :codigoUnico ORDER BY  sdx.idTomaMx.codigoUnicoMx";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("codigoUnico",codigoUnico);
        q.setParameter("userName",userName);
        return (DaSolicitudDx) q.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<DaSolicitudDx> getSolicitudesDxCodigo(String codigo, String userName){
        String query = " select sdx from DaSolicitudDx sdx, AutoridadLaboratorio al " +
                "where al.pasivo = false and sdx.anulado = false and sdx.labProcesa.codigo = al.laboratorio.codigo and al.user.username =:userName and sdx.idTomaMx.codigoUnicoMx = :codigo ORDER BY sdx.fechaHSolicitud";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("codigo",codigo);
        q.setParameter("userName",userName);
        return q.list();
    }

    @SuppressWarnings("unchecked")
    public List<DaSolicitudDx> getSoliDxAprobByTomaAndUser(String idToma, String userName){
        String query = " select sdx from DaSolicitudDx sdx, AutoridadLaboratorio al " +
                "where al.pasivo = false and sdx.anulado = false and sdx.labProcesa.codigo = al.laboratorio.codigo and al.user.username =:userName and sdx.idTomaMx.idTomaMx = :idToma and sdx.aprobada = true ORDER BY sdx.fechaHSolicitud";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idToma",idToma);
        q.setParameter("userName",userName);
        return q.list();
    }

    @SuppressWarnings("unchecked")
    public List<DaSolicitudDx> getSoliDxAprobByToma_User_Area(String idToma, String userName, int idArea){
        String query = " select sdx from DaSolicitudDx sdx, AutoridadLaboratorio al " +
                "where al.pasivo = false and sdx.anulado = false and sdx.aprobada = true and " +
                "(sdx.labProcesa.codigo = al.laboratorio.codigo or sdx.idSolicitudDx in (select oe.solicitudDx.idSolicitudDx from OrdenExamen oe where oe.solicitudDx.idSolicitudDx = sdx.idSolicitudDx and oe.labProcesa.codigo = al.laboratorio.codigo))" +
                "and al.user.username =:userName and sdx.idTomaMx.idTomaMx = :idToma and sdx.codDx.area.idArea = :idArea " +
                "ORDER BY sdx.fechaHSolicitud";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idToma",idToma);
        q.setParameter("userName",userName);
        q.setParameter("idArea", idArea);
        return q.list();
    }

    @SuppressWarnings("unchecked")
    public List<DatosSolicitud> getSolicitudesAprobByToma_User_Area(String idToma, String userName, int idArea){
        String query = " select sdx.idSolicitudEstudio as idSolicitud, sdx.usuarioAprobacion.completeName as usuarioAprobacion from DaSolicitudEstudio sdx, AutoridadLaboratorio al " +
                "where al.pasivo = false and sdx.anulado = false and sdx.aprobada = true and " +
                "sdx.idSolicitudEstudio in (select oe.solicitudEstudio.idSolicitudEstudio from OrdenExamen oe where oe.solicitudEstudio.idSolicitudEstudio = sdx.idSolicitudEstudio and oe.labProcesa.codigo = al.laboratorio.codigo) " +
                "and al.user.username =:userName and sdx.idTomaMx.idTomaMx = :idToma and sdx.tipoEstudio.area.idArea = :idArea " +
                "ORDER BY sdx.fechaHSolicitud";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idToma",idToma);
        q.setParameter("userName",userName);
        q.setParameter("idArea", idArea);
        q.setResultTransformer(Transformers.aliasToBean(DatosSolicitud.class));
        List<DatosSolicitud> datos = q.list();
        query = " select sdx.idSolicitudDx as idSolicitud, sdx.usuarioAprobacion.completeName as usuarioAprobacion from DaSolicitudDx sdx, AutoridadLaboratorio al " +
                "where al.pasivo = false and sdx.anulado = false and sdx.aprobada = true and " +
                "(sdx.labProcesa.codigo = al.laboratorio.codigo " +
                "or sdx.idSolicitudDx in (select oe.solicitudDx.idSolicitudDx from OrdenExamen oe where oe.solicitudDx.idSolicitudDx = sdx.idSolicitudDx and oe.labProcesa.codigo = al.laboratorio.codigo)" +
                "or sdx.idTomaMx.idTomaMx = (select r.tomaMx.idTomaMx from RecepcionMx r where r.tomaMx.idTomaMx = :idToma and r.trasladoViajero = true ))" +
                "and al.user.username =:userName and sdx.idTomaMx.idTomaMx = :idToma and sdx.codDx.area.idArea = :idArea " +
                "ORDER BY sdx.fechaHSolicitud";
        q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idToma",idToma);
        q.setParameter("userName",userName);
        q.setParameter("idArea", idArea);
        q.setResultTransformer(Transformers.aliasToBean(DatosSolicitud.class));
        datos.addAll(q.list());
        return datos;
    }

    @SuppressWarnings("unchecked")
    public List<Area> getAreaSolicitudesAprobByTomaAndUser(String idToma, String userName){
        String query = "select a from Area as a where a.idArea in (select sdx.codDx.area.idArea from DaSolicitudDx as sdx, AutoridadLaboratorio as al " +
                "where al.pasivo = false and sdx.anulado = false and " +
                "(sdx.labProcesa.codigo = al.laboratorio.codigo " +
                "or sdx.idSolicitudDx in (select oe.solicitudDx.idSolicitudDx from OrdenExamen oe where oe.solicitudDx.idSolicitudDx = sdx.idSolicitudDx and oe.labProcesa.codigo = al.laboratorio.codigo) " +
                " or sdx.idTomaMx.idTomaMx = (select r.tomaMx.idTomaMx from RecepcionMx r where r.tomaMx.idTomaMx = :idToma and r.trasladoViajero = true ))" +
                " and al.user.username =:userName and sdx.idTomaMx.idTomaMx = :idToma and sdx.aprobada = true " +
                "group by sdx.codDx.area.idArea)";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idToma",idToma);
        q.setParameter("userName",userName);
        List<Area> areas = q.list();
        query = "select a from Area as a where a.idArea in (select sdx.tipoEstudio.area from DaSolicitudEstudio sdx, AutoridadLaboratorio al " +
                "where al.pasivo = false and sdx.anulado = false and sdx.aprobada = true and " +
                "sdx.idSolicitudEstudio in (select oe.solicitudEstudio.idSolicitudEstudio from OrdenExamen oe where oe.solicitudEstudio.idSolicitudEstudio = sdx.idSolicitudEstudio and oe.labProcesa.codigo = al.laboratorio.codigo) " +
                "and al.user.username =:userName and sdx.idTomaMx.idTomaMx = :idToma " +
                "group by sdx.tipoEstudio.area.idArea)";
        q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idToma",idToma);
        q.setParameter("userName",userName);
        areas.addAll(q.list());
        return  areas;
    }

    /**
     * Obtiene las areas a las que pertenecen los dx solicitidos en una muetra, según la autoridad del usuario indicado
     * @param idToma toma a obtener areas de dx
     * @param userName usuario que tiene la autoridad
     * @return
     */
    public List<Area> getAreaSolicitudDxByTomaAndUser(String idToma, String userName){
        String query = "select a from Area as a where a.idArea in (select sdx.codDx.area.idArea from DaSolicitudDx as sdx, AutoridadLaboratorio as al " +
                "where al.pasivo = false and sdx.anulado = false and " +
                "(sdx.labProcesa.codigo = al.laboratorio.codigo or sdx.idSolicitudDx in (select oe.solicitudDx.idSolicitudDx from OrdenExamen oe where oe.solicitudDx.idSolicitudDx = sdx.idSolicitudDx and oe.labProcesa.codigo = al.laboratorio.codigo))" +
                " and al.user.username =:userName and sdx.idTomaMx.idTomaMx = :idToma and sdx.aprobada = false " +
                "group by sdx.codDx.area.idArea)";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idToma",idToma);
        q.setParameter("userName",userName);
        return q.list();
    }

    public DaSolicitudDx getSolicitudDxByIdSolicitudUser(String idSolicitud, String userName){
        String query = " select sdx from DaSolicitudDx sdx, AutoridadLaboratorio al " +
                "where al.pasivo = false and sdx.anulado = false and sdx.labProcesa.codigo = al.laboratorio.codigo and al.user.username =:userName and sdx.idSolicitudDx = :idSolicitud ORDER BY sdx.fechaHSolicitud";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idSolicitud",idSolicitud);
        q.setParameter("userName",userName);
        return (DaSolicitudDx)q.uniqueResult();
    }


    /************************************************************/
    /***************TOMA MX VIP**********************************/
    /************************************************************/
    @SuppressWarnings("unchecked")
    public List<TipoMx_TipoNotificacion> getTipoMxByTipoNoti(String codigo){
        //Retrieve session Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM TipoMx_TipoNotificacion tmx where tmx.tipoNotificacion = :codigo and tmx.pasivo= false");
        query.setString("codigo", codigo);
        //retrieve all
        return query.list();

    }

    /**
     * Retorna tipoMx
     * @param id
     */
    public TipoMx getTipoMxById(String id){
        String query = "from TipoMx where idTipoMx = :id";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("id", id);
        return (TipoMx)q.uniqueResult();
    }

    /**
     * Agrega solicitud rutina
     */
    public void addSolicitudDx(DaSolicitudDx orden) {
        Session session = sessionFactory.getCurrentSession();
        session.save(orden);
    }

    /**
     * Agrega solicitud estudio
     */
    public void addSolicitudEstudio(DaSolicitudEstudio orden) {
        Session session = sessionFactory.getCurrentSession();
        session.save(orden);
    }

    public Integer deleteSolicitudesDxByTomaMx(String idTomaMx) {
        // Retrieve session from Hibernate
        Session s = sessionFactory.openSession();
        Transaction tx = s.beginTransaction();

        String hqlDelete = "delete DaSolicitudDx dx where dx.idTomaMx.idTomaMx = :idTomaMx";
        int deletedEntities = s.createQuery( hqlDelete )
                .setString("idTomaMx", idTomaMx)
                .executeUpdate();
        tx.commit();
        s.close();
        return deletedEntities;
    }

    public void addEnvioOrden(DaEnvioMx dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.save(dto);
            }
            else
                throw new Exception("Objeto Envio Orden es NULL");
        }catch (Exception ex){
            ex.printStackTrace();
            throw ex;
        }
    }

    public void deleteEnvioOrden(DaEnvioMx dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.delete(dto);
            }
            else
                throw new Exception("Objeto Envio Orden es NULL");
        }catch (Exception ex){
            ex.printStackTrace();
            throw ex;
        }
    }
    /**
     * Retorna rutina
     * @param id
     */
    public Catalogo_Dx getDxById(String id){
        String query = "from Catalogo_Dx where idDiagnostico = :id";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("id", id);
        return (Catalogo_Dx)q.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<DaTomaMx> getTomaMxByIdNoti(String idNotificacion){
        //Retrieve session Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM DaTomaMx tmx where tmx.idNotificacion = :idNotificacion");
        query.setString("idNotificacion", idNotificacion);
        //retrieve all
        return query.list();

    }

    @SuppressWarnings("unchecked")
    public List<DaTomaMx> getTomaMxActivaByIdNoti(String idNotificacion){
        //Retrieve session Hibernate
        Session session = sessionFactory.getCurrentSession();
        //Create a hibernate query (HQL)
        Query query = session.createQuery("FROM DaTomaMx tmx where tmx.idNotificacion = :idNotificacion and tmx.anulada = false order by fechaHTomaMx asc");
        query.setString("idNotificacion", idNotificacion);
        //retrieve all
        return query.list();
    }

    public List<DaSolicitudDx> getSolicitudesDxPrioridadByIdToma(String idTomaMx){
        String query = "select sdx from DaSolicitudDx as sdx inner join sdx.codDx dx where sdx.anulado = false and sdx.idTomaMx.idTomaMx = :idTomaMx ORDER BY dx.prioridad asc, sdx.fechaHSolicitud asc ";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idTomaMx",idTomaMx);
        return q.list();
    }

    public List<DaSolicitudDx> getSoliDxPrioridadByTomaAndLab(String idTomaMx, String lab){
        String query = "select sdx from DaSolicitudDx as sdx inner join sdx.codDx dx where sdx.anulado = false and sdx.idTomaMx.idTomaMx = :idTomaMx and " +
                "(sdx.labProcesa.codigo =:lab " +
                "or sdx.idSolicitudDx in (select oe.solicitudDx.idSolicitudDx from OrdenExamen oe where oe.solicitudDx.idSolicitudDx = sdx.idSolicitudDx and oe.labProcesa.codigo = :lab ) " +
                "or sdx.idTomaMx.idTomaMx = (select r.tomaMx.idTomaMx from RecepcionMx r where r.tomaMx.idTomaMx = :idTomaMx and r.trasladoViajero = true ) " +
                ") ORDER BY dx.prioridad asc, sdx.fechaHSolicitud asc ";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idTomaMx",idTomaMx);
        q.setParameter("lab",lab);
        return q.list();
    }

    public DaSolicitudDx getMaxSoliByToma(String tomaMx) {
        Session session = sessionFactory.getCurrentSession();
        String query = "select sol from DaSolicitudDx as sol  inner join sol.idTomaMx as t where sol.anulado = false and t.idTomaMx= :tomaMx and sol.fechaHSolicitud= (SELECT MAX(tsdx.fechaHSolicitud) " +
                "FROM DaSolicitudDx as tsdx where tsdx.anulado = false  and tsdx.idTomaMx.idTomaMx = t.idTomaMx)";
        Query q = session.createQuery(query);
        q.setParameter("tomaMx", tomaMx);
        return (DaSolicitudDx)q.uniqueResult();
    }


    /**
     * Se toma las solicitudes dx cuya área no se encuentra en la tabla de traslados para esa mx
     * @param idTomaMx
     * @return
     */
    public List<DaSolicitudDx> getSolicitudesDxSinTrasladoByIdToma(String idTomaMx){
        String query = "select sdx from DaSolicitudDx sdx inner join sdx.idTomaMx t inner join sdx.codDx dx, RecepcionMx r " +
                "where sdx.anulado = false and r.tomaMx.idTomaMx = t.idTomaMx and t.idTomaMx = :idTomaMx " +
                "and dx.area.idArea not in ( " +
                "           select rl.area.idArea from RecepcionMxLab rl where rl.recepcionMx.idRecepcion = r.idRecepcion) " +
                "ORDER BY dx.prioridad asc";

        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idTomaMx",idTomaMx);
        return q.list();
    }


    public List<DaSolicitudDx> getSolicitudesDxByIdTomaArea(String idTomaMx, int idArea, String username){
        String query = "select sdx from DaSolicitudDx sdx, AutoridadLaboratorio al where al.pasivo = false and sdx.anulado = false and sdx.idTomaMx.idTomaMx = :idTomaMx " +
                "and sdx.labProcesa.codigo = al.laboratorio.codigo and sdx.codDx.area.idArea = :idArea and al.user.username = :username " +
                "ORDER BY sdx.fechaHSolicitud";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idTomaMx",idTomaMx);
        q.setParameter("idArea",idArea);
        q.setParameter("username",username);
        return q.list();
    }

    /**
     * Método que obtiena las solicitudes de rutina por control de calidad para una muestra determinada, y que ya tenga resultado aprobado
     * @param codigoMx
     * @return
     */
    public List<DaSolicitudDx> getSolicitudesDxQCAprobByToma(String codigoMx){
        String query = " select sdx from DaSolicitudDx sdx " +
                "where sdx.idTomaMx.codigoUnicoMx = :codigoMx and sdx.anulado = false and sdx.aprobada = true and sdx.controlCalidad = true ORDER BY sdx.fechaHSolicitud";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("codigoMx",codigoMx);
        return q.list();
    }

    /*public String estaEmbarazada(String strIdNotificacion){
        String embarazo = "No";
        Session session = sessionFactory.getCurrentSession();
        //IRAG
        //String query = "select irag from DaIrag as irag, DaCondicionesPreviasIrag cIrag where irag.idNotificacion.idNotificacion = cIrag.idNotificacion.idNotificacion.idNotificacion" +
        //        " and irag.idNotificacion.idNotificacion = :idNotificacion" +
        //        " and cIrag.codCondicion.codigo = :codCondicion";
        String query = "from DaIrag where idNotificacion.idNotificacion = :idNotificacion and condiciones like :codCondicion";
        Query q = session.createQuery(query);
        q.setParameter("idNotificacion", strIdNotificacion);
        q.setParameter("codCondicion","%"+"CONDPRE|EMB"+"%");//código para condición embarazo

        //SINDROMES FEBRILES
        String query2 = "from DaSindFebril where idNotificacion.idNotificacion = :idNotificacion" +
                " and embarazo.codigo = :codigoEmb";
        Query q2 = session.createQuery(query2);
        q2.setParameter("idNotificacion", strIdNotificacion);
        q2.setParameter("codigoEmb","RESP|S"); //respuesta afirmativa

        DaIrag iragNoti= (DaIrag)q.uniqueResult();
        DaSindFebril sinFebNoti= (DaSindFebril)q2.uniqueResult();
        if(iragNoti!=null)
            embarazo="Si";
        else if(sinFebNoti!=null)
            embarazo="Si";

        return embarazo;
    }*/

    /**
     *
     * Método que obtiene las solicitudes dx de una muestra, que tenga examenes a procesar en un lab determinado
     * @param idTomaMx toma a filtrar
     * @param codigoLab laboratorio a filtrar
     * @return List<DaSolicitudDx>
     */
    public List<DaSolicitudDx> getSolicitudesDxTrasladoExtByIdToma(String idTomaMx, String codigoLab){
        String query = "select distinct sdx from OrdenExamen oe inner join oe.solicitudDx sdx inner join sdx.idTomaMx mx " +
                "where sdx.anulado = false and mx.idTomaMx = :idTomaMx " +
                "and oe.labProcesa.codigo = :codigoLab ORDER BY sdx.fechaHSolicitud";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idTomaMx",idTomaMx);
        q.setParameter("codigoLab",codigoLab);
        return q.list();
    }

    public DaSolicitudDx getSolicitudDxByMxDxNoCC(String idTomaMx,  Integer idDiagnostico){
        String query = "from DaSolicitudDx where idTomaMx.idTomaMx = :idTomaMx and anulado = false and controlCalidad = false  " +
                "and codDx.idDiagnostico = :idDiagnostico ORDER BY fechaHSolicitud";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idTomaMx",idTomaMx);
        q.setParameter("idDiagnostico",idDiagnostico);
        return (DaSolicitudDx)q.uniqueResult();
    }

    public String getIdSolicitudDxByMxDxNoCC(String idTomaMx,  Integer idDiagnostico){
        String query = "select idSolicitudDx from DaSolicitudDx where idTomaMx.idTomaMx = :idTomaMx and anulado = false and controlCalidad = false  " +
                "and codDx.idDiagnostico = :idDiagnostico ORDER BY fechaHSolicitud";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idTomaMx",idTomaMx);
        q.setParameter("idDiagnostico",idDiagnostico);
        return (String)q.uniqueResult();
    }

    /**
     * Obtiene los dx iniciales solicitados en la toma de mx en una fecha de toma especifica
     * @param id del estudio a buscar
     * @return Catalogo_Estudio
     */
    public List<DaSolicitudDx> getSoliDxByIdMxFechaToma(String id, Date fechaToma){
        String query = "select sdx from DaSolicitudDx sdx inner join sdx.idTomaMx mx " +
                "where sdx.anulado = false and sdx.inicial = true and mx.idTomaMx  = :id and mx.fechaHTomaMx = :fechaToma ORDER BY sdx.idTomaMx.idTomaMx";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("id", id);
        q.setParameter("fechaToma", fechaToma);
        return q.list();
    }

    /**
     * Obtiene las mx tomadas para una notificación en fecha de toma especifica
     * @param id del estudio a buscar
     * @return Catalogo_Estudio
     */
    public List<DaTomaMx> getTomaMxByIdNotiAndFechaToma(String id, Date fechaToma){
        String query = "select mx from DaTomaMx mx " +
                "where mx.idNotificacion.idNotificacion  = :id and mx.fechaHTomaMx = :fechaToma";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("id", id);
        q.setParameter("fechaToma", fechaToma);
        return q.list();
    }

    public List<DaSolicitudDx> getSolicitudesDxByIdNoti(String idNotificacion, String codigoLab, boolean nivelCentral){
        String query = "select distinct sdx from DaSolicitudDx sdx inner join sdx.idTomaMx mx " +
                "where sdx.anulado = false and mx.idNotificacion.idNotificacion = :idNotificacion ";
        if (!nivelCentral)
            query += "and (sdx.labProcesa.codigo = :codigoLab" +
                    " or sdx.idSolicitudDx in (select oe.solicitudDx.idSolicitudDx " +
                    "                   from OrdenExamen oe where oe.solicitudDx.idSolicitudDx = sdx.idSolicitudDx and oe.labProcesa.codigo = :codigoLab )) ";
        query +=  "ORDER BY sdx.fechaHSolicitud ";

        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idNotificacion", idNotificacion);
        if (!nivelCentral)
            q.setParameter("codigoLab",codigoLab);
        return q.list();
    }

    public List<DaSolicitudEstudio> getSolicitudesEstudioByIdNoti(String idNotificacion){
        String query = "select distinct sde from DaSolicitudEstudio sde inner join sde.idTomaMx mx " +
                "where sde.anulado = false and mx.idNotificacion.idNotificacion = :idNotificacion ORDER BY sde.fechaHSolicitud ";

        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idNotificacion", idNotificacion);
        return q.list();
    }

    /**
     * Retorna rutina
     * @param idDiagnosticos
     */
    public List<Solicitud> getDxs(String idDiagnosticos){
        String query = "select m.idDiagnostico as idSolicitud, m.nombre as nombre, 'R' as tipo from Catalogo_Dx m where idDiagnostico in ("+idDiagnosticos+")";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setResultTransformer(Transformers.aliasToBean(Solicitud.class));
        return q.list();
    }

    public List<Solicitud> getEstudios(String ids) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("select m.idEstudio as idSolicitud, m.nombre as nombre, 'E' as tipo"
                + " from Catalogo_Estudio m where m.idEstudio in ("+ids+")");
        query.setResultTransformer(Transformers.aliasToBean(Solicitud.class));
        List<Solicitud> results = query.list();
        return  results;
    }

    /**
     *Retorna una lista de Catalogo_Dx segun tipoMx y tipo Notificacion y las autoridades del usuario
     * @param codMx tipo de Mx
     * @param tipoNoti tipo Notificacion
     *
     */
    @SuppressWarnings("unchecked")
    public List<Catalogo_Dx> getDxByTipoMxTipoNoti(String codMx, String tipoNoti, String userName) {
        String query = "select distinct dx.diagnostico from Dx_TipoMx_TipoNoti dx " +
                "where dx.tipoMx_tipoNotificacion.tipoMx.idTipoMx = :codMx " +
                "and dx.tipoMx_tipoNotificacion.tipoNotificacion.codigo = :tipoNoti and dx.diagnostico.pasivo = false";
        if (userName!=null) {
            query +=  " and dx.diagnostico.area.idArea in (select a.idArea from AutoridadArea as aa inner join aa.area as a where aa.pasivo = false and aa.user.username = :userName)";
        }
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("codMx", codMx);
        q.setString("tipoNoti", tipoNoti);
        if (userName!=null) {
            q.setString("userName", userName);
        }
        return q.list();
    }

    /**
     *Retorna una lista de Catalogo_Estudio segun tipoMx y tipo Notificacion
     * @param codTipoMx código del tipo de Mx
     * @param codTipoNoti código del tipo Notificacion
     *
     */
    @SuppressWarnings("unchecked")
    public List<Catalogo_Estudio> getEstudiosByTipoMxTipoNoti(String codTipoMx, String codTipoNoti) {
        String query = "select distinct est.estudio from Estudio_TipoMx_TipoNoti est " +
                "where est.tipoMx_tipoNotificacion.tipoMx.idTipoMx = :codTipoMx " +
                "and est.tipoMx_tipoNotificacion.tipoNotificacion.codigo = :codTipoNoti " +
                "and est.pasivo = false and est.estudio.pasivo = false " +
                "and est.tipoMx_tipoNotificacion.pasivo = false " +
                "and est.tipoMx_tipoNotificacion.tipoMx.pasivo = false " +
                "and est.tipoMx_tipoNotificacion.tipoNotificacion.pasivo = false" ;

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("codTipoMx", codTipoMx);
        q.setString("codTipoNoti", codTipoNoti);

        return q.list();
    }

    /**
     * M?todo para generar un string alfanum?rico de 8 caracteres, que se usar? como c?digo ?nico de muestra
     * @return String codigoUnicoMx
     */
    public String generarCodigoUnicoMx(){
        DaTomaMx validaC;
        //Se genera el c?digo
        String codigoUnicoMx = StringUtil.getCadenaAlfanumAleatoria(8);
        //Se consulta BD para ver si existe toma de Mx que tenga mismo c?digo
        validaC = this.getTomaMxByCodUnicoMx(codigoUnicoMx);
        //si existe, de manera recursiva se solicita un nuevo c?digo
        if (validaC!=null){
            codigoUnicoMx = generarCodigoUnicoMx();
        }
        //si no existe se retorna el ?ltimo c?digo generado
        return codigoUnicoMx;
    }

    public boolean existeTomaMx(String idNotificacion, String fechaToma, String dxs) throws Exception{
        int totalEncontrados = 0;
        boolean respuesta = false;
        String[] dxArray = dxs.split(",");
        Date fecha1 = DateUtil.StringToDate(fechaToma, "dd/MM/yyyy");
        List<DaTomaMx> muestras = this.getTomaMxActivaByIdNoti(idNotificacion);
        for(DaTomaMx muestra : muestras){
            List<DaSolicitudDx> solicitudDxList = this.getSoliDxByIdMxFechaToma(muestra.getIdTomaMx(), fecha1);
            for(String dx : dxArray) {
                for (DaSolicitudDx solicitudDx : solicitudDxList) {
                    if (solicitudDx.getCodDx().getIdDiagnostico().equals(Integer.valueOf(dx))) {
                        totalEncontrados++;
                        break;
                    }
                }
            }
            if (totalEncontrados == dxArray.length && totalEncontrados == solicitudDxList.size()) {
                respuesta = true;
                break;
            }
            totalEncontrados = 0;
        }
        return respuesta;
    }

    @SuppressWarnings("unchecked")
    public List<Area> getAreaSoliDxAprobByTomaAndLabProcesa(String idToma, String codigoLab){
        String query = "select a from Area as a where a.idArea in (select sdx.codDx.area.idArea from DaSolicitudDx as sdx " +
                "where sdx.anulado = false " +
                "and sdx.labProcesa.codigo = :codigoLab " +
                " and sdx.idTomaMx.idTomaMx = :idToma and sdx.aprobada = true " +
                "group by sdx.codDx.area.idArea)";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idToma",idToma);
        q.setParameter("codigoLab", codigoLab);
        return q.list();
    }

    public boolean muestraTieneDxProcesadosEnLab(String idToma, String codigoLab){
        String query = "select count(sdx.idSolicitudDx) from DaSolicitudDx as sdx "+
        "where sdx.anulado = false " +
                "and sdx.labProcesa.codigo = :codigoLab " +
                " and sdx.idTomaMx.idTomaMx = :idToma and sdx.aprobada = true ";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("codigoLab", codigoLab);
        q.setParameter("idToma", idToma);
        Long total = (Long)q.uniqueResult();
        return total.intValue()>0;
    }

    public ResultadoVigilancia getDatosMx(String codigomx){
        Session session = sessionFactory.getCurrentSession();
        Query queryMx = null;
        queryMx = session.createQuery(" select cast(p.personaId as string) as codigoExpUnico, p.identificacion as identificacion, p.primerNombre as primerNombre, p.segundoNombre as segundoNombre, p.primerApellido as primerApellido, p.segundoApellido as segundoApellido, p.fechaNacimiento as fechaNacimiento, p.sexo.codigo as sexo, " +
                " p.direccionResidencia as direccionResidencia, p.telefonoResidencia as telefonoResidencia, p.telefonoMovil as telefonoMovil, coalesce((select co.nombre from  Comunidades co where co.codigo=p.comunidadResidencia.codigo), null) as comunidadResidencia, " +
                " noti.idNotificacion as idNotificacion, noti.codTipoNotificacion.codigo as codigoTipoNoti, noti.codExpediente as expediente, noti.semanasEmbarazo as semanasEmbarazo, noti.fechaInicioSintomas as fechaInicioSintomas, coalesce((select r.valor from Respuesta r where r.codigo = noti.urgente.codigo), null) as urgente,  coalesce((select r.valor from Respuesta r where r.codigo = noti.embarazada.codigo), null) as embarazada, " +
                " coalesce((select ea.codigo from EntidadesAdtvas ea where ea.codigo = noti.codSilaisAtencion.codigo ), null) as codigoSilaisNoti, coalesce((select ea.nombre from EntidadesAdtvas ea where ea.codigo = noti.codSilaisAtencion.codigo ), null) as nombreSilaisNoti, " +
                " coalesce((select u.codigo from Unidades u where u.codigo = noti.codUnidadAtencion.codigo), null) as codigoUnidadNoti, coalesce((select u.nombre from Unidades u where u.codigo = noti.codUnidadAtencion.codigo), null) as nombreUnidadNoti, " +
                " coalesce((select u.codigoNacional from Divisionpolitica u where u.codigoNacional = noti.codUnidadAtencion.municipio.codigoNacional), null) as codigoMuniNoti, coalesce((select u.nombre from Divisionpolitica u where u.codigoNacional = noti.codUnidadAtencion.municipio.codigoNacional), null) as nombreMuniNoti, " +
                " coalesce((select u.codigoNacional from Divisionpolitica u where u.codigoNacional = noti.municipioResidencia.codigoNacional), null) as codigoMuniResid, coalesce((select u.nombre from Divisionpolitica u where u.codigoNacional = noti.municipioResidencia.codigoNacional), null) as nombreMuniResid, " +
                " coalesce((select ea.codigo from EntidadesAdtvas ea where ea.codigo = noti.municipioResidencia.dependenciaSilais.codigo ), null) as codigoSilaisResid, coalesce((select ea.nombre from EntidadesAdtvas ea where ea.codigo = noti.municipioResidencia.dependenciaSilais.codigo ), null) as nombreSilaisResid, " +
                " coalesce((select ea.codigo from EntidadesAdtvas ea where ea.codigo = mx.codSilaisAtencion.codigo ), null) as codigoSilaisMx, coalesce((select ea.nombre from EntidadesAdtvas ea where ea.codigo = mx.codSilaisAtencion.codigo ), null) as nombreSilaisMx, " +
                " coalesce((select u.codigo from Unidades u where u.codigo = mx.codUnidadAtencion.codigo), null) as codigoUnidadMx, coalesce((select u.nombre from Unidades u where u.codigo = mx.codUnidadAtencion.codigo), null) as nombreUnidadMx, " +
                " coalesce((select u.codigoNacional from Divisionpolitica u where u.codigoNacional = mx.codUnidadAtencion.municipio.codigoNacional), null) as codigoMuniMx, coalesce((select u.nombre from Divisionpolitica u where u.codigoNacional = mx.codUnidadAtencion.municipio.codigoNacional), null) as nombreMuniMx, " +
                " mx.idTomaMx as idTomaMx, mx.fechaHTomaMx as fechaTomaMx, mx.codigoLab as codigoMx, mx.codigoUnicoMx as codUnicoMx, mx.codTipoMx.idTipoMx as idTipoMx, mx.codTipoMx.nombre as nombreTipoMx, noti.codigoPacienteVIH as codigoVIH, mx.codigoValidacion as codigoValidacion  " +
                " from DaTomaMx mx inner join mx.idNotificacion noti inner join noti.persona p  " +
                " where noti.pasivo = false and mx.anulada = false and mx.codigoLab = :codigomx or mx.codigoUnicoMx = :codigomx ");
        queryMx.setParameter("codigomx", codigomx);
        queryMx.setResultTransformer(Transformers.aliasToBean(ResultadoVigilancia.class));
        return (ResultadoVigilancia)queryMx.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<DatosSolicitud> getSolicitudesAprobByToma_Lab_Area(String idToma, String codigoLab, int idArea){
        String query = " select sdx.idSolicitudEstudio as idSolicitud, sdx.usuarioAprobacion.completeName as usuarioAprobacion, sdx.tipoEstudio.nombre as nombre from DaSolicitudEstudio sdx  " +
                "where sdx.anulado = false and sdx.aprobada = true and " +
                "sdx.idSolicitudEstudio in (select oe.solicitudEstudio.idSolicitudEstudio from OrdenExamen oe where oe.solicitudEstudio.idSolicitudEstudio = sdx.idSolicitudEstudio and oe.labProcesa.codigo = :codigoLab) " +
                "and sdx.idTomaMx.idTomaMx = :idToma and sdx.tipoEstudio.area.idArea = :idArea " +
                "ORDER BY sdx.fechaHSolicitud";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idToma",idToma);
        q.setParameter("codigoLab",codigoLab);
        q.setParameter("idArea", idArea);
        q.setResultTransformer(Transformers.aliasToBean(DatosSolicitud.class));
        List<DatosSolicitud> datos = q.list();
        query = " select sdx.idSolicitudDx as idSolicitud, sdx.usuarioAprobacion.completeName as usuarioAprobacion, sdx.codDx.nombre as nombre from DaSolicitudDx sdx " +
                "where sdx.anulado = false and sdx.aprobada = true and " +
                "(sdx.labProcesa.codigo = :codigoLab or sdx.idSolicitudDx in (select oe.solicitudDx.idSolicitudDx from OrdenExamen oe where oe.solicitudDx.idSolicitudDx = sdx.idSolicitudDx and oe.labProcesa.codigo = :codigoLab))" +
                "and sdx.idTomaMx.idTomaMx = :idToma and sdx.codDx.area.idArea = :idArea " +
                "ORDER BY sdx.fechaHSolicitud";
        q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idToma",idToma);
        q.setParameter("codigoLab",codigoLab);
        q.setParameter("idArea", idArea);
        q.setResultTransformer(Transformers.aliasToBean(DatosSolicitud.class));
        datos.addAll(q.list());
        return datos;
    }

    public List<DatosSolicitud> getDatosSolicitudesPrioridadByIdToma(String idTomaMx){
        String query = "select sdx.idSolicitudDx as idSolicitud, sdx.codDx.nombre as nombre, sdx.aprobada as aprobada from DaSolicitudDx as sdx inner join sdx.codDx dx where sdx.anulado = false and sdx.idTomaMx.idTomaMx = :idTomaMx ORDER BY dx.prioridad asc, sdx.fechaHSolicitud asc ";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idTomaMx",idTomaMx);
        q.setResultTransformer(Transformers.aliasToBean(DatosSolicitud.class));
        return q.list();
    }

    /***
     * Validar si la muestra es para realizar dx molecular covid19 para viajeros
     * @param codigoMx
     * @return TRUE si la mx contiene el dx, FALSE en caso contrario
     */
    public boolean esMxViajeroCovid(String codigoMx){
        String query = "select count(dx.idSolicitudDx) from DaSolicitudDx dx inner join dx.idTomaMx mx where mx.codigoLab = :codigoMx " +
                "and dx.codDx.idDiagnostico = (select cast(p.valor as long) from Parametro p where p.nombre = 'DX_VIAJERO_COVID19')";

        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("codigoMx", codigoMx);
        q.uniqueResult();
        Long total = (Long)q.uniqueResult();
        return total.intValue()>0;
    }

    public List<String> getCodigosMxViajerosByIdNoti(String idNotificacion){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -4);
        String query = "select mx.codigoLab from DaSolicitudDx dx inner join dx.idTomaMx mx inner join mx.idNotificacion noti where noti.idNotificacion = :idNotificacion " +
                "and dx.codDx.idDiagnostico = (select cast(p.valor as long) from Parametro p where p.nombre = 'DX_VIAJERO_COVID19') " +
                "and mx.fechaRegistro > :fechaMinima";

        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idNotificacion", idNotificacion);
        q.setParameter("fechaMinima", calendar.getTime());
        return q.list();
    }
}
