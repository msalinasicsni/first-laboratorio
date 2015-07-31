package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.muestra.*;
import ni.gob.minsa.laboratorio.domain.notificacion.DaNotificacion;
import ni.gob.minsa.laboratorio.domain.parametros.Parametro;
import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
import ni.gob.minsa.laboratorio.domain.resultados.DetalleResultadoFinal;
import ni.gob.minsa.laboratorio.domain.resultados.RespuestaSolicitud;
import ni.gob.minsa.laboratorio.service.*;
import ni.gob.minsa.laboratorio.utilities.ConstantsSecurity;
import ni.gob.minsa.laboratorio.utilities.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by souyen-ics on 11-05-14.
 */
@Controller
@RequestMapping("tomaMx")
public class TomaMxController {

    private static final Logger logger = LoggerFactory.getLogger(TomaMxController.class);

    @Autowired
    @Qualifier(value = "seguridadService")
    private SeguridadService seguridadService;

    @Resource(name="tomaMxService")
    private TomaMxService tomaMxService;

    @Resource(name="usuarioService")
    public UsuarioService usuarioService;

    @Resource(name = "catalogosService")
    public CatalogoService catalogoService;

    @Resource(name = "daNotificacionService")
    public DaNotificacionService daNotificacionService;

    @Resource(name = "personaService")
    public PersonaService personaService;

    @Resource(name = "parametrosService")
    private ParametrosService parametrosService;

    @Resource(name = "datosSolicitudService")
    private DatosSolicitudService datosSolicitudService;

    @Autowired
    MessageSource messageSource;

    Map<String, Object> mapModel;
    List<TipoMx_TipoNotificacion> catTipoMx;



    @RequestMapping(value = "search", method = RequestMethod.GET)
    public String initSearchForm(Model model, HttpServletRequest request) throws ParseException {
        logger.debug("Crear/Buscar Toma de Mx");

        String urlValidacion= "";
        try {
            urlValidacion = seguridadService.validarLogin(request);
            //si la url esta vacia significa que la validación del login fue exitosa
            if (urlValidacion.isEmpty())
                urlValidacion = seguridadService.validarAutorizacionUsuario(request, ConstantsSecurity.SYSTEM_CODE, false);
        }catch (Exception e){
            e.printStackTrace();
            urlValidacion = "404";
        }

        if(urlValidacion.isEmpty()){
            return "tomaMx/search";
        }else{
            return urlValidacion;
        }

    }

    @RequestMapping("noticesrut/{idPerson}")
    public ModelAndView showNoticesRutPerson(@PathVariable("idPerson") long idPerson) throws Exception {
        ModelAndView mav = new ModelAndView();
        List<DaNotificacion> results = daNotificacionService.getNoticesByPerson(idPerson,"TPNOTI|PCNT");
        mav.addObject("notificaciones", results);
        mav.addObject("personaId",idPerson);
        mav.setViewName("tomaMx/results");
        return  mav;
    }

    /**
     * Handler for create tomaMx.
     *
     * @param idPersona the ID of the person to create noti
     * @return a ModelMap with the model attributes for the respective view
     */
    @RequestMapping("createnoti/{idPersona}")
    public ModelAndView createTomaMxNoti(@PathVariable("idPersona") String idPersona) throws Exception {
        ModelAndView mav = new ModelAndView();
            //registros anteriores de toma Mx
        DaTomaMx tomaMx = new DaTomaMx();
        DaNotificacion noti;
        noti = new DaNotificacion();
        noti.setPersona(personaService.getPersona(Long.valueOf(idPersona)));
        noti.setFechaRegistro(new Timestamp(new Date().getTime()));
        Parametro pUsuarioRegistro = parametrosService.getParametroByName("USU_REGISTRO_NOTI_CAESP");
        if(pUsuarioRegistro!=null) {
            long idUsuario = Long.valueOf(pUsuarioRegistro.getValor());
            noti.setUsuarioRegistro(usuarioService.getUsuarioById((int)idUsuario));
        }
        //noti.setCodTipoNotificacion(catalogoService.getTipoNotificacion("TPNOTI|CAESP"));
        noti.setCodTipoNotificacion(catalogoService.getTipoNotificacion("TPNOTI|PCNT"));
        daNotificacionService.addNotification(noti);

        //catTipoMx = tomaMxService.getTipoMxByTipoNoti("TPNOTI|CAESP");
        catTipoMx = tomaMxService.getTipoMxByTipoNoti("TPNOTI|PCNT");

        mav.addObject("noti", noti);
        mav.addObject("tomaMx", tomaMx);
        mav.addObject("catTipoMx", catTipoMx);
        mav.addAllObjects(mapModel);
        mav.setViewName("tomaMx/enterForm");

        return mav;
    }

