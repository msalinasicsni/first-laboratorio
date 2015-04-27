package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.estructura.EntidadesAdtvas;
import ni.gob.minsa.laboratorio.domain.examen.Area;
import ni.gob.minsa.laboratorio.domain.muestra.*;
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
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDPixelMap;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
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

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Created by FIRSTICT on 4/21/2015.
 * V1.0
 */
@Controller
@RequestMapping("workSheet")
public class WorkSheetController {

    private static final Logger logger = LoggerFactory.getLogger(SendMxReceiptController.class);
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
    @Qualifier(value = "areaService")
    private AreaService areaService;

    @Autowired
    @Qualifier(value = "hojaTrabajoService")
    private HojaTrabajoService hojaTrabajoService;

    @Autowired
    MessageSource messageSource;

    @RequestMapping(value = "init", method = RequestMethod.GET)
    public ModelAndView initSearchForm(HttpServletRequest request) throws Exception {
        logger.debug("buscar ordenes para recepcion");

        ModelAndView mav = new ModelAndView();
        List<EntidadesAdtvas> entidadesAdtvases =  entidadAdmonService.getAllEntidadesAdtvas();
        List<TipoMx> tipoMxList = catalogosService.getTipoMuestra();
        List<Area> areaList = areaService.getAreas();
        mav.addObject("entidades",entidadesAdtvases);
        mav.addObject("tipoMuestra", tipoMxList);
        mav.addObject("area",areaList);
        mav.setViewName("recepcionMx/searchWorkSheet");

        return mav;
    }

    @RequestMapping(value = "printWorkSheets", method = RequestMethod.GET)
    public @ResponseBody
    String getPDFHoja(@RequestParam(value = "hojas", required = true) String hojas) throws IOException, COSVisitorException, ParseException {
        String res = null;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PDDocument doc = new PDDocument();


        List<DaSolicitudDx> solicitudDxList;
        List<DaSolicitudEstudio> solicitudEstudioList;

        String workingDir = System.getProperty("user.dir");
        String fechaImpresion = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").format(new Date());
        List<DaTomaMx> tomasHoja = new ArrayList<DaTomaMx>();
        Laboratorio labProcesa = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());

        String[] hojasArray = hojas.split(",");

