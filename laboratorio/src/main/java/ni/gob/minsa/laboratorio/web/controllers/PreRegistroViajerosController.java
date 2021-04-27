package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.ciportal.dto.InfoResultado;
import ni.gob.minsa.ejbPersona.dto.Persona;
import ni.gob.minsa.laboratorio.domain.muestra.*;
import ni.gob.minsa.laboratorio.domain.notificacion.DaNotificacion;
import ni.gob.minsa.laboratorio.domain.parametros.Parametro;
import ni.gob.minsa.laboratorio.domain.persona.SisPersona;
import ni.gob.minsa.laboratorio.restServices.ServiciosEnLinea.CallServiciosEnLineaServices;
import ni.gob.minsa.laboratorio.restServices.ServiciosEnLinea.ListaPreRegistroRequest;
import ni.gob.minsa.laboratorio.restServices.ServiciosEnLinea.entidades.PreRegistro;
import ni.gob.minsa.laboratorio.service.*;
import ni.gob.minsa.laboratorio.utilities.DateUtil;
import org.apache.commons.lang3.text.translate.UnicodeEscaper;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by miguel on 28/1/2021.
 */
@Controller
@RequestMapping("preregistro/viajeros")
public class PreRegistroViajerosController {

    private static final Logger logger = LoggerFactory.getLogger(PreRegistroViajerosController.class);

    @Resource(name = "parametrosService")
    private ParametrosService parametrosService;

    @Autowired
    @Qualifier(value = "seguridadService")
    private SeguridadService seguridadService;

    @Resource(name="usuarioService")
    public UsuarioService usuarioService;

    @Resource(name = "daNotificacionService")
    public DaNotificacionService daNotificacionService;

    @Resource(name = "personaService")
    public PersonaService personaService;

    @Resource(name = "catalogosService")
    public CatalogoService catalogoService;

    @Resource(name = "datosSolicitudService")
    private DatosSolicitudService datosSolicitudService;

    @Autowired
    MessageSource messageSource;


