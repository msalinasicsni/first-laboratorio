package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.muestra.*;
import ni.gob.minsa.laboratorio.domain.persona.SisPersona;
import ni.gob.minsa.laboratorio.domain.resultados.DetalleResultado;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.AutoridadExamen;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.AutoridadLaboratorio;
import ni.gob.minsa.laboratorio.domain.solicitante.Solicitante;
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
            logger.error("Error al actualizar recepción de muestra",ex);
            throw ex;
        }
    }

    public OrdenExamen getOrdenExamenById(String idOrdenExamen){
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery("from OrdenExamen where idOrdenExamen =:idOrdenExamen");
        q.setParameter("idOrdenExamen",idOrdenExamen);
        return (OrdenExamen)q.uniqueResult();
    }

    /**
     * Método que obtiene las ordenes de exámenes generadas para la muestra, en las àreas a las que tiene autoridad el usuario que consulta y el lab que las procesa
     * @param idTomaMx
     * @param username
     * @return
     */
    public List<OrdenExamen> getOrdenesExamenByIdMxAndUser(String idTomaMx, String username){
        Session session = sessionFactory.getCurrentSession();
        List<OrdenExamen> ordenExamenList = new ArrayList<OrdenExamen>();
        /*Query q = session.createQuery("select oe from OrdenExamen as oe inner join oe.solicitudDx.idTomaMx as mx inner join oe.solicitudDx.codDx as dx, " +
                "AutoridadArea as aa where dx.area.idArea = aa.area.idArea and mx.idTomaMx =:idTomaMx and aa.user.username = :username");
        */
        Query q = session.createQuery("select oe from OrdenExamen as oe inner join oe.solicitudDx as sdx inner join sdx.idTomaMx as mx inner join oe.solicitudDx.codDx as dx, " +
                "AutoridadArea as aa, AutoridadLaboratorio  al where oe.labProcesa.codigo = al.laboratorio.codigo and  dx.area.idArea = aa.area.idArea " +
                "and mx.idTomaMx =:idTomaMx and aa.user.username = :username and al.user.username = :username and al.pasivo = false ");

        q.setParameter("idTomaMx",idTomaMx);
        q.setParameter("username",username);
        ordenExamenList = q.list();
        //se toman las que son de estudio
        Query q2 = session.createQuery("select oe from OrdenExamen as oe inner join oe.solicitudEstudio.idTomaMx as mx inner join oe.solicitudEstudio.tipoEstudio as es, " +
                "AutoridadArea as aa,AutoridadLaboratorio  al where al.pasivo = false and oe.labProcesa.codigo = al.laboratorio.codigo and es.area.idArea = aa.area.idArea " +
                "and mx.idTomaMx =:idTomaMx and aa.user.username = :username and al.user.username = :username ");
        q2.setParameter("idTomaMx",idTomaMx);
        q2.setParameter("username",username);
        ordenExamenList.addAll(q2.list());
        return ordenExamenList;
    }

    public List<OrdenExamen> getOrdenesExamenNoAnuladasByCodigoUnico(String codigoUnicoMx){
        Session session = sessionFactory.getCurrentSession();
        List<OrdenExamen> ordenExamenList = new ArrayList<OrdenExamen>();
        Query q = session.createQuery("select oe from OrdenExamen as oe inner join oe.solicitudDx.idTomaMx as mx where mx.codigoUnicoMx =:codigoUnicoMx and oe.anulado = false");
        q.setParameter("codigoUnicoMx", codigoUnicoMx);
        ordenExamenList = q.list();
        //se toman las que son de estudio
        Query q2 = session.createQuery("select oe from OrdenExamen as oe inner join oe.solicitudEstudio.idTomaMx as mx where mx.codigoUnicoMx =:codigoUnicoMx and oe.anulado = false ");
        q2.setParameter("codigoUnicoMx", codigoUnicoMx);
        ordenExamenList.addAll(q2.list());
        return ordenExamenList;
    }

      /**
     * Obtiene las ordenes de examen no anuladas para la muestra. Una toma no puede tener de dx y de estudios, es uno u otro.
     * @param idTomaMx id de la toma a consultar
     * @return List<OrdenExamen>
     */
    public List<OrdenExamen> getOrdenesExamenNoAnuladasByIdMx(String idTomaMx){
        Session session = sessionFactory.getCurrentSession();
        List<OrdenExamen> ordenExamenList = new ArrayList<OrdenExamen>();
        //se toman las que son de diagnóstico.
        Query q = session.createQuery("select oe from OrdenExamen as oe inner join oe.solicitudDx.idTomaMx as mx where mx.idTomaMx =:idTomaMx and oe.anulado = false ");
        q.setParameter("idTomaMx",idTomaMx);
        ordenExamenList = q.list();
        //se toman las que son de estudio
        Query q2 = session.createQuery("select oe from OrdenExamen as oe inner join oe.solicitudEstudio.idTomaMx as mx where mx.idTomaMx =:idTomaMx and oe.anulado = false ");
        q2.setParameter("idTomaMx",idTomaMx);
        ordenExamenList.addAll(q2.list());
        return ordenExamenList;
    }

    public List<OrdenExamen> getOrdExamenNoAnulByIdMxIdDxIdExamen(String idTomaMx, int idDx, int idExamen, String userName){
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery("select oe from OrdenExamen as oe inner join oe.solicitudDx as sdx inner join sdx.idTomaMx as mx, AutoridadLaboratorio as al " +
                "where al.pasivo = false and al.laboratorio.codigo = oe.labProcesa.codigo and  mx.idTomaMx =:idTomaMx " +
                "and sdx.codDx.idDiagnostico = :idDx and oe.codExamen.idExamen = :idExamen and al.user.username = :userName and oe.anulado = false order by oe.fechaHOrden");
        q.setParameter("idTomaMx",idTomaMx);
        q.setParameter("idDx",idDx);
        q.setParameter("idExamen",idExamen);
        q.setParameter("userName",userName);
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
        //siempre se toman las ordenes con dx no aprobado
        crit.add( Restrictions.and(
                       Restrictions.eq("solicitudDx.aprobada", false))
        );
        crit.add(Restrictions.eq("solicitudDx.anulado",false));
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
        //se filtra por código de muestra (único o lab)
        if(filtro.getCodigoUnicoMx()!=null){
            crit.add(Restrictions.or(
                            Restrictions.eq("tomaMx.codigoUnicoMx", filtro.getCodigoUnicoMx())).add(Restrictions.or(Restrictions.eq("tomaMx.codigoLab", filtro.getCodigoUnicoMx())))
            );
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
//Se filtra por rango de fecha de recepción
        if (filtro.getFechaInicioRecep()!=null && filtro.getFechaFinRecep()!=null){
            crit.add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(RecepcionMx.class)
                    .createAlias("tomaMx", "toma").add(Restrictions.between("fechaHoraRecepcion", filtro.getFechaInicioRecep(),filtro.getFechaFinRecep()))
                    .setProjection(Property.forName("toma.idTomaMx"))));

        }
        //se filtra por tipo de solicitud
        if(filtro.getCodTipoSolicitud()!=null){
            if(filtro.getCodTipoSolicitud().equals("Estudio")){
                crit.add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                        .add(Restrictions.eq("anulado",false))
                        .createAlias("idTomaMx", "toma")
                        .setProjection(Property.forName("toma.idTomaMx"))));
            }else{
                crit.add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                        .add(Restrictions.eq("anulado",false))
                        .createAlias("idTomaMx", "toma")
                        .setProjection(Property.forName("toma.idTomaMx"))));
            }

        }

        //nombre solicitud
        if (filtro.getNombreSolicitud() != null) {
            if (filtro.getCodTipoSolicitud() != null) {
                if (filtro.getCodTipoSolicitud().equals("Estudio")) {
                    crit.add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                            .add(Restrictions.eq("anulado",false))
                            .createAlias("tipoEstudio", "estudio")
                            .add(Restrictions.ilike("estudio.nombre", "%" + filtro.getNombreSolicitud() + "%"))
                            .createAlias("idTomaMx", "toma")
                            .setProjection(Property.forName("toma.idTomaMx"))));
                } else {
                    crit.add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                            .add(Restrictions.eq("anulado",false))
                            .createAlias("codDx", "dx")
                            .add(Restrictions.ilike("dx.nombre", "%" + filtro.getNombreSolicitud() + "%"))
                            .createAlias("idTomaMx", "toma")
                            .setProjection(Property.forName("toma.idTomaMx"))));
                }
            } else {

                Junction conditGroup = Restrictions.disjunction();
                conditGroup.add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                        .add(Restrictions.eq("anulado",false))
                        .createAlias("tipoEstudio", "estudio")
                        .add(Restrictions.ilike("estudio.nombre", "%" + filtro.getNombreSolicitud() + "%"))
                        .createAlias("idTomaMx", "toma")
                        .setProjection(Property.forName("toma.idTomaMx"))))
                        .add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                                .add(Restrictions.eq("anulado",false))
                                .createAlias("codDx", "dx")
                                .add(Restrictions.ilike("dx.nombre", "%" + filtro.getNombreSolicitud() + "%"))
                                .createAlias("idTomaMx", "toma")
                                .setProjection(Property.forName("toma.idTomaMx"))));

                crit.add(conditGroup);
            }

        }

        //filtro examen con resultado
        if(filtro.getResultado() != null){
            if (filtro.getResultado().equals("Si")){
                crit.add(Subqueries.propertyIn("idOrdenExamen", DetachedCriteria.forClass(DetalleResultado.class)
                        .createAlias("examen", "ex")
                        .setProjection(Property.forName("ex.idOrdenExamen"))));
            } else{
                crit.add(Subqueries.propertyNotIn("idOrdenExamen", DetachedCriteria.forClass(DetalleResultado.class)
                        .createAlias("examen", "ex")
                        .setProjection(Property.forName("ex.idOrdenExamen"))));
            }
        }
    //se filtra que el usuario tenga autoridad para ver el tipo de examen(Catalogo_Examen) que tiene la orden
        if(filtro.getNombreUsuario()!=null){
            crit.createAlias("ordenEx.codExamen","catalogoExamen");
            crit.add(Subqueries.propertyIn("catalogoExamen.idExamen", DetachedCriteria.forClass(AutoridadExamen.class)
                    .createAlias("examen", "examen")
                    .createAlias("autoridadArea","autoridadArea")
                    .add(Restrictions.eq("pasivo",false)) //autoridad examen activa
                    .add(Restrictions.and(Restrictions.eq("autoridadArea.pasivo",false)))  //autoridad area que pertenece examen activa
                    .add(Restrictions.and(Restrictions.eq("autoridadArea.user.username",filtro.getNombreUsuario()))) //usuario
                    .setProjection(Property.forName("examen.idExamen"))));

            crit.add(Subqueries.propertyIn("labProcesa.codigo", DetachedCriteria.forClass(AutoridadLaboratorio.class)
                    .createAlias("laboratorio", "labautorizado")
                    .createAlias("user", "usuario")
                    .add(Restrictions.eq("pasivo", false)) //autoridad laboratorio activa
                    .add(Restrictions.and(Restrictions.eq("usuario.username", filtro.getNombreUsuario()))) //usuario
                    .setProjection(Property.forName("labautorizado.codigo"))));

        }

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
        //siempre se toman las ordenes con estudio no aprobado
        crit.add( Restrictions.and(
                        Restrictions.eq("solicitudEstudio.aprobada", false))
        );
        crit.add(Restrictions.eq("solicitudEstudio.anulado",false));
        //y las ordenes en estado según filtro
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
        //se filtra por código de muestra(único o lab)
        if(filtro.getCodigoUnicoMx()!=null){
            crit.add(Restrictions.or(
                            Restrictions.eq("tomaMx.codigoUnicoMx", filtro.getCodigoUnicoMx())).add(Restrictions.or(Restrictions.eq("tomaMx.codigoLab", filtro.getCodigoUnicoMx())))
            );
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

        //se filtra por tipo de solicitud
        if(filtro.getCodTipoSolicitud()!=null){
            if(filtro.getCodTipoSolicitud().equals("Estudio")){
                crit.add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                        .add(Restrictions.eq("anulado",false))
                        .createAlias("idTomaMx", "toma")
                        .setProjection(Property.forName("toma.idTomaMx"))));
            }else{
                crit.add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                        .add(Restrictions.eq("anulado",false))
                        .createAlias("idTomaMx", "toma")
                        .setProjection(Property.forName("toma.idTomaMx"))));
            }

        }

        //nombre solicitud
        if (filtro.getNombreSolicitud() != null) {
            if (filtro.getCodTipoSolicitud() != null) {
                if (filtro.getCodTipoSolicitud().equals("Estudio")) {
                    crit.add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                            .add(Restrictions.eq("anulado",false))
                            .createAlias("tipoEstudio", "estudio")
                            .add(Restrictions.ilike("estudio.nombre", "%" + filtro.getNombreSolicitud() + "%"))
                            .createAlias("idTomaMx", "toma")
                            .setProjection(Property.forName("toma.idTomaMx"))));
                } else {
                    crit.add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                            .add(Restrictions.eq("anulado",false))
                            .createAlias("codDx", "dx")
                            .add(Restrictions.ilike("dx.nombre", "%" + filtro.getNombreSolicitud() + "%"))
                            .createAlias("idTomaMx", "tom")
                            .setProjection(Property.forName("tom.idTomaMx"))));
                }
            } else {

                Junction conditGroup = Restrictions.disjunction();
                conditGroup.add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                        .add(Restrictions.eq("anulado",false))
                        .createAlias("tipoEstudio", "estudio")
                        .add(Restrictions.ilike("estudio.nombre", "%" + filtro.getNombreSolicitud() + "%"))
                        .createAlias("idTomaMx", "toma")
                        .setProjection(Property.forName("toma.idTomaMx"))))
                        .add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                                .add(Restrictions.eq("anulado",false))
                                .createAlias("codDx", "dx")
                                .add(Restrictions.ilike("dx.nombre", "%" + filtro.getNombreSolicitud() + "%"))
                                .createAlias("idTomaMx", "toma")
                                .setProjection(Property.forName("toma.idTomaMx"))));

                crit.add(conditGroup);
            }

           }

        //filtro examen con resultado
        if(filtro.getResultado() != null){
            if (filtro.getResultado().equals("Si")){
                crit.add(Subqueries.propertyIn("idOrdenExamen", DetachedCriteria.forClass(DetalleResultado.class)
                        .createAlias("examen", "ex")
                        .setProjection(Property.forName("ex.idOrdenExamen"))));
            } else{
                crit.add(Subqueries.propertyNotIn("idOrdenExamen", DetachedCriteria.forClass(DetalleResultado.class)
                        .createAlias("examen", "ex")
                        .setProjection(Property.forName("ex.idOrdenExamen"))));
            }
        }

        //se filtra que el usuario tenga autoridad para ver el tipo de examen(Catalogo_Examen) que tiene la orden
        if(filtro.getNombreUsuario()!=null){
            crit.createAlias("ordenEx.codExamen","catalogoExamen");
            crit.add(Subqueries.propertyIn("catalogoExamen.idExamen", DetachedCriteria.forClass(AutoridadExamen.class)
                    .createAlias("examen", "examen")
                    .createAlias("autoridadArea","autoridadArea")
                    .add(Restrictions.eq("pasivo",false)) //autoridad examen activa
                    .add(Restrictions.and(Restrictions.eq("autoridadArea.pasivo",false)))  //autoridad area que pertenece examen activa
                    .add(Restrictions.and(Restrictions.eq("autoridadArea.user.username",filtro.getNombreUsuario()))) //usuario
                    .setProjection(Property.forName("examen.idExamen"))));

            crit.add(Subqueries.propertyIn("labProcesa.codigo", DetachedCriteria.forClass(AutoridadLaboratorio.class)
                    .createAlias("laboratorio", "labautorizado")
                    .createAlias("user", "usuario")
                    .add(Restrictions.eq("pasivo", false)) //autoridad laboratorio activa
                    .add(Restrictions.and(Restrictions.eq("usuario.username", filtro.getNombreUsuario()))) //usuario
                    .setProjection(Property.forName("labautorizado.codigo"))));
        }


        return crit.list();
    }

    public List<OrdenExamen> getOrdenesExamenEstudioResultadoByFiltro(FiltroMx filtro){
        Session session = sessionFactory.getCurrentSession();
        Soundex varSoundex = new Soundex();
        Criteria crit = session.createCriteria(OrdenExamen.class, "ordenEx");
        crit.createAlias("ordenEx.solicitudEstudio","solicitudEstudio");
        crit.createAlias("solicitudEstudio.idTomaMx","tomaMx");

        //siempre se tomam las muestras que no estan anuladas
        crit.add( Restrictions.and(
                        Restrictions.eq("tomaMx.anulada", false))
        );
        //la solicitud que no este anulada
        crit.add(Restrictions.eq("solicitudEstudio.anulado",false));
        //siempre se tomam las ordenes que no estan anuladas
        crit.add( Restrictions.and(
                        Restrictions.eq("ordenEx.anulado", false))
        );


        //filtro examen con resultado
        if(filtro.getResultado() != null){
            if (filtro.getResultado().equals("Si")){
                crit.add(Subqueries.propertyIn("idOrdenExamen", DetachedCriteria.forClass(DetalleResultado.class)
                        .createAlias("examen", "ex")
                        .setProjection(Property.forName("ex.idOrdenExamen"))));
            } else{
                crit.add(Subqueries.propertyNotIn("idOrdenExamen", DetachedCriteria.forClass(DetalleResultado.class)
                        .createAlias("examen", "ex")
                        .setProjection(Property.forName("ex.idOrdenExamen"))));
            }
        }


        //filtro por Codigo Unico
        if (filtro.getCodigoUnicoMx()!=null) {
            crit.add(Restrictions.and(
                    Restrictions.eq("tomaMx.codigoUnicoMx", filtro.getCodigoUnicoMx())));
        }

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

    public List<OrdenExamen> getOrdenesExamenNoAnuladasByIdSolicitud(String idSolicitud){
        Session session = sessionFactory.getCurrentSession();
        List<OrdenExamen> ordenExamenList = new ArrayList<OrdenExamen>();
        //se toman las que son de diagnóstico.
        Query q = session.createQuery("select oe from OrdenExamen as oe inner join oe.solicitudDx as sdx where sdx.idSolicitudDx =:idSolicitud and oe.anulado = false ");
        q.setParameter("idSolicitud",idSolicitud);
        ordenExamenList = q.list();
        //se toman las que son de estudio
        Query q2 = session.createQuery("select oe from OrdenExamen as oe inner join oe.solicitudEstudio as se where se.idSolicitudEstudio =:idSolicitud and oe.anulado = false ");
        q2.setParameter("idSolicitud",idSolicitud);
        ordenExamenList.addAll(q2.list());
        return ordenExamenList;
    }

    /**
     * Obtiene las ordenes de examen no anuladas para la muestra y que el usuario tenga autorizasión sobre el examen. Una toma no puede tener de dx y de estudios, es uno u otro.
     * @param idTomaMx id de la toma a consultar
     * @return List<OrdenExamen>
     */
    public List<OrdenExamen> getOrdenesExamenNoAnuladasByIdMxAndUser(String idTomaMx, String username){
        Session session = sessionFactory.getCurrentSession();
        List<OrdenExamen> ordenExamenList = new ArrayList<OrdenExamen>();
        //se toman las que son de diagnóstico.
        Query q = session.createQuery("select oe from OrdenExamen as oe inner join oe.solicitudDx as sdx inner join sdx.idTomaMx as mx inner join oe.solicitudDx.codDx as dx, " +
                "AutoridadArea as aa, AutoridadLaboratorio  al where oe.labProcesa.codigo = al.laboratorio.codigo and  dx.area.idArea = aa.area.idArea " +
                "and mx.idTomaMx =:idTomaMx and aa.user.username = :username and al.user.username = :username and oe.anulado = false and aa.pasivo = false  and al.pasivo = false ");

        q.setParameter("idTomaMx",idTomaMx);
        q.setParameter("username",username);
        ordenExamenList = q.list();
        //se toman las que son de estudio
        Query q2 = session.createQuery("select oe from OrdenExamen as oe inner join oe.solicitudEstudio.idTomaMx as mx inner join oe.solicitudEstudio.tipoEstudio as es, " +
                "AutoridadArea as aa,AutoridadLaboratorio  al where oe.labProcesa.codigo = al.laboratorio.codigo and es.area.idArea = aa.area.idArea " +
                "and mx.idTomaMx =:idTomaMx and aa.user.username = :username and al.user.username = :username and oe.anulado = false and aa.pasivo = false  and al.pasivo = false ");
        q2.setParameter("idTomaMx",idTomaMx);
        q2.setParameter("username",username);
        ordenExamenList.addAll(q2.list());
        return ordenExamenList;
    }

    public OrdenExamen getOrdExamenNoAnulByCodLabMxIdDxIdExamen(String codigoLab, int idDx, int idExamen, String userName){
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery("select oe from OrdenExamen as oe inner join oe.solicitudDx as sdx inner join sdx.idTomaMx as mx, AutoridadLaboratorio as al " +
                "where al.laboratorio.codigo = oe.labProcesa.codigo and  mx.codigoLab =:codigoLab " +
                "and sdx.codDx.idDiagnostico = :idDx and oe.codExamen.idExamen = :idExamen and al.user.username = :userName and oe.anulado = false and al.pasivo = false order by oe.fechaHOrden");
        q.setParameter("codigoLab",codigoLab);
        q.setParameter("idDx",idDx);
        q.setParameter("idExamen",idExamen);
        q.setParameter("userName",userName);
        return (OrdenExamen)q.uniqueResult();
    }

    public List<OrdenExamen> getOrdenesExamenByIdSolicitud(String idSolicitud){
        Session session = sessionFactory.getCurrentSession();
        List<OrdenExamen> ordenExamenList = new ArrayList<OrdenExamen>();
        //se toman las que son de diagnóstico.
        Query q = session.createQuery("select oe from OrdenExamen as oe inner join oe.solicitudDx as sdx where sdx.idSolicitudDx =:idSolicitud ");
        q.setParameter("idSolicitud",idSolicitud);
        ordenExamenList = q.list();
        //se toman las que son de estudio
        Query q2 = session.createQuery("select oe from OrdenExamen as oe inner join oe.solicitudEstudio as se where se.idSolicitudEstudio =:idSolicitud ");
        q2.setParameter("idSolicitud",idSolicitud);
        ordenExamenList.addAll(q2.list());
        return ordenExamenList;
    }

    public List<OrdenExamen> getOrdenesExamenNoAnuladasSinResulByIdSolicitud(String idSolicitud){
        Session session = sessionFactory.getCurrentSession();
        List<OrdenExamen> ordenExamenList = new ArrayList<OrdenExamen>();
        //se toman las que son de diagnóstico.
        Query q = session.createQuery("select oe from OrdenExamen as oe inner join oe.solicitudDx as sdx, DetalleResultado as a " +
                "where oe.idOrdenExamen = a.examen.idOrdenExamen and sdx.idSolicitudDx =:idSolicitud and oe.anulado = false and a.pasivo = false ");

        q.setParameter("idSolicitud",idSolicitud);
        ordenExamenList = q.list();
        //se toman las que son de estudio
        Query q2 = session.createQuery("select oe from OrdenExamen as oe inner join oe.solicitudEstudio as se, DetalleResultado as a " +
                "where oe.idOrdenExamen = a.examen.idOrdenExamen and se.idSolicitudEstudio =:idSolicitud and oe.anulado = false and a.pasivo = false ");
        q2.setParameter("idSolicitud",idSolicitud);
        ordenExamenList.addAll(q2.list());
        return ordenExamenList;
    }
}