    /**
     * Handler for create tomaMx.
     *
     * @param idNotificacion the ID of the notification
     * @return a ModelMap with the model attributes for the respective view
     */
    @RequestMapping("create/{idNotificacion}")
    public ModelAndView createTomaMx(@PathVariable("idNotificacion") String idNotificacion) throws Exception {
        ModelAndView mav = new ModelAndView();
        //registros anteriores de toma Mx
        DaTomaMx tomaMx = new DaTomaMx();
        DaNotificacion noti;
        //si es numero significa que es un id de persona, no de notificación por tanto hay que crear una notificación para esa persona
        noti = daNotificacionService.getNotifById(idNotificacion);
        if (noti != null) {
            //catTipoMx = tomaMxService.getTipoMxByTipoNoti("TPNOTI|CAESP");
            catTipoMx = tomaMxService.getTipoMxByTipoNoti("TPNOTI|PCNT");

            mav.addObject("noti", noti);
            mav.addObject("tomaMx", tomaMx);
            mav.addObject("catTipoMx", catTipoMx);
            mav.addAllObjects(mapModel);
            mav.setViewName("tomaMx/enterForm");
        } else {
            mav.setViewName("404");
        }

        return mav;
    }

    /**
     * Retorna una lista de dx
     * @return Un arreglo JSON de dx
     */
    @RequestMapping(value = "dxByMx", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    List<Dx_TipoMx_TipoNoti> getDxBySample(@RequestParam(value = "codMx", required = true) String codMx, @RequestParam(value = "tipoNoti", required = true) String tipoNoti) throws Exception {
        logger.info("Obteniendo los diagnósticos segun muestra y tipo de Notificacion en JSON");
        //nombre usuario null, para que no valide autoridad
        return tomaMxService.getDx(codMx, tipoNoti,null);
    }


    @RequestMapping(value = "tomaMxByIdNoti", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    List<DaTomaMx> getTestBySample(@RequestParam(value = "idNotificacion", required = true) String idNotificacion) throws Exception {
        logger.info("Realizando búsqueda de Toma de Mx.");

        return tomaMxService.getTomaMxByIdNoti(idNotificacion);

    }

    private void saveDxRequest(String idTomaMx, String dx, String strRespuestas, Integer cantRespuestas) throws Exception {

        DaSolicitudDx soli = new DaSolicitudDx();
        String[] arrayDx = dx.split(",");
        Laboratorio laboratorio = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
        for (String anArrayDx : arrayDx) {
            soli.setCodDx(tomaMxService.getDxById(anArrayDx));
            soli.setFechaHSolicitud(new Timestamp(new Date().getTime()));
            Parametro pUsuarioRegistro = parametrosService.getParametroByName("USU_REGISTRO_NOTI_CAESP");
            if(pUsuarioRegistro!=null) {
                long idUsuario = Long.valueOf(pUsuarioRegistro.getValor());
                soli.setUsarioRegistro(usuarioService.getUsuarioById((int)idUsuario));
            }
            soli.setIdTomaMx(tomaMxService.getTomaMxById(idTomaMx));
            soli.setAprobada(false);
            soli.setLabProcesa(laboratorio);
            soli.setControlCalidad(false);
            tomaMxService.addSolicitudDx(soli);

            JsonObject jObjectRespuestas = new Gson().fromJson(strRespuestas, JsonObject.class);
            for(int i = 0; i< cantRespuestas;i++) {
                String respuesta = jObjectRespuestas.get(String.valueOf(i)).toString();
                JsonObject jsRespuestaObject = new Gson().fromJson(respuesta, JsonObject.class);

                Integer idRespuesta = jsRespuestaObject.get("idRespuesta").getAsInt();
                Integer idConcepto = jsRespuestaObject.get("idConcepto").getAsInt();

                DatoSolicitud conceptoTmp =  datosSolicitudService.getDatoRecepcionSolicitudById(idRespuesta);
                //si la respuesta pertenece al dx de la solicitud, se registra
                if (conceptoTmp.getDiagnostico().getIdDiagnostico().equals(soli.getCodDx().getIdDiagnostico())) {
                    String valor = jsRespuestaObject.get("valor").getAsString();
                    DatoSolicitudDetalle datoSolicitudDetalle = new DatoSolicitudDetalle();
                    datoSolicitudDetalle.setFechahRegistro(new Timestamp(new Date().getTime()));
                    datoSolicitudDetalle.setValor(valor);
                    datoSolicitudDetalle.setDatoSolicitud(conceptoTmp);
                    datoSolicitudDetalle.setSolicitudDx(soli);
                    datoSolicitudDetalle.setUsuarioRegistro(seguridadService.getUsuario(seguridadService.obtenerNombreUsuario()));
                    datosSolicitudService.saveOrUpdateDetalleDatoRecepcion(datoSolicitudDetalle);
                    //validar respuesta solicitud
                    /*DetalleResultadoFinal resFinalRegistrado = resultadoFinalService.getDetResBySolicitudAndRespuesta(idSolicitud, idRespuesta);
                    if (resFinalRegistrado != null) {
                        datoSolicitudDetalle.setIdDetalle(resFinalRegistrado.getIdDetalle());
                        resultadoFinalService.updateDetResFinal(datoSolicitudDetalle);
                    } else {
                        //validar respuesta examen como respuesta solicitud
                        resFinalRegistrado = resultadoFinalService.getDetResBySolicitudAndRespuestaExa(idSolicitud, idRespuesta);
                        if (resFinalRegistrado != null) {
                            datoSolicitudDetalle.setIdDetalle(resFinalRegistrado.getIdDetalle());
                            resultadoFinalService.updateDetResFinal(datoSolicitudDetalle);
                        } else {
                            //validar si hay respuesta examen con el concetpo a registrar
                            if (resultadoFinalService.getDetResBySolicitudAndConceptoRespuestaExa(idSolicitud, idConcepto).size() <= 0) {
                                if (datoSolicitudDetalle.getValor() != null && !datoSolicitudDetalle.getValor().isEmpty()) {
                                    resultadoFinalService.saveDetResFinal(datoSolicitudDetalle);
                                }
                            }
                        }
                    }*/
                }
            }
        }

    }

    private Timestamp StringToTimestamp(String fechah) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        Date date = sdf.parse(fechah);
        return new Timestamp(date.getTime());
    }

    private ResponseEntity<String> createJsonResponse(Object o) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        Gson gson = new Gson();
        String json = gson.toJson(o);
        return new ResponseEntity<>(json, headers, HttpStatus.CREATED);
    }

