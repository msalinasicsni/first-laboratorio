package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.examen.Area;
import ni.gob.minsa.laboratorio.domain.examen.CatalogoExamenes;
import ni.gob.minsa.laboratorio.domain.examen.Direccion;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.AutoridadArea;
import ni.gob.minsa.laboratorio.service.AreaService;
import ni.gob.minsa.laboratorio.service.ExamenesService;
import ni.gob.minsa.laboratorio.service.LaboratoriosService;
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
@RequestMapping("administracion/direccion")
public class DireccionesController {
    private static final Logger logger = LoggerFactory.getLogger(UsuariosController.class);

    @Autowired
    @Qualifier(value = "seguridadService")
    private SeguridadService seguridadService;


    @Autowired
    @Qualifier(value = "laboratoriosService")
    private LaboratoriosService laboratoriosService;

    @Autowired
    MessageSource messageSource;

    @RequestMapping(value = "list", method = RequestMethod.GET)
    public String initForm(Model model) throws ParseException {
        logger.debug("Mostrando direcciones en JSP");
        return "administracion/catalogos/management";
    }

    @RequestMapping(value = "getManagements", method = RequestMethod.GET, produces = "application/json")
    public   @ResponseBody
    List<Direccion> obtenerDirecciones() throws Exception {
        logger.info("Realizando búsqueda de todas las direcciones");
        return laboratoriosService.getDirecciones();
    }

    @RequestMapping(value = "getManagement", method = RequestMethod.GET, produces = "application/json")
    public   @ResponseBody
    Direccion obtenerDireccion(@RequestParam(value = "idDireccion", required = true) int idDireccion) throws Exception {
        logger.info("Realizando búsqueda de direción "+idDireccion);
        return laboratoriosService.getDireccionById(idDireccion);
    }

    @RequestMapping(value = "save", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void guardarDireccion(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        boolean habilitado=false;
        String nombre="";
        Integer idDireccion = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            if (jsonpObject.get("idDireccion") != null && !jsonpObject.get("idDireccion").getAsString().isEmpty())
                idDireccion = jsonpObject.get("idDireccion").getAsInt();
            nombre = jsonpObject.get("nombre").getAsString();
            habilitado = jsonpObject.get("habilitado").getAsBoolean();

            Direccion direccion;
            //existe dirección
            if (idDireccion != null){
                direccion = laboratoriosService.getDireccionById(idDireccion);
                direccion.setNombre(nombre);
                direccion.setPasivo(!habilitado);
            } else { //es nueva dirección
                direccion = new Direccion();
                direccion.setNombre(nombre);
                direccion.setPasivo(!habilitado);
                direccion.setFechaRegistro(new Date());
                direccion.setUsuarioRegistro(seguridadService.getUsuario(seguridadService.obtenerNombreUsuario()));
            }
            //se registra o actualiza dirección
            laboratoriosService.saveDireccion(direccion);

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.error.save.management",null,null);
            resultado=resultado+". \n "+ex.getMessage();
        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("nombre", nombre);
            map.put("idDireccion", String.valueOf(idDireccion));
            map.put("habilitado",String.valueOf(habilitado));
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = "override", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void anularDireccion(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        Integer idDireccion=null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            idDireccion = jsonpObject.get("idDireccion").getAsInt();
            Direccion direccion = laboratoriosService.getDireccionById(idDireccion);
            if (direccion!=null) {
                direccion.setPasivo(true);
                laboratoriosService.saveDireccion(direccion);
            }else{
                throw new Exception(messageSource.getMessage("msg.not.found.management",null,null));
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.override.management.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("idDireccion",String.valueOf(idDireccion));
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }
}
