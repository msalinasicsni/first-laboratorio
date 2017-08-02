package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.estructura.EntidadesAdtvas;
import ni.gob.minsa.laboratorio.domain.estructura.Unidades;
import ni.gob.minsa.laboratorio.domain.irag.DaIrag;
import ni.gob.minsa.laboratorio.domain.irag.Respuesta;
import ni.gob.minsa.laboratorio.domain.muestra.*;
import ni.gob.minsa.laboratorio.domain.notificacion.DaNotificacion;
import ni.gob.minsa.laboratorio.domain.notificacion.TipoNotificacion;
import ni.gob.minsa.laboratorio.domain.parametros.Parametro;
import ni.gob.minsa.laboratorio.domain.poblacion.Divisionpolitica;
import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
import ni.gob.minsa.laboratorio.domain.vigilanciaSindFebril.DaSindFebril;
import ni.gob.minsa.laboratorio.service.*;
import ni.gob.minsa.laboratorio.utilities.ConstantsSecurity;
import ni.gob.minsa.laboratorio.utilities.DateUtil;
import ni.gob.minsa.laboratorio.utilities.StringUtil;
import ni.gob.minsa.laboratorio.utilities.enumeration.HealthUnitType;
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
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by souyen-ics on 11-05-14.
 */
@Controller
@RequestMapping("tomaMx")
public class TomaMxController {

    private static final Logger logger = LoggerFactory.getLogger(TomaMxController.class);

    @Autowired
    @Qualifier(value = "seguridadService")
    private SeguridadService seguridadService;

    @Resource(name="tomaMxService")
    private TomaMxService tomaMxService;

    @Resource(name="usuarioService")
    public UsuarioService usuarioService;

    @Resource(name = "catalogosService")
    public CatalogoService catalogoService;

    @Resource(name = "daNotificacionService")
    public DaNotificacionService daNotificacionService;

    @Resource(name = "sindFebrilService")
    public SindFebrilService sindFebrilService;

    @Resource(name = "daIragService")
    public DaIragService daIragService;

    @Resource(name = "personaService")
    public PersonaService personaService;

    @Resource(name = "parametrosService")
    private ParametrosService parametrosService;

    @Resource(name = "datosSolicitudService")
    private DatosSolicitudService datosSolicitudService;

    @Resource(name = "solicitanteService")
    private SolicitanteService solicitanteService;

    @Resource(name = "recepcionMxService")
    private RecepcionMxService recepcionMxService;

    @Resource(name = "entidadAdmonService")
    private EntidadAdmonService entidadAdmonService;

    @Resource(name = "divisionPoliticaService")
    private DivisionPoliticaService divisionPoliticaService;

    @Resource(name = "unidadesService")
    private UnidadesService unidadesService;

    @Autowired
    MessageSource messageSource;

    //Map<String, Object> mapModel;
    List<TipoMx_TipoNotificacion> catTipoMx;



    @RequestMapping(value = "search", method = RequestMethod.GET)
    public String initSearchForm(Model model, HttpServletRequest request) throws ParseException {
        logger.debug("Crear/Buscar Toma de Mx");

        String urlValidacion= "";
        try {
            urlValidacion = seguridadService.validarLogin(request);
            //si la url esta vacia significa que la validación del login fue exitosa
            if (urlValidacion.isEmpty())
                urlValidacion = seguridadService.validarAutorizacionUsuario(request, ConstantsSecurity.SYSTEM_CODE, false);
        }catch (Exception e){
            e.printStackTrace();
            urlValidacion = "404";
        }

        if(urlValidacion.isEmpty()){
            return "tomaMx/search";
        }else{
            return urlValidacion;
        }

    }

