package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.concepto.Catalogo_Lista;
import ni.gob.minsa.laboratorio.domain.concepto.Concepto;
import ni.gob.minsa.laboratorio.domain.muestra.Catalogo_Dx;
import ni.gob.minsa.laboratorio.domain.muestra.DatoSolicitud;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.User;
import ni.gob.minsa.laboratorio.service.*;
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
@RequestMapping("administracion/datosSolicitud")
public class DatosIngresoSolicitudController {

    private static final Logger logger = LoggerFactory.getLogger(DatosIngresoSolicitudController.class);
    @Resource(name = "seguridadService")
    private SeguridadService seguridadService;

    @Resource(name = "datosSolicitudService")
    private DatosSolicitudService datosSolicitudService;

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


   /* @RequestMapping(value = "init", method = RequestMethod.GET)
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
            mav.addObject("isAnswer",false);
            mav.setViewName("administracion/searchSolicitud");
        }else
            mav.setViewName(urlValidacion);

        return mav;
    }*/

  /*  @RequestMapping(value = "getDx", method = RequestMethod.GET, produces = "application/json")
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
    }*/

   /* private String DxToJson(List<Catalogo_Dx> objectsDx, List<Catalogo_Estudio> objectsE ){
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
    }*/


    /*@RequestMapping(value = "create/{strParametros}", method = RequestMethod.GET)
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

            List<Concepto> conceptsList = conceptoService.getConceptsList(false);
            Parametro parametro = parametrosService.getParametroByName("DATO_NUM_CONCEPTO");
            mav.addObject("dx", dx);
            mav.addObject("estudio", estudio);
            mav.addObject("conceptsList",conceptsList);
            mav.addObject("codigoDatoNumerico",parametro.getValor());
            mav.setViewName("administracion/conceptsRequest");
        }else
            mav.setViewName(urlValidacion);

        return mav;
    }*/

    @RequestMapping(value = "getDatosIngresoSolicitud", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    List<DatoSolicitud> getRespuestasSolicitud(@RequestParam(value = "idSolicitud", required = false) Integer idSolicitud) throws Exception {
        logger.info("Obteniendo los datos de ingreso de información en recepción de dx  en JSON");
        return datosSolicitudService.getDatosRecepcionDxByIdSolicitud(idSolicitud);
    }

    @RequestMapping(value = "getDatoSolicitudById", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    DatoSolicitud getDatoSolicitudById(@RequestParam(value = "idConceptoSol", required = true) Integer idConceptoSol) throws Exception {
        logger.info("Obteniendo datos de recepción solicitud en JSON");
        return datosSolicitudService.getDatoRecepcionSolicitudById(idConceptoSol);
    }

    @RequestMapping(value = "agregarActualizarDato", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void agregarActualizarDato(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        String strRespuesta="";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            strRespuesta = jsonpObject.get("respuesta").toString();
            User usuario = seguridadService.getUsuario(seguridadService.obtenerNombreUsuario());
            DatoSolicitud datoSolicitud = jsonToDatoRecepcionSolicitud(strRespuesta);
            datoSolicitud.setUsuarioRegistro(usuario);
            datosSolicitudService.saveOrUpdateDatoRecepcion(datoSolicitud);

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


    private DatoSolicitud jsonToDatoRecepcionSolicitud(String jsonRespuesta) throws Exception {
        DatoSolicitud datoSolicitud = new DatoSolicitud();
        JsonObject jsonpObject = new Gson().fromJson(jsonRespuesta, JsonObject.class);
        //si hay idConcepto se obtiene registro para actualizar, luego si vienen los demas datos se actualizan
        if (jsonpObject.get("idDato")!=null && !jsonpObject.get("idDato").getAsString().isEmpty()) {
            datoSolicitud = datosSolicitudService.getDatoRecepcionSolicitudById(jsonpObject.get("idDato").getAsInt());
        }
        if (jsonpObject.get("idSolicitud")!=null && !jsonpObject.get("idSolicitud").getAsString().isEmpty()) {
            Catalogo_Dx dx = tomaMxService.getDxsById(jsonpObject.get("idSolicitud").getAsInt());
            datoSolicitud.setDiagnostico(dx);
        }

        if (jsonpObject.get("nombre")!=null && !jsonpObject.get("nombre").getAsString().isEmpty())
            datoSolicitud.setNombre(jsonpObject.get("nombre").getAsString());
        if (jsonpObject.get("concepto")!=null && !jsonpObject.get("concepto").getAsString().isEmpty()) {
            Concepto concepto = conceptoService.getConceptById(jsonpObject.get("concepto").getAsInt());
            datoSolicitud.setConcepto(concepto);
        }
        if (jsonpObject.get("orden")!=null && !jsonpObject.get("orden").getAsString().isEmpty())
            datoSolicitud.setOrden(jsonpObject.get("orden").getAsInt());
        if (jsonpObject.get("requerido")!=null && !jsonpObject.get("requerido").getAsString().isEmpty())
            datoSolicitud.setRequerido(jsonpObject.get("requerido").getAsBoolean());
        if (jsonpObject.get("pasivo")!=null && !jsonpObject.get("pasivo").getAsString().isEmpty())
            datoSolicitud.setPasivo(jsonpObject.get("pasivo").getAsBoolean());

        if (jsonpObject.get("descripcion")!=null && !jsonpObject.get("descripcion").getAsString().isEmpty())
            datoSolicitud.setDescripcion(jsonpObject.get("descripcion").getAsString());

        datoSolicitud.setFechahRegistro(new Timestamp(new Date().getTime()));
        return datoSolicitud;
    }

    @RequestMapping(value = "getTipoDato", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    Concepto getTipoDato(@RequestParam(value = "idTipoDato", required = true) Integer idTipoDato) throws Exception {
        logger.info("Obteniendo concepto en JSON");
        return conceptoService.getConceptById(idTipoDato);
    }


    @RequestMapping(value = "getDatosRecepcionActivosDx", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    List<DatoSolicitud> getDatosRecepcionActivosDx(@RequestParam(value = "idSolicitud", required = true) Integer idSolicitud) throws Exception {
        logger.info("Obteniendo los datos de ingreso de información en recepción activos de dx  en JSON");
        return datosSolicitudService.getDatosRecepcionActivosDxByIdSolicitud(idSolicitud);
    }

    @RequestMapping(value = "getDatosRecepcionActivos", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    List<DatoSolicitud> getDatosRecepcionActivos(@RequestParam(value = "solicitudes", required = true) String solicitudes) throws Exception {
        logger.info("Obteniendo los datos de ingreso de información en recepción activos de los dx  en JSON");
        List<DatoSolicitud> datoSolicituds = datosSolicitudService.getDatosRecepcionActivosDxByIdSolicitudes(solicitudes);
        return datoSolicituds;
    }

    @RequestMapping(value = "getCatalogosListaConcepto", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    List<Catalogo_Lista> getCatalogoListaConcepto(@RequestParam(value = "idDx", required = true) String idDx) throws Exception {
        logger.info("Obteniendo los valores para los conceptos tipo lista asociados a las respuesta del estudio o dx");
        if (!idDx.isEmpty())
            return datosSolicitudService.getCatalogoListaConceptoByIdDx(Integer.valueOf(idDx));
        else return null;
    }
}
