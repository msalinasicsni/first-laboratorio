package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.muestra.*;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.AutoridadLaboratorio;
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
import java.util.Random;

/**
 * Created by FIRSTICT on 4/17/2015.
 * V1.0
 */
@Service("hojaTrabajoService")
@Transactional
public class HojaTrabajoService {
    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;

    public void addHojaTrabajo(HojaTrabajo dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.save(dto);
            }
            else
                throw new Exception("Objeto hoja trabajo es NULL");
        }catch (Exception ex){
            ex.printStackTrace();
            throw ex;
        }
    }

    public void addDetalleHojaTrabajo(Mx_HojaTrabajo dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.save(dto);
            }
            else
                throw new Exception("Objeto mx_hoja trabajo es NULL");
        }catch (Exception ex){
            ex.printStackTrace();
            throw ex;
        }
    }

    public List<DaTomaMx> getTomaMxByHojaTrabajo(int numeroHoja){
        String query = "select b from Mx_HojaTrabajo as a inner join a.tomaMx as b inner join a.hojaTrabajo as c " +
                "where c.numero =:numero";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setInteger("numero", numeroHoja);
        return q.list();
    }

    public HojaTrabajo getHojaTrabajo(int numeroHoja){
        String query = "from HojaTrabajo as c " +
                "where c.numero =:numero";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setInteger("numero", numeroHoja);
        return (HojaTrabajo)q.uniqueResult();
    }

    public int obtenerNumeroHoja(){
        Random r = new Random();
        int numero = r.nextInt(9999999 - 1) + 1;
        String query = "from HojaTrabajo as a where a.numero =:numero";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setInteger("numero", numero);
        HojaTrabajo existeHoja  = (HojaTrabajo)q.uniqueResult();
        if (existeHoja!=null){
            return obtenerNumeroHoja();
        }
        return numero;
    }

    public List<HojaTrabajo> getTomaMxByFiltro(FiltroMx filtro){
        Session session = sessionFactory.getCurrentSession();
        Soundex varSoundex = new Soundex();
        Criteria crit = session.createCriteria(HojaTrabajo.class, "hoja");
        //siempre se tomam las muestras que no estan anuladas
        crit.add(Subqueries.propertyIn("numero", DetachedCriteria.forClass(Mx_HojaTrabajo.class)
                .createAlias("hojaTrabajo","hojaTrabajo")
                .createAlias("tomaMx","tomaMx")
                .add( Restrictions.and(
                                Restrictions.eq("tomaMx.anulada", false))
                )
                .setProjection(Property.forName("hojaTrabajo.numero"))));//y las ordenes en estado según filtro
        /*if (filtro.getCodEstado()!=null) {
            crit.add(Restrictions.and(
                    Restrictions.eq("estado.codigo", filtro.getCodEstado()).ignoreCase()));
        }*/
        // se filtra por nombre y apellido persona
        if (filtro.getNombreApellido()!=null) {
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
                //crit.add(conditionGroup);
                crit.add(Subqueries.propertyIn("numero", DetachedCriteria.forClass(Mx_HojaTrabajo.class)
                        .createAlias("hojaTrabajo","hojaTrabajo")
                        .createAlias("tomaMx","tomaMx")
                        .createAlias("tomaMx.idNotificacion", "notifi")
                        .createAlias("notifi.persona", "person")
                        .add(conditionGroup)
                        .setProjection(Property.forName("hojaTrabajo.numero"))));
            }

        }
        //se filtra por SILAIS
        if (filtro.getCodSilais()!=null){
            crit.add(Subqueries.propertyIn("numero", DetachedCriteria.forClass(Mx_HojaTrabajo.class)
                    .createAlias("hojaTrabajo","hojaTrabajo")
                    .createAlias("tomaMx","tomaMx")
                    .createAlias("tomaMx.idNotificacion", "notifi")
                    .add( Restrictions.and(
                                    Restrictions.eq("notifi.codSilaisAtencion.codigo", Long.valueOf(filtro.getCodSilais())))
                    )
                    .setProjection(Property.forName("hojaTrabajo.numero"))));
        }
        //se filtra por unidad de salud
        if (filtro.getCodUnidadSalud()!=null){
            crit.add(Subqueries.propertyIn("numero", DetachedCriteria.forClass(Mx_HojaTrabajo.class)
                    .createAlias("hojaTrabajo","hojaTrabajo")
                    .createAlias("tomaMx","tomaMx")
                    .createAlias("tomaMx.idNotificacion", "notifi")
                    .createAlias("notifi.codUnidadAtencion","unidadS")
                    .add( Restrictions.and(
                                    Restrictions.eq("unidadS.codigo", Long.valueOf(filtro.getCodUnidadSalud())))
                    )
                    .setProjection(Property.forName("hojaTrabajo.numero"))));
        }
        //Se filtra por rango de fecha de registro hoja trabajo (se usan campos de filtro que hacen referencia a fecha toma mx)
        if (filtro.getFechaInicioTomaMx()!=null && filtro.getFechaFinTomaMx()!=null){
            crit.add( Restrictions.and(
                            Restrictions.between("hoja.fechaRegistro", filtro.getFechaInicioTomaMx(),filtro.getFechaFinTomaMx()))
            );
        }
        // se filtra por tipo de muestra
        if (filtro.getCodTipoMx()!=null){
            crit.add(Subqueries.propertyIn("numero", DetachedCriteria.forClass(Mx_HojaTrabajo.class)
                    .createAlias("hojaTrabajo","hojaTrabajo")
                    .createAlias("tomaMx","tomaMx")
                    .add( Restrictions.and(
                                    Restrictions.eq("tomaMx.codTipoMx.idTipoMx", Integer.valueOf(filtro.getCodTipoMx())))
                    )
                    .setProjection(Property.forName("hojaTrabajo.numero"))));
        }

        //se filtra por tipo de solicitud
        if(filtro.getCodTipoSolicitud()!=null){
            if(filtro.getCodTipoSolicitud().equals("Estudio")){
                crit.add(Subqueries.propertyIn("numero", DetachedCriteria.forClass(Mx_HojaTrabajo.class)
                        .createAlias("hojaTrabajo","hojaTrabajo")
                        .createAlias("tomaMx","tomaMx")
                        .add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                                .createAlias("idTomaMx", "idTomaMx")
                                .setProjection(Property.forName("idTomaMx.idTomaMx"))))
                        .setProjection(Property.forName("hojaTrabajo.numero"))));
            }else{
                crit.add(Subqueries.propertyIn("numero", DetachedCriteria.forClass(Mx_HojaTrabajo.class)
                        .createAlias("hojaTrabajo","hojaTrabajo")
                        .createAlias("tomaMx","tomaMx")
                        .add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                                .createAlias("idTomaMx", "idTomaMx")
                                .setProjection(Property.forName("idTomaMx.idTomaMx"))))
                        .setProjection(Property.forName("hojaTrabajo.numero"))));
            }
        }

        //nombre solicitud
        if (filtro.getNombreSolicitud() != null) {
            if (filtro.getCodTipoSolicitud() != null) {
                if (filtro.getCodTipoSolicitud().equals("Estudio")) {
                    crit.add(Subqueries.propertyIn("numero", DetachedCriteria.forClass(Mx_HojaTrabajo.class)
                            .createAlias("hojaTrabajo","hojaTrabajo")
                            .createAlias("tomaMx","tomaMx")
                            .add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                                    .createAlias("tipoEstudio", "estudio")
                                    .add(Restrictions.ilike("estudio.nombre", "%" + filtro.getNombreSolicitud() + "%"))
                                    .setProjection(Property.forName("idTomaMx.idTomaMx"))))
                            .setProjection(Property.forName("hojaTrabajo.numero"))));
                } else {
                    crit.add(Subqueries.propertyIn("numero", DetachedCriteria.forClass(Mx_HojaTrabajo.class)
                            .createAlias("hojaTrabajo","hojaTrabajo")
                            .createAlias("tomaMx","tomaMx")
                            .add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                                    .createAlias("codDx", "dx")
                                    .add(Restrictions.ilike("dx.nombre", "%" + filtro.getNombreSolicitud() + "%"))
                                    .setProjection(Property.forName("idTomaMx.idTomaMx"))))
                            .setProjection(Property.forName("hojaTrabajo.numero"))));
                }
            } else {

                Junction conditGroup = Restrictions.disjunction();
                conditGroup.add(Subqueries.propertyIn("tomaMx.idTomaMx", DetachedCriteria.forClass(DaSolicitudEstudio.class)
                        .createAlias("tipoEstudio", "estudio")
                        .add(Restrictions.ilike("estudio.nombre", "%" + filtro.getNombreSolicitud() + "%"))
                        .setProjection(Property.forName("idTomaMx.idTomaMx"))))
                        .add(Subqueries.propertyIn("idTomaMx", DetachedCriteria.forClass(DaSolicitudDx.class)
                                .createAlias("codDx", "dx")
                                .add(Restrictions.ilike("dx.nombre", "%" + filtro.getNombreSolicitud() + "%"))
                                .setProjection(Property.forName("idTomaMx.idTomaMx"))));
                crit.add(Subqueries.propertyIn("numero", DetachedCriteria.forClass(Mx_HojaTrabajo.class)
                        .createAlias("hojaTrabajo","hojaTrabajo")
                        .createAlias("tomaMx","tomaMx")
                        .add(conditGroup)
                        .setProjection(Property.forName("hojaTrabajo.numero"))));
            }
        }
        //se filtra que usuario tenga autorizado laboratorio al que se envio la muestra desde ALERTA
        if (filtro.getNombreUsuario()!=null) {
            crit.add(Subqueries.propertyIn("numero", DetachedCriteria.forClass(Mx_HojaTrabajo.class)
                    .createAlias("hojaTrabajo","hojaTrabajo")
                    .createAlias("tomaMx","tomaMx")
                    .createAlias("tomaMx.envio","envioMx")
                    .add(Subqueries.propertyIn("envioMx.laboratorioDestino.codigo", DetachedCriteria.forClass(AutoridadLaboratorio.class)
                            .createAlias("laboratorio", "labautorizado")
                            .createAlias("user", "usuario")
                            .add(Restrictions.eq("pasivo",false)) //autoridad laboratorio activa
                            .add(Restrictions.and(Restrictions.eq("usuario.username",filtro.getNombreUsuario()))) //usuario
                            .setProjection(Property.forName("labautorizado.codigo"))))
                    .setProjection(Property.forName("hojaTrabajo.numero"))));

        }

        return crit.list();
    }
}
