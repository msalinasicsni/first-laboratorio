package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.concepto.Catalogo_Lista;
import ni.gob.minsa.laboratorio.domain.muestra.DaSolicitudDx;
import ni.gob.minsa.laboratorio.domain.muestra.DaTomaMx;
import ni.gob.minsa.laboratorio.domain.muestra.FiltroMx;
import ni.gob.minsa.laboratorio.domain.persona.SisPersona;
import ni.gob.minsa.laboratorio.domain.resultados.DetalleResultado;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.User;
import ni.gob.minsa.laboratorio.domain.serviciosEnLinea.ResultadoViajero;
import ni.gob.minsa.laboratorio.service.*;
import ni.gob.minsa.laboratorio.utilities.DateUtil;
import ni.gob.minsa.laboratorio.utilities.reportes.DatosOrdenExamen;
import ni.gob.minsa.laboratorio.utilities.reportes.ResultadoExamen;
import ni.gob.minsa.laboratorio.utilities.reportes.ResultadoSolicitud;
import ni.gob.minsa.laboratorio.utilities.reportes.ResultadoVigilancia;
import org.apache.commons.lang3.text.translate.UnicodeEscaper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by miguel on 19/3/2021.
 */
@Controller
@RequestMapping("publicar/viajeros")
public class PublicarResultadoController {

    private static final Logger logger = LoggerFactory.getLogger(PublicarResultadoController.class);

    @Resource(name = "seguridadService")
    private SeguridadService seguridadService;

    @Resource(name = "parametrosService")
    private ParametrosService parametrosService;

    @Resource(name = "resultadoFinalService")
    private ResultadoFinalService resultadoFinalService;

    @Resource(name = "tomaMxService")
    private TomaMxService tomaMxService;

    @Resource(name= "ordenExamenMxService")
    private OrdenExamenMxService ordenExamenMxService;

    @Resource(name= "resultadosService")
    private ResultadosService resultadosService;

    @Resource(name = "datosSolicitudService")
    private DatosSolicitudService datosSolicitudService;

    @Resource(name = "serviciosEnLineaService")
    private ServiciosEnLineaService serviciosEnLineaService;


    @Autowired
    MessageSource messageSource;

    @RequestMapping(value = "/init", method = RequestMethod.GET)
    public ModelAndView initSearchForm(HttpServletRequest request) throws Exception {
        logger.debug("Iniciando publicacion de resultados");

        ModelAndView mav = new ModelAndView();
        boolean usuarioAutorizadoCovid19 = seguridadService.usuarioAutorizadoCovid19(seguridadService.obtenerNombreUsuario());
        if (usuarioAutorizadoCovid19){
            mav.setViewName("viajeros/publishApprovedResults");
        } else {
            mav.setViewName("403");
        }

        return mav;
    }

