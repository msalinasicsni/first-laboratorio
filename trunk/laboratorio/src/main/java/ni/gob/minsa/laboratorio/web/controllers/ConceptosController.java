package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
import ni.gob.minsa.laboratorio.domain.concepto.Catalogo_Lista;
import ni.gob.minsa.laboratorio.domain.concepto.Concepto;
import ni.gob.minsa.laboratorio.domain.concepto.TipoDatoCatalogo;
import ni.gob.minsa.laboratorio.service.*;
import ni.gob.minsa.laboratorio.utilities.ConstantsSecurity;
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
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by souyen-ics.
 */
@Controller
@RequestMapping("administracion/conceptos")
public class ConceptosController {

    private static final Logger logger = LoggerFactory.getLogger(ConceptosController.class);

    @Resource(name = "seguridadService")
    private SeguridadService seguridadService;

    @Resource(name = "catalogosService")
    private CatalogoService catalogoService;

    @Resource(name = "usuarioService")
    private UsuarioService usuarioService;

    @Resource(name="conceptoService")
    private ConceptoService conceptoService;

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
            List<Concepto> conceptsList =  getConcepts();
            List<TipoDatoCatalogo> dataTypeCat = catalogoService.getTipoDatoCatalogo();
            mav.addObject("conceptsList",conceptsList);
            mav.addObject("dataTypeCat",dataTypeCat);
            mav.setViewName("administracion/conceptsEnter");
        }else
            mav.setViewName(urlValidacion);

        return mav;
    }

    //Cargar lista de Tipos de Datos
    @RequestMapping(value = "getConcepts", method = RequestMethod.GET,  produces = "application/json")
    public @ResponseBody List<Concepto> getConcepts() throws Exception {
        logger.info("Obteniendo los tipos de Datos");

        List<Concepto> conceptsList = null;
        conceptsList = conceptoService.getConceptsList(false);
        return conceptsList;
    }

    @RequestMapping(value = "addUpdateConcept", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void addUpdateConcept(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json = "";
        String resultado = "";
        String nombre = "";
        String tipo = "";
        Integer idConcepto = null;
        String pasivo = "";

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);

            if(jsonpObject.get("nombre")!=null && !jsonpObject.get("nombre").getAsString().isEmpty()) {
                nombre = jsonpObject.get("nombre").getAsString();
            }
            if(jsonpObject.get("tipo")!=null && !jsonpObject.get("tipo").getAsString().isEmpty()) {
                tipo = jsonpObject.get("tipo").getAsString();
            }

            if(!jsonpObject.get("pasivo").getAsString().isEmpty()){
                pasivo = jsonpObject.get("pasivo").getAsString();
            }

            if(!jsonpObject.get("idConcepto").getAsString().isEmpty() ){
                idConcepto = jsonpObject.get("idConcepto").getAsInt();
            }

            long idUsuario = seguridadService.obtenerIdUsuario(request);
            Usuarios usuario = usuarioService.getUsuarioById((int) idUsuario);


            //se obtiene el tipo de dato segun id
            Concepto concept;
            if(idConcepto != null){
             concept = conceptoService.getConceptById(idConcepto);

                if(!pasivo.isEmpty()){
                    concept.setPasivo(true);

                }else{
                    concept.setPasivo(false);
                }

            }else{
                concept = new Concepto();
                concept.setFechahRegistro(new Timestamp(new Date().getTime()));
                concept.setUsuarioRegistro(usuario);
            }

            if(!nombre.isEmpty()){
                concept.setNombre(nombre);
            }

           if(!tipo.isEmpty()){
               // se obtiene catalago de Concepto por el codigo
               TipoDatoCatalogo cat = catalogoService.getTipoDatoCatalogo(tipo);
               concept.setTipo(cat);
           }

            conceptoService.addOrUpdateConcept( concept);

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            ex.printStackTrace();
            resultado = messageSource.getMessage("msg.dataType.error", null, null);
            resultado = resultado + ". \n " + ex.getMessage();

        } finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("nombre", nombre);
            map.put("mensaje", resultado);
            map.put("tipo", tipo);
            map.put("idConcepto", "");
            map.put("pasivo", "");
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    //Cargar lista de Tipos de Datos
    @RequestMapping(value = "getValuesCat", method = RequestMethod.GET,  produces = "application/json")
    public @ResponseBody List<Catalogo_Lista> getValuesCat(@RequestParam(value = "idConcepto", required = true) Integer idConcepto) throws Exception {
        logger.info("Obteniendo los valores de la lista");

        List<Catalogo_Lista> valuesList = null;
        valuesList = conceptoService.getValuesByIdConcepto(idConcepto);
        return valuesList;
    }

    @RequestMapping(value = "addUpdateValue", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void addUpdateValue(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json = "";
        String resultado = "";
        String valor = "";
        String pasivo = "";
        Integer idConcepto = null;
        Integer idCatalogoLista = null;

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);

            valor = jsonpObject.get("valor").getAsString();


            if(!jsonpObject.get("pasivo").getAsString().isEmpty()){
                pasivo = jsonpObject.get("pasivo").getAsString();
            }

            if(!jsonpObject.get("idConcepto").getAsString().isEmpty() ){
                idConcepto = jsonpObject.get("idConcepto").getAsInt();
            }

            if(!jsonpObject.get("idCatalogoLista").getAsString().isEmpty() ){
                idCatalogoLista = jsonpObject.get("idCatalogoLista").getAsInt();
            }


            long idUsuario = seguridadService.obtenerIdUsuario(request);
            Usuarios usuario = usuarioService.getUsuarioById((int) idUsuario);


            //se obtiene el concepto segun id
            Catalogo_Lista value;
            if(idCatalogoLista != null){
                value = conceptoService.getCatalogoListaById(idCatalogoLista);
               if(!pasivo.isEmpty()){
                   value.setPasivo(true);

               }else{
                   value.setPasivo(false);
               }

            }else{

                value = new Catalogo_Lista();
                Concepto concepto = conceptoService.getConceptById(idConcepto);
                value.setIdConcepto(concepto);
                value.setFechaHRegistro(new Timestamp(new Date().getTime()));
                value.setUsarioRegistro(usuario);

            }
            if(!valor.isEmpty()){
                value.setValor(valor);
            }


            conceptoService.addOrUpdateValue(value);

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            ex.printStackTrace();
            resultado = messageSource.getMessage("msg.cat.error", null, null);
            resultado = resultado + ". \n " + ex.getMessage();

        } finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("valor", valor);
            map.put("mensaje", resultado);
            map.put("idCatalogoLista", "");
            map.put("pasivo", pasivo);
            map.put("idConcepto", String.valueOf(idConcepto));
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

}
