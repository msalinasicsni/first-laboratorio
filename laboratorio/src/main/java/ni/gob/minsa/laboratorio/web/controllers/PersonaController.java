package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.persona.*;
import ni.gob.minsa.laboratorio.domain.poblacion.Comunidades;
import ni.gob.minsa.laboratorio.domain.poblacion.Divisionpolitica;
import ni.gob.minsa.laboratorio.domain.poblacion.Paises;
import ni.gob.minsa.laboratorio.service.*;
import ni.gob.minsa.laboratorio.utilities.ConstantsSecurity;
import ni.gob.minsa.ciportal.dto.InfoResultado;
import ni.gob.minsa.ejbPersona.dto.Persona;
import ni.gob.minsa.laboratorio.utilities.DateUtil;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador web de peticiones relacionadas a SisPersona
 * 
 * @author William Aviles
 */
@Controller
@RequestMapping("personas")
public class PersonaController {
	private static final Logger logger = LoggerFactory.getLogger(PersonaController.class);
	@Resource(name="personaService")
	private PersonaService personaService;

    @Autowired
    @Qualifier(value = "seguridadService")
    private SeguridadService seguridadService;

    @Autowired
    @Qualifier(value = "divisionPoliticaService")
    private DivisionPoliticaService divisionPoliticaService;

    @Autowired
    @Qualifier(value = "comunidadesService")
    private ComunidadesService comunidadesService;

    @Autowired
    @Qualifier(value = "paisesService")
    private PaisesService paisesService;

    @Autowired
    @Qualifier(value = "catalogosService")
    private CatalogoService catalogosService;

    @Autowired
    @Qualifier(value = "ocupacionService")
    private OcupacionService ocupacionService;

    @Autowired
    MessageSource messageSource;

    @RequestMapping(value = "search", method = RequestMethod.GET)
    public String initSearchForm(Model model) throws ParseException { 	
    	logger.debug("Buscar una Persona");
    	return  "personas/search";
	}
	
	/**
     * Retorna una lista de personas. Acepta una solicitud GET para JSON
     * @return Un arreglo JSON de personas 
     */
    @RequestMapping(value = "persons", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<String> fetchPersonasJson(@RequestParam(value = "strFilter", required = true) String filtro,
                                                            @RequestParam(value = "pPaginaActual", required = true) int pPaginaActual) {
        logger.info("Obteniendo las personas en JSON");
        List<SisPersona> personas = null;
        try{
            personas = personaService.getPersonas(pPaginaActual,50,filtro); //personaService.getPersonas(filtro);
        }catch(HibernateException he){
            logger.error("HibernateException", he);
            Map<String, String> map = new HashMap<String, String>();
            map.put("mensaje", he.getMessage());
            return createJsonResponse(map);
        }catch (Exception e){
            logger.error("Exception", e);
            Map<String, String> map = new HashMap<String, String>();
            map.put("mensaje", e.getMessage());
            return createJsonResponse(map);
        }
        return createJsonResponse(personas);
    }

    private ResponseEntity<String> createJsonResponse( Object o )
    {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy").create();
        String json = gson.toJson( o );
        return new ResponseEntity<String>( json, headers, HttpStatus.CREATED );
    }

    @RequestMapping(value = "search/{idPerson}", method = RequestMethod.GET)
    public ModelAndView showPersonReport(@PathVariable("idPerson") long idPerson) throws Exception {
        ModelAndView mav = new ModelAndView();
        Persona persona = personaService.buscarPorId(idPerson);
        mav.setViewName("personas/search");
        mav.addObject("persona",persona);
        int edad = DateUtil.calcularEdadAnios(persona.getFechaNacimiento());
        mav.addObject("edad", edad + " años");
        return mav;
    }
    @RequestMapping(value = "create", method = RequestMethod.GET)
    public ModelAndView initCreateForm(Model model, HttpServletRequest request) throws Exception, ParseException {
        logger.debug("Crear una Persona");
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
            mav.setViewName("personas/create");
            List <Divisionpolitica> departamentos = divisionPoliticaService.getAllDepartamentos();
            List <Paises> paisesList = paisesService.getPaises();
            List <Sexo> sexoList = catalogosService.getListaSexo();
            List <EstadoCivil> estadoCivilList = catalogosService.getListaEstadoCivil();
            List <Etnia> etniaList = catalogosService.getListaEtnia();
            List <Escolaridad> escolaridadList = catalogosService.getListaEscolaridad();
            List <Ocupacion> ocupacionList = ocupacionService.getAllOcupaciones();
            List <Identificacion> identificacionList = catalogosService.getListaTipoIdentificacion();
            List <TipoAsegurado> tipoAseguradoList = catalogosService.getTiposAsegurados();

            mav.addObject("departReside", departamentos);
            mav.addObject("paises",paisesList);
            mav.addObject("sexo",sexoList);
            mav.addObject("etnia",etniaList);
            mav.addObject("escolaridad",escolaridadList);
            mav.addObject("estadoCivil",estadoCivilList);
            mav.addObject("ocupacion",ocupacionList);
            mav.addObject("tipoIdentificacion",identificacionList);
            mav.addObject("tipoAsegurado",tipoAseguradoList);
        }else{
            mav.setViewName(urlValidacion);
        }
        return mav;
    }