    @RequestMapping(value = "search", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String fetchSolicitudesJson(@RequestParam(value = "strFilter", required = true) String filtro) throws Exception{
        logger.info("Obteniendo los diagnósticos aprobados para publicar");
        FiltroMx filtroMx= jsonToFiltroDx(filtro);
        List<ResultadoVigilancia> resultadoVigilancia = resultadoFinalService.getDiagnosticosAprobadosByFiltro(filtroMx);
        return resultadosToJson(resultadoVigilancia, filtroMx);
    }

    @RequestMapping(value = "publishMassiveResult", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void publishMassiveResult(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        String idSolicitud="";
        Integer cantPublicProcesadas = 0;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            String strSolicitudes = jsonpObject.get("strSolicitudes").toString();
            Integer cantPublicaciones = jsonpObject.get("cantPublicaciones").getAsInt();
            JsonObject jObjectSolicitudes = new Gson().fromJson(strSolicitudes, JsonObject.class);
            for (int i = 0; i < cantPublicaciones; i++){
                idSolicitud = jObjectSolicitudes.get(String.valueOf(i)).getAsString();
                DaSolicitudDx solicitudDx = tomaMxService.getSolicitudDxByIdSolicitud(idSolicitud);
                if (solicitudDx == null) {
                    throw new Exception(messageSource.getMessage("msg.approve.result.solic.not.found", null, null));
                } else {
                    cantPublicProcesadas += saveResultadoViajero(solicitudDx);
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.approve.result.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("strSolicitudes","-");
            map.put("cantPublicaciones","-");
            map.put("cantPublicProcesadas",cantPublicProcesadas.toString());
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    private String resultadosToJson(List<ResultadoVigilancia> solicitudDxList, FiltroMx filtroMx){
        String jsonResponse="";
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice=0;
        boolean usuarioAutorizadoCovid19 = seguridadService.usuarioAutorizadoCovid19(seguridadService.obtenerNombreUsuario());
        if (usuarioAutorizadoCovid19){
            for(ResultadoVigilancia diagnostico : solicitudDxList) {

                Map<String, String> map = new HashMap<String, String>();
                map.put("codigoUnicoMx", diagnostico.getCodigoMx());
                map.put("idTomaMx", diagnostico.getIdTomaMx());
                map.put("tipoMuestra", diagnostico.getNombreTipoMx());
                map.put("tipoNotificacion", diagnostico.getNombreTipoNoti());
                String nombres = "";
                if (diagnostico.getPrimerNombre() != null) {
                    nombres = diagnostico.getPrimerNombre();
                    if (diagnostico.getSegundoNombre() != null)
                        nombres = nombres + " " + diagnostico.getSegundoNombre();

                    nombres = nombres + " " +diagnostico.getPrimerApellido();
                    if (diagnostico.getSegundoApellido() != null)
                        nombres = nombres + " " + diagnostico.getSegundoApellido();

                    map.put("persona", nombres);
                }

                map.put("solicitud", diagnostico.getNombreSolicitud());
                map.put("idSolicitud", diagnostico.getIdSolicitud());
                map.put("fechaSolicitud", DateUtil.DateToString(diagnostico.getFechaSolicitud(), "dd/MM/yyyy hh:mm:ss a"));
                map.put("fechaAprobacion", DateUtil.DateToString(diagnostico.getFechaAprobacion(), "dd/MM/yyyy hh:mm:ss a"));
                String resultado = parseFinalResultDetails(diagnostico.getIdSolicitud());
                if (filtroMx.getResultadoFinal() == null) {
                    map.put("resultados", resultado);

                    mapResponse.put(indice, map);
                    indice++;
                } else {
                    if (filtroMx.getResultadoFinal().equalsIgnoreCase(resultado)) {
                        map.put("resultados", resultado);

                        mapResponse.put(indice, map);
                        indice++;
                    }
                }
            }
        }

        jsonResponse = new Gson().toJson(mapResponse);
        UnicodeEscaper escaper     = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }

    private String parseFinalResultDetails(String idSolicitud){
        List<ResultadoSolicitud> resFinalList = resultadoFinalService.getDetResActivosBySolicitudV2(idSolicitud);
        String resultados="";
        for(ResultadoSolicitud res: resFinalList){
            if (res.getRespuesta()!=null) {
                if (res.getTipo().equals("TPDATO|LIST")) {
                    Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(res.getValor());
                    resultados+=cat_lista.getEtiqueta();
                }else if (res.getTipo().equals("TPDATO|LOG")) {
                    String valorBoleano = (Boolean.valueOf(res.getValor())?"lbl.yes":"lbl.no");
                    resultados+=valorBoleano;
                } else if (res.getValor().toLowerCase().contains("inadecuad")) {
                    resultados+=res.getValor();
                }
            }else if (res.getRespuestaExamen()!=null){
                if (res.getTipoExamen().equals("TPDATO|LIST")) {
                    Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(res.getValor());
                    resultados+=cat_lista.getEtiqueta();
                } else if (res.getTipoExamen().equals("TPDATO|LOG")) {
                    String valorBoleano = (Boolean.valueOf(res.getValor())?"lbl.yes":"lbl.no");
                    resultados+=valorBoleano;
                }
            }
        }
        return resultados;
    }

    /****
     * Metodo para determinar si la solicitud es de diagnostico viajero covid19.
     * Si es viajero se registra resultado en tabla de serviciosenlinea.se_resultado_viajero para que el solicitante lo pueda imprimir en linea
     * @param solicitudDx solicitud que se esta aprobando
     */
    private int saveResultadoViajero(DaSolicitudDx solicitudDx){
        try {
            boolean esMxViajeroCovid = tomaMxService.esMxViajeroCovid(solicitudDx.getIdTomaMx().getCodigoLab());
            if (esMxViajeroCovid) {
                String nombres = "";
                String apellidos = "";
                String nombreExamen = "";
                String detalleResultado = "";
                Date fechaProcesamiento = null;
                String procesadoPor = "";
                ResultadoViajero viajero = new ResultadoViajero();

                DaTomaMx tomaMx = solicitudDx.getIdTomaMx();
                SisPersona persona = tomaMx.getIdNotificacion().getPersona();
                if (persona.getPrimerNombre() != null) {
                    nombres = persona.getPrimerNombre();
                    if (persona.getSegundoNombre() != null)
                        nombres = nombres + " " + persona.getSegundoNombre();

                    apellidos = persona.getPrimerApellido();
                    if (persona.getSegundoApellido() != null)
                        apellidos = apellidos + " " + persona.getSegundoApellido();
                }
                List<DatosOrdenExamen> examenes = ordenExamenMxService.getOrdenesExamenByIdSolicitudV2(solicitudDx.getIdSolicitudDx());
                for (DatosOrdenExamen examen : examenes) {
                    nombreExamen = examen.getExamen();
                    List<ResultadoExamen> resultados = resultadosService.getDetallesResultadoActivosByExamenV2(examen.getIdOrdenExamen());
                    for (ResultadoExamen resultado : resultados) {
                        if (resultado.getTipo().equals("TPDATO|LIST")) {
                            Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(resultado.getValor());
                            detalleResultado = cat_lista.getValor();
                        } else if (resultado.getTipo().equals("TPDATO|LOG")) {
                            detalleResultado = (Boolean.valueOf(resultado.getValor()) ? "lbl.yes" : "lbl.no");
                        } else {
                            detalleResultado = resultado.getValor();
                        }
                        procesadoPor = resultado.getUsuarioProcesa();
                        fechaProcesamiento = new Date(resultado.getFechahProcesa().getTime());
                    }
                }
                String documentoViaje = datosSolicitudService.getIdentificacionViajero(solicitudDx.getIdTomaMx().getIdTomaMx(), persona.getIdentificacion());
                viajero.setCodigoMx(solicitudDx.getIdTomaMx().getCodigoLab());
                viajero.setIdentificacion((persona.getIdentificacion() != null ? persona.getIdentificacion() : documentoViaje));
                viajero.setDocumentoViaje(documentoViaje);
                viajero.setNombres(nombres);
                viajero.setApellidos(apellidos);
                viajero.setFechaNacimiento(persona.getFechaNacimiento());
                viajero.setSilais((tomaMx.getCodSilaisAtencion() != null ? tomaMx.getCodSilaisAtencion().getNombre().replaceAll("SILAIS ", "") : ""));
                viajero.setMunicipio((tomaMx.getCodUnidadAtencion() != null ? tomaMx.getCodUnidadAtencion().getMunicipio().getNombre() : ""));
                viajero.setUnidadSalud((tomaMx.getCodUnidadAtencion() != null ? tomaMx.getCodUnidadAtencion().getNombre() : ""));
                viajero.setTipoMuestra(tomaMx.getCodTipoMx().getNombre());
                viajero.setFechaTomaMuestra(tomaMx.getFechaHTomaMx());
                viajero.setExamen(nombreExamen);
                viajero.setResultado(detalleResultado);
                viajero.setFechaProcesamiento(fechaProcesamiento);
                viajero.setProcesadoPor(procesadoPor);
                viajero.setAprobadoPor(solicitudDx.getUsuarioAprobacion().getCompleteName());
                viajero.setCodigoValidacion(tomaMx.getCodigoValidacion());

                serviciosEnLineaService.saveOrUpdateResultadoViajero(viajero);
            }
        }catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
        return 1;
    }

    private FiltroMx jsonToFiltroDx(String strJson) throws Exception {
        JsonObject jObjectFiltro = new Gson().fromJson(strJson, JsonObject.class);
        FiltroMx filtroMx = new FiltroMx();
        String nombreApellido = null;
        String codigoUnicoMx = null;
        Date fechaInicioAprob = null;
        Date fechaFinAprob = null;
        String resultadoFinal = null;

        if (jObjectFiltro.get("nombreApellido") != null && !jObjectFiltro.get("nombreApellido").getAsString().isEmpty())
            nombreApellido = jObjectFiltro.get("nombreApellido").getAsString();
        if (jObjectFiltro.get("codigoUnicoMx") != null && !jObjectFiltro.get("codigoUnicoMx").getAsString().isEmpty())
            codigoUnicoMx = jObjectFiltro.get("codigoUnicoMx").getAsString();
        if (jObjectFiltro.get("fechaInicioAprob") != null && !jObjectFiltro.get("fechaInicioAprob").getAsString().isEmpty())
            fechaInicioAprob = DateUtil.StringToDate(jObjectFiltro.get("fechaInicioAprob").getAsString());
        if (jObjectFiltro.get("fechaFinAprob") != null && !jObjectFiltro.get("fechaFinAprob").getAsString().isEmpty())
            fechaFinAprob = DateUtil.StringToDate(jObjectFiltro.get("fechaFinAprob").getAsString());
        if (jObjectFiltro.get("finalRes") != null && !jObjectFiltro.get("finalRes").getAsString().isEmpty())
            resultadoFinal = jObjectFiltro.get("finalRes").getAsString();

        filtroMx.setNombreApellido(nombreApellido);
        filtroMx.setIdDx(Integer.valueOf(parametrosService.getParametroByName("DX_VIAJERO_COVID19").getValor()));
        filtroMx.setCodLaboratio(seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario()).getCodigo());
        filtroMx.setCodigoUnicoMx(codigoUnicoMx);
        filtroMx.setFechaInicioAprob(fechaInicioAprob);
        filtroMx.setFechaFinAprob(fechaFinAprob);
        filtroMx.setResultadoFinal(resultadoFinal);
        return filtroMx;
    }

}
