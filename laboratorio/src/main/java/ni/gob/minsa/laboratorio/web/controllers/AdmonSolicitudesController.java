package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.examen.Area;
import ni.gob.minsa.laboratorio.domain.muestra.Catalogo_Dx;
import ni.gob.minsa.laboratorio.domain.muestra.Catalogo_Estudio;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.User;
import ni.gob.minsa.laboratorio.service.AdmonRequestService;
import ni.gob.minsa.laboratorio.service.AreaService;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
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
@RequestMapping("administracion/request")
public class AdmonSolicitudesController {

    private static final Logger logger = LoggerFactory.getLogger(AdmonSolicitudesController.class);


    @Autowired
    @Qualifier(value = "seguridadService")
    private SeguridadService seguridadService;

    @Autowired
    @Qualifier(value = "areaService")
    private AreaService areaService;

    @Autowired
    @Qualifier(value = "admonRequestService")
    private AdmonRequestService admonRequestService;

    @Autowired
    MessageSource messageSource;

    @RequestMapping(value = "init", method = RequestMethod.GET)
    public ModelAndView init(HttpServletRequest request) throws Exception {
        logger.debug("Cargando Conceptos");
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
              List<Area> areaList =  areaService.getAreasActivas();
              mav.addObject("areaList",areaList);
            mav.setViewName("administracion/catalogos/request");
        }else
            mav.setViewName(urlValidacion);