    @RequestMapping(value = "update/{idPerson}", method = RequestMethod.GET)
    public ModelAndView initEditForm(@PathVariable("idPerson") long idPerson, HttpServletRequest request) throws Exception, ParseException {
        logger.debug("Crear una Persona");
        String urlValidacion="";
        try {
            urlValidacion = seguridadService.validarLogin(request);
            //si la url esta vacia significa que la validación del login fue exitosa
            if (urlValidacion.isEmpty())
                urlValidacion = seguridadService.validarAutorizacionUsuario(request, ConstantsSecurity.SYSTEM_CODE, true);
        }catch (Exception e){
            e.printStackTrace();
            urlValidacion = "404";
        }
        ModelAndView mav = new ModelAndView();
        if (urlValidacion.isEmpty()) {
            mav.setViewName("personas/create");
            List <Divisionpolitica> departamentos = divisionPoliticaService.getAllDepartamentos();
            List <Paises> paisesList = paisesService.getPaises();
            List <Sexo> sexoList = catalogosService.getListaSexo();
            List <EstadoCivil> estadoCivilList = catalogosService.getListaEstadoCivil();
            List <Etnia> etniaList = catalogosService.getListaEtnia();
            List <Escolaridad> escolaridadList = catalogosService.getListaEscolaridad();
            List <Ocupacion> ocupacionList = ocupacionService.getAllOcupaciones();
            List <Identificacion> identificacionList = catalogosService.getListaTipoIdentificacion();
            List <TipoAsegurado> tipoAseguradoList = catalogosService.getTiposAsegurados();
            Persona persona = null;
            try {
                SisPersona sisPersona = personaService.getPersona(idPerson);
                 persona = personaService.ensamblarObjetoPersona(sisPersona);//.buscarPorId(idPerson);
            }catch (Exception ex){
                logger.error(ex.getMessage());
            }
            String depaNac = "";
            String depaResi = "";
            List<Divisionpolitica> municipiosNac = null;
            if (persona !=null && persona.getMuniNacCodigoNac()!=null) {
                Divisionpolitica departamentoNac = divisionPoliticaService.getDepartamentoByMunicipi(persona.getMuniNacCodigoNac());
                depaNac = departamentoNac.getCodigoNacional();
                municipiosNac = divisionPoliticaService.getMunicipiosFromDepartamento(depaNac);
            }
            List<Divisionpolitica> municipiosResi = null;
            if (persona !=null && persona.getMuniResiCodigoNac()!=null) {
                Divisionpolitica departamentoResi = divisionPoliticaService.getDepartamentoByMunicipi(persona.getMuniResiCodigoNac());
                depaResi = departamentoResi.getCodigoNacional();
                municipiosResi = divisionPoliticaService.getMunicipiosFromDepartamento(depaResi);
            }
            List<Comunidades> comunidadesesRes = null;
            if (persona !=null && persona.getComuResiCodigo()!=null && persona.getMuniResiCodigoNac()!=null){
                comunidadesesRes = comunidadesService.getComunidades(persona.getMuniResiCodigoNac());
            }
            mav.addObject("persona",persona);
            mav.addObject("departReside", departamentos);
            mav.addObject("depaResi",depaResi);
            mav.addObject("depaNac",depaNac);
            mav.addObject("muniNac", municipiosNac);
            mav.addObject("muniResi", municipiosResi);
            mav.addObject("comunidadesesRes",comunidadesesRes);
            mav.addObject("paises",paisesList);
            mav.addObject("sexo",sexoList);
            mav.addObject("etnia",etniaList);
            mav.addObject("escolaridad",escolaridadList);
            mav.addObject("estadoCivil",estadoCivilList);
            mav.addObject("ocupacion",ocupacionList);
            mav.addObject("tipoIdentificacion",identificacionList);
            mav.addObject("tipoAsegurado",tipoAseguradoList);
        }else{
            mav.setViewName(urlValidacion);
        }
        return mav;
    }

