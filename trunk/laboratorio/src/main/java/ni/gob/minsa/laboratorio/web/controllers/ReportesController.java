package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.estructura.EntidadesAdtvas;
import ni.gob.minsa.laboratorio.domain.examen.Area;
import ni.gob.minsa.laboratorio.domain.muestra.*;
import ni.gob.minsa.laboratorio.domain.muestra.traslado.TrasladoMx;
import ni.gob.minsa.laboratorio.domain.resultados.Catalogo_Lista;
import ni.gob.minsa.laboratorio.domain.resultados.DetalleResultado;
import ni.gob.minsa.laboratorio.domain.resultados.DetalleResultadoFinal;
import ni.gob.minsa.laboratorio.service.*;
import ni.gob.minsa.laboratorio.utilities.ConstantsSecurity;
import ni.gob.minsa.laboratorio.utilities.DateUtil;
import ni.gob.minsa.laboratorio.utilities.pdfUtils.BaseTable;
import ni.gob.minsa.laboratorio.utilities.pdfUtils.Cell;
import ni.gob.minsa.laboratorio.utilities.pdfUtils.GeneralUtils;
import ni.gob.minsa.laboratorio.utilities.pdfUtils.Row;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.text.translate.UnicodeEscaper;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Created by souyen-ics.
 */
@Controller
@RequestMapping("reports")
public class ReportesController {

    private static final Logger logger = LoggerFactory.getLogger(ReportesController.class);

    @Autowired
    @Qualifier(value = "seguridadService")
    private SeguridadService seguridadService;

    @Autowired
    @Qualifier(value = "catalogosService")
    private CatalogoService catalogosService;

    @Autowired
    @Qualifier(value = "entidadAdmonService")
    private EntidadAdmonService entidadAdmonService;

    @Autowired
    @Qualifier(value = "tomaMxService")
    private TomaMxService tomaMxService;

    @Autowired
    @Qualifier(value = "recepcionMxService")
    private RecepcionMxService recepcionMxService;

    @Autowired
    @Qualifier(value = "reportesService")
    private ReportesService reportesService;

    @Autowired
    @Qualifier(value = "resultadoFinalService")
    private ResultadoFinalService resultadoFinalService;

    @Autowired
    @Qualifier(value = "respuestasExamenService")
    private RespuestasExamenService respuestasExamenService;

    @Autowired
    @Qualifier (value = "areaService")
    private AreaService areaService;

    @Autowired
    @Qualifier(value = "laboratoriosService")
    private LaboratoriosService laboratoriosService;

    @Autowired
    @Qualifier(value = "trasladosService")
    private TrasladosService trasladosService;

    @Autowired
    @Qualifier(value = "conceptoService")
    private ConceptoService conceptoService;

    @Autowired
    MessageSource messageSource;


    /**
     * Método que se llama al entrar a la opción de menu de Reportes "Reporte Recepcion Mx".
     *
     * @param request para obtener información de la petición del cliente
     * @return ModelAndView
     * @throws Exception
     */
    @RequestMapping(value = "/reception/init", method = RequestMethod.GET)
    public ModelAndView initSearchForm(HttpServletRequest request) throws Exception {
        logger.debug("Iniciando Reporte de Recepción");
        String urlValidacion;
        try {
            urlValidacion = seguridadService.validarLogin(request);
            //si la url esta vacia significa que la validación del login fue exitosa
            if (urlValidacion.isEmpty())
                urlValidacion = seguridadService.validarAutorizacionUsuario(request, ConstantsSecurity.SYSTEM_CODE, false);
        } catch (Exception e) {
            e.printStackTrace();
            urlValidacion = "404";
        }
        ModelAndView mav = new ModelAndView();
        if (urlValidacion.isEmpty()) {
            List<EntidadesAdtvas> entidadesAdtvases = entidadAdmonService.getAllEntidadesAdtvas();
            List<TipoMx> tipoMxList = catalogosService.getTipoMuestra();
            mav.addObject("entidades", entidadesAdtvases);
            mav.addObject("tipoMuestra", tipoMxList);
            mav.setViewName("reportes/receptionReport");
        } else
            mav.setViewName(urlValidacion);

        return mav;
    }


    /**
     * Método para realizar la búsqueda de Mx recepcionadas
     *
     * @param filtro JSon con los datos de los filtros a aplicar en la búsqueda(Rango Fec Recepcion, Tipo Mx, SILAIS, unidad salud, tipo solicitud, descripcion)
     * @return String con las Mx encontradas
     * @throws Exception
     */
    @RequestMapping(value = "searchSamples", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    String fetchOrdersJson(@RequestParam(value = "strFilter", required = true) String filtro) throws Exception {
        logger.info("Obteniendo las mx recepcionadas según filtros en JSON");
        FiltroMx filtroMx = jsonToFiltroMx(filtro);
        List<RecepcionMx> receivedList = reportesService.getReceivedSamplesByFiltro(filtroMx);
        return receivedToJson(receivedList);
    }

    /**
     * Método para convertir estructura Json que se recibe desde el cliente a FiltroMx para realizar búsqueda de Mx(Vigilancia) y Recepción Mx(Laboratorio)
     *
     * @param strJson String con la información de los filtros
     * @return FiltroMx
     * @throws Exception
     */
    private FiltroMx jsonToFiltroMx(String strJson) throws Exception {
        JsonObject jObjectFiltro = new Gson().fromJson(strJson, JsonObject.class);
        FiltroMx filtroMx = new FiltroMx();

        Date fechaInicioRecepcion = null;
        Date fechaFinRecepcion = null;
        Date fechaInicioAprob = null;
        Date fechaFinAprob = null;
        String codSilais = null;
        String codUnidadSalud = null;
        String codTipoMx = null;
        String codTipoSolicitud = null;
        String nombreSolicitud = null;
        String area = null;
        String finalRes = null;
        String codLaboratorio = null;

        if (jObjectFiltro.get("fechaInicioRecepcion") != null && !jObjectFiltro.get("fechaInicioRecepcion").getAsString().isEmpty())
            fechaInicioRecepcion = DateUtil.StringToDate(jObjectFiltro.get("fechaInicioRecepcion").getAsString() + " 00:00:00");
        if (jObjectFiltro.get("fechaFinRecepcion") != null && !jObjectFiltro.get("fechaFinRecepcion").getAsString().isEmpty())
            fechaFinRecepcion = DateUtil.StringToDate(jObjectFiltro.get("fechaFinRecepcion").getAsString() + " 23:59:59");
        if (jObjectFiltro.get("codSilais") != null && !jObjectFiltro.get("codSilais").getAsString().isEmpty())
            codSilais = jObjectFiltro.get("codSilais").getAsString();
        if (jObjectFiltro.get("codUnidadSalud") != null && !jObjectFiltro.get("codUnidadSalud").getAsString().isEmpty())
            codUnidadSalud = jObjectFiltro.get("codUnidadSalud").getAsString();
        if (jObjectFiltro.get("codTipoMx") != null && !jObjectFiltro.get("codTipoMx").getAsString().isEmpty())
            codTipoMx = jObjectFiltro.get("codTipoMx").getAsString();
        if (jObjectFiltro.get("codTipoSolicitud") != null && !jObjectFiltro.get("codTipoSolicitud").getAsString().isEmpty())
            codTipoSolicitud = jObjectFiltro.get("codTipoSolicitud").getAsString();
        if (jObjectFiltro.get("nombreSolicitud") != null && !jObjectFiltro.get("nombreSolicitud").getAsString().isEmpty())
            nombreSolicitud = jObjectFiltro.get("nombreSolicitud").getAsString();
        if (jObjectFiltro.get("fechaInicioAprob") != null && !jObjectFiltro.get("fechaInicioAprob").getAsString().isEmpty())
            fechaInicioAprob = DateUtil.StringToDate(jObjectFiltro.get("fechaInicioAprob").getAsString() + " 00:00:00");
        if (jObjectFiltro.get("fechaFinAprob") != null && !jObjectFiltro.get("fechaFinAprob").getAsString().isEmpty())
            fechaFinAprob = DateUtil.StringToDate(jObjectFiltro.get("fechaFinAprob").getAsString() + " 23:59:59");
        if (jObjectFiltro.get("area") != null && !jObjectFiltro.get("area").getAsString().isEmpty())
            area = jObjectFiltro.get("area").getAsString();
        if (jObjectFiltro.get("finalRes") != null && !jObjectFiltro.get("finalRes").getAsString().isEmpty())
            finalRes = jObjectFiltro.get("finalRes").getAsString();
        if (jObjectFiltro.get("laboratorio") != null && !jObjectFiltro.get("laboratorio").getAsString().isEmpty())
            codLaboratorio = jObjectFiltro.get("laboratorio").getAsString();

        filtroMx.setCodSilais(codSilais);
        filtroMx.setCodUnidadSalud(codUnidadSalud);
        filtroMx.setFechaInicioRecep(fechaInicioRecepcion);
        filtroMx.setFechaFinRecep(fechaFinRecepcion);
        filtroMx.setCodTipoMx(codTipoMx);
        filtroMx.setCodTipoSolicitud(codTipoSolicitud);
        filtroMx.setNombreSolicitud(nombreSolicitud);
        filtroMx.setNombreUsuario(seguridadService.obtenerNombreUsuario());
        filtroMx.setFechaInicioAprob(fechaInicioAprob);
        filtroMx.setFechaFinAprob(fechaFinAprob);
        filtroMx.setArea(area);
        filtroMx.setResultadoFinal(finalRes);
        filtroMx.setCodLaboratio(codLaboratorio);

        return filtroMx;
    }


    /**
     * Método que convierte una lista de mx a un string con estructura Json
     *
     * @param receivedList lista con las mx recepcionadas a convertir
     * @return String
     */
    private String receivedToJson(List<RecepcionMx> receivedList) {
        String jsonResponse;
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice = 0;
        boolean esEstudio;
        for (RecepcionMx receivedMx : receivedList) {
            esEstudio = tomaMxService.getSolicitudesEstudioByIdTomaMx( receivedMx.getTomaMx().getIdTomaMx()).size() > 0;
            Map<String, String> map = new HashMap<String, String>();
            map.put("codigoUnicoMx", esEstudio?receivedMx.getTomaMx().getCodigoUnicoMx():receivedMx.getTomaMx().getCodigoLab());
            map.put("fechaRecepcion", DateUtil.DateToString(receivedMx.getFechaHoraRecepcion(), "dd/MM/yyyy hh:mm:ss a"));

            if (receivedMx.getCalidadMx() != null) {
                map.put("calidad", receivedMx.getCalidadMx().getValor());
            } else {
                map.put("calidad", "");
            }


            if (receivedMx.getTomaMx().getIdNotificacion().getCodSilaisAtencion() != null) {
                map.put("codSilais", receivedMx.getTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre());
            } else {
                map.put("codSilais", "");
            }
            if (receivedMx.getTomaMx().getIdNotificacion().getCodUnidadAtencion() != null) {
                map.put("codUnidadSalud", receivedMx.getTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre());
            } else {
                map.put("codUnidadSalud", "");
            }
            map.put("tipoMuestra", receivedMx.getTomaMx().getCodTipoMx().getNombre());


            //Si hay persona
            if (receivedMx.getTomaMx().getIdNotificacion().getPersona() != null) {
                /// se obtiene el nombre de la persona asociada a la ficha
                String nombreCompleto = "";
                nombreCompleto = receivedMx.getTomaMx().getIdNotificacion().getPersona().getPrimerNombre();
                if (receivedMx.getTomaMx().getIdNotificacion().getPersona().getSegundoNombre() != null)
                    nombreCompleto = nombreCompleto + " " + receivedMx.getTomaMx().getIdNotificacion().getPersona().getSegundoNombre();
                nombreCompleto = nombreCompleto + " " + receivedMx.getTomaMx().getIdNotificacion().getPersona().getPrimerApellido();
                if (receivedMx.getTomaMx().getIdNotificacion().getPersona().getSegundoApellido() != null)
                    nombreCompleto = nombreCompleto + " " + receivedMx.getTomaMx().getIdNotificacion().getPersona().getSegundoApellido();
                map.put("persona", nombreCompleto);
            } else {
                map.put("persona", " ");
            }

            //se arma estructura de diagnósticos o estudios
            Laboratorio labUser = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
            List<DaSolicitudDx> solicitudDxList = tomaMxService.getSolicitudesDxByIdToma(receivedMx.getTomaMx().getIdTomaMx(), labUser.getCodigo());
            DaSolicitudEstudio solicitudE = tomaMxService.getSoliEstByCodigo(receivedMx.getTomaMx().getCodigoUnicoMx());

            if (!solicitudDxList.isEmpty()) {
                int cont = 0;
                String dxs = "";
                for (DaSolicitudDx solicitudDx : solicitudDxList) {
                    cont++;
                    if (cont == solicitudDxList.size()) {
                        dxs += solicitudDx.getCodDx().getNombre();
                    } else {
                        dxs += solicitudDx.getCodDx().getNombre() + "," + " ";
                    }

                }
                map.put("solicitudes", dxs);
            } else {
                if(solicitudE != null){
                    map.put("solicitudes", solicitudE.getTipoEstudio().getNombre());
                }else{
                    map.put("solicitudes", "");
                }

            }

            mapResponse.put(indice, map);
            indice++;
        }
        jsonResponse = new Gson().toJson(mapResponse);
        //escapar caracteres especiales, escape de los caracteres con valor numérico mayor a 127
        UnicodeEscaper escaper = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }


    @RequestMapping(value = "expToPDF", method = RequestMethod.GET)
    public
    @ResponseBody
    String expToPDF(@RequestParam(value = "codes", required = true) String codes, @RequestParam(value = "fromDate", required = false) String fromDate, @RequestParam(value = "toDate", required = false) String toDate, HttpServletRequest request) throws IOException, COSVisitorException, ParseException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PDDocument doc = new PDDocument();
        Laboratorio labProcesa = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
        String res = null;
        String fechaImpresion = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());


