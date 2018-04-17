package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.concepto.Catalogo_Lista;
import ni.gob.minsa.laboratorio.domain.estructura.EntidadesAdtvas;
import ni.gob.minsa.laboratorio.domain.muestra.*;
import ni.gob.minsa.laboratorio.domain.muestra.traslado.TrasladoMx;
import ni.gob.minsa.laboratorio.domain.notificacion.DaNotificacion;
import ni.gob.minsa.laboratorio.domain.notificacion.TipoNotificacion;
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
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;


/**
 * Created by souyen-ics.
 */
@Controller
@RequestMapping("gestion")
public class GestionController {

    private static final Logger logger = LoggerFactory.getLogger(GestionController.class);

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
    @Qualifier(value = "recepcionMxService")
    private RecepcionMxService recepcionMxService;

    @Autowired
    @Qualifier(value = "tomaMxService")
    private TomaMxService tomaMxService;

    @Autowired
    @Qualifier(value = "resultadoFinalService")
    private ResultadoFinalService resultadoFinalService;

    @Autowired
    @Qualifier(value = "trasladosService")
    private TrasladosService trasladosService;

    @Autowired
    @Qualifier(value = "daNotificacionService")
    private DaNotificacionService daNotificacionService;

    @Resource(name = "catalogosService")
    public CatalogoService catalogoService;

    @Autowired
    MessageSource messageSource;

    String nombreSoli = null;
    String nombrePersona = null;
    String nombreSilais = null;
    String nombreUS = null;
    Integer edad = null;
    String sexo = null;
    String fechaRecepcion = null;
    String fechaToma = null;
    String fis = null;
    String fechaResultado = null;
    String fechaAprobacion = null;
    List<DetalleResultadoFinal> detalleResultado = null;
    String fechaImpresion = null;
    String orderSample = null;


    /**
     * M�todo que se llama al entrar a la opci�n de menu "Gestión/Muestras". Se encarga de inicializar las listas para realizar la búsqueda de Mx
     *
     * @param request para obtener informaci�n de la petici�n del cliente
     * @return ModelAndView
     * @throws Exception
     */
    @RequestMapping(value = "initsample", method = RequestMethod.GET)
    public ModelAndView initSearchForm(HttpServletRequest request) throws Exception {
        logger.debug("Buscar Mx");
        String urlValidacion;
        try {
            urlValidacion = seguridadService.validarLogin(request);
            //si la url esta vacia significa que la validaci�n del login fue exitosa
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
            List<TipoNotificacion> tiposNotificacion = new ArrayList<TipoNotificacion>();
            tiposNotificacion.add(catalogoService.getTipoNotificacion("TPNOTI|SINFEB"));
            tiposNotificacion.add(catalogoService.getTipoNotificacion("TPNOTI|IRAG"));

            mav.addObject("entidades", entidadesAdtvases);
            mav.addObject("tipoMuestra", tipoMxList);
            mav.addObject("notificaciones",tiposNotificacion);
            mav.addObject("nivelCentral", seguridadService.esUsuarioNivelCentral(seguridadService.obtenerNombreUsuario()));
            mav.setViewName("gestion/searchMx");
        } else
            mav.setViewName(urlValidacion);

        return mav;
    }

