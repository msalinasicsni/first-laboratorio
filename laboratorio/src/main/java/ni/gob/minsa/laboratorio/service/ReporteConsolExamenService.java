package ni.gob.minsa.laboratorio.service;

import com.google.common.base.Predicate;
import ni.gob.minsa.laboratorio.utilities.FiltrosReporte;
import ni.gob.minsa.laboratorio.utilities.reportes.ConsolidadoExamen;
import ni.gob.minsa.laboratorio.utilities.reportes.ConsolidadoExamenRespuesta;
import ni.gob.minsa.laboratorio.utilities.reportes.FilterLists;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Miguel Salinas on 3/22/2018.
 * V1.0
 */
@Service("reporteConsolExamenService")
@Transactional
public class ReporteConsolExamenService {
    private Logger logger = LoggerFactory.getLogger(ReporteConsolExamenService.class);

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;
    @Resource(name = "respuestasExamenService")
    private RespuestasExamenService respuestasExamenService;

    private static final String sqlLab = " and dx.labProcesa.codigo = :codigoLab ";
    /**
     * M?todo que retornar la informaci?n para generar reporte y gr?fico de notificaciones por tipo de resultado (positivo, negativo, sin resultado y % positividad)
     * @param filtro indicando el nivel (pais, silais, departamento, municipio, unidad salud), tipo notificaci?n, rango de fechas, factor tasas de poblaci?n
     * @return Lista de objetos a mostrar
     */
    @SuppressWarnings("unchecked")
    public List<ConsolidadoExamen> getDataDxResultReport(FiltrosReporte filtro) {
        // Retrieve session from Hibernate
        List<ConsolidadoExamen> consolidadoList = new ArrayList<ConsolidadoExamen>();
        List<ConsolidadoExamenRespuesta> consolidadoRespuestasList = new ArrayList<ConsolidadoExamenRespuesta>();
        List<Object[]> respuestas = new ArrayList<Object[]>();
        List<Object[]> examenes = new ArrayList<Object[]>();

        Session session = sessionFactory.getCurrentSession();
        Query queryExamenes = null;
        Query queryRespuestaExamenes = null;
        //se sacan todas las ordenes de examen para los filtros indicados
        queryExamenes = session.createQuery(" select dx.codDx.idDiagnostico, oe.codExamen.idExamen, noti.codSilaisAtencion.codigo, cal.noSemana, " +
                "cal.noMes, oe.idOrdenExamen " +
                "from DaNotificacion  noti, DaTomaMx mx, DaSolicitudDx  dx, OrdenExamen oe, CalendarioEpi cal "+
                "where noti.idNotificacion = mx.idNotificacion and mx.idTomaMx = dx.idTomaMx and dx.idSolicitudDx = oe.solicitudDx " +
                "and noti.pasivo = false and mx.anulada = false " +
                (!filtro.getCodLaboratio().equalsIgnoreCase("ALL")?sqlLab:"") +
                "and dx.anulado = false and dx.aprobada = true " +
                "and dx.controlCalidad = false and oe.anulado = false " +
                "and noti.fechaInicioSintomas between cal.fechaInicial and cal.fechaFinal " +
                //"and oe.idOrdenExamen in (select dr.examen.idOrdenExamen from DetalleResultado dr where dr.fechahProcesa between cal.fechaInicial and cal.fechaFinal) " +
                "and cal.anio = :anio and dx.codDx.idDiagnostico in ("+filtro.getDiagnosticos()+") " +
                "and cal.noSemana between :semI and :semF " +
                //" and noti.codSilaisAtencion.nombre = 'SILAIS RAAS' "+
                "order by dx.codDx.idDiagnostico, oe.codExamen.idExamen, noti.codSilaisAtencion.codigo, cal.noSemana");

        //se sacan todas las respuesta(un examen puede tener n respuestas) para cada orden de examen según los filtros indicados
        queryRespuestaExamenes = session.createQuery(" select oe.idOrdenExamen, " +
                "re.idRespuesta, c.idConcepto, c.tipo.codigo, (select cl.valor from Catalogo_Lista cl " +
                "where cl.idCatalogoLista = cast(dr.valor as integer ) and cl.idConcepto.idConcepto = c.idConcepto and tp.codigo = 'TPDATO|LIST'), dr.valor  " +
                "from DaNotificacion  noti, DaTomaMx mx, DaSolicitudDx  dx, OrdenExamen oe , DetalleResultado dr, RespuestaExamen re, Concepto c, TipoDatoCatalogo tp, CalendarioEpi cal "+
                "where noti.idNotificacion = mx.idNotificacion and mx.idTomaMx = dx.idTomaMx and dx.idSolicitudDx = oe.solicitudDx " +
                "and oe.idOrdenExamen = dr.examen and re.idRespuesta = dr.respuesta.idRespuesta and c.idConcepto = re.concepto.idConcepto and c.tipo.catalogoId = tp.catalogoId " +
                "and noti.pasivo = false and mx.anulada = false " +
                (!filtro.getCodLaboratio().equalsIgnoreCase("ALL")?sqlLab:"") +
                "and dx.anulado = false and dx.aprobada = true " +
                "and dx.controlCalidad = false and oe.anulado = false and dr.pasivo = false " +
                "and noti.fechaInicioSintomas between cal.fechaInicial and cal.fechaFinal " +
                //"and dr.fechahProcesa between cal.fechaInicial and cal.fechaFinal " +
                //" and noti.codSilaisAtencion.nombre = 'SILAIS RAAS' "+
                "and cal.anio = :anio and dx.codDx.idDiagnostico in ("+filtro.getDiagnosticos()+") " +
                "and cal.noSemana between :semI and :semF ");

        queryExamenes.setParameter("semI", filtro.getSemInicial());
        queryExamenes.setParameter("semF", filtro.getSemFinal());
        queryExamenes.setParameter("anio", Integer.valueOf(filtro.getAnioInicial()));

        queryRespuestaExamenes.setParameter("semI", filtro.getSemInicial());
        queryRespuestaExamenes.setParameter("semF", filtro.getSemFinal());
        queryRespuestaExamenes.setParameter("anio", Integer.valueOf(filtro.getAnioInicial()));

        if (!filtro.getCodLaboratio().equalsIgnoreCase("ALL")) {
            queryExamenes.setParameter("codigoLab", filtro.getCodLaboratio());
            queryRespuestaExamenes.setParameter("codigoLab", filtro.getCodLaboratio());
        }

        respuestas.addAll(queryRespuestaExamenes.list());
        examenes.addAll(queryExamenes.list());

        //todas las respuestas se agregan a una lista
        for (Object[] tmp : respuestas) {
            if (tmp[2] != null) {
                String valor = "";
                if (tmp[3].toString().equalsIgnoreCase("TPDATO|LIST")){
                    valor = tmp[4].toString();
                }else{
                    valor = tmp[5].toString();
                }
                //idOrdenExamen, idRespuesta, idConcepto, tipoConcepto, valor
                ConsolidadoExamenRespuesta dat = new ConsolidadoExamenRespuesta(tmp[0].toString(), (Integer)tmp[1], (Integer)tmp[2], tmp[3].toString(), valor);
                consolidadoRespuestasList.add(dat);
            }
        }
        //todos los examenes se agregan a una lista
        for (Object[] tmp : examenes) {
            if (tmp[2] != null) {
                //idDiagnostico, idExamen, codigoSilais, noSemana, noMes, idOrdenExamen
                final ConsolidadoExamen dat = new ConsolidadoExamen((Integer) tmp[0], (Integer) tmp[1], (Long) tmp[2], (Integer) tmp[3], (Integer) tmp[4], tmp[5].toString());

                //para cada examen, filtrar sus respuestas positivas
                Predicate<ConsolidadoExamenRespuesta> byIdOrdenExamen = new Predicate<ConsolidadoExamenRespuesta>() {
                    @Override
                    public boolean apply(ConsolidadoExamenRespuesta examenRespuesta) {
                        return examenRespuesta.getIdOrdenExamen().equalsIgnoreCase(dat.getIdOrdenExamen()) && examenRespuesta.getValor().toLowerCase().contains("positivo");
                    }
                };
                //si hay mas de un elemento como resultado del filtro, significa que el examen es positivo
                Collection<ConsolidadoExamenRespuesta> resExamen = FilterLists.filter(consolidadoRespuestasList, byIdOrdenExamen);
                if (resExamen.size()>0) dat.setResultado("positivo");
                else dat.setResultado("");
                consolidadoList.add(dat);
            }
        }

        return consolidadoList;
    }


}
