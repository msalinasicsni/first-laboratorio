package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.examen.CatalogoExamenes;
import ni.gob.minsa.laboratorio.domain.examen.Examen_Dx;
import ni.gob.minsa.laboratorio.domain.examen.Examen_Estudio;
import ni.gob.minsa.laboratorio.domain.muestra.Catalogo_Dx;
import ni.gob.minsa.laboratorio.domain.muestra.Catalogo_Estudio;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.User;
import ni.gob.minsa.laboratorio.service.AssociationSamplesRequestService;
import ni.gob.minsa.laboratorio.service.ExamenesService;
import ni.gob.minsa.laboratorio.service.ExamenesSolicitudService;
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
@RequestMapping("administracion/testsRequest")
public class ExamenesSolicitudController {

    private static final Logger logger = LoggerFactory.getLogger(ExamenesSolicitudController.class);

    @Autowired
    @Qualifier(value = "seguridadService")
    private SeguridadService seguridadService;

    @Autowired
    @Qualifier(value = "examenesSolicitudService")
    private ExamenesSolicitudService testsRequestService;

    @Autowired
    @Qualifier(value = "associationSR")
    private AssociationSamplesRequestService associationSR;

    @Autowired
    @Qualifier(value = "examenesService")
    private ExamenesService examenesService;

    @Autowired
    MessageSource messageSource;

    @RequestMapping(value = "init", method = RequestMethod.GET)
    public ModelAndView initForm(HttpServletRequest request) throws Exception {
        logger.debug("Cargando lista de rutinas y estudios");
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
           // getRoutinesAndStudies();
            mav.setViewName("administracion/testsRequest");
        }else
            mav.setViewName(urlValidacion);

