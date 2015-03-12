package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.muestra.DaSolicitudDx;
import ni.gob.minsa.laboratorio.domain.muestra.FiltroMx;
import ni.gob.minsa.laboratorio.domain.muestra.OrdenExamen;
import ni.gob.minsa.laboratorio.domain.muestra.RecepcionMx;
import ni.gob.minsa.laboratorio.domain.resultados.Catalogo_Lista;
import ni.gob.minsa.laboratorio.domain.resultados.DetalleResultado;
import ni.gob.minsa.laboratorio.domain.resultados.DetalleResultadoFinal;
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
        );//y las ordenes en estado según filtro
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

        //Se filtra por rango de fecha de recepción
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

        if(filtro.getCodigoUnicoMx()!=null){
            crit.add( Restrictions.and(
                            Restrictions.eq("tomaMx.codigoUnicoMx", filtro.getCodigoUnicoMx()))
            );
        }

        crit.add(Subqueries.propertyIn("idSolicitudDx", DetachedCriteria.forClass(DetalleResultado.class)
                .createAlias("examen", "examen").add(Restrictions.eq("pasivo", false))
                .setProjection(Property.forName("examen.solicitudDx.idSolicitudDx"))));

        return crit.list();
    }

    @SuppressWarnings("unchecked")
    public List<OrdenExamen> getOrdenExaBySolicitud(String idSolicitud){
        Session session = sessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(OrdenExamen.class, "orden");
        crit.createAlias("orden.solicitudDx","solicitud");

        crit.add( Restrictions.and(
                        Restrictions.eq("solicitud.idSolicitudDx", idSolicitud))
        );

        crit.add(Subqueries.propertyIn("idOrdenExamen", DetachedCriteria.forClass(DetalleResultado.class)
                .createAlias("examen", "examen")
                .setProjection(Property.forName("examen.idOrdenExamen"))));

        return crit.list();
    }

    public List<DetalleResultado> getResultDetailExaByIdOrden(String idOrdenExa){
        String query = "from DetalleResultado where examen.idOrdenExamen = :idOrdenExa ORDER BY fechahRegistro ";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idOrdenExa",idOrdenExa);
        return q.list();
    }

    /**
     * Obtiene un catalogo lista según el id indicado
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

    public List<DetalleResultadoFinal> getDetResActivosBySolicitud(String idSolicitud){
        List<DetalleResultadoFinal> resultadoFinals = new ArrayList<DetalleResultadoFinal>();
        Session session = sessionFactory.getCurrentSession();
        String query = "select a from DetalleResultadoFinal as a inner join a.solicitudDx as r where a.pasivo = false and r.idSolicitudDx = :idSolicitud ";
        Query q = session.createQuery(query);
        q.setParameter("idSolicitud", idSolicitud);
        resultadoFinals = q.list();
        String query2 = "select a from DetalleResultadoFinal as a inner join a.solicitudEstudio as r where a.pasivo = false and r.idSolicitudEstudio = :idSolicitud ";
        Query q2 = session.createQuery(query2);
        q2.setParameter("idSolicitud", idSolicitud);
        resultadoFinals.addAll(q2.list());
        return  resultadoFinals;
    }

    /**
     * Verifica si existe registrado un resultado para la respuesta y dx indicado, siempre y cuando el registro este activo
     * @param idSolicitud solicitud a verificar
     * @param idRespuesta respuesta a verificar
     * @return DetalleResultadoFinal
     */
    public DetalleResultadoFinal getDetResBySolicitudAndRespuesta(String idSolicitud, int idRespuesta){
        String query = "Select a from DetalleResultadoFinal as a inner join a.solicitudDx as ex inner join a.respuesta as re " +
                "where ex.idSolicitudDx = :idSolicitud and re.idRespuesta = :idRespuesta and a.pasivo = false ";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idSolicitud", idSolicitud);
        q.setParameter("idRespuesta", idRespuesta);
        return  (DetalleResultadoFinal)q.uniqueResult();
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



}
