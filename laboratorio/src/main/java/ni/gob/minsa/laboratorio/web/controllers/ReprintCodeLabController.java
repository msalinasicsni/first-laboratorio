package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.estructura.EntidadesAdtvas;
import ni.gob.minsa.laboratorio.domain.examen.Area;
import ni.gob.minsa.laboratorio.domain.muestra.*;
import ni.gob.minsa.laboratorio.service.*;
import ni.gob.minsa.laboratorio.utilities.ConstantsSecurity;
import ni.gob.minsa.laboratorio.utilities.DateUtil;
import org.apache.commons.lang3.text.translate.UnicodeEscaper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by souyen-ics.
 */
@Controller
@RequestMapping("reprint")
public class ReprintCodeLabController {
    private static final Logger logger = LoggerFactory.getLogger(ReprintCodeLabController.class);

    @Autowired
    @Qualifier(value = "seguridadService")
    private SeguridadService seguridadService;

    @Autowired
    @Qualifier(value = "catalogosService")
    private CatalogoService catalogosService;

    @Autowired
    @Qualifier(value = "entidadAdmonService")
    private EntidadAdmonService entidadAdmonService;

    @Autowired
    @Qualifier(value = "recepcionMxService")
    private RecepcionMxService recepcionMxService;

    @Autowired
    @Qualifier(value = "tomaMxService")
    private TomaMxService tomaMxService;

    @Autowired
    @Qualifier(value = "areaService")
    private AreaService areaService;

    @Autowired
    @Qualifier(value = "hojaTrabajoService")
    private HojaTrabajoService hojaTrabajoService;

    @Autowired
    MessageSource messageSource;

    /**
     * Método que se llama al entrar a la opción de menu "Enviar Mx Recepcionadas". Se encarga de inicializar las listas para realizar la búsqueda de recepcionesMx
     * @param request para obtener información de la petición del cliente
     * @return ModelAndView
     * @throws Exception
     */
    @RequestMapping(value = "init", method = RequestMethod.GET)
    public ModelAndView initSearchForm(HttpServletRequest request) throws Exception {
        logger.debug("Buscar mx recepcionadas para imprimir ");
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
            List<Area> areaList = areaService.getAreas();

            mav.addObject("entidades",entidadesAdtvases);
            mav.addObject("tipoMuestra", tipoMxList);
            mav.setViewName("recepcionMx/reprintCodeLab");
        }else
            mav.setViewName(urlValidacion);

