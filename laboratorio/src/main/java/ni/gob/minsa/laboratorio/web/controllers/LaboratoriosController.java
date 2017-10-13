package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.estructura.EntidadesAdtvas;
import ni.gob.minsa.laboratorio.domain.examen.EntidadAdtvaLaboratorio;
import ni.gob.minsa.laboratorio.domain.muestra.Laboratorio;
import ni.gob.minsa.laboratorio.service.EntidadAdmonService;
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
@RequestMapping("administracion/laboratorio")
public class LaboratoriosController {
    private static final Logger logger = LoggerFactory.getLogger(UsuariosController.class);

    @Autowired
    @Qualifier(value = "seguridadService")
    private SeguridadService seguridadService;

    @Autowired
    @Qualifier(value = "laboratoriosService")
    private LaboratoriosService laboratoriosService;

    @Autowired
    @Qualifier(value = "entidadAdmonService")
    private EntidadAdmonService entidadAdmonService;

    @Autowired
    MessageSource messageSource;

    @RequestMapping(value = "list", method = RequestMethod.GET)
    public String initForm(Model model) throws ParseException {
        logger.debug("Mostrando direcciones en JSP");
        return "administracion/catalogos/laboratorio";
    }

    @RequestMapping(value = "getLaboratorios", method = RequestMethod.GET, produces = "application/json")
    public   @ResponseBody
    List<Laboratorio> obtenerLaboratorios() throws Exception {
        logger.info("Realizando búsqueda de todas los laboratorios");
        return laboratoriosService.getLaboratoriosRegionales();
    }

    @RequestMapping(value = "getSILAIS", method = RequestMethod.GET, produces = "application/json")
    public   @ResponseBody
    List<EntidadAdtvaLaboratorio> obtenerSILAIS(@RequestParam(value = "codigo", required = true) String codigo) throws Exception {
        logger.info("Realizando búsqueda de todos los SILAIS que estan asociadas a un laboratorio");
        return laboratoriosService.getEntidadesAdtvasLab(codigo);
    }

    @RequestMapping(value = "getSILAISDisponibles", method = RequestMethod.GET, produces = "application/json")
    public   @ResponseBody
    List<EntidadesAdtvas> obtenerSILAISDisponibles(@RequestParam(value = "codigo", required = true) String codigo) throws Exception {
        logger.info("Realizando búsqueda de todos los SILAIS que estan disponibles para un laboratorio");
        return laboratoriosService.getEntidadesAdtvasDisponiblesLab(codigo);
    }

    @RequestMapping(value = "getLaboratorio", method = RequestMethod.GET, produces = "application/json")
    public   @ResponseBody
    Laboratorio obtenerLaboratorio(@RequestParam(value = "codigo", required = true) String codigo) throws Exception {
        logger.info("Realizando búsqueda de laboratorio "+codigo);
        return laboratoriosService.getLaboratorioByCodigo(codigo);
    }