        for (String numHoja : hojasArray) {
            List<String[]> filasSolicitudes = new ArrayList<String[]>();
            float inY = 750;
            float m = 20;
            float m1 = 50;
            String textoImprimir="";
            //Prepare the document.
            PDPage page = GeneralUtils.addNewPage(doc);
            PDPageContentStream stream = new PDPageContentStream(doc, page);
            HojaTrabajo hojaTrabajo = hojaTrabajoService.getHojaTrabajo(Integer.valueOf(numHoja));
            tomasHoja = hojaTrabajoService.getTomaMxByHojaTrabajo(Integer.valueOf(numHoja));
            //dibujar encabezado pag y pie de pagina
            BufferedImage awtImage = ImageIO.read(new File(workingDir + "/encabezadoMinsa.jpg"));
            PDXObjectImage ximage = new PDPixelMap(doc, awtImage);
            stream.drawXObject(ximage, 5, inY, 590, 80);
            inY -= m1;

            BufferedImage awtImage2 = ImageIO.read(new File(workingDir + "/piePMinsa.jpg"));
            PDXObjectImage ximage2 = new PDPixelMap(doc, awtImage2);
            stream.drawXObject(ximage2, 5, 30, 600, 70);

            textoImprimir = messageSource.getMessage("lbl.minsa", null, null);
            stream.beginText();
            stream.setFont(PDType1Font.HELVETICA_BOLD, 14f);
            stream.moveTextPositionByAmount(GeneralUtils.centerTextPositionX(page, PDType1Font.HELVETICA_BOLD, 14f,textoImprimir), inY);
            inY -= m;
            stream.drawString(textoImprimir);
            stream.endText();

            textoImprimir = labProcesa.getDescripcion()!=null?labProcesa.getDescripcion():"";
            stream.beginText();
            stream.setFont(PDType1Font.HELVETICA_BOLD, 14f);
            stream.moveTextPositionByAmount(GeneralUtils.centerTextPositionX(page, PDType1Font.HELVETICA_BOLD, 14f,textoImprimir), inY);
            inY -= m;
            stream.drawString(textoImprimir);
            stream.endText();

            textoImprimir = messageSource.getMessage("lbl.work.sheet", null, null);
            stream.beginText();
            stream.setFont(PDType1Font.HELVETICA_BOLD, 14f);
            stream.moveTextPositionByAmount(GeneralUtils.centerTextPositionX(page, PDType1Font.HELVETICA_BOLD, 14f,textoImprimir), inY);
            inY -= m;
            stream.drawString(textoImprimir);
            stream.endText();

            textoImprimir = messageSource.getMessage("lbl.address", null, null)+ " " + (labProcesa.getDireccion()!=null?labProcesa.getDireccion():"");
            stream.beginText();
            stream.setFont(PDType1Font.HELVETICA_BOLD, 14f);
            stream.moveTextPositionByAmount(GeneralUtils.centerTextPositionX(page, PDType1Font.HELVETICA_BOLD, 14f,textoImprimir), inY);
            inY -= m;
            stream.drawString(textoImprimir);
            stream.endText();

            textoImprimir = messageSource.getMessage("lbl.telephone", null, null)+ " " +
                    (labProcesa.getTelefono()!=null?labProcesa.getTelefono():"--")+
                    " - " +
                    messageSource.getMessage("lbl.fax", null, null)+ " " +
                    (labProcesa.getTelefax()!=null?labProcesa.getTelefax():"--");
            stream.beginText();
            stream.setFont(PDType1Font.HELVETICA_BOLD, 14f);
            stream.moveTextPositionByAmount(GeneralUtils.centerTextPositionX(page, PDType1Font.HELVETICA_BOLD, 14f,textoImprimir), inY);
            inY -= m1;
            stream.drawString(textoImprimir);
            stream.endText();
            //Dibujar encabezado de resultado

            stream.beginText();
            stream.setFont(PDType1Font.HELVETICA_BOLD, 12f);
            stream.moveTextPositionByAmount(30, inY);

            stream.drawString(messageSource.getMessage("lbl.sheet.number", null, null) + ": ");
            stream.setFont(PDType1Font.HELVETICA, 10f);
            stream.drawString(String.valueOf(hojaTrabajo.getNumero()));
            stream.endText();

            stream.beginText();
            stream.setFont(PDType1Font.HELVETICA_BOLD, 12f);
            stream.moveTextPositionByAmount(290, inY);

            stream.drawString(messageSource.getMessage("lbl.sheet.date", null, null) + ": ");
            stream.setFont(PDType1Font.HELVETICA, 10f);
            stream.drawString(DateUtil.DateToString(hojaTrabajo.getFechaRegistro(), "dd/MM/yyyy hh:mm:ss a"));
            stream.endText();
            for (DaTomaMx tomaMx_hoja : tomasHoja) {
                float y = 540;

                //cod_mx, solicitud,lab_destino,techa toma mx, fec_inicio_sintomas
                solicitudDxList = tomaMxService.getSolicitudesDxByIdToma(tomaMx_hoja.getIdTomaMx());
                solicitudEstudioList = tomaMxService.getSolicitudesEstudioByIdTomaMx(tomaMx_hoja.getIdTomaMx());
                //int numFila = 0;
                String[] content = null;
                String fis = "";
                for (DaSolicitudDx solicitudDx : solicitudDxList) {
                    content = new String[5];
                    y = y - 20;
                    if (solicitudDx.getIdTomaMx().getIdNotificacion().getFechaInicioSintomas()!=null) {
                        fis = DateUtil.DateToString(solicitudDx.getIdTomaMx().getIdNotificacion().getFechaInicioSintomas(), "dd/MM/yyyy");
                    }
                    content[0] = solicitudDx.getIdTomaMx().getCodigoUnicoMx();
                    content[1] = solicitudDx.getCodDx().getNombre();
                    content[2] = solicitudDx.getCodDx().getArea().getNombre();
                    content[3] = DateUtil.DateToString(solicitudDx.getIdTomaMx().getFechaHTomaMx(), "dd/MM/yyyy hh:mm:ss a");
                    content[4] = fis;
                    filasSolicitudes.add(content);
                    //numFila++;

                }
                for (DaSolicitudEstudio solicitudEstudio : solicitudEstudioList) {
                    content = new String[5];
                    y = y - 20;
                    if (solicitudEstudio.getIdTomaMx().getIdNotificacion().getFechaInicioSintomas()!=null) {
                        fis = DateUtil.DateToString(solicitudEstudio.getIdTomaMx().getIdNotificacion().getFechaInicioSintomas(), "dd/MM/yyyy");
                    }
                    content[0] = solicitudEstudio.getIdTomaMx().getCodigoUnicoMx();
                    content[1] = solicitudEstudio.getTipoEstudio().getNombre();
                    content[2] = solicitudEstudio.getTipoEstudio().getArea().getNombre();
                    content[3] = DateUtil.DateToString(solicitudEstudio.getIdTomaMx().getFechaHTomaMx(),"dd/MM/yyyy hh:mm:ss a");
                    content[4] = fis;
                    filasSolicitudes.add(content);
                    //numFila++;

                }
                //Initialize table
                float margin = 30;
                float tableWidth = 530;
                float yStartNewPage = 520;
                float yStart = yStartNewPage;
                float bottomMargin = 45;
                BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true, true);


                //Create Header row
                Row headerRow = table.createRow(15f);
                Cell cell = headerRow.createCell(100, "");
                cell.setFont(PDType1Font.HELVETICA_BOLD);
                cell.setFillColor(Color.black);
                cell.setTextColor(Color.WHITE);

                table.setHeader(headerRow);



                //Create Fact header row
                Row factHeaderrow = table.createRow(15f);
                cell = factHeaderrow.createCell(17, messageSource.getMessage("lbl.unique.code.mx.short", null, null));
                cell.setFont(PDType1Font.HELVETICA_BOLD);
                cell.setFontSize(10);
                cell.setFillColor(Color.LIGHT_GRAY);

                cell = factHeaderrow.createCell((20), messageSource.getMessage("lbl.request.large", null, null));
                cell.setFillColor(Color.lightGray);
                cell.setFont(PDType1Font.HELVETICA_BOLD);
                cell.setFontSize(10);

                cell = factHeaderrow.createCell((20), messageSource.getMessage("lbl.solic.area.prc", null, null));
                cell.setFillColor(Color.lightGray);
                cell.setFont(PDType1Font.HELVETICA_BOLD);
                cell.setFontSize(10);

                cell = factHeaderrow.createCell((23), messageSource.getMessage("lbl.sampling.datetime", null, null));
                cell.setFillColor(Color.lightGray);
                cell.setFont(PDType1Font.HELVETICA_BOLD);
                cell.setFontSize(10);

                cell = factHeaderrow.createCell((20), messageSource.getMessage("lbl.receipt.symptoms.start.date", null, null));
                cell.setFillColor(Color.lightGray);
                cell.setFont(PDType1Font.HELVETICA_BOLD);
                cell.setFontSize(10);
                //Create row
                Row row;
                //Add multiple rows with random facts about Belgium

                for (String[] fact : filasSolicitudes) {

                    row = table.createRow(15f);
                    for (int i = 0; i < fact.length; i++) {
                        if (i==0) {
                            cell = row.createCell(17, fact[i]);
                        }else if (i==3) {
                            cell = row.createCell(23, fact[i]);
                        }else {
                            cell = row.createCell(20, fact[i]);
                        }
                        cell.setFont(PDType1Font.HELVETICA);
                        cell.setFontSize(10);
                    }
                }
                table.draw();

                stream.beginText();
                stream.setFont(PDType1Font.HELVETICA_BOLD, 10f);
                stream.moveTextPositionByAmount(360, 115);
                stream.drawString(messageSource.getMessage("lbl.print.datetime", null, null) + " ");
                stream.setFont(PDType1Font.HELVETICA, 10f);
                stream.drawString(fechaImpresion);
                stream.endText();



            }
            stream.close();
        }

        doc.save(output);
        doc.close();
        // generate the file
        res = Base64.encodeBase64String(output.toByteArray());

        return res;
    }


    /**
     * Método para realizar la búsqueda de Mx para recepcionar en Mx Vigilancia general
     * @param filtro JSon con los datos de los filtros a aplicar en la búsqueda(Nombre Apellido, Rango Fec Toma Mx, Tipo Mx, SILAIS, unidad salud)
     * @return String con las Mx encontradas
     * @throws Exception
     */
    @RequestMapping(value = "search", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String fetchOrdersJson(@RequestParam(value = "strFilter", required = true) String filtro) throws Exception{
        logger.info("Obteniendo las mx según filtros en JSON");
        JsonObject jObjectFiltro = new Gson().fromJson(filtro, JsonObject.class);
        Integer hoja = null;
        Date fechaInicioHoja = null;
        Date fechaFinHoja = null;
        if (jObjectFiltro.get("hoja") != null && !jObjectFiltro.get("hoja").getAsString().isEmpty())
            hoja = jObjectFiltro.get("hoja").getAsInt();
        if (jObjectFiltro.get("fechaInicioHoja") != null && !jObjectFiltro.get("fechaInicioHoja").getAsString().isEmpty())
            fechaInicioHoja = DateUtil.StringToDate(jObjectFiltro.get("fechaInicioHoja").getAsString() + " 00:00:00");
        if (jObjectFiltro.get("fechaFinHoja") != null && !jObjectFiltro.get("fechaFinHoja").getAsString().isEmpty())
            fechaFinHoja = DateUtil.StringToDate(jObjectFiltro.get("fechaFinHoja").getAsString()+" 23:59:59");
        List<HojaTrabajo> hojaTrabajoList = hojaTrabajoService.getTomaMxByFiltro(hoja,fechaInicioHoja,fechaFinHoja,seguridadService.obtenerNombreUsuario());
        return hojasTrabajoToJson(hojaTrabajoList);
    }

    /**
     * Método que convierte una lista de tomaMx a un string con estructura Json
     * @param hojaTrabajoList lista con las hojas de trabajo a convertir
     * @return String
     */
    private String hojasTrabajoToJson(List<HojaTrabajo> hojaTrabajoList){
        String jsonResponse;
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice=0;
        List<DaTomaMx> tomaMxList = null;
        for(HojaTrabajo hojaTrabajo:hojaTrabajoList){
            Map<String, String> map = new HashMap<String, String>();
            map.put("numero", String.valueOf(hojaTrabajo.getNumero()));
            map.put("fecha",DateUtil.DateToString(hojaTrabajo.getFechaRegistro(), "dd/MM/yyyy hh:mm:ss a"));
            tomaMxList = hojaTrabajoService.getTomaMxByHojaTrabajo(hojaTrabajo.getNumero());
            map.put("cantidad",String.valueOf(tomaMxList.size()));
            Map<Integer, Object> mapMxList = new HashMap<Integer, Object>();
            Map<String, String> mapMx = new HashMap<String, String>();
            int subIndice=0;
            for (DaTomaMx tomaMx : tomaMxList){
                mapMx.put("codigoUnicoMx", tomaMx.getCodigoUnicoMx());
                mapMx.put("fechaTomaMx",DateUtil.DateToString(tomaMx.getFechaHTomaMx(), "dd/MM/yyyy hh:mm:ss a"));
                if (tomaMx.getIdNotificacion().getCodSilaisAtencion()!=null) {
                    mapMx.put("codSilais", tomaMx.getIdNotificacion().getCodSilaisAtencion().getNombre());
                }else {
                    mapMx.put("codSilais","");
                }
                if (tomaMx.getIdNotificacion().getCodUnidadAtencion()!=null) {
                    mapMx.put("codUnidadSalud", tomaMx.getIdNotificacion().getCodUnidadAtencion().getNombre());
                }else{
                    mapMx.put("codUnidadSalud","");
                }
                mapMx.put("tipoMuestra", tomaMx.getCodTipoMx().getNombre());
                //Si hay persona
                if (tomaMx.getIdNotificacion().getPersona()!=null){
                    /// se obtiene el nombre de la persona asociada a la ficha
                    String nombreCompleto = "";
                    nombreCompleto = tomaMx.getIdNotificacion().getPersona().getPrimerNombre();
                    if (tomaMx.getIdNotificacion().getPersona().getSegundoNombre()!=null)
                        nombreCompleto = nombreCompleto +" "+ tomaMx.getIdNotificacion().getPersona().getSegundoNombre();
                    nombreCompleto = nombreCompleto+" "+ tomaMx.getIdNotificacion().getPersona().getPrimerApellido();
                    if (tomaMx.getIdNotificacion().getPersona().getSegundoApellido()!=null)
                        nombreCompleto = nombreCompleto +" "+ tomaMx.getIdNotificacion().getPersona().getSegundoApellido();
                    mapMx.put("persona",nombreCompleto);
                }else{
                    mapMx.put("persona"," ");
                }
                subIndice++;
                mapMxList.put(subIndice,mapMx);
                mapMx = new HashMap<String, String>();
            }
            map.put("muestras", new Gson().toJson(mapMxList));
            mapResponse.put(indice, map);
            indice ++;
        }
        jsonResponse = new Gson().toJson(mapResponse);
        //escapar caracteres especiales, escape de los caracteres con valor numérico mayor a 127
        UnicodeEscaper escaper     = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }

}
