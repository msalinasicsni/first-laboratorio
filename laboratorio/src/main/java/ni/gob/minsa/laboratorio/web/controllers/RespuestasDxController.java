package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.muestra.Catalogo_Dx;
import ni.gob.minsa.laboratorio.domain.notificacion.TipoNotificacion;
import ni.gob.minsa.laboratorio.domain.parametros.Parametro;
import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
import ni.gob.minsa.laboratorio.domain.resultados.Concepto;
import ni.gob.minsa.laboratorio.domain.resultados.RespuestaDx;
import ni.gob.minsa.laboratorio.service.*;
import ni.gob.minsa.laboratorio.utilities.ConstantsSecurity;
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

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by souyen-ics.
 */
@Controller
@RequestMapping("administracion/respuestasDx")
public class RespuestasDxController {

    private static final Logger logger = LoggerFactory.getLogger(RespuestasDxController.class);
    @Resource(name = "seguridadService")
    private SeguridadService seguridadService;

    @Resource(name = "usuarioService")
    private UsuarioService usuarioService;

    @Resource(name = "respuestasDxService")
    private RespuestasDxService respuestasDxService;

    @Resource(name = "tomaMxService")
    private TomaMxService tomaMxService;

    @Resource(name = "catalogosService")
    private CatalogoService catalogoService;

    @Resource(name = "conceptoService")
    private ConceptoService conceptoService;

    @Resource(name = "parametrosService")
    private ParametrosService parametrosService;

    @Autowired
    MessageSource messageSource;


    @RequestMapping(value = "init", method = RequestMethod.GET)
    public ModelAndView initForm(HttpServletRequest request) throws Exception {
        logger.debug("Pantalla de inicio para crear respuestas- búsqueda de dx");
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
            List<TipoNotificacion> notificacionList = catalogoService.getTipoNotificacion();
            mav.addObject("notificaciones", notificacionList);
            mav.setViewName("administracion/searchDx");
        }else
            mav.setViewName(urlValidacion);

