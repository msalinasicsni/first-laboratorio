package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.muestra.FiltroMx;
import ni.gob.minsa.laboratorio.domain.muestra.RecepcionMx;
import org.apache.commons.codec.language.Soundex;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by FIRSTICT on 12/10/2014.
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

    public RecepcionMx getRecepcionMxByCodUnicoMx(String codigoUnicoMx){
        String query = "from RecepcionMx as a where tomaMx.codigoUnicoMx= :codigoUnicoMx";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setString("codigoUnicoMx", codigoUnicoMx);
        return  (RecepcionMx)q.uniqueResult();
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
            crit.add( Restrictions.and(
                            Restrictions.eq("tomaMx.codigoUnicoMx", filtro.getCodigoUnicoMx()))
            );
        }

        return crit.list();
    }

}
