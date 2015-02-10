package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.examen.CatalogoExamenes;
import ni.gob.minsa.laboratorio.domain.muestra.*;
import ni.gob.minsa.laboratorio.domain.notificacion.TipoNotificacion;
import ni.gob.minsa.laboratorio.domain.parametros.Parametro;
import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
import ni.gob.minsa.laboratorio.domain.resultados.RespuestaExamen;
import ni.gob.minsa.laboratorio.domain.resultados.Concepto;
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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by Miguel Salinas on 2/3/2015.
 * V1.0
 */
@Controller
@RequestMapping("administracion/respuestas")
public class RespuestasExamenController {

    private static final Logger logger = LoggerFactory.getLogger(RespuestasExamenController.class);
    @Autowired
    @Qualifier(value = "seguridadService")
    private SeguridadService seguridadService;

    @Autowired
    @Qualifier(value = "usuarioService")
    private UsuarioService usuarioService;

    @Autowired
    @Qualifier(value = "respuestasExamenService")
    private RespuestasExamenService respuestasExamenService;

    @Autowired
    @Qualifier(value = "tomaMxService")
    private TomaMxService tomaMxService;

    @Autowired
    @Qualifier(value = "catalogosService")
    private CatalogoService catalogoService;

    @Autowired
    @Qualifier(value = "examenesService")
    private ExamenesService examenesService;

    @Autowired
    @Qualifier(value = "conceptoService")
    private ConceptoService conceptoService;

    @Autowired
    @Qualifier(value = "parametrosService")
    private ParametrosService parametrosService;

    @Autowired
    MessageSource messageSource;

    @RequestMapping(value = "init", method = RequestMethod.GET)
    public ModelAndView initSearchForm(HttpServletRequest request) throws Exception {
        logger.debug("pantalla de inicio para crear respuestas - b�squeda examenes");
        String urlValidacion;
        try {
            urlValidacion = seguridadService.validarLogin(request);
            //si la url esta vacia significa que la validaci�n del login fue exitosa
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
            mav.setViewName("administracion/searchResponse");
        }else
            mav.setViewName(urlValidacion);

        return mav;
    }

