package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.estructura.EntidadesAdtvas;
import ni.gob.minsa.laboratorio.domain.muestra.FiltroOrdenExamen;
import ni.gob.minsa.laboratorio.domain.muestra.RecepcionMx;
import ni.gob.minsa.laboratorio.domain.muestra.TipoMx;
import ni.gob.minsa.laboratorio.service.*;
import ni.gob.minsa.laboratorio.utilities.ConstantsSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by souyen-ics on 15-12-14.
 */
@Controller
@RequestMapping("generacionAlicuota")
public class GeneracionAlicuotaController {

    private static final Logger logger = LoggerFactory.getLogger(GeneracionAlicuotaController.class);

    @Resource(name = "seguridadService")
    private SeguridadService seguridadService;

    @Resource(name = "catalogosService")
    private CatalogoService catalogoService;

    @Resource(name = "tomaMxService")
    private TomaMxService tomaMxService;

    @Resource(name = "unidadesService")
    private UnidadesService unidadesService;

    @Resource(name = "entidadAdmonService")
    private EntidadAdmonService entidadAdmonService;

    @Resource(name = "recepcionMxService")
    private RecepcionMxService recepcionMxService;

   @Resource(name = "ordenExamenMxService")
    private OrdenExamenMxService ordenExamenMxService;

    @RequestMapping(value = "init", method = RequestMethod.GET)
    public ModelAndView initSearchForm(HttpServletRequest request) throws Exception {
        logger.debug("buscar muestras recepcionadas en el laboratorio");
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
            List<EntidadesAdtvas> entidadesAdtvas =  entidadAdmonService.getAllEntidadesAdtvas();
            List<TipoMx> tipoMxList = catalogoService.getTipoMuestra();
            mav.addObject("entidades",entidadesAdtvas);
            mav.addObject("tipoMuestra", tipoMxList);
            mav.setViewName("laboratorio/generacionAlicuota/searchSamplesReceived");
        }else
            mav.setViewName(urlValidacion);

        return mav;
    }


    @RequestMapping(value = "search", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String fetchOrdersJson(@RequestParam(value = "strFilter", required = true) String filtro) throws Exception{
        logger.info("Obteniendo las ordenes de examen recepcionadas en el laboratorio");
        FiltroOrdenExamen filtroOrdenExamen= jsonToFiltroOrdenExamen(filtro);
        List<RecepcionMx> recepcionMxList = recepcionMxService.getRecepcionesByFiltro(filtroOrdenExamen);
        return OrdenesExamenToJson(recepcionMxList);
    }

    private String OrdenesExamenToJson(List<RecepcionMx> recepcionMxList){
        String jsonResponse="";
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice=0;
        for(RecepcionMx recepcion : recepcionMxList){
            Map<String, String> map = new HashMap<String, String>();
            map.put("idOrdenExamen", recepcion.getOrdenExamen().getIdOrdenExamen());
            map.put("idTomaMx", recepcion.getOrdenExamen().getIdTomaMx().getIdTomaMx());
            map.put("fechaHoraOrden",DateToString(recepcion.getOrdenExamen().getFechaHOrden(), "dd/MM/yyyy hh:mm:ss a"));
            map.put("fechaTomaMx",DateToString(recepcion.getOrdenExamen().getIdTomaMx().getFechaHTomaMx(),"dd/MM/yyyy hh:mm:ss a"));
            map.put("fechaRecepcionLab",DateToString(recepcion.getFechaHoraRecepcionLab(),"dd/MM/yyyy hh:mm:ss a"));
            map.put("codSilais", recepcion.getOrdenExamen().getIdTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre());
            map.put("codUnidadSalud", recepcion.getOrdenExamen().getIdTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre());
            map.put("estadoOrden", recepcion.getOrdenExamen().getCodEstado().getValor());
            map.put("separadaMx",(recepcion.getOrdenExamen().getIdTomaMx().getMxSeparada()!=null?(recepcion.getOrdenExamen().getIdTomaMx().getMxSeparada()?"Si":"No"):""));
            map.put("cantidadTubos", (recepcion.getOrdenExamen().getIdTomaMx().getCanTubos()!=null?String.valueOf(recepcion.getOrdenExamen().getIdTomaMx().getCanTubos()):""));
            map.put("tipoMuestra", recepcion.getOrdenExamen().getIdTomaMx().getCodTipoMx().getNombre());
            map.put("tipoExamen", recepcion.getOrdenExamen().getCodExamen().getNombre());

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
        Date fecInicioRecepcionLab = null;
        Date fecFinRecepcionLab = null;
        String codSilais = null;
        String codUnidadSalud = null;
        String codTipoMx = null;


        if (jObjectFiltro.get("nombreApellido") != null && !jObjectFiltro.get("nombreApellido").getAsString().isEmpty())
            nombreApellido = jObjectFiltro.get("nombreApellido").getAsString();
        if (jObjectFiltro.get("fecInicioRecepcionLab") != null && !jObjectFiltro.get("fecInicioRecepcionLab").getAsString().isEmpty())
            fecInicioRecepcionLab = StringToDate(jObjectFiltro.get("fecInicioRecepcionLab").getAsString()+" 00:00:00");
        if (jObjectFiltro.get("fecFinRecepcionLab") != null && !jObjectFiltro.get("fecFinRecepcionLab").getAsString().isEmpty())
            fecFinRecepcionLab = StringToDate(jObjectFiltro.get("fecFinRecepcionLab").getAsString()+" 23:59:59");
        if (jObjectFiltro.get("codSilais") != null && !jObjectFiltro.get("codSilais").getAsString().isEmpty())
            codSilais = jObjectFiltro.get("codSilais").getAsString();
        if (jObjectFiltro.get("codUnidadSalud") != null && !jObjectFiltro.get("codUnidadSalud").getAsString().isEmpty())
            codUnidadSalud = jObjectFiltro.get("codUnidadSalud").getAsString();
        if (jObjectFiltro.get("codTipoMx") != null && !jObjectFiltro.get("codTipoMx").getAsString().isEmpty())
            codTipoMx = jObjectFiltro.get("codTipoMx").getAsString();

        filtroOrdenExamen.setCodSilais(codSilais);
        filtroOrdenExamen.setCodUnidadSalud(codUnidadSalud);
        filtroOrdenExamen.setFechaInicioRecepLab(fecInicioRecepcionLab);
        filtroOrdenExamen.setFechaFinRecepLab(fecFinRecepcionLab);
        filtroOrdenExamen.setNombreApellido(nombreApellido);
        filtroOrdenExamen.setCodTipoMx(codTipoMx);
        filtroOrdenExamen.setCodEstado("ESTORDEN|RCLAB"); // recepcionadas en lab
        filtroOrdenExamen.setIncluirMxInadecuada(true);

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

}
