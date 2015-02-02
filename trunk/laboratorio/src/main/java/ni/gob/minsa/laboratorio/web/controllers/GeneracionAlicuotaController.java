package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.estructura.EntidadesAdtvas;
import ni.gob.minsa.laboratorio.domain.muestra.*;
import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
import ni.gob.minsa.laboratorio.service.*;
import ni.gob.minsa.laboratorio.utilities.ConstantsSecurity;
import ni.gob.minsa.laboratorio.utilities.DateUtil;
import ni.gob.minsa.laboratorio.utilities.StringUtil;
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
import java.sql.Timestamp;
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

    @Resource(name="generacionAlicuotaService")
    private GeneracionAlicuotaService generacionAlicuotaService;

    @Resource(name = "usuarioService")
    private UsuarioService usuarioService;

   @Resource(name = "catalogosService")
    private CatalogoService catalogosService;

    @Resource(name= "alicuotaService")
    private AlicuotaService alicuotaService;

    @Autowired
    MessageSource messageSource;

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
        logger.info("Obteniendo las muestras recepcionadas en el laboratorio");
        FiltroMx filtroMx= jsonToFiltroMx(filtro);
        List<RecepcionMx> recepcionMxList = recepcionMxService.getRecepcionesByFiltro(filtroMx);
        return RecepcionMxToJson(recepcionMxList);
    }

    private String RecepcionMxToJson(List<RecepcionMx> recepcionMxList){
        String jsonResponse="";
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice=0;
        for(RecepcionMx recepcion : recepcionMxList){
            Map<String, String> map = new HashMap<String, String>();
            map.put("codigoUnicoMx", recepcion.getTomaMx().getCodigoUnicoMx());
            map.put("idRecepcion", recepcion.getIdRecepcion());
            map.put("idTomaMx", recepcion.getTomaMx().getIdTomaMx());
            map.put("fechaTomaMx",DateUtil.DateToString(recepcion.getTomaMx().getFechaHTomaMx(),"dd/MM/yyyy hh:mm:ss a"));
            map.put("fechaRecepcionLab",DateUtil.DateToString(recepcion.getFechaHoraRecepcionLab(),"dd/MM/yyyy hh:mm:ss a"));
            map.put("codSilais", recepcion.getTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre());
            map.put("codUnidadSalud", recepcion.getTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre());
            map.put("separadaMx",(recepcion.getTomaMx().getMxSeparada()!=null?(recepcion.getTomaMx().getMxSeparada()?"Si":"No"):""));
            map.put("cantidadTubos", (recepcion.getTomaMx().getCanTubos()!=null?String.valueOf(recepcion.getTomaMx().getCanTubos()):""));
            map.put("tipoMuestra", recepcion.getTomaMx().getCodTipoMx().getNombre());
            //Si hay fecha de inicio de sintomas se muestra
            Date fechaInicioSintomas = tomaMxService.getFechaInicioSintomas(recepcion.getTomaMx().getIdNotificacion().getIdNotificacion());
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

            //se arma estructura de diagnósticos
            List<DaSolicitudDx> solicitudDxList = tomaMxService.getSolicitudesDxByMx(recepcion.getTomaMx().getIdTomaMx());
            Map<Integer, Object> mapDxList = new HashMap<Integer, Object>();
            Map<String, String> mapDx = new HashMap<String, String>();
            int subIndice=0;
            for(DaSolicitudDx solicitudDx: solicitudDxList){
                mapDx.put("nombre",solicitudDx.getCodDx().getNombre());
                mapDx.put("fechaSolicitud", DateUtil.DateToString(solicitudDx.getFechaHSolicitud(), "dd/MM/yyyy hh:mm:ss a"));
                subIndice++;
            }
            mapDxList.put(subIndice,mapDx);
            map.put("diagnosticos", new Gson().toJson(mapDxList));

            mapResponse.put(indice, map);
            indice ++;
        }
        jsonResponse = new Gson().toJson(mapResponse);
        return jsonResponse;
    }

    private FiltroMx jsonToFiltroMx(String strJson) throws Exception {
        JsonObject jObjectFiltro = new Gson().fromJson(strJson, JsonObject.class);
        FiltroMx filtroMx = new FiltroMx();
        String nombreApellido = null;
        Date fecInicioRecepcionLab = null;
        Date fecFinRecepcionLab = null;
        String codSilais = null;
        String codUnidadSalud = null;
        String codTipoMx = null;


        if (jObjectFiltro.get("nombreApellido") != null && !jObjectFiltro.get("nombreApellido").getAsString().isEmpty())
            nombreApellido = jObjectFiltro.get("nombreApellido").getAsString();
        if (jObjectFiltro.get("fecInicioRecepcionLab") != null && !jObjectFiltro.get("fecInicioRecepcionLab").getAsString().isEmpty())
            fecInicioRecepcionLab = DateUtil.StringToDate(jObjectFiltro.get("fecInicioRecepcionLab").getAsString()+" 00:00:00");
        if (jObjectFiltro.get("fecFinRecepcionLab") != null && !jObjectFiltro.get("fecFinRecepcionLab").getAsString().isEmpty())
            fecFinRecepcionLab = DateUtil.StringToDate(jObjectFiltro.get("fecFinRecepcionLab").getAsString() + " 23:59:59");
        if (jObjectFiltro.get("codSilais") != null && !jObjectFiltro.get("codSilais").getAsString().isEmpty())
            codSilais = jObjectFiltro.get("codSilais").getAsString();
        if (jObjectFiltro.get("codUnidadSalud") != null && !jObjectFiltro.get("codUnidadSalud").getAsString().isEmpty())
            codUnidadSalud = jObjectFiltro.get("codUnidadSalud").getAsString();
        if (jObjectFiltro.get("codTipoMx") != null && !jObjectFiltro.get("codTipoMx").getAsString().isEmpty())
            codTipoMx = jObjectFiltro.get("codTipoMx").getAsString();

        filtroMx.setCodSilais(codSilais);
        filtroMx.setCodUnidadSalud(codUnidadSalud);
        filtroMx.setFechaInicioRecepLab(fecInicioRecepcionLab);
        filtroMx.setFechaFinRecepLab(fecFinRecepcionLab);
        filtroMx.setNombreApellido(nombreApellido);
        filtroMx.setCodTipoMx(codTipoMx);
        filtroMx.setCodEstado("ESTDMX|RCLAB"); // recepcionadas en lab
        filtroMx.setIncluirMxInadecuada(true);

        return filtroMx;
    }


    @RequestMapping(value = "create/{codigoUnicoMx}", method = RequestMethod.GET)
    public ModelAndView initCreationForm(@PathVariable("codigoUnicoMx") String codigoUnicoMx, HttpServletRequest request) throws Exception {
        logger.debug("Iniciando la generación de alicuotas ");
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

            if(codigoUnicoMx != null){

                RecepcionMx recepcionMx=  recepcionMxService.getRecepcionMxByCodUnicoMx(codigoUnicoMx);
                //cargar lista de etiquetas segun tipo de notificacion
                List<Alicuota> alicuotaCat = generacionAlicuotaService.getAlicuotasByTipoNoti(recepcionMx. getTomaMx().getIdNotificacion().getCodTipoNotificacion().getCodigo(), recepcionMx.getTomaMx().getCodTipoMx().getIdTipoMx());
                mav.addObject("alicuotaCat", alicuotaCat);
                mav.addObject("recepcionMx", recepcionMx);
                mav.setViewName("laboratorio/generacionAlicuota/enterForm");
            }

        }else
            mav.setViewName(urlValidacion);

        return mav;
    }

    @RequestMapping(value = "addAliquot", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void addAliquot(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json = "";
        String resultado = "";
        String etiqueta = "";
        String volumen = "";
        String codigoUnicoMx = "";
        String idAlicuota = "";
        String idOrden = "";

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);

            etiqueta = jsonpObject.get("etiqueta").getAsString();
            volumen = jsonpObject.get("volumen").getAsString();
            codigoUnicoMx = jsonpObject.get("codigoUnicoMx").getAsString();
            idOrden = jsonpObject.get("idOrden").getAsString();

            long idUsuario = seguridadService.obtenerIdUsuario(request);
            Usuarios usuario = usuarioService.getUsuarioById((int) idUsuario);

            //se obtiene Catalogo Alicuota por Id
            Alicuota aliCat = alicuotaService.getAlicuota(Integer.valueOf(etiqueta));

            //se obtiene el codigo etiqueta
            String etiq = aliCat.getAlicuota();

            //se obtiene la orden de examen segun id
            OrdenExamen orden = ordenExamenMxService.getOrdenExamenById(idOrden);

            AlicuotaRegistro alicuotaReg = new AlicuotaRegistro();

            alicuotaReg.setUsuarioRegistro(usuario);
            alicuotaReg.setFechaHoraRegistro(new Timestamp(new Date().getTime()));
            alicuotaReg.setIdOrden(orden);

            alicuotaReg.setVolumen(Float.valueOf(volumen));
            alicuotaReg.setAlicuotaCatalogo(aliCat);
            DaTomaMx tomaMx = tomaMxService.getTomaMxByCodUnicoMx(codigoUnicoMx);
            alicuotaReg.setCodUnicoMx(tomaMx);

            //Se genera código único de alicuota segun etiqueta seleccionada
            idAlicuota = generarCodigoAlicuota(codigoUnicoMx, etiq);
            alicuotaReg.setIdAlicuota(idAlicuota);
            try {
               String alicuota = generacionAlicuotaService.addAliquot(alicuotaReg);
            } catch (Exception ex) {
                resultado = messageSource.getMessage("msg.add.aliquot.error", null, null);
                resultado = resultado + ". \n " + ex.getMessage();
                ex.printStackTrace();
            }
        /*    if (!alicuota.isEmpty()) {
                //se tiene que actualizar la orden de examen
                ordenExamen.setCodEstado(estadoOrdenEx);
                try {
                    ordenExamenMxService.updateOrdenExamen(ordenExamen);
                }catch (Exception ex){
                    resultado = messageSource.getMessage("msg.update.order.error",null,null);
                    resultado=resultado+". \n "+ex.getMessage();
                    ex.printStackTrace();
                }
            }*/
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            ex.printStackTrace();
            resultado = messageSource.getMessage("msg.aliquot.error", null, null);
            resultado = resultado + ". \n " + ex.getMessage();

        } finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("idAlicuota", idAlicuota);
            map.put("mensaje", resultado);
            map.put("etiqueta", etiqueta);
            map.put("volumen", volumen);
            map.put("irOrden", idOrden);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    /**
     * Método para generar un identificador alfanumérico de 11 caracteres, compuesto por el codigoUnicoMx + idAlicuota segun etiqueta seleccionada
     *
     * @return String codigoUnicoMx
     */
    private String generarCodigoAlicuota(String codigo, String etiqueta) {
        Long cantidadReg;
        String alicuotaId;
        //Se consulta el ultimo registro realizado para la etiqueta de alicuota
        String id = codigo + etiqueta;
        cantidadReg = generacionAlicuotaService.cantidadAlicuotas(id);
        //Asignacion de codigo segun cantidad de registros encontrados

          Long suma = cantidadReg + 1;
          alicuotaId = codigo + etiqueta + suma;

        //si no existe se retorna el último código generado
        return alicuotaId;
    }

    //Cargar lista de Alicuotas
    @RequestMapping(value = "getAliquots", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    List<AlicuotaRegistro> getAliquots(@RequestParam(value = "codigoUnicoMx", required = false) String codigoUnicoMx) {
        logger.info("Obteniendo las alicuotas agregadas");

        List<AlicuotaRegistro> aliquotsList = null;

        if (codigoUnicoMx != null) {
            aliquotsList = generacionAlicuotaService.getAliquotsById(codigoUnicoMx);

        }
        return aliquotsList;
    }

    //Cargar lista de orden de examenes agregadas en la recepcion de laboratorio
    @RequestMapping(value = "getTestOrders", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    List<OrdenExamen> getTestOrders(@RequestParam(value = "codigoUnicoMx", required = false) String codigoUnicoMx) {
        logger.info("Obteniendo las ordenes de examenes");

        List<OrdenExamen> testOrdersList = null;

        if (codigoUnicoMx != null) {
            testOrdersList = ordenExamenMxService.getOrdenesExamenNoAnuladasByCodigoUnico(codigoUnicoMx);

        }
        return testOrdersList;
    }

    /**
     * Override Vaccine
     *
     * @param idAlicuota the ID of the record
     *
     */
    @RequestMapping(value = "overrideAliquot/{idAlicuota}" ,method = RequestMethod.GET )
    public String overrideVaccine(@PathVariable("idAlicuota") String idAlicuota, HttpServletRequest request) throws Exception {
        AlicuotaRegistro  alic = generacionAlicuotaService.getAliquotById(idAlicuota);
        alic.setPasivo(true);
        generacionAlicuotaService.updateAlicuotaReg(alic);
        String codUnicoMx = alic.getCodUnicoMx().getCodigoUnicoMx();

        return  "redirect:/generacionAlicuota/create/" + codUnicoMx;
    }

}
