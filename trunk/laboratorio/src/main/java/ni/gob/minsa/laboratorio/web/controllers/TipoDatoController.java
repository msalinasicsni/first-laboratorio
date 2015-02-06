package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
import ni.gob.minsa.laboratorio.domain.resultados.Catalogo_Lista;
import ni.gob.minsa.laboratorio.domain.resultados.TipoDato;
import ni.gob.minsa.laboratorio.domain.resultados.TipoDatoCatalogo;
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
@RequestMapping("tipoDato")
public class TipoDatoController {

    private static final Logger logger = LoggerFactory.getLogger(TipoDatoController.class);

    @Resource(name = "seguridadService")
    private SeguridadService seguridadService;

    @Resource(name = "catalogosService")
    private CatalogoService catalogoService;

    @Resource(name = "tomaMxService")
    private TomaMxService tomaMxService;

    @Resource(name = "unidadesService")
    private UnidadesService unidadesService;

    @Resource(name = "entidadAdmonService")
    private EntidadAdmonService entidadAdmonService;

    @Resource(name = "recepcionMxService")
    private RecepcionMxService recepcionMxService;

    @Resource(name = "ordenExamenMxService")
    private OrdenExamenMxService ordenExamenMxService;

    @Resource(name="generacionAlicuotaService")
    private GeneracionAlicuotaService generacionAlicuotaService;

    @Resource(name = "usuarioService")
    private UsuarioService usuarioService;

    @Resource(name= "alicuotaService")
    private AlicuotaService alicuotaService;

    @Resource(name="tipoDatoService")
    private TipoDatoService tipoDatoService;

    @Autowired
    MessageSource messageSource;


    @RequestMapping(value = "init", method = RequestMethod.GET)
    public ModelAndView initForm(HttpServletRequest request) throws Exception {
        logger.debug("Cargando Tipos de Datos");
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
            List<TipoDato> dataTypeList =  getDataTypes();
            List<TipoDatoCatalogo> dataTypeCat = catalogoService.getTipoDatoCatalogo();
            mav.addObject("dataTypeList",dataTypeList);
            mav.addObject("dataTypeCat",dataTypeCat);
            mav.setViewName("administracion/dataTypeEnter");
        }else
            mav.setViewName(urlValidacion);

        return mav;
    }

    //Cargar lista de Tipos de Datos
    @RequestMapping(value = "getDataTypes", method = RequestMethod.GET,  produces = "application/json")
    public @ResponseBody List<TipoDato> getDataTypes() throws Exception {
        logger.info("Obteniendo los tipos de Datos");

        List<TipoDato> dataTypeList = null;
        dataTypeList = tipoDatoService.getDataTypeList();
        return dataTypeList;
    }

    @RequestMapping(value = "addUpdateDataType", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void addUpdateDataType(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json = "";
        String resultado = "";
        String nombre = "";
        String tipo = "";
        Integer idTipoDato = null;
        String pasivo = "";

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);

            nombre = jsonpObject.get("nombre").getAsString();
            tipo = jsonpObject.get("tipo").getAsString();

            if(!jsonpObject.get("pasivo").getAsString().isEmpty()){
                pasivo = jsonpObject.get("pasivo").getAsString();
            }

            if(!jsonpObject.get("idTipoDato").getAsString().isEmpty() ){
                idTipoDato = jsonpObject.get("idTipoDato").getAsInt();
            }

            long idUsuario = seguridadService.obtenerIdUsuario(request);
            Usuarios usuario = usuarioService.getUsuarioById((int) idUsuario);


            //se obtiene el tipo de dato segun id
            TipoDato dataType;
            if(idTipoDato != null){
             dataType = tipoDatoService.getDataTypeById(idTipoDato);

                if(!pasivo.isEmpty()){
                    dataType.setPasivo(true);

                }else{
                    dataType.setPasivo(false);
                }

            }else{
             dataType = new TipoDato();
             dataType.setFechahRegistro(new Timestamp(new Date().getTime()));
             dataType.setUsuarioRegistro(usuario);
            }

            if(!nombre.isEmpty()){
                dataType.setNombre(nombre);
            }

           if(!tipo.isEmpty()){
               // se obtiene catalago de Tipo lista por el codigo
               TipoDatoCatalogo cat = catalogoService.getTipoDatoCatalogo(tipo);
               dataType.setTipo(cat);
           }

            tipoDatoService.addOrUpdateDataType(dataType);

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
            map.put("idTipoDato", "");
            map.put("pasivo", "");
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    //Cargar lista de Tipos de Datos
    @RequestMapping(value = "getValuesCat", method = RequestMethod.GET,  produces = "application/json")
    public @ResponseBody List<Catalogo_Lista> getValuesCat(@RequestParam(value = "idTipoDato", required = true) Integer idTipoDato) throws Exception {
        logger.info("Obteniendo los valores de la lista");

        List<Catalogo_Lista> valuesList = null;
        valuesList = tipoDatoService.getValuesByIdTipoDato(idTipoDato);
        return valuesList;
    }

    @RequestMapping(value = "addUpdateValue", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void addUpdateValue(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json = "";
        String resultado = "";
        String valor = "";
        String pasivo = "";
        Integer idTipoDato = null;
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

            if(!jsonpObject.get("idTipoDato").getAsString().isEmpty() ){
                idTipoDato = jsonpObject.get("idTipoDato").getAsInt();
            }

            if(!jsonpObject.get("idCatalogoLista").getAsString().isEmpty() ){
                idCatalogoLista = jsonpObject.get("idCatalogoLista").getAsInt();
            }


            long idUsuario = seguridadService.obtenerIdUsuario(request);
            Usuarios usuario = usuarioService.getUsuarioById((int) idUsuario);


            //se obtiene el tipo de dato segun id
            Catalogo_Lista value;
            if(idCatalogoLista != null){
                value = tipoDatoService.getCatalogoListaById(idCatalogoLista);
               if(!pasivo.isEmpty()){
                   value.setPasivo(true);

               }else{
                   value.setPasivo(false);
               }

            }else{

                value = new Catalogo_Lista();
                TipoDato tipoDato = tipoDatoService.getDataTypeById(idTipoDato);
                value.setIdTipoDato(tipoDato);
                value.setFechaHRegistro(new Timestamp(new Date().getTime()));
                value.setUsarioRegistro(usuario);

            }
            if(!valor.isEmpty()){
                value.setValor(valor);
            }


            tipoDatoService.addOrUpdateValue(value);

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
            map.put("idTipoDato", String.valueOf(idTipoDato));
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

}
