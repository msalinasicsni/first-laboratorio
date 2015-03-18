package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.estructura.EntidadesAdtvas;
import ni.gob.minsa.laboratorio.domain.muestra.*;
import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
import ni.gob.minsa.laboratorio.domain.resultados.Catalogo_Lista;
import ni.gob.minsa.laboratorio.domain.resultados.DetalleResultado;
import ni.gob.minsa.laboratorio.domain.resultados.DetalleResultadoFinal;
import ni.gob.minsa.laboratorio.domain.resultados.RespuestaExamen;
import ni.gob.minsa.laboratorio.service.*;
import ni.gob.minsa.laboratorio.utilities.ConstantsSecurity;
import ni.gob.minsa.laboratorio.utilities.DateUtil;
import org.apache.commons.lang3.text.translate.UnicodeEscaper;
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
import java.util.*;

/**
 * Created by souyen-ics.
 */
@Controller
@RequestMapping("aprobacion")
public class AprobacionResultadoController {

    private static final Logger logger = LoggerFactory.getLogger(AprobacionResultadoController.class);

    @Resource(name = "seguridadService")
    private SeguridadService seguridadService;

    @Resource(name = "catalogosService")
    private CatalogoService catalogoService;

    @Resource(name = "usuarioService")
    private UsuarioService usuarioService;

    @Resource(name = "entidadAdmonService")
    private EntidadAdmonService entidadAdmonService;

    @Resource(name = "resultadoFinalService")
    private ResultadoFinalService resultadoFinalService;

    @Resource(name= "tomaMxService")
    private TomaMxService tomaMxService;

    @Resource(name= "ordenExamenMxService")
    private OrdenExamenMxService ordenExamenMxService;

    @Resource(name= "rechazoResultadoSolicitudService")
    private RechazoResultadoSolicitudService rechazoResultadoSolicitudService;

    @Autowired
    MessageSource messageSource;

    @RequestMapping(value = "init", method = RequestMethod.GET)
    public ModelAndView initForm(HttpServletRequest request) throws Exception {
        logger.debug("Inicio de busqueda de dx para ingreso de resultado final");
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
            List<EntidadesAdtvas> entidadesAdtvases =  entidadAdmonService.getAllEntidadesAdtvas();
            List<TipoMx> tipoMxList = catalogoService.getTipoMuestra();
            mav.addObject("entidades",entidadesAdtvases);
            mav.addObject("tipoMuestra", tipoMxList);

            mav.setViewName("resultados/searchFinalResult");
        }else
            mav.setViewName(urlValidacion);

