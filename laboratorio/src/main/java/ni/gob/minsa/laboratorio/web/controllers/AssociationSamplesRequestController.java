package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.muestra.*;
import ni.gob.minsa.laboratorio.domain.notificacion.TipoNotificacion;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.User;
import ni.gob.minsa.laboratorio.service.AssociationSamplesRequestService;
import ni.gob.minsa.laboratorio.service.CatalogoService;
import ni.gob.minsa.laboratorio.service.SampleTypesService;
import ni.gob.minsa.laboratorio.service.SeguridadService;
import ni.gob.minsa.laboratorio.utilities.ConstantsSecurity;
import org.apache.commons.lang3.text.translate.UnicodeEscaper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
 * Created by souyen-ics.
 */
@Controller
@RequestMapping("associationSR")
public class AssociationSamplesRequestController {

    private static final Logger logger = LoggerFactory.getLogger(AssociationSamplesRequestController.class);

    @Autowired
    @Qualifier(value = "seguridadService")
    private SeguridadService seguridadService;

    @Autowired
    @Qualifier(value = "catalogosService")
    private CatalogoService catalogosService;

    @Autowired
    @Qualifier(value = "associationSR")
    private AssociationSamplesRequestService associationSR;

    @Resource(name = "sampleTypesService")
    public SampleTypesService sampleTypesService;

    @Autowired
    MessageSource messageSource;

    @RequestMapping(value = "init", method = RequestMethod.GET)
    public ModelAndView initForm(HttpServletRequest request) throws Exception {
        logger.debug("Cargando Lista de Notificaciones");
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
            List<TipoNotificacion> notiList = getNotif();
            List<Catalogo_Dx> catDx = associationSR.getDxs();
            List<Catalogo_Estudio> catEst = associationSR.getStudies();
            List<TipoMx> samplesList =  sampleTypesService.getSamplesList();
            mav.addObject("catDx",catDx);
            mav.addObject("catEst",catEst);
            mav.addObject("samplesList", samplesList);
            mav.setViewName("administracion/associationSamplesRequest");
        }else
            mav.setViewName(urlValidacion);

