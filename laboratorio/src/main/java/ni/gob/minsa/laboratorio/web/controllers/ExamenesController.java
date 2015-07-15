package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.examen.Area;
import ni.gob.minsa.laboratorio.domain.examen.CatalogoExamenes;
import ni.gob.minsa.laboratorio.service.AreaService;
import ni.gob.minsa.laboratorio.service.ExamenesService;
import ni.gob.minsa.laboratorio.service.SeguridadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by FIRSTICT on 7/10/2015.
 * V1.0
 */
@Controller
@RequestMapping("administracion/examenes")
public class ExamenesController {
    private static final Logger logger = LoggerFactory.getLogger(UsuariosController.class);

    @Autowired
    @Qualifier(value = "seguridadService")
    private SeguridadService seguridadService;


    @Autowired
    @Qualifier(value = "examenesService")
    private ExamenesService examenesService;

    @Autowired
    @Qualifier(value = "areaService")
    private AreaService areaService;

    @Autowired
    MessageSource messageSource;

    @RequestMapping(value = "list", method = RequestMethod.GET)
    public String obtenerUsuarios(Model model) throws ParseException {
        logger.debug("Mostrando examenes en JSP");
        List<Area> areaList = areaService.getAreas();
        model.addAttribute("areas",areaList);
        return "administracion/catalogos/tests";
    }

    @RequestMapping(value = "getTests", method = RequestMethod.GET, produces = "application/json")
    public   @ResponseBody
    List<CatalogoExamenes> obtenerAreas() throws Exception {
        logger.info("Realizando búsqueda de todas los examenes");
        return examenesService.getExamenes();
    }

    @RequestMapping(value = "getTest", method = RequestMethod.GET, produces = "application/json")
    public   @ResponseBody
    CatalogoExamenes obtenerExamen(@RequestParam(value = "idExamen", required = true) int idExamen) throws Exception {
        logger.info("Realizando búsqueda de examen "+idExamen);
        return examenesService.getExamenById(idExamen);
    }

    @RequestMapping(value = "save", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void agregarExamen(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        boolean habilitado=false;
        String nombre="";
        Float precio=null;
        Integer idArea = 0;
        Integer idExamen = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            nombre = jsonpObject.get("nombre").getAsString();
            if (jsonpObject.get("precio")!=null && !jsonpObject.get("precio").getAsString().isEmpty())
                precio = jsonpObject.get("precio").getAsFloat();
            if (jsonpObject.get("idExamen")!=null && !jsonpObject.get("idExamen").getAsString().isEmpty())
                idExamen = jsonpObject.get("idExamen").getAsInt();
            habilitado = jsonpObject.get("habilitado").getAsBoolean();
            idArea = jsonpObject.get("idArea").getAsInt();

            Area area = areaService.getArea(idArea);
            CatalogoExamenes examen;
            if (idExamen!=null){
                examen = examenesService.getExamenById(idExamen);
                examen.setArea(area);
                examen.setPasivo(!habilitado);
                examen.setNombre(nombre);
                examen.setPrecio(precio);
            }else {
                examen = new CatalogoExamenes();
                examen.setPasivo(!habilitado);
                examen.setNombre(nombre);
                examen.setPrecio(precio);
                examen.setFechaRegistro(new Timestamp(new Date().getTime()));
                examen.setArea(area);
                examen.setUsuarioRegistro(seguridadService.getUsuario(seguridadService.obtenerNombreUsuario()));
            }
            this.examenesService.saveExamen(examen);

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.test.add.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();
        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("nombre", nombre);
            map.put("precio", String.valueOf(precio));
            map.put("habilitado",String.valueOf(habilitado));
            map.put("mensaje",resultado);
            map.put("idArea",idArea.toString());
            map.put("idExamen",String.valueOf(idExamen));
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = "override", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void deshabilitarExamen(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        Integer idExamen = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            if (jsonpObject.get("idExamen")!=null && !jsonpObject.get("idExamen").getAsString().isEmpty())
                idExamen = jsonpObject.get("idExamen").getAsInt();
            CatalogoExamenes examen = examenesService.getExamenById(idExamen);
            if (examen!=null){
                examen.setPasivo(true);
                this.examenesService.saveExamen(examen);
            }else {
               throw new Exception(messageSource.getMessage("msg.test.not.found",null,null));
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.test.add.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();
        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("mensaje",resultado);
            map.put("idExamen",String.valueOf(idExamen));
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }
}