    /**
     * M�todo que se llama al entrar a la opci�n de menu "Gestión/Muestras". Se encarga de inicializar las listas para realizar la búsqueda de Mx
     *
     * @param request para obtener informaci�n de la petici�n del cliente
     * @return ModelAndView
     * @throws Exception
     */
    @RequestMapping(value = "initnoti", method = RequestMethod.GET)
    public ModelAndView initnotiSearchForm(HttpServletRequest request) throws Exception {
        logger.debug("Buscar Mx");
        String urlValidacion;
        try {
            urlValidacion = seguridadService.validarLogin(request);
            //si la url esta vacia significa que la validaci�n del login fue exitosa
            if (urlValidacion.isEmpty())
                urlValidacion = seguridadService.validarAutorizacionUsuario(request, ConstantsSecurity.SYSTEM_CODE, false);
        } catch (Exception e) {
            e.printStackTrace();
            urlValidacion = "404";
        }
        ModelAndView mav = new ModelAndView();
        if (urlValidacion.isEmpty()) {
            List<EntidadesAdtvas> entidadesAdtvases = entidadAdmonService.getAllEntidadesAdtvas();
            List<TipoNotificacion> tiposNotificacion = new ArrayList<TipoNotificacion>();
            tiposNotificacion.add(catalogoService.getTipoNotificacion("TPNOTI|SINFEB"));
            tiposNotificacion.add(catalogoService.getTipoNotificacion("TPNOTI|IRAG"));

            mav.addObject("entidades", entidadesAdtvases);
            mav.addObject("notificaciones",tiposNotificacion);
            mav.addObject("nivelCentral", seguridadService.esUsuarioNivelCentral(seguridadService.obtenerNombreUsuario()));
            mav.setViewName("gestion/searchNotification");
        } else
            mav.setViewName(urlValidacion);

        return mav;
    }

