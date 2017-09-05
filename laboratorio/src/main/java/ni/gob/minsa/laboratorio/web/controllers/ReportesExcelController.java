package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.catalogos.AreaRep;
import ni.gob.minsa.laboratorio.domain.concepto.Catalogo_Lista;
import ni.gob.minsa.laboratorio.domain.estructura.CalendarioEpi;
import ni.gob.minsa.laboratorio.domain.estructura.EntidadesAdtvas;
import ni.gob.minsa.laboratorio.domain.estructura.Unidades;
import ni.gob.minsa.laboratorio.domain.examen.Departamento;
import ni.gob.minsa.laboratorio.domain.muestra.*;
import ni.gob.minsa.laboratorio.domain.parametros.Parametro;
import ni.gob.minsa.laboratorio.domain.resultados.DetalleResultado;
import ni.gob.minsa.laboratorio.domain.resultados.DetalleResultadoFinal;
import ni.gob.minsa.laboratorio.domain.vigilanciaSindFebril.DaSindFebril;
import ni.gob.minsa.laboratorio.service.*;
import ni.gob.minsa.laboratorio.utilities.ConstantsSecurity;
import ni.gob.minsa.laboratorio.utilities.DateUtil;
import ni.gob.minsa.laboratorio.utilities.Email.Attachment;
import ni.gob.minsa.laboratorio.utilities.Email.EmailUtil;
import ni.gob.minsa.laboratorio.utilities.Email.SessionData;
import ni.gob.minsa.laboratorio.utilities.FiltrosReporte;
import ni.gob.minsa.laboratorio.utilities.excelUtils.ExcelBuilder;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.activation.DataSource;
import javax.annotation.Resource;
import javax.mail.Session;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.util.*;

/**
 * Created by Miguel Salinas on 8/30/2017.
 * V1.0
 */
@Controller
@RequestMapping("reports")
public class ReportesExcelController {

    private static final Logger logger = LoggerFactory.getLogger(ReportesExcelController.class);
    @Resource(name = "seguridadService")
    private SeguridadService seguridadService;

    @Resource(name = "catalogosService")
    private CatalogoService catalogosService;

    @Resource(name = "entidadAdmonService")
    private EntidadAdmonService entidadAdmonService;

    @Resource(name = "tomaMxService")
    private TomaMxService tomaMxService;

    @Resource(name = "recepcionMxService")
    private RecepcionMxService recepcionMxService;

    @Resource(name = "sindFebrilService")
    private SindFebrilService sindFebrilService;

    @Resource(name = "calendarioEpiService")
    private CalendarioEpiService calendarioEpiService;

    @Resource(name = "organizationChartService")
    private OrganizationChartService organizationChartService;

    @Resource(name = "reportesService")
    private ReportesService reportesService;

    @Resource(name = "resultadoFinalService")
    private ResultadoFinalService resultadoFinalService;

    @Resource(name = "ordenExamenMxService")
    private OrdenExamenMxService ordenExamenMxService;

    @Resource(name = "resultadosService")
    private ResultadosService resultadosService;

    @Resource(name = "associationSR")
    private AssociationSamplesRequestService associationSamplesRequestService;

    @Resource(name = "unidadesService")
    private UnidadesService unidadesService;

    @Resource(name = "parametrosService")
    private ParametrosService parametrosService;

    @Autowired
    MessageSource messageSource;

    /*******************************************************************/
    /************************ REPORTE POR RESULTADO DX PARA VIGILANCIA ***********************/
    /*******************************************************************/

    @RequestMapping(value = "reportResultDxVig/init", method = RequestMethod.GET)
    public String initReportResultDxVig(Model model,HttpServletRequest request) throws Exception {
        logger.debug("Reporte por Resultado dx enviado a vigilancia");
        String urlValidacion="";
        try {
            urlValidacion = seguridadService.validarLogin(request);
            //si la url esta vacia significa que la validación del login fue exitosa
            if (urlValidacion.isEmpty())
                urlValidacion = seguridadService.validarAutorizacionUsuario(request, ConstantsSecurity.SYSTEM_CODE, false);
        }catch (Exception e){
            e.printStackTrace();
            urlValidacion = "404";
        }
        if (urlValidacion.isEmpty()) {
            long idUsuario = seguridadService.obtenerIdUsuario(request);
            List<EntidadesAdtvas> entidades = new ArrayList<EntidadesAdtvas>();
            if (seguridadService.esUsuarioNivelCentral(idUsuario, ConstantsSecurity.SYSTEM_CODE)){
                entidades = entidadAdmonService.getAllEntidadesAdtvas();
            }else {
                entidades = seguridadService.obtenerEntidadesPorUsuario((int) idUsuario, ConstantsSecurity.SYSTEM_CODE);
            }
            List<AreaRep> areas = new ArrayList<AreaRep>();
            areas.add(catalogosService.getAreaRep("AREAREP|PAIS"));
            areas.add(catalogosService.getAreaRep("AREAREP|SILAIS"));
            areas.add(catalogosService.getAreaRep("AREAREP|UNI"));
            List<Catalogo_Dx> catDx = associationSamplesRequestService.getDxs();
            model.addAttribute("areas", areas);
            model.addAttribute("entidades", entidades);
            model.addAttribute("dxs", catDx);
            return "reportes/resultadoDxVig";
        }else{
            return  urlValidacion;
        }
    }