        if (!codes.isEmpty()) {

            PDPage page = new PDPage(PDPage.PAGE_SIZE_A4);
            page.setRotation(90);
            doc.addPage(page);
            PDPageContentStream stream = new PDPageContentStream(doc, page);
            stream.concatenate2CTM(0, 1, -1, 0, page.getMediaBox().getWidth(), 0);
            float xCenter;

            GeneralUtils.drawHeaderAndFooter(stream, doc, 500, 840, 90, 840, 70);
            String pageNumber = String.valueOf(doc.getNumberOfPages());
            GeneralUtils.drawTEXT(pageNumber, 15, 800, stream, 10, PDType1Font.HELVETICA_BOLD);
            drawInfoLab(stream, page, labProcesa);

            float y = 400;
            float m = 20;

            //nombre del reporte
            xCenter = centerTextPositionX(page, PDType1Font.HELVETICA_BOLD, 12, messageSource.getMessage("lbl.reception.report", null, null).toUpperCase());
            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.reception.report", null, null).toUpperCase(), y, xCenter, stream, 12, PDType1Font.HELVETICA_BOLD);
            y = y - 10;
            //Rango de Fechas
            if (!fromDate.equals("") && !toDate.equals("")) {
                GeneralUtils.drawTEXT(messageSource.getMessage("lbl.from", null, null), y, 55, stream, 12, PDType1Font.HELVETICA_BOLD);
                GeneralUtils.drawTEXT(fromDate, y, 100, stream, 12, PDType1Font.HELVETICA_BOLD);

                GeneralUtils.drawTEXT(messageSource.getMessage("lbl.to", null, null), y, 660, stream, 12, PDType1Font.HELVETICA_BOLD);
                GeneralUtils.drawTEXT(toDate, y, 720, stream, 12, PDType1Font.HELVETICA_BOLD);
                y -= m;
            }


            String[] codigosArray = codes.replaceAll("\\*", "-").split(",");
            List<String[]> recList = new ArrayList<String[]>();

            int numFila = 0;

            for (String codigoUnico : codigosArray) {
                String[] content = null;

                RecepcionMx recepcion = recepcionMxService.getRecepcionMxByCodUnicoMx(codigoUnico, labProcesa.getCodigo());

                if (recepcion != null) {
                    String nombreSolitud = null;
                    String nombrePersona = null;

                    List<DaSolicitudDx> listDx = tomaMxService.getSolicitudesDxCodigo(recepcion.getTomaMx().getCodigoUnicoMx(), seguridadService.obtenerNombreUsuario());
                    DaSolicitudEstudio soliE = tomaMxService.getSoliEstByCodigo(recepcion.getTomaMx().getCodigoUnicoMx());

                    if (!listDx.isEmpty()) {
                        int cont = 0;
                        String dxs = "";
                        for (DaSolicitudDx sol : listDx) {
                            cont++;
                            if (cont == listDx.size()) {
                                dxs += sol.getCodDx().getNombre();
                            } else {
                                dxs += sol.getCodDx().getNombre() + "," + " ";
                            }

                        }
                        content = new String[8];

                        nombreSolitud = dxs;

                        nombrePersona = recepcion.getTomaMx().getIdNotificacion().getPersona().getPrimerNombre();
                        if (recepcion.getTomaMx().getIdNotificacion().getPersona().getSegundoNombre() != null)
                            nombrePersona = nombrePersona + " " + recepcion.getTomaMx().getIdNotificacion().getPersona().getSegundoNombre();
                        nombrePersona = nombrePersona + " " + recepcion.getTomaMx().getIdNotificacion().getPersona().getPrimerApellido();
                        if (recepcion.getTomaMx().getIdNotificacion().getPersona().getSegundoApellido() != null)
                            nombrePersona = nombrePersona + " " + recepcion.getTomaMx().getIdNotificacion().getPersona().getSegundoApellido();

                        content[0] = recepcion.getTomaMx().getCodigoLab() != null ? recepcion.getTomaMx().getCodigoLab() : "";
                        content[1] = recepcion.getTomaMx().getCodTipoMx() != null ? recepcion.getTomaMx().getCodTipoMx().getNombre() : "";
                        content[2] = recepcion.getFechaHoraRecepcion() != null ? DateUtil.DateToString(recepcion.getFechaHoraRecepcion(), "dd/MM/yyyy hh:mm:ss a") : "";
                        content[3] = recepcion.getCalidadMx() != null ? recepcion.getCalidadMx().getValor() : "";
                        content[4] = recepcion.getTomaMx().getIdNotificacion().getCodSilaisAtencion() != null ? recepcion.getTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre() : "";
                        content[5] = recepcion.getTomaMx().getIdNotificacion().getCodUnidadAtencion() != null ? recepcion.getTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre() : "";
                        content[6] = recepcion.getTomaMx().getIdNotificacion().getPersona() != null ? nombrePersona : "";
                        content[7] = nombreSolitud != null ? nombreSolitud : "";

                        recList.add(content);
                    }

                    if (soliE != null) {
                        content = new String[8];

                        nombreSolitud = soliE.getTipoEstudio().getNombre();

                        nombrePersona = recepcion.getTomaMx().getIdNotificacion().getPersona().getPrimerNombre();
                        if (recepcion.getTomaMx().getIdNotificacion().getPersona().getSegundoNombre() != null)
                            nombrePersona = nombrePersona + " " + recepcion.getTomaMx().getIdNotificacion().getPersona().getSegundoNombre();
                        nombrePersona = nombrePersona + " " + recepcion.getTomaMx().getIdNotificacion().getPersona().getPrimerApellido();
                        if (recepcion.getTomaMx().getIdNotificacion().getPersona().getSegundoApellido() != null)
                            nombrePersona = nombrePersona + " " + recepcion.getTomaMx().getIdNotificacion().getPersona().getSegundoApellido();

                        content[0] = recepcion.getTomaMx().getCodigoUnicoMx() != null ? recepcion.getTomaMx().getCodigoUnicoMx() : "";
                        content[1] = recepcion.getTomaMx().getCodTipoMx() != null ? recepcion.getTomaMx().getCodTipoMx().getNombre() : "";
                        content[2] = recepcion.getFechaHoraRecepcion() != null ? DateUtil.DateToString(recepcion.getFechaHoraRecepcion(), "dd/MM/yyyy hh:mm:ss a") : "";
                        content[3] = recepcion.getCalidadMx() != null ? recepcion.getCalidadMx().getValor() : "";
                        content[4] = recepcion.getTomaMx().getIdNotificacion().getCodSilaisAtencion() != null ? recepcion.getTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre() : "";
                        content[5] = recepcion.getTomaMx().getIdNotificacion().getCodUnidadAtencion() != null ? recepcion.getTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre() : "";
                        content[6] = recepcion.getTomaMx().getIdNotificacion().getPersona() != null ? nombrePersona : "";
                        content[7] = nombreSolitud != null ? nombreSolitud : "";

                        recList.add(content);
                    }

                }
            }

            //drawTable

            //Initialize table
            float margin = 50;
            float tableWidth = 730;
            float yStartNewPage = y;
            float yStart = yStartNewPage;
            float bottomMargin = 45;
            BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true, true);

            //Create Header row
            Row headerRow = table.createRow(15f);
            table.setHeader(headerRow);

            //Create 2 column row

            Cell cell;
            Row row;


            //Create Fact header row
            Row factHeaderrow = table.createRow(15f);
            cell = factHeaderrow.createCell(10, messageSource.getMessage("lbl.lab.code.mx", null, null));
            cell.setFont(PDType1Font.HELVETICA_BOLD);
            cell.setFontSize(10);
            cell.setFillColor(Color.LIGHT_GRAY);

            cell = factHeaderrow.createCell(10, messageSource.getMessage("lbl.sample.type", null, null));
            cell.setFillColor(Color.lightGray);
            cell.setFont(PDType1Font.HELVETICA_BOLD);
            cell.setFontSize(10);

            cell = factHeaderrow.createCell(9, messageSource.getMessage("lbl.receipt.dateTime", null, null));
            cell.setFillColor(Color.lightGray);
            cell.setFont(PDType1Font.HELVETICA_BOLD);
            cell.setFontSize(10);

            cell = factHeaderrow.createCell(12, messageSource.getMessage("lbl.sample.quality", null, null));
            cell.setFillColor(Color.lightGray);
            cell.setFont(PDType1Font.HELVETICA_BOLD);
            cell.setFontSize(10);

            cell = factHeaderrow.createCell(17, messageSource.getMessage("lbl.silais", null, null));
            cell.setFillColor(Color.lightGray);
            cell.setFont(PDType1Font.HELVETICA_BOLD);
            cell.setFontSize(10);

            cell = factHeaderrow.createCell(17, messageSource.getMessage("lbl.health.unit", null, null));
            cell.setFillColor(Color.lightGray);
            cell.setFont(PDType1Font.HELVETICA_BOLD);
            cell.setFontSize(10);

            cell = factHeaderrow.createCell(16, messageSource.getMessage("lbl.receipt.person.name", null, null));
            cell.setFillColor(Color.lightGray);
            cell.setFont(PDType1Font.HELVETICA_BOLD);
            cell.setFontSize(10);

            cell = factHeaderrow.createCell(9, messageSource.getMessage("lbl.request.large", null, null));
            cell.setFillColor(Color.lightGray);
            cell.setFont(PDType1Font.HELVETICA_BOLD);
            cell.setFontSize(10);
            y -= 15;

            //Add multiple rows with random facts about Belgium
            for (String[] fact : recList) {

                if (y < 260) {
                    table.draw();
                    stream.close();
                    page = new PDPage(PDPage.PAGE_SIZE_A4);
                    page.setRotation(90);
                    doc.addPage(page);
                    stream = new PDPageContentStream(doc, page);
                    stream.concatenate2CTM(0, 1, -1, 0, page.getMediaBox().getWidth(), 0);
                    y = 470;
                    GeneralUtils.drawHeaderAndFooter(stream, doc, 500, 840, 90, 840, 70);
                    pageNumber = String.valueOf(doc.getNumberOfPages());
                    GeneralUtils.drawTEXT(pageNumber, 15, 800, stream, 10, PDType1Font.HELVETICA_BOLD);


                    table = new BaseTable(y, y, bottomMargin, tableWidth, margin, doc, page, true, true);

                    //Create Header row
                    headerRow = table.createRow(15f);
                    table.setHeader(headerRow);

                    //Create Fact header row
                    factHeaderrow = table.createRow(15f);
                    cell = factHeaderrow.createCell(10, messageSource.getMessage("lbl.lab.code.mx", null, null));
                    cell.setFont(PDType1Font.HELVETICA_BOLD);
                    cell.setFontSize(10);
                    cell.setFillColor(Color.LIGHT_GRAY);

                    cell = factHeaderrow.createCell(10, messageSource.getMessage("lbl.sample.type", null, null));
                    cell.setFillColor(Color.lightGray);
                    cell.setFont(PDType1Font.HELVETICA_BOLD);
                    cell.setFontSize(10);

                    cell = factHeaderrow.createCell(9, messageSource.getMessage("lbl.receipt.dateTime", null, null));
                    cell.setFillColor(Color.lightGray);
                    cell.setFont(PDType1Font.HELVETICA_BOLD);
                    cell.setFontSize(10);

                    cell = factHeaderrow.createCell(12, messageSource.getMessage("lbl.sample.quality", null, null));
                    cell.setFillColor(Color.lightGray);
                    cell.setFont(PDType1Font.HELVETICA_BOLD);
                    cell.setFontSize(10);

                    cell = factHeaderrow.createCell(17, messageSource.getMessage("lbl.silais", null, null));
                    cell.setFillColor(Color.lightGray);
                    cell.setFont(PDType1Font.HELVETICA_BOLD);
                    cell.setFontSize(10);

                    cell = factHeaderrow.createCell(17, messageSource.getMessage("lbl.health.unit", null, null));
                    cell.setFillColor(Color.lightGray);
                    cell.setFont(PDType1Font.HELVETICA_BOLD);
                    cell.setFontSize(10);

                    cell = factHeaderrow.createCell(16, messageSource.getMessage("lbl.receipt.person.name", null, null));
                    cell.setFillColor(Color.lightGray);
                    cell.setFont(PDType1Font.HELVETICA_BOLD);
                    cell.setFontSize(10);

                    cell = factHeaderrow.createCell(9, messageSource.getMessage("lbl.request.large", null, null));
                    cell.setFillColor(Color.lightGray);
                    cell.setFont(PDType1Font.HELVETICA_BOLD);
                    cell.setFontSize(10);
                    y -= 15;


                }

                row = table.createRow(15);
                cell = row.createCell(10, fact[0]);
                cell.setFont(PDType1Font.HELVETICA);
                cell.setFontSize(10);
                y -= 15;

                for (int i = 1; i < fact.length; i++) {
                    if (i == 1) {
                        cell = row.createCell(10, fact[i]);
                        cell.setFont(PDType1Font.HELVETICA);
                        cell.setFontSize(10);
                    } else if (i == 2) {
                        cell = row.createCell(9, fact[i]);
                        cell.setFont(PDType1Font.HELVETICA);
                        cell.setFontSize(10);
                    } else if (i == 5) {
                        cell = row.createCell(17, fact[i]);
                        cell.setFont(PDType1Font.HELVETICA);
                        cell.setFontSize(10);

                    } else if (i == 7) {
                        cell = row.createCell(9, fact[i]);
                        cell.setFont(PDType1Font.HELVETICA);
                        cell.setFontSize(10);

                    } else if (i == 4) {
                        cell = row.createCell(17, fact[i]);
                        cell.setFont(PDType1Font.HELVETICA);
                        cell.setFontSize(10);

                    } else if (i == 6) {
                        cell = row.createCell(16, fact[i]);
                        cell.setFont(PDType1Font.HELVETICA);
                        cell.setFontSize(10);


                    } else {
                        cell = row.createCell(12, fact[i]);
                        cell.setFont(PDType1Font.HELVETICA);
                        cell.setFontSize(10);
                    }

                }
            }
            table.draw();

