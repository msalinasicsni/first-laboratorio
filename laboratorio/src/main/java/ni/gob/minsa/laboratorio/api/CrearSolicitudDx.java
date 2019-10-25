package ni.gob.minsa.laboratorio.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.irag.DaIrag;
import ni.gob.minsa.laboratorio.domain.muestra.*;
import ni.gob.minsa.laboratorio.domain.notificacion.DaNotificacion;
import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.User;
import ni.gob.minsa.laboratorio.domain.vigilanciaSindFebril.DaSindFebril;
import ni.gob.minsa.laboratorio.service.*;
import ni.gob.minsa.laboratorio.utilities.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Created by Miguel Salinas on 16/05/2019.
 * V1.0
 */

@Controller
@RequestMapping(value = "/api/v1/crearSolicitudDx")
public class CrearSolicitudDx {

    @Resource(name = "daNotificacionService")
    public DaNotificacionService daNotificacionService;

    @Resource(name = "sindFebrilService")
    public SindFebrilService sindFebrilService;

    @Resource(name = "daIragService")
    public DaIragService daIragService;

    @Resource(name="tomaMxService")
    private TomaMxService tomaMxService;

    @Resource(name = "datosSolicitudService")
    private DatosSolicitudService datosSolicitudService;

    @Resource(name = "personaService")
    public PersonaService personaService;

    @Resource(name = "entidadAdmonService")
    private EntidadAdmonService entidadAdmonService;

    @Resource(name = "unidadesService")
    private UnidadesService unidadesService;

    @Resource(name = "catalogosService")
    public CatalogoService catalogoService;

    @Resource(name="usuarioService")
    public UsuarioService usuarioService;

    @Resource(name = "laboratoriosService")
    public LaboratoriosService laboratoriosService;

    @Autowired
    MessageSource messageSource;

    /*Sin esto no funciona el CORS*/
    @RequestMapping(value = "/**",method = RequestMethod.OPTIONS)
    public String getOption(HttpServletResponse response, Model model)
    {
        response.setHeader("Access-Control-Allow-Origin","*");
        response.setHeader("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE");

        return "";
    }

