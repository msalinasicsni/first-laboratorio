package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.examen.Departamento;
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
@RequestMapping("administracion/departamento")
public class DepartamentosController {
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
    public String initForm() throws ParseException {
        logger.debug("Mostrando departamentos en JSP");
        return "administracion/catalogos/department";
    }

    @RequestMapping(value = "getDepartments", method = RequestMethod.GET, produces = "application/json")
    public   @ResponseBody
    List<Departamento> obtenerDepartamentos() throws Exception {
        logger.info("Realizando búsqueda de todos los departamentos");
        return laboratoriosService.getDepartamentos();
    }

    @RequestMapping(value = "getDepartment", method = RequestMethod.GET, produces = "application/json")
    public   @ResponseBody
    Departamento obtenerDepartamento(@RequestParam(value = "idDepartamento", required = true) int idDepartamento) throws Exception {
        logger.info("Realizando búsqueda de departamento "+idDepartamento);
        return laboratoriosService.getDepartamentoById(idDepartamento);
    }

    @RequestMapping(value = "save", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void guardarDepartamento(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        boolean habilitado=false;
        String nombre="";
        Integer idDepartamento = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            if (jsonpObject.get("idDepartamento") != null && !jsonpObject.get("idDepartamento").getAsString().isEmpty())
                idDepartamento = jsonpObject.get("idDepartamento").getAsInt();
            nombre = jsonpObject.get("nombre").getAsString();
            habilitado = jsonpObject.get("habilitado").getAsBoolean();

            Departamento departamento;
            //existe departamento
            if (idDepartamento != null){
                departamento = laboratoriosService.getDepartamentoById(idDepartamento);
                departamento.setNombre(nombre);
                departamento.setPasivo(!habilitado);
            } else { //es nuevo departamento
                departamento = new Departamento();
                departamento.setNombre(nombre);
                departamento.setPasivo(!habilitado);
                departamento.setFechaRegistro(new Date());
                departamento.setUsuarioRegistro(seguridadService.getUsuario(seguridadService.obtenerNombreUsuario()));
            }
            //se registra o actualiza departamento
            laboratoriosService.saveDepartamento(departamento);

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.error.save.department",null,null);
            resultado=resultado+". \n "+ex.getMessage();
        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("nombre", nombre);
            map.put("idDepartamento", String.valueOf(idDepartamento));
            map.put("habilitado",String.valueOf(habilitado));
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = "override", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void anularDepartamento(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        Integer idDepartamento=null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            idDepartamento = jsonpObject.get("idDepartamento").getAsInt();
            Departamento departamento = laboratoriosService.getDepartamentoById(idDepartamento);
            if (departamento!=null) {
                departamento.setPasivo(true);
                laboratoriosService.saveDepartamento(departamento);
            }else{
                throw new Exception(messageSource.getMessage("msg.not.found.department",null,null));
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.override.department.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("idDepartamento",String.valueOf(idDepartamento));
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }
}