    /**
     * Método para obtener data para Reporte por Resultado dx
     * @param filtro JSon con los datos de los filtros a aplicar en la búsqueda
     * @return Object
     * @throws Exception
     */
    @RequestMapping(value = "reportResultDxVigMail", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String fetchReportResultDxVigEmail(@RequestParam(value = "filtro", required = true) String filtro) throws Exception{
        logger.info("Obteniendo los datos para Reporte por Resultado ");

        try {
            FiltrosReporte filtroRep = jsonToFiltroReportes(filtro);
            Laboratorio labUser = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
            /************ARMANDO MODELO PARA GENERAR EXCEL************/
            Map<String, Object> model = new HashMap<String, Object>();
            logger.info("Obteniendo los datos para Reporte por Resultado dx vigilancia ");
            List<DaSolicitudDx> dxList = reportesService.getDiagnosticosAprobadosByFiltro(filtroRep, labUser.getCodigo());
            List<Object[]> registrosPos = new ArrayList<Object[]>();
            List<Object[]> registrosNeg = new ArrayList<Object[]>();
            List<Object[]> registrosMxInadec = new ArrayList<Object[]>();
            List<String> columnas = new ArrayList<String>();
            Catalogo_Dx dx = tomaMxService.getDxById(filtroRep.getIdDx().toString());


            String tipoReporte = "";
            if (dx.getNombre().toLowerCase().contains("dengue")){
                tipoReporte = "DENGUE";
                setNombreColumnasDengue(columnas);
                setDatosDengue(dxList, registrosPos, registrosNeg, labUser.getCodigo(), filtroRep.isIncluirMxInadecuadas(), registrosMxInadec);
            }else if (dx.getNombre().toLowerCase().contains("chikun")){
                tipoReporte = "CHIK";
                setNombreColumnasChik(columnas);
                setDatosChikungunya(dxList, registrosPos, registrosNeg, filtroRep.isIncluirMxInadecuadas(), registrosMxInadec);
            }else if (dx.getNombre().toLowerCase().contains("zika")){
                tipoReporte = "ZIKA";
                setNombreColumnasZika(columnas);
                setDatosZika(dxList, registrosPos, registrosNeg, labUser.getCodigo(), filtroRep.isIncluirMxInadecuadas(), registrosMxInadec);
            }else{
                tipoReporte = dx.getNombre().replace(" ","_");
                setNombreColumnasDefecto(columnas);
                setDatosDefecto(dxList, registrosPos, registrosNeg, filtroRep.isIncluirMxInadecuadas(), registrosMxInadec);
            }

            Departamento departamento = organizationChartService.getDepartamentoAreaByLab(labUser.getCodigo(), dx.getArea().getIdArea());
            model.put("titulo", messageSource.getMessage("lbl.minsa", null, null) + " - " + labUser.getDescripcion());
            model.put("subtitulo", departamento.getNombre().toUpperCase() + "/" + dx.getNombre().toUpperCase());

            model.put("tablaPos", String.format(messageSource.getMessage("lbl.excel.filter", null, null),
                    messageSource.getMessage("lbl.positives", null, null),
                    DateUtil.DateToString(filtroRep.getFechaInicio(), "dd/MM/yyyy"),
                    DateUtil.DateToString(filtroRep.getFechaFin(), "dd/MM/yyyy")));

            model.put("tablaNeg", String.format(messageSource.getMessage("lbl.excel.filter", null, null),
                    messageSource.getMessage("lbl.negatives", null, null),
                    DateUtil.DateToString(filtroRep.getFechaInicio(), "dd/MM/yyyy"),
                    DateUtil.DateToString(filtroRep.getFechaFin(), "dd/MM/yyyy")));

            model.put("tablaMxInadec", String.format(messageSource.getMessage("lbl.excel.filter.mx.inadec", null, null),
                    DateUtil.DateToString(filtroRep.getFechaInicio(), "dd/MM/yyyy"),
                    DateUtil.DateToString(filtroRep.getFechaFin(), "dd/MM/yyyy")));

            model.put("columnas", columnas);
            model.put("tipoReporte", tipoReporte);

            model.put("listaDxPos", registrosPos);
            model.put("listaDxNeg", registrosNeg);
            model.put("listaDxInadec", registrosMxInadec);
            model.put("incluirMxInadecuadas", filtroRep.isIncluirMxInadecuadas());
            model.put("sinDatos", messageSource.getMessage("lbl.nothing.to.show", null, null));
               /***********/
            /*GENERAR EXCEL*/
            ExcelBuilder builder = new ExcelBuilder();
            HSSFWorkbook workbook = builder.buildExcel(model);
            /*CONSTRUIR CORREO*/
            //Destinatario
            String toEmail = "";
            Parametro parametro = parametrosService.getParametroByName("EMAIL_DEST_RESDX");
            if (parametro!=null)
                toEmail = parametro.getValor(); // can be any email id
            //asunto
            String subject = messageSource.getMessage("lbl.simlab", null, null)+ " - " + messageSource.getMessage("lbl.report", null, null) + " " + messageSource.getMessage("lbl.results", null, null)+ " " + dx.getNombre();

            //cuerpo
            String body = messageSource.getMessage("mail.body.resultDx", null, null);

            AreaRep area = catalogosService.getAreaRep(filtroRep.getCodArea());
            String entidad = "";
            String desde = messageSource.getMessage("lbl.from", null, null) + DateUtil.DateToString(filtroRep.getFechaInicio(), "dd/MM/yyyy");
            String hasta = messageSource.getMessage("lbl.to", null, null) + DateUtil.DateToString(filtroRep.getFechaFin(), "dd/MM/yyyy");
            if (filtroRep.getCodArea().equalsIgnoreCase("AREAREP|PAIS")) {
                entidad = messageSource.getMessage("lbl.nic.rep", null, null);
            } else if (filtroRep.getCodArea().equalsIgnoreCase("AREAREP|SILAIS")) {
                EntidadesAdtvas entidadesAdtva = entidadAdmonService.getSilaisById(filtroRep.getCodSilais());
                if (entidadesAdtva != null)
                    entidad = messageSource.getMessage("lbl.silais1", null, null) + " " + entidadesAdtva.getNombre();
            }
            if (filtroRep.getCodArea().equalsIgnoreCase("AREAREP|UNI")) {
                Unidades unidad = unidadesService.getUnidadById(filtroRep.getCodUnidad());
                if (unidad != null)
                    entidad = messageSource.getMessage("lbl.health.unit1", null, null) + " " + unidad.getNombre();
            }

            body = String.format(body, area.getValor(), entidad, desde, hasta);
            //adjunto
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos); // write excel data to a byte array
            bos.close();
            DataSource fds = new ByteArrayDataSource(bos.toByteArray(), "application/vnd.ms-excel");
            Attachment attachment = new Attachment("reporteResDx_"+tipoReporte+".xls","application/vnd.ms-excel", fds);

            //enviar correo
            Session session = EmailUtil.openSession(getMailSessionData());
            EmailUtil.sendAttachmentEmail(session, toEmail, subject, body, attachment);

            return new Gson().toJson("OK");
        }catch (Exception ex){
            return new Gson().toJson(messageSource.getMessage("msg.error.sending.email",null,null)+" "+ ex.getMessage());
        }
    }

