package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.estructura.EntidadesAdtvas;
import ni.gob.minsa.laboratorio.domain.muestra.*;
import ni.gob.minsa.laboratorio.domain.resultados.Catalogo_Lista;
import ni.gob.minsa.laboratorio.domain.resultados.DetalleResultadoFinal;
import ni.gob.minsa.laboratorio.service.*;
import ni.gob.minsa.laboratorio.utilities.ConstantsSecurity;
import ni.gob.minsa.laboratorio.utilities.DateUtil;
import ni.gob.minsa.laboratorio.utilities.pdfUtils.BaseTable;
import ni.gob.minsa.laboratorio.utilities.pdfUtils.Cell;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by souyen-ics.
 */
@Controller
@RequestMapping("searchMx")
public class SearchMxController {

    private static final Logger logger = LoggerFactory.getLogger(SearchMxController.class);

    @Autowired
    @Qualifier(value = "seguridadService")
    private SeguridadService seguridadService;

    @Autowired
    @Qualifier(value = "usuarioService")
    private UsuarioService usuarioService;

    @Autowired
    @Qualifier(value = "catalogosService")
    private CatalogoService catalogosService;

    @Autowired
    @Qualifier(value = "entidadAdmonService")
    private EntidadAdmonService entidadAdmonService;

    @Autowired
    @Qualifier(value = "laboratoriosService")
    private LaboratoriosService laboratoriosService;

    @Autowired
    @Qualifier(value = "recepcionMxService")
    private RecepcionMxService recepcionMxService;

    @Autowired
    @Qualifier(value = "tomaMxService")
    private TomaMxService tomaMxService;

    @Autowired
    @Qualifier(value = "unidadesService")
    private UnidadesService unidadesService;

    @Autowired
    @Qualifier(value = "parametrosService")
    private ParametrosService parametrosService;

    @Autowired
    @Qualifier(value = "examenesService")
    private ExamenesService examenesService;

    @Autowired
    @Qualifier(value = "ordenExamenMxService")
    private OrdenExamenMxService ordenExamenMxService;

    @Autowired
    @Qualifier(value = "resultadoFinalService")
    private ResultadoFinalService resultadoFinalService;

    @Autowired
    @Qualifier(value = "rechazoResultadoSolicitudService")
    private RechazoResultadoSolicitudService rechazoResultadoSolicitudService;

    @Autowired
    @Qualifier(value = "conceptoService")
    private ConceptoService conceptoService;

    @Autowired
    MessageSource messageSource;


    /**
     * Método que se llama al entrar a la opción de menu "Recepción Mx Vigilancia". Se encarga de inicializar las listas para realizar la búsqueda de envios de Mx
     * @param request para obtener información de la petición del cliente
     * @return ModelAndView
     * @throws Exception
     */
    @RequestMapping(value = "init", method = RequestMethod.GET)
    public ModelAndView initSearchForm(HttpServletRequest request) throws Exception {
        logger.debug("Buscar Mx");
        String urlValidacion;
        try {
            urlValidacion = seguridadService.validarLogin(request);
            //si la url esta vacia significa que la validación del login fue exitosa
            if (urlValidacion.isEmpty())
                urlValidacion = seguridadService.validarAutorizacionUsuario(request, ConstantsSecurity.SYSTEM_CODE, false);
        }catch (Exception e){
            e.printStackTrace();
            urlValidacion = "404";
        }
        ModelAndView mav = new ModelAndView();
        if (urlValidacion.isEmpty()) {
            List<EntidadesAdtvas> entidadesAdtvases =  entidadAdmonService.getAllEntidadesAdtvas();
            List<TipoMx> tipoMxList = catalogosService.getTipoMuestra();
            mav.addObject("entidades",entidadesAdtvases);
            mav.addObject("tipoMuestra", tipoMxList);
            mav.setViewName("recepcionMx/searchMx");
        }else
            mav.setViewName(urlValidacion);

        return mav;
    }