    @RequestMapping(value = "save", method = RequestMethod.POST, consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    ResponseEntity<String> save(@RequestBody RegistroSolicitud solicitud) {
        RespuestaRegistroSolicitud resultado = new RespuestaRegistroSolicitud();
        resultado.setStatus("200");
        resultado.setError("");
        resultado.setMessage("");
        try {
            if (solicitud == null) {
                resultado.setError("RegistroSolicitud Null!");
            } else {

                String requeridos = validarParametrosEntrada(solicitud);
                if (requeridos.isEmpty()) {
                    //validar si usuario está registrado en el sistema
                    Laboratorio laboratorioProcesa = laboratoriosService.getLaboratorioByCodigo(solicitud.getCodigoLab());
                    if (laboratorioProcesa == null)
                        resultado.setError("Laboratorio enviado no se reconoce como laboratorio válido!");

                    //validar si usuario está registrado en el sistema
                    Usuarios usuarioRegistro = usuarioService.getUsuarioById(Integer.valueOf(solicitud.getIdUsuario()));
                    if (usuarioRegistro == null) {
                        resultado.setError("Usuario enviado no se reconoce como usuario válido!");
                    }

                    DaNotificacion notificacion = null;
                    DaTomaMx tomaMx = new DaTomaMx();
                    boolean esSeguimiento = solicitud.getSeguimiento().equalsIgnoreCase("1");
                    if (esSeguimiento){
                        List<DaNotificacion> notificaciones = daNotificacionService.getNoticesByPerson(Integer.valueOf(solicitud.getCodExpedienteUnico()), solicitud.getCodTipoNoti());
                        if (notificaciones.size()>0) notificacion = notificaciones.get(0);
                    }

                    //registrar notificacion, si no es seguimiento o si es seguimiento pero no se encontró notificación registrada
                    if (notificacion==null) {
                        notificacion = new DaNotificacion();
                        notificacion.setPersona(personaService.getPersona(Integer.valueOf(solicitud.getCodExpedienteUnico())));
                        if (!solicitud.getIdSilais().isEmpty()) {
                            notificacion.setCodSilaisAtencion(entidadAdmonService.getSilaisById(Long.valueOf(solicitud.getIdSilais())));
                            tomaMx.setCodSilaisAtencion(notificacion.getCodSilaisAtencion());
                        }
                        if (!solicitud.getIdUnidadSalud().isEmpty()) {
                            notificacion.setCodUnidadAtencion(unidadesService.getUnidadById(Long.valueOf(solicitud.getIdUnidadSalud())));
                            tomaMx.setCodUnidadAtencion(notificacion.getCodUnidadAtencion());
                        }
                        if (!solicitud.getCodTipoNoti().isEmpty()) {
                            notificacion.setCodTipoNotificacion(catalogoService.getTipoNotificacion(solicitud.getCodTipoNoti()));
                        }
                        if (!solicitud.getFechaInicioSintomas().isEmpty()) {
                            notificacion.setFechaInicioSintomas(DateUtil.StringToDate(solicitud.getFechaInicioSintomas(), "dd/MM/yyyy"));
                        }
                        if (!solicitud.getUrgente().isEmpty()) {
                            notificacion.setUrgente(catalogoService.getRespuesta(solicitud.getUrgente()));
                        }
                        if (!solicitud.getEmbarazada().isEmpty()) {
                            notificacion.setEmbarazada(catalogoService.getRespuesta(solicitud.getEmbarazada()));
                        }
                        if (!solicitud.getSemanasEmbarazo().isEmpty()) {
                            notificacion.setSemanasEmbarazo(Integer.valueOf(solicitud.getSemanasEmbarazo()));
                        }
                        if (!solicitud.getCodExpediente().isEmpty()) {
                            notificacion.setCodExpediente(solicitud.getCodExpediente());
                        }
                        if (solicitud.getCodigoVIH()!=null && !solicitud.getCodigoVIH().isEmpty()) {
                            notificacion.setCodigoPacienteVIH(solicitud.getCodigoVIH());
                        }
                        notificacion.setFechaRegistro(new Timestamp(new Date().getTime()));
                        notificacion.setUsuarioRegistro(usuarioRegistro);
                        notificacion.setPasivo(false);
                        notificacion.setCompleta(false);

                        daNotificacionService.addNotification(notificacion);

                        //crear ficha si es necesario
                        try {
                            crearFicha(notificacion);
                        } catch (Throwable ex) {
                            daNotificacionService.deleteNotificacion(notificacion);
                            resultado.setError(messageSource.getMessage("msg.error.update.noti", null, null) + ". \n " + ex.getMessage());
                            ex.printStackTrace();
                            return createJsonResponse(resultado);
                        }

                    }else{
                        if (!solicitud.getIdSilais().isEmpty()) {
                            tomaMx.setCodSilaisAtencion(notificacion.getCodSilaisAtencion());
                        }
                        if (!solicitud.getIdUnidadSalud().isEmpty()) {
                            tomaMx.setCodUnidadAtencion(notificacion.getCodUnidadAtencion());
                        }
                        if (solicitud.getCodigoVIH()!=null && !solicitud.getCodigoVIH().isEmpty()) {
                            notificacion.setCodigoPacienteVIH(solicitud.getCodigoVIH());
                            daNotificacionService.updateNotificacion(notificacion);
                        }

                    }
                    tomaMx.setIdNotificacion(notificacion);


                    //registrar envio de la muestra hacia el lab que procesa
                    DaEnvioMx envioOrden = new DaEnvioMx();
                    envioOrden.setUsarioRegistro(usuarioRegistro);
                    envioOrden.setFechaHoraEnvio(new Timestamp(new Date().getTime()));
                    envioOrden.setNombreTransporta("");
                    envioOrden.setTemperaturaTermo(null);
                    envioOrden.setLaboratorioDestino(laboratorioProcesa);
                    try {
                        tomaMxService.addEnvioOrden(envioOrden);
                    } catch (Exception ex) {
                        if (!esSeguimiento) {
                            if (notificacion.getCodTipoNotificacion().getCodigo().equalsIgnoreCase("TPNOTI|SINFEB"))
                                sindFebrilService.deleteDaSindFebril(sindFebrilService.getDaSindFebril(notificacion.getIdNotificacion()));
                            else if (notificacion.getCodTipoNotificacion().getCodigo().equalsIgnoreCase("TPNOTI|IRAG"))
                                daIragService.deleteDaIrag(daIragService.getFormById(notificacion.getIdNotificacion()));
                            daNotificacionService.deleteNotificacion(notificacion);
                        }
                        resultado.setError(messageSource.getMessage("msg.sending.error.add", null, null) + ". \n " + ex.getMessage());
                        ex.printStackTrace();
                        return createJsonResponse(resultado);
                    }
                    //registrar muestra
                    if (solicitud.getFechaTomaMx() != null) {
                        tomaMx.setFechaHTomaMx(DateUtil.StringToTimestamp(solicitud.getFechaTomaMx()));
                    }

                    tomaMx.setCodTipoMx(tomaMxService.getTipoMxById(solicitud.getIdTipoMx()));
                    tomaMx.setCanTubos(null);
                    tomaMx.setHoraTomaMx(solicitud.getHoraTomaMx());

                    if (!solicitud.getVolumen().isEmpty()) {
                        tomaMx.setVolumen(Float.valueOf(solicitud.getVolumen()));
                    }

                    tomaMx.setHoraRefrigeracion(null);
                    tomaMx.setMxSeparada(false);
                    tomaMx.setFechaRegistro(new Timestamp(new Date().getTime()));

                    tomaMx.setUsuario(usuarioRegistro);
                    tomaMx.setEstadoMx(catalogoService.getEstadoMx("ESTDMX|ENV")); //quedan listas para enviar a procesar en el area que le corresponde
                    String codigo = tomaMxService.generarCodigoUnicoMx();
                    tomaMx.setCodigoUnicoMx(codigo);
                    tomaMx.setEnvio(envioOrden);
                    try {
                        if (tomaMxService.existeTomaMx(notificacion.getIdNotificacion(), solicitud.getFechaTomaMx(), solicitud.getDiagnosticos())) {
                            throw new Exception(messageSource.getMessage("msg.existe.toma", null, null));
                        } else {
                            tomaMxService.addTomaMx(tomaMx);
                        }
                    } catch (Exception ex) {
                        tomaMxService.deleteEnvioOrden(envioOrden);
                        if (!esSeguimiento) {
                            if (notificacion.getCodTipoNotificacion().getCodigo().equalsIgnoreCase("TPNOTI|SINFEB"))
                                sindFebrilService.deleteDaSindFebril(sindFebrilService.getDaSindFebril(notificacion.getIdNotificacion()));
                            else if (notificacion.getCodTipoNotificacion().getCodigo().equalsIgnoreCase("TPNOTI|IRAG"))
                                daIragService.deleteDaIrag(daIragService.getFormById(notificacion.getIdNotificacion()));
                            daNotificacionService.deleteNotificacion(notificacion);
                        }
                        resultado.setError(ex.getMessage());
                        ex.printStackTrace();
                        return createJsonResponse(resultado);
                    }

                    //registrar los dxs
                    //se procede a registrar los diagnosticos o rutinas solicitados (incluyendo los datos que se pidan para cada uno. En este caso no se requieren para los sistemas externos)
                    if (!saveDxRequest(tomaMx.getIdTomaMx(), solicitud.getDiagnosticos(), null, 0, laboratorioProcesa, usuarioRegistro, null)) {
                        //rollback completo
                        datosSolicitudService.deleteDetallesDatosRecepcionByTomaMx(tomaMx.getIdTomaMx());
                        tomaMxService.deleteSolicitudesDxByTomaMx(tomaMx.getIdTomaMx());
                        tomaMxService.deleteTomaMx(tomaMx);
                        tomaMxService.deleteEnvioOrden(envioOrden);
                        if (!esSeguimiento) {
                            if (notificacion.getCodTipoNotificacion().getCodigo().equalsIgnoreCase("TPNOTI|SINFEB"))
                                sindFebrilService.deleteDaSindFebril(sindFebrilService.getDaSindFebril(notificacion.getIdNotificacion()));
                            else if (notificacion.getCodTipoNotificacion().getCodigo().equalsIgnoreCase("TPNOTI|IRAG"))
                                daIragService.deleteDaIrag(daIragService.getFormById(notificacion.getIdNotificacion()));

                            daNotificacionService.deleteNotificacion(notificacion);
                        }
                        resultado.setError("Dx no fueron agregados");
                    }
                } else {
                    resultado.setError(requeridos);
                }
            }
            if (resultado.getError().isEmpty()) resultado.setMessage("Success");
        }catch (Exception ex){
            resultado.setError("-"+ex.getMessage());
            ex.printStackTrace();
        }

        return createJsonResponse(resultado);
    }

    private String validarParametrosEntrada(RegistroSolicitud solicitud){
        if (solicitud.getCodTipoNoti()==null || solicitud.getCodTipoNoti().isEmpty()) return "Debe proporcionar valor para 'codTipoNoti'";
        if (solicitud.getIdTipoMx()==null || solicitud.getIdTipoMx().isEmpty()) return "Debe proporcionar valor para 'idTipoMx'";
        if (solicitud.getCodigoLab()==null || solicitud.getCodigoLab().isEmpty()) return "Debe proporcionar valor para 'codigoLab'";
        if (solicitud.getIdSilais()==null || solicitud.getIdSilais().isEmpty()) return "Debe proporcionar valor para 'idSilais'";
        if (solicitud.getIdUnidadSalud()==null || solicitud.getIdUnidadSalud().isEmpty()) return "Debe proporcionar valor para 'idUnidadSalud'";
        if (solicitud.getCodExpedienteUnico()==null || solicitud.getCodExpedienteUnico().isEmpty()) return "Debe proporcionar valor para 'codExpedienteUnico'";
        if (solicitud.getDiagnosticos()==null || solicitud.getDiagnosticos().isEmpty()) return "Debe proporcionar valor para 'diagnosticos'";
        if (solicitud.getIdUsuario()==null || solicitud.getIdUsuario().isEmpty()) return "Debe proporcionar valor para 'idUsuario'";
        if (solicitud.getSeguimiento()==null || solicitud.getSeguimiento().isEmpty()) return "Debe proporcionar valor para 'seguimiento'";
        if (solicitud.getFechaTomaMx()==null || solicitud.getFechaTomaMx().isEmpty()) return "Debe proporcionar valor para 'fechaTomaMx'";
        if (solicitud.getCodTipoNoti()!=null && solicitud.getCodTipoNoti().equalsIgnoreCase("TPNOTI|VIH")
                && (solicitud.getCodigoVIH()==null || solicitud.getCodigoVIH().isEmpty())) return "Debe proporcionar valor para 'codigoVIH'";
        return "";

    }

    private ResponseEntity<String> createJsonResponse( Object o )
    {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy").create();
        String json = gson.toJson( o );
        return new ResponseEntity<String>( json, headers, HttpStatus.CREATED );
    }

    private void crearFicha(DaNotificacion notificacion) throws Exception{
        switch (notificacion.getCodTipoNotificacion().getCodigo()){
            case "TPNOTI|SINFEB": {
                DaSindFebril sindFebril = sindFebrilService.getDaSindFebril(notificacion.getIdNotificacion());
                if (sindFebril==null) {
                    sindFebril = new DaSindFebril();
                    sindFebril.setFechaFicha(notificacion.getFechaRegistro());
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
                if (notificacion.getCodExpediente()!=null){
                    sindFebril.setCodExpediente(notificacion.getCodExpediente());
                }
                sindFebrilService.saveSindFebril(sindFebril);
                break;
            }
            case "TPNOTI|IRAG": {
                DaIrag irag = daIragService.getFormById(notificacion.getIdNotificacion());
                if (irag==null) {
                    irag = new DaIrag();
                    irag.setFechaRegistro(notificacion.getFechaRegistro());
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
                if (notificacion.getCodExpediente()!=null){
                    irag.setCodExpediente(notificacion.getCodExpediente());
                }
                daIragService.saveOrUpdateIrag(irag);
                break;
            }
            default:
                DaNotificacion noti = daNotificacionService.getNotifById(notificacion.getIdNotificacion());
                if (noti!=null) {
                    daNotificacionService.updateNotificacion(notificacion);
                }else{
                    daNotificacionService.addNotification(notificacion);
                }
                break;
        }
    }

    private boolean saveDxRequest(String idTomaMx, String dx, String strRespuestas, Integer cantRespuestas, Laboratorio laboratorio, Usuarios usuAlerta,  User usuLab) {
        try {
            String[] arrayDx = dx.split(",");
            for (String anArrayDx : arrayDx) {
                DaSolicitudDx soli = new DaSolicitudDx();
                soli.setCodDx(tomaMxService.getDxById(anArrayDx));
                soli.setFechaHSolicitud(new Timestamp(new Date().getTime()));
                soli.setUsarioRegistro(usuAlerta);
                soli.setIdTomaMx(tomaMxService.getTomaMxById(idTomaMx));
                soli.setAprobada(false);
                soli.setLabProcesa(laboratorio);
                soli.setControlCalidad(false);
                soli.setInicial(true);//es lo que viene en la ficha
                tomaMxService.addSolicitudDx(soli);

                if (strRespuestas!=null) {
                    JsonObject jObjectRespuestas = new Gson().fromJson(strRespuestas, JsonObject.class);
                    for (int i = 0; i < cantRespuestas; i++) {
                        String respuesta = jObjectRespuestas.get(String.valueOf(i)).toString();
                        JsonObject jsRespuestaObject = new Gson().fromJson(respuesta, JsonObject.class);

                        Integer idRespuesta = jsRespuestaObject.get("idRespuesta").getAsInt();

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
                                datoSolicitudDetalle.setUsuarioRegistro(usuLab);
                                datosSolicitudService.saveOrUpdateDetalleDatoRecepcion(datoSolicitudDetalle);
                            }
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
