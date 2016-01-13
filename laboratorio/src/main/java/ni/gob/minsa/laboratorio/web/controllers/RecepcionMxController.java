package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.estructura.EntidadesAdtvas;
import ni.gob.minsa.laboratorio.domain.estructura.Unidades;
import ni.gob.minsa.laboratorio.domain.examen.Area;
import ni.gob.minsa.laboratorio.domain.examen.CatalogoExamenes;
import ni.gob.minsa.laboratorio.domain.examen.Examen_Dx;
import ni.gob.minsa.laboratorio.domain.muestra.*;
import ni.gob.minsa.laboratorio.domain.muestra.traslado.TrasladoMx;
import ni.gob.minsa.laboratorio.domain.parametros.Parametro;
import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
import ni.gob.minsa.laboratorio.domain.resultados.DetalleResultadoFinal;
import ni.gob.minsa.laboratorio.domain.resultados.RespuestaSolicitud;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.User;
import ni.gob.minsa.laboratorio.service.*;
import ni.gob.minsa.laboratorio.utilities.ConstantsSecurity;
import ni.gob.minsa.laboratorio.utilities.DateUtil;
import ni.gob.minsa.laboratorio.utilities.StringUtil;
import ni.gob.minsa.laboratorio.utilities.enumeration.HealthUnitType;
import org.apache.commons.lang3.text.translate.UnicodeEscaper;
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
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by Miguel Salinas on 12/10/2014.
 * V 1.0
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
    @Qualifier(value = "tomaMxService")
    private TomaMxService tomaMxService;

    @Autowired
    @Qualifier(value = "unidadesService")
    private UnidadesService unidadesService;

    @Autowired
    @Qualifier(value = "parametrosService")
    private ParametrosService parametrosService;

    @Autowired
    @Qualifier(value = "examenesService")
    private ExamenesService examenesService;

    @Autowired
    @Qualifier(value = "ordenExamenMxService")
    private OrdenExamenMxService ordenExamenMxService;

    @Autowired
    @Qualifier(value = "trasladosService")
    private TrasladosService trasladosService;

    @Autowired
    @Qualifier(value = "respuestasSolicitudService")
    private RespuestasSolicitudService respuestasSolicitudService;

    @Autowired
    @Qualifier(value = "resultadoFinalService")
    private ResultadoFinalService resultadoFinalService;

    @Resource(name = "datosSolicitudService")
    private DatosSolicitudService datosSolicitudService;

    @Autowired
    MessageSource messageSource;

    /**
     * Método que se llama al entrar a la opción de menu "Recepción Mx Vigilancia". Se encarga de inicializar las listas para realizar la búsqueda de envios de Mx
     * @param request para obtener información de la petición del cliente
     * @return ModelAndView
     * @throws Exception
     */
    @RequestMapping(value = "init", method = RequestMethod.GET)
    public ModelAndView initSearchForm(HttpServletRequest request) throws Exception {
        logger.debug("buscar ordenes para ordenExamen");
        String urlValidacion;
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

    /**
     * Método que se llama al entrar a la opción de menu "Recepción Mx Laboratorio". Se encarga de inicializar las listas para realizar la búsqueda de envios de Mx
     * @param request para obtener información de la petición del cliente
     * @return ModelAndView
     * @throws Exception
     */
    @RequestMapping(value = "initLab", method = RequestMethod.GET)
    public ModelAndView initSearchLabForm(HttpServletRequest request) throws Exception {
        logger.debug("buscar ordenes para ordenExamen");
        String urlValidacion;
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

    /***
     * Método que se llama para crear una Recepción Mx Vigilancia. Setea los datos de la Muestra e inicializa listas y demas controles
     * @param request para obtener información de la petición del cliente
     * @param strIdOrden Id de la toma de Muestra a recepcionar
     * @return ModelAndView
     * @throws Exception
     */
    @RequestMapping(value = "create/{strIdOrden}", method = RequestMethod.GET)
    public ModelAndView createReceiptForm(HttpServletRequest request, @PathVariable("strIdOrden")  String strIdOrden) throws Exception {
        logger.debug("buscar ordenes para ordenExamen");
        String urlValidacion;
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
            DaTomaMx tomaMx = tomaMxService.getTomaMxById(strIdOrden);
            List<EntidadesAdtvas> entidadesAdtvases =  entidadAdmonService.getAllEntidadesAdtvas();
            List<TipoMx> tipoMxList = catalogosService.getTipoMuestra();
            List<Laboratorio> laboratorioList = laboratoriosService.getLaboratoriosInternos();
            List<Unidades> unidades = null;
            Laboratorio labUser = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
            List<DaSolicitudDx> solicitudDxList = tomaMxService.getSolicitudesDxByIdToma(tomaMx.getIdTomaMx(),labUser.getCodigo());
            TrasladoMx trasladoMxActivo = trasladosService.getTrasladoActivoMxRecepcion(tomaMx.getIdTomaMx(),false);
            if (trasladoMxActivo!=null) {
                if (trasladoMxActivo.isTrasladoExterno()){
                    solicitudDxList = tomaMxService.getSolicitudesDxTrasladoExtByIdToma(tomaMx.getIdTomaMx(),labUser.getCodigo());
                }
            }
            List<DaSolicitudEstudio> solicitudEstudioList = tomaMxService.getSolicitudesEstudioByIdTomaMx(tomaMx.getIdTomaMx());
            Date fechaInicioSintomas = null;
            if (tomaMx.getIdNotificacion()!=null) {
                if (tomaMx.getIdNotificacion().getCodSilaisAtencion()!=null) {
                    unidades = unidadesService.getPrimaryUnitsBySilais(tomaMx.getIdNotificacion().getCodSilaisAtencion().getCodigo(), HealthUnitType.UnidadesPrimHosp.getDiscriminator().split(","));
                }
                fechaInicioSintomas = tomaMx.getIdNotificacion().getFechaInicioSintomas();
            }
            String html = "";
            //si hay fecha de inicio de síntomas validar si es muestra válida para vigilancia rutinaria
            if (fechaInicioSintomas!=null){
                Parametro diasMinRecepMx = parametrosService.getParametroByName("DIAS_MIN_MX_VIG_RUT");
                int diffDias = DateUtil.CalcularDiferenciaDiasFechas(fechaInicioSintomas,new Date());
                if (diffDias < Integer.valueOf(diasMinRecepMx.getValor())){
                    html = messageSource.getMessage("msg.mx.must.be.inadequate",null,null).replace("{0}",diasMinRecepMx.getValor()); //"La cantidad de días desde el inicio de síntomas no es mayor o igual a "+diasMinRecepMx.getValor()+", la muestra debería marcarse como inadecuada";
                }
            }

            List<CausaRechazoMx> causaRechazoMxList = catalogosService.getCausaRechazoMxRecepGeneral();

            mav.addObject("tomaMx",tomaMx);
            mav.addObject("entidades",entidadesAdtvases);
            mav.addObject("unidades",unidades);
            mav.addObject("tipoMuestra", tipoMxList);
            mav.addObject("laboratorios",laboratorioList);
            mav.addObject("fechaInicioSintomas",fechaInicioSintomas);
            mav.addObject("inadecuada",html);
            mav.addObject("dxList",solicitudDxList);
            mav.addObject("estudiosList",solicitudEstudioList);
            mav.addObject("causasRechazo",causaRechazoMxList);
            mav.setViewName("recepcionMx/recepcionarOrders");
        }else
            mav.setViewName(urlValidacion);

        return mav;
    }

    /**
     * Método que se llama para crear una Recepción Mx en el Laboratorio. Setea los datos de la recepción e inicializa listas y demas controles.
     * Además si es la primera vez que se carga el registro se registran ordenes de examen para los examenes configurados por defecto en la tabla
     * de parámetros según el tipo de notificación, tipo de mx, tipo dx
     * @param request para obtener información de la petición del cliente
     * @param strIdRecepcion Id de la recepción general a recepcionar en el laboratorio
     * @return ModelAndView
     * @throws Exception
     */
    @RequestMapping(value = "createLab/{strIdRecepcion}", method = RequestMethod.GET)
    public ModelAndView createReceiptLabForm(HttpServletRequest request, @PathVariable("strIdRecepcion")  String strIdRecepcion) throws Exception {
        logger.debug("buscar ordenes para ordenExamen");
        String urlValidacion;
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
            List<CondicionMx> condicionesMx = catalogosService.getCondicionesMx();
            //List<TipoTubo> tipoTubos = catalogosService.getTipoTubos();
            List<Unidades> unidades = null;
            List<Examen_Dx> examenesList = null;
            List<OrdenExamen> ordenExamenList;
            Date fechaInicioSintomas = null;
            boolean esEstudio = false;
            if (recepcionMx!=null) {
                //se determina si es una muestra para estudio o para vigilancia rutinaria(Dx)
                List<DaSolicitudEstudio> solicitudEstudioList = tomaMxService.getSolicitudesEstudioByIdTomaMx(recepcionMx.getTomaMx().getIdTomaMx());
                esEstudio = solicitudEstudioList.size()>0;

                if(recepcionMx.getTomaMx().getIdNotificacion().getCodSilaisAtencion()!=null) {
                    unidades = unidadesService.getPrimaryUnitsBySilais(recepcionMx.getTomaMx().getIdNotificacion().getCodSilaisAtencion().getCodigo(), HealthUnitType.UnidadesPrimHosp.getDiscriminator().split(","));
                }
                fechaInicioSintomas = recepcionMx.getTomaMx().getIdNotificacion().getFechaInicioSintomas();
                //anuladas y activas
                ordenExamenList = ordenExamenMxService.getOrdenesExamenByIdMxAndUser(recepcionMx.getTomaMx().getIdTomaMx(),seguridadService.obtenerNombreUsuario());
                User usuario = seguridadService.getUsuario(seguridadService.obtenerNombreUsuario());
                Laboratorio labUsuario = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
                if (ordenExamenList==null || ordenExamenList.size()<=0) {
                    if (!esEstudio) {
                        //verificar si hay traslado activo, para saber que área es la que proceso
                        Area areaDestino = null;
                        boolean procesar = true;
                        TrasladoMx trasladoMxActivo = trasladosService.getTrasladoActivoMxRecepcion(recepcionMx.getTomaMx().getIdTomaMx(),false);
                        if (trasladoMxActivo!=null) {
                            if (trasladoMxActivo.isTrasladoExterno()) {
                                if (!seguridadService.usuarioAutorizadoLaboratorio(seguridadService.obtenerNombreUsuario(),trasladoMxActivo.getLaboratorioDestino().getCodigo())){
                                    procesar = false;
                                }else{
                                    areaDestino = trasladoMxActivo.getAreaDestino();
                                }
                            }else {
                                if (!seguridadService.usuarioAutorizadoArea(seguridadService.obtenerNombreUsuario(), trasladoMxActivo.getAreaDestino().getIdArea())){
                                    procesar = false;
                                }else{
                                    areaDestino = trasladoMxActivo.getAreaDestino();
                                }
                            }
                        }else {
                            //si no hay traslado, validar si el usuario tiene acceso al dx de mayor prioridad
                            List<DaSolicitudDx> solicitudDxList = tomaMxService.getSolicitudesDxPrioridadByIdToma(recepcionMx.getTomaMx().getIdTomaMx());
                            if (solicitudDxList.size() > 0) {
                                if (!seguridadService.usuarioAutorizadoArea(seguridadService.obtenerNombreUsuario(), solicitudDxList.get(0).getCodDx().getArea().getIdArea())) {
                                    procesar = false;
                                }else{
                                    areaDestino = solicitudDxList.get(0).getCodDx().getArea();
                                }
                            }
                        }
                        if (procesar) {
                            //se obtiene la lista de examenes por defecto para dx según el tipo de notificación configurado en tabla de parámetros. Se pasa como paràmetro el codigo del tipo de notificación
                            Parametro pTipoNoti = parametrosService.getParametroByName(recepcionMx.getTomaMx().getIdNotificacion().getCodTipoNotificacion().getCodigo());
                            if (pTipoNoti != null) {
                                //se obtienen los id de los examenes por defecto
                                Parametro pExamenesDefecto = parametrosService.getParametroByName(pTipoNoti.getValor());
                                if (pExamenesDefecto != null)
                                    examenesList = examenesService.getExamenesDxByIdsExamenes(pExamenesDefecto.getValor());
                                if (examenesList != null) {
                                    //se registran los examenes por defecto
                                    for (Examen_Dx examenTmp : examenesList) {
                                        //si el área actual que debe procesa la mx es la misma area del exámen entonces se registra la orden
                                        if (areaDestino!=null && areaDestino.getIdArea().equals(examenTmp.getExamen().getArea().getIdArea())) {
                                            OrdenExamen ordenExamen = new OrdenExamen();
                                            DaSolicitudDx solicitudDx = tomaMxService.getSolicitudesDxByMxDx(recepcionMx.getTomaMx().getIdTomaMx(), examenTmp.getDiagnostico().getIdDiagnostico());
                                            ordenExamen.setSolicitudDx(solicitudDx);
                                            ordenExamen.setCodExamen(examenTmp.getExamen());
                                            ordenExamen.setFechaHOrden(new Timestamp(new Date().getTime()));
                                            ordenExamen.setUsuarioRegistro(usuario);
                                            ordenExamen.setLabProcesa(labUsuario);
                                            try {
                                                ordenExamenMxService.addOrdenExamen(ordenExamen);
                                            } catch (Exception ex) {
                                                ex.printStackTrace();
                                                logger.error("Error al agregar orden de examen", ex);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }else{
                        if (solicitudEstudioList.size()>0) {
                            //procesar examenes default para cada estudio
                            for (DaSolicitudEstudio solicitudEstudio : solicitudEstudioList) {
                                String nombreParametroExam = solicitudEstudio.getTipoEstudio().getCodigo();
                                //nombre parámetro que contiene los examenes que se deben aplicar para cada estudio puede estar configurado de 3 maneras:
                                //cod_estudio+cod_categ+gravedad
                                //cod_estudio+gravedad
                                //cod_estudio
                                String gravedad = null;
                                String codUnicoMx = solicitudEstudio.getIdTomaMx().getCodigoUnicoMx();
                                if (codUnicoMx.contains("."))
                                    gravedad = codUnicoMx.substring(codUnicoMx.lastIndexOf(".") + 1);

                                if (solicitudEstudio.getIdTomaMx().getCategoriaMx() != null) {
                                    nombreParametroExam += "_" + solicitudEstudio.getIdTomaMx().getCategoriaMx().getCodigo();
                                    if (gravedad != null)
                                        nombreParametroExam += "_" + gravedad;
                                } else {
                                    if (gravedad != null)
                                        nombreParametroExam += "_" + gravedad;
                                }

                                Parametro examenesEstudio = parametrosService.getParametroByName(nombreParametroExam);
                                if (examenesEstudio != null) {
                                    List<CatalogoExamenes> examenesEstList = examenesService.getExamenesByIdsExamenes(examenesEstudio.getValor());
                                    for (CatalogoExamenes examen : examenesEstList) {
                                        OrdenExamen ordenExamen = new OrdenExamen();
                                        ordenExamen.setSolicitudEstudio(solicitudEstudio);
                                        ordenExamen.setCodExamen(examen);
                                        ordenExamen.setFechaHOrden(new Timestamp(new Date().getTime()));
                                        ordenExamen.setUsuarioRegistro(usuario);
                                        ordenExamen.setLabProcesa(labUsuario);
                                        try {
                                            ordenExamenMxService.addOrdenExamen(ordenExamen);
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                            logger.error("Error al agregar orden de examen", ex);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    //List<OrdenExamen> ordenExamenListNew = ordenExamenMxService.getOrdenesExamenNoAnuladasByIdMx(recepcionMx.getTomaMx().getIdTomaMx());
                    //mav.addObject("examenesList",ordenExamenListNew);
                }//else{
                    //mav.addObject("examenesList",ordenExamenList);
                //}
                mav.addObject("esEstudio",esEstudio);
                TrasladoMx trasladoActivo = trasladosService.getTrasladoActivoMx(recepcionMx.getTomaMx().getIdTomaMx());
                List<DaSolicitudDx> solicitudDxList = tomaMxService.getSolicitudesDxByIdTomaAreaLabUser(recepcionMx.getTomaMx().getIdTomaMx(), seguridadService.obtenerNombreUsuario());
                List<DaSolicitudEstudio> solicitudEstudios = tomaMxService.getSolicitudesEstudioByIdMxUser(recepcionMx.getTomaMx().getIdTomaMx(), seguridadService.obtenerNombreUsuario());
                List<DaSolicitudDx> dxMostrar = new ArrayList<DaSolicitudDx>();
                if (trasladoActivo!=null && trasladoActivo.isTrasladoInterno()){
                    for (DaSolicitudDx solicitudDx : solicitudDxList) {
                        if (trasladoActivo.getAreaDestino().getIdArea().equals(solicitudDx.getCodDx().getArea().getIdArea())){
                            dxMostrar.add(solicitudDx);
                        }
                    }
                }else{
                    dxMostrar = solicitudDxList;
                }
                mav.addObject("dxList",dxMostrar);
                List<DatoSolicitudDetalle> datoSolicitudDetalles = new ArrayList<DatoSolicitudDetalle>();
                for(DaSolicitudDx solicitudDx : dxMostrar){
                    datoSolicitudDetalles.addAll(datosSolicitudService.getDatosSolicitudDetalleBySolicitud(solicitudDx.getIdSolicitudDx()));
                }

                mav.addObject("estudiosList",solicitudEstudios);
                for(DaSolicitudEstudio solicitudEstudio : solicitudEstudios){
                    datoSolicitudDetalles.addAll(datosSolicitudService.getDatosSolicitudDetalleBySolicitud(solicitudEstudio.getIdSolicitudEstudio()));
                }
                mav.addObject("datosList",datoSolicitudDetalles);
            }


            List<CausaRechazoMx> causaRechazoMxList = catalogosService.getCausaRechazoMxRecepLab();

            mav.addObject("recepcionMx",recepcionMx);
            mav.addObject("entidades",entidadesAdtvases);
            mav.addObject("unidades",unidades);
            mav.addObject("tipoMuestra", tipoMxList);
            mav.addObject("laboratorios",laboratorioList);
            mav.addObject("calidadMx",calidadMx);
            mav.addObject("condicionesMx",condicionesMx);
            mav.addObject("causasRechazo",causaRechazoMxList);
            mav.addObject("fechaInicioSintomas",fechaInicioSintomas);
            mav.setViewName("recepcionMx/recepcionarOrdersLab");
        }else
            mav.setViewName(urlValidacion);

        return mav;
    }

    /**
     * Método para realizar la búsqueda de Mx para recepcionar en Mx Vigilancia general
     * @param filtro JSon con los datos de los filtros a aplicar en la búsqueda(Nombre Apellido, Rango Fec Toma Mx, Tipo Mx, SILAIS, unidad salud)
     * @return String con las Mx encontradas
     * @throws Exception
     */
    @RequestMapping(value = "searchOrders", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String fetchOrdersJson(@RequestParam(value = "strFilter", required = true) String filtro) throws Exception{
        logger.info("Obteniendo las ordenes de examen pendienetes según filtros en JSON");
        FiltroMx filtroMx = jsonToFiltroMx(filtro);
        List<DaTomaMx> tomaMxList = tomaMxService.getTomaMxByFiltro(filtroMx);
        return tomaMxToJson(tomaMxList);
    }

    /**
     * Método para realizar la búsqueda de Recepcion Mx para recepcionar en laboratorio
     * @param filtro JSon con los datos de los filtros a aplicar en la búsqueda(Nombre Apellido, Rango Fec Toma Mx, Tipo Mx, SILAIS, unidad salud)
     * @return String con las Recepciones encontradas
     * @throws Exception
     */
    @RequestMapping(value = "searchOrdersLab", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String fetchOrdersLabJson(@RequestParam(value = "strFilter", required = true) String filtro) throws Exception{
        logger.info("Obteniendo las ordenes de examen pendienetes según filtros en JSON");
        FiltroMx filtroMx = jsonToFiltroMx(filtro);
        List<RecepcionMx> recepcionMxList = recepcionMxService.getRecepcionesByFiltro(filtroMx);
        return RecepcionMxToJson(recepcionMxList);
    }

    /**
     * Método para registrar una recepción de muestra de vigilancia. Modifica la Mx al estado ESTDMX|RCP
     * @param request para obtener información de la petición del cliente. Contiene en un parámetro la estructura json del registro a agregar
     * @param response para notificar al cliente del resultado de la operación
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "agregarRecepcion", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void agregarRecepcion(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        String idRecepcion = "";
        String verificaCantTb = "";
        String verificaTipoMx = "";
        String idTomaMx = "";
        String codigoLabMx = "";
        String causaRechazo;
        boolean mxInadecuada = false;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            verificaCantTb = jsonpObject.get("verificaCantTb").getAsString();
            verificaTipoMx = jsonpObject.get("verificaTipoMx").getAsString();
            idTomaMx = jsonpObject.get("idTomaMx").getAsString();
            causaRechazo = jsonpObject.get("causaRechazo").getAsString();

            User usuario = seguridadService.getUsuario(seguridadService.obtenerNombreUsuario());
            Laboratorio labUsuario = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
            //Se obtiene estado recepcionado
            EstadoMx estadoMx = catalogosService.getEstadoMx("ESTDMX|RCP");

            //se obtiene tomaMx de examen a recepcionar
            DaTomaMx tomaMx = tomaMxService.getTomaMxById(idTomaMx);

            TipoRecepcionMx tipoRecepcionMx = null;
            //se determina si es una muestra para estudio o para vigilancia rutinaria(Dx)
            boolean esEstudio = tomaMxService.getSolicitudesEstudioByIdTomaMx(tomaMx.getIdTomaMx()).size()>0;
            tipoRecepcionMx = catalogosService.getTipoRecepcionMx((!esEstudio?"TPRECPMX|VRT":"TPRECPMX|EST"));
            RecepcionMx recepcionMx = new RecepcionMx();

            recepcionMx.setUsuarioRecepcion(usuario);
            recepcionMx.setLabRecepcion(labUsuario);
            recepcionMx.setFechaHoraRecepcion(new Timestamp(new Date().getTime()));
            recepcionMx.setTipoMxCk(Boolean.valueOf(verificaTipoMx));
            recepcionMx.setCantidadTubosCk(Boolean.valueOf(verificaCantTb));
            if (!causaRechazo.isEmpty()) {
                CausaRechazoMx causaRechazoMx = catalogosService.getCausaRechazoMx(causaRechazo);
                recepcionMx.setCausaRechazo(causaRechazoMx);
                //se obtiene calidad de la muestra inadecuada
                //CalidadMx calidadMx = catalogosService.getCalidadMx("CALIDMX|IDC");
                CondicionMx condicionMx = catalogosService.getCondicionMx("CONDICIONMX|IDC");
                //recepcionMx.setCalidadMx(calidadMx);
                recepcionMx.setCondicionMx(condicionMx);
                mxInadecuada = true;
            }
            recepcionMx.setTipoRecepcionMx(tipoRecepcionMx);
            recepcionMx.setTomaMx(tomaMx);
            try {
                //se setea consecutivo codigo lab. Formato COD_LAB-CONSECUTIVO-ANIO. Sólo para rutinas, que no vengan por traslado externo
                if (!esEstudio && tomaMx.getCodigoLab()==null)
                    tomaMx.setCodigoLab(recepcionMxService.obtenerCodigoLab(labUsuario.getCodigo()));
                idRecepcion = recepcionMxService.addRecepcionMx(recepcionMx);
                //si tiene traslado activo marcarlo como recepcionado
                TrasladoMx trasladoActivo = trasladosService.getTrasladoActivoMx(idTomaMx);
                if (trasladoActivo!=null) {
                    if (trasladoActivo.isTrasladoExterno() || trasladoActivo.isControlCalidad()){ //control de calidad, por tanto llega a recepción general
                        if (trasladoActivo.getLaboratorioDestino().getCodigo().equals(recepcionMx.getLabRecepcion().getCodigo())){
                            trasladoActivo.setRecepcionado(true);
                            trasladoActivo.setFechaHoraRecepcion(new Timestamp(new Date().getTime()));
                            trasladoActivo.setUsuarioRecepcion(usuario);
                            trasladosService.saveTrasladoMx(trasladoActivo);
                        }
                    }
                }
                //si muestra es inadecuada.. entonces resultado final de solicitudes asociadas a la mx es mx inadecuada
                if (mxInadecuada){
                    long idUsuario = seguridadService.obtenerIdUsuario(request);
                    Usuarios usuApro = usuarioService.getUsuarioById((int) idUsuario);
                    if (!esEstudio) {
                        List<DaSolicitudDx> solicitudDxList = tomaMxService.getSolicitudesDxByIdToma(idTomaMx, labUsuario.getCodigo());
                        for (DaSolicitudDx solicitudDx : solicitudDxList) {
                            RespuestaSolicitud respuestaDefecto = respuestasSolicitudService.getRespuestaDefectoMxInadecuada();
                            DetalleResultadoFinal resultadoFinal = new DetalleResultadoFinal();
                            resultadoFinal.setPasivo(false);
                            resultadoFinal.setFechahRegistro(new Timestamp(new Date().getTime()));
                            resultadoFinal.setUsuarioRegistro(usuario);//ESTO SE DEBE CAMBIAR
                            resultadoFinal.setRespuesta(respuestaDefecto);
                            resultadoFinal.setSolicitudDx(solicitudDx);
                            resultadoFinal.setValor(respuestaDefecto.getNombre());
                            resultadoFinalService.saveDetResFinal(resultadoFinal);

                            solicitudDx.setAprobada(true);
                            solicitudDx.setFechaAprobacion(new Timestamp(new Date().getTime()));
                            solicitudDx.setUsuarioAprobacion(usuApro);
                            tomaMxService.updateSolicitudDx(solicitudDx);
                        }
                    }else{
                        List<DaSolicitudEstudio> solicitudEstudioList = tomaMxService.getSolicitudesEstudioByIdTomaMx(idTomaMx);
                        for (DaSolicitudEstudio solicitudEst : solicitudEstudioList){
                            RespuestaSolicitud respuestaDefecto = respuestasSolicitudService.getRespuestaDefectoMxInadecuada();
                            DetalleResultadoFinal resultadoFinal = new DetalleResultadoFinal();
                            resultadoFinal.setPasivo(false);
                            resultadoFinal.setFechahRegistro(new Timestamp(new Date().getTime()));
                            resultadoFinal.setUsuarioRegistro(usuario);//ESTO SE DEBE CAMBIAR
                            resultadoFinal.setRespuesta(respuestaDefecto);
                            resultadoFinal.setSolicitudEstudio(solicitudEst);
                            resultadoFinal.setValor(respuestaDefecto.getNombre());
                            resultadoFinalService.saveDetResFinal(resultadoFinal);

                            solicitudEst.setAprobada(true);
                            solicitudEst.setFechaAprobacion(new Timestamp(new Date().getTime()));
                            solicitudEst.setUsuarioAprobacion(usuApro);
                            tomaMxService.updateSolicitudEstudio(solicitudEst);
                        }
                    }
                }
            }catch (Exception ex){
                resultado = messageSource.getMessage("msg.add.receipt.error",null,null);
                resultado=resultado+". \n "+ex.getMessage();
                ex.printStackTrace();
            }
            if (!idRecepcion.isEmpty()) {
               //se tiene que actualizar la tomaMx
                tomaMx.setEstadoMx(estadoMx);
                try {
                    tomaMxService.updateTomaMx(tomaMx);
                    codigoLabMx = esEstudio?tomaMx.getCodigoUnicoMx():tomaMx.getCodigoLab();
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
            map.put("idTomaMx", idTomaMx);
            map.put("verificaCantTb", verificaCantTb);
            map.put("verificaTipoMx", verificaTipoMx);
            map.put("codigoUnicoMx",codigoLabMx);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    /**
     * Método para actualizar una recepción de vigilancia indicando que se ha recepcionado en el laboratorio. Modifica la Mx al estado ESTDMX|RCLAB
     * @param request para obtener información de la petición del cliente. Contiene en un parámetro la estructura json del registro a actualizar
     * @param response para notificar al cliente del resultado de la operación
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "receiptLaboratory", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void recepcionLaboratorio(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        String idRecepcion = "";
        String causaRechazo = null;
        String codCalidadMx = "";
        String codCondicionMx = "";
        boolean mxInadecuada = false;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            idRecepcion = jsonpObject.get("idRecepcion").getAsString();
            codCalidadMx = jsonpObject.get("calidadMx").getAsString();
            codCondicionMx = jsonpObject.get("condicionMx").getAsString();

            if (jsonpObject.get("causaRechazo")!=null && !jsonpObject.get("causaRechazo").getAsString().isEmpty())
                causaRechazo = jsonpObject.get("causaRechazo").getAsString();

            User usuario = seguridadService.getUsuario(seguridadService.obtenerNombreUsuario());
            Laboratorio labUsuario = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
            //Se obtiene estado recepcionado en laboratorio
            EstadoMx estadoMx = catalogosService.getEstadoMx("ESTDMX|RCLAB");
            //se obtiene calidad de la muestra
            CalidadMx calidadMx = catalogosService.getCalidadMx(codCalidadMx);
            //se obtiene condición de la muestra
            CondicionMx condicionMx = catalogosService.getCondicionMx(codCondicionMx);
            //se obtiene recepción a actualizar
            RecepcionMx recepcionMx = recepcionMxService.getRecepcionMx(idRecepcion);
            //se determina si es una muestra para estudio o para vigilancia rutinaria(Dx)
            boolean esEstudio = tomaMxService.getSolicitudesEstudioByIdTomaMx(recepcionMx.getTomaMx().getIdTomaMx()).size()>0;
            //se setean valores a actualizar
            //recepcionMx.setUsuarioRecepcionLab(usuario);
            //recepcionMx.setFechaHoraRecepcionLab(new Timestamp(new Date().getTime()));
            recepcionMx.setCalidadMx(calidadMx);
            recepcionMx.setCondicionMx(condicionMx);
            if (causaRechazo!=null) {
                CausaRechazoMx causaRechazoMx = catalogosService.getCausaRechazoMx(causaRechazo);
                recepcionMx.setCausaRechazo(causaRechazoMx);
                mxInadecuada = true;
            }
            RecepcionMxLab recepcionMxLab = new RecepcionMxLab();
            recepcionMxLab.setRecepcionMx(recepcionMx);
            recepcionMxLab.setUsuarioRecepcion(usuario);
            recepcionMxLab.setFechaHoraRecepcion(new Timestamp(new Date().getTime()));
            TrasladoMx trasladoMxActivo = trasladosService.getTrasladoInternoActivoMxRecepcion(recepcionMx.getTomaMx().getIdTomaMx());
            boolean actualizarTraslado = false;
            if (trasladoMxActivo!=null) {
                if (!trasladoMxActivo.isTrasladoExterno()) {
                    if (seguridadService.usuarioAutorizadoArea(seguridadService.obtenerNombreUsuario(), trasladoMxActivo.getAreaDestino().getIdArea())){
                        recepcionMxLab.setArea(trasladoMxActivo.getAreaDestino());
                        //si tiene traslado activo marcarlo como recepcionado
                        trasladoMxActivo.setRecepcionado(true);
                        trasladoMxActivo.setFechaHoraRecepcion(new Timestamp(new Date().getTime()));
                        trasladoMxActivo.setUsuarioRecepcion(usuario);
                        actualizarTraslado = true;
                    }
                }
            }else{
                //si no hay traslado, obtener area de dx con mayor prioridad
                List<DaSolicitudDx> solicitudDxList = tomaMxService.getSolicitudesDxPrioridadByIdToma(recepcionMx.getTomaMx().getIdTomaMx());
                if (solicitudDxList.size() > 0) {
                    recepcionMxLab.setArea(solicitudDxList.get(0).getCodDx().getArea());

                }else{ //es estudio, se toma el area del estudio. Sólo se permite un estudio por muestra
                    List<DaSolicitudEstudio> solicitudEstudioList = tomaMxService.getSolicitudesEstudioByIdTomaMx(recepcionMx.getTomaMx().getIdTomaMx());
                    if (solicitudEstudioList.size()>0){
                        recepcionMxLab.setArea(solicitudEstudioList.get(0).getTipoEstudio().getArea());
                    }
                }
            }

            try {
                recepcionMxService.updateRecepcionMx(recepcionMx);
                recepcionMxService.addRecepcionMxLab(recepcionMxLab);
                if (actualizarTraslado)
                    trasladosService.saveTrasladoMx(trasladoMxActivo);
                //si muestra es inadecuada.. entonces resultado final de solicitudes asociadas a la mx es mx inadecuada
                if (mxInadecuada){
                    long idUsuario = seguridadService.obtenerIdUsuario(request);
                    Usuarios usuApro = usuarioService.getUsuarioById((int) idUsuario);
                    if (!esEstudio) {
                        List<DaSolicitudDx> solicitudDxList = tomaMxService.getSolicitudesDxByIdToma(recepcionMx.getTomaMx().getIdTomaMx(), labUsuario.getCodigo());
                        for (DaSolicitudDx solicitudDx : solicitudDxList) {
                            RespuestaSolicitud respuestaDefecto = respuestasSolicitudService.getRespuestaDefectoMxInadecuada();
                            DetalleResultadoFinal resultadoFinal = new DetalleResultadoFinal();
                            resultadoFinal.setPasivo(false);
                            resultadoFinal.setFechahRegistro(new Timestamp(new Date().getTime()));
                            resultadoFinal.setUsuarioRegistro(usuario);//ESTO SE DEBE CAMBIAR
                            resultadoFinal.setRespuesta(respuestaDefecto);
                            resultadoFinal.setSolicitudDx(solicitudDx);
                            resultadoFinal.setValor(respuestaDefecto.getNombre());
                            resultadoFinalService.saveDetResFinal(resultadoFinal);

                            solicitudDx.setAprobada(true);
                            solicitudDx.setFechaAprobacion(new Timestamp(new Date().getTime()));
                            solicitudDx.setUsuarioAprobacion(usuApro);
                            tomaMxService.updateSolicitudDx(solicitudDx);
                        }
                    }else{
                        List<DaSolicitudEstudio> solicitudEstudioList = tomaMxService.getSolicitudesEstudioByIdTomaMx(recepcionMx.getTomaMx().getIdTomaMx());
                        for (DaSolicitudEstudio solicitudEst : solicitudEstudioList){
                            RespuestaSolicitud respuestaDefecto = respuestasSolicitudService.getRespuestaDefectoMxInadecuada();
                            DetalleResultadoFinal resultadoFinal = new DetalleResultadoFinal();
                            resultadoFinal.setPasivo(false);
                            resultadoFinal.setFechahRegistro(new Timestamp(new Date().getTime()));
                            resultadoFinal.setUsuarioRegistro(usuario);//ESTO SE DEBE CAMBIAR
                            resultadoFinal.setRespuesta(respuestaDefecto);
                            resultadoFinal.setSolicitudEstudio(solicitudEst);
                            resultadoFinal.setValor(respuestaDefecto.getNombre());
                            resultadoFinalService.saveDetResFinal(resultadoFinal);

                            solicitudEst.setAprobada(true);
                            solicitudEst.setFechaAprobacion(new Timestamp(new Date().getTime()));
                            solicitudEst.setUsuarioAprobacion(usuApro);
                            tomaMxService.updateSolicitudEstudio(solicitudEst);
                        }
                    }
                }
            }catch (Exception ex){
                resultado = messageSource.getMessage("msg.add.receipt.error",null,null);
                resultado=resultado+". \n "+ex.getMessage();
                ex.printStackTrace();
            }
            if (!idRecepcion.isEmpty()) {
                //se tiene que actualizar la tomaMx
                DaTomaMx tomaMx = tomaMxService.getTomaMxById(recepcionMx.getTomaMx().getIdTomaMx());
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
            map.put("calidadMx", codCalidadMx);
            map.put("condicionMx", codCondicionMx);
            map.put("causaRechazo", causaRechazo);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    /***
     * Método para recuperar las ordenes de examen registradas para la mx en la recepción.
     * @param idTomaMx id de la toma mx a obtener ordenes
     * @return String con las ordenes en formato Json
     * @throws Exception
     */
    @RequestMapping(value = "getOrdenesExamen", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    String getOrdenesExamen(@RequestParam(value = "idTomaMx", required = true) String idTomaMx) throws Exception {
        logger.info("antes getOrdenesExamen");
        List<OrdenExamen> ordenExamenList = ordenExamenMxService.getOrdenesExamenNoAnuladasByIdMxAndUser(idTomaMx, seguridadService.obtenerNombreUsuario());
        TrasladoMx trasladoMx = trasladosService.getTrasladoActivoMx(idTomaMx);
        logger.info("despues getOrdenesExamen");
        return OrdenesExamenToJson(ordenExamenList, trasladoMx);
    }

    /**
     * Método para anular una orden de examen
     * @param request para obtener información de la petición del cliente. Contiene en un parámetro la estructura json del registro a anular
     * @param response para notificar al cliente del resultado de la operación
     * @throws Exception
     */
    @RequestMapping(value = "anularExamen", method = RequestMethod.POST)
    protected void anularExamen(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.debug("buscar ordenes para ordenExamen");
        String urlValidacion;
        String idOrdenExamen = "";
        String causaAnulacion = "";
        String json="";
        String resultado = "";
        try {

            try {
                urlValidacion = seguridadService.validarLogin(request);
                //si la url esta vacia significa que la validación del login fue exitosa
                if (urlValidacion.isEmpty())
                    urlValidacion = seguridadService.validarAutorizacionUsuario(request, ConstantsSecurity.SYSTEM_CODE, false);
            }catch (Exception e){
                e.printStackTrace();
                urlValidacion = "404";
            }
            if (urlValidacion.isEmpty()) {
                BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
                json = br.readLine();
                JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
                idOrdenExamen = jsonpObject.get("idOrdenExamen").getAsString();
                causaAnulacion = jsonpObject.get("causaAnulacion").getAsString();
                OrdenExamen ordenExamen = ordenExamenMxService.getOrdenExamenById(idOrdenExamen);
                if(ordenExamen!=null){
                    ordenExamen.setAnulado(true);
                    ordenExamen.setUsuarioAnulacion(seguridadService.getUsuario(seguridadService.obtenerNombreUsuario()));
                    ordenExamen.setCausaAnulacion(causaAnulacion);
                    try{
                        ordenExamenMxService.updateOrdenExamen(ordenExamen);
                    }catch (Exception ex){
                        logger.error("Error al anular orden de examen",ex);
                        resultado = messageSource.getMessage("msg.receipt.test.cancel.error2", null, null);
                        resultado = resultado + ". \n " + ex.getMessage();
                   }
                }else{
                    throw new Exception(messageSource.getMessage("msg.receipt.test.order.notfound", null, null));
                }
            }else{
                resultado = messageSource.getMessage("msg.not.have.permission", null, null);
            }

        }catch (Exception ex){
            logger.error("Sucedio un error al anular orden de examen",ex);
            resultado = messageSource.getMessage("msg.receipt.test.cancel.error1", null, null);
            resultado = resultado + ". \n " + ex.getMessage();
        } finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("idOrdenExamen", idOrdenExamen);
            map.put("causaAnulacion",causaAnulacion);
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    /**
     * Método para agregar una orden de examen para una mx
     * @param request para obtener información de la petición del cliente. Contiene en un parámetro la estructura json del registro a agregar
     * @param response para notificar al cliente del resultado de la operación
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "agregarOrdenExamen", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void agregarOrdenExamen(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        boolean esEstudio=false;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            esEstudio = jsonpObject.get("esEstudio").getAsBoolean();
            if(esEstudio)
                resultado = agregarOrdenExamenEstudio(jsonpObject,request);
            else
                resultado = agregarOrdenExamenVigRut(jsonpObject,request);

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.receipt.test.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("idTomaMx","tmp");
            map.put("idDiagnostico", "tmp");
            map.put("idExamen", "tmp");
            map.put("esEstudio",String.valueOf(esEstudio));
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = "recepcionMasivaGral", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void recepcionMasivaGeneral(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String json;
        String resultado = "";
        String strMuestras="";
        String codigosLabMx="";
        Integer cantMuestras = 0;
        Integer cantMxProc = 0;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            strMuestras = jsonpObject.get("strMuestras").toString();
            cantMuestras = jsonpObject.get("cantMuestras").getAsInt();

            User usuario = seguridadService.getUsuario(seguridadService.obtenerNombreUsuario());
            Laboratorio labUsuario = seguridadService.getLaboratorioUsuario(usuario.getUsername());
            //Se obtiene estado recepcionado
            EstadoMx estadoMx = catalogosService.getEstadoMx("ESTDMX|RCP");
            //se obtienen muestras a recepcionar
            JsonObject jObjectRecepciones = new Gson().fromJson(strMuestras, JsonObject.class);
            for(int i = 0; i< cantMuestras;i++) {
                String idRecepcion = "";
                String codigoUnicoMx = "";
                String idTomaMx = jObjectRecepciones.get(String.valueOf(i)).getAsString();
                //se obtiene tomaMx a recepcionar
                DaTomaMx tomaMx = tomaMxService.getTomaMxById(idTomaMx);
                TipoRecepcionMx tipoRecepcionMx;
                //se determina si es una muestra para estudio o para vigilancia rutinaria(Dx)
                boolean esEstudio = tomaMxService.getSolicitudesEstudioByIdTomaMx(tomaMx.getIdTomaMx()).size() > 0;
                tipoRecepcionMx = catalogosService.getTipoRecepcionMx((!esEstudio ? "TPRECPMX|VRT" : "TPRECPMX|EST"));
                RecepcionMx recepcionMx = new RecepcionMx();

                recepcionMx.setUsuarioRecepcion(usuario);
                recepcionMx.setLabRecepcion(labUsuario);
                recepcionMx.setFechaHoraRecepcion(new Timestamp(new Date().getTime()));
                recepcionMx.setTipoMxCk(true);
                recepcionMx.setCantidadTubosCk(true);
                recepcionMx.setTipoRecepcionMx(tipoRecepcionMx);
                recepcionMx.setTomaMx(tomaMx);
                try {
                    //se setea consecutivo codigo lab. Formato COD_LAB-CONSECUTIVO-ANIO. Sólo para rutinas
                    if (!esEstudio)
                        tomaMx.setCodigoLab(recepcionMxService.obtenerCodigoLab(labUsuario.getCodigo()));
                    idRecepcion = recepcionMxService.addRecepcionMx(recepcionMx);
                    //si tiene traslado activo marcarlo como recepcionado
                    TrasladoMx trasladoActivo = trasladosService.getTrasladoActivoMx(idTomaMx);
                    if (trasladoActivo!=null) {
                        if (trasladoActivo.isTrasladoExterno()){ //control de calidad, por tanto llega a recepción general
                            if (trasladoActivo.getLaboratorioDestino().getCodigo().equals(recepcionMx.getLabRecepcion().getCodigo())){
                                trasladoActivo.setRecepcionado(true);
                                trasladoActivo.setFechaHoraRecepcion(new Timestamp(new Date().getTime()));
                                trasladoActivo.setUsuarioRecepcion(usuario);
                                trasladosService.saveTrasladoMx(trasladoActivo);
                            }
                        }
                    }
                } catch (Exception ex) {
                    resultado = messageSource.getMessage("msg.add.receipt.error", null, null);
                    resultado = resultado + ". \n " + ex.getMessage();
                    ex.printStackTrace();
                }
                if (!idRecepcion.isEmpty()) {
                    //se tiene que actualizar la tomaMx
                    tomaMx.setEstadoMx(estadoMx);
                    try {
                        tomaMxService.updateTomaMx(tomaMx);
                        cantMxProc++;
                        if(cantMxProc==1)
                            codigosLabMx = esEstudio?tomaMx.getCodigoUnicoMx():tomaMx.getCodigoLab();
                        else
                            codigosLabMx += ","+ (esEstudio?tomaMx.getCodigoUnicoMx():tomaMx.getCodigoLab());
                    } catch (Exception ex) {
                        resultado = messageSource.getMessage("msg.update.order.error", null, null);
                        resultado = resultado + ". \n " + ex.getMessage();
                        ex.printStackTrace();
                    }
                }
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.receipt.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("strMuestras",strMuestras);
            map.put("mensaje",resultado);
            map.put("cantMuestras", cantMuestras.toString());
            map.put("cantMxProc", cantMxProc.toString());
            map.put("codigosUnicosMx",codigosLabMx);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = "recepcionMasivaLab", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void recepcionMasivaLaboratorio(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        String strRecepciones="";
        Integer cantRecepciones = 0;
        Integer cantRecepProc = 0;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            strRecepciones = jsonpObject.get("strRecepciones").toString();
            cantRecepciones = jsonpObject.get("cantRecepciones").getAsInt();

            User user = seguridadService.getUsuario(seguridadService.obtenerNombreUsuario());
            Laboratorio labUsuario = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
            //Se obtiene estado recepcionado en laboratorio
            EstadoMx estadoMx = catalogosService.getEstadoMx("ESTDMX|RCLAB");
            //se obtiene calidad de la muestra
            CalidadMx calidadMx = catalogosService.getCalidadMx("CALIDMX|ADC");
            //se obtienen recepciones a recepcionar en lab
            JsonObject jObjectRecepciones = new Gson().fromJson(strRecepciones, JsonObject.class);
            for(int i = 0; i< cantRecepciones;i++) {
                String idRecepcion = jObjectRecepciones.get(String.valueOf(i)).getAsString();
                RecepcionMx recepcionMx = recepcionMxService.getRecepcionMx(idRecepcion);
                //se setean valores a actualizar
                //recepcionMx.setUsuarioRecepcionLab(usuario);
                //recepcionMx.setFechaHoraRecepcionLab(new Timestamp(new Date().getTime()));
                recepcionMx.setCalidadMx(calidadMx);
                recepcionMx.setCausaRechazo(null);

                RecepcionMxLab recepcionMxLab = new RecepcionMxLab();
                recepcionMxLab.setRecepcionMx(recepcionMx);
                recepcionMxLab.setUsuarioRecepcion(user);
                recepcionMxLab.setFechaHoraRecepcion(new Timestamp(new Date().getTime()));
                TrasladoMx trasladoMxActivo = trasladosService.getTrasladoInternoActivoMxRecepcion(recepcionMx.getTomaMx().getIdTomaMx());
                boolean actualizarTraslado = false;
                if (trasladoMxActivo!=null) {
                    if (!trasladoMxActivo.isTrasladoExterno()) {
                        if (seguridadService.usuarioAutorizadoArea(seguridadService.obtenerNombreUsuario(), trasladoMxActivo.getAreaDestino().getIdArea())){
                            recepcionMxLab.setArea(trasladoMxActivo.getAreaDestino());
                            //si tiene traslado activo marcarlo como recepcionado
                            trasladoMxActivo.setRecepcionado(true);
                            trasladoMxActivo.setFechaHoraRecepcion(new Timestamp(new Date().getTime()));
                            trasladoMxActivo.setUsuarioRecepcion(user);
                            actualizarTraslado = true;
                        }
                    }
                }else {
                    //se si no hay traslado, pero tiene mas de un dx validar si el usuario tiene acceso al de mayor prioridad
                    List<DaSolicitudDx> solicitudDxList = tomaMxService.getSolicitudesDxPrioridadByIdToma(recepcionMx.getTomaMx().getIdTomaMx());
                    if (solicitudDxList.size() > 0) {
                        if (seguridadService.usuarioAutorizadoArea(seguridadService.obtenerNombreUsuario(), solicitudDxList.get(0).getCodDx().getArea().getIdArea())) {
                            recepcionMxLab.setArea(solicitudDxList.get(0).getCodDx().getArea());
                        }
                    }else{ //es estudio, se toma el area del estudio. Sólo se permite un estudio por muestra
                        List<DaSolicitudEstudio> solicitudEstudioList = tomaMxService.getSolicitudesEstudioByIdTomaMx(recepcionMx.getTomaMx().getIdTomaMx());
                        if (solicitudEstudioList.size()>0){
                            recepcionMxLab.setArea(solicitudEstudioList.get(0).getTipoEstudio().getArea());
                        }
                    }
                }

                boolean procesarRecepcion = false;
                try {
                    //se procesan las ordenes de examen
                    //boolean tieneOrdenesAnuladas = ordenExamenMxService.getOrdenesExamenNoAnuladasByIdMx(recepcionMx.getTomaMx().getIdTomaMx()).size()>0;
                    List<DaSolicitudDx> solicitudDxList = tomaMxService.getSolicitudesDxByIdTomaArea(recepcionMx.getTomaMx().getIdTomaMx(), recepcionMxLab.getArea().getIdArea(),seguridadService.obtenerNombreUsuario());
                    if (solicitudDxList!=null && solicitudDxList.size()> 0){
                        //se obtiene la lista de examenes por defecto para dx según el tipo de notificación configurado en tabla de parámetros. Se pasa como paràmetro el codigo del tipo de notificación
                        Parametro pTipoNoti = parametrosService.getParametroByName(recepcionMx.getTomaMx().getIdNotificacion().getCodTipoNotificacion().getCodigo());
                        if (pTipoNoti != null) {
                            for(DaSolicitudDx solicitudDx : solicitudDxList) {
                                List<Examen_Dx> examenesList = null;
                                //se obtienen los id de los examenes por defecto
                                Parametro pExamenesDefecto = parametrosService.getParametroByName(pTipoNoti.getValor());
                                if (pExamenesDefecto != null)
                                    examenesList = examenesService.getExamenesByIdDxAndIdsEx(solicitudDx.getCodDx().getIdDiagnostico(), pExamenesDefecto.getValor());
                                if (examenesList != null) {
                                    //se registran los examenes por defecto
                                    for (Examen_Dx examenTmp : examenesList) {
                                        //sólo se agrega la oorden si aún no tiene registrada orden de examen, misma toma, mismo dx, mismo examen y no está anulado
                                        if (ordenExamenMxService.getOrdExamenNoAnulByIdMxIdDxIdExamen(recepcionMx.getTomaMx().getIdTomaMx(), solicitudDx.getCodDx().getIdDiagnostico(), examenTmp.getExamen().getIdExamen(),seguridadService.obtenerNombreUsuario()).size() <= 0) {
                                            OrdenExamen ordenExamen = new OrdenExamen();
                                            ordenExamen.setSolicitudDx(solicitudDx);
                                            ordenExamen.setCodExamen(examenTmp.getExamen());
                                            ordenExamen.setFechaHOrden(new Timestamp(new Date().getTime()));
                                            ordenExamen.setUsuarioRegistro(user);
                                            ordenExamen.setLabProcesa(labUsuario);
                                            try {
                                                ordenExamenMxService.addOrdenExamen(ordenExamen);
                                                procesarRecepcion = true; //si se agregó al menos un examen se puede procesar la recepción
                                            } catch (Exception ex) {
                                                ex.printStackTrace();
                                                logger.error("Error al agregar orden de examen", ex);
                                            }
                                        }else{//si ya esta registrada una orden válida, entonces se puede procesar
                                            procesarRecepcion = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    List<DaSolicitudEstudio> solicitudEstudioList = tomaMxService.getSolicitudesEstudioByIdTomaMx(recepcionMx.getTomaMx().getIdTomaMx());
                    if (solicitudEstudioList!=null && solicitudEstudioList.size()>0){
                        //procesar examenes default para cada estudio
                        for(DaSolicitudEstudio solicitudEstudio:solicitudEstudioList){
                            String nombreParametroExam = solicitudEstudio.getTipoEstudio().getCodigo();
                            //nombre parámetro que contiene los examenes que se deben aplicar para cada estudio puede estar configurado de 3 maneras:
                            //cod_estudio+cod_categ+gravedad
                            //cod_estudio+gravedad
                            //cod_estudio
                            String gravedad = null;
                            String codUnicoMx = solicitudEstudio.getIdTomaMx().getCodigoUnicoMx();
                            if (codUnicoMx.contains("."))
                                gravedad = codUnicoMx.substring(codUnicoMx.lastIndexOf(".")+1);

                            if (solicitudEstudio.getIdTomaMx().getCategoriaMx()!=null){
                                nombreParametroExam += "_"+solicitudEstudio.getIdTomaMx().getCategoriaMx().getCodigo();
                                if (gravedad!=null)
                                    nombreParametroExam+= "_"+gravedad;
                            }else{
                                if (gravedad!=null)
                                    nombreParametroExam+= "_"+gravedad;
                            }

                            Parametro examenesEstudio = parametrosService.getParametroByName(nombreParametroExam);
                            if (examenesEstudio!=null){
                                List<CatalogoExamenes> examenesList = examenesService.getExamenesByIdsExamenes(examenesEstudio.getValor());
                                for(CatalogoExamenes examen : examenesList){
                                    //sólo se agrega la oorden si aún no tiene registrada orden de examen, misma toma, mismo estudio, mismo examen y no está anulado
                                    if (ordenExamenMxService.getOrdExamenNoAnulByIdMxIdEstIdExamen(recepcionMx.getTomaMx().getIdTomaMx(), solicitudEstudio.getTipoEstudio().getIdEstudio(), examen.getIdExamen()).size() <= 0) {
                                        OrdenExamen ordenExamen = new OrdenExamen();
                                        ordenExamen.setSolicitudEstudio(solicitudEstudio);
                                        ordenExamen.setCodExamen(examen);
                                        ordenExamen.setFechaHOrden(new Timestamp(new Date().getTime()));
                                        ordenExamen.setUsuarioRegistro(user);
                                        ordenExamen.setLabProcesa(labUsuario);
                                        try {
                                            ordenExamenMxService.addOrdenExamen(ordenExamen);
                                            procesarRecepcion = true; //si se agregó al menos un examen se puede procesar la recepción
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                            logger.error("Error al agregar orden de examen", ex);
                                        }
                                    }else{//si ya esta registrada una orden válida, entonces se puede procesar
                                        procesarRecepcion = true;
                                    }
                                }
                            }else { // se consulta si hay configuración sólo por codigo de estudio
                                examenesEstudio = parametrosService.getParametroByName(solicitudEstudio.getTipoEstudio().getCodigo());
                                if (examenesEstudio!=null){
                                    List<CatalogoExamenes> examenesList = examenesService.getExamenesByIdsExamenes(examenesEstudio.getValor());
                                    for(CatalogoExamenes examen : examenesList){
                                        OrdenExamen ordenExamen = new OrdenExamen();
                                        ordenExamen.setSolicitudEstudio(solicitudEstudio);
                                        ordenExamen.setCodExamen(examen);
                                        ordenExamen.setFechaHOrden(new Timestamp(new Date().getTime()));
                                        ordenExamen.setUsuarioRegistro(user);
                                        ordenExamen.setLabProcesa(labUsuario);
                                        try {
                                            ordenExamenMxService.addOrdenExamen(ordenExamen);
                                            procesarRecepcion = true; //si se agregó al menos un examen se puede procesar la recepción
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                            logger.error("Error al agregar orden de examen", ex);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (procesarRecepcion) {
                        recepcionMxService.addRecepcionMxLab(recepcionMxLab);
                        recepcionMxService.updateRecepcionMx(recepcionMx);
                        if (actualizarTraslado)
                            trasladosService.saveTrasladoMx(trasladoMxActivo);
                    }
                }catch (Exception ex){
                    resultado = messageSource.getMessage("msg.add.receipt.error",null,null);
                    resultado=resultado+". \n "+ex.getMessage();
                    ex.printStackTrace();
                }
                if (!idRecepcion.isEmpty() && procesarRecepcion) {
                    //se tiene que actualizar la tomaMx
                    DaTomaMx tomaMx = tomaMxService.getTomaMxById(recepcionMx.getTomaMx().getIdTomaMx());
                    tomaMx.setEstadoMx(estadoMx);
                    try {
                        tomaMxService.updateTomaMx(tomaMx);
                        cantRecepProc++;
                    }catch (Exception ex){
                        resultado = messageSource.getMessage("msg.update.order.error",null,null);
                        resultado=resultado+". \n "+ex.getMessage();
                        ex.printStackTrace();
                    }
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.receipt.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("strRecepciones",strRecepciones);
            map.put("mensaje",resultado);
            map.put("cantRecepciones",cantRecepciones.toString());
            map.put("cantRecepProc",cantRecepProc.toString());
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    private String agregarOrdenExamenVigRut(JsonObject jsonpObject, HttpServletRequest request) throws Exception {
        String resultado = "";
        String idTomaMx = "";
        int idDiagnostico = 0;
        int idExamen = 0;
        idTomaMx = jsonpObject.get("idTomaMx").getAsString();
        idDiagnostico = jsonpObject.get("idDiagnostico").getAsInt();
        idExamen = jsonpObject.get("idExamen").getAsInt();
        //se valida si existe una orden activa para la muestra, el diagnóstico y el examen
        List<OrdenExamen> ordenExamenList = ordenExamenMxService.getOrdExamenNoAnulByIdMxIdDxIdExamen(idTomaMx, idDiagnostico, idExamen, seguridadService.obtenerNombreUsuario());
        if (ordenExamenList!=null && ordenExamenList.size()>0) {
            resultado = messageSource.getMessage("msg.receipt.test.exist", null, null);
        }else{
            CatalogoExamenes examen = examenesService.getExamenById(idExamen);
            User usuario = seguridadService.getUsuario(seguridadService.obtenerNombreUsuario());
            Laboratorio labUsuario = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
            OrdenExamen ordenExamen = new OrdenExamen();
            DaSolicitudDx solicitudDx = tomaMxService.getSolicitudesDxByMxDx(idTomaMx, idDiagnostico);
            if (solicitudDx!=null){
                ordenExamen.setSolicitudDx(solicitudDx);
                ordenExamen.setCodExamen(examen);
                ordenExamen.setFechaHOrden(new Timestamp(new Date().getTime()));
                ordenExamen.setUsuarioRegistro(usuario);
                ordenExamen.setLabProcesa(labUsuario);
                try {
                    ordenExamenMxService.addOrdenExamen(ordenExamen);
                } catch (Exception ex) {
                    resultado = messageSource.getMessage("msg.receipt.add.test.error", null, null);
                    resultado = resultado + ". \n " + ex.getMessage();
                    ex.printStackTrace();
                }
            }else{
                Catalogo_Dx dx = tomaMxService.getDxById(String.valueOf(idDiagnostico));
                resultado = messageSource.getMessage("msg.receipt.add.test.error2", null, null);
                resultado = resultado.replace("{0}", dx.getNombre());
            }
        }
        UnicodeEscaper escaper     = UnicodeEscaper.above(127);
        return escaper.translate(resultado);
    }

    private String agregarOrdenExamenEstudio(JsonObject jsonpObject, HttpServletRequest request) throws Exception {
        String resultado = "";
        String idTomaMx = "";
        int idEstudio = 0;
        int idExamen = 0;
        idTomaMx = jsonpObject.get("idTomaMx").getAsString();
        idEstudio = jsonpObject.get("idEstudio").getAsInt();
        idExamen = jsonpObject.get("idExamen").getAsInt();
        //se valida si existe una orden activa para la muestra, el diagnóstico y el examen
        List<OrdenExamen> ordenExamenList = ordenExamenMxService.getOrdExamenNoAnulByIdMxIdEstIdExamen(idTomaMx, idEstudio, idExamen);
        if (ordenExamenList!=null && ordenExamenList.size()>0) {
            resultado = messageSource.getMessage("msg.receipt.test.exist", null, null);
        }else{
            CatalogoExamenes examen = examenesService.getExamenById(idExamen);
            User usuario = seguridadService.getUsuario(seguridadService.obtenerNombreUsuario());
            Laboratorio labUsuario = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
            OrdenExamen ordenExamen = new OrdenExamen();
            DaSolicitudEstudio solicitudEstudio = tomaMxService.getSolicitudesEstudioByMxEst(idTomaMx, idEstudio);
            if (solicitudEstudio!=null) {
                ordenExamen.setSolicitudEstudio(solicitudEstudio);
                ordenExamen.setCodExamen(examen);
                ordenExamen.setFechaHOrden(new Timestamp(new Date().getTime()));
                ordenExamen.setUsuarioRegistro(usuario);
                ordenExamen.setLabProcesa(labUsuario);
                try {
                    ordenExamenMxService.addOrdenExamen(ordenExamen);
                } catch (Exception ex) {
                    resultado = messageSource.getMessage("msg.receipt.add.test.error", null, null);
                    resultado = resultado + ". \n " + ex.getMessage();
                    ex.printStackTrace();
                }
            }else{
                Catalogo_Estudio est = tomaMxService.getEstudioById(idEstudio);
                resultado = messageSource.getMessage("msg.receipt.add.test.error2", null, null);
                resultado = resultado.replace("{0}",est.getNombre());
            }
        }
        UnicodeEscaper escaper     = UnicodeEscaper.above(127);
        return escaper.translate(resultado);
    }

    /**
     * Método que convierte una lista de tomaMx a un string con estructura Json
     * @param tomaMxList lista con las tomaMx a convertir
     * @return String
     */
    private String tomaMxToJson(List<DaTomaMx> tomaMxList){
        String jsonResponse;
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice=0;
        Laboratorio labUser = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
        boolean esEstudio;
        for(DaTomaMx tomaMx : tomaMxList){
            esEstudio = tomaMxService.getSolicitudesEstudioByIdTomaMx(tomaMx.getIdTomaMx()).size() > 0;
            String traslado = messageSource.getMessage("lbl.no",null,null);
            Laboratorio labOrigen = null;
            Map<String, String> map = new HashMap<String, String>();
            //map.put("idOrdenExamen",tomaMx.getIdOrdenExamen());
            map.put("idTomaMx", tomaMx.getIdTomaMx());
            map.put("codigoUnicoMx", esEstudio?tomaMx.getCodigoUnicoMx():tomaMx.getCodigoLab());
            //map.put("fechaHoraOrden",DateUtil.DateToString(tomaMx.getFechaHOrden(), "dd/MM/yyyy hh:mm:ss a"));
            map.put("fechaTomaMx",DateUtil.DateToString(tomaMx.getFechaHTomaMx(), "dd/MM/yyyy hh:mm:ss a"));
            if (tomaMx.getIdNotificacion().getCodSilaisAtencion()!=null) {
                map.put("codSilais", tomaMx.getIdNotificacion().getCodSilaisAtencion().getNombre());
            }else{
                map.put("codSilais","");
            }
            if (tomaMx.getIdNotificacion().getCodUnidadAtencion()!=null) {
                map.put("codUnidadSalud", tomaMx.getIdNotificacion().getCodUnidadAtencion().getNombre());
            }else {
                map.put("codUnidadSalud","");
            }
            //notificacion urgente
            if(tomaMx.getIdNotificacion().getUrgente()!= null){
                map.put("urgente", tomaMx.getIdNotificacion().getUrgente().getValor());
            }else{
                map.put("urgente", "--");
            }


            //hospitalizado
            String[] arrayHosp =  {"13", "17", "11", "16", "10", "12"};
            boolean hosp = false;

            if(tomaMx.getCodUnidadAtencion() != null){
                int h =  Arrays.binarySearch(arrayHosp, String.valueOf(tomaMx.getCodUnidadAtencion().getTipoUnidad()));
                hosp = h > 0;

            }

            if(hosp){
                map.put("hospitalizado", messageSource.getMessage("lbl.yes",null,null));
            }else{
                map.put("hospitalizado", messageSource.getMessage("lbl.no",null,null));
            }

            //map.put("estadoOrden", tomaMx.getCodEstado().getValor());
            map.put("separadaMx",(tomaMx.getMxSeparada()!=null?(tomaMx.getMxSeparada()?"Si":"No"):""));
            map.put("cantidadTubos", (tomaMx.getCanTubos()!=null?String.valueOf(tomaMx.getCanTubos()):""));
            map.put("tipoMuestra", tomaMx.getCodTipoMx().getNombre());
            //map.put("tipoExamen", tomaMx.getCodExamen().getNombre());
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
                //Se calcula la edad
                int edad = DateUtil.calcularEdadAnios(tomaMx.getIdNotificacion().getPersona().getFechaNacimiento());
                map.put("edad",String.valueOf(edad));
                //se obtiene el sexo
                map.put("sexo",tomaMx.getIdNotificacion().getPersona().getSexo().getValor());
                if(edad > 12 && tomaMx.getIdNotificacion().getPersona().isSexoFemenino()){
                    map.put("embarazada", tomaMxService.estaEmbarazada(tomaMx.getIdNotificacion().getIdNotificacion()));
                }else
                    map.put("embarazada","--");
            } else if (tomaMx.getIdNotificacion().getSolicitante() != null) {
                map.put("persona", tomaMx.getIdNotificacion().getSolicitante().getNombre());
                map.put("embarazada","--");
            }else{
                map.put("persona"," ");
                map.put("embarazada","--");
            }

            TrasladoMx trasladoMxActivo = trasladosService.getTrasladoActivoMxRecepcion(tomaMx.getIdTomaMx(),true);
            if (trasladoMxActivo!=null) {
                if (trasladoMxActivo.isControlCalidad()) {
                    traslado = messageSource.getMessage("lbl.yes",null,null);
                    labOrigen = trasladoMxActivo.getLaboratorioOrigen();
                }else if (trasladoMxActivo.isTrasladoExterno()){
                    labOrigen = trasladoMxActivo.getLaboratorioOrigen();
                }
            }
            map.put("traslado",traslado);
            map.put("origen",labOrigen!=null?labOrigen.getNombre():"");
            //sólo si no es traslado o si es traslado, pero el laboratorio del usuario es distinto del lab de origen del traslado se muestra en los resultados
            //se hace asi porque la consulta de búsqueda esta tomando tanto los envios actuales como los que estan en históricos(se necesita asi en la búsqueda mx)
            if (labOrigen==null || (!labOrigen.getCodigo().equals(labUser.getCodigo()))) {
                mapResponse.put(indice, map);
                indice++;
            }
        }
        jsonResponse = new Gson().toJson(mapResponse);
        UnicodeEscaper escaper     = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }

    /**
     * Método para convertir una lista de RecepcionMx a un string con estructura Json
     * @param recepcionMxList lista con las Recepciones a convertir
     * @return String
     */
    private String RecepcionMxToJson(List<RecepcionMx> recepcionMxList){
        String jsonResponse="";
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice=0;
        for(RecepcionMx recepcion : recepcionMxList){
            boolean mostrar = true;
            String traslado = messageSource.getMessage("lbl.no",null,null);
            String areaOrigen = "";
            TrasladoMx trasladoMxActivo = trasladosService.getTrasladoActivoMxRecepcion(recepcion.getTomaMx().getIdTomaMx(),false);
            if (trasladoMxActivo!=null) {
                if (trasladoMxActivo.isTrasladoExterno()) {
                    if (!seguridadService.usuarioAutorizadoLaboratorio(seguridadService.obtenerNombreUsuario(),trasladoMxActivo.getLaboratorioDestino().getCodigo())){
                        mostrar = false;
                    }else{
                        traslado = messageSource.getMessage("lbl.yes",null,null);
                        areaOrigen = trasladoMxActivo.getAreaOrigen().getNombre();
                    }
                }else {
                    if (!seguridadService.usuarioAutorizadoArea(seguridadService.obtenerNombreUsuario(), trasladoMxActivo.getAreaDestino().getIdArea())){
                        mostrar = false;
                    }else{
                        traslado = messageSource.getMessage("lbl.yes",null,null);
                        areaOrigen = trasladoMxActivo.getAreaOrigen().getNombre();
                    }
                }
            }else {
                //se si no hay traslado, pero tiene mas de un dx validar si el usuario tiene acceso al de mayor prioridad. Si sólo hay uno siempre se muestra
                List<DaSolicitudDx> solicitudDxList = tomaMxService.getSolicitudesDxPrioridadByIdToma(recepcion.getTomaMx().getIdTomaMx());
                if (solicitudDxList.size() > 1) {
                    if (!seguridadService.usuarioAutorizadoArea(seguridadService.obtenerNombreUsuario(), solicitudDxList.get(0).getCodDx().getArea().getIdArea())) {
                        mostrar = false;
                    }
                }
            }
            if (mostrar) {
                boolean esEstudio = tomaMxService.getSolicitudesEstudioByIdTomaMx( recepcion.getTomaMx().getIdTomaMx()).size() > 0;
                Map<String, String> map = new HashMap<String, String>();
                map.put("idRecepcion", recepcion.getIdRecepcion());
                //map.put("idOrdenExamen", ordenExamen.getOrdenExamen().getIdOrdenExamen());
                map.put("idTomaMx", recepcion.getTomaMx().getIdTomaMx());
                map.put("codigoUnicoMx", esEstudio?recepcion.getTomaMx().getCodigoUnicoMx():recepcion.getTomaMx().getCodigoLab());
                //map.put("fechaHoraOrden",DateUtil.DateToString(ordenExamen.getOrdenExamen().getFechaHOrden(), "dd/MM/yyyy hh:mm:ss a"));
                map.put("fechaTomaMx", DateUtil.DateToString(recepcion.getTomaMx().getFechaHTomaMx(), "dd/MM/yyyy hh:mm:ss a"));
                map.put("fechaRecepcion", DateUtil.DateToString(recepcion.getFechaHoraRecepcion(), "dd/MM/yyyy hh:mm:ss a"));
                if (recepcion.getTomaMx().getIdNotificacion().getCodSilaisAtencion() != null) {
                    map.put("codSilais", recepcion.getTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre());
                } else {
                    map.put("codSilais", "");
                }

                //notificacion urgente
                if(recepcion.getTomaMx().getIdNotificacion().getUrgente()!= null){
                    map.put("urgente", recepcion.getTomaMx().getIdNotificacion().getUrgente().getValor());
                }else{
                    map.put("urgente", "--");
                }


                //hospitalizado
                String[] arrayHosp =  {"13", "17", "11", "16", "10", "12"};
                boolean hosp = false;

                if(recepcion.getTomaMx().getCodUnidadAtencion() != null){
                    int h =  Arrays.binarySearch(arrayHosp, String.valueOf(recepcion.getTomaMx().getCodUnidadAtencion().getTipoUnidad()));
                    hosp = h > 0;

                }

                if(hosp){
                    map.put("hospitalizado", messageSource.getMessage("lbl.yes",null,null));
                }else{
                    map.put("hospitalizado", messageSource.getMessage("lbl.no",null,null));
                }

                if (recepcion.getTomaMx().getIdNotificacion().getCodUnidadAtencion() != null) {
                    map.put("codUnidadSalud", recepcion.getTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre());
                } else {
                    map.put("codUnidadSalud", "");
                }
                //map.put("estadoOrden", ordenExamen.getOrdenExamen().getCodEstado().getValor());
                map.put("separadaMx", (recepcion.getTomaMx().getMxSeparada() != null ? (recepcion.getTomaMx().getMxSeparada() ? "Si" : "No") : ""));
                map.put("cantidadTubos", (recepcion.getTomaMx().getCanTubos() != null ? String.valueOf(recepcion.getTomaMx().getCanTubos()) : ""));
                map.put("tipoMuestra", recepcion.getTomaMx().getCodTipoMx().getNombre());
                //map.put("tipoExamen", ordenExamen.getOrdenExamen().getCodExamen().getNombre());
                //map.put("areaProcesa", ordenExamen.getOrdenExamen().getCodExamen().getArea().getNombre());
                //Si hay fecha de inicio de sintomas se muestra
                Date fechaInicioSintomas = recepcion.getTomaMx().getIdNotificacion().getFechaInicioSintomas();
                if (fechaInicioSintomas != null)
                    map.put("fechaInicioSintomas", DateUtil.DateToString(fechaInicioSintomas, "dd/MM/yyyy"));
                else
                    map.put("fechaInicioSintomas", " ");
                //Si hay persona
                if (recepcion.getTomaMx().getIdNotificacion().getPersona() != null) {
                    /// se obtiene el nombre de la persona asociada a la ficha
                    String nombreCompleto = "";
                    nombreCompleto = recepcion.getTomaMx().getIdNotificacion().getPersona().getPrimerNombre();
                    if (recepcion.getTomaMx().getIdNotificacion().getPersona().getSegundoNombre() != null)
                        nombreCompleto = nombreCompleto + " " + recepcion.getTomaMx().getIdNotificacion().getPersona().getSegundoNombre();
                    nombreCompleto = nombreCompleto + " " + recepcion.getTomaMx().getIdNotificacion().getPersona().getPrimerApellido();
                    if (recepcion.getTomaMx().getIdNotificacion().getPersona().getSegundoApellido() != null)
                        nombreCompleto = nombreCompleto + " " + recepcion.getTomaMx().getIdNotificacion().getPersona().getSegundoApellido();
                    map.put("persona", nombreCompleto);
                    //Se calcula la edad
                    int edad = DateUtil.calcularEdadAnios(recepcion.getTomaMx().getIdNotificacion().getPersona().getFechaNacimiento());
                    map.put("edad",String.valueOf(edad));
                    //se obtiene el sexo
                    map.put("sexo",recepcion.getTomaMx().getIdNotificacion().getPersona().getSexo().getValor());
                    if(edad > 12 && recepcion.getTomaMx().getIdNotificacion().getPersona().isSexoFemenino()){
                        map.put("embarazada", tomaMxService.estaEmbarazada(recepcion.getTomaMx().getIdNotificacion().getIdNotificacion()));
                    }else
                        map.put("embarazada","--");
                } else if (recepcion.getTomaMx().getIdNotificacion().getSolicitante() != null) {
                    map.put("persona", recepcion.getTomaMx().getIdNotificacion().getSolicitante().getNombre());
                    map.put("embarazada","--");
                }else {
                    map.put("persona", " ");
                    map.put("embarazada","--");
                }
                map.put("traslado",traslado);
                map.put("origen",areaOrigen);
                mapResponse.put(indice, map);
                indice++;
            }
        }
        jsonResponse = new Gson().toJson(mapResponse);
        return jsonResponse;
    }

    /**
     * Método para convertir una lista de Ordenes Examen a un string con estructura Json
     * @param ordenesExamenList lista con las ordenes de examen a convertir
     * @return String
     * @throws UnsupportedEncodingException
     */
    private String OrdenesExamenToJson(List<OrdenExamen> ordenesExamenList, TrasladoMx trasladoMx) throws UnsupportedEncodingException {
        String jsonResponse="";
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice=0;
        boolean agregarExamenDx = true;
        for(OrdenExamen ordenExamen : ordenesExamenList){
            Map<String, String> map = new HashMap<String, String>();
            if (ordenExamen.getSolicitudDx()!=null) {
                //si hay traslado interno, mostrar los examenes que corresponden al area destino del traslado
                if (trasladoMx!=null && trasladoMx.isTrasladoInterno()){
                    if (!trasladoMx.getAreaDestino().getIdArea().equals(ordenExamen.getSolicitudDx().getCodDx().getArea().getIdArea())){
                        agregarExamenDx = false;
                    }
                }

                if (agregarExamenDx) {
                    map.put("idTomaMx", ordenExamen.getSolicitudDx().getIdTomaMx().getIdTomaMx());
                    map.put("idOrdenExamen", ordenExamen.getIdOrdenExamen());
                    map.put("idExamen", ordenExamen.getCodExamen().getIdExamen().toString());
                    map.put("nombreExamen", ordenExamen.getCodExamen().getNombre());
                    map.put("nombreSolic", ordenExamen.getSolicitudDx().getCodDx().getNombre());
                    map.put("nombreAreaPrc", ordenExamen.getSolicitudDx().getCodDx().getArea().getNombre());
                    map.put("fechaSolicitud", DateUtil.DateToString(ordenExamen.getFechaHOrden(), "dd/MM/yyyy hh:mm:ss a"));
                    map.put("tipo", "Rutina");
                    if (ordenExamen.getSolicitudDx().getControlCalidad())
                        map.put("cc", messageSource.getMessage("lbl.yes", null, null));
                    else
                        map.put("cc", messageSource.getMessage("lbl.no", null, null));

                    if (!ordenExamen.getLabProcesa().getCodigo().equals(ordenExamen.getSolicitudDx().getLabProcesa().getCodigo()))
                        map.put("externo", messageSource.getMessage("lbl.yes", null, null));
                    else
                        map.put("externo", messageSource.getMessage("lbl.no", null, null));

                    mapResponse.put(indice, map);
                    indice ++;
                }
                agregarExamenDx = true;
            }else{
                map.put("idTomaMx", ordenExamen.getSolicitudEstudio().getIdTomaMx().getIdTomaMx());
                map.put("idOrdenExamen", ordenExamen.getIdOrdenExamen());
                map.put("idExamen", ordenExamen.getCodExamen().getIdExamen().toString());
                map.put("nombreExamen", ordenExamen.getCodExamen().getNombre());
                map.put("nombreSolic", ordenExamen.getSolicitudEstudio().getTipoEstudio().getNombre());
                map.put("nombreAreaPrc", ordenExamen.getSolicitudEstudio().getTipoEstudio().getArea().getNombre());
                map.put("fechaSolicitud", DateUtil.DateToString(ordenExamen.getFechaHOrden(), "dd/MM/yyyy hh:mm:ss a"));
                map.put("tipo","Estudio");
                map.put("cc",messageSource.getMessage("lbl.no",null,null));
                map.put("externo",messageSource.getMessage("lbl.no",null,null));
                mapResponse.put(indice, map);
                indice ++;
            }
        }
        jsonResponse = new Gson().toJson(mapResponse);
        //escapar caracteres especiales, escape de los caracteres con valor numérico mayor a 127
        UnicodeEscaper escaper     = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }

    /**
     * Método para convertir estructura Json que se recibe desde el cliente a FiltroMx para realizar búsqueda de Mx(Vigilancia) y Recepción Mx(Laboratorio)
     * @param strJson String con la información de los filtros
     * @return FiltroMx
     * @throws Exception
     */
    private FiltroMx jsonToFiltroMx(String strJson) throws Exception {
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
        String codigoUnicoMx = null;
        String codTipoSolicitud = null;
        String nombreSolicitud = null;
        Boolean controlCalidad = null;

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
        if (jObjectFiltro.get("codigoUnicoMx") != null && !jObjectFiltro.get("codigoUnicoMx").getAsString().isEmpty())
            codigoUnicoMx = jObjectFiltro.get("codigoUnicoMx").getAsString();
        if (jObjectFiltro.get("codTipoSolicitud") != null && !jObjectFiltro.get("codTipoSolicitud").getAsString().isEmpty())
            codTipoSolicitud = jObjectFiltro.get("codTipoSolicitud").getAsString();
        if (jObjectFiltro.get("nombreSolicitud") != null && !jObjectFiltro.get("nombreSolicitud").getAsString().isEmpty())
            nombreSolicitud = jObjectFiltro.get("nombreSolicitud").getAsString();
        if (jObjectFiltro.get("controlCalidad") != null && !jObjectFiltro.get("controlCalidad").getAsString().isEmpty())
            controlCalidad = jObjectFiltro.get("controlCalidad").getAsBoolean();

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
        if (!Boolean.valueOf(esLab)) { //es recepción general
            filtroMx.setCodEstado("ESTDMX|ENV"); // sólo las enviadas
        } else { //es recepción en laboratorio
            filtroMx.setCodEstado("ESTDMX|EPLAB"); // sólo las enviadas para procesar en laboratorio
            filtroMx.setIncluirMxInadecuada(true);
            filtroMx.setSolicitudAprobada(false);
        }
        filtroMx.setCodigoUnicoMx(codigoUnicoMx);
        filtroMx.setNombreUsuario(seguridadService.obtenerNombreUsuario());
        filtroMx.setIncluirTraslados(true);
        filtroMx.setControlCalidad(controlCalidad);

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
        Laboratorio laboratorioUsuario = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
        validaRecepcionMx = recepcionMxService.getRecepcionMxByCodUnicoMx(codigoUnicoMx,(laboratorioUsuario.getCodigo()!=null?laboratorioUsuario.getCodigo():""));
        //si existe, de manera recursiva se solicita un nuevo código
        if (validaRecepcionMx!=null){
            codigoUnicoMx = generarCodigoUnicoMx();
        }
        //si no existe se retorna el último código generado
        return codigoUnicoMx;
    }

}