    /**
     * Abrir sessión en servidor de correo
     * @return SessionData
     */
    private SessionData getMailSessionData(){
        SessionData sessionData = new SessionData();
        Parametro parametro = parametrosService.getParametroByName("EMAIL_USER");
        if (parametro!=null)
            sessionData.setFromEmail(parametro.getValor());

        parametro = parametrosService.getParametroByName("EMAIL_USER_PASS");
        if (parametro!=null)
            sessionData.setPassword(parametro.getValor());

        parametro = parametrosService.getParametroByName("SMTP_SERVER");
        if (parametro!=null)
            sessionData.setSmtpHost(parametro.getValor());

        parametro = parametrosService.getParametroByName("SMTP_PORT");
        if (parametro!=null)
            sessionData.setSmtpPort(parametro.getValor());

        parametro = parametrosService.getParametroByName("SSL_PORT");
        if (parametro!=null)
            sessionData.setSslPort(parametro.getValor());

        return sessionData;
    }

    @RequestMapping(value = "/downloadExcel", method = RequestMethod.GET)
    public ModelAndView downloadExcel(@RequestParam(value = "filtro", required = true) String filtro) throws Exception{
        // create some sample data
        logger.info("Obteniendo los datos para Reporte por Resultado dx vigilancia ");
        FiltrosReporte filtroRep = jsonToFiltroReportes(filtro);
        Laboratorio labUser = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
        List<DaSolicitudDx> dxList = reportesService.getDiagnosticosAprobadosByFiltro(filtroRep, labUser.getCodigo());
        List<Object[]> registrosPos = new ArrayList<Object[]>();
        List<Object[]> registrosNeg = new ArrayList<Object[]>();
        List<Object[]> registrosMxInadec = new ArrayList<Object[]>();
        List<String> columnas = new ArrayList<String>();
        Catalogo_Dx dx = tomaMxService.getDxById(filtroRep.getIdDx().toString());


        ModelAndView excelView = new ModelAndView("excelView");
        String tipoReporte = "";
        if (dx.getNombre().toLowerCase().contains("dengue")){
            tipoReporte = "DENGUE";
            setNombreColumnasDengue(columnas);
            setDatosDengue(dxList, registrosPos, registrosNeg, labUser.getCodigo(), filtroRep.isIncluirMxInadecuadas(), registrosMxInadec);
        }else if (dx.getNombre().toLowerCase().contains("chikun")){
            tipoReporte = "CHIK";
            setNombreColumnasChik(columnas);
            setDatosChikungunya(dxList, registrosPos, registrosNeg, filtroRep.isIncluirMxInadecuadas(), registrosMxInadec);
        }else if (dx.getNombre().toLowerCase().contains("zika")){
            tipoReporte = "ZIKA";
            setNombreColumnasZika(columnas);
            setDatosZika(dxList, registrosPos, registrosNeg, labUser.getCodigo(), filtroRep.isIncluirMxInadecuadas(), registrosMxInadec);
        }else{
            tipoReporte = dx.getNombre().replace(" ","_");
            setNombreColumnasDefecto(columnas);
            setDatosDefecto(dxList, registrosPos, registrosNeg, filtroRep.isIncluirMxInadecuadas(), registrosMxInadec);
        }

        Departamento departamento = organizationChartService.getDepartamentoAreaByLab(labUser.getCodigo(), dx.getArea().getIdArea());
        excelView.addObject("titulo", messageSource.getMessage("lbl.minsa", null, null)+ " - "+labUser.getDescripcion());
        excelView.addObject("subtitulo", departamento.getNombre().toUpperCase()+"/"+dx.getNombre().toUpperCase());

        excelView.addObject("tablaPos", String.format(messageSource.getMessage("lbl.excel.filter", null, null),
                messageSource.getMessage("lbl.positives", null, null),
                DateUtil.DateToString(filtroRep.getFechaInicio(), "dd/MM/yyyy"),
                DateUtil.DateToString(filtroRep.getFechaFin(), "dd/MM/yyyy")));

        excelView.addObject("tablaNeg", String.format(messageSource.getMessage("lbl.excel.filter", null, null),
                messageSource.getMessage("lbl.negatives", null, null),
                DateUtil.DateToString(filtroRep.getFechaInicio(), "dd/MM/yyyy"),
                DateUtil.DateToString(filtroRep.getFechaFin(), "dd/MM/yyyy")));

        excelView.addObject("tablaMxInadec", String.format(messageSource.getMessage("lbl.excel.filter.mx.inadec", null, null),
                DateUtil.DateToString(filtroRep.getFechaInicio(), "dd/MM/yyyy"),
                DateUtil.DateToString(filtroRep.getFechaFin(), "dd/MM/yyyy")));

        excelView.addObject("columnas", columnas);
        excelView.addObject("tipoReporte", tipoReporte);

        excelView.addObject("listaDxPos", registrosPos);
        excelView.addObject("listaDxNeg", registrosNeg);
        excelView.addObject("listaDxInadec", registrosMxInadec);
        excelView.addObject("incluirMxInadecuadas", filtroRep.isIncluirMxInadecuadas());
        excelView.addObject("sinDatos", messageSource.getMessage("lbl.nothing.to.show",null,null));
        return excelView;
    }