    /**
     * Metodo que se llama al entrar a la opcion de menu "Viajeros". Se encarga de inicializar los controles para realizar la busqueda de preregistro para dx viajero en linea
     * @param request para obtener informacion de la peticion del cliente
     * @return ModelAndView
     * @throws Exception
     */
    @RequestMapping(value = "init", method = RequestMethod.GET)
    public ModelAndView initSearchForm(HttpServletRequest request) throws Exception {
        logger.debug("buscar pre registro para dx covid viajeros");
        ModelAndView mav = new ModelAndView();
        try {
            seguridadService.authenticateServiciosLinea(request);
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        mav.setViewName("viajeros/searchPreRegistration");
        return mav;
    }

    @RequestMapping(value = "listaPreRegistro", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    List<PreRegistro> obtenerListaPreRegistro(@RequestParam(value = "strFilter", required = true) String filtro, HttpServletRequest request) throws Exception {
        logger.info("Obteniendo los preregistros en JSON");
        ListaPreRegistroRequest listaPreRegistroRequest = jsonToListaPreRegistroRequest(filtro);
        List<PreRegistro> preRegistros = new ArrayList<PreRegistro>();
        try {
            String wdctk = seguridadService.getTokenServiciosLinea(request);
            Parametro apiUrlParam = parametrosService.getParametroByName("BASE_URL_API_SE");
            preRegistros = CallServiciosEnLineaServices.obtenerListaPreRegistro(apiUrlParam.getValor(), listaPreRegistroRequest, wdctk);
        } catch (Exception e){
            e.printStackTrace();
        }
        return preRegistros;
    }

    private ListaPreRegistroRequest jsonToListaPreRegistroRequest(String strJson){
        JsonObject jObjectFiltro = new Gson().fromJson(strJson, JsonObject.class);
        ListaPreRegistroRequest request = new ListaPreRegistroRequest();
        String fechainicial = null;
        String fechafinal = null;
        String identificacion = null;

        if (jObjectFiltro.get("fechainicial") != null && !jObjectFiltro.get("fechainicial").getAsString().isEmpty())
            fechainicial = jObjectFiltro.get("fechainicial").getAsString();
        if (jObjectFiltro.get("fechafinal") != null && !jObjectFiltro.get("fechafinal").getAsString().isEmpty())
            fechafinal = jObjectFiltro.get("fechafinal").getAsString();
        if (jObjectFiltro.get("identificacion") != null && !jObjectFiltro.get("identificacion").getAsString().isEmpty())
            identificacion = jObjectFiltro.get("identificacion").getAsString();

        request.setIdentificacion(identificacion);
        request.setFechafinal(fechafinal);
        request.setFechainicial(fechainicial);

        return request;

    }

    @RequestMapping(value = "aprobarPreRegistro", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/json")
    protected void aprobarPreRegistro(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<SisPersona> personas = null;
        DaNotificacion noti = new DaNotificacion();
        SisPersona persona = null;
        String idNotificacion = "";
        String resultado = "";
        Long idPreregistro =0L;
        String json = "";
        String token = null;
        String factura = "";
        String documentoViaje = "";
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            idPreregistro = jsonpObject.get("idPreregistro").getAsLong();
            ListaPreRegistroRequest listaPreRegistroRequest = new ListaPreRegistroRequest(idPreregistro);
            PreRegistro preRegistro = null;
            Parametro apiUrlParam = parametrosService.getParametroByName("BASE_URL_API_SE");
            try {
                token = seguridadService.getTokenServiciosLinea(request);
                preRegistro = CallServiciosEnLineaServices.obtenerPreRegistro(apiUrlParam.getValor(), listaPreRegistroRequest, token);
            } catch (Exception e){
                e.printStackTrace();
            }
            if (preRegistro != null) {
                Long preRegistroConfirmado = daNotificacionService.getNotifByIdPreregistro(preRegistro.getId());
                if (preRegistroConfirmado > 0) {
                    resultado = messageSource.getMessage("msg.preregistration.already.confirme", null, null);
                    return;
                }
                //Solo verificar persona si el número de identificacion es igual al número de documento de viaje. Andrea, 07/04/2021
                if (preRegistro.getPersona().getIdentificacion().getNumeroIdentificacion().trim().equalsIgnoreCase(preRegistro.getDocumentoviaje().getNumerodocumento().trim())) {
                    personas = personaService.getPersonasPorIdentificacion(0, 50, preRegistro.getPersona().getIdentificacion().getNumeroIdentificacion(), null);
                }
                if (personas != null && personas.size() > 0) {
                    persona = personas.get(0);
                } else {
                    String usuarioRegistra = seguridadService.obtenerNombreUsuario(request);
                    Persona nuevaPersona = crearPersona(preRegistro, usuarioRegistra);
                    if (nuevaPersona != null) {
                        persona = personaService.ensamblarObjetoSisPersona(nuevaPersona);
                    } else {
                        throw new Exception("Persona no se ha podido registrar");
                    }
                }
                noti.setPersona(persona);
                noti.setMunicipioResidencia(persona.getMunicipioResidencia());
                noti.setDireccionResidencia(persona.getDireccionResidencia());
                noti.setCodTipoNotificacion(catalogoService.getTipoNotificacion("TPNOTI|PCNT"));
                noti.setFechaRegistro(new Timestamp(new Date().getTime()));
                Parametro pUsuarioRegistro = parametrosService.getParametroByName("USU_REGISTRO_NOTI_CAESP");
                if (pUsuarioRegistro != null) {
                    long idUsuario = Long.valueOf(pUsuarioRegistro.getValor());
                    noti.setUsuarioRegistro(usuarioService.getUsuarioById((int) idUsuario));
                }
                noti.setPasivo(false);
                noti.setCompleta(false);
                noti.setIdPreregistro(idPreregistro);
                daNotificacionService.addNotification(noti);
                idNotificacion = noti.getIdNotificacion();
                if (preRegistro.getDetallepago() != null)
                    factura = preRegistro.getDetallepago().getReferencia();
                if (preRegistro.getDocumentoviaje() != null)
                    documentoViaje = preRegistro.getDocumentoviaje().getNumerodocumento();
                /*indicar que preregistro ya fue confirmado*/
                preRegistro.getEstadoregistro().setCodigo("CONFREG");
                CallServiciosEnLineaServices.actualizarPreregistro(apiUrlParam.getValor(), preRegistro, seguridadService.getTokenServiciosLinea(request));
            }
        }catch(HibernateException he){
            logger.error("HibernateException", he);
            resultado = messageSource.getMessage("msg.error.saving", null, null);
        }catch (Exception e){
            logger.error("Exception", e);
            resultado = messageSource.getMessage("msg.error.saving", null, null);
        } finally {
            UnicodeEscaper escaper = UnicodeEscaper.above(127);
            Map<String, String> map = new HashMap<String, String>();
            map.put("idNotificacion",idNotificacion);
            map.put("idPreregistro",String.valueOf(idPreregistro));
            map.put("factura",factura);
            map.put("documentoViaje",documentoViaje);
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(escaper.translate(jsonResponse).getBytes());
            response.getOutputStream().close();
        }
    }

    private Persona crearPersona(PreRegistro preRegistro, String usuarioRegistra) throws Exception{
        InfoResultado infoResultado;
        Long idPersona = 0L;
        Persona nuevaPersona = null;
        try {
            Persona persona = parsePregistroToPersona(preRegistro);
            personaService.iniciarTransaccion();
            logger.info("GUARDAR PERSONSA");
            infoResultado =  personaService.guardarPersona(persona, usuarioRegistra );
            if (infoResultado.isOk() && infoResultado.getObjeto() != null ){
                nuevaPersona = (Persona) (infoResultado.getObjeto());
            }else {
                logger.error(infoResultado.getMensaje());
            }
            logger.info("FIN GUARDAR PERSONSA");
            personaService.commitTransaccion();

        } catch (Exception ex) {
            logger.error("Error guardar persona",ex);
            ex.printStackTrace();
            try {
                personaService.rollbackTransaccion();
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("Rollback error",e);
            }
            throw ex;
        }finally {
            try {
                personaService.remover();
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("Cerrar conexi�n error",e);
                //resultado = messageSource.getMessage("msg.person.error.unhandled",null,null);
                //resultado=resultado+". \n "+(e.getMessage()!=null?e.getMessage():"");
            }
        }
        return nuevaPersona;
    }

    private Persona parsePregistroToPersona(PreRegistro preRegistro) throws Exception{
        Persona persona = new Persona();
        persona.setPersonaId(0L);
        persona.setPrimerNombre(preRegistro.getPersona().getPrimerNombre());
        persona.setPrimerApellido(preRegistro.getPersona().getPrimerApellido());
        persona.setSegundoNombre(preRegistro.getPersona().getSegundoNombre());
        persona.setSegundoApellido(preRegistro.getPersona().getSegundoApellido());
        persona.setIdentNumero(preRegistro.getPersona().getIdentificacion().getNumeroIdentificacion());
        if (preRegistro.getPersona().getIdentificacion().getTipo() != null) {
          if (preRegistro.getPersona().getIdentificacion().getTipo().getCodigo() != null && preRegistro.getPersona().getIdentificacion().getTipo().getCodigo().equalsIgnoreCase("CED")) {
              persona.setIdentCodigo("TPOID|CDULA");
          } else if (preRegistro.getPersona().getIdentificacion().getTipo().getCodigo() != null && preRegistro.getPersona().getIdentificacion().getTipo().getCodigo().equalsIgnoreCase("PAS")) {
              persona.setIdentCodigo("TPOID|03");
          } else {
              persona.setIdentCodigo("TPOID|CDULA");
          }
        } else persona.setIdentCodigo("TPOID|CDULA");
        //persona.setIdentCodigo(preRegistro.getPersona().getIdentificacion().getTipo());
        if (preRegistro.getPersona().getFechanacimiento()!=null)
            persona.setFechaNacimiento(DateUtil.StringToDate(preRegistro.getPersona().getFechanacimiento(), "yyyy-MM-dd"));
        persona.setTelefonoMovil(preRegistro.getPersona().getTelefono());
        persona.setDireccionResi(preRegistro.getPersona().getResidencia().getDireccionhabitual());
        persona.setSexoCodigo(preRegistro.getPersona().getSexo().getCodigo());
        persona.setPaisNacCodigoAlfados(preRegistro.getPersona().getPaisorigen().getCodigo());
        if (preRegistro.getPersona().getResidencia().getDivisionpolitica()!=null
                && preRegistro.getPersona().getResidencia().getDivisionpolitica().getDepartamento() != null
                && preRegistro.getPersona().getResidencia().getDivisionpolitica().getMunicipio() != null) {
            String codigoNacMuniResidencia= preRegistro.getPersona().getResidencia().getDivisionpolitica().getDepartamento().getCodigo()+preRegistro.getPersona().getResidencia().getDivisionpolitica().getMunicipio().getCodigo();
            persona.setMuniResiCodigoNac(codigoNacMuniResidencia);
        }

        return persona;
    }

}