    @RequestMapping("noticesrut/{idPerson}")
    public ModelAndView showNoticesRutPerson(@PathVariable("idPerson") long idPerson) throws Exception {
        ModelAndView mav = new ModelAndView();
        List<DaNotificacion> results = daNotificacionService.getNoticesByPerson(idPerson);
        //List<DaNotificacion> results = daNotificacionService.getNoticesByPerson(idPerson,"TPNOTI|PCNT");
        mav.addObject("notificaciones", results);
        mav.addObject("personaId",idPerson);
        mav.setViewName("tomaMx/results");
        return  mav;
    }

    /**
     * Handler for create tomaMx.
     *
     * @param idNotificacion the ID of the person to create noti
     * @return a ModelMap with the model attributes for the respective view
     */
    @RequestMapping("createInicial/{idNotificacion}")
    public ModelAndView createTomaMxInicial(@PathVariable("idNotificacion") String idNotificacion) throws Exception {
        ModelAndView mav = new ModelAndView();
        //registros anteriores de toma Mx
        DaTomaMx tomaMx = new DaTomaMx();
        DaNotificacion noti;
        //si es numero significa que es un id de persona, no de notificación por tanto hay que crear una notificación para esa persona
        noti = daNotificacionService.getNotifById(idNotificacion);
        if (noti != null) {
            //catTipoMx = tomaMxService.getTipoMxByTipoNoti("TPNOTI|CAESP");
            catTipoMx = tomaMxService.getTipoMxByTipoNoti("TPNOTI|PCNT");
            List<EntidadesAdtvas> entidadesAdtvases =  entidadAdmonService.getAllEntidadesAdtvas();
            List<Divisionpolitica> municipios = null;
            if (noti.getCodSilaisAtencion()!=null){
                municipios = divisionPoliticaService.getMunicipiosBySilais(noti.getCodSilaisAtencion().getCodigo());
            }
            List<Unidades> unidades = null;
            if (noti.getCodUnidadAtencion()!=null && noti.getCodSilaisAtencion()!=null){
                unidades = unidadesService.getPrimaryUnitsByMunicipio_Silais(noti.getCodUnidadAtencion().getMunicipio().getCodigoNacional(),
                        noti.getCodSilaisAtencion().getCodigo(), HealthUnitType.UnidadesPrimarias.getDiscriminator().split(","));
            }
            List<TipoNotificacion> tiposNotificacion = new ArrayList<TipoNotificacion>();

            tiposNotificacion.add(catalogoService.getTipoNotificacion("TPNOTI|PCNT"));
            tiposNotificacion.add(catalogoService.getTipoNotificacion("TPNOTI|SINFEB"));
            tiposNotificacion.add(catalogoService.getTipoNotificacion("TPNOTI|IRAG"));

            List<Respuesta> catResp =catalogoService.getRespuesta();

            mav.addObject("noti", noti);
            mav.addObject("tomaMx", tomaMx);
            mav.addObject("catTipoMx", catTipoMx);
            mav.addObject("entidades",entidadesAdtvases);
            mav.addObject("municipios",municipios);
            mav.addObject("unidades",unidades);
            mav.addObject("notificaciones",tiposNotificacion);
            mav.addObject("catResp", catResp);
            mav.addObject("esNuevaNoti",true);
            //mav.addAllObjects(mapModel);
            mav.setViewName("tomaMx/enterForm");
        } else {
            mav.setViewName("404");
        }

        return mav;
    }