        return mav;
    }

    /**
     * Método para realizar la búsqueda de recepcionesMx para enviar a recepción de Mx en laboratorio
     * @param filtro JSon con los datos de los filtros a aplicar en la búsqueda(Nombre Apellido, Rango Fec Toma Mx, Tipo Mx, SILAIS, unidad salud)
     * @return String con las recepcionesMx encontradas
     * @throws Exception
     */
    @RequestMapping(value = "searchReceptionRecords", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String fetchOrdersJson(@RequestParam(value = "strFilter", required = true) String filtro) throws Exception{
        logger.info("Obteniendo las mx recepcionadas");
        FiltroMx filtroMx = jsonToFiltroMx(filtro);
        List<RecepcionMx> recepcionMxList = recepcionMxService.getRecepcionesByFiltro(filtroMx);
        return RecepcionMxToJson(recepcionMxList);
    }

    /**
     * Método para convertir una lista de RecepcionMx a un string con estructura Json
     * @param recepcionMxList lista con las Recepciones a convertir
     * @return String
     */
    private String RecepcionMxToJson(List<RecepcionMx> recepcionMxList){
        String jsonResponse;
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice=0;
        for(RecepcionMx recepcion : recepcionMxList){
            boolean esEstudio = tomaMxService.getSolicitudesEstudioByIdTomaMx( recepcion.getTomaMx().getIdTomaMx()).size() > 0;
            Map<String, String> map = new HashMap<String, String>();
            map.put("idTomaMx", recepcion.getTomaMx().getIdTomaMx());
            map.put("codigoUnicoMx", esEstudio?recepcion.getTomaMx().getCodigoUnicoMx():recepcion.getTomaMx().getCodigoLab());
            map.put("fechaTomaMx", DateUtil.DateToString(recepcion.getTomaMx().getFechaHTomaMx(),"dd/MM/yyyy")+
                    (recepcion.getTomaMx().getHoraTomaMx()!=null?" "+recepcion.getTomaMx().getHoraTomaMx():""));
            map.put("fechaRecepcion", DateUtil.DateToString(recepcion.getFechaHoraRecepcion(), "dd/MM/yyyy hh:mm:ss a"));
            if (recepcion.getTomaMx().getIdNotificacion().getCodSilaisAtencion() != null) {
                map.put("codSilais", recepcion.getTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre());
            } else {
                map.put("codSilais", "");
            }
            if (recepcion.getTomaMx().getIdNotificacion().getCodUnidadAtencion() != null) {
                map.put("codUnidadSalud", recepcion.getTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre());
            } else {
                map.put("codUnidadSalud", "");
            }
            map.put("tipoMuestra", recepcion.getTomaMx().getCodTipoMx().getNombre());

            //Si hay persona
            if (recepcion.getTomaMx().getIdNotificacion().getPersona() != null) {
                /// se obtiene el nombre de la persona asociada a la ficha
                String nombreCompleto;
                nombreCompleto = recepcion.getTomaMx().getIdNotificacion().getPersona().getPrimerNombre();
                if (recepcion.getTomaMx().getIdNotificacion().getPersona().getSegundoNombre() != null)
                    nombreCompleto = nombreCompleto + " " + recepcion.getTomaMx().getIdNotificacion().getPersona().getSegundoNombre();
                nombreCompleto = nombreCompleto + " " + recepcion.getTomaMx().getIdNotificacion().getPersona().getPrimerApellido();
                if (recepcion.getTomaMx().getIdNotificacion().getPersona().getSegundoApellido() != null)
                    nombreCompleto = nombreCompleto + " " + recepcion.getTomaMx().getIdNotificacion().getPersona().getSegundoApellido();
                map.put("persona", nombreCompleto);
            } else if (recepcion.getTomaMx().getIdNotificacion().getSolicitante() != null) {
                map.put("persona", recepcion.getTomaMx().getIdNotificacion().getSolicitante().getNombre());
            } else {
                map.put("persona", " ");
            }
            //se arma estructura de diagnósticos o estudios
            Laboratorio labUser = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
            List<DaSolicitudDx> solicitudDxList = tomaMxService.getSolicitudesDxByIdToma(recepcion.getTomaMx().getIdTomaMx(), labUser.getCodigo());
            Map<Integer, Object> mapSolicitudesList = new HashMap<Integer, Object>();
            Map<String, String> mapSolicitud = new HashMap<String, String>();
            String areaEntrega = "";
            int prioridad = 100;
            if (solicitudDxList.size() > 0) {
                int subIndice = 0;
                for (DaSolicitudDx solicitudDx : solicitudDxList) {
                    if (prioridad >= solicitudDx.getCodDx().getPrioridad()) {
                        areaEntrega = solicitudDx.getCodDx().getArea().getNombre();
                        prioridad = solicitudDx.getCodDx().getPrioridad();
                    }
                    mapSolicitud.put("nombre", solicitudDx.getCodDx().getNombre());
                    mapSolicitud.put("tipo", "Rutina");
                    mapSolicitud.put("fechaSolicitud", DateUtil.DateToString(solicitudDx.getFechaHSolicitud(), "dd/MM/yyyy hh:mm:ss a"));
                    subIndice++;
                    mapSolicitudesList.put(subIndice, mapSolicitud);
                    mapSolicitud = new HashMap<String, String>();
                }
                map.put("solicitudes", new Gson().toJson(mapSolicitudesList));
            } else {
                List<DaSolicitudEstudio> solicitudEstudios = tomaMxService.getSolicitudesEstudioByIdTomaMx(recepcion.getTomaMx().getIdTomaMx());
                int subIndice = 0;
                for (DaSolicitudEstudio solicitudEstudio : solicitudEstudios) {
                    areaEntrega = solicitudEstudio.getTipoEstudio().getArea().getNombre();
                    mapSolicitud.put("nombre", solicitudEstudio.getTipoEstudio().getNombre());
                    mapSolicitud.put("tipo", "Estudio");
                    mapSolicitud.put("fechaSolicitud", DateUtil.DateToString(solicitudEstudio.getFechaHSolicitud(), "dd/MM/yyyy hh:mm:ss a"));
                    subIndice++;
                    mapSolicitudesList.put(subIndice, mapSolicitud);
                    mapSolicitud = new HashMap<String, String>();
                }
                map.put("solicitudes", new Gson().toJson(mapSolicitudesList));
            }
            map.put("area",areaEntrega);
            mapResponse.put(indice, map);
            indice++;
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
        Date fechaInicioRecep = null;
        Date fechaFinRecep = null;
        String codSilais = null;
        String codUnidadSalud = null;
        String codTipoMx = null;
        String codigoUnicoMx=null;
        String codTipoSolicitud = null;
        String nombreSolicitud = null;

        if (jObjectFiltro.get("nombreApellido") != null && !jObjectFiltro.get("nombreApellido").getAsString().isEmpty())
            nombreApellido = jObjectFiltro.get("nombreApellido").getAsString();
        if (jObjectFiltro.get("fechaInicioRecep") != null && !jObjectFiltro.get("fechaInicioRecep").getAsString().isEmpty())
            fechaInicioRecep = DateUtil.StringToDate(jObjectFiltro.get("fechaInicioRecep").getAsString()+" 00:00:00");
        if (jObjectFiltro.get("fechaFinRecepcion") != null && !jObjectFiltro.get("fechaFinRecepcion").getAsString().isEmpty())
            fechaFinRecep = DateUtil.StringToDate(jObjectFiltro.get("fechaFinRecepcion").getAsString()+" 23:59:59");
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
        filtroMx.setIncluirMxInadecuada(true);
        filtroMx.setCodigoUnicoMx(codigoUnicoMx);
        filtroMx.setNombreUsuario(seguridadService.obtenerNombreUsuario());
        filtroMx.setIncluirTraslados(true);
        return filtroMx;
    }



}
