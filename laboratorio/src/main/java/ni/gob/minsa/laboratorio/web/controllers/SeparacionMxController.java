package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.estructura.EntidadesAdtvas;
import ni.gob.minsa.laboratorio.domain.muestra.*;
import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
import ni.gob.minsa.laboratorio.service.*;
import ni.gob.minsa.laboratorio.utilities.ConstantsSecurity;
import ni.gob.minsa.laboratorio.utilities.DateUtil;
import org.apache.commons.lang3.text.translate.UnicodeEscaper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by souyen-ics on 15-12-14.
 */
@Controller
@RequestMapping("separacionMx")
public class SeparacionMxController {

    private static final Logger logger = LoggerFactory.getLogger(SeparacionMxController.class);

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

    @Resource(name= "separacionMxService")
    private SeparacionMxService separacionMxService;

    @Resource(name = "usuarioService")
    private UsuarioService usuarioService;

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
            mav.setViewName("laboratorio/separacionMx/searchSamplesReceived");
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
            if (recepcion.getTomaMx().getIdNotificacion().getCodSilaisAtencion()!=null) {
                map.put("codSilais", recepcion.getTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre());
            }else{
                map.put("codSilais","");
            }
            if (recepcion.getTomaMx().getIdNotificacion().getCodUnidadAtencion()!=null) {
                map.put("codUnidadSalud", recepcion.getTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre());
            }else {
                map.put("codUnidadSalud","");
            }
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

            //se arma estructura de diagnósticos o estudios
            List<DaSolicitudDx> solicitudDxList = tomaMxService.getSolicitudesDxByMx(recepcion.getTomaMx().getIdTomaMx());
            List<DaSolicitudEstudio> solicitudEList = tomaMxService.getSolicitudesEstudioByIdTomaMx(recepcion.getTomaMx().getIdTomaMx());


            Map<Integer, Object> mapDxList = new HashMap<Integer, Object>();
            Map<String, String> mapDx = new HashMap<String, String>();
            int subIndice=0;

            if(!solicitudDxList.isEmpty()){
                for(DaSolicitudDx solicitudDx: solicitudDxList){
                    mapDx.put("idSolicitud", solicitudDx.getIdSolicitudDx());
                    mapDx.put("nombre",solicitudDx.getCodDx().getNombre());
                    mapDx.put("fechaSolicitud", DateUtil.DateToString(solicitudDx.getFechaHSolicitud(), "dd/MM/yyyy hh:mm:ss a"));
                    subIndice++;
                    mapDxList.put(subIndice,mapDx);
                    mapDx = new HashMap<String, String>();
                }
            }else{
                for(DaSolicitudEstudio solicitudEstudio: solicitudEList){
                    mapDx.put("idSolicitud", solicitudEstudio.getIdSolicitudEstudio());
                    mapDx.put("nombre",solicitudEstudio.getTipoEstudio().getNombre());
                    mapDx.put("fechaSolicitud", DateUtil.DateToString(solicitudEstudio.getFechaHSolicitud(), "dd/MM/yyyy hh:mm:ss a"));
                    subIndice++;
                    mapDxList.put(subIndice,mapDx);
                    mapDx = new HashMap<String, String>();
                }
            }


            map.put("diagnosticos", new Gson().toJson(mapDxList));

