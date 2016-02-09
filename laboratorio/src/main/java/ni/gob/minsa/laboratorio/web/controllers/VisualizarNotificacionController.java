package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.concepto.Catalogo_Lista;
import ni.gob.minsa.laboratorio.domain.estructura.EntidadesAdtvas;
import ni.gob.minsa.laboratorio.domain.irag.DaIrag;
import ni.gob.minsa.laboratorio.domain.irag.DaVacunasIrag;
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
    @Qualifier(value = "catalogosService")
    private CatalogoService catalogosService;

    @Autowired
    @Qualifier(value = "entidadAdmonService")
    private EntidadAdmonService entidadAdmonService;

    @Autowired
    @Qualifier(value = "recepcionMxService")
    private RecepcionMxService recepcionMxService;

    @Autowired
    @Qualifier(value = "tomaMxService")
    private TomaMxService tomaMxService;

    @Autowired
    @Qualifier(value = "ordenExamenMxService")
    private OrdenExamenMxService ordenExamenMxService;

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
    @Qualifier(value = "daVacunasIragService")
    public DaVacunasIragService daVacunasIragService;

    @Autowired
    @Qualifier(value = "daIragService")
    public DaIragService daIragService;

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
        String jsonResponse;
        Map<Integer, Object> mapResponse = new HashMap<>();
        Integer indice = 0;
        for (RecepcionMx recepcion : recepcionMxList) {
            boolean esEstudio = tomaMxService.getSolicitudesEstudioByIdTomaMx(recepcion.getTomaMx().getIdTomaMx()).size() > 0;
            Map<String, String> map = new HashMap<>();
            map.put("idNotificacion", recepcion.getTomaMx().getIdNotificacion().getIdNotificacion());
            map.put("codigoUnicoMx", esEstudio ? recepcion.getTomaMx().getCodigoUnicoMx() : recepcion.getTomaMx().getCodigoLab());
            map.put("idRecepcion", recepcion.getIdRecepcion());
            map.put("idTomaMx", recepcion.getTomaMx().getIdTomaMx());
            map.put("fechaTomaMx", DateUtil.DateToString(recepcion.getTomaMx().getFechaHTomaMx(), "dd/MM/yyyy")+
                    (recepcion.getTomaMx().getHoraTomaMx()!=null?" "+recepcion.getTomaMx().getHoraTomaMx():""));

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
                String nombreCompleto;
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


            Map<Integer, Object> mapDxList = new HashMap<>();
            Map<String, String> mapDx = new HashMap<>();
            int subIndice = 0;

            if (!solicitudDxList.isEmpty()) {
                for (DaSolicitudDx solicitudDx : solicitudDxList) {
                    mapDx.put("idSolicitud", solicitudDx.getIdSolicitudDx());
                    mapDx.put("nombre", solicitudDx.getCodDx().getNombre());
                    mapDx.put("fechaSolicitud", DateUtil.DateToString(solicitudDx.getFechaHSolicitud(), "dd/MM/yyyy hh:mm:ss a"));
                    subIndice++;
                    mapDxList.put(subIndice, mapDx);
                    mapDx = new HashMap<>();
                }
            } else {
                for (DaSolicitudEstudio solicitudEstudio : solicitudEList) {
                    mapDx.put("idSolicitud", solicitudEstudio.getIdSolicitudEstudio());
                    mapDx.put("nombre", solicitudEstudio.getTipoEstudio().getNombre());
                    mapDx.put("fechaSolicitud", DateUtil.DateToString(solicitudEstudio.getFechaHSolicitud(), "dd/MM/yyyy hh:mm:ss a"));
                    subIndice++;
                    mapDxList.put(subIndice, mapDx);
                    mapDx = new HashMap<>();
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
    String getPDF(@RequestParam(value = "idNotificacion", required = true) String idNotificacion) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PDDocument doc = new PDDocument();
        DaNotificacion not = daNotificacionService.getNotifById(idNotificacion);
        String res = null;
        if (not != null) {
            if (not.getCodTipoNotificacion().getCodigo().equals("TPNOTI|SINFEB")) {
                DaSindFebril febril = sindFebrilService.getDaSindFebril(idNotificacion);


                //String fechaImpresion = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());


                if (febril != null) {
                    PDPage page = new PDPage(PDPage.PAGE_SIZE_A4);
                    doc.addPage(page);
                    PDPageContentStream stream = new PDPageContentStream(doc, page);

                    String workingDir = System.getProperty("user.dir");

                    BufferedImage image = ImageIO.read(new File(workingDir + "/fichaFebril.png"));

                    GeneralUtils.drawObject(stream, doc, image, 20, 30, 545, 745);
                    String silais = febril.getIdNotificacion().getCodSilaisAtencion().getNombre();

                    String nombreS = silais != null ? silais.replace("SILAIS", "") : "----";
                    String municipio = febril.getIdNotificacion().getCodUnidadAtencion() != null ? febril.getIdNotificacion().getCodUnidadAtencion().getMunicipio().getNombre() : "----";
                    String us = febril.getIdNotificacion().getCodUnidadAtencion() != null ? febril.getIdNotificacion().getCodUnidadAtencion().getNombre() : "----";
                    String nExp = febril.getCodExpediente() != null ? febril.getCodExpediente() : "----------";
                    //laboratorio pendiente
                    String fecha = febril.getFechaFicha() != null ? DateUtil.DateToString(febril.getFechaFicha(), "yyyy/MM/dd") : null;
                    String[] array = fecha != null ? fecha.split("/") : null;

                    String dia = array != null ? array[2] : "--";
                    String mes = array != null ? array[1] : "--";
                    String anio = array != null ? array[0] : "--";
                    String nombrePersona;

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

                    String numFicha = febril.getNumFicha()!= null? febril.getNumFicha():null;

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
                   // boolean respiratorio = false;
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


                    float y = 668;
                    float m = 11;
                    float x = 86;
                    float x1 = 86;
                    float y3 = 0;

                    if(numFicha!= null){
                        GeneralUtils.drawTEXT(numFicha, y+18, x+405, stream,7, PDType1Font.TIMES_ROMAN);

                    }

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
                        x1 += 141;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.none", null, null), y, x1, stream, 7, PDType1Font.TIMES_ROMAN);
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
                    List<DaSolicitudEstudio> estudiosList = resultadoFinalService.getSolicitudesEByIdNotificacion(febril.getIdNotificacion().getIdNotificacion());

                    float y1 = 0;

                    if(!diagnosticosList.isEmpty() || !estudiosList.isEmpty()){
                        stream.close();
                        page = new PDPage(PDPage.PAGE_SIZE_A4);
                        doc.addPage(page);
                        stream = new PDPageContentStream(doc, page);

                        y = 770;
                        x1 = x -35;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.febril.lab.data", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);

                        y-=10;

                        if (!diagnosticosList.isEmpty()) {
                            int con = 0;
                            for (DaSolicitudDx soli : diagnosticosList) {
                                List<String[]> reqList = new ArrayList<>();
                                List<String[]> dxList = new ArrayList<>();
                                con++;
                                if (con >= 2) {
                                    y = y1;
                                }
                                String[] content = new String[5];
                                List<OrdenExamen> ordenes = ordenExamenMxService.getOrdenesExamenNoAnuladasByIdSolicitud(soli.getIdSolicitudDx());
                                List<DetalleResultadoFinal> resul = resultadoFinalService.getDetResActivosBySolicitud(soli.getIdSolicitudDx());

                                content[0] = soli.getCodDx().getNombre() != null ? soli.getCodDx().getNombre() : "";
                                content[1] = soli.getFechaHSolicitud() != null ? DateUtil.DateToString(soli.getFechaHSolicitud(), "dd/MM/yyyy HH:mm:ss") : "";
                                content[2] = soli.getIdTomaMx().getFechaHTomaMx() != null ?
                                        DateUtil.DateToString(soli.getIdTomaMx().getFechaHTomaMx(), "dd/MM/yyyy")+(soli.getIdTomaMx().getHoraTomaMx()!=null?" "+soli.getIdTomaMx().getHoraTomaMx():"")
                                        : "";
                                content[3] = soli.getIdTomaMx().getCodTipoMx() != null ? soli.getIdTomaMx().getCodTipoMx().getNombre() : "";

                                int cont = 0;
                                String rFinal = null;
                                //records request results
                                for (DetalleResultadoFinal det : resul) {
                                    cont++;
                                    //first record
                                    if (cont == 1) {
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

                                        //no first record
                                    } else {
                                            if (det.getRespuesta() != null) {
                                                if (det.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                                                    Catalogo_Lista valor = respuestasExamenService.getCatalogoListaConceptoByIdLista(Integer.valueOf(det.getValor()));
                                                    rFinal += "," + " "+ det.getRespuesta().getNombre() + ":" + " " + valor.getValor();

                                                } else {
                                                    rFinal += "," + " "+ det.getRespuesta().getNombre() + ":" + " " + det.getValor();
                                                }
                                            } else {
                                                if (det.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                                                    Catalogo_Lista valor = respuestasExamenService.getCatalogoListaConceptoByIdLista(Integer.valueOf(det.getValor()));
                                                    rFinal += "," + " "+ det.getRespuestaExamen().getNombre() + ":" + " " + valor.getValor();

                                                } else {
                                                    rFinal += "," + " "+ det.getRespuestaExamen().getNombre() + ":" + " " + det.getValor();
                                                }
                                            }
                                    }

                                }

                                content[4] = rFinal;
                                reqList.add(content);

                                if (!ordenes.isEmpty()) {

                                    String rExamen = null;
                                    String fechaProcesamiento = "";
                                    for (OrdenExamen ex : ordenes) {
                                        String[] examen = new String[3];
                                        List<DetalleResultado> results = resultadosService.getDetallesResultadoActivosByExamen(ex.getIdOrdenExamen());

                                        examen[0] = ex.getCodExamen() != null ? ex.getCodExamen().getNombre() : "";


                                        int contt = 0;
                                        //records tests results
                                        for (DetalleResultado resExamen : results) {
                                            contt++;
                                            //first record
                                            if (contt == 1) {
                                                    fechaProcesamiento = DateUtil.DateToString(resExamen.getFechahRegistro(), "dd/MM/yyyy HH:mm:ss");
                                                    if (resExamen.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                                                        Catalogo_Lista valor = respuestasExamenService.getCatalogoListaConceptoByIdLista(Integer.valueOf(resExamen.getValor()));
                                                        rExamen = resExamen.getRespuesta().getNombre() + ":" + " " + valor.getValor();

                                                    } else {
                                                        rExamen = resExamen.getRespuesta().getNombre() + ":" + " " + resExamen.getValor();
                                                    }

                                                //no first record
                                            } else {
                                                    if (resExamen.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                                                        Catalogo_Lista valor = respuestasExamenService.getCatalogoListaConceptoByIdLista(Integer.valueOf(resExamen.getValor()));
                                                        rExamen += " " + resExamen.getRespuesta().getNombre() + ":" + " " + valor.getValor();

                                                    } else {
                                                        rExamen += " " + resExamen.getRespuesta().getNombre() + ":" + " " + resExamen.getValor();
                                                    }

                                            }

                                        }
                                        examen[1] = fechaProcesamiento;
                                        examen[2] = rExamen != null ? rExamen : "";
                                        dxList.add(examen);

                                    }

                                }

                                float height1 =  drawTable(reqList, doc, page, y);
                                y-= height1;
                                float height2 = drawTable1(dxList,doc,page,y);
                                y1 = y - height2;
                                y3 = y1;

                            }

                        }

                        if(!estudiosList.isEmpty()){
                            int cn = 0;
                            for (DaSolicitudEstudio est : estudiosList) {
                                List<String[]> reqList1 = new ArrayList<>();
                                List<String[]> dxList1 = new ArrayList<>();
                                cn++;

                                if (cn >= 2) {
                                    y = y3;
                                } else {
                                    if (y3 != 0) {
                                        y = y3;
                                    } else {
                                        y = 760;
                                    }
                                }

                                String[] content1 = new String[5];
                                List<OrdenExamen> ordenes = ordenExamenMxService.getOrdenesExamenNoAnuladasByIdSolicitud(est.getIdSolicitudEstudio());
                                List<DetalleResultadoFinal> resul = resultadoFinalService.getDetResActivosBySolicitud(est.getIdSolicitudEstudio());

                                content1[0] = est.getTipoEstudio().getNombre() != null ? est.getTipoEstudio().getNombre() : "";
                                content1[1] = est.getFechaHSolicitud() != null ? DateUtil.DateToString(est.getFechaHSolicitud(), "dd/MM/yyyy HH:mm:ss") : "";
                                content1[2] = est.getIdTomaMx().getFechaHTomaMx() != null ?
                                        DateUtil.DateToString(est.getIdTomaMx().getFechaHTomaMx(), "dd/MM/yyyy")+(est.getIdTomaMx().getHoraTomaMx()!=null?" "+est.getIdTomaMx().getHoraTomaMx():"")
                                        : "";
                                content1[3] = est.getIdTomaMx().getCodTipoMx() != null ? est.getIdTomaMx().getCodTipoMx().getNombre() : "";

                                int cont1 = 0;
                                String rFinal = null;
                                //records request results
                                for (DetalleResultadoFinal det : resul) {
                                    cont1++;
                                    //first record
                                    if (cont1 == 1) {
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

                                        //no first record
                                    } else {
                                            if (det.getRespuesta() != null) {
                                                if (det.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                                                    Catalogo_Lista valor = respuestasExamenService.getCatalogoListaConceptoByIdLista(Integer.valueOf(det.getValor()));
                                                    rFinal += "," + " "+ det.getRespuesta().getNombre() + ":" + " " + valor.getValor();

                                                } else {
                                                    rFinal += "," + " "+ det.getRespuesta().getNombre() + ":" + " " + det.getValor();
                                                }
                                            } else {
                                                if (det.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                                                    Catalogo_Lista valor = respuestasExamenService.getCatalogoListaConceptoByIdLista(Integer.valueOf(det.getValor()));
                                                    rFinal += "," + " "+ det.getRespuestaExamen().getNombre() + ":" + " " + valor.getValor();

                                                } else {
                                                    rFinal += "," + " "+ det.getRespuestaExamen().getNombre() + ":" + " " + det.getValor();
                                                }
                                            }
                                    }

                                }

                                content1[4] = rFinal;
                                reqList1.add(content1);

                                if (!ordenes.isEmpty()) {

                                    String rExamen = null;
                                    String fechaProcesamiento = "";
                                    for (OrdenExamen ex : ordenes) {
                                        String[] examen1 = new String[3];
                                        List<DetalleResultado> results = resultadosService.getDetallesResultadoActivosByExamen(ex.getIdOrdenExamen());

                                        examen1[0] = ex.getCodExamen() != null ? ex.getCodExamen().getNombre() : "";

                                        int cont2 = 0;
                                        //records tests results
                                        for (DetalleResultado resExamen : results) {
                                            cont2++;
                                            //first record
                                            if (cont2 == 1) {
                                                    fechaProcesamiento = DateUtil.DateToString(resExamen.getFechahRegistro(), "dd/MM/yyyy HH:mm:ss");
                                                    if (resExamen.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                                                        Catalogo_Lista valor = respuestasExamenService.getCatalogoListaConceptoByIdLista(Integer.valueOf(resExamen.getValor()));
                                                        rExamen = resExamen.getRespuesta().getNombre() + ":" + " " + valor.getValor();

                                                    } else {
                                                        rExamen = resExamen.getRespuesta().getNombre() + ":" + " " + resExamen.getValor();
                                                    }

                                                //no first record
                                            } else {
                                                    if (resExamen.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                                                        Catalogo_Lista valor = respuestasExamenService.getCatalogoListaConceptoByIdLista(Integer.valueOf(resExamen.getValor()));
                                                        rExamen +=  ","+ " " + resExamen.getRespuesta().getNombre() + ":" + " " + valor.getValor();

                                                    } else {
                                                        rExamen += ","+ " " + resExamen.getRespuesta().getNombre() + ":" + " " + resExamen.getValor();
                                                    }


                                            }

                                        }
                                        examen1[1] = fechaProcesamiento;
                                        examen1[2] = rExamen != null ? rExamen : "";
                                        dxList1.add(examen1);


                                    }


                                }
                                float height1 =  drawTable(reqList1, doc, page, y);
                                y-= height1;
                                float height2 = drawTable1(dxList1,doc,page,y);
                                y3 = y - height2;


                            }
                        }


                        //dx final
                        y = y3 - 20;
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
            } else if (not.getCodTipoNotificacion().getCodigo().equals("TPNOTI|IRAG")) {
                DaIrag irag = daIragService.getFormById(not.getIdNotificacion());
               // String fechaImpresion = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());

                if (irag != null) {
                    PDPage page = new PDPage(PDPage.PAGE_SIZE_A4);
                    doc.addPage(page);
                    PDPageContentStream stream = new PDPageContentStream(doc, page);
                    String workingDir = System.getProperty("user.dir");

                    BufferedImage image = ImageIO.read(new File(workingDir + "/fichaIrag1.png"));

                    GeneralUtils.drawObject(stream, doc, image, 10, 50, 580, 780);

                    String clasificacion = irag.getCodClasificacion()!= null? irag.getCodClasificacion().getCodigo():null;

                    String us = irag.getIdNotificacion().getCodUnidadAtencion() != null ? irag.getIdNotificacion().getCodUnidadAtencion().getNombre() : "----";

                    //String silais = irag.getIdNotificacion().getCodSilaisAtencion().getNombre();

                   // String nombreS = silais != null ? silais.replace("SILAIS", "") : "----";
                   // String municipio = irag.getIdNotificacion().getPersona().getMunicipioResidencia() != null ? irag.getIdNotificacion().getPersona().getMunicipioResidencia().getNombre() : "----";
                    String nExp = irag.getCodExpediente() != null ? irag.getCodExpediente() : "----------";

                    String fecha = irag.getFechaConsulta() != null ? DateUtil.DateToString(irag.getFechaConsulta(), "yyyy/MM/dd") : null;
                    String[] array = fecha != null ? fecha.split("/") : null;

                    String dia = array != null ? array[2] : "--";
                    String mes = array != null ? array[1] : "--";
                    String anio = array != null ? array[0] : "--";


                    String fechaPrimera = irag.getFechaPrimeraConsulta() != null ? DateUtil.DateToString(irag.getFechaPrimeraConsulta(), "yyyy/MM/dd") : null;
                    String[] array1 = fechaPrimera != null ? fechaPrimera.split("/") : null;

                    String diaFP = array1 != null ? array1[2] : "--";
                    String mesFP = array1 != null ? array1[1] : "--";
                    String anioFP = array1 != null ? array1[0] : "--";

                    String nombrePersona;

                    nombrePersona = irag.getIdNotificacion().getPersona().getPrimerNombre();
                    if (irag.getIdNotificacion().getPersona().getSegundoNombre() != null)
                        nombrePersona = nombrePersona + " " + irag.getIdNotificacion().getPersona().getSegundoNombre();
                    nombrePersona = nombrePersona + " " + irag.getIdNotificacion().getPersona().getPrimerApellido();
                    if (irag.getIdNotificacion().getPersona().getSegundoApellido() != null)
                        nombrePersona = nombrePersona + " " + irag.getIdNotificacion().getPersona().getSegundoApellido();

                    String edad = null;
                    if (irag.getIdNotificacion().getPersona().getFechaNacimiento() != null && irag.getFechaConsulta() != null) {
                        edad = DateUtil.calcularEdad(irag.getIdNotificacion().getPersona().getFechaNacimiento(), irag.getFechaConsulta());
                    }

                    String[] edad1 = edad != null ? edad.split("/") : null;
                    String anios = edad1 != null ? edad1[0] : "--";
                    String meses = edad1 != null ? edad1[1] : "--";
                    String dias = edad1 != null ? edad1[2] : "--";

                    String fNac = irag.getIdNotificacion().getPersona().getFechaNacimiento() != null ? DateUtil.DateToString(irag.getIdNotificacion().getPersona().getFechaNacimiento(), "yyyy/MM/dd") : null;
                    String[] fechaNac = fNac != null ? fNac.split("/") : null;
                    String anioNac = fechaNac != null ? fechaNac[0] : "--";
                    String mesNac = fechaNac != null ? fechaNac[1] : "--";
                    String diaNac = fechaNac != null ? fechaNac[2] : "--";

                    String sexo = irag.getIdNotificacion().getPersona().getSexo() != null ? irag.getIdNotificacion().getPersona().getSexo().getValor() : null;

                    String tutor = irag.getNombreMadreTutor() != null ? irag.getNombreMadreTutor() : "----------";

                    String procedencia = irag.getCodProcedencia() != null ? irag.getCodProcedencia().getValor() : null;


                    String fis = irag.getIdNotificacion().getFechaInicioSintomas() != null ? DateUtil.DateToString(irag.getIdNotificacion().getFechaInicioSintomas(), "dd/MM/yyyy") : null;
                    String[] fechaFis = fis != null ? fis.split("/") : null;
                    String diaFis = fechaFis != null ? fechaFis[0] : "--";
                    String mesFis = fechaFis != null ? fechaFis[1] : "--";
                    String anioFis = fechaFis != null ? fechaFis[2] : "--";


                    String depProce = irag.getIdNotificacion().getPersona().getMunicipioResidencia() != null ? irag.getIdNotificacion().getPersona().getMunicipioResidencia().getDependencia().getNombre() : "----------";
                    String municProce = irag.getIdNotificacion().getPersona().getMunicipioResidencia() != null ? irag.getIdNotificacion().getPersona().getMunicipioResidencia().getNombre() : "----------";
                    String comunidadResidencia = irag.getIdNotificacion().getPersona().getComunidadResidencia() != null ? irag.getIdNotificacion().getPersona().getComunidadResidencia().getNombre() : "----------";
                    String direccionResidencia = irag.getIdNotificacion().getDireccionResidencia() != null ? irag.getIdNotificacion().getDireccionResidencia() : "----------";
                    String telefono = irag.getIdNotificacion().getPersona().getTelefonoResidencia() != null ? irag.getIdNotificacion().getPersona().getTelefonoResidencia() : "----------";
                    String captacion = irag.getCodCaptacion() != null ? irag.getCodCaptacion().getCodigo() : null;
                    String dxIngreso = irag.getDiagnostico() != null ? irag.getDiagnostico().getNombreCie10() : "----------";
                    Integer tarjetaVac = irag.getTarjetaVacuna() != null ? irag.getTarjetaVacuna() : null;

                    List<DaVacunasIrag> vacunas = daVacunasIragService.getAllVaccinesByIdIrag(irag.getIdNotificacion().getIdNotificacion());
                    String hib = null;
                    String influenza = null;
                    String menin = null;
                    String neumo = null;

                    for (DaVacunasIrag vac : vacunas) {

                        if (vac.getCodVacuna().getCodigo().equals("VAC|HIB")) {

                            hib = vac.getCodTipoVacuna() + "-" + vac.getDosis() + "-" + DateUtil.DateToString(vac.getFechaUltimaDosis(), "dd/MM/yyyy");

                        } else if (vac.getCodVacuna().getCodigo().equals("VAC|INFLU")) {

                            influenza = vac.getCodTipoVacuna() + "-" + vac.getDosis() + "-" + DateUtil.DateToString(vac.getFechaUltimaDosis(), "dd/MM/yyyy");

                        } else if (vac.getCodVacuna().getCodigo().equals("VAC|MEN")) {

                            menin = vac.getCodTipoVacuna() + "-" + vac.getDosis() + "-" + DateUtil.DateToString(vac.getFechaUltimaDosis(), "dd/MM/yyyy");

                        } else if (vac.getCodVacuna().getCodigo().equals("VAC|NEUM")) {

                            neumo = vac.getCodTipoVacuna() + "-" + vac.getDosis() + "-" + DateUtil.DateToString(vac.getFechaUltimaDosis(), "dd/MM/yyyy");

                        }

                    }

                    String condicPre = irag.getCondiciones() != null ? irag.getCondiciones() : null;

                    String mesesEmb = irag.getSemanasEmbarazo() != null ? irag.getSemanasEmbarazo().toString() : "--";

                    String otraCondicion = irag.getOtraCondicion() != null ? irag.getOtraCondicion() : "----------";

                    String manifestaciones = irag.getManifestaciones() != null ? irag.getManifestaciones() : null;

                    String otraManif = irag.getOtraManifestacion() != null ? irag.getOtraManifestacion() : "----------";

                    String usoAntib = irag.getCodAntbUlSem() != null ? irag.getCodAntbUlSem().getValor() : null;

                    String cantAntib = irag.getCantidadAntib() != null ? irag.getCantidadAntib().toString() : "--";

                    String nombreAntib = irag.getNombreAntibiotico() != null ? irag.getNombreAntibiotico() : "-----------";

                    Integer difDiasAntib = irag.getFechaPrimDosisAntib() != null && irag.getFechaUltDosisAntib() != null ? DateUtil.CalcularDiferenciaDiasFechas(irag.getFechaPrimDosisAntib(), irag.getFechaUltDosisAntib()) : 0;

                    String viaAntib = irag.getCodViaAntb() != null ? irag.getCodViaAntb().getValor() : null;

                    String fechaUltDosis = irag.getFechaUltDosisAntib() != null ? DateUtil.DateToString(irag.getFechaUltDosisAntib(), "dd/MM/yyyy") : "-----------";

                    String usoAntiv = irag.getUsoAntivirales() != null ? irag.getUsoAntivirales().getValor() : null;

                    String nombreAntiv = irag.getNombreAntiviral() != null ? irag.getNombreAntiviral() : "----------";

                    String fechaPDAntiv = irag.getFechaPrimDosisAntiviral() != null ? DateUtil.DateToString(irag.getFechaPrimDosisAntiviral(), "dd/MM/yyyy") : "----------";

                    String fechaUDAntiv = irag.getFechaUltDosisAntiviral() != null ? DateUtil.DateToString(irag.getFechaUltDosisAntiviral(), "dd/MM/yyyy") : "----------";

                    Integer dosisAntiv = irag.getNoDosisAntiviral() != null ? irag.getNoDosisAntiviral() : 0;

                    String radiologia = irag.getCodResRadiologia() != null ? irag.getCodResRadiologia() : null;

                    String otroResRad = irag.getOtroResultadoRadiologia() != null ? irag.getOtroResultadoRadiologia() : null;

                    String uci = irag.getUci() != null ? irag.getUci().toString() : null;

                    Integer diasUci = irag.getNoDiasHospitalizado() != null ? irag.getNoDiasHospitalizado() : 0;

                    String ventilacion = irag.getVentilacionAsistida() != null ? irag.getVentilacionAsistida().toString() : null;

                    String dxEgreso1 = irag.getDiagnostico1Egreso() != null ? irag.getDiagnostico1Egreso().getNombreCie10() : "----------";

                    String dxEgreso2 = irag.getDiagnostico2Egreso() != null ? irag.getDiagnostico2Egreso().getNombreCie10() : "----------";

                    String fechaEgreso = irag.getFechaEgreso() != null ? DateUtil.DateToString(irag.getFechaEgreso(), "dd/MM/yyyy") : "----------";

                    String condicionEgreso = irag.getCodCondEgreso() != null ? irag.getCodCondEgreso().getCodigo() : null;

                    String codEgreso1 = irag.getDiagnostico1Egreso() != null ? irag.getDiagnostico1Egreso().getCodigoCie10() : null;

                    String codEgreso2 = irag.getDiagnostico2Egreso() != null ? irag.getDiagnostico2Egreso().getCodigoCie10() : null;

                    String clasFinal = irag.getCodClasFCaso() != null ? irag.getCodClasFCaso() : null;

                    String nb = irag.getCodClasFDetalleNB() != null ? irag.getCodClasFDetalleNB().getCodigo() : null;

                    String nv = irag.getCodClasFDetalleNV() != null ? irag.getCodClasFDetalleNV().getCodigo() : null;

                    String etiologicoBacteriano = irag.getAgenteBacteriano() != null ? irag.getAgenteBacteriano() : null;

                    String etiologicoViral = irag.getAgenteViral() != null ? irag.getAgenteViral() : null;

                  //String agentesEt = irag.getAgenteEtiologico() != null ? irag.getAgenteEtiologico() : null;

                    String seroti = irag.getSerotipificacion() != null ? irag.getSerotipificacion() : null;

                    String fechaRegistro = irag.getFechaRegistro() != null ? DateUtil.DateToString(irag.getFechaRegistro(), "dd/MM/yyyy") : "------";

                    String nombreUsuario = irag.getUsuario() != null ? irag.getUsuario().getNombre() : null;

                    float y = 737;
                    float x = 86;
                    float x1 = 86;

                    if(clasificacion!= null){
                        switch (clasificacion) {
                            case "CLASIFVI|ETI":
                                x1 += 377;
                                GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y + 25, x1, stream, 8, PDType1Font.TIMES_BOLD);
                                break;
                            case "CLASIFVI|IRAG":
                                x1 += 336;
                                GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y + 25, x1, stream, 8, PDType1Font.TIMES_BOLD);

                                break;
                            case "CLASIFVI|INUS":
                                x1 += 463;
                                GeneralUtils.drawTEXT("(" + messageSource.getMessage("lbl.x", null, null) +")", y + 25, x1, stream, 8, PDType1Font.TIMES_BOLD);

                                break;
                        }
                    }

                    x1= x +55;
                    GeneralUtils.drawTEXT(us, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);

                    y -= 42;
                    x1 = x + 55;
                    GeneralUtils.drawTEXT(dia, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);
                    x1 = x1 + 20;
                    GeneralUtils.drawTEXT(mes, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);
                    x1 = x1 + 20;
                    GeneralUtils.drawTEXT(anio, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);

                    x1 = x + 188;
                    GeneralUtils.drawTEXT(diaFP, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);
                    x1 = x1 + 20;
                    GeneralUtils.drawTEXT(mesFP, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);
                    x1 = x1 + 23;
                    GeneralUtils.drawTEXT(anioFP, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);

                    x1 = x1 + 110;
                    GeneralUtils.drawTEXT(nExp, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);

                    y -= 20;
                    //menor o mayor de 5 años
                    if (anios != null) {
                        int edadAnios = Integer.parseInt(anios);
                        if (edadAnios > 5) {
                            x1 = x + 163;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);

                        } else {
                            x1 = x + 303;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 8, PDType1Font.TIMES_BOLD);
                        }
                    }

                    y -= 23;
                    x1 = x + 23;
                    GeneralUtils.drawTEXT(nombrePersona, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);


                    x1 = x1 + 265;
                    GeneralUtils.drawTEXT(tutor, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);


                    y -= 12;
                    if (sexo != null) {
                        if (sexo.equals("Hombre")) {
                            x1 = x + 24;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);
                        } else if (sexo.equals("Mujer")) {
                            x1 = x + 67;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }
                    }

                    x1 = x + 180;
                    GeneralUtils.drawTEXT(diaNac, y, x1, stream, 6, PDType1Font.TIMES_ROMAN);
                    x1 = x1 + 16;
                    GeneralUtils.drawTEXT(mesNac, y, x1, stream, 6, PDType1Font.TIMES_ROMAN);
                    x1 = x1 + 15;
                    GeneralUtils.drawTEXT(anioNac, y, x1, stream, 6, PDType1Font.TIMES_ROMAN);


                    if (anios != null && meses != null) {
                        int edadAnios = Integer.parseInt(anios);
                        int mesesEdad = Integer.parseInt(meses);

                        if (edadAnios < 1) {
                            x1 = x + 415;
                            GeneralUtils.drawTEXT(meses, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);
                        } else if (mesesEdad < 1) {
                            y -= 10;
                            x1 = x + 25;
                            GeneralUtils.drawTEXT(dias, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);
                        } else if (edadAnios > 1) {
                            x1 = x + 330;
                            GeneralUtils.drawTEXT(anios, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);
                        }

                    }

                    y -= 28;
                    x1 = x + 82;
                    GeneralUtils.drawTEXT(depProce, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);
                    x1 = x1 + 180;
                    GeneralUtils.drawTEXT(municProce, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);

                    y -= 9;
                    x1 = x + 38;
                    GeneralUtils.drawTEXT(direccionResidencia, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);

                    y -= 8;
                    x1 = x1 + 28;
                    GeneralUtils.drawTEXT(comunidadResidencia + " " + telefono, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);


                    if (procedencia != null) {
                        if (procedencia.equals("Urbano")) {
                            x1 = x + 334;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);
                        } else if (procedencia.equals("Rural")) {
                            x1 = x + 368;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);
                        }

                    }


                    y -= 9;
                    if (captacion != null) {
                        switch (captacion) {
                            case "CAPTAC|EMER":
                                x1 = x + 60;
                                GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                                break;
                            case "CAPTAC|SALA":
                                x1 = x + 98;
                                GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                                break;
                            case "CAPTAC|UCI":
                                x1 = x + 132;
                                GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                                break;
                            case "CAPTAC|AMB":
                                x1 = x + 65;
                                GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y - 8, x1, stream, 7, PDType1Font.TIMES_BOLD);

                                break;
                        }

                    }

                    y -= 8;
                    x1 = x + 230;
                    GeneralUtils.drawTEXT(dxIngreso, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);

                    y -= 16;
                    x1 = x + 110;
                    GeneralUtils.drawTEXT(diaFis, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);
                    x1 = x1 + 20;
                    GeneralUtils.drawTEXT(mesFis, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);
                    x1 = x1 + 16;
                    GeneralUtils.drawTEXT(anioFis, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);

                    y -= 18;
                    if (tarjetaVac != null) {
                        if (tarjetaVac == 0) {
                            x1 = x + 248;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        } else {
                            x1 = x + 287;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);
                        }
                    }


                    y -= 19;
                    if (hib != null) {
                        x1 = x + 60;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        String[] hib1 = hib.split("-");
                        x1 = x + 320;
                        GeneralUtils.drawTEXT(hib1[1], y, x1, stream, 7, PDType1Font.TIMES_ROMAN);

                        x1 = x + 368;
                        GeneralUtils.drawTEXT(hib1[2], y, x1, stream, 7, PDType1Font.TIMES_ROMAN);

                        if (hib.contains("TVAC|HIB1")) {
                            x1 = x + 153;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);
                        }

                    } else {
                        x1 = x + 85;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                    }

                    y -= 12;
                    if (menin != null) {
                        x1 = x + 60;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        String[] menin1 = menin.split("-");
                        x1 = x + 320;
                        GeneralUtils.drawTEXT(menin1[1], y, x1, stream, 7, PDType1Font.TIMES_ROMAN);

                        x1 = x + 368;
                        GeneralUtils.drawTEXT(menin1[2], y, x1, stream, 7, PDType1Font.TIMES_ROMAN);

                        if (menin.contains("TVAC|MENING1")) {
                            x1 = x + 153;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);
                        }

                        if (menin.contains("TVAC|MENING2")) {
                            x1 = x + 199;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }

                    } else {
                        x1 = x + 85;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                    }

                    y -= 14;
                    if (neumo != null) {
                        x1 = x + 60;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        String[] neumo1 = neumo.split("-");
                        x1 = x + 320;
                        GeneralUtils.drawTEXT(neumo1[1], y, x1, stream, 7, PDType1Font.TIMES_ROMAN);

                        x1 = x + 368;
                        GeneralUtils.drawTEXT(neumo1[2], y, x1, stream, 7, PDType1Font.TIMES_ROMAN);

                        if (neumo.contains("TVAC|NEUMO1")) {
                            x1 = x + 153;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y + 4, x1, stream, 7, PDType1Font.TIMES_BOLD);
                        }

                        if (neumo.contains("TVAC|NEUMO2")) {
                            x1 = x + 205;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y + 4, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }

                        if (neumo.contains("TVAC|NEUMO3")) {
                            x1 = x + 155;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y - 4, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }

                    } else {
                        x1 = x + 85;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                    }

                    y -= 14;
                    if (influenza != null) {
                        x1 = x + 60;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        String[] flu1 = influenza.split("-");
                        x1 = x + 320;
                        GeneralUtils.drawTEXT(flu1[1], y, x1, stream, 7, PDType1Font.TIMES_ROMAN);

                        x1 = x + 368;
                        GeneralUtils.drawTEXT(flu1[2], y, x1, stream, 7, PDType1Font.TIMES_ROMAN);

                        if (influenza.contains("TVAC|FLU1")) {
                            x1 = x + 153;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);
                        }

                        if (influenza.contains("TVAC|FLU2")) {
                            x1 = x + 199;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }

                        if (influenza.contains("TVAC|FLU3")) {
                            x1 = x + 235;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }

                    } else {
                        x1 = x + 85;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                    }


                    y -= 40;
                    if (condicPre != null) {
                        if (condicPre.contains("CONDPRE|CANC")) {
                            x1 = x + 79;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        } else {
                            x1 = x + 99;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }

                        if (condicPre.contains("CONDPRE|ENFCARD")) {
                            x1 = x + 225;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        } else {
                            x1 = x + 245;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }

                        if (condicPre.contains("CONDPRE|DESN")) {
                            x1 = x + 388;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        } else {
                            x1 = x + 408;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }


                        y -= 15;

                        if (condicPre.contains("CONDPRE|DIAB")) {
                            x1 = x + 79;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        } else {
                            x1 = x + 99;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }

                        if (condicPre.contains("CONDPRE|ASMA")) {
                            x1 = x + 225;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        } else {
                            x1 = x + 245;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }

                        if (condicPre.contains("CONDPRE|OBES")) {
                            x1 = x + 388;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        } else {
                            x1 = x + 408;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }


                        y -= 15;

                        if (condicPre.contains("CONDPRE|VIH")) {
                            x1 = x + 79;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        } else {
                            x1 = x + 99;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }

                        if (condicPre.contains("CONDPRE|EPOC")) {
                            x1 = x + 225;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        } else {
                            x1 = x + 245;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }

                        if (condicPre.contains("CONDPRE|EMB")) {
                            x1 = x + 388;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                            x1 = x + 350;
                            GeneralUtils.drawTEXT(mesesEmb, y + 4, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        } else {
                            x1 = x + 408;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }


                        y -= 13;

                        if (condicPre.contains("CONDPRE|OTINM")) {
                            x1 = x + 79;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        } else {
                            x1 = x + 99;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }

                        if (condicPre.contains("CONDPRE|OTENFPUL")) {
                            x1 = x + 225;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        } else {
                            x1 = x + 245;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }

                        if (condicPre.contains("CONDPRE|CORTIC")) {
                            x1 = x + 388;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        } else {
                            x1 = x + 408;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }


                        y -= 13;

                        if (condicPre.contains("CONDPRE|ENFNEU")) {
                            x1 = x + 79;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        } else {
                            x1 = x + 99;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }

                        if (condicPre.contains("CONDPRE|INSRENAL")) {
                            x1 = x + 225;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        } else {
                            x1 = x + 245;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }

                        if (condicPre.contains("CONDPRE|OTRA")) {
                            x1 = x + 388;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                            x1 = x + 300;
                            GeneralUtils.drawTEXT(otraCondicion, y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        } else {
                            x1 = x + 408;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }
                    }

                    y -= 40;
                    if (manifestaciones != null) {
                        if (manifestaciones.contains("MANCLIN|FIEB")) {
                            x1 = x + 79;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        } else {
                            x1 = x + 99;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }

                        if (manifestaciones.contains("MANCLIN|TIRAJS")) {
                            x1 = x + 225;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        } else {
                            x1 = x + 245;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }

                        if (manifestaciones.contains("MANCLIN|DLMUSC")) {
                            x1 = x + 388;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        } else {
                            x1 = x + 408;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }


                        y -= 13;

                        if (manifestaciones.contains("MANCLIN|DLGAR")) {
                            x1 = x + 79;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        } else {
                            x1 = x + 99;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }

                        if (manifestaciones.contains("MANCLIN|ESTRID")) {
                            x1 = x + 225;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        } else {
                            x1 = x + 245;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }

                        if (manifestaciones.contains("MANCLIN|EXANT")) {
                            x1 = x + 388;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        } else {
                            x1 = x + 408;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }


                        y -= 11;

                        if (manifestaciones.contains("MANCLIN|TOS")) {
                            x1 = x + 79;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        } else {
                            x1 = x + 99;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }

                        if (manifestaciones.contains("MANCLIN|VOMTS")) {
                            x1 = x + 225;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        } else {
                            x1 = x + 245;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }

                        if (manifestaciones.contains("MANCLIN|MALGRAL")) {
                            x1 = x + 388;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        } else {
                            x1 = x + 408;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }


                        y -= 11;

                        if (manifestaciones.contains("MANCLIN|ESTOR")) {
                            x1 = x + 79;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        } else {
                            x1 = x + 99;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }

                        if (manifestaciones.contains("MANCLIN|INTVO")) {
                            x1 = x + 225;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        } else {
                            x1 = x + 245;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }

                        if (manifestaciones.contains("MANCLIN|LETAR")) {
                            x1 = x + 388;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        } else {
                            x1 = x + 408;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }


                        y -= 9;

                        if (manifestaciones.contains("MANCLIN|SIBIL")) {
                            x1 = x + 79;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        } else {
                            x1 = x + 99;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }

                        if (manifestaciones.contains("MANCLIN|DIARREA")) {
                            x1 = x + 225;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        } else {
                            x1 = x + 245;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }

                        if (manifestaciones.contains("MANCLIN|CONV")) {
                            x1 = x + 388;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);


                        } else {
                            x1 = x + 408;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }


                        y -= 11;

                        if (manifestaciones.contains("MANCLIN|SECNAS")) {
                            x1 = x + 79;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        } else {
                            x1 = x + 99;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }

                        if (manifestaciones.contains("MANCLIN|DLCBZA")) {
                            x1 = x + 225;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        } else {
                            x1 = x + 245;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }

                        if (manifestaciones.contains("MANCLIN|OTRA")) {
                            x1 = x + 388;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                            x1 = x + 335;
                            GeneralUtils.drawTEXT(otraManif, y, x1, stream, 7, PDType1Font.TIMES_BOLD);


                        } else {
                            x1 = x + 408;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }

                        y -= 9;

                        if (manifestaciones.contains("MANCLIN|DIFRESP")) {
                            x1 = x + 79;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        } else {
                            x1 = x + 99;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }

                        if (manifestaciones.contains("MANCLIN|CONJUN")) {
                            x1 = x + 225;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        } else {
                            x1 = x + 245;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }

                        y -= 9;

                        if (manifestaciones.contains("MANCLIN|TAQPN")) {
                            x1 = x + 79;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        } else {
                            x1 = x + 99;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }

                        if (manifestaciones.contains("MANCLIN|DLABDM")) {
                            x1 = x + 225;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        } else {
                            x1 = x + 245;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }

                    }

                    y -= 18;
                    if (usoAntib != null) {
                        switch (usoAntib) {
                            case "Si":
                                x1 = x + 192;
                                GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                                x1 = x + 108;
                                GeneralUtils.drawTEXT(cantAntib, y - 13, x1, stream, 7, PDType1Font.TIMES_ROMAN);

                                x1 = x + 210;
                                GeneralUtils.drawTEXT(nombreAntib, y - 13, x1, stream, 7, PDType1Font.TIMES_ROMAN);

                                if (difDiasAntib != 0) {
                                    x1 = x + 17;
                                    GeneralUtils.drawTEXT(difDiasAntib.toString(), y - 26, x1, stream, 7, PDType1Font.TIMES_ROMAN);
                                }

                                if (viaAntib != null) {
                                    switch (viaAntib) {
                                        case "Oral":
                                            x1 = x + 63;
                                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y - 26, x1, stream, 7, PDType1Font.TIMES_BOLD);
                                            break;
                                        case "Parenteral":
                                            x1 = x + 117;
                                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y - 26, x1, stream, 7, PDType1Font.TIMES_BOLD);
                                            break;
                                        case "Ambas":
                                            x1 = x + 156;
                                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y - 26, x1, stream, 7, PDType1Font.TIMES_BOLD);
                                            break;
                                    }
                                }

                                x1 = x + 250;
                                GeneralUtils.drawTEXT(fechaUltDosis, y - 26, x1, stream, 7, PDType1Font.TIMES_ROMAN);


                                break;
                            case "No":
                                x1 = x + 230;
                                GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                                break;
                            case "No Sabe":
                                x1 = x + 295;
                                GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                                break;
                        }
                    }

                    y -= 38;
                    if (usoAntiv != null) {
                        switch (usoAntiv) {
                            case "Si":
                                x1 = x + 98;
                                GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                                x1 = x + 230;
                                GeneralUtils.drawTEXT(nombreAntiv, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);

                                x1 = x + 47;
                                GeneralUtils.drawTEXT(fechaPDAntiv, y - 12, x1, stream, 7, PDType1Font.TIMES_ROMAN);

                                x1 = x + 151;
                                GeneralUtils.drawTEXT(fechaUDAntiv, y - 12, x1, stream, 7, PDType1Font.TIMES_ROMAN);

                                if (dosisAntiv != 0) {
                                    x1 = x + 270;
                                    GeneralUtils.drawTEXT(dosisAntiv.toString(), y - 12, x1, stream, 7, PDType1Font.TIMES_ROMAN);

                                }


                                break;
                            case "No":
                                x1 = x + 126;
                                GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                                break;

                        }
                    }

                    y -= 47;
                    if (radiologia != null) {
                        if (radiologia.contains("RESRAD|CONS")) {
                            x1 = x + 79;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }

                        if (radiologia.contains("RESRAD|DERR")) {
                            x1 = x + 173;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }

                        if (radiologia.contains("RESRAD|PAMIX")) {
                            x1 = x + 285;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }

                        if (radiologia.contains("RESRAD|PAINT")) {
                            x1 = x + 355;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }

                        if (radiologia.contains("RESRAD|AIRE")) {
                            x1 = x + 430;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }

                        if (otroResRad != null) {
                            x1 = x + 30;
                            GeneralUtils.drawTEXT(otroResRad, y - 10, x1, stream, 7, PDType1Font.TIMES_ROMAN);

                        }


                    }

                    stream.close();
                    page = new PDPage(PDPage.PAGE_SIZE_A4);
                    doc.addPage(page);
                    stream = new PDPageContentStream(doc, page);

                    y = 770;
                    x1 = 65;
                    GeneralUtils.drawTEXT(messageSource.getMessage("lbl.irag.lab.data", null, null), y, x1, stream, 9, PDType1Font.TIMES_BOLD);
                    y -= 10;

                    //load all the request by notification

                    List<DaSolicitudDx> diagnosticosList = resultadoFinalService.getSolicitudesDxByIdNotificacion(irag.getIdNotificacion().getIdNotificacion());

                    if (diagnosticosList.isEmpty()) {
                        x1 = 75;
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.nothing.to.show", null, null), y, x1, stream, 7, PDType1Font.TIMES_ROMAN);
                    }


                    float y1 = 0;
                    BufferedImage image2 = ImageIO.read(new File(workingDir + "/fichaIrag2.png"));
                    //records request results
                    if (!diagnosticosList.isEmpty()) {
                        int con = 0;
                        for (DaSolicitudDx soli : diagnosticosList) {
                            List<String[]> reqList = new ArrayList<>();
                            List<String[]> dxList = new ArrayList<>();
                            con++;
                            if (con >= 2) {
                                y = y1;
                            }
                            String[] content = new String[5];
                            List<OrdenExamen> ordenes = ordenExamenMxService.getOrdenesExamenNoAnuladasByIdSolicitud(soli.getIdSolicitudDx());
                            List<DetalleResultadoFinal> resul = resultadoFinalService.getDetResActivosBySolicitud(soli.getIdSolicitudDx());

                            content[0] = soli.getCodDx().getNombre() != null ? soli.getCodDx().getNombre() : "";
                            content[1] = soli.getFechaHSolicitud() != null ? DateUtil.DateToString(soli.getFechaHSolicitud(), "dd/MM/yyyy HH:mm:ss") : "";
                            content[2] = soli.getIdTomaMx().getFechaHTomaMx() != null ?
                                    DateUtil.DateToString(soli.getIdTomaMx().getFechaHTomaMx(), "dd/MM/yyyy")+(soli.getIdTomaMx().getHoraTomaMx()!=null?" "+soli.getIdTomaMx().getHoraTomaMx():"")
                                    : "";
                            content[3] = soli.getIdTomaMx().getCodTipoMx() != null ? soli.getIdTomaMx().getCodTipoMx().getNombre() : "";

                            int cont = 0;
                            String rFinal = null;
                            for (DetalleResultadoFinal det : resul) {
                                cont++;
                                //first record
                                if (cont == 1) {
                                    //single record result
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

                                    //no first record
                                } else {
                                        if (det.getRespuesta() != null) {
                                            if (det.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                                                Catalogo_Lista valor = respuestasExamenService.getCatalogoListaConceptoByIdLista(Integer.valueOf(det.getValor()));
                                                rFinal += ","+ " " +det.getRespuesta().getNombre() + ":" + " " + valor.getValor();

                                            } else {
                                                rFinal += ","+ " " + det.getRespuesta().getNombre() + ":" + " " + det.getValor();
                                            }
                                        } else {
                                            if (det.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                                                Catalogo_Lista valor = respuestasExamenService.getCatalogoListaConceptoByIdLista(Integer.valueOf(det.getValor()));
                                                rFinal += ","+ " " + det.getRespuestaExamen().getNombre() + ":" + " " + valor.getValor();

                                            } else {
                                                rFinal += ","+ " " + det.getRespuestaExamen().getNombre() + ":" + " " + det.getValor();
                                            }
                                        }
                            }

                            }

                            content[4] = rFinal;
                            reqList.add(content);


                            if (!ordenes.isEmpty()) {

                                String rExamen = null;
                                String fechaProcesamiento = "";
                                for (OrdenExamen ex : ordenes) {
                                    String[] examen = new String[3];
                                    List<DetalleResultado> results = resultadosService.getDetallesResultadoActivosByExamen(ex.getIdOrdenExamen());

                                    examen[0] = ex.getCodExamen() != null ? ex.getCodExamen().getNombre() : "";


                                    int cont1 = 0;
                                    //records tests results
                                    for (DetalleResultado resExamen : results) {
                                        cont1++;
                                        //first record
                                        if (cont1 == 1) {
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
                                                    rExamen += "," + " " + resExamen.getRespuesta().getNombre() + ":" + " " + valor.getValor();

                                                } else {
                                                    rExamen += "," + " " + resExamen.getRespuesta().getNombre() + ":" + " " + resExamen.getValor();
                                                }
                                        }

                                    }
                                    examen[1] = fechaProcesamiento;
                                    examen[2] = rExamen != null ? rExamen : "";
                                    dxList.add(examen);


                                }


                            }
                            float height1 = drawRequestTable(reqList, doc, page, y);
                            y -= height1;
                            float height2 = drawTestTable(dxList, doc, page, y);
                            y1 = y - height2;

                        }
                    }


                    if (y1 == 0) {
                        y1 = 610;
                        GeneralUtils.drawObject(stream, doc, image2, 10, y1, 580, 140);
                        y1 += 150;

                    } else {
                        GeneralUtils.drawObject(stream, doc, image2, 10, y1 - 150, 580, 140);
                    }

                    y = y1 - 32;
                    if (uci != null) {
                        if (uci.equals("1")) {
                            x1 = x + 181;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                            if (diasUci != 0) {
                                x1 = x + 232;
                                GeneralUtils.drawTEXT(diasUci.toString(), y + 2, x1, stream, 7, PDType1Font.TIMES_ROMAN);
                            }

                            if (ventilacion != null) {
                                if (ventilacion.equals("1")) {
                                    x1 = x + 338;
                                    GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                                } else {
                                    x1 = x + 362;
                                    GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);
                                }
                            }

                            if (codEgreso1 != null) {
                                x1 = x + 87;
                                GeneralUtils.drawTEXT(codEgreso1, y - 7, x1, stream, 5, PDType1Font.TIMES_ROMAN);
                                x1 = x + 109;
                                GeneralUtils.drawTEXT(dxEgreso1, y - 7, x1, stream, 5, PDType1Font.TIMES_ROMAN);
                            }

                            if (codEgreso2 != null) {
                                x1 = x + 240;
                                GeneralUtils.drawTEXT(codEgreso2, y - 7, x1, stream, 5, PDType1Font.TIMES_ROMAN);
                                x1 = x + 264;
                                GeneralUtils.drawTEXT(dxEgreso2, y - 7, x1, stream, 5, PDType1Font.TIMES_ROMAN);
                            }

                            x1 = x + 35;
                            GeneralUtils.drawTEXT(fechaEgreso, y - 17, x1, stream, 5, PDType1Font.TIMES_ROMAN);

                            if (condicionEgreso != null) {
                                switch (condicionEgreso) {
                                    case "CONEGRE|ALTA":
                                        x1 = x + 129;
                                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y - 30, x1, stream, 7, PDType1Font.TIMES_BOLD);
                                        break;
                                    case "CONEGRE|FUGA":
                                        x1 = x + 206;
                                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y - 30, x1, stream, 7, PDType1Font.TIMES_BOLD);
                                        break;
                                    case "CONEGRE|REF":
                                        x1 = x + 302;
                                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y - 30, x1, stream, 7, PDType1Font.TIMES_BOLD);

                                        break;
                                    case "CONEGRE|FALL":
                                        x1 = x + 348;
                                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y - 30, x1, stream, 7, PDType1Font.TIMES_BOLD);
                                        break;
                                }

                            }


                        } else {
                            x1 = x + 256;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                        }
                    }

                    y -= 57;
                    if (clasFinal != null) {
                        if (clasFinal.contains("CLASFI|INAD")) {
                            x1 = x + 76;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);
                        }

                        if (clasFinal.contains("CLASFI|NV")) {
                            x1 = x + 192;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                            if (nv != null) {
                                if (nv.equals("CLASFNV|CONF")) {
                                    x1 = x + 30;
                                    GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y - 20, x1, stream, 7, PDType1Font.TIMES_BOLD);

                                    if (etiologicoViral != null) {
                                        x1 = x + 112;
                                        GeneralUtils.drawTEXT(etiologicoViral, y - 18, x1, stream, 7, PDType1Font.TIMES_ROMAN);
                                    }
                                }

                                if (nv.equals("CLASFNV|DESC")) {
                                    x1 = x + 28;
                                    GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y - 26, x1, stream, 7, PDType1Font.TIMES_BOLD);

                                }
                            }
                        }

                        if (clasFinal.contains("CLASFI|NB")) {
                            x1 = x + 133;
                            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y, x1, stream, 7, PDType1Font.TIMES_BOLD);

                            if (nb != null) {
                                if (nb.equals("CLASFNB|CONF")) {
                                    x1 = x + 30;
                                    GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y - 7, x1, stream, 7, PDType1Font.TIMES_BOLD);

                                    if (etiologicoViral != null) {
                                        x1 = x + 129;
                                        GeneralUtils.drawTEXT(etiologicoBacteriano, y - 5, x1, stream, 7, PDType1Font.TIMES_ROMAN);
                                    }

                                    if (seroti != null) {
                                        x1 = x + 300;
                                        GeneralUtils.drawTEXT(seroti, y - 5, x1, stream, 7, PDType1Font.TIMES_ROMAN);
                                    }
                                }

                                if (nb.equals("CLASFNB|DESC")) {
                                    x1 = x + 30;
                                    GeneralUtils.drawTEXT(messageSource.getMessage("lbl.x", null, null), y - 14, x1, stream, 7, PDType1Font.TIMES_BOLD);
                                }
                            }

                        }
                    }

                    y -= 54;

                    if (nombreUsuario != null) {
                        x1 = x + 75;
                        GeneralUtils.drawTEXT(nombreUsuario, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);
                    }
                    x1 = x + 290;
                    GeneralUtils.drawTEXT(fechaRegistro, y, x1, stream, 7, PDType1Font.TIMES_ROMAN);


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

    private float drawTable(List<String[]> reqList, PDDocument doc, PDPage page, float y) throws IOException {

        //drawTable

        //Initialize table
        float height;
        float margin = 33;
        float tableWidth = 520;
        float bottomMargin = 45;
        BaseTable table = new BaseTable(y, y, bottomMargin, tableWidth, margin, doc, page, true, true);

        //Create Header row
        Row headerRow = table.createRow(10f);
        table.setHeader(headerRow);

        //Create 2 column row

        Cell cell;
        Row row;


        //Create Fact header row
        Row factHeaderrow = table.createRow(10f);
        cell = factHeaderrow.createCell(20, messageSource.getMessage("lbl.request", null, null));
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

        height = factHeaderrow.getHeight();

        //Add multiple rows with random facts about Belgium
        for (String[] fact : reqList) {
            row = table.createRow(10);

            for (String aFact : fact) {
                cell = row.createCell(20, aFact);
                cell.setFont(PDType1Font.TIMES_ROMAN);
                cell.setFontSize(7);

            }
            height += row.getHeight();
        }
        table.draw();
        return height;
    }


    private float drawTable1(List<String[]> reqList, PDDocument doc, PDPage page, float y) throws IOException {

        //drawTable

        //Initialize table
        float height;
        float margin = 33;
        float tableWidth = 520;
        float bottomMargin = 45;
        BaseTable table = new BaseTable(y, y, bottomMargin, tableWidth, margin, doc, page, true, true);

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

        height = factHeaderrow.getHeight();


        //Add multiple rows with random facts about Belgium
        for (String[] fact : reqList) {

            row = table.createRow(10);


            for (int i = 0; i < fact.length; i++) {

                switch (i) {
                    case 2: {
                        cell = row.createCell(60, fact[i]);
                        cell.setFont(PDType1Font.TIMES_ROMAN);
                        cell.setFontSize(7);
                        break;
                    }
                    default: {
                        cell = row.createCell(20, fact[i]);
                        cell.setFont(PDType1Font.TIMES_ROMAN);
                        cell.setFontSize(7);
                    }

                }


            }
            height += row.getHeight();
        }
        table.draw();
        return height;
    }

    private float drawRequestTable(List<String[]> reqList, PDDocument doc, PDPage page, float y) throws IOException {

        //drawRequestTable

        //Initialize table
        float height;
        float margin = 60;
        float tableWidth = 470;
        float bottomMargin = 45;
        BaseTable table = new BaseTable(y, y, bottomMargin, tableWidth, margin, doc, page, true, true);

        //Create Header row
        Row headerRow = table.createRow(10f);
        table.setHeader(headerRow);

        //Create 2 column row

        Cell cell;
        Row row;


        //Create Fact header row
        Row factHeaderrow = table.createRow(10f);
        cell = factHeaderrow.createCell(20, messageSource.getMessage("lbl.request.large", null, null));
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

        height = factHeaderrow.getHeight();

        //Add multiple rows with random facts about Belgium
        for (String[] fact : reqList) {

            row = table.createRow(10);


            for (String aFact : fact) {
                cell = row.createCell(20, aFact);
                cell.setFont(PDType1Font.TIMES_ROMAN);
                cell.setFontSize(7);

            }
            height += row.getHeight();
        }
        table.draw();
        return height;
    }


    private float drawTestTable(List<String[]> reqList, PDDocument doc, PDPage page, float y) throws IOException {

        //drawRequestTable

        //Initialize table
        float height;
        float margin = 60;
        float tableWidth = 470;
        float bottomMargin = 45;
        BaseTable table = new BaseTable(y, y, bottomMargin, tableWidth, margin, doc, page, true, true);

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

        height = factHeaderrow.getHeight();


        //Add multiple rows with random facts about Belgium
        for (String[] fact : reqList) {
            row = table.createRow(10);


            for (int i = 0; i < fact.length; i++) {

                switch (i) {
                    case 2: {
                        cell = row.createCell(60, fact[i]);
                        cell.setFont(PDType1Font.TIMES_ROMAN);
                        cell.setFontSize(7);
                        break;
                    }
                    default: {
                        cell = row.createCell(20, fact[i]);
                        cell.setFont(PDType1Font.TIMES_ROMAN);
                        cell.setFontSize(7);
                    }

                }


            }
            height += row.getHeight();
        }
        table.draw();
        return height;
    }


}
