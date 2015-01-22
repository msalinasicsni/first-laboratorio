package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.estructura.EntidadesAdtvas;
import ni.gob.minsa.laboratorio.domain.estructura.Unidades;
import ni.gob.minsa.laboratorio.domain.muestra.*;
import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
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
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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
 * Created by FIRSTICT on 12/10/2014.
 */
@Controller
@RequestMapping("recepcionMx")
public class RecepcionMxController {
    private static final Logger logger = LoggerFactory.getLogger(RecepcionMxController.class);
    @Autowired
    @Qualifier(value = "seguridadService")
    private SeguridadService seguridadService;

    @Autowired
    @Qualifier(value = "usuarioService")
    private UsuarioService usuarioService;

    @Autowired
    @Qualifier(value = "catalogosService")
    private CatalogoService catalogosService;

    @Autowired
    @Qualifier(value = "entidadAdmonService")
    private EntidadAdmonService entidadAdmonService;

    @Autowired
    @Qualifier(value = "laboratoriosService")
    private LaboratoriosService laboratoriosService;

    @Autowired
    @Qualifier(value = "recepcionMxService")
    private RecepcionMxService recepcionMxService;

    @Autowired
    @Qualifier(value = "ordenExamenMxService")
    private OrdenExamenMxService ordenExamenMxService;

    @Autowired
    @Qualifier(value = "tomaMxService")
    private TomaMxService tomaMxService;

    @Autowired
    @Qualifier(value = "unidadesService")
    private UnidadesService unidadesService;

    @Autowired
    MessageSource messageSource;

    @RequestMapping(value = "init", method = RequestMethod.GET)
    public ModelAndView initSearchForm(HttpServletRequest request) throws Exception {
        logger.debug("buscar ordenes para recepcion");
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
            List<TipoMx> tipoMxList = catalogosService.getTipoMuestra();
            mav.addObject("entidades",entidadesAdtvases);
            mav.addObject("tipoMuestra", tipoMxList);
            mav.setViewName("recepcionMx/searchOrders");
        }else
            mav.setViewName(urlValidacion);

