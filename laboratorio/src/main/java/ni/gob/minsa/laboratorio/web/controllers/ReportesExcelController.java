package ni.gob.minsa.laboratorio.web.controllers;

import com.google.common.base.Predicate;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.catalogos.Anios;
import ni.gob.minsa.laboratorio.domain.catalogos.AreaRep;
import ni.gob.minsa.laboratorio.domain.catalogos.Semanas;
import ni.gob.minsa.laboratorio.domain.concepto.Catalogo_Lista;
import ni.gob.minsa.laboratorio.domain.estructura.CalendarioEpi;
import ni.gob.minsa.laboratorio.domain.estructura.EntidadesAdtvas;
import ni.gob.minsa.laboratorio.domain.estructura.Unidades;
import ni.gob.minsa.laboratorio.domain.examen.CatalogoExamenes;
import ni.gob.minsa.laboratorio.domain.examen.Departamento;
import ni.gob.minsa.laboratorio.domain.muestra.*;
import ni.gob.minsa.laboratorio.domain.parametros.Parametro;
import ni.gob.minsa.laboratorio.domain.resultados.DetalleResultado;
import ni.gob.minsa.laboratorio.domain.resultados.DetalleResultadoFinal;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.User;
import ni.gob.minsa.laboratorio.domain.vigilanciaSindFebril.DaSindFebril;
import ni.gob.minsa.laboratorio.service.*;
import ni.gob.minsa.laboratorio.utilities.ConstantsSecurity;
import ni.gob.minsa.laboratorio.utilities.DateUtil;
import ni.gob.minsa.laboratorio.utilities.Email.Attachment;
import ni.gob.minsa.laboratorio.utilities.Email.EmailUtil;
import ni.gob.minsa.laboratorio.utilities.Email.SessionData;
import ni.gob.minsa.laboratorio.utilities.FiltrosReporte;
import ni.gob.minsa.laboratorio.utilities.excelUtils.ExcelBuilder;
import ni.gob.minsa.laboratorio.utilities.reportes.ConsolidadoExamen;
import ni.gob.minsa.laboratorio.utilities.reportes.FilterLists;
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

    @Resource(name = "reporteConsolExamenService")
    private ReporteConsolExamenService reporteConsolExamenService;

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

    @Resource(name = "laboratoriosService")
    private LaboratoriosService laboratoriosService;

    @Resource(name = "examenesService")
    private ExamenesService examenesService;

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
            //si la url esta vacia significa que la validaci�n del login fue exitosa
            if (urlValidacion.isEmpty())
                urlValidacion = seguridadService.validarAutorizacionUsuario(request, ConstantsSecurity.SYSTEM_CODE, false);
        }catch (Exception e){
            e.printStackTrace();
            urlValidacion = "404";
        }
        if (urlValidacion.isEmpty()) {
            List<Laboratorio> laboratorios = null;
            long idUsuario = seguridadService.obtenerIdUsuario(request);
            List<EntidadesAdtvas> entidades = new ArrayList<EntidadesAdtvas>();
            if (seguridadService.esUsuarioNivelCentral(idUsuario, ConstantsSecurity.SYSTEM_CODE)){
                entidades = entidadAdmonService.getAllEntidadesAdtvas();
                laboratorios = laboratoriosService.getLaboratoriosRegionales();
            }else {
                entidades = seguridadService.obtenerEntidadesPorUsuario((int) idUsuario, ConstantsSecurity.SYSTEM_CODE);
                Laboratorio laboratorio = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
                if (laboratorio!=null) {
                    laboratorios = new ArrayList<Laboratorio>();
                    laboratorios.add(laboratorio);
                }
            }
            List<AreaRep> areas = new ArrayList<AreaRep>();
            areas.add(catalogosService.getAreaRep("AREAREP|PAIS"));
            areas.add(catalogosService.getAreaRep("AREAREP|SILAIS"));
            areas.add(catalogosService.getAreaRep("AREAREP|UNI"));
            List<Catalogo_Dx> catDx = associationSamplesRequestService.getDxs();
            model.addAttribute("laboratorios", laboratorios);
            model.addAttribute("areas", areas);
            model.addAttribute("entidades", entidades);
            model.addAttribute("dxs", catDx);
            return "reportes/resultadoDxVig";
        }else{
            return  urlValidacion;
        }
    }

    /*******************************************************************/
    /***** REPORTE POSITIVIDAD CONSILIDADO POR T�CNICA, DX Y SEMANA ****/
    /*******************************************************************/

    @RequestMapping(value = "consolidatedexams/init", method = RequestMethod.GET)
    public String initReportConsolidatedByExams(Model model,HttpServletRequest request) throws Exception {
        logger.debug("Reporte por examenes(t�cnica)");
        List<Catalogo_Dx> catDx = associationSamplesRequestService.getDxs();
        List<Semanas> semanas = catalogosService.getSemanas();
        List<Anios> anios = catalogosService.getAnios();
        List<Laboratorio> laboratorios = null;
        User usuario = seguridadService.getUsuario(seguridadService.obtenerNombreUsuario());
        if (usuario.getNivelCentral()){
            laboratorios = laboratoriosService.getLaboratoriosRegionales();
        }else {
            Laboratorio laboratorio = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
            if (laboratorio!=null) {
                laboratorios = new ArrayList<Laboratorio>();
                laboratorios.add(laboratorio);
            }
        }
        model.addAttribute("semanas", semanas);
        model.addAttribute("anios", anios);
        model.addAttribute("dxs", catDx);
        model.addAttribute("laboratorios", laboratorios);
        return "reportes/consolidatedByExams";
    }
    /**
     * M�todo para obtener data para Reporte por Resultado dx
     * @param filtro JSon con los datos de los filtros a aplicar en la b�squeda
     * @return Object
     * @throws Exception
     */
    @RequestMapping(value = "reportResultDxVigMail", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String fetchReportResultDxVigEmail(@RequestParam(value = "filtro", required = true) String filtro) throws Exception{
        logger.info("Obteniendo los datos para Reporte por Resultado ");

        try {
            FiltrosReporte filtroRep = jsonToFiltroReportes(filtro);
            Laboratorio labUser = laboratoriosService.getLaboratorioByCodigo(filtroRep.getCodLaboratio()); //seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
            /************ARMANDO MODELO PARA GENERAR EXCEL************/
            Map<String, Object> model = new HashMap<String, Object>();
            logger.info("Obteniendo los datos para Reporte por Resultado dx vigilancia ");
            List<DaSolicitudDx> dxList = reportesService.getDiagnosticosAprobadosByFiltro(filtroRep);
            List<Object[]> registrosPos = new ArrayList<Object[]>();
            List<Object[]> registrosNeg = new ArrayList<Object[]>();
            List<Object[]> registrosMxInadec = new ArrayList<Object[]>();
            List<String> columnas = new ArrayList<String>();
            Catalogo_Dx dx = tomaMxService.getDxById(filtroRep.getIdDx().toString());
            boolean mostrarTabla1 = true, mostrarTabla2 = true;

            String tipoReporte = "";
            if (dx.getNombre().toLowerCase().contains("dengue")){
                tipoReporte = "DENGUE";
                setNombreColumnasDengue(columnas);
                setDatosDengue(dxList, registrosPos, registrosNeg, labUser.getCodigo(), filtroRep.isIncluirMxInadecuadas(), registrosMxInadec, columnas.size());
            }else if (dx.getNombre().toLowerCase().contains("chikun")){
                tipoReporte = "CHIK";
                setNombreColumnasChik(columnas);
                setDatosChikungunya(dxList, registrosPos, registrosNeg, filtroRep.isIncluirMxInadecuadas(), registrosMxInadec, labUser.getCodigo(), columnas.size());
            }else if (dx.getNombre().toLowerCase().contains("zika")){
                tipoReporte = "ZIKA";
                setNombreColumnasZika(columnas);
                setDatosZika(dxList, registrosPos, registrosNeg, labUser.getCodigo(), filtroRep.isIncluirMxInadecuadas(), registrosMxInadec, columnas.size());
            }else if (dx.getNombre().toLowerCase().contains("leptospi")){
                tipoReporte = "LEPTO";
                setNombreColumnasLepto(columnas);
                setDatosLepto(dxList, registrosPos, registrosNeg, filtroRep.isIncluirMxInadecuadas(), registrosMxInadec, columnas.size());
            }else if (dx.getNombre().toLowerCase().contains("molecular") && dx.getNombre().toLowerCase().contains("vih")){
                tipoReporte = "ML_VIH";
                setNombreColumnasVIHMolecular(columnas);
            }else if (dx.getNombre().toLowerCase().contains("seguimiento") && dx.getNombre().toLowerCase().contains("vih")){
                tipoReporte = "SG_VIH";
            }else if (dx.getNombre().toLowerCase().contains("serolog") && dx.getNombre().toLowerCase().contains("vih")){
                tipoReporte = "SR_VIH";
            } //Mycobacterium Tuberculosis
            else if (dx.getNombre().toLowerCase().contains("mycobacterium") && (dx.getNombre().toLowerCase().contains("tuberculosis") || dx.getNombre().toLowerCase().contains("tb"))) {
                tipoReporte = "XPERT_TB";
                mostrarTabla2 = false;
                setNombreColumnasMycobacTB(columnas);
                setDatosXpertTB(dxList, registrosPos, registrosNeg, filtroRep.isIncluirMxInadecuadas(), registrosMxInadec, columnas.size());
            }//Cultivo TB
            else if (dx.getNombre().toLowerCase().contains("cultivo") && (dx.getNombre().toLowerCase().contains("tuberculosis") || dx.getNombre().toLowerCase().contains("tb"))) {
                tipoReporte = "CULTIVO_TB";
                mostrarTabla2 = false;
                filtroRep.setIncluirMxInadecuadas(false);
                setNombreColumnasCultivoTB(columnas);
                setDatosCultivoTB(dxList, registrosPos, registrosNeg, filtroRep.isIncluirMxInadecuadas(), registrosMxInadec, columnas.size());
            } else if (dx.getNombre().toLowerCase().contains("ifi virus respiratorio")) {
                tipoReporte = "IFI_VIRUS_RESP";
                setNombreColumnasIFIVR(columnas);
                setDatosIFIVR(dxList, registrosPos, registrosNeg, filtroRep.isIncluirMxInadecuadas(), registrosMxInadec, columnas.size());
            } else if (dx.getNombre().toLowerCase().contains("molecular virus respiratorio")) {
                tipoReporte = "BIO_MOL_VIRUS_RESP";
                setNombreColumnasBioMolVR(columnas);
                setDatosBioMolVR(dxList, registrosPos, registrosNeg, filtroRep.isIncluirMxInadecuadas(), registrosMxInadec, columnas.size());
            } else{
                tipoReporte = dx.getNombre().replace(" ","_");
                setNombreColumnasDefecto(columnas);
                setDatosDefecto(dxList, registrosPos, registrosNeg, filtroRep.isIncluirMxInadecuadas(), registrosMxInadec);
            }

            Departamento departamento = organizationChartService.getDepartamentoAreaByLab(labUser.getCodigo(), dx.getArea().getIdArea());
            model.put("titulo", messageSource.getMessage("lbl.minsa", null, null) + " - " + labUser.getNombre());
            model.put("subtitulo", departamento.getNombre().toUpperCase() + "/" + dx.getNombre().toUpperCase());

            model.put("tablaPos", getSubtituloTabla1(tipoReporte, filtroRep));
            model.put("tablaNeg", getSubtituloTabla2(tipoReporte, filtroRep));

            model.put("tablaMxInadec", String.format(messageSource.getMessage("lbl.excel.filter.mx.inadec", null, null),
                    DateUtil.DateToString(filtroRep.getFechaInicio(), "dd/MM/yyyy"),
                    DateUtil.DateToString(filtroRep.getFechaFin(), "dd/MM/yyyy")));

            model.put("columnas", columnas);
            model.put("tipoReporte", tipoReporte);

            model.put("listaDxPos", registrosPos);
            model.put("listaDxNeg", registrosNeg);
            model.put("listaDxInadec", registrosMxInadec);
            model.put("incluirMxInadecuadas", filtroRep.isIncluirMxInadecuadas());
            model.put("mostrarTabla1", mostrarTabla1);
            model.put("mostrarTabla2", mostrarTabla2);
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
     * Abrir sessi�n en servidor de correo
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

    @RequestMapping(value = "/consolidadoTecnica", method = RequestMethod.GET)
    public ModelAndView downloadExcelConsolidatedExam(@RequestParam(value = "filtro", required = true) String filtro) throws Exception {
        ModelAndView excelView = new ModelAndView("excelView");
        FiltrosReporte filtroRep = jsonToFiltroReportes(filtro);
        List<Catalogo_Dx> catalogoDxList = tomaMxService.getDxs(filtroRep.getDiagnosticos());

        List<String> semanas = new ArrayList<String>();
        List<String> meses = new ArrayList<String>();
        List<CalendarioEpi> semanasEpi = calendarioEpiService.getCalendarioRangoSemanas(filtroRep.getSemInicial(), filtroRep.getSemFinal(), Integer.valueOf(filtroRep.getAnioInicial()));
        Integer mesActual = null;
        Integer semanasMes = 0;
        int contadorSemana = 1;
        //se determina la cantidad de semanas de cada mes
        for (CalendarioEpi semana : semanasEpi) {
            //primera iteraci�n
            if (mesActual == null) {
                mesActual = semana.getNoMes();
            }
            //primer semana de siguiente mes
            if (semana.getNoMes() != mesActual) {
                //agregar el mes que se ha completado el conteo de semanas
                meses.add(mesActual.toString() + "," + semanasMes.toString());
                semanasMes = 1;
                mesActual = semana.getNoMes();
            } else {
                semanasMes++;
            }
            //es �ltimo registro, agregar el mes correspondiente
            if (contadorSemana == semanasEpi.size()) {
                meses.add(mesActual.toString() + "," + semanasMes.toString());
            }
            contadorSemana++;
        }
        //sacar todas las semanas contenidas dentro del rango indicado por el usuario
        for (int i = filtroRep.getSemInicial(); i <= filtroRep.getSemFinal(); i++) {
            semanas.add(String.valueOf(i));
        }
        //trae todos los registros que coinciden con el filtro de b�squeda
        List<ConsolidadoExamen> registros = reporteConsolExamenService.getDataDxResultReport(filtroRep);
        //por entidades es que se cuentan los datos
        List<EntidadesAdtvas> entidades = entidadAdmonService.getAllEntidadesAdtvas();
        //la cantidad de entidades es la que indica cuanto registros deber�a tener cada tabla
        int registrosPorTabla = entidades.size();
        List<List<Object[]>> datos = new ArrayList<List<Object[]>>();
        List<List<Object[]>> consolidados = new ArrayList<List<Object[]>>();
        List<String> dxsList = new ArrayList<String>();
        for (final Catalogo_Dx dx : catalogoDxList) {
            dxsList.add(dx.getNombre());
            List<Object[]> consolidadoList = new ArrayList<Object[]>();
            List<Object[]> datosList = new ArrayList<Object[]>();
            //sacar todos los examenes(t�cnica) activos por cada dx
            List<CatalogoExamenes> examenesList = examenesService.getExamenesByIdDx(dx.getIdDiagnostico());
            for(final CatalogoExamenes examen : examenesList) {
                Object[] salto = new Object[1];
                salto[0] = examen.getNombre();
                datosList.add(salto);
                consolidadoList.add(salto);
                //Patron para filtras registros por dx y examen
                Predicate<ConsolidadoExamen> byDxAndExam = new Predicate<ConsolidadoExamen>() {
                    @Override
                    public boolean apply(ConsolidadoExamen consolidadoExamen) {
                        return consolidadoExamen.getIdDiagnostico().equals(dx.getIdDiagnostico()) && consolidadoExamen.getIdExamen().equals(examen.getIdExamen());
                    }
                };
                //aplicar filtro por dx y examen
                Collection<ConsolidadoExamen> registrosdx = FilterLists.filter(registros, byDxAndExam);
                for (final EntidadesAdtvas SILAIS : entidades) {
                    //representa una fila para la tabla de datos. El tama�o es: (semanas.size() * 2), porque cada semana de datos lleva total y positivos; y (+ 1), por que se agrega el nombre del SILAIS al inicio
                    Object[] registro = new Object[(semanas.size() * 2) + 1];
                    //representa una fila para la tabla de consolidados
                    Object[] registroMes = new Object[(meses.size() * 2) + 1];
                    registro[0] = SILAIS.getNombre().replaceAll("SILAIS","").trim();
                    registroMes[0] = SILAIS.getNombre().replaceAll("SILAIS","").trim();
                    int indice = 1;
                    int indiceMes = 1;
                    //se arma estructura para hoja de datos
                    for (final String semana : semanas) {
                        //Del subconjunto por dx y examen, se filtran todos los registros por semana y SILAIS
                        Predicate<ConsolidadoExamen> totalBySilaisSemana = new Predicate<ConsolidadoExamen>() {
                            @Override
                            public boolean apply(ConsolidadoExamen consolidadoExamen) {
                                return consolidadoExamen.getCodigoSilais() == SILAIS.getCodigo() && consolidadoExamen.getNoSemana().equals(Integer.valueOf(semana));
                            }
                        };
                        //aplicar filtro por semana y SILAIS
                        Collection<ConsolidadoExamen> examenes = FilterLists.filter(registrosdx, totalBySilaisSemana);
                        //el total es el tama�o del subconjunto resultado del filtro
                        registro[indice] = examenes.size();

                        //total positivos
                        //Del subconjunto por dx, examen, semana y SILAIS, se filtran todos los registros con resultado positivo
                        Predicate<ConsolidadoExamen> posBySilaisSemana = new Predicate<ConsolidadoExamen>() {
                            @Override
                            public boolean apply(ConsolidadoExamen consolidadoExamen) {
                                return consolidadoExamen.getResultado().equalsIgnoreCase("positivo");
                            }
                        };
                        //se aplica filtro de registros con resultado positivo
                        Collection<ConsolidadoExamen> positivos = FilterLists.filter(examenes, posBySilaisSemana);
                        //el total de positivos es el tama�o del subconjunto resultado del filtro
                        registro[indice + 1] = positivos.size();

                        indice += 2;
                    }

                    //se arma estructura para hoja de consolidado
                    for (final String mes : meses) {
                        //Del subconjunto por dx y examen, se filtran todos los registros por mes y SILAIS
                        Predicate<ConsolidadoExamen> totalBySilaisSemana = new Predicate<ConsolidadoExamen>() {
                            @Override
                            public boolean apply(ConsolidadoExamen consolidadoExamen) {
                                return consolidadoExamen.getCodigoSilais() == SILAIS.getCodigo() && consolidadoExamen.getNoMes().equals(Integer.valueOf(mes.substring(0,mes.indexOf(","))));
                            }
                        };
                        //se aplica filtro por mes y SILAIS
                        Collection<ConsolidadoExamen> examenes = FilterLists.filter(registrosdx, totalBySilaisSemana);
                        registroMes[indiceMes] = examenes.size();

                        //total positivos
                        //Del subconjunto por dx, examen, mes y SILAIS, se filtran todos los registros con resultado positivo
                        Predicate<ConsolidadoExamen> posBySilaisSemana = new Predicate<ConsolidadoExamen>() {
                            @Override
                            public boolean apply(ConsolidadoExamen consolidadoExamen) {
                                return consolidadoExamen.getResultado().equalsIgnoreCase("positivo");
                            }
                        };
                        Collection<ConsolidadoExamen> positivos = FilterLists.filter(examenes, posBySilaisSemana);
                        registroMes[indiceMes + 1] = positivos.size();

                        indiceMes += 2;
                    }
                    datosList.add(registro);
                    consolidadoList.add(registroMes);
                }
            }
            datos.add(datosList);
            consolidados.add(consolidadoList);
        }
        excelView.addObject("consol",consolidados);
        excelView.addObject("datos", datos);
        excelView.addObject("columnas", semanas);
        excelView.addObject("meses", meses);
        excelView.addObject("dxs", dxsList);
        excelView.addObject("anio", Integer.valueOf(filtroRep.getAnioInicial()));
        excelView.addObject("registrosPorTabla", registrosPorTabla);
        excelView.addObject("reporte","DXEXAMS");
        return excelView;
    }

        @RequestMapping(value = "/downloadExcel", method = RequestMethod.GET)
    public ModelAndView downloadExcel(@RequestParam(value = "filtro", required = true) String filtro) throws Exception {
            // create some sample data
            logger.info("Obteniendo los datos para Reporte por Resultado dx vigilancia ");
            FiltrosReporte filtroRep = jsonToFiltroReportes(filtro);
            Laboratorio labUser = laboratoriosService.getLaboratorioByCodigo(filtroRep.getCodLaboratio());//seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
            List<DaSolicitudDx> dxList = reportesService.getDiagnosticosAprobadosByFiltro(filtroRep);
            List<Object[]> registrosPos = new ArrayList<Object[]>();
            List<Object[]> registrosNeg = new ArrayList<Object[]>();
            List<Object[]> registrosMxInadec = new ArrayList<Object[]>();
            List<String> columnas = new ArrayList<String>();
            Catalogo_Dx dx = tomaMxService.getDxById(filtroRep.getIdDx().toString());
            boolean mostrarTabla1 = true, mostrarTabla2 = true;

            ModelAndView excelView = new ModelAndView("excelView");
            String tipoReporte = "";
            if (dx.getNombre().toLowerCase().contains("dengue")) {
                tipoReporte = "DENGUE";
                setNombreColumnasDengue(columnas);
                setDatosDengue(dxList, registrosPos, registrosNeg, labUser.getCodigo(), filtroRep.isIncluirMxInadecuadas(), registrosMxInadec, columnas.size());
            } else if (dx.getNombre().toLowerCase().contains("chikun")) {
                tipoReporte = "CHIK";
                setNombreColumnasChik(columnas);
                setDatosChikungunya(dxList, registrosPos, registrosNeg, filtroRep.isIncluirMxInadecuadas(), registrosMxInadec, labUser.getCodigo(), columnas.size());
            } else if (dx.getNombre().toLowerCase().contains("zika")) {
                tipoReporte = "ZIKA";
                setNombreColumnasZika(columnas);
                setDatosZika(dxList, registrosPos, registrosNeg, labUser.getCodigo(), filtroRep.isIncluirMxInadecuadas(), registrosMxInadec, columnas.size());
            } else if (dx.getNombre().toLowerCase().contains("leptospi")) {
                tipoReporte = "LEPTO";
                setNombreColumnasLepto(columnas);
                setDatosLepto(dxList, registrosPos, registrosNeg, filtroRep.isIncluirMxInadecuadas(), registrosMxInadec, columnas.size());
            } else if (dx.getNombre().toLowerCase().contains("molecular") && dx.getNombre().toLowerCase().contains("vih")) {
                tipoReporte = "ADN_VIH";
                filtroRep.setIncluirMxInadecuadas(false);
                setNombreColumnasVIHMolecular(columnas);
                setDatosVIHMolecular(dxList, registrosPos, columnas.size(), labUser.getCodigo());
            } else if (dx.getNombre().toLowerCase().contains("seguimiento") && dx.getNombre().toLowerCase().contains("vih")) {
                tipoReporte = "CV-CD4_VIH";
                filtroRep.setIncluirMxInadecuadas(false);
                setNombreColumnasVIHSeguimiento(columnas);
            } else if (dx.getNombre().toLowerCase().contains("serolog") && dx.getNombre().toLowerCase().contains("vih")) {
                tipoReporte = "SEROLOGIA_VIH";
                filtroRep.setIncluirMxInadecuadas(false);
                setNombreColumnasVIHSerologia(columnas);
            } //Mycobacterium Tuberculosis
            else if (dx.getNombre().toLowerCase().contains("mycobacterium") && (dx.getNombre().toLowerCase().contains("tuberculosis") || dx.getNombre().toLowerCase().contains("tb"))) {
                tipoReporte = "XPERT_TB";
                mostrarTabla2 = false;
                setNombreColumnasMycobacTB(columnas);
                setDatosXpertTB(dxList, registrosPos, registrosNeg, filtroRep.isIncluirMxInadecuadas(), registrosMxInadec, columnas.size());
            }//Cultivo TB
            else if (dx.getNombre().toLowerCase().contains("cultivo") && (dx.getNombre().toLowerCase().contains("tuberculosis") || dx.getNombre().toLowerCase().contains("tb"))) {
                tipoReporte = "CULTIVO_TB";
                mostrarTabla2 = false;
                filtroRep.setIncluirMxInadecuadas(false);
                setNombreColumnasCultivoTB(columnas);
                setDatosCultivoTB(dxList, registrosPos, registrosNeg, filtroRep.isIncluirMxInadecuadas(), registrosMxInadec, columnas.size());
            } else if (dx.getNombre().toLowerCase().contains("ifi virus respiratorio")) {
                tipoReporte = "IFI_VIRUS_RESP";
                setNombreColumnasIFIVR(columnas);
                setDatosIFIVR(dxList, registrosPos, registrosNeg, filtroRep.isIncluirMxInadecuadas(), registrosMxInadec, columnas.size());
            }else if (dx.getNombre().toLowerCase().contains("molecular virus respiratorio")) {
                tipoReporte = "BIO_MOL_VIRUS_RESP";
                setNombreColumnasBioMolVR(columnas);
                setDatosBioMolVR(dxList, registrosPos, registrosNeg, filtroRep.isIncluirMxInadecuadas(), registrosMxInadec, columnas.size());
            } else {
                tipoReporte = dx.getNombre().replace(" ", "_");
                setNombreColumnasDefecto(columnas);
                setDatosDefecto(dxList, registrosPos, registrosNeg, filtroRep.isIncluirMxInadecuadas(), registrosMxInadec);
            }

            Departamento departamento = organizationChartService.getDepartamentoAreaByLab(labUser.getCodigo(), dx.getArea().getIdArea());
            excelView.addObject("titulo", messageSource.getMessage("lbl.minsa", null, null) + " - " + labUser.getNombre());
            excelView.addObject("subtitulo", (departamento != null ? departamento.getNombre().toUpperCase() : "") + "/" + dx.getNombre().toUpperCase());
            excelView.addObject("tablaPos", getSubtituloTabla1(tipoReporte, filtroRep));
            excelView.addObject("tablaNeg", getSubtituloTabla2(tipoReporte, filtroRep));
            excelView.addObject("tablaMxInadec", String.format(messageSource.getMessage("lbl.excel.filter.mx.inadec", null, null),
                    DateUtil.DateToString(filtroRep.getFechaInicio(), "dd/MM/yyyy"),
                    DateUtil.DateToString(filtroRep.getFechaFin(), "dd/MM/yyyy")));

            excelView.addObject("columnas", columnas);
            excelView.addObject("tipoReporte", tipoReporte);
            excelView.addObject("reporte", "DXVIG");

            excelView.addObject("listaDxPos", registrosPos);
            excelView.addObject("listaDxNeg", registrosNeg);
            excelView.addObject("listaDxInadec", registrosMxInadec);
            excelView.addObject("incluirMxInadecuadas", filtroRep.isIncluirMxInadecuadas());
            excelView.addObject("mostrarTabla1", mostrarTabla1);
            excelView.addObject("mostrarTabla2", mostrarTabla2);
            excelView.addObject("sinDatos", messageSource.getMessage("lbl.nothing.to.show", null, null));
            return excelView;
        }

    private String getSubtituloTabla1(String tipoReporte, FiltrosReporte filtroRep){
        String subTituloPos = "";
        if (tipoReporte.equalsIgnoreCase("LEPTO")){
            subTituloPos = String.format(messageSource.getMessage("lbl.excel.filter", null, null),
                    messageSource.getMessage("lbl.reactor", null, null),
                    DateUtil.DateToString(filtroRep.getFechaInicio(), "dd/MM/yyyy"),
                    DateUtil.DateToString(filtroRep.getFechaFin(), "dd/MM/yyyy"));
        }else if (tipoReporte.equalsIgnoreCase("XPERT_TB") || tipoReporte.equalsIgnoreCase("CULTIVO_TB")){
            subTituloPos = String.format(messageSource.getMessage("lbl.excel.filter", null, null), "",
                    DateUtil.DateToString(filtroRep.getFechaInicio(), "dd/MM/yyyy"),
                    DateUtil.DateToString(filtroRep.getFechaFin(), "dd/MM/yyyy"));
        }else{
            subTituloPos = String.format(messageSource.getMessage("lbl.excel.filter", null, null),
                    messageSource.getMessage("lbl.positives", null, null),
                    DateUtil.DateToString(filtroRep.getFechaInicio(), "dd/MM/yyyy"),
                    DateUtil.DateToString(filtroRep.getFechaFin(), "dd/MM/yyyy"));
        }
        return subTituloPos;
    }

    private String getSubtituloTabla2(String tipoReporte, FiltrosReporte filtroRep){
        String subTituloPos = "";
        if (tipoReporte.equalsIgnoreCase("LEPTO")){
            subTituloPos = String.format(messageSource.getMessage("lbl.excel.filter", null, null),
                    messageSource.getMessage("lbl.no.reactor", null, null),
                    DateUtil.DateToString(filtroRep.getFechaInicio(), "dd/MM/yyyy"),
                    DateUtil.DateToString(filtroRep.getFechaFin(), "dd/MM/yyyy"));
        }else if (tipoReporte.equalsIgnoreCase("XPERT_TB") || tipoReporte.equalsIgnoreCase("CULTIVO_TB")){
            subTituloPos = null;
        }else{
            subTituloPos = String.format(messageSource.getMessage("lbl.excel.filter", null, null),
                    messageSource.getMessage("lbl.negatives", null, null),
                    DateUtil.DateToString(filtroRep.getFechaInicio(), "dd/MM/yyyy"),
                    DateUtil.DateToString(filtroRep.getFechaFin(), "dd/MM/yyyy"));
        }
        return subTituloPos;
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
        columnas.add(messageSource.getMessage("lbl.absorbance", null, null).toUpperCase());
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
        columnas.add(messageSource.getMessage("lbl.reception.date", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.result.pcr", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.pcr.date", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.igm", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.igm.date", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.res.final", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.res.final.date.long", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.week", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.SILAIS.res", null, null).toUpperCase().replace(" ", "_"));
        columnas.add(messageSource.getMessage("person.mun.res", null, null).toUpperCase().replace(" ", "_"));
        columnas.add(messageSource.getMessage("lbl.clinical.dx", null, null));
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
        columnas.add(messageSource.getMessage("lbl.result.pcr", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.pcr.date", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.igm", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.igm.date", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.res.final", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.res.final.date", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.pregnant", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.SILAIS.res", null, null).toUpperCase().replace(" ", "_"));
        columnas.add(messageSource.getMessage("lbl.ctzica", null, null));
        columnas.add(messageSource.getMessage("person.sexo", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.week", null, null).toUpperCase());
    }

    private void setNombreColumnasLepto(List<String> columnas){
        columnas.add(messageSource.getMessage("lbl.num", null, null));
        columnas.add(messageSource.getMessage("lbl.lab.code.mx", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.igm.lepto", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.lepto.igm.date", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.names", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.lastnames", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.silais", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.muni", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.health.unit.excel", null, null));
        columnas.add(messageSource.getMessage("lbl.address", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.age", null, null).toUpperCase().replace(":", ""));
        columnas.add(messageSource.getMessage("lbl.age.um", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("person.sexo", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.fis.short", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.ftm", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.SILAIS.res", null, null).toUpperCase().replace(" ", "_"));
        columnas.add(messageSource.getMessage("lbl.week", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.hosp", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.date.admission", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.deceased", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.date.deceased", null, null).toUpperCase());
    }

    private void setNombreColumnasMycobacTB(List<String> columnas){
        columnas.add(messageSource.getMessage("lbl.num", null, null));
        columnas.add(messageSource.getMessage("lbl.lab.code.mx", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.receipt.person.name", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("person.sexo", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.age", null, null).toUpperCase().replace(":", ""));
        columnas.add(messageSource.getMessage("lbl.age.um", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.silais", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.health.unit", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.population.risk", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.category.patient", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.comorbidities", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.location.infection", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.sample.type1", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.bacilloscopy", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.date.xpert.tb", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.fr.expert.tb", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.observations", null, null).toUpperCase());
    }

    private void setNombreColumnasCultivoTB(List<String> columnas){
        columnas.add(messageSource.getMessage("lbl.num", null, null));
        columnas.add(messageSource.getMessage("lbl.lab.code.mx", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.receipt.person.name", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("person.sexo", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.age", null, null).toUpperCase().replace(":", ""));
        columnas.add(messageSource.getMessage("lbl.age.um", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.silais", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.health.unit", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.population.risk", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.category.patient", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.comorbidities", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.location.infection", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.sample.type1", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.bacilloscopy", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.date.xpert.tb", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.fr.expert.tb", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.planting.date", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.num.tubes", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.num.tubes.con", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.res.planting", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.date.res.planting", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.lote.lj", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.observations", null, null).toUpperCase());
    }

    private void setNombreColumnasVIHMolecular(List<String> columnas){
        columnas.add(messageSource.getMessage("lbl.lab.code", null, null));
        columnas.add(messageSource.getMessage("lbl.lab.code.mx", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.file.number", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("person.ocupacion", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.age", null, null).toUpperCase().replace(":", ""));
        columnas.add(messageSource.getMessage("lbl.age.um", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("person.sexo", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.pregnant", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.silais", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.muni", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.health.unit", null, null));
        columnas.add(messageSource.getMessage("lbl.consigned.unit", null, null));
        columnas.add(messageSource.getMessage("lbl.adn.number", null, null));
        columnas.add(messageSource.getMessage("lbl.sample.quality", null, null));
        columnas.add(messageSource.getMessage("lbl.results", null, null));
        columnas.add(messageSource.getMessage("lbl.result.date", null, null));
        columnas.add(messageSource.getMessage("lbl.delivery.date.results", null, null));
        columnas.add(messageSource.getMessage("lbl.reception.datetime", null, null));
        columnas.add(messageSource.getMessage("lbl.observations", null, null));
    }

    private void setNombreColumnasVIHSeguimiento(List<String> columnas){
        columnas.add(messageSource.getMessage("lbl.lab.code", null, null));
        columnas.add(messageSource.getMessage("lbl.lab.code.mx", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.file.number", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("person.ocupacion", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.age", null, null).toUpperCase().replace(":", ""));
        columnas.add(messageSource.getMessage("lbl.age.um", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("person.sexo", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.pregnant", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.silais", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.muni", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.health.unit", null, null));
        columnas.add(messageSource.getMessage("lbl.consigned.unit", null, null));
        columnas.add(messageSource.getMessage("lbl.result.cpml", null, null));
        columnas.add(messageSource.getMessage("lbl.processing.date", null, null));
        columnas.add(messageSource.getMessage("lbl.reception.datetime", null, null));
        columnas.add(messageSource.getMessage("lbl.delivery.date.cv", null, null));
        columnas.add(messageSource.getMessage("lbl.observations.cv", null, null));
        columnas.add(messageSource.getMessage("lbl.consigned", null, null));
        columnas.add(messageSource.getMessage("lbl.cd3.result", null, null));
        columnas.add(messageSource.getMessage("lbl.cd4.result", null, null));
        columnas.add(messageSource.getMessage("lbl.cd8.result", null, null));
        columnas.add(messageSource.getMessage("lbl.cd4.cd8.result", null, null));
        columnas.add(messageSource.getMessage("lbl.cd4.percent", null, null));
        columnas.add(messageSource.getMessage("lbl.cd.date", null, null));
        columnas.add(messageSource.getMessage("lbl.observations.cd", null, null));
        columnas.add(messageSource.getMessage("lbl.delivery.date.cd", null, null));

    }

    private void setNombreColumnasVIHSerologia(List<String> columnas){
        columnas.add(messageSource.getMessage("lbl.lab.code", null, null));
        columnas.add(messageSource.getMessage("lbl.lab.code.mx", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.age", null, null).toUpperCase().replace(":", ""));
        columnas.add(messageSource.getMessage("lbl.age.um", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("person.sexo", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.pregnant", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.silais", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.muni", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.health.unit", null, null));
        columnas.add(messageSource.getMessage("lbl.consigned.unit", null, null));
        columnas.add(messageSource.getMessage("lbl.A1", null, null)+messageSource.getMessage("lbl.CS", null, null));
        columnas.add(messageSource.getMessage("lbl.A2", null, null)+messageSource.getMessage("lbl.CS", null, null));
        columnas.add(messageSource.getMessage("lbl.A1", null, null)+messageSource.getMessage("lbl.CNDR", null, null));
        columnas.add(messageSource.getMessage("lbl.A2", null, null)+messageSource.getMessage("lbl.CNDR", null, null));
        columnas.add(messageSource.getMessage("lbl.A1", null, null)+messageSource.getMessage("lbl.CS", null, null));
        columnas.add(messageSource.getMessage("lbl.fec", null, null)+messageSource.getMessage("lbl.A1", null, null));
        columnas.add(messageSource.getMessage("lbl.fec", null, null)+messageSource.getMessage("lbl.A2", null, null));
        columnas.add(messageSource.getMessage("lbl.elisa", null, null)+messageSource.getMessage("lbl.CS", null, null));
        columnas.add(messageSource.getMessage("lbl.elisa", null, null)+messageSource.getMessage("lbl.CNDR", null, null));
        columnas.add(messageSource.getMessage("lbl.fec", null, null)+messageSource.getMessage("lbl.elisa", null, null));
        columnas.add(messageSource.getMessage("lbl.elisa.dup", null, null));
        columnas.add(messageSource.getMessage("lbl.fec.elisa.dup", null, null));
        columnas.add(messageSource.getMessage("lbl.Western.Blot", null, null));
        columnas.add(messageSource.getMessage("lbl.fecWB", null, null));
        columnas.add(messageSource.getMessage("lbl.final.result", null, null));
        columnas.add(messageSource.getMessage("lbl.observation.short", null, null));
        columnas.add(messageSource.getMessage("lbl.reception.datetime", null, null).replace(":", ""));
        columnas.add(messageSource.getMessage("lbl.delivery.date.results", null, null));
        columnas.add(messageSource.getMessage("lbl.observations", null, null));
        columnas.add(messageSource.getMessage("lbl.purpose.mx", null, null));
        columnas.add(messageSource.getMessage("lbl.current.date", null, null));

    }

    private void setNombreColumnasIFIVR(List<String> columnas){
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
        columnas.add(messageSource.getMessage("lbl.ifi.flu.a", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.ifi.flu.b", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.ifi.flu.rsv", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.ifi.flu.adv", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.ifi.flu.piv1", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.ifi.flu.piv2", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.ifi.flu.piv3", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.ifi.flu.mpv", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.date.proc", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.res.final.date.long", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.res.final", null, null).toUpperCase());
    }

    private void setNombreColumnasBioMolVR(List<String> columnas){
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
        columnas.add(messageSource.getMessage("lbl.pcr.flu.a", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.pcr.flu.a.sub", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.pcr.flu.a.date", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.pcr.flu.b", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.pcr.flu.b.linaje", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.pcr.flu.b.date", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.res.final", null, null).toUpperCase());
        columnas.add(messageSource.getMessage("lbl.res.final.date.long", null, null).toUpperCase());
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

    public Integer getSemanaEpi(Date fechaSemana) throws Exception{
        CalendarioEpi calendario = null;
        if (fechaSemana != null)
            calendario = calendarioEpiService.getCalendarioEpiByFecha(DateUtil.DateToString(fechaSemana, "dd/MM/yyyy"));
        if (calendario != null) {
            return calendario.getNoSemana();
        } else return null;
    }


    private void setDatosDengue(List<DaSolicitudDx> dxList, List<Object[]> registrosPos, List<Object[]> registrosNeg, String codigoLab, boolean incluirMxInadecuadas, List<Object[]> registrosMxInadec, int numColumnas) throws Exception{
// create data rows
        int rowCountPos = 1;
        int rowCountNeg = 1;
        int rowCountInadec = 1;
        for (DaSolicitudDx solicitudDx : dxList) {
            String nombres = "";
            String apellidos = "";

            DaSindFebril sindFebril = sindFebrilService.getDaSindFebril(solicitudDx.getIdTomaMx().getIdNotificacion().getIdNotificacion());
            Object[] registro = new Object[numColumnas];
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
            registro[6] = (solicitudDx.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre()://silais en la notif
                    (solicitudDx.getIdTomaMx().getCodSilaisAtencion()!=null?solicitudDx.getIdTomaMx().getCodSilaisAtencion().getNombre():"")); //silais en la toma mx
            registro[7] = (solicitudDx.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion().getMunicipio().getNombre(): //unidad en la noti
                    (solicitudDx.getIdTomaMx().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getCodUnidadAtencion().getMunicipio().getNombre():"")); //unidad en la toma
            registro[8] = (solicitudDx.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre()://unidad en la noti
                    (solicitudDx.getIdTomaMx().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getCodUnidadAtencion().getNombre():""));//unidad en la toma
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
            registro[11] = DateUtil.DateToString(solicitudDx.getIdTomaMx().getIdNotificacion().getFechaInicioSintomas(),"dd/MM/yyyy");
            registro[12] = DateUtil.DateToString(solicitudDx.getIdTomaMx().getFechaHTomaMx(),"dd/MM/yyyy");
            RecepcionMx recepcionMx = recepcionMxService.getRecepcionMxByCodUnicoMx(solicitudDx.getIdTomaMx().getCodigoUnicoMx(), codigoLab);
            if (recepcionMx!=null){
                registro[13] = DateUtil.DateToString(recepcionMx.getFechaRecibido()!=null?recepcionMx.getFechaRecibido():recepcionMx.getFechaHoraRecepcion(),"dd/MM/yyyy");
            }

            validarPCRIgMDengue(registro, solicitudDx.getIdSolicitudDx());
            registro[18] = getSemanaEpi(solicitudDx.getIdTomaMx().getIdNotificacion().getFechaInicioSintomas());

            registro[20] = parseFinalResultDetails(solicitudDx.getIdSolicitudDx());
            registro[22] = DateUtil.DateToString(solicitudDx.getFechaAprobacion(),"dd/MM/yyyy");
            registro[23] = (solicitudDx.getIdTomaMx().getIdNotificacion().getMunicipioResidencia()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getMunicipioResidencia().getNombre():"");
            registro[24] = (sindFebril!=null?DateUtil.DateToString(sindFebril.getFechaFicha(),"dd/MM/yyyy"):"");
            registro[25] = DateUtil.DateToString(solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getFechaNacimiento(),"dd/MM/yyyy");
            String sexo = solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSexo().getCodigo();
            registro[26] = sexo.substring(sexo.length()-1, sexo.length());
            registro[27] = (sindFebril!=null && sindFebril.getCodProcedencia()!=null?sindFebril.getCodProcedencia().getValor():"");
            registro[28] = (solicitudDx.getIdTomaMx().getIdNotificacion().getEmbarazada()!=null? solicitudDx.getIdTomaMx().getIdNotificacion().getEmbarazada().getValor():"");
            registro[29] = solicitudDx.getIdTomaMx().getIdNotificacion().getSemanasEmbarazo();
            registro[30] = (sindFebril!=null && sindFebril.getHosp()!=null?sindFebril.getHosp().getValor():"");
            registro[31] = (sindFebril!=null?DateUtil.DateToString(sindFebril.getFechaIngreso(),"dd/MM/yyyy"):"");
            registro[32] = (sindFebril!=null && sindFebril.getFallecido()!=null?sindFebril.getFallecido().getValor():"");
            registro[33] = (sindFebril!=null?DateUtil.DateToString(sindFebril.getFechaFallecido(),"dd/MM/yyyy"):"");
            if (sindFebril!=null && sindFebril.getDxPresuntivo()!=null && !sindFebril.getDxPresuntivo().isEmpty()) {
                registro[34] = sindFebril.getDxPresuntivo();
            } else {
                registro[34] = parseDxs(solicitudDx.getIdTomaMx().getIdTomaMx(), codigoLab);
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

    private void setDatosChikungunya(List<DaSolicitudDx> dxList, List<Object[]> registrosPos, List<Object[]> registrosNeg, boolean incluirMxInadecuadas, List<Object[]> registrosMxInadec, String codigoLab, int numColumnas) throws Exception{
// create data rows
        int rowCountPos = 1;
        int rowCountNeg = 1;
        int rowCountInadec = 1;
        for (DaSolicitudDx solicitudDx : dxList) {
            String nombres = "";
            String apellidos = "";
            DaSindFebril sindFebril = sindFebrilService.getDaSindFebril(solicitudDx.getIdTomaMx().getIdNotificacion().getIdNotificacion());
            Object[] registro = new Object[numColumnas];
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
            registro[7] = (solicitudDx.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre(): //SILAIS  en la notifi
                    (solicitudDx.getIdTomaMx().getCodSilaisAtencion()!=null?solicitudDx.getIdTomaMx().getCodSilaisAtencion().getNombre():"")); //silais en la toma mx
            registro[8] = (solicitudDx.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion().getMunicipio().getNombre(): //unidad en la notifi
                    (solicitudDx.getIdTomaMx().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getCodUnidadAtencion().getMunicipio().getNombre():"")); //unidad en la toma mx
            registro[9] = (solicitudDx.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre()://unidad en la noti
                    (solicitudDx.getIdTomaMx().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getCodUnidadAtencion().getNombre():"")); //unidad en la toma mx
            String sexo = solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSexo().getCodigo();
            registro[10] = sexo.substring(sexo.length()-1, sexo.length());
            registro[11] = DateUtil.DateToString(solicitudDx.getIdTomaMx().getIdNotificacion().getFechaInicioSintomas(),"dd/MM/yyyy");
            registro[12] = DateUtil.DateToString(solicitudDx.getIdTomaMx().getFechaHTomaMx(),"dd/MM/yyyy");
            RecepcionMx recepcionMx = recepcionMxService.getRecepcionMxByCodUnicoMx(solicitudDx.getIdTomaMx().getCodigoUnicoMx(), codigoLab);
            if (recepcionMx!=null){
                registro[13] = DateUtil.DateToString(recepcionMx.getFechaRecibido()!=null?recepcionMx.getFechaRecibido():recepcionMx.getFechaHoraRecepcion(),"dd/MM/yyyy");
            }
            validarPCRIgMChikunZika(registro, solicitudDx.getIdSolicitudDx(), 14, 15, 16, 17);
            registro[18] = parseFinalResultDetails(solicitudDx.getIdSolicitudDx());
            if (registro[18].toString().toLowerCase().contains("positivo")) {
                registro[0]= rowCountPos++;
                registrosPos.add(registro);
            } else if (registro[18].toString().toLowerCase().contains("negativo")) {
                registro[0]= rowCountNeg++;
                registrosNeg.add(registro);
            } else if (incluirMxInadecuadas && registro[18].toString().toLowerCase().contains("inadecuada")){
                registro[0]= rowCountInadec++;
                registrosMxInadec.add(registro);
            }
            registro[19] = DateUtil.DateToString(solicitudDx.getFechaAprobacion(),"dd/MM/yyyy");
            registro[20] = getSemanaEpi(solicitudDx.getIdTomaMx().getIdNotificacion().getFechaInicioSintomas());
            registro[21] = (solicitudDx.getIdTomaMx().getIdNotificacion().getMunicipioResidencia()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getMunicipioResidencia().getDependenciaSilais().getNombre():"");
            registro[22] = (solicitudDx.getIdTomaMx().getIdNotificacion().getMunicipioResidencia()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getMunicipioResidencia().getNombre():"");
            if (sindFebril!=null && sindFebril.getDxPresuntivo()!=null && !sindFebril.getDxPresuntivo().isEmpty()) {
                registro[23] = sindFebril.getDxPresuntivo();
            } else {
                registro[23] = parseDxs(solicitudDx.getIdTomaMx().getIdTomaMx(), codigoLab);
            }
        }
    }

    private void setDatosZika(List<DaSolicitudDx> dxList, List<Object[]> registrosPos, List<Object[]> registrosNeg, String codigoLab, boolean incluirMxInadecuadas, List<Object[]> registrosMxInadec, int numColumnas) throws Exception{
// create data rows
        int rowCountPos = 1;
        int rowCountNeg = 1;
        int rowCountInadec = 1;
        for (DaSolicitudDx solicitudDx : dxList) {
            String nombres = "";
            String apellidos = "";

            Object[] registro = new Object[numColumnas];
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
            registro[6] = (solicitudDx.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre(): //silais en la notificacion
                    (solicitudDx.getIdTomaMx().getCodSilaisAtencion()!=null?solicitudDx.getIdTomaMx().getCodSilaisAtencion().getNombre():"")); //silais en la toma mx
            registro[7] = (solicitudDx.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion().getMunicipio().getNombre()://unidad en la notificacion
                    (solicitudDx.getIdTomaMx().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getCodUnidadAtencion().getMunicipio().getNombre():"")); //unidad en la toma mx
            registro[8] = (solicitudDx.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre()://unidad en la notificacion
                    (solicitudDx.getIdTomaMx().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getCodUnidadAtencion().getNombre():""));//unidad en la toma mx
            String direccion = solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getDireccionResidencia();
            if (solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getTelefonoResidencia()!=null || solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getTelefonoMovil()!=null ){
                direccion += ". TEL. ";
                direccion+= (solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getTelefonoResidencia()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getTelefonoResidencia()+",":"");
                direccion+= (solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getTelefonoMovil()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getTelefonoMovil():"");
            }
            registro[9] = direccion;
            registro[10] = DateUtil.DateToString(solicitudDx.getIdTomaMx().getIdNotificacion().getFechaInicioSintomas(),"dd/MM/yyyy");
            registro[11] = DateUtil.DateToString(solicitudDx.getIdTomaMx().getFechaHTomaMx(),"dd/MM/yyyy");
            RecepcionMx recepcionMx = recepcionMxService.getRecepcionMxByCodUnicoMx(solicitudDx.getIdTomaMx().getCodigoUnicoMx(), codigoLab);
            if (recepcionMx!=null){
                registro[12] = DateUtil.DateToString(recepcionMx.getFechaRecibido()!=null?recepcionMx.getFechaRecibido():recepcionMx.getFechaHoraRecepcion(),"dd/MM/yyyy");
            }
            validarPCRIgMChikunZika(registro, solicitudDx.getIdSolicitudDx(), 13, 14, 15, 16);

            registro[17] = parseFinalResultDetails(solicitudDx.getIdSolicitudDx());
            registro[18] = DateUtil.DateToString(solicitudDx.getFechaAprobacion(),"dd/MM/yyyy");

            registro[19] = (solicitudDx.getIdTomaMx().getIdNotificacion().getEmbarazada()!=null? solicitudDx.getIdTomaMx().getIdNotificacion().getEmbarazada().getValor():"");
            registro[20] = (solicitudDx.getIdTomaMx().getIdNotificacion().getMunicipioResidencia()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getMunicipioResidencia().getDependenciaSilais().getNombre():"");
            registro[21] = "";
            String sexo = solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSexo().getCodigo();
            registro[22] = sexo.substring(sexo.length() - 1, sexo.length());
            //la posici�n que contiene el resultado final
            if (registro[17].toString().toLowerCase().contains("positivo")) {
                registro[0]= rowCountPos++;
                registrosPos.add(registro);
            } else if (registro[17].toString().toLowerCase().contains("negativo")) {
                registro[0]= rowCountNeg++;
                registrosNeg.add(registro);
            } else if (incluirMxInadecuadas && registro[17].toString().toLowerCase().contains("inadecuada")){
                registro[0]= rowCountInadec++;
                registrosMxInadec.add(registro);
            }
            registro[23] = getSemanaEpi(solicitudDx.getIdTomaMx().getIdNotificacion().getFechaInicioSintomas());
        }
    }

    private void setDatosLepto(List<DaSolicitudDx> dxList, List<Object[]> registrosPos, List<Object[]> registrosNeg, boolean incluirMxInadecuadas, List<Object[]> registrosMxInadec, int numColumnas) throws Exception{
// create data rows
        int rowCountPos = 1;
        int rowCountNeg = 1;
        int rowCountInadec = 1;
        for (DaSolicitudDx solicitudDx : dxList) {
            String nombres = "";
            String apellidos = "";

            DaSindFebril sindFebril = sindFebrilService.getDaSindFebril(solicitudDx.getIdTomaMx().getIdNotificacion().getIdNotificacion());
            Object[] registro = new Object[numColumnas];
            registro[1] = solicitudDx.getIdTomaMx().getCodigoLab();
            registro[2] = parseFinalResultDetails(solicitudDx.getIdSolicitudDx());
            registro[3] = DateUtil.DateToString(solicitudDx.getFechaAprobacion(),"dd/MM/yyyy");
            nombres = solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getPrimerNombre();
            if (solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre()!=null)
                nombres += " "+solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre();
            registro[4] = nombres;

            apellidos = solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getPrimerApellido();
            if (solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido()!=null)
                apellidos += " "+solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido();
            registro[5] = apellidos;
            registro[6] = (solicitudDx.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre(): //silais en la notificacion
                    (solicitudDx.getIdTomaMx().getCodSilaisAtencion()!=null?solicitudDx.getIdTomaMx().getCodSilaisAtencion().getNombre():"")); //silais en la toma mx
            registro[7] = (solicitudDx.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion().getMunicipio().getNombre(): //unidad en la notif
                    (solicitudDx.getIdTomaMx().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getCodUnidadAtencion().getMunicipio().getNombre():"")); //unidad en la toma mx
            registro[8] = (solicitudDx.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre()://unidad en la notif
                    (solicitudDx.getIdTomaMx().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getCodUnidadAtencion().getNombre():""));//unidad en la toma mx
            String direccion = solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getDireccionResidencia();
            if (solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getTelefonoResidencia()!=null || solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getTelefonoMovil()!=null ){
                direccion += ". TEL. ";
                direccion+= (solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getTelefonoResidencia()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getTelefonoResidencia()+",":"");
                direccion+= (solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getTelefonoMovil()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getTelefonoMovil():"");
            }
            registro[9] = direccion;
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
            registro[10] = edad;
            registro[11] = medidaEdad;
            String sexo = solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSexo().getCodigo();
            registro[12] = sexo.substring(sexo.length() - 1, sexo.length());
            registro[13] = DateUtil.DateToString(solicitudDx.getIdTomaMx().getIdNotificacion().getFechaInicioSintomas(),"dd/MM/yyyy");
            registro[14] = DateUtil.DateToString(solicitudDx.getIdTomaMx().getFechaHTomaMx(),"dd/MM/yyyy");
            registro[15] = (solicitudDx.getIdTomaMx().getIdNotificacion().getMunicipioResidencia()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getMunicipioResidencia().getDependenciaSilais().getNombre():"");
            CalendarioEpi calendario = null;
            if (solicitudDx.getIdTomaMx().getIdNotificacion().getFechaInicioSintomas()!=null)
                calendario = calendarioEpiService.getCalendarioEpiByFecha(DateUtil.DateToString(solicitudDx.getIdTomaMx().getIdNotificacion().getFechaInicioSintomas(),"dd/MM/yyyy"));
            if (calendario!=null) {
                registro[16] = calendario.getNoSemana();
            }
            registro[17] = (sindFebril!=null && sindFebril.getHosp()!=null?sindFebril.getHosp().getValor():"");
            registro[18] = (sindFebril!=null?DateUtil.DateToString(sindFebril.getFechaIngreso(),"dd/MM/yyyy"):"");
            registro[19] = (sindFebril!=null && sindFebril.getFallecido()!=null?sindFebril.getFallecido().getValor():"");
            registro[20] = (sindFebril!=null?DateUtil.DateToString(sindFebril.getFechaFallecido(),"dd/MM/yyyy"):"");
            //la posici�n que contiene el resultado final
            if (registro[2].toString().toLowerCase().contains("no reactor")) {
                registro[0]= rowCountNeg++;
                registrosNeg.add(registro);
            }else if (registro[2].toString().toLowerCase().contains("reactor")) {
                registro[0]= rowCountPos++;
                registrosPos.add(registro);
            } else if (incluirMxInadecuadas && registro[2].toString().toLowerCase().contains("inadecuada")){
                registro[0]= rowCountInadec++;
                registrosMxInadec.add(registro);
            }
        }
    }

    private void setDatosXpertTB(List<DaSolicitudDx> dxList, List<Object[]> registrosPos, List<Object[]> registrosNeg, boolean incluirMxInadecuadas, List<Object[]> registrosMxInadec, int numColumnas) throws Exception{
// create data rows
        int rowCountPos = 1;
        int rowCountNeg = 1;
        int rowCountInadec = 1;
        for (DaSolicitudDx solicitudDx : dxList) {
            String nombres = "";
            String apellidos = "";

            Object[] registro = new Object[numColumnas];
            registro[1] = solicitudDx.getIdTomaMx().getCodigoLab();
            nombres = solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getPrimerNombre();
            if (solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre()!=null)
                nombres += " "+solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre();

            apellidos = solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getPrimerApellido();
            if (solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido()!=null)
                apellidos += " "+solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido();
            registro[2] = nombres + " " + apellidos;
            String sexo = solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSexo().getCodigo();
            registro[3] = sexo.substring(sexo.length() - 1, sexo.length());
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
            registro[6] = (solicitudDx.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre(): //silais en la notificacion
                    (solicitudDx.getIdTomaMx().getCodSilaisAtencion()!=null?solicitudDx.getIdTomaMx().getCodSilaisAtencion().getNombre():"")); //silais en la toma mx
            registro[7] = (solicitudDx.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre()://unidad en la notif
                    (solicitudDx.getIdTomaMx().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getCodUnidadAtencion().getNombre():""));//unidad en la toma mx
            registro[12] = solicitudDx.getIdTomaMx().getCodTipoMx().getNombre();

            validarPCRTB(registro, solicitudDx.getIdSolicitudDx(), 15, 14);

             if (incluirMxInadecuadas && registro[15].toString().toLowerCase().contains("inadecuada")){
                registro[0]= rowCountInadec++;
                registrosMxInadec.add(registro);
            }else {
                 //la posici�n que contiene el resultado final
                 registro[0]= rowCountPos++;
                 registrosPos.add(registro);
             }
        }
    }

    private void setDatosCultivoTB(List<DaSolicitudDx> dxList, List<Object[]> registrosPos, List<Object[]> registrosNeg, boolean incluirMxInadecuadas, List<Object[]> registrosMxInadec, int numColumnas) throws Exception{
// create data rows
        int rowCountPos = 1;
        int rowCountInadec = 1;
        for (DaSolicitudDx solicitudDx : dxList) {
            String nombres = "";
            String apellidos = "";

            Object[] registro = new Object[numColumnas];
            registro[1] = solicitudDx.getIdTomaMx().getCodigoLab();
            nombres = solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getPrimerNombre();
            if (solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre()!=null)
                nombres += " "+solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre();

            apellidos = solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getPrimerApellido();
            if (solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido()!=null)
                apellidos += " "+solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido();
            registro[2] = nombres + " " + apellidos;
            String sexo = solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSexo().getCodigo();
            registro[3] = sexo.substring(sexo.length() - 1, sexo.length());
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
            registro[6] = (solicitudDx.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre(): //silais en la notificacion
                    (solicitudDx.getIdTomaMx().getCodSilaisAtencion()!=null?solicitudDx.getIdTomaMx().getCodSilaisAtencion().getNombre():"")); //silais en la toma mx
            registro[7] = (solicitudDx.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre()://unidad en la notif
                    (solicitudDx.getIdTomaMx().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getCodUnidadAtencion().getNombre():""));//unidad en la toma mx
            registro[12] = solicitudDx.getIdTomaMx().getCodTipoMx().getNombre();

            validarCultivoTB(registro, solicitudDx.getIdSolicitudDx());

            if (incluirMxInadecuadas && registro[19].toString().toLowerCase().contains("inadecuada")){
                registro[0]= rowCountInadec++;
                registrosMxInadec.add(registro);
            }else {
                //la posici�n que contiene el resultado final
                registro[0]= rowCountPos++;
                registrosPos.add(registro);
            }

            List<DaSolicitudDx> buscarCultivoTb = tomaMxService.getSoliDxAprobByIdToma(solicitudDx.getIdTomaMx().getIdTomaMx());
            for(DaSolicitudDx cultivoDx : buscarCultivoTb){
                if (cultivoDx.getCodDx().getNombre().toLowerCase().contains("mycobacterium") && (cultivoDx.getCodDx().getNombre().toLowerCase().contains("tuberculosis") || cultivoDx.getCodDx().getNombre().toLowerCase().contains("tb"))){
                    validarPCRTB(registro, cultivoDx.getIdSolicitudDx(), 15, 14);
                }
            }
        }
    }

    private void setDatosVIHMolecular(List<DaSolicitudDx> dxList, List<Object[]> registros, int numColumnas, String codigoLab) throws Exception{
// create data rows
        int rowCountPos = 1;
        int rowCountNeg = 1;
        int rowCountInadec = 1;
        for (DaSolicitudDx solicitudDx : dxList) {

            Object[] registro = new Object[numColumnas];
            registro[0] = solicitudDx.getIdTomaMx().getCodigoLab();
            registro[1] = solicitudDx.getIdTomaMx().getIdNotificacion().getCodigoPacienteVIH();
            registro[2] = "expediente";
            registro[3] = "ocupacion";
            registro[4] = "Edad";
            registro[5] = "En";
            registro[6] = "Sexo";
            registro[7] = "Embarazada";
            registro[8] = (solicitudDx.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre(): //silais en la notificacion
                    (solicitudDx.getIdTomaMx().getCodSilaisAtencion()!=null?solicitudDx.getIdTomaMx().getCodSilaisAtencion().getNombre():"")); //silais en la toma mx
            registro[9] = (solicitudDx.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion().getMunicipio().getNombre(): //unidad en la notif
                    (solicitudDx.getIdTomaMx().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getCodUnidadAtencion().getMunicipio().getNombre():"")); //unidad en la toma mx
            registro[10] = (solicitudDx.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre()://unidad en la notif
                    (solicitudDx.getIdTomaMx().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getCodUnidadAtencion().getNombre():""));//unidad en la toma mx
            registro[11] = "UnidadConsignada";
            registro[12] = "NumeroADN";
            RecepcionMx recepcionMx = recepcionMxService.getRecepcionMxByCodUnicoMx(solicitudDx.getIdTomaMx().getCodigoLab(), codigoLab);
            if (recepcionMx!=null){
                registro[13] = recepcionMx.getCalidadMx().getValor();
                if (recepcionMx.getFechaRecibido()!=null) {
                    registro[17] = DateUtil.DateToString(recepcionMx.getFechaRecibido(),"dd/MM/yyyy");
                }else{
                    registro[17] = DateUtil.DateToString(recepcionMx.getFechaHoraRecepcion(),"dd/MM/yyyy");
                }
            }
            registro[14] = parseFinalResultDetails(solicitudDx.getIdSolicitudDx(), "resultado");
            registro[15] = DateUtil.DateToString(solicitudDx.getFechaAprobacion(),"dd/MM/yyyy");
            registro[16] = "FechaEntregaResultado";

            registro[18] = parseFinalResultDetails(solicitudDx.getIdSolicitudDx(), "observaci");




            /*
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
            registro[10] = edad;
            registro[11] = medidaEdad;
            String sexo = solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSexo().getCodigo();
            registro[12] = sexo.substring(sexo.length() - 1, sexo.length());
            */
            //la posici�n que contiene el resultado final
            registros.add(registro);
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
            registro[7] = (solicitudDx.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre(): //silais en la notificacion
                    (solicitudDx.getIdTomaMx().getCodSilaisAtencion()!=null?solicitudDx.getIdTomaMx().getCodSilaisAtencion().getNombre():"")); //solais en la toma mx
            registro[8] = (solicitudDx.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion().getMunicipio().getNombre(): //unidad en la noti
                    (solicitudDx.getIdTomaMx().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getCodUnidadAtencion().getMunicipio().getNombre():"")); //unidad en la toma mx
            registro[9] = (solicitudDx.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre(): //unidad en la noti
                    (solicitudDx.getIdTomaMx().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getCodUnidadAtencion().getNombre():"")); //unidad en la toma mx
            String sexo = solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSexo().getCodigo();
            registro[10] = sexo.substring(sexo.length()-1, sexo.length());
            registro[11] = DateUtil.DateToString(solicitudDx.getIdTomaMx().getIdNotificacion().getFechaInicioSintomas(),"dd/MM/yyyy");
            registro[12] = DateUtil.DateToString(solicitudDx.getIdTomaMx().getFechaHTomaMx(),"dd/MM/yyyy");
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

    private void setDatosIFIVR(List<DaSolicitudDx> dxList, List<Object[]> registrosPos, List<Object[]> registrosNeg, boolean incluirMxInadecuadas, List<Object[]> registrosMxInadec, int numColumnas) throws Exception{
// create data rows
        int rowCountPos = 1;
        int rowCountNeg = 1;
        int rowCountInadec = 1;
        for (DaSolicitudDx solicitudDx : dxList) {
            String nombres = "";
            String apellidos = "";

            Object[] registro = new Object[numColumnas];
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
            registro[7] = (solicitudDx.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre(): //silais en la notificacion
                    (solicitudDx.getIdTomaMx().getCodSilaisAtencion()!=null?solicitudDx.getIdTomaMx().getCodSilaisAtencion().getNombre():"")); //solais en la toma mx
            registro[8] = (solicitudDx.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion().getMunicipio().getNombre(): //unidad en la noti
                    (solicitudDx.getIdTomaMx().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getCodUnidadAtencion().getMunicipio().getNombre():"")); //unidad en la toma mx
            registro[9] = (solicitudDx.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre(): //unidad en la noti
                    (solicitudDx.getIdTomaMx().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getCodUnidadAtencion().getNombre():"")); //unidad en la toma mx
            String sexo = solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSexo().getCodigo();
            registro[10] = sexo.substring(sexo.length()-1, sexo.length());
            registro[11] = DateUtil.DateToString(solicitudDx.getIdTomaMx().getIdNotificacion().getFechaInicioSintomas(),"dd/MM/yyyy");
            registro[12] = DateUtil.DateToString(solicitudDx.getIdTomaMx().getFechaHTomaMx(),"dd/MM/yyyy");
            validarTipoIFI(registro, solicitudDx.getIdSolicitudDx());
            registro[22] = DateUtil.DateToString(solicitudDx.getFechaAprobacion(),"dd/MM/yyyy");
            registro[23] = parseFinalResultDetails(solicitudDx.getIdSolicitudDx());
            if (registro[23].toString().toLowerCase().contains("negativo")) {
                registro[0]= rowCountNeg++;
                registrosNeg.add(registro);
            } else if (incluirMxInadecuadas && registro[23].toString().toLowerCase().contains("inadecuada")){
                registro[0]= rowCountInadec++;
                registrosMxInadec.add(registro);
            }else if (!registro[23].toString().toLowerCase().contains("indetermin")) {
                registro[0]= rowCountPos++;
                registrosPos.add(registro);
            }
        }
    }

    private void setDatosBioMolVR(List<DaSolicitudDx> dxList, List<Object[]> registrosPos, List<Object[]> registrosNeg, boolean incluirMxInadecuadas, List<Object[]> registrosMxInadec, int numColumnas) throws Exception{
// create data rows
        int rowCountPos = 1;
        int rowCountNeg = 1;
        int rowCountInadec = 1;
        for (DaSolicitudDx solicitudDx : dxList) {
            String nombres = "";
            String apellidos = "";

            Object[] registro = new Object[numColumnas];
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
            registro[7] = (solicitudDx.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre(): //silais en la notificacion
                    (solicitudDx.getIdTomaMx().getCodSilaisAtencion()!=null?solicitudDx.getIdTomaMx().getCodSilaisAtencion().getNombre():"")); //solais en la toma mx
            registro[8] = (solicitudDx.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion().getMunicipio().getNombre(): //unidad en la noti
                    (solicitudDx.getIdTomaMx().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getCodUnidadAtencion().getMunicipio().getNombre():"")); //unidad en la toma mx
            registro[9] = (solicitudDx.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre(): //unidad en la noti
                    (solicitudDx.getIdTomaMx().getCodUnidadAtencion()!=null?solicitudDx.getIdTomaMx().getCodUnidadAtencion().getNombre():"")); //unidad en la toma mx
            String sexo = solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSexo().getCodigo();
            registro[10] = sexo.substring(sexo.length()-1, sexo.length());
            registro[11] = DateUtil.DateToString(solicitudDx.getIdTomaMx().getIdNotificacion().getFechaInicioSintomas(),"dd/MM/yyyy");
            registro[12] = DateUtil.DateToString(solicitudDx.getIdTomaMx().getFechaHTomaMx(),"dd/MM/yyyy");
            validarPCRVirusResp(registro, solicitudDx.getIdSolicitudDx());
            registro[20] = DateUtil.DateToString(solicitudDx.getFechaAprobacion(),"dd/MM/yyyy");
            registro[19] = parseFinalResultDetails(solicitudDx.getIdSolicitudDx());
            if (registro[19].toString().toLowerCase().contains("negativo")) {
                registro[0]= rowCountNeg++;
                registrosNeg.add(registro);
            } else if (incluirMxInadecuadas && registro[19].toString().toLowerCase().contains("inadecuada")){
                registro[0]= rowCountInadec++;
                registrosMxInadec.add(registro);
            }else if (!registro[19].toString().toLowerCase().contains("indetermin")) {
                registro[0]= rowCountPos++;
                registrosPos.add(registro);
            }
        }
    }

    private void validarTipoIFI(Object[] dato, String idSolicitudDx){

        List<OrdenExamen> examenes = ordenExamenMxService.getOrdenesExamenByIdSolicitud(idSolicitudDx);
        Date fechaProcesamiento = null;
        for (OrdenExamen examen : examenes) {
            List<DetalleResultado> resultados = resultadosService.getDetallesResultadoActivosByExamen(examen.getIdOrdenExamen());


            String detalleResultado = "";
            for (DetalleResultado resultado : resultados) {
                if (resultado.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                    Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(resultado.getValor());
                    detalleResultado = cat_lista.getEtiqueta();
                } else if (resultado.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                    detalleResultado = (Boolean.valueOf(resultado.getValor()) ? "lbl.yes" : "lbl.no");
                }
                fechaProcesamiento = resultado.getFechahProcesa();
            }
            if (resultados.size() > 0) {
                String nombreEx = examen.getCodExamen().getNombre().toUpperCase();
                if (nombreEx.contains("INFLUENZA A") || nombreEx.contains("FLUA")){
                    dato[13] = detalleResultado;
                }else if (nombreEx.contains("INFLUENZA B") || nombreEx.contains("FLUB")){
                    dato[14] = detalleResultado;
                }else if (nombreEx.contains("VIRUS SINCITIAL RESPIRATORIO") || nombreEx.contains("RSV")){
                    dato[15] = detalleResultado;
                }else if (nombreEx.contains("ADENOVIRUS") || nombreEx.contains("ADV")){
                    dato[16] = detalleResultado;
                }else if (nombreEx.contains("PIV1")){
                    dato[17] = detalleResultado;
                }else if (nombreEx.contains("PIV2")){
                    dato[18] = detalleResultado;
                }else if (nombreEx.contains("PIV3")){
                    dato[19] = detalleResultado;
                }else if (nombreEx.contains("METAPNEUMOVIRUS") || nombreEx.contains("MPV")){
                    dato[20] = detalleResultado;
                }
            }
        }
        dato[21] = DateUtil.DateToString(fechaProcesamiento,"dd/MM/yyyy");
    }

    private void validarPCRVirusResp(Object[] dato, String idSolicitudDx){

        List<OrdenExamen> examenes = ordenExamenMxService.getOrdenesExamenByIdSolicitud(idSolicitudDx);
        for (OrdenExamen examen : examenes) {
            if (examen.getCodExamen().getNombre().toUpperCase().contains("FLU A")){
                List<DetalleResultado> resultados = resultadosService.getDetallesResultadoActivosByExamen(examen.getIdOrdenExamen());
                Date fechaProcesamiento = null;
                String detalleResultado = "";
                String subtipo = "";
                for (DetalleResultado resultado : resultados) {
                    if (resultado.getRespuesta().getNombre().toLowerCase().contains("subtipo")){
                        Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(resultado.getValor());
                        subtipo = cat_lista.getEtiqueta();
                    }else{
                        if (resultado.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                            Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(resultado.getValor());
                            detalleResultado = cat_lista.getEtiqueta();
                        } else if (resultado.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                            detalleResultado = (Boolean.valueOf(resultado.getValor()) ? "lbl.yes" : "lbl.no");
                        }
                    }
                    fechaProcesamiento = resultado.getFechahProcesa();
                }
                if (resultados.size() > 0) {
                    dato[13] = detalleResultado;
                    dato[14] = subtipo;
                    dato[15] = DateUtil.DateToString(fechaProcesamiento,"dd/MM/yyyy");
                }
            }else if (examen.getCodExamen().getNombre().toUpperCase().contains("FLU B")){
                List<DetalleResultado> resultados = resultadosService.getDetallesResultadoActivosByExamen(examen.getIdOrdenExamen());
                Date fechaProcesamiento = null;
                String detalleResultado = "";
                String linaje = "";
                for (DetalleResultado resultado : resultados) {
                    if (resultado.getRespuesta().getNombre().toLowerCase().contains("linaje")){
                        Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(resultado.getValor());
                        linaje = cat_lista.getEtiqueta();
                    }else{
                        if (resultado.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                            Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(resultado.getValor());
                            detalleResultado = cat_lista.getEtiqueta();
                        } else if (resultado.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                            detalleResultado = (Boolean.valueOf(resultado.getValor()) ? "lbl.yes" : "lbl.no");
                        }
                    }
                    fechaProcesamiento = resultado.getFechahProcesa();
                }
                if (resultados.size() > 0) {
                    dato[16] = detalleResultado;
                    dato[17] = linaje;
                    dato[18] = DateUtil.DateToString(fechaProcesamiento,"dd/MM/yyyy");
                }
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
                        serotipo = cat_lista.getEtiqueta();
                    }else{
                        if (resultado.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                            Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(resultado.getValor());
                            detalleResultado = cat_lista.getEtiqueta();
                        } else if (resultado.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                            detalleResultado = (Boolean.valueOf(resultado.getValor()) ? "lbl.yes" : "lbl.no");
                        }/* else {
                            detalleResultado = resultado.getValor();
                        }*/
                    }
                    fechaProcesamiento = resultado.getFechahProcesa();
                }
                if (resultados.size() > 0) {
                    dato[15] = detalleResultado;
                    dato[16] = serotipo;
                    dato[17] = DateUtil.DateToString(fechaProcesamiento,"dd/MM/yyyy");
                }
            }else if (examen.getCodExamen().getNombre().toUpperCase().contains("IGM")){
                List<DetalleResultado> resultados = resultadosService.getDetallesResultadoActivosByExamen(examen.getIdOrdenExamen());

                Date fechaProcesamiento = null;
                String detalleResultado = "";
                String densidad = "";
                for (DetalleResultado resultado : resultados) {
                    if (resultado.getRespuesta().getNombre().toLowerCase().contains("densidad optica")){
                        densidad = resultado.getValor();
                    }else {
                        if (resultado.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                            Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(resultado.getValor());
                            detalleResultado = cat_lista.getEtiqueta();
                        } else if (resultado.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                            detalleResultado = (Boolean.valueOf(resultado.getValor()) ? "lbl.yes" : "lbl.no");
                        }/*else {
                        detalleResultado = resultado.getValor();
                        }*/
                    }
                    fechaProcesamiento = resultado.getFechahProcesa();
                }
                if (resultados.size() > 0) {
                    dato[19] = detalleResultado;
                    dato[14] = DateUtil.DateToString(fechaProcesamiento,"dd/MM/yyyy");
                    dato[21] = densidad;
                }
            }
        }
    }

    private void validarPCRIgMChikunZika(Object[] dato, String idSolicitudDx, int indicePCR, int indiceFPCR, int indiceIgm, int indiceFIgm){

        List<OrdenExamen> examenes = ordenExamenMxService.getOrdenesExamenByIdSolicitud(idSolicitudDx);
        for (OrdenExamen examen : examenes) {
            if (examen.getCodExamen().getNombre().toUpperCase().contains("PCR")){
                List<DetalleResultado> resultados = resultadosService.getDetallesResultadoActivosByExamen(examen.getIdOrdenExamen());

                Date fechaProcesamiento = null;
                String detalleResultado = "";
                for (DetalleResultado resultado : resultados) {

                    if (resultado.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                        Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(resultado.getValor());
                        detalleResultado = cat_lista.getEtiqueta();
                    } else if (resultado.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                        detalleResultado = (Boolean.valueOf(resultado.getValor()) ? "lbl.yes" : "lbl.no");
                    }/* else {
                            detalleResultado = resultado.getValor();
                        }*/
                    fechaProcesamiento = resultado.getFechahProcesa();
                }
                if (resultados.size() > 0) {
                    dato[indicePCR] = detalleResultado;
                    dato[indiceFPCR] = DateUtil.DateToString(fechaProcesamiento,"dd/MM/yyyy");
                }
            }else if (examen.getCodExamen().getNombre().toUpperCase().contains("IGM")){
                List<DetalleResultado> resultados = resultadosService.getDetallesResultadoActivosByExamen(examen.getIdOrdenExamen());
                Date fechaProcesamiento = null;
                String detalleResultado = "";
                for (DetalleResultado resultado : resultados) {
                    if (resultado.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                        Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(resultado.getValor());
                        detalleResultado = cat_lista.getEtiqueta();
                    } else if (resultado.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                        detalleResultado = (Boolean.valueOf(resultado.getValor()) ? "lbl.yes" : "lbl.no");
                    }/* else {
                        detalleResultado = resultado.getValor();
                    }*/
                    fechaProcesamiento = resultado.getFechahProcesa();
                }
                if (resultados.size() > 0) {
                    dato[indiceIgm] = detalleResultado;
                    dato[indiceFIgm] = DateUtil.DateToString(fechaProcesamiento,"dd/MM/yyyy");
                }
            }
        }
    }

    private void validarPCRTB(Object[] dato, String idSolicitudDx, int indiceRes, int indiceFR){

        List<OrdenExamen> examenes = ordenExamenMxService.getOrdenesExamenByIdSolicitud(idSolicitudDx);
        for (OrdenExamen examen : examenes) {
            if (examen.getCodExamen().getNombre().toUpperCase().contains("XPERT")){
                List<DetalleResultado> resultados = resultadosService.getDetallesResultadoActivosByExamen(examen.getIdOrdenExamen());

                Date fechaProcesamiento = null;
                String detalleResultado = "";
                for (DetalleResultado resultado : resultados) {

                    if (resultado.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                        Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(resultado.getValor());
                        detalleResultado = (detalleResultado.isEmpty()?cat_lista.getEtiqueta():detalleResultado+"/"+cat_lista.getEtiqueta());
                    } else if (resultado.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                        String valorSN = (Boolean.valueOf(resultado.getValor()) ? "lbl.yes" : "lbl.no");
                        detalleResultado = (detalleResultado.isEmpty()?valorSN:detalleResultado+"/"+valorSN);
                    }/* else {
                            detalleResultado = resultado.getValor();
                        }*/
                    fechaProcesamiento = resultado.getFechahProcesa();
                }
                if (resultados.size() > 0) {
                    dato[indiceRes] = detalleResultado;
                    dato[indiceFR] = DateUtil.DateToString(fechaProcesamiento,"dd/MM/yyyy");
                }
            }
        }
    }

    private void validarCultivoTB(Object[] dato, String idSolicitudDx) throws Exception{

        List<OrdenExamen> examenes = ordenExamenMxService.getOrdenesExamenByIdSolicitud(idSolicitudDx);
        for (OrdenExamen examen : examenes) {
            if (examen.getCodExamen().getNombre().toLowerCase().contains("cultivo")){
                List<DetalleResultado> resultados = resultadosService.getDetallesResultadoActivosByExamen(examen.getIdOrdenExamen());

                Date fechaProcesamiento = null;
                String fechaSiembra = null;
                String detalleResultado = "";
                for (DetalleResultado resultado : resultados) {
                    if (resultado.getRespuesta().getNombre().toLowerCase().contains("fecha") && resultado.getRespuesta().getNombre().toLowerCase().contains("siembra"))
                    {
                       fechaSiembra = resultado.getValor();
                    }else {

                        if (resultado.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                            Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(resultado.getValor());
                            detalleResultado = cat_lista.getEtiqueta();
                        } else if (resultado.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                            detalleResultado = (Boolean.valueOf(resultado.getValor()) ? "lbl.yes" : "lbl.no");
                        }/* else {
                            detalleResultado = resultado.getValor();
                        }*/
                    }
                    fechaProcesamiento = resultado.getFechahProcesa();
                }
                if (resultados.size() > 0) {
                    dato[16] = fechaSiembra;
                    dato[19] = detalleResultado;
                    dato[20] = DateUtil.DateToString(fechaProcesamiento,"dd/MM/yyyy");
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
                        detalleResultado = cat_lista.getEtiqueta();
                    } else if (resultado.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                        detalleResultado = (Boolean.valueOf(resultado.getValor()) ? "lbl.yes" : "lbl.no");
                    }/*else {
                        detalleResultado = resultado.getValor();
                    }*/
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
                        detalleResultado = cat_lista.getEtiqueta();
                    } else if (resultado.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                        detalleResultado = (Boolean.valueOf(resultado.getValor()) ? "lbl.yes" : "lbl.no");
                    } /*else {
                        detalleResultado = resultado.getValor();
                    }*/
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
                    resultados+=cat_lista.getEtiqueta();
                }else if (res.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                    String valorBoleano = (Boolean.valueOf(res.getValor())?"lbl.yes":"lbl.no");
                    resultados+=valorBoleano;
                } /*else { // no tomar en cuenta respuestas auxiliares
                    resultados+=res.getValor();
                }*/
            }else if (res.getRespuestaExamen()!=null){
                //resultados+=(resultados.isEmpty()?res.getRespuestaExamen().getNombre():", "+res.getRespuestaExamen().getNombre());
                if (res.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                    Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(res.getValor());
                    resultados+=cat_lista.getEtiqueta();
                } else if (res.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                    String valorBoleano = (Boolean.valueOf(res.getValor())?"lbl.yes":"lbl.no");
                    resultados+=valorBoleano;
                }/*else { // no tomar en cuenta respuestas auxiliares
                    resultados+=res.getValor();
                }*/
            }
        }
        return resultados;
    }

    private String parseFinalResultDetails(String idSolicitud, String respuesta){
        List<DetalleResultadoFinal> resFinalList = resultadoFinalService.getDetResActivosBySolicitud(idSolicitud);
        String resultados="";
        for(DetalleResultadoFinal res: resFinalList){
            if (res.getRespuesta()!=null && res.getRespuesta().getNombre().toLowerCase().contains(respuesta)) {
                //resultados+=(resultados.isEmpty()?res.getRespuesta().getNombre():", "+res.getRespuesta().getNombre());
                if (res.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                    Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(res.getValor());
                    resultados+=cat_lista.getEtiqueta();
                }else if (res.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                    String valorBoleano = (Boolean.valueOf(res.getValor())?"lbl.yes":"lbl.no");
                    resultados+=valorBoleano;
                } else {
                    resultados+=res.getValor();
                }
            }else if (res.getRespuestaExamen()!=null && res.getRespuestaExamen().getNombre().toLowerCase().contains(respuesta)){
                //resultados+=(resultados.isEmpty()?res.getRespuestaExamen().getNombre():", "+res.getRespuestaExamen().getNombre());
                if (res.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                    Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(res.getValor());
                    resultados+=cat_lista.getEtiqueta();
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
     * Convierte un JSON con los filtros de b�squeda a objeto FiltrosReporte
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
        String codLabo = null;
        String diagnosticos = null;
        Integer semInicial = null;
        Integer semFinal = null;
        String anio = null;
        String consolidarPor = null;

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
        if (jObjectFiltro.get("codLabo") != null && !jObjectFiltro.get("codLabo").getAsString().isEmpty())
            codLabo = jObjectFiltro.get("codLabo").getAsString();
        if (jObjectFiltro.get("diagnosticos") != null && !jObjectFiltro.get("diagnosticos").getAsString().isEmpty())
            diagnosticos = jObjectFiltro.get("diagnosticos").getAsString();
        if (jObjectFiltro.get("semInicial") != null && !jObjectFiltro.get("semInicial").getAsString().isEmpty())
            semInicial = jObjectFiltro.get("semInicial").getAsInt();
        if (jObjectFiltro.get("semFinal") != null && !jObjectFiltro.get("semFinal").getAsString().isEmpty())
            semFinal = jObjectFiltro.get("semFinal").getAsInt();
        if (jObjectFiltro.get("anio") != null && !jObjectFiltro.get("anio").getAsString().isEmpty())
            anio = jObjectFiltro.get("anio").getAsString();
        if (jObjectFiltro.get("consolidarPor") != null && !jObjectFiltro.get("consolidarPor").getAsString().isEmpty())
            consolidarPor = jObjectFiltro.get("consolidarPor").getAsString();

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
        filtroRep.setCodLaboratio(codLabo);
        filtroRep.setDiagnosticos(diagnosticos);
        filtroRep.setSemInicial(semInicial);
        filtroRep.setSemFinal(semFinal);
        filtroRep.setAnioInicial(anio);
        filtroRep.setConsolidarPor(consolidarPor);
        return filtroRep;
    }
}
