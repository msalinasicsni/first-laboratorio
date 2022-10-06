package ni.gob.minsa.laboratorio.web.controllers;

import com.google.common.base.Predicate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.concepto.Catalogo_Lista;
import ni.gob.minsa.laboratorio.domain.estructura.EntidadesAdtvas;
import ni.gob.minsa.laboratorio.domain.estructura.Unidades;
import ni.gob.minsa.laboratorio.domain.examen.*;
import ni.gob.minsa.laboratorio.domain.muestra.*;
import ni.gob.minsa.laboratorio.domain.muestra.traslado.TrasladoMx;
import ni.gob.minsa.laboratorio.domain.parametros.Parametro;
import ni.gob.minsa.laboratorio.domain.resultados.DetalleResultado;
import ni.gob.minsa.laboratorio.domain.resultados.DetalleResultadoFinal;
import ni.gob.minsa.laboratorio.domain.resultados.RespuestaExamen;
import ni.gob.minsa.laboratorio.domain.resultados.RespuestaSolicitud;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.Authority;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.AutoridadArea;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.User;
import ni.gob.minsa.laboratorio.restServices.CallRestServices;
import ni.gob.minsa.laboratorio.service.*;
import ni.gob.minsa.laboratorio.utilities.ConstantsSecurity;
import ni.gob.minsa.laboratorio.utilities.DateUtil;
import ni.gob.minsa.laboratorio.utilities.GuidGenerator;
import ni.gob.minsa.laboratorio.utilities.HL7.TestOrder;
import ni.gob.minsa.laboratorio.utilities.StringUtil;
import ni.gob.minsa.laboratorio.utilities.dto.DatosCovidViajeroDTO;
import ni.gob.minsa.laboratorio.utilities.enumeration.HealthUnitType;
import ni.gob.minsa.laboratorio.utilities.pdfUtils.GeneralUtils;
import ni.gob.minsa.laboratorio.utilities.reportes.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.text.translate.UnicodeEscaper;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;
import java.util.List;

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

    @Resource(name = "daNotificacionService")
    private DaNotificacionService notificacionService;

    @Resource(name = "resultadosService")
    private ResultadosService resultadosService;

    @Resource(name = "organizationChartService")
    private OrganizationChartService organizationChartService;

    @Resource(name = "autoridadesService")
    private AutoridadesService autoridadesService;

    @Resource(name = "respuestasExamenService")
    private RespuestasExamenService respuestasExamenService;

    @Resource(name = "equiposProcesamientoService")
    private EquiposProcesamientoService equiposProcesamientoService;

    @Resource(name = "comunicacionResultadosService")
    private ComunicacionResultadosService comunicacionResultadosService;

    @Autowired
    MessageSource messageSource;

    /**
     * M�todo que se llama al entrar a la opci�n de menu "Recepci�n Mx Vigilancia". Se encarga de inicializar las listas para realizar la b�squeda de envios de Mx
     * @param request para obtener informaci�n de la petici�n del cliente
     * @return ModelAndView
     * @throws Exception
     */
    @RequestMapping(value = "init", method = RequestMethod.GET)
    public ModelAndView initSearchForm(HttpServletRequest request) throws Exception {
        logger.debug("buscar ordenes para ordenExamen");

        ModelAndView mav = new ModelAndView();
        List<EntidadesAdtvas> entidadesAdtvases =  entidadAdmonService.getAllEntidadesAdtvas();
        List<TipoMx> tipoMxList = catalogosService.getTipoMuestra();
        Laboratorio labUser = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
        mav.addObject("entidades",entidadesAdtvases);
        mav.addObject("tipoMuestra", tipoMxList);
        mav.addObject("mostrarPopUpMx",(labUser!=null?labUser.getPopUpCodigoMx():false));
        mav.setViewName("recepcionMx/searchOrders");

        return mav;
    }

    /**
     * M�todo que se llama al entrar a la opci�n de menu "Recepci�n Mx Laboratorio". Se encarga de inicializar las listas para realizar la b�squeda de envios de Mx
     * @param request para obtener informaci�n de la petici�n del cliente
     * @return ModelAndView
     * @throws Exception
     */
    @RequestMapping(value = "initLab", method = RequestMethod.GET)
    public ModelAndView initSearchLabForm(HttpServletRequest request) throws Exception {
        logger.debug("buscar ordenes para ordenExamen");
        String urlValidacion;
        try {
            urlValidacion = seguridadService.validarLogin(request);
            //si la url esta vacia significa que la validaci�n del login fue exitosa
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
     * M�todo que se llama para crear una Recepci�n Mx Vigilancia. Setea los datos de la Muestra e inicializa listas y resto de controles
     * @param request para obtener informaci�n de la petici�n del cliente
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
            //si la url esta vacia significa que la validaci�n del login fue exitosa
            if (urlValidacion.isEmpty())
                urlValidacion = seguridadService.validarAutorizacionUsuario(request, ConstantsSecurity.SYSTEM_CODE, false);
        }catch (Exception e){
            e.printStackTrace();
            urlValidacion = "404";
        }
        ModelAndView mav = new ModelAndView();
        if (urlValidacion.isEmpty()) {
            boolean trasladoCC = false;
            DaTomaMx tomaMx = tomaMxService.getTomaMxById(strIdOrden);
            List<EntidadesAdtvas> entidadesAdtvases =  entidadAdmonService.getAllEntidadesAdtvas();
            List<TipoMx> tipoMxList = catalogosService.getTipoMuestra();
            //List<Laboratorio> laboratorioList = laboratoriosService.getLaboratoriosInternos();
            List<Unidades> unidades = null;
            Laboratorio labUser = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
            List<DaSolicitudDx> solicitudDxList = tomaMxService.getSolicitudesDxByIdToma(tomaMx.getIdTomaMx(),labUser.getCodigo());
            TrasladoMx trasladoMxActivo = trasladosService.getTrasladoActivoMxRecepcion(tomaMx.getIdTomaMx(),false);
            if (trasladoMxActivo!=null) {
                if (trasladoMxActivo.isTrasladoExterno()){
                    solicitudDxList = tomaMxService.getSolicitudesDxTrasladoExtByIdToma(tomaMx.getIdTomaMx(),labUser.getCodigo());
                }
                trasladoCC = trasladoMxActivo.isControlCalidad();
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
            //si hay fecha de inicio de s�ntomas validar si es muestra v�lida para vigilancia rutinaria
            if (fechaInicioSintomas!=null){
                Parametro diasMinRecepMx = parametrosService.getParametroByName("DIAS_MIN_MX_VIG_RUT");
                int diffDias = DateUtil.CalcularDiferenciaDiasFechas(fechaInicioSintomas,new Date());
                if (diffDias < Integer.valueOf(diasMinRecepMx.getValor())){
                    html = messageSource.getMessage("msg.mx.must.be.inadequate",null,null).replace("{0}",diasMinRecepMx.getValor()); //"La cantidad de d�as desde el inicio de s�ntomas no es mayor o igual a "+diasMinRecepMx.getValor()+", la muestra deber�a marcarse como inadecuada";
                }
            }

            List<CausaRechazoMx> causaRechazoMxList = catalogosService.getCausaRechazoMxRecepGeneral();

            mav.addObject("tomaMx",tomaMx);
            mav.addObject("entidades",entidadesAdtvases);
            mav.addObject("unidades",unidades);
            mav.addObject("tipoMuestra", tipoMxList);
            //mav.addObject("laboratorios",laboratorioList);
            mav.addObject("fechaInicioSintomas",fechaInicioSintomas);
            mav.addObject("inadecuada",html);
            mav.addObject("dxList",solicitudDxList);
            mav.addObject("estudiosList",solicitudEstudioList);
            mav.addObject("causasRechazo",causaRechazoMxList);
            mav.addObject("mostrarPopUpMx",labUser.getPopUpCodigoMx());
            mav.addObject("trasladoCC",trasladoCC);
            mav.setViewName("recepcionMx/recepcionarOrders");
        }else
            mav.setViewName(urlValidacion);

        return mav;
    }

    /**
     * M�todo que se llama para crear una Recepci�n Mx en el Laboratorio. Setea los datos de la recepci�n e inicializa listas y demas controles.
     * Adem�s si es la primera vez que se carga el registro se registran ordenes de examen para los examenes configurados por defecto en la tabla
     * de par�metros seg�n el tipo de notificaci�n, tipo de mx, tipo dx
     * @param request para obtener informaci�n de la petici�n del cliente
     * @param strIdRecepcion Id de la recepci�n general a recepcionar en el laboratorio
     * @return ModelAndView
     * @throws Exception
     */
    @RequestMapping(value = "createLab/{strIdRecepcion}", method = RequestMethod.GET)
    public ModelAndView createReceiptLabForm(HttpServletRequest request, @PathVariable("strIdRecepcion")  String strIdRecepcion) throws Exception {
        logger.debug("buscar ordenes para ordenExamen");
        String urlValidacion;
        try {
            urlValidacion = seguridadService.validarLogin(request);
            //si la url esta vacia significa que la validaci�n del login fue exitosa
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
            //List<Laboratorio> laboratorioList = laboratoriosService.getLaboratoriosInternos();
            List<CalidadMx> calidadMx= catalogosService.getCalidadesMx();
            List<CondicionMx> condicionesMx = catalogosService.getCondicionesMx();
            //List<TipoTubo> tipoTubos = catalogosService.getTipoTubos();
            List<Unidades> unidades = null;
            List<Examen_Dx> examenesList = null;
            List<Examen_Estudio> examenesEstList = null;
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
                        //verificar si hay traslado activo, para saber que �rea es la que proceso
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
                            //SI el usuario tiene autoridad sobre mas de un dx en distintas areas, agregar exmaen por defecto
                            //Areas sobre las que tiene autoridad el usuario
                            List<AutoridadArea> areasAutorizadas = autoridadesService.getAutoridadesArea(usuario.getUsername());
                            for(AutoridadArea area : areasAutorizadas) {
                                List<DaSolicitudDx> solicitudDxList = tomaMxService.getSolicitudesDxByIdTomaArea(recepcionMx.getTomaMx().getIdTomaMx(), area.getArea().getIdArea(), seguridadService.obtenerNombreUsuario());
                                if (solicitudDxList != null && solicitudDxList.size() > 0) {
                                    for (DaSolicitudDx solicitudDx : solicitudDxList) {
                                        //se obtienen los id de los examenes por defecto
                                        examenesList = examenesService.getExamenesDefectoByIdDx(solicitudDx.getCodDx().getIdDiagnostico(), usuario.getUsername());
                                        if (examenesList != null) {
                                            //se registran los examenes por defecto
                                            for (Examen_Dx examenTmp : examenesList) {
                                                //si el �rea actual que debe procesa la mx es la misma area del ex�men entonces se registra la orden
                                                if (areaDestino != null && areaDestino.getIdArea().equals(examenTmp.getExamen().getArea().getIdArea())) {
                                                    OrdenExamen ordenExamen = new OrdenExamen();
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
                            }
                        }
                    }else{
                        if (solicitudEstudioList.size()>0) {
                            //procesar examenes default para cada estudio
                            for (DaSolicitudEstudio solicitudEstudio : solicitudEstudioList) {
                                //se obtienen los id de los examenes por defecto
                                examenesEstList = examenesService.getExamenesDefectoByIdEst(solicitudEstudio.getTipoEstudio().getIdEstudio(), usuario.getUsername());
                                if (examenesEstList != null) {
                                    //se registran los examenes por defecto
                                    for (Examen_Estudio examenTmp : examenesEstList) {
                                        OrdenExamen ordenExamen = new OrdenExamen();
                                        ordenExamen.setSolicitudEstudio(solicitudEstudio);
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
                        /*if (solicitudEstudioList.size()>0) {
                            //procesar examenes default para cada estudio
                            for (DaSolicitudEstudio solicitudEstudio : solicitudEstudioList) {
                                String nombreParametroExam = solicitudEstudio.getTipoEstudio().getCodigo();
                                //nombre par�metro que contiene los examenes que se deben aplicar para cada estudio puede estar configurado de 3 maneras:
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
                        }*/
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
                if (esEstudio) {
                    List<Catalogo_Estudio> catalogoEstudios =
                            tomaMxService.getEstudiosByTipoMxTipoNoti(recepcionMx.getTomaMx().getCodTipoMx().getIdTipoMx().toString(),
                                    recepcionMx.getTomaMx().getIdNotificacion().getCodTipoNotificacion().getCodigo());
                    mav.addObject("catEst", catalogoEstudios);
                }
                List<Catalogo_Dx> catalogoDxs =
                        tomaMxService.getDxByTipoMxTipoNoti(recepcionMx.getTomaMx().getCodTipoMx().getIdTipoMx().toString(),
                                recepcionMx.getTomaMx().getIdNotificacion().getCodTipoNotificacion().getCodigo(),seguridadService.obtenerNombreUsuario());
                mav.addObject("catDx", catalogoDxs);


            }


            List<CausaRechazoMx> causaRechazoMxList = catalogosService.getCausaRechazoMxRecepLab();
            mav.addObject("recepcionMx",recepcionMx);
            mav.addObject("entidades",entidadesAdtvases);
            mav.addObject("unidades",unidades);
            mav.addObject("tipoMuestra", tipoMxList);
            //mav.addObject("laboratorios",laboratorioList);
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
     * M�todo para realizar la b�squeda de Mx para recepcionar en Mx Vigilancia general
     * @param filtro JSon con los datos de los filtros a aplicar en la b�squeda(Nombre Apellido, Rango Fec Toma Mx, Tipo Mx, SILAIS, unidad salud)
     * @return String con las Mx encontradas
     * @throws Exception
     */
    @RequestMapping(value = "searchOrders", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String fetchOrdersJson(@RequestParam(value = "strFilter", required = true) String filtro) throws Exception{
        logger.info("Obteniendo las ordenes de examen pendientes seg�n filtros en JSON");
        FiltroMx filtroMx = jsonToFiltroMx(filtro);
        List<DaTomaMx> tomaMxList = tomaMxService.getTomaMxByFiltro(filtroMx);
        return tomaMxToJson(tomaMxList, (filtroMx.getControlCalidad()!=null && filtroMx.getControlCalidad()));
    }

    /**
     * M�todo para realizar la b�squeda de Recepcion Mx para recepcionar en laboratorio
     * @param filtro JSon con los datos de los filtros a aplicar en la b�squeda(Nombre Apellido, Rango Fec Toma Mx, Tipo Mx, SILAIS, unidad salud)
     * @return String con las Recepciones encontradas
     * @throws Exception
     */
    @RequestMapping(value = "searchOrdersLab", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String fetchOrdersLabJson(@RequestParam(value = "strFilter", required = true) String filtro) throws Exception{
        logger.info("Obteniendo las ordenes de examen pendienetes seg�n filtros en JSON");
        FiltroMx filtroMx = jsonToFiltroMx(filtro);
        List<RecepcionMx> recepcionMxList = recepcionMxService.getRecepcionesByFiltro(filtroMx);
        return RecepcionMxToJson(recepcionMxList);
    }

    /**
     * M�todo para registrar una recepci�n de muestra de vigilancia. Modifica la Mx al estado ESTDMX|RCP
     * @param request para obtener informaci�n de la petici�n del cliente. Contiene en un par�metro la estructura json del registro a agregar
     * @param response para notificar al cliente del resultado de la operaci�n
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
        String horaRecibido = "";
        String fechaRecibido ="";
        boolean mxInadecuada = false;
        boolean esControlCalidad = false;
        String areaEntrega = "";
        String sarsCov2 = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            verificaCantTb = jsonpObject.get("verificaCantTb").getAsString();
            verificaTipoMx = jsonpObject.get("verificaTipoMx").getAsString();
            idTomaMx = jsonpObject.get("idTomaMx").getAsString();
            causaRechazo = jsonpObject.get("causaRechazo").getAsString();
            if (jsonpObject.get("horaRecibido") != null && !jsonpObject.get("horaRecibido").getAsString().isEmpty())
                horaRecibido = jsonpObject.get("horaRecibido").getAsString();

            if (jsonpObject.get("fechaRecibido") != null && !jsonpObject.get("fechaRecibido").getAsString().isEmpty())
                fechaRecibido = jsonpObject.get("fechaRecibido").getAsString();

            User usuario = seguridadService.getUsuario(seguridadService.obtenerNombreUsuario());
            Laboratorio labUsuario = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
            //Se obtiene estado recepcionado
            EstadoMx estadoMx = catalogosService.getEstadoMx("ESTDMX|RCP");

            //se obtiene tomaMx de examen a recepcionar
            DaTomaMx tomaMx = tomaMxService.getTomaMxById(idTomaMx);

            TipoRecepcionMx tipoRecepcionMx = null;
            //se determina si es una muestra para estudio o para vigilancia rutinaria(Dx)
            List<DaSolicitudEstudio> solicitudEstudioList = tomaMxService.getSolicitudesEstudioByIdTomaMx(tomaMx.getIdTomaMx());
            boolean esEstudio = solicitudEstudioList.size()>0;
            tipoRecepcionMx = catalogosService.getTipoRecepcionMx((!esEstudio?"TPRECPMX|VRT":"TPRECPMX|EST"));
            RecepcionMx recepcionMx = new RecepcionMx();
            DatosRecepcionMx datosRecepcionMx = recepcionMxService.getRecepcionMxByCodUnicoMxV2(tomaMx.getCodigoUnicoMx(), labUsuario.getCodigo());
            //En algunas ocasiones se encuentra con una recepcion existente pero sin la mx actualizada a recepcionada, entonces lo que vamos a hacer es actualizar la existente, para que no de el error
            //ORA-00001: unique constraint (LABORATORIO.RECEPCION_MX_CODUNICO_LAB) violated
            if (datosRecepcionMx != null && tomaMx.getEstadoMx().getCodigo().equalsIgnoreCase("ESTDMX|ENV")) {
                recepcionMx.setIdRecepcion(datosRecepcionMx.getIdRecepcion());
            }
            if(fechaRecibido != null && !fechaRecibido.isEmpty()){
                recepcionMx.setFechaRecibido(DateUtil.StringToDate(fechaRecibido,"dd/MM/yyyy" ));
            }
            recepcionMx.setHoraRecibido(horaRecibido);
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
                //si tiene traslado activo marcarlo como recepcionado
                TrasladoMx trasladoActivo = trasladosService.getTrasladoActivoMx(idTomaMx);
                if (trasladoActivo!=null) esControlCalidad = trasladoActivo.isControlCalidad();
                if (recepcionMx.getIdRecepcion() != null)
                    recepcionMxService.updateRecepcionMx(recepcionMx);
                else
                    recepcionMxService.addRecepcionMx(recepcionMx);
                idRecepcion = recepcionMx.getIdRecepcion();
                if (trasladoActivo!=null) {
                    if (trasladoActivo.isTrasladoExterno() || trasladoActivo.isControlCalidad()){ //control de calidad, por tanto llega a recepci�n general
                        if (trasladoActivo.getLaboratorioDestino().getCodigo().equals(recepcionMx.getLabRecepcion().getCodigo())){
                            trasladoActivo.setRecepcionado(true);
                            trasladoActivo.setFechaHoraRecepcion(new Timestamp(new Date().getTime()));
                            trasladoActivo.setUsuarioRecepcion(usuario);
                            trasladosService.saveTrasladoMx(trasladoActivo);
                        }
                    }
                }
                //determinar area de entrega luego de recepci�n
                List<DaSolicitudDx> solicitudDxList = tomaMxService.getSoliDxPrioridadByTomaAndLab(idTomaMx, labUsuario.getCodigo());

                if (solicitudDxList.size()> 0)
                    areaEntrega = solicitudDxList.get(0).getCodDx().getArea().getNombre();
                else {
                    if (esEstudio) {
                            areaEntrega = solicitudEstudioList.get(0).getTipoEstudio().getArea().getNombre();
                    }
                }
                //saber si es sars-cov-2
                Predicate<DaSolicitudDx> byName = new Predicate<DaSolicitudDx>() {
                    @Override
                    public boolean apply(DaSolicitudDx solicitud) {
                        return solicitud.getCodDx().getNombre().toLowerCase().contains("sars-cov-2");
                    }
                };
                //si se encuentra el dx poner valor para imprimir 4 copias
                boolean tieneBioMol = false;
                Collection<DaSolicitudDx> dxSarsCoV2 = FilterLists.filter(solicitudDxList, byName);
                if (dxSarsCoV2.size() > 0) sarsCov2 = "sars-cov-2"; else sarsCov2 = "";

                //si muestra es inadecuada.. entonces resultado final de solicitudes asociadas a la mx es mx inadecuada
                if (mxInadecuada){
                    User usuApro = seguridadService.getUsuario(seguridadService.obtenerNombreUsuario());
                    if (!esEstudio) {
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
                //se setea consecutivo codigo lab. Formato COD_LAB-CONSECUTIVO-ANIO. S�lo para rutinas, que no vengan por traslado externo o control de calidad
                if (!esEstudio && tomaMx.getCodigoLab()==null && !esControlCalidad) {
                    tomaMx.setCodigoLab(recepcionMxService.obtenerCodigoLab(labUsuario.getCodigo(), 1));
                }
                tomaMx.setEstadoMx(estadoMx);
                tomaMx.setCodigoValidacion(GuidGenerator.next()); //servicios en linea. 17/02/2021
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
            UnicodeEscaper escaper     = UnicodeEscaper.above(127);
            Map<String, String> map = new HashMap<String, String>();
            map.put("idRecepcion",idRecepcion);
            map.put("mensaje",resultado);
            map.put("esSarsCov2", sarsCov2);
            map.put("idTomaMx", idTomaMx);
            map.put("verificaCantTb", verificaCantTb);
            map.put("verificaTipoMx", verificaTipoMx);
            map.put("codigoUnicoMx",codigoLabMx);
            map.put("area",escaper.translate(areaEntrega));
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    /**
     * M�todo para actualizar una recepci�n de vigilancia indicando que se ha recepcionado en el laboratorio. Modifica la Mx al estado ESTDMX|RCLAB
     * @param request para obtener informaci�n de la petici�n del cliente. Contiene en un par�metro la estructura json del registro a actualizar
     * @param response para notificar al cliente del resultado de la operaci�n
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "receiptLaboratory", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void recepcionLaboratorio(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String json;
        String resultado = "";
        String idRecepcion = "";
        String causaRechazo = null;
        String codCalidadMx = "";
        String codCondicionMx = "";
        boolean mxInadecuada = false;
        String horaRecibido = "";
        String fechaRecibido ="";
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

            if (jsonpObject.get("horaRecibido") != null && !jsonpObject.get("horaRecibido").getAsString().isEmpty())
                horaRecibido = jsonpObject.get("horaRecibido").getAsString();

            if (jsonpObject.get("fechaRecibido") != null && !jsonpObject.get("fechaRecibido").getAsString().isEmpty())
                fechaRecibido = jsonpObject.get("fechaRecibido").getAsString();

            User usuario = seguridadService.getUsuario(seguridadService.obtenerNombreUsuario());
            Laboratorio labUsuario = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
            //Se obtiene estado recepcionado en laboratorio
            EstadoMx estadoMx = catalogosService.getEstadoMx("ESTDMX|RCLAB");
            //se obtiene calidad de la muestra
            CalidadMx calidadMx = catalogosService.getCalidadMx(codCalidadMx);
            //se obtiene condici�n de la muestra
            CondicionMx condicionMx = catalogosService.getCondicionMx(codCondicionMx);
            //se obtiene recepci�n a actualizar
            RecepcionMx recepcionMx = recepcionMxService.getRecepcionMx(idRecepcion);

            if (recepcionMx.getTomaMx().getEstadoMx().getCodigo().equalsIgnoreCase("ESTDMX|TRAS") || recepcionMx.getTomaMx().getEstadoMx().getCodigo().equalsIgnoreCase("ESTDMX|EPLAB")) {
                //se determina si es una muestra para estudio o para vigilancia rutinaria(Dx)
                List<DaSolicitudEstudio> solicitudEstudioList = tomaMxService.getSolicitudesEstudioByIdTomaMx(recepcionMx.getTomaMx().getIdTomaMx());
                boolean esEstudio = solicitudEstudioList.size() > 0;
                //se setean valores a actualizar
                //recepcionMx.setUsuarioRecepcionLab(usuario);
                //recepcionMx.setFechaHoraRecepcionLab(new Timestamp(new Date().getTime()));
                recepcionMx.setCalidadMx(calidadMx);
                recepcionMx.setCondicionMx(condicionMx);
                if (causaRechazo != null) {
                    CausaRechazoMx causaRechazoMx = catalogosService.getCausaRechazoMx(causaRechazo);
                    recepcionMx.setCausaRechazo(causaRechazoMx);
                    mxInadecuada = true;
                }
                RecepcionMxLab recepcionMxLab = new RecepcionMxLab();
                recepcionMxLab.setRecepcionMx(recepcionMx);
                recepcionMxLab.setUsuarioRecepcion(usuario);
                if (fechaRecibido != null && !fechaRecibido.isEmpty()) {
                    if (horaRecibido != null && !horaRecibido.isEmpty()) {
                        recepcionMxLab.setFechaHoraRecepcion(new Timestamp(DateUtil.StringToDate(fechaRecibido + " " + horaRecibido, "dd/MM/yyyy HH:mm").getTime()));
                    } else {
                        recepcionMxLab.setFechaHoraRecepcion(new Timestamp(DateUtil.StringToDate(fechaRecibido, "dd/MM/yyyy").getTime()));
                    }
                } else {
                    recepcionMxLab.setFechaHoraRecepcion(new Timestamp(new Date().getTime()));
                }
                recepcionMxLab.setFechaHoraRegistro(new Timestamp(new Date().getTime()));
                List<DaSolicitudDx> solicitudDxList = tomaMxService.getSolicitudesDxPrioridadByIdToma(recepcionMx.getTomaMx().getIdTomaMx());
                TrasladoMx trasladoMxActivo = trasladosService.getTrasladoInternoActivoMxRecepcion(recepcionMx.getTomaMx().getIdTomaMx());
                boolean actualizarTraslado = false;
                if (trasladoMxActivo != null) {
                    if (!trasladoMxActivo.isTrasladoExterno()) {
                        if (seguridadService.usuarioAutorizadoArea(seguridadService.obtenerNombreUsuario(), trasladoMxActivo.getAreaDestino().getIdArea())) {
                            recepcionMxLab.setArea(trasladoMxActivo.getAreaDestino());
                            //si tiene traslado activo marcarlo como recepcionado
                            trasladoMxActivo.setRecepcionado(true);
                            trasladoMxActivo.setFechaHoraRecepcion(new Timestamp(new Date().getTime()));
                            trasladoMxActivo.setUsuarioRecepcion(usuario);
                            actualizarTraslado = true;
                        }
                    }
                } else {
                    //si no hay traslado, obtener area de dx con mayor prioridad
                    if (solicitudDxList.size() > 0) {
                        int prioridad = solicitudDxList.get(0).getCodDx().getPrioridad();
                        for (DaSolicitudDx solicitudDx : solicitudDxList) {
                            if (prioridad == solicitudDx.getCodDx().getPrioridad()) {
                                if (seguridadService.usuarioAutorizadoArea(seguridadService.obtenerNombreUsuario(), solicitudDx.getCodDx().getArea().getIdArea())) {
                                    recepcionMxLab.setArea(solicitudDx.getCodDx().getArea());
                                    break;
                                }
                            } else break;
                            prioridad = solicitudDx.getCodDx().getPrioridad();
                        }
                        //deberia ser siempre distinto de null, pero para evitar null pointer
                        if (recepcionMxLab.getArea() == null) {
                            recepcionMxLab.setArea(solicitudDxList.get(0).getCodDx().getArea());
                        }

                    } else { //es estudio, se toma el area del estudio. S�lo se permite un estudio por muestra
                        if (solicitudEstudioList.size() > 0) {
                            recepcionMxLab.setArea(solicitudEstudioList.get(0).getTipoEstudio().getArea());
                        }
                    }
                }

                try {
                    recepcionMxService.updateRecepcionMx(recepcionMx);
                    //Si el usuario tiene autoridad sobre mas de un area seg�n los dx solicitados en la muestra, agregar recepcion en lab para cada area
                    List<AutoridadArea> autoridadesArea = autoridadesService.getAutoridadesArea(usuario.getUsername());
                    List<Area> areasDx = tomaMxService.getAreaSolicitudDxByTomaAndUser(recepcionMx.getTomaMx().getIdTomaMx(), usuario.getUsername());
                    if (autoridadesArea.size() > 1) {
                        boolean agregarAreaEstudio =true; //13012020
                        for (AutoridadArea autoridadArea : autoridadesArea) {
                            for (Area area : areasDx) {
                                if (esEstudio) {//para no duplicar recepcion en laboratorio con misma area en dx y est
                                    if (area.getIdArea().equals(solicitudEstudioList.get(0).getTipoEstudio().getArea().getIdArea()))
                                        agregarAreaEstudio = false;
                                }
                                if (autoridadArea.getArea().getIdArea().equals(area.getIdArea())) {
                                    recepcionMxLab.setArea(autoridadArea.getArea());
                                    recepcionMxService.addRecepcionMxLab(recepcionMxLab);
                                }
                            }
                            //es estudio y tiene autoridad para varias areas y no se ha agregado recepcion en laboratorio del area del estudio
                            if (agregarAreaEstudio && solicitudEstudioList.size()>0){
                                recepcionMxLab.setArea(solicitudEstudioList.get(0).getTipoEstudio().getArea());
                                recepcionMxService.addRecepcionMxLab(recepcionMxLab);
                            }
                        }
                    } else {
                        recepcionMxService.addRecepcionMxLab(recepcionMxLab);
                    }
                    if (actualizarTraslado)
                        trasladosService.saveTrasladoMx(trasladoMxActivo);
                    //si muestra es inadecuada.. entonces resultado final de solicitudes asociadas a la mx es mx inadecuada
                    if (mxInadecuada) {
                        User usuApro = seguridadService.getUsuario(seguridadService.obtenerNombreUsuario());
                        if (!esEstudio) {
                            List<DaSolicitudDx> solicitudDxListInd = tomaMxService.getSolicitudesDxByIdToma(recepcionMx.getTomaMx().getIdTomaMx(), labUsuario.getCodigo());
                            for (DaSolicitudDx solicitudDx : solicitudDxListInd) {
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
                        } else {
                            for (DaSolicitudEstudio solicitudEst : solicitudEstudioList) {
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
                    } else {
                        //Registrar resultados por defecto. Se toman el concepto y el valor por defecto para los examenes de la tabla catalogo_dx, para que sea mas dinamico. 03/08/2022. Ya no se usan parametros
                        for (DaSolicitudDx solicitudDx : solicitudDxList) {
                            if (!solicitudDx.getAprobada() && solicitudDx.getCodDx().getConceptoDefResulExam() != null && solicitudDx.getCodDx().getValorDefResulExam() != null) {
                                List<OrdenExamen> ordenesExamen = ordenExamenMxService.getOrdenesExamenNoAnuladasByIdSolicitud(solicitudDx.getIdSolicitudDx());
                                boolean soloUnExamen = ordenesExamen.size()==1;
                                for (OrdenExamen examen : ordenesExamen) {
                                    //no tiene resultado registrado aún
                                    if (resultadosService.getDetallesResultadoActivosByExamen(examen.getIdOrdenExamen()).size() <= 0) {
                                        RespuestaExamen conceptoTmp = respuestasExamenService.getRespuestaByExamenAndConcepto(examen.getCodExamen().getIdExamen(), solicitudDx.getCodDx().getConceptoDefResulExam());
                                        if (conceptoTmp != null) {
                                            DetalleResultado detalleResultado = new DetalleResultado();
                                            detalleResultado.setFechahProcesa(recepcionMxLab.getFechaHoraRecepcion());
                                            detalleResultado.setFechahoraRegistro(recepcionMxLab.getFechaHoraRegistro());
                                            detalleResultado.setValor(solicitudDx.getCodDx().getValorDefResulExam());
                                            detalleResultado.setRespuesta(conceptoTmp);
                                            detalleResultado.setExamen(examen);
                                            detalleResultado.setUsuarioRegistro(usuario);
                                            if (detalleResultado.getValor() != null && !detalleResultado.getValor().isEmpty())
                                                resultadosService.addDetalleResultado(detalleResultado);
                                            if (soloUnExamen) guardarResultadoFinal(detalleResultado, false);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    //VALIDA SI ES NECESARIO ENVIAR PRE ORDEN A SISTEMA INFINITY
                    try {
                        validarEnviarSolicitudInfinity(recepcionMx, usuario);
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }

                    if (!idRecepcion.isEmpty()) {
                        //se tiene que actualizar la tomaMx
                        DaTomaMx tomaMx = tomaMxService.getTomaMxById(recepcionMx.getTomaMx().getIdTomaMx());
                        tomaMx.setEstadoMx(estadoMx);
                        try {
                            tomaMxService.updateTomaMx(tomaMx);
                        } catch (Exception ex) {
                            resultado = messageSource.getMessage("msg.update.order.error", null, null);
                            resultado = resultado + ". \n " + ex.getMessage();
                            ex.printStackTrace();
                        }
                    }
                } catch (Exception ex) {
                    resultado = messageSource.getMessage("msg.add.receipt.error", null, null);
                    resultado = resultado + ". \n " + ex.getMessage();
                    ex.printStackTrace();
                }
            }else{
                resultado = messageSource.getMessage("msg.receipt.error.mx",null,null)+ " "+ recepcionMx.getTomaMx().getCodigoLab()+" - "+recepcionMx.getTomaMx().getEstadoMx().getDescripcion();
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

    private void validarEnviarSolicitudInfinity(RecepcionMx recepcionMx, User usuario) throws Exception{
        try {
            String idExamenes = equiposProcesamientoService.ordersProcessedInInfinity(recepcionMx.getTomaMx().getIdTomaMx());
            if (idExamenes != null){
                Parametro ipSocketInfinity = parametrosService.getParametroByName("IP_SOCKET_INFINITY");
                Parametro puertoSocketInfinity = parametrosService.getParametroByName("PUERTO_SOCKET_INFINITY");
                TestOrder testOrder = new TestOrder();
                testOrder.setIpServer(ipSocketInfinity.getValor());
                testOrder.setPuertoServer(Integer.valueOf(puertoSocketInfinity.getValor()));
                testOrder.setMessageId(DateUtil.DateToString(new Date(), "yyyyMMddHHmmss"));
                if (recepcionMx.getTomaMx().getIdNotificacion().getCodigoPacienteVIH()!=null){
                    testOrder.setCodExpediente(recepcionMx.getTomaMx().getIdNotificacion().getCodigoPacienteVIH());
                    //testOrder.setCodExpediente("401JMPSM21128901");
                }else{
                    testOrder.setCodExpediente(String.valueOf(recepcionMx.getTomaMx().getIdNotificacion().getPersona().getPersonaId()));
                }

                testOrder.setPersonaId(String.valueOf(recepcionMx.getTomaMx().getIdNotificacion().getPersona().getPersonaId()));

                testOrder.setApellido1(recepcionMx.getTomaMx().getIdNotificacion().getPersona().getPrimerApellido());
                testOrder.setApellido2(recepcionMx.getTomaMx().getIdNotificacion().getPersona().getSegundoApellido());
                testOrder.setNombre1(recepcionMx.getTomaMx().getIdNotificacion().getPersona().getPrimerNombre());
                testOrder.setNombre2(recepcionMx.getTomaMx().getIdNotificacion().getPersona().getSegundoNombre());
                testOrder.setFechaNac(DateUtil.DateToString(recepcionMx.getTomaMx().getIdNotificacion().getPersona().getFechaNacimiento(),"yyyyMMdd"));
                String sexo = recepcionMx.getTomaMx().getIdNotificacion().getPersona().getSexo().getCodigo();
                testOrder.setSexo(sexo.substring(sexo.length()-1, sexo.length()));

                testOrder.setIdMuestra(comunicacionResultadosService.generarIdMuestra()); //Inicia en un millon uno (1000001)
                //testOrder.setIdMuestra("1000001");
                testOrder.setFechaHoraMx(DateUtil.DateToString(recepcionMx.getTomaMx().getFechaHTomaMx(),"yyyyMMddHHmmss"));//"20190722094500"
                //testOrder.setFechaHoraMx("201911111217");
                testOrder.setIdUnidadSalud(String.valueOf(recepcionMx.getTomaMx().getCodUnidadAtencion().getUnidadId()));
                testOrder.setNombreUnidadSalud(recepcionMx.getTomaMx().getCodUnidadAtencion().getNombre());
                testOrder.setIdOrigen("3");
                testOrder.setNombreOrigen("COMPONENTE");
                testOrder.setIdSilais(String.valueOf(recepcionMx.getTomaMx().getCodSilaisAtencion().getEntidadAdtvaId()));
                testOrder.setNombreSilais(recepcionMx.getTomaMx().getCodSilaisAtencion().getNombre());
                testOrder.setIdExamenes(idExamenes);
                testOrder.setUsuarioRegistro(usuario.getUsername());
                testOrder.setIdMuestraLaboratorio(recepcionMx.getTomaMx().getIdTomaMx());
                EquiposProcesamiento equiposProcesamiento = equiposProcesamientoService.getEquipoExamenes(idExamenes);
                if (equiposProcesamiento!=null) testOrder.setEquipo(equiposProcesamiento.getIdEquipo());
                testOrder.setCodigoLab(recepcionMx.getTomaMx().getCodigoLab());
                //Llamar servicio que envia solicitud HL7 al infinity y la registra en base de datos
                try {
                    CallRestServices.crearSolicitud(testOrder);
                }catch (Exception ex){
                    ex.printStackTrace();
                }

                /*
                SimpleMLLPBasedTCPClient.sendHL7TestOrder(testOrder);

                SolicitudHL7 solicitudHL7 = new SolicitudHL7();
                solicitudHL7.setAnulado(false);
                solicitudHL7.setExamenes(testOrder.getIdExamenes());
                solicitudHL7.setFechaRegistro(new Date());
                solicitudHL7.setIdMuestraSecundario(testOrder.getIdMuestra());
                solicitudHL7.setTrama(testOrder.getTrama());
                solicitudHL7.setMuestra(recepcionMx.getTomaMx());
                solicitudHL7.setUsuarioRegistro(usuario);
                comunicacionResultadosService.saveOrUpdateSolicitudHL7(solicitudHL7);
                */
            }
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }

    }
    /***
     * M�todo para recuperar las ordenes de examen registradas para la mx en la recepci�n.
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

    @RequestMapping(value = "getSolicitudes", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    String getSolicitudes(@RequestParam(value = "idTomaMx", required = true) String idTomaMx) throws Exception {
        logger.info("antes getSolicitudes");
        TrasladoMx trasladoActivo = trasladosService.getTrasladoActivoMx(idTomaMx);
        List<DaSolicitudDx> solicitudDxList = tomaMxService.getSolicitudesDxByIdTomaAreaLabUser(idTomaMx, seguridadService.obtenerNombreUsuario());
        List<DaSolicitudEstudio> solicitudEstudios = tomaMxService.getSolicitudesEstudioByIdMxUser(idTomaMx, seguridadService.obtenerNombreUsuario());
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

        logger.info("despues getSolicitudes");
        return SolicutudesToJson(dxMostrar,solicitudEstudios);
    }

    /**
     * M�todo para anular una orden de examen
     * @param request para obtener informaci�n de la petici�n del cliente. Contiene en un par�metro la estructura json del registro a anular
     * @param response para notificar al cliente del resultado de la operaci�n
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
                //si la url esta vacia significa que la validaci�n del login fue exitosa
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
     * M�todo para anular una orden de examen
     * @param request para obtener informaci�n de la petici�n del cliente. Contiene en un par�metro la estructura json del registro a anular
     * @param response para notificar al cliente del resultado de la operaci�n
     * @throws Exception
     */
    @RequestMapping(value = "anularSolicitud", method = RequestMethod.POST)
    protected void anularSolicitud(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.debug("anular solicitud dx o estudio");
        String urlValidacion;
        String idSolicitud = "";
        String causaAnulacion = "";
        String json="";
        String resultado = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            idSolicitud = jsonpObject.get("idSolicitud").getAsString();
            causaAnulacion = jsonpObject.get("causaAnulacion").getAsString();
            DaSolicitudDx solicitudDx = tomaMxService.getSolicitudDxByIdSolicitud(idSolicitud);
            if(solicitudDx!=null){
                try{
                    tomaMxService.bajaSolicitudDx(seguridadService.obtenerNombreUsuario(),idSolicitud, causaAnulacion);
                }catch (Exception ex){
                    logger.error("Error al anular solicitud dx",ex);
                    resultado = messageSource.getMessage("msg.receipt.request.cancel.error2", null, null);
                    resultado = resultado + ". \n " + ex.getMessage();
                }
            }else{
                DaSolicitudEstudio solicitudEst = tomaMxService.getSolicitudEstByIdSolicitud(idSolicitud);
                if(solicitudEst!=null){
                    try{
                        tomaMxService.bajaSolicitudEstudio(seguridadService.obtenerNombreUsuario(),idSolicitud, causaAnulacion);
                    }catch (Exception ex){
                        logger.error("Error al anular solicitud de estudio",ex);
                        resultado = messageSource.getMessage("msg.receipt.request.cancel.error2", null, null);
                        resultado = resultado + ". \n " + ex.getMessage();
                    }
                }else{
                    throw new Exception(messageSource.getMessage("msg.receipt.request.notfound", null, null));
                }
            }

        }catch (Exception ex){
            logger.error("Sucedio un error al anular solicitud",ex);
            resultado = messageSource.getMessage("msg.receipt.request.cancel.error1", null, null);
            resultado = resultado + ". \n " + ex.getMessage();
        } finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("idSolicitud", idSolicitud);
            map.put("causaAnulacion",causaAnulacion);
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    /**
     * M�todo para agregar una solicitud de dx o estudio para una mx
     * @param request para obtener informaci�n de la petici�n del cliente. Contiene en un par�metro la estructura json del registro a agregar
     * @param response para notificar al cliente del resultado de la operaci�n
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "agregarSolicitud", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void agregarSolicitud(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        boolean esEstudio=false;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            esEstudio = jsonpObject.get("esEstudio").getAsBoolean();
            if(esEstudio)  //cuando es estudio tambien se pueden agregar dx de rutina
                resultado = agregarSolicitudEstudio(jsonpObject);
            else
                resultado = agregarSolicitudDx(jsonpObject, request);

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.receipt.request.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("idTomaMx","tmp");
            map.put("idDiagnostico", "tmp");
            map.put("idEstudio", "tmp");
            map.put("esEstudio",String.valueOf(esEstudio));
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    /**
     * M�todo para agregar una orden de examen para una mx
     * @param request para obtener informaci�n de la petici�n del cliente. Contiene en un par�metro la estructura json del registro a agregar
     * @param response para notificar al cliente del resultado de la operaci�n
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
        String nombresCodigosLabMx="";
        String fechasNacimiento="";
        Integer cantMuestras = 0;
        Integer cantMxProc = 0;
        boolean esControlCalidad = false;
        String sarsCov2 = "";
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
                List<DaSolicitudEstudio> solicitudEstudioList = tomaMxService.getSolicitudesEstudioByIdTomaMx(tomaMx.getIdTomaMx());
                boolean esEstudio = solicitudEstudioList.size() > 0;
                tipoRecepcionMx = catalogosService.getTipoRecepcionMx((!esEstudio ? "TPRECPMX|VRT" : "TPRECPMX|EST"));
                RecepcionMx recepcionMx = new RecepcionMx();
                DatosRecepcionMx datosRecepcionMx = recepcionMxService.getRecepcionMxByCodUnicoMxV2(tomaMx.getCodigoUnicoMx(), labUsuario.getCodigo());
                //En algunas ocasiones se encuentra con una recepcion existente pero sin la mx actualizada a recepcionada, entonces lo que vamos a hacer es actualizar la existente, para que no de el error
                //ORA-00001: unique constraint (LABORATORIO.RECEPCION_MX_CODUNICO_LAB) violated
                if (datosRecepcionMx != null && tomaMx.getEstadoMx().getCodigo().equalsIgnoreCase("ESTDMX|ENV")) {
                    recepcionMx.setIdRecepcion(datosRecepcionMx.getIdRecepcion());
                }
                recepcionMx.setUsuarioRecepcion(usuario);
                recepcionMx.setLabRecepcion(labUsuario);
                recepcionMx.setFechaHoraRecepcion(new Timestamp(new Date().getTime()));
                recepcionMx.setTipoMxCk(true);
                recepcionMx.setCantidadTubosCk(true);
                recepcionMx.setTipoRecepcionMx(tipoRecepcionMx);
                recepcionMx.setTomaMx(tomaMx);
                try {
                    //si tiene traslado activo marcarlo como recepcionado
                    TrasladoMx trasladoActivo = trasladosService.getTrasladoActivoMx(idTomaMx);
                    if (trasladoActivo!=null) esControlCalidad = trasladoActivo.isControlCalidad();
                    if (recepcionMx.getIdRecepcion() != null)
                        recepcionMxService.updateRecepcionMx(recepcionMx);
                    else
                        recepcionMxService.addRecepcionMx(recepcionMx);

                    idRecepcion = recepcionMx.getIdRecepcion();
                    //si tiene traslado activo marcarlo como recepcionado
                    if (trasladoActivo!=null) {
                        if (trasladoActivo.isTrasladoExterno()){ //control de calidad, por tanto llega a recepci�n general
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
                    //se setea consecutivo codigo lab. Formato COD_LAB-CONSECUTIVO-ANIO. S�lo para rutinas, que no vengan por traslado externo o control de calidad
                    if (!esEstudio && tomaMx.getCodigoLab()==null && !esControlCalidad) {
                        tomaMx.setCodigoLab(recepcionMxService.obtenerCodigoLab(labUsuario.getCodigo(), 1));
                    }
                    tomaMx.setEstadoMx(estadoMx);
                    tomaMx.setCodigoValidacion(GuidGenerator.next()); //servicios en linea. 17/02/2021
                    try {
                        tomaMxService.updateTomaMx(tomaMx);
                        cantMxProc++;
                        String areaEntrega = "";
                        List<DaSolicitudDx> solicitudDxList = tomaMxService.getSoliDxPrioridadByTomaAndLab(tomaMx.getIdTomaMx(),labUsuario.getCodigo());

                        //area que procesa la solicitud con mayor prioridad
                        if (solicitudDxList.size()> 0)
                            areaEntrega = solicitudDxList.get(0).getCodDx().getArea().getNombre();
                        else {
                            solicitudEstudioList = tomaMxService.getSolicitudesEstudioByIdTomaMx(tomaMx.getIdTomaMx());
                            if (solicitudEstudioList.size()> 0)
                                areaEntrega = solicitudEstudioList.get(0).getTipoEstudio().getArea().getNombre();
                        }

                        //saber si es sars-cov-2
                        Predicate<DaSolicitudDx> byName = new Predicate<DaSolicitudDx>() {
                            @Override
                            public boolean apply(DaSolicitudDx solicitud) {
                                return solicitud.getCodDx().getNombre().toLowerCase().contains("sars-cov-2");
                            }
                        };
                        //si se encuentra el dx poner valor para imprimir 4 copias
                        Collection<DaSolicitudDx> dxSarsCoV2 = FilterLists.filter(solicitudDxList, byName);
                        if (dxSarsCoV2.size() > 0) sarsCov2 = "sars"; else sarsCov2 = sarsCov2+"";

                        String nombreCompleto="";
                        if(tomaMx.getIdNotificacion().getPersona()!=null) {
	                        nombreCompleto = tomaMx.getIdNotificacion().getPersona().getPrimerNombre();
	                        if (tomaMx.getIdNotificacion().getPersona().getSegundoNombre() != null)
	                            nombreCompleto = nombreCompleto + " " + tomaMx.getIdNotificacion().getPersona().getSegundoNombre();
	                        nombreCompleto = nombreCompleto + " " + tomaMx.getIdNotificacion().getPersona().getPrimerApellido();
	                        if (tomaMx.getIdNotificacion().getPersona().getSegundoApellido() != null)
	                            nombreCompleto = nombreCompleto + " " + tomaMx.getIdNotificacion().getPersona().getSegundoApellido();
	                        }
	                        else if(tomaMx.getIdNotificacion().getCodigoPacienteVIH()!=null) {
	                        	nombreCompleto = tomaMx.getIdNotificacion().getCodigoPacienteVIH();
                        }
                        
                        String fechaNac = "";
                        if(tomaMx.getIdNotificacion().getPersona()!=null) {
                        	fechaNac = (tomaMx.getIdNotificacion().getPersona().getFechaNacimiento()!=null?DateUtil.DateToString(tomaMx.getIdNotificacion().getPersona().getFechaNacimiento(),"dd/MM/yyyy"):" ");
                        }
                        if(cantMxProc==1) {
                            codigosLabMx = (esEstudio ? tomaMx.getCodigoUnicoMx() : tomaMx.getCodigoLab()) + "*" + areaEntrega;
                            nombresCodigosLabMx = nombreCompleto;
                            fechasNacimiento = fechaNac;
                        }
                        else {
                            codigosLabMx += "," + (esEstudio ? tomaMx.getCodigoUnicoMx() : tomaMx.getCodigoLab()) + "*" + areaEntrega;
                            nombresCodigosLabMx += "," + nombreCompleto;
                            fechasNacimiento += "," + fechaNac;
                        }
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
            UnicodeEscaper escaper     = UnicodeEscaper.above(127);
            Map<String, String> map = new HashMap<String, String>();
            map.put("strMuestras",strMuestras);
            map.put("mensaje",resultado);
            map.put("esSarsCov2", sarsCov2);
            map.put("cantMuestras", cantMuestras.toString());
            map.put("cantMxProc", cantMxProc.toString());
            map.put("codigosUnicosMx",escaper.translate(codigosLabMx));
            map.put("nombresCodigosLabMx",escaper.translate(nombresCodigosLabMx));
            map.put("fechasNacimiento",fechasNacimiento);
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
            //Areas sobre las que tiene autoridad el usuario
            List<AutoridadArea> areasAutorizadas = autoridadesService.getAutoridadesArea(seguridadService.obtenerNombreUsuario());
            //se obtienen recepciones a recepcionar en lab
            JsonObject jObjectRecepciones = new Gson().fromJson(strRecepciones, JsonObject.class);
            for(int i = 0; i< cantRecepciones;i++) {
                String idRecepcion = jObjectRecepciones.get(String.valueOf(i)).getAsString();
                RecepcionMx recepcionMx = recepcionMxService.getRecepcionMx(idRecepcion);
                if (recepcionMx.getTomaMx().getEstadoMx().getCodigo().equalsIgnoreCase("ESTDMX|TRAS") || recepcionMx.getTomaMx().getEstadoMx().getCodigo().equalsIgnoreCase("ESTDMX|EPLAB")) {
                    //se setean valores a actualizar
                    //recepcionMx.setUsuarioRecepcionLab(usuario);
                    //recepcionMx.setFechaHoraRecepcionLab(new Timestamp(new Date().getTime()));
                    recepcionMx.setCalidadMx(calidadMx);
                    recepcionMx.setCausaRechazo(null);

                    RecepcionMxLab recepcionMxLab = new RecepcionMxLab();
                    recepcionMxLab.setRecepcionMx(recepcionMx);
                    recepcionMxLab.setUsuarioRecepcion(user);
                    recepcionMxLab.setFechaHoraRecepcion(new Timestamp(new Date().getTime()));
                    recepcionMxLab.setFechaHoraRegistro(new Timestamp(new Date().getTime()));
                    TrasladoMx trasladoMxActivo = trasladosService.getTrasladoInternoActivoMxRecepcion(recepcionMx.getTomaMx().getIdTomaMx());
                    boolean actualizarTraslado = false;
                    if (trasladoMxActivo != null) {
                        if (!trasladoMxActivo.isTrasladoExterno()) {
                            if (seguridadService.usuarioAutorizadoArea(seguridadService.obtenerNombreUsuario(), trasladoMxActivo.getAreaDestino().getIdArea())) {
                                recepcionMxLab.setArea(trasladoMxActivo.getAreaDestino());
                                //si tiene traslado activo marcarlo como recepcionado
                                trasladoMxActivo.setRecepcionado(true);
                                trasladoMxActivo.setFechaHoraRecepcion(new Timestamp(new Date().getTime()));
                                trasladoMxActivo.setUsuarioRecepcion(user);
                                actualizarTraslado = true;
                            }
                        }
                    } else {
                        //se si no hay traslado, pero tiene mas de un dx validar si el usuario tiene acceso al de mayor prioridad
                        List<DaSolicitudDx> solicitudDxList = tomaMxService.getSolicitudesDxPrioridadByIdToma(recepcionMx.getTomaMx().getIdTomaMx());
                        if (solicitudDxList.size() > 0) {
                            int prioridad = solicitudDxList.get(0).getCodDx().getPrioridad();
                            for (DaSolicitudDx solicitudDx : solicitudDxList) {
                                if (prioridad == solicitudDx.getCodDx().getPrioridad()) {
                                    if (seguridadService.usuarioAutorizadoArea(seguridadService.obtenerNombreUsuario(), solicitudDx.getCodDx().getArea().getIdArea())) {
                                        recepcionMxLab.setArea(solicitudDx.getCodDx().getArea());
                                        break;
                                    }
                                } else break;
                                prioridad = solicitudDx.getCodDx().getPrioridad();
                            }
                            //deberia ser siempre distinto de null, pero para evitar null pointer
                            if (recepcionMxLab.getArea() == null) {
                                recepcionMxLab.setArea(solicitudDxList.get(0).getCodDx().getArea());
                            }
                        /*if (seguridadService.usuarioAutorizadoArea(seguridadService.obtenerNombreUsuario(), solicitudDxList.get(0).getCodDx().getArea().getIdArea())) {
                            recepcionMxLab.setArea(solicitudDxList.get(0).getCodDx().getArea());
                        }*/
                        } else { //es estudio, se toma el area del estudio. S�lo se permite un estudio por muestra
                            List<DaSolicitudEstudio> solicitudEstudioList = tomaMxService.getSolicitudesEstudioByIdTomaMx(recepcionMx.getTomaMx().getIdTomaMx());
                            if (solicitudEstudioList.size() > 0) {
                                recepcionMxLab.setArea(solicitudEstudioList.get(0).getTipoEstudio().getArea());
                            }
                        }
                    }

                    boolean procesarRecepcion = false;
                    try {
                        //se procesan las ordenes de examen
                        //boolean tieneOrdenesAnuladas = ordenExamenMxService.getOrdenesExamenNoAnuladasByIdMx(recepcionMx.getTomaMx().getIdTomaMx()).size()>0;
                        //Se valida en cada �rea que tenga asociada el usuario si se puede agregar el examen por defecto configurado
                        for (AutoridadArea area : areasAutorizadas) {
                            //List<DaSolicitudDx> solicitudDxList = tomaMxService.getSolicitudesDxByIdTomaArea(recepcionMx.getTomaMx().getIdTomaMx(), recepcionMxLab.getArea().getIdArea(), seguridadService.obtenerNombreUsuario());
                            List<DaSolicitudDx> solicitudDxList = tomaMxService.getSolicitudesDxByIdTomaArea(recepcionMx.getTomaMx().getIdTomaMx(), area.getArea().getIdArea(), seguridadService.obtenerNombreUsuario());
                            if (solicitudDxList != null && solicitudDxList.size() > 0) {
                                for (DaSolicitudDx solicitudDx : solicitudDxList) {
                                    List<Examen_Dx> examenesList = null;
                                    //se obtienen los id de los examenes por defecto
                                    examenesList = examenesService.getExamenesDefectoByIdDx(solicitudDx.getCodDx().getIdDiagnostico(), user.getUsername());
                                    if (examenesList != null) {
                                        //se registran los examenes por defecto
                                        for (Examen_Dx examenTmp : examenesList) {
                                            //s�lo se agrega la oorden si a�n no tiene registrada orden de examen, misma toma, mismo dx, mismo examen y no est� anulado
                                            if (ordenExamenMxService.getOrdExamenNoAnulByIdMxIdDxIdExamen(recepcionMx.getTomaMx().getIdTomaMx(), solicitudDx.getCodDx().getIdDiagnostico(), examenTmp.getExamen().getIdExamen(), seguridadService.obtenerNombreUsuario()).size() <= 0) {
                                                OrdenExamen ordenExamen = new OrdenExamen();
                                                ordenExamen.setSolicitudDx(solicitudDx);
                                                ordenExamen.setCodExamen(examenTmp.getExamen());
                                                ordenExamen.setFechaHOrden(new Timestamp(new Date().getTime()));
                                                ordenExamen.setUsuarioRegistro(user);
                                                ordenExamen.setLabProcesa(labUsuario);
                                                try {
                                                    ordenExamenMxService.addOrdenExamen(ordenExamen);
                                                    procesarRecepcion = true; //si se agreg� al menos un examen se puede procesar la recepci�n
                                                } catch (Exception ex) {
                                                    ex.printStackTrace();
                                                    logger.error("Error al agregar orden de examen", ex);
                                                }
                                            } else {//si ya esta registrada una orden v�lida, entonces se puede procesar
                                                procesarRecepcion = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        List<DaSolicitudEstudio> solicitudEstudioList = tomaMxService.getSolicitudesEstudioByIdTomaMx(recepcionMx.getTomaMx().getIdTomaMx());
                        if (solicitudEstudioList != null && solicitudEstudioList.size() > 0) {
                            //procesar examenes default para cada estudio
                            for (DaSolicitudEstudio solicitudEstudio : solicitudEstudioList) {
                                //se obtienen los id de los examenes por defecto
                                List<Examen_Estudio> examenesEstList = examenesService.getExamenesDefectoByIdEst(solicitudEstudio.getTipoEstudio().getIdEstudio(), user.getUsername());
                                if (examenesEstList != null) {
                                    //se registran los examenes por defecto
                                    for (Examen_Estudio examenTmp : examenesEstList) {
                                        OrdenExamen ordenExamen = new OrdenExamen();
                                        ordenExamen.setSolicitudEstudio(solicitudEstudio);
                                        ordenExamen.setCodExamen(examenTmp.getExamen());
                                        ordenExamen.setFechaHOrden(new Timestamp(new Date().getTime()));
                                        ordenExamen.setUsuarioRegistro(user);
                                        ordenExamen.setLabProcesa(labUsuario);
                                        try {
                                            ordenExamenMxService.addOrdenExamen(ordenExamen);
                                            procesarRecepcion = true; //si se agreg� al menos un examen se puede procesar la recepci�n
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                            logger.error("Error al agregar orden de examen", ex);
                                        }
                                    }
                                }
                                /*String nombreParametroExam = solicitudEstudio.getTipoEstudio().getCodigo();
                                //nombre par�metro que contiene los examenes que se deben aplicar para cada estudio puede estar configurado de 3 maneras:
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
                                    List<CatalogoExamenes> examenesList = examenesService.getExamenesByIdsExamenes(examenesEstudio.getValor());
                                    for (CatalogoExamenes examen : examenesList) {
                                        //s�lo se agrega la oorden si a�n no tiene registrada orden de examen, misma toma, mismo estudio, mismo examen y no est� anulado
                                        if (ordenExamenMxService.getOrdExamenNoAnulByIdMxIdEstIdExamen(recepcionMx.getTomaMx().getIdTomaMx(), solicitudEstudio.getTipoEstudio().getIdEstudio(), examen.getIdExamen()).size() <= 0) {
                                            OrdenExamen ordenExamen = new OrdenExamen();
                                            ordenExamen.setSolicitudEstudio(solicitudEstudio);
                                            ordenExamen.setCodExamen(examen);
                                            ordenExamen.setFechaHOrden(new Timestamp(new Date().getTime()));
                                            ordenExamen.setUsuarioRegistro(user);
                                            ordenExamen.setLabProcesa(labUsuario);
                                            try {
                                                ordenExamenMxService.addOrdenExamen(ordenExamen);
                                                procesarRecepcion = true; //si se agreg� al menos un examen se puede procesar la recepci�n
                                            } catch (Exception ex) {
                                                ex.printStackTrace();
                                                logger.error("Error al agregar orden de examen", ex);
                                            }
                                        } else {//si ya esta registrada una orden v�lida, entonces se puede procesar
                                            procesarRecepcion = true;
                                        }
                                    }
                                } else { // se consulta si hay configuraci�n s�lo por codigo de estudio
                                    examenesEstudio = parametrosService.getParametroByName(solicitudEstudio.getTipoEstudio().getCodigo());
                                    if (examenesEstudio != null) {
                                        List<CatalogoExamenes> examenesList = examenesService.getExamenesByIdsExamenes(examenesEstudio.getValor());
                                        for (CatalogoExamenes examen : examenesList) {
                                            OrdenExamen ordenExamen = new OrdenExamen();
                                            ordenExamen.setSolicitudEstudio(solicitudEstudio);
                                            ordenExamen.setCodExamen(examen);
                                            ordenExamen.setFechaHOrden(new Timestamp(new Date().getTime()));
                                            ordenExamen.setUsuarioRegistro(user);
                                            ordenExamen.setLabProcesa(labUsuario);
                                            try {
                                                ordenExamenMxService.addOrdenExamen(ordenExamen);
                                                procesarRecepcion = true; //si se agreg� al menos un examen se puede procesar la recepci�n
                                            } catch (Exception ex) {
                                                ex.printStackTrace();
                                                logger.error("Error al agregar orden de examen", ex);
                                            }
                                        }
                                    }
                                }*/
                            }
                        }

                        if (procesarRecepcion) {
                            //Si el usuario tiene autoridad sobre mas de un area seg�n los dx solicitados en la muestra, agregar recepcion en lab para cada area
                            List<Area> areasDx = tomaMxService.getAreaSolicitudDxByTomaAndUser(recepcionMx.getTomaMx().getIdTomaMx(), user.getUsername());
                            if (areasAutorizadas.size() > 1) {
                                boolean agregarAreaEstudio =true; //13012020
                                for (AutoridadArea autoridadArea : areasAutorizadas) {
                                    for (Area area : areasDx) {
                                        if (solicitudEstudioList != null && solicitudEstudioList.size() > 0) {//para no duplicar recepcion en laboratorio con misma area en dx y est
                                            if (area.getIdArea().equals(solicitudEstudioList.get(0).getTipoEstudio().getArea().getIdArea()))
                                                agregarAreaEstudio = false;
                                        }
                                        if (autoridadArea.getArea().getIdArea().equals(area.getIdArea())) {
                                            recepcionMxLab.setArea(autoridadArea.getArea());
                                            recepcionMxService.addRecepcionMxLab(recepcionMxLab);
                                        }
                                    }
                                    //es estudio y tiene autoridad para varias areas y no se ha agregado recepcion en laboratorio del area del estudio
                                    if (agregarAreaEstudio && solicitudEstudioList.size()>0){
                                        recepcionMxLab.setArea(solicitudEstudioList.get(0).getTipoEstudio().getArea());
                                        recepcionMxService.addRecepcionMxLab(recepcionMxLab);
                                    }
                                }
                            } else {
                                recepcionMxService.addRecepcionMxLab(recepcionMxLab);
                            }
                            recepcionMxService.updateRecepcionMx(recepcionMx);
                            if (actualizarTraslado)
                                trasladosService.saveTrasladoMx(trasladoMxActivo);
                            //Registrar resultados por defecto. Se toman el concepto y el valor por defecto para los examenes de la tabla catalogo_dx, para que sea mas dinamico. 03/08/2022. Ya no se usan parametros
                            List<DatosSolicitud> solicitudDxList = tomaMxService.getDatosSolicitudesPrioridadByIdToma(recepcionMx.getTomaMx().getIdTomaMx());
                            for (DatosSolicitud solicitudDx : solicitudDxList) {
                                if (!solicitudDx.getAprobada() && solicitudDx.getConceptoDefResulExam() != null && solicitudDx.getValorDefResulExam() != null) {
                                    List<OrdenExamen> ordenesExamen = ordenExamenMxService.getOrdenesExamenNoAnuladasByIdSolicitud(solicitudDx.getIdSolicitud());
                                    boolean soloUnExamen = ordenesExamen.size()==1;
                                    for (OrdenExamen examen : ordenesExamen) {
                                        //no tiene resultado registrado aún
                                        if (resultadosService.getDetallesResultadoActivosByExamen(examen.getIdOrdenExamen()).size() <= 0) {
                                            RespuestaExamen conceptoTmp = respuestasExamenService.getRespuestaByExamenAndConcepto(examen.getCodExamen().getIdExamen(), solicitudDx.getConceptoDefResulExam());
                                            if (conceptoTmp != null) {
                                                DetalleResultado detalleResultado = new DetalleResultado();
                                                detalleResultado.setFechahProcesa(recepcionMxLab.getFechaHoraRecepcion());
                                                detalleResultado.setFechahoraRegistro(recepcionMxLab.getFechaHoraRegistro());
                                                detalleResultado.setValor(solicitudDx.getValorDefResulExam());
                                                detalleResultado.setRespuesta(conceptoTmp);
                                                detalleResultado.setExamen(examen);
                                                detalleResultado.setUsuarioRegistro(user);
                                                if (detalleResultado.getValor() != null && !detalleResultado.getValor().isEmpty())
                                                    resultadosService.addDetalleResultado(detalleResultado);
                                                if (soloUnExamen) guardarResultadoFinal(detalleResultado, false);
                                            }
                                        }
                                    }
                                }
                            }
                            /*
                            //Registrar resultados por defecto como negativo para dx ifi virus respiratorio
                            Parametro conceptoIFINegativo = parametrosService.getParametroByName("CONCEPTO_RES_EXAM_DX_IFIVR");
                            Parametro valorIFINegativo = parametrosService.getParametroByName("NEGATIVO_CONCEPTO_RES_EXAM_DX_IFIVR");
                            Parametro conceptoCovid19Negativo = parametrosService.getParametroByName("CONCEPTO_RES_EXAM_DX_BMCOV19");
                            Parametro valorCovid19Negativo = parametrosService.getParametroByName("NEGATIVO_CONCEPTO_RES_EXAM_DX_BMCOV19");

                            if ((conceptoIFINegativo != null && valorIFINegativo != null) || (conceptoCovid19Negativo != null && valorCovid19Negativo != null)) {
                                List<DatosSolicitud> solicitudDxList = tomaMxService.getDatosSolicitudesPrioridadByIdToma(recepcionMx.getTomaMx().getIdTomaMx());
                                for (DatosSolicitud solicitudDx : solicitudDxList) {
                                    if (!solicitudDx.getAprobada() && solicitudDx.getNombre().toLowerCase().contains("ifi virus respiratorio")) {
                                        List<OrdenExamen> ordenesExamen = ordenExamenMxService.getOrdenesExamenNoAnuladasByIdSolicitud(solicitudDx.getIdSolicitud());
                                        for (OrdenExamen examen : ordenesExamen) {
                                            //no tiene resultado registrado aún
                                            if (resultadosService.getDetallesResultadoActivosByExamen(examen.getIdOrdenExamen()).size() <= 0) {
                                                RespuestaExamen conceptoTmp = respuestasExamenService.getRespuestaByExamenAndConcepto(examen.getCodExamen().getIdExamen(), Integer.valueOf(conceptoIFINegativo.getValor()));
                                                if (conceptoTmp != null) {
                                                    DetalleResultado detalleResultado = new DetalleResultado();
                                                    detalleResultado.setFechahProcesa(recepcionMxLab.getFechaHoraRecepcion());
                                                    detalleResultado.setFechahoraRegistro(recepcionMxLab.getFechaHoraRegistro());
                                                    detalleResultado.setValor(valorIFINegativo.getValor());
                                                    detalleResultado.setRespuesta(conceptoTmp);
                                                    detalleResultado.setExamen(examen);
                                                    detalleResultado.setUsuarioRegistro(user);
                                                    if (detalleResultado.getValor() != null && !detalleResultado.getValor().isEmpty())
                                                        resultadosService.addDetalleResultado(detalleResultado);
                                                }
                                            }
                                        }
                                    } else if (!solicitudDx.getAprobada() && //Biologia Molecular Covid19 (viajeros) o Biologia Molecular SARS-CoV-2(vigilancia). 14/07/2022
                                            (solicitudDx.getNombre().toLowerCase().contains("molecular covid19") || solicitudDx.getNombre().toLowerCase().contains("sars-cov-2"))) {
                                        List<OrdenExamen> ordenesExamen = ordenExamenMxService.getOrdenesExamenNoAnuladasByIdSolicitud(solicitudDx.getIdSolicitud());
                                        boolean soloUnExamen = ordenesExamen.size()==1;
                                        for (OrdenExamen examen : ordenesExamen) {
                                            //no tiene resultado registrado aún
                                            if (resultadosService.getDetallesResultadoActivosByExamen(examen.getIdOrdenExamen()).size() <= 0) {
                                                RespuestaExamen conceptoTmp = respuestasExamenService.getRespuestaByExamenAndConcepto(examen.getCodExamen().getIdExamen(), Integer.valueOf(conceptoCovid19Negativo.getValor()));
                                                if (conceptoTmp != null) {
                                                    DetalleResultado detalleResultado = new DetalleResultado();
                                                    detalleResultado.setFechahProcesa(recepcionMxLab.getFechaHoraRecepcion());
                                                    detalleResultado.setFechahoraRegistro(recepcionMxLab.getFechaHoraRegistro());
                                                    detalleResultado.setValor(valorCovid19Negativo.getValor());
                                                    detalleResultado.setRespuesta(conceptoTmp);
                                                    detalleResultado.setExamen(examen);
                                                    detalleResultado.setUsuarioRegistro(user);
                                                    if (detalleResultado.getValor() != null && !detalleResultado.getValor().isEmpty())
                                                        resultadosService.addDetalleResultado(detalleResultado);
                                                    if (soloUnExamen) guardarResultadoFinal(detalleResultado, false);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            */
                        }
                        //VALIDA SI ES NECESARIO ENVIAR PRE ORDEN A SISTEMA INFINITY
                        try {
                            validarEnviarSolicitudInfinity(recepcionMx, user);
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    } catch (Exception ex) {
                        resultado = messageSource.getMessage("msg.add.receipt.error", null, null);
                        resultado = resultado + ". \n " + ex.getMessage();
                        ex.printStackTrace();
                    }
                    if (!idRecepcion.isEmpty() && procesarRecepcion) {
                        //se tiene que actualizar la tomaMx
                        DaTomaMx tomaMx = tomaMxService.getTomaMxById(recepcionMx.getTomaMx().getIdTomaMx());
                        tomaMx.setEstadoMx(estadoMx);
                        try {
                            tomaMxService.updateTomaMx(tomaMx);
                            cantRecepProc++;
                        } catch (Exception ex) {
                            resultado = messageSource.getMessage("msg.update.order.error", null, null);
                            resultado = resultado + ". \n " + ex.getMessage();
                            ex.printStackTrace();
                        }
                    }
                }else{
                    resultado = messageSource.getMessage("msg.receipt.error.mx",null,null)+ " "+ recepcionMx.getTomaMx().getCodigoLab()+" - "+recepcionMx.getTomaMx().getEstadoMx().getDescripcion();
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

    /***
     * M�todo para registrar resultado de una orden examen como resultado final de la solicitud a la que pertenece dicha orden
     * @param detalleResultado Detalle del resultado del examen
     * @param esUpdate True si es un update del resultado, false si es nuevo
     * @throws Exception
     */
    private void guardarResultadoFinal(DetalleResultado detalleResultado, boolean esUpdate) throws Exception {
        try {
            String idSolicitud="";
            DetalleResultadoFinal resultadoFinal = new DetalleResultadoFinal();
            resultadoFinal.setFechahRegistro(new Timestamp(new Date().getTime()));
            resultadoFinal.setValor(detalleResultado.getValor());
            resultadoFinal.setRespuestaExamen(detalleResultado.getRespuesta());
            if (detalleResultado.getExamen().getSolicitudDx()!=null){
                resultadoFinal.setSolicitudDx(detalleResultado.getExamen().getSolicitudDx());
                idSolicitud = detalleResultado.getExamen().getSolicitudDx().getIdSolicitudDx();
            }
            if (detalleResultado.getExamen().getSolicitudEstudio()!=null) {
                resultadoFinal.setSolicitudEstudio(detalleResultado.getExamen().getSolicitudEstudio());
                idSolicitud =detalleResultado.getExamen().getSolicitudEstudio().getIdSolicitudEstudio();
            }
            resultadoFinal.setUsuarioRegistro(detalleResultado.getUsuarioRegistro());
            DetalleResultadoFinal resFinalRegistrado = null;
            //si es un update del detalle resultado se valida si la solicitud tiene la misma respuesta final para actualizarla
            if (esUpdate) resFinalRegistrado = resultadoFinalService.getDetResBySolicitudAndRespuestaExa(idSolicitud, detalleResultado.getRespuesta().getIdRespuesta());
            if (resFinalRegistrado != null) {
                resultadoFinal.setIdDetalle(resFinalRegistrado.getIdDetalle());
                resultadoFinalService.updateDetResFinal(resultadoFinal);
            } else {
                if (resultadoFinal.getValor() != null && !resultadoFinal.getValor().isEmpty() && !esUpdate) {
                    resultadoFinalService.saveDetResFinal(resultadoFinal);
                }
            }
        }catch (Exception ex)
        {
            throw new Exception(ex);
        }
    }

    private String agregarSolicitudDx(JsonObject jsonpObject, HttpServletRequest request) throws Exception {
        String resultado = "";
        String idTomaMx = "";
        int idDiagnostico = 0;
        idTomaMx = jsonpObject.get("idTomaMx").getAsString();
        idDiagnostico = jsonpObject.get("idDiagnostico").getAsInt();
        //se valida si existe una orden activa para la muestra, el diagn�stico y el examen
        DaSolicitudDx solicitudDx = tomaMxService.getSolicitudesDxByMxDx(idTomaMx, idDiagnostico);
        if (solicitudDx!=null){
            Catalogo_Dx dx = tomaMxService.getDxById(String.valueOf(idDiagnostico));
            resultado = messageSource.getMessage("msg.receipt.add.request.error2", null, null);
            resultado = resultado.replace("{0}", dx.getNombre());
        }else{
            try {
                Laboratorio labUsuario = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());

                DaSolicitudDx soli = new DaSolicitudDx();
                soli.setCodDx(tomaMxService.getDxById(String.valueOf(idDiagnostico)));
                soli.setFechaHSolicitud(new Timestamp(new Date().getTime()));
                Parametro pUsuarioRegistro = parametrosService.getParametroByName("USU_REGISTRO_NOTI_CAESP");
                if (pUsuarioRegistro != null) {
                    long idUsuario = Long.valueOf(pUsuarioRegistro.getValor());
                    soli.setUsarioRegistro(usuarioService.getUsuarioById((int) idUsuario));
                }
                soli.setIdTomaMx(tomaMxService.getTomaMxById(idTomaMx));
                soli.setAprobada(false);
                soli.setLabProcesa(labUsuario);
                soli.setControlCalidad(false);
                soli.setInicial(false);//no viene en la ficha, se agrega en el laboratorio
                tomaMxService.addSolicitudDx(soli);

            } catch (Exception ex) {
                resultado = messageSource.getMessage("msg.receipt.add.request.error", null, null);
                resultado = resultado + ". \n " + ex.getMessage();
                ex.printStackTrace();
            }

        }

        UnicodeEscaper escaper     = UnicodeEscaper.above(127);
        return escaper.translate(resultado);
    }


    private String agregarSolicitudEstudio(JsonObject jsonpObject) throws Exception {
        String resultado = "";
        String idTomaMx = "";
        int idEstudio = 0;
        idTomaMx = jsonpObject.get("idTomaMx").getAsString();
        idEstudio = jsonpObject.get("idEstudio").getAsInt();
        //se valida si existe una orden activa para la muestra, el diagn�stico y el examen
        DaSolicitudEstudio solicitudEstudio = tomaMxService.getSolicitudesEstudioByMxEst(idTomaMx, idEstudio);
        if (solicitudEstudio!=null){
            Catalogo_Estudio estudio = tomaMxService.getEstudioById(idEstudio);
            resultado = messageSource.getMessage("msg.receipt.add.request.error2", null, null);
            resultado = resultado.replace("{0}", estudio.getNombre());
        }else{
            try {
                DaSolicitudEstudio soli = new DaSolicitudEstudio();
                soli.setTipoEstudio(tomaMxService.getEstudioById(idEstudio));
                soli.setFechaHSolicitud(new Timestamp(new Date().getTime()));
                Parametro pUsuarioRegistro = parametrosService.getParametroByName("USU_REGISTRO_NOTI_CAESP");
                if (pUsuarioRegistro != null) {
                    long idUsuario = Long.valueOf(pUsuarioRegistro.getValor());
                    soli.setUsarioRegistro(usuarioService.getUsuarioById((int) idUsuario));
                }
                soli.setIdTomaMx(tomaMxService.getTomaMxById(idTomaMx));
                soli.setAprobada(false);
                tomaMxService.addSolicitudEstudio(soli);

            } catch (Exception ex) {
                resultado = messageSource.getMessage("msg.receipt.add.request.error", null, null);
                resultado = resultado + ". \n " + ex.getMessage();
                ex.printStackTrace();
            }

        }

        UnicodeEscaper escaper     = UnicodeEscaper.above(127);
        return escaper.translate(resultado);
    }

    private String agregarOrdenExamenVigRut(JsonObject jsonpObject, HttpServletRequest request) throws Exception {
        String resultado = "";
        String idTomaMx = "";
        int idDiagnostico = 0;
        int idExamen = 0;
        idTomaMx = jsonpObject.get("idTomaMx").getAsString();
        idDiagnostico = jsonpObject.get("idDiagnostico").getAsInt();
        idExamen = jsonpObject.get("idExamen").getAsInt();
        //se valida si existe una orden activa para la muestra, el diagn�stico y el examen
        List<OrdenExamen> ordenExamenList = ordenExamenMxService.getOrdExamenNoAnulByIdMxIdDxIdExamen(idTomaMx, idDiagnostico, idExamen, seguridadService.obtenerNombreUsuario());
        if (ordenExamenList!=null && ordenExamenList.size()>0) {
            resultado = messageSource.getMessage("msg.receipt.test.exist", null, null);
        }else{
            String username = seguridadService.obtenerNombreUsuario();
            CatalogoExamenes examen = examenesService.getExamenById(idExamen);
            if (autoridadesService.tieneAutoridadExamen(username, idExamen)) {
                User usuario = seguridadService.getUsuario(username);
                Laboratorio labUsuario = seguridadService.getLaboratorioUsuario(username);
                OrdenExamen ordenExamen = new OrdenExamen();
                DaSolicitudDx solicitudDx = tomaMxService.getSolicitudesDxByMxDx(idTomaMx, idDiagnostico);
                if (solicitudDx != null) {
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
                } else {
                    Catalogo_Dx dx = tomaMxService.getDxById(String.valueOf(idDiagnostico));
                    resultado = messageSource.getMessage("msg.receipt.add.test.error2", null, null);
                    resultado = resultado.replace("{0}", dx.getNombre());
                }
            }else{
                resultado = messageSource.getMessage("msg.receipt.test.denied", null, null);
                resultado = resultado.replace("{0}", examen.getNombre());
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
        //se valida si existe una orden activa para la muestra, el diagn�stico y el examen
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
     * M�todo que convierte una lista de tomaMx a un string con estructura Json
     * @param tomaMxList lista con las tomaMx a convertir
     * @return String
     */
    private String tomaMxToJson(List<DaTomaMx> tomaMxList, boolean incluirSolic){
        String jsonResponse;
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice=0;
        Laboratorio labUser = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
        boolean esEstudio;
        for(DaTomaMx tomaMx : tomaMxList){
            boolean agregar = true;
            //traslado activo por traslado externo o control de calidad
            TrasladoMx trasladoMxActivo = trasladosService.getTrasladoActivoMxRecepcion(tomaMx.getIdTomaMx(),true);
            if (trasladoMxActivo==null && tomaMx.getEstadoMx().getCodigo().equalsIgnoreCase("ESTDMX|TRAS")){
                agregar = false; //es traslado interno, no mostrar en los resultado de la b�squeda
            }
            if (agregar) {
                esEstudio = tomaMxService.getSolicitudesEstudioByIdTomaMx(tomaMx.getIdTomaMx()).size() > 0;
                String traslado = messageSource.getMessage("lbl.no", null, null);
                Laboratorio labOrigen = null;
                Map<String, String> map = new HashMap<String, String>();
                //map.put("idOrdenExamen",tomaMx.getIdOrdenExamen());
                map.put("idTomaMx", tomaMx.getIdTomaMx());
                map.put("codigoUnicoMx", esEstudio ? tomaMx.getCodigoUnicoMx() : (tomaMx.getCodigoLab()!=null?tomaMx.getCodigoLab():""));
                //map.put("fechaHoraOrden",DateUtil.DateToString(tomaMx.getFechaHOrden(), "dd/MM/yyyy hh:mm:ss a"));
                map.put("fechaTomaMx", DateUtil.DateToString(tomaMx.getFechaHTomaMx(), "dd/MM/yyyy") +
                        (tomaMx.getHoraTomaMx() != null ? " " + tomaMx.getHoraTomaMx() : ""));
                if (tomaMx.getIdNotificacion().getCodSilaisAtencion() != null) {
                    map.put("codSilais", tomaMx.getIdNotificacion().getCodSilaisAtencion().getNombre());
                } else {
                    map.put("codSilais", "");
                }
                if (tomaMx.getIdNotificacion().getCodUnidadAtencion() != null) {
                    map.put("codUnidadSalud", tomaMx.getIdNotificacion().getCodUnidadAtencion().getNombre());
                } else {
                    map.put("codUnidadSalud", "");
                }
                //notificacion urgente
                if (tomaMx.getIdNotificacion().getUrgente() != null) {
                    map.put("urgente", tomaMx.getIdNotificacion().getUrgente().getValor());
                } else {
                    map.put("urgente", "--");
                }

                //hospitalizado
                String[] arrayHosp = {"13", "17", "11", "16", "10", "12"};
                boolean hosp = false;

                if (tomaMx.getCodUnidadAtencion() != null) {
                    int h = Arrays.binarySearch(arrayHosp, String.valueOf(tomaMx.getCodUnidadAtencion().getTipoUnidad()));
                    hosp = h > 0;

                }

                if (hosp) {
                    map.put("hospitalizado", messageSource.getMessage("lbl.yes", null, null));
                } else {
                    map.put("hospitalizado", messageSource.getMessage("lbl.no", null, null));
                }

                //map.put("estadoOrden", tomaMx.getCodEstado().getValor());
                map.put("separadaMx", (tomaMx.getMxSeparada() != null ? (tomaMx.getMxSeparada() ? "Si" : "No") : ""));
                map.put("cantidadTubos", (tomaMx.getCanTubos() != null ? String.valueOf(tomaMx.getCanTubos()) : ""));
                map.put("tipoMuestra", tomaMx.getCodTipoMx().getNombre());
                //map.put("tipoExamen", tomaMx.getCodExamen().getNombre());
                //Si hay fecha de inicio de sintomas se muestra
                Date fechaInicioSintomas = tomaMx.getIdNotificacion().getFechaInicioSintomas();
                if (fechaInicioSintomas != null)
                    map.put("fechaInicioSintomas", DateUtil.DateToString(fechaInicioSintomas, "dd/MM/yyyy"));
                else
                    map.put("fechaInicioSintomas", " ");
                //Si hay persona
                if (tomaMx.getIdNotificacion().getCodigoPacienteVIH() != null) {
                    map.put("persona", tomaMx.getIdNotificacion().getCodigoPacienteVIH());
                    if (tomaMx.getIdNotificacion().getEmbarazada()!=null) {
                        map.put("embarazada", (tomaMx.getIdNotificacion().getEmbarazada().getCodigo().equalsIgnoreCase("RESP|S") ?
                                messageSource.getMessage("lbl.yes", null, null) : messageSource.getMessage("lbl.no", null, null)));
                    }else{
                        map.put("embarazada", "--");
                    }
                }else if (tomaMx.getIdNotificacion().getPersona() != null) {
                    /// se obtiene el nombre de la persona asociada a la ficha
                    String nombreCompleto = "";
                    nombreCompleto = tomaMx.getIdNotificacion().getPersona().getPrimerNombre();
                    if (tomaMx.getIdNotificacion().getPersona().getSegundoNombre() != null)
                        nombreCompleto = nombreCompleto + " " + tomaMx.getIdNotificacion().getPersona().getSegundoNombre();
                    nombreCompleto = nombreCompleto + " " + tomaMx.getIdNotificacion().getPersona().getPrimerApellido();
                    if (tomaMx.getIdNotificacion().getPersona().getSegundoApellido() != null)
                        nombreCompleto = nombreCompleto + " " + tomaMx.getIdNotificacion().getPersona().getSegundoApellido();
                    map.put("persona", nombreCompleto);
                    //Se calcula la edad
                    int edad = DateUtil.calcularEdadAnios(tomaMx.getIdNotificacion().getPersona().getFechaNacimiento());
                    map.put("edad", String.valueOf(edad));
                    //se obtiene el sexo
                    map.put("sexo", tomaMx.getIdNotificacion().getPersona().getSexo().getValor());
                    if (edad > 12 && tomaMx.getIdNotificacion().getPersona().isSexoFemenino()) {
                        //map.put("embarazada", tomaMxService.estaEmbarazada(tomaMx.getIdNotificacion().getIdNotificacion()));
                        if (tomaMx.getIdNotificacion().getEmbarazada()!=null) {
                            map.put("embarazada", (tomaMx.getIdNotificacion().getEmbarazada().getCodigo().equalsIgnoreCase("RESP|S") ?
                                    messageSource.getMessage("lbl.yes", null, null) : messageSource.getMessage("lbl.no", null, null)));
                        }else{
                            map.put("embarazada", "--");
                        }
                    } else
                        map.put("embarazada", "--");
                } else if (tomaMx.getIdNotificacion().getSolicitante() != null) {
                    map.put("persona", tomaMx.getIdNotificacion().getSolicitante().getNombre());
                    map.put("embarazada", "--");
                } else  {
                    map.put("persona", " ");
                    map.put("embarazada", "--");
                }

                if (trasladoMxActivo != null) {
                    if (trasladoMxActivo.isControlCalidad()) {
                        traslado = messageSource.getMessage("lbl.yes", null, null);
                        labOrigen = trasladoMxActivo.getLaboratorioOrigen();
                    } else if (trasladoMxActivo.isTrasladoExterno()) {
                        labOrigen = trasladoMxActivo.getLaboratorioOrigen();
                    }
                }
                map.put("traslado", traslado);
                map.put("origen", labOrigen != null ? labOrigen.getNombre() : "");
                //s�lo si no es traslado o si es traslado, pero el laboratorio del usuario es distinto del lab de origen del traslado se muestra en los resultados
                //se hace asi porque la consulta de b�squeda esta tomando tanto los envios actuales como los que estan en hist�ricos(se necesita asi en la b�squeda mx)
                if (labOrigen == null || (!labOrigen.getCodigo().equals(labUser.getCodigo()))) {
                    mapResponse.put(indice, map);
                    indice++;
                }
                if (incluirSolic){
                    //se arma estructura de diagn�sticos o estudios
                    List<DaSolicitudDx> solicitudDxList = tomaMxService.getSolicitudesDxByIdToma(tomaMx.getIdTomaMx(), labUser.getCodigo());
                    if (!solicitudDxList.isEmpty()) {
                        int cont = 0;
                        String dxs = "";
                        for (DaSolicitudDx solicitudDx : solicitudDxList) {
                            if (solicitudDx.getControlCalidad()) {
                                cont++;
                                if (cont == solicitudDxList.size()) {
                                    dxs += solicitudDx.getCodDx().getNombre();
                                } else {
                                    dxs += solicitudDx.getCodDx().getNombre() + ", ";
                                }
                            }

                        }
                        map.put("solicitudes", dxs);
                    } else {
                        DaSolicitudEstudio solicitudE = tomaMxService.getSoliEstByCodigo(tomaMx.getCodigoUnicoMx());
                        if(solicitudE != null && solicitudE.getAprobada()){
                            map.put("solicitudes", solicitudE.getTipoEstudio().getNombre());
                        }else{
                            map.put("solicitudes", "");
                        }

                    }
                }
            }
        }
        jsonResponse = new Gson().toJson(mapResponse);
        UnicodeEscaper escaper     = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }

    /**
     * M�todo para convertir una lista de RecepcionMx a un string con estructura Json
     * @param recepcionMxList lista con las Recepciones a convertir
     * @return String
     */
    private String RecepcionMxToJson(List<RecepcionMx> recepcionMxList){
        String jsonResponse="";
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice=0;
        Laboratorio labUser = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
        for(RecepcionMx recepcion : recepcionMxList){
            boolean mostrar = true;
            String traslado = messageSource.getMessage("lbl.no",null,null);
            String areaOrigen = "";
            TrasladoMx trasladoMxActivo = trasladosService.getTrasladoActivoMxRecepcion(recepcion.getTomaMx().getIdTomaMx(),false);
            if (trasladoMxActivo!=null) {
                if (trasladoMxActivo.isTrasladoExterno() || trasladoMxActivo.isControlCalidad()) {
                    if (!seguridadService.usuarioAutorizadoLaboratorio(seguridadService.obtenerNombreUsuario(),trasladoMxActivo.getLaboratorioDestino().getCodigo())){
                        mostrar = false;
                    }else{
                        traslado = messageSource.getMessage("lbl.yes",null,null);
                        areaOrigen = (trasladoMxActivo.getAreaOrigen()!=null?trasladoMxActivo.getAreaOrigen().getNombre():"");
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
                //se si no hay traslado, pero tiene mas de un dx validar si el usuario tiene acceso al de mayor prioridad. Si s�lo hay uno siempre se muestra
                List<DaSolicitudDx> solicitudDxList = tomaMxService.getSoliDxPrioridadByTomaAndLab(recepcion.getTomaMx().getIdTomaMx(), labUser.getCodigo());
                //List<DaSolicitudDx> solicitudDxList = tomaMxService.getSolicitudesDxPrioridadByIdToma(recepcion.getTomaMx().getIdTomaMx());
                if (solicitudDxList.size() > 1) {
                    int prioridad = solicitudDxList.get(0).getCodDx().getPrioridad();
                    for(DaSolicitudDx solicitudDx: solicitudDxList) {
                        if (prioridad==solicitudDx.getCodDx().getPrioridad()) {
                            if (seguridadService.usuarioAutorizadoArea(seguridadService.obtenerNombreUsuario(), solicitudDx.getCodDx().getArea().getIdArea())) {
                                mostrar = true;
                                break;
                            }else{mostrar = false;}
                        }else break;
                        prioridad = solicitudDx.getCodDx().getPrioridad();
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
                map.put("fechaTomaMx", DateUtil.DateToString(recepcion.getTomaMx().getFechaHTomaMx(), "dd/MM/yyyy")+
                        (recepcion.getTomaMx().getHoraTomaMx()!=null?" "+recepcion.getTomaMx().getHoraTomaMx():""));
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
                //map.put("separadaMx", (recepcion.getTomaMx().getMxSeparada() != null ? (recepcion.getTomaMx().getMxSeparada() ? "Si" : "No") : ""));
                //map.put("cantidadTubos", (recepcion.getTomaMx().getCanTubos() != null ? String.valueOf(recepcion.getTomaMx().getCanTubos()) : ""));
                map.put("tipoMuestra", recepcion.getTomaMx().getCodTipoMx().getNombre());
                //map.put("tipoExamen", ordenExamen.getOrdenExamen().getCodExamen().getNombre());
                //map.put("areaProcesa", ordenExamen.getOrdenExamen().getCodExamen().getArea().getNombre());
                //Si hay fecha de inicio de sintomas se muestra
                Date fechaInicioSintomas = recepcion.getTomaMx().getIdNotificacion().getFechaInicioSintomas();
                if (!recepcion.getTomaMx().getIdNotificacion().getCodTipoNotificacion().getCodigo().equalsIgnoreCase("TPNOTI|VIH")) {
                    if (fechaInicioSintomas != null) {
                        map.put("fechaInicioSintomas", DateUtil.DateToString(fechaInicioSintomas, "dd/MM/yyyy"));
                        map.put("dias", String.valueOf(DateUtil.CalcularDiferenciaDiasFechas(fechaInicioSintomas, recepcion.getTomaMx().getFechaHTomaMx()) + 1));
                    } else {
                        map.put("fechaInicioSintomas", " ");
                        map.put("dias", "");
                    }
                }else {
                    map.put("fechaInicioSintomas", " ");
                    //para VIH los dias son de la fecha de toma hasta la fecha actual en que se está recepcionando en lab
                    map.put("dias", String.valueOf(DateUtil.CalcularDiferenciaDiasFechas(recepcion.getTomaMx().getFechaHTomaMx(), new Date()) + 1));
                }

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
                    /*map.put("edad",String.valueOf(edad));
                    //se obtiene el sexo
                    map.put("sexo",recepcion.getTomaMx().getIdNotificacion().getPersona().getSexo().getValor());
                    */
                    if(edad > 12 && recepcion.getTomaMx().getIdNotificacion().getPersona().isSexoFemenino()){
                        //map.put("embarazada", tomaMxService.estaEmbarazada(recepcion.getTomaMx().getIdNotificacion().getIdNotificacion()));
                        if (recepcion.getTomaMx().getIdNotificacion().getEmbarazada()!=null)
                            map.put("embarazada",(recepcion.getTomaMx().getIdNotificacion().getEmbarazada().getCodigo().equalsIgnoreCase("RESP|S")?
                                    messageSource.getMessage("lbl.yes",null,null):messageSource.getMessage("lbl.no",null,null)));
                        else map.put("embarazada",messageSource.getMessage("lbl.no",null,null));
                    }else
                        map.put("embarazada","--");
                } else if (recepcion.getTomaMx().getIdNotificacion().getSolicitante() != null) {
                    map.put("persona", recepcion.getTomaMx().getIdNotificacion().getSolicitante().getNombre());
                    map.put("embarazada","--");
                }else if (recepcion.getTomaMx().getIdNotificacion().getCodigoPacienteVIH() != null) {
                	map.put("persona", recepcion.getTomaMx().getIdNotificacion().getCodigoPacienteVIH());
                	if (recepcion.getTomaMx().getIdNotificacion().getEmbarazada()!=null) {
                        map.put("embarazada", (recepcion.getTomaMx().getIdNotificacion().getEmbarazada().getCodigo().equalsIgnoreCase("RESP|S") ?
                                messageSource.getMessage("lbl.yes", null, null) : messageSource.getMessage("lbl.no", null, null)));
                    }else{
                        map.put("embarazada", "--");
                    }
                }else {
                    map.put("persona", " ");
                    map.put("embarazada","--");
                }
                map.put("traslado",traslado);
                map.put("origen",areaOrigen);

                //se arma estructura de diagn�sticos o estudios
                List<DaSolicitudDx> solicitudDxList = tomaMxService.getSolicitudesDxByIdToma(recepcion.getTomaMx().getIdTomaMx(), labUser.getCodigo());
                DaSolicitudEstudio solicitudE = tomaMxService.getSoliEstByCodigo(recepcion.getTomaMx().getCodigoUnicoMx());
                String dxs = "";
                if (!solicitudDxList.isEmpty()) {
                    int cont = 0;

                    for (DaSolicitudDx solicitudDx : solicitudDxList) {
                        cont++;
                        if (cont == solicitudDxList.size()) {
                            dxs += solicitudDx.getCodDx().getNombre();
                        } else {
                            dxs += solicitudDx.getCodDx().getNombre() + ", ";
                        }

                    }
                    map.put("solicitudes", dxs);
                }
                if(solicitudE != null){
                    map.put("solicitudes",(dxs.isEmpty()?solicitudE.getTipoEstudio().getNombre():dxs+", "+solicitudE.getTipoEstudio().getNombre()));
                }else{
                    map.put("solicitudes", dxs);
                }



                mapResponse.put(indice, map);
                indice++;
            }
        }
        jsonResponse = new Gson().toJson(mapResponse);
        //escapar caracteres especiales, escape de los caracteres con valor num�rico mayor a 127
        UnicodeEscaper escaper = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }

    /**
     * M�todo para convertir una lista de Ordenes Examen a un string con estructura Json
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
        //escapar caracteres especiales, escape de los caracteres con valor num�rico mayor a 127
        UnicodeEscaper escaper     = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }

    /**
     * M�todo para convertir una lista de Ordenes Examen a un string con estructura Json
     * @param dxList lista con las solicitudes de diagn�sticos a convertir
     * @param estudioList lista con las solicitudes de estudio a convertir
     * @return String
     * @throws UnsupportedEncodingException
     */
    private String SolicutudesToJson(List<DaSolicitudDx> dxList, List<DaSolicitudEstudio> estudioList) throws UnsupportedEncodingException {
        String jsonResponse="";
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice=0;
        boolean agregarExamenDx = true;
        List<Area> areasUsuario = autoridadesService.getAreasUsuario(seguridadService.obtenerNombreUsuario());
        for(DaSolicitudDx dx : dxList){
            Map<String, String> map = new HashMap<String, String>();
            map.put("idTomaMx", dx.getIdTomaMx().getIdTomaMx());
            map.put("idSolicitud", dx.getIdSolicitudDx());
            map.put("nombre", dx.getCodDx().getNombre());
            map.put("nombreAreaPrc", dx.getCodDx().getArea().getNombre());
            map.put("fechaSolicitud", DateUtil.DateToString(dx.getFechaHSolicitud(), "dd/MM/yyyy hh:mm:ss a"));
            map.put("tipo", messageSource.getMessage("lbl.routine",null,null));
            if (dx.getControlCalidad())
                map.put("cc", messageSource.getMessage("lbl.yes", null, null));
            else
                map.put("cc", messageSource.getMessage("lbl.no", null, null));
            if (areasUsuario.contains(dx.getCodDx().getArea()))
                map.put("permiso", "true");
            else
                map.put("permiso", "false");

            mapResponse.put(indice, map);
            indice ++;
        }

        for(DaSolicitudEstudio estudio : estudioList)
        {
            Map<String, String> map = new HashMap<String, String>();
            map.put("idTomaMx", estudio.getIdTomaMx().getIdTomaMx());
            map.put("idSolicitud", estudio.getIdSolicitudEstudio() );
            map.put("nombre", estudio.getTipoEstudio().getNombre());
            map.put("nombreAreaPrc", estudio.getTipoEstudio().getArea().getNombre());
            map.put("fechaSolicitud", DateUtil.DateToString(estudio.getFechaHSolicitud(), "dd/MM/yyyy hh:mm:ss a"));
            map.put("tipo",messageSource.getMessage("lbl.study",null,null));
            map.put("cc",messageSource.getMessage("lbl.no",null,null));
            mapResponse.put(indice, map);
            indice ++;
        }
        jsonResponse = new Gson().toJson(mapResponse);
        //escapar caracteres especiales, escape de los caracteres con valor num�rico mayor a 127
        UnicodeEscaper escaper     = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }

    /**
     * M�todo para convertir estructura Json que se recibe desde el cliente a FiltroMx para realizar b�squeda de Mx(Vigilancia) y Recepci�n Mx(Laboratorio)
     * @param strJson String con la informaci�n de los filtros
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
        String codigoVIH = null;
        Date fechaInicioProc = null;
        Date fechaFinProc = null;
        Date fechaInicioAprob = null;
        Date fechaFinAprob = null;
        String idioma = null;
        String modalidad = null;

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
        if (jObjectFiltro.get("codigoVIH") != null && !jObjectFiltro.get("codigoVIH").getAsString().isEmpty())
        	codigoVIH = jObjectFiltro.get("codigoVIH").getAsString();
        if (jObjectFiltro.get("fecInicioProc") != null && !jObjectFiltro.get("fecInicioProc").getAsString().isEmpty()) {
            String strFechaInicioProc = jObjectFiltro.get("fecInicioProc").getAsString();
            if (strFechaInicioProc.length() <= 10) //no trae hora
                fechaInicioProc = DateUtil.StringToDate(strFechaInicioProc + " 00:00:00");
            else
                fechaInicioProc = DateUtil.StringToDate(strFechaInicioProc);
        }
        if (jObjectFiltro.get("fecFinProc") != null && !jObjectFiltro.get("fecFinProc").getAsString().isEmpty()) {
            String strFecFinProc = jObjectFiltro.get("fecFinProc").getAsString();
            if (strFecFinProc.length() <= 10) //no trae hora
                fechaFinProc = DateUtil.StringToDate(strFecFinProc + " 23:59:59");
            else
                fechaFinProc = DateUtil.StringToDate(strFecFinProc);
        }
        if (jObjectFiltro.get("fechaInicioAprob") != null && !jObjectFiltro.get("fechaInicioAprob").getAsString().isEmpty()) {
            String strFechaInicioAprob = jObjectFiltro.get("fechaInicioAprob").getAsString();
            if (strFechaInicioAprob.length() <= 10) //no trae hora
                fechaInicioAprob = DateUtil.StringToDate(strFechaInicioAprob + " 00:00:00");
            else
                fechaInicioAprob = DateUtil.StringToDate(strFechaInicioAprob);
        }
        if (jObjectFiltro.get("fechaFinAprob") != null && !jObjectFiltro.get("fechaFinAprob").getAsString().isEmpty()) {
            String strFechaFinAprob = jObjectFiltro.get("fechaFinAprob").getAsString();
            if (strFechaFinAprob.length() <= 10) //no trae hora
                fechaFinAprob = DateUtil.StringToDate(strFechaFinAprob + " 23:59:59");
            else
                fechaFinAprob = DateUtil.StringToDate(strFechaFinAprob);
        }
        if (jObjectFiltro.get("idioma") != null && !jObjectFiltro.get("idioma").getAsString().isEmpty())
            idioma = jObjectFiltro.get("idioma").getAsString();
        if (jObjectFiltro.get("modalidad") != null && !jObjectFiltro.get("modalidad").getAsString().isEmpty())
            modalidad = jObjectFiltro.get("modalidad").getAsString();

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
        if (!Boolean.valueOf(esLab)) { //es recepci�n general
            filtroMx.setCodEstado("ESTDMX|ENV"); // s�lo las enviadas
        } else { //es recepci�n en laboratorio
            filtroMx.setCodEstado("ESTDMX|EPLAB"); // s�lo las enviadas para procesar en laboratorio
            filtroMx.setIncluirMxInadecuada(true);
            filtroMx.setSolicitudAprobada(false);
        }
        filtroMx.setCodigoUnicoMx(codigoUnicoMx);
        filtroMx.setNombreUsuario(seguridadService.obtenerNombreUsuario());
        filtroMx.setIncluirTraslados(true);
        filtroMx.setControlCalidad(controlCalidad);
        filtroMx.setCodigoVIH(codigoVIH);
        filtroMx.setFechaInicioProcesamiento(fechaInicioProc);
        filtroMx.setFechaFinProcesamiento(fechaFinProc);
        filtroMx.setIdioma(idioma);
        filtroMx.setModalidad(modalidad);
        filtroMx.setFechaInicioAprob(fechaInicioAprob);
        filtroMx.setFechaFinAprob(fechaFinAprob);

        return filtroMx;
    }

    /**
     * M�todo para generar un string alfanum�rico de 8 caracteres, que se usar� como c�digo �nico de muestra
     * @return String codigoUnicoMx
     */
    private String generarCodigoUnicoMx(){
        RecepcionMx validaRecepcionMx;
        //Se genera el c�digo
        String codigoUnicoMx = StringUtil.getCadenaAlfanumAleatoria(8);
        //Se consulta BD para ver si existe recepci�n con muestra que tenga mismo c�digo
        Laboratorio laboratorioUsuario = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
        validaRecepcionMx = recepcionMxService.getRecepcionMxByCodUnicoMx(codigoUnicoMx,(laboratorioUsuario.getCodigo()!=null?laboratorioUsuario.getCodigo():""));
        //si existe, de manera recursiva se solicita un nuevo c�digo
        if (validaRecepcionMx!=null){
            codigoUnicoMx = generarCodigoUnicoMx();
        }
        //si no existe se retorna el �ltimo c�digo generado
        return codigoUnicoMx;
    }


    /**
     * M�todo que se llama al entrar a la opci�n de menu "Recepci�n Mx Vigilancia". Se encarga de inicializar las listas para realizar la b�squeda de envios de Mx
     * @param request para obtener informaci�n de la petici�n del cliente
     * @return ModelAndView
     * @throws Exception
     */
    @RequestMapping(value = "printResults", method = RequestMethod.GET)
    public ModelAndView initPrintResultsForm(HttpServletRequest request) throws Exception {
        logger.debug("inicia formulario para imprimir resultados aprobados y liberados para los pacientes");
        Laboratorio labUser = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
        ModelAndView mav = new ModelAndView();
        Parametro parametro = parametrosService.getParametroByName("CODIGOS_LAB_VIAJEROS");
        boolean permitirFiltroViajero = parametro != null && labUser != null && parametro.getValor().contains(labUser.getCodigo());
        mav.addObject("permitirFiltroViajero", permitirFiltroViajero);
        mav.setViewName("recepcionMx/printResults");
        return mav;
    }

    @RequestMapping(value = "searchResults", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String fetchAprovedResultsJson(@RequestParam(value = "strFilter", required = true) String filtro) throws Exception{
        logger.info("Obteniendo las solicitudes con resultados aprobados");
        FiltroMx filtroMx= jsonToFiltroMx(filtro);
        filtroMx.setSolicitudAprobada(true);
        filtroMx.setIncluirMxInadecuada(true);
        filtroMx.setCodEstado("ESTDMX|RCLAB");

        List<DaTomaMx> tomaMxList = tomaMxService.getTomaMxByFiltro(filtroMx);
        return tomaMxPrintToJson(tomaMxList, filtroMx);
    }

    /**
     * M�todo que convierte una lista de tomaMx a un string con estructura Json
     * @param tomaMxList lista con las tomaMx a convertir
     * @return String
     */
    private String tomaMxPrintToJson(List<DaTomaMx> tomaMxList, FiltroMx filtroMx) throws Exception{
        String jsonResponse;
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice=0;
        String userName = seguridadService.obtenerNombreUsuario();
        Laboratorio labUser = seguridadService.getLaboratorioUsuario(userName);
        boolean esEstudio;
        boolean mostrar = false;
        boolean usuarioAutorizadoCovid19 = seguridadService.usuarioAutorizadoCovid19(seguridadService.obtenerNombreUsuario());

        for(DaTomaMx tomaMx : tomaMxList){
            Map<String, String> map = new HashMap<String, String>();
            //se arma estructura de diagn�sticos o estudios
            List<Solicitud> solicitudDxList = tomaMxService.getSolicitudesDxByIdTomaV2(tomaMx.getIdTomaMx(), labUser.getCodigo());
            Authority esAnalista = usuarioService.getAuthority(userName, "ROLE_ANALISTA");
            String dxs = "";
            if (!solicitudDxList.isEmpty()) {
                int cont = 0;
                for (Solicitud solicitudDx : solicitudDxList) {
                    if (solicitudDx.getAprobada()) {
                        cont++;
                        if (cont == solicitudDxList.size()) {
                            dxs += solicitudDx.getNombre();
                        } else {
                            dxs += solicitudDx.getNombre() + ", ";
                        }
                        //Si no es analista, es recepcionista mostrar muestra o si es analista y tiene autoridad en al menos uno de los dx solicitados mostrar muestra
                        if (esAnalista==null || (esAnalista!=null && seguridadService.usuarioAutorizadoArea(userName, solicitudDx.getIdArea()) && !mostrar)){
                            mostrar = true;
                        }
                        if (mostrar && solicitudDx.getNombre().toLowerCase().contains("covid")) { //si es covid validar si usuario tiene autorizado ver ese Dx
                            mostrar = usuarioAutorizadoCovid19;
                            if (usuarioAutorizadoCovid19) {
                                boolean filtrarIdioma = filtroMx.getIdioma() != null && !filtroMx.getIdioma().isEmpty();
                                boolean filtrarModalidad = filtroMx.getModalidad() != null && !filtroMx.getModalidad().isEmpty();
                                if (filtrarIdioma || filtrarModalidad) {
                                    DatosCovidViajeroDTO datosCovidViajeroDTO = datosSolicitudService.getDatosCovidViajero(solicitudDx.getIdSolicitudDx(), null);
                                    if (mostrar && filtrarIdioma) {
                                        mostrar = ((filtroMx.getIdioma().equalsIgnoreCase("ES") && (datosCovidViajeroDTO.getIdioma() != null && datosCovidViajeroDTO.getIdioma().toLowerCase().contains("esp")))
                                                || (filtroMx.getIdioma().equalsIgnoreCase("EN") && (datosCovidViajeroDTO.getIdioma() != null && datosCovidViajeroDTO.getIdioma().toLowerCase().contains("ing"))));

                                    }
                                    if (mostrar && filtrarModalidad) {
                                        mostrar = ((filtroMx.getModalidad().equalsIgnoreCase("RF") && (datosCovidViajeroDTO.getModalidad() != null && datosCovidViajeroDTO.getModalidad().toLowerCase().contains("ico")))
                                                || (filtroMx.getModalidad().equalsIgnoreCase("RE") && (datosCovidViajeroDTO.getModalidad() != null && datosCovidViajeroDTO.getModalidad().toLowerCase().contains("nea"))));
                                    }
                                }
                            }
                        }
                    }
                }
                map.put("solicitudes", dxs);
            }
                DaSolicitudEstudio solicitudE = tomaMxService.getSoliEstByCodigo(tomaMx.getCodigoUnicoMx());
                if(solicitudE != null && solicitudE.getAprobada()){
                    map.put("solicitudes",(dxs.isEmpty()?solicitudE.getTipoEstudio().getNombre():dxs+", "+solicitudE.getTipoEstudio().getNombre()));
                    if (esAnalista==null || (esAnalista!=null && seguridadService.usuarioAutorizadoArea(userName, solicitudE.getTipoEstudio().getArea().getIdArea()) && !mostrar)){
                        mostrar = true;
                    }
                }else{
                    map.put("solicitudes", dxs);
                }

            if (mostrar) {
                esEstudio = tomaMxService.tieneEstudiosByIdTomaMx(tomaMx.getIdTomaMx());
                //map.put("idOrdenExamen",tomaMx.getIdOrdenExamen());
                map.put("idTomaMx", tomaMx.getIdTomaMx());
                map.put("codigoUnicoMx", esEstudio ? tomaMx.getCodigoUnicoMx() : tomaMx.getCodigoLab());
                //map.put("fechaHoraOrden",DateUtil.DateToString(tomaMx.getFechaHOrden(), "dd/MM/yyyy hh:mm:ss a"));
                map.put("fechaTomaMx", DateUtil.DateToString(tomaMx.getFechaHTomaMx(), "dd/MM/yyyy") +
                        (tomaMx.getHoraTomaMx() != null ? " " + tomaMx.getHoraTomaMx() : ""));
                if (tomaMx.getIdNotificacion().getCodSilaisAtencion() != null) {
                    map.put("codSilais", tomaMx.getIdNotificacion().getCodSilaisAtencion().getNombre());
                } else {
                    map.put("codSilais", "");
                }
                map.put("tipoMuestra", tomaMx.getCodTipoMx().getNombre());
                map.put("tipoNotificacion", tomaMx.getIdNotificacion().getCodTipoNotificacion().getValor());

                //Si hay persona
                if (tomaMx.getIdNotificacion().getPersona() != null) {
                    /// se obtiene el nombre de la persona asociada a la ficha
                    String nombreCompleto = "";
                    nombreCompleto = tomaMx.getIdNotificacion().getPersona().getPrimerNombre();
                    if (tomaMx.getIdNotificacion().getPersona().getSegundoNombre() != null)
                        nombreCompleto = nombreCompleto + " " + tomaMx.getIdNotificacion().getPersona().getSegundoNombre();
                    nombreCompleto = nombreCompleto + " " + tomaMx.getIdNotificacion().getPersona().getPrimerApellido();
                    if (tomaMx.getIdNotificacion().getPersona().getSegundoApellido() != null)
                        nombreCompleto = nombreCompleto + " " + tomaMx.getIdNotificacion().getPersona().getSegundoApellido();
                    map.put("persona", nombreCompleto);
                } else if (tomaMx.getIdNotificacion().getSolicitante() != null) {
                    map.put("persona", tomaMx.getIdNotificacion().getSolicitante().getNombre());
                } else if (tomaMx.getIdNotificacion().getCodigoPacienteVIH() != null) {
                	map.put("persona", tomaMx.getIdNotificacion().getCodigoPacienteVIH());
                	if (tomaMx.getIdNotificacion().getEmbarazada()!=null) {
                        map.put("embarazada", (tomaMx.getIdNotificacion().getEmbarazada().getCodigo().equalsIgnoreCase("RESP|S") ?
                                messageSource.getMessage("lbl.yes", null, null) : messageSource.getMessage("lbl.no", null, null)));
                    }else{
                        map.put("embarazada", "--");
                    }
                }else {
                    map.put("persona", " ");
                }

                mapResponse.put(indice, map);
                indice++;
            }
        }
        jsonResponse = new Gson().toJson(mapResponse);
        UnicodeEscaper escaper     = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }

    @RequestMapping(value = "resultsPDF", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public
    @ResponseBody
    ResponseEntity<String> expToPDF(@RequestBody ExcelVigilanciaRequest filtros) throws IOException, COSVisitorException, ParseException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String languaje = filtros.getLanguaje();
        PDDocument doc = new PDDocument();
        String formatoFecha = "dd/MM/yyyy";
        String formatoFechaHora = "dd/MM/yyyy hh:mm:ss a";
        Locale locale = new Locale("es", "NI");
        Laboratorio labProcesa = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
        String response = null;
        Parametro urlServiciosEnLinea = parametrosService.getParametroByName("URL_VALIDACION_SE");
        String userName = seguridadService.obtenerNombreUsuario();
        try {

            String[] codigos = filtros.getCodes().split(",");
            Arrays.sort(codigos);//ordenar codigos de mx para que salgan ordenados en el pdf. 20/10/2020
            for (String code : codigos) {
                if (!code.isEmpty()) {
                    //DaTomaMx tomaMx = tomaMxService.getTomaMxByCodLab(code);
                    ResultadoVigilancia tomaMx = tomaMxService.getDatosMx(code);
                    //Prepare the document.
                    if (tomaMx != null) {
                        boolean esMxViajeroCovid = tomaMxService.esMxViajeroCovid(code);
                        if (esMxViajeroCovid) {
                            if (languaje == null || languaje.isEmpty() || codigos.length > 1) //es distinto de null cuando se indica explicitamente el idioma desde las banderitas de cada registro O es mas de una mx, validar el idioma de cada una
                                languaje = datosSolicitudService.getIdiomaResultadoViajero(tomaMx.getIdTomaMx());
                            if (languaje.equalsIgnoreCase("EN")){
                                locale = Locale.ENGLISH;
                                formatoFecha = "MM/dd/yyyy";
                                formatoFechaHora = "MM/dd/yyyy hh:mm:ss a";
                            } else {
                                formatoFecha = "dd/MM/yyyy";
                                formatoFechaHora = "dd/MM/yyyy hh:mm:ss a";
                                locale = new Locale("es");
                            }
                        }
                        String fechaImpresion = DateUtil.DateToString(new Date(), formatoFechaHora); //volver a poner la hora. Andrea 18/11/2021 //solo poner fecha, sin hora. Sonia 28/10/2020
                        List<Area> areaDxList = tomaMxService.getAreaSolicitudesAprobByTomaAndUser(tomaMx.getIdTomaMx(), seguridadService.obtenerNombreUsuario());
                        Authority esRecepcionista = usuarioService.getAuthority(userName, "ROLE_RECEPCION");
                        float yPosicionExamen = 0;
                        for (Area area : areaDxList) {
                            if (esRecepcionista != null || seguridadService.usuarioAutorizadoArea(userName, area.getIdArea())) {
                                PDPage page = GeneralUtils.addNewPage(doc);
                                PDPageContentStream stream = new PDPageContentStream(doc, page);

                                String nombreDireccion = "";
                                Direccion direccion = organizationChartService.getDireccionesByLab(labProcesa.getCodigo(), area.getIdArea());
                                if (direccion != null) nombreDireccion = direccion.getNombre();
                                if (esMxViajeroCovid && locale == Locale.ENGLISH) nombreDireccion = messageSource.getMessage("lbl.microbiology", null, locale);
                                //dibujar encabezado y pie de pagina
                                GeneralUtils.drawHeaderAndFooter(stream, doc, 750, 590, 80, 600, 70);

                                String pageNumber = String.valueOf(doc.getNumberOfPages());
                                GeneralUtils.drawTEXT(pageNumber, 15, 550, stream, 10, PDType1Font.HELVETICA_BOLD);

                                drawInfoLab(stream, page, labProcesa, esMxViajeroCovid, locale);

                                float y = 648;
                                //nombre del reporte
                                float xCenter;
                                xCenter = GeneralUtils.centerTextPositionX(page, PDType1Font.HELVETICA_BOLD_OBLIQUE, 12, nombreDireccion);
                                GeneralUtils.drawTEXT(nombreDireccion, y, xCenter, stream, 12, PDType1Font.HELVETICA_BOLD_OBLIQUE);
                                y = y - 15;
                                xCenter = GeneralUtils.centerTextPositionX(page, PDType1Font.HELVETICA_BOLD_OBLIQUE, 11, messageSource.getMessage("lbl.lab.result", null, locale).toUpperCase());
                                GeneralUtils.drawTEXT(messageSource.getMessage("lbl.lab.result", null, locale).toUpperCase(), y, xCenter, stream, 11, PDType1Font.HELVETICA_BOLD_OBLIQUE);
                                y = y - 15;
                                xCenter = GeneralUtils.centerTextPositionX(page, PDType1Font.HELVETICA_BOLD_OBLIQUE, 11, (esMxViajeroCovid && locale == Locale.ENGLISH ? messageSource.getMessage("lbl.sars.cov2", null, locale) : area.getNombre().toUpperCase()));
                                GeneralUtils.drawTEXT((esMxViajeroCovid && locale == Locale.ENGLISH ? messageSource.getMessage("lbl.sars.cov2", null, locale) : area.getNombre().toUpperCase()), y, xCenter, stream, 11, PDType1Font.HELVETICA_BOLD_OBLIQUE);
                                y = y - 20;

                                String nombres = "";
                                String apellidos = "";
                                String edad = "";
                                if (!tomaMx.getCodigoTipoNoti().equalsIgnoreCase("TPNOTI|VIH")) {
                                    if (tomaMx.getPrimerNombre() != null) {
                                        nombres = tomaMx.getPrimerNombre();
                                        if (tomaMx.getSegundoNombre() != null)
                                            nombres = nombres + " " + tomaMx.getSegundoNombre();

                                        apellidos = tomaMx.getPrimerApellido();
                                        if (tomaMx.getSegundoApellido() != null)
                                            apellidos = apellidos + " " + tomaMx.getSegundoApellido();
                                    } else if (tomaMx.getCodigoVIH() != null) {
                                        nombres = tomaMx.getCodigoVIH();
                                    } /*else {
                                            nombres = tomaMx.getIdNotificacion().getSolicitante().getNombre();
                                        }*/
                                } else {
                                    if (tomaMx.getCodigoVIH() != null) {
                                        nombres = tomaMx.getCodigoVIH();
                                    } else {
                                        if (tomaMx.getPrimerNombre() != null) {
                                            nombres = tomaMx.getPrimerNombre();
                                            if (tomaMx.getSegundoNombre() != null)
                                                nombres = nombres + " " + tomaMx.getSegundoNombre();

                                            apellidos = tomaMx.getPrimerApellido();
                                            if (tomaMx.getSegundoApellido() != null)
                                                apellidos = apellidos + " " + tomaMx.getSegundoApellido();
                                        }
                                    }
                                }

                                String[] arrEdad = DateUtil.calcularEdad(tomaMx.getFechaNacimiento(), new Date()).split("/");
                                if (arrEdad[0] != null) edad = arrEdad[0] + (locale != Locale.ENGLISH ? " A" : " Y");
                                if (arrEdad[1] != null) edad = edad + " " + arrEdad[1] + " M";

                                //datos personales
                                GeneralUtils.drawTEXT(messageSource.getMessage("lbl.code", null, locale) + ": ", y, 60, stream, 11, PDType1Font.HELVETICA);
                                GeneralUtils.drawTEXT(code, y, (locale == Locale.ENGLISH ? 130 : 120), stream, 11, PDType1Font.HELVETICA_BOLD);
                                if (!esMxViajeroCovid) {
                                    GeneralUtils.drawTEXT(messageSource.getMessage("lbl.file.number", null, locale) + ": ", y, 300, stream, 11, PDType1Font.HELVETICA);
                                    String numExpediente = (tomaMx.getExpediente() != null ? tomaMx.getExpediente() : notificacionService.getNumExpediente(tomaMx.getIdNotificacion()));
                                    GeneralUtils.drawTEXT(numExpediente, y, 420, stream, 11, PDType1Font.HELVETICA_BOLD);
                                } else {
                                    GeneralUtils.drawTEXT(messageSource.getMessage("lbl.identificacion", null, locale) + ": ", y, 300, stream, 11, PDType1Font.HELVETICA);
                                    GeneralUtils.drawTEXT(datosSolicitudService.getIdentificacionViajero(tomaMx.getIdTomaMx(), tomaMx.getIdentificacion()), y, 380, stream, 11, PDType1Font.HELVETICA_BOLD);
                                }
                                y = y - 15;
                                GeneralUtils.drawTEXT(messageSource.getMessage("lbl.names", null, locale) + ":", y, 60, stream, 11, PDType1Font.HELVETICA);
                                GeneralUtils.drawTEXT(nombres, y, (locale == Locale.ENGLISH ? 130 : 120), stream, 11, PDType1Font.HELVETICA_BOLD);
                                GeneralUtils.drawTEXT(messageSource.getMessage("lbl.lastnames", null, locale) + ":", y, 300, stream, 11, PDType1Font.HELVETICA);
                                GeneralUtils.drawTEXT(apellidos, y, (locale == Locale.ENGLISH ? 380 : 360), stream, 11, PDType1Font.HELVETICA_BOLD);
                                y = y - 15;
                                if (!esMxViajeroCovid) {
                                    GeneralUtils.drawTEXT(messageSource.getMessage("lbl.age", null, locale), y, 60, stream, 11, PDType1Font.HELVETICA);
                                    GeneralUtils.drawTEXT(edad, y, (locale == Locale.ENGLISH ? 130 : 120), stream, 11, PDType1Font.HELVETICA_BOLD);
                                    GeneralUtils.drawTEXT(messageSource.getMessage("lbl.silais1", null, locale), y, 185, stream, 11, PDType1Font.HELVETICA);
                                    GeneralUtils.drawTEXT(tomaMx.getCodigoSilaisMx() != null ? tomaMx.getNombreSilaisMx().replaceAll("SILAIS", "") : "", y, 235, stream, 10, PDType1Font.HELVETICA_BOLD);
                                    GeneralUtils.drawTEXT(messageSource.getMessage("lbl.muni", null, locale) + ":", y, 370, stream, 11, PDType1Font.HELVETICA);
                                    GeneralUtils.drawTEXT(tomaMx.getCodigoMuniMx() != null ? tomaMx.getNombreMuniMx() : "", y, 430, stream, 10, PDType1Font.HELVETICA_BOLD);
                                    y = y - 15;
                                }else {
                                    GeneralUtils.drawTEXT(messageSource.getMessage("lbl.age", null, locale), y, 60, stream, 11, PDType1Font.HELVETICA);
                                    GeneralUtils.drawTEXT(edad, y, (locale == Locale.ENGLISH ? 130 : 120), stream, 11, PDType1Font.HELVETICA_BOLD);
                                    GeneralUtils.drawTEXT(messageSource.getMessage("person.fecnac", null, locale) + ":", y, 300, stream, 11, PDType1Font.HELVETICA);
                                    GeneralUtils.drawTEXT(DateUtil.DateToString(tomaMx.getFechaNacimiento(), formatoFecha), y, (locale == Locale.ENGLISH ? 380 : 400), stream, 11, PDType1Font.HELVETICA_BOLD);
                                    y = y - 15;
                                    GeneralUtils.drawTEXT(messageSource.getMessage("lbl.silais1", null, locale), y, 60, stream, 11, PDType1Font.HELVETICA);
                                    GeneralUtils.drawTEXT(tomaMx.getCodigoSilaisMx() != null ? tomaMx.getNombreSilaisMx().replaceAll("SILAIS ", "") : "", y, (locale == Locale.ENGLISH ? 130 : 120), stream, 10, PDType1Font.HELVETICA_BOLD);
                                    GeneralUtils.drawTEXT(messageSource.getMessage("lbl.muni", null, locale) + ":", y, 300, stream, 11, PDType1Font.HELVETICA);
                                    GeneralUtils.drawTEXT(tomaMx.getCodigoMuniMx() != null ? tomaMx.getNombreMuniMx() : "", y, (locale == Locale.ENGLISH ? 380 : 360), stream, 10, PDType1Font.HELVETICA_BOLD);
                                    y = y - 15;
                                }
                                GeneralUtils.drawTEXT(messageSource.getMessage("lbl.health.unit1", null, locale), y, 60, stream, 11, PDType1Font.HELVETICA);
                                GeneralUtils.drawTEXT(tomaMx.getCodigoUnidadMx() != null ? tomaMx.getNombreUnidadMx() : "", y, (locale == Locale.ENGLISH ? 130 : 150), stream, 9, PDType1Font.HELVETICA_BOLD);
                                y = y - 15;
                                GeneralUtils.drawTEXT(messageSource.getMessage("lbl.sample.type.long", null, locale) + ":", y, 60, stream, 11, PDType1Font.HELVETICA);
                                GeneralUtils.drawTEXT((esMxViajeroCovid && locale == Locale.ENGLISH ? messageSource.getMessage("lbl.sample.type.nasopharyngeal.swab", null, locale) : tomaMx.getNombreTipoMx()), y, 150, stream, 11, PDType1Font.HELVETICA_BOLD);
                                GeneralUtils.drawTEXT(messageSource.getMessage("lbl.sampling.datetime1", null, locale), y, (esMxViajeroCovid && locale == Locale.ENGLISH ? 300 : 300), stream, 11, PDType1Font.HELVETICA);
                                GeneralUtils.drawTEXT(DateUtil.DateToString(tomaMx.getFechaTomaMx(), formatoFecha)+ " " + DateUtil.DateToString(tomaMx.getFechaRegistro(), "hh:mm:ss a"), y, (esMxViajeroCovid && locale == Locale.ENGLISH ? 380 : 410), stream, (esMxViajeroCovid && locale != Locale.ENGLISH ? 10 : 11), PDType1Font.HELVETICA_BOLD);

                                //resultados
                                List<DatosSolicitud> listDx = tomaMxService.getSolicitudesAprobByToma_User_Area(tomaMx.getIdTomaMx(), seguridadService.obtenerNombreUsuario(), area.getIdArea());
                                y = y - 4;
                                RecepcionMx recepcionMx = recepcionMxService.getRecepcionMxByCodUnicoMx(tomaMx.getCodUnicoMx(), labProcesa.getCodigo());
                                String procesadoPor = "";
                                String aprobadoPor = "";
                                String codMinsaProcesa = "";
                                String codMinsaAprueba = "";
                                String fechaProcesamiento = "";
                                if (recepcionMx.getCalidadMx() != null && recepcionMx.getCalidadMx().getCodigo().equalsIgnoreCase("CALIDMX|IDC")) {
                                    y = y - 20;
                                    GeneralUtils.drawTEXT(messageSource.getMessage("lbl.sample.inadequate2", null, locale), y, 100, stream, 10, PDType1Font.HELVETICA);
                                } else {
                                    for (DatosSolicitud dx : listDx) {
                                        aprobadoPor = dx.getUsuarioAprobacion();
                                        fechaProcesamiento = DateUtil.DateToString(dx.getFechaAprobacion(), formatoFechaHora); //poner fecha y hora de aprobación. Andrea 18/11/2021
                                        y = y - 20;
                                        List<DatosOrdenExamen> examenes = ordenExamenMxService.getOrdenesExamenByIdSolicitudV2(dx.getIdSolicitud());
                                        for (DatosOrdenExamen examen : examenes) {
                                            //salto de página
                                            if ((y - 15) < 180) {
                                                stream.close();
                                                page = GeneralUtils.addNewPage(doc);
                                                stream = new PDPageContentStream(doc, page);
                                                //dibujar encabezado y pie de pagina
                                                GeneralUtils.drawHeaderAndFooter(stream, doc, 750, 590, 80, 600, 70);
                                                pageNumber = String.valueOf(doc.getNumberOfPages());
                                                GeneralUtils.drawTEXT(pageNumber, 15, 550, stream, 10, PDType1Font.HELVETICA_BOLD);
                                                drawInfoLab(stream, page, labProcesa, esMxViajeroCovid, locale);
                                                y = 640;
                                                //nombre del reporte
                                                xCenter = GeneralUtils.centerTextPositionX(page, PDType1Font.HELVETICA_BOLD_OBLIQUE, 12, nombreDireccion);
                                                GeneralUtils.drawTEXT(nombreDireccion, y, xCenter, stream, 12, PDType1Font.HELVETICA_BOLD_OBLIQUE);
                                                y = y - 15;
                                                xCenter = GeneralUtils.centerTextPositionX(page, PDType1Font.HELVETICA_BOLD_OBLIQUE, 11, messageSource.getMessage("lbl.lab.result", null, locale).toUpperCase());
                                                GeneralUtils.drawTEXT(messageSource.getMessage("lbl.lab.result", null, locale).toUpperCase(), y, xCenter, stream, 11, PDType1Font.HELVETICA_BOLD_OBLIQUE);
                                                y = y - 15;
                                                xCenter = GeneralUtils.centerTextPositionX(page, PDType1Font.HELVETICA_BOLD_OBLIQUE, 11, area.getNombre().toUpperCase());
                                                GeneralUtils.drawTEXT(area.getNombre().toUpperCase(), y, xCenter, stream, 11, PDType1Font.HELVETICA_BOLD_OBLIQUE);
                                                y = y - 30;

                                            }
                                            List<DetalleResultado> resultados = resultadosService.getDetallesResultadoActivosByExamen(examen.getIdOrdenExamen());
                                            if (resultados.size() > 0) {
                                                yPosicionExamen = y;
                                                //GeneralUtils.drawTEXT(examen.getCodExamen().getNombre(), y, 100, stream, 10, PDType1Font.HELVETICA);
                                                y = y - 15;
                                            }


                                            for (DetalleResultado resultado : resultados) {
                                                String detalleResultado = "";
                                                if (resultado.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                                                    Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(resultado.getValor());
                                                    detalleResultado = (locale != Locale.ENGLISH ? cat_lista.getValor() : messageSource.getMessage("lbl."+cat_lista.getValor().toLowerCase().replaceAll(" ", "."), null, locale));
                                                } else if (resultado.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                                                    detalleResultado = (Boolean.valueOf(resultado.getValor()) ? "lbl.yes" : "lbl.no");
                                                } else {
                                                    detalleResultado = resultado.getValor();
                                                }
                                                procesadoPor = resultado.getUsuarioRegistro().getCompleteName();
                                                //fechaProcesamiento = DateUtil.DateToString(resultado.getFechahProcesa(), formatoFecha); //poner fecha y hora de aprobación. Andrea 18/11/2021
                                                GeneralUtils.drawTEXT(resultado.getRespuesta().getNombre() + ": " + detalleResultado, y, 150, stream, 12, PDType1Font.HELVETICA_BOLD);
                                                y = y - 15;
                                            }
                                            if (resultados.size() > 0) {
                                                GeneralUtils.drawTEXT(examen.getExamen() + " - " + messageSource.getMessage("lbl.processing.date", null, locale) + ": " + fechaProcesamiento, yPosicionExamen, 100, stream, 10, PDType1Font.HELVETICA);
                                                //y = y - 15;
                                            }
                                        }
                                    }
                                }

                                //fecha impresi?n
                                GeneralUtils.drawTEXT(messageSource.getMessage("lbl.date.delivery.results", null, locale) + ": ", (esMxViajeroCovid? 210 : 160), 60, stream, 11, PDType1Font.HELVETICA);
                                GeneralUtils.drawTEXT(fechaImpresion, (esMxViajeroCovid? 210 : 160), 190, stream, 10, PDType1Font.HELVETICA);

                                GeneralUtils.drawTEXT(messageSource.getMessage("lbl.bioanalyst", null, locale) + ": ", (esMxViajeroCovid? 180 : 130), 60, stream, 11, PDType1Font.HELVETICA);
                                if (procesadoPor.replaceAll(" ","").toLowerCase().contains("cod.minsa")){
                                    codMinsaProcesa = procesadoPor.substring(procesadoPor.lastIndexOf("-")+1, procesadoPor.length());
                                    procesadoPor = procesadoPor.substring(0, procesadoPor.lastIndexOf("-"));
                                }
                                GeneralUtils.drawTEXT(procesadoPor, (esMxViajeroCovid? 180 : 130), 122, stream, 10, PDType1Font.HELVETICA);
                                GeneralUtils.drawTEXT(codMinsaProcesa, (esMxViajeroCovid? 168 : 118), 122, stream, 10, PDType1Font.HELVETICA);

                                GeneralUtils.drawTEXT(messageSource.getMessage("lbl.validated.by", null, locale) + ": ", (esMxViajeroCovid? 180 : 130), 300, stream, 11, PDType1Font.HELVETICA);
                                if (aprobadoPor.replaceAll(" ","").toLowerCase().contains("cod.minsa")){
                                    codMinsaAprueba = aprobadoPor.substring(aprobadoPor.lastIndexOf("-")+1, aprobadoPor.length());
                                    aprobadoPor = aprobadoPor.substring(0, aprobadoPor.lastIndexOf("-"));
                                }
                                GeneralUtils.drawTEXT(aprobadoPor, (esMxViajeroCovid? 180 : 130), 370, stream, 10, PDType1Font.HELVETICA);
                                GeneralUtils.drawTEXT(codMinsaAprueba, (esMxViajeroCovid? 168 : 118), 370, stream, 10, PDType1Font.HELVETICA);

                                GeneralUtils.drawTEXT(messageSource.getMessage("lbl.footer.note.results", null, locale), (esMxViajeroCovid? 140 : 100), 60, stream, 11, PDType1Font.HELVETICA_BOLD);
                                if (locale == Locale.ENGLISH) {
                                    GeneralUtils.drawTEXT(messageSource.getMessage("lbl.footer.note.2.results", null, locale), (esMxViajeroCovid ? 126 : 90), 60, stream, 11, PDType1Font.HELVETICA_BOLD);
                                    if (esMxViajeroCovid) {
                                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.footer.note.3.results", null, locale), 112, 60, stream, 11, PDType1Font.HELVETICA_BOLD);
                                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.validation.code", null, locale)+ " "+tomaMx.getCodigoValidacion(), 98, 60, stream, 11, PDType1Font.HELVETICA_BOLD);
                                        GeneralUtils.drawTEXT((urlServiciosEnLinea != null && urlServiciosEnLinea.getValor()!=null? urlServiciosEnLinea.getValor() : ""), 84, 60, stream, 11, PDType1Font.HELVETICA_BOLD);
                                        GeneralUtils.addLink(doc, page, (urlServiciosEnLinea != null && urlServiciosEnLinea.getValor()!=null? urlServiciosEnLinea.getValor() : ""), 84, 60, 11, PDType1Font.HELVETICA_BOLD);
                                        GeneralUtils.addBarcode128(stream, doc, (tomaMx.getCodigoValidacion() != null ? tomaMx.getCodigoValidacion() : ""), 370, 90);
                                    }
                                } else {
                                    if (esMxViajeroCovid) {
                                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.footer.note.3.results", null, locale), 126, 60, stream, 11, PDType1Font.HELVETICA_BOLD);
                                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.validation.code", null, locale)+ " "+tomaMx.getCodigoValidacion(), 112, 60, stream, 11, PDType1Font.HELVETICA_BOLD);
                                        GeneralUtils.drawTEXT((urlServiciosEnLinea != null && urlServiciosEnLinea.getValor()!=null? urlServiciosEnLinea.getValor() : ""), 98, 60, stream, 11, PDType1Font.HELVETICA_BOLD);
                                        GeneralUtils.addLink(doc, page, (urlServiciosEnLinea != null && urlServiciosEnLinea.getValor()!=null? urlServiciosEnLinea.getValor() : ""), 98, 60, 11, PDType1Font.HELVETICA_BOLD);
                                        GeneralUtils.addBarcode128(stream, doc, (tomaMx.getCodigoValidacion() != null ? tomaMx.getCodigoValidacion() : ""), 370, 90);
                                    }
                                }
                                stream.close();
                            }
                        }
                    }
                }
            }
            doc.save(output);
            doc.close();
            // generate the file
            response = Base64.encodeBase64String(output.toByteArray());
            filtros.setStrB64Response(response);

        }catch (Exception e){
            e.printStackTrace();
        }

        return createJsonResponse(filtros);

//        return response;
    }

    private ResponseEntity<String> createJsonResponse( Object o )
    {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy").create();
        String json = gson.toJson( o );
        return new ResponseEntity<String>( json, headers, HttpStatus.CREATED );
    }


    private void drawInfoLab(PDPageContentStream stream, PDPage page, Laboratorio labProcesa, boolean esMxViajeroCovid,Locale locale) throws IOException {
        float xCenter;

        float inY = 720;
        float m = 18;
        if (esMxViajeroCovid) {
            xCenter = GeneralUtils.centerTextPositionX(page, PDType1Font.HELVETICA_BOLD, 14, messageSource.getMessage("lbl.minsa.2", null, locale));
            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.minsa.2", null, locale), inY, xCenter, stream, 14, PDType1Font.HELVETICA_BOLD);
            inY -= m;
        } else {
            xCenter = GeneralUtils.centerTextPositionX(page, PDType1Font.HELVETICA_BOLD, 14, messageSource.getMessage("lbl.minsa", null, locale));
            GeneralUtils.drawTEXT(messageSource.getMessage("lbl.minsa", null, locale), inY, xCenter, stream, 14, PDType1Font.HELVETICA_BOLD);
            inY -= m;
        }
        if(labProcesa != null){

            if (esMxViajeroCovid){
                xCenter = GeneralUtils.centerTextPositionX(page, PDType1Font.HELVETICA_BOLD, 14, messageSource.getMessage("lbl.cndr", null, locale));
                GeneralUtils.drawTEXT(messageSource.getMessage("lbl.cndr", null, locale), inY, xCenter, stream, 14, PDType1Font.HELVETICA_BOLD);
                inY -= m;
            } else {
                if (labProcesa.getNombre() != null) {
                    xCenter = GeneralUtils.centerTextPositionX(page, PDType1Font.HELVETICA_BOLD, 14, labProcesa.getNombre());
                    GeneralUtils.drawTEXT(labProcesa.getNombre(), inY, xCenter, stream, 14, PDType1Font.HELVETICA_BOLD);
                    inY -= m;
                }
            }

            if(labProcesa.getDireccion() != null){
                xCenter = GeneralUtils.centerTextPositionX(page, PDType1Font.HELVETICA_BOLD, 11, labProcesa.getDireccion());
                GeneralUtils.drawTEXT(labProcesa.getDireccion(), inY, xCenter, stream, 11, PDType1Font.HELVETICA_BOLD);
                inY -= m;
            }

            if(labProcesa.getTelefono() != null) {
                if (!esMxViajeroCovid) {
                    if (labProcesa.getTelefax() != null) {
                        xCenter = GeneralUtils.centerTextPositionX(page, PDType1Font.HELVETICA_BOLD, 11, messageSource.getMessage("lbl.telephone", null, locale) + ": " + labProcesa.getTelefono()
                                + " ," + messageSource.getMessage("person.fax", null, locale) + ": " + labProcesa.getTelefax());
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.telephone", null, locale) + ": " + labProcesa.getTelefono() + ", "
                                + messageSource.getMessage("person.fax", null, locale) + ": " + labProcesa.getTelefax(), inY, xCenter, stream, 11, PDType1Font.HELVETICA_BOLD);
                    } else {
                        xCenter = GeneralUtils.centerTextPositionX(page, PDType1Font.HELVETICA_BOLD, 11, messageSource.getMessage("lbl.telephone", null, locale) + ": " + labProcesa.getTelefono());
                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.telephone", null, locale) + ": " + labProcesa.getTelefono(), inY, xCenter, stream, 11, PDType1Font.HELVETICA_BOLD);
                    }
                } else {
                    xCenter = GeneralUtils.centerTextPositionX(page, PDType1Font.HELVETICA_BOLD, 11, messageSource.getMessage("lbl.telephone", null, locale) + ": 505-" + labProcesa.getTelefono());
                    GeneralUtils.drawTEXT(messageSource.getMessage("lbl.telephone", null, locale) + ": 505-" + labProcesa.getTelefono(), inY, xCenter, stream, 11, PDType1Font.HELVETICA_BOLD);
                }
            }
        }
    }

    /**
     * M�todo que se llama al entrar a la opci�n de menu "Recepci�n Mx CC". Se encarga de inicializar las listas para realizar la b�squeda de muestras para CC
     * @return ModelAndView
     * @throws Exception
     */
    @RequestMapping(value = "initCC", method = RequestMethod.GET)
    public ModelAndView initSearchFormCC() throws Exception {
        logger.debug("buscar ordenes para ordenExamen");

        ModelAndView mav = new ModelAndView();
        List<EntidadesAdtvas> entidadesAdtvases =  entidadAdmonService.getAllEntidadesAdtvas();
        List<Laboratorio> laboratorioList = laboratoriosService.getAllLaboratories();
        Laboratorio labUser = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
        String nose = recepcionMxService.obtenerCodigoLab(labUser.getCodigo(),1);
        mav.addObject("entidades",entidadesAdtvases);
        mav.addObject("laboratorios", laboratorioList);
        nose = "nose";
        mav.addObject("mostrarPopUpMx",labUser.getPopUpCodigoMx());
        mav.setViewName("recepcionMx/searchOrdersCC");

        return mav;
    }
}
