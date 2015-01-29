package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.estructura.EntidadesAdtvas;
import ni.gob.minsa.laboratorio.domain.estructura.Unidades;
import ni.gob.minsa.laboratorio.domain.examen.CatalogoExamenes;
import ni.gob.minsa.laboratorio.domain.examen.Examen_Dx;
import ni.gob.minsa.laboratorio.domain.muestra.*;
import ni.gob.minsa.laboratorio.domain.parametros.Parametro;
import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
import ni.gob.minsa.laboratorio.service.*;
import ni.gob.minsa.laboratorio.utilities.ConstantsSecurity;
import ni.gob.minsa.laboratorio.utilities.DateUtil;
import ni.gob.minsa.laboratorio.utilities.StringUtil;
import ni.gob.minsa.laboratorio.utilities.enumeration.HealthUnitType;
import org.apache.commons.lang3.text.translate.UnicodeEscaper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Miguel Salinas on 12/10/2014.
 * V 1.0
 */
@Controller
@RequestMapping("recepcionMx")
public class RecepcionMxController {
    private static final Logger logger = LoggerFactory.getLogger(RecepcionMxController.class);
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
    MessageSource messageSource;

    /**
     * Método que se llama al entrar a la opción de menu "Recepción Mx Vigilancia". Se encarga de inicializar las listas para realizar la búsqueda de envios de Mx
     * @param request para obtener información de la petición del cliente
     * @return ModelAndView
     * @throws Exception
     */
    @RequestMapping(value = "init", method = RequestMethod.GET)
    public ModelAndView initSearchForm(HttpServletRequest request) throws Exception {
        logger.debug("buscar ordenes para ordenExamen");
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
            mav.setViewName("recepcionMx/searchOrders");
        }else
            mav.setViewName(urlValidacion);

