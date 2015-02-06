package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.estructura.EntidadesAdtvas;
import ni.gob.minsa.laboratorio.domain.estructura.Unidades;
import ni.gob.minsa.laboratorio.domain.muestra.*;
import ni.gob.minsa.laboratorio.domain.resultados.Catalogo_Lista;
import ni.gob.minsa.laboratorio.domain.resultados.Conceptos;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by FIRSTICT on 1/9/2015.
 */
@Controller
@RequestMapping("resultados")
public class ResultadosController {
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
    @Qualifier(value = "tomaMxService")
    private TomaMxService tomaMxService;

    @Autowired
    @Qualifier(value = "alicuotaService")
    private AlicuotaService alicuotaService;

    @Autowired
    @Qualifier(value = "generacionAlicuotaService")
    private GeneracionAlicuotaService generacionAlicuotaService;

    @Autowired
    @Qualifier(value = "conceptosService")
    private ConceptosService conceptosService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    ServletContext servletContext;

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
            mav.setViewName("resultados/searchOrders");
        }else
            mav.setViewName(urlValidacion);

        return mav;
    }

    @RequestMapping(value = "create/{strIdRegAli}", method = RequestMethod.GET)
    public ModelAndView createReceiptForm(HttpServletRequest request, @PathVariable("strIdRegAli")  String strIdRegAli) throws Exception {
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
            AlicuotaRegistro alicuota =  generacionAlicuotaService.getAliquotById(strIdRegAli);
            List<Conceptos> conceptosList = conceptosService.getConceptosActivosByExamen(alicuota.getIdOrden().getCodExamen().getIdExamen());
            List<Catalogo_Lista> listas = conceptosService.getCatalogoListaConceptoByIdExamen(alicuota.getIdOrden().getCodExamen().getIdExamen());
            List<TipoMx> tipoMxList = catalogosService.getTipoMuestra();
            Date fechaInicioSintomas = tomaMxService.getFechaInicioSintomas(alicuota.getCodUnicoMx().getIdNotificacion().getIdNotificacion());
            mav.addObject("alicuota",alicuota);
            mav.addObject("tipoMuestra", tipoMxList);
            mav.addObject("fechaInicioSintomas",fechaInicioSintomas);
            mav.addObject("conceptosList", conceptosList);
            mav.addObject("valoresListas",listas);
            mav.setViewName("resultados/incomeResult");
        }else
            mav.setViewName(urlValidacion);

        return mav;
    }

    @RequestMapping(value = "searchOrders", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String fetchOrdersJson(@RequestParam(value = "strFilter", required = true) String filtro) throws Exception{
        logger.info("Obteniendo las ordenes de examen pendienetes según filtros en JSON");
        FiltroMx filtroMx = jsonToFiltroMx(filtro);
        List<AlicuotaRegistro> ordenExamenList = alicuotaService.getAlicutoasByFiltro(filtroMx);
        return RegistroAlicuotaToJson(ordenExamenList);
    }

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
        String codigoUnicoMx = null;

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

        filtroMx.setCodSilais(codSilais);
        filtroMx.setCodUnidadSalud(codUnidadSalud);
        filtroMx.setFechaInicioTomaMx(fechaInicioTomaMx);
        filtroMx.setFechaFinTomaMx(fechaFinTomaMx);
        filtroMx.setFechaInicioRecep(fechaInicioRecep);
        filtroMx.setFechaFinRecep(fechaFinRecep);
        filtroMx.setNombreApellido(nombreApellido);
        filtroMx.setCodTipoMx(codTipoMx);
        filtroMx.setCodEstado("ESTDMX|RCLAB"); // sólo las recepcionadas en laboratorio
        filtroMx.setCodigoUnicoMx(codigoUnicoMx);

        return filtroMx;
    }

    private String RegistroAlicuotaToJson(List<AlicuotaRegistro> alicuotaRegistros){
        String jsonResponse="";
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice=0;
        for(AlicuotaRegistro alicuota :alicuotaRegistros){
            Map<String, String> map = new HashMap<String, String>();
            map.put("idAlicuota", alicuota.getIdAlicuota());
            map.put("idOrdenExamen", alicuota.getIdOrden().getIdOrdenExamen());
            map.put("idTomaMx", alicuota.getCodUnicoMx().getIdTomaMx());
            map.put("codigoUnicoMx", alicuota.getCodUnicoMx().getCodigoUnicoMx());
            map.put("etiquetaPara", alicuota.getAlicuotaCatalogo().getEtiquetaPara());
            map.put("volumen", String.valueOf(alicuota.getVolumen()));
            map.put("fechaHoraOrden",DateUtil.DateToString(alicuota.getIdOrden().getFechaHOrden(), "dd/MM/yyyy hh:mm:ss a"));
            map.put("examen", alicuota.getIdOrden().getCodExamen().getNombre());
            map.put("fechaHoraDx",DateUtil.DateToString(alicuota.getIdOrden().getSolicitudDx().getFechaHSolicitud(), "dd/MM/yyyy hh:mm:ss a"));
            map.put("tipoDx", alicuota.getIdOrden().getSolicitudDx().getCodDx().getNombre());
            map.put("fechaTomaMx",DateUtil.DateToString(alicuota.getCodUnicoMx().getFechaHTomaMx(), "dd/MM/yyyy hh:mm:ss a"));
            map.put("codSilais", alicuota.getCodUnicoMx().getIdNotificacion().getCodSilaisAtencion().getNombre());
            map.put("codUnidadSalud", alicuota.getCodUnicoMx().getIdNotificacion().getCodUnidadAtencion().getNombre());
            map.put("tipoMuestra", alicuota.getCodUnicoMx().getCodTipoMx().getNombre());
            //Si hay fecha de inicio de sintomas se muestra
            Date fechaInicioSintomas = tomaMxService.getFechaInicioSintomas(alicuota.getCodUnicoMx().getIdNotificacion().getIdNotificacion());
            if (fechaInicioSintomas!=null)
                map.put("fechaInicioSintomas",DateUtil.DateToString(fechaInicioSintomas,"dd/MM/yyyy"));
            else
                map.put("fechaInicioSintomas"," ");
            //Si hay persona
            if (alicuota.getCodUnicoMx().getIdNotificacion().getPersona()!=null){
                /// se obtiene el nombre de la persona asociada a la ficha
                String nombreCompleto = "";
                nombreCompleto = alicuota.getCodUnicoMx().getIdNotificacion().getPersona().getPrimerNombre();
                if (alicuota.getCodUnicoMx().getIdNotificacion().getPersona().getSegundoNombre()!=null)
                    nombreCompleto = nombreCompleto +" "+ alicuota.getCodUnicoMx().getIdNotificacion().getPersona().getSegundoNombre();
                nombreCompleto = nombreCompleto+" "+ alicuota.getCodUnicoMx().getIdNotificacion().getPersona().getPrimerApellido();
                if (alicuota.getCodUnicoMx().getIdNotificacion().getPersona().getSegundoApellido()!=null)
                    nombreCompleto = nombreCompleto +" "+ alicuota.getCodUnicoMx().getIdNotificacion().getPersona().getSegundoApellido();
                map.put("persona",nombreCompleto);
            }else{
                map.put("persona"," ");
            }

            mapResponse.put(indice, map);
            indice ++;
        }
        jsonResponse = new Gson().toJson(mapResponse);
        //escapar caracteres especiales, escape de los caracteres con valor numérico mayor a 127
        UnicodeEscaper escaper     = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }

    @RequestMapping(value = "printBC/{strBarCodes}", method = RequestMethod.GET)
    //public String openFile(@RequestParam(value = "path", required = true) String path, @RequestParam(value="objectName", required = true) String objectName,@RequestParam(value="objectType", required = true) String objectType, HttpServletRequest request) {
    public ModelAndView openFile(HttpServletRequest request,@PathVariable("strBarCodes")  String strBarCodes) {
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
            mav.addObject("strBarCodes",strBarCodes);
            mav.setViewName("impresion/print");
        }else
            mav.setViewName(urlValidacion);

        return mav;
    }

    @RequestMapping(value = "ver", method = RequestMethod.GET)
    public ModelAndView verResultado(HttpServletRequest request) throws Exception {
        logger.debug("ver resultado demo");
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
            mav.setViewName("resultados/verResultado");
        }else
            mav.setViewName(urlValidacion);

        return mav;
    }
}
