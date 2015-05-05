package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.estructura.EntidadesAdtvas;
import ni.gob.minsa.laboratorio.domain.muestra.*;
import ni.gob.minsa.laboratorio.service.*;
import ni.gob.minsa.laboratorio.utilities.ConstantsSecurity;
import ni.gob.minsa.laboratorio.utilities.DateUtil;
import ni.gob.minsa.laboratorio.utilities.pdfUtils.*;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        String codSilais = null;
        String codUnidadSalud = null;
        String codTipoMx = null;
        String codTipoSolicitud = null;
        String nombreSolicitud = null;


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

        filtroMx.setCodSilais(codSilais);
        filtroMx.setCodUnidadSalud(codUnidadSalud);
        filtroMx.setFechaInicioRecep(fechaInicioRecepcion);
        filtroMx.setFechaFinRecep(fechaFinRecepcion);
        filtroMx.setCodTipoMx(codTipoMx);
        filtroMx.setCodTipoSolicitud(codTipoSolicitud);
        filtroMx.setNombreSolicitud(nombreSolicitud);
        filtroMx.setNombreUsuario(seguridadService.obtenerNombreUsuario());

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
        for (RecepcionMx receivedMx : receivedList) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("codigoUnicoMx", receivedMx.getTomaMx().getCodigoUnicoMx());
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
            List<DaSolicitudDx> solicitudDxList = tomaMxService.getSolicitudesDxByIdToma(receivedMx.getTomaMx().getIdTomaMx());
            List<DaSolicitudEstudio> solicitudEList = tomaMxService.getSolicitudesEstudioByIdTomaMx(receivedMx.getTomaMx().getIdTomaMx());


            Map<Integer, Object> mapDxList = new HashMap<Integer, Object>();
            Map<String, String> mapDx = new HashMap<String, String>();
            int subIndice = 0;

            if (!solicitudDxList.isEmpty()) {
                for (DaSolicitudDx solicitudDx : solicitudDxList) {
                    mapDx.put("idSolicitud", solicitudDx.getIdSolicitudDx());
                    mapDx.put("nombre", solicitudDx.getCodDx().getNombre());
                    mapDx.put("fechaSolicitud", DateUtil.DateToString(solicitudDx.getFechaHSolicitud(), "dd/MM/yyyy hh:mm:ss a"));

                    subIndice++;
                    mapDxList.put(subIndice, mapDx);
                    mapDx = new HashMap<String, String>();
                }
            } else {
                for (DaSolicitudEstudio solicitudEstudio : solicitudEList) {
                    mapDx.put("idSolicitud", solicitudEstudio.getIdSolicitudEstudio());
                    mapDx.put("nombre", solicitudEstudio.getTipoEstudio().getNombre());
                    mapDx.put("fechaSolicitud", DateUtil.DateToString(solicitudEstudio.getFechaHSolicitud(), "dd/MM/yyyy hh:mm:ss a"));

                    subIndice++;
                    mapDxList.put(subIndice, mapDx);
                    mapDx = new HashMap<String, String>();
                }
            }


            map.put("solicitudes", new Gson().toJson(mapDxList));

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
            drawInfoLab(stream, page, labProcesa);

            float y = 400;
            float m = 20;

            //nombre del reporte
            xCenter = centerTextPositionX(page, PDType1Font.HELVETICA_BOLD, 12, messageSource.getMessage("lbl.reception.report", null, null));
            GeneralUtils.drawTEXT( messageSource.getMessage("lbl.reception.report", null, null), y, xCenter, stream, 12, PDType1Font.HELVETICA_BOLD);
            y = y-10;
            //Rango de Fechas
            if(!fromDate.equals("") && !toDate.equals("")){
                GeneralUtils.drawTEXT( messageSource.getMessage("lbl.from", null, null), y, 55, stream, 12, PDType1Font.HELVETICA_BOLD);
                GeneralUtils.drawTEXT(fromDate, y, 100, stream, 12, PDType1Font.HELVETICA_BOLD);

                GeneralUtils.drawTEXT( messageSource.getMessage("lbl.to", null, null), y, 660, stream, 12, PDType1Font.HELVETICA_BOLD);
                GeneralUtils.drawTEXT( toDate, y, 720, stream, 12, PDType1Font.HELVETICA_BOLD);
                y -= m;
            }


            String[] codigosArray = codes.replaceAll("\\*", "-").split(",");
            String[][] content = new String[codigosArray.length][8];

            int numFila = 0;
            for (String codigoUnico : codigosArray) {

                RecepcionMx recepcion = recepcionMxService.getRecepcionMxByCodUnicoMx(codigoUnico);

                if (recepcion != null) {
                    String nombreSolitud = null;
                    String nombrePersona = null;

                    DaSolicitudDx soliDx = tomaMxService.getSoliDxByCodigo(recepcion.getTomaMx().getCodigoUnicoMx());
                    DaSolicitudEstudio soliE = tomaMxService.getSoliEstByCodigo(recepcion.getTomaMx().getCodigoUnicoMx());

                    if (soliDx != null || soliE != null) {
                        if (soliDx != null) {
                            nombreSolitud = soliDx.getCodDx().getNombre();
                        } else {
                            nombreSolitud = soliE.getTipoEstudio().getNombre();
                        }
                    }

                    nombrePersona = recepcion.getTomaMx().getIdNotificacion().getPersona().getPrimerNombre();
                    if (recepcion.getTomaMx().getIdNotificacion().getPersona().getSegundoNombre() != null)
                        nombrePersona = nombrePersona + " " + recepcion.getTomaMx().getIdNotificacion().getPersona().getSegundoNombre();
                    nombrePersona = nombrePersona + " " + recepcion.getTomaMx().getIdNotificacion().getPersona().getPrimerApellido();
                    if (recepcion.getTomaMx().getIdNotificacion().getPersona().getSegundoApellido() != null)
                        nombrePersona = nombrePersona + " " + recepcion.getTomaMx().getIdNotificacion().getPersona().getSegundoApellido();

                    content[numFila][0] = recepcion.getTomaMx().getCodigoUnicoMx() != null ? recepcion.getTomaMx().getCodigoUnicoMx() : "";
                    content[numFila][1] = recepcion.getTomaMx().getCodTipoMx() != null ? recepcion.getTomaMx().getCodTipoMx().getNombre() : "";
                    content[numFila][2] = recepcion.getFechaHoraRecepcion() != null ? DateUtil.DateToString(recepcion.getFechaHoraRecepcion(), "dd/MM/yyyy hh:mm:ss a") : "";
                    content[numFila][3] = recepcion.getCalidadMx() != null ? recepcion.getCalidadMx().getValor() : "";
                    content[numFila][4] = recepcion.getTomaMx().getIdNotificacion().getCodSilaisAtencion() != null ? recepcion.getTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre() : "";
                    content[numFila][5] = recepcion.getTomaMx().getIdNotificacion().getCodUnidadAtencion() != null ? recepcion.getTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre() : "";
                    content[numFila][6] = recepcion.getTomaMx().getIdNotificacion().getPersona() != null ? nombrePersona : "";
                    content[numFila][7] = nombreSolitud != null ? nombreSolitud : "";

                    numFila++;
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
            Row row ;


            //Create Fact header row
            Row factHeaderrow = table.createRow(15f);
            cell = factHeaderrow.createCell(10, messageSource.getMessage("lbl.unique.code.mx.short", null, null));
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
            y -=15;

            //Add multiple rows with random facts about Belgium
            for (String[] fact : content) {

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


                    table = new BaseTable(y, y, bottomMargin, tableWidth, margin, doc, page, true, true);

                    //Create Header row
                    headerRow = table.createRow(15f);
                    table.setHeader(headerRow);

                    //Create Fact header row
                    factHeaderrow = table.createRow(15f);
                    cell = factHeaderrow.createCell(10, messageSource.getMessage("lbl.unique.code.mx.short", null, null));
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
                y -=15;

                for (int i = 1; i < fact.length; i++) {
                        if(i == 1){
                        cell = row.createCell(10, fact[i]);
                        cell.setFont(PDType1Font.HELVETICA);
                        cell.setFontSize(10);
                    }else if(i == 2) {
                        cell = row.createCell(9, fact[i]);
                        cell.setFont(PDType1Font.HELVETICA);
                        cell.setFontSize(10);
                    }else if(i== 5) {
                        cell = row.createCell(17, fact[i]);
                        cell.setFont(PDType1Font.HELVETICA);
                        cell.setFontSize(10);

                    }else if(i== 7) {
                        cell = row.createCell(9, fact[i]);
                        cell.setFont(PDType1Font.HELVETICA);
                        cell.setFontSize(10);

                    }else if(i == 4){
                        cell = row.createCell(17, fact[i]);
                        cell.setFont(PDType1Font.HELVETICA);
                        cell.setFontSize(10);

                    } else if(i==6){
                       cell = row.createCell(16, fact[i] );
                       cell.setFont(PDType1Font.HELVETICA);
                       cell.setFontSize(10);


                    }else {
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

        if(labProcesa != null){

            if(labProcesa.getDescripcion()!= null){
                xCenter = centerTextPositionX(page, PDType1Font.HELVETICA_BOLD, 14, labProcesa.getDescripcion());
                GeneralUtils.drawTEXT(labProcesa.getDescripcion(), inY, xCenter, stream, 14, PDType1Font.HELVETICA_BOLD);
                inY -= m;
            }

            if(labProcesa.getDireccion() != null){
                xCenter = centerTextPositionX(page, PDType1Font.HELVETICA_BOLD, 14, labProcesa.getDescripcion());
                GeneralUtils.drawTEXT(labProcesa.getDireccion(), inY, xCenter, stream, 14, PDType1Font.HELVETICA_BOLD);
                inY -= m;
            }

            if(labProcesa.getTelefono() != null){

                if(labProcesa.getTelefax() != null){
                    xCenter = centerTextPositionX(page, PDType1Font.HELVETICA_BOLD, 14, labProcesa.getTelefono() + " " + labProcesa.getTelefax());
                    GeneralUtils.drawTEXT(labProcesa.getTelefono() + " " + labProcesa.getTelefax(), inY, xCenter, stream, 14, PDType1Font.HELVETICA_BOLD);
                }else{
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

}
