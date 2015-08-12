package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.concepto.Catalogo_Lista;
import ni.gob.minsa.laboratorio.domain.estructura.EntidadesAdtvas;
import ni.gob.minsa.laboratorio.domain.muestra.*;
import ni.gob.minsa.laboratorio.domain.notificacion.DaNotificacion;
import ni.gob.minsa.laboratorio.domain.resultados.DetalleResultado;
import ni.gob.minsa.laboratorio.domain.resultados.DetalleResultadoFinal;
import ni.gob.minsa.laboratorio.domain.vigilanciaSindFebril.DaSindFebril;
import ni.gob.minsa.laboratorio.service.*;
import ni.gob.minsa.laboratorio.utilities.ConstantsSecurity;
import ni.gob.minsa.laboratorio.utilities.DateUtil;
import ni.gob.minsa.laboratorio.utilities.pdfUtils.BaseTable;
import ni.gob.minsa.laboratorio.utilities.pdfUtils.Cell;
import ni.gob.minsa.laboratorio.utilities.pdfUtils.GeneralUtils;
import ni.gob.minsa.laboratorio.utilities.pdfUtils.Row;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.text.translate.UnicodeEscaper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
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

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Created by souyen-ics.
 */
@Controller
@RequestMapping("viewNoti")
public class VisualizarNotificacionController {

    private static final Logger logger = LoggerFactory.getLogger(VisualizarNotificacionController.class);
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
    @Qualifier(value = "respuestasSolicitudService")
    private RespuestasSolicitudService respuestasSolicitudService;

    @Autowired
    @Qualifier(value = "resultadoFinalService")
    private ResultadoFinalService resultadoFinalService;

    @Autowired
    @Qualifier(value = "daNotificacionService")
    public DaNotificacionService daNotificacionService;

    @Autowired
    @Qualifier(value = "sindFebrilService")
    private SindFebrilService sindFebrilService;

    @Autowired
    @Qualifier(value = "respuestasExamenService")
    private RespuestasExamenService respuestasExamenService;

    @Autowired
    @Qualifier(value = "resultadosService")
    private ResultadosService resultadosService;

    @Autowired
    MessageSource messageSource;


    /**
     * Método que se llama al entrar a la opción de menu "Visualizar Notificacion". Se encarga de inicializar las listas para realizar la búsqueda de Mx recepcionadas en el lab
     *
     * @param request para obtener información de la petición del cliente
     * @return ModelAndView
     * @throws Exception
     */
    @RequestMapping(value = "init", method = RequestMethod.GET)
    public ModelAndView initSearchLabForm(HttpServletRequest request) throws Exception {
        logger.debug("buscar muestras recepcionadas para visualizar PDF");
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
            mav.setViewName("laboratorio/notificationPdf");
        } else
            mav.setViewName(urlValidacion);

