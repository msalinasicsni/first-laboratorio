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

    @Resource(name="tomaMxService")
    private TomaMxService tomaMxService;

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
        String identificacion = "";
        DaNotificacion noti = new DaNotificacion();
        SisPersona persona = null;
        Long idPersona = null;
        String idNotificacion = "";
        String resultado = "";
        Long idPreregistro =0L;
        String json = "";
        String token = null;
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

                personas = personaService.getPersonasPorIdentificacion(0, 50, preRegistro.getPersona().getIdentificacion().getNumeroIdentificacion(), null);
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
        persona.setMuniResiCodigoNac(preRegistro.getPersona().getResidencia().getDivisionpolitica().getMunicipio().getCodigo());
        return persona;
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

    private boolean saveDxRequest(String idTomaMx, String dx, String strRespuestas, Integer cantRespuestas) throws Exception {
        try {
            DaSolicitudDx soli = new DaSolicitudDx();
            String[] arrayDx = dx.split(",");
            Laboratorio laboratorio = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
            for (String anArrayDx : arrayDx) {
                soli.setCodDx(tomaMxService.getDxById(anArrayDx));
                soli.setFechaHSolicitud(new Timestamp(new Date().getTime()));
                Parametro pUsuarioRegistro = parametrosService.getParametroByName("USU_REGISTRO_NOTI_CAESP");
                if (pUsuarioRegistro != null) {
                    long idUsuario = Long.valueOf(pUsuarioRegistro.getValor());
                    soli.setUsarioRegistro(usuarioService.getUsuarioById((int) idUsuario));
                }
                soli.setIdTomaMx(tomaMxService.getTomaMxById(idTomaMx));
                soli.setAprobada(false);
                soli.setLabProcesa(laboratorio);
                soli.setControlCalidad(false);
                soli.setInicial(true);//es lo que viene en la ficha
                tomaMxService.addSolicitudDx(soli);

                JsonObject jObjectRespuestas = new Gson().fromJson(strRespuestas, JsonObject.class);
                for (int i = 0; i < cantRespuestas; i++) {
                    String respuesta = jObjectRespuestas.get(String.valueOf(i)).toString();
                    JsonObject jsRespuestaObject = new Gson().fromJson(respuesta, JsonObject.class);

                    Integer idRespuesta = jsRespuestaObject.get("idRespuesta").getAsInt();
                    Integer idConcepto = jsRespuestaObject.get("idConcepto").getAsInt();

                    DatoSolicitud conceptoTmp = datosSolicitudService.getDatoRecepcionSolicitudById(idRespuesta);
                    //si la respuesta pertenece al dx de la solicitud, se registra
                    if (conceptoTmp.getDiagnostico().getIdDiagnostico().equals(soli.getCodDx().getIdDiagnostico())) {
                        String valor = jsRespuestaObject.get("valor").getAsString();
                        if (valor != null) {
                            DatoSolicitudDetalle datoSolicitudDetalle = new DatoSolicitudDetalle();
                            datoSolicitudDetalle.setFechahRegistro(new Timestamp(new Date().getTime()));
                            datoSolicitudDetalle.setValor(valor.isEmpty() ? " " : valor);
                            datoSolicitudDetalle.setDatoSolicitud(conceptoTmp);
                            datoSolicitudDetalle.setSolicitudDx(soli);
                            datoSolicitudDetalle.setUsuarioRegistro(seguridadService.getUsuario(seguridadService.obtenerNombreUsuario()));
                            datosSolicitudService.saveOrUpdateDetalleDatoRecepcion(datoSolicitudDetalle);
                        }
                    }
                }
            }
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

}
