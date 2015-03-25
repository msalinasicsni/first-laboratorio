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
    String fetchSolicitudesJson(@RequestParam(value = "strFilter", required = true) String filtro) throws Exception{
        logger.info("Obteniendo los diagnósticos con examenes realizados");
        FiltroMx filtroMx= jsonToFiltroDx(filtro);
        List<DaSolicitudDx> dxList = resultadoFinalService.getDxByFiltro(filtroMx);
        List<DaSolicitudEstudio> solicitudEstudioList = resultadoFinalService.getEstudioByFiltro(filtroMx);
        return solicitudDx_Est_ToJson(dxList, solicitudEstudioList, false);
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
        Boolean solicitudAprobada=null;

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
        if (jObjectFiltro.get("solicitudAprobada") != null && !jObjectFiltro.get("solicitudAprobada").getAsString().isEmpty())
            solicitudAprobada = jObjectFiltro.get("solicitudAprobada").getAsBoolean();

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
        filtroMx.setSolicitudAprobada(solicitudAprobada);

        return filtroMx;
    }

    private  String solicitudDx_Est_ToJson(List<DaSolicitudDx> solicitudDxList, List<DaSolicitudEstudio> solicitudEstudioList, boolean incluirResultados){
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
            map.put("tipoNotificacion", diagnostico.getIdTomaMx().getIdNotificacion().getCodTipoNotificacion().getValor());
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
            map.put("fechaSolicitud",DateUtil.DateToString(diagnostico.getFechaHSolicitud(),"dd/MM/yyyy hh:mm:ss a"));

            if(diagnostico.getAprobada()!=null && diagnostico.getAprobada() && diagnostico.getFechaAprobacion()!=null){
                map.put("fechaAprobacion",DateUtil.DateToString(diagnostico.getFechaAprobacion(),"dd/MM/yyyy hh:mm:ss a"));
            }else {
                map.put("fechaAprobacion","");
            }

            if (incluirResultados){
                //detalle resultado final solicitud
                List<DetalleResultadoFinal> resultList = resultadoFinalService.getDetResActivosBySolicitud(diagnostico.getIdSolicitudDx());
                int subIndice=1;
                Map<Integer, Object> mapResponseResp = new HashMap<Integer, Object>();
                for(DetalleResultadoFinal res: resultList){
                    Map<String, String> mapResp = new HashMap<String, String>();
                    if (res.getRespuesta()!=null) {
                        if (res.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                            Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(res.getValor());
                            mapResp.put("valor", cat_lista.getValor());
                        }else if (res.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                            String valorBoleano = (Boolean.valueOf(res.getValor())?"lbl.yes":"lbl.no");
                            mapResp.put("valor", messageSource.getMessage(valorBoleano, null, null));
                        } else {
                            mapResp.put("valor", res.getValor());
                        }
                        mapResp.put("respuesta", res.getRespuesta().getNombre());

                    }else if (res.getRespuestaExamen()!=null){
                        if (res.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                            Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(res.getValor());
                            mapResp.put("valor", cat_lista.getValor());
                        } else if (res.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                            String valorBoleano = (Boolean.valueOf(res.getValor())?"lbl.yes":"lbl.no");
                            mapResp.put("valor", messageSource.getMessage(valorBoleano,null,null));
                        }else {
                            mapResp.put("valor", res.getValor());
                        }
                        mapResp.put("respuesta", res.getRespuestaExamen().getNombre());
                    }
                    mapResp.put("fechaResultado", DateUtil.DateToString(res.getFechahRegistro(), "dd/MM/yyyy hh:mm:ss a"));
                    mapResponseResp.put(subIndice,mapResp);
                    subIndice++;
                }
                map.put("resultados",new Gson().toJson(mapResponseResp));
            }
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
            map.put("tipoNotificacion", estudio.getIdTomaMx().getIdNotificacion().getCodTipoNotificacion().getValor());
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
            map.put("fechaSolicitud",DateUtil.DateToString(estudio.getFechaHSolicitud(),"dd/MM/yyyy hh:mm:ss a"));
            if(estudio.getAprobada()!=null && estudio.getAprobada() && estudio.getFechaAprobacion()!=null){
                map.put("fechaAprobacion",DateUtil.DateToString(estudio.getFechaAprobacion(),"dd/MM/yyyy hh:mm:ss a"));
            }else {
                map.put("fechaAprobacion","");
            }
            if (incluirResultados){
                //detalle resultado final solicitud
                List<DetalleResultadoFinal> resultList = resultadoFinalService.getDetResActivosBySolicitud(estudio.getIdSolicitudEstudio());
                int subIndice=1;
                Map<Integer, Object> mapResponseResp = new HashMap<Integer, Object>();
                for(DetalleResultadoFinal res: resultList){
                    Map<String, String> mapResp = new HashMap<String, String>();
                    if (res.getRespuesta()!=null) {
                        if (res.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                            Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(res.getValor());
                            mapResp.put("valor", cat_lista.getValor());
                        }else if (res.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                            String valorBoleano = (Boolean.valueOf(res.getValor())?"lbl.yes":"lbl.no");
                            mapResp.put("valor", messageSource.getMessage(valorBoleano, null, null));
                        } else {
                            mapResp.put("valor", res.getValor());
                        }
                        mapResp.put("respuesta", res.getRespuesta().getNombre());

                    }else if (res.getRespuestaExamen()!=null){
                        if (res.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                            Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(res.getValor());
                            mapResp.put("valor", cat_lista.getValor());
                        } else if (res.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                            String valorBoleano = (Boolean.valueOf(res.getValor())?"lbl.yes":"lbl.no");
                            mapResp.put("valor", messageSource.getMessage(valorBoleano,null,null));
                        }else {
                            mapResp.put("valor", res.getValor());
                        }
                        mapResp.put("respuesta", res.getRespuestaExamen().getNombre());
                    }
                    mapResp.put("fechaResultado", DateUtil.DateToString(res.getFechahRegistro(), "dd/MM/yyyy hh:mm:ss a"));
                    mapResponseResp.put(subIndice,mapResp);
                    subIndice++;
                }
                map.put("resultados",new Gson().toJson(mapResponseResp));
            }

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

    @RequestMapping(value = "approved", method = RequestMethod.GET)
    public ModelAndView initApprovedForm(HttpServletRequest request) throws Exception{
        logger.debug("Inicio de busqueda de solicitudes con resultado final aprobado");
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

            mav.setViewName("resultados/approvedResults");
        }else
            mav.setViewName(urlValidacion);

        return mav;
    }

    @RequestMapping(value = "searchApproved", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String fetchAprovedResultsJson(@RequestParam(value = "strFilter", required = true) String filtro) throws Exception{
        logger.info("Obteniendo los solicutdes con resultados rechazados");
        FiltroMx filtroMx= jsonToFiltroDx(filtro);
        List<DaSolicitudDx> solicitudDxList = resultadoFinalService.getDxByFiltro(filtroMx);
        List<DaSolicitudEstudio> solicitudEstudioList = resultadoFinalService.getEstudioByFiltro(filtroMx);
        return solicitudDx_Est_ToJson(solicitudDxList, solicitudEstudioList, true);
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
                //Se anula el detalle del resultado final para la solicitud
                List<DetalleResultadoFinal> resultadoFinalList = resultadoFinalService.getDetResActivosBySolicitud(idSolicitud);
                for(DetalleResultadoFinal resultadoFinal : resultadoFinalList){
                    resultadoFinal.setPasivo(true);
                    resultadoFinalService.updateDetResFinal(resultadoFinal);
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

    @RequestMapping(value = "rejected", method = RequestMethod.GET)
    public ModelAndView initRejectedForm(HttpServletRequest request) throws Exception {
        logger.debug("Inicio de busqueda de solicitudes con resultado final rechazado");
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

            mav.setViewName("resultados/rejectedResults");
        }else
            mav.setViewName(urlValidacion);

        return mav;
    }

    @RequestMapping(value = "searchRejected", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String fetchRejectResultsJson(@RequestParam(value = "strFilter", required = true) String filtro) throws Exception{
        logger.info("Obteniendo los solicutdes con resultados rechazados");
        FiltroMx filtroMx= jsonToFiltroDx(filtro);
        List<RechazoResultadoFinalSolicitud> rechazosList = resultadoFinalService.getResultadosRechazadosByFiltro(filtroMx);
        return rechazosToJson(rechazosList);
    }

    private String rechazosToJson(List<RechazoResultadoFinalSolicitud> rechazosList){
        String jsonResponse="";
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice=0;
        for(RechazoResultadoFinalSolicitud rechazo : rechazosList){
            Map<String, String> map = new HashMap<String, String>();
            DaTomaMx tomaMx;
            String idSolicitud="";
            if (rechazo.getSolicitudDx()!=null){
                tomaMx = rechazo.getSolicitudDx().getIdTomaMx();
                map.put("nombreSolicitud", rechazo.getSolicitudDx().getCodDx().getNombre());
                map.put("fechaSolicitud",DateUtil.DateToString(rechazo.getSolicitudDx().getFechaHSolicitud(),"dd/MM/yyyy hh:mm:ss a"));
                idSolicitud = rechazo.getSolicitudDx().getIdSolicitudDx();
            }else{
                tomaMx = rechazo.getSolicitudEstudio().getIdTomaMx();
                map.put("nombreSolicitud", rechazo.getSolicitudEstudio().getTipoEstudio().getNombre());
                map.put("fechaSolicitud",DateUtil.DateToString(rechazo.getSolicitudEstudio().getFechaHSolicitud(),"dd/MM/yyyy hh:mm:ss a"));
                idSolicitud = rechazo.getSolicitudEstudio().getIdSolicitudEstudio();
            }
            map.put("codigoUnicoMx", tomaMx.getCodigoUnicoMx());
            map.put("fechaTomaMx",DateUtil.DateToString(tomaMx.getFechaHTomaMx(),"dd/MM/yyyy hh:mm:ss a"));
            map.put("tipoMx", tomaMx.getCodTipoMx().getNombre());
            map.put("tipoNotificacion", tomaMx.getIdNotificacion().getCodTipoNotificacion().getValor());
            //Si hay fecha de inicio de sintomas se muestra
            Date fechaInicioSintomas = tomaMxService.getFechaInicioSintomas(tomaMx.getIdNotificacion().getIdNotificacion());
            if (fechaInicioSintomas!=null)
                map.put("fechaInicioSintomas",DateUtil.DateToString(fechaInicioSintomas,"dd/MM/yyyy"));
            else
                map.put("fechaInicioSintomas"," ");

            //Si hay persona
            if (tomaMx.getIdNotificacion().getPersona()!=null){
                /// se obtiene el nombre de la persona asociada a la ficha
                String nombreCompleto = "";
                nombreCompleto = tomaMx.getIdNotificacion().getPersona().getPrimerNombre();
                if (tomaMx.getIdNotificacion().getPersona().getSegundoNombre()!=null)
                    nombreCompleto = nombreCompleto +" "+ tomaMx.getIdNotificacion().getPersona().getSegundoNombre();
                nombreCompleto = nombreCompleto+" "+ tomaMx.getIdNotificacion().getPersona().getPrimerApellido();
                if (tomaMx.getIdNotificacion().getPersona().getSegundoApellido()!=null)
                    nombreCompleto = nombreCompleto +" "+ tomaMx.getIdNotificacion().getPersona().getSegundoApellido();
                map.put("persona",nombreCompleto);
            }else{
                map.put("persona"," ");
            }

            map.put("fechaRechazo",DateUtil.DateToString(rechazo.getFechaHRechazo(),"dd/MM/yyyy hh:mm:ss a"));

            //detalle resultado final solicitud
            List<DetalleResultadoFinal> resultList = resultadoFinalService.getDetResPasivosBySolicitud(idSolicitud);
            int subIndice=1;
            Map<Integer, Object> mapResponseResp = new HashMap<Integer, Object>();
            for(DetalleResultadoFinal res: resultList){
                Map<String, String> mapResp = new HashMap<String, String>();
                if (res.getRespuesta()!=null) {
                    if (res.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                        Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(res.getValor());
                        mapResp.put("valor", cat_lista.getValor());
                    }else if (res.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                        String valorBoleano = (Boolean.valueOf(res.getValor())?"lbl.yes":"lbl.no");
                        mapResp.put("valor", messageSource.getMessage(valorBoleano, null, null));
                    } else {
                        mapResp.put("valor", res.getValor());
                    }
                    mapResp.put("respuesta", res.getRespuesta().getNombre());

                }else if (res.getRespuestaExamen()!=null){
                    if (res.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                        Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(res.getValor());
                        mapResp.put("valor", cat_lista.getValor());
                    } else if (res.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                        String valorBoleano = (Boolean.valueOf(res.getValor())?"lbl.yes":"lbl.no");
                        mapResp.put("valor", messageSource.getMessage(valorBoleano,null,null));
                    }else {
                        mapResp.put("valor", res.getValor());
                    }
                    mapResp.put("respuesta", res.getRespuestaExamen().getNombre());
                }
                mapResp.put("fechaResultado", DateUtil.DateToString(res.getFechahRegistro(), "dd/MM/yyyy hh:mm:ss a"));
                mapResponseResp.put(subIndice,mapResp);
                subIndice++;
            }
            map.put("resultados",new Gson().toJson(mapResponseResp));
            mapResponse.put(indice, map);
            indice++;
        }
        jsonResponse = new Gson().toJson(mapResponse);
        UnicodeEscaper escaper     = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }
}
