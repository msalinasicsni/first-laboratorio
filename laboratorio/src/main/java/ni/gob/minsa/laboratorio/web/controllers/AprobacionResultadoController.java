package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.estructura.EntidadesAdtvas;
import ni.gob.minsa.laboratorio.domain.muestra.*;
import ni.gob.minsa.laboratorio.domain.persona.SisPersona;
import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
import ni.gob.minsa.laboratorio.domain.concepto.Catalogo_Lista;
import ni.gob.minsa.laboratorio.domain.resultados.DetalleResultado;
import ni.gob.minsa.laboratorio.domain.resultados.DetalleResultadoFinal;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.User;
import ni.gob.minsa.laboratorio.domain.solicitante.Solicitante;
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

    @Resource(name= "resultadosService")
    private ResultadosService resultadosService;

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
        return solicitudDx_Est_ToJson(dxList, solicitudEstudioList, true);
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
        Date fechaInicioProc = null;
        Date fechaFinProc = null;
        Date fechaInicioAprob = null;
        Date fechaFinAprob = null;
        Date fechaInicioRechazo = null;
        Date fechaFinRechazo = null;

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
        if (jObjectFiltro.get("fecInicioProc") != null && !jObjectFiltro.get("fecInicioProc").getAsString().isEmpty())
            fechaInicioProc = DateUtil.StringToDate(jObjectFiltro.get("fecInicioProc").getAsString()+" 00:00:00");
        if (jObjectFiltro.get("fecFinProc") != null && !jObjectFiltro.get("fecFinProc").getAsString().isEmpty())
            fechaFinProc =DateUtil. StringToDate(jObjectFiltro.get("fecFinProc").getAsString()+" 23:59:59");
        if (jObjectFiltro.get("fechaInicioAprob") != null && !jObjectFiltro.get("fechaInicioAprob").getAsString().isEmpty())
            fechaInicioAprob = DateUtil.StringToDate(jObjectFiltro.get("fechaInicioAprob").getAsString() + " 00:00:00");
        if (jObjectFiltro.get("fechaFinAprob") != null && !jObjectFiltro.get("fechaFinAprob").getAsString().isEmpty())
            fechaFinAprob = DateUtil.StringToDate(jObjectFiltro.get("fechaFinAprob").getAsString() + " 23:59:59");
        if (jObjectFiltro.get("fechaInicioRechazo") != null && !jObjectFiltro.get("fechaInicioRechazo").getAsString().isEmpty())
            fechaInicioRechazo = DateUtil.StringToDate(jObjectFiltro.get("fechaInicioRechazo").getAsString() + " 00:00:00");
        if (jObjectFiltro.get("fechaFinRechazo") != null && !jObjectFiltro.get("fechaFinRechazo").getAsString().isEmpty())
            fechaFinRechazo = DateUtil.StringToDate(jObjectFiltro.get("fechaFinRechazo").getAsString() + " 23:59:59");
        String nombreUsuario = seguridadService.obtenerNombreUsuario();
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
        filtroMx.setNombreUsuario(nombreUsuario);
        filtroMx.setNivelLaboratorio(seguridadService.esDirector(nombreUsuario)?3:seguridadService.esJefeDepartamento(nombreUsuario)?2:1);
        filtroMx.setIncluirTraslados(true);
        filtroMx.setFechaInicioProcesamiento(fechaInicioProc);
        filtroMx.setFechaFinProcesamiento(fechaFinProc);
        filtroMx.setFechaInicioAprob(fechaInicioAprob);
        filtroMx.setFechaFinAprob(fechaFinAprob);
        filtroMx.setFechaInicioRechazo(fechaInicioRechazo);
        filtroMx.setFechaFinRechazo(fechaFinRechazo);
        return filtroMx;
    }

    private  String solicitudDx_Est_ToJson(List<DaSolicitudDx> solicitudDxList, List<DaSolicitudEstudio> solicitudEstudioList, boolean incluirResultados){
        String jsonResponse="";
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice=0;
        for(DaSolicitudDx diagnostico : solicitudDxList){
            Map<String, String> map = new HashMap<String, String>();
            map.put("codigoUnicoMx", diagnostico.getIdTomaMx().getCodigoLab()!=null?diagnostico.getIdTomaMx().getCodigoLab():diagnostico.getIdTomaMx().getCodigoUnicoMx());
            map.put("idTomaMx", diagnostico.getIdTomaMx().getIdTomaMx());
            map.put("fechaTomaMx",DateUtil.DateToString(diagnostico.getIdTomaMx().getFechaHTomaMx(),"dd/MM/yyyy")+
                    (diagnostico.getIdTomaMx().getHoraTomaMx()!=null?" "+diagnostico.getIdTomaMx().getHoraTomaMx():""));
            if (diagnostico.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion()!=null) {
                map.put("codSilais", diagnostico.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre());
            }else{
                map.put("codSilais","");
            }
            if (diagnostico.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion()!=null) {
                map.put("codUnidadSalud", diagnostico.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre());
            }else{
                map.put("codUnidadSalud","");
            }
            map.put("tipoMuestra", diagnostico.getIdTomaMx().getCodTipoMx().getNombre());
            map.put("tipoNotificacion", diagnostico.getIdTomaMx().getIdNotificacion().getCodTipoNotificacion().getValor());
            //Si hay fecha de inicio de sintomas se muestra
            Date fechaInicioSintomas = diagnostico.getIdTomaMx().getIdNotificacion().getFechaInicioSintomas();
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
            }else if (diagnostico.getIdTomaMx().getIdNotificacion().getSolicitante() != null) {
                map.put("persona", diagnostico.getIdTomaMx().getIdNotificacion().getSolicitante().getNombre());
            }else if (diagnostico.getIdTomaMx().getIdNotificacion().getCodigoPacienteVIH() != null) {
                map.put("persona", diagnostico.getIdTomaMx().getIdNotificacion().getCodigoPacienteVIH());
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
                //int subIndice=1;
                //Map<Integer, Object> mapResponseResp = new HashMap<Integer, Object>();
                String resultados="";
                for(DetalleResultadoFinal res: resultList){
                    //Map<String, String> mapResp = new HashMap<String, String>();
                    if (res.getRespuesta()!=null) {
                        resultados+=(resultados.isEmpty()?res.getRespuesta().getNombre():", "+res.getRespuesta().getNombre());
                        if (res.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                            Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(res.getValor());
                            //mapResp.put("valor", cat_lista.getValor());
                            resultados+=": "+cat_lista.getValor();
                        }else if (res.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                            String valorBoleano = (Boolean.valueOf(res.getValor())?"lbl.yes":"lbl.no");
                            //mapResp.put("valor", messageSource.getMessage(valorBoleano, null, null));
                            resultados+=": "+valorBoleano;
                        } else {
                            //mapResp.put("valor", res.getValor());
                            resultados+=": "+res.getValor();
                        }
                        //mapResp.put("respuesta", res.getRespuesta().getNombre());

                    }else if (res.getRespuestaExamen()!=null){
                        resultados+=(resultados.isEmpty()?res.getRespuestaExamen().getNombre():", "+res.getRespuestaExamen().getNombre());
                        if (res.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                            Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(res.getValor());
                            //mapResp.put("valor", cat_lista.getValor());
                            resultados+=": "+cat_lista.getValor();
                        } else if (res.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                            String valorBoleano = (Boolean.valueOf(res.getValor())?"lbl.yes":"lbl.no");
                            //mapResp.put("valor", messageSource.getMessage(valorBoleano,null,null));
                            resultados+=": "+valorBoleano;
                        }else {
                            //mapResp.put("valor", res.getValor());
                            resultados+=": "+res.getValor();
                        }
                        //mapResp.put("respuesta", res.getRespuestaExamen().getNombre());
                    }
                    //mapResp.put("fechaResultado", DateUtil.DateToString(res.getFechahRegistro(), "dd/MM/yyyy hh:mm:ss a"));
                    //mapResponseResp.put(subIndice,mapResp);
                    //subIndice++;
                }
                //map.put("resultados",new Gson().toJson(mapResponseResp));
                map.put("resultados",resultados);
            }
            mapResponse.put(indice, map);
            indice ++;
        }

        for(DaSolicitudEstudio estudio : solicitudEstudioList){
            Map<String, String> map = new HashMap<String, String>();
            map.put("codigoUnicoMx", estudio.getIdTomaMx().getCodigoUnicoMx());
            map.put("idTomaMx", estudio.getIdTomaMx().getIdTomaMx());
            map.put("fechaTomaMx",DateUtil.DateToString(estudio.getIdTomaMx().getFechaHTomaMx(),"dd/MM/yyyy")+
                    (estudio.getIdTomaMx().getHoraTomaMx()!=null?" "+estudio.getIdTomaMx().getHoraTomaMx():""));
            map.put("codSilais", estudio.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre());
            map.put("codUnidadSalud", estudio.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre());
            map.put("tipoMuestra", estudio.getIdTomaMx().getCodTipoMx().getNombre());
            map.put("tipoNotificacion", estudio.getIdTomaMx().getIdNotificacion().getCodTipoNotificacion().getValor());
            //Si hay fecha de inicio de sintomas se muestra
            Date fechaInicioSintomas = estudio.getIdTomaMx().getIdNotificacion().getFechaInicioSintomas();
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
                String resultados="";
                //detalle resultado final solicitud
                List<DetalleResultadoFinal> resultList = resultadoFinalService.getDetResActivosBySolicitud(estudio.getIdSolicitudEstudio());
                //int subIndice=1;
                //Map<Integer, Object> mapResponseResp = new HashMap<Integer, Object>();
                for(DetalleResultadoFinal res: resultList){
                    //Map<String, String> mapResp = new HashMap<String, String>();
                    if (res.getRespuesta()!=null) {
                        resultados+=(resultados.isEmpty()?res.getRespuesta().getNombre():", "+res.getRespuesta().getNombre());
                        if (res.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                            Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(res.getValor());
                            //mapResp.put("valor", cat_lista.getValor());
                            resultados+=": "+cat_lista.getValor();
                        }else if (res.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                            String valorBoleano = (Boolean.valueOf(res.getValor())?"lbl.yes":"lbl.no");
                            //mapResp.put("valor", messageSource.getMessage(valorBoleano, null, null));
                            resultados+=": "+valorBoleano;
                        } else {
                            //mapResp.put("valor", res.getValor());
                            resultados+=": "+res.getValor();
                        }
                        //mapResp.put("respuesta", res.getRespuesta().getNombre());

                    }else if (res.getRespuestaExamen()!=null){
                        resultados+=(resultados.isEmpty()?res.getRespuestaExamen().getNombre():", "+res.getRespuestaExamen().getNombre());
                        if (res.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                            Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(res.getValor());
                            //mapResp.put("valor", cat_lista.getValor());
                            resultados+=": "+cat_lista.getValor();
                        } else if (res.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                            String valorBoleano = (Boolean.valueOf(res.getValor())?"lbl.yes":"lbl.no");
                            //mapResp.put("valor", messageSource.getMessage(valorBoleano,null,null));
                            resultados+=": "+valorBoleano;
                        }else {
                            //mapResp.put("valor", res.getValor());
                            resultados+=": "+res.getValor();
                        }
                        //mapResp.put("respuesta", res.getRespuestaExamen().getNombre());
                    }
                    //mapResp.put("fechaResultado", DateUtil.DateToString(res.getFechahRegistro(), "dd/MM/yyyy hh:mm:ss a"));
                    //mapResponseResp.put(subIndice,mapResp);
                    //subIndice++;
                }
                //map.put("resultados",new Gson().toJson(mapResponseResp));
                map.put("resultados",resultados);
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
                    fechaInicioSintomas = solicitudDx.getIdTomaMx().getIdNotificacion().getFechaInicioSintomas();
                }else {
                    fechaInicioSintomas = solicitudEstudio.getIdTomaMx().getIdNotificacion().getFechaInicioSintomas();
                }
                SisPersona persona;
                Solicitante solicitante;
                if (solicitudDx!=null){
                    persona = solicitudDx.getIdTomaMx().getIdNotificacion().getPersona();
                    solicitante = solicitudDx.getIdTomaMx().getIdNotificacion().getSolicitante();
                }else{
                    persona = solicitudEstudio.getIdTomaMx().getIdNotificacion().getPersona();
                    solicitante = solicitudEstudio.getIdTomaMx().getIdNotificacion().getSolicitante();
                }
                mav.addObject("solicitudDx", solicitudDx);
                mav.addObject("solicitudEstudio", solicitudEstudio);
                mav.addObject("fechaInicioSintomas",fechaInicioSintomas);
                mav.addObject("persona", persona);
                mav.addObject("solicitante", solicitante);
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
        Date fechaHoraAprobacion = new Date();
        String fechaAprobacion = "";
        String horaAprobacion = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            idSolicitud = jsonpObject.get("idSolicitud").getAsString();
            if (jsonpObject.get("fechaAprobacion") != null && !jsonpObject.get("fechaAprobacion").getAsString().isEmpty())
                fechaAprobacion = jsonpObject.get("fechaAprobacion").getAsString();
            if (jsonpObject.get("horaAprobacion") != null && !jsonpObject.get("horaAprobacion").getAsString().isEmpty())
                horaAprobacion = jsonpObject.get("horaAprobacion").getAsString();

            if (!fechaAprobacion.isEmpty()){
                if (horaAprobacion.isEmpty())
                    fechaHoraAprobacion = DateUtil.StringToDate(fechaAprobacion, "dd/MM/yyyy");
                else
                    fechaHoraAprobacion = DateUtil.StringToDate(fechaAprobacion+ " "+horaAprobacion, "dd/MM/yyyy hh:mm a");
            }

            DaSolicitudDx solicitudDx = tomaMxService.getSolicitudDxByIdSolicitud(idSolicitud);
            DaSolicitudEstudio solicitudEstudio = tomaMxService.getSolicitudEstByIdSolicitud(idSolicitud);
            if (solicitudEstudio == null && solicitudDx == null){
                throw new Exception(messageSource.getMessage("msg.approve.result.solic.not.found",null,null));
            }else {
                User usuario = seguridadService.getUsuario(seguridadService.obtenerNombreUsuario());
                if (solicitudDx!=null){
                    solicitudDx.setAprobada(true);
                    solicitudDx.setFechaAprobacion(fechaHoraAprobacion);
                    solicitudDx.setUsuarioAprobacion(usuario);
                    tomaMxService.updateSolicitudDx(solicitudDx);
                }
                if (solicitudEstudio!=null){
                    solicitudEstudio.setAprobada(true);
                    solicitudEstudio.setFechaAprobacion(fechaHoraAprobacion);
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

    @RequestMapping(value = "approveMassiveResult", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void approveMassiveResult(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        String idSolicitud="";
        Integer cantAprobProc = 0;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            String strSolicitudes = jsonpObject.get("strSolicitudes").toString();

            JsonObject jObjectSolicitudes = new Gson().fromJson(strSolicitudes, JsonObject.class);

            Integer cantAprobaciones = jsonpObject.get("cantAprobaciones").getAsInt();

            User usuario = seguridadService.getUsuario(seguridadService.obtenerNombreUsuario());

            for (int i = 0; i < cantAprobaciones; i++){
                idSolicitud = jObjectSolicitudes.get(String.valueOf(i)).getAsString();
                DaSolicitudDx solicitudDx = tomaMxService.getSolicitudDxByIdSolicitud(idSolicitud);
                DaSolicitudEstudio solicitudEstudio = tomaMxService.getSolicitudEstByIdSolicitud(idSolicitud);
                if (solicitudEstudio == null && solicitudDx == null) {
                    throw new Exception(messageSource.getMessage("msg.approve.result.solic.not.found", null, null));
                } else {
                    if (solicitudDx != null) {
                        solicitudDx.setAprobada(true);
                        solicitudDx.setFechaAprobacion(new Date());
                        solicitudDx.setUsuarioAprobacion(usuario);
                        tomaMxService.updateSolicitudDx(solicitudDx);
                    }
                    if (solicitudEstudio != null) {
                        solicitudEstudio.setAprobada(true);
                        solicitudEstudio.setFechaAprobacion(new Date());
                        solicitudEstudio.setUsuarioAprobacion(usuario);
                        tomaMxService.updateSolicitudEstudio(solicitudEstudio);
                    }
                    cantAprobProc++;
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
            map.put("cantAprobaciones","-");
            map.put("cantAprobProc",cantAprobProc.toString());
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
        logger.info("Obteniendo las solicitudes con resultados aprobados");
        FiltroMx filtroMx= jsonToFiltroDx(filtro);
        filtroMx.setCodEstado(null);
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
        String idOrdenes="";
        Integer cantOrdenes;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            idSolicitud = jsonpObject.get("idSolicitud").getAsString();
            idOrdenes = jsonpObject.get("idOrdenes").toString();
            causaRechazo = jsonpObject.get("causaRechazo").getAsString();
            cantOrdenes = jsonpObject.get("cantOrdenes").getAsInt();
            DaSolicitudDx solicitudDx = tomaMxService.getSolicitudDxByIdSolicitud(idSolicitud);
            DaSolicitudEstudio solicitudEstudio = tomaMxService.getSolicitudEstByIdSolicitud(idSolicitud);

            if (solicitudEstudio == null && solicitudDx == null){
                throw new Exception(messageSource.getMessage("msg.approve.result.solic.not.found",null,null));
            } else {
                User usuario = seguridadService.getUsuario(seguridadService.obtenerNombreUsuario());
                Laboratorio labUsuario = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
                JsonObject jObjectOrdenes = new Gson().fromJson(idOrdenes, JsonObject.class);
                //List<OrdenExamen> ordenExamenList = ordenExamenMxService.getOrdenesExamenNoAnuladasByIdSolicitud(idSolicitud);
                //for(OrdenExamen ordenExamen:ordenExamenList){
                for(int i = 0; i< cantOrdenes;i++) {
                    String idOrden = jObjectOrdenes.get(String.valueOf(i)).getAsString();
                    OrdenExamen ordenExamen = ordenExamenMxService.getOrdenExamenById(idOrden);
                    //se anula orden actual
                    ordenExamen.setAnulado(true);
                    ordenExamenMxService.updateOrdenExamen(ordenExamen);
                    //se anulan resultado de la orden
                    List<DetalleResultado> detalleResultados = resultadosService.getDetallesResultadoActivosByExamen(idOrden);

                    for(DetalleResultado detalleResultado :detalleResultados){
                        detalleResultado.setPasivo(true);
                        detalleResultado.setRazonAnulacion(causaRechazo);
                        detalleResultado.setUsuarioAnulacion(usuario);
                        detalleResultado.setFechahAnulacion(new Timestamp(new Date().getTime()));
                        resultadosService.updateDetalleResultado(detalleResultado);
                    }

                    //se agrega nueva orden de examen
                    OrdenExamen nuevaOrdenExamen = new OrdenExamen();
                    nuevaOrdenExamen.setSolicitudEstudio(ordenExamen.getSolicitudEstudio());
                    nuevaOrdenExamen.setUsuarioRegistro(usuario);
                    nuevaOrdenExamen.setCodExamen(ordenExamen.getCodExamen());
                    nuevaOrdenExamen.setFechaHOrden(new Timestamp(new Date().getTime()));
                    nuevaOrdenExamen.setSolicitudDx(ordenExamen.getSolicitudDx());
                    nuevaOrdenExamen.setLabProcesa(labUsuario);
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
        filtroMx.setCodEstado(null);
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
                map.put("codigoUnicoMx", tomaMx.getCodigoLab());
            }else{
                tomaMx = rechazo.getSolicitudEstudio().getIdTomaMx();
                map.put("nombreSolicitud", rechazo.getSolicitudEstudio().getTipoEstudio().getNombre());
                map.put("fechaSolicitud",DateUtil.DateToString(rechazo.getSolicitudEstudio().getFechaHSolicitud(),"dd/MM/yyyy hh:mm:ss a"));
                idSolicitud = rechazo.getSolicitudEstudio().getIdSolicitudEstudio();
                map.put("codigoUnicoMx", tomaMx.getCodigoUnicoMx());
            }
            map.put("fechaTomaMx",DateUtil.DateToString(tomaMx.getFechaHTomaMx(),"dd/MM/yyyy")+
                    (tomaMx.getHoraTomaMx()!=null?" "+tomaMx.getHoraTomaMx():""));
            map.put("tipoMx", tomaMx.getCodTipoMx().getNombre());
            map.put("tipoNotificacion", tomaMx.getIdNotificacion().getCodTipoNotificacion().getValor());
            //Si hay fecha de inicio de sintomas se muestra
            Date fechaInicioSintomas = tomaMx.getIdNotificacion().getFechaInicioSintomas();
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
            }else if (tomaMx.getIdNotificacion().getSolicitante() != null) {
                map.put("persona", tomaMx.getIdNotificacion().getSolicitante().getNombre());
            }else{
                map.put("persona"," ");
            }

            map.put("fechaRechazo",DateUtil.DateToString(rechazo.getFechaHRechazo(),"dd/MM/yyyy hh:mm:ss a"));

            //detalle resultado final solicitud
            List<DetalleResultadoFinal> resultList = resultadoFinalService.getDetResPasivosBySolicitud(idSolicitud);
            //int subIndice=1;
            //Map<Integer, Object> mapResponseResp = new HashMap<Integer, Object>();
            String resultados="";
            for(DetalleResultadoFinal res: resultList){
                //Map<String, String> mapResp = new HashMap<String, String>();
                if (res.getRespuesta()!=null) {
                    resultados+=(resultados.isEmpty()?res.getRespuesta().getNombre():", "+res.getRespuesta().getNombre());
                    if (res.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                        Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(res.getValor());
                        //mapResp.put("valor", cat_lista.getValor());
                        resultados+=": "+cat_lista.getValor();
                    }else if (res.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                        String valorBoleano = (Boolean.valueOf(res.getValor())?"lbl.yes":"lbl.no");
                        //mapResp.put("valor", messageSource.getMessage(valorBoleano, null, null));
                        resultados+=": "+valorBoleano;
                    } else {
                        //mapResp.put("valor", res.getValor());
                        resultados+=": "+res.getValor();
                    }
                    //mapResp.put("respuesta", res.getRespuesta().getNombre());

                }else if (res.getRespuestaExamen()!=null){
                    resultados+=(resultados.isEmpty()?res.getRespuestaExamen().getNombre():", "+res.getRespuestaExamen().getNombre());
                    if (res.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                        Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(res.getValor());
                        //mapResp.put("valor", cat_lista.getValor());
                        resultados+=": "+cat_lista.getValor();
                    } else if (res.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                        String valorBoleano = (Boolean.valueOf(res.getValor())?"lbl.yes":"lbl.no");
                        //mapResp.put("valor", messageSource.getMessage(valorBoleano,null,null));
                        resultados+=": "+valorBoleano;
                    }else {
                        //mapResp.put("valor", res.getValor());
                        resultados+=": "+res.getValor();
                    }
                    //mapResp.put("respuesta", res.getRespuestaExamen().getNombre());
                }
                //mapResp.put("fechaResultado", DateUtil.DateToString(res.getFechahRegistro(), "dd/MM/yyyy hh:mm:ss a"));
                //mapResponseResp.put(subIndice,mapResp);
                //subIndice++;
            }
            //map.put("resultados",new Gson().toJson(mapResponseResp));
            map.put("resultados",resultados);
            mapResponse.put(indice, map);
            indice++;
        }
        jsonResponse = new Gson().toJson(mapResponse);
        UnicodeEscaper escaper     = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }

    @RequestMapping(value = "examenesRepetir", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    List<OrdenExamen> repeatExams(@RequestParam(value = "idSolicitud", required = true) String idSolicitud) throws Exception {
        logger.info("Realizando búsqueda de Toma de Mx.");
        return ordenExamenMxService.getOrdenesExamenNoAnuladasByIdSolicitud(idSolicitud);
    }

    @RequestMapping(value = "undoApprovalResult", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void undoApprovalResult(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
                throw new Exception(messageSource.getMessage("msg.solic.not.found",null,null));
            }else {
                if (solicitudDx!=null){
                    solicitudDx.setAprobada(false);
                    solicitudDx.setFechaAprobacion(null);
                    solicitudDx.setUsuarioAprobacion(null);
                    tomaMxService.updateSolicitudDx(solicitudDx);
                } else if (solicitudEstudio!=null){
                    solicitudEstudio.setAprobada(false);
                    solicitudEstudio.setFechaAprobacion(null);
                    solicitudEstudio.setUsuarioAprobacion(null);
                    tomaMxService.updateSolicitudEstudio(solicitudEstudio);
                }

            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.undo.approve.result.error",null,null);
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
}