            //fecha impresión
            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.print.datetime", null, null), 100, 605, stream, 10, PDType1Font.HELVETICA_BOLD);
            GeneralUtils.drawTEXT(fechaImpresion, 100, 710, stream, 10, PDType1Font.HELVETICA);
            stream.close();

            doc.save(output);
            doc.close();
            // generate the file
            res = Base64.encodeBase64String(output.toByteArray());

        }

        return res;
    }


    private void drawInfoLab(PDPageContentStream stream, PDPage page, Laboratorio labProcesa) throws IOException {
        float xCenter;

        float inY = 490;
        float m = 20;

        xCenter = centerTextPositionX(page, PDType1Font.HELVETICA_BOLD, 14, messageSource.getMessage("lbl.minsa", null, null));
        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.minsa", null, null), inY, xCenter, stream, 14, PDType1Font.HELVETICA_BOLD);
        inY -= m;

        if (labProcesa != null) {

            if (labProcesa.getDescripcion() != null) {
                xCenter = centerTextPositionX(page, PDType1Font.HELVETICA_BOLD, 14, labProcesa.getDescripcion());
                GeneralUtils.drawTEXT(labProcesa.getDescripcion(), inY, xCenter, stream, 14, PDType1Font.HELVETICA_BOLD);
                inY -= m;
            }

            if (labProcesa.getDireccion() != null) {
                xCenter = centerTextPositionX(page, PDType1Font.HELVETICA_BOLD, 14, labProcesa.getDescripcion());
                GeneralUtils.drawTEXT(labProcesa.getDireccion(), inY, xCenter, stream, 14, PDType1Font.HELVETICA_BOLD);
                inY -= m;
            }

            if (labProcesa.getTelefono() != null) {

                if (labProcesa.getTelefax() != null) {
                    xCenter = centerTextPositionX(page, PDType1Font.HELVETICA_BOLD, 14, labProcesa.getTelefono() + " " + labProcesa.getTelefax());
                    GeneralUtils.drawTEXT(labProcesa.getTelefono() + " " + labProcesa.getTelefax(), inY, xCenter, stream, 14, PDType1Font.HELVETICA_BOLD);
                } else {
                    xCenter = centerTextPositionX(page, PDType1Font.HELVETICA_BOLD, 14, labProcesa.getTelefono());
                    GeneralUtils.drawTEXT(labProcesa.getTelefono(), inY, xCenter, stream, 14, PDType1Font.HELVETICA_BOLD);
                }
            }
        }
    }


    public static float centerTextPositionX(PDPage page, PDFont font, float fontSize, String texto) throws IOException {
        float titleWidth = font.getStringWidth(texto) / 1000 * fontSize;
        return (page.getMediaBox().getHeight() - titleWidth) / 2;
    }

    /**
     * Método que se llama al entrar a la opción de menu de Reportes "Reporte Resultados Positivos".
     *
     * @param request para obtener información de la petición del cliente
     * @return ModelAndView
     * @throws Exception
     */
    @RequestMapping(value = "/positiveResults/init", method = RequestMethod.GET)
    public ModelAndView initForm(HttpServletRequest request) throws Exception {
        logger.debug("Iniciando Reporte de Resultados Positivos");
        String urlValidacion;
        try {
            urlValidacion = seguridadService.validarLogin(request);
            //si la url esta vacia significa que la validación del login fue exitosa
            if (urlValidacion.isEmpty())
                urlValidacion = seguridadService.validarAutorizacionUsuario(request, ConstantsSecurity.SYSTEM_CODE, false);
        } catch (Exception e) {
            e.printStackTrace();
            urlValidacion = "404";
        }
        ModelAndView mav = new ModelAndView();
        if (urlValidacion.isEmpty()) {
            List<EntidadesAdtvas> entidadesAdtvases = entidadAdmonService.getAllEntidadesAdtvas();
            List<TipoMx> tipoMxList = catalogosService.getTipoMuestra();
            List<Area> areas = areaService.getAreas();
            mav.addObject("entidades", entidadesAdtvases);
            mav.addObject("areas", areas);
            mav.setViewName("reportes/positiveResultsReport");
        } else
            mav.setViewName(urlValidacion);

        return mav;
    }

    /**
     * Método para realizar la búsqueda de Resultados positivos
     *
     * @param filtro JSon con los datos de los filtros a aplicar en la búsqueda(Rango Fec Aprob, SILAIS, unidad salud, tipo solicitud, descripcion)
     * @return String con las solicitudes encontradas
     * @throws Exception
     */
    @RequestMapping(value = "searchRequest", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    String fetchRequestJson(@RequestParam(value = "strFilter", required = true) String filtro) throws Exception {
        logger.info("Obteniendo las solicitudes positivas según filtros en JSON");
        FiltroMx filtroMx = jsonToFiltroMx(filtro);
        List<DaSolicitudDx> positiveRoutineReqList = null;
        List<DaSolicitudEstudio> positiveStudyReqList = null;

        if (filtroMx.getCodTipoSolicitud() != null) {
            if (filtroMx.getCodTipoSolicitud().equals("Estudio")) {
                positiveStudyReqList = reportesService.getPositiveStudyRequestByFilter(filtroMx);
            } else {
                positiveRoutineReqList = reportesService.getPositiveRoutineRequestByFilter(filtroMx);
            }

        } else {
            positiveRoutineReqList = reportesService.getPositiveRoutineRequestByFilter(filtroMx);
            positiveStudyReqList = reportesService.getPositiveStudyRequestByFilter(filtroMx);
        }

        return reqPositiveToJson(positiveRoutineReqList, positiveStudyReqList);
    }

    /**
     * Método que convierte una lista de solicitudes a un string con estructura Json
     *
     * @param positiveRoutineReqList lista con las mx recepcionadas a convertir
     * @return String
     */
    private String reqPositiveToJson(List<DaSolicitudDx> positiveRoutineReqList, List<DaSolicitudEstudio> positiveStudyReqList) throws Exception {
        String jsonResponse;
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice = 0;


        if (positiveRoutineReqList != null) {
            for (DaSolicitudDx soli : positiveRoutineReqList) {
                boolean mostrar = false;

                //search positive results from list
                //get Response for each request
                List<DetalleResultadoFinal> finalRes = resultadoFinalService.getDetResActivosBySolicitud(soli.getIdSolicitudDx());
                for (DetalleResultadoFinal res : finalRes) {

                    if (res.getRespuesta() != null) {
                        if (res.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                            Integer idLista = Integer.valueOf(res.getValor());
                            Catalogo_Lista valor = respuestasExamenService.getCatalogoListaConceptoByIdLista(idLista);

                            if (valor.getValor().toLowerCase().equals("positivo")) {
                                mostrar = true;
                            }

                        } else if (res.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|TXT")) {
                            if (res.getValor().toLowerCase().equals("positivo")) {
                                mostrar = true;
                            }
                        }
                    } else if (res.getRespuestaExamen() != null) {
                        if (res.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                            Integer idLista = Integer.valueOf(res.getValor());
                            Catalogo_Lista valor = respuestasExamenService.getCatalogoListaConceptoByIdLista(idLista);

                            if (valor.getValor().toLowerCase().equals("positivo")) {
                                mostrar = true;
                            }

                        } else if (res.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|TXT")) {
                            if (res.getValor().toLowerCase().equals("positivo")) {
                                mostrar = true;
                            }
                        }

                    }
                }

                if (mostrar) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("solicitud", soli.getCodDx().getNombre());
                    map.put("idSolicitud", soli.getIdSolicitudDx());
                    map.put("codigoUnicoMx", soli.getIdTomaMx().getCodigoLab());
                    map.put("fechaAprobacion", DateUtil.DateToString(soli.getFechaAprobacion(), "dd/MM/yyyy hh:mm:ss a"));

                    if (soli.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion() != null) {
                        map.put("codSilais", soli.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre());
                    } else {
                        map.put("codSilais", "");
                    }
                    if (soli.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion() != null) {
                        map.put("codUnidadSalud", soli.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre());
                    } else {
                        map.put("codUnidadSalud", "");
                    }

                    //Si hay persona
                    if (soli.getIdTomaMx().getIdNotificacion().getPersona() != null) {
                        /// se obtiene el nombre de la persona asociada a la ficha
                        String nombreCompleto = "";
                        nombreCompleto = soli.getIdTomaMx().getIdNotificacion().getPersona().getPrimerNombre();
                        if (soli.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre() != null)
                            nombreCompleto = nombreCompleto + " " + soli.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre();
                        nombreCompleto = nombreCompleto + " " + soli.getIdTomaMx().getIdNotificacion().getPersona().getPrimerApellido();
                        if (soli.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido() != null)
                            nombreCompleto = nombreCompleto + " " + soli.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido();
                        map.put("persona", nombreCompleto);
                    } else {
                        map.put("persona", " ");
                    }

                    mapResponse.put(indice, map);
                    indice++;
                }
            }

        }
        if (positiveStudyReqList != null) {

            for (DaSolicitudEstudio soliE : positiveStudyReqList) {
                boolean mostrar = false;

                //search positive results from list
                //get Response for each request
                List<DetalleResultadoFinal> finalRes = resultadoFinalService.getDetResActivosBySolicitud(soliE.getIdSolicitudEstudio());
                for (DetalleResultadoFinal res : finalRes) {

                    if (res.getRespuesta() != null) {
                        if (res.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                            Integer idLista = Integer.valueOf(res.getValor());
                            Catalogo_Lista valor = respuestasExamenService.getCatalogoListaConceptoByIdLista(idLista);

                            if (valor.getValor().toLowerCase().equals("positivo")) {
                                mostrar = true;
                            }

                        } else if (res.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|TXT")) {
                            if (res.getValor().toLowerCase().equals("positivo")) {
                                mostrar = true;
                            }
                        }
                    } else if (res.getRespuestaExamen() != null) {
                        if (res.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                            Integer idLista = Integer.valueOf(res.getValor());
                            Catalogo_Lista valor = respuestasExamenService.getCatalogoListaConceptoByIdLista(idLista);

                            if (valor.getValor().toLowerCase().equals("positivo")) {
                                mostrar = true;
                            }

                        } else if (res.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|TXT")) {
                            if (res.getValor().toLowerCase().equals("positivo")) {
                                mostrar = true;
                            }
                        }

                    }
                }

                if (mostrar) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("solicitud", soliE.getTipoEstudio().getNombre());
                    map.put("idSolicitud", soliE.getIdSolicitudEstudio());
                    map.put("codigoUnicoMx", soliE.getIdTomaMx().getCodigoUnicoMx());
                    map.put("fechaAprobacion", DateUtil.DateToString(soliE.getFechaAprobacion(), "dd/MM/yyyy hh:mm:ss a"));

                    if (soliE.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion() != null) {
                        map.put("codSilais", soliE.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre());
                    } else {
                        map.put("codSilais", "");
                    }
                    if (soliE.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion() != null) {
                        map.put("codUnidadSalud", soliE.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre());
                    } else {
                        map.put("codUnidadSalud", "");
                    }

                    //Si hay persona
                    if (soliE.getIdTomaMx().getIdNotificacion().getPersona() != null) {
                        /// se obtiene el nombre de la persona asociada a la ficha
                        String nombreCompleto = "";
                        nombreCompleto = soliE.getIdTomaMx().getIdNotificacion().getPersona().getPrimerNombre();
                        if (soliE.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre() != null)
                            nombreCompleto = nombreCompleto + " " + soliE.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre();
                        nombreCompleto = nombreCompleto + " " + soliE.getIdTomaMx().getIdNotificacion().getPersona().getPrimerApellido();
                        if (soliE.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido() != null)
                            nombreCompleto = nombreCompleto + " " + soliE.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido();
                        map.put("persona", nombreCompleto);
                    } else {
                        map.put("persona", " ");
                    }

                    mapResponse.put(indice, map);
                    indice++;
                }
            }
        }
        jsonResponse = new Gson().toJson(mapResponse);
        //escapar caracteres especiales, escape de los caracteres con valor numérico mayor a 127
        UnicodeEscaper escaper = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }


    @RequestMapping(value = "positiveRequestToPDF", method = RequestMethod.GET)
    public
    @ResponseBody
    String positiveRequestToPDF(@RequestParam(value = "codes", required = true) String codes, @RequestParam(value = "fromDate", required = false) String fromDate, @RequestParam(value = "toDate", required = false) String toDate, HttpServletRequest request) throws IOException, COSVisitorException, ParseException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PDDocument doc = new PDDocument();
        Laboratorio labProcesa = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
        String res = null;
        String fechaImpresion = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());


        if (!codes.isEmpty()) {

            PDPage page = new PDPage(PDPage.PAGE_SIZE_A4);
            page.setRotation(90);
            doc.addPage(page);
            PDPageContentStream stream = new PDPageContentStream(doc, page);
            stream.concatenate2CTM(0, 1, -1, 0, page.getMediaBox().getWidth(), 0);
            float xCenter;

            GeneralUtils.drawHeaderAndFooter(stream, doc, 500, 840, 90, 840, 70);

            String pageNumber = String.valueOf(doc.getNumberOfPages());
            GeneralUtils.drawTEXT(pageNumber, 15, 800, stream, 10, PDType1Font.HELVETICA_BOLD);

            drawInfoLab(stream, page, labProcesa);

            float y = 400;
            float m = 20;

            //nombre del reporte
            xCenter = centerTextPositionX(page, PDType1Font.HELVETICA_BOLD, 12, messageSource.getMessage("lbl.positiveResultReport", null, null).toUpperCase());
            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.positiveResultReport", null, null).toUpperCase(), y, xCenter, stream, 12, PDType1Font.HELVETICA_BOLD);
            y = y - 10;
            //Rango de Fechas
            if (!fromDate.equals("") && !toDate.equals("")) {
                GeneralUtils.drawTEXT(messageSource.getMessage("lbl.from", null, null), y, 55, stream, 12, PDType1Font.HELVETICA_BOLD);
                GeneralUtils.drawTEXT(fromDate, y, 100, stream, 12, PDType1Font.HELVETICA_BOLD);

                GeneralUtils.drawTEXT(messageSource.getMessage("lbl.to", null, null), y, 660, stream, 12, PDType1Font.HELVETICA_BOLD);
                GeneralUtils.drawTEXT(toDate, y, 720, stream, 12, PDType1Font.HELVETICA_BOLD);
                y -= m;
            }


            String[] idSoli = codes.split(",");
            List<String[]> reqList = new ArrayList<String[]>();


            for (String idSolicitud : idSoli) {
                String nombreSolitud = null;
                String nombrePersona = null;
                String fechaAprob = null;
                String silais = null;
                String unidadSalud = null;
                String[] content = null;

               DaSolicitudDx soli = tomaMxService.getSolicitudDxByIdSolicitudUser(idSolicitud, seguridadService.obtenerNombreUsuario());
               DaSolicitudEstudio soliE = tomaMxService.getSolicitudEstByIdSolicitud(idSolicitud);


               if(soli != null){

                    content = new String[6];
                    nombreSolitud = soli.getCodDx().getNombre();

                    nombrePersona = soli.getIdTomaMx().getIdNotificacion().getPersona().getPrimerNombre();
                    if (soli.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre() != null)
                        nombrePersona = nombrePersona + " " + soli.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre();
                    nombrePersona = nombrePersona + " " + soli.getIdTomaMx().getIdNotificacion().getPersona().getPrimerApellido();
                    if (soli.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido() != null)
                        nombrePersona = nombrePersona + " " + soli.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido();

                    if (soli.getFechaAprobacion() != null) {
                        fechaAprob = DateUtil.DateToString(soli.getFechaAprobacion(), "dd/MM/yyyy hh:mm:ss a");
                    }

                    if (soli.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion() != null) {
                        silais = soli.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre();
                    }

                    if (soli.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion() != null) {
                        unidadSalud = soli.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre();
                    }

                    content[0] = soli.getIdTomaMx() != null ? soli.getIdTomaMx().getCodigoUnicoMx() : "";
                    content[1] = fechaAprob != null ? fechaAprob : "";
                    content[2] = silais != null ? silais : "";
                    content[3] = unidadSalud != null ? unidadSalud : "";
                    content[4] = nombrePersona != null ? nombrePersona : "";
                    content[5] = nombreSolitud != null ? nombreSolitud : "";
                    reqList.add(content);

                }

                if (soliE != null) {
                    content = new String[6];
                    nombreSolitud = soliE.getTipoEstudio().getNombre();

                    nombrePersona = soliE.getIdTomaMx().getIdNotificacion().getPersona().getPrimerNombre();
                    if (soliE.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre() != null)
                        nombrePersona = nombrePersona + " " + soliE.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre();
                    nombrePersona = nombrePersona + " " + soliE.getIdTomaMx().getIdNotificacion().getPersona().getPrimerApellido();
                    if (soliE.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido() != null)
                        nombrePersona = nombrePersona + " " + soliE.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido();

                    if (soliE.getFechaAprobacion() != null) {
                        fechaAprob = DateUtil.DateToString(soliE.getFechaAprobacion(), "dd/MM/yyyy hh:mm:ss a");
                    }

                    if (soliE.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion() != null) {
                        silais = soliE.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre();
                    }

                    if (soliE.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion() != null) {
                        unidadSalud = soliE.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre();
                    }

                    content[0] = soliE.getIdTomaMx() != null ? soliE.getIdTomaMx().getCodigoUnicoMx() : "";
                    content[1] = fechaAprob != null ? fechaAprob : "";
                    content[2] = silais != null ? silais : "";
                    content[3] = unidadSalud != null ? unidadSalud : "";
                    content[4] = nombrePersona != null ? nombrePersona : "";
                    content[5] = nombreSolitud != null ? nombreSolitud : "";
                    reqList.add(content);


                }
            }

            //drawTable

            //Initialize table
            float margin = 50;
            float tableWidth = 730;
            float yStartNewPage = y;
            float yStart = yStartNewPage;
            float bottomMargin = 45;
            BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true, true);

            //Create Header row
            Row headerRow = table.createRow(15f);
            table.setHeader(headerRow);

            //Create 2 column row
            Cell cell;
            Row row;

            //Create Fact header row
            Row factHeaderrow = table.createRow(15f);
            cell = factHeaderrow.createCell(12, messageSource.getMessage("lbl.lab.code.mx", null, null));
            cell.setFont(PDType1Font.HELVETICA_BOLD);
            cell.setFontSize(10);
            cell.setFillColor(Color.LIGHT_GRAY);

            cell = factHeaderrow.createCell(16, messageSource.getMessage("lbl.approve.date", null, null));
            cell.setFillColor(Color.lightGray);
            cell.setFont(PDType1Font.HELVETICA_BOLD);
            cell.setFontSize(10);

            cell = factHeaderrow.createCell(17, messageSource.getMessage("lbl.silais", null, null));
            cell.setFillColor(Color.lightGray);
            cell.setFont(PDType1Font.HELVETICA_BOLD);
            cell.setFontSize(10);

            cell = factHeaderrow.createCell(20, messageSource.getMessage("lbl.health.unit", null, null));
            cell.setFillColor(Color.lightGray);
            cell.setFont(PDType1Font.HELVETICA_BOLD);
            cell.setFontSize(10);

            cell = factHeaderrow.createCell(20, messageSource.getMessage("lbl.receipt.person.name", null, null));
            cell.setFillColor(Color.lightGray);
            cell.setFont(PDType1Font.HELVETICA_BOLD);
            cell.setFontSize(10);

            cell = factHeaderrow.createCell(15, messageSource.getMessage("lbl.request.large", null, null));
            cell.setFillColor(Color.lightGray);
            cell.setFont(PDType1Font.HELVETICA_BOLD);
            cell.setFontSize(10);
            y -= 15;

            //Add multiple rows with random facts about Belgium
            for (String[] fact : reqList) {

                if (y < 260) {
                    table.draw();
                    stream.close();
                    page = new PDPage(PDPage.PAGE_SIZE_A4);
                    page.setRotation(90);
                    doc.addPage(page);
                    stream = new PDPageContentStream(doc, page);
                    stream.concatenate2CTM(0, 1, -1, 0, page.getMediaBox().getWidth(), 0);
                    y = 470;
                    GeneralUtils.drawHeaderAndFooter(stream, doc, 500, 840, 90, 840, 70);

                    pageNumber = String.valueOf(doc.getNumberOfPages());
                    GeneralUtils.drawTEXT(pageNumber, 15, 800, stream, 10, PDType1Font.HELVETICA_BOLD);

                    table = new BaseTable(y, y, bottomMargin, tableWidth, margin, doc, page, true, true);

                    //Create Header row
                    headerRow = table.createRow(15f);
                    table.setHeader(headerRow);

                    //Create Fact header row
                    factHeaderrow = table.createRow(15f);
                    cell = factHeaderrow.createCell(15, messageSource.getMessage("lbl.lab.code.mx", null, null));
                    cell.setFont(PDType1Font.HELVETICA_BOLD);
                    cell.setFontSize(10);
                    cell.setFillColor(Color.LIGHT_GRAY);

                    cell = factHeaderrow.createCell(15, messageSource.getMessage("lbl.approve.date", null, null));
                    cell.setFillColor(Color.lightGray);
                    cell.setFont(PDType1Font.HELVETICA_BOLD);
                    cell.setFontSize(10);

                    cell = factHeaderrow.createCell(15, messageSource.getMessage("lbl.silais", null, null));
                    cell.setFillColor(Color.lightGray);
                    cell.setFont(PDType1Font.HELVETICA_BOLD);
                    cell.setFontSize(10);

                    cell = factHeaderrow.createCell(20, messageSource.getMessage("lbl.health.unit", null, null));
                    cell.setFillColor(Color.lightGray);
                    cell.setFont(PDType1Font.HELVETICA_BOLD);
                    cell.setFontSize(10);

                    cell = factHeaderrow.createCell(20, messageSource.getMessage("lbl.receipt.person.name", null, null));
                    cell.setFillColor(Color.lightGray);
                    cell.setFont(PDType1Font.HELVETICA_BOLD);
                    cell.setFontSize(10);

                    cell = factHeaderrow.createCell(15, messageSource.getMessage("lbl.request.large", null, null));
                    cell.setFillColor(Color.lightGray);
                    cell.setFont(PDType1Font.HELVETICA_BOLD);
                    cell.setFontSize(10);
                    y -= 15;

                }

                row = table.createRow(15f);
                cell = row.createCell(12, fact[0]);
                cell.setFont(PDType1Font.HELVETICA);
                cell.setFontSize(10);
                y -= 15;

                for (int i = 1; i < fact.length; i++) {
                    if (i == 1) {
                        cell = row.createCell(16, fact[i]);
                        cell.setFont(PDType1Font.HELVETICA);
                        cell.setFontSize(10);
                    } else if (i == 2) {
                        cell = row.createCell(17, fact[i]);
                        cell.setFont(PDType1Font.HELVETICA);
                        cell.setFontSize(10);
                    } else if (i == 3) {
                        cell = row.createCell(20, fact[i]);
                        cell.setFont(PDType1Font.HELVETICA);
                        cell.setFontSize(10);

                    } else if (i == 4) {
                        cell = row.createCell(20, fact[i]);
                        cell.setFont(PDType1Font.HELVETICA);
                        cell.setFontSize(10);

                    } else if (i == 5) {
                        cell = row.createCell(15, fact[i]);
                        cell.setFont(PDType1Font.HELVETICA);
                        cell.setFontSize(10);
                    }
                }
            }
            table.draw();

            //fecha impresión
            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.print.datetime", null, null), 100, 605, stream, 10, PDType1Font.HELVETICA_BOLD);
            GeneralUtils.drawTEXT(fechaImpresion, 100, 710, stream, 10, PDType1Font.HELVETICA);

            stream.close();

            doc.save(output);
            doc.close();
            // generate the file
            res = Base64.encodeBase64String(output.toByteArray());

        }

        return res;
    }


    /**
     * Método que se llama al entrar a la opción de menu de Reportes "Reporte Resultados Positivos y Negativos".
     *
     * @param request para obtener información de la petición del cliente
     * @return ModelAndView
     * @throws Exception
     */
    @RequestMapping(value = "/posNegResults/init", method = RequestMethod.GET)
    public ModelAndView initReportForm(HttpServletRequest request) throws Exception {
        logger.debug("Iniciando Reporte de Resultados Positivos y Negativos");
        String urlValidacion;
        try {
            urlValidacion = seguridadService.validarLogin(request);
            //si la url esta vacia significa que la validación del login fue exitosa
            if (urlValidacion.isEmpty())
                urlValidacion = seguridadService.validarAutorizacionUsuario(request, ConstantsSecurity.SYSTEM_CODE, false);
        } catch (Exception e) {
            e.printStackTrace();
            urlValidacion = "404";
        }
        ModelAndView mav = new ModelAndView();
        if (urlValidacion.isEmpty()) {
            List<EntidadesAdtvas> entidadesAdtvases = entidadAdmonService.getAllEntidadesAdtvas();
            List<TipoMx> tipoMxList = catalogosService.getTipoMuestra();
            List<Area> areas = areaService.getAreas();
            mav.addObject("entidades", entidadesAdtvases);
            mav.addObject("areas", areas);
            mav.setViewName("reportes/positiveNegativeResults");
        } else
            mav.setViewName(urlValidacion);

        return mav;
    }


    /**
     * Método para realizar la búsqueda de Resultados positivos
     *
     * @param filtro JSon con los datos de los filtros a aplicar en la búsqueda(Rango Fec Aprob, SILAIS, unidad salud, tipo solicitud, descripcion)
     * @return String con las solicitudes encontradas
     * @throws Exception
     */
    @RequestMapping(value = "searchPosNegRequest", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    String fetchPosNegRequestJson(@RequestParam(value = "strFilter", required = true) String filtro) throws Exception {
        logger.info("Obteniendo las solicitudes positivas y negativas según filtros en JSON");
        FiltroMx filtroMx = jsonToFiltroMx(filtro);
        List<DaSolicitudDx> positiveRoutineReqList = null;
        List<DaSolicitudEstudio> positiveStudyReqList = null;

        if (filtroMx.getCodTipoSolicitud() != null) {
            if (filtroMx.getCodTipoSolicitud().equals("Estudio")) {
                positiveStudyReqList = reportesService.getPositiveStudyRequestByFilter(filtroMx);
            } else {
                positiveRoutineReqList = reportesService.getPositiveRoutineRequestByFilter(filtroMx);
            }

        } else {
            positiveRoutineReqList = reportesService.getPositiveRoutineRequestByFilter(filtroMx);
            positiveStudyReqList = reportesService.getPositiveStudyRequestByFilter(filtroMx);
        }

        return requestPositiveNegativeToJson(positiveRoutineReqList, positiveStudyReqList, filtroMx.getResultadoFinal());
    }

    /**
     * Método que convierte una lista de solicitudes a un string con estructura Json
     *
     * @param posNegRoutineReqList lista con las mx recepcionadas a convertir
     * @return String
     */
    private String requestPositiveNegativeToJson(List<DaSolicitudDx> posNegRoutineReqList, List<DaSolicitudEstudio> posNegStudyReqList, String filtroResu) throws Exception {
        String jsonResponse;
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice = 0;


        if (posNegRoutineReqList != null) {
            for (DaSolicitudDx soli : posNegRoutineReqList) {
                boolean mostrar = false;
                String valorResultado = null;
                String content = null;

                //search positive results from list
                //get Response for each request
                List<DetalleResultadoFinal> finalRes = resultadoFinalService.getDetResActivosBySolicitud(soli.getIdSolicitudDx());
                for (DetalleResultadoFinal res : finalRes) {

                    if(filtroResu != null){
                        if(filtroResu.equals("Positivo")){
                            content = getPositiveResult(res);
                        }else{
                            content = getNegativeResult(res);
                        }

                    }else{
                        content = getResult(res);
                    }

                    String[] arrayContent = content.split(",");
                    valorResultado = arrayContent[0];
                    mostrar = Boolean.parseBoolean(arrayContent[1]);

                    if (mostrar) {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("solicitud", soli.getCodDx().getNombre());
                        map.put("idSolicitud", soli.getIdSolicitudDx());
                        map.put("codigoUnicoMx", soli.getIdTomaMx().getCodigoLab());
                        map.put("fechaAprobacion", DateUtil.DateToString(soli.getFechaAprobacion(), "dd/MM/yyyy hh:mm:ss a"));
                        map.put("resultado", valorResultado);

                        if (soli.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion() != null) {
                            map.put("codSilais", soli.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre());
                        } else {
                            map.put("codSilais", "");
                        }
                        if (soli.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion() != null) {
                            map.put("codUnidadSalud", soli.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre());
                        } else {
                            map.put("codUnidadSalud", "");
                        }

                        //Si hay persona
                        if (soli.getIdTomaMx().getIdNotificacion().getPersona() != null) {
                            /// se obtiene el nombre de la persona asociada a la ficha
                            String nombreCompleto = "";
                            nombreCompleto = soli.getIdTomaMx().getIdNotificacion().getPersona().getPrimerNombre();
                            if (soli.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre() != null)
                                nombreCompleto = nombreCompleto + " " + soli.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre();
                            nombreCompleto = nombreCompleto + " " + soli.getIdTomaMx().getIdNotificacion().getPersona().getPrimerApellido();
                            if (soli.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido() != null)
                                nombreCompleto = nombreCompleto + " " + soli.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido();
                            map.put("persona", nombreCompleto);
                        } else {
                            map.put("persona", " ");
                        }

                        mapResponse.put(indice, map);
                        indice++;
                        break;
                    }

                }


            }

        }
        if (posNegStudyReqList != null) {

            for (DaSolicitudEstudio soliE : posNegStudyReqList) {
                boolean mostrar = false;
                String valorResultado = null;
                String content = null;

                //search positive results from list
                //get Response for each request
                List<DetalleResultadoFinal> finalRes = resultadoFinalService.getDetResActivosBySolicitud(soliE.getIdSolicitudEstudio());
                for (DetalleResultadoFinal res : finalRes) {


                    if(filtroResu != null){
                        if(filtroResu.equals("Positivo")){
                            content = getPositiveResult(res);
                        }else{
                            content = getNegativeResult(res);
                        }

                    }else{
                        content = getResult(res);
                    }

                    String[] arrayContent = content.split(",");
                    valorResultado = arrayContent[0];
                    mostrar = Boolean.parseBoolean(arrayContent[1]);

                    if (mostrar) {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("solicitud", soliE.getTipoEstudio().getNombre());
                        map.put("idSolicitud", soliE.getIdSolicitudEstudio());
                        map.put("codigoUnicoMx", soliE.getIdTomaMx().getCodigoUnicoMx());
                        map.put("fechaAprobacion", DateUtil.DateToString(soliE.getFechaAprobacion(), "dd/MM/yyyy hh:mm:ss a"));
                        map.put("resultado", valorResultado);

                        if (soliE.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion() != null) {
                            map.put("codSilais", soliE.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre());
                        } else {
                            map.put("codSilais", "");
                        }
                        if (soliE.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion() != null) {
                            map.put("codUnidadSalud", soliE.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre());
                        } else {
                            map.put("codUnidadSalud", "");
                        }

                        //Si hay persona
                        if (soliE.getIdTomaMx().getIdNotificacion().getPersona() != null) {
                            /// se obtiene el nombre de la persona asociada a la ficha
                            String nombreCompleto = "";
                            nombreCompleto = soliE.getIdTomaMx().getIdNotificacion().getPersona().getPrimerNombre();
                            if (soliE.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre() != null)
                                nombreCompleto = nombreCompleto + " " + soliE.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre();
                            nombreCompleto = nombreCompleto + " " + soliE.getIdTomaMx().getIdNotificacion().getPersona().getPrimerApellido();
                            if (soliE.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido() != null)
                                nombreCompleto = nombreCompleto + " " + soliE.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido();
                            map.put("persona", nombreCompleto);
                        } else {
                            map.put("persona", " ");
                        }

                        mapResponse.put(indice, map);
                        indice++;
                        break;
                    }
                }


            }
        }
        jsonResponse = new Gson().toJson(mapResponse);
        //escapar caracteres especiales, escape de los caracteres con valor numérico mayor a 127
        UnicodeEscaper escaper = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }


    private String getResult(DetalleResultadoFinal res) throws Exception {
        boolean mostrar= false;
        String valorResultado = null;

        if (res.getRespuesta() != null) {
            if (res.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                Integer idLista = Integer.valueOf(res.getValor());
                Catalogo_Lista valor = respuestasExamenService.getCatalogoListaConceptoByIdLista(idLista);

                if (valor.getValor().toLowerCase().equals("positivo") ||valor.getValor().toLowerCase().equals("negativo") ) {
                    mostrar = true;
                    valorResultado = valor.getValor();
                }

            } else if (res.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|TXT")) {
                if (res.getValor().toLowerCase().equals("positivo") || res.getValor().toLowerCase().equals("negativo")) {
                    mostrar = true;
                    valorResultado = res.getValor();
                }
            }
        } else if (res.getRespuestaExamen() != null) {
            if (res.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                Integer idLista = Integer.valueOf(res.getValor());
                Catalogo_Lista valor = respuestasExamenService.getCatalogoListaConceptoByIdLista(idLista);

                if (valor.getValor().toLowerCase().equals("positivo") || valor.getValor().toLowerCase().equals("negativo") ) {
                    mostrar = true;
                    valorResultado = valor.getValor();
                }

            } else if (res.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|TXT")) {
                if (res.getValor().toLowerCase().equals("positivo") || res.getValor().toLowerCase().equals("negativo")) {
                    mostrar = true;
                    valorResultado = res.getValor();
                }
            }

        }
        return valorResultado + "," + mostrar;
    }

    private String getNegativeResult(DetalleResultadoFinal res) throws Exception {
        boolean mostrar= false;
        String valorResultado = null;

        if (res.getRespuesta() != null) {
            if (res.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                Integer idLista = Integer.valueOf(res.getValor());
                Catalogo_Lista valor = respuestasExamenService.getCatalogoListaConceptoByIdLista(idLista);

                if (valor.getValor().toLowerCase().equals("negativo") ) {
                    mostrar = true;
                    valorResultado = valor.getValor();
                }

            } else if (res.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|TXT")) {
                if (res.getValor().toLowerCase().equals("negativo")) {
                    mostrar = true;
                    valorResultado = res.getValor();
                }
            }
        } else if (res.getRespuestaExamen() != null) {
            if (res.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                Integer idLista = Integer.valueOf(res.getValor());
                Catalogo_Lista valor = respuestasExamenService.getCatalogoListaConceptoByIdLista(idLista);

                if (valor.getValor().toLowerCase().equals("negativo") ) {
                    mostrar = true;
                    valorResultado = valor.getValor();
                }

            } else if (res.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|TXT")) {
                if (res.getValor().toLowerCase().equals("negativo")) {
                    mostrar = true;
                    valorResultado = res.getValor();
                }
            }

        }
        return valorResultado + "," + mostrar;
    }

    private String getPositiveResult(DetalleResultadoFinal res) throws Exception {
        boolean mostrar= false;
        String valorResultado = null;

        if (res.getRespuesta() != null) {
            if (res.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                Integer idLista = Integer.valueOf(res.getValor());
                Catalogo_Lista valor = respuestasExamenService.getCatalogoListaConceptoByIdLista(idLista);

                if (valor.getValor().toLowerCase().equals("positivo") ) {
                    mostrar = true;
                    valorResultado = valor.getValor();
                }

            } else if (res.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|TXT")) {
                if (res.getValor().toLowerCase().equals("positivo")) {
                    mostrar = true;
                    valorResultado = res.getValor();
                }
            }
        } else if (res.getRespuestaExamen() != null) {
            if (res.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                Integer idLista = Integer.valueOf(res.getValor());
                Catalogo_Lista valor = respuestasExamenService.getCatalogoListaConceptoByIdLista(idLista);

                if (valor.getValor().toLowerCase().equals("positivo")) {
                    mostrar = true;
                    valorResultado = valor.getValor();
                }

            } else if (res.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|TXT")) {
                if (res.getValor().toLowerCase().equals("positivo")) {
                    mostrar = true;
                    valorResultado = res.getValor();
                }
            }

        }
        return valorResultado + "," + mostrar;
    }

    @RequestMapping(value = "posNegRequestToPDF", method = RequestMethod.GET)
    public
    @ResponseBody
    String posNegRequestToPDF(@RequestParam(value = "codes", required = true) String codes, @RequestParam(value = "fromDate", required = false) String fromDate, @RequestParam(value = "toDate", required = false) String toDate, HttpServletRequest request) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PDDocument doc = new PDDocument();
        Laboratorio labProcesa = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
        String res = null;
        String fechaImpresion = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());


        if (!codes.isEmpty()) {

            PDPage page = new PDPage(PDPage.PAGE_SIZE_A4);
            page.setRotation(90);
            doc.addPage(page);
            PDPageContentStream stream = new PDPageContentStream(doc, page);
            stream.concatenate2CTM(0, 1, -1, 0, page.getMediaBox().getWidth(), 0);
            float xCenter;

            GeneralUtils.drawHeaderAndFooter(stream, doc, 500, 840, 90, 840, 70);

            String pageNumber = String.valueOf(doc.getNumberOfPages());
            GeneralUtils.drawTEXT(pageNumber, 15, 800, stream, 10, PDType1Font.HELVETICA_BOLD);

            drawInfoLab(stream, page, labProcesa);

            float y = 400;
            float m = 20;

            //nombre del reporte
            xCenter = centerTextPositionX(page, PDType1Font.HELVETICA_BOLD, 12, messageSource.getMessage("lbl.positiveResultReport", null, null).toUpperCase());
            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.posNegReport", null, null).toUpperCase(), y, xCenter, stream, 12, PDType1Font.HELVETICA_BOLD);
            y = y - 10;
            //Rango de Fechas
            if (!fromDate.equals("") && !toDate.equals("")) {
                GeneralUtils.drawTEXT(messageSource.getMessage("lbl.from", null, null), y, 55, stream, 12, PDType1Font.HELVETICA_BOLD);
                GeneralUtils.drawTEXT(fromDate, y, 100, stream, 12, PDType1Font.HELVETICA_BOLD);

                GeneralUtils.drawTEXT(messageSource.getMessage("lbl.to", null, null), y, 660, stream, 12, PDType1Font.HELVETICA_BOLD);
                GeneralUtils.drawTEXT(toDate, y, 720, stream, 12, PDType1Font.HELVETICA_BOLD);
                y -= m;
            }


            String[] idSoli = codes.split(",");
            List<String[]> reqList = new ArrayList<String[]>();


            for (String idSolicitud : idSoli) {
                String nombreSolitud = null;
                String nombrePersona = null;
                String fechaAprob = null;
                String silais = null;
                String unidadSalud = null;
                String[] content = null;

                DaSolicitudDx soli = tomaMxService.getSolicitudDxByIdSolicitudUser(idSolicitud, seguridadService.obtenerNombreUsuario());
                DaSolicitudEstudio soliE = tomaMxService.getSolicitudEstByIdSolicitud(idSolicitud);


                if(soli != null){
                String cont = null;
                String valorResultado= null;
                boolean mostrar = false;

                    List<DetalleResultadoFinal> finalRes = resultadoFinalService.getDetResActivosBySolicitud(soli.getIdSolicitudDx());
                    for (DetalleResultadoFinal resu : finalRes) {

                        cont = getResult(resu);
                        String[] arrayContent = cont.split(",");
                        valorResultado = arrayContent[0];
                        mostrar = Boolean.parseBoolean(arrayContent[1]);

                        if(mostrar){
                            content = new String[7];
                            nombreSolitud = soli.getCodDx().getNombre();

                            nombrePersona = soli.getIdTomaMx().getIdNotificacion().getPersona().getPrimerNombre();
                            if (soli.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre() != null)
                                nombrePersona = nombrePersona + " " + soli.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre();
                            nombrePersona = nombrePersona + " " + soli.getIdTomaMx().getIdNotificacion().getPersona().getPrimerApellido();
                            if (soli.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido() != null)
                                nombrePersona = nombrePersona + " " + soli.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido();

                            if (soli.getFechaAprobacion() != null) {
                                fechaAprob = DateUtil.DateToString(soli.getFechaAprobacion(), "dd/MM/yyyy hh:mm:ss a");
                            }

                            if (soli.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion() != null) {
                                silais = soli.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre();
                            }

                            if (soli.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion() != null) {
                                unidadSalud = soli.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre();
                            }

                            content[0] = soli.getIdTomaMx() != null ? soli.getIdTomaMx().getCodigoUnicoMx() : "";
                            content[1] = fechaAprob != null ? fechaAprob : "";
                            content[2] = silais != null ? silais : "";
                            content[3] = unidadSalud != null ? unidadSalud : "";
                            content[4] = nombrePersona != null ? nombrePersona : "";
                            content[5] = nombreSolitud != null ? nombreSolitud : "";
                            content[6] = valorResultado != null ? valorResultado : "";
                            reqList.add(content);
                            break;
                        }

                    }

                }

                if (soliE != null) {
                    String cont = null;
                    String valorResultado = null;
                    boolean mostrar = false;

                    List<DetalleResultadoFinal> finalRes = resultadoFinalService.getDetResActivosBySolicitud(soliE.getIdSolicitudEstudio());
                    for (DetalleResultadoFinal resu : finalRes) {

                        cont = getResult(resu);
                        String[] arrayContent = cont.split(",");
                        valorResultado = arrayContent[0];
                        mostrar = Boolean.parseBoolean(arrayContent[1]);

                        if(mostrar){
                            content = new String[7];
                            nombreSolitud = soliE.getTipoEstudio().getNombre();

                            nombrePersona = soliE.getIdTomaMx().getIdNotificacion().getPersona().getPrimerNombre();
                            if (soliE.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre() != null)
                                nombrePersona = nombrePersona + " " + soliE.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre();
                            nombrePersona = nombrePersona + " " + soliE.getIdTomaMx().getIdNotificacion().getPersona().getPrimerApellido();
                            if (soliE.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido() != null)
                                nombrePersona = nombrePersona + " " + soliE.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido();

                            if (soliE.getFechaAprobacion() != null) {
                                fechaAprob = DateUtil.DateToString(soliE.getFechaAprobacion(), "dd/MM/yyyy hh:mm:ss a");
                            }

                            if (soliE.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion() != null) {
                                silais = soliE.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre();
                            }

                            if (soliE.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion() != null) {
                                unidadSalud = soliE.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre();
                            }

                            content[0] = soliE.getIdTomaMx() != null ? soliE.getIdTomaMx().getCodigoUnicoMx() : "";
                            content[1] = fechaAprob != null ? fechaAprob : "";
                            content[2] = silais != null ? silais : "";
                            content[3] = unidadSalud != null ? unidadSalud : "";
                            content[4] = nombrePersona != null ? nombrePersona : "";
                            content[5] = nombreSolitud != null ? nombreSolitud : "";
                            content[6] = valorResultado != null? valorResultado: "";
                            reqList.add(content);
                            break;
                        }
                    }

                }
            }

            //drawTable

            //Initialize table
            float margin = 50;
            float tableWidth = 730;
            float yStartNewPage = y;
            float yStart = yStartNewPage;
            float bottomMargin = 45;
            BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true, true);

            //Create Header row
            Row headerRow = table.createRow(15f);
            table.setHeader(headerRow);

            //Create 2 column row
            Cell cell;
            Row row;

            //Create Fact header row
            Row factHeaderrow = table.createRow(15f);
            cell = factHeaderrow.createCell(12, messageSource.getMessage("lbl.lab.code.mx", null, null));
            cell.setFont(PDType1Font.HELVETICA_BOLD);
            cell.setFontSize(10);
            cell.setFillColor(Color.LIGHT_GRAY);

            cell = factHeaderrow.createCell(16, messageSource.getMessage("lbl.approve.date", null, null));
            cell.setFillColor(Color.lightGray);
            cell.setFont(PDType1Font.HELVETICA_BOLD);
            cell.setFontSize(10);

            cell = factHeaderrow.createCell(17, messageSource.getMessage("lbl.silais", null, null));
            cell.setFillColor(Color.lightGray);
            cell.setFont(PDType1Font.HELVETICA_BOLD);
            cell.setFontSize(10);

            cell = factHeaderrow.createCell(20, messageSource.getMessage("lbl.health.unit", null, null));
            cell.setFillColor(Color.lightGray);
            cell.setFont(PDType1Font.HELVETICA_BOLD);
            cell.setFontSize(10);

            cell = factHeaderrow.createCell(15, messageSource.getMessage("lbl.receipt.person.name", null, null));
            cell.setFillColor(Color.lightGray);
            cell.setFont(PDType1Font.HELVETICA_BOLD);
            cell.setFontSize(10);

            cell = factHeaderrow.createCell(10, messageSource.getMessage("lbl.request.large", null, null));
            cell.setFillColor(Color.lightGray);
            cell.setFont(PDType1Font.HELVETICA_BOLD);
            cell.setFontSize(10);

            cell = factHeaderrow.createCell(10, messageSource.getMessage("lbl.final.result", null, null));
            cell.setFillColor(Color.lightGray);
            cell.setFont(PDType1Font.HELVETICA_BOLD);
            cell.setFontSize(10);
            y -= 15;

            //Add multiple rows with random facts about Belgium
            for (String[] fact : reqList) {

                if (y < 260) {
                    table.draw();
                    stream.close();
                    page = new PDPage(PDPage.PAGE_SIZE_A4);
                    page.setRotation(90);
                    doc.addPage(page);
                    stream = new PDPageContentStream(doc, page);
                    stream.concatenate2CTM(0, 1, -1, 0, page.getMediaBox().getWidth(), 0);
                    y = 470;
                    GeneralUtils.drawHeaderAndFooter(stream, doc, 500, 840, 90, 840, 70);

                    pageNumber = String.valueOf(doc.getNumberOfPages());
                    GeneralUtils.drawTEXT(pageNumber, 15, 800, stream, 10, PDType1Font.HELVETICA_BOLD);

                    table = new BaseTable(y, y, bottomMargin, tableWidth, margin, doc, page, true, true);

                    //Create Header row
                    headerRow = table.createRow(15f);
                    table.setHeader(headerRow);

                    //Create Fact header row
                    factHeaderrow = table.createRow(15f);
                    cell = factHeaderrow.createCell(15, messageSource.getMessage("lbl.lab.code.mx", null, null));
                    cell.setFont(PDType1Font.HELVETICA_BOLD);
                    cell.setFontSize(10);
                    cell.setFillColor(Color.LIGHT_GRAY);

                    cell = factHeaderrow.createCell(15, messageSource.getMessage("lbl.approve.date", null, null));
                    cell.setFillColor(Color.lightGray);
                    cell.setFont(PDType1Font.HELVETICA_BOLD);
                    cell.setFontSize(10);

                    cell = factHeaderrow.createCell(15, messageSource.getMessage("lbl.silais", null, null));
                    cell.setFillColor(Color.lightGray);
                    cell.setFont(PDType1Font.HELVETICA_BOLD);
                    cell.setFontSize(10);

                    cell = factHeaderrow.createCell(20, messageSource.getMessage("lbl.health.unit", null, null));
                    cell.setFillColor(Color.lightGray);
                    cell.setFont(PDType1Font.HELVETICA_BOLD);
                    cell.setFontSize(10);

                    cell = factHeaderrow.createCell(15, messageSource.getMessage("lbl.receipt.person.name", null, null));
                    cell.setFillColor(Color.lightGray);
                    cell.setFont(PDType1Font.HELVETICA_BOLD);
                    cell.setFontSize(10);

                    cell = factHeaderrow.createCell(10, messageSource.getMessage("lbl.request.large", null, null));
                    cell.setFillColor(Color.lightGray);
                    cell.setFont(PDType1Font.HELVETICA_BOLD);
                    cell.setFontSize(10);

                    cell = factHeaderrow.createCell(10, messageSource.getMessage("lbl.final.result", null, null));
                    cell.setFillColor(Color.lightGray);
                    cell.setFont(PDType1Font.HELVETICA_BOLD);
                    cell.setFontSize(10);
                    y -= 15;

                }

                row = table.createRow(15f);
                cell = row.createCell(12, fact[0]);
                cell.setFont(PDType1Font.HELVETICA);
                cell.setFontSize(10);
                y -= 15;

                for (int i = 1; i < fact.length; i++) {
                    if (i == 1) {
                        cell = row.createCell(16, fact[i]);
                        cell.setFont(PDType1Font.HELVETICA);
                        cell.setFontSize(10);
                    } else if (i == 2) {
                        cell = row.createCell(17, fact[i]);
                        cell.setFont(PDType1Font.HELVETICA);
                        cell.setFontSize(10);
                    } else if (i == 3) {
                        cell = row.createCell(20, fact[i]);
                        cell.setFont(PDType1Font.HELVETICA);
                        cell.setFontSize(10);

                    } else if (i == 4) {
                        cell = row.createCell(15, fact[i]);
                        cell.setFont(PDType1Font.HELVETICA);
                        cell.setFontSize(10);

                    } else if (i == 5) {
                        cell = row.createCell(10, fact[i]);
                        cell.setFont(PDType1Font.HELVETICA);
                        cell.setFontSize(10);
                    } else if (i == 6) {
                        cell = row.createCell(10, fact[i]);
                        cell.setFont(PDType1Font.HELVETICA);
                        cell.setFontSize(10);
                    }
                }
            }
            table.draw();

            //fecha impresión
            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.print.datetime", null, null), 100, 605, stream, 10, PDType1Font.HELVETICA_BOLD);
            GeneralUtils.drawTEXT(fechaImpresion, 100, 710, stream, 10, PDType1Font.HELVETICA);

            stream.close();

            doc.save(output);
            doc.close();
            // generate the file
            res = Base64.encodeBase64String(output.toByteArray());

        }

        return res;
    }

    /**
     * Método que se llama al entrar a la opción de menu de Reportes "Control de Calidad".
     *
     * @param request para obtener información de la petición del cliente
     * @return ModelAndView
     * @throws Exception
     */
    @RequestMapping(value = "/qualityControl/init", method = RequestMethod.GET)
    public ModelAndView initSearchQCForm(HttpServletRequest request) throws Exception {
        logger.debug("Iniciando Reporte de Recepción");
        ModelAndView mav = new ModelAndView();
        List<EntidadesAdtvas> entidadesAdtvases = entidadAdmonService.getAllEntidadesAdtvas();
        List<Laboratorio> laboratorios = laboratoriosService.getLaboratoriosRegionales();
        List<TipoMx> tipoMxList = catalogosService.getTipoMuestra();
        mav.addObject("entidades", entidadesAdtvases);
        mav.addObject("tipoMuestra", tipoMxList);
        mav.addObject("laboratorios",laboratorios);
        mav.setViewName("reportes/qualityControlReport");
        return mav;
    }

    /**
     * Método para realizar la búsqueda de Mx recepcionadas
     *
     * @param filtro JSon con los datos de los filtros a aplicar en la búsqueda(Rango Fec Recepcion, Tipo Mx, SILAIS, unidad salud, tipo solicitud, descripcion)
     * @return String con las Mx encontradas
     * @throws Exception
     */
    @RequestMapping(value = "searchSamplesQC", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    String fetchMxQCJson(@RequestParam(value = "strFilter", required = true) String filtro) throws Exception {
        logger.info("Obteniendo las mx recepcionadas según filtros en JSON");
        FiltroMx filtroMx = jsonToFiltroMx(filtro);
        filtroMx.setControlCalidad(true);
        List<DaSolicitudDx> receivedList = reportesService.getQCRoutineRequestByFilter(filtroMx);
       return solicitudesDxToJson(receivedList, true);
    }

    private  String solicitudesDxToJson(List<DaSolicitudDx> solicitudDxList, boolean incluirResultados){
        String jsonResponse="";
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice=0;
        for(DaSolicitudDx diagnostico : solicitudDxList){
            Map<String, String> map = new HashMap<String, String>();
            map.put("codigoUnicoMx", diagnostico.getIdTomaMx().getCodigoLab());
            map.put("idTomaMx", diagnostico.getIdTomaMx().getIdTomaMx());
            map.put("fechaTomaMx",DateUtil.DateToString(diagnostico.getIdTomaMx().getFechaHTomaMx(),"dd/MM/yyyy hh:mm:ss a"));
            if (diagnostico.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion()!=null) {
                map.put("codSilais", diagnostico.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre());
            }else{
                map.put("codSilais","");
            }
            if (diagnostico.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion()!=null) {
                map.put("codUnidadSalud", diagnostico.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre());
            }else{
                map.put("codUnidadSalud","");
            }
            map.put("tipoMuestra", diagnostico.getIdTomaMx().getCodTipoMx().getNombre());
            map.put("tipoNotificacion", diagnostico.getIdTomaMx().getIdNotificacion().getCodTipoNotificacion().getValor());
            //Si hay fecha de inicio de sintomas se muestra
            Date fechaInicioSintomas = diagnostico.getIdTomaMx().getIdNotificacion().getFechaInicioSintomas();
            if (fechaInicioSintomas!=null)
                map.put("fechaInicioSintomas",DateUtil.DateToString(fechaInicioSintomas,"dd/MM/yyyy"));
            else
                map.put("fechaInicioSintomas"," ");

            //Si hay persona
            if (diagnostico.getIdTomaMx().getIdNotificacion().getPersona()!=null){
                /// se obtiene el nombre de la persona asociada a la ficha
                String nombreCompleto = "";
                nombreCompleto = diagnostico.getIdTomaMx().getIdNotificacion().getPersona().getPrimerNombre();
                if (diagnostico.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre()!=null)
                    nombreCompleto = nombreCompleto +" "+ diagnostico.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre();
                nombreCompleto = nombreCompleto+" "+ diagnostico.getIdTomaMx().getIdNotificacion().getPersona().getPrimerApellido();
                if (diagnostico.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido()!=null)
                    nombreCompleto = nombreCompleto +" "+ diagnostico.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido();
                map.put("persona",nombreCompleto);
            }else{
                map.put("persona"," ");
            }

            map.put("solicitud", diagnostico.getCodDx().getNombre());
            map.put("idSolicitud", diagnostico.getIdSolicitudDx());
            map.put("fechaSolicitud",DateUtil.DateToString(diagnostico.getFechaHSolicitud(),"dd/MM/yyyy hh:mm:ss a"));

            if(diagnostico.getAprobada()!=null && diagnostico.getAprobada() && diagnostico.getFechaAprobacion()!=null){
                map.put("fechaAprobacion",DateUtil.DateToString(diagnostico.getFechaAprobacion(),"dd/MM/yyyy hh:mm:ss a"));
            }else {
                map.put("fechaAprobacion","");
            }
            TrasladoMx trasladoMxCC = trasladosService.getTrasladoCCMx(diagnostico.getIdTomaMx().getIdTomaMx());
            if (trasladoMxCC!=null) {
                map.put("laboratorio", trasladoMxCC.getLaboratorioOrigen().getNombre());
            }else {
                map.put("laboratorio", "-");
            }

            if (incluirResultados){
                //detalle resultado final solicitud
                List<DetalleResultadoFinal> resultList = resultadoFinalService.getDetResActivosBySolicitud(diagnostico.getIdSolicitudDx());
                int subIndice=1;
                Map<Integer, Object> mapResponseResp = new HashMap<Integer, Object>();
                for(DetalleResultadoFinal res: resultList){
                    Map<String, String> mapResp = new HashMap<String, String>();
                    if (res.getRespuesta()!=null) {
                        if (res.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                            Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(res.getValor());
                            mapResp.put("valor", cat_lista.getValor());
                        }else if (res.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                            String valorBoleano = (Boolean.valueOf(res.getValor())?"lbl.yes":"lbl.no");
                            mapResp.put("valor", messageSource.getMessage(valorBoleano, null, null));
                        } else {
                            mapResp.put("valor", res.getValor());
                        }
                        mapResp.put("respuesta", res.getRespuesta().getNombre());

                    }else if (res.getRespuestaExamen()!=null){
                        if (res.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                            Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(res.getValor());
                            mapResp.put("valor", cat_lista.getValor());
                        } else if (res.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                            String valorBoleano = (Boolean.valueOf(res.getValor())?"lbl.yes":"lbl.no");
                            mapResp.put("valor", messageSource.getMessage(valorBoleano,null,null));
                        }else {
                            mapResp.put("valor", res.getValor());
                        }
                        mapResp.put("respuesta", res.getRespuestaExamen().getNombre());
                    }
                    mapResp.put("fechaResultado", DateUtil.DateToString(res.getFechahRegistro(), "dd/MM/yyyy hh:mm:ss a"));
                    mapResponseResp.put(subIndice,mapResp);
                    subIndice++;
                }
                map.put("resultados",new Gson().toJson(mapResponseResp));
            }
            mapResponse.put(indice, map);
            indice ++;
        }

        jsonResponse = new Gson().toJson(mapResponse);
        UnicodeEscaper escaper     = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }

    @RequestMapping(value = "expQCToPDF", method = RequestMethod.GET)
    public
    @ResponseBody
    String expQCToPDF(@RequestParam(value = "codigos", required = true) String codigos, HttpServletRequest request) throws IOException, COSVisitorException, ParseException {
        String res = null;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        String fechaAprobacion = null;
        List<DetalleResultadoFinal> detalleResultado = null;
        String fechaImpresion = null;
        PDDocument doc = new PDDocument();
        List<DaSolicitudDx> solicDx = null;
        DaSolicitudDx detalleSoliDx = null;
        PDPageContentStream stream = null;
        Laboratorio labProcesa = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());

        String[] tomasArray = codigos.split(",");
        boolean reporteCRes = false;

        for (String codigoMx : tomasArray) {
            solicDx = tomaMxService.getSolicitudesDxQCAprobByToma(codigoMx);

            fechaAprobacion = null;


                //Obtener las respuestas activas de la solicitud
                if (solicDx.size()>0) {
                    reporteCRes = true;
                    for (DaSolicitudDx solicitudDx : solicDx) {
                        detalleSoliDx = solicitudDx;
                        detalleResultado = resultadoFinalService.getDetResActivosBySolicitud(solicitudDx.getIdSolicitudDx());
                        fechaImpresion = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());

                        if (solicitudDx.getFechaAprobacion() != null) {
                            fechaAprobacion = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").format(solicitudDx.getFechaAprobacion());
                        }


                        if (detalleResultado != null) {

                            //Prepare the document.
                            float y = 480;
                            float m1 = 20;

                            PDPage page = new PDPage(PDPage.PAGE_SIZE_A4);
                            doc.addPage(page);
                            stream = new PDPageContentStream(doc, page);

                            GeneralUtils.drawHeaderAndFooter(stream, doc, 750, 590, 80, 600, 70);
                            drawInfoLabVertical(stream, page, labProcesa);
                            drawReportHeader(stream, detalleSoliDx, null);

                            drawInfoSample(stream, detalleSoliDx, null, y);
                            y -= m1;


                            boolean lista = false;
                            String valor = null;
                            String respuesta;
                            String[][] content = new String[detalleResultado.size()][2];


                            int numFila = 0;
                            for (DetalleResultadoFinal resul : detalleResultado) {
                                y = y - 20;
                                if (resul.getRespuesta() != null) {
                                    respuesta = resul.getRespuesta().getNombre();
                                    lista = resul.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST");
                                } else {
                                    respuesta = resul.getRespuestaExamen().getNombre();
                                    lista = resul.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST");
                                }

                                if (lista) {
                                    Catalogo_Lista catLista = conceptoService.getCatalogoListaById(Integer.valueOf(resul.getValor()));
                                    valor = catLista.getValor();
                                } else {
                                    valor = resul.getValor();
                                }


                                content[numFila][0] = respuesta;
                                content[numFila][1] = valor;
                                numFila++;

                            }

                            drawFinalResultTable(content, doc, page, y);
                            y = y - 140;
                            drawFinalInfo(stream, y, fechaAprobacion, fechaImpresion);
                            stream.close();

                        }
                    }
                }

        }
        if (reporteCRes) {
            doc.save(output);
            doc.close();
            // generate the file
            res = Base64.encodeBase64String(output.toByteArray());
        }

        return res;
    }

    private void drawReportHeader(PDPageContentStream stream, DaSolicitudDx soliDx, DaSolicitudEstudio soliE) throws IOException {
        String nombreSoli = null;
        String nombrePersona = null;
        String nombreSilais = null;
        String nombreUS = null;
        int edad = 0;
        String sexo = null;
        String fis = null;
        String labOrigen = "----------------";
        float inY = 610;
        float m = 20;

        if (soliDx != null || soliE != null) {

            if (soliDx != null) {
                nombrePersona = soliDx.getIdTomaMx().getIdNotificacion().getPersona().getPrimerNombre();
                nombreSoli = soliDx.getCodDx().getNombre();

                if (soliDx.getIdTomaMx().getIdNotificacion().getFechaInicioSintomas() != null) {
                    fis = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").format(soliDx.getIdTomaMx().getIdNotificacion().getFechaInicioSintomas());
                } else {
                    fis = "---------------";
                }

                if (soliDx.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre() != null) {
                    nombrePersona = nombrePersona + " " + soliDx.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre();
                    nombrePersona = nombrePersona + " " + soliDx.getIdTomaMx().getIdNotificacion().getPersona().getPrimerApellido();
                } else {
                    nombrePersona = nombrePersona + " " + soliDx.getIdTomaMx().getIdNotificacion().getPersona().getPrimerApellido();

                }
                if (soliDx.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido() != null) {
                    nombrePersona = nombrePersona + " " + soliDx.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido();
                }

                if (soliDx.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion() != null) {
                    nombreSilais = soliDx.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre();
                    nombreUS = soliDx.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre();

                } else {
                    nombreSilais = "---------------";
                    nombreUS = "---------------";
                }

                if (soliDx.getIdTomaMx().getIdNotificacion().getPersona().getSexo().getValor() != null) {
                    sexo = soliDx.getIdTomaMx().getIdNotificacion().getPersona().getSexo().getValor();
                } else {
                    sexo = "----------------";
                }

                if (soliDx.getIdTomaMx().getIdNotificacion().getPersona().getFechaNacimiento() != null) {
                    String fechaformateada = DateUtil.DateToString(soliDx.getIdTomaMx().getIdNotificacion().getPersona().getFechaNacimiento(), "dd/MM/yyyy");
                    edad = DateUtil.edad(fechaformateada);
                }

                TrasladoMx trasladoMxCC = trasladosService.getTrasladoCCMx(soliDx.getIdTomaMx().getIdTomaMx());
                if (trasladoMxCC!=null) {
                     labOrigen = trasladoMxCC.getLaboratorioOrigen().getNombre();
                }

            } else {
                nombrePersona = soliE.getIdTomaMx().getIdNotificacion().getPersona().getPrimerNombre();
                nombreSoli = soliE.getTipoEstudio().getNombre();

                if (soliE.getIdTomaMx().getIdNotificacion().getFechaInicioSintomas() != null) {
                    fis = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").format(soliE.getIdTomaMx().getIdNotificacion().getFechaInicioSintomas());
                } else {
                    fis = "---------------";
                }

                if (soliE.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre() != null) {
                    nombrePersona = nombrePersona + " " + soliE.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre();
                    nombrePersona = nombrePersona + " " + soliE.getIdTomaMx().getIdNotificacion().getPersona().getPrimerApellido();
                } else {
                    nombrePersona = nombrePersona + " " + soliE.getIdTomaMx().getIdNotificacion().getPersona().getPrimerApellido();

                }
                if (soliE.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido() != null) {
                    nombrePersona = nombrePersona + " " + soliE.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido();
                }

                if (soliE.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion() != null) {
                    nombreSilais = soliE.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre();
                    nombreUS = soliE.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre();

                } else {
                    nombreSilais = "---------------";
                    nombreUS = "---------------";
                }

                if (soliE.getIdTomaMx().getIdNotificacion().getPersona().getSexo().getValor() != null) {
                    sexo = soliE.getIdTomaMx().getIdNotificacion().getPersona().getSexo().getValor();
                } else {
                    sexo = "----------------";
                }

                if (soliE.getIdTomaMx().getIdNotificacion().getPersona().getFechaNacimiento() != null) {
                    String fechaformateada = DateUtil.DateToString(soliE.getIdTomaMx().getIdNotificacion().getPersona().getFechaNacimiento(), "dd/MM/yyyy");
                    edad = DateUtil.edad(fechaformateada);
                }


            }

            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.full.origin.lab", null, null) + ": ", inY, 15, stream, 14, PDType1Font.HELVETICA_BOLD);
            GeneralUtils.drawTEXT(labOrigen, inY, 160, stream, 12, PDType1Font.HELVETICA);
            inY -= m;

            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.request.name1", null, null) + " ", inY, 15, stream, 14, PDType1Font.HELVETICA_BOLD);
            GeneralUtils.drawTEXT(nombreSoli.toUpperCase(), inY, 80, stream, 12, PDType1Font.HELVETICA);
            inY -= m;

            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.name1", null, null) + " ", inY, 15, stream, 14, PDType1Font.HELVETICA_BOLD);
            GeneralUtils.drawTEXT(nombrePersona, inY, 80, stream, 12, PDType1Font.HELVETICA);

            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.age", null, null) + " ", inY, 380, stream, 14, PDType1Font.HELVETICA_BOLD);
            GeneralUtils.drawTEXT(String.valueOf(edad), inY, 425, stream, 12, PDType1Font.HELVETICA);

            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.sex", null, null) + " ", inY, 450, stream, 14, PDType1Font.HELVETICA_BOLD);
            GeneralUtils.drawTEXT(sexo, inY, 495, stream, 12, PDType1Font.HELVETICA);
            inY -= m;

            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.silais1", null, null) + " ", inY, 15, stream, 14, PDType1Font.HELVETICA_BOLD);
            GeneralUtils.drawTEXT(nombreSilais, inY, 80, stream, 12, PDType1Font.HELVETICA);
            inY -= m;

            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.health.unit1", null, null) + " ", inY, 15, stream, 14, PDType1Font.HELVETICA_BOLD);
            GeneralUtils.drawTEXT(nombreUS, inY, 140, stream, 12, PDType1Font.HELVETICA);
            inY -= m;

            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.fis", null, null) + " ", inY, 15, stream, 14, PDType1Font.HELVETICA_BOLD);
            GeneralUtils.drawTEXT(fis, inY, 210, stream, 12, PDType1Font.HELVETICA);

        }

    }

    private void drawInfoSample(PDPageContentStream stream, DaSolicitudDx solDx, DaSolicitudEstudio solE, float inY) throws IOException {
        float m = 20;
        String fechaRecepcion = null;
        String fechaToma = null;
        String fechaResultado = null;
        Laboratorio laboratorioUsuario = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario()); //laboratorio al que pertenece el usuario
        if (solDx != null || solE != null) {
            if (solDx != null) {

                if (solDx.getIdTomaMx().getFechaHTomaMx() != null) {
                    fechaToma = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").format(solDx.getIdTomaMx().getFechaHTomaMx());
                }

                RecepcionMx recepcion = recepcionMxService.getRecepcionMxByCodUnicoMx(solDx.getIdTomaMx().getCodigoUnicoMx(), (laboratorioUsuario.getCodigo() != null ? laboratorioUsuario.getCodigo() : ""));
                if (recepcion != null) {
                    fechaRecepcion = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").format(recepcion.getFechaHoraRecepcion());
                }


                Object fechaResultadoFinal = resultadoFinalService.getFechaResultadoByIdSoli(solDx.getIdSolicitudDx());
                if (fechaResultadoFinal != null) {
                    fechaResultado = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").format(fechaResultadoFinal);
                }


            } else {

                if (solE.getIdTomaMx().getFechaHTomaMx() != null) {
                    fechaToma = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").format(solE.getIdTomaMx().getFechaHTomaMx());
                }

                RecepcionMx recepcion = recepcionMxService.getRecepcionMxByCodUnicoMx(solE.getIdTomaMx().getCodigoUnicoMx(), (laboratorioUsuario.getCodigo() != null ? laboratorioUsuario.getCodigo() : ""));
                if (recepcion != null) {
                    fechaRecepcion = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").format(recepcion.getFechaHoraRecepcion());
                }

                Object fechaResultadoFinal = resultadoFinalService.getFechaResultadoByIdSoli(solE.getIdSolicitudEstudio());
                if (fechaResultadoFinal != null) {
                    fechaResultado = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").format(fechaResultadoFinal);
                }

            }

            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.sampling.datetime1", null, null) + " ", inY, 15, stream, 14, PDType1Font.HELVETICA_BOLD);
            GeneralUtils.drawTEXT(fechaToma, inY, 140, stream, 12, PDType1Font.HELVETICA);


            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.reception.datetime", null, null) + " ", inY, 310, stream, 14, PDType1Font.HELVETICA_BOLD);
            GeneralUtils.drawTEXT(fechaRecepcion!=null?fechaRecepcion:"", inY, 435, stream, 12, PDType1Font.HELVETICA);
            inY -= m;

            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.finalResult.datetime", null, null) + " ", inY, 15, stream, 14, PDType1Font.HELVETICA_BOLD);
            GeneralUtils.drawTEXT(fechaResultado!=null?fechaResultado:"", inY, 180, stream, 12, PDType1Font.HELVETICA);

        }

    }

    private void drawFinalResultTable(String[][] content, PDDocument doc, PDPage page, float y) throws IOException {

        //Initialize table
        float margin = 50;
        float tableWidth = 500;
        float yStartNewPage = y;
        float yStart = yStartNewPage;
        float bottomMargin = 45;
        BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true, true);

        //Create Header row
        Row headerRow = table.createRow(15f);
      /*  Cell cell = headerRow.createCell(100, "");
        cell.setFont(PDType1Font.HELVETICA_BOLD);
        cell.setFillColor(Color.black);
        cell.setTextColor(Color.WHITE);*/

        table.setHeader(headerRow);

        //Create 2 column row
        Row row;
        Cell cell;

        //Create Fact header row
        Row factHeaderrow = table.createRow(15f);
        cell = factHeaderrow.createCell(50, messageSource.getMessage("lbl.approve.response", null, null));
        cell.setFont(PDType1Font.HELVETICA_BOLD);
        cell.setFontSize(10);
        cell.setFillColor(Color.LIGHT_GRAY);

        cell = factHeaderrow.createCell((50), messageSource.getMessage("lbl.value", null, null));
        cell.setFillColor(Color.lightGray);
        cell.setFont(PDType1Font.HELVETICA_BOLD);
        cell.setFontSize(10);

        //Add multiple rows with random facts about Belgium

        for (String[] fact : content) {

            row = table.createRow(15f);
            cell = row.createCell(50, fact[0]);
            cell.setFont(PDType1Font.HELVETICA);
            cell.setFontSize(10);


            for (int i = 1; i < fact.length; i++) {
                cell = row.createCell(50, fact[i]);
                cell.setFont(PDType1Font.HELVETICA_OBLIQUE);
                cell.setFontSize(10);
            }
        }
        table.draw();
    }

    private void drawInfoLabVertical(PDPageContentStream stream, PDPage page, Laboratorio labProcesa) throws IOException {
        float xCenter;

        float inY = 700;
        float m = 20;

        xCenter = GeneralUtils.centerTextPositionX(page, PDType1Font.HELVETICA_BOLD, 14, messageSource.getMessage("lbl.minsa", null, null));
        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.minsa", null, null), inY, xCenter, stream, 14, PDType1Font.HELVETICA_BOLD);
        inY -= m;

        if (labProcesa != null) {

            if (labProcesa.getDescripcion() != null) {
                xCenter = GeneralUtils.centerTextPositionX(page, PDType1Font.HELVETICA_BOLD, 14, labProcesa.getDescripcion());
                GeneralUtils.drawTEXT(labProcesa.getDescripcion(), inY, xCenter, stream, 14, PDType1Font.HELVETICA_BOLD);
                inY -= m;
            }

            if (labProcesa.getDireccion() != null) {
                xCenter = GeneralUtils.centerTextPositionX(page, PDType1Font.HELVETICA_BOLD, 14, labProcesa.getDescripcion());
                GeneralUtils.drawTEXT(labProcesa.getDireccion(), inY, xCenter, stream, 14, PDType1Font.HELVETICA_BOLD);
                inY -= m;
            }

            if (labProcesa.getTelefono() != null) {

                if (labProcesa.getTelefax() != null) {
                    xCenter = GeneralUtils.centerTextPositionX(page, PDType1Font.HELVETICA_BOLD, 14, labProcesa.getTelefono() + " " + labProcesa.getTelefax());
                    GeneralUtils.drawTEXT(labProcesa.getTelefono() + " " + labProcesa.getTelefax(), inY, xCenter, stream, 14, PDType1Font.HELVETICA_BOLD);
                } else {
                    xCenter = GeneralUtils.centerTextPositionX(page, PDType1Font.HELVETICA_BOLD, 14, labProcesa.getTelefono());
                    GeneralUtils.drawTEXT(labProcesa.getTelefono(), inY, xCenter, stream, 14, PDType1Font.HELVETICA_BOLD);
                }
            }
        }
    }

    private void drawFinalInfo(PDPageContentStream stream, float y, String fechaAprobacion, String fechaImpresion) throws IOException {
        //dibujar lineas de firmas
        stream.drawLine(90, y, 250, y);
        stream.drawLine(340, y, 500, y);

        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.analyst", null, null), y - 10, 145, stream, 10, PDType1Font.HELVETICA_BOLD);
        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.director", null, null), y - 10, 400, stream, 10, PDType1Font.HELVETICA_BOLD);

        //info reporte
        if (fechaAprobacion != null) {
            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.approval.datetime", null, null), 115, 15, stream, 10, PDType1Font.HELVETICA_BOLD);
            GeneralUtils.drawTEXT(fechaAprobacion, 115, 120, stream, 10, PDType1Font.HELVETICA);

        }

        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.print.datetime", null, null), 115, 360, stream, 10, PDType1Font.HELVETICA_BOLD);
        GeneralUtils.drawTEXT(fechaImpresion, 115, 450, stream, 10, PDType1Font.HELVETICA);

    }

}
