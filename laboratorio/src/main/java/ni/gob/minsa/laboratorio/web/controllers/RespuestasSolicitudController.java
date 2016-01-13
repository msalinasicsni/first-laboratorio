package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.concepto.Concepto;
import ni.gob.minsa.laboratorio.domain.muestra.Catalogo_Dx;
import ni.gob.minsa.laboratorio.domain.muestra.Catalogo_Estudio;
import ni.gob.minsa.laboratorio.domain.notificacion.TipoNotificacion;
import ni.gob.minsa.laboratorio.domain.parametros.Parametro;
import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
import ni.gob.minsa.laboratorio.domain.resultados.RespuestaSolicitud;
import ni.gob.minsa.laboratorio.service.*;
import ni.gob.minsa.laboratorio.utilities.ConstantsSecurity;
import org.apache.commons.lang3.text.translate.UnicodeEscaper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("administracion/respuestasSolicitud")
public class RespuestasSolicitudController {

    private static final Logger logger = LoggerFactory.getLogger(RespuestasSolicitudController.class);
    @Resource(name = "seguridadService")
    private SeguridadService seguridadService;

    @Resource(name = "usuarioService")
    private UsuarioService usuarioService;

    @Resource(name = "respuestasSolicitudService")
    private RespuestasSolicitudService respuestasSolicitudService;

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
            List<Concepto> conceptsList = conceptoService.getConceptsList(true);
            Parametro parametro = parametrosService.getParametroByName("DATO_NUM_CONCEPTO");


