package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.solicitante.Solicitante;
import ni.gob.minsa.laboratorio.service.SeguridadService;
import ni.gob.minsa.laboratorio.service.SolicitanteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by FIRSTICT on 8/4/2015.
 * V1.0
 */
@Controller
@RequestMapping("solicitante")
public class SolicitanteController {
    private static final Logger logger = LoggerFactory.getLogger(SolicitanteController.class);
    @Autowired
    MessageSource messageSource;

    @Resource(name = "solicitanteService")
    private SolicitanteService solicitanteService;

    @Resource(name = "seguridadService")
    private SeguridadService seguridadService;

    @RequestMapping(value = "search", method = RequestMethod.GET)
    public String initSearchForm() throws ParseException {
        logger.debug("Buscar una Persona");
        return  "solicitantes/search";
    }

    @RequestMapping(value = "search/{idSolicitante}", method = RequestMethod.GET)
    public ModelAndView showPersonReport(@PathVariable("idSolicitante") String idSolicitante) throws Exception {
        ModelAndView mav = new ModelAndView();
        Solicitante solicitante = solicitanteService.getSolicitanteById(idSolicitante);
        mav.setViewName("solicitantes/search");
        mav.addObject("solicitante",solicitante);
        return mav;
    }

    /**
     * Retorna una lista de personas. Acepta una solicitud GET para JSON
     * @return Un arreglo JSON de personas
     */
    @RequestMapping(value = "getSolicitantes", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    List<Solicitante> fetchPersonasJson(@RequestParam(value = "strFilter", required = true) String filtro) {
        logger.info("Obteniendo las personas en JSON");
        List<Solicitante> solicitantes = solicitanteService.getSolicitantes(filtro);
        if (solicitantes == null){
            logger.debug("Nulo");
        }
        return solicitantes;
    }
    @RequestMapping(value = "create", method = RequestMethod.GET)
    public ModelAndView initCreateForm() throws Exception {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("solicitantes/create");
        mav.addObject("accion",messageSource.getMessage("act.add",null,null));
        return mav;
    }

    @RequestMapping(value = "update/{idSolicitante}", method = RequestMethod.GET)
    public ModelAndView initUpdateForm(@PathVariable("idSolicitante") String idSolicitante) throws Exception {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("solicitantes/create");
        Solicitante solicitante = solicitanteService.getSolicitanteById(idSolicitante);
        mav.addObject("solicitante",solicitante);
        mav.addObject("accion",messageSource.getMessage("act.edit",null,null));
        return mav;
    }

    @RequestMapping(value = "save", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void guardarSolicitante(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        String strSolicitante="";
        Solicitante solicitante;
        String idSolicitante="";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF8"));
            json = br.readLine();
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            strSolicitante = jsonpObject.get("solicitante").toString();
            solicitante = jsonToSolicitante(strSolicitante);
            solicitanteService.saveSolicitante(solicitante);
            idSolicitante = solicitante.getIdSolicitante();

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.applicant.error.add",null,null);
            resultado=resultado+". \n "+ex.getMessage();
        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("idSolicitante",idSolicitante);
            map.put("solicitante",strSolicitante);
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }
    private Solicitante jsonToSolicitante(String strSolicitante) throws UnsupportedEncodingException {
        JsonObject jsonObject = new Gson().fromJson(strSolicitante,JsonObject.class);
        Solicitante solicitante = new Solicitante();
        String idSolicitante=null;
        String nombre=null;
        String direccion=null;
        String telefono=null;
        String contacto=null;
        String correoContacto=null;
        String telefonoContacto=null;
        Boolean habilitado;

        if (jsonObject.get("idSolicitante")!=null && !jsonObject.get("idSolicitante").getAsString().isEmpty())
            idSolicitante = jsonObject.get("idSolicitante").getAsString();

        if (jsonObject.get("nombre")!=null && !jsonObject.get("nombre").getAsString().isEmpty())
            nombre = jsonObject.get("nombre").getAsString();

        if (jsonObject.get("direccion")!=null && !jsonObject.get("direccion").getAsString().isEmpty())
            direccion = URLDecoder.decode(jsonObject.get("direccion").getAsString(), "utf-8");

        if (jsonObject.get("telefono")!=null && !jsonObject.get("telefono").getAsString().isEmpty())
            telefono = jsonObject.get("telefono").getAsString();

        if (jsonObject.get("contacto")!=null && !jsonObject.get("contacto").getAsString().isEmpty())
            contacto = jsonObject.get("contacto").getAsString();

        if (jsonObject.get("correoContacto")!=null && !jsonObject.get("correoContacto").getAsString().isEmpty())
            correoContacto = jsonObject.get("correoContacto").getAsString();

        if (jsonObject.get("telefonoContacto")!=null && !jsonObject.get("telefonoContacto").getAsString().isEmpty())
            telefonoContacto = jsonObject.get("telefonoContacto").getAsString();

        habilitado = jsonObject.get("habilitado").getAsBoolean();

        solicitante.setIdSolicitante(idSolicitante);
        solicitante.setNombre(URLDecoder.decode(nombre, "utf-8"));
        solicitante.setDireccion(direccion);
        solicitante.setTelefono(telefono);
        solicitante.setNombreContacto(URLDecoder.decode(contacto, "utf-8"));
        solicitante.setCorreoContacto(correoContacto);
        solicitante.setTelefonoContacto(telefonoContacto);
        solicitante.setPasivo(!habilitado);
        if (solicitante.getIdSolicitante()==null) { //es nuevo
            solicitante.setFechahRegistro(new Timestamp(new Date().getTime()));
            solicitante.setUsuarioRegistro(seguridadService.getUsuario(seguridadService.obtenerNombreUsuario()));
        }

        return solicitante;
    }
}