        return mav;
    }

    @RequestMapping(value = "search", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    String fetchOrdersJson(@RequestParam(value = "strFilter", required = true) String filtro) throws Exception {
        logger.info("Obteniendo las muestras recepcionadas en el laboratorio");
        FiltroMx filtroMx = jsonToFiltroMx(filtro);
        List<RecepcionMx> recepcionMxList = recepcionMxService.getRecepcionesByFiltro(filtroMx);
        return RecepcionMxToJson(recepcionMxList);
    }

    private String RecepcionMxToJson(List<RecepcionMx> recepcionMxList) {
        String jsonResponse = "";
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice = 0;
        for (RecepcionMx recepcion : recepcionMxList) {
            boolean esEstudio = tomaMxService.getSolicitudesEstudioByIdTomaMx(recepcion.getTomaMx().getIdTomaMx()).size() > 0;
            Map<String, String> map = new HashMap<String, String>();
            map.put("idNotificacion", recepcion.getTomaMx().getIdNotificacion().getIdNotificacion());
            map.put("codigoUnicoMx", esEstudio ? recepcion.getTomaMx().getCodigoUnicoMx() : recepcion.getTomaMx().getCodigoLab());
            map.put("idRecepcion", recepcion.getIdRecepcion());
            map.put("idTomaMx", recepcion.getTomaMx().getIdTomaMx());
            map.put("fechaTomaMx", DateUtil.DateToString(recepcion.getTomaMx().getFechaHTomaMx(), "dd/MM/yyyy hh:mm:ss a"));

            RecepcionMxLab recepcionMxLab = recepcionMxService.getRecepcionMxLabByIdRecepGral(recepcion.getIdRecepcion());
            if (recepcionMxLab != null)
                map.put("fechaRecepcionLab", DateUtil.DateToString(recepcionMxLab.getFechaHoraRecepcion(), "dd/MM/yyyy hh:mm:ss a"));
            else
                map.put("fechaRecepcionLab", "");

            if (recepcion.getTomaMx().getIdNotificacion().getCodSilaisAtencion() != null) {
                map.put("codSilais", recepcion.getTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre());
            } else {
                map.put("codSilais", "");
            }
            if (recepcion.getTomaMx().getIdNotificacion().getCodUnidadAtencion() != null) {
                map.put("codUnidadSalud", recepcion.getTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre());
            } else {
                map.put("codUnidadSalud", "");
            }
            map.put("cantidadTubos", (recepcion.getTomaMx().getCanTubos() != null ? String.valueOf(recepcion.getTomaMx().getCanTubos()) : ""));
            map.put("tipoMuestra", recepcion.getTomaMx().getCodTipoMx().getNombre());
            //Si hay fecha de inicio de sintomas se muestra
            Date fechaInicioSintomas = recepcion.getTomaMx().getIdNotificacion().getFechaInicioSintomas();
            if (fechaInicioSintomas != null)
                map.put("fechaInicioSintomas", DateUtil.DateToString(fechaInicioSintomas, "dd/MM/yyyy"));
            else
                map.put("fechaInicioSintomas", " ");
            //Si hay persona
            if (recepcion.getTomaMx().getIdNotificacion().getPersona() != null) {
                /// se obtiene el nombre de la persona asociada a la ficha
                String nombreCompleto = "";
                nombreCompleto = recepcion.getTomaMx().getIdNotificacion().getPersona().getPrimerNombre();
                if (recepcion.getTomaMx().getIdNotificacion().getPersona().getSegundoNombre() != null)
                    nombreCompleto = nombreCompleto + " " + recepcion.getTomaMx().getIdNotificacion().getPersona().getSegundoNombre();
                nombreCompleto = nombreCompleto + " " + recepcion.getTomaMx().getIdNotificacion().getPersona().getPrimerApellido();
                if (recepcion.getTomaMx().getIdNotificacion().getPersona().getSegundoApellido() != null)
                    nombreCompleto = nombreCompleto + " " + recepcion.getTomaMx().getIdNotificacion().getPersona().getSegundoApellido();
                map.put("persona", nombreCompleto);
            } else if (recepcion.getTomaMx().getIdNotificacion().getSolicitante() != null) {
                map.put("persona", recepcion.getTomaMx().getIdNotificacion().getSolicitante().getNombre());
            } else {
                map.put("persona", " ");
            }

            //se arma estructura de diagnósticos o estudios
            Laboratorio labUser = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
            List<DaSolicitudDx> solicitudDxList = tomaMxService.getSolicitudesDxByIdToma(recepcion.getTomaMx().getIdTomaMx(), labUser.getCodigo());
            List<DaSolicitudEstudio> solicitudEList = tomaMxService.getSolicitudesEstudioByIdTomaMx(recepcion.getTomaMx().getIdTomaMx());


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


            map.put("diagnosticos", new Gson().toJson(mapDxList));

            mapResponse.put(indice, map);
            indice++;
        }
        jsonResponse = new Gson().toJson(mapResponse);
        //escapar caracteres especiales, escape de los caracteres con valor numérico mayor a 127
        UnicodeEscaper escaper = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }

    private FiltroMx jsonToFiltroMx(String strJson) throws Exception {
        JsonObject jObjectFiltro = new Gson().fromJson(strJson, JsonObject.class);
        FiltroMx filtroMx = new FiltroMx();
        String nombreApellido = null;
        Date fecInicioRecepcionLab = null;
        Date fecFinRecepcionLab = null;
        String codSilais = null;
        String codUnidadSalud = null;
        String codTipoMx = null;
        String codigoUnicoMx = null;
        String codTipoSolicitud = null;
        String nombreSolicitud = null;


        if (jObjectFiltro.get("nombreApellido") != null && !jObjectFiltro.get("nombreApellido").getAsString().isEmpty())
            nombreApellido = jObjectFiltro.get("nombreApellido").getAsString();
        if (jObjectFiltro.get("fecInicioRecepcionLab") != null && !jObjectFiltro.get("fecInicioRecepcionLab").getAsString().isEmpty())
            fecInicioRecepcionLab = DateUtil.StringToDate(jObjectFiltro.get("fecInicioRecepcionLab").getAsString() + " 00:00:00");
        if (jObjectFiltro.get("fecFinRecepcionLab") != null && !jObjectFiltro.get("fecFinRecepcionLab").getAsString().isEmpty())
            fecFinRecepcionLab = DateUtil.StringToDate(jObjectFiltro.get("fecFinRecepcionLab").getAsString() + " 23:59:59");
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

        filtroMx.setCodSilais(codSilais);
        filtroMx.setCodUnidadSalud(codUnidadSalud);
        filtroMx.setFechaInicioRecepLab(fecInicioRecepcionLab);
        filtroMx.setFechaFinRecepLab(fecFinRecepcionLab);
        filtroMx.setNombreApellido(nombreApellido);
        filtroMx.setCodTipoMx(codTipoMx);
        filtroMx.setCodEstado("ESTDMX|RCLAB"); // recepcionadas en lab
        filtroMx.setIncluirMxInadecuada(true);
        filtroMx.setCodigoUnicoMx(codigoUnicoMx);
        filtroMx.setCodTipoSolicitud(codTipoSolicitud);
        filtroMx.setNombreSolicitud(nombreSolicitud);
        filtroMx.setNombreUsuario(seguridadService.obtenerNombreUsuario());
        filtroMx.setIncluirTraslados(false);

        return filtroMx;
    }


    @RequestMapping(value = "getPDF", method = RequestMethod.GET)
    public
    @ResponseBody
    String getPDF(@RequestParam(value = "idNotificacion", required = true) String idNotificacion, HttpServletRequest request) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PDDocument doc = new PDDocument();
        DaNotificacion not = daNotificacionService.getNotifById(idNotificacion);
        String res = null;
        if (not != null) {
            if (not.getCodTipoNotificacion().getCodigo().equals("TPNOTI|SINFEB")) {
                DaSindFebril febril = sindFebrilService.getDaSindFebril(idNotificacion);


                String fechaImpresion = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());


                if (febril != null) {
                    PDPage page = new PDPage(PDPage.PAGE_SIZE_A4);
                    doc.addPage(page);
                    PDPageContentStream stream = new PDPageContentStream(doc, page);
                    float xCenter;

                    String workingDir = System.getProperty("user.dir");

                    BufferedImage image = ImageIO.read(new File(workingDir + "/fichaFebril.png"));

                    GeneralUtils.drawObject(stream, doc, image, 20, 85, 545, 745);
                    String silais = febril.getIdNotificacion().getCodSilaisAtencion().getNombre();

                    String nombreS = silais != null ? silais.replace("SILAIS", "") : "----";
                    String municipio = febril.getIdNotificacion().getPersona().getMunicipioResidencia() != null ? febril.getIdNotificacion().getPersona().getMunicipioResidencia().getNombre() : "----";
                    String us = febril.getIdNotificacion().getCodUnidadAtencion() != null ? febril.getIdNotificacion().getCodUnidadAtencion().getNombre() : "----";
                    String nExp = febril.getCodExpediente() != null ? febril.getCodExpediente() : "----------";
                    //laboratorio pendiente
                    String fecha = febril.getFechaFicha() != null ? DateUtil.DateToString(febril.getFechaFicha(), "yyyy/MM/dd") : null;
                    String[] array = fecha != null ? fecha.split("/") : null;

                    String dia = array != null ? array[2] : "--";
                    String mes = array != null ? array[1] : "--";
                    String anio = array != null ? array[0] : "--";
                    String nombrePersona = null;

                    nombrePersona = febril.getIdNotificacion().getPersona().getPrimerNombre();
                    if (febril.getIdNotificacion().getPersona().getSegundoNombre() != null)
                        nombrePersona = nombrePersona + " " + febril.getIdNotificacion().getPersona().getSegundoNombre();
                    nombrePersona = nombrePersona + " " + febril.getIdNotificacion().getPersona().getPrimerApellido();
                    if (febril.getIdNotificacion().getPersona().getSegundoApellido() != null)
                        nombrePersona = nombrePersona + " " + febril.getIdNotificacion().getPersona().getSegundoApellido();

                    String edad = null;
                    if (febril.getIdNotificacion().getPersona().getFechaNacimiento() != null && febril.getFechaFicha() != null) {
                        edad = DateUtil.calcularEdad(febril.getIdNotificacion().getPersona().getFechaNacimiento(), febril.getFechaFicha());
                    }

                    String[] edadDias = edad != null ? edad.split("/") : null;
                    String anios = edadDias != null ? edadDias[0] : "--";
                    String meses = edadDias != null ? edadDias[1] : "--";

                    String fNac = febril.getIdNotificacion().getPersona().getFechaNacimiento() != null ? DateUtil.DateToString(febril.getIdNotificacion().getPersona().getFechaNacimiento(), "yyyy/MM/dd") : null;
                    String[] fechaNac = fNac != null ? fNac.split("/") : null;
                    String anioNac = fechaNac != null ? fechaNac[0] : "--";
                    String mesNac = fechaNac != null ? fechaNac[1] : "--";
                    String diaNac = fechaNac != null ? fechaNac[2] : "--";

                    String sexo = febril.getIdNotificacion().getPersona().getSexo() != null ? febril.getIdNotificacion().getPersona().getSexo().getValor() : null;

                    String ocupacion = febril.getIdNotificacion().getPersona().getOcupacion() != null ? febril.getIdNotificacion().getPersona().getOcupacion().getNombre() : "----------";

                    String tutor = febril.getNombPadre() != null ? febril.getNombPadre() : "----------";

                    String direccion = febril.getIdNotificacion().getDireccionResidencia() != null ? febril.getIdNotificacion().getDireccionResidencia() : "----------";

                    String procedencia = febril.getCodProcedencia() != null ? febril.getCodProcedencia().getValor() : null;

                    String viaje = febril.getViaje() != null ? febril.getViaje().getValor() : "----";

                    String donde = febril.getDondeViaje() != null ? febril.getDondeViaje() : "----------";

                    String emb = febril.getEmbarazo() != null ? febril.getEmbarazo().getValor() : "----";

                    String mesesEmb = febril.getMesesEmbarazo() != 0 ? String.valueOf(febril.getMesesEmbarazo()) : "--";

                    String enfCronica = febril.getEnfCronica() != null ? febril.getEnfCronica() : null;


                    boolean asma = false;
                    boolean alergiaR = false;
                    boolean alergiaD = false;
                    boolean diab = false;
                    boolean otra = false;
                    boolean ninguna = false;
                    if (enfCronica != null) {
                        if (enfCronica.contains("CRONICAS|ASMA")) {
                            asma = true;
                        }
                        if (enfCronica.contains("CRONICAS|ALERRESP")) {
                            alergiaR = true;
                        }
                        if (enfCronica.contains("CRONICAS|ALERDER")) {
                            alergiaD = true;
                        }
                        if (enfCronica.contains("CRONICAS|DIAB")) {
                            diab = true;
                        }
                        if (enfCronica.contains("CRONICAS|OTRA")) {
                            otra = true;
                        }

                        if (enfCronica.contains("CRONICAS|NING")) {
                            ninguna = true;
                        }

                    }

                    String eAguda = febril.getEnfAgudaAdicional() != null ? febril.getEnfAgudaAdicional() : null;

                    boolean neumonia = false;
                    boolean malaria = false;
                    boolean infeccionV = false;
                    boolean otraAguda = false;

                    if (eAguda != null) {
                        if (eAguda.contains("AGUDAS|NEU")) {
                            neumonia = true;
                        }
                        if (eAguda.contains("AGUDAS|MAL")) {
                            malaria = true;
                        }
                        if (eAguda.contains("AGUDAS|IVU")) {
                            infeccionV = true;
                        }
                        if (eAguda.contains("AGUDAS|OTRA")) {
                            otraAguda = true;
                        }
                    }

                    String fAgua = febril.getFuenteAgua() != null ? febril.getFuenteAgua() : null;

                    boolean aguaP = false;
                    boolean puestoP = false;
                    boolean pozo = false;
                    boolean rio = false;

                    if (fAgua != null) {
                        if (fAgua.contains("AGUA|APP")) {
                            aguaP = true;
                        }

                        if (fAgua.contains("AGUA|PP")) {
                            puestoP = true;
                        }

                        if (fAgua.contains("AGUA|POZO")) {
                            pozo = true;
                        }

                        if (fAgua.contains("AGUA|RIO")) {
                            rio = true;
                        }
                    }

                    String animales = febril.getAnimales() != null ? febril.getAnimales() : null;
                    boolean perros = false;
                    boolean gatos = false;
                    boolean cerdos = false;
                    boolean ganado = false;
                    boolean ratones = false;
                    boolean ratas = false;
                    boolean otrosAnim = false;

                    if (animales != null) {
                        if (animales.contains("ANIM|PERRO")) {
                            perros = true;
                        }
                        if (animales.contains("ANIM|GATO")) {
                            gatos = true;
                        }
                        if (animales.contains("ANIM|CERDO")) {
                            cerdos = true;
                        }
                        if (animales.contains("ANIM|GANADO")) {
                            ganado = true;
                        }
                        if (animales.contains("ANIM|RATON")) {
                            ratones = true;
                        }
                        if (animales.contains("ANIM|RATA")) {
                            ratas = true;
                        }
                        if (animales.contains("ANIM|OTRA")) {
                            otrosAnim = true;
                        }

                    }

                    String fis = febril.getIdNotificacion().getFechaInicioSintomas() != null ? DateUtil.DateToString(febril.getIdNotificacion().getFechaInicioSintomas(), "yyyy/MM/dd") : null;
                    String[] fechaFis = fis != null ? fis.split("/") : null;
                    String anioFis = fechaFis != null ? fechaFis[0] : "--";
                    String mesFis = fechaFis != null ? fechaFis[1] : "--";
                    String diaFis = fechaFis != null ? fechaFis[2] : "--";

                    String dsa = febril.getSsDSA() != null ? febril.getSsDSA() : null;

                    boolean fiebre = false;
                    boolean cefalea = false;
                    boolean mialgias = false;
                    boolean artralgias = false;
                    boolean dolorRetro = false;
                    boolean nauseas = false;
                    boolean rash = false;
                    boolean pruebaTorn = false;

                    if (dsa != null) {
                        if (dsa.contains("DSSA|FIE")) {
                            fiebre = true;
                        }
                        if (dsa.contains("DSSA|CEF")) {
                            cefalea = true;
                        }
                        if (dsa.contains("DSSA|MIA")) {
                            mialgias = true;
                        }

                        if (dsa.contains("DSSA|DRO")) {
                            dolorRetro = true;
                        }
                        if (dsa.contains("DSSA|NAU")) {
                            nauseas = true;
                        }
                        if (dsa.contains("DSSA|RAS")) {
                            rash = true;
                        }
                        if (dsa.contains("DSSA|PTO")) {
                            pruebaTorn = true;
                        }

                        if (dsa.contains("DSSA|ART")) {
                            artralgias = true;
                        }
                    }

                    String dcsa = febril.getSsDCA() != null ? febril.getSsDCA() : null;
                    boolean dolorAbd = false;
                    boolean vomitos = false;
                    boolean hemorragias = false;
                    boolean letargia = false;
                    boolean hepatomegalia = false;
                    boolean acumulacion = false;
                    if (dcsa != null) {
                        if (dcsa.contains("DCSA|ABD")) {
                            dolorAbd = true;
                        }

                        if (dcsa.contains("DCSA|VOM")) {
                            vomitos = true;
                        }

                        if (dcsa.contains("DCSA|HEM")) {
                            hemorragias = true;
                        }

                        if (dcsa.contains("DCSA|LET")) {
                            letargia = true;
                        }

                        if (dcsa.contains("DCSA|HEP")) {
                            hepatomegalia = true;
                        }

                        if (dcsa.contains("DCSA|ACU")) {
                            acumulacion = true;
                        }

                    }

                    String dengueGrave = febril.getSsDS() != null ? febril.getSsDS() : null;
                    boolean pinzamiento = false;
                    boolean hipotension = false;
                    boolean shock = false;
                    boolean distres = false;
                    boolean fallaOrg = false;
                    if (dengueGrave != null) {
                        if (dengueGrave.contains("DGRA|PIN")) {
                            pinzamiento = true;
                        }

                        if (dengueGrave.contains("DGRA|HIP")) {
                            hipotension = true;
                        }

                        if (dengueGrave.contains("DGRA|SHO")) {
                            shock = true;
                        }

                        if (dengueGrave.contains("DGRA|DIS")) {
                            distres = true;
                        }

                        if (dengueGrave.contains("DGRA|ORG")) {
                            fallaOrg = true;
                        }
                    }

                    String leptospirosis = febril.getSsLepto() != null ? febril.getSsLepto() : null;
                    boolean cefaleaIn = false;
                    boolean tos = false;
                    boolean respiratorio = false;
                    boolean ictericia = false;
                    boolean oliguria = false;
                    boolean escalofrio = false;
                    boolean dolorPant = false;
                    boolean hematuria = false;
                    boolean congestion = false;
                    if (leptospirosis != null) {

                        if (leptospirosis.contains("LEPT|CEF")) {
                            cefaleaIn = true;
                        }

                        if (leptospirosis.contains("LEPT|TOS")) {
                            tos = true;
                        }

                        if (leptospirosis.contains("LEPT|ICT")) {
                            ictericia = true;
                        }

                        if (leptospirosis.contains("LEPT|OLI")) {
                            oliguria = true;
                        }

                        if (leptospirosis.contains("LEPT|ESC")) {
                            escalofrio = true;
                        }

                        if (leptospirosis.contains("LEPT|DOL")) {
                            dolorPant = true;
                        }

                        if (leptospirosis.contains("LEPT|HEM")) {
                            hematuria = true;
                        }

                        if (leptospirosis.contains("LEPT|CON")) {
                            congestion = true;
                        }

                    }

                    String hantavirus = febril.getSsHV() != null ? febril.getSsHV() : null;
                    boolean difResp = false;
                    boolean hip2 = false;
                    boolean dAbdIn = false;
                    boolean dLumbar = false;
                    boolean oliguria2 = false;
                    if (hantavirus != null) {
                        if (hantavirus.contains("HANT|DIF")) {
                            difResp = false;
                        }
                        if (hantavirus.contains("HANT|HIP")) {
                            hip2 = true;
                        }
                        if (hantavirus.contains("HANT|ABD")) {
                            dAbdIn = true;
                        }
                        if (hantavirus.contains("HANT|LUM")) {
                            dLumbar = true;
                        }
                        if (hantavirus.contains("HANT|OLI")) {
                            oliguria2 = true;
                        }
                    }

                    String chik = febril.getSsCK() != null ? febril.getSsCK() : null;
                    boolean cefaleaChik = false;
                    boolean fiebreChik = false;
                    boolean artritisChik = false;
                    boolean artralgiasChik = false;
                    boolean edemaChik = false;
                    boolean maniChik = false;
                    boolean mialgiaCHik = false;
                    boolean dEspChik = false;
                    boolean meninChik = false;

                    if (chik != null) {
                        if (chik.contains("CHIK|CEF")) {
                            cefaleaChik = true;
                        }

                        if (chik.contains("CHIK|FIE")) {
                            fiebreChik = true;
                        }

                        if (chik.contains("CHIK|ART")) {
                            artritisChik = true;
                        }

                        if (chik.contains("CHIK|ARL")) {
                            artralgiasChik = true;
                        }

                        if (chik.contains("CHIK|EDE")) {
                            edemaChik = true;
                        }

                        if (chik.contains("CHIK|MAN")) {
                            maniChik = true;
                        }

                        if (chik.contains("CHIK|MIA")) {
                            mialgiaCHik = true;
                        }

                        if (chik.contains("CHIK|ESP")) {
                            dEspChik = true;
                        }

                        if (chik.contains("CHIK|MEN")) {
                            meninChik = true;
                        }
                    }

                    String hospitalizado = febril.getHosp() != null ? febril.getHosp().getValor() : "----";

                    String fechaIngreso = febril.getFechaIngreso() != null ? DateUtil.DateToString(febril.getFechaIngreso(), "yyyy/MM/dd") : null;


                    String[] fechaIn = fechaIngreso != null ? fechaIngreso.split("/") : null;
                    String anioIn = fechaIn != null ? fechaIn[0] : "--";
                    String mesIn = fechaIn != null ? fechaIn[1] : "--";
                    String diaIn = fechaIn != null ? fechaIn[2] : "--";

                    String fallecido = febril.getFallecido() != null ? febril.getFallecido().getValor() : "--";

                    String fechaFallecido = febril.getFechaFallecido() != null ? DateUtil.DateToString(febril.getFechaFallecido(), "yyyy/MM/dd") : null;

                    String[] fechaFa = fechaFallecido != null ? fechaFallecido.split("/") : null;
                    String anioFa = fechaFa != null ? fechaFa[0] : "--";
                    String mesFa = fechaFa != null ? fechaFa[1] : "--";
                    String diaFa = fechaFa != null ? fechaFa[2] : "--";

                    String dxPresuntivo = febril.getDxPresuntivo() != null ? febril.getDxPresuntivo() : "----------";
                    String temp = febril.getTemperatura() != null ? febril.getTemperatura().toString() : "--";
                    String pad = febril.getPad() != null ? febril.getPad().toString() : "--";
                    String pas = febril.getPas() != null ? febril.getPas().toString() : "--";

                    String dxFinal = febril.getDxFinal() != null ? febril.getDxFinal() : "----------";

                    String personFilledTab = febril.getNombreLlenoFicha() != null ? febril.getNombreLlenoFicha() : "----------";


                    float y = 723;
                    float m = 11;
                    float m1 = 29;
                    float x = 86;
                    float x1 = 86;
                    GeneralUtils.drawTEXT(nombreS, y, x, stream, 7, PDType1Font.TIMES_ROMAN);
                    x1 += 122;
                    GeneralUtils.drawTEXT(municipio, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);
                    x1 += 160;
                    GeneralUtils.drawTEXT(us, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);
                    y -= m;
                    x1 = x + 45;
                    GeneralUtils.drawTEXT(nExp, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);

                    x1 += 199;
                    GeneralUtils.drawTEXT(dia, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);
                    x1 += 23;
                    GeneralUtils.drawTEXT(mes, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);
                    x1 += 25;
                    GeneralUtils.drawTEXT(anio, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);

                    y -= 28;
                    x1 = x + 55;
                    GeneralUtils.drawTEXT(nombrePersona, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);

                    y -= 9;
                    x1 = x - 3;
                    GeneralUtils.drawTEXT(anios, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);
                    x1 += 16;
                    GeneralUtils.drawTEXT(meses, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);

                    x1 += 102;
                    GeneralUtils.drawTEXT(diaNac, y, x1, stream, 6, PDType1Font.TIMES_ROMAN);
                    x1 += 15;
                    GeneralUtils.drawTEXT(mesNac, y, x1, stream, 6, PDType1Font.TIMES_ROMAN);
                    x1 += 13;
                    GeneralUtils.drawTEXT(anioNac, y, x1, stream, 6, PDType1Font.TIMES_ROMAN);

                    if (sexo != null) {
                        if (sexo.equals("Hombre")) {
                            x1 += 78;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);
                        } else if (sexo.equals("Mujer")) {
                            x1 += 58;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }
                    }

                    x1 = x + 290;
                    GeneralUtils.drawTEXT(ocupacion, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);

                    y -= m;
                    x1 = x + 75;
                    GeneralUtils.drawTEXT(tutor, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);

                    y -= 9;
                    x1 = x + 15;
                    GeneralUtils.drawTEXT(direccion, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);

                    if (procedencia != null) {
                        y -= 9;
                        if (procedencia.equals("Urbano")) {

                            x1 = x + 55;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);
                        } else if (procedencia.equals("Rural")) {
                            x1 = x + 98;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);
                        }

                    }

                    x1 = x + 210;
                    GeneralUtils.drawTEXT(viaje, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);

                    x1 += 58;
                    GeneralUtils.drawTEXT(donde, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);


                    y -= 9;
                    x1 = x + 25;
                    GeneralUtils.drawTEXT(emb, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);

                    x1 += 100;
                    GeneralUtils.drawTEXT(mesesEmb, y, x1, stream, 7, PDType1Font.COURIER);

                    if (ninguna) {
                        x1 += 150;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.none", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);
                    }

                    if (asma) {
                        x1 = x + 350;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);
                    }

                    y -= 9;
                    if (alergiaR) {
                        x1 = x + 15;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);
                    }

                    if (alergiaD) {
                        x1 = x + 117;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);
                    }

                    if (diab) {
                        x1 = x + 175;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);
                    }

                    if (otra) {
                        x1 += 110;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);
                    }

                    if (neumonia) {
                        x1 = x + 420;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);
                    }

                    y -= 9;
                    if (malaria) {
                        x1 = x + 8;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);
                    }

                    if (infeccionV) {
                        x1 = x + 115;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);
                    }

                    if (otraAguda) {
                        x1 = x + 175;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);
                    }

                    y -= 28;

                    if (aguaP) {
                        x1 = x + 143;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                    } else {
                        x1 = x + 171;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);
                    }

                    if (puestoP) {
                        x1 = x + 260;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);
                    }

                    if (pozo) {
                        x1 = x + 318;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);
                    }

                    if (rio) {
                        x1 = x + 370;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);
                    }


                    y -= 9;

                    if (perros) {
                        x1 = x + 130;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                    }

                    if (gatos) {
                        x1 = x + 172;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                    }

                    if (cerdos) {
                        x1 = x + 220;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                    }

                    if (ganado) {
                        x1 = x + 272;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);
                    }

                    if (ratones) {
                        x1 = x + 323;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                    }

                    if (ratas) {
                        x1 = x + 365;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);
                    }

                    if (otrosAnim) {
                        x1 = x + 405;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);
                    }

                    y -= 37;
                    x1 = x + 88;
                    GeneralUtils.drawTEXT(diaFis, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);
                    x1 = x + 115;
                    GeneralUtils.drawTEXT(mesFis, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);
                    x1 = x + 150;
                    GeneralUtils.drawTEXT(anioFis, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);

                    y -= 22;
                    x1 = x + 25;
                    GeneralUtils.drawTEXT(temp, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);
                    x1 = x + 140;
                    GeneralUtils.drawTEXT(pas, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);
                    x1 = x + 170;
                    GeneralUtils.drawTEXT(pad, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);

                    y -= 47;
                    if (fiebre) {
                        x1 = x + 90;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.yes", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    } else {
                        x1 = x + 90;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.no", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    }

                    if (dolorAbd) {
                        x1 = x + 275;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.yes", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    } else {
                        x1 = x + 275;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.no", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    }


                    if (pinzamiento) {
                        x1 = x + 388;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.yes", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    } else {
                        x1 = x + 388;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.no", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    }

                    y -= 9;
                    if (cefalea) {
                        x1 = x + 90;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.yes", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    } else {
                        x1 = x + 90;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.no", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    }

                    if (vomitos) {
                        x1 = x + 275;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.yes", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    } else {
                        x1 = x + 275;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.no", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    }


                    if (hipotension) {
                        x1 = x + 388;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.yes", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    } else {
                        x1 = x + 388;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.no", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    }

                    y -= 10;
                    if (mialgias) {
                        x1 = x + 90;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.yes", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    } else {
                        x1 = x + 90;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.no", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    }


                    if (hemorragias) {
                        x1 = x + 275;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.yes", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    } else {
                        x1 = x + 275;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.no", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    }


                    if (shock) {
                        x1 = x + 388;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.yes", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    } else {
                        x1 = x + 388;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.no", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    }


                    y -= 10;
                    if (artralgias) {
                        x1 = x + 90;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.yes", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    } else {
                        x1 = x + 90;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.no", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    }

                    if (letargia) {
                        x1 = x + 275;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.yes", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    } else {
                        x1 = x + 275;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.no", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    }


                    if (distres) {
                        x1 = x + 388;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.yes", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    } else {
                        x1 = x + 388;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.no", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    }

                    y -= 9;
                    if (dolorRetro) {
                        x1 = x + 90;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.yes", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    } else {
                        x1 = x + 90;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.no", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    }

                    if (hepatomegalia) {
                        x1 = x + 275;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.yes", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    } else {
                        x1 = x + 275;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.no", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    }


                    if (fallaOrg) {
                        x1 = x + 388;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.yes", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    } else {
                        x1 = x + 388;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.no", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    }


                    y -= 9;
                    if (nauseas) {
                        x1 = x + 90;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.yes", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    } else {
                        x1 = x + 90;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.no", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    }

                    if (acumulacion) {
                        x1 = x + 275;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.yes", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    } else {
                        x1 = x + 275;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.no", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    }


                    y -= 10;
                    if (rash) {
                        x1 = x + 90;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.yes", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    } else {
                        x1 = x + 90;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.no", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    }


                    y -= 9;
                    if (pruebaTorn) {
                        x1 = x + 90;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.yes", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    } else {
                        x1 = x + 90;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.no", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    }


                    y -= 37;

                    if (cefaleaIn) {
                        x1 = x + 127;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.yes", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    } else {
                        x1 = x + 127;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.no", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    }

                    if (difResp) {
                        x1 = x + 264;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.yes", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    } else {
                        x1 = x + 264;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.no", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    }

                    if (fiebreChik) {
                        x1 = x + 410;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.yes", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    } else {
                        x1 = x + 410;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.no", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    }


                    y -= 10;

                    if (tos) {
                        x1 = x + 127;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.yes", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    } else {
                        x1 = x + 127;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.no", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    }


                    if (hip2) {
                        x1 = x + 264;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.yes", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    } else {
                        x1 = x + 264;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.no", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    }

                    if (artritisChik) {
                        x1 = x + 410;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.yes", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    } else {
                        x1 = x + 410;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.no", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    }


                    y -= 9;

                    if (ictericia) {
                        x1 = x + 127;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.yes", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    } else {
                        x1 = x + 127;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.no", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    }


                    if (dAbdIn) {
                        x1 = x + 264;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.yes", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    } else {
                        x1 = x + 264;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.no", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    }

                    if (artralgiasChik) {
                        x1 = x + 410;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.yes", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    } else {
                        x1 = x + 410;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.no", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    }


                    y -= 9;

                    if (oliguria) {
                        x1 = x + 127;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.yes", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    } else {
                        x1 = x + 127;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.no", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    }


                    if (dLumbar) {
                        x1 = x + 264;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.yes", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    } else {
                        x1 = x + 264;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.no", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    }

                    if (edemaChik) {
                        x1 = x + 410;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.yes", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    } else {
                        x1 = x + 410;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.no", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    }


                    y -= 9;

                    if (escalofrio) {
                        x1 = x + 127;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.yes", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    } else {
                        x1 = x + 127;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.no", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    }


                    if (oliguria2) {
                        x1 = x + 264;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.yes", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    } else {
                        x1 = x + 264;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.no", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    }

                    if (maniChik) {
                        x1 = x + 410;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.yes", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    } else {
                        x1 = x + 410;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.no", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    }


                    y -= 10;

                    if (dolorPant) {
                        x1 = x + 127;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.yes", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    } else {
                        x1 = x + 127;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.no", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    }

                    if (mialgiaCHik) {
                        x1 = x + 410;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.yes", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    } else {
                        x1 = x + 410;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.no", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    }


                    y -= 9;

                    if (hematuria) {
                        x1 = x + 127;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.yes", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    } else {
                        x1 = x + 127;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.no", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    }

                    if (dEspChik) {
                        x1 = x + 410;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.yes", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    } else {
                        x1 = x + 410;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.no", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    }


                    y -= 10;

                    if (congestion) {
                        x1 = x + 127;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.yes", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    } else {
                        x1 = x + 127;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.no", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    }

                    if (cefaleaChik) {
                        x1 = x + 410;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.yes", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    } else {
                        x1 = x + 410;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.no", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    }

                    y -= 10;

                    if (meninChik) {
                        x1 = x + 410;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.yes", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    } else {
                        x1 = x + 410;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.abbreviation.no", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                    }


                    y -= 27;
                    x1 = x + 35;
                    GeneralUtils.drawTEXT(hospitalizado, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);
                    x1 += 83;
                    GeneralUtils.drawTEXT(diaIn, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);
                    x1 += 35;
                    GeneralUtils.drawTEXT(mesIn, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);
                    x1 += 30;
                    GeneralUtils.drawTEXT(anioIn, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);

                    x1 = x + 240;
                    GeneralUtils.drawTEXT(fallecido, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);

                    x1 += 88;
                    GeneralUtils.drawTEXT(diaFa, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);
                    x1 += 21;
                    GeneralUtils.drawTEXT(mesFa, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);
                    x1 += 20;
                    GeneralUtils.drawTEXT(anioFa, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);

                    y -= 20;
                    x1 = x + 70;
                    GeneralUtils.drawTEXT(dxPresuntivo, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);

                    y -= 26;

                    //load all the request by notification

                    List<DaSolicitudDx> diagnosticosList = resultadoFinalService.getSolicitudesDxByIdNotificacion(febril.getIdNotificacion().getIdNotificacion());

                    float y1 = 0;


                    if (!diagnosticosList.isEmpty()) {
                        int con = 0;
                        for (DaSolicitudDx soli : diagnosticosList) {
                            List<String[]> reqList = new ArrayList<String[]>();
                            List<String[]> dxList = new ArrayList<String[]>();
                            con++;
                            if (con >= 2) {
                                y = y1;
                            }
                            String[] content = new String[5];
                            List<OrdenExamen> ordenes = ordenExamenMxService.getOrdenesExamenNoAnuladasByIdSolicitud(soli.getIdSolicitudDx());
                            List<DetalleResultadoFinal> resul = resultadoFinalService.getDetResActivosBySolicitud(soli.getIdSolicitudDx());

                            content[0] = soli.getCodDx().getNombre() != null ? soli.getCodDx().getNombre() : "";
                            content[1] = soli.getFechaHSolicitud() != null ? DateUtil.DateToString(soli.getFechaHSolicitud(), "dd/MM/yyyy HH:mm:ss") : "";
                            content[2] = soli.getIdTomaMx().getFechaHTomaMx() != null ? DateUtil.DateToString(soli.getIdTomaMx().getFechaHTomaMx(), "dd/MM/yyyy HH:mm:ss") : "";
                            content[3] = soli.getIdTomaMx().getCodTipoMx() != null ? soli.getIdTomaMx().getCodTipoMx().getNombre() : "";

                            int cont = 0;
                            String rFinal = null;
                            for (DetalleResultadoFinal det : resul) {
                                cont++;

                                if (cont == 1) {
                                    if (cont == resul.size()) {

                                        if (det.getRespuesta() != null) {
                                            if (det.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                                                Catalogo_Lista valor = respuestasExamenService.getCatalogoListaConceptoByIdLista(Integer.valueOf(det.getValor()));
                                                rFinal = det.getRespuesta().getNombre() + ":" + " " + valor.getValor();

                                            } else {
                                                rFinal = det.getRespuesta().getNombre() + ":" + " " + det.getValor();
                                            }
                                        } else {
                                            if (det.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                                                Catalogo_Lista valor = respuestasExamenService.getCatalogoListaConceptoByIdLista(Integer.valueOf(det.getValor()));
                                                rFinal = det.getRespuestaExamen().getNombre() + ":" + " " + valor.getValor();

                                            } else {
                                                rFinal = det.getRespuestaExamen().getNombre() + ":" + " " + det.getValor();
                                            }
                                        }

                                    } else {
                                        if (det.getRespuesta() != null) {
                                            if (det.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                                                Catalogo_Lista valor = respuestasExamenService.getCatalogoListaConceptoByIdLista(Integer.valueOf(det.getValor()));
                                                rFinal = "," + " " + det.getRespuesta().getNombre() + ":" + " " + valor.getValor();

                                            } else {
                                                rFinal = "," + " " + det.getRespuesta().getNombre() + ":" + " " + det.getValor();
                                            }
                                        } else {
                                            if (det.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                                                Catalogo_Lista valor = respuestasExamenService.getCatalogoListaConceptoByIdLista(Integer.valueOf(det.getValor()));
                                                rFinal = "," + " " + det.getRespuestaExamen().getNombre() + ":" + " " + valor.getValor();

                                            } else {
                                                rFinal = "," + " " + det.getRespuestaExamen().getNombre() + ":" + " " + det.getValor();
                                            }
                                        }

                                    }
                                } else {
                                    if (cont == resul.size()) {
                                        if (det.getRespuesta() != null) {
                                            if (det.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                                                Catalogo_Lista valor = respuestasExamenService.getCatalogoListaConceptoByIdLista(Integer.valueOf(det.getValor()));
                                                rFinal += det.getRespuesta().getNombre() + ":" + " " + valor.getValor();

                                            } else {
                                                rFinal += det.getRespuesta().getNombre() + ":" + " " + det.getValor();
                                            }
                                        } else {
                                            if (det.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                                                Catalogo_Lista valor = respuestasExamenService.getCatalogoListaConceptoByIdLista(Integer.valueOf(det.getValor()));
                                                rFinal += det.getRespuestaExamen().getNombre() + ":" + " " + valor.getValor();

                                            } else {
                                                rFinal += det.getRespuestaExamen().getNombre() + ":" + " " + det.getValor();
                                            }
                                        }

                                    } else {
                                        if (det.getRespuesta() != null) {
                                            if (det.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                                                Catalogo_Lista valor = respuestasExamenService.getCatalogoListaConceptoByIdLista(Integer.valueOf(det.getValor()));
                                                rFinal += "," + " " + det.getRespuesta().getNombre() + ":" + " " + valor.getValor();

                                            } else {
                                                rFinal += "," + " " + det.getRespuesta().getNombre() + ":" + " " + det.getValor();
                                            }
                                        } else {
                                            if (det.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                                                Catalogo_Lista valor = respuestasExamenService.getCatalogoListaConceptoByIdLista(Integer.valueOf(det.getValor()));
                                                rFinal += "," + " " + det.getRespuestaExamen().getNombre() + ":" + " " + valor.getValor();

                                            } else {
                                                rFinal += "," + " " + det.getRespuestaExamen().getNombre() + ":" + " " + det.getValor();
                                            }
                                        }

                                    }
                                }

                            }

                            content[4] = rFinal;
                            reqList.add(content);


                            if (!ordenes.isEmpty()) {

                                String rExamen = null;
                                String fechaProcesamiento = null;
                                for (OrdenExamen ex : ordenes) {
                                    String[] examen = new String[3];
                                    List<DetalleResultado> results = resultadosService.getDetallesResultadoActivosByExamen(ex.getIdOrdenExamen());

                                    examen[0] = ex.getCodExamen() != null ? ex.getCodExamen().getNombre() : "";


                                    int cont1 = 0;
                                    for (DetalleResultado resExamen : results) {
                                        cont1++;

                                        if (cont1 == 1) {

                                            if (cont1 == resul.size()) {
                                                fechaProcesamiento = DateUtil.DateToString(resExamen.getFechahRegistro(), "dd/MM/yyyy HH:mm:ss");
                                                if (resExamen.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                                                    Catalogo_Lista valor = respuestasExamenService.getCatalogoListaConceptoByIdLista(Integer.valueOf(resExamen.getValor()));
                                                    rExamen = resExamen.getRespuesta().getNombre() + ":" + " " + valor.getValor();

                                                } else {
                                                    rExamen = resExamen.getRespuesta().getNombre() + ":" + " " + resExamen.getValor();
                                                }
                                            } else {
                                                if (resExamen.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                                                    Catalogo_Lista valor = respuestasExamenService.getCatalogoListaConceptoByIdLista(Integer.valueOf(resExamen.getValor()));
                                                    rExamen = "," + " " + resExamen.getRespuesta().getNombre() + ":" + " " + valor.getValor();

                                                } else {
                                                    rExamen = "," + " " + resExamen.getRespuesta().getNombre() + ":" + " " + resExamen.getValor();
                                                }
                                            }
                                        } else {
                                            if (cont1 == resul.size()) {
                                                fechaProcesamiento = DateUtil.DateToString(resExamen.getFechahRegistro(), "dd/MM/yyyy HH:mm:ss");

                                                if (resExamen.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                                                    Catalogo_Lista valor = respuestasExamenService.getCatalogoListaConceptoByIdLista(Integer.valueOf(resExamen.getValor()));
                                                    rExamen += resExamen.getRespuesta().getNombre() + ":" + " " + valor.getValor();

                                                } else {
                                                    rExamen += resExamen.getRespuesta().getNombre() + ":" + " " + resExamen.getValor();
                                                }
                                            } else {
                                                if (resExamen.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                                                    Catalogo_Lista valor = respuestasExamenService.getCatalogoListaConceptoByIdLista(Integer.valueOf(resExamen.getValor()));
                                                    rExamen += "," + " " + resExamen.getRespuesta().getNombre() + ":" + " " + valor.getValor();

                                                } else {
                                                    rExamen += "," + " " + resExamen.getRespuesta().getNombre() + ":" + " " + resExamen.getValor();
                                                }
                                            }
                                        }

                                    }
                                    examen[1] = fechaProcesamiento;
                                    examen[2] = rExamen != null ? rExamen : "";
                                    dxList.add(examen);


                                }


                            }
                            drawTable(reqList, doc, page, y);
                            y -= 20;
                            drawTable1(dxList, doc, page, y);
                            y1 = y - ((dxList.size() + 2) * 10);


                        }

                        //dx final
                        y = y1 - 5;
                        x1 = x - 25;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.final.dx", null, null), y, x1, stream, 8, PDType1Font.TIMES_ROMAN);
                        x1 += 70;
                        GeneralUtils.drawTEXT(dxFinal, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);

                    }

                    y -= 10;
                    x1 = x - 25;
                    GeneralUtils.drawTEXT(messageSource.getMessage("lbl.person.who.filled.tab", null, null), y, x1, stream, 8, PDType1Font.TIMES_ROMAN);
                    x1 += 180;
                    GeneralUtils.drawTEXT(personFilledTab, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);

                    //fecha impresión
                   /* GeneralUtils.drawTEXT(messageSource.getMessage("lbl.print.datetime", null, null), 100, 605, stream, 10, PDType1Font.HELVETICA_BOLD);
                    GeneralUtils.drawTEXT(fechaImpresion, 100, 900, stream, 10, PDType1Font.HELVETICA);*/

                    stream.close();

                    doc.save(output);
                    doc.close();
                    // generate the file
                    res = Base64.encodeBase64String(output.toByteArray());
                }
            }
        }

        return res;
    }

    private void drawTable(List<String[]> reqList, PDDocument doc, PDPage page, float y) throws IOException {

        //drawTable

        //Initialize table
        float margin = 33;
        float tableWidth = 520;
        float yStartNewPage = y;
        float yStart = yStartNewPage;
        float bottomMargin = 45;
        BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true, true);

        //Create Header row
        Row headerRow = table.createRow(10f);
        table.setHeader(headerRow);

        //Create 2 column row

        Cell cell;
        Row row;


        //Create Fact header row
        Row factHeaderrow = table.createRow(10f);
        cell = factHeaderrow.createCell(20, messageSource.getMessage("lbl.request1", null, null));
        cell.setFont(PDType1Font.TIMES_BOLD);
        cell.setFontSize(7);
        cell.setFillColor(Color.LIGHT_GRAY);

        cell = factHeaderrow.createCell(20, messageSource.getMessage("lbl.send.request.date", null, null));
        cell.setFillColor(Color.lightGray);
        cell.setFont(PDType1Font.TIMES_BOLD);
        cell.setFontSize(7);

        cell = factHeaderrow.createCell(20, messageSource.getMessage("lbl.sampling.datetime", null, null));
        cell.setFillColor(Color.lightGray);
        cell.setFont(PDType1Font.TIMES_BOLD);
        cell.setFontSize(7);

        cell = factHeaderrow.createCell(20, messageSource.getMessage("lbl.sample.type", null, null));
        cell.setFillColor(Color.lightGray);
        cell.setFont(PDType1Font.TIMES_BOLD);
        cell.setFontSize(7);

        cell = factHeaderrow.createCell(20, messageSource.getMessage("lbl.final.result", null, null));
        cell.setFillColor(Color.lightGray);
        cell.setFont(PDType1Font.TIMES_BOLD);
        cell.setFontSize(7);

        //Add multiple rows with random facts about Belgium
        for (String[] fact : reqList) {

           /* if (y < 260) {
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
                cell = factHeaderrow.createCell(13, messageSource.getMessage("lbl.lab.code.mx", null, null));
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

                cell = factHeaderrow.createCell(11, messageSource.getMessage("lbl.sample.quality", null, null));
                cell.setFillColor(Color.lightGray);
                cell.setFont(PDType1Font.HELVETICA_BOLD);
                cell.setFontSize(10);

                cell = factHeaderrow.createCell(16, messageSource.getMessage("lbl.silais", null, null));
                cell.setFillColor(Color.lightGray);
                cell.setFont(PDType1Font.HELVETICA_BOLD);
                cell.setFontSize(10);

                cell = factHeaderrow.createCell(16, messageSource.getMessage("lbl.health.unit", null, null));
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


            }*/

            row = table.createRow(10);
            cell = row.createCell(20, fact[0]);
            cell.setFont(PDType1Font.TIMES_ROMAN);
            cell.setFontSize(7);
            y -= 15;

            for (int i = 1; i < fact.length; i++) {
                cell = row.createCell(20, fact[i]);
                cell.setFont(PDType1Font.TIMES_ROMAN);
                cell.setFontSize(7);


            }
        }
        table.draw();
    }


    private void drawTable1(List<String[]> reqList, PDDocument doc, PDPage page, float y) throws IOException {

        //drawTable

        //Initialize table
        float margin = 33;
        float tableWidth = 520;
        float yStartNewPage = y;
        float yStart = yStartNewPage;
        float bottomMargin = 45;
        BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true, true);

        //Create Header row
        Row headerRow = table.createRow(10f);
        table.setHeader(headerRow);

        //Create 2 column row

        Cell cell;
        Row row;


        //Create Fact header row
        Row factHeaderrow = table.createRow(10f);
        cell = factHeaderrow.createCell(20, messageSource.getMessage("lbl.test", null, null));
        cell.setFont(PDType1Font.TIMES_BOLD);
        cell.setFontSize(7);
        cell.setFillColor(Color.LIGHT_GRAY);

        cell = factHeaderrow.createCell(20, messageSource.getMessage("lbl.processing.datetime", null, null));
        cell.setFillColor(Color.lightGray);
        cell.setFont(PDType1Font.TIMES_BOLD);
        cell.setFontSize(7);

        cell = factHeaderrow.createCell(60, messageSource.getMessage("lbl.result", null, null));
        cell.setFillColor(Color.lightGray);
        cell.setFont(PDType1Font.TIMES_BOLD);
        cell.setFontSize(7);


        //Add multiple rows with random facts about Belgium
        for (String[] fact : reqList) {

           /* if (y < 260) {
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
                cell = factHeaderrow.createCell(13, messageSource.getMessage("lbl.lab.code.mx", null, null));
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

                cell = factHeaderrow.createCell(11, messageSource.getMessage("lbl.sample.quality", null, null));
                cell.setFillColor(Color.lightGray);
                cell.setFont(PDType1Font.HELVETICA_BOLD);
                cell.setFontSize(10);

                cell = factHeaderrow.createCell(16, messageSource.getMessage("lbl.silais", null, null));
                cell.setFillColor(Color.lightGray);
                cell.setFont(PDType1Font.HELVETICA_BOLD);
                cell.setFontSize(10);

                cell = factHeaderrow.createCell(16, messageSource.getMessage("lbl.health.unit", null, null));
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


            }*/

            row = table.createRow(10);
            cell = row.createCell(20, fact[0]);
            cell.setFont(PDType1Font.TIMES_ROMAN);
            cell.setFontSize(7);
            y -= 15;

            for (int i = 1; i < fact.length; i++) {

                if (i == 2) {
                    cell = row.createCell(60, fact[i]);
                    cell.setFont(PDType1Font.TIMES_ROMAN);
                    cell.setFontSize(7);
                } else {
                    cell = row.createCell(20, fact[i]);
                    cell.setFont(PDType1Font.TIMES_ROMAN);
                    cell.setFontSize(7);
                }


            }
        }
        table.draw();
    }
}