    @RequestMapping(value = "agregarActualizarPersona", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
        protected void agregarActualizarPersona(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json = "";
        String resultado = "";
        String strPersona="";
        String idPersona = "";
        InfoResultado infoResultado;
        boolean hacerRollback = false;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            strPersona = jsonpObject.get("persona").toString();
            Persona persona = jsonToSisPersona(strPersona);

            personaService.iniciarTransaccion();
            logger.info("GUARDAR PERSONSA");
            String usuarioRegistra = seguridadService.obtenerNombreUsuario(request);
            infoResultado =  personaService.guardarPersona(persona, usuarioRegistra );
            if (infoResultado.isOk() && infoResultado.getObjeto() != null ){
                idPersona = String.valueOf(((Persona) (infoResultado.getObjeto())).getPersonaId());
            }else
                resultado = infoResultado.getMensaje();
            logger.info("FIN GUARDAR PERSONSA");
            personaService.commitTransaccion();

        } catch (Exception ex) {
            logger.error("Error guardar persona",ex);
            ex.printStackTrace();
            resultado = messageSource.getMessage("msg.person.error.add",null,null);
            resultado=resultado+". \n "+ex.getMessage();
            try {
                personaService.rollbackTransaccion();
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("Rollback error",e);
                //resultado = messageSource.getMessage("msg.person.error.unhandled",null,null);
                //resultado=resultado+". \n "+(e.getMessage()!=null?e.getMessage():"");
            }

        }finally {
            try {
                personaService.remover();
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("Cerrar conexión error",e);
                //resultado = messageSource.getMessage("msg.person.error.unhandled",null,null);
                //resultado=resultado+". \n "+(e.getMessage()!=null?e.getMessage():"");
            }
            Map<String, String> map = new HashMap<String, String>();
            map.put("idPersona", idPersona);
            map.put("mensaje",resultado);
            map.put("persona", strPersona);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    private Persona jsonToSisPersona(String strJsonPersona) throws Exception{
        JsonObject jObjectPerson = new Gson().fromJson(strJsonPersona, JsonObject.class);
        Persona persona = new Persona();
        Long idPersona = 0L; //-1 indica que es nuevo registro
        if (jObjectPerson.get("idPersona")!=null && !jObjectPerson.get("idPersona").getAsString().isEmpty())
            idPersona = jObjectPerson.get("idPersona").getAsLong();

        String primerNombre = jObjectPerson.get("primerNombre").getAsString();
        String segundoNombre = jObjectPerson.get("segundoNombre").getAsString();
        String primerApellido = jObjectPerson.get("primerApellido").getAsString();
        String segundoApellido = jObjectPerson.get("segundoApellido").getAsString();
        String fechaNac = jObjectPerson.get("fechaNac").getAsString();
        String numAsegurado = jObjectPerson.get("numAsegurado").getAsString();
        String numIdent = jObjectPerson.get("numIdent").getAsString();
        String direccion = jObjectPerson.get("direccion").getAsString();
        String telReside = jObjectPerson.get("telReside").getAsString();
        String telMovil = jObjectPerson.get("telMovil").getAsString();

        String codSexo = jObjectPerson.get("codSexo").getAsString();
        String codEstadoCivil = jObjectPerson.get("codEstadoCivil").getAsString();
        String codTipIdent = jObjectPerson.get("codTipIdent").getAsString();
        String codEtnia = jObjectPerson.get("codEtnia").getAsString();
        String codEscolaridad = jObjectPerson.get("codEscolaridad").getAsString();
        String codOcupacion = jObjectPerson.get("codOcupacion").getAsString();
        String codTipoAseg = jObjectPerson.get("codTipoAseg").getAsString();
        String codPaisNac = jObjectPerson.get("codPaisNac").getAsString();
        String codMuniNac = jObjectPerson.get("codMuniNac").getAsString();
        String codMuniRes = jObjectPerson.get("codMuniRes").getAsString();
        String codComunidadRes = jObjectPerson.get("codComunidadRes").getAsString();

        persona.setPersonaId(idPersona);
        persona.setPrimerNombre( URLDecoder.decode(primerNombre, "utf-8"));
        persona.setSegundoNombre(URLDecoder.decode(segundoNombre, "utf-8"));
        persona.setPrimerApellido(URLDecoder.decode(primerApellido, "utf-8"));
        persona.setSegundoApellido(URLDecoder.decode(segundoApellido, "utf-8"));
        persona.setFechaNacimiento(DateUtil.StringToDate(fechaNac, "dd/MM/yyyy"));
        persona.setIdentNumero(numIdent.trim().isEmpty() ? null : numIdent);
        persona.setAseguradoNumero(numAsegurado.trim().isEmpty() ? null : numAsegurado);
        persona.setDireccionResi(direccion.trim().isEmpty() ? null : URLDecoder.decode(direccion, "utf-8"));
        persona.setTelefonoResi(telReside.trim().isEmpty() ? null : telReside);
        persona.setTelefonoMovil(telMovil.trim().isEmpty() ? null : telMovil.trim());
        persona.setSexoCodigo(codSexo.trim().isEmpty() ? null : codSexo);
        persona.setEtniaCodigo(codEtnia.trim().isEmpty() ? null : codEtnia);
        persona.setEscolaridadCodigo(codEscolaridad.trim().isEmpty() ? null : codEscolaridad);
        persona.setEstadoCivilCodigo(codEstadoCivil.trim().isEmpty() ? null : codEstadoCivil);
        persona.setIdentCodigo(codTipIdent.trim().isEmpty() ? null : codTipIdent);
        persona.setTipoAsegCodigo(codTipoAseg.trim().isEmpty() ? null : codTipoAseg);
        persona.setOcupacionCodigo((codOcupacion!=null && !codOcupacion.isEmpty())? codOcupacion : null);
        persona.setPaisNacCodigoAlfados(codPaisNac.trim().isEmpty() ? null : codPaisNac);
        persona.setMuniNacCodigoNac(codMuniNac.trim().isEmpty() ? null : codMuniNac);
        persona.setMuniResiCodigoNac(codMuniRes.trim().isEmpty() ? null : codMuniRes);
        persona.setComuResiCodigo(codComunidadRes.trim().isEmpty() ? null : codComunidadRes);

        return persona;
    }

}