    /**
     * Handler for create tomaMx.
     *
     * @param idNotificacion the ID of the notification
     * @return a ModelMap with the model attributes for the respective view
     */
    @RequestMapping("create/{idNotificacion}")
    public ModelAndView createTomaMx(@PathVariable("idNotificacion") String idNotificacion) throws Exception {
        ModelAndView mav = new ModelAndView();
        //registros anteriores de toma Mx
        DaTomaMx tomaMx = new DaTomaMx();
        DaNotificacion noti;
        //si es numero significa que es un id de persona, no de notificación por tanto hay que crear una notificación para esa persona
        noti = daNotificacionService.getNotifById(idNotificacion);
        if (noti != null) {
            catTipoMx = tomaMxService.getTipoMxByTipoNoti(noti.getCodTipoNotificacion().getCodigo());
            List<EntidadesAdtvas> entidadesAdtvases =  entidadAdmonService.getAllEntidadesAdtvas();
            List<Divisionpolitica> municipios = null;
            if (noti.getCodSilaisAtencion()!=null){
                municipios = divisionPoliticaService.getMunicipiosBySilais(noti.getCodSilaisAtencion().getCodigo());
            }
            List<Unidades> unidades = null;
            if (noti.getCodUnidadAtencion()!=null && noti.getCodSilaisAtencion()!=null){
                unidades = unidadesService.getPrimaryUnitsByMunicipio_Silais(noti.getCodUnidadAtencion().getMunicipio().getCodigoNacional(),
                        noti.getCodSilaisAtencion().getCodigo(), HealthUnitType.UnidadesPrimarias.getDiscriminator().split(","));
            }
            List<TipoNotificacion> tiposNotificacion = new ArrayList<TipoNotificacion>();

            tiposNotificacion.add(catalogoService.getTipoNotificacion("TPNOTI|PCNT"));
            tiposNotificacion.add(catalogoService.getTipoNotificacion("TPNOTI|SINFEB"));
            tiposNotificacion.add(catalogoService.getTipoNotificacion("TPNOTI|IRAG"));

            List<Respuesta> catResp =catalogoService.getRespuesta();

            mav.addObject("noti", noti);
            mav.addObject("tomaMx", tomaMx);
            mav.addObject("catTipoMx", catTipoMx);
            mav.addObject("entidades",entidadesAdtvases);
            mav.addObject("municipios",municipios);
            mav.addObject("unidades",unidades);
            mav.addObject("notificaciones",tiposNotificacion);
            mav.addObject("catResp", catResp);
            mav.addObject("esNuevaNoti",false);
            //mav.addAllObjects(mapModel);
            mav.setViewName("tomaMx/enterForm");
        } else {
            mav.setViewName("404");
        }

        return mav;
    }

    @RequestMapping("override/{idNotificacion}")
    public String overrideNotificacion(@PathVariable("idNotificacion") String idNotificacion) throws Exception {
        ModelAndView mav = new ModelAndView();
        //registros anteriores de toma Mx
        DaTomaMx tomaMx = new DaTomaMx();
        DaNotificacion noti;
        //si es numero significa que es un id de persona, no de notificación por tanto hay que crear una notificación para esa persona
        noti = daNotificacionService.getNotifById(idNotificacion);
        if (noti != null) {
            noti.setPasivo(true);
            noti.setFechaAnulacion(new Timestamp(new Date().getTime()));
            daNotificacionService.updateNotificacion(noti);
            tomaMxService.anularTomasMxByIdNotificacion(noti.getIdNotificacion(),noti.getFechaAnulacion());
            if (noti.getPersona()!=null)
            return "redirect:/tomaMx/noticesrut/"+noti.getPersona().getPersonaId();
            else
                return "redirect:/tomaMx/notices/applicant/"+noti.getSolicitante().getIdSolicitante();
        } else {
            return "redirect:/404";
        }
    }

    /**
     * Retorna una lista de dx
     * @return Un arreglo JSON de dx
     */
    @RequestMapping(value = "dxByMx", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    List<Dx_TipoMx_TipoNoti> getDxBySample(@RequestParam(value = "codMx", required = true) String codMx, @RequestParam(value = "tipoNoti", required = true) String tipoNoti) throws Exception {
        logger.info("Obteniendo los diagnósticos segun muestra y tipo de Notificacion en JSON");
        //nombre usuario null, para que no valide autoridad
        return tomaMxService.getDx(codMx, tipoNoti,null);
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

    private Timestamp StringToTimestamp(String fechah) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = sdf.parse(fechah);
        return new Timestamp(date.getTime());
    }

    private ResponseEntity<String> createJsonResponse(Object o) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        Gson gson = new Gson();
        String json = gson.toJson(o);
        return new ResponseEntity<String>(json, headers, HttpStatus.CREATED);
    }