            mapResponse.put(indice, map);
            indice ++;
        }
        jsonResponse = new Gson().toJson(mapResponse);
        //escapar caracteres especiales, escape de los caracteres con valor numérico mayor a 127
        UnicodeEscaper escaper     = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
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
        String codigoUnicoMx = null;
        String codTipoSolicitud = null;
        String nombreSolicitud = null;


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
        if (jObjectFiltro.get("codigoUnicoMx") != null && !jObjectFiltro.get("codigoUnicoMx").getAsString().isEmpty())
            codigoUnicoMx = jObjectFiltro.get("codigoUnicoMx").getAsString();
        if (jObjectFiltro.get("codTipoSolicitud") != null && !jObjectFiltro.get("codTipoSolicitud").getAsString().isEmpty())
            codTipoSolicitud = jObjectFiltro.get("codTipoSolicitud").getAsString();
        if (jObjectFiltro.get("nombreSolicitud") != null && !jObjectFiltro.get("nombreSolicitud").getAsString().isEmpty())
            nombreSolicitud = jObjectFiltro.get("nombreSolicitud").getAsString();

        filtroMx.setCodSilais(codSilais);
        filtroMx.setCodUnidadSalud(codUnidadSalud);
        filtroMx.setFechaInicioRecepLab(fecInicioRecepcionLab);
        filtroMx.setFechaFinRecepLab(fecFinRecepcionLab);
        filtroMx.setNombreApellido(nombreApellido);
        filtroMx.setCodTipoMx(codTipoMx);
        filtroMx.setCodEstado("ESTDMX|RCLAB"); // recepcionadas en lab
        filtroMx.setIncluirMxInadecuada(true);
        filtroMx.setCodigoUnicoMx(codigoUnicoMx);
        filtroMx.setCodTipoSolicitud(codTipoSolicitud);
        filtroMx.setNombreSolicitud(nombreSolicitud);
        filtroMx.setNombreUsuario(seguridadService.obtenerNombreUsuario());

        return filtroMx;
    }


    @RequestMapping(value = "create/{idSolicitud}", method = RequestMethod.GET)
    public ModelAndView initCreationForm(@PathVariable("idSolicitud") String idSolicitud, HttpServletRequest request) throws Exception {
        logger.debug("Iniciando la generación de alicuotas");
        String urlValidacion = "";
        try {
            urlValidacion = seguridadService.validarLogin(request);
            //si la url esta vacia significa que la validación del login fue exitosa
            if (urlValidacion.isEmpty())
                urlValidacion = seguridadService.validarAutorizacionUsuario(request, ConstantsSecurity.SYSTEM_CODE, false);
        } catch (Exception e) {
            e.printStackTrace();
            urlValidacion = "404";
        }
        ModelAndView mav = new ModelAndView();
        if (urlValidacion.isEmpty()) {
            if (idSolicitud != null) {
                RecepcionMx recepcionMx = null;
                DaSolicitudEstudio soliE = tomaMxService.getSolicitudEstByIdSolicitud(idSolicitud);
                DaSolicitudDx soliDx = tomaMxService.getSolicitudDxByIdSolicitud(idSolicitud);

                if (soliE != null) {
                    recepcionMx = recepcionMxService.getRecepcionMxByCodUnicoMx(soliE.getIdTomaMx().getCodigoUnicoMx());
                } else {
                    recepcionMx = recepcionMxService.getRecepcionMxByCodUnicoMx(soliDx.getIdTomaMx().getCodigoUnicoMx());
                }

                //obtener la descripcion del estudio o solicitud de la muestra
                List<Alicuota> alicuotaCat = null;
                List<AlicuotaRegistro> aliqRec = null;


                if (soliDx != null) {
                    alicuotaCat = separacionMxService.getAlicuotasByTRecSoliTMx(soliDx.getCodDx().getIdDiagnostico(), recepcionMx.getTomaMx().getCodTipoMx().getIdTipoMx(), recepcionMx.getTipoRecepcionMx().getCodigo());

                    //buscar registros de alicuotas
                    aliqRec = separacionMxService.getAliquotsRecordsByCodigoUnicoMx(soliDx.getIdTomaMx().getCodigoUnicoMx());
                } else {
                    alicuotaCat = separacionMxService.getAlicuotasByTRecSoliTMx(soliE.getTipoEstudio().getIdEstudio(), recepcionMx.getTomaMx().getCodTipoMx().getIdTipoMx(), recepcionMx.getTipoRecepcionMx().getCodigo());

                    //buscar registros de alicuotas
                    aliqRec = separacionMxService.getAliquotsRecordsByCodigoUnicoMx(soliE.getIdTomaMx().getCodigoUnicoMx());

                }

                //generar creacion de alicuotas segun tipo de recepcion
                if (aliqRec.isEmpty()) {

                    //generar alicuotas para solicitud de estudios
                    if (soliE != null) {
                        String codigo = soliE.getIdTomaMx().getCodigoUnicoMx();
                        String clasificacion = null;
                        Integer idTipoMx = soliE.getIdTomaMx().getCodTipoMx().getIdTipoMx();
                        Integer idEstudio = soliE.getTipoEstudio().getIdEstudio();
                        String nombreMx = soliE.getIdTomaMx().getCodTipoMx().getNombre();
                        String nombreEstudio = soliE.getTipoEstudio().getNombre();
                        //caso de estudio cohorte
                        switch (nombreEstudio) {
                            case "Cohorte Dengue":
                                    generarAliqEstudioCohorteDengue(soliE, request);
                                break;
                            case "Clínico Dengue":
                                    generarAliqEstudioClinicoDengue(soliE, request);
                                break;
                            case "Cohorte Influenza":
                                generarAligEstudioCohorteInfluenza(soliE,request);
                                break;
                            case "Transmisión Influenza":
                                generarAliqEstudioTransmisionInfluenza(soliE, request);
                                break;
                        }
                    }else{
                        String nombreDx = soliDx.getCodDx().getNombre();

                        if(nombreDx.equals("Diagnóstico Influenza")){
                            generarAliqDiagnosticoInfluenza(soliDx,request);
                        }
                    }

                }
                //cargar lista de etiquetas segun solicitud
                mav.addObject("alicuotaCat", alicuotaCat);
                mav.addObject("recepcionMx", recepcionMx);
                mav.addObject("soliE", soliE);
                mav.addObject("soliDx", soliDx);
                mav.setViewName("laboratorio/separacionMx/enterForm");
            }

        } else
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
        String idSoliE = "";
        String idSoliDx = "";


        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);

            etiqueta = jsonpObject.get("etiqueta").getAsString();
            volumen = jsonpObject.get("volumen").getAsString();
            codigoUnicoMx = jsonpObject.get("codigoUnicoMx").getAsString();
            idSoliDx = jsonpObject.get("idSoliDx").getAsString();
            idSoliE = jsonpObject.get("idSoliE").getAsString();


            long idUsuario = seguridadService.obtenerIdUsuario(request);
            Usuarios usuario = usuarioService.getUsuarioById((int) idUsuario);
            boolean consecutivo = true;


            //se obtiene Catalogo Alicuota por Id
            Alicuota aliCat = alicuotaService.getAlicuota(Integer.valueOf(etiqueta));

            //se obtiene el codigo etiqueta
            String etiq = aliCat.getEtiquetaPara();

            DaSolicitudEstudio soliEstudio = tomaMxService.getSolicitudEstByIdSolicitud(idSoliE);
            DaSolicitudDx soliDx = tomaMxService.getSolicitudDxByIdSolicitud(idSoliDx);

            AlicuotaRegistro alicuotaReg = new AlicuotaRegistro();

            alicuotaReg.setUsuarioRegistro(usuario);
            alicuotaReg.setFechaHoraRegistro(new Timestamp(new Date().getTime()));
            alicuotaReg.setSolicitudEstudio(soliEstudio);
            alicuotaReg.setSolicitudDx(soliDx);
            alicuotaReg.setVolumen(Float.valueOf(volumen));
            alicuotaReg.setAlicuotaCatalogo(aliCat);
            DaTomaMx tomaMx = tomaMxService.getTomaMxByCodUnicoMx(codigoUnicoMx);
            alicuotaReg.setCodUnicoMx(tomaMx);


            if(soliEstudio!= null){
                if(soliEstudio.getTipoEstudio().getIdEstudio().equals(2)){
                    consecutivo = false;
                }
            }
            //Se genera código único de alicuota segun etiqueta seleccionada
            idAlicuota = generarCodigoAlicuota(codigoUnicoMx, etiq, consecutivo);

            if(idAlicuota == null){
                resultado = messageSource.getMessage("msg.add.aliquot.error1", null, null);
                throw new Exception(resultado);
            }else{
                alicuotaReg.setIdAlicuota(idAlicuota);
                try {
                    separacionMxService.addAliquot(alicuotaReg);
                } catch (Exception ex) {
                    resultado = messageSource.getMessage("msg.add.aliquot.error", null, null);
                    resultado = resultado + ". \n " + ex.getMessage();
                    ex.printStackTrace();
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            resultado = messageSource.getMessage("msg.aliquot.error", null, null);
            resultado = resultado + ". \n " + ex.getMessage();

        } finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("idAlicuota", idAlicuota);
            map.put("mensaje", resultado);
            map.put("etiqueta", etiqueta);
            map.put("volumen", volumen);
            map.put("idSoliE", idSoliE);
            map.put("idSoliDx", idSoliDx);
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
    private String generarCodigoAlicuota(String codigo, String etiqueta, boolean consecutivo) {
        Long cantidadReg;
        String alicuotaId;

        //se genera el codigo sin consecutivo en caso de false

        if(!consecutivo){
            alicuotaId = codigo + etiqueta;
            //Buscar registros con el idAlicuota
            AlicuotaRegistro registro = separacionMxService.getAliquotById(alicuotaId);
            if(registro != null){
                alicuotaId = null;
            }

        }else{
            //Se consulta el ultimo registro realizado para la etiqueta de alicuota
            String id = codigo + etiqueta;
            cantidadReg = separacionMxService.cantidadAlicuotas(id);
            //Asignacion de codigo segun cantidad de registros encontrados

            Long suma = cantidadReg + 1;
            alicuotaId = codigo + etiqueta + suma;
        }

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
            aliquotsList = separacionMxService.getAliquotsById(codigoUnicoMx);

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
        AlicuotaRegistro  alic = separacionMxService.getAliquotById(idAlicuota);
        alic.setPasivo(true);
        separacionMxService.updateAlicuotaReg(alic);
        String codUnicoMx = alic.getCodUnicoMx().getCodigoUnicoMx();

        return  "redirect:/separacionMx/create/" + codUnicoMx;
    }

    @RequestMapping(value = "getVolume", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<String> getVolume(@RequestParam(value = "idAlicuota", required = true) Integer idAlicuota) throws Exception{
        logger.info("Obteniendo info alicuota por id");
        String alicuota = separacionMxService.getAliquotVolumeById(idAlicuota);
        return createJsonResponse(alicuota);
    }

    private ResponseEntity<String> createJsonResponse(Object o) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        Gson gson = new Gson();
        String json = gson.toJson(o);
        return new ResponseEntity<>(json, headers, HttpStatus.CREATED);
    }

    private void addAliq(HttpServletRequest request, Integer quantity, Alicuota alicuotaCat, String codigoUnico, DaSolicitudEstudio estudio, DaSolicitudDx dx ) throws Exception {
        AlicuotaRegistro alicuotaReg = new AlicuotaRegistro();
        long idUsuario = seguridadService.obtenerIdUsuario(request);
        Usuarios usuario = usuarioService.getUsuarioById((int) idUsuario);
        boolean consecutivo = true;

        for(int i = 0; i < quantity; i++ ){
            //en caso de ser estudio clínico, alicuotas no llevan consecutivo
            if(estudio != null){
                if(estudio.getTipoEstudio().getIdEstudio().equals(2)){
                    consecutivo = false;
                }
            }
            //generar codigo unico
            String idAlicuota = generarCodigoAlicuota(codigoUnico, alicuotaCat.getEtiquetaPara(), consecutivo);
            DaTomaMx tomaMx = tomaMxService.getTomaMxByCodUnicoMx(codigoUnico);
            alicuotaReg.setAlicuotaCatalogo(alicuotaCat);
            alicuotaReg.setCodUnicoMx(tomaMx);
            alicuotaReg.setIdAlicuota(idAlicuota);
            alicuotaReg.setSolicitudEstudio(estudio);
            alicuotaReg.setSolicitudDx(dx);
            alicuotaReg.setUsuarioRegistro(usuario);
            alicuotaReg.setFechaHoraRegistro(new Timestamp(new Date().getTime()));
            separacionMxService.addAliquot(alicuotaReg);

        }
    }

    private void generarAliqEstudioCohorteDengue(DaSolicitudEstudio soliE, HttpServletRequest request) throws Exception{
        String codigo = soliE.getIdTomaMx().getCodigoUnicoMx();
        String clasificacion = null;
        Integer idTipoMx = soliE.getIdTomaMx().getCodTipoMx().getIdTipoMx();
        Integer idEstudio = soliE.getTipoEstudio().getIdEstudio();
        String nombreMx = soliE.getIdTomaMx().getCodTipoMx().getNombre();
        String categoria = soliE.getIdTomaMx().getCategoriaMx().getValor();
        if (codigo.contains("."))
            clasificacion = codigo.substring(codigo.lastIndexOf(".") + 1);
        if (clasificacion != null) {
            //En caso de ser una mx gategoria A y de clasificacion Aguda (1) realizar la generacion de 9 alicuotas

            //si el tipo de mx es suero hacer la generacion las 9 alicuotas
            if (nombreMx.equals("Suero")) {
                if (clasificacion.equals("1") && (categoria.equals("A") || categoria.equals("B"))) {
                    //crear 1 alicuota Serologia
                    //obtener alicuota catalogo para serologia
                    Alicuota alicSero = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "SER");
                    if (alicSero != null) {
                        addAliq(request, 1, alicSero, codigo, soliE, null);
                    }

                    //obtener alicuota catalogo para PCR
                    Alicuota alicuotaPCR = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "PC");
                    if (alicuotaPCR != null) {
                        addAliq(request, 3, alicuotaPCR, codigo, soliE, null);
                    }

                    //obtener alicuota catalogo para Aislamiento Viral
                    Alicuota alicuotaAV = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "AV");
                    if (alicuotaAV != null) {
                        addAliq(request, 3, alicuotaAV, codigo, soliE, null);
                    }

                    //Obtener alicuota catalogo para Archivo
                    Alicuota alicuotaAR = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "AR");
                    if (alicuotaAR != null) {
                        addAliq(request, 2, alicuotaAR, codigo, soliE, null);
                    }
                } else if (clasificacion.equals("2") && (categoria.equals("A") || categoria.equals("B"))) {
                    //En caso de ser una mx gategoria A y de clasificacion Convaleciente (2) realizar la generacion de 3 alicuotas
                    //obtener alicuota catalogo para serologia
                    Alicuota alicSero = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "SER");
                    if (alicSero != null) {
                        addAliq(request, 1, alicSero, codigo, soliE, null);
                    }

                    //Obtener alicuota catalogo para Archivo
                    Alicuota alicuotaAR = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "AR");
                    if (alicuotaAR != null) {
                        addAliq(request, 2, alicuotaAR, codigo, soliE, null);
                    }

                }
            }
        }
    }

    private void generarAliqEstudioClinicoDengue(DaSolicitudEstudio soliE, HttpServletRequest request) throws Exception {
        String codigo = soliE.getIdTomaMx().getCodigoUnicoMx();
        String clasificacion = null;
        Integer idTipoMx = soliE.getIdTomaMx().getCodTipoMx().getIdTipoMx();
        Integer idEstudio = soliE.getTipoEstudio().getIdEstudio();
        String nombreMx = soliE.getIdTomaMx().getCodTipoMx().getNombre();
        if (codigo.contains("."))
            clasificacion = codigo.substring(codigo.lastIndexOf(".") + 1);
        if (clasificacion != null) {
            //En caso de ser una mx aguda (1) realizar generacion de alicuotas de todas las alicuotas del tipo de la mx tomada
            switch (clasificacion) {
                case "1":
                    //Realizar la generacion de alicuotas segun tipo de mx
                    //Mx suero
                    switch (nombreMx) {
                        case "Suero":
                            //Obtener alicuota para PCR (b)
                            Alicuota alicuotaPCRb = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "b");
                            if (alicuotaPCRb != null) {
                                addAliq(request, 1, alicuotaPCRb, codigo, soliE, null);
                            }
                            //Obtener alicuota para PCR (c)
                            Alicuota alicuotaPCRc = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "c");
                            if (alicuotaPCRc != null) {
                                addAliq(request, 1, alicuotaPCRc, codigo, soliE, null);
                            }

                            //Obtener alicuota para PCR(d)
                            Alicuota alicuotaPCRd = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "d");
                            if (alicuotaPCRd != null) {
                                addAliq(request, 1, alicuotaPCRd, codigo, soliE, null);
                            }

                            //Obtener alicuota para AV(e)
                            Alicuota alicuotaAVe = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "e");
                            if (alicuotaAVe != null) {
                                addAliq(request, 1, alicuotaAVe, codigo, soliE, null);
                            }

                            //Obtener alicuota para AV(f)
                            Alicuota alicuotaAVf = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "f");
                            if (alicuotaAVf != null) {
                                addAliq(request, 1, alicuotaAVf, codigo, soliE, null);
                            }

                            //Obtener alicuota para AV(g)
                            Alicuota alicuotaAVg = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "g");
                            if (alicuotaAVg != null) {
                                addAliq(request, 1, alicuotaAVg, codigo, soliE, null);
                            }

                            //Obtener alicuota para archivo (h)
                            Alicuota alicuotah = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "h");
                            if (alicuotah != null) {
                                addAliq(request, 1, alicuotah, codigo, soliE, null);
                            }

                            //Obtener alicuota para archivo (i)
                            Alicuota alicuotai = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "i");
                            if (alicuotai != null) {
                                addAliq(request, 1, alicuotai, codigo, soliE, null);
                            }

                            //Obtener alicuota para NS1
                            Alicuota alicuotaS = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "s");
                            if (alicuotaS != null) {
                                addAliq(request, 1, alicuotaS, codigo, soliE, null);
                            }

                            //Obtener alicuota para viremia
                            Alicuota alicuotat = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "t");
                            if (alicuotat != null) {
                                addAliq(request, 1, alicuotat, codigo, soliE, null);
                            }

                            //Obtener alicuota para TDR
                            Alicuota alicuotau = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "u");
                            if (alicuotau != null) {
                                addAliq(request, 1, alicuotau, codigo, soliE, null);
                            }

                            break;
                        case "Plasma":
                            //Obtener alicuota para serologia (a)
                            Alicuota alicuotaa = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "a");
                            if (alicuotaa != null) {
                                addAliq(request, 1, alicuotaa, codigo, soliE, null);
                            }

                            //Obtener alicuota para serologia (j)
                            Alicuota alicuotaj = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "j");
                            if (alicuotaj != null) {
                                addAliq(request, 1, alicuotaj, codigo, soliE, null);
                            }

                            //Obtener alicuota para serologia (k)
                            Alicuota alicuotak = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "k");
                            if (alicuotak != null) {
                                addAliq(request, 1, alicuotak, codigo, soliE, null);
                            }

                            //Obtener alicuota para serologia (xx)
                            Alicuota alicuotaxx = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "xx");
                            if (alicuotaxx != null) {
                                addAliq(request, 1, alicuotaxx, codigo, soliE, null);
                            }

                            //Obtener alicuota para serologia (RV1)
                            Alicuota alicuotaRV1 = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "RV1");
                            if (alicuotaRV1 != null) {
                                addAliq(request, 1, alicuotaRV1, codigo, soliE, null);
                            }

                            //Obtener alicuota para serologia (RV2)
                            Alicuota alicuotaRV2 = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "RV2");
                            if (alicuotaRV2 != null) {
                                addAliq(request, 1, alicuotaRV2, codigo, soliE, null);
                            }

                            break;
                        case "PBMC":
                            //Obtener alicuota para PBMC (m)
                            Alicuota alicuotam = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "m");
                            if (alicuotam != null) {
                                addAliq(request, 1, alicuotam, codigo, soliE, null);
                            }

                            //Obtener alicuota para PBMC (n)
                            Alicuota alicuotan = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "n");
                            if (alicuotan != null) {
                                addAliq(request, 1, alicuotan, codigo, soliE, null);
                            }

                            //Obtener alicuota para PBMC (o)
                            Alicuota alicuotao = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "o");
                            if (alicuotao != null) {
                                addAliq(request, 1, alicuotao, codigo, soliE, null);
                            }
                            break;
                        case "Adn":
                            //Obtener alicuota para mcmaster (r)
                            Alicuota alicuotar = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "r");
                            if (alicuotar != null) {
                                addAliq(request, 1, alicuotar, codigo, soliE, null);
                            }
                            break;
                        case "Saliva":
                            //Obtener alicuota para metabolomica (v1)
                            Alicuota alicuotav1 = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "v1");
                            if (alicuotav1 != null) {
                                addAliq(request, 1, alicuotav1, codigo, soliE, null);
                            }

                            //Obtener alicuota para metabolomica (v2)
                            Alicuota alicuotav2 = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "v2");
                            if (alicuotav2 != null) {
                                addAliq(request, 1, alicuotav2, codigo, soliE, null);
                            }

                            break;
                        case "Orina":
                            //Obtener alicuota para metabolomica (w1)
                            Alicuota alicuotaw1 = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "w1");
                            if (alicuotaw1 != null) {
                                addAliq(request, 1, alicuotaw1, codigo, soliE, null);
                            }

                            //Obtener alicuota para metabolomica (w2)
                            Alicuota alicuotaw2 = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "w2");
                            if (alicuotaw2 != null) {
                                addAliq(request, 1, alicuotaw2, codigo, soliE, null);
                            }

                            break;
                        case "PAXGENE":
                            //Obtener alicuota para Q1
                            Alicuota alicuotaQ1 = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "Q1");
                            if (alicuotaQ1 != null) {
                                addAliq(request, 1, alicuotaQ1, codigo, soliE, null);
                            }

                            //Obtener alicuota para Q2
                            Alicuota alicuotaQ2 = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "Q2");
                            if (alicuotaQ2 != null) {
                                addAliq(request, 1, alicuotaQ2, codigo, soliE, null);
                            }
                            break;
                    }
                    //En caso de ser una mx convaleciente (9)
                    break;
                case "9":
                    //si un tipo de mx plasma
                    switch (nombreMx) {
                        case "Plasma":
                            //Obtener alicuota para serologia (a)
                            Alicuota alicuotaa = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "a");
                            if (alicuotaa != null) {
                                addAliq(request, 1, alicuotaa, codigo, soliE, null);
                            }

                            //Obtener alicuota para serologia (j)
                            Alicuota alicuotaj = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "j");
                            if (alicuotaj != null) {
                                addAliq(request, 1, alicuotaj, codigo, soliE, null);
                            }

                            //Obtener alicuota para serologia (k)
                            Alicuota alicuotak = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "k");
                            if (alicuotak != null) {
                                addAliq(request, 1, alicuotak, codigo, soliE, null);
                            }

                            //Obtener alicuota para serologia (xx)
                            Alicuota alicuotaxx = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "xx");
                            if (alicuotaxx != null) {
                                addAliq(request, 1, alicuotaxx, codigo, soliE, null);
                            }

                            //Obtener alicuota para serologia (RV1)
                            Alicuota alicuotaRV1 = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "RV1");
                            if (alicuotaRV1 != null) {
                                addAliq(request, 1, alicuotaRV1, codigo, soliE, null);
                            }

                            //Obtener alicuota para serologia (RV2)
                            Alicuota alicuotaRV2 = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "RV2");
                            if (alicuotaRV2 != null) {
                                addAliq(request, 1, alicuotaRV2, codigo, soliE, null);
                            }
                            break;
                        case "Suero":
                            //Obtener alicuota para TDR
                            Alicuota alicuotau = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "u");
                            if (alicuotau != null) {
                                addAliq(request, 1, alicuotau, codigo, soliE, null);
                            }
                            break;
                        case "PBMC":
                            //Obtener alicuota para PBMC (m)
                            Alicuota alicuotam = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "m");
                            if (alicuotam != null) {
                                addAliq(request, 1, alicuotam, codigo, soliE, null);
                            }

                            //Obtener alicuota para PBMC (n)
                            Alicuota alicuotan = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "n");
                            if (alicuotan != null) {
                                addAliq(request, 1, alicuotan, codigo, soliE, null);
                            }

                            //Obtener alicuota para PBMC (o)
                            Alicuota alicuotao = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "o");
                            if (alicuotao != null) {
                                addAliq(request, 1, alicuotao, codigo, soliE, null);
                            }
                            break;
                        case "Adn":
                            //Obtener alicuota para mcmaster (r)
                            Alicuota alicuotar = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "r");
                            if (alicuotar != null) {
                                addAliq(request, 1, alicuotar, codigo, soliE, null);
                            }
                            break;
                        case "Saliva":
                            //Obtener alicuota para metabolomica (v1)
                            Alicuota alicuotav1 = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "v1");
                            if (alicuotav1 != null) {
                                addAliq(request, 1, alicuotav1, codigo, soliE, null);
                            }

                            //Obtener alicuota para metabolomica (v2)
                            Alicuota alicuotav2 = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "v2");
                            if (alicuotav2 != null) {
                                addAliq(request, 1, alicuotav2, codigo, soliE, null);
                            }
                            break;
                        case "Orina":
                            //Obtener alicuota para metabolomica (w1)
                            Alicuota alicuotaw1 = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "w1");
                            if (alicuotaw1 != null) {
                                addAliq(request, 1, alicuotaw1, codigo, soliE, null);
                            }

                            //Obtener alicuota para metabolomica (w2)
                            Alicuota alicuotaw2 = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "w2");
                            if (alicuotaw2 != null) {
                                addAliq(request, 1, alicuotaw2, codigo, soliE, null);
                            }

                            break;
                        case "PAXGENE":
                            //Obtener alicuota para Q1
                            Alicuota alicuotaQ1 = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "Q1");
                            if (alicuotaQ1 != null) {
                                addAliq(request, 1, alicuotaQ1, codigo, soliE, null);
                            }

                            //Obtener alicuota para Q2
                            Alicuota alicuotaQ2 = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "Q2");
                            if (alicuotaQ2 != null) {
                                addAliq(request, 1, alicuotaQ2, codigo, soliE, null);
                            }
                            break;
                    }
                    //
                    break;
                case "2":
                case "3":
                    //Generacion de Alicuotas segun tipo de mx
                    switch (nombreMx) {
                        case "Plasma":

                            //Obtener alicuota para serologia (j)
                            Alicuota alicuotaj = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "j");
                            if (alicuotaj != null) {
                                addAliq(request, 1, alicuotaj, codigo, soliE, null);
                            }

                            //Obtener alicuota para serologia (k)
                            Alicuota alicuotak = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "k");
                            if (alicuotak != null) {
                                addAliq(request, 1, alicuotak, codigo, soliE, null);
                            }

                            //Obtener alicuota para serologia (xx)
                            Alicuota alicuotaxx = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "xx");
                            if (alicuotaxx != null) {
                                addAliq(request, 1, alicuotaxx, codigo, soliE, null);
                            }
                            break;
                        case "PBMC":
                            //Obtener alicuota para PBMC (m)
                            Alicuota alicuotam = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "m");
                            if (alicuotam != null) {
                                addAliq(request, 1, alicuotam, codigo, soliE, null);
                            }

                            //Obtener alicuota para PBMC (n)
                            Alicuota alicuotan = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "n");
                            if (alicuotan != null) {
                                addAliq(request, 1, alicuotan, codigo, soliE, null);
                            }

                            //Obtener alicuota para PBMC (o)
                            Alicuota alicuotao = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "o");
                            if (alicuotao != null) {
                                addAliq(request, 1, alicuotao, codigo, soliE, null);
                            }
                            break;
                        case "Adn":
                            //Obtener alicuota para mcmaster (r)
                            Alicuota alicuotar = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "r");
                            if (alicuotar != null) {
                                addAliq(request, 1, alicuotar, codigo, soliE, null);
                            }
                            break;
                        case "Suero":
                            //Obtener alicuota para TDR
                            Alicuota alicuotau = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "u");
                            if (alicuotau != null) {
                                addAliq(request, 1, alicuotau, codigo, soliE, null);
                            }
                            break;
                        case "Saliva":
                            //Obtener alicuota para metabolomica (v1)
                            Alicuota alicuotav1 = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "v1");
                            if (alicuotav1 != null) {
                                addAliq(request, 1, alicuotav1, codigo, soliE, null);
                            }

                            //Obtener alicuota para metabolomica (v2)
                            Alicuota alicuotav2 = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "v2");
                            if (alicuotav2 != null) {
                                addAliq(request, 1, alicuotav2, codigo, soliE, null);
                            }
                            break;
                        case "Orina":
                            //Obtener alicuota para metabolomica (w1)
                            Alicuota alicuotaw1 = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "w1");
                            if (alicuotaw1 != null) {
                                addAliq(request, 1, alicuotaw1, codigo, soliE, null);
                            }

                            //Obtener alicuota para metabolomica (w2)
                            Alicuota alicuotaw2 = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "w2");
                            if (alicuotaw2 != null) {
                                addAliq(request, 1, alicuotaw2, codigo, soliE, null);
                            }
                            break;
                        case "PAXGENE":
                            //Obtener alicuota para Q1
                            Alicuota alicuotaQ1 = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "Q1");
                            if (alicuotaQ1 != null) {
                                addAliq(request, 1, alicuotaQ1, codigo, soliE, null);
                            }

                            //Obtener alicuota para Q2
                            Alicuota alicuotaQ2 = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "Q2");
                            if (alicuotaQ2 != null) {
                                addAliq(request, 1, alicuotaQ2, codigo, soliE, null);
                            }
                            break;
                    }
                    break;
            }
        }
    }

    private void generarAligEstudioCohorteInfluenza(DaSolicitudEstudio soliE, HttpServletRequest request) throws Exception {
        String codigo = soliE.getIdTomaMx().getCodigoUnicoMx();
        Integer idTipoMx = soliE.getIdTomaMx().getCodTipoMx().getIdTipoMx();
        Integer idEstudio = soliE.getTipoEstudio().getIdEstudio();
        //Generar 2 alicuotas PCR
        Alicuota alicuotaPCR = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "PC");
        if (alicuotaPCR != null) {
            addAliq(request, 2, alicuotaPCR, codigo, soliE, null);
        }

        //Generar 1 alicuota Aislamiento Viral
        Alicuota alicuotaAV = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "AV");
        if (alicuotaAV != null) {
            addAliq(request, 1, alicuotaAV, codigo, soliE, null);
        }

        //Generar 2 alicuotas para archivo
        Alicuota alicuotaAR = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx, idEstudio, "AR");
        if (alicuotaAR != null) {
            addAliq(request, 2, alicuotaAR, codigo, soliE, null);
        }
    }

    private void generarAliqEstudioTransmisionInfluenza(DaSolicitudEstudio soliE, HttpServletRequest request) throws Exception {
        String codigo = soliE.getIdTomaMx().getCodigoUnicoMx();
        Integer idTipoMx = soliE.getIdTomaMx().getCodTipoMx().getIdTipoMx();
        Integer idEstudio = soliE.getTipoEstudio().getIdEstudio();
        String nombreMx = soliE.getIdTomaMx().getCodTipoMx().getNombre();
        if(nombreMx.equals("Hisopado")){
            //Generar 1 alicuota para PC
            Alicuota alicuotaPC = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx,idEstudio,"PC");
            if(alicuotaPC != null){
                addAliq(request,1,alicuotaPC,codigo,soliE,null);
            }

            //Generar 1 alicuota para Archivo
            Alicuota alicuotaARC = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx,idEstudio,"AR");
            if(alicuotaARC != null){
                addAliq(request,1,alicuotaARC,codigo,soliE,null);
            }

            //Generar 1 alicuota para Aislamiento Viral
            Alicuota alicuotaAisV = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx,idEstudio,"AV");
            if(alicuotaAisV != null){
                addAliq(request,1,alicuotaAisV,codigo,soliE,null);
            }

        }

        if(nombreMx.equals("Suero")){
            //Generar 2 alicuotas archivo
            Alicuota alicuotaArchivo = separacionMxService.getAliquotCatByTipoMxEstudioEtiqueta(idTipoMx,idEstudio,"AR");
            if(alicuotaArchivo != null){
                addAliq(request,2,alicuotaArchivo,codigo,soliE,null);
            }
        }
    }

    private void generarAliqDiagnosticoInfluenza(DaSolicitudDx soliDx, HttpServletRequest request) throws Exception{
        String codigo = soliDx.getIdTomaMx().getCodigoUnicoMx();
        Integer idTipoMx = soliDx.getIdTomaMx().getCodTipoMx().getIdTipoMx();
        Integer idDx = soliDx.getCodDx().getIdDiagnostico();
        String nombreMx = soliDx.getIdTomaMx().getCodTipoMx().getNombre();
        //generar 1 alicuota PCR
        Alicuota alicuotaPCR = separacionMxService.getAliquotCatByTipoMxDxEtiqueta(idTipoMx,idDx,"PC" );
        if(alicuotaPCR != null){
            addAliq(request,1,alicuotaPCR,codigo,null,soliDx);
        }

        //generar 1 alicuota archivo
        Alicuota alicuotaAR = separacionMxService.getAliquotCatByTipoMxDxEtiqueta(idTipoMx,idDx,"AR" );
        if(alicuotaAR != null){
            addAliq(request,1,alicuotaAR,codigo,null,soliDx);
        }
    }

    @RequestMapping(value = "impresionMasiva" ,method = RequestMethod.GET, produces = "application/json" )
    public ResponseEntity<String> impresionMasiva(@RequestParam(value = "idSolicitudes", required = true) String idSolicitudes, HttpServletRequest request) throws Exception {
        String idAlicuotasImprimir = "";
        if (!idSolicitudes.isEmpty()){
            String[] idSolicitudesArray = idSolicitudes.replaceAll("\\*","-").split(",");
            for(String idSolicitud : idSolicitudesArray){
                idAlicuotasImprimir += generarAliqSolicitud(idSolicitud, request);
            }
            if (!idAlicuotasImprimir.isEmpty()){
                //si el último caracter en el string es una coma, la borramos para que no de errores a la hora de imprimir
                char ultimoCaracter = idAlicuotasImprimir.charAt(idAlicuotasImprimir.length()-1);
                if (ultimoCaracter == ','){
                    idAlicuotasImprimir = idAlicuotasImprimir.substring(0,idAlicuotasImprimir.length()-1);
                }
            }
        }
        return createJsonResponse(idAlicuotasImprimir);
    }

    private String generarAliqSolicitud(String idSolicitud, HttpServletRequest request) throws Exception{
        String idAlicuotas = "";
        if (idSolicitud != null) {
            DaSolicitudEstudio soliE = tomaMxService.getSolicitudEstByIdSolicitud(idSolicitud);
            DaSolicitudDx soliDx = tomaMxService.getSolicitudDxByIdSolicitud(idSolicitud);

            if (soliDx != null || soliE != null) {
                //obtener la descripcion del estudio o solicitud de la muestra
                List<AlicuotaRegistro> aliqRec = null;

                if (soliDx != null) {
                    //buscar registros de alicuotas
                    aliqRec = separacionMxService.getAliquotsRecordsByCodigoUnicoMx(soliDx.getIdTomaMx().getCodigoUnicoMx());
                } else {
                    //buscar registros de alicuotas
                    aliqRec = separacionMxService.getAliquotsRecordsByCodigoUnicoMx(soliE.getIdTomaMx().getCodigoUnicoMx());
                }
                //generar creacion de alicuotas segun tipo de recepcion
                if (aliqRec.isEmpty()) {

                    //generar alicuotas para solicitud de estudios
                    if (soliE != null) {
                        String nombreEstudio = soliE.getTipoEstudio().getNombre();
                        //caso de estudio cohorte
                        switch (nombreEstudio) {
                            case "Cohorte Dengue":
                                generarAliqEstudioCohorteDengue(soliE, request);
                                break;
                            case "Clínico Dengue":
                                generarAliqEstudioClinicoDengue(soliE, request);
                                break;
                            case "Cohorte Influenza":
                                generarAligEstudioCohorteInfluenza(soliE, request);
                                break;
                            case "Transmisión Influenza":
                                generarAliqEstudioTransmisionInfluenza(soliE, request);
                                break;
                        }
                    } else {
                        String nombreDx = soliDx.getCodDx().getNombre();

                        if (nombreDx.equals("Diagnóstico Influenza")) {
                            generarAliqDiagnosticoInfluenza(soliDx, request);
                        }
                    }
                    //se consultan las alicuotas recien registradas
                    if (soliDx != null) {
                        //buscar registros de alicuotas
                        aliqRec = separacionMxService.getAliquotsRecordsByCodigoUnicoMx(soliDx.getIdTomaMx().getCodigoUnicoMx());
                    } else {
                        //buscar registros de alicuotas
                        aliqRec = separacionMxService.getAliquotsRecordsByCodigoUnicoMx(soliE.getIdTomaMx().getCodigoUnicoMx());
                    }
                }
                for (AlicuotaRegistro alicuotaRegistro : aliqRec) {
                    idAlicuotas = idAlicuotas + alicuotaRegistro.getIdAlicuota() + ",";
                }
            }

        }
        return idAlicuotas;
    }
}