        return mav;
    }

    @RequestMapping(value = "initLab", method = RequestMethod.GET)
    public ModelAndView initSearchLabForm(HttpServletRequest request) throws Exception {
        logger.debug("buscar ordenes para recepcion");
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
            List<TipoMx> tipoMxList = catalogosService.getTipoMuestra();
            mav.addObject("entidades",entidadesAdtvases);
            mav.addObject("tipoMuestra", tipoMxList);
            mav.setViewName("recepcionMx/searchOrdersLab");
        }else
            mav.setViewName(urlValidacion);

        return mav;
    }

    @RequestMapping(value = "create/{strIdOrden}", method = RequestMethod.GET)
    public ModelAndView createReceiptForm(HttpServletRequest request, @PathVariable("strIdOrden")  String strIdOrden) throws Exception {
        logger.debug("buscar ordenes para recepcion");
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
            DaTomaMx ordenExamen = tomaMxService.getTomaMxById(strIdOrden);
            List<EntidadesAdtvas> entidadesAdtvases =  entidadAdmonService.getAllEntidadesAdtvas();
            List<TipoMx> tipoMxList = catalogosService.getTipoMuestra();
            List<Laboratorio> laboratorioList = laboratoriosService.getLaboratoriosInternos();
            //List<CalidadMx> calidadMx= catalogosService.getCalidadesMx();
            //List<TipoTubo> tipoTubos = catalogosService.getTipoTubos();
            List<Unidades> unidades = unidadesService.getPrimaryUnitsBySilais(ordenExamen.getIdNotificacion().getCodSilaisAtencion().getCodigo(), HealthUnitType.UnidadesPrimHosp.getDiscriminator().split(","));
            Date fechaInicioSintomas = ordenExamenMxService.getFechaInicioSintomas(ordenExamen.getIdNotificacion().getIdNotificacion());
            mav.addObject("ordenExamen",ordenExamen);
            mav.addObject("entidades",entidadesAdtvases);
            mav.addObject("unidades",unidades);
            mav.addObject("tipoMuestra", tipoMxList);
            mav.addObject("laboratorios",laboratorioList);
            mav.addObject("fechaInicioSintomas",fechaInicioSintomas);
            mav.setViewName("recepcionMx/recepcionarOrders");
        }else
            mav.setViewName(urlValidacion);

        return mav;
    }

    @RequestMapping(value = "createLab/{strIdRecepcion}", method = RequestMethod.GET)
    public ModelAndView createReceiptLabForm(HttpServletRequest request, @PathVariable("strIdRecepcion")  String strIdRecepcion) throws Exception {
        logger.debug("buscar ordenes para recepcion");
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
            RecepcionMx recepcionMx = recepcionMxService.getRecepcionMx(strIdRecepcion);
            List<EntidadesAdtvas> entidadesAdtvases =  entidadAdmonService.getAllEntidadesAdtvas();
            List<TipoMx> tipoMxList = catalogosService.getTipoMuestra();
            List<Laboratorio> laboratorioList = laboratoriosService.getLaboratoriosInternos();
            List<CalidadMx> calidadMx= catalogosService.getCalidadesMx();
            //List<TipoTubo> tipoTubos = catalogosService.getTipoTubos();
            List<Unidades> unidades = unidadesService.getPrimaryUnitsBySilais(recepcionMx.getTomaMx().getIdNotificacion().getCodSilaisAtencion().getCodigo(), HealthUnitType.UnidadesPrimHosp.getDiscriminator().split(","));
            Date fechaInicioSintomas = ordenExamenMxService.getFechaInicioSintomas(recepcionMx.getTomaMx().getIdNotificacion().getIdNotificacion());
            mav.addObject("recepcionMx",recepcionMx);
            mav.addObject("entidades",entidadesAdtvases);
            mav.addObject("unidades",unidades);
            mav.addObject("tipoMuestra", tipoMxList);
            mav.addObject("laboratorios",laboratorioList);
            mav.addObject("calidadMx",calidadMx);
            //mav.addObject("tipoTubo",tipoTubos);
            mav.addObject("fechaInicioSintomas",fechaInicioSintomas);
            mav.setViewName("recepcionMx/recepcionarOrdersLab");
        }else
            mav.setViewName(urlValidacion);

        return mav;
    }

    @RequestMapping(value = "searchOrders", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String fetchOrdersJson(@RequestParam(value = "strFilter", required = true) String filtro) throws Exception{
        logger.info("Obteniendo las ordenes de examen pendienetes según filtros en JSON");
        FiltroMx filtroMx = jsonToFiltroOrdenExamen(filtro);
        List<DaTomaMx> tomaMxList = ordenExamenMxService.getTomaMxByFiltro(filtroMx);
        return tomaMxToJson(tomaMxList);
    }

    @RequestMapping(value = "searchOrdersLab", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String fetchOrdersLabJson(@RequestParam(value = "strFilter", required = true) String filtro) throws Exception{
        logger.info("Obteniendo las ordenes de examen pendienetes según filtros en JSON");
        FiltroMx filtroMx = jsonToFiltroOrdenExamen(filtro);
        List<RecepcionMx> recepcionMxList = recepcionMxService.getRecepcionesByFiltro(filtroMx);
        return RecepcionMxToJson(recepcionMxList);
    }

    @RequestMapping(value = "agregarRecepcion", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void agregarRecepcion(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json = "";
        String resultado = "";
        String strOrdenes="";
        String idRecepcion = "";
        String verificaCantTb = "";
        String verificaTipoMx = "";
        String idOrdenExamen = "";
        String codigoUnicoMx = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            verificaCantTb = jsonpObject.get("verificaCantTb").getAsString();
            verificaTipoMx = jsonpObject.get("verificaTipoMx").getAsString();
            idOrdenExamen = jsonpObject.get("idOrdenExamen").getAsString();

            long idUsuario = seguridadService.obtenerIdUsuario(request);
            Usuarios usuario = usuarioService.getUsuarioById((int)idUsuario);
            //Se obtiene estado recepcionado
            EstadoMx estadoMx = catalogosService.getEstadoMx("ESTDMX|RCP");
            TipoRecepcionMx tipoRecepcionMx = catalogosService.getTipoRecepcionMx("TPRECPMX|VRT");
            //se obtiene tomaMx de examen a recepcionar
            DaTomaMx tomaMx = tomaMxService.getTomaMxById(idOrdenExamen);

            RecepcionMx recepcionMx = new RecepcionMx();

            recepcionMx.setUsuarioRecepcion(usuario);
            recepcionMx.setFechaHoraRecepcion(new Timestamp(new Date().getTime()));
            recepcionMx.setTipoMxCk(Boolean.valueOf(verificaTipoMx));
            recepcionMx.setCantidadTubosCk(Boolean.valueOf(verificaCantTb));
            recepcionMx.setTipoRecepcionMx(tipoRecepcionMx);
            recepcionMx.setTomaMx(tomaMx);
            //recepcionMx.setOrdenExamen(ordenExamen);
            //recepcionMx.setLaboratorioEnvio(labProcedencia);
            //Se genara código único de muestra (sólo para vigilancia) y se valida que aún no exista en BD
            //codigoUnicoMx = generarCodigoUnicoMx();
            //recepcionMx.setCodigoUnicoMx(codigoUnicoMx);
            try {
                idRecepcion = recepcionMxService.addRecepcionMx(recepcionMx);
            }catch (Exception ex){
                resultado = messageSource.getMessage("msg.add.receipt.error",null,null);
                resultado=resultado+". \n "+ex.getMessage();
                ex.printStackTrace();
            }
            if (!idRecepcion.isEmpty()) {
               //se tiene que actualizar la tomaMx de examen
                tomaMx.setEstadoMx(estadoMx);
                try {
                    tomaMxService.updateTomaMx(tomaMx);
                }catch (Exception ex){
                    resultado = messageSource.getMessage("msg.update.order.error",null,null);
                    resultado=resultado+". \n "+ex.getMessage();
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.receipt.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("idRecepcion",idRecepcion);
            map.put("mensaje",resultado);
            map.put("idOrdenExamen", strOrdenes);
            map.put("verificaCantTb", verificaCantTb);
            map.put("verificaTipoMx", verificaTipoMx);
            //map.put("codigoUnicoMx",codigoUnicoMx);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = "receiptLaboratory", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void recepcionLaboratorio(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json = "";
        String resultado = "";
        String strOrdenes="";
        String idRecepcion = "";
        String causaRechazo = null;
        String codCalidadMx = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            idRecepcion = jsonpObject.get("idRecepcion").getAsString();
            codCalidadMx = jsonpObject.get("calidadMx").getAsString();

            if (jsonpObject.get("causaRechazo")!=null && !jsonpObject.get("causaRechazo").getAsString().isEmpty())
                causaRechazo = jsonpObject.get("causaRechazo").getAsString();

            long idUsuario = seguridadService.obtenerIdUsuario(request);
            Usuarios usuario = usuarioService.getUsuarioById((int) idUsuario);
            //Se obtiene estado recepcionado en laboratorio
            EstadoOrdenEx estadoOrdenEx = catalogosService.getEstadoOrdenEx("ESTORDEN|RCLAB");
            //se obtiene calidad de la muestra
            CalidadMx calidadMx = catalogosService.getCalidadMx(codCalidadMx);
            //se obtiene recepción a actualizar
            RecepcionMx recepcionMx = recepcionMxService.getRecepcionMx(idRecepcion);
            //se setean valores a actualizar
            recepcionMx.setUsuarioRecepcionLab(usuario);
            recepcionMx.setFechaHoraRecepcionLab(new Timestamp(new Date().getTime()));
            recepcionMx.setCalidadMx(calidadMx);
            recepcionMx.setCausaRechazo(causaRechazo);

            try {
                recepcionMxService.updateRecepcionMx(recepcionMx);
            }catch (Exception ex){
                resultado = messageSource.getMessage("msg.add.receipt.error",null,null);
                resultado=resultado+". \n "+ex.getMessage();
                ex.printStackTrace();
            }
            if (!idRecepcion.isEmpty()) {
                //se tiene que actualizar la tomaMx de examen
                /*DaOrdenExamen ordenExamen = recepcionMx.getOrdenExamen();
                ordenExamen.setCodEstado(estadoOrdenEx);
                try {
                    ordenExamenMxService.updateOrdenExamen(ordenExamen);
                }catch (Exception ex){
                    resultado = messageSource.getMessage("msg.update.order.error",null,null);
                    resultado=resultado+". \n "+ex.getMessage();
                    ex.printStackTrace();
                }*/
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.receipt.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("idRecepcion",idRecepcion);
            map.put("mensaje",resultado);
            map.put("calidadMx", codCalidadMx);
            map.put("causaRechazo", causaRechazo);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    private String tomaMxToJson(List<DaTomaMx> tomaMxList){
        String jsonResponse="";
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice=0;
        for(DaTomaMx tomaMx : tomaMxList){
            Map<String, String> map = new HashMap<String, String>();
            //map.put("idOrdenExamen",tomaMx.getIdOrdenExamen());
            map.put("idTomaMx", tomaMx.getIdTomaMx());
            //map.put("fechaHoraOrden",DateUtil.DateToString(tomaMx.getFechaHOrden(), "dd/MM/yyyy hh:mm:ss a"));
            map.put("fechaTomaMx",DateUtil.DateToString(tomaMx.getFechaHTomaMx(), "dd/MM/yyyy hh:mm:ss a"));
            map.put("codSilais", tomaMx.getIdNotificacion().getCodSilaisAtencion().getNombre());
            map.put("codUnidadSalud", tomaMx.getIdNotificacion().getCodUnidadAtencion().getNombre());
            //map.put("estadoOrden", tomaMx.getCodEstado().getValor());
            map.put("separadaMx",(tomaMx.getMxSeparada()!=null?(tomaMx.getMxSeparada()?"Si":"No"):""));
            map.put("cantidadTubos", (tomaMx.getCanTubos()!=null?String.valueOf(tomaMx.getCanTubos()):""));
            map.put("tipoMuestra", tomaMx.getCodTipoMx().getNombre());
            //map.put("tipoExamen", tomaMx.getCodExamen().getNombre());
            //Si hay fecha de inicio de sintomas se muestra
            Date fechaInicioSintomas = ordenExamenMxService.getFechaInicioSintomas(tomaMx.getIdNotificacion().getIdNotificacion());
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

            mapResponse.put(indice, map);
            indice ++;
        }
        jsonResponse = new Gson().toJson(mapResponse);
        return jsonResponse;
    }

    private String RecepcionMxToJson(List<RecepcionMx> recepcionMxList){
        String jsonResponse="";
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice=0;
        for(RecepcionMx recepcion : recepcionMxList){
            Map<String, String> map = new HashMap<String, String>();
            map.put("idRecepcion", recepcion.getIdRecepcion());
            //map.put("idOrdenExamen", recepcion.getOrdenExamen().getIdOrdenExamen());
            map.put("idTomaMx", recepcion.getTomaMx().getIdTomaMx());
            //map.put("fechaHoraOrden",DateUtil.DateToString(recepcion.getOrdenExamen().getFechaHOrden(), "dd/MM/yyyy hh:mm:ss a"));
            map.put("fechaTomaMx",DateUtil.DateToString(recepcion.getTomaMx().getFechaHTomaMx(),"dd/MM/yyyy hh:mm:ss a"));
            map.put("fechaRecepcion",DateUtil.DateToString(recepcion.getFechaHoraRecepcion(),"dd/MM/yyyy hh:mm:ss a"));
            map.put("codSilais", recepcion.getTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre());
            map.put("codUnidadSalud", recepcion.getTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre());
            //map.put("estadoOrden", recepcion.getOrdenExamen().getCodEstado().getValor());
            map.put("separadaMx",(recepcion.getTomaMx().getMxSeparada()!=null?(recepcion.getTomaMx().getMxSeparada()?"Si":"No"):""));
            map.put("cantidadTubos", (recepcion.getTomaMx().getCanTubos()!=null?String.valueOf(recepcion.getTomaMx().getCanTubos()):""));
            map.put("tipoMuestra", recepcion.getTomaMx().getCodTipoMx().getNombre());
            //map.put("tipoExamen", recepcion.getOrdenExamen().getCodExamen().getNombre());
            //map.put("areaProcesa", recepcion.getOrdenExamen().getCodExamen().getArea().getNombre());
            //Si hay fecha de inicio de sintomas se muestra
            Date fechaInicioSintomas = ordenExamenMxService.getFechaInicioSintomas(recepcion.getTomaMx().getIdNotificacion().getIdNotificacion());
            if (fechaInicioSintomas!=null)
                map.put("fechaInicioSintomas",DateUtil.DateToString(fechaInicioSintomas,"dd/MM/yyyy"));
            else
                map.put("fechaInicioSintomas"," ");
            //Si hay persona
            if (recepcion.getTomaMx().getIdNotificacion().getPersona()!=null){
                /// se obtiene el nombre de la persona asociada a la ficha
                String nombreCompleto = "";
                nombreCompleto = recepcion.getTomaMx().getIdNotificacion().getPersona().getPrimerNombre();
                if (recepcion.getTomaMx().getIdNotificacion().getPersona().getSegundoNombre()!=null)
                    nombreCompleto = nombreCompleto +" "+ recepcion.getTomaMx().getIdNotificacion().getPersona().getSegundoNombre();
                nombreCompleto = nombreCompleto+" "+ recepcion.getTomaMx().getIdNotificacion().getPersona().getPrimerApellido();
                if (recepcion.getTomaMx().getIdNotificacion().getPersona().getSegundoApellido()!=null)
                    nombreCompleto = nombreCompleto +" "+ recepcion.getTomaMx().getIdNotificacion().getPersona().getSegundoApellido();
                map.put("persona",nombreCompleto);
            }else{
                map.put("persona"," ");
            }

            mapResponse.put(indice, map);
            indice ++;
        }
        jsonResponse = new Gson().toJson(mapResponse);
        return jsonResponse;
    }

    private FiltroMx jsonToFiltroOrdenExamen(String strJson) throws Exception {
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
        String esLab = null;

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
        if (jObjectFiltro.get("esLab") !=null && !jObjectFiltro.get("esLab").getAsString().isEmpty())
            esLab = jObjectFiltro.get("esLab").getAsString();

        filtroMx.setCodSilais(codSilais);
        filtroMx.setCodUnidadSalud(codUnidadSalud);
        filtroMx.setFechaInicioTomaMx(fechaInicioTomaMx);
        filtroMx.setFechaFinTomaMx(fechaFinTomaMx);
        filtroMx.setFechaInicioRecep(fechaInicioRecep);
        filtroMx.setFechaFinRecep(fechaFinRecep);
        filtroMx.setNombreApellido(nombreApellido);
        filtroMx.setCodTipoMx(codTipoMx);
        if (!Boolean.valueOf(esLab)) //es recepción general
            filtroMx.setCodEstado("ESTDMX|ENV"); // sólo las enviadas
        else //es recepción en laboratorio
            filtroMx.setCodEstado("ESTDMX|EPLAB"); // sólo las enviadas para procesar en laboratorio

        return filtroMx;
    }

    /**
     * Método para generar un string alfanumérico de 8 caracteres, que se usará como código único de muestra
     * @return String codigoUnicoMx
     */
    private String generarCodigoUnicoMx(){
        RecepcionMx validaRecepcionMx;
        //Se genera el código
        String codigoUnicoMx = StringUtil.getCadenaAlfanumAleatoria(8);
        //Se consulta BD para ver si existe recepción con muestra que tenga mismo código
        validaRecepcionMx = recepcionMxService.getRecepcionMxByCodUnicoMx(codigoUnicoMx);
        //si existe, de manera recursiva se solicita un nuevo código
        if (validaRecepcionMx!=null){
            codigoUnicoMx = generarCodigoUnicoMx();
        }
        //si no existe se retorna el último código generado
        return codigoUnicoMx;
    }

}