    @RequestMapping(value = "getExamenes", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String getOrdenesExamen(@RequestParam(value = "codTipoNoti", required = true) String codTipoNoti, @RequestParam(value = "idDx", required = true) String idDx, @RequestParam(value = "nombreExamen", required = true) String nombreExamen) throws Exception {
        List<Object[]> objectsExamen = examenesService.getExamenesByFiltro(idDx, codTipoNoti, nombreExamen);
        return ExamenesToJson(objectsExamen);
    }

    @RequestMapping(value = "create/{strParametros}", method = RequestMethod.GET)
    public ModelAndView createResponseForm(HttpServletRequest request, @PathVariable("strParametros") String strParametros) throws Exception {
        logger.debug("inicializar pantalla de creaci�n de respuestas para examen");
        String urlValidacion;
        try {
            urlValidacion = seguridadService.validarLogin(request);
            //si la url esta vacia significa que la validaci�n del login fue exitosa
            if (urlValidacion.isEmpty())
                urlValidacion = seguridadService.validarAutorizacionUsuario(request, ConstantsSecurity.SYSTEM_CODE, false);
        }catch (Exception e){
            e.printStackTrace();
            urlValidacion = "404";
        }
        ModelAndView mav = new ModelAndView();
        if (urlValidacion.isEmpty()) {
            String[] arParametros = strParametros.split(",");
            if (arParametros.length == 3) {//tienen que venir los 3 par�metros
                CatalogoExamenes examen = examenesService.getExamenesById(Integer.valueOf(arParametros[0]));
                Catalogo_Dx diagnostico = tomaMxService.getDxsById(Integer.valueOf(arParametros[1]));
                TipoNotificacion tipoNotificacion = catalogoService.getTipoNotificacion(arParametros[2]);
                List<Concepto> conceptsList = conceptoService.getConceptsList();
                Parametro parametro = parametrosService.getParametroByName("DATO_NUM_CONCEPTO");
                mav.addObject("examen", examen);
                mav.addObject("tipoNotificacion", tipoNotificacion);
                mav.addObject("diagnostico", diagnostico);
                mav.addObject("conceptsList",conceptsList);
                mav.addObject("codigoDatoNumerico",parametro.getValor());
            }

            mav.setViewName("administracion/createResponse");
        }else
            mav.setViewName(urlValidacion);

        return mav;
    }

    @RequestMapping(value = "getRespuetasExamen", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    List<RespuestaExamen> getRespuetasExamen(@RequestParam(value = "idExamen", required = true) String idExamen) throws Exception {
        logger.info("Obteniendo los sectores por unidad de salud en JSON");
        return respuestasExamenService.getRespuestasByExamen(Integer.valueOf(idExamen));
    }

    @RequestMapping(value = "getRespuestasActivasExamen", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    List<RespuestaExamen> getRespuestasActivasExamen(@RequestParam(value = "idExamen", required = true) String idExamen) throws Exception {
        logger.info("Obteniendo los sectores por unidad de salud en JSON");
        return respuestasExamenService.getRespuestasActivasByExamen(Integer.valueOf(idExamen));
    }

    @RequestMapping(value = "getTipoDato", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    Concepto getTipoDato(@RequestParam(value = "idTipoDato", required = true) Integer idTipoDato) throws Exception {
        logger.info("Obteniendo los sectores por unidad de salud en JSON");
        return conceptoService.getConceptById(idTipoDato);
    }

    @RequestMapping(value = "agregarActualizarRespuesta", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void agregarActualizarRespuesta(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        String strConcepto="";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            strConcepto = jsonpObject.get("concepto").toString();
            long idUsuario = seguridadService.obtenerIdUsuario(request);
            Usuarios usuario = usuarioService.getUsuarioById((int)idUsuario);
            RespuestaExamen concepto = jsonToRespuesta(strConcepto);
            concepto.setUsuarioRegistro(usuario);
            //si tiene id de concepto entonces se debe actualizar, sino se agrega
            if (concepto.getIdRespuesta()!=null)
                respuestasExamenService.updateResponse(concepto);
            else
                respuestasExamenService.addResponse(concepto);


        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.receipt.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("concepto",strConcepto);
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = "getRespuestaById", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    RespuestaExamen getRespuestaById(@RequestParam(value = "idConcepto", required = true) Integer idConcepto) throws Exception {
        logger.info("Obteniendo los sectores por unidad de salud en JSON");
        return respuestasExamenService.getRespuestaById(idConcepto);
    }

    private String ExamenesToJson(List<Object[]> objectsExamen){
        String jsonResponse="";
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice=0;
        for(Object[] examen: objectsExamen) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("idExamen",String.valueOf((Integer)examen[0]));
            map.put("idDx",String.valueOf((Integer)examen[1]));
            map.put("codNoti",(String)examen[2]);
            map.put("nombreExamen",(String)examen[3]);
            map.put("nombreNoti",(String)examen[4]);
            map.put("nombreDx",(String)examen[5]);
            map.put("nombreArea",(String)examen[6]);
            mapResponse.put(indice, map);
            indice ++;
        }
        jsonResponse = new Gson().toJson(mapResponse);
        //escapar caracteres especiales, escape de los caracteres con valor num�rico mayor a 127
        UnicodeEscaper escaper     = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }

    private RespuestaExamen jsonToRespuesta(String jsonRespuesta){
        RespuestaExamen respuestaExamen = new RespuestaExamen();
        JsonObject jsonpObject = new Gson().fromJson(jsonRespuesta, JsonObject.class);
        //si hay idConcepto se obtiene registro para actualizar, luego si vienen los demas datos se actualizan
        if (jsonpObject.get("idRespuesta")!=null && !jsonpObject.get("idRespuesta").getAsString().isEmpty()) {
            respuestaExamen = respuestasExamenService.getRespuestaById(jsonpObject.get("idRespuesta").getAsInt());
        }
        if (jsonpObject.get("idExamen")!=null && !jsonpObject.get("idExamen").getAsString().isEmpty()) {
            CatalogoExamenes examen = examenesService.getExamenesById(jsonpObject.get("idExamen").getAsInt());
            respuestaExamen.setIdExamen(examen);
        }
        if (jsonpObject.get("nombre")!=null && !jsonpObject.get("nombre").getAsString().isEmpty())
            respuestaExamen.setNombre(jsonpObject.get("nombre").getAsString());
        if (jsonpObject.get("concepto")!=null && !jsonpObject.get("concepto").getAsString().isEmpty()) {
            Concepto concepto = conceptoService.getConceptById(jsonpObject.get("concepto").getAsInt());
            respuestaExamen.setConcepto(concepto);
        }
        if (jsonpObject.get("orden")!=null && !jsonpObject.get("orden").getAsString().isEmpty())
            respuestaExamen.setOrden(jsonpObject.get("orden").getAsInt());
        if (jsonpObject.get("requerido")!=null && !jsonpObject.get("requerido").getAsString().isEmpty())
            respuestaExamen.setRequerido(jsonpObject.get("requerido").getAsBoolean());
        if (jsonpObject.get("pasivo")!=null && !jsonpObject.get("pasivo").getAsString().isEmpty())
            respuestaExamen.setPasivo(jsonpObject.get("pasivo").getAsBoolean());
        if (jsonpObject.get("minimo")!=null && !jsonpObject.get("minimo").getAsString().isEmpty())
            respuestaExamen.setMinimo(jsonpObject.get("minimo").getAsInt());
        if (jsonpObject.get("maximo")!=null && !jsonpObject.get("maximo").getAsString().isEmpty())
            respuestaExamen.setMaximo(jsonpObject.get("maximo").getAsInt());
        respuestaExamen.setFechahRegistro(new Timestamp(new Date().getTime()));
        return  respuestaExamen;
    }

}