    private void setNombreColumnasDengue(List<String> columnas){
        columnas.add(messageSource.getMessage("lbl.num", null, null));
        columnas.add(messageSource.getMessage("lbl.lab.code.mx", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.names", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.lastnames", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.age", null, null).toUpperCase().replace(":",""));
        columnas.add(messageSource.getMessage("lbl.age.um", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.silais", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.muni", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.health.unit.excel", null, null));
        columnas.add(messageSource.getMessage("lbl.parents.names", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.address", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.fis.short", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.ftm", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.reception.date", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.dengue.igm.date", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.result.pcr", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.serotype", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.pcr.date", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.week", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.igm.dengue", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.res.final", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.res.final.date", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("person.mun.res", null, null).toUpperCase().replace(" ", "_"));
        columnas.add(messageSource.getMessage("lbl.fill.date", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("person.fecnac", null, null).toUpperCase().replace(" ", "_"));
        columnas.add(messageSource.getMessage("person.sexo", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.provenance", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.pregnant", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.time.pregnancy", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.hosp", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.date.admission", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.deceased", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.date.deceased", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.clinical.dx", null, null));
    }

    private void setNombreColumnasChik(List<String> columnas){
        columnas.add(messageSource.getMessage("lbl.num", null, null));
        columnas.add(messageSource.getMessage("lbl.lab.code.mx", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.names", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.lastnames", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.age", null, null).toUpperCase().replace(":",""));
        columnas.add(messageSource.getMessage("lbl.age.um", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.address", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.silais", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.muni", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.health.unit.excel", null, null));
        columnas.add(messageSource.getMessage("person.sexo", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.fis.short", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.ftm", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.result.pcr", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.igm", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.res.final", null, null).toUpperCase());
    }

    private void setNombreColumnasZika(List<String> columnas){
        columnas.add(messageSource.getMessage("lbl.num", null, null));
        columnas.add(messageSource.getMessage("lbl.lab.code.mx", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.names", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.lastnames", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.age", null, null).toUpperCase().replace(":", ""));
        columnas.add(messageSource.getMessage("lbl.age.um", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.silais", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.muni", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.health.unit.excel", null, null));
        columnas.add(messageSource.getMessage("lbl.address", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.fis.short", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.ftm", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.reception.date", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.res.final", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.res.final.date", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.pregnant", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.SILAIS.res", null, null).toUpperCase().replace(" ", "_"));
        columnas.add(messageSource.getMessage("lbl.ctzica", null, null));
        columnas.add(messageSource.getMessage("person.sexo", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.week", null, null).toUpperCase());
    }

    private void setNombreColumnasDefecto(List<String> columnas){
        columnas.add(messageSource.getMessage("lbl.num", null, null));
        columnas.add(messageSource.getMessage("lbl.lab.code.mx", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.names", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.lastnames", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.age", null, null).toUpperCase().replace(":",""));
        columnas.add(messageSource.getMessage("lbl.age.um", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.address", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.silais", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.muni", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.health.unit.excel", null, null));
        columnas.add(messageSource.getMessage("person.sexo", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.fis.short", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.ftm", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.result.pcr", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.igm", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.res.final", null, null).toUpperCase());
    }

    private void setDatosDengue(List<DaSolicitudDx> dxList, List<Object[]> registrosPos, List<Object[]> registrosNeg, String codigoLab, boolean incluirMxInadecuadas, List<Object[]> registrosMxInadec) throws Exception{
// create data rows
        int rowCountPos = 1;
        int rowCountNeg = 1;
        int rowCountInadec = 1;
        for (DaSolicitudDx solicitudDx : dxList) {
            String nombres = "";
            String apellidos = "";

            DaSindFebril sindFebril = sindFebrilService.getDaSindFebril(solicitudDx.getIdTomaMx().getIdNotificacion().getIdNotificacion());
            Object[] registro = new Object[34];
            //registro[0]= rowCount;
            registro[1] = solicitudDx.getIdTomaMx().getCodigoLab();

            nombres = solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getPrimerNombre();
            if (solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre()!=null)
                nombres += " "+solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre();
            registro[2] = nombres;

            apellidos = solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getPrimerApellido();
            if (solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido()!=null)
                apellidos += " "+solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido();
            registro[3] = apellidos;

            Integer edad = null;
            String medidaEdad = "";
            String[] arrEdad = DateUtil.calcularEdad(solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getFechaNacimiento(), new Date()).split("/");
            if (arrEdad[0] != null) {
                edad = Integer.valueOf(arrEdad[0]); medidaEdad = "A";
            }else if (arrEdad[1] != null) {
                edad = Integer.valueOf(arrEdad[1]); medidaEdad = "M";
            }else if (arrEdad[2] != null) {
                edad = Integer.valueOf(arrEdad[2]); medidaEdad = "D";
            }
            registro[4] = edad;
            registro[5] = medidaEdad;
            registro[6] = (solicitudDx.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion()!=null?
                    solicitudDx.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre():"");
            registro[7] = (solicitudDx.getIdTomaMx().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getCodUnidadAtencion().getMunicipio().getNombre():"");
            registro[8] = (solicitudDx.getIdTomaMx().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getCodUnidadAtencion().getNombre():"");
            if (edad!=null && edad<18)
                registro[9] = (sindFebril!=null?sindFebril.getNombPadre():"");
            else
                registro[9] = "";
            String direccion = solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getDireccionResidencia();
            if (solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getTelefonoResidencia()!=null || solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getTelefonoMovil()!=null ){
                direccion += ". TEL. ";
                direccion+= (solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getTelefonoResidencia()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getTelefonoResidencia()+",":"");
                direccion+= (solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getTelefonoMovil()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getTelefonoMovil():"");
            }
            registro[10] = direccion;
            registro[11] = solicitudDx.getIdTomaMx().getIdNotificacion().getFechaInicioSintomas();
            registro[12] = solicitudDx.getIdTomaMx().getFechaHTomaMx();
            RecepcionMx recepcionMx = recepcionMxService.getRecepcionMxByCodUnicoMx(solicitudDx.getIdTomaMx().getCodigoUnicoMx(), codigoLab);
            if (recepcionMx!=null){
                registro[13] = recepcionMx.getFechaRecibido()!=null?recepcionMx.getFechaRecibido():recepcionMx.getFechaHoraRecepcion();
            }

            validarPCRIgMDengue(registro, solicitudDx.getIdSolicitudDx());

            CalendarioEpi calendario = null;
            if (sindFebril!=null)
                calendarioEpiService.getCalendarioEpiByFecha(DateUtil.DateToString(sindFebril.getFechaFicha(),"dd/MM/yyyy"));
            if (calendario!=null) {
                registro[18] = calendario.getNoSemana();
            }

            registro[20] = parseFinalResultDetails(solicitudDx.getIdSolicitudDx());
            registro[21] = solicitudDx.getFechaAprobacion();
            registro[22] = (solicitudDx.getIdTomaMx().getIdNotificacion().getMunicipioResidencia()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getMunicipioResidencia().getNombre():"");
            registro[23] = (sindFebril!=null?sindFebril.getFechaFicha():"");
            registro[24] = solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getFechaNacimiento();
            String sexo = solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSexo().getCodigo();
            registro[25] = sexo.substring(sexo.length()-1, sexo.length());
            registro[26] = (sindFebril!=null && sindFebril.getCodProcedencia()!=null?sindFebril.getCodProcedencia().getValor():"");
            registro[27] = (solicitudDx.getIdTomaMx().getIdNotificacion().getEmbarazada()!=null? solicitudDx.getIdTomaMx().getIdNotificacion().getEmbarazada().getValor():"");
            registro[28] = solicitudDx.getIdTomaMx().getIdNotificacion().getSemanasEmbarazo();
            registro[29] = (sindFebril!=null && sindFebril.getHosp()!=null?sindFebril.getHosp().getValor():"");
            registro[30] = (sindFebril!=null?sindFebril.getFechaIngreso():"");
            registro[31] = (sindFebril!=null && sindFebril.getFallecido()!=null?sindFebril.getFallecido().getValor():"");
            registro[32] = (sindFebril!=null?sindFebril.getFechaFallecido():"");
            if (sindFebril!=null && sindFebril.getDxPresuntivo()!=null && !sindFebril.getDxPresuntivo().isEmpty()) {
                registro[33] = sindFebril.getDxPresuntivo();
            } else {
                registro[33] = parseDxs(solicitudDx.getIdTomaMx().getIdTomaMx(), codigoLab);
            }
            if (registro[20].toString().toLowerCase().contains("positivo")) {
                registro[0]= rowCountPos++;
                registrosPos.add(registro);
            }else if (registro[20].toString().toLowerCase().contains("negativo")) {
                registro[0]= rowCountNeg++;
                registrosNeg.add(registro);
            }else if (incluirMxInadecuadas && registro[20].toString().toLowerCase().contains("inadecuada")){
                registro[0]= rowCountInadec++;
                registrosMxInadec.add(registro);
            }
        }
    }

    private void setDatosChikungunya(List<DaSolicitudDx> dxList, List<Object[]> registrosPos, List<Object[]> registrosNeg, boolean incluirMxInadecuadas, List<Object[]> registrosMxInadec) throws Exception{
// create data rows
        int rowCountPos = 1;
        int rowCountNeg = 1;
        int rowCountInadec = 1;
        for (DaSolicitudDx solicitudDx : dxList) {
            String nombres = "";
            String apellidos = "";

            Object[] registro = new Object[16];
            registro[1] = solicitudDx.getIdTomaMx().getCodigoLab();

            nombres = solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getPrimerNombre();
            if (solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre()!=null)
                nombres += " "+solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre();
            registro[2] = nombres;

            apellidos = solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getPrimerApellido();
            if (solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido()!=null)
                apellidos += " "+solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido();
            registro[3] = apellidos;

            Integer edad = null;
            String medidaEdad = "";
            String[] arrEdad = DateUtil.calcularEdad(solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getFechaNacimiento(), new Date()).split("/");
            if (arrEdad[0] != null) {
                edad = Integer.valueOf(arrEdad[0]); medidaEdad = "A";
            }else if (arrEdad[1] != null) {
                edad = Integer.valueOf(arrEdad[1]); medidaEdad = "M";
            }else if (arrEdad[2] != null) {
                edad = Integer.valueOf(arrEdad[2]); medidaEdad = "D";
            }
            registro[4] = edad;
            registro[5] = medidaEdad;
            String direccion = solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getDireccionResidencia();
            if (solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getTelefonoResidencia()!=null || solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getTelefonoMovil()!=null ){
                direccion += ". TEL. ";
                direccion+= (solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getTelefonoResidencia()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getTelefonoResidencia()+",":"");
                direccion+= (solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getTelefonoMovil()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getTelefonoMovil():"");
            }
            registro[6] = direccion;
            registro[7] = (solicitudDx.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre():"");
            registro[8] = (solicitudDx.getIdTomaMx().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getCodUnidadAtencion().getMunicipio().getNombre():"");
            registro[9] = (solicitudDx.getIdTomaMx().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getCodUnidadAtencion().getNombre():"");
            String sexo = solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSexo().getCodigo();
            registro[10] = sexo.substring(sexo.length()-1, sexo.length());
            registro[11] = solicitudDx.getIdTomaMx().getIdNotificacion().getFechaInicioSintomas();
            registro[12] = solicitudDx.getIdTomaMx().getFechaHTomaMx();
            validarPCRIgMChikun(registro, solicitudDx.getIdSolicitudDx());
            registro[15] = parseFinalResultDetails(solicitudDx.getIdSolicitudDx());
            if (registro[15].toString().toLowerCase().contains("positivo")) {
                registro[0]= rowCountPos++;
                registrosPos.add(registro);
            } else if (registro[15].toString().toLowerCase().contains("negativo")) {
                registro[0]= rowCountNeg++;
                registrosNeg.add(registro);
            } else if (incluirMxInadecuadas && registro[15].toString().toLowerCase().contains("inadecuada")){
                registro[0]= rowCountInadec++;
                registrosMxInadec.add(registro);
            }
        }
    }

    private void setDatosZika(List<DaSolicitudDx> dxList, List<Object[]> registrosPos, List<Object[]> registrosNeg, String codigoLab, boolean incluirMxInadecuadas, List<Object[]> registrosMxInadec) throws Exception{
// create data rows
        int rowCountPos = 1;
        int rowCountNeg = 1;
        int rowCountInadec = 1;
        for (DaSolicitudDx solicitudDx : dxList) {
            String nombres = "";
            String apellidos = "";

            DaSindFebril sindFebril = sindFebrilService.getDaSindFebril(solicitudDx.getIdTomaMx().getIdNotificacion().getIdNotificacion());
            Object[] registro = new Object[20];
            registro[1] = solicitudDx.getIdTomaMx().getCodigoLab();

            nombres = solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getPrimerNombre();
            if (solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre()!=null)
                nombres += " "+solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre();
            registro[2] = nombres;

            apellidos = solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getPrimerApellido();
            if (solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido()!=null)
                apellidos += " "+solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido();
            registro[3] = apellidos;

            Integer edad = null;
            String medidaEdad = "";
            String[] arrEdad = DateUtil.calcularEdad(solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getFechaNacimiento(), new Date()).split("/");
            if (arrEdad[0] != null) {
                edad = Integer.valueOf(arrEdad[0]); medidaEdad = "A";
            }else if (arrEdad[1] != null) {
                edad = Integer.valueOf(arrEdad[1]); medidaEdad = "M";
            }else if (arrEdad[2] != null) {
                edad = Integer.valueOf(arrEdad[2]); medidaEdad = "D";
            }
            registro[4] = edad;
            registro[5] = medidaEdad;
            registro[6] = (solicitudDx.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre():"");
            registro[7] = (solicitudDx.getIdTomaMx().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getCodUnidadAtencion().getMunicipio().getNombre():"");
            registro[8] = (solicitudDx.getIdTomaMx().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getCodUnidadAtencion().getNombre():"");
            String direccion = solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getDireccionResidencia();
            if (solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getTelefonoResidencia()!=null || solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getTelefonoMovil()!=null ){
                direccion += ". TEL. ";
                direccion+= (solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getTelefonoResidencia()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getTelefonoResidencia()+",":"");
                direccion+= (solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getTelefonoMovil()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getTelefonoMovil():"");
            }
            registro[9] = direccion;
            registro[10] = solicitudDx.getIdTomaMx().getIdNotificacion().getFechaInicioSintomas();
            registro[11] = solicitudDx.getIdTomaMx().getFechaHTomaMx();
            RecepcionMx recepcionMx = recepcionMxService.getRecepcionMxByCodUnicoMx(solicitudDx.getIdTomaMx().getCodigoUnicoMx(), codigoLab);
            if (recepcionMx!=null){
                registro[12] = recepcionMx.getFechaRecibido()!=null?recepcionMx.getFechaRecibido():recepcionMx.getFechaHoraRecepcion();
            }
            registro[13] = parseFinalResultDetails(solicitudDx.getIdSolicitudDx());
            registro[14] = solicitudDx.getFechaAprobacion();
            registro[15] = (solicitudDx.getIdTomaMx().getIdNotificacion().getEmbarazada()!=null? solicitudDx.getIdTomaMx().getIdNotificacion().getEmbarazada().getValor():"");
            registro[16] = (solicitudDx.getIdTomaMx().getIdNotificacion().getMunicipioResidencia()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getMunicipioResidencia().getDependenciaSilais().getNombre():"");
            registro[17] = "";
            String sexo = solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSexo().getCodigo();
            registro[18] = sexo.substring(sexo.length() - 1, sexo.length());
            CalendarioEpi calendario = null;
            if (sindFebril!=null)
                calendarioEpiService.getCalendarioEpiByFecha(DateUtil.DateToString(sindFebril.getFechaFicha(),"dd/MM/yyyy"));
            if (calendario!=null) {
                registro[19] = calendario.getNoSemana();
            }
            //la posición que contiene el resultado final
            if (registro[13].toString().toLowerCase().contains("positivo")) {
                registro[0]= rowCountPos++;
                registrosPos.add(registro);
            } else if (registro[13].toString().toLowerCase().contains("negativo")) {
                registro[0]= rowCountNeg++;
                registrosNeg.add(registro);
            } else if (incluirMxInadecuadas && registro[13].toString().toLowerCase().contains("inadecuada")){
                registro[0]= rowCountInadec++;
                registrosMxInadec.add(registro);
            }
        }
    }

    private void setDatosDefecto(List<DaSolicitudDx> dxList, List<Object[]> registrosPos, List<Object[]> registrosNeg, boolean incluirMxInadecuadas, List<Object[]> registrosMxInadec) throws Exception{
// create data rows
        int rowCountPos = 1;
        int rowCountNeg = 1;
        int rowCountInadec = 1;
        for (DaSolicitudDx solicitudDx : dxList) {
            String nombres = "";
            String apellidos = "";

            Object[] registro = new Object[16];
            registro[1] = solicitudDx.getIdTomaMx().getCodigoLab();

            nombres = solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getPrimerNombre();
            if (solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre()!=null)
                nombres += " "+solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre();
            registro[2] = nombres;

            apellidos = solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getPrimerApellido();
            if (solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido()!=null)
                apellidos += " "+solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido();
            registro[3] = apellidos;

            Integer edad = null;
            String medidaEdad = "";
            String[] arrEdad = DateUtil.calcularEdad(solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getFechaNacimiento(), new Date()).split("/");
            if (arrEdad[0] != null) {
                edad = Integer.valueOf(arrEdad[0]); medidaEdad = "A";
            }else if (arrEdad[1] != null) {
                edad = Integer.valueOf(arrEdad[1]); medidaEdad = "M";
            }else if (arrEdad[2] != null) {
                edad = Integer.valueOf(arrEdad[2]); medidaEdad = "D";
            }
            registro[4] = edad;
            registro[5] = medidaEdad;
            String direccion = solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getDireccionResidencia();
            if (solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getTelefonoResidencia()!=null || solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getTelefonoMovil()!=null ){
                direccion += ". TEL. ";
                direccion+= (solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getTelefonoResidencia()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getTelefonoResidencia()+",":"");
                direccion+= (solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getTelefonoMovil()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getTelefonoMovil():"");
            }
            registro[6] = direccion;
            registro[7] = (solicitudDx.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre():"");
            registro[8] = (solicitudDx.getIdTomaMx().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getCodUnidadAtencion().getMunicipio().getNombre():"");
            registro[9] = (solicitudDx.getIdTomaMx().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getCodUnidadAtencion().getNombre():"");
            String sexo = solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSexo().getCodigo();
            registro[10] = sexo.substring(sexo.length()-1, sexo.length());
            registro[11] = solicitudDx.getIdTomaMx().getIdNotificacion().getFechaInicioSintomas();
            registro[12] = solicitudDx.getIdTomaMx().getFechaHTomaMx();
            validarPCRIgMDefecto(registro, solicitudDx.getIdSolicitudDx());
            registro[15] = parseFinalResultDetails(solicitudDx.getIdSolicitudDx());
            if (registro[15].toString().toLowerCase().contains("positivo")) {
                registro[0]= rowCountPos++;
                registrosPos.add(registro);
            } else if (registro[15].toString().toLowerCase().contains("negativo")) {
                registro[0]= rowCountNeg++;
                registrosNeg.add(registro);
            } else if (incluirMxInadecuadas && registro[15].toString().toLowerCase().contains("inadecuada")){
                registro[0]= rowCountInadec++;
                registrosMxInadec.add(registro);
            }
        }
    }

    private void validarPCRIgMDengue(Object[] dato, String idSolicitudDx){

        List<OrdenExamen> examenes = ordenExamenMxService.getOrdenesExamenByIdSolicitud(idSolicitudDx);
        for (OrdenExamen examen : examenes) {
            if (examen.getCodExamen().getNombre().toUpperCase().contains("PCR")){
                List<DetalleResultado> resultados = resultadosService.getDetallesResultadoActivosByExamen(examen.getIdOrdenExamen());

                Date fechaProcesamiento = null;
                String detalleResultado = "";
                String serotipo = "";
                for (DetalleResultado resultado : resultados) {
                    if (resultado.getRespuesta().getNombre().toLowerCase().contains("serotipo")){
                        Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(resultado.getValor());
                        serotipo = cat_lista.getValor();
                    }else{
                        if (resultado.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                            Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(resultado.getValor());
                            detalleResultado = cat_lista.getValor();
                        } else if (resultado.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                            detalleResultado = (Boolean.valueOf(resultado.getValor()) ? "lbl.yes" : "lbl.no");
                        } else {
                            detalleResultado = resultado.getValor();
                        }
                    }
                    fechaProcesamiento = resultado.getFechahRegistro();
                }
                if (resultados.size() > 0) {
                    dato[15] = detalleResultado;
                    dato[16] = serotipo;
                    dato[17] = fechaProcesamiento;
                }
            }else if (examen.getCodExamen().getNombre().toUpperCase().contains("IGM")){
                List<DetalleResultado> resultados = resultadosService.getDetallesResultadoActivosByExamen(examen.getIdOrdenExamen());

                Date fechaProcesamiento = null;
                String detalleResultado = "";
                for (DetalleResultado resultado : resultados) {
                    if (resultado.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                        Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(resultado.getValor());
                        detalleResultado = cat_lista.getValor();
                    } else if (resultado.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                        detalleResultado = (Boolean.valueOf(resultado.getValor()) ? "lbl.yes" : "lbl.no");
                    } else {
                        detalleResultado = resultado.getValor();
                    }
                    fechaProcesamiento = resultado.getFechahRegistro();
                }
                if (resultados.size() > 0) {
                    dato[19] = detalleResultado;
                    dato[14] = fechaProcesamiento;
                }
            }
        }
    }

    private void validarPCRIgMChikun(Object[] dato, String idSolicitudDx){

        List<OrdenExamen> examenes = ordenExamenMxService.getOrdenesExamenByIdSolicitud(idSolicitudDx);
        for (OrdenExamen examen : examenes) {
            if (examen.getCodExamen().getNombre().toUpperCase().contains("PCR")){
                List<DetalleResultado> resultados = resultadosService.getDetallesResultadoActivosByExamen(examen.getIdOrdenExamen());

                String detalleResultado = "";
                for (DetalleResultado resultado : resultados) {

                        if (resultado.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                            Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(resultado.getValor());
                            detalleResultado = cat_lista.getValor();
                        } else if (resultado.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                            detalleResultado = (Boolean.valueOf(resultado.getValor()) ? "lbl.yes" : "lbl.no");
                        } else {
                            detalleResultado = resultado.getValor();
                        }
                }
                if (resultados.size() > 0) {
                    dato[13] = detalleResultado;
                }
            }else if (examen.getCodExamen().getNombre().toUpperCase().contains("IGM")){
                List<DetalleResultado> resultados = resultadosService.getDetallesResultadoActivosByExamen(examen.getIdOrdenExamen());

                String detalleResultado = "";
                for (DetalleResultado resultado : resultados) {
                    if (resultado.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                        Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(resultado.getValor());
                        detalleResultado = cat_lista.getValor();
                    } else if (resultado.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                        detalleResultado = (Boolean.valueOf(resultado.getValor()) ? "lbl.yes" : "lbl.no");
                    } else {
                        detalleResultado = resultado.getValor();
                    }
                }
                if (resultados.size() > 0) {
                    dato[14] = detalleResultado;
                }
            }
        }
    }

    private void validarPCRIgMDefecto(Object[] dato, String idSolicitudDx){

        List<OrdenExamen> examenes = ordenExamenMxService.getOrdenesExamenByIdSolicitud(idSolicitudDx);
        for (OrdenExamen examen : examenes) {
            if (examen.getCodExamen().getNombre().toUpperCase().contains("PCR")){
                List<DetalleResultado> resultados = resultadosService.getDetallesResultadoActivosByExamen(examen.getIdOrdenExamen());

                String detalleResultado = "";
                for (DetalleResultado resultado : resultados) {

                    if (resultado.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                        Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(resultado.getValor());
                        detalleResultado = cat_lista.getValor();
                    } else if (resultado.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                        detalleResultado = (Boolean.valueOf(resultado.getValor()) ? "lbl.yes" : "lbl.no");
                    } else {
                        detalleResultado = resultado.getValor();
                    }
                }
                if (resultados.size() > 0) {
                    dato[13] = detalleResultado;
                }
            }else if (examen.getCodExamen().getNombre().toUpperCase().contains("IGM")){
                List<DetalleResultado> resultados = resultadosService.getDetallesResultadoActivosByExamen(examen.getIdOrdenExamen());

                String detalleResultado = "";
                for (DetalleResultado resultado : resultados) {
                    if (resultado.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                        Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(resultado.getValor());
                        detalleResultado = cat_lista.getValor();
                    } else if (resultado.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                        detalleResultado = (Boolean.valueOf(resultado.getValor()) ? "lbl.yes" : "lbl.no");
                    } else {
                        detalleResultado = resultado.getValor();
                    }
                }
                if (resultados.size() > 0) {
                    dato[14] = detalleResultado;
                }
            }
        }
    }

    private String parseDxs(String idTomaMx, String codigoLab){
        List<DaSolicitudDx> solicitudDxList = tomaMxService.getSolicitudesDxByIdToma(idTomaMx, codigoLab);
        String dxs = "";
        if (!solicitudDxList.isEmpty()) {
            int cont = 0;
            for (DaSolicitudDx solicitudDx : solicitudDxList) {
                cont++;
                if (cont == solicitudDxList.size()) {
                    dxs += solicitudDx.getCodDx().getNombre();
                } else {
                    dxs += solicitudDx.getCodDx().getNombre() + ", ";
                }
            }
        }
        return dxs;
    }

    private String parseFinalResultDetails(String idSolicitud){
        List<DetalleResultadoFinal> resFinalList = resultadoFinalService.getDetResActivosBySolicitud(idSolicitud);
        String resultados="";
        for(DetalleResultadoFinal res: resFinalList){
            if (res.getRespuesta()!=null) {
                //resultados+=(resultados.isEmpty()?res.getRespuesta().getNombre():", "+res.getRespuesta().getNombre());
                if (res.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                    Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(res.getValor());
                    resultados+=cat_lista.getValor();
                }else if (res.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                    String valorBoleano = (Boolean.valueOf(res.getValor())?"lbl.yes":"lbl.no");
                    resultados+=valorBoleano;
                } else {
                    resultados+=res.getValor();
                }
            }else if (res.getRespuestaExamen()!=null){
                //resultados+=(resultados.isEmpty()?res.getRespuestaExamen().getNombre():", "+res.getRespuestaExamen().getNombre());
                if (res.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                    Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(res.getValor());
                    resultados+=cat_lista.getValor();
                } else if (res.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                    String valorBoleano = (Boolean.valueOf(res.getValor())?"lbl.yes":"lbl.no");
                    resultados+=valorBoleano;
                }else {
                    resultados+=res.getValor();
                }
            }
        }
        return resultados;
    }

    /**
     * Convierte un JSON con los filtros de búsqueda a objeto FiltrosReporte
     * @param strJson filtros
     * @return FiltrosReporte
     * @throws Exception
     */
    private FiltrosReporte jsonToFiltroReportes(String strJson) throws Exception {
        JsonObject jObjectFiltro = new Gson().fromJson(strJson, JsonObject.class);
        FiltrosReporte filtroRep = new FiltrosReporte();
        Date fechaInicio = null;
        Date fechaFin = null;
        Long codSilais = null;
        Long codUnidadSalud = null;
        String tipoNotificacion = null;
        Integer factor= 0;
        Long codDepartamento = null;
        Long codMunicipio = null;
        String codArea = null;
        boolean subunidad = false;
        boolean porSilais = true;//por defecto true
        String codZona = null;
        Integer idDx = null;
        boolean mxInadecuadas = true;

        if (jObjectFiltro.get("codSilais") != null && !jObjectFiltro.get("codSilais").getAsString().isEmpty())
            codSilais = jObjectFiltro.get("codSilais").getAsLong();
        if (jObjectFiltro.get("codUnidadSalud") != null && !jObjectFiltro.get("codUnidadSalud").getAsString().isEmpty())
            codUnidadSalud = jObjectFiltro.get("codUnidadSalud").getAsLong();
        if (jObjectFiltro.get("tipoNotificacion") != null && !jObjectFiltro.get("tipoNotificacion").getAsString().isEmpty())
            tipoNotificacion = jObjectFiltro.get("tipoNotificacion").getAsString();
        if (jObjectFiltro.get("codFactor") != null && !jObjectFiltro.get("codFactor").getAsString().isEmpty())
            factor = jObjectFiltro.get("codFactor").getAsInt();
        if (jObjectFiltro.get("fechaInicio") != null && !jObjectFiltro.get("fechaInicio").getAsString().isEmpty())
            fechaInicio = DateUtil.StringToDate(jObjectFiltro.get("fechaInicio").getAsString() + " 00:00:00");
        if (jObjectFiltro.get("fechaFin") != null && !jObjectFiltro.get("fechaFin").getAsString().isEmpty())
            fechaFin = DateUtil.StringToDate(jObjectFiltro.get("fechaFin").getAsString() + " 23:59:59");
        if (jObjectFiltro.get("codDepartamento") != null && !jObjectFiltro.get("codDepartamento").getAsString().isEmpty())
            codDepartamento = jObjectFiltro.get("codDepartamento").getAsLong();
        if (jObjectFiltro.get("codMunicipio") != null && !jObjectFiltro.get("codMunicipio").getAsString().isEmpty())
            codMunicipio = jObjectFiltro.get("codMunicipio").getAsLong();
        if (jObjectFiltro.get("codArea") != null && !jObjectFiltro.get("codArea").getAsString().isEmpty())
            codArea = jObjectFiltro.get("codArea").getAsString();
        if (jObjectFiltro.get("subunidades") != null && !jObjectFiltro.get("subunidades").getAsString().isEmpty())
            subunidad = jObjectFiltro.get("subunidades").getAsBoolean();
        if (jObjectFiltro.get("porSilais") != null && !jObjectFiltro.get("porSilais").getAsString().isEmpty())
            porSilais = jObjectFiltro.get("porSilais").getAsBoolean();
        if (jObjectFiltro.get("codZona") != null && !jObjectFiltro.get("codZona").getAsString().isEmpty())
            codZona = jObjectFiltro.get("codZona").getAsString();
        if (jObjectFiltro.get("idDx") != null && !jObjectFiltro.get("idDx").getAsString().isEmpty())
            idDx = jObjectFiltro.get("idDx").getAsInt();
        if (jObjectFiltro.get("incluirMxInadecuadas") != null && !jObjectFiltro.get("incluirMxInadecuadas").getAsString().isEmpty())
            mxInadecuadas = jObjectFiltro.get("incluirMxInadecuadas").getAsBoolean();

        filtroRep.setSubunidades(subunidad);
        filtroRep.setCodSilais(codSilais);
        filtroRep.setCodUnidad(codUnidadSalud);
        filtroRep.setFechaInicio(fechaInicio);
        filtroRep.setFechaFin(fechaFin);
        filtroRep.setTipoNotificacion(tipoNotificacion);
        filtroRep.setFactor(factor);
        filtroRep.setCodDepartamento(codDepartamento);
        filtroRep.setCodMunicipio(codMunicipio);
        filtroRep.setCodArea(codArea);
        filtroRep.setAnioInicial(DateUtil.DateToString(fechaInicio, "yyyy"));
        filtroRep.setPorSilais(porSilais);
        filtroRep.setCodZona(codZona);
        filtroRep.setIdDx(idDx);
        filtroRep.setIncluirMxInadecuadas(mxInadecuadas);

        return filtroRep;
    }
}