    /**
     * M�todo para realizar la b�squeda de Mx para recepcionar en Mx Vigilancia general
     *
     * @param filtro JSon con los datos de los filtros a aplicar en la b�squeda(Nombre Apellido, Rango Fec Toma Mx, Tipo Mx, SILAIS, unidad salud)
     * @return String con las Mx encontradas
     * @throws Exception
     */
    @RequestMapping(value = "searchMx", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    String fetchOrdersJson(@RequestParam(value = "strFilter", required = true) String filtro) throws Exception {
        logger.info("Obteniendo las mx según filtros en JSON");
        FiltroMx filtroMx = jsonToFiltroMx(filtro);
        List<DaTomaMx> tomaMxList = tomaMxService.getTomaMxByFiltro(filtroMx);
        return tomaMxToJson(tomaMxList);
    }

    /**
     * M�todo para convertir estructura Json que se recibe desde el cliente a FiltroMx para realizar b�squeda de Mx(Vigilancia) y Recepci�n Mx(Laboratorio)
     *
     * @param strJson String con la informaci�n de los filtros
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
        Boolean solicitudAprobada = null;
        Date fechaInicioNoti = null;
        Date fechaFinNoti = null;
        String tipoNotificacion = null;

        if (jObjectFiltro.get("nombreApellido") != null && !jObjectFiltro.get("nombreApellido").getAsString().isEmpty())
            nombreApellido = jObjectFiltro.get("nombreApellido").getAsString();
        if (jObjectFiltro.get("fechaInicioTomaMx") != null && !jObjectFiltro.get("fechaInicioTomaMx").getAsString().isEmpty())
            fechaInicioTomaMx = DateUtil.StringToDate(jObjectFiltro.get("fechaInicioTomaMx").getAsString() + " 00:00:00");
        if (jObjectFiltro.get("fechaFinTomaMx") != null && !jObjectFiltro.get("fechaFinTomaMx").getAsString().isEmpty())
            fechaFinTomaMx = DateUtil.StringToDate(jObjectFiltro.get("fechaFinTomaMx").getAsString() + " 23:59:59");
        if (jObjectFiltro.get("fechaInicioRecep") != null && !jObjectFiltro.get("fechaInicioRecep").getAsString().isEmpty())
            fechaInicioRecep = DateUtil.StringToDate(jObjectFiltro.get("fechaInicioRecep").getAsString() + " 00:00:00");
        if (jObjectFiltro.get("fechaFinRecepcion") != null && !jObjectFiltro.get("fechaFinRecepcion").getAsString().isEmpty())
            fechaFinRecep = DateUtil.StringToDate(jObjectFiltro.get("fechaFinRecepcion").getAsString() + " 23:59:59");
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
        if (jObjectFiltro.get("solicitudAprobada") != null && !jObjectFiltro.get("solicitudAprobada").getAsString().isEmpty())
            solicitudAprobada = jObjectFiltro.get("solicitudAprobada").getAsBoolean();
        if (jObjectFiltro.get("fechaInicioNoti") != null && !jObjectFiltro.get("fechaInicioNoti").getAsString().isEmpty())
            fechaInicioNoti = DateUtil.StringToDate(jObjectFiltro.get("fechaInicioNoti").getAsString() + " 00:00:00");
        if (jObjectFiltro.get("fechaFinNoti") != null && !jObjectFiltro.get("fechaFinNoti").getAsString().isEmpty())
            fechaFinNoti = DateUtil.StringToDate(jObjectFiltro.get("fechaFinNoti").getAsString() + " 23:59:59");
        if (jObjectFiltro.get("tipoNotificacion") != null && !jObjectFiltro.get("tipoNotificacion").getAsString().isEmpty())
            tipoNotificacion = jObjectFiltro.get("tipoNotificacion").getAsString();

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
        filtroMx.setSolicitudAprobada(solicitudAprobada);
        filtroMx.setIncluirTraslados(true);
        filtroMx.setIncluirAnuladas(true); //en esta pantalla de gestión si mostrar muestras anuladas
        filtroMx.setFechaInicioNotificacion(fechaInicioNoti);
        filtroMx.setFechaFinNotificacion(fechaFinNoti);
        filtroMx.setTipoNotificacion(tipoNotificacion);

        return filtroMx;
    }

    /**
     * M�todo que convierte una lista de tomaMx a un string con estructura Json
     *
     * @param tomaMxList lista con las tomaMx a convertir
     * @return String
     */
    private String tomaMxToJson(List<DaTomaMx> tomaMxList) {
        String jsonResponse;
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice = 0;
        boolean esEstudio;
        //Laboratorio laboratorioUsuario = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
        for (DaTomaMx tomaMx : tomaMxList) {
            esEstudio = tomaMxService.getSolicitudesEstudioByIdTomaMx(tomaMx.getIdTomaMx()).size() > 0;
            Map<String, String> map = new HashMap<String, String>();
            map.put("idTomaMx", tomaMx.getIdTomaMx());
            map.put("codigoUnicoMx", esEstudio?tomaMx.getCodigoUnicoMx():(tomaMx.getCodigoLab()!=null?tomaMx.getCodigoLab():(messageSource.getMessage("lbl.not.generated", null, null))));
            map.put("fechaTomaMx", DateUtil.DateToString(tomaMx.getFechaHTomaMx(), "dd/MM/yyyy")+
                    (tomaMx.getHoraTomaMx()!=null?" "+tomaMx.getHoraTomaMx():""));
            map.put("fechaAnulacion", (tomaMx.getFechaAnulacion()!=null ? DateUtil.DateToString(tomaMx.getFechaAnulacion(), "dd/MM/yyyy") : ""));
            map.put("anulada", (tomaMx.isAnulada()? "1": "0"));
            if (tomaMx.getIdNotificacion().getCodSilaisAtencion() != null) {
                map.put("codSilais", tomaMx.getIdNotificacion().getCodSilaisAtencion().getNombre());
            } else {
                map.put("codSilais", "");
            }
            if (tomaMx.getIdNotificacion().getCodUnidadAtencion() != null) {
                map.put("codUnidadSalud", tomaMx.getIdNotificacion().getCodUnidadAtencion().getNombre());
            } else {
                map.put("codUnidadSalud", "");
            }
            map.put("fechaNotificacion", DateUtil.DateToString(tomaMx.getIdNotificacion().getFechaRegistro(), "dd/MM/yyyy"));
            //laboratorio y area
             if(tomaMx.getEstadoMx().getCodigo().equals("ESTDMX|ENV")){
                //map.put("area", (messageSource.getMessage("lbl.not.received", null, null)));
                map.put("laboratorio", (messageSource.getMessage("lbl.not.received", null, null)));
            }else{
                //Search transferred assets
                DaSolicitudEstudio estudio = tomaMxService.getSoliEstByCodigo(tomaMx.getCodigoUnicoMx());
                RecepcionMx lastRecepcion = recepcionMxService.getMaxRecepcionMxByCodUnicoMx(tomaMx.getCodigoUnicoMx());
                TrasladoMx traslado = trasladosService.getTrasladoActivoMx(tomaMx.getIdTomaMx());

                if (estudio != null) {
                    //map.put("area", estudio.getTipoEstudio().getArea().getNombre());
                    map.put("laboratorio", estudio.getIdTomaMx().getEnvio().getLaboratorioDestino().getNombre());
                } else {
                    //asset transfers
                    if (traslado != null) {
                        //CC
                        if (traslado.isTrasladoExterno()) {
                            List<DaSolicitudDx> soliPriori = tomaMxService.getSoliDxPrioridadByTomaAndLab(tomaMx.getIdTomaMx(), traslado.getLaboratorioDestino().getCodigo());

                            map.put("laboratorio", traslado.getLaboratorioDestino().getNombre());
                            //map.put("area", soliPriori.get(0).getCodDx().getArea().getNombre());

                        } else {
                            //Intern
                            /*Laboratorio lab = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
                            if(lab != null){
                                List<DaSolicitudDx> soli = tomaMxService.getSoliDxPrioridadByTomaAndLab(tomaMx.getIdTomaMx(), lab.getCodigo());

                                if(soli != null){
                                    map.put("area", soli.get(0).getCodDx().getArea().getNombre());
                                }
                            }*/

                            if (lastRecepcion != null) {
                                map.put("laboratorio", lastRecepcion.getLabRecepcion().getNombre());
                            }
                        }
                    } else {
                        if (lastRecepcion != null) {
                            map.put("laboratorio", lastRecepcion.getLabRecepcion().getNombre());
                        } else {
                            map.put("laboratorio", "");
                        }

                        /*DaSolicitudDx soli = tomaMxService.getMaxSoliByToma(tomaMx.getIdTomaMx());

                        if (soli != null) {

                            map.put("area", soli.getCodDx().getArea().getNombre());
                        } else {
                            map.put("area", "");
                        }*/
                    }
                }
            }

            map.put("tipoMuestra", tomaMx.getCodTipoMx().getNombre());
            map.put("estadoMx", tomaMx.getEstadoMx().getValor());

            //Si hay persona
            if (tomaMx.getIdNotificacion().getPersona() != null) {
                /// se obtiene el nombre de la persona asociada a la ficha
                String nombreCompleto = "";
                nombreCompleto = tomaMx.getIdNotificacion().getPersona().getPrimerNombre();
                if (tomaMx.getIdNotificacion().getPersona().getSegundoNombre() != null)
                    nombreCompleto = nombreCompleto + " " + tomaMx.getIdNotificacion().getPersona().getSegundoNombre();
                nombreCompleto = nombreCompleto + " " + tomaMx.getIdNotificacion().getPersona().getPrimerApellido();
                if (tomaMx.getIdNotificacion().getPersona().getSegundoApellido() != null)
                    nombreCompleto = nombreCompleto + " " + tomaMx.getIdNotificacion().getPersona().getSegundoApellido();
                map.put("persona", nombreCompleto);
            } else if (tomaMx.getIdNotificacion().getSolicitante() != null) {
                map.put("persona", tomaMx.getIdNotificacion().getSolicitante().getNombre());
            } else {
                map.put("persona", " ");
            }

            //se arma estructura de diagn�sticos o estudios
            Laboratorio labUser = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
            List<DaSolicitudDx> solicitudDxList = tomaMxService.getSolicitudesDxByIdToma(tomaMx.getIdTomaMx(), labUser.getCodigo());
            List<DaSolicitudEstudio> solicitudEList = tomaMxService.getSolicitudesEstudioByIdTomaMx(tomaMx.getIdTomaMx());

            Map<Integer, Object> mapDxList = new HashMap<Integer, Object>();
            Map<String, String> mapDx = new HashMap<String, String>();
            int subIndice = 0;

            if (!solicitudDxList.isEmpty()) {
                for (DaSolicitudDx solicitudDx : solicitudDxList) {
                    mapDx.put("idSolicitud", solicitudDx.getIdSolicitudDx());
                    mapDx.put("nombre", solicitudDx.getCodDx().getNombre());
                    mapDx.put("fechaSolicitud", DateUtil.DateToString(solicitudDx.getFechaHSolicitud(), "dd/MM/yyyy hh:mm:ss a"));
                    List<DetalleResultadoFinal> detRes = resultadoFinalService.getDetResActivosBySolicitud(solicitudDx.getIdSolicitudDx());

                    if (solicitudDx.getAprobada() != null) {
                        if (solicitudDx.getAprobada().equals(true)) {
                            mapDx.put("estado", (messageSource.getMessage("lbl.approval.result", null, null)));
                            mapDx.put("fechaAprobacion", DateUtil.DateToString(solicitudDx.getFechaAprobacion(), "dd/MM/yyyy"));
                        } else {
                            if (!detRes.isEmpty()) {
                                mapDx.put("estado", (messageSource.getMessage("lbl.result.pending.approval", null, null)));
                            } else {
                                mapDx.put("estado", (messageSource.getMessage("lbl.without.result", null, null)));
                            }
                            mapDx.put("fechaAprobacion", "");
                        }
                    } else {
                        if (!detRes.isEmpty()) {
                            mapDx.put("estado", (messageSource.getMessage("lbl.result.pending.approval", null, null)));
                        } else {
                            mapDx.put("estado", (messageSource.getMessage("lbl.without.result", null, null)));
                        }
                        mapDx.put("fechaAprobacion", "");
                    }

                    if(solicitudDx.getControlCalidad().equals(true)){
                        mapDx.put("cc", (messageSource.getMessage("lbl.yes", null, null)));

                    }else{
                        mapDx.put("cc", (messageSource.getMessage("lbl.no", null, null)));

                    }


                    subIndice++;
                    mapDxList.put(subIndice, mapDx);
                    mapDx = new HashMap<String, String>();
                }
            } else {
                for (DaSolicitudEstudio solicitudEstudio : solicitudEList) {
                    mapDx.put("idSolicitud", solicitudEstudio.getIdSolicitudEstudio());
                    mapDx.put("nombre", solicitudEstudio.getTipoEstudio().getNombre());
                    mapDx.put("fechaSolicitud", DateUtil.DateToString(solicitudEstudio.getFechaHSolicitud(), "dd/MM/yyyy hh:mm:ss a"));
                    List<DetalleResultadoFinal> detRes = resultadoFinalService.getDetResActivosBySolicitud(solicitudEstudio.getIdSolicitudEstudio());

                    if (solicitudEstudio.getAprobada() != null) {
                        if (solicitudEstudio.getAprobada().equals(true)) {
                            mapDx.put("estado", (messageSource.getMessage("lbl.approval.result", null, null)));
                        } else {
                            mapDx.put("estado", (messageSource.getMessage("lbl.without.result", null, null)));
                        }
                    } else {
                        if (!detRes.isEmpty()) {
                            mapDx.put("estado", (messageSource.getMessage("lbl.result.pending.approval", null, null)));
                        } else {
                            mapDx.put("estado", (messageSource.getMessage("lbl.without.result", null, null)));
                        }
                    }

                    mapDx.put("cc", (messageSource.getMessage("lbl.not.apply", null, null)));


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
        //escapar caracteres especiales, escape de los caracteres con valor num�rico mayor a 127
        UnicodeEscaper escaper = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }

  //MOVER MUESTRAS DE NOTIFICACIONES
    @RequestMapping(value = "getNotificationsMoveMx", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String getNotificationsMoveMx(@RequestParam(value = "strFilter", required = true) String filtro, HttpServletRequest request) throws Exception{
        logger.info("Obteniendo las ordenes de examen pendienetes según filtros en JSON");
        FiltroMx filtroMx = jsonToFiltroMx(filtro);
        List<DaNotificacion> notificacionList = daNotificacionService.getNoticesByFilro(filtroMx);
        return notificacionesToJson(notificacionList, false);
    }

    @RequestMapping(value = "moveMx", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void moveSample(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        String idNotificacion="";
        String codigoMx="";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            idNotificacion = jsonpObject.get("idNotificacion").getAsString();
            codigoMx = jsonpObject.get("codigoMx").getAsString();
            DaTomaMx tomaMx = tomaMxService.getTomaMxByCodLab(codigoMx);
            if (tomaMx!=null) {
                DaNotificacion notificacion = daNotificacionService.getNotifById(idNotificacion);
                if (notificacion!=null){
                    tomaMx.setIdNotificacion(notificacion);
                    tomaMxService.updateTomaMx(tomaMx);
                }else{
                    resultado =  messageSource.getMessage("msg.notification.notfound",null,null);
                }
            }else{
                resultado =  messageSource.getMessage("msg.tomamx.notfound",null,null);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.move.sample.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();
        }finally {
            UnicodeEscaper escaper     = UnicodeEscaper.above(127);
            Map<String, String> map = new HashMap<String, String>();
            map.put("idNotificacion",idNotificacion);
            map.put("codigoMx",codigoMx);
            map.put("mensaje",escaper.translate(resultado));
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = "override", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void anularMuestra(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        String codigoMx=null;
        String causaAnulacion = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            codigoMx = jsonpObject.get("codigoMx").getAsString();
            causaAnulacion = jsonpObject.get("causaAnulacion").getAsString();
            DaTomaMx tomaMx = tomaMxService.getTomaMxByCodLab(codigoMx);
            if (tomaMx!=null) {
                tomaMx.setAnulada(true);
                tomaMx.setFechaAnulacion(new Timestamp(new Date().getTime()));
                tomaMxService.updateTomaMx(tomaMx);
                List<DaSolicitudDx> solicitudDxList = tomaMxService.getSolicitudesDxPrioridadByIdToma(tomaMx.getIdTomaMx());
                for(DaSolicitudDx solicitudDx:solicitudDxList){
                    tomaMxService.bajaSolicitudDx(seguridadService.obtenerNombreUsuario(), solicitudDx.getIdSolicitudDx(), causaAnulacion);
                }

            }else{
                throw new Exception(messageSource.getMessage("msg.tomamx.notfound",null,null));
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.override.tomamx.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();
        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("codigoMx",String.valueOf(codigoMx));
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = "getNotifications", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String getNotifications(@RequestParam(value = "strFilter", required = true) String filtro, HttpServletRequest request) throws Exception{
        logger.info("Obteniendo las ordenes de examen pendienetes según filtros en JSON");
        FiltroMx filtroMx = jsonToFiltroMx(filtro);
        List<DaNotificacion> notificacionList = daNotificacionService.getNoticesByFilro(filtroMx);
        return notificacionesToJson(notificacionList, true);
    }

    @RequestMapping(value = "overridenoti", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void anularNotificacion(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        String idNotificacion=null;
        String causaAnulacion = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            idNotificacion = jsonpObject.get("idNotificacion").getAsString();
            causaAnulacion = jsonpObject.get("causaAnulacion").getAsString();
            DaNotificacion noti = daNotificacionService.getNotifById(idNotificacion);
            if (noti != null) {
                noti.setPasivo(true);
                noti.setFechaAnulacion(new Timestamp(new Date().getTime()));
                daNotificacionService.updateNotificacion(noti);
                List<DaTomaMx> tomasMx = tomaMxService.getTomaMxActivaByIdNoti(idNotificacion);
                for(DaTomaMx tomaMx : tomasMx) {
                    tomaMx.setAnulada(true);
                    tomaMx.setFechaAnulacion(new Timestamp(new Date().getTime()));
                    tomaMxService.updateTomaMx(tomaMx);
                    List<DaSolicitudDx> solicitudDxList = tomaMxService.getSolicitudesDxPrioridadByIdToma(tomaMx.getIdTomaMx());
                    for (DaSolicitudDx solicitudDx : solicitudDxList) {
                        tomaMxService.bajaSolicitudDx(seguridadService.obtenerNombreUsuario(), solicitudDx.getIdSolicitudDx(), causaAnulacion);
                    }
                }
            }else {
                throw new Exception(messageSource.getMessage("msg.notification.notfound", null, null));
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.override.noti.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();
        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("idNotificacion",idNotificacion);
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }
    /**
     * Convierte una lista de notificaciones a formato JSON
     * @param notificacions lista de nofiticaciones
     * @return JSON
     */
    private String notificacionesToJson(List<DaNotificacion> notificacions, boolean incluirMx){
        String jsonResponse="";
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice=0;
        for(DaNotificacion notificacion : notificacions) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("idNotificacion", notificacion.getIdNotificacion());
            if (notificacion.getFechaInicioSintomas() != null)
                map.put("fechaInicioSintomas", DateUtil.DateToString(notificacion.getFechaInicioSintomas(), "dd/MM/yyyy"));
            else
                map.put("fechaInicioSintomas", " ");
            map.put("codtipoNoti", notificacion.getCodTipoNotificacion().getCodigo());
            map.put("tipoNoti", notificacion.getCodTipoNotificacion().getValor());
            map.put("fechaRegistro", DateUtil.DateToString(notificacion.getFechaRegistro(), "dd/MM/yyyy"));
            map.put("silais", (notificacion.getCodSilaisAtencion() != null ? notificacion.getCodSilaisAtencion().getNombre() : ""));
            map.put("unidad", (notificacion.getCodUnidadAtencion() != null ? notificacion.getCodUnidadAtencion().getNombre() : ""));
            //Si hay persona
            if (notificacion.getPersona() != null) {
                /// se obtiene el nombre de la persona asociada a la ficha
                String nombreCompleto = "";
                nombreCompleto = notificacion.getPersona().getPrimerNombre();
                if (notificacion.getPersona().getSegundoNombre() != null)
                    nombreCompleto = nombreCompleto + " " + notificacion.getPersona().getSegundoNombre();
                nombreCompleto = nombreCompleto + " " + notificacion.getPersona().getPrimerApellido();
                if (notificacion.getPersona().getSegundoApellido() != null)
                    nombreCompleto = nombreCompleto + " " + notificacion.getPersona().getSegundoApellido();
                map.put("persona", nombreCompleto);
                //Se calcula la edad
                int edad = DateUtil.calcularEdadAnios(notificacion.getPersona().getFechaNacimiento());
                map.put("edad", String.valueOf(edad));
                //se obtiene el sexo
                map.put("sexo", notificacion.getPersona().getSexo().getValor());
                if (notificacion.getPersona().getMunicipioResidencia() != null) {
                    map.put("municipio", notificacion.getPersona().getMunicipioResidencia().getNombre());
                } else {
                    map.put("municipio", "--");
                }
            } else {
                map.put("persona", " ");
                map.put("edad", " ");
                map.put("sexo", " ");
                map.put("embarazada", "--");
                map.put("municipio", "");
            }

            if (incluirMx){
                int subIndice = 0;
                String username = seguridadService.obtenerNombreUsuario();
                Laboratorio labUser = seguridadService.getLaboratorioUsuario(username);
                boolean nivelCentral = seguridadService.esUsuarioNivelCentral(username);
                List<DaSolicitudDx> dxList = tomaMxService.getSolicitudesDxByIdNoti(notificacion.getIdNotificacion(), labUser.getCodigo(), nivelCentral);
                List<DaSolicitudEstudio> estudioList = tomaMxService.getSolicitudesEstudioByIdNoti(notificacion.getIdNotificacion());
                Map<Integer, Object> mapSolicList = new HashMap<Integer, Object>();
                if(dxList != null){
                    for(DaSolicitudDx diagnostico : dxList){
                        Map<String, String> mapSolicDx = new HashMap<String, String>();
                        mapSolicDx.put("codigoUnicoMx", (diagnostico.getIdTomaMx().getCodigoLab()!=null ? diagnostico.getIdTomaMx().getCodigoLab() : messageSource.getMessage("lbl.undefined", null, null)));
                        mapSolicDx.put("fechaTomaMx",DateUtil.DateToString(diagnostico.getIdTomaMx().getFechaHTomaMx(),"dd/MM/yyyy")+
                                (diagnostico.getIdTomaMx().getHoraTomaMx()!=null?" "+diagnostico.getIdTomaMx().getHoraTomaMx():""));
                        mapSolicDx.put("diagnostico", diagnostico.getCodDx().getNombre());

                        List<DetalleResultadoFinal> resFinal = resultadoFinalService.getDetResActivosBySolicitud(diagnostico.getIdSolicitudDx());
                        if (!resFinal.isEmpty()) {
                            mapSolicDx.put("resultadoS", messageSource.getMessage("lbl.yes", null, null));
                        } else {
                            mapSolicDx.put("resultadoS", messageSource.getMessage("lbl.no", null, null));
                        }
                        mapSolicDx.put("detResultado",parseResultDetails(resFinal));
                        subIndice ++;
                        mapSolicList.put(subIndice, mapSolicDx);
                    }
                }

                if(estudioList != null){
                    for(DaSolicitudEstudio estudio : estudioList){
                        boolean agregar = true;
                        //para estudios cohorte dengue y clinico dengue, sólo tomar en cuenta la muestra inicial
                        if (estudio.getTipoEstudio().getIdEstudio() == 1 || estudio.getTipoEstudio().getIdEstudio() == 2){
                            String codigoUnicoMx = estudio.getIdTomaMx().getCodigoUnicoMx();
                            String inicial = codigoUnicoMx.substring(codigoUnicoMx.lastIndexOf('.') + 1, codigoUnicoMx.length());
                            agregar = inicial.equals("1");
                        }
                        if (agregar) {
                            Map<String, String> mapSoliEst = new HashMap<String, String>();
                            mapSoliEst.put("codigoUnicoMx", estudio.getIdTomaMx().getCodigoUnicoMx());
                            mapSoliEst.put("fechaTomaMx", DateUtil.DateToString(estudio.getIdTomaMx().getFechaHTomaMx(), "dd/MM/yyyy")+
                                    (estudio.getIdTomaMx().getHoraTomaMx()!=null?" "+estudio.getIdTomaMx().getHoraTomaMx():""));
                            mapSoliEst.put("diagnostico", estudio.getTipoEstudio().getNombre());

                            List<DetalleResultadoFinal> resFinal = resultadoFinalService.getDetResActivosBySolicitud(estudio.getIdSolicitudEstudio());
                            if (!resFinal.isEmpty()) {
                                mapSoliEst.put("resultadoS", messageSource.getMessage("lbl.yes", null, null));
                            } else {
                                mapSoliEst.put("resultadoS", messageSource.getMessage("lbl.no", null, null));
                            }
                            mapSoliEst.put("detResultado",parseResultDetails(resFinal));
                            subIndice++;
                            mapSolicList.put(subIndice, mapSoliEst);
                        }
                    }
                }

                map.put("solicitudes", new Gson().toJson(mapSolicList));
            }
            mapResponse.put(indice, map);
            indice++;

        }
        jsonResponse = new Gson().toJson(mapResponse);
        UnicodeEscaper escaper     = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }

    private String parseResultDetails(List<DetalleResultadoFinal> resultList){
        String resultados="";
        for(DetalleResultadoFinal res: resultList){
            if (res.getRespuesta()!=null) {
                resultados+=(resultados.isEmpty()?res.getRespuesta().getNombre():", "+res.getRespuesta().getNombre());
                if (res.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                    Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(res.getValor());
                    resultados+=": "+cat_lista.getValor();
                }else if (res.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                    String valorBoleano = (Boolean.valueOf(res.getValor())?"lbl.yes":"lbl.no");
                    resultados+=": "+valorBoleano;
                } else {
                    resultados+=": "+res.getValor();
                }
            }else if (res.getRespuestaExamen()!=null){
                resultados+=(resultados.isEmpty()?res.getRespuestaExamen().getNombre():", "+res.getRespuestaExamen().getNombre());
                if (res.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                    Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(res.getValor());
                    resultados+=": "+cat_lista.getValor();
                } else if (res.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                    String valorBoleano = (Boolean.valueOf(res.getValor())?"lbl.yes":"lbl.no");
                    resultados+=": "+valorBoleano;
                }else {
                    resultados+=": "+res.getValor();
                }
            }
        }
        return resultados;
    }

}