        return mav;
    }

    /**
     * Método que se llama al entrar a la opción de menu "Recepción Mx Laboratorio". Se encarga de inicializar las listas para realizar la búsqueda de envios de Mx
     * @param request para obtener información de la petición del cliente
     * @return ModelAndView
     * @throws Exception
     */
    @RequestMapping(value = "initLab", method = RequestMethod.GET)
    public ModelAndView initSearchLabForm(HttpServletRequest request) throws Exception {
        logger.debug("buscar ordenes para ordenExamen");
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
            mav.setViewName("recepcionMx/searchOrdersLab");
        }else
            mav.setViewName(urlValidacion);

        return mav;
    }

    /***
     * Método que se llama para crear una Recepción Mx Vigilancia. Setea los datos de la Muestra e inicializa listas y demas controles
     * @param request para obtener información de la petición del cliente
     * @param strIdOrden Id de la toma de Muestra a recepcionar
     * @return ModelAndView
     * @throws Exception
     */
    @RequestMapping(value = "create/{strIdOrden}", method = RequestMethod.GET)
    public ModelAndView createReceiptForm(HttpServletRequest request, @PathVariable("strIdOrden")  String strIdOrden) throws Exception {
        logger.debug("buscar ordenes para ordenExamen");
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
            DaTomaMx tomaMx = tomaMxService.getTomaMxById(strIdOrden);
            List<EntidadesAdtvas> entidadesAdtvases =  entidadAdmonService.getAllEntidadesAdtvas();
            List<TipoMx> tipoMxList = catalogosService.getTipoMuestra();
            List<Laboratorio> laboratorioList = laboratoriosService.getLaboratoriosInternos();
            List<Unidades> unidades = null;
            List<DaSolicitudDx> solicitudDxList = tomaMxService.getSolicitudesDxByMx(tomaMx.getIdTomaMx());
            Date fechaInicioSintomas = null;
            if (tomaMx.getIdNotificacion()!=null) {
                unidades = unidadesService.getPrimaryUnitsBySilais(tomaMx.getIdNotificacion().getCodSilaisAtencion().getCodigo(), HealthUnitType.UnidadesPrimHosp.getDiscriminator().split(","));
                fechaInicioSintomas = tomaMxService.getFechaInicioSintomas(tomaMx.getIdNotificacion().getIdNotificacion());
            }
            String html = "";
            //si hay fecha de inicio de síntomas validar si es muestra válida para vigilancia rutinaria
            if (fechaInicioSintomas!=null){
                Parametro diasMinRecepMx = parametrosService.getParametroByName("DIAS_MIN_MX_VIG_RUT");
                int diffDias = DateUtil.CalcularDiferenciaDiasFechas(fechaInicioSintomas,new Date());
                if (diffDias < Integer.valueOf(diasMinRecepMx.getValor())){
                    html = messageSource.getMessage("msg.mx.must.be.inadequate",null,null).replace("{0}",diasMinRecepMx.getValor()); //"La cantidad de días desde el inicio de síntomas no es mayor o igual a "+diasMinRecepMx.getValor()+", la muestra debería marcarse como inadecuada";
                }
            }
            mav.addObject("tomaMx",tomaMx);
            mav.addObject("entidades",entidadesAdtvases);
            mav.addObject("unidades",unidades);
            mav.addObject("tipoMuestra", tipoMxList);
            mav.addObject("laboratorios",laboratorioList);
            mav.addObject("fechaInicioSintomas",fechaInicioSintomas);
            mav.addObject("inadecuada",html);
            mav.addObject("dxList",solicitudDxList);
            mav.setViewName("recepcionMx/recepcionarOrders");
        }else
            mav.setViewName(urlValidacion);

        return mav;
    }

    /**
     * Método que se llama para crear una Recepción Mx en el Laboratorio. Setea los datos de la recepción e inicializa listas y demas controles.
     * Además si es la primera vez que se carga el registro se registran ordenes de examen para los examenes configurados por defecto en la tabla
     * de parámetros según el tipo de notificación, tipo de mx, tipo dx
     * @param request para obtener información de la petición del cliente
     * @param strIdRecepcion Id de la recepción general a recepcionar en el laboratorio
     * @return ModelAndView
     * @throws Exception
     */
    @RequestMapping(value = "createLab/{strIdRecepcion}", method = RequestMethod.GET)
    public ModelAndView createReceiptLabForm(HttpServletRequest request, @PathVariable("strIdRecepcion")  String strIdRecepcion) throws Exception {
        logger.debug("buscar ordenes para ordenExamen");
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
            RecepcionMx recepcionMx = recepcionMxService.getRecepcionMx(strIdRecepcion);
            List<EntidadesAdtvas> entidadesAdtvases =  entidadAdmonService.getAllEntidadesAdtvas();
            List<TipoMx> tipoMxList = catalogosService.getTipoMuestra();
            List<Laboratorio> laboratorioList = laboratoriosService.getLaboratoriosInternos();
            List<CalidadMx> calidadMx= catalogosService.getCalidadesMx();
            //List<TipoTubo> tipoTubos = catalogosService.getTipoTubos();
            List<Unidades> unidades = null;
            List<Examen_Dx> examenesList = null;
            List<OrdenExamen> ordenExamenList;
            Date fechaInicioSintomas = null;
            if (recepcionMx!=null) {
                unidades = unidadesService.getPrimaryUnitsBySilais(recepcionMx.getTomaMx().getIdNotificacion().getCodSilaisAtencion().getCodigo(), HealthUnitType.UnidadesPrimHosp.getDiscriminator().split(","));
                fechaInicioSintomas = tomaMxService.getFechaInicioSintomas(recepcionMx.getTomaMx().getIdNotificacion().getIdNotificacion());
                ordenExamenList = ordenExamenMxService.getOrdenesExamenByIdMx(recepcionMx.getTomaMx().getIdTomaMx());
                if (ordenExamenList==null || ordenExamenList.size()<=0) {
                    //se obtiene la lista de examenes por defecto para dx según el tipo de notificación configurado en tabla de parámetros. Se pasa como paràmetro el codigo del tipo de notificación
                    Parametro pTipoNoti = parametrosService.getParametroByName(recepcionMx.getTomaMx().getIdNotificacion().getCodTipoNotificacion().getCodigo());
                    if (pTipoNoti != null) {
                        //se obtienen los id de los examenes por defecto
                        Parametro pExamenesDefecto = parametrosService.getParametroByName(pTipoNoti.getValor());
                        if (pExamenesDefecto != null)
                            examenesList = examenesService.getExamenesByIds(pExamenesDefecto.getValor());
                        if (examenesList != null) {
                            //se registran los examenes por defecto
                            for (Examen_Dx examenTmp : examenesList) {
                                OrdenExamen ordenExamen = new OrdenExamen();
                                DaSolicitudDx solicitudDx = tomaMxService.getSolicitudesDxByMxDx(recepcionMx.getTomaMx().getIdTomaMx(), examenTmp.getDiagnostico().getIdDiagnostico());
                                long idUsuario = seguridadService.obtenerIdUsuario(request);
                                Usuarios usuario = usuarioService.getUsuarioById((int) idUsuario);
                                ordenExamen.setSolicitudDx(solicitudDx);
                                ordenExamen.setCodExamen(examenTmp.getExamen());
                                ordenExamen.setFechaHOrden(new Timestamp(new Date().getTime()));
                                ordenExamen.setUsarioRegistro(usuario);
                                try {
                                    ordenExamenMxService.addOrdenExamen(ordenExamen);
                                }catch (Exception ex){
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }
                    List<OrdenExamen> ordenExamenListNew = ordenExamenMxService.getOrdenesExamenByIdMx(recepcionMx.getTomaMx().getIdTomaMx());
                    mav.addObject("examenesList",ordenExamenListNew);
                }else{
                    mav.addObject("examenesList",ordenExamenList);
                }
            }

            mav.addObject("recepcionMx",recepcionMx);
            mav.addObject("entidades",entidadesAdtvases);
            mav.addObject("unidades",unidades);
            mav.addObject("tipoMuestra", tipoMxList);
            mav.addObject("laboratorios",laboratorioList);
            mav.addObject("calidadMx",calidadMx);
            //mav.addObject("examenesDfList",examenesList);

            mav.addObject("fechaInicioSintomas",fechaInicioSintomas);
            mav.setViewName("recepcionMx/recepcionarOrdersLab");
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
    @RequestMapping(value = "searchOrders", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String fetchOrdersJson(@RequestParam(value = "strFilter", required = true) String filtro) throws Exception{
        logger.info("Obteniendo las ordenes de examen pendienetes según filtros en JSON");
        FiltroMx filtroMx = jsonToFiltroMx(filtro);
        List<DaTomaMx> tomaMxList = tomaMxService.getTomaMxByFiltro(filtroMx);
        return tomaMxToJson(tomaMxList);
    }

    /**
     * Método para realizar la búsqueda de Recepcion Mx para recepcionar en laboratorio
     * @param filtro JSon con los datos de los filtros a aplicar en la búsqueda(Nombre Apellido, Rango Fec Toma Mx, Tipo Mx, SILAIS, unidad salud)
     * @return String con las Recepciones encontradas
     * @throws Exception
     */
    @RequestMapping(value = "searchOrdersLab", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String fetchOrdersLabJson(@RequestParam(value = "strFilter", required = true) String filtro) throws Exception{
        logger.info("Obteniendo las ordenes de examen pendienetes según filtros en JSON");
        FiltroMx filtroMx = jsonToFiltroMx(filtro);
        List<RecepcionMx> recepcionMxList = recepcionMxService.getRecepcionesByFiltro(filtroMx);
        return RecepcionMxToJson(recepcionMxList);
    }

    /**
     * Método para registrar una recepción de muestra de vigilancia. Modifica la Mx al estado ESTDMX|RCP
     * @param request para obtener información de la petición del cliente. Contiene en un parámetro la estructura json del registro a agregar
     * @param response para notificar al cliente del resultado de la operación
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "agregarRecepcion", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void agregarRecepcion(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        String idRecepcion = "";
        String verificaCantTb = "";
        String verificaTipoMx = "";
        String idTomaMx = "";
        String codigoUnicoMx = "";
        String causaRechazo;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            verificaCantTb = jsonpObject.get("verificaCantTb").getAsString();
            verificaTipoMx = jsonpObject.get("verificaTipoMx").getAsString();
            idTomaMx = jsonpObject.get("idTomaMx").getAsString();
            causaRechazo = jsonpObject.get("causaRechazo").getAsString();

            long idUsuario = seguridadService.obtenerIdUsuario(request);
            Usuarios usuario = usuarioService.getUsuarioById((int)idUsuario);
            //Se obtiene estado recepcionado
            EstadoMx estadoMx = catalogosService.getEstadoMx("ESTDMX|RCP");
            TipoRecepcionMx tipoRecepcionMx = catalogosService.getTipoRecepcionMx("TPRECPMX|VRT");
            //se obtiene tomaMx de examen a recepcionar
            DaTomaMx tomaMx = tomaMxService.getTomaMxById(idTomaMx);

            RecepcionMx recepcionMx = new RecepcionMx();

            recepcionMx.setUsuarioRecepcion(usuario);
            recepcionMx.setFechaHoraRecepcion(new Timestamp(new Date().getTime()));
            recepcionMx.setTipoMxCk(Boolean.valueOf(verificaTipoMx));
            recepcionMx.setCantidadTubosCk(Boolean.valueOf(verificaCantTb));
            if (!causaRechazo.isEmpty()) {
                recepcionMx.setCausaRechazo(causaRechazo);
                //se obtiene calidad de la muestra inadecuada
                CalidadMx calidadMx = catalogosService.getCalidadMx("CALIDMX|IDC");
                recepcionMx.setCalidadMx(calidadMx);
            }
            recepcionMx.setTipoRecepcionMx(tipoRecepcionMx);
            recepcionMx.setTomaMx(tomaMx);
            try {
                idRecepcion = recepcionMxService.addRecepcionMx(recepcionMx);
            }catch (Exception ex){
                resultado = messageSource.getMessage("msg.add.receipt.error",null,null);
                resultado=resultado+". \n "+ex.getMessage();
                ex.printStackTrace();
            }
            if (!idRecepcion.isEmpty()) {
               //se tiene que actualizar la tomaMx
                tomaMx.setEstadoMx(estadoMx);
                try {
                    tomaMxService.updateTomaMx(tomaMx);
                    codigoUnicoMx = tomaMx.getCodigoUnicoMx();
                }catch (Exception ex){
                    resultado = messageSource.getMessage("msg.update.order.error",null,null);
                    resultado=resultado+". \n "+ex.getMessage();
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.receipt.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("idRecepcion",idRecepcion);
            map.put("mensaje",resultado);
            map.put("idTomaMx", idTomaMx);
            map.put("verificaCantTb", verificaCantTb);
            map.put("verificaTipoMx", verificaTipoMx);
            map.put("codigoUnicoMx",codigoUnicoMx);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    /**
     * Método para actualizar una recepción de vigilancia indicando que se ha recepcionado en el laboratorio. Modifica la Mx al estado ESTDMX|RCLAB
     * @param request para obtener información de la petición del cliente. Contiene en un parámetro la estructura json del registro a actualizar
     * @param response para notificar al cliente del resultado de la operación
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "receiptLaboratory", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void recepcionLaboratorio(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        String idRecepcion = "";
        String causaRechazo = null;
        String codCalidadMx = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            idRecepcion = jsonpObject.get("idRecepcion").getAsString();
            codCalidadMx = jsonpObject.get("calidadMx").getAsString();

            if (jsonpObject.get("causaRechazo")!=null && !jsonpObject.get("causaRechazo").getAsString().isEmpty())
                causaRechazo = jsonpObject.get("causaRechazo").getAsString();

            long idUsuario = seguridadService.obtenerIdUsuario(request);
            Usuarios usuario = usuarioService.getUsuarioById((int) idUsuario);
            //Se obtiene estado recepcionado en laboratorio
            EstadoMx estadoMx = catalogosService.getEstadoMx("ESTDMX|RCLAB");
            //se obtiene calidad de la muestra
            CalidadMx calidadMx = catalogosService.getCalidadMx(codCalidadMx);
            //se obtiene recepción a actualizar
            RecepcionMx recepcionMx = recepcionMxService.getRecepcionMx(idRecepcion);
            //se setean valores a actualizar
            recepcionMx.setUsuarioRecepcionLab(usuario);
            recepcionMx.setFechaHoraRecepcionLab(new Timestamp(new Date().getTime()));
            recepcionMx.setCalidadMx(calidadMx);
            recepcionMx.setCausaRechazo(causaRechazo);

            try {
                recepcionMxService.updateRecepcionMx(recepcionMx);
            }catch (Exception ex){
                resultado = messageSource.getMessage("msg.add.receipt.error",null,null);
                resultado=resultado+". \n "+ex.getMessage();
                ex.printStackTrace();
            }
            if (!idRecepcion.isEmpty()) {
                //se tiene que actualizar la tomaMx
                DaTomaMx tomaMx = tomaMxService.getTomaMxById(recepcionMx.getTomaMx().getIdTomaMx());
                tomaMx.setEstadoMx(estadoMx);
                try {
                    tomaMxService.updateTomaMx(tomaMx);
                }catch (Exception ex){
                    resultado = messageSource.getMessage("msg.update.order.error",null,null);
                    resultado=resultado+". \n "+ex.getMessage();
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.receipt.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("idRecepcion",idRecepcion);
            map.put("mensaje",resultado);
            map.put("calidadMx", codCalidadMx);
            map.put("causaRechazo", causaRechazo);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    /***
     * Método para recuperar las ordenes de examen registradas para la mx en la recepción.
     * @param idTomaMx id de la toma mx a obtener ordenes
     * @return String con las ordenes en formato Json
     * @throws Exception
     */
    @RequestMapping(value = "getOrdenesExamen", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    String getOrdenesExamen(@RequestParam(value = "idTomaMx", required = true) String idTomaMx) throws Exception {
        List<OrdenExamen> ordenExamenList = ordenExamenMxService.getOrdenesExamenNoAnuladasByIdMx(idTomaMx);
        return OrdenesExamenToJson(ordenExamenList);
    }

    /**
     * Método para anular una orden de examen
     * @param request para obtener información de la petición del cliente. Contiene en un parámetro la estructura json del registro a anular
     * @param response para notificar al cliente del resultado de la operación
     * @throws Exception
     */
    @RequestMapping(value = "anularExamen", method = RequestMethod.POST)
    protected void anularExamen(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.debug("buscar ordenes para ordenExamen");
        String urlValidacion;
        String idOrdenExamen = "";
        String json="";
        String resultado = "";
        try {

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
                BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
                json = br.readLine();
                JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
                idOrdenExamen = jsonpObject.get("idOrdenExamen").getAsString();
                OrdenExamen ordenExamen = ordenExamenMxService.getOrdenExamenById(idOrdenExamen);
                if(ordenExamen!=null){
                    ordenExamen.setAnulado(true);
                    try{
                        ordenExamenMxService.updateOrdenExamen(ordenExamen);
                    }catch (Exception ex){
                        logger.error("Error al anular orden de examen",ex);
                        resultado = messageSource.getMessage("msg.receipt.test.cancel.error2", null, null);
                        resultado = resultado + ". \n " + ex.getMessage();
                   }
                }else{
                    throw new Exception(messageSource.getMessage("msg.receipt.test.order.notfound", null, null));
                }
            }else{
                resultado = messageSource.getMessage("msg.not.have.permission", null, null);
            }

        }catch (Exception ex){
            logger.error("Sucedio un error al anular orden de examen",ex);
            resultado = messageSource.getMessage("msg.receipt.test.cancel.error1", null, null);
            resultado = resultado + ". \n " + ex.getMessage();
        } finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("idOrdenExamen", idOrdenExamen);
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    /**
     * Método para agregar una orden de examen para una mx
     * @param request para obtener información de la petición del cliente. Contiene en un parámetro la estructura json del registro a agregar
     * @param response para notificar al cliente del resultado de la operación
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "agregarOrdenExamen", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void agregarOrdenExamen(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        String idTomaMx = "";
        int idDiagnostico = 0;
        int idExamen = 0;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            idTomaMx = jsonpObject.get("idTomaMx").getAsString();
            idDiagnostico = jsonpObject.get("idDiagnostico").getAsInt();
            idExamen = jsonpObject.get("idExamen").getAsInt();
            //se valida si existe una orden activa para la muestra, el diagnóstico y el examen
            List<OrdenExamen> ordenExamenList = ordenExamenMxService.getOrdExamenNoAnulByIdMxIdDxIdExamen(idTomaMx, idDiagnostico, idExamen);
            if (ordenExamenList!=null && ordenExamenList.size()>0) {
                resultado = messageSource.getMessage("msg.receipt.test.exist", null, null);
            }else{
                CatalogoExamenes examen = examenesService.getExamenesById(idExamen);
                long idUsuario = seguridadService.obtenerIdUsuario(request);
                Usuarios usuario = usuarioService.getUsuarioById((int) idUsuario);
                OrdenExamen ordenExamen = new OrdenExamen();
                DaSolicitudDx solicitudDx = tomaMxService.getSolicitudesDxByMxDx(idTomaMx, idDiagnostico);
                ordenExamen.setSolicitudDx(solicitudDx);
                ordenExamen.setCodExamen(examen);
                ordenExamen.setFechaHOrden(new Timestamp(new Date().getTime()));
                ordenExamen.setUsarioRegistro(usuario);
                try {
                    ordenExamenMxService.addOrdenExamen(ordenExamen);
                } catch (Exception ex) {
                    resultado = messageSource.getMessage("msg.receipt.add.test.error", null, null);
                    resultado = resultado + ". \n " + ex.getMessage();
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.receipt.test.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            UnicodeEscaper escaper     = UnicodeEscaper.above(127);
            Map<String, String> map = new HashMap<String, String>();
            map.put("idTomaMx",idTomaMx);
            map.put("idDiagnostico", String.valueOf(idDiagnostico));
            map.put("idExamen", String.valueOf(idExamen));
            map.put("mensaje",escaper.translate(resultado));
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
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
            //map.put("idOrdenExamen",tomaMx.getIdOrdenExamen());
            map.put("idTomaMx", tomaMx.getIdTomaMx());
            //map.put("fechaHoraOrden",DateUtil.DateToString(tomaMx.getFechaHOrden(), "dd/MM/yyyy hh:mm:ss a"));
            map.put("fechaTomaMx",DateUtil.DateToString(tomaMx.getFechaHTomaMx(), "dd/MM/yyyy hh:mm:ss a"));
            map.put("codSilais", tomaMx.getIdNotificacion().getCodSilaisAtencion().getNombre());
            map.put("codUnidadSalud", tomaMx.getIdNotificacion().getCodUnidadAtencion().getNombre());
            //map.put("estadoOrden", tomaMx.getCodEstado().getValor());
            map.put("separadaMx",(tomaMx.getMxSeparada()!=null?(tomaMx.getMxSeparada()?"Si":"No"):""));
            map.put("cantidadTubos", (tomaMx.getCanTubos()!=null?String.valueOf(tomaMx.getCanTubos()):""));
            map.put("tipoMuestra", tomaMx.getCodTipoMx().getNombre());
            //map.put("tipoExamen", tomaMx.getCodExamen().getNombre());
            //Si hay fecha de inicio de sintomas se muestra
            Date fechaInicioSintomas = tomaMxService.getFechaInicioSintomas(tomaMx.getIdNotificacion().getIdNotificacion());
            if (fechaInicioSintomas!=null)
                map.put("fechaInicioSintomas",DateUtil.DateToString(fechaInicioSintomas,"dd/MM/yyyy"));
            else
                map.put("fechaInicioSintomas"," ");
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

            mapResponse.put(indice, map);
            indice ++;
        }
        jsonResponse = new Gson().toJson(mapResponse);
        return jsonResponse;
    }

    /**
     * Método para convertir una lista de RecepcionMx a un string con estructura Json
     * @param recepcionMxList lista con las Recepciones a convertir
     * @return String
     */
    private String RecepcionMxToJson(List<RecepcionMx> recepcionMxList){
        String jsonResponse="";
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice=0;
        for(RecepcionMx recepcion : recepcionMxList){
            Map<String, String> map = new HashMap<String, String>();
            map.put("idRecepcion", recepcion.getIdRecepcion());
            //map.put("idOrdenExamen", ordenExamen.getOrdenExamen().getIdOrdenExamen());
            map.put("idTomaMx", recepcion.getTomaMx().getIdTomaMx());
            //map.put("fechaHoraOrden",DateUtil.DateToString(ordenExamen.getOrdenExamen().getFechaHOrden(), "dd/MM/yyyy hh:mm:ss a"));
            map.put("fechaTomaMx",DateUtil.DateToString(recepcion.getTomaMx().getFechaHTomaMx(),"dd/MM/yyyy hh:mm:ss a"));
            map.put("fechaRecepcion",DateUtil.DateToString(recepcion.getFechaHoraRecepcion(),"dd/MM/yyyy hh:mm:ss a"));
            map.put("codSilais", recepcion.getTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre());
            map.put("codUnidadSalud", recepcion.getTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre());
            //map.put("estadoOrden", ordenExamen.getOrdenExamen().getCodEstado().getValor());
            map.put("separadaMx",(recepcion.getTomaMx().getMxSeparada()!=null?(recepcion.getTomaMx().getMxSeparada()?"Si":"No"):""));
            map.put("cantidadTubos", (recepcion.getTomaMx().getCanTubos()!=null?String.valueOf(recepcion.getTomaMx().getCanTubos()):""));
            map.put("tipoMuestra", recepcion.getTomaMx().getCodTipoMx().getNombre());
            //map.put("tipoExamen", ordenExamen.getOrdenExamen().getCodExamen().getNombre());
            //map.put("areaProcesa", ordenExamen.getOrdenExamen().getCodExamen().getArea().getNombre());
            //Si hay fecha de inicio de sintomas se muestra
            Date fechaInicioSintomas = tomaMxService.getFechaInicioSintomas(recepcion.getTomaMx().getIdNotificacion().getIdNotificacion());
            if (fechaInicioSintomas!=null)
                map.put("fechaInicioSintomas",DateUtil.DateToString(fechaInicioSintomas,"dd/MM/yyyy"));
            else
                map.put("fechaInicioSintomas"," ");
            //Si hay persona
            if (recepcion.getTomaMx().getIdNotificacion().getPersona()!=null){
                /// se obtiene el nombre de la persona asociada a la ficha
                String nombreCompleto = "";
                nombreCompleto = recepcion.getTomaMx().getIdNotificacion().getPersona().getPrimerNombre();
                if (recepcion.getTomaMx().getIdNotificacion().getPersona().getSegundoNombre()!=null)
                    nombreCompleto = nombreCompleto +" "+ recepcion.getTomaMx().getIdNotificacion().getPersona().getSegundoNombre();
                nombreCompleto = nombreCompleto+" "+ recepcion.getTomaMx().getIdNotificacion().getPersona().getPrimerApellido();
                if (recepcion.getTomaMx().getIdNotificacion().getPersona().getSegundoApellido()!=null)
                    nombreCompleto = nombreCompleto +" "+ recepcion.getTomaMx().getIdNotificacion().getPersona().getSegundoApellido();
                map.put("persona",nombreCompleto);
            }else{
                map.put("persona"," ");
            }

            mapResponse.put(indice, map);
            indice ++;
        }
        jsonResponse = new Gson().toJson(mapResponse);
        return jsonResponse;
    }

    /**
     * Método para convertir una lista de Ordenes Examen a un string con estructura Json
     * @param ordenesExamenList lista con las ordenes de examen a convertir
     * @return String
     * @throws UnsupportedEncodingException
     */
    private String OrdenesExamenToJson(List<OrdenExamen> ordenesExamenList) throws UnsupportedEncodingException {
        String jsonResponse="";
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice=0;
        for(OrdenExamen ordenExamen : ordenesExamenList){
            Map<String, String> map = new HashMap<String, String>();
            map.put("idTomaMx", ordenExamen.getSolicitudDx().getIdTomaMx().getIdTomaMx());
            map.put("idOrdenExamen",ordenExamen.getIdOrdenExamen());
            map.put("nombreExamen",ordenExamen.getCodExamen().getNombre());
            map.put("nombreDx",ordenExamen.getSolicitudDx().getCodDx().getNombre());
            map.put("nombreAreaPrc",ordenExamen.getSolicitudDx().getCodDx().getArea().getNombre());
            map.put("fechaSolicitudDx",DateUtil.DateToString(ordenExamen.getSolicitudDx().getFechaHSolicitud(),"dd/MM/yyyy hh:mm:ss a"));
            mapResponse.put(indice, map);
            indice ++;
        }
        jsonResponse = new Gson().toJson(mapResponse);
        //escapar caracteres especiales, escape de los caracteres con valor numérico mayor a 127
        UnicodeEscaper escaper     = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
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
        String esLab = null;

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
        if (jObjectFiltro.get("esLab") !=null && !jObjectFiltro.get("esLab").getAsString().isEmpty())
            esLab = jObjectFiltro.get("esLab").getAsString();

        filtroMx.setCodSilais(codSilais);
        filtroMx.setCodUnidadSalud(codUnidadSalud);
        filtroMx.setFechaInicioTomaMx(fechaInicioTomaMx);
        filtroMx.setFechaFinTomaMx(fechaFinTomaMx);
        filtroMx.setFechaInicioRecep(fechaInicioRecep);
        filtroMx.setFechaFinRecep(fechaFinRecep);
        filtroMx.setNombreApellido(nombreApellido);
        filtroMx.setCodTipoMx(codTipoMx);
        if (!Boolean.valueOf(esLab)) { //es recepción general
            filtroMx.setCodEstado("ESTDMX|ENV"); // sólo las enviadas
        } else { //es recepción en laboratorio
            filtroMx.setCodEstado("ESTDMX|EPLAB"); // sólo las enviadas para procesar en laboratorio
            filtroMx.setIncluirMxInadecuada(true);
        }

        return filtroMx;
    }

    /**
     * Método para generar un string alfanumérico de 8 caracteres, que se usará como código único de muestra
     * @return String codigoUnicoMx
     */
    private String generarCodigoUnicoMx(){
        RecepcionMx validaRecepcionMx;
        //Se genera el código
        String codigoUnicoMx = StringUtil.getCadenaAlfanumAleatoria(8);
        //Se consulta BD para ver si existe recepción con muestra que tenga mismo código
        validaRecepcionMx = recepcionMxService.getRecepcionMxByCodUnicoMx(codigoUnicoMx);
        //si existe, de manera recursiva se solicita un nuevo código
        if (validaRecepcionMx!=null){
            codigoUnicoMx = generarCodigoUnicoMx();
        }
        //si no existe se retorna el último código generado
        return codigoUnicoMx;
    }

}