    /**
     * Método para generar un string alfanumérico de 8 caracteres, que se usará como código único de muestra
     * @return String codigoUnicoMx
     */
    private String generarCodigoUnicoMx(){
        DaTomaMx validaC;
        //Se genera el código
        String codigoUnicoMx = StringUtil.getCadenaAlfanumAleatoria(8);
        //Se consulta BD para ver si existe toma de Mx que tenga mismo código
        validaC = tomaMxService.getTomaMxByCodUnicoMx(codigoUnicoMx);
        //si existe, de manera recursiva se solicita un nuevo código
        if (validaC!=null){
            codigoUnicoMx = generarCodigoUnicoMx();
        }
        //si no existe se retorna el último código generado
        return codigoUnicoMx;
    }

    /***************************************************************************/
    /******************** TOMA MUESTRAS PACIENTES Y OTRAS MX********************************/
    /***************************************************************************/


    @RequestMapping(value = "saveToma", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/json")
    protected void saveTomaMxPacienteOtras(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json = "";
        String resultado = "";
        String fechaHTomaMx="";
        String idNotificacion="";
        String codTipoMx="";
        Integer canTubos=null;
        String volumen=null;
        String horaRefrigeracion="";
        String dx="";
        String strRespuestas="";
        Integer cantRespuestas=0;
        Integer codSilais=null;
        Integer codUnidadSalud=null;
        String codTipoNoti="";
        String horaTomaMx="";
        String fechaInicioSintomas="";
        String codigoGenerado = "";
        String urgente = "";
        String embarazada = "";
        Integer semanasEmbarazo=null;
        try {
            logger.debug("Guardando datos de Toma de Muestra");
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            idNotificacion = jsonpObject.get("idNotificacion").getAsString();
            fechaHTomaMx = jsonpObject.get("fechaHTomaMx").getAsString();
            codTipoMx = jsonpObject.get("codTipoMx").getAsString();
            if (jsonpObject.get("strRespuestas")!=null)
                strRespuestas = jsonpObject.get("strRespuestas").toString();
            if (jsonpObject.get("cantRespuestas")!=null && !jsonpObject.get("cantRespuestas").getAsString().isEmpty())
                cantRespuestas = jsonpObject.get("cantRespuestas").getAsInt();
            if (jsonpObject.get("canTubos")!=null && !jsonpObject.get("canTubos").getAsString().isEmpty())
                canTubos = jsonpObject.get("canTubos").getAsInt();

            if (jsonpObject.get("volumen")!=null && !jsonpObject.get("volumen").getAsString().isEmpty())
                volumen = jsonpObject.get("volumen").getAsString();

            if (jsonpObject.get("dx")!=null && !jsonpObject.get("dx").getAsString().isEmpty())
                dx = jsonpObject.get("dx").getAsString();

            if (jsonpObject.get("codSilais")!=null && !jsonpObject.get("codSilais").getAsString().isEmpty())
                codSilais = jsonpObject.get("codSilais").getAsInt();

            if (jsonpObject.get("codUnidadSalud")!=null && !jsonpObject.get("codUnidadSalud").getAsString().isEmpty())
                codUnidadSalud = jsonpObject.get("codUnidadSalud").getAsInt();

            if (jsonpObject.get("codTipoNoti")!=null && !jsonpObject.get("codTipoNoti").getAsString().isEmpty())
                codTipoNoti = jsonpObject.get("codTipoNoti").getAsString();

            if (jsonpObject.get("horaTomaMx")!=null && !jsonpObject.get("horaTomaMx").getAsString().isEmpty())
                horaTomaMx = jsonpObject.get("horaTomaMx").getAsString();

            if (jsonpObject.get("fechaInicioSintomas")!=null && !jsonpObject.get("fechaInicioSintomas").getAsString().isEmpty())
                fechaInicioSintomas = jsonpObject.get("fechaInicioSintomas").getAsString();

            horaRefrigeracion = jsonpObject.get("horaRefrigeracion").getAsString();

            if (jsonpObject.get("urgente")!=null && !jsonpObject.get("urgente").getAsString().isEmpty())
                urgente = jsonpObject.get("urgente").getAsString();
            if (jsonpObject.get("embarazada")!=null && !jsonpObject.get("embarazada").getAsString().isEmpty())
                embarazada = jsonpObject.get("embarazada").getAsString();
            if (jsonpObject.get("semanasEmbarazo")!=null && !jsonpObject.get("semanasEmbarazo").getAsString().isEmpty())
                semanasEmbarazo = jsonpObject.get("semanasEmbarazo").getAsInt();

            Parametro pUsuarioRegistro = parametrosService.getParametroByName("USU_REGISTRO_NOTI_CAESP");
            Usuarios usuarioRegistro = new Usuarios();
            if(pUsuarioRegistro!=null) {
                long idUsuario = Long.valueOf(pUsuarioRegistro.getValor());
                usuarioRegistro = usuarioService.getUsuarioById((int)idUsuario);
            }

            DaEnvioMx envioOrden = new DaEnvioMx();

            envioOrden.setUsarioRegistro(usuarioRegistro);
            envioOrden.setFechaHoraEnvio(new Timestamp(new Date().getTime()));
            envioOrden.setNombreTransporta("");
            envioOrden.setTemperaturaTermo(null);
            envioOrden.setLaboratorioDestino(seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario()));
            try {
                tomaMxService.addEnvioOrden(envioOrden);
            }catch (Exception ex){
                resultado = messageSource.getMessage("msg.sending.error.add",null,null);
                resultado=resultado+". \n "+ex.getMessage();
                ex.printStackTrace();
                throw new Exception(ex);
            }

            DaTomaMx tomaMx = new DaTomaMx();
            Laboratorio labUsuario = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
            DaNotificacion notificacion = daNotificacionService.getNotifById(idNotificacion);
            if (codSilais!=null){
                notificacion.setCodSilaisAtencion(entidadAdmonService.getSilaisByCodigo(codSilais));
                tomaMx.setCodSilaisAtencion(notificacion.getCodSilaisAtencion());
            }
            if (codUnidadSalud!=null){
                notificacion.setCodUnidadAtencion(unidadesService.getUnidadByCodigo(codUnidadSalud));
                tomaMx.setCodUnidadAtencion(notificacion.getCodUnidadAtencion());
            }
            if (!codTipoNoti.isEmpty()){
                notificacion.setCodTipoNotificacion(catalogoService.getTipoNotificacion(codTipoNoti));
            }
            if (!fechaInicioSintomas.isEmpty()){
                notificacion.setFechaInicioSintomas(DateUtil.StringToDate(fechaInicioSintomas,"dd/MM/yyyyy"));
            }
            if (!urgente.isEmpty()) {
                notificacion.setUrgente(catalogoService.getRespuesta(urgente));
            }
            if (!embarazada.isEmpty()) {
                notificacion.setEmbarazada(catalogoService.getRespuesta(embarazada));
            }
            if (semanasEmbarazo!=null) {
                notificacion.setSemanasEmbarazo(semanasEmbarazo);
            }
            tomaMx.setIdNotificacion(notificacion);
            if(fechaHTomaMx != null){
                tomaMx.setFechaHTomaMx(StringToTimestamp(fechaHTomaMx));
            }

            tomaMx.setCodTipoMx(tomaMxService.getTipoMxById(codTipoMx));
            tomaMx.setCanTubos(canTubos);
            tomaMx.setHoraTomaMx(horaTomaMx);

            if(volumen != null && !volumen.equals("")){
                tomaMx.setVolumen(Float.valueOf(volumen));
            }

            tomaMx.setHoraRefrigeracion(horaRefrigeracion);
            tomaMx.setMxSeparada(false);
            tomaMx.setFechaRegistro(new Timestamp(new Date().getTime()));

            tomaMx.setUsuario(usuarioRegistro);
            tomaMx.setEstadoMx(catalogoService.getEstadoMx("ESTDMX|RCP")); //quedan listas para enviar a procesar en el area que le corresponde
            String codigo = generarCodigoUnicoMx();
            tomaMx.setCodigoUnicoMx(codigo);
            //todas deben tener codigo lab, porque son rutinas
            tomaMx.setCodigoLab(recepcionMxService.obtenerCodigoLab(labUsuario.getCodigo(),1));
            codigoGenerado = tomaMx.getCodigoLab();
            tomaMx.setEnvio(envioOrden);
            try {
                tomaMxService.addTomaMx(tomaMx);
            }catch (Exception ex){
                tomaMxService.deleteEnvioOrden(envioOrden);
                resultado=resultado+". \n "+ex.getMessage();
                ex.printStackTrace();
                throw ex;
            }

            try {
                //daNotificacionService.updateNotificacion(notificacion);
                crearFicha(notificacion);
            }catch (Throwable ex){
                tomaMxService.deleteTomaMx(tomaMx);
                tomaMxService.deleteEnvioOrden(envioOrden);
                resultado = messageSource.getMessage("msg.error.update.noti",null,null);
                resultado=resultado+". \n "+ex.getMessage();
                ex.printStackTrace();
                throw ex;
            }
            //se procede a registrar los diagnósticos o rutinas solicitados (incluyendo los datos que se pidan para cada uno)
            if (saveDxRequest(tomaMx.getIdTomaMx(), dx, strRespuestas, cantRespuestas)) {
                //Como la muestra queda en estado recepcionada, entonces es necesario registrar la recepción de la misma
                RecepcionMx recepcionMx = new RecepcionMx();
                recepcionMx.setUsuarioRecepcion(seguridadService.getUsuario(seguridadService.obtenerNombreUsuario()));
                recepcionMx.setLabRecepcion(labUsuario);
                recepcionMx.setFechaHoraRecepcion(new Timestamp(new Date().getTime()));
                recepcionMx.setTipoMxCk(true);
                recepcionMx.setCantidadTubosCk(true);
                recepcionMx.setTipoRecepcionMx(catalogoService.getTipoRecepcionMx("TPRECPMX|VRT"));
                recepcionMx.setTomaMx(tomaMx);
                try {
                    recepcionMxService.addRecepcionMx(recepcionMx);
                } catch (Exception ex) { //rollback completo
                    ex.printStackTrace();
                    datosSolicitudService.deleteDetallesDatosRecepcionByTomaMx(tomaMx.getIdTomaMx());
                    tomaMxService.deleteSolicitudesDxByTomaMx(tomaMx.getIdTomaMx());
                    tomaMxService.deleteTomaMx(tomaMx);
                    resultado=resultado+". \n "+ex.getMessage();
                }
            }else{ //rollback completo
                datosSolicitudService.deleteDetallesDatosRecepcionByTomaMx(tomaMx.getIdTomaMx());
                tomaMxService.deleteSolicitudesDxByTomaMx(tomaMx.getIdTomaMx());
                tomaMxService.deleteTomaMx(tomaMx);
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.add.receipt.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        } finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("idNotificacion",idNotificacion);
            map.put("canTubos",String.valueOf(canTubos));
            map.put("volumen",volumen);
            map.put("mensaje",resultado);
            map.put("fechaHTomaMx", fechaHTomaMx);
            map.put("codTipoMx",codTipoMx);
            map.put("horaRefrigeracion", horaRefrigeracion);
            map.put("strRespuestas",strRespuestas);
            map.put("cantRespuestas",cantRespuestas.toString());
            map.put("codSilais","");
            map.put("codUnidadSalud","");
            map.put("codTipoNoti",codTipoNoti);
            map.put("horaTomaMx",horaTomaMx);
            map.put("fechaInicioSintomas",fechaInicioSintomas);
            map.put("codigoLab", codigoGenerado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    private void crearFicha(DaNotificacion notificacion) throws Exception{
        switch (notificacion.getCodTipoNotificacion().getCodigo()){
            case "TPNOTI|SINFEB": {
                DaSindFebril sindFebril = sindFebrilService.getDaSindFebril(notificacion.getIdNotificacion());
                if (sindFebril==null) {
                    sindFebril = new DaSindFebril();
                    sindFebril.setFechaFicha(new Date());
                }
                sindFebril.setIdNotificacion(notificacion);
                if (notificacion.getSemanasEmbarazo()!=null) {
                    sindFebril.setMesesEmbarazo(notificacion.getSemanasEmbarazo());
                }else {
                    sindFebril.setMesesEmbarazo(0);
                }
                if (notificacion.getEmbarazada()!=null){
                    sindFebril.setEmbarazo(notificacion.getEmbarazada());
                }
                sindFebrilService.saveSindFebril(sindFebril);
                break;
            }
            case "TPNOTI|IRAG": {
                DaIrag irag = daIragService.getFormById(notificacion.getIdNotificacion());
                if (irag==null) {
                    irag = new DaIrag();
                    irag.setFechaRegistro(new Timestamp(new Date().getTime()));
                    irag.setUsuario(notificacion.getUsuarioRegistro());
                }
                irag.setIdNotificacion(notificacion);
                if (notificacion.getEmbarazada()!=null){
                    if (irag.getCondiciones()!=null) {
                        if (!irag.getCondiciones().contains("CONDPRE|EMB"))
                            irag.setCondiciones(irag.getCondiciones() + ",CONDPRE|EMB");
                    }
                    else irag.setCondiciones("CONDPRE|EMB");
                }
                if (notificacion.getSemanasEmbarazo()!=null) {
                    irag.setSemanasEmbarazo(notificacion.getSemanasEmbarazo());
                }
                daIragService.saveOrUpdateIrag(irag);
                break;
            }
            default:
                break;
        }
    }

    @RequestMapping(value = "getDatosSolicitudDetalleBySolicitud", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    List<DatoSolicitudDetalle> getDatosSolicitudDetalleBySolicitud(@RequestParam(value = "idSolicitud", required = true) String idSolicitud) throws Exception {
        logger.info("Se obtienen los detalles de resultados activos para la solicitud");
        List<DatoSolicitudDetalle> resultados = datosSolicitudService.getDatosSolicitudDetalleBySolicitud(idSolicitud);
        return resultados;
    }

    @RequestMapping(value = "createnoti", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/json")
    protected void crearNotificacion(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json = "";
        String resultado = "";
        String idNotificacion = "";
        Integer idPersona=null;
        String idSolicitante = "";
        try {
            logger.debug("Guardando datos de la notificacion");
            DaNotificacion noti = new DaNotificacion();
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            if (jsonpObject.get("idPersona")!=null && !jsonpObject.get("idPersona").getAsString().isEmpty()) {
                idPersona = jsonpObject.get("idPersona").getAsInt();
                noti.setPersona(personaService.getPersona(Long.valueOf(idPersona)));
                noti.setCodTipoNotificacion(catalogoService.getTipoNotificacion("TPNOTI|PCNT"));
            }
            if (jsonpObject.get("idSolicitante")!=null && !jsonpObject.get("idSolicitante").getAsString().isEmpty()){
                idSolicitante = jsonpObject.get("idSolicitante").getAsString();
                noti.setSolicitante(solicitanteService.getSolicitanteById(idSolicitante));
                noti.setCodTipoNotificacion(catalogoService.getTipoNotificacion("TPNOTI|OMX"));
            }

            noti.setFechaRegistro(new Timestamp(new Date().getTime()));
            Parametro pUsuarioRegistro = parametrosService.getParametroByName("USU_REGISTRO_NOTI_CAESP");
            if(pUsuarioRegistro!=null) {
                long idUsuario = Long.valueOf(pUsuarioRegistro.getValor());
                noti.setUsuarioRegistro(usuarioService.getUsuarioById((int)idUsuario));
            }
            noti.setCompleta(false);
            //noti.setCodTipoNotificacion(catalogoService.getTipoNotificacion("TPNOTI|CAESP"));

            daNotificacionService.addNotification(noti);
            idNotificacion = noti.getIdNotificacion();
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.add.notification.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("idNotificacion",idNotificacion);
            map.put("mensaje",resultado);
            map.put("idPersona",String.valueOf(idPersona));
            map.put("idSolicitante",idSolicitante);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = "tomaMxByIdNoti", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    List<DaTomaMx> getTestBySample(@RequestParam(value = "idNotificacion", required = true) String idNotificacion) throws Exception {
        logger.info("Realizando búsqueda de Toma de Mx.");

        return tomaMxService.getTomaMxByIdNoti(idNotificacion);

    }

    @RequestMapping(value = "getTipoMxByTipoNoti", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    List<TipoMx_TipoNotificacion> getTipoMxByTipoNoti(@RequestParam(value = "codigo", required = true) String codigo) throws Exception {
        logger.info("Realizando búsqueda de tipos de muestras según el tipo de notificación");

        return tomaMxService.getTipoMxByTipoNoti(codigo);

    }
    /*******************************************************************************/
    /***************************** OTRAS MUESTRAS **********************************/
    /*******************************************************************************/

    @RequestMapping(value = "searchOMx", method = RequestMethod.GET)
    public String initSearchOtrasForm() throws ParseException {
        logger.debug("Crear/Buscar Toma de Mx");
            return "tomaMx/searchOtherSamples";
    }

    @RequestMapping("notices/applicant/{idSolicitante}")
    public ModelAndView showNoticesApplicant (@PathVariable("idSolicitante") String idSolicitante) throws Exception {
        ModelAndView mav = new ModelAndView();
        List<DaNotificacion> results = daNotificacionService.getNoticesByApplicant(idSolicitante, "TPNOTI|OMX");
        mav.addObject("notificaciones", results);
        mav.addObject("idSolicitante",idSolicitante);
        mav.setViewName("tomaMx/resultsOtherSamples");
        return  mav;
    }

    /**
     * Handler for create tomaMx.
     *
     * @param idNotificacion the ID of the notification
     * @return a ModelMap with the model attributes for the respective view
     */
    @RequestMapping("createOMx/{idNotificacion}")
    public ModelAndView createOtherSample(@PathVariable("idNotificacion") String idNotificacion) throws Exception {
        ModelAndView mav = new ModelAndView();
        //registros anteriores de toma Mx
        DaTomaMx tomaMx = new DaTomaMx();
        DaNotificacion noti;
        //si es numero significa que es un id de persona, no de notificación por tanto hay que crear una notificación para esa persona
        noti = daNotificacionService.getNotifById(idNotificacion);
        if (noti != null) {
            //catTipoMx = tomaMxService.getTipoMxByTipoNoti("TPNOTI|CAESP");
            catTipoMx = tomaMxService.getTipoMxByTipoNoti("TPNOTI|OMX");
            List<TipoNotificacion> tiposNotificacion = new ArrayList<TipoNotificacion>();

            tiposNotificacion.add(catalogoService.getTipoNotificacion("TPNOTI|OMX"));
            mav.addObject("notificaciones",tiposNotificacion);
            mav.addObject("noti", noti);
            mav.addObject("tomaMx", tomaMx);
            mav.addObject("catTipoMx", catTipoMx);
            //mav.addAllObjects(mapModel);
            mav.setViewName("tomaMx/enterFormOtherSamples");
        } else {
            mav.setViewName("404");
        }

        return mav;
    }
}
