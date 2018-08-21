package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.examen.AreaDepartamento;
import ni.gob.minsa.laboratorio.domain.muestra.*;
import ni.gob.minsa.laboratorio.domain.concepto.Catalogo_Lista;
import ni.gob.minsa.laboratorio.domain.persona.SisPersona;
import ni.gob.minsa.laboratorio.domain.resultados.DetalleResultado;
import ni.gob.minsa.laboratorio.domain.resultados.DetalleResultadoFinal;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.AutoridadArea;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.AutoridadDepartamento;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.AutoridadDireccion;
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
 * Created by souyen-ics.
 */
@Service("resultadoFinalService")
@Transactional
public class ResultadoFinalService {

    private Logger logger = LoggerFactory.getLogger(ResultadoFinalService.class);

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;


    public ResultadoFinalService() {
    }

    @SuppressWarnings("unchecked")
    public List<DaSolicitudDx> getDxByFiltro(FiltroMx filtro){
        Session session = sessionFactory.getCurrentSession();
        Soundex varSoundex = new Soundex();
        Criteria crit = session.createCriteria(DaSolicitudDx.class, "diagnostico");
        crit.createAlias("diagnostico.idTomaMx","tomaMx");
        crit.createAlias("tomaMx.estadoMx","estado");
        crit.createAlias("tomaMx.idNotificacion", "noti");
        //siempre se tomam las muestras que no estan anuladas
        crit.add( Restrictions.and(
                        Restrictions.eq("tomaMx.anulada", false))
        );
        crit.add(Restrictions.eq("diagnostico.anulado", false));
        //y las ordenes en estado seg�n filtro
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
            //crit.createAlias("noti.persona", "person");
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
                conditGroup.add(Subqueries.propertyIn("noti.persona.personaId", DetachedCriteria.forClass(SisPersona.class,"person")
                        .add(Restrictions.or(Restrictions.ilike("person.primerNombre", "%" + partes[i] + "%"))
                                .add(Restrictions.or(Restrictions.ilike("person.primerApellido", "%" + partes[i] + "%"))
                                        .add(Restrictions.or(Restrictions.ilike("person.segundoNombre", "%" + partes[i] + "%"))
                                                .add(Restrictions.or(Restrictions.ilike("person.segundoApellido", "%" + partes[i] + "%"))
                                                        .add(Restrictions.or(Restrictions.ilike("person.sndNombre", "%" + partesSnd[i] + "%")))))))
                        .setProjection(Property.forName("personaId"))))
                        .add(Subqueries.propertyIn("noti.solicitante.idSolicitante", DetachedCriteria.forClass(Solicitante.class,"solicitante")
                                .add(Restrictions.ilike("solicitante.nombre", "%" + partes[i] + "%"))
                                .setProjection(Property.forName("idSolicitante"))));

                crit.add(conditGroup);
            }
        }
        //se filtra por SILAIS
        if (filtro.getCodSilais()!=null){
            crit.createAlias("noti.codSilaisAtencion","silais");
            crit.add( Restrictions.and(
                            Restrictions.eq("silais.codigo", Long.valueOf(filtro.getCodSilais())))
            );
        }
        //se filtra por unidad de salud
        if (filtro.getCodUnidadSalud()!=null){
            crit.createAlias("noti.codUnidadAtencion","unidadS");
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
        //Se filtra por rango de fecha de recepci�n
        if (filtro.getFechaInicioRecep()!=null && filtro.getFechaFinRecep()!=null){
            crit.add(Subqueries.propertyIn("idTomaMx.idTomaMx", DetachedCriteria.forClass(RecepcionMx.class)
                    .createAlias("tomaMx", "toma").add(Restrictions.between("fechaHoraRecepcion", filtro.getFechaInicioRecep(),filtro.getFechaFinRecep()))
                    .setProjection(Property.forName("toma.idTomaMx"))));

        }
        // se filtra por tipo de muestra
        if (filtro.getCodTipoMx()!=null){
            crit.add( Restrictions.and(
                            Restrictions.eq("tomaMx.codTipoMx.idTipoMx", Integer.valueOf(filtro.getCodTipoMx())))
            );
        }

        if(filtro.getIncluirMxInadecuada()!=null && filtro.getIncluirMxInadecuada()){

            crit.add(Subqueries.propertyIn("idTomaMx.idTomaMx", DetachedCriteria.forClass(RecepcionMx.class)
                    .createAlias("tomaMx", "toma").add(Restrictions.isNull("calidadMx.codigo"))
                    .add(Restrictions.or(Restrictions.ne("calidadMx.codigo", "CALIDMX|IDC")))
                    .setProjection(Property.forName("toma.idTomaMx"))));

        }
        //se filtra por c�digo de muestra(�nico o lab)
        if(filtro.getCodigoUnicoMx()!=null){
            crit.add(Restrictions.or(
                            Restrictions.eq("tomaMx.codigoUnicoMx", filtro.getCodigoUnicoMx())).add(Restrictions.or(Restrictions.eq("tomaMx.codigoLab", filtro.getCodigoUnicoMx())))
            );
        }

        // filtro examenes con resultado
        if (filtro.getSolicitudAprobada()==null) {
            //fecha de procesamiento de examenes (SOLO PARA BUSQUEDA DE RESULTADO FINAL)
            if (filtro.getFechaInicioProcesamiento()!=null && filtro.getFechaFinProcesamiento()!=null){
                crit.add(Subqueries.propertyIn("idSolicitudDx", DetachedCriteria.forClass(DetalleResultado.class)
                        .createAlias("examen", "examen").add(Restrictions.eq("examen.anulado", false)).add(Restrictions.eq("pasivo", false))
                        .add(Restrictions.between("fechahProcesa",filtro.getFechaInicioProcesamiento(), filtro.getFechaFinProcesamiento()))
                        .setProjection(Property.forName("examen.solicitudDx.idSolicitudDx"))));
            }else {
                crit.add(Subqueries.propertyIn("idSolicitudDx", DetachedCriteria.forClass(DetalleResultado.class)
                        .createAlias("examen", "examen").add(Restrictions.eq("examen.anulado", false)).add(Restrictions.eq("pasivo", false))
                        .setProjection(Property.forName("examen.solicitudDx.idSolicitudDx"))));
            }
        }else {
            //filtro por fecha de procesamiento de dx
            if (filtro.getFechaInicioProcesamiento()!=null && filtro.getFechaFinProcesamiento()!=null){
                crit.add(Subqueries.propertyIn("idSolicitudDx", DetachedCriteria.forClass(DetalleResultadoFinal.class)
                        .createAlias("solicitudDx", "dx").add(Restrictions.eq("pasivo",false))
                        .add(Restrictions.between("fechahRegistro", filtro.getFechaInicioProcesamiento(),filtro.getFechaFinProcesamiento()))
                        .setProjection(Property.forName("dx.idSolicitudDx"))));

            }
        }

        //se filtra por tipo de solicitud
        if(filtro.getCodTipoSolicitud()!=null){
            if(filtro.getCodTipoSolicitud().equals("Estudio")){
                crit.add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                        .add(Restrictions.eq("anulado", false))
                        .createAlias("idTomaMx", "toma")
                        .setProjection(Property.forName("toma.idTomaMx"))));
            }else{
                crit.add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                        .add(Restrictions.eq("anulado", false))
                        .createAlias("idTomaMx", "toma")
                        .setProjection(Property.forName("toma.idTomaMx"))));
            }

        }

        //nombre solicitud
        if (filtro.getNombreSolicitud() != null) {
            if (filtro.getCodTipoSolicitud() != null) {
                if (filtro.getCodTipoSolicitud().equals("Rutina")) {
                             crit.add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                             .add(Restrictions.eq("anulado", false))
                            .createAlias("codDx", "dx")
                            .add(Restrictions.ilike("dx.nombre", "%" + filtro.getNombreSolicitud() + "%"))
                            .createAlias("idTomaMx", "toma")
                            .setProjection(Property.forName("toma.idTomaMx"))));
                }
            } else {

                Junction conditGroup = Restrictions.disjunction();
                conditGroup.add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                        .add(Restrictions.eq("anulado", false))
                        .createAlias("tipoEstudio", "estudio")
                        .add(Restrictions.ilike("estudio.nombre", "%" + filtro.getNombreSolicitud() + "%"))
                        .createAlias("idTomaMx", "toma")
                        .setProjection(Property.forName("toma.idTomaMx"))))
                        .add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                                .add(Restrictions.eq("anulado", false))
                                .createAlias("codDx", "dx")
                                .add(Restrictions.ilike("dx.nombre", "%" + filtro.getNombreSolicitud() + "%"))
                                .createAlias("idTomaMx", "toma")
                                .setProjection(Property.forName("toma.idTomaMx"))));

                crit.add(conditGroup);
            }

        }

        //filtro dx con resultado activo
        if(filtro.getResultado() != null){
            if (filtro.getResultado().equals("Si")){
                crit.add(Subqueries.propertyIn("idSolicitudDx", DetachedCriteria.forClass(DetalleResultadoFinal.class)
                        .createAlias("solicitudDx", "dx").add(Restrictions.eq("pasivo",false))
                        .setProjection(Property.forName("dx.idSolicitudDx"))));
            } else{
                crit.add(Subqueries.propertyNotIn("idSolicitudDx", DetachedCriteria.forClass(DetalleResultadoFinal.class)
                        .createAlias("solicitudDx", "dx").add(Restrictions.eq("pasivo",false))
                        .setProjection(Property.forName("dx.idSolicitudDx"))));
            }
        }

        //se filtra que la solicitud este aprobada. (SOLO CUANDO ES CONSULTA DE RESULTADOS APROBADOS)
        if (filtro.getSolicitudAprobada()!=null && filtro.getSolicitudAprobada()){
            crit.add( Restrictions.and(
                            Restrictions.eq("diagnostico.aprobada", true))
            );
        }else{
            crit.add( Restrictions.and(
                            Restrictions.eq("diagnostico.aprobada", false))
            );
        }
        //Se filtra por rango de fecha de aprobacion
        if (filtro.getFechaInicioAprob()!=null && filtro.getFechaFinAprob()!=null){
            crit.add( Restrictions.and(
                            Restrictions.between("diagnostico.fechaAprobacion", filtro.getFechaInicioAprob(),filtro.getFechaFinAprob()))
            );
        }

        //se filtra que usuario tenga autorizado laboratorio en el que se proceso la solicitud(rutina)
        if (filtro.getNombreUsuario()!=null) {
            //se filtra que laboratorio que procesa solicitud o si es traslado lab que procesa examen este autorizado al usuario
            Junction conditGroup = Restrictions.disjunction();
            conditGroup.add(Subqueries.propertyIn("labProcesa.codigo", DetachedCriteria.forClass(AutoridadLaboratorio.class)
                    .createAlias("laboratorio", "labautorizado")
                    .createAlias("user", "usuario")
                    .add(Restrictions.eq("pasivo",false)) //autoridad laboratorio activa
                    .add(Restrictions.and(Restrictions.eq("usuario.username",filtro.getNombreUsuario()))) //usuario
                    .setProjection(Property.forName("labautorizado.codigo"))))
                    .add(Restrictions.or(Subqueries.propertyIn("diagnostico.idSolicitudDx", DetachedCriteria.forClass(OrdenExamen.class)
                            .add(Restrictions.eq("anulado", false))
                            .createAlias("solicitudDx", "dx")
                            .createAlias("labProcesa", "labProcesa")
                            .add(Subqueries.propertyIn("labProcesa.codigo", DetachedCriteria.forClass(AutoridadLaboratorio.class)
                                    .add(Restrictions.eq("pasivo", false)) //autoridad lab activa
                                    .createAlias("laboratorio", "labautorizadoex")
                                    .createAlias("user", "usuario")
                                    .add(Restrictions.and(Restrictions.eq("usuario.username",filtro.getNombreUsuario()))) //usuario
                                    .setProjection(Property.forName("labautorizadoex.codigo"))))
                            .setProjection(Property.forName("dx.idSolicitudDx")))));
            crit.add(conditGroup);

            //Se filtra que el �rea a la que pertenece la solicitud este asociada al usuario autenticado ya sea  analista, jefe departamento o director
            //nivel analista
            if (filtro.getNivelLaboratorio() == 1) {
                crit.add(Subqueries.propertyIn("diagnostico.idSolicitudDx", DetachedCriteria.forClass(DaSolicitudDx.class)
                        .add(Restrictions.eq("anulado", false))
                        .createAlias("codDx", "dx")
                        .createAlias("dx.area","area")
                        .add(Subqueries.propertyIn("area.idArea", DetachedCriteria.forClass(AutoridadArea.class)
                                .add(Restrictions.eq("pasivo", false)) //autoridad area activa
                                .add(Restrictions.and(Restrictions.eq("user.username", filtro.getNombreUsuario()))) //usuario
                                .setProjection(Property.forName("area.idArea"))))
                        //.createAlias("idTomaMx", "toma")
                        .setProjection(Property.forName("idSolicitudDx"))));
            }
            //nivel jefe departamento
            if (filtro.getNivelLaboratorio() == 2) {
                crit.add(Restrictions.and(Subqueries.propertyIn("diagnostico.idSolicitudDx", DetachedCriteria.forClass(DaSolicitudDx.class)
                        .add(Restrictions.eq("anulado", false))
                        .createAlias("codDx", "dx")
                        .createAlias("dx.area", "area")
                        .add(Subqueries.propertyIn("area.idArea", DetachedCriteria.forClass(AreaDepartamento.class)
                                        .createAlias("area","area")
                                        .createAlias("depDireccion", "depDir")
                                        .createAlias("depDir.departamento","departamento")
                                        .add(Restrictions.eq("pasivo", false)) //area departamento activa
                                        .add(Subqueries.propertyIn("departamento.idDepartamento", DetachedCriteria.forClass(AutoridadDepartamento.class)
                                                .add(Restrictions.eq("pasivo", false)) //autoridad area activa
                                                .add(Restrictions.and(Restrictions.eq("user.username", filtro.getNombreUsuario()))) //usuario
                                                .setProjection(Property.forName("departamento.idDepartamento"))))
                                        .setProjection(Property.forName("area.idArea"))
                        ))
                        .setProjection(Property.forName("idSolicitudDx")))));
            }
            //nivel director
            if (filtro.getNivelLaboratorio() == 3) {
                //Se filtra que el �rea a la que pertenece la solicitud este asociada al usuario autenticado
                crit.add(Restrictions.and(Subqueries.propertyIn("diagnostico.idSolicitudDx", DetachedCriteria.forClass(DaSolicitudDx.class)
                        .add(Restrictions.eq("anulado", false))
                        .createAlias("codDx", "dx")
                        .createAlias("dx.area","area")
                        .add(Subqueries.propertyIn("area.idArea", DetachedCriteria.forClass(AreaDepartamento.class)
                                        .createAlias("area","area")
                                        .createAlias("depDireccion", "depDir")
                                        .createAlias("depDir.direccionLab","dirLab")
                                        .createAlias("dirLab.direccion","direccion")
                                        .add(Restrictions.eq("pasivo", false)) //area departamento activa
                                        .add(Restrictions.eq("depDir.pasivo", false)) //departamento direccion activa
                                        .add(Restrictions.eq("dirLab.pasivo", false)) //direccion laboratorio activa
                                        .add(Subqueries.propertyIn("direccion.idDireccion", DetachedCriteria.forClass(AutoridadDireccion.class)
                                                .add(Restrictions.eq("pasivo", false)) //autoridad direccion activa
                                                .add(Restrictions.and(Restrictions.eq("user.username", filtro.getNombreUsuario()))) //usuario
                                                .setProjection(Property.forName("direccion.idDireccion"))))
                                        .setProjection(Property.forName("area.idArea"))
                        ))
                        .setProjection(Property.forName("idSolicitudDx")))));
            }
        }

        //ordenar por fecha
        crit.addOrder(Order.desc("fechaHSolicitud"));

        return crit.list();
    }

    @SuppressWarnings("unchecked")
    public List<DaSolicitudEstudio> getEstudioByFiltro(FiltroMx filtro){
        Session session = sessionFactory.getCurrentSession();
        Soundex varSoundex = new Soundex();
        Criteria crit = session.createCriteria(DaSolicitudEstudio.class, "estudio");
        crit.createAlias("estudio.idTomaMx","tomaMx");
        crit.createAlias("tomaMx.estadoMx","estado");
        crit.createAlias("tomaMx.idNotificacion", "noti");
        //siempre se tomam las muestras que no estan anuladas
        crit.add( Restrictions.and(
                        Restrictions.eq("tomaMx.anulada", false))
        );//y las ordenes en estado seg�n filtro
        if (filtro.getCodEstado()!=null) {
            crit.add(Restrictions.and(
                    Restrictions.eq("estado.codigo", filtro.getCodEstado()).ignoreCase()));
        }
        // se filtra por nombre y apellido persona
        if (filtro.getNombreApellido()!=null) {
            crit.createAlias("noti.persona", "person");
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
            crit.createAlias("noti.codSilaisAtencion","silais");
            crit.add( Restrictions.and(
                            Restrictions.eq("silais.codigo", Long.valueOf(filtro.getCodSilais())))
            );
        }
        //se filtra por unidad de salud
        if (filtro.getCodUnidadSalud()!=null){
            crit.createAlias("noti.codUnidadAtencion","unidadS");
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
        //Se filtra por rango de fecha de recepci�n
        if (filtro.getFechaInicioRecep()!=null && filtro.getFechaFinRecep()!=null){
            crit.add(Subqueries.propertyIn("idTomaMx.idTomaMx", DetachedCriteria.forClass(RecepcionMx.class)
                    .createAlias("tomaMx", "toma").add(Restrictions.between("fechaHoraRecepcion", filtro.getFechaInicioRecep(),filtro.getFechaFinRecep()))
                    .setProjection(Property.forName("toma.idTomaMx"))));

        }
        // se filtra por tipo de muestra
        if (filtro.getCodTipoMx()!=null){
            crit.add( Restrictions.and(
                            Restrictions.eq("tomaMx.codTipoMx.idTipoMx", Integer.valueOf(filtro.getCodTipoMx())))
            );
        }

        if(filtro.getIncluirMxInadecuada()!=null && filtro.getIncluirMxInadecuada()){

            crit.add(Subqueries.propertyIn("idTomaMx.idTomaMx", DetachedCriteria.forClass(RecepcionMx.class)
                    .createAlias("tomaMx", "toma").add(Restrictions.isNull("calidadMx.codigo"))
                    .add(Restrictions.or(Restrictions.ne("calidadMx.codigo", "CALIDMX|IDC")))
                    .setProjection(Property.forName("toma.idTomaMx"))));

        }
        //se filtra por c�digo de muestra(�nico o lab)
        if(filtro.getCodigoUnicoMx()!=null){
            crit.add(Restrictions.or(
                            Restrictions.eq("tomaMx.codigoUnicoMx", filtro.getCodigoUnicoMx())).add(Restrictions.or(Restrictions.eq("tomaMx.codigoLab", filtro.getCodigoUnicoMx())))
            );
        }
        //s�lo cuando se busca desde resultado final, este filtro es null
        if (filtro.getSolicitudAprobada()==null) {
            // examenes con resultado
            crit.add(Subqueries.propertyIn("idSolicitudEstudio", DetachedCriteria.forClass(DetalleResultado.class)
                    .createAlias("examen", "examen").add(Restrictions.eq("pasivo", false))
                    .setProjection(Property.forName("examen.solicitudEstudio.idSolicitudEstudio"))));
        }

        //se filtra por tipo de solicitud
        if(filtro.getCodTipoSolicitud()!=null){
            if(filtro.getCodTipoSolicitud().equals("Estudio")){
                crit.add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                        .add(Restrictions.eq("anulado", false))
                        .createAlias("idTomaMx", "toma")
                        .setProjection(Property.forName("toma.idTomaMx"))));
            }else{
                crit.add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                        .add(Restrictions.eq("anulado", false))
                        .createAlias("idTomaMx", "toma")
                        .setProjection(Property.forName("toma.idTomaMx"))));
            }

        }
         //nombre solicitud
        if (filtro.getNombreSolicitud() != null) {
            if (filtro.getCodTipoSolicitud() != null) {
                if (filtro.getCodTipoSolicitud().equals("Estudio")) {
                    crit.add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                            .add(Restrictions.eq("anulado", false))
                            .createAlias("tipoEstudio", "estudio")
                            .add(Restrictions.ilike("estudio.nombre", "%" + filtro.getNombreSolicitud() + "%"))
                            .createAlias("idTomaMx", "toma")
                            .setProjection(Property.forName("toma.idTomaMx"))));
                }
            } else {

                Junction conditGroup = Restrictions.disjunction();
                conditGroup.add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                        .add(Restrictions.eq("anulado", false))
                        .createAlias("tipoEstudio", "estudio")
                        .add(Restrictions.ilike("estudio.nombre", "%" + filtro.getNombreSolicitud() + "%"))
                        .createAlias("idTomaMx", "toma")
                        .setProjection(Property.forName("toma.idTomaMx"))))
                        .add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                                .add(Restrictions.eq("anulado", false))
                                .createAlias("codDx", "dx")
                                .add(Restrictions.ilike("dx.nombre", "%" + filtro.getNombreSolicitud() + "%"))
                                .createAlias("idTomaMx", "toma")
                                .setProjection(Property.forName("toma.idTomaMx"))));

                crit.add(conditGroup);
            }

        }
        //ordenar por fecha
        crit.addOrder(Order.desc("fechaHSolicitud"));

        //filtro estudio con resultado activo
        if(filtro.getResultado() != null){
            if (filtro.getResultado().equals("Si")){
                crit.add(Subqueries.propertyIn("idSolicitudEstudio", DetachedCriteria.forClass(DetalleResultadoFinal.class)
                        .createAlias("solicitudEstudio", "estudio").add(Restrictions.eq("pasivo",false))
                        .setProjection(Property.forName("estudio.idSolicitudEstudio"))));
            } else{
                crit.add(Subqueries.propertyNotIn("idSolicitudEstudio", DetachedCriteria.forClass(DetalleResultadoFinal.class)
                        .createAlias("solicitudEstudio", "estudio").add(Restrictions.eq("pasivo",false))
                        .setProjection(Property.forName("estudio.idSolicitudEstudio"))));
            }
        }

        //filtro por fecha de procesamiento
        if (filtro.getFechaInicioProcesamiento()!=null && filtro.getFechaFinProcesamiento()!=null){
            crit.add(Subqueries.propertyIn("idSolicitudEstudio", DetachedCriteria.forClass(DetalleResultadoFinal.class)
                    .createAlias("solicitudEstudio", "estudio").add(Restrictions.eq("pasivo",false))
                    .add(Restrictions.between("fechahRegistro", filtro.getFechaInicioProcesamiento(),filtro.getFechaFinProcesamiento()))
                    .setProjection(Property.forName("estudio.idSolicitudEstudio"))));

        }

        //se filtra que la solicitud de estudio ya este aprobada
        if (filtro.getSolicitudAprobada()!=null && filtro.getSolicitudAprobada()){
            crit.add( Restrictions.and(
                            Restrictions.eq("estudio.aprobada", true))
            );
        }else {
            crit.add( Restrictions.and(
                            Restrictions.eq("estudio.aprobada", false))
            );
        }
        //Se filtra por rango de fecha de aprobacion
        if (filtro.getFechaInicioAprob()!=null && filtro.getFechaFinAprob()!=null){
            crit.add( Restrictions.and(
                            Restrictions.between("estudio.fechaAprobacion", filtro.getFechaInicioAprob(),filtro.getFechaFinAprob()))
            );
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
            //Se filtra que el �rea a la que pertenece la solicitud este asociada al usuario autenticado ya sea  analista, jefe departamento o director
            //nivel analista
            if (filtro.getNivelLaboratorio() == 1) {
                //Se filtra que el �rea a la que pertenece la solicitud este asociada al usuario autenticado
                crit.add(Restrictions.and(Subqueries.propertyIn("estudio.idSolicitudEstudio", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                        .add(Restrictions.eq("anulado", false))
                        .createAlias("tipoEstudio", "estudio")
                        .createAlias("estudio.area", "area")
                        .add(Subqueries.propertyIn("area.idArea", DetachedCriteria.forClass(AutoridadArea.class)
                                .add(Restrictions.eq("pasivo", false)) //autoridad area activa
                                .add(Restrictions.and(Restrictions.eq("user.username", filtro.getNombreUsuario()))) //usuario
                                .setProjection(Property.forName("area.idArea"))))
                        //.createAlias("idTomaMx", "toma")
                        .setProjection(Property.forName("idSolicitudEstudio")))));
            }
            //nivel jefe departamento
            if (filtro.getNivelLaboratorio() == 2) {
                //Se filtra que el �rea a la que pertenece la solicitud este asociada al usuario autenticado
                crit.add(Restrictions.and(Subqueries.propertyIn("estudio.idSolicitudEstudio", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                        .add(Restrictions.eq("anulado", false))
                        .createAlias("tipoEstudio", "estudio")
                        .createAlias("estudio.area", "area")
                        .add(Subqueries.propertyIn("area.idArea", DetachedCriteria.forClass(AreaDepartamento.class)
                                        .createAlias("area","area")
                                        .createAlias("depDireccion", "depDir")
                                        .createAlias("depDir.departamento","departamento")
                                        .add(Restrictions.eq("pasivo", false)) //area departamento activa
                                        .add(Subqueries.propertyIn("departamento.idDepartamento", DetachedCriteria.forClass(AutoridadDepartamento.class)
                                                .add(Restrictions.eq("pasivo", false)) //autoridad area activa
                                                .add(Restrictions.and(Restrictions.eq("user.username", filtro.getNombreUsuario()))) //usuario
                                                .setProjection(Property.forName("departamento.idDepartamento"))))
                                        .setProjection(Property.forName("area.idArea"))
                        ))
                        .setProjection(Property.forName("idSolicitudEstudio")))));
            }
            //nivel director
            if (filtro.getNivelLaboratorio() == 3) {
                //Se filtra que el �rea a la que pertenece la solicitud este asociada al usuario autenticado
                crit.add(Restrictions.and(Subqueries.propertyIn("estudio.idSolicitudEstudio", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                        .add(Restrictions.eq("anulado", false))
                        .createAlias("tipoEstudio", "estudio")
                        .createAlias("estudio.area", "area")
                        .add(Subqueries.propertyIn("area.idArea", DetachedCriteria.forClass(AreaDepartamento.class)
                                        .createAlias("area","area")
                                        .createAlias("depDireccion", "depDir")
                                        .createAlias("depDir.direccionLab","dirLab")
                                        .createAlias("dirLab.direccion","direccion")
                                        .add(Restrictions.eq("pasivo", false)) //area departamento activa
                                        .add(Restrictions.eq("depDir.pasivo", false)) //departamento direccion activa
                                        .add(Restrictions.eq("dirLab.pasivo", false)) //direccion laboratorio activa
                                        .add(Subqueries.propertyIn("direccion.idDireccion", DetachedCriteria.forClass(AutoridadDireccion.class)
                                                .add(Restrictions.eq("pasivo", false)) //autoridad direccion activa
                                                .add(Restrictions.and(Restrictions.eq("user.username", filtro.getNombreUsuario()))) //usuario
                                                .setProjection(Property.forName("direccion.idDireccion"))))
                                        .setProjection(Property.forName("area.idArea"))
                        ))
                        .setProjection(Property.forName("idSolicitudEstudio")))));
            }
        }

        return crit.list();
    }

    @SuppressWarnings("unchecked")
    public List<OrdenExamen> getOrdenExaBySolicitudDx(String idSolicitud) {
        Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(OrdenExamen.class, "orden");
        crit.createAlias("orden.solicitudDx", "rutina");

        crit.add(Restrictions.and(
                Restrictions.eq("rutina.idSolicitudDx", idSolicitud)));

        /*crit.add(Subqueries.propertyIn("idOrdenExamen", DetachedCriteria.forClass(DetalleResultado.class)
                .createAlias("examen", "examen")
                .add(Restrictions.and(Restrictions.eq("pasivo",false)))
                .setProjection(Property.forName("examen.idOrdenExamen"))));*/

        return crit.list();
    }

    @SuppressWarnings("unchecked")
    public List<OrdenExamen> getOrdenExaBySolicitudEstudio(String idSolicitud) {
        Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(OrdenExamen.class, "orden");
        crit.createAlias("orden.solicitudEstudio", "estudio");

        crit.add(Restrictions.and(
                Restrictions.eq("estudio.idSolicitudEstudio", idSolicitud)));

        crit.add(Subqueries.propertyIn("idOrdenExamen", DetachedCriteria.forClass(DetalleResultado.class)
                .createAlias("examen", "examen")
                .add(Restrictions.and(Restrictions.eq("pasivo",false)))
                .setProjection(Property.forName("examen.idOrdenExamen"))));

        return crit.list();
    }

    public List<DetalleResultado> getResultDetailExaByIdOrden(String idOrdenExa){
        String query = "from DetalleResultado where examen.idOrdenExamen = :idOrdenExa and pasivo=false ORDER BY respuesta.orden asc";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idOrdenExa",idOrdenExa);
        return q.list();
    }



    /**
     * Obtiene un catalogo lista seg�n el id indicado
     * @param id del Catalogo Lista a obtener
     * @return Catalogo_lista
     */
    public Catalogo_Lista getCatalogoLista(String id){
        String query = "from Catalogo_Lista as c where c.idCatalogoLista= :id";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("id", id);
        return  (Catalogo_Lista)q.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<DetalleResultadoFinal> getDetResActivosBySolicitud(String idSolicitud){
        List<DetalleResultadoFinal> resultadoFinals = new ArrayList<DetalleResultadoFinal>();
        Session session = sessionFactory.getCurrentSession();
        String query = "select a from DetalleResultadoFinal as a inner join a.solicitudDx as r where a.pasivo = false and r.idSolicitudDx = :idSolicitud ";
        Query q = session.createQuery(query);
        q.setParameter("idSolicitud", idSolicitud);
        resultadoFinals = q.list();
        if (resultadoFinals.size()<=0) {
            String query2 = "select a from DetalleResultadoFinal as a inner join a.solicitudEstudio as r where a.pasivo = false and r.idSolicitudEstudio = :idSolicitud ";
            Query q2 = session.createQuery(query2);
            q2.setParameter("idSolicitud", idSolicitud);
            resultadoFinals.addAll(q2.list());
        }
        return  resultadoFinals;
    }

    /**
     * Verifica si existe registrado un resultado para la respuesta y dx indicado, siempre y cuando el registro este activo
     * @param idSolicitud solicitud a verificar
     * @param idRespuesta respuesta a verificar
     * @return DetalleResultadoFinal
     */
    public DetalleResultadoFinal getDetResBySolicitudAndRespuesta(String idSolicitud, int idRespuesta){
        DetalleResultadoFinal resultadoFinal;
        String query = "Select a from DetalleResultadoFinal as a inner join a.solicitudDx as ex inner join a.respuesta as re " +
                "where ex.idSolicitudDx = :idSolicitud and re.idRespuesta = :idRespuesta and a.pasivo = false ";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idSolicitud", idSolicitud);
        q.setParameter("idRespuesta", idRespuesta);
        resultadoFinal = (DetalleResultadoFinal)q.uniqueResult();
        if (resultadoFinal==null) {
            String query2 = "Select a from DetalleResultadoFinal as a inner join a.solicitudEstudio as ex inner join a.respuesta as re " +
                    "where ex.idSolicitudEstudio = :idSolicitud and re.idRespuesta = :idRespuesta and a.pasivo = false ";
            Query q2 = session.createQuery(query2);
            q2.setParameter("idSolicitud", idSolicitud);
            q2.setParameter("idRespuesta", idRespuesta);
            resultadoFinal = (DetalleResultadoFinal)q2.uniqueResult();
        }
        return  resultadoFinal;
    }

    /**
     * Verifica si existe registrado un resultado para la respuesta y dx indicado, siempre y cuando el registro este activo
     * @param idSolicitud solicitud a verificar
     * @param idRespuesta respuesta a verificar
     * @return DetalleResultadoFinal
     */
    public DetalleResultadoFinal getDetResBySolicitudAndRespuestaExa(String idSolicitud, int idRespuesta){
        DetalleResultadoFinal resultadoFinal;
        Session session = sessionFactory.getCurrentSession();
        String query = "Select a from DetalleResultadoFinal as a inner join a.solicitudDx as ex inner join a.respuestaExamen as re " +
                "where ex.idSolicitudDx = :idSolicitud and re.idRespuesta = :idRespuesta and a.pasivo = false ";
        Query q = session.createQuery(query);
        q.setParameter("idSolicitud", idSolicitud);
        q.setParameter("idRespuesta", idRespuesta);
        resultadoFinal = (DetalleResultadoFinal)q.uniqueResult();
        if (resultadoFinal==null) {
            String query2 = "Select a from DetalleResultadoFinal as a inner join a.solicitudEstudio as ex inner join a.respuestaExamen as re " +
                    "where ex.idSolicitudEstudio = :idSolicitud and re.idRespuesta = :idRespuesta and a.pasivo = false ";
            Query q2 = session.createQuery(query2);
            q2.setParameter("idSolicitud", idSolicitud);
            q2.setParameter("idRespuesta", idRespuesta);
            resultadoFinal = (DetalleResultadoFinal)q2.uniqueResult();
        }
        return  resultadoFinal;
    }

    /**
     * Verifica si existe registrado un resultado para la respuesta y dx indicado, siempre y cuando el registro este activo
     * @param idSolicitud solicitud a verificar
     * @param idConcepto concepto a verificar
     * @return DetalleResultadoFinal
     */
    public List<DetalleResultadoFinal> getDetResBySolicitudAndConceptoRespuestaExa(String idSolicitud, int idConcepto){
        List<DetalleResultadoFinal> resultadoFinal;
        Session session = sessionFactory.getCurrentSession();
        String query = "Select a from DetalleResultadoFinal as a inner join a.solicitudDx as ex inner join a.respuestaExamen as re " +
                "where ex.idSolicitudDx = :idSolicitud and re.concepto.idConcepto = :idConcepto and a.pasivo = false ";
        Query q = session.createQuery(query);
        q.setParameter("idSolicitud", idSolicitud);
        q.setParameter("idConcepto", idConcepto);
        resultadoFinal = q.list();
        if (resultadoFinal==null) {
            String query2 = "Select a from DetalleResultadoFinal as a inner join a.solicitudEstudio as ex inner join a.respuestaExamen as re " +
                    "where ex.idSolicitudEstudio = :idSolicitud and re.concepto.idConcepto = :idConcepto and a.pasivo = false ";
            Query q2 = session.createQuery(query2);
            q2.setParameter("idSolicitud", idSolicitud);
            q2.setParameter("idConcepto", idConcepto);
            resultadoFinal = q2.list();
        }
        return  resultadoFinal;
    }

    /**
     * Actualiza un Registro de detalle de resultado final
     *
     * @param dto Objeto a agregar o actualizar
     * @throws Exception
     */
    public void updateDetResFinal(DetalleResultadoFinal dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.update(dto);
            }
            else
                throw new Exception("Objeto DetalleResultadoFinal es NULL");
        }catch (Exception ex){
            logger.error("Error al actualizar DetalleResultadoFinal",ex);
            throw ex;
        }
    }


    /**
     * Agrega un Registro de detalle de resultado final
     *
     * @param dto Objeto a agregar o actualizar
     * @throws Exception
     */
    public void saveDetResFinal(DetalleResultadoFinal dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.save(dto);
            }
            else
                throw new Exception("Objeto DetalleResultadoFinal es NULL");
        }catch (Exception ex){
            logger.error("Error al guardar DetalleResultadoFinal",ex);
            throw ex;
        }
    }

    public List<RechazoResultadoFinalSolicitud> getResultadosRechazadosByFiltro(FiltroMx filtro){
        Session session = sessionFactory.getCurrentSession();
        Soundex varSoundex = new Soundex();
        Criteria crit = session.createCriteria(RechazoResultadoFinalSolicitud.class, "rechazo");
        crit.createAlias("rechazo.solicitudEstudio","estudio");
        crit.createAlias("estudio.idTomaMx","tomaMx");
        crit.createAlias("tomaMx.estadoMx","estado");
        crit.createAlias("tomaMx.idNotificacion", "noti");
        //siempre se tomam las muestras que no estan anuladas
        crit.add( Restrictions.and(
                        Restrictions.eq("tomaMx.anulada", false))
        );//y las ordenes en estado seg�n filtro
        Criteria crit2 = session.createCriteria(RechazoResultadoFinalSolicitud.class, "rechazo");
        crit2.createAlias("rechazo.solicitudDx","rutina");
        crit2.createAlias("rutina.idTomaMx","tomaMx");
        crit2.createAlias("tomaMx.estadoMx","estado");
        crit2.createAlias("tomaMx.idNotificacion", "noti");
        //siempre se tomam las muestras que no estan anuladas
        crit2.add( Restrictions.and(
                        Restrictions.eq("tomaMx.anulada", false))
        );//y las ordenes en estado seg�n filtro
        if (filtro.getCodEstado()!=null) {
            crit.add(Restrictions.and(
                    Restrictions.eq("estado.codigo", filtro.getCodEstado()).ignoreCase()));
            crit2.add(Restrictions.and(
                    Restrictions.eq("estado.codigo", filtro.getCodEstado()).ignoreCase()));
        }
        // se filtra por nombre y apellido persona
        if (filtro.getNombreApellido()!=null) {
            //crit.createAlias("noti.persona", "person");
            //crit2.createAlias("noti.persona", "person");
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
                conditGroup.add(Subqueries.propertyIn("noti.persona.personaId", DetachedCriteria.forClass(SisPersona.class,"person")
                        .add(Restrictions.or(Restrictions.ilike("person.primerNombre", "%" + partes[i] + "%"))
                                .add(Restrictions.or(Restrictions.ilike("person.primerApellido", "%" + partes[i] + "%"))
                                        .add(Restrictions.or(Restrictions.ilike("person.segundoNombre", "%" + partes[i] + "%"))
                                                .add(Restrictions.or(Restrictions.ilike("person.segundoApellido", "%" + partes[i] + "%"))
                                                        .add(Restrictions.or(Restrictions.ilike("person.sndNombre", "%" + partesSnd[i] + "%")))))))
                        .setProjection(Property.forName("personaId"))))
                        .add(Subqueries.propertyIn("noti.solicitante.idSolicitante", DetachedCriteria.forClass(Solicitante.class,"solicitante")
                                .add(Restrictions.ilike("solicitante.nombre", "%" + partes[i] + "%"))
                                .setProjection(Property.forName("idSolicitante"))));
                crit.add(conditGroup);
                crit2.add(conditGroup);
            }
        }
        //se filtra por SILAIS
        if (filtro.getCodSilais()!=null){
            crit.createAlias("noti.codSilaisAtencion","silais");
            crit.add( Restrictions.and(
                            Restrictions.eq("silais.codigo", Long.valueOf(filtro.getCodSilais())))
            );
            crit2.createAlias("noti.codSilaisAtencion","silais");
            crit2.add( Restrictions.and(
                            Restrictions.eq("silais.codigo", Long.valueOf(filtro.getCodSilais())))
            );
        }
        //se filtra por unidad de salud
        if (filtro.getCodUnidadSalud()!=null){
            crit.createAlias("noti.codUnidadAtencion","unidadS");
            crit.add( Restrictions.and(
                            Restrictions.eq("unidadS.codigo", Long.valueOf(filtro.getCodUnidadSalud())))
            );
            crit2.createAlias("noti.codUnidadAtencion","unidadS");
            crit2.add( Restrictions.and(
                            Restrictions.eq("unidadS.codigo", Long.valueOf(filtro.getCodUnidadSalud())))
            );
        }
        /*
        //Se filtra por rango de fecha de toma de muestra
        if (filtro.getFechaInicioTomaMx()!=null && filtro.getFechaFinTomaMx()!=null){
            crit.add( Restrictions.and(
                            Restrictions.between("tomaMx.fechaHTomaMx", filtro.getFechaInicioTomaMx(),filtro.getFechaFinTomaMx()))
            );
            crit2.add( Restrictions.and(
                            Restrictions.between("tomaMx.fechaHTomaMx", filtro.getFechaInicioTomaMx(),filtro.getFechaFinTomaMx()))
            );
        }
        //Se filtra por rango de fecha de recepci�n
        if (filtro.getFechaInicioRecep()!=null && filtro.getFechaFinRecep()!=null){
            crit.add(Subqueries.propertyIn("idTomaMx.idTomaMx", DetachedCriteria.forClass(RecepcionMx.class)
                    .createAlias("tomaMx", "toma").add(Restrictions.between("fechaHoraRecepcion", filtro.getFechaInicioRecep(),filtro.getFechaFinRecep()))
                    .setProjection(Property.forName("toma.idTomaMx"))));
            crit2.add(Subqueries.propertyIn("idTomaMx.idTomaMx", DetachedCriteria.forClass(RecepcionMx.class)
                    .createAlias("tomaMx", "toma").add(Restrictions.between("fechaHoraRecepcion", filtro.getFechaInicioRecep(),filtro.getFechaFinRecep()))
                    .setProjection(Property.forName("toma.idTomaMx"))));

        }*/
        //Se filtra por rango de fecha de toma de muestra
        if (filtro.getFechaInicioRechazo()!=null && filtro.getFechaFinRechazo()!=null){
            crit.add( Restrictions.and(
                            Restrictions.between("fechaHRechazo", filtro.getFechaInicioRechazo(),filtro.getFechaFinRechazo()))
            );
            crit2.add( Restrictions.and(
                            Restrictions.between("fechaHRechazo", filtro.getFechaInicioRechazo(),filtro.getFechaFinRechazo()))
            );
        }
        // se filtra por tipo de muestra
        if (filtro.getCodTipoMx()!=null){
            crit.add( Restrictions.and(
                            Restrictions.eq("tomaMx.codTipoMx.idTipoMx", Integer.valueOf(filtro.getCodTipoMx())))
            );
            crit2.add( Restrictions.and(
                            Restrictions.eq("tomaMx.codTipoMx.idTipoMx", Integer.valueOf(filtro.getCodTipoMx())))
            );
        }

        if(filtro.getIncluirMxInadecuada()!=null && filtro.getIncluirMxInadecuada()){

            crit.add(Subqueries.propertyIn("idTomaMx.idTomaMx", DetachedCriteria.forClass(RecepcionMx.class)
                    .createAlias("tomaMx", "toma").add(Restrictions.isNull("calidadMx.codigo"))
                    .add(Restrictions.or(Restrictions.ne("calidadMx.codigo", "CALIDMX|IDC")))
                    .setProjection(Property.forName("toma.idTomaMx"))));
            crit2.add(Subqueries.propertyIn("idTomaMx.idTomaMx", DetachedCriteria.forClass(RecepcionMx.class)
                    .createAlias("tomaMx", "toma").add(Restrictions.isNull("calidadMx.codigo"))
                    .add(Restrictions.or(Restrictions.ne("calidadMx.codigo", "CALIDMX|IDC")))
                    .setProjection(Property.forName("toma.idTomaMx"))));

        }

        if(filtro.getCodigoUnicoMx()!=null){
            crit.add( Restrictions.and(
                            Restrictions.eq("tomaMx.codigoUnicoMx", filtro.getCodigoUnicoMx()))
            );
            crit2.add( Restrictions.and(
                            Restrictions.eq("tomaMx.codigoUnicoMx", filtro.getCodigoUnicoMx()))
            );
        }

        //nombre solicitud
        if (filtro.getNombreSolicitud() != null) {
            if (filtro.getCodTipoSolicitud() != null) {
                if (filtro.getCodTipoSolicitud().equals("Estudio")) {
                    crit.add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                            .add(Restrictions.eq("anulado", false))
                            .createAlias("tipoEstudio", "estudio")
                            .add(Restrictions.ilike("estudio.nombre", "%" + filtro.getNombreSolicitud() + "%"))
                            .createAlias("idTomaMx", "toma")
                            .setProjection(Property.forName("toma.idTomaMx"))));
                }
                if (filtro.getCodTipoSolicitud().equals("Rutina")) {
                    crit2.add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                            .add(Restrictions.eq("anulado", false))
                            .createAlias("codDx", "dx")
                            .add(Restrictions.ilike("dx.nombre", "%" + filtro.getNombreSolicitud() + "%"))
                            .createAlias("idTomaMx", "toma")
                            .setProjection(Property.forName("toma.idTomaMx"))));
                }
            } else {

                Junction conditGroup = Restrictions.disjunction();
                conditGroup.add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                        .add(Restrictions.eq("anulado", false))
                        .createAlias("tipoEstudio", "estudio")
                        .add(Restrictions.ilike("estudio.nombre", "%" + filtro.getNombreSolicitud() + "%"))
                        .createAlias("idTomaMx", "toma")
                        .setProjection(Property.forName("toma.idTomaMx"))))
                        .add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                                .add(Restrictions.eq("anulado", false))
                                .createAlias("codDx", "dx")
                                .add(Restrictions.ilike("dx.nombre", "%" + filtro.getNombreSolicitud() + "%"))
                                .createAlias("idTomaMx", "toma")
                                .setProjection(Property.forName("toma.idTomaMx"))));

                crit.add(conditGroup);
                crit2.add(conditGroup);
            }
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
            //Se filtra que el �rea a la que pertenece la solicitud este asociada al usuario autenticado ya sea  analista, jefe departamento o director
            //nivel analista
            if (filtro.getNivelLaboratorio() == 1) {
                //Se filtra que el �rea a la que pertenece la solicitud este asociada al usuario autenticado
                crit.add(Restrictions.and(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                        .add(Restrictions.eq("anulado", false))
                        .createAlias("tipoEstudio", "estudio")
                        .createAlias("estudio.area", "area")
                        .add(Subqueries.propertyIn("area.idArea", DetachedCriteria.forClass(AutoridadArea.class)
                                .add(Restrictions.eq("pasivo", false)) //autoridad area activa
                                .add(Restrictions.and(Restrictions.eq("user.username", filtro.getNombreUsuario()))) //usuario
                                .setProjection(Property.forName("area.idArea"))))
                        .createAlias("idTomaMx", "toma")
                        .setProjection(Property.forName("toma.idTomaMx")))));
                crit2.add(Restrictions.and(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                        .add(Restrictions.eq("anulado", false))
                        .createAlias("codDx", "dx")
                        .createAlias("dx.area","area")
                        .add(Subqueries.propertyIn("area.idArea", DetachedCriteria.forClass(AutoridadArea.class)
                                .add(Restrictions.eq("pasivo", false)) //autoridad area activa
                                .add(Restrictions.and(Restrictions.eq("user.username", filtro.getNombreUsuario()))) //usuario
                                .setProjection(Property.forName("area.idArea"))))
                        .createAlias("idTomaMx", "toma")
                        .setProjection(Property.forName("toma.idTomaMx")))));
            }
            //nivel jefe departamento
            if (filtro.getNivelLaboratorio() == 2) {
                //Se filtra que el �rea a la que pertenece la solicitud este asociada al usuario autenticado
                crit.add(Restrictions.and(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                        .add(Restrictions.eq("anulado", false))
                        .createAlias("tipoEstudio", "estudio")
                        .createAlias("estudio.area", "area")
                        .add(Subqueries.propertyIn("area.idArea", DetachedCriteria.forClass(AreaDepartamento.class)
                                        .createAlias("area","area")
                                        .createAlias("depDireccion", "depDir")
                                        .createAlias("depDir.departamento","departamento")
                                        .add(Restrictions.eq("pasivo", false)) //area departamento activa
                                        .add(Subqueries.propertyIn("departamento.idDepartamento", DetachedCriteria.forClass(AutoridadDepartamento.class)
                                                .add(Restrictions.eq("pasivo", false)) //autoridad area activa
                                                .add(Restrictions.and(Restrictions.eq("user.username", filtro.getNombreUsuario()))) //usuario
                                                .setProjection(Property.forName("departamento.idDepartamento"))))
                                        .setProjection(Property.forName("area.idArea"))
                        ))
                        .createAlias("idTomaMx", "toma")
                        .setProjection(Property.forName("toma.idTomaMx")))));

                crit2.add(Restrictions.and(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                        .add(Restrictions.eq("anulado", false))
                        .createAlias("codDx", "dx")
                        .createAlias("dx.area","area")
                        .add(Subqueries.propertyIn("area.idArea", DetachedCriteria.forClass(AreaDepartamento.class)
                                        .createAlias("area","area")
                                        .createAlias("depDireccion", "depDir")
                                        .createAlias("depDir.departamento","departamento")
                                        .add(Restrictions.eq("pasivo", false)) //area departamento activa
                                        .add(Subqueries.propertyIn("departamento.idDepartamento", DetachedCriteria.forClass(AutoridadDepartamento.class)
                                                .add(Restrictions.eq("pasivo", false)) //autoridad area activa
                                                .add(Restrictions.and(Restrictions.eq("user.username", filtro.getNombreUsuario()))) //usuario
                                                .setProjection(Property.forName("departamento.idDepartamento"))))
                                        .setProjection(Property.forName("area.idArea"))
                        ))
                        .createAlias("idTomaMx", "toma")
                        .setProjection(Property.forName("toma.idTomaMx")))));
            }
            //nivel director
            if (filtro.getNivelLaboratorio() == 3) {
                //Se filtra que el �rea a la que pertenece la solicitud este asociada al usuario autenticado
                crit.add(Restrictions.and(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                        .add(Restrictions.eq("anulado", false))
                        .createAlias("tipoEstudio", "estudio")
                        .createAlias("estudio.area", "area")
                        .add(Subqueries.propertyIn("area.idArea", DetachedCriteria.forClass(AreaDepartamento.class)
                                        .createAlias("area","area")
                                        .createAlias("depDireccion", "depDir")
                                        .createAlias("depDir.direccionLab","dirLab")
                                        .createAlias("dirLab.direccion","direccion")
                                        .add(Restrictions.eq("pasivo", false)) //area departamento activa
                                        .add(Restrictions.eq("depDir.pasivo", false)) //departamento direccion activa
                                        .add(Restrictions.eq("dirLab.pasivo", false)) //direccion laboratorio activa
                                        .add(Subqueries.propertyIn("direccion.idDireccion", DetachedCriteria.forClass(AutoridadDireccion.class)
                                                .add(Restrictions.eq("pasivo", false)) //autoridad direccion activa
                                                .add(Restrictions.and(Restrictions.eq("user.username", filtro.getNombreUsuario()))) //usuario
                                                .setProjection(Property.forName("direccion.idDireccion"))))
                                        .setProjection(Property.forName("area.idArea"))
                        ))
                        .createAlias("idTomaMx", "toma")
                        .setProjection(Property.forName("toma.idTomaMx")))));

                crit2.add(Restrictions.and(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                        .add(Restrictions.eq("anulado", false))
                        .createAlias("codDx", "dx")
                        .createAlias("dx.area","area")
                        .add(Subqueries.propertyIn("area.idArea", DetachedCriteria.forClass(AreaDepartamento.class)
                                        .createAlias("area","area")
                                        .createAlias("depDireccion", "depDir")
                                        .createAlias("depDir.direccionLab","dirLab")
                                        .createAlias("dirLab.direccion","direccion")
                                        .add(Restrictions.eq("pasivo", false)) //area departamento activa
                                        .add(Restrictions.eq("depDir.pasivo", false)) //departamento direccion activa
                                        .add(Restrictions.eq("dirLab.pasivo", false)) //direccion laboratorio activa
                                        .add(Subqueries.propertyIn("direccion.idDireccion", DetachedCriteria.forClass(AutoridadDireccion.class)
                                                .add(Restrictions.eq("pasivo", false)) //autoridad direccion activa
                                                .add(Restrictions.and(Restrictions.eq("user.username", filtro.getNombreUsuario()))) //usuario
                                                .setProjection(Property.forName("direccion.idDireccion"))))
                                        .setProjection(Property.forName("area.idArea"))
                        ))
                        .createAlias("idTomaMx", "toma")
                        .setProjection(Property.forName("toma.idTomaMx")))));
            }
        }

        List<RechazoResultadoFinalSolicitud> resultado = crit.list();
        resultado.addAll(crit2.list());
        return resultado;
    }

    public List<DetalleResultadoFinal> getDetResPasivosBySolicitud(String idSolicitud){
        List<DetalleResultadoFinal> resultadoFinals = new ArrayList<DetalleResultadoFinal>();
        Session session = sessionFactory.getCurrentSession();
        String query = "select a from DetalleResultadoFinal as a inner join a.solicitudDx as r where a.pasivo = true and r.idSolicitudDx = :idSolicitud ";
        Query q = session.createQuery(query);
        q.setParameter("idSolicitud", idSolicitud);
        resultadoFinals = q.list();
        String query2 = "select a from DetalleResultadoFinal as a inner join a.solicitudEstudio as r where a.pasivo = true and r.idSolicitudEstudio = :idSolicitud ";
        Query q2 = session.createQuery(query2);
        q2.setParameter("idSolicitud", idSolicitud);
        resultadoFinals.addAll(q2.list());
        return  resultadoFinals;
    }

    /**
     * Obtiene el ultimo registro de un detalle de resultado
     * @param idSolicitud
     * @return Catalogo_lista
     */
    public Object getFechaResultadoByIdSoli(String idSolicitud) {
        Object fechaResultado = null;
        Session session = sessionFactory.getCurrentSession();
        String query = "select max (a.fechahRegistro) from DetalleResultadoFinal as a inner join a.solicitudDx as r where a.pasivo = false and r.idSolicitudDx = :idSolicitud ";
        Query q = session.createQuery(query);
        q.setParameter("idSolicitud", idSolicitud);

        String query2 = "select max(a.fechahRegistro) from DetalleResultadoFinal as a inner join a.solicitudEstudio as r where a.pasivo = false and r.idSolicitudEstudio = :idSolicitud ";
        Query q2 = session.createQuery(query2);
        q2.setParameter("idSolicitud", idSolicitud);

        Object det = q.uniqueResult();
        Object detE = q2.uniqueResult();

        if (det != null) {
            fechaResultado = det;
        } else if (detE != null) {
            fechaResultado = detE;
        }

        return fechaResultado;
    }

    @SuppressWarnings("unchecked")
    public List<DaSolicitudDx> getSolicitudesDxByIdNotificacion(String idNotificacion){
        Session session = sessionFactory.getCurrentSession();
        Soundex varSoundex = new Soundex();
        Criteria crit = session.createCriteria(DaSolicitudDx.class, "diagnostico");
        crit.createAlias("diagnostico.idTomaMx","tomaMx");
        //crit.createAlias("tomaMx.estadoMx","estado");
        crit.createAlias("tomaMx.idNotificacion", "noti");
        //siempre se tomam las muestras que no estan anuladas
        crit.add( Restrictions.and(
                        Restrictions.eq("tomaMx.anulada", false))
        );
        //siempre se tomam las rutinas que no son control de calidad
        crit.add( Restrictions.and(
                        Restrictions.eq("diagnostico.controlCalidad", false))
        );
        crit.add( Restrictions.and(
                        Restrictions.eq("noti.idNotificacion", idNotificacion))
        );
        crit.add(Restrictions.and(Restrictions.eq("diagnostico.aprobada", true)));
        crit.add(Restrictions.eq("diagnostico.anulado", false));

        return crit.list();
    }

    @SuppressWarnings("unchecked")
    public List<DaSolicitudEstudio> getSolicitudesEByIdNotificacion(String idNotificacion){
        Session session = sessionFactory.getCurrentSession();
        Soundex varSoundex = new Soundex();
        Criteria crit = session.createCriteria(DaSolicitudEstudio.class, "estudio");
        crit.createAlias("estudio.idTomaMx","tomaMx");
        //crit.createAlias("tomaMx.estadoMx","estado");
        crit.createAlias("tomaMx.idNotificacion", "noti");
        //siempre se tomam las muestras que no estan anuladas
        crit.add( Restrictions.and(
                        Restrictions.eq("tomaMx.anulada", false))
        );

        crit.add( Restrictions.and(
                        Restrictions.eq("noti.idNotificacion", idNotificacion))
        );
        crit.add(Restrictions.and(Restrictions.eq("estudio.aprobada", true)));
        crit.add(Restrictions.eq("estudio.anulado", false));
        return crit.list();
    }

}