        return mav;
    }

    @RequestMapping(value = "search", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String fetchDxJson(@RequestParam(value = "strFilter", required = true) String filtro) throws Exception{
        logger.info("Obteniendo los diagnósticos con examenes realizados");
        FiltroMx filtroMx= jsonToFiltroDx(filtro);
        List<DaSolicitudDx> dxList = resultadoFinalService.getDxByFiltro(filtroMx);
        List<DaSolicitudEstudio> solicitudEstudioList = new ArrayList<DaSolicitudEstudio>();
        return solicitudDx_Est_ToJson(dxList, solicitudEstudioList);
    }

    private FiltroMx jsonToFiltroDx(String strJson) throws Exception {
        JsonObject jObjectFiltro = new Gson().fromJson(strJson, JsonObject.class);
        FiltroMx filtroMx = new FiltroMx();
        String nombreApellido = null;
        Date fechaInicioTomaMx = null;
        Date fechaFinTomaMx = null;
        Date fechaInicioRecep = null;
        Date fechaFinRecep = null;
        String codSilais = null;
        String codUnidadSalud = null;
        String codTipoMx = null;
        String codigoUnicoMx = null;
        String codTipoSolicitud = null;
        String nombreSolicitud = null;
        String conResultado = null;

        if (jObjectFiltro.get("nombreApellido") != null && !jObjectFiltro.get("nombreApellido").getAsString().isEmpty())
            nombreApellido = jObjectFiltro.get("nombreApellido").getAsString();
        if (jObjectFiltro.get("fechaInicioTomaMx") != null && !jObjectFiltro.get("fechaInicioTomaMx").getAsString().isEmpty())
            fechaInicioTomaMx = DateUtil.StringToDate(jObjectFiltro.get("fechaInicioTomaMx").getAsString() + " 00:00:00");
        if (jObjectFiltro.get("fechaFinTomaMx") != null && !jObjectFiltro.get("fechaFinTomaMx").getAsString().isEmpty())
            fechaFinTomaMx = DateUtil.StringToDate(jObjectFiltro.get("fechaFinTomaMx").getAsString()+" 23:59:59");
        if (jObjectFiltro.get("fechaInicioRecep") != null && !jObjectFiltro.get("fechaInicioRecep").getAsString().isEmpty())
            fechaInicioRecep = DateUtil.StringToDate(jObjectFiltro.get("fechaInicioRecep").getAsString()+" 00:00:00");
        if (jObjectFiltro.get("fechaFinRecepcion") != null && !jObjectFiltro.get("fechaFinRecepcion").getAsString().isEmpty())
            fechaFinRecep =DateUtil. StringToDate(jObjectFiltro.get("fechaFinRecepcion").getAsString()+" 23:59:59");
        if (jObjectFiltro.get("codSilais") != null && !jObjectFiltro.get("codSilais").getAsString().isEmpty())
            codSilais = jObjectFiltro.get("codSilais").getAsString();
        if (jObjectFiltro.get("codUnidadSalud") != null && !jObjectFiltro.get("codUnidadSalud").getAsString().isEmpty())
            codUnidadSalud = jObjectFiltro.get("codUnidadSalud").getAsString();
        if (jObjectFiltro.get("codTipoMx") != null && !jObjectFiltro.get("codTipoMx").getAsString().isEmpty())
            codTipoMx = jObjectFiltro.get("codTipoMx").getAsString();
        if (jObjectFiltro.get("codigoUnicoMx") != null && !jObjectFiltro.get("codigoUnicoMx").getAsString().isEmpty())
            codigoUnicoMx = jObjectFiltro.get("codigoUnicoMx").getAsString();
        if (jObjectFiltro.get("codTipoSolicitud") != null && !jObjectFiltro.get("codTipoSolicitud").getAsString().isEmpty())
            codTipoSolicitud = jObjectFiltro.get("codTipoSolicitud").getAsString();
        if (jObjectFiltro.get("nombreSolicitud") != null && !jObjectFiltro.get("nombreSolicitud").getAsString().isEmpty())
            nombreSolicitud = jObjectFiltro.get("nombreSolicitud").getAsString();
        if (jObjectFiltro.get("conResultado") != null && !jObjectFiltro.get("conResultado").getAsString().isEmpty())
            conResultado = jObjectFiltro.get("conResultado").getAsString();


        filtroMx.setCodSilais(codSilais);
        filtroMx.setCodUnidadSalud(codUnidadSalud);
        filtroMx.setFechaInicioTomaMx(fechaInicioTomaMx);
        filtroMx.setFechaFinTomaMx(fechaFinTomaMx);
        filtroMx.setFechaInicioRecep(fechaInicioRecep);
        filtroMx.setFechaFinRecep(fechaFinRecep);
        filtroMx.setNombreApellido(nombreApellido);
        filtroMx.setCodTipoMx(codTipoMx);
        filtroMx.setCodTipoSolicitud(codTipoSolicitud);
        filtroMx.setNombreSolicitud(nombreSolicitud);
        filtroMx.setCodEstado("ESTDMX|RCLAB"); // sólo las recepcionadas en laboratorio
        filtroMx.setCodigoUnicoMx(codigoUnicoMx);
        filtroMx.setResultado(conResultado);

        return filtroMx;
    }

    private  String solicitudDx_Est_ToJson(List<DaSolicitudDx> solicitudDxList, List<DaSolicitudEstudio> solicitudEstudioList){
        String jsonResponse="";
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice=0;
        for(DaSolicitudDx diagnostico : solicitudDxList){
            Map<String, String> map = new HashMap<String, String>();
            map.put("codigoUnicoMx", diagnostico.getIdTomaMx().getCodigoUnicoMx());
            map.put("idTomaMx", diagnostico.getIdTomaMx().getIdTomaMx());
            map.put("fechaTomaMx",DateUtil.DateToString(diagnostico.getIdTomaMx().getFechaHTomaMx(),"dd/MM/yyyy hh:mm:ss a"));
            map.put("codSilais", diagnostico.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre());
            map.put("codUnidadSalud", diagnostico.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre());
            map.put("tipoMuestra", diagnostico.getIdTomaMx().getCodTipoMx().getNombre());
            //Si hay fecha de inicio de sintomas se muestra
            Date fechaInicioSintomas = tomaMxService.getFechaInicioSintomas(diagnostico.getIdTomaMx().getIdNotificacion().getIdNotificacion());
            if (fechaInicioSintomas!=null)
                map.put("fechaInicioSintomas",DateUtil.DateToString(fechaInicioSintomas,"dd/MM/yyyy"));
            else
                map.put("fechaInicioSintomas"," ");

            //Si hay persona
            if (diagnostico.getIdTomaMx().getIdNotificacion().getPersona()!=null){
                /// se obtiene el nombre de la persona asociada a la ficha
                String nombreCompleto = "";
                nombreCompleto = diagnostico.getIdTomaMx().getIdNotificacion().getPersona().getPrimerNombre();
                if (diagnostico.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre()!=null)
                    nombreCompleto = nombreCompleto +" "+ diagnostico.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre();
                nombreCompleto = nombreCompleto+" "+ diagnostico.getIdTomaMx().getIdNotificacion().getPersona().getPrimerApellido();
                if (diagnostico.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido()!=null)
                    nombreCompleto = nombreCompleto +" "+ diagnostico.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido();
                map.put("persona",nombreCompleto);
            }else{
                map.put("persona"," ");
            }

            map.put("solicitud", diagnostico.getCodDx().getNombre());
            map.put("idSolicitud", diagnostico.getIdSolicitudDx());

            mapResponse.put(indice, map);
            indice ++;
        }

        for(DaSolicitudEstudio estudio : solicitudEstudioList){
            Map<String, String> map = new HashMap<String, String>();
            map.put("codigoUnicoMx", estudio.getIdTomaMx().getCodigoUnicoMx());
            map.put("idTomaMx", estudio.getIdTomaMx().getIdTomaMx());
            map.put("fechaTomaMx",DateUtil.DateToString(estudio.getIdTomaMx().getFechaHTomaMx(),"dd/MM/yyyy hh:mm:ss a"));
            map.put("codSilais", estudio.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre());
            map.put("codUnidadSalud", estudio.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre());
            map.put("tipoMuestra", estudio.getIdTomaMx().getCodTipoMx().getNombre());
            //Si hay fecha de inicio de sintomas se muestra
            Date fechaInicioSintomas = tomaMxService.getFechaInicioSintomas(estudio.getIdTomaMx().getIdNotificacion().getIdNotificacion());
            if (fechaInicioSintomas!=null)
                map.put("fechaInicioSintomas",DateUtil.DateToString(fechaInicioSintomas,"dd/MM/yyyy"));
            else
                map.put("fechaInicioSintomas"," ");

            //Si hay persona
            if (estudio.getIdTomaMx().getIdNotificacion().getPersona()!=null){
                /// se obtiene el nombre de la persona asociada a la ficha
                String nombreCompleto = "";
                nombreCompleto = estudio.getIdTomaMx().getIdNotificacion().getPersona().getPrimerNombre();
                if (estudio.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre()!=null)
                    nombreCompleto = nombreCompleto +" "+ estudio.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre();
                nombreCompleto = nombreCompleto+" "+ estudio.getIdTomaMx().getIdNotificacion().getPersona().getPrimerApellido();
                if (estudio.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido()!=null)
                    nombreCompleto = nombreCompleto +" "+ estudio.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido();
                map.put("persona",nombreCompleto);
            }else{
                map.put("persona"," ");
            }

            map.put("solicitud", estudio.getTipoEstudio().getNombre());
            map.put("idSolicitud", estudio.getIdSolicitudEstudio());

            mapResponse.put(indice, map);
            indice ++;
        }

        jsonResponse = new Gson().toJson(mapResponse);
        UnicodeEscaper escaper     = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }

    @RequestMapping(value = "create/{idSolicitud}", method = RequestMethod.GET)
    public ModelAndView initCreationForm(@PathVariable("idSolicitud") String idSolicitud, HttpServletRequest request) throws Exception {
        logger.debug("Iniciando el ingreso de resultado final");
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

            if(idSolicitud != null){
                Date fechaInicioSintomas = null;
                DaSolicitudDx solicitudDx =  tomaMxService.getSolicitudDxByIdSolicitud(idSolicitud);
                DaSolicitudEstudio solicitudEstudio =  tomaMxService.getSolicitudEstByIdSolicitud(idSolicitud);
                if (solicitudDx!=null) {
                    fechaInicioSintomas =  tomaMxService.getFechaInicioSintomas(solicitudDx.getIdTomaMx().getIdNotificacion().getIdNotificacion());
                }else {
                    fechaInicioSintomas = tomaMxService.getFechaInicioSintomas(solicitudEstudio.getIdTomaMx().getIdNotificacion().getIdNotificacion());
                }
                mav.addObject("solicitudDx", solicitudDx);
                mav.addObject("solicitudEstudio", solicitudEstudio);
                mav.addObject("fechaInicioSintomas",fechaInicioSintomas);
                mav.setViewName("resultados/approveResult");
            }

        }else
            mav.setViewName(urlValidacion);

        return mav;
    }

    @RequestMapping(value = "searchSolicitud", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String fetchExamsJson(@RequestParam(value = "idSolicitud", required = true) String idSolicitud) throws Exception{
        logger.info("Obteniendo los examenes realizados");
        DaSolicitudDx solicitudDx = tomaMxService.getSolicitudDxByIdSolicitud(idSolicitud);
        DaSolicitudEstudio solicitudEstudio = tomaMxService.getSolicitudEstByIdSolicitud(idSolicitud);
        return resultadoSolicitudToJson(solicitudDx, solicitudEstudio);
    }

    private  String resultadoSolicitudToJson(DaSolicitudDx diagnostico, DaSolicitudEstudio estudio){
        String jsonResponse="";
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice=0;
        Map<String, String> map = new HashMap<String, String>();
        String idSolicitud="";
        if (diagnostico!=null) {
            idSolicitud = diagnostico.getIdSolicitudDx();
        }
        if(estudio!=null){
            idSolicitud = estudio.getIdSolicitudEstudio();
        }

        //detalle resultado solicitud
        List<DetalleResultadoFinal> resultList = resultadoFinalService.getDetResActivosBySolicitud(idSolicitud);
        for(DetalleResultadoFinal res: resultList){
            if (res.getRespuesta()!=null) {
                if (res.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                    Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(res.getValor());
                    map.put("valor", cat_lista.getValor());
                }else if (res.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                    String valorBoleano = (Boolean.valueOf(res.getValor())?"lbl.yes":"lbl.no");
                    map.put("valor", messageSource.getMessage(valorBoleano, null, null));
                } else {
                    map.put("valor", res.getValor());
                }
                map.put("respuesta", res.getRespuesta().getNombre());

            }else if (res.getRespuestaExamen()!=null){
                if (res.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                    Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(res.getValor());
                    map.put("valor", cat_lista.getValor());
                } else if (res.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                    String valorBoleano = (Boolean.valueOf(res.getValor())?"lbl.yes":"lbl.no");
                    map.put("valor", messageSource.getMessage(valorBoleano,null,null));
                }else {
                    map.put("valor", res.getValor());
                }
                map.put("respuesta", res.getRespuestaExamen().getNombre());
            }
            map.put("fechaResultado", DateUtil.DateToString(res.getFechahRegistro(), "dd/MM/yyyy hh:mm:ss a"));
            mapResponse.put(indice,map);
            indice++;
            map = new HashMap<String, String>();
        }
        jsonResponse = new Gson().toJson(mapResponse);
        UnicodeEscaper escaper     = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }


    @RequestMapping(value = "approveResult", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void approveResult(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        String idSolicitud="";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            idSolicitud = jsonpObject.get("idSolicitud").getAsString();
            DaSolicitudDx solicitudDx = tomaMxService.getSolicitudDxByIdSolicitud(idSolicitud);
            DaSolicitudEstudio solicitudEstudio = tomaMxService.getSolicitudEstByIdSolicitud(idSolicitud);
            if (solicitudEstudio == null && solicitudDx == null){
                throw new Exception(messageSource.getMessage("msg.approve.result.solic.not.found",null,null));
            }else {
                long idUsuario = seguridadService.obtenerIdUsuario(request);
                Usuarios usuario = usuarioService.getUsuarioById((int) idUsuario);
                //se obtiene datos de los conceptos a registrar
                if (solicitudDx!=null){
                    solicitudDx.setAprobada(true);
                    solicitudDx.setFechaAprobacion(new Date());
                    solicitudDx.setUsuarioAprobacion(usuario);
                    tomaMxService.updateSolicitudDx(solicitudDx);
                }
                if (solicitudEstudio!=null){
                    solicitudEstudio.setAprobada(true);
                    solicitudEstudio.setFechaAprobacion(new Date());
                    solicitudEstudio.setUsuarioAprobacion(usuario);
                    tomaMxService.updateSolicitudEstudio(solicitudEstudio);
                }

            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.approve.result.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("idSolicitud",idSolicitud);
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = "rejectResult", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void rejectResult(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        String idSolicitud="";
        String causaRechazo = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            idSolicitud = jsonpObject.get("idSolicitud").getAsString();
            causaRechazo = jsonpObject.get("causaRechazo").getAsString();
            DaSolicitudDx solicitudDx = tomaMxService.getSolicitudDxByIdSolicitud(idSolicitud);
            DaSolicitudEstudio solicitudEstudio = tomaMxService.getSolicitudEstByIdSolicitud(idSolicitud);

            if (solicitudEstudio == null && solicitudDx == null){
                throw new Exception(messageSource.getMessage("msg.approve.result.solic.not.found",null,null));
            }else {
                long idUsuario = seguridadService.obtenerIdUsuario(request);
                Usuarios usuario = usuarioService.getUsuarioById((int) idUsuario);
                List<OrdenExamen> ordenExamenList = ordenExamenMxService.getOrdenesExamenNoAnuladasByIdSolicitud(idSolicitud);
                for(OrdenExamen ordenExamen:ordenExamenList){
                    //se anula orden actual
                    ordenExamen.setAnulado(true);
                    ordenExamenMxService.updateOrdenExamen(ordenExamen);
                    //se agrega nueva orden de examen
                    OrdenExamen nuevaOrdenExamen = new OrdenExamen();
                    nuevaOrdenExamen.setSolicitudEstudio(ordenExamen.getSolicitudEstudio());
                    nuevaOrdenExamen.setUsarioRegistro(usuario);
                    nuevaOrdenExamen.setCodExamen(ordenExamen.getCodExamen());
                    nuevaOrdenExamen.setFechaHOrden(new Timestamp(new Date().getTime()));
                    nuevaOrdenExamen.setSolicitudDx(ordenExamen.getSolicitudDx());
                    ordenExamenMxService.addOrdenExamen(nuevaOrdenExamen);
                }
                RechazoResultadoFinalSolicitud rechazo = new RechazoResultadoFinalSolicitud();
                rechazo.setSolicitudDx(solicitudDx);
                rechazo.setSolicitudEstudio(solicitudEstudio);
                rechazo.setFechaHRechazo(new Timestamp(new Date().getTime()));
                rechazo.setUsarioRechazo(usuario);
                rechazo.setCausaRechazo(causaRechazo);
                //se registra el rechazo
                rechazoResultadoSolicitudService.addRechazoResultadoSolicitud(rechazo);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.approve.result.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("idSolicitud",idSolicitud);
            map.put("causaRechazo",causaRechazo);
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }
}