    /**
     * Método para realizar la búsqueda de Mx para recepcionar en Mx Vigilancia general
     * @param filtro JSon con los datos de los filtros a aplicar en la búsqueda(Nombre Apellido, Rango Fec Toma Mx, Tipo Mx, SILAIS, unidad salud)
     * @return String con las Mx encontradas
     * @throws Exception
     */
    @RequestMapping(value = "searchMx", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String fetchOrdersJson(@RequestParam(value = "strFilter", required = true) String filtro) throws Exception{
        logger.info("Obteniendo las mx según filtros en JSON");
        FiltroMx filtroMx = jsonToFiltroMx(filtro);
        List<DaTomaMx> tomaMxList = tomaMxService.getTomaMxByFiltro(filtroMx);
        return tomaMxToJson(tomaMxList);
    }

    /**
     * Método para convertir estructura Json que se recibe desde el cliente a FiltroMx para realizar búsqueda de Mx(Vigilancia) y Recepción Mx(Laboratorio)
     * @param strJson String con la información de los filtros
     * @return FiltroMx
     * @throws Exception
     */
    private FiltroMx jsonToFiltroMx(String strJson) throws Exception {
        JsonObject jObjectFiltro = new Gson().fromJson(strJson, JsonObject.class);
        FiltroMx filtroMx = new FiltroMx();
        String nombreApellido = null;
        Date fechaInicioTomaMx = null;
        Date fechaFinTomaMx = null;
        Date fechaInicioRecep = null;
        Date fechaFinRecep = null;
        String codSilais = null;
        String codUnidadSalud = null;
        String codTipoMx = null;
        String codigoUnicoMx = null;
        String codTipoSolicitud = null;
        String nombreSolicitud = null;
        String aprobado = null;

        if (jObjectFiltro.get("nombreApellido") != null && !jObjectFiltro.get("nombreApellido").getAsString().isEmpty())
            nombreApellido = jObjectFiltro.get("nombreApellido").getAsString();
        if (jObjectFiltro.get("fechaInicioTomaMx") != null && !jObjectFiltro.get("fechaInicioTomaMx").getAsString().isEmpty())
            fechaInicioTomaMx = DateUtil.StringToDate(jObjectFiltro.get("fechaInicioTomaMx").getAsString() + " 00:00:00");
        if (jObjectFiltro.get("fechaFinTomaMx") != null && !jObjectFiltro.get("fechaFinTomaMx").getAsString().isEmpty())
            fechaFinTomaMx = DateUtil.StringToDate(jObjectFiltro.get("fechaFinTomaMx").getAsString()+" 23:59:59");
        if (jObjectFiltro.get("fechaInicioRecep") != null && !jObjectFiltro.get("fechaInicioRecep").getAsString().isEmpty())
            fechaInicioRecep = DateUtil.StringToDate(jObjectFiltro.get("fechaInicioRecep").getAsString()+" 00:00:00");
        if (jObjectFiltro.get("fechaFinRecepcion") != null && !jObjectFiltro.get("fechaFinRecepcion").getAsString().isEmpty())
            fechaFinRecep =DateUtil. StringToDate(jObjectFiltro.get("fechaFinRecepcion").getAsString()+" 23:59:59");
        if (jObjectFiltro.get("codSilais") != null && !jObjectFiltro.get("codSilais").getAsString().isEmpty())
            codSilais = jObjectFiltro.get("codSilais").getAsString();
        if (jObjectFiltro.get("codUnidadSalud") != null && !jObjectFiltro.get("codUnidadSalud").getAsString().isEmpty())
            codUnidadSalud = jObjectFiltro.get("codUnidadSalud").getAsString();
        if (jObjectFiltro.get("codTipoMx") != null && !jObjectFiltro.get("codTipoMx").getAsString().isEmpty())
            codTipoMx = jObjectFiltro.get("codTipoMx").getAsString();
        if (jObjectFiltro.get("codigoUnicoMx") != null && !jObjectFiltro.get("codigoUnicoMx").getAsString().isEmpty())
            codigoUnicoMx = jObjectFiltro.get("codigoUnicoMx").getAsString();
        if (jObjectFiltro.get("codTipoSolicitud") != null && !jObjectFiltro.get("codTipoSolicitud").getAsString().isEmpty())
            codTipoSolicitud = jObjectFiltro.get("codTipoSolicitud").getAsString();
        if (jObjectFiltro.get("nombreSolicitud") != null && !jObjectFiltro.get("nombreSolicitud").getAsString().isEmpty())
            nombreSolicitud = jObjectFiltro.get("nombreSolicitud").getAsString();
        if (jObjectFiltro.get("aprobado") != null && !jObjectFiltro.get("aprobado").getAsString().isEmpty())
            aprobado = jObjectFiltro.get("aprobado").getAsString();

        filtroMx.setCodSilais(codSilais);
        filtroMx.setCodUnidadSalud(codUnidadSalud);
        filtroMx.setFechaInicioTomaMx(fechaInicioTomaMx);
        filtroMx.setFechaFinTomaMx(fechaFinTomaMx);
        filtroMx.setFechaInicioRecep(fechaInicioRecep);
        filtroMx.setFechaFinRecep(fechaFinRecep);
        filtroMx.setNombreApellido(nombreApellido);
        filtroMx.setCodTipoMx(codTipoMx);
        filtroMx.setCodTipoSolicitud(codTipoSolicitud);
        filtroMx.setNombreSolicitud(nombreSolicitud);
        filtroMx.setCodigoUnicoMx(codigoUnicoMx);
        filtroMx.setNombreUsuario(seguridadService.obtenerNombreUsuario());
       // filtroMx.setSolicitudAprobada(aprobado);
        return filtroMx;
    }

    /**
     * Método que convierte una lista de tomaMx a un string con estructura Json
     * @param tomaMxList lista con las tomaMx a convertir
     * @return String
     */
    private String tomaMxToJson(List<DaTomaMx> tomaMxList){
        String jsonResponse;
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice=0;
        for(DaTomaMx tomaMx : tomaMxList){
            Map<String, String> map = new HashMap<String, String>();
            map.put("idTomaMx", tomaMx.getIdTomaMx());
            map.put("codigoUnicoMx", tomaMx.getCodigoUnicoMx());
            map.put("fechaTomaMx",DateUtil.DateToString(tomaMx.getFechaHTomaMx(), "dd/MM/yyyy hh:mm:ss a"));
            map.put("codSilais", tomaMx.getIdNotificacion().getCodSilaisAtencion().getNombre());
            map.put("codUnidadSalud", tomaMx.getIdNotificacion().getCodUnidadAtencion().getNombre());
            map.put("tipoMuestra", tomaMx.getCodTipoMx().getNombre());
            map.put("estadoMx", tomaMx.getEstadoMx().getValor());

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
                map.put("persona",nombreCompleto);
            }else{
                map.put("persona"," ");
            }

            //se arma estructura de diagnósticos o estudios
            List<DaSolicitudDx> solicitudDxList = tomaMxService.getSolicitudesDxByIdToma(tomaMx.getIdTomaMx());
            List<DaSolicitudEstudio> solicitudEList = tomaMxService.getSolicitudesEstudioByIdTomaMx(tomaMx.getIdTomaMx());


            Map<Integer, Object> mapDxList = new HashMap<Integer, Object>();
            Map<String, String> mapDx = new HashMap<String, String>();
            int subIndice=0;

            if(!solicitudDxList.isEmpty()){
                for(DaSolicitudDx solicitudDx: solicitudDxList){
                    mapDx.put("idSolicitud", solicitudDx.getIdSolicitudDx());
                    mapDx.put("nombre",solicitudDx.getCodDx().getNombre());
                    mapDx.put("fechaSolicitud", DateUtil.DateToString(solicitudDx.getFechaHSolicitud(), "dd/MM/yyyy hh:mm:ss a"));
                    List<DetalleResultadoFinal> detRes = resultadoFinalService.getDetResActivosBySolicitud(solicitudDx.getIdSolicitudDx());

                    if(solicitudDx.getAprobada() != null){
                        if (solicitudDx.getAprobada().equals(true)){
                            mapDx.put("estado", "Resultado Aprobado");
                        }
                    } else if(!detRes.isEmpty()){
                        mapDx.put("estado", "Resultado en Espera de Aprobación");
                    }else {
                        mapDx.put("estado", "Sin Resultado");
                    }

                    subIndice++;
                    mapDxList.put(subIndice,mapDx);
                    mapDx = new HashMap<String, String>();
                }
            }else{
                for(DaSolicitudEstudio solicitudEstudio: solicitudEList){
                    mapDx.put("idSolicitud", solicitudEstudio.getIdSolicitudEstudio());
                    mapDx.put("nombre",solicitudEstudio.getTipoEstudio().getNombre());
                    mapDx.put("fechaSolicitud", DateUtil.DateToString(solicitudEstudio.getFechaHSolicitud(), "dd/MM/yyyy hh:mm:ss a"));
                    List<DetalleResultadoFinal> detRes = resultadoFinalService.getDetResActivosBySolicitud(solicitudEstudio.getIdSolicitudEstudio());

                   if(solicitudEstudio.getAprobada() != null){
                       if (solicitudEstudio.getAprobada().equals(true)){
                           mapDx.put("estado", "Resultado Aprobado");
                       }
                   } else if(!detRes.isEmpty()){
                       mapDx.put("estado", "Resultado en Espera de Aprobación");
                   }else{
                       mapDx.put("estado", "Sin Resultado");
                   }

                    subIndice++;
                    mapDxList.put(subIndice,mapDx);
                    mapDx = new HashMap<String, String>();
                }
            }


            map.put("solicitudes", new Gson().toJson(mapDxList));

            mapResponse.put(indice, map);
            indice ++;
        }
        jsonResponse = new Gson().toJson(mapResponse);
        //escapar caracteres especiales, escape de los caracteres con valor numérico mayor a 127
        UnicodeEscaper escaper     = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }

