package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.concepto.Catalogo_Lista;
import ni.gob.minsa.laboratorio.domain.estructura.EntidadesAdtvas;
import ni.gob.minsa.laboratorio.domain.estructura.Unidades;
import ni.gob.minsa.laboratorio.domain.muestra.*;
import ni.gob.minsa.laboratorio.domain.muestra.traslado.TrasladoMx;
import ni.gob.minsa.laboratorio.domain.resultados.DetalleResultado;
import ni.gob.minsa.laboratorio.domain.resultados.DetalleResultadoFinal;
import ni.gob.minsa.laboratorio.service.*;
import ni.gob.minsa.laboratorio.utilities.ConstantsSecurity;
import ni.gob.minsa.laboratorio.utilities.DateUtil;
import ni.gob.minsa.laboratorio.utilities.enumeration.HealthUnitType;
import org.apache.commons.lang3.text.translate.UnicodeEscaper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by Miguel Salinas on 7/26/2017.
 * V1.0
 */
@Controller
@RequestMapping("editarMx")
public class EditarSolicitudesMxController {

    private static final Logger logger = LoggerFactory.getLogger(RecepcionMxController.class);

    @Resource(name = "seguridadService")
    private SeguridadService seguridadService;

    @Resource(name = "catalogosService")
    private CatalogoService catalogosService;

    @Resource(name = "entidadAdmonService")
    private EntidadAdmonService entidadAdmonService;

    @Resource(name = "recepcionMxService")
    private RecepcionMxService recepcionMxService;

    @Resource(name = "trasladosService")
    private TrasladosService trasladosService;

    @Resource(name = "tomaMxService")
    private TomaMxService tomaMxService;

    @Resource(name = "ordenExamenMxService")
    private OrdenExamenMxService ordenExamenMxService;

    @Resource(name = "unidadesService")
    private UnidadesService unidadesService;

    @Resource(name = "datosSolicitudService")
    private DatosSolicitudService datosSolicitudService;

    @Resource(name = "resultadoFinalService")
    private ResultadoFinalService resultadoFinalService;

    @Resource(name = "resultadosService")
    private ResultadosService resultadosService;

    @Autowired
    MessageSource messageSource;