    @RequestMapping(value = "save", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void guardarLaboratorio(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        boolean habilitado=false;
        String nombre="";
        String codigo="";
        String descripcion=null;
        String direccion=null;
        String telefono=null;
        String fax=null;
        String edicion="";
        boolean popUpCodigoMx = false;

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            if (jsonpObject.get("codigo") != null && !jsonpObject.get("codigo").getAsString().isEmpty())
                codigo = jsonpObject.get("codigo").getAsString();
            nombre = jsonpObject.get("nombre").getAsString();
            edicion = jsonpObject.get("edicion").getAsString();
            if (jsonpObject.get("descripcion") != null && !jsonpObject.get("descripcion").getAsString().isEmpty())
                descripcion = jsonpObject.get("descripcion").getAsString();
            if (jsonpObject.get("direccion") != null && !jsonpObject.get("direccion").getAsString().isEmpty())
                direccion = jsonpObject.get("direccion").getAsString();
            if (jsonpObject.get("telefono") != null && !jsonpObject.get("telefono").getAsString().isEmpty())
                telefono = jsonpObject.get("telefono").getAsString();
            if (jsonpObject.get("fax") != null && !jsonpObject.get("fax").getAsString().isEmpty())
                fax = jsonpObject.get("fax").getAsString();
            if (jsonpObject.get("habilitado") != null && !jsonpObject.get("habilitado").getAsString().isEmpty())
                habilitado = jsonpObject.get("habilitado").getAsBoolean();
            if (jsonpObject.get("popUpCodigoMx") != null && !jsonpObject.get("popUpCodigoMx").getAsString().isEmpty())
                popUpCodigoMx = jsonpObject.get("popUpCodigoMx").getAsBoolean();

            Laboratorio laboratorio;
            //existe laboratorio
            if (edicion.equals("si")){
                laboratorio = laboratoriosService.getLaboratorioByCodigo(codigo);
                laboratorio.setNombre(nombre);
                laboratorio.setDescripcion(descripcion);
                laboratorio.setDireccion(direccion);
                laboratorio.setTelefono(telefono);
                laboratorio.setTelefax(fax);
                laboratorio.setPasivo(!habilitado);
                laboratorio.setPopUpCodigoMx(popUpCodigoMx);
            } else { //es nuevo laboratorio
                laboratorio = new Laboratorio();
                laboratorio.setCodigo(codigo);
                laboratorio.setNombre(nombre);
                laboratorio.setDescripcion(descripcion);
                laboratorio.setDireccion(direccion);
                laboratorio.setTelefono(telefono);
                laboratorio.setTelefax(fax);
                laboratorio.setCodTipo("REG");
                laboratorio.setPasivo(!habilitado);
                laboratorio.setFechaRegistro(new Date());
                laboratorio.setUsuarioRegistro(seguridadService.getUsuario(seguridadService.obtenerNombreUsuario()));
                laboratorio.setPopUpCodigoMx(popUpCodigoMx);
            }
            //se registra o actualiza dirección
            laboratoriosService.saveLaboratorio(laboratorio);

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.error.save.labo",null,null);
            resultado=resultado+". \n "+ex.getMessage();
        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("nombre", nombre);
            map.put("codigo", codigo);
            map.put("descripcion", descripcion!=null?descripcion:"");
            map.put("direccion", direccion!=null?direccion:"");
            map.put("telefono", telefono!=null?fax:"");
            map.put("fax", fax!=null?fax:"");
            map.put("habilitado",String.valueOf(habilitado));
            map.put("popUpCodigoMx",String.valueOf(popUpCodigoMx));
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = "override", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void anularLaboratorio(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        String codigo="";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            if (jsonpObject.get("codigo") != null && !jsonpObject.get("codigo").getAsString().isEmpty())
                codigo = jsonpObject.get("codigo").getAsString();
            Laboratorio laboratorio = laboratoriosService.getLaboratorioByCodigo(codigo);
            if (laboratorio!=null) {
                laboratorio.setPasivo(true);
                laboratoriosService.saveLaboratorio(laboratorio);
            }else{
                throw new Exception(messageSource.getMessage("msg.not.found.labo",null,null));
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.override.labo.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("codigo",codigo);
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = "saveSILAIS", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void guardarSILAISLaboratorio(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        String codigo="";
        Integer codigoSILAIS=null;

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            if (jsonpObject.get("codigo") != null && !jsonpObject.get("codigo").getAsString().isEmpty())
                codigo = jsonpObject.get("codigo").getAsString();
            if (jsonpObject.get("codigoSILAIS") != null && !jsonpObject.get("codigoSILAIS").getAsString().isEmpty())
                codigoSILAIS = jsonpObject.get("codigoSILAIS").getAsInt();

            EntidadAdtvaLaboratorio entidadAdtvaLaboratorio = new EntidadAdtvaLaboratorio();

            EntidadesAdtvas entidadesAdtvas = entidadAdmonService.getSilaisByCodigo(codigoSILAIS);
            if (entidadesAdtvas == null){
                throw new Exception(messageSource.getMessage("msg.not.found.SILAIS.labo",null,null));
            }
            Laboratorio laboratorio = laboratoriosService.getLaboratorioByCodigo(codigo);
            if (laboratorio == null){
                throw new Exception(messageSource.getMessage("msg.not.found.labo",null,null));
            }
            entidadAdtvaLaboratorio.setLaboratorio(laboratorio);
            entidadAdtvaLaboratorio.setEntidadAdtva(entidadesAdtvas);
            entidadAdtvaLaboratorio.setPasivo(false);
            entidadAdtvaLaboratorio.setFechaRegistro(new Date());
            entidadAdtvaLaboratorio.setUsuarioRegistro(seguridadService.getUsuario(seguridadService.obtenerNombreUsuario()));

            //se registra o actualiza dirección
            laboratoriosService.saveEntidadAdtvaLaboratorio(entidadAdtvaLaboratorio);

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.error.save.SILAIS.labo",null,null);
            resultado=resultado+". \n "+ex.getMessage();
        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("codigo", codigo);
            map.put("codigoSILAIS", String.valueOf(codigoSILAIS));
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = "overrideSILAIS", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void anularSILAISLaboratorio(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        Integer idEntidadLab=null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            if (jsonpObject.get("idEntidadLab") != null && !jsonpObject.get("idEntidadLab").getAsString().isEmpty())
                idEntidadLab = jsonpObject.get("idEntidadLab").getAsInt();
            EntidadAdtvaLaboratorio entidadAdtvaLaboratorio = laboratoriosService.getEntidadAdtvaLaboratorio(idEntidadLab);
            if (entidadAdtvaLaboratorio!=null) {
                entidadAdtvaLaboratorio.setPasivo(true);
                laboratoriosService.saveEntidadAdtvaLaboratorio(entidadAdtvaLaboratorio);
            }else{
                throw new Exception(messageSource.getMessage("msg.not.found.SILAIS.labo",null,null));
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.override.SILAIS.labo.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("idEntidadLab",String.valueOf(idEntidadLab));
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }
}