    @RequestMapping(value = "printResults", method = RequestMethod.GET)
    public @ResponseBody String getPDF(@RequestParam(value = "codigos", required = true) String codigos, HttpServletRequest request) throws IOException, COSVisitorException, ParseException {
        String res = null;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PDDocument doc = new PDDocument();


        List<DaSolicitudDx> solicDx;
        List<DaSolicitudEstudio> solicEstudio;
        String nombreSoli = null;
        String nombrePersona = null;
        String nombreSilais = null;
        String nombreUS = null;
        int edad = 0;
        String fechaRecepcion = null;
        String fechaToma = null;
        String fis = null;
        String fechaResultado = null;
        String workingDir = System.getProperty("user.dir");
        String fechaImpresion = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());


        String[] toma = codigos.split(",");

        for (String idToma : toma) {
            solicDx = tomaMxService.getSoliDxAprobByIdToma(idToma);
            solicEstudio = tomaMxService.getSoliEstudioAprobByIdTomaMx(idToma);
            List<DetalleResultadoFinal> detalleResultado = null;


            //Obtener las respuestas activas de la solicitud
            if (!solicDx.isEmpty()) {
                for (DaSolicitudDx solicitudDx : solicDx) {
                    RecepcionMx recepcion = recepcionMxService.getRecepcionMxByCodUnicoMx(solicitudDx.getIdTomaMx().getCodigoUnicoMx());
                    String fechaformateada = DateUtil.DateToString(solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getFechaNacimiento(), "dd/MM/yyyy");
                    edad = DateUtil.edad(fechaformateada);
                    fechaToma =  new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").format(solicitudDx.getIdTomaMx().getFechaHTomaMx());
                    fis =  new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").format(tomaMxService.getFechaInicioSintomas(solicitudDx.getIdTomaMx().getIdNotificacion().getIdNotificacion()));
                    fechaRecepcion = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").format(recepcion.getFechaHoraRecepcion());
                    fechaResultado = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").format(resultadoFinalService.getFechaResultadoByIdSoli(solicitudDx.getIdSolicitudDx()));
                    detalleResultado = resultadoFinalService.getDetResActivosBySolicitud(solicitudDx.getIdSolicitudDx());
                    nombreSoli = solicitudDx.getCodDx().getNombre();
                    nombrePersona = solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getPrimerNombre();
                    if(solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre() != null){
                        nombrePersona = nombrePersona + " " + solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre();
                        nombrePersona = nombrePersona + " " + solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getPrimerApellido();
                    }else{
                        nombrePersona = nombrePersona + " " + solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getPrimerApellido();

                    }
                    if(solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido() != null){
                        nombrePersona = nombrePersona + " " + solicitudDx.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido();
                    }

                    if (solicitudDx.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre() != null){
                        nombreSilais = solicitudDx.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre();
                        nombreUS = solicitudDx.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre();

                    }else {
                        nombreSilais = "---------------";
                        nombreUS = "---------------";
                    }


                }
            } else {
                for (DaSolicitudEstudio solicitudE : solicEstudio) {
                    String fechaformateada = DateUtil.DateToString(solicitudE.getIdTomaMx().getIdNotificacion().getPersona().getFechaNacimiento(), "dd/MM/yyyy");
                    RecepcionMx recepcion = recepcionMxService.getRecepcionMxByCodUnicoMx(solicitudE.getIdTomaMx().getCodigoUnicoMx());
                    edad = DateUtil.edad(fechaformateada);
                    fechaToma =  new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").format(solicitudE.getIdTomaMx().getFechaHTomaMx());
                    fis =  new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(tomaMxService.getFechaInicioSintomas(solicitudE.getIdTomaMx().getIdNotificacion().getIdNotificacion()));
                    fechaRecepcion = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").format(recepcion.getFechaHoraRecepcion());
                    detalleResultado = resultadoFinalService.getDetResActivosBySolicitud(solicitudE.getIdSolicitudEstudio());
                    fechaResultado = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").format(resultadoFinalService.getFechaResultadoByIdSoli(solicitudE.getIdSolicitudEstudio()));
                    nombreSoli = solicitudE.getTipoEstudio().getNombre();
                    nombrePersona = solicitudE.getIdTomaMx().getIdNotificacion().getPersona().getPrimerNombre();
                    if(solicitudE.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre() != null){
                        nombrePersona = nombrePersona + " " + solicitudE.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre();
                        nombrePersona = nombrePersona + " " + solicitudE.getIdTomaMx().getIdNotificacion().getPersona().getPrimerApellido();
                    }else{
                        nombrePersona = nombrePersona + " " + solicitudE.getIdTomaMx().getIdNotificacion().getPersona().getPrimerApellido();

                    }
                    if(solicitudE.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido() != null){
                        nombrePersona = nombrePersona + " " + solicitudE.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido();
                    }
                    nombreSilais = solicitudE.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre();
                    nombreUS = solicitudE.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre();


                }
            }

            if (detalleResultado != null) {

                float inY = 750;
                float m = 20;
                float m1 = 50;

                //Prepare the document.
                PDPage page = addNewPage(doc);
                PDPageContentStream stream = new PDPageContentStream(doc, page);

                //dibujar encabezado pag y pie de pagina
                BufferedImage awtImage = ImageIO.read(new File(workingDir + "/encabezadoMinsa.jpg"));
                PDXObjectImage ximage = new PDPixelMap(doc, awtImage);
                stream.drawXObject(ximage, 5, inY, 590, 80);
                inY -= m1;

                BufferedImage awtImage2 = ImageIO.read(new File(workingDir + "/piePMinsa.jpg"));
                PDXObjectImage ximage2 = new PDPixelMap(doc, awtImage2);
                stream.drawXObject(ximage2, 5, 30, 600, 70);

                stream.beginText();
                stream.setFont(PDType1Font.HELVETICA_BOLD, 14f);
                stream.moveTextPositionByAmount(216, inY);
                inY -= m;
                stream.drawString(messageSource.getMessage("lbl.minsa",null,null));
                stream.endText();

                stream.beginText();
                stream.setFont(PDType1Font.HELVETICA_BOLD, 14f);
                stream.moveTextPositionByAmount(110, inY);
                inY -= m;
                stream.drawString(messageSource.getMessage("lbl.cndr",null,null));
                stream.endText();

                stream.beginText();
                stream.setFont(PDType1Font.HELVETICA_BOLD, 14f);
                stream.moveTextPositionByAmount(208, inY);
                inY -= m;
                stream.drawString(messageSource.getMessage("lbl.lab.result",null,null));
                stream.endText();

                stream.beginText();
                stream.setFont(PDType1Font.HELVETICA_BOLD, 14f);
                stream.moveTextPositionByAmount(80, inY);
                inY -= m;
                stream.drawString(messageSource.getMessage("lbl.info.cndr",null,null));
                stream.endText();

                stream.beginText();
                stream.setFont(PDType1Font.HELVETICA_BOLD, 14f);
                stream.moveTextPositionByAmount(187, inY);
                inY -= m1;
                stream.drawString(messageSource.getMessage("lbl.tel.cndr",null,null));
                stream.endText();


                //Dibujar encabezado de resultado

                stream.beginText();
                stream.setFont(PDType1Font.HELVETICA_BOLD, 12f);
                stream.moveTextPositionByAmount(15, inY);
                inY -= m;
                stream.drawString(messageSource.getMessage("lbl.request.name1",null,null) + " ");
                stream.setFont(PDType1Font.HELVETICA, 10f);
                stream.drawString(nombreSoli);
                stream.endText();

                stream.beginText();
                stream.setFont(PDType1Font.HELVETICA_BOLD, 12f);
                stream.moveTextPositionByAmount(15, inY);
                stream.drawString(messageSource.getMessage("lbl.name1",null,null) + " ");
                stream.setFont(PDType1Font.HELVETICA, 10f);
                stream.drawString(nombrePersona);
                stream.endText();

                stream.beginText();
                stream.setFont(PDType1Font.HELVETICA_BOLD, 12f);
                stream.moveTextPositionByAmount(290, inY);
                inY -= m;
                stream.drawString(messageSource.getMessage("lbl.age", null, null) + " ");
                stream.setFont(PDType1Font.HELVETICA, 10f);
                stream.drawString(String.valueOf(edad));
                stream.endText();

                stream.beginText();
                stream.setFont(PDType1Font.HELVETICA_BOLD, 12f);
                stream.moveTextPositionByAmount(15, inY);
                stream.drawString(messageSource.getMessage("lbl.fis", null, null) + " ");
                stream.setFont(PDType1Font.HELVETICA, 10f);
                stream.drawString(fis);
                stream.endText();

                stream.beginText();
                stream.setFont(PDType1Font.HELVETICA_BOLD, 12f);
                stream.moveTextPositionByAmount(290, inY);
                inY -= m;
                stream.drawString(messageSource.getMessage("lbl.sampling.datetime1", null, null) +" ");
                stream.setFont(PDType1Font.HELVETICA, 10f);
                stream.drawString(fechaToma);
                stream.endText();

                stream.beginText();
                stream.setFont(PDType1Font.HELVETICA_BOLD, 12f);
                stream.moveTextPositionByAmount(15, inY);
                stream.drawString(messageSource.getMessage("lbl.reception.datetime",null,null) + " ");
                stream.setFont(PDType1Font.HELVETICA, 10f);
                stream.drawString(fechaRecepcion);
                stream.endText();

                stream.beginText();
                stream.setFont(PDType1Font.HELVETICA_BOLD, 12f);
                stream.moveTextPositionByAmount(290, inY);
                inY -= m;
                stream.drawString(messageSource.getMessage("lbl.result.datetime", null, null) + " ");
                stream.setFont(PDType1Font.HELVETICA, 10f);
                stream.drawString(fechaResultado);
                stream.endText();


                stream.beginText();
                stream.setFont(PDType1Font.HELVETICA_BOLD, 12f);
                stream.moveTextPositionByAmount(15, inY);
                inY -= m;
                stream.drawString(messageSource.getMessage("lbl.silais1",null,null) + " ");
                stream.setFont(PDType1Font.HELVETICA, 10f);
                stream.drawString(nombreSilais);
                stream.endText();

                stream.beginText();
                stream.setFont(PDType1Font.HELVETICA_BOLD, 12f);
                stream.moveTextPositionByAmount(15, inY);
                stream.drawString(messageSource.getMessage("lbl.health.unit1",null,null) + " ");
                stream.setFont(PDType1Font.HELVETICA, 10f);
                stream.drawString(nombreUS);
                stream.endText();

                float y = 540;
                boolean lista = false;
                String valor = null;
                String respuesta;
                String[][] content = new String[detalleResultado.size()][2];

/*
                //draw the header
                stream.drawLine(50,y,50+500,y);
                stream.drawLine(50,y,50,y-20);
                stream.drawLine(300,y,300,y-20);
                stream.drawLine(550,y,550,y-20);

                stream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                stream.beginText();
                stream.moveTextPositionByAmount(55, 555);
                stream.drawString("Respuesta");
                stream.endText();

                stream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                stream.beginText();
                stream.moveTextPositionByAmount(305, 555);
                stream.drawString("Valor");
                stream.endText();
*/
                int numFila = 0;
                for (DetalleResultadoFinal resul : detalleResultado) {
                    y = y - 20;
                  if(resul.getRespuesta() != null){
                      respuesta = resul.getRespuesta().getNombre();
                      lista = resul.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST");
                    }else{
                        respuesta = resul.getRespuestaExamen().getNombre();
                      lista = resul.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST");
                    }

                    if(lista){
                        Catalogo_Lista catLista = conceptoService.getCatalogoListaById(Integer.valueOf(resul.getValor()));
                    valor = catLista.getValor() ;
                    }else{
                        valor = resul.getValor();
                    }


                    content[numFila][0] = respuesta;
                    content[numFila][1] = valor;
                    numFila ++;

                   /* float margin = 50;
                    final int rows = content.length;
                    final int cols = content[0].length;
                    final float rowHeight = 20f;
                    final float tableWidth = 500;
                    final float tableHeight = rowHeight * rows;
                    final float colWidth = tableWidth/(float)cols;
                    final float cellMargin=5f;


                    //draw the rows
                    float nexty = y ;
                    for (int i = 0; i <= rows; i++) {
                        stream.drawLine(margin,nexty,margin+tableWidth,nexty);
                        nexty-= rowHeight;
                    }

                    //draw the columns
                    float nextx = margin;
                    for (int i = 0; i <= cols; i++) {
                        stream.drawLine(nextx,y,nextx,y-tableHeight);
                        nextx += colWidth;
                    }

                    //now add the text
                    float textx = margin+cellMargin;
                    float texty = y-15;
                    for (String[] aContent : content) {
                        for (int j = 0; j < aContent.length; j++) {

                            if (j == 0) {
                                stream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                                String text = aContent[j];
                                stream.beginText();
                                stream.moveTextPositionByAmount(textx, texty);
                                stream.drawString(text);
                                stream.endText();
                            } else {
                                stream.setFont(PDType1Font.HELVETICA, 12);
                                String text = aContent[j];
                                stream.beginText();
                                stream.moveTextPositionByAmount(textx, texty);
                                stream.drawString(text);
                                stream.endText();
                            }

                            textx += colWidth;
                        }
                        texty -= rowHeight;
                        textx = margin + cellMargin;
                    }*/


                }
            //Initialize table
                float margin = 50;
                float tableWidth = 500;
                float yStartNewPage = 410;
                float yStart = yStartNewPage;
                float bottomMargin = 45;
                BaseTable table = new BaseTable(yStart,yStartNewPage,bottomMargin,tableWidth, margin, doc, page, true, true);


                //Create Header row
                Row headerRow = table.createRow(15f);
                Cell cell = headerRow.createCell(100,"");
                cell.setFont(PDType1Font.HELVETICA_BOLD);
                cell.setFillColor(Color.black);cell.setTextColor(Color.WHITE);

                table.setHeader(headerRow);

                //Create 2 column row
                Row row;
               /* cell = row.createCell(75,"Source:");
                cell.setFont(PDType1Font.HELVETICA);

                cell = row.createCell(25,"http://www.factsofbelgium.com/");
                cell.setFont(PDType1Font.HELVETICA_OBLIQUE);*/

                //Create Fact header row
                Row factHeaderrow = table.createRow(15f);
                cell = factHeaderrow.createCell(50 , messageSource.getMessage("lbl.approve.response",null,null));
                cell.setFont(PDType1Font.HELVETICA_BOLD);
                cell.setFontSize(10);
                cell.setFillColor(Color.LIGHT_GRAY);

                cell = factHeaderrow.createCell((50),messageSource.getMessage("lbl.value",null,null));
                cell.setFillColor(Color.lightGray);
                cell.setFont(PDType1Font.HELVETICA_BOLD);cell.setFontSize(10);

                //Add multiple rows with random facts about Belgium

                for(String[] fact : content) {

                    row = table.createRow(15f);
                    cell = row.createCell(50, fact[0]);
                    cell.setFont(PDType1Font.HELVETICA);cell.setFontSize(10);


                    for(int i = 1; i< fact.length; i++) {
                        cell = row.createCell(50 ,fact[i]);
                        cell.setFont(PDType1Font.HELVETICA_OBLIQUE);cell.setFontSize(10);
                    }
                }
                table.draw();


                //dibujar lineas de firmas
                stream.drawLine(90,200, 250 ,200);
                stream.drawLine(340,200,500,200);

                stream.beginText();
                stream.setFont(PDType1Font.HELVETICA_BOLD, 10f);
                stream.moveTextPositionByAmount(145, 190);
                stream.drawString(messageSource.getMessage("lbl.analyst",null,null));
                stream.endText();

                stream.beginText();
                stream.setFont(PDType1Font.HELVETICA_BOLD, 10f);
                stream.moveTextPositionByAmount(400, 190);
                stream.drawString(messageSource.getMessage("lbl.director",null,null));
                stream.endText();

                stream.beginText();
                stream.setFont(PDType1Font.HELVETICA_BOLD, 10f);
                stream.moveTextPositionByAmount(360, 115);
                stream.drawString(messageSource.getMessage("lbl.print.datetime",null,null) + " ");
                stream.setFont(PDType1Font.HELVETICA, 10f);
                stream.drawString(fechaImpresion);
                stream.endText();

                stream.close();

            }

            }

        doc.save(output);
        doc.close();
        // generate the file
        res = Base64.encodeBase64String(output.toByteArray());

        return res;
    }



    private static PDPage addNewPage(PDDocument doc) {
        PDPage page = new PDPage();
        page.setMediaBox(PDPage.PAGE_SIZE_A4);
        doc.addPage(page);
        return page;
    }


}