    /**
     * Método que se llama al entrar a la opción de menu "Recepción Mx Laboratorio". Se encarga de inicializar las listas para realizar la búsqueda de envios de Mx
     * @param request para obtener información de la petición del cliente
     * @return ModelAndView
     * @throws Exception
     */
    @RequestMapping(value = "init", method = RequestMethod.GET)
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
            mav.setViewName("laboratorio/editarMx/searchMxLab");
        }else
            mav.setViewName(urlValidacion);

        return mav;
    }

    /**
     * Método para realizar la búsqueda de Recepcion Mx para recepcionar en laboratorio
     * @param filtro JSon con los datos de los filtros a aplicar en la búsqueda(Nombre Apellido, Rango Fec Toma Mx, Tipo Mx, SILAIS, unidad salud)
     * @return String con las Recepciones encontradas
     * @throws Exception
     */
    @RequestMapping(value = "searchMxLab", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String fetchOrdersLabJson(@RequestParam(value = "strFilter", required = true) String filtro) throws Exception{
        logger.info("Obteniendo las ordenes de examen pendienetes según filtros en JSON");
        FiltroMx filtroMx = jsonToFiltroMx(filtro);
        List<RecepcionMx> recepcionMxList = recepcionMxService.getRecepcionesByFiltro(filtroMx);
        return RecepcionMxToJson(recepcionMxList);
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
        Date fechaInicioRecep = null;
        Date fechaFinRecep = null;
        String codSilais = null;
        String codUnidadSalud = null;
        String codTipoMx = null;
        String codigoUnicoMx = null;
        String codTipoSolicitud = null;
        String nombreSolicitud = null;

        if (jObjectFiltro.get("nombreApellido") != null && !jObjectFiltro.get("nombreApellido").getAsString().isEmpty())
            nombreApellido = jObjectFiltro.get("nombreApellido").getAsString();
        if (jObjectFiltro.get("fechaInicioRecep") != null && !jObjectFiltro.get("fechaInicioRecep").getAsString().isEmpty())
            fechaInicioRecep = DateUtil.StringToDate(jObjectFiltro.get("fechaInicioRecep").getAsString() + " 00:00:00");
        if (jObjectFiltro.get("fechaFinRecepcion") != null && !jObjectFiltro.get("fechaFinRecepcion").getAsString().isEmpty())
            fechaFinRecep =DateUtil. StringToDate(jObjectFiltro.get("fechaFinRecepcion").getAsString() + " 23:59:59");
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

        filtroMx.setCodSilais(codSilais);
        filtroMx.setCodUnidadSalud(codUnidadSalud);
        filtroMx.setFechaInicioRecep(fechaInicioRecep);
        filtroMx.setFechaFinRecep(fechaFinRecep);
        filtroMx.setNombreApellido(nombreApellido);
        filtroMx.setCodTipoMx(codTipoMx);
        filtroMx.setCodTipoSolicitud(codTipoSolicitud);
        filtroMx.setNombreSolicitud(nombreSolicitud);
        filtroMx.setCodEstado("ESTDMX|RCLAB"); // sólo las recepcionadas en laboratorio
        filtroMx.setCodigoUnicoMx(codigoUnicoMx);
        filtroMx.setNombreUsuario(seguridadService.obtenerNombreUsuario());
        filtroMx.setIncluirTraslados(true);

        return filtroMx;
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
                    /*if (!seguridadService.usuarioAutorizadoArea(seguridadService.obtenerNombreUsuario(), trasladoMxActivo.getAreaDestino().getIdArea())){
                        mostrar = false;
                    }else{*/
                        traslado = messageSource.getMessage("lbl.yes",null,null);
                        areaOrigen = trasladoMxActivo.getAreaOrigen().getNombre();
                    //}
                }
            }/*else {
                //se si no hay traslado, pero tiene mas de un dx validar si el usuario tiene acceso al de mayor prioridad. Si sólo hay uno siempre se muestra
                List<DaSolicitudDx> solicitudDxList = tomaMxService.getSolicitudesDxPrioridadByIdToma(recepcion.getTomaMx().getIdTomaMx());
                if (solicitudDxList.size() > 1) {
                    if (!seguridadService.usuarioAutorizadoArea(seguridadService.obtenerNombreUsuario(), solicitudDxList.get(0).getCodDx().getArea().getIdArea())) {
                        mostrar = false;
                    }
                }
            }*/
            if (mostrar) {
                boolean esEstudio = tomaMxService.getSolicitudesEstudioByIdTomaMx( recepcion.getTomaMx().getIdTomaMx()).size() > 0;
                Map<String, String> map = new HashMap<String, String>();
                map.put("idRecepcion", recepcion.getIdRecepcion());
                map.put("idTomaMx", recepcion.getTomaMx().getIdTomaMx());
                map.put("codigoUnicoMx", esEstudio?recepcion.getTomaMx().getCodigoUnicoMx():recepcion.getTomaMx().getCodigoLab());
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

                if (recepcion.getTomaMx().getIdNotificacion().getCodUnidadAtencion() != null) {
                    map.put("codUnidadSalud", recepcion.getTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre());
                } else {
                    map.put("codUnidadSalud", "");
                }
                //Si hay fecha de inicio de sintomas se muestra
                Date fechaInicioSintomas = recepcion.getTomaMx().getIdNotificacion().getFechaInicioSintomas();
                if (fechaInicioSintomas != null) {
                    map.put("fechaInicioSintomas", DateUtil.DateToString(fechaInicioSintomas, "dd/MM/yyyy"));
                    map.put("dias",String.valueOf(DateUtil.CalcularDiferenciaDiasFechas(fechaInicioSintomas, recepcion.getTomaMx().getFechaHTomaMx())+1));
                }
                else {
                    map.put("fechaInicioSintomas", " ");
                    map.put("dias","");
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
                    if(edad > 12 && recepcion.getTomaMx().getIdNotificacion().getPersona().isSexoFemenino()){
                        if (recepcion.getTomaMx().getIdNotificacion().getEmbarazada()!=null)
                            map.put("embarazada",(recepcion.getTomaMx().getIdNotificacion().getEmbarazada().getCodigo().equalsIgnoreCase("RESP|S")?
                                    messageSource.getMessage("lbl.yes",null,null):messageSource.getMessage("lbl.no",null,null)));
                        else map.put("embarazada",messageSource.getMessage("lbl.no",null,null));
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

                //se arma estructura de diagnósticos o estudios
                Laboratorio labUser = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
                List<DaSolicitudDx> solicitudDxList = tomaMxService.getSolicitudesDxByIdToma(recepcion.getTomaMx().getIdTomaMx(), labUser.getCodigo());
                DaSolicitudEstudio solicitudE = tomaMxService.getSoliEstByCodigo(recepcion.getTomaMx().getCodigoUnicoMx());

                if (!solicitudDxList.isEmpty()) {
                    int cont = 0;
                    String dxs = "";
                    for (DaSolicitudDx solicitudDx : solicitudDxList) {
                        cont++;
                        if (cont == solicitudDxList.size()) {
                            dxs += solicitudDx.getCodDx().getNombre();
                        } else {
                            dxs += solicitudDx.getCodDx().getNombre() + ", ";
                        }

                    }
                    map.put("solicitudes", dxs);
                } else {
                    if(solicitudE != null){
                        map.put("solicitudes", solicitudE.getTipoEstudio().getNombre());
                    }else{
                        map.put("solicitudes", "");
                    }

                }

                mapResponse.put(indice, map);
                indice++;
            }
        }
        jsonResponse = new Gson().toJson(mapResponse);
        //escapar caracteres especiales, escape de los caracteres con valor numérico mayor a 127
        UnicodeEscaper escaper = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }

    /**
     * Método que se llama para crear una Recepción Mx en el Laboratorio. Setea los datos de la recepción e inicializa listas y demas controles.
     * Además si es la primera vez que se carga el registro se registran ordenes de examen para los examenes configurados por defecto en la tabla
     * de parámetros según el tipo de notificación, tipo de mx, tipo dx
     * @param request para obtener información de la petición del cliente
     * @param strIdMuestra Id de la muestra a editar
     * @return ModelAndView
     * @throws Exception
     */
    @RequestMapping(value = "editLab/{strIdRecepcion}", method = RequestMethod.GET)
    public ModelAndView createReceiptLabForm(HttpServletRequest request, @PathVariable("strIdRecepcion")  String strIdMuestra) throws Exception {
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
            //RecepcionMx recepcionMx = recepcionMxService.getRecepcionMx(strIdRecepcion);
            DaTomaMx tomaMx = tomaMxService.getTomaMxById(strIdMuestra);
            List<EntidadesAdtvas> entidadesAdtvases =  entidadAdmonService.getAllEntidadesAdtvas();
            List<TipoMx> tipoMxList = catalogosService.getTipoMuestra();
            List<Unidades> unidades = null;
            Date fechaInicioSintomas = null;
            boolean esEstudio = false;
            if (tomaMx!=null) {
                //se determina si es una muestra para estudio o para vigilancia rutinaria(Dx)
                List<DaSolicitudEstudio> solicitudEstudioList = tomaMxService.getSolicitudesEstudioByIdTomaMx(tomaMx.getIdTomaMx());
                esEstudio = solicitudEstudioList.size()>0;
                if(tomaMx.getIdNotificacion().getCodSilaisAtencion()!=null) {
                    unidades = unidadesService.getPrimaryUnitsBySilais(tomaMx.getIdNotificacion().getCodSilaisAtencion().getCodigo(), HealthUnitType.UnidadesPrimHosp.getDiscriminator().split(","));
                }
                fechaInicioSintomas = tomaMx.getIdNotificacion().getFechaInicioSintomas();
                TrasladoMx trasladoActivo = trasladosService.getTrasladoActivoMx(tomaMx.getIdTomaMx());
                List<DaSolicitudDx> solicitudDxList = tomaMxService.getSolicitudesDxByIdTomaAreaLabUser(tomaMx.getIdTomaMx(), seguridadService.obtenerNombreUsuario());
                List<DaSolicitudEstudio> solicitudEstudios = tomaMxService.getSolicitudesEstudioByIdMxUser(tomaMx.getIdTomaMx(), seguridadService.obtenerNombreUsuario());
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
                List<DatoSolicitudDetalle> datoSolicitudDetalles = new ArrayList<DatoSolicitudDetalle>();
                for(DaSolicitudDx solicitudDx : dxMostrar){
                    datoSolicitudDetalles.addAll(datosSolicitudService.getDatosSolicitudDetalleBySolicitud(solicitudDx.getIdSolicitudDx()));
                }
                for(DaSolicitudEstudio solicitudEstudio : solicitudEstudios){
                    datoSolicitudDetalles.addAll(datosSolicitudService.getDatosSolicitudDetalleBySolicitud(solicitudEstudio.getIdSolicitudEstudio()));
                }
                mav.addObject("datosList",datoSolicitudDetalles);
            }
            mav.addObject("esEstudio",esEstudio);
            mav.addObject("tomaMx",tomaMx);
            mav.addObject("entidades",entidadesAdtvases);
            mav.addObject("unidades",unidades);
            mav.addObject("tipoMuestra", tipoMxList);
            mav.addObject("fechaInicioSintomas",fechaInicioSintomas);
            mav.setViewName("laboratorio/editarMx/editarMxLab");
        }else
            mav.setViewName(urlValidacion);

        return mav;
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
        logger.info("despues getOrdenesExamen");
        return OrdenesExamenToJson(ordenExamenList);
    }

    /**
     * Método para convertir una lista de Ordenes Examen a un string con estructura Json
     * @param ordenesExamenList lista con las ordenes de examen a convertir
     * @return String
     * @throws java.io.UnsupportedEncodingException
     */
    private String OrdenesExamenToJson(List<OrdenExamen> ordenesExamenList) throws UnsupportedEncodingException {
        String jsonResponse="";
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice=0;
        boolean agregarExamenDx = true;
        for(OrdenExamen ordenExamen : ordenesExamenList){
            Map<String, String> map = new HashMap<String, String>();
            if (ordenExamen.getSolicitudDx()!=null) {
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
                map.put("resultado", parseResultDetails(ordenExamen.getIdOrdenExamen()));
                mapResponse.put(indice, map);
                indice ++;
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
                map.put("resultado", parseResultDetails(ordenExamen.getIdOrdenExamen()));
                mapResponse.put(indice, map);
                indice ++;
            }
        }
        jsonResponse = new Gson().toJson(mapResponse);
        //escapar caracteres especiales, escape de los caracteres con valor numérico mayor a 127
        UnicodeEscaper escaper     = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }

    @RequestMapping(value = "getSolicitudes", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    String getSolicitudes(@RequestParam(value = "idTomaMx", required = true) String idTomaMx) throws Exception {
        logger.info("antes getSolicitudes");
        List<DaSolicitudDx> solicitudDxList = tomaMxService.getSolicitudesDxByIdTomaAreaLabUser(idTomaMx, seguridadService.obtenerNombreUsuario());
        List<DaSolicitudEstudio> solicitudEstudios = tomaMxService.getSolicitudesEstudioByIdMxUser(idTomaMx, seguridadService.obtenerNombreUsuario());
        logger.info("despues getSolicitudes");
        return SolicutudesToJson(solicitudDxList,solicitudEstudios);
    }

    /**
     * Método para convertir una lista de Ordenes Examen a un string con estructura Json
     * @param dxList lista con las solicitudes de diagnósticos a convertir
     * @param estudioList lista con las solicitudes de estudio a convertir
     * @return String
     * @throws UnsupportedEncodingException
     */
    private String SolicutudesToJson(List<DaSolicitudDx> dxList, List<DaSolicitudEstudio> estudioList) throws UnsupportedEncodingException {
        String jsonResponse="";
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice=0;
        boolean agregarExamenDx = true;
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
            //map.put("aprobada", (dx.getAprobada()?messageSource.getMessage("lbl.yes", null, null):messageSource.getMessage("lbl.no", null, null)));
            map.put("aprobada", String.valueOf(dx.getAprobada()));
            map.put("resultados", parseFinalResultDetails(dx.getIdSolicitudDx()));
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
            map.put("aprobada", (estudio.getAprobada()?messageSource.getMessage("lbl.yes", null, null):messageSource.getMessage("lbl.no", null, null)));
            map.put("resultados", parseFinalResultDetails(estudio.getIdSolicitudEstudio()));
            mapResponse.put(indice, map);
            indice ++;
        }
        jsonResponse = new Gson().toJson(mapResponse);
        //escapar caracteres especiales, escape de los caracteres con valor numérico mayor a 127
        UnicodeEscaper escaper     = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }

    private String parseFinalResultDetails(String idSolicitud){
        List<DetalleResultadoFinal> resFinalList = resultadoFinalService.getDetResActivosBySolicitud(idSolicitud);
        String resultados="";
        for(DetalleResultadoFinal res: resFinalList){
            if (res.getRespuesta()!=null) {
                resultados+=(resultados.isEmpty()?res.getRespuesta().getNombre():", "+res.getRespuesta().getNombre());
                if (res.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                    Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(res.getValor());
                    resultados+=": "+cat_lista.getValor();
                }else if (res.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                    String valorBoleano = (Boolean.valueOf(res.getValor())?"lbl.yes":"lbl.no");
                    resultados+=": "+valorBoleano;
                } else {
                    resultados+=": "+res.getValor();
                }
            }else if (res.getRespuestaExamen()!=null){
                resultados+=(resultados.isEmpty()?res.getRespuestaExamen().getNombre():", "+res.getRespuestaExamen().getNombre());
                if (res.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                    Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(res.getValor());
                    resultados+=": "+cat_lista.getValor();
                } else if (res.getRespuestaExamen().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                    String valorBoleano = (Boolean.valueOf(res.getValor())?"lbl.yes":"lbl.no");
                    resultados+=": "+valorBoleano;
                }else {
                    resultados+=": "+res.getValor();
                }
            }
        }
        return resultados;
    }

    private String parseResultDetails(String idOrdenExamen){
        List<DetalleResultado> resFinalList = resultadosService.getDetallesResultadoActivosByExamen(idOrdenExamen);
        String resultados="";
        for(DetalleResultado res: resFinalList){
            if (res.getRespuesta()!=null) {
                resultados+=(resultados.isEmpty()?res.getRespuesta().getNombre():", "+res.getRespuesta().getNombre());
                if (res.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                    Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(res.getValor());
                    resultados+=": "+cat_lista.getValor();
                }else if (res.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                    String valorBoleano = (Boolean.valueOf(res.getValor())?"lbl.yes":"lbl.no");
                    resultados+=": "+valorBoleano;
                } else {
                    resultados+=": "+res.getValor();
                }
            }
        }
        return resultados;
    }

}
