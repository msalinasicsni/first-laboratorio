package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.muestra.*;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.AutoridadArea;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.AutoridadLaboratorio;
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
 * Created by FIRSTICT on 12/10/2014.
 * V 1.0
 */
@Service("recepcionMxService")
@Transactional
public class RecepcionMxService {

    private Logger logger = LoggerFactory.getLogger(RecepcionMxService.class);

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;

    public RecepcionMxService(){}

    /**
     * Agrega una Registro de Recepción de muestra
     *
     * @param dto Objeto a agregar
     * @throws Exception
     */
    public String addRecepcionMx(RecepcionMx dto) throws Exception {
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

    /**
     * Agrega una Registro de Recepción de muestra en laboratorio
     *
     * @param dto Objeto a agregar
     * @throws Exception
     */
    public void addRecepcionMxLab(RecepcionMxLab dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.save(dto);
            }
            else
                throw new Exception("Objeto Recepción Muestra Lab es NULL");
        }catch (Exception ex){
            logger.error("Error al agregar recepción de muestra Lab",ex);
            throw ex;
        }
    }

    /**
     * Actualiza una Registro de Recepción de muestra
     *
     * @param dto Objeto a actualizar
     * @throws Exception
     */
    public void updateRecepcionMx(RecepcionMx dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.update(dto);
            }
            else
                throw new Exception("Objeto Recepción Muestra es NULL");
        }catch (Exception ex){
            logger.error("Error al actualizar recepción de muestra",ex);
            throw ex;
        }
    }

    public RecepcionMx getRecepcionMx(String idRecepcion){
        String query = "from RecepcionMx as a where idRecepcion= :idRecepcion";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("idRecepcion", idRecepcion);
        return  (RecepcionMx)q.uniqueResult();
    }

    public RecepcionMx getRecepcionMxByCodUnicoMx(String codigoUnicoMx, String codLaboratorio){
        String query = "select a from RecepcionMx as a inner join a.tomaMx as t where (t.codigoUnicoMx= :codigoUnicoMx or t.codigoLab = :codigoUnicoMx) " +
                "and a.labRecepcion.codigo = :codLaboratorio";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("codigoUnicoMx", codigoUnicoMx);
        q.setString("codLaboratorio",codLaboratorio);
        return  (RecepcionMx)q.uniqueResult();
    }

    public RecepcionMx getMaxRecepcionMxByCodUnicoMx(String codigoUnicoMx) {
        Session session = sessionFactory.getCurrentSession();
        String query = "select  re from RecepcionMx as re  inner join re.tomaMx as t " +
                "where t.codigoUnicoMx= :codigoUnicoMx and re.fechaHoraRecepcion= (SELECT MAX(remax.fechaHoraRecepcion)" +
                "FROM RecepcionMx as remax where remax.tomaMx.codigoUnicoMx = :codigoUnicoMx)";
        Query q = session.createQuery(query);
        q.setParameter("codigoUnicoMx", codigoUnicoMx);
        return (RecepcionMx)q.uniqueResult();
    }

    public List<RecepcionMx> getRecepcionesByFiltro(FiltroMx filtro){
        Session session = sessionFactory.getCurrentSession();
        Soundex varSoundex = new Soundex();
        Criteria crit = session.createCriteria(RecepcionMx.class, "recepcion");
        crit.createAlias("recepcion.tomaMx","tomaMx");
        crit.createAlias("tomaMx.estadoMx","estado");
        //crit.createAlias("orden.idTomaMx", "tomaMx");
        crit.createAlias("tomaMx.idNotificacion", "notifi");
        //siempre se tomam las muestras que no estan anuladas
        crit.add( Restrictions.and(
                        Restrictions.eq("tomaMx.anulada", false))
        );//y las ordenes en estado según filtro
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
                            Restrictions.between("tomaMx.fechaHTomaMx", filtro.getFechaInicioTomaMx(),filtro.getFechaFinTomaMx()))
            );
        }
        //Se filtra por rango de fecha de recepción
        if (filtro.getFechaInicioRecep()!=null && filtro.getFechaFinRecep()!=null){
            crit.add( Restrictions.and(
                            Restrictions.between("recepcion.fechaHoraRecepcion", filtro.getFechaInicioRecep(),filtro.getFechaFinRecep()))
            );
        }
        // se filtra por tipo de muestra
        if (filtro.getCodTipoMx()!=null){
            crit.add( Restrictions.and(
                            Restrictions.eq("tomaMx.codTipoMx.idTipoMx", Integer.valueOf(filtro.getCodTipoMx())))
            );
        }
        //se filtra por area que procesa
        /*if (filtro.getIdAreaProcesa()!=null){
            crit.createAlias("orden.codExamen", "examen");
            crit.add( Restrictions.and(
                            Restrictions.eq("examen.area.idArea", Integer.valueOf(filtro.getIdAreaProcesa())))
            );
        }*/

        //Se filtra por rango de fecha de recepcion en laboratorio
        if (filtro.getFechaInicioRecepLab()!=null && filtro.getFechaFinRecepLab()!=null){
            crit.add( Restrictions.or(
                            Restrictions.between("recepcion.fechaHoraRecepcionLab", filtro.getFechaInicioRecepLab(),filtro.getFechaFinRecepLab()))
            );
        }

        if(filtro.getIncluirMxInadecuada()!=null && filtro.getIncluirMxInadecuada()){
            crit.add(Restrictions.or(Restrictions.isNull("recepcion.calidadMx.codigo")).add(Restrictions.or(Restrictions.ne("recepcion.calidadMx.codigo", "CALIDMX|IDC"))));
        }
        if(filtro.getCodigoUnicoMx()!=null){
            crit.add(Restrictions.or(
                            Restrictions.eq("tomaMx.codigoUnicoMx", filtro.getCodigoUnicoMx())).add(Restrictions.or(Restrictions.eq("tomaMx.codigoLab", filtro.getCodigoUnicoMx())))
            );
        }

        //se filtra por tipo de solicitud
        if(filtro.getCodTipoSolicitud()!=null){
            if(filtro.getCodTipoSolicitud().equals("Estudio")){
                crit.add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                        .createAlias("idTomaMx", "toma")
                        .setProjection(Property.forName("toma.idTomaMx"))));
            }else{
                crit.add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                        .createAlias("idTomaMx", "toma")
                        .add(Subqueries.propertyIn("labProcesa.codigo", DetachedCriteria.forClass(AutoridadLaboratorio.class)
                                .createAlias("laboratorio", "labautorizado")
                                .createAlias("user", "usuario")
                                .add(Restrictions.eq("pasivo",false)) //autoridad laboratorio activa
                                .add(Restrictions.and(Restrictions.eq("usuario.username",filtro.getNombreUsuario()))) //usuario
                                .setProjection(Property.forName("labautorizado.codigo"))))
                        .setProjection(Property.forName("toma.idTomaMx"))));
            }
        }

        //nombre solicitud
        if (filtro.getNombreSolicitud() != null) {
            if (filtro.getCodTipoSolicitud() != null) {
                if (filtro.getCodTipoSolicitud().equals("Estudio")) {
                    crit.add(Subqueries.propertyIn("solicitudtomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                            .createAlias("tipoEstudio", "estudio")
                            .add(Restrictions.ilike("estudio.nombre", "%" + filtro.getNombreSolicitud() + "%"))
                            .createAlias("idTomaMx", "toma")
                            .setProjection(Property.forName("toma.idTomaMx"))));
                } else {
                    crit.add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                            .createAlias("codDx", "dx")
                            .add(Restrictions.ilike("dx.nombre", "%" + filtro.getNombreSolicitud() + "%"))
                            .createAlias("idTomaMx", "toma")
                            .setProjection(Property.forName("toma.idTomaMx"))));
                }
            } else {

                Junction conditGroup = Restrictions.disjunction();
                conditGroup.add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                        .createAlias("tipoEstudio", "estudio")
                        .add(Restrictions.ilike("estudio.nombre", "%" + filtro.getNombreSolicitud() + "%"))
                        .createAlias("idTomaMx", "toma")
                        .setProjection(Property.forName("toma.idTomaMx"))))
                        .add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                                .createAlias("codDx", "dx")
                                .add(Restrictions.ilike("dx.nombre", "%" + filtro.getNombreSolicitud() + "%"))
                                .createAlias("idTomaMx", "toma")
                                .setProjection(Property.forName("toma.idTomaMx"))));

                crit.add(conditGroup);
            }
        }
        //se filtra que usuario tenga autorizado laboratorio al que se envio la muestra desde ALERTA
        /*if (filtro.getNombreUsuario()!=null) {
            crit.createAlias("tomaMx.envio","envioMx");
            crit.add(Subqueries.propertyIn("envioMx.laboratorioDestino.codigo", DetachedCriteria.forClass(AutoridadLaboratorio.class)
                    .createAlias("laboratorio", "labautorizado")
                    .createAlias("user", "usuario")
                    .add(Restrictions.eq("pasivo",false)) //autoridad laboratorio activa
                    .add(Restrictions.and(Restrictions.eq("usuario.username",filtro.getNombreUsuario()))) //usuario
                    .setProjection(Property.forName("labautorizado.codigo"))));

        }*/
            if(filtro.getCodEstado() != null){
                if (filtro.getCodEstado().equalsIgnoreCase("ESTDMX|EPLAB")){ //significa que es recepción en laboratorio
                    //Se filtra que el área a la que pertenece la solicitud este asociada al usuario autenticado
                    Junction conditGroup = Restrictions.disjunction();

                    conditGroup.add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                            .createAlias("tipoEstudio", "estudio")
                            .createAlias("estudio.area", "area")
                            .add(Subqueries.propertyIn("area.idArea", DetachedCriteria.forClass(AutoridadArea.class)
                                    .add(Restrictions.eq("pasivo", false)) //autoridad area activa
                                    .add(Restrictions.and(Restrictions.eq("user.username", filtro.getNombreUsuario()))) //usuario
                                    .setProjection(Property.forName("area.idArea"))))
                            .createAlias("idTomaMx", "toma")
                            .setProjection(Property.forName("idTomaMx.idTomaMx"))))
                            .add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                                    .createAlias("codDx", "dx")
                                    .createAlias("dx.area","area")
                                    .add(Subqueries.propertyIn("area.idArea", DetachedCriteria.forClass(AutoridadArea.class)
                                            .add(Restrictions.eq("pasivo", false)) //autoridad area activa
                                            .add(Restrictions.and(Restrictions.eq("user.username", filtro.getNombreUsuario()))) //usuario
                                            .setProjection(Property.forName("area.idArea"))))
                                    .createAlias("idTomaMx", "toma")
                                    .setProjection(Property.forName("toma.idTomaMx"))));

                    crit.add(conditGroup);
                }
            }


        //filtro que las rutinas pertenezcan al laboratorio del usuario que consulta
        crit.createAlias("recepcion.labRecepcion","labRecep");
        crit.add(Subqueries.propertyIn("labRecep.codigo", DetachedCriteria.forClass(AutoridadLaboratorio.class)
                        .createAlias("laboratorio", "labautorizado")
                        .createAlias("user", "usuario")
                        .add(Restrictions.eq("pasivo",false)) //autoridad laboratorio activa
                        .add(Restrictions.and(Restrictions.eq("usuario.username",filtro.getNombreUsuario()))) //usuario
                        .setProjection(Property.forName("labautorizado.codigo"))));

        //sólo la última recepción de cada muestra

        DetachedCriteria maxDateQuery = DetachedCriteria.forClass(RecepcionMx.class);
        maxDateQuery.createAlias("tomaMx","mx");
        maxDateQuery.add(Restrictions.eqProperty("mx.idTomaMx","tomaMx.idTomaMx"));
        maxDateQuery.setProjection(Projections.max("fechaHoraRecepcion"));
        crit.add(Property.forName("fechaHoraRecepcion").eq(maxDateQuery));

        //filtro sólo control calidad en el laboratio del usuario
        if (filtro.getControlCalidad()!=null) {
            if (filtro.getControlCalidad()){  //si hay filtro por control de calidad y es "Si", sólo incluir rutinas
                crit.add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                        .add(Restrictions.eq("controlCalidad", filtro.getControlCalidad()))
                        .createAlias("idTomaMx", "toma")
                        .add(Subqueries.propertyIn("labProcesa.codigo", DetachedCriteria.forClass(AutoridadLaboratorio.class)
                                .createAlias("laboratorio", "labautorizado")
                                .createAlias("user", "usuario")
                                .add(Restrictions.eq("pasivo",false)) //autoridad laboratorio activa
                                .add(Restrictions.and(Restrictions.eq("usuario.username",filtro.getNombreUsuario()))) //usuario
                                .setProjection(Property.forName("labautorizado.codigo"))))
                        .setProjection(Property.forName("toma.idTomaMx"))));
            }else { //si hay filtro por control de calidad y es "No", siempre incluir los estudios
                Junction conditGroup = Restrictions.disjunction();
                conditGroup.add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                        .add(Restrictions.eq("controlCalidad", filtro.getControlCalidad()))
                        .createAlias("idTomaMx", "toma")
                        .add(Subqueries.propertyIn("labProcesa.codigo", DetachedCriteria.forClass(AutoridadLaboratorio.class)
                                .createAlias("laboratorio", "labautorizado")
                                .createAlias("user", "usuario")
                                .add(Restrictions.eq("pasivo", false)) //autoridad laboratorio activa
                                .add(Restrictions.and(Restrictions.eq("usuario.username", filtro.getNombreUsuario()))) //usuario
                                .setProjection(Property.forName("labautorizado.codigo"))))
                        .setProjection(Property.forName("toma.idTomaMx"))))
                        .add(Restrictions.or(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                                .createAlias("idTomaMx", "idTomaMx")
                                .setProjection(Property.forName("idTomaMx.idTomaMx")))));
                crit.add(conditGroup);
            }
        }
        //filtro para sólo solicitudes aprobadas
        if (filtro.getSolicitudAprobada() != null) {
            Junction conditGroup = Restrictions.disjunction();
            conditGroup.add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                    .add(Restrictions.eq("aprobada", filtro.getSolicitudAprobada()))
                    .createAlias("idTomaMx", "toma")
                    .setProjection(Property.forName("toma.idTomaMx"))))
                    .add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                            .add(Restrictions.eq("aprobada", filtro.getSolicitudAprobada()))
                            //.add(Restrictions.eq("controlCalidad",false)) ¿¿¿¿¿¿¿¿¿?????????????
                            .createAlias("idTomaMx", "toma")
                            .setProjection(Property.forName("toma.idTomaMx"))));


            crit.add(conditGroup);

        }

        return crit.list();
    }

    public RecepcionMxLab getRecepcionMxLabByIdRecepGral(String idRecepcion){
        RecepcionMxLab resultado = null;
        String query = "select a from RecepcionMxLab as a inner join a.recepcionMx as t where t.idRecepcion= :idRecepcion order by a.fechaHoraRecepcion desc";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("idRecepcion", idRecepcion);
        List<RecepcionMxLab> recepcionMxLabList = q.list();
        if (recepcionMxLabList!=null & recepcionMxLabList.size()>0){
            resultado = recepcionMxLabList.get(0);
        }
        return  resultado;
    }

    /**
     * Método que genera
     * @param codigoLaboratorio
     * @return
     */
    public String obtenerCodigoLab(String codigoLaboratorio){
        String codigoLab=null;
        String query = "select concat(to_char((count(a.idRecepcion)+1)),concat('-',to_char(current_date,'YY'))) " +
                "from RecepcionMx as a where a.labRecepcion.codigo = :codLab and a.tipoRecepcionMx.codigo = 'TPRECPMX|VRT'";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("codLab", codigoLaboratorio);
        Object oNumero  = q.uniqueResult();
        if (oNumero!=null){
            codigoLab = oNumero.toString();
            codigoLab = codigoLaboratorio.concat("-").concat(codigoLab);
        }
        return codigoLab;
    }
}