        return mav;
    }

    //Load Routines and Studies list
    @RequestMapping(value = "getRoutinesAndStudies", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String getRoutinesAndStudies() throws Exception {
        logger.info("Obteniendo las rutinas y estudios asociados al tipoMxNoti");

        List<Catalogo_Dx> dxList = admonRequestService.getAllDxs();
        List<Catalogo_Estudio> estudioList = admonRequestService.getAllStudies();

        return listToJson(dxList, estudioList);
    }


    private String listToJson(List<Catalogo_Dx> dxList, List<Catalogo_Estudio> estudioList) {
        String jsonResponse = "";
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice = 0;

        if (dxList != null) {
            for (Catalogo_Dx dx : dxList) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("nombre", dx.getNombre());
                map.put("id", dx.getIdDiagnostico().toString());
                map.put("idArea", dx.getArea().getIdArea().toString());
                map.put("area", dx.getArea().getNombre());
                map.put("tipo",messageSource.getMessage("lbl.routine", null, null));
                map.put("pasivo", String.valueOf(dx.isPasivo()));
                mapResponse.put(indice, map);
                indice++;
            }
        }

        if (estudioList != null) {
            for (Catalogo_Estudio estudio : estudioList) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("nombre", estudio.getNombre());
                map.put("id", estudio.getIdEstudio().toString());
                map.put("idArea", estudio.getArea().getIdArea().toString());
                map.put("area", estudio.getArea().getNombre());
                map.put("tipo",messageSource.getMessage("lbl.study", null, null));
                map.put("pasivo", String.valueOf(estudio.isPasivo()));
                mapResponse.put(indice, map);
                indice++;
            }
        }
        jsonResponse = new Gson().toJson(mapResponse);
        UnicodeEscaper escaper = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }

    @RequestMapping(value = "addUpdateRequest", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void addUpdateRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String json = "";
        String resultado = "";
        Integer id = 0;
        String nombre = "";
        boolean pasivo = false;
        String tipo ="";
        Integer area = 0;
        Integer prioridad = 0;
        String codigo = null;


        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);

            if(jsonpObject.get("id") != null && !jsonpObject.get("id").getAsString().isEmpty() ) {
                id = jsonpObject.get("id").getAsInt();
            }

            if(jsonpObject.get("area") != null && !jsonpObject.get("area").getAsString().isEmpty() ) {
                area = jsonpObject.get("area").getAsInt();
            }

            if(jsonpObject.get("prioridad") != null && !jsonpObject.get("prioridad").getAsString().isEmpty() ) {
                prioridad = jsonpObject.get("prioridad").getAsInt();
            }

            if (jsonpObject.get("tipo") != null && !jsonpObject.get("tipo").getAsString().isEmpty()) {
                tipo = jsonpObject.get("tipo").getAsString();
            }

            if (jsonpObject.get("nombre") != null && !jsonpObject.get("nombre").getAsString().isEmpty()) {
                nombre = jsonpObject.get("nombre").getAsString();
            }

            if (jsonpObject.get("codigo") != null && !jsonpObject.get("codigo").getAsString().isEmpty()) {
                codigo = jsonpObject.get("codigo").getAsString();
            }

            pasivo = jsonpObject.get("pasivo").getAsBoolean();


            User usuario = seguridadService.getUsuario(seguridadService.obtenerNombreUsuario());

            if (id == 0) {
                if (tipo != null && nombre != null && area != 0) {
                    if (tipo.equals("Rutina")) {
                        //search log
                        Catalogo_Dx record = admonRequestService.getDxRecord(nombre, area) ;

                        if (record == null) {
                            Catalogo_Dx dx = new Catalogo_Dx();
                            dx.setFechaRegistro(new Timestamp(new Date().getTime()));
                            dx.setUsuarioRegistro(usuario);
                            dx.setArea(areaService.getArea(area));
                            dx.setPrioridad(prioridad);
                            dx.setPasivo(!pasivo);
                            dx.setNombre(nombre);
                            admonRequestService.addOrUpdateDx(dx);
                        } else {
                            resultado = messageSource.getMessage("msg.existing.record.error", null, null);
                            throw new Exception(resultado);
                        }


                    } else {
                        //search log
                        Catalogo_Estudio record = admonRequestService.getStudyRecord(nombre, area);

                        if (record == null) {
                            Catalogo_Estudio est = new Catalogo_Estudio();
                            est.setFechaRegistro(new Timestamp(new Date().getTime()));
                            est.setUsuarioRegistro(usuario);
                            est.setArea(areaService.getArea(area));
                            est.setPasivo(false);
                            est.setNombre(nombre);
                            est.setCodigo(codigo);
                            admonRequestService.addOrUpdateStudy(est);
                        } else {
                            resultado = messageSource.getMessage("msg.existing.record.error", null, null);
                            throw new Exception(resultado);
                        }
                    }

                }
            } else {

                    if (tipo.equals("Rutina")) {
                        Catalogo_Dx rec = admonRequestService.getDxRecordById(id);

                            if(!nombre.equals("")){
                                rec.setNombre(nombre);
                                rec.setPrioridad(prioridad);
                                rec.setArea(areaService.getArea(area));
                                rec.setPasivo(!pasivo);
                            }else{
                                rec.setPasivo(true);
                            }

                        admonRequestService.addOrUpdateDx(rec);

                    } else if (tipo.equals("Estudio")) {
                        Catalogo_Estudio rec = admonRequestService.getStudyRecordById(id);

                            if(!nombre.equals("")){
                                rec.setArea(areaService.getArea(area));
                                rec.setNombre(nombre);
                                rec.setCodigo(codigo);
                                rec.setPasivo(!pasivo);
                            }else{
                                rec.setPasivo(true);
                            }

                        admonRequestService.addOrUpdateStudy(rec);
                    }
                }


        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            ex.printStackTrace();
            resultado = messageSource.getMessage("msg.add.request.error", null, null);
            resultado = resultado + ". \n " + ex.getMessage();

        } finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("id", id.toString());
            map.put("mensaje", resultado);
            map.put("tipo", "");
            map.put("nombre", "");
            map.put("pasivo", "");
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    //load associated tests
    @RequestMapping(value = "getRequestById", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String getRequestById(@RequestParam(value = "id", required = true) Integer id, @RequestParam(value = "tipo", required = true) String tipo ) throws Exception{
        logger.info("Obteniendo los examenes asociados a una rutina o estudio");

        Catalogo_Dx dxRecord = null;
       Catalogo_Estudio studyRecord = null;

        if(tipo != null){
            if(tipo.equals("Rutina")){
                dxRecord = admonRequestService.getDxRecordById(id);
            }else{
                studyRecord = admonRequestService.getStudyRecordById(id);
            }

        }

        return requestToJson(dxRecord, studyRecord);
    }


    private String requestToJson(Catalogo_Dx dx, Catalogo_Estudio estudio) {
        String jsonResponse = "";
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice = 0;

        if (dx != null) {

            Map<String, String> map = new HashMap<String, String>();
            map.put("id", dx.getIdDiagnostico().toString());
            map.put("nombre", dx.getNombre());
            map.put("tipo", messageSource.getMessage("lbl.routine", null, null));
            map.put("idArea", dx.getArea().getIdArea().toString());
            map.put("prioridad", dx.getPrioridad().toString());
            map.put("codigo", "");
            map.put("pasivo", String.valueOf(dx.isPasivo()));

            mapResponse.put(indice, map);


        } else if (estudio != null) {

            Map<String, String> map = new HashMap<String, String>();
            map.put("id", estudio.getIdEstudio().toString());
            map.put("nombre", estudio.getNombre());
            map.put("tipo", messageSource.getMessage("lbl.study", null, null));
            map.put("idArea", estudio.getArea().getIdArea().toString());
            map.put("prioridad", "");
            map.put("codigo", estudio.getCodigo());
            map.put("pasivo", String.valueOf(estudio.isPasivo()));

            mapResponse.put(indice, map);

        }

        jsonResponse = new Gson().toJson(mapResponse);
        UnicodeEscaper escaper = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }

}