            mav.addObject("notificaciones", notificacionList);
            mav.addObject("isAnswer",true);
            mav.addObject("conceptsList",conceptsList);
            mav.addObject("codigoDatoNumerico",parametro.getValor());
            mav.setViewName("administracion/searchSolicitud");
        }else
            mav.setViewName(urlValidacion);

        return mav;
    }

    @RequestMapping(value = "getDx", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String getOrdenesExamen(@RequestParam(value = "tipo", required = false) String tipo, @RequestParam(value = "nombre", required = false) String nombre) throws Exception {
        List<Catalogo_Dx> records = null;
        List<Catalogo_Estudio> recordsE = null;

        if(!tipo.isEmpty()){
            if(tipo.equals("Rutina")){
                records = respuestasSolicitudService.getDxByFiltro(nombre);
            }else{
                recordsE = respuestasSolicitudService.getEstudioByFiltro(nombre);
            }
        }else{
            records = respuestasSolicitudService.getDxByFiltro(nombre);
            recordsE = respuestasSolicitudService.getEstudioByFiltro(nombre);
        }


        return DxToJson(records, recordsE);
    }

    private String DxToJson(List<Catalogo_Dx> objectsDx, List<Catalogo_Estudio> objectsE ){
        String jsonResponse="";
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice=0;

        if(objectsDx != null  && objectsE != null){
            for(Catalogo_Dx dx: objectsDx) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("idDx",String.valueOf(dx.getIdDiagnostico()));
                map.put("nombreDx",dx.getNombre());
                map.put("nombreArea",dx.getArea().getNombre());
                map.put("tipoSolicitud", "Rutina");
                mapResponse.put(indice, map);
                indice ++;
            }

            for(Catalogo_Estudio est: objectsE) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("idDx",String.valueOf(est.getIdEstudio()));
                map.put("nombreDx",est.getNombre());
                map.put("nombreArea",est.getArea().getNombre());
                map.put("tipoSolicitud", "Estudio");
                mapResponse.put(indice, map);
                indice ++;
            }
        }
        if(objectsDx != null && objectsE == null){
            for(Catalogo_Dx dx: objectsDx) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("idDx",String.valueOf(dx.getIdDiagnostico()));
                map.put("nombreDx",dx.getNombre());
                map.put("nombreArea",dx.getArea().getNombre());
                map.put("tipoSolicitud", "Rutina");
                mapResponse.put(indice, map);
                indice ++;
            }
        }

        if(objectsE != null && objectsDx == null){
            for(Catalogo_Estudio est: objectsE) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("idDx",String.valueOf(est.getIdEstudio()));
                map.put("nombreDx",est.getNombre());
                map.put("nombreArea",est.getArea().getNombre());
                map.put("tipoSolicitud", "Estudio");
                mapResponse.put(indice, map);
                indice ++;
            }
        }


        jsonResponse = new Gson().toJson(mapResponse);
        //escapar caracteres especiales, escape de los caracteres con valor numérico mayor a 127
        UnicodeEscaper escaper     = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }



    @RequestMapping(value = "getRequestData", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String getRequestData(@RequestParam(value = "strParametros", required = false) String strParametros) throws Exception {
       Catalogo_Dx dx = null;
       Catalogo_Estudio estudio = null;
        String[] arParametros = strParametros.split(",");

        if(arParametros[1] != null){
            if(arParametros[1].equals("Rutina")){
                dx = tomaMxService.getDxsById(Integer.valueOf(arParametros[0]));
            }else{
                estudio = tomaMxService.getEstudioById(Integer.valueOf(arParametros[0]));
            }
        }

        return dataToJson(dx, estudio);
    }

    private String dataToJson(Catalogo_Dx diagnostico,Catalogo_Estudio estudio ){
        String jsonResponse="";
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();

        if (diagnostico != null) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("id", String.valueOf(diagnostico.getIdDiagnostico()));
            map.put("nombre", diagnostico.getNombre());
            map.put("area", diagnostico.getArea().getNombre());
            map.put("tipo", "Rutina");
            mapResponse.put(0, map);
        }

        if (estudio != null) {

            Map<String, String> map = new HashMap<String, String>();
            map.put("id", String.valueOf(estudio.getIdEstudio()));
            map.put("nombre", estudio.getNombre());
            map.put("area", estudio.getArea().getNombre());
            map.put("tipo", "Estudio");
            mapResponse.put(0, map);

        }

        jsonResponse = new Gson().toJson(mapResponse);
        //escapar caracteres especiales, escape de los caracteres con valor numérico mayor a 127
        UnicodeEscaper escaper     = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }




   /* @RequestMapping(value = "create/{strParametros}", method = RequestMethod.GET)
    public ModelAndView createResponseForm(HttpServletRequest request, @PathVariable("strParametros") String strParametros) throws Exception {
        logger.debug("inicializar pantalla de creación de respuestas para dx o estudio");
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
         *//*   Catalogo_Dx dx = null;
            Catalogo_Estudio estudio = null;
            String[] arParametros = strParametros.split(",");

            if(arParametros[1] != null){
                if(arParametros[1].equals("Rutina")){
                    dx = tomaMxService.getDxsById(Integer.valueOf(arParametros[0]));
                }else{
                    estudio = tomaMxService.getEstudioById(Integer.valueOf(arParametros[0]));
                }
            }*//*

            List<Concepto> conceptsList = conceptoService.getConceptsList(true);
            Parametro parametro = parametrosService.getParametroByName("DATO_NUM_CONCEPTO");
           // mav.addObject("dx", dx);
          //  mav.addObject("estudio", estudio);
            mav.addObject("conceptsList",conceptsList);
            mav.addObject("codigoDatoNumerico",parametro.getValor());
           // mav.setViewName("administracion/enterAnswersRequest");
        }else
            mav.setViewName(urlValidacion);

        return mav;
    }*/

    @RequestMapping(value = "getRespuestasSolicitud", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    List<RespuestaSolicitud> getRespuestasSolicitud(@RequestParam(value = "strParametros", required = false) String strParametros) throws Exception {
        logger.info("Obteniendo las respuestas de un diagnostico o estudio en json en JSON");
        List<RespuestaSolicitud> respuestas = null;
        String[] arParametros = strParametros.split(",");

        if(arParametros[1] != null){
            if(arParametros[1].equals("Rutina")){
                respuestas = respuestasSolicitudService.getRespuestasByDx(Integer.valueOf(arParametros[0]));

            }else{
                respuestas = respuestasSolicitudService.getRespuestasByEstudio(Integer.valueOf(arParametros[0]));
            }
        }

        return respuestas;
    }

    @RequestMapping(value = "getRespuestaDxById", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    RespuestaSolicitud getRespuestaById(@RequestParam(value = "idRespuesta", required = true) Integer idRespuesta) throws Exception {
        logger.info("Obteniendo respuesta dx en JSON");
        return respuestasSolicitudService.getRespuestaDxById(idRespuesta);
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
            RespuestaSolicitud respuesta = jsonToRespuesta(strRespuesta);
            respuesta.setUsuarioRegistro(usuario);
            respuestasSolicitudService.saveOrUpdateResponse(respuesta);

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


    private RespuestaSolicitud jsonToRespuesta(String jsonRespuesta) throws Exception {
        RespuestaSolicitud respuestaDx = new RespuestaSolicitud();
        JsonObject jsonpObject = new Gson().fromJson(jsonRespuesta, JsonObject.class);
        //si hay idConcepto se obtiene registro para actualizar, luego si vienen los demas datos se actualizan
        if (jsonpObject.get("idRespuesta")!=null && !jsonpObject.get("idRespuesta").getAsString().isEmpty()) {
            respuestaDx = respuestasSolicitudService.getRespuestaDxById(jsonpObject.get("idRespuesta").getAsInt());
        }

        if (jsonpObject.get("tipo")!=null && !jsonpObject.get("tipo").getAsString().isEmpty()) {

            if(jsonpObject.get("idRequest")!=null && !jsonpObject.get("idRequest").getAsString().isEmpty()){

                if(jsonpObject.get("tipo").getAsString() == "Rutina"){
                    Catalogo_Dx dx = tomaMxService.getDxsById(jsonpObject.get("idRequest").getAsInt());
                    respuestaDx.setDiagnostico(dx);
                }else{
                    Catalogo_Estudio estudio = tomaMxService.getEstudioById(jsonpObject.get("idRequest").getAsInt());
                    respuestaDx.setEstudio(estudio);
                }
            }

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


    @RequestMapping(value = "getRespuestasActivas", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    List<RespuestaSolicitud> getRespuestasActivas(@RequestParam(value = "idDx", required = false) String idDx, @RequestParam(value = "idEstudio", required = false) String idEstudio) throws Exception {
        logger.info("Obteniendo las respuestas activas de dx o estudio en JSON");
        List<RespuestaSolicitud> respuestasActivas = null;

        if(!idDx.equals("") || !idEstudio.equals("")){
            if(idEstudio.equals("")){
                respuestasActivas = respuestasSolicitudService.getRespuestasActivasByDx(Integer.valueOf(idDx));
            }else{
                respuestasActivas = respuestasSolicitudService.getRespuestasActivasByEstudio(Integer.valueOf(idEstudio));
            }
        }

       return  respuestasActivas;

    }


}