        return mav;
    }

    //Cargar lista de Tipos de Notificaciones
    @RequestMapping(value = "getNotif", method = RequestMethod.GET,  produces = "application/json")
    public @ResponseBody
    List<TipoNotificacion> getNotif() throws Exception {
        logger.info("Obteniendo los tipos de notificaciones");
        List<TipoNotificacion> notiList = null;
        notiList = catalogosService.getTipoNotificacion();
        return notiList;
    }

    //Cargar lista de mx asociadas a la notificacion
    @RequestMapping(value = "getMxNoti", method = RequestMethod.GET)
    public @ResponseBody List<TipoMx_TipoNotificacion> getMxNoti(@RequestParam(value = "codNoti", required = true) String codNoti) {
        logger.info("Obteniendo los tipos de muestra asociados a la notificacion");
        List<TipoMx_TipoNotificacion> mxNotiList = null;
        mxNotiList = associationSR.getMxNoti(codNoti);
        return mxNotiList;
    }

    //Cargar lista de solicitudes y estudios asociadas al tipo de mx y tipo noti
    @RequestMapping(value = "getRoutinesAndStudies", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String fetchDxJson(@RequestParam(value = "idTipoMxNoti", required = true) Integer idTipoMxNoti) throws Exception{
        logger.info("Obteniendo las rutinas y estudios asociados al tipoMxNoti");

        List<Dx_TipoMx_TipoNoti> dxList = null;
        List<Estudio_TipoMx_TipoNoti> estudioList = null;

        if(idTipoMxNoti != null){
            dxList = associationSR.getRoutines(idTipoMxNoti);
            estudioList = associationSR.getStudies(idTipoMxNoti);
        }

        return listToJson(dxList, estudioList);
    }

    private String listToJson(List<Dx_TipoMx_TipoNoti> dxList, List<Estudio_TipoMx_TipoNoti> estudioList) {
        String jsonResponse = "";
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice = 0;

        if (dxList != null) {
            for (Dx_TipoMx_TipoNoti dx : dxList) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("nombre", dx.getDiagnostico().getNombre());
                map.put("id", dx.getIdDxTipoMxNt().toString());
                map.put("idTipoMxTipoNoti", dx.getTipoMx_tipoNotificacion().getId().toString());
                map.put("tipoSolicitud",messageSource.getMessage("lbl.routine", null, null));
                map.put("nombreNotificacion", dx.getTipoMx_tipoNotificacion().getTipoNotificacion().getValor());
                map.put("tipoMx", dx.getTipoMx_tipoNotificacion().getTipoMx().getNombre());
                mapResponse.put(indice, map);
                indice++;
            }
        }

        if (estudioList != null) {
            for (Estudio_TipoMx_TipoNoti estudio : estudioList) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("nombre", estudio.getEstudio().getNombre());
                map.put("id", estudio.getIdEstTipoMxNt().toString());
                map.put("idTipoMxTipoNoti", estudio.getTipoMx_tipoNotificacion().getId().toString());
                map.put("tipoSolicitud",messageSource.getMessage("lbl.study", null, null));
                map.put("nombreNotificacion", estudio.getTipoMx_tipoNotificacion().getTipoNotificacion().getValor());
                map.put("tipoMx", estudio.getTipoMx_tipoNotificacion().getTipoMx().getNombre());
                mapResponse.put(indice, map);
                indice++;
            }
        }
        jsonResponse = new Gson().toJson(mapResponse);
        UnicodeEscaper escaper = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }

    @RequestMapping(value = "addUpdateTipoMxAss", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void addUpdateTipoMxAss(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json = "";
        String resultado = "";
        Integer idRecord = 0;
        String noti = "";
        String pasivo = "";
        Integer tipoMx = 0;

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);

            if(jsonpObject.get("noti") != null && !jsonpObject.get("noti").getAsString().isEmpty() ) {
                noti = jsonpObject.get("noti").getAsString();
            }

            if(jsonpObject.get("tipoMx") != null && !jsonpObject.get("tipoMx").getAsString().isEmpty()) {
                tipoMx = jsonpObject.get("tipoMx").getAsInt();
            }

            if(jsonpObject.get("idRecord") != null && !jsonpObject.get("idRecord").getAsString().isEmpty() ) {
                idRecord = jsonpObject.get("idRecord").getAsInt();
            }

            if(jsonpObject.get("pasivo") != null && !jsonpObject.get("pasivo").getAsString().isEmpty()){
                pasivo = jsonpObject.get("pasivo").getAsString();
            }

            User usuario = seguridadService.getUsuario(seguridadService.obtenerNombreUsuario());

            if(idRecord == 0) {
                if (tipoMx != 0 && noti != null) {
                    //search log
                    TipoMx_TipoNotificacion record = associationSR.getTMxNotiByIdAndNoti(tipoMx, noti);

                    if(record == null) {
                        TipoMx_TipoNotificacion tMxNoti = new TipoMx_TipoNotificacion();
                        tMxNoti.setFechaRegistro(new Timestamp(new Date().getTime()));
                        tMxNoti.setUsuarioRegistro(usuario);
                        tMxNoti.setPasivo(false);
                        tMxNoti.setTipoNotificacion(catalogosService.getTipoNotificacion(noti));
                        tMxNoti.setTipoMx(sampleTypesService.getTipoMxById(tipoMx));
                        associationSR.addOrUpdateTMxNoti(tMxNoti);
                    }else{
                        resultado = messageSource.getMessage("msg.existing.record.error", null, null);
                        throw new Exception(resultado);
                    }
                }

            } else {
                //override request
                TipoMx_TipoNotificacion rec = associationSR.getTipoMxNotiById(idRecord);
                if (rec != null) {
                    rec.setPasivo(true);
                    associationSR.addOrUpdateTMxNoti(rec);
                }
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            ex.printStackTrace();
            resultado = messageSource.getMessage("msg.add.AssTMxNoti.error1", null, null);
            resultado = resultado + ". \n " + ex.getMessage();

        } finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("id",idRecord.toString());
            map.put("mensaje", resultado);
            map.put("tipoMx", "" );
            map.put("noti", "");
            map.put("pasivo", "");
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = "addUpdateRequest", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void addUpdateRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json = "";
        String resultado = "";
        Integer idRecord = 0;
        Integer id = null;
        Integer codDx= 0;
        Integer codEstudio = 0;
        String pasivo = "";
        String tipo ="";

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);

            if(jsonpObject.get("id") != null && !jsonpObject.get("id").getAsString().isEmpty() ) {
                id = jsonpObject.get("id").getAsInt();
            }

            if(jsonpObject.get("tipo") != null && !jsonpObject.get("tipo").getAsString().isEmpty()) {
                tipo = jsonpObject.get("tipo").getAsString();
            }

            if(jsonpObject.get("idRecord") != null && !jsonpObject.get("idRecord").getAsString().isEmpty() ) {
                idRecord = jsonpObject.get("idRecord").getAsInt();
            }

            if(jsonpObject.get("pasivo") != null && !jsonpObject.get("pasivo").getAsString().isEmpty()){
                pasivo = jsonpObject.get("pasivo").getAsString();
            }

            if(jsonpObject.get("codDx") != null && !jsonpObject.get("codDx").getAsString().isEmpty() ){
                codDx = jsonpObject.get("codDx").getAsInt();
            }

            if(jsonpObject.get("codEstudio") != null && !jsonpObject.get("codEstudio").getAsString().isEmpty() ){
                codEstudio = jsonpObject.get("codEstudio").getAsInt();
            }

            User usuario = seguridadService.getUsuario(seguridadService.obtenerNombreUsuario());

            if(idRecord == 0) {
                    if (codDx != 0 && id != 0) {
                        //search log
                        Dx_TipoMx_TipoNoti record = associationSR.getDxByIdAndDx(id, codDx);

                        if(record == null) {
                            Dx_TipoMx_TipoNoti dx = new Dx_TipoMx_TipoNoti();
                            dx.setFechaRegistro(new Timestamp(new Date().getTime()));
                            dx.setUsuarioRegistro(usuario);
                            dx.setDiagnostico(associationSR.getDx(codDx));
                            dx.setTipoMx_tipoNotificacion(associationSR.getTipoMxTipoNoti(id));
                            dx.setPasivo(false);
                            associationSR.addOrUpdateRequestRoutine(dx);
                        }else{
                            resultado = messageSource.getMessage("msg.existing.record.error", null, null);
                            throw new Exception(resultado);
                        }

                    } else if (codEstudio != 0 && id!= 0 ) {
                        //search log
                        Estudio_TipoMx_TipoNoti record = associationSR.getEstudioByIdAndEst(id, codEstudio);

                        if(record == null){
                        Estudio_TipoMx_TipoNoti estudio = new Estudio_TipoMx_TipoNoti();
                        estudio.setEstudio(associationSR.getEstudio(codEstudio));
                        estudio.setFechaRegistro(new Timestamp(new Date().getTime()));
                        estudio.setUsuarioRegistro(usuario);
                        estudio.setTipoMx_tipoNotificacion(associationSR.getTipoMxTipoNoti(id));
                        estudio.setPasivo(false);
                        associationSR.addOrUpdateRequestStudy(estudio);
                        }else{
                            resultado = messageSource.getMessage("msg.existing.record.error", null, null);
                            throw new Exception(resultado);
                        }
                    }

            } else {
                //override request
                if (tipo != null) {
                    if (tipo.equals("Rutina")) {
                        Dx_TipoMx_TipoNoti rec = associationSR.getDxByidDxMxNoti(idRecord);
                        if (rec != null) {
                            rec.setPasivo(true);
                            associationSR.addOrUpdateRequestRoutine(rec);
                        }
                    } else if (tipo.equals("Estudio")) {
                        Estudio_TipoMx_TipoNoti rec1 = associationSR.getEstByidEstMxNoti(idRecord);
                        if (rec1 != null) {
                            rec1.setPasivo(true);
                            associationSR.addOrUpdateRequestStudy(rec1);
                        }
                    }
                }
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            ex.printStackTrace();
            resultado = messageSource.getMessage("msg.add.AssRequest.error1", null, null);
            resultado = resultado + ". \n " + ex.getMessage();

        } finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("id", String.valueOf(id));
            map.put("mensaje", resultado);
            map.put("codDx", "" );
            map.put("codEstudio", "");
            map.put("pasivo", "");
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

}


