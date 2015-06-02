package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.muestra.*;
import ni.gob.minsa.laboratorio.domain.notificacion.DaNotificacion;
import ni.gob.minsa.laboratorio.domain.parametros.Parametro;
import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
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
        List<DaNotificacion> results = daNotificacionService.getNoticesByPerson(idPerson);
        mav.addObject("notificaciones", results);
        mav.addObject("personaId",idPerson);
        mav.setViewName("tomaMx/results");
        return  mav;
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
        if (StringUtil.isNumeric(idNotificacion)){
            noti = new DaNotificacion();
            noti.setPersona(personaService.getPersona(Long.valueOf(idNotificacion)));
            noti.setFechaRegistro(new Timestamp(new Date().getTime()));
            Parametro pUsuarioRegistro = parametrosService.getParametroByName("USU_REGISTRO_NOTI_CAESP");
            if(pUsuarioRegistro!=null) {
                long idUsuario = Long.valueOf(pUsuarioRegistro.getValor());
                noti.setUsuarioRegistro(usuarioService.getUsuarioById((int)idUsuario));
            }
            noti.setCodTipoNotificacion(catalogoService.getTipoNotificacion("TPNOTI|CAESP"));
            daNotificacionService.addNotification(noti);
        }else{
            noti = daNotificacionService.getNotifById(idNotificacion);
        }
        if (noti != null) {
            catTipoMx = tomaMxService.getTipoMxByTipoNoti("TPNOTI|CAESP");

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

    @RequestMapping(value = "/saveTomaold", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> saveTomaMx(HttpServletRequest request,
              @RequestParam(value = "dx", required = false) String dx
            , @RequestParam(value = "fechaHTomaMx", required = false) String fechaHTomaMx
            , @RequestParam(value = "codTipoMx", required = false) String codTipoMx
            , @RequestParam(value = "canTubos", required = false) Integer canTubos
            , @RequestParam(value = "volumen", required = false) String volumen
            , @RequestParam(value = "horaRefrigeracion", required = false) String horaRefrigeracion
            , @RequestParam(value = "mxSeparada", required = false) Integer  mxSeparada
            , @RequestParam(value = "idNotificacion", required = false) String idNotificacion

    ) throws Exception {
        logger.debug("Guardando datos de Toma de Muestra");

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


        if(mxSeparada != null){
            if(mxSeparada == 1){
                tomaMx.setMxSeparada(true);
            }else {
                tomaMx.setMxSeparada(false);
            }
        }

        tomaMx.setFechaRegistro(new Timestamp(new Date().getTime()));
        long idUsuario = seguridadService.obtenerIdUsuario(request);
        tomaMx.setUsuario(usuarioService.getUsuarioById((int)idUsuario));
        tomaMx.setEstadoMx(catalogoService.getEstadoMx("ESTDMX|PEND"));
        String codigo = generarCodigoUnicoMx();
        tomaMx.setCodigoUnicoMx(codigo);
        tomaMxService.addTomaMx(tomaMx);
        saveDxRequest(tomaMx.getIdTomaMx(), dx);
        return createJsonResponse(tomaMx);
    }

    private void saveDxRequest(String idTomaMx, String dx) throws Exception {

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
            tomaMxService.addSolicitudDx(soli);
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
    /******************** TOMA MUESTRAS ESTUDIOS********************************/
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
        try {
            logger.debug("Guardando datos de Toma de Muestra");
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            idNotificacion = jsonpObject.get("idNotificacion").getAsString();
            fechaHTomaMx = jsonpObject.get("fechaHTomaMx").getAsString();
            codTipoMx = jsonpObject.get("codTipoMx").getAsString();

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
            tomaMx.setEnvio(envioOrden);
            tomaMxService.addTomaMx(tomaMx);
            saveDxRequest(tomaMx.getIdTomaMx(), dx);

            //saveSolicitudesEstudio(tomaMx.getIdTomaMx(), jsonArray, request);
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("lbl.messagebox.error.saving",null,null);
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
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }
}
