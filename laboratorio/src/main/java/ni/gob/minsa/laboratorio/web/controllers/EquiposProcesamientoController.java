package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.examen.CatalogoExamenes;
import ni.gob.minsa.laboratorio.domain.examen.EquiposProcesamiento;
import ni.gob.minsa.laboratorio.domain.examen.Examen_Equipo;
import ni.gob.minsa.laboratorio.service.EquiposProcesamientoService;
import ni.gob.minsa.laboratorio.service.ExamenesService;
import ni.gob.minsa.laboratorio.service.SeguridadService;
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
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Miguel Salinas on 25/07/2019.
 * V1.0
 */
@Controller
@RequestMapping("administracion/equipos/")
public class EquiposProcesamientoController {

    private static final Logger logger = LoggerFactory.getLogger(EquiposProcesamientoController.class);

    @Resource(name = "equiposProcesamientoService")
    private EquiposProcesamientoService equiposProcesamientoService;

    @Resource(name = "seguridadService")
    private SeguridadService seguridadService;

    @Resource(name = "examenesService")
    private ExamenesService examenesService;

    @Autowired
    MessageSource messageSource;

    @RequestMapping(value = "list", method = RequestMethod.GET)
    public String obtenerUsuarios() throws ParseException {
        logger.debug("Mostrando equipos en JSP");
        return "administracion/catalogos/processingEquipment";
    }

    @RequestMapping(value = "getEquiposProcesamiento", method = RequestMethod.GET, produces = "application/json")
    public   @ResponseBody
    List<EquiposProcesamiento> getEquiposProcesamiento() throws Exception {
        logger.info("Realizando búsqueda de todos los equipos");
        return equiposProcesamientoService.getEquipos();
    }

    @RequestMapping(value = "getEquipoProcesamiento", method = RequestMethod.GET, produces = "application/json")
    public   @ResponseBody
    EquiposProcesamiento getEquiposProcesamiento(@RequestParam(value = "idEquipo", required = true) int idEquipo) throws Exception {
        logger.info("Realizando búsqueda de equipo "+idEquipo);
        return equiposProcesamientoService.getEquipo(idEquipo);
    }

    @RequestMapping(value = "getExamenesEquipo", method = RequestMethod.GET, produces = "application/json")
    public   @ResponseBody
    List<Examen_Equipo> getExamenesEquipo(@RequestParam(value = "idEquipo", required = true) int idEquipo) throws Exception {
        logger.info("Realizando búsqueda de equipo "+idEquipo);
        return equiposProcesamientoService.getExamenesEquipo(idEquipo);
    }

    @RequestMapping(value = "getExamenesDisponiblesEquipo", method = RequestMethod.GET, produces = "application/json")
    public   @ResponseBody
    List<CatalogoExamenes> getExamenesDisponiblesEquipo(@RequestParam(value = "idEquipo", required = true) int idEquipo) throws Exception {
        logger.info("Realizando búsqueda de equipo "+idEquipo);
        return equiposProcesamientoService.getExamenesDisponiblesEquipo(idEquipo);
    }

    @RequestMapping(value = "save", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void save(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        boolean habilitado=false;
        String nombre="";
        String marca="";
        String modelo="";
        String descripcion="";
        Integer idEquipo = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            if (jsonpObject.get("idEquipo") != null && !jsonpObject.get("idEquipo").getAsString().isEmpty())
                idEquipo = jsonpObject.get("idEquipo").getAsInt();
            nombre = jsonpObject.get("nombre").getAsString();
            marca = jsonpObject.get("marca").getAsString();
            modelo = jsonpObject.get("modelo").getAsString();
            descripcion = jsonpObject.get("descripcion").getAsString();
            habilitado = jsonpObject.get("habilitado").getAsBoolean();

            EquiposProcesamiento equipo;
            //existe
            if (idEquipo != null){
                equipo = equiposProcesamientoService.getEquipo(idEquipo);
                equipo.setNombre(nombre);
                equipo.setMarca(marca);
                equipo.setModelo(modelo);
                equipo.setDescripcion(descripcion);
                equipo.setPasivo(!habilitado);
            } else { //es nuevo
                equipo = new EquiposProcesamiento();
                equipo.setNombre(nombre);
                equipo.setMarca(marca);
                equipo.setModelo(modelo);
                equipo.setDescripcion(descripcion);
                equipo.setPasivo(!habilitado);
                equipo.setFechaRegistro(new Date());
                equipo.setUsuarioRegistro(seguridadService.getUsuario(seguridadService.obtenerNombreUsuario()));
            }
            //se registra o actualiza
            equiposProcesamientoService.saveEquipo(equipo);

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.error.save.equipment",null,null);
            resultado=resultado+". \n "+ex.getMessage();
        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("nombre", nombre);
            map.put("idEquipo", String.valueOf(idEquipo));
            map.put("habilitado",String.valueOf(habilitado));
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = "saveExamenEquipo", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void saveExamenEquipo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        Integer idEquipo = null;
        Integer idExamen = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            idEquipo = jsonpObject.get("idEquipo").getAsInt();
            idExamen = jsonpObject.get("idExamen").getAsInt();

            EquiposProcesamiento equipo = equiposProcesamientoService.getEquipo(idEquipo);
            CatalogoExamenes examen = examenesService.getExamenById(idExamen);

            Examen_Equipo examenEquipo = new Examen_Equipo();
            examenEquipo.setEquipo(equipo);
            examenEquipo.setExamen(examen);
            examenEquipo.setPasivo(false);
            examenEquipo.setFechaRegistro(new Date());
            examenEquipo.setUsuarioRegistro(seguridadService.getUsuario(seguridadService.obtenerNombreUsuario()));

            //se registra o actualiza
            equiposProcesamientoService.saveExamenEquipo(examenEquipo);

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.error.save.equipment.test",null,null);
            resultado=resultado+". \n "+ex.getMessage();
        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("idEquipo", String.valueOf(idEquipo));
            map.put("idExamen",String.valueOf(idExamen));
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = "override", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void override(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        Integer idEquipo=null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            idEquipo = jsonpObject.get("idEquipo").getAsInt();
            EquiposProcesamiento equipo = equiposProcesamientoService.getEquipo(idEquipo);
            if (equipo!=null) {
                equipo.setPasivo(true);
                equiposProcesamientoService.saveEquipo(equipo);
            }else{
                throw new Exception(messageSource.getMessage("msg.not.found.equipment",null,null));
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.override.equipment.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("idEquipo",String.valueOf(idEquipo));
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = "overrideTest", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void overrideTest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        Integer idExamenEquipo=null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            idExamenEquipo = jsonpObject.get("idExamenEquipo").getAsInt();
            int aplicado = equiposProcesamientoService.anularExamenEquipo(idExamenEquipo);
            if (aplicado<=0) {
                throw new Exception(messageSource.getMessage("msg.not.found.equipment",null,null));
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.override.equipment.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("idExamenEquipo",String.valueOf(idExamenEquipo));
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

}
