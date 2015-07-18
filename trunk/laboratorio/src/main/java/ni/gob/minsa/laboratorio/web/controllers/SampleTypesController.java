package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.muestra.TipoMx;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.User;
import ni.gob.minsa.laboratorio.service.SampleTypesService;
import ni.gob.minsa.laboratorio.service.SeguridadService;
import ni.gob.minsa.laboratorio.utilities.ConstantsSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
@RequestMapping("/administracion/sampleTypes")
public class SampleTypesController {

    private static final Logger logger = LoggerFactory.getLogger(SampleTypesController.class);

    @Autowired
    @Qualifier(value = "seguridadService")
    private SeguridadService seguridadService;

    @Resource(name = "sampleTypesService")
    public SampleTypesService sampleTypesService;


    @Autowired
    MessageSource messageSource;

    @RequestMapping(value = "init", method = RequestMethod.GET)
    public ModelAndView initForm(HttpServletRequest request) throws Exception {
        logger.debug("Cargando Conceptos");
        String urlValidacion="";
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
            List<TipoMx> samplesList =  getSampleTypes();
            mav.addObject("samplesList",samplesList);
            mav.setViewName("administracion/catalogos/samplesTypesEnter");
        }else
            mav.setViewName(urlValidacion);

        return mav;
    }

    //Cargar lista de Tipos de Muestras
    @RequestMapping(value = "getSampleTypes", method = RequestMethod.GET,  produces = "application/json")
    public @ResponseBody
    List<TipoMx> getSampleTypes() throws Exception {
        logger.info("Obteniendo los tipos de muestra");
        List<TipoMx> samplesList = null;
        samplesList = sampleTypesService.getAllSamplesList();
        return samplesList;
    }

    @RequestMapping(value = "addUpdateSampleTypes", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void addUpdateConcept(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json = "";
        String resultado = "";
        String nombre = "";
        Integer idTipoMx = null;
        boolean pasivo = false;

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);

            nombre = jsonpObject.get("nombre").getAsString();

            pasivo = jsonpObject.get("pasivo").getAsBoolean();

            if(!jsonpObject.get("idTipoMx").getAsString().isEmpty() ){
                idTipoMx = jsonpObject.get("idTipoMx").getAsInt();
            }

            User usuario = seguridadService.getUsuario(seguridadService.obtenerNombreUsuario());

            //se obtiene el tipo de dato segun id
            TipoMx tipo;
            if(idTipoMx != null){
                tipo = sampleTypesService.getTipoMxById(idTipoMx);
                if(!nombre.equals("")){
                    tipo.setNombre(nombre);
                    tipo.setPasivo(!pasivo);
                }else{
                    tipo.setPasivo(true);
                }



            }else{
                tipo = new TipoMx();
                tipo.setFechaRegistro(new Timestamp(new Date().getTime()));
                tipo.setUsuarioRegistro(usuario);
                tipo.setNombre(nombre);
                tipo.setPasivo(!pasivo);
            }

            sampleTypesService.addOrUpdateSampleTypes(tipo);

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            ex.printStackTrace();
            resultado = messageSource.getMessage("msg.sampleType.error", null, null);
            resultado = resultado + ". \n " + ex.getMessage();

        } finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("nombre", nombre);
            map.put("mensaje", resultado);
            map.put("idTipoMx", "");
            map.put("pasivo", "");
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

}