        return mav;
    }

    @RequestMapping(value = "getDx", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String getOrdenesExamen(@RequestParam(value = "nombreDx", required = true) String nombreDx) throws Exception {
        List<Catalogo_Dx> objectsDx = respuestasDxService.getDxByFiltro(nombreDx);
        return DxToJson(objectsDx);
    }

    private String DxToJson(List<Catalogo_Dx> objectsDx){
        String jsonResponse="";
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice=0;
        for(Catalogo_Dx dx: objectsDx) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("idDx",String.valueOf(dx.getIdDiagnostico()));
            map.put("nombreDx",dx.getNombre());
            map.put("nombreArea",dx.getArea().getNombre());
            mapResponse.put(indice, map);
            indice ++;
        }
        jsonResponse = new Gson().toJson(mapResponse);
        //escapar caracteres especiales, escape de los caracteres con valor numérico mayor a 127
        UnicodeEscaper escaper     = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }


    @RequestMapping(value = "create/{strParametros}", method = RequestMethod.GET)
    public ModelAndView createResponseForm(HttpServletRequest request, @PathVariable("strParametros") String strParametros) throws Exception {
        logger.debug("inicializar pantalla de creación de respuestas para dx");
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
            String[] arParametros = strParametros.split(",");
            Catalogo_Dx dx = tomaMxService.getDxsById(Integer.valueOf(arParametros[0]));
            List<Concepto> conceptsList = conceptoService.getConceptsList();
            Parametro parametro = parametrosService.getParametroByName("DATO_NUM_CONCEPTO");
            mav.addObject("dx", dx);
            mav.addObject("conceptsList",conceptsList);
            mav.addObject("codigoDatoNumerico",parametro.getValor());
            mav.setViewName("administracion/enterDxAnswers");
        }else
            mav.setViewName(urlValidacion);

        return mav;
    }

    @RequestMapping(value = "getRespuestasDx", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    List<RespuestaDx> getRespuestasDx(@RequestParam(value = "idDx", required = true) String idDx) throws Exception {
        logger.info("Obteniendo las respuestas de un diagnostico en JSON");
        return respuestasDxService.getRespuestasByDx(Integer.valueOf(idDx));
    }

    @RequestMapping(value = "getRespuestaDxById", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    RespuestaDx getRespuestaById(@RequestParam(value = "idRespuesta", required = true) Integer idRespuesta) throws Exception {
        logger.info("Obteniendo respuesta dx en JSON");
        return respuestasDxService.getRespuestaDxById(idRespuesta);
    }

    @RequestMapping(value = "agregarActualizarRespuesta", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void agregarActualizarRespuesta(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        String strRespuesta="";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            strRespuesta = jsonpObject.get("respuesta").toString();
            long idUsuario = seguridadService.obtenerIdUsuario(request);
            Usuarios usuario = usuarioService.getUsuarioById((int)idUsuario);
            RespuestaDx respuesta = jsonToRespuesta(strRespuesta);
            respuesta.setUsuarioRegistro(usuario);
            respuestasDxService.saveOrUpdateResponse(respuesta);

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.response.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("concepto",strRespuesta);
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }


    private RespuestaDx jsonToRespuesta(String jsonRespuesta) throws Exception {
        RespuestaDx respuestaDx = new RespuestaDx();
        JsonObject jsonpObject = new Gson().fromJson(jsonRespuesta, JsonObject.class);
        //si hay idConcepto se obtiene registro para actualizar, luego si vienen los demas datos se actualizan
        if (jsonpObject.get("idRespuesta")!=null && !jsonpObject.get("idRespuesta").getAsString().isEmpty()) {
            respuestaDx = respuestasDxService.getRespuestaDxById(jsonpObject.get("idRespuesta").getAsInt());
        }
        if (jsonpObject.get("idDx")!=null && !jsonpObject.get("idDx").getAsString().isEmpty()) {
            Catalogo_Dx dx = tomaMxService.getDxsById(jsonpObject.get("idDx").getAsInt());
            respuestaDx.setDiagnostico(dx);
        }
        if (jsonpObject.get("nombre")!=null && !jsonpObject.get("nombre").getAsString().isEmpty())
            respuestaDx.setNombre(jsonpObject.get("nombre").getAsString());
        if (jsonpObject.get("concepto")!=null && !jsonpObject.get("concepto").getAsString().isEmpty()) {
            Concepto concepto = conceptoService.getConceptById(jsonpObject.get("concepto").getAsInt());
            respuestaDx.setConcepto(concepto);
        }
        if (jsonpObject.get("orden")!=null && !jsonpObject.get("orden").getAsString().isEmpty())
            respuestaDx.setOrden(jsonpObject.get("orden").getAsInt());
        if (jsonpObject.get("requerido")!=null && !jsonpObject.get("requerido").getAsString().isEmpty())
            respuestaDx.setRequerido(jsonpObject.get("requerido").getAsBoolean());
        if (jsonpObject.get("pasivo")!=null && !jsonpObject.get("pasivo").getAsString().isEmpty())
            respuestaDx.setPasivo(jsonpObject.get("pasivo").getAsBoolean());
        if (jsonpObject.get("minimo")!=null && !jsonpObject.get("minimo").getAsString().isEmpty())
            respuestaDx.setMinimo(jsonpObject.get("minimo").getAsInt());
        if (jsonpObject.get("maximo")!=null && !jsonpObject.get("maximo").getAsString().isEmpty())
            respuestaDx.setMaximo(jsonpObject.get("maximo").getAsInt());
        if (jsonpObject.get("descRespuesta")!=null && !jsonpObject.get("descRespuesta").getAsString().isEmpty())
            respuestaDx.setDescripcion(jsonpObject.get("descRespuesta").getAsString());

        respuestaDx.setFechahRegistro(new Timestamp(new Date().getTime()));
        return  respuestaDx;
    }

    @RequestMapping(value = "getTipoDato", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    Concepto getTipoDato(@RequestParam(value = "idTipoDato", required = true) Integer idTipoDato) throws Exception {
        logger.info("Obteniendo concepto en JSON");
        return conceptoService.getConceptById(idTipoDato);
    }

    @RequestMapping(value = "getRespuestasActivasDx", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    List<RespuestaDx> getRespuestasActivasDx(@RequestParam(value = "idDx", required = true) String idDx) throws Exception {
        logger.info("Obteniendo las respuestas activas de dx en JSON");
        return respuestasDxService.getRespuestasActivasByDx(Integer.valueOf(idDx));
    }

}
