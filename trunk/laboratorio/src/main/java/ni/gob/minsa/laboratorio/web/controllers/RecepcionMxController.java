package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.estructura.EntidadesAdtvas;
import ni.gob.minsa.laboratorio.domain.estructura.Unidades;
import ni.gob.minsa.laboratorio.domain.muestra.*;
import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
import ni.gob.minsa.laboratorio.service.*;
import ni.gob.minsa.laboratorio.utilities.ConstantsSecurity;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
            DaOrdenExamen ordenExamen = tomaMxService.getOrdenExamenById(strIdOrden);
            List<EntidadesAdtvas> entidadesAdtvases =  entidadAdmonService.getAllEntidadesAdtvas();
            List<TipoMx> tipoMxList = catalogosService.getTipoMuestra();
            List<Laboratorio> laboratorioList = laboratoriosService.getLaboratoriosInternos();
            List<CalidadMx> calidadMx= catalogosService.getCalidadesMx();
            List<TipoTubo> tipoTubos = catalogosService.getTipoTubos();
            List<Unidades> unidades = unidadesService.getPrimaryUnitsBySilais(ordenExamen.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion().getCodigo(), HealthUnitType.UnidadesPrimHosp.getDiscriminator().split(","));
            Date fechaInicioSintomas = ordenExamenMxService.getFechaInicioSintomas(ordenExamen.getIdTomaMx().getIdNotificacion().getIdNotificacion());
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
            List<Unidades> unidades = unidadesService.getPrimaryUnitsBySilais(recepcionMx.getOrdenExamen().getIdTomaMx().getIdNotificacion().getCodSilaisAtencion().getCodigo(), HealthUnitType.UnidadesPrimHosp.getDiscriminator().split(","));
            Date fechaInicioSintomas = ordenExamenMxService.getFechaInicioSintomas(recepcionMx.getOrdenExamen().getIdTomaMx().getIdNotificacion().getIdNotificacion());
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
        FiltroOrdenExamen filtroOrdenExamen= jsonToFiltroOrdenExamen(filtro);
        List<DaOrdenExamen> ordenExamenList = ordenExamenMxService.getOrdenesExamen(filtroOrdenExamen);
        return OrdenesExamenToJson(ordenExamenList);
    }

    @RequestMapping(value = "searchOrdersLab", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String fetchOrdersLabJson(@RequestParam(value = "strFilter", required = true) String filtro) throws Exception{
        logger.info("Obteniendo las ordenes de examen pendienetes según filtros en JSON");
        FiltroOrdenExamen filtroOrdenExamen= jsonToFiltroOrdenExamen(filtro);
        List<RecepcionMx> recepcionMxList = recepcionMxService.getRecepcionesByFiltro(filtroOrdenExamen);
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
            EstadoOrdenEx estadoOrdenEx = catalogosService.getEstadoOrdenEx("ESTORDEN|RCP");
            TipoRecepcionMx tipoRecepcionMx = catalogosService.getTipoRecepcionMx("TPRECPMX|VRT");
            //se obtiene orden de examen a recepcionar
            DaOrdenExamen ordenExamen = ordenExamenMxService.getOrdenExamenById(idOrdenExamen);

            RecepcionMx recepcionMx = new RecepcionMx();

            recepcionMx.setUsuarioRecepcion(usuario);
            recepcionMx.setFechaHoraRecepcion(new Timestamp(new Date().getTime()));
            recepcionMx.setTipoMxCk(Boolean.valueOf(verificaTipoMx));
            recepcionMx.setCantidadTubosCk(Boolean.valueOf(verificaCantTb));
            recepcionMx.setTipoRecepcionMx(tipoRecepcionMx);
            recepcionMx.setOrdenExamen(ordenExamen);
            //recepcionMx.setLaboratorioEnvio(labProcedencia);

            try {
                idRecepcion = recepcionMxService.addRecepcionMx(recepcionMx);
            }catch (Exception ex){
                resultado = messageSource.getMessage("msg.add.receipt.error",null,null);
                resultado=resultado+". \n "+ex.getMessage();
                ex.printStackTrace();
            }
            if (!idRecepcion.isEmpty()) {
               //se tiene que actualizar la orden de examen
                ordenExamen.setCodEstado(estadoOrdenEx);
                try {
                    ordenExamenMxService.updateOrdenExamen(ordenExamen);
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
                //se tiene que actualizar la orden de examen
                DaOrdenExamen ordenExamen = recepcionMx.getOrdenExamen();
                ordenExamen.setCodEstado(estadoOrdenEx);
                try {
                    ordenExamenMxService.updateOrdenExamen(ordenExamen);
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
            map.put("calidadMx", codCalidadMx);
            map.put("causaRechazo", causaRechazo);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    private String OrdenesExamenToJson(List<DaOrdenExamen> ordenExamenList){
        String jsonResponse="";
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice=0;
        for(DaOrdenExamen orden:ordenExamenList){
            Map<String, String> map = new HashMap<String, String>();
            map.put("idOrdenExamen",orden.getIdOrdenExamen());
            map.put("idTomaMx",orden.getIdTomaMx().getIdTomaMx());
            map.put("fechaHoraOrden",DateToString(orden.getFechaHOrden(), "dd/MM/yyyy hh:mm:ss a"));
            map.put("fechaTomaMx",DateToString(orden.getIdTomaMx().getFechaHTomaMx(),"dd/MM/yyyy hh:mm:ss a"));
            map.put("codSilais",orden.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre());
            map.put("codUnidadSalud",orden.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre());
            map.put("estadoOrden",orden.getCodEstado().getValor());
            map.put("separadaMx",(orden.getIdTomaMx().getMxSeparada()!=null?(orden.getIdTomaMx().getMxSeparada()?"Si":"No"):""));
            map.put("cantidadTubos", (orden.getIdTomaMx().getCanTubos()!=null?String.valueOf(orden.getIdTomaMx().getCanTubos()):""));
            map.put("tipoMuestra",orden.getIdTomaMx().getCodTipoMx().getNombre());
            map.put("tipoExamen",orden.getCodExamen().getNombre());
            //Si hay fecha de inicio de sintomas se muestra
            Date fechaInicioSintomas = ordenExamenMxService.getFechaInicioSintomas(orden.getIdTomaMx().getIdNotificacion().getIdNotificacion());
            if (fechaInicioSintomas!=null)
                map.put("fechaInicioSintomas",DateToString(fechaInicioSintomas,"dd/MM/yyyy"));
            else
                map.put("fechaInicioSintomas"," ");
            //Si hay persona
            if (orden.getIdTomaMx().getIdNotificacion().getPersona()!=null){
                /// se obtiene el nombre de la persona asociada a la ficha
                String nombreCompleto = "";
                nombreCompleto = orden.getIdTomaMx().getIdNotificacion().getPersona().getPrimerNombre();
                if (orden.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre()!=null)
                    nombreCompleto = nombreCompleto +" "+ orden.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre();
                nombreCompleto = nombreCompleto+" "+orden.getIdTomaMx().getIdNotificacion().getPersona().getPrimerApellido();
                if (orden.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido()!=null)
                    nombreCompleto = nombreCompleto +" "+ orden.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido();
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
            map.put("idOrdenExamen", recepcion.getOrdenExamen().getIdOrdenExamen());
            map.put("idTomaMx", recepcion.getOrdenExamen().getIdTomaMx().getIdTomaMx());
            map.put("fechaHoraOrden",DateToString(recepcion.getOrdenExamen().getFechaHOrden(), "dd/MM/yyyy hh:mm:ss a"));
            map.put("fechaTomaMx",DateToString(recepcion.getOrdenExamen().getIdTomaMx().getFechaHTomaMx(),"dd/MM/yyyy hh:mm:ss a"));
            map.put("fechaRecepcion",DateToString(recepcion.getFechaHoraRecepcion(),"dd/MM/yyyy hh:mm:ss a"));
            map.put("codSilais", recepcion.getOrdenExamen().getIdTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre());
            map.put("codUnidadSalud", recepcion.getOrdenExamen().getIdTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre());
            map.put("estadoOrden", recepcion.getOrdenExamen().getCodEstado().getValor());
            map.put("separadaMx",(recepcion.getOrdenExamen().getIdTomaMx().getMxSeparada()!=null?(recepcion.getOrdenExamen().getIdTomaMx().getMxSeparada()?"Si":"No"):""));
            map.put("cantidadTubos", (recepcion.getOrdenExamen().getIdTomaMx().getCanTubos()!=null?String.valueOf(recepcion.getOrdenExamen().getIdTomaMx().getCanTubos()):""));
            map.put("tipoMuestra", recepcion.getOrdenExamen().getIdTomaMx().getCodTipoMx().getNombre());
            map.put("tipoExamen", recepcion.getOrdenExamen().getCodExamen().getNombre());
            map.put("areaProcesa", recepcion.getOrdenExamen().getCodExamen().getArea().getNombre());
            //Si hay fecha de inicio de sintomas se muestra
            Date fechaInicioSintomas = ordenExamenMxService.getFechaInicioSintomas(recepcion.getOrdenExamen().getIdTomaMx().getIdNotificacion().getIdNotificacion());
            if (fechaInicioSintomas!=null)
                map.put("fechaInicioSintomas",DateToString(fechaInicioSintomas,"dd/MM/yyyy"));
            else
                map.put("fechaInicioSintomas"," ");
            //Si hay persona
            if (recepcion.getOrdenExamen().getIdTomaMx().getIdNotificacion().getPersona()!=null){
                /// se obtiene el nombre de la persona asociada a la ficha
                String nombreCompleto = "";
                nombreCompleto = recepcion.getOrdenExamen().getIdTomaMx().getIdNotificacion().getPersona().getPrimerNombre();
                if (recepcion.getOrdenExamen().getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre()!=null)
                    nombreCompleto = nombreCompleto +" "+ recepcion.getOrdenExamen().getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre();
                nombreCompleto = nombreCompleto+" "+ recepcion.getOrdenExamen().getIdTomaMx().getIdNotificacion().getPersona().getPrimerApellido();
                if (recepcion.getOrdenExamen().getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido()!=null)
                    nombreCompleto = nombreCompleto +" "+ recepcion.getOrdenExamen().getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido();
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

    private FiltroOrdenExamen jsonToFiltroOrdenExamen(String strJson) throws Exception {
        JsonObject jObjectFiltro = new Gson().fromJson(strJson, JsonObject.class);
        FiltroOrdenExamen filtroOrdenExamen = new FiltroOrdenExamen();
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
            fechaInicioTomaMx = StringToDate(jObjectFiltro.get("fechaInicioTomaMx").getAsString()+" 00:00:00");
        if (jObjectFiltro.get("fechaFinTomaMx") != null && !jObjectFiltro.get("fechaFinTomaMx").getAsString().isEmpty())
            fechaFinTomaMx = StringToDate(jObjectFiltro.get("fechaFinTomaMx").getAsString()+" 23:59:59");
        if (jObjectFiltro.get("fechaInicioRecep") != null && !jObjectFiltro.get("fechaInicioRecep").getAsString().isEmpty())
            fechaInicioRecep = StringToDate(jObjectFiltro.get("fechaInicioRecep").getAsString()+" 00:00:00");
        if (jObjectFiltro.get("fechaFinRecepcion") != null && !jObjectFiltro.get("fechaFinRecepcion").getAsString().isEmpty())
            fechaFinRecep = StringToDate(jObjectFiltro.get("fechaFinRecepcion").getAsString()+" 23:59:59");
        if (jObjectFiltro.get("codSilais") != null && !jObjectFiltro.get("codSilais").getAsString().isEmpty())
            codSilais = jObjectFiltro.get("codSilais").getAsString();
        if (jObjectFiltro.get("codUnidadSalud") != null && !jObjectFiltro.get("codUnidadSalud").getAsString().isEmpty())
            codUnidadSalud = jObjectFiltro.get("codUnidadSalud").getAsString();
        if (jObjectFiltro.get("codTipoMx") != null && !jObjectFiltro.get("codTipoMx").getAsString().isEmpty())
            codTipoMx = jObjectFiltro.get("codTipoMx").getAsString();
        if (jObjectFiltro.get("esLab") !=null && !jObjectFiltro.get("esLab").getAsString().isEmpty())
            esLab = jObjectFiltro.get("esLab").getAsString();

        filtroOrdenExamen.setCodSilais(codSilais);
        filtroOrdenExamen.setCodUnidadSalud(codUnidadSalud);
        filtroOrdenExamen.setFechaInicioTomaMx(fechaInicioTomaMx);
        filtroOrdenExamen.setFechaFinTomaMx(fechaFinTomaMx);
        filtroOrdenExamen.setFechaInicioRecep(fechaInicioRecep);
        filtroOrdenExamen.setFechaFinRecep(fechaFinRecep);
        filtroOrdenExamen.setNombreApellido(nombreApellido);
        filtroOrdenExamen.setCodTipoMx(codTipoMx);
        if (!Boolean.valueOf(esLab)) //es recepción general
            filtroOrdenExamen.setCodEstado("ESTORDEN|ENV"); // sólo las enviadas
        else //es recepción en laboratorio
            filtroOrdenExamen.setCodEstado("ESTORDEN|EPLAB"); // sólo las enviadas para procesar en laboratorio

        return filtroOrdenExamen;
    }

    //region ****** UTILITARIOS *******

    /**
     * Convierte un string a Date con formato dd/MM/yyyy
     * @param strFecha cadena a convertir
     * @return Fecha
     * @throws java.text.ParseException
     */
    private Date StringToDate(String strFecha) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return simpleDateFormat.parse(strFecha);
    }

    private String DateToString(Date dtFecha, String format)  {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        if(dtFecha!=null)
            return simpleDateFormat.format(dtFecha);
        else
            return null;
    }
    //endregion
}