    /**
     * Método para generar un string alfanumérico de 8 caracteres, que se usará como código único de muestra
     * @return String codigoUnicoMx
     */
    private String generarCodigoUnicoMx(){
        DaTomaMx validaC;
        //Se genera el código
        String codigoUnicoMx = StringUtil.getCadenaAlfanumAleatoria(8);
        //Se consulta BD para ver si existe toma de Mx que tenga mismo código
        validaC = tomaMxService.getTomaMxByCodUnicoMx(codigoUnicoMx);
        //si existe, de manera recursiva se solicita un nuevo código
        if (validaC!=null){
            codigoUnicoMx = generarCodigoUnicoMx();
        }
        //si no existe se retorna el último código generado
        return codigoUnicoMx;
    }

    /***************************************************************************/
    /******************** TOMA MUESTRAS PACIENTES Y OTRAS MX********************************/
    /***************************************************************************/


    @RequestMapping(value = "saveToma", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/json")
    protected void saveTomaMxStudy(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json = "";
        String resultado = "";
        String fechaHTomaMx="";
        String idNotificacion="";
        String codTipoMx="";
        Integer canTubos=null;
        String volumen=null;
        String horaRefrigeracion="";
        String dx="";
        String strRespuestas="";
        Integer cantRespuestas=0;
        try {
            logger.debug("Guardando datos de Toma de Muestra");
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            idNotificacion = jsonpObject.get("idNotificacion").getAsString();
            fechaHTomaMx = jsonpObject.get("fechaHTomaMx").getAsString();
            codTipoMx = jsonpObject.get("codTipoMx").getAsString();
            strRespuestas = jsonpObject.get("strRespuestas").toString();
            cantRespuestas = jsonpObject.get("cantRespuestas").getAsInt();
            if (jsonpObject.get("canTubos")!=null && !jsonpObject.get("canTubos").getAsString().isEmpty())
                canTubos = jsonpObject.get("canTubos").getAsInt();

            if (jsonpObject.get("volumen")!=null && !jsonpObject.get("volumen").getAsString().isEmpty())
                volumen = jsonpObject.get("volumen").getAsString();

            if (jsonpObject.get("dx")!=null && !jsonpObject.get("dx").getAsString().isEmpty())
                dx = jsonpObject.get("dx").getAsString();

            horaRefrigeracion = jsonpObject.get("horaRefrigeracion").getAsString();

            Parametro pUsuarioRegistro = parametrosService.getParametroByName("USU_REGISTRO_NOTI_CAESP");
            Usuarios usuarioRegistro = new Usuarios();
            if(pUsuarioRegistro!=null) {
                long idUsuario = Long.valueOf(pUsuarioRegistro.getValor());
                usuarioRegistro = usuarioService.getUsuarioById((int)idUsuario);
            }

            DaEnvioMx envioOrden = new DaEnvioMx();

            envioOrden.setUsarioRegistro(usuarioRegistro);
            envioOrden.setFechaHoraEnvio(new Timestamp(new Date().getTime()));
            envioOrden.setNombreTransporta("");
            envioOrden.setTemperaturaTermo(null);
            envioOrden.setLaboratorioDestino(seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario()));
            try {
                tomaMxService.addEnvioOrden(envioOrden);
            }catch (Exception ex){
                resultado = messageSource.getMessage("msg.sending.error.add",null,null);
                resultado=resultado+". \n "+ex.getMessage();
                ex.printStackTrace();
                throw new Exception(ex);
            }

            DaTomaMx tomaMx = new DaTomaMx();

            tomaMx.setIdNotificacion(daNotificacionService.getNotifById(idNotificacion));
            if(fechaHTomaMx != null){
                tomaMx.setFechaHTomaMx(StringToTimestamp(fechaHTomaMx));
            }

            tomaMx.setCodTipoMx(tomaMxService.getTipoMxById(codTipoMx));
            tomaMx.setCanTubos(canTubos);

            if(volumen != null && !volumen.equals("")){
                tomaMx.setVolumen(Float.valueOf(volumen));
            }

            tomaMx.setHoraRefrigeracion(horaRefrigeracion);
            tomaMx.setMxSeparada(false);
            tomaMx.setFechaRegistro(new Timestamp(new Date().getTime()));

            tomaMx.setUsuario(usuarioRegistro);
            tomaMx.setEstadoMx(catalogoService.getEstadoMx("ESTDMX|ENV"));
            String codigo = generarCodigoUnicoMx();
            tomaMx.setCodigoUnicoMx(codigo);
            tomaMx.setCodigoLab(null);
            tomaMx.setEnvio(envioOrden);
            tomaMxService.addTomaMx(tomaMx);
            saveDxRequest(tomaMx.getIdTomaMx(), dx, strRespuestas, cantRespuestas);

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.add.receipt.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("idNotificacion",idNotificacion);
            map.put("canTubos",String.valueOf(canTubos));
            map.put("volumen",volumen);
            map.put("mensaje",resultado);
            map.put("fechaHTomaMx", fechaHTomaMx);
            map.put("codTipoMx",codTipoMx);
            map.put("horaRefrigeracion", horaRefrigeracion);
            map.put("strRespuestas",strRespuestas);
            map.put("cantRespuestas",cantRespuestas.toString());
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = "getDatosSolicitudDetalleBySolicitud", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    List<DatoSolicitudDetalle> getDatosSolicitudDetalleBySolicitud(@RequestParam(value = "idSolicitud", required = true) String idSolicitud) throws Exception {
        logger.info("Se obtienen los detalles de resultados activos para la solicitud");
        List<DatoSolicitudDetalle> resultados = datosSolicitudService.getDatosSolicitudDetalleBySolicitud(idSolicitud);
        return resultados;
    }

    @RequestMapping(value = "createnoti", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/json")
    protected void crearNotificacion(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json = "";
        String resultado = "";
        String idNotificacion = "";
        Integer idPersona=null;
        try {
            logger.debug("Guardando datos de la notificacion");
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            idPersona = jsonpObject.get("idPersona").getAsInt();
            DaNotificacion noti = new DaNotificacion();
            noti.setPersona(personaService.getPersona(Long.valueOf(idPersona)));
            noti.setFechaRegistro(new Timestamp(new Date().getTime()));
            Parametro pUsuarioRegistro = parametrosService.getParametroByName("USU_REGISTRO_NOTI_CAESP");
            if(pUsuarioRegistro!=null) {
                long idUsuario = Long.valueOf(pUsuarioRegistro.getValor());
                noti.setUsuarioRegistro(usuarioService.getUsuarioById((int)idUsuario));
            }
            //noti.setCodTipoNotificacion(catalogoService.getTipoNotificacion("TPNOTI|CAESP"));
            noti.setCodTipoNotificacion(catalogoService.getTipoNotificacion("TPNOTI|PCNT"));
            daNotificacionService.addNotification(noti);
            idNotificacion = noti.getIdNotificacion();
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.add.notification.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("idNotificacion",idNotificacion);
            map.put("mensaje",resultado);
            map.put("idPersona",String.valueOf(idPersona));
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

}