        return mav;
    }

    //Load Routines and Studies list
    @RequestMapping(value = "getRoutinesAndStudies", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String getRoutinesAndStudies() throws Exception {
        logger.info("Obteniendo las rutinas y estudios asociados al tipoMxNoti");

        List<Catalogo_Dx> dxList = associationSR.getDxs();
        List<Catalogo_Estudio> estudioList = associationSR.getStudies();

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
                mapResponse.put(indice, map);
                indice++;
            }
        }
        jsonResponse = new Gson().toJson(mapResponse);
        UnicodeEscaper escaper = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }

    //load associated tests
    @RequestMapping(value = "getAssociatedTest", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String fetchDxJson(@RequestParam(value = "id", required = true) Integer id, @RequestParam(value = "tipo", required = true) String tipo ) throws Exception{
        logger.info("Obteniendo los examenes asociados a una rutina o estudio");

        List<Examen_Dx> exaDxList = null;
        List<Examen_Estudio> exaEstList = null;

        if(tipo != null){
            if(tipo.equals("Rutina")){
              exaDxList = testsRequestService.getTestsDxByIdDx(id);
            }else{
             exaEstList = testsRequestService.getTestsEstByIdDx(id);
            }

        }

        return testsToJson(exaDxList, exaEstList);
    }


    private String testsToJson(List<Examen_Dx> exaDxList, List<Examen_Estudio> exaEstList) {
        String jsonResponse = "";
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice = 0;

        if (exaDxList != null) {
            for (Examen_Dx dx : exaDxList) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("id", dx.getIdExamen_Dx().toString());
                map.put("nombreExamen", dx.getExamen().getNombre());
                map.put("porDefecto", String.valueOf(dx.isPorDefecto()));
                mapResponse.put(indice, map);
                indice++;
            }
        }

        if (exaEstList != null) {
            for (Examen_Estudio estudio : exaEstList) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("id", estudio.getIdExamen_Estudio().toString());
                map.put("nombreExamen", estudio.getExamen().getNombre());
                map.put("porDefecto", String.valueOf(estudio.isPorDefecto()));
                mapResponse.put(indice, map);
                indice++;
            }
        }
        jsonResponse = new Gson().toJson(mapResponse);
        UnicodeEscaper escaper = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }


    @RequestMapping(value = "testsByArea", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    List<CatalogoExamenes> getTestsByArea(@RequestParam(value = "idArea", required = true) Integer idArea) throws Exception {
        logger.info("Obteniendo los examenes por area");

        List<CatalogoExamenes> cat = testsRequestService.getTestsByIdArea(idArea);

        return cat;
    }

    @RequestMapping(value = "addUpdateTest", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void addUpdateTest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String json = "";
        String resultado = "";
        Integer idRecord = 0;
        Integer idExamen = 0;
        Integer idSolicitud = 0;
        String pasivo = "";
        String porDefecto = "";
        String tipo ="";

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);

            if(jsonpObject.get("idExamen") != null && !jsonpObject.get("idExamen").getAsString().isEmpty() ) {
                idExamen = jsonpObject.get("idExamen").getAsInt();
            }

            if (jsonpObject.get("idSolicitud") != null && !jsonpObject.get("idSolicitud").getAsString().isEmpty()) {
                idSolicitud = jsonpObject.get("idSolicitud").getAsInt();
            }

            if (jsonpObject.get("tipo") != null && !jsonpObject.get("tipo").getAsString().isEmpty()) {
                tipo = jsonpObject.get("tipo").getAsString();
            }

            if (jsonpObject.get("idRecord") != null && !jsonpObject.get("idRecord").getAsString().isEmpty()) {
                idRecord = jsonpObject.get("idRecord").getAsInt();
            }

            if (jsonpObject.get("pasivo") != null && !jsonpObject.get("pasivo").getAsString().isEmpty()) {
                pasivo = jsonpObject.get("pasivo").getAsString();
            }

            if (jsonpObject.get("porDefecto") != null && !jsonpObject.get("porDefecto").getAsString().isEmpty()) {
                porDefecto = jsonpObject.get("porDefecto").getAsString();
            }


            User usuario = seguridadService.getUsuario(seguridadService.obtenerNombreUsuario());

            if (idRecord == 0) {
                if (tipo != null && idSolicitud != 0 && idExamen != 0) {
                    if (tipo.equals("Rutina")) {
                        //search log
                        Examen_Dx record = testsRequestService.getDxTestRecord(idSolicitud, idExamen);

                        if (record == null) {
                            Examen_Dx exa = new Examen_Dx();
                            exa.setFechaRegistro(new Timestamp(new Date().getTime()));
                            exa.setUsuarioRegistro(usuario);
                            exa.setDiagnostico(associationSR.getDx(idSolicitud));
                            exa.setExamen(examenesService.getExamenById(idExamen));
                            exa.setPasivo(false);
                            exa.setPorDefecto(Boolean.valueOf(porDefecto));
                            testsRequestService.addOrUpdateTest(exa);
                        } else {
                            resultado = messageSource.getMessage("msg.existing.record.error", null, null);
                            throw new Exception(resultado);
                        }


                    } else {
                        //search log
                        Examen_Estudio record = testsRequestService.getStudyTestRecord(idSolicitud, idExamen);

                        if (record == null) {
                            Examen_Estudio exa = new Examen_Estudio();
                            exa.setFechaRegistro(new Timestamp(new Date().getTime()));
                            exa.setUsuarioRegistro(usuario);
                            exa.setEstudio(associationSR.getEstudio(idSolicitud));
                            exa.setExamen(examenesService.getExamenById(idExamen));
                            exa.setPasivo(false);
                            exa.setPorDefecto(Boolean.valueOf(porDefecto));
                            testsRequestService.addOrUpdateTestE(exa);
                        } else {
                            resultado = messageSource.getMessage("msg.existing.record.error", null, null);
                            throw new Exception(resultado);
                        }
                    }

                }
            }else{
                if(tipo!= null ){
                    if (tipo.equals("Rutina")) {
                        Examen_Dx rec = testsRequestService.getRoutineTestById(idRecord);
                        if (rec != null) {
                            if (!pasivo.isEmpty()) {
                                rec.setPasivo(Boolean.valueOf(pasivo));
                            }
                            //cuando es por defecto, trae el valor actual, entonces se pone el valor inverso
                            if (!porDefecto.isEmpty()) {
                                rec.setPorDefecto(!Boolean.valueOf(porDefecto));
                            }
                            testsRequestService.addOrUpdateTest(rec);
                        }
                    } else if (tipo.equals("Estudio")) {
                        Examen_Estudio rec1 = testsRequestService.getStudyTestById(idRecord);
                        if (rec1 != null) {
                            if (!pasivo.isEmpty()) {
                                rec1.setPasivo(Boolean.valueOf(pasivo));
                            }
                            //cuando es por defecto, trae el valor actual, entonces se pone el valor inverso
                            if (!porDefecto.isEmpty()) {
                                rec1.setPorDefecto(!Boolean.valueOf(porDefecto));
                            }
                            testsRequestService.addOrUpdateTestE(rec1);
                        }
                    }
                }
            }


        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            ex.printStackTrace();
            resultado = messageSource.getMessage("msg.add.AssTest.error1", null, null);
            resultado = resultado + ". \n " + ex.getMessage();

        } finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("idExame", String.valueOf(idExamen));
            map.put("mensaje", resultado);
            map.put("idRecord", "");
            map.put("idSolicitud", "");
            map.put("pasivo", "");
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }



}
