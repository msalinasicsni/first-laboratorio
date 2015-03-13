package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.estructura.EntidadesAdtvas;
import ni.gob.minsa.laboratorio.domain.muestra.*;
import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
import ni.gob.minsa.laboratorio.domain.resultados.Catalogo_Lista;
import ni.gob.minsa.laboratorio.domain.resultados.DetalleResultado;
import ni.gob.minsa.laboratorio.domain.resultados.DetalleResultadoFinal;
import ni.gob.minsa.laboratorio.domain.resultados.RespuestaDx;
import ni.gob.minsa.laboratorio.service.*;
import ni.gob.minsa.laboratorio.utilities.ConstantsSecurity;
import ni.gob.minsa.laboratorio.utilities.DateUtil;
import org.apache.commons.lang3.text.translate.UnicodeEscaper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by souyen-ics.
 */
@Controller
@RequestMapping("resultadoFinal")
public class ResultadoFinalController {

    private static final Logger logger = LoggerFactory.getLogger(ResultadoFinalController.class);

    @Resource(name = "seguridadService")
    private SeguridadService seguridadService;

    @Resource(name = "catalogosService")
    private CatalogoService catalogoService;

    @Resource(name = "usuarioService")
    private UsuarioService usuarioService;

    @Resource(name = "entidadAdmonService")
    private EntidadAdmonService entidadAdmonService;

    @Resource(name = "resultadoFinalService")
    private ResultadoFinalService resultadoFinalService;

    @Resource(name= "tomaMxService")
    private TomaMxService tomaMxService;

    @Resource(name= "respuestasDxService")
    private RespuestasDxService respuestasDxService;

    @Autowired
    MessageSource messageSource;

    @RequestMapping(value = "init", method = RequestMethod.GET)
    public ModelAndView initForm(HttpServletRequest request) throws Exception {
        logger.debug("Inicio de busqueda de dx para ingreso de resultado final");
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
            List<TipoMx> tipoMxList = catalogoService.getTipoMuestra();
            mav.addObject("entidades",entidadesAdtvases);
            mav.addObject("tipoMuestra", tipoMxList);

            mav.setViewName("resultados/searchResults");
        }else
            mav.setViewName(urlValidacion);

        return mav;
    }


    @RequestMapping(value = "search", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String fetchDxJson(@RequestParam(value = "strFilter", required = true) String filtro) throws Exception{
        logger.info("Obteniendo los diagnósticos con examenes realizados");
        FiltroMx dx= jsonToFiltroDx(filtro);
        List<DaSolicitudDx> dxList = resultadoFinalService.getDxByFiltro(dx);
        return DxToJson(dxList);
    }


    private String DxToJson(List<DaSolicitudDx> dxList){
        String jsonResponse="";
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice=0;
        for(DaSolicitudDx diagnostico : dxList){
            Map<String, String> map = new HashMap<String, String>();
            map.put("codigoUnicoMx", diagnostico.getIdTomaMx().getCodigoUnicoMx());
            map.put("idTomaMx", diagnostico.getIdTomaMx().getIdTomaMx());
            map.put("fechaTomaMx",DateUtil.DateToString(diagnostico.getIdTomaMx().getFechaHTomaMx(),"dd/MM/yyyy hh:mm:ss a"));
            map.put("codSilais", diagnostico.getIdTomaMx().getIdNotificacion().getCodSilaisAtencion().getNombre());
            map.put("codUnidadSalud", diagnostico.getIdTomaMx().getIdNotificacion().getCodUnidadAtencion().getNombre());
            map.put("tipoMuestra", diagnostico.getIdTomaMx().getCodTipoMx().getNombre());
           //Si hay fecha de inicio de sintomas se muestra
            Date fechaInicioSintomas = tomaMxService.getFechaInicioSintomas(diagnostico.getIdTomaMx().getIdNotificacion().getIdNotificacion());
            if (fechaInicioSintomas!=null)
                map.put("fechaInicioSintomas",DateUtil.DateToString(fechaInicioSintomas,"dd/MM/yyyy"));
            else
                map.put("fechaInicioSintomas"," ");

            //Si hay persona
            if (diagnostico.getIdTomaMx().getIdNotificacion().getPersona()!=null){
                /// se obtiene el nombre de la persona asociada a la ficha
                String nombreCompleto = "";
                nombreCompleto = diagnostico.getIdTomaMx().getIdNotificacion().getPersona().getPrimerNombre();
                if (diagnostico.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre()!=null)
                    nombreCompleto = nombreCompleto +" "+ diagnostico.getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre();
                nombreCompleto = nombreCompleto+" "+ diagnostico.getIdTomaMx().getIdNotificacion().getPersona().getPrimerApellido();
                if (diagnostico.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido()!=null)
                    nombreCompleto = nombreCompleto +" "+ diagnostico.getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido();
                map.put("persona",nombreCompleto);
            }else{
                map.put("persona"," ");
            }

            map.put("diagnostico", diagnostico.getCodDx().getNombre());
            map.put("idSolicitud", diagnostico.getIdSolicitudDx());

            mapResponse.put(indice, map);
            indice ++;
        }
        jsonResponse = new Gson().toJson(mapResponse);
        UnicodeEscaper escaper     = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }

    private FiltroMx jsonToFiltroDx(String strJson) throws Exception {
        JsonObject jObjectFiltro = new Gson().fromJson(strJson, JsonObject.class);
        FiltroMx filtroDx = new FiltroMx();
        String nombreApellido = null;
        Date fechaInicioRecepcion = null;
        Date fechaFinRecepcion = null;
        String codSilais = null;
        String codUnidadSalud = null;
        String codTipoMx = null;
        String codigoUnicoMx = null;


        if (jObjectFiltro.get("nombreApellido") != null && !jObjectFiltro.get("nombreApellido").getAsString().isEmpty())
            nombreApellido = jObjectFiltro.get("nombreApellido").getAsString();
        if (jObjectFiltro.get("fechaInicioRecepcion") != null && !jObjectFiltro.get("fechaInicioRecepcion").getAsString().isEmpty())
            fechaInicioRecepcion = DateUtil.StringToDate(jObjectFiltro.get("fechaInicioRecepcion").getAsString() + " 00:00:00");
        if (jObjectFiltro.get("fechaFinRecepcion") != null && !jObjectFiltro.get("fechaFinRecepcion").getAsString().isEmpty())
            fechaFinRecepcion = DateUtil.StringToDate(jObjectFiltro.get("fechaFinRecepcion").getAsString() + " 23:59:59");
        if (jObjectFiltro.get("codSilais") != null && !jObjectFiltro.get("codSilais").getAsString().isEmpty())
            codSilais = jObjectFiltro.get("codSilais").getAsString();
        if (jObjectFiltro.get("codUnidadSalud") != null && !jObjectFiltro.get("codUnidadSalud").getAsString().isEmpty())
            codUnidadSalud = jObjectFiltro.get("codUnidadSalud").getAsString();
        if (jObjectFiltro.get("codTipoMx") != null && !jObjectFiltro.get("codTipoMx").getAsString().isEmpty())
            codTipoMx = jObjectFiltro.get("codTipoMx").getAsString();
        if (jObjectFiltro.get("codigoUnicoMx") != null && !jObjectFiltro.get("codigoUnicoMx").getAsString().isEmpty())
            codigoUnicoMx = jObjectFiltro.get("codigoUnicoMx").getAsString();

        filtroDx.setCodSilais(codSilais);
        filtroDx.setCodUnidadSalud(codUnidadSalud);
        filtroDx.setFechaInicioRecepLab(fechaInicioRecepcion);
        filtroDx.setFechaFinRecepLab(fechaFinRecepcion);
        filtroDx.setNombreApellido(nombreApellido);
        filtroDx.setCodTipoMx(codTipoMx);
        filtroDx.setCodEstado("ESTDMX|RCLAB"); // recepcionadas en lab
        filtroDx.setIncluirMxInadecuada(false);
        filtroDx.setCodigoUnicoMx(codigoUnicoMx);

        return filtroDx;
    }

    @RequestMapping(value = "create/{idSolicitud}", method = RequestMethod.GET)
    public ModelAndView initCreationForm(@PathVariable("idSolicitud") String idSolicitud, HttpServletRequest request) throws Exception {
        logger.debug("Iniciando el ingreso de resultado final");
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

            if(idSolicitud != null){
              DaSolicitudDx solicitud =  tomaMxService.getSolicitudDxByIdSolicitud(idSolicitud);
                mav.addObject("solicitud", solicitud);
                mav.setViewName("resultados/enterFinalResult");
            }

        }else
            mav.setViewName(urlValidacion);

        return mav;
    }

    @RequestMapping(value = "searchExams", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String fetchExamsJson(@RequestParam(value = "idSolicitud", required = true) String idSolicitud) throws Exception{
        logger.info("Obteniendo los examenes realizados");
        List<OrdenExamen> ordenExa=  resultadoFinalService.getOrdenExaBySolicitud(idSolicitud);
        return ExamsToJson(ordenExa);
    }

    private String ExamsToJson(List<OrdenExamen> examsList){
        String jsonResponse="";
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice=0;
        for(OrdenExamen examen : examsList){
            Map<String, String> map = new HashMap<String, String>();
            map.put("idSolicitud", examen.getSolicitudDx().getIdSolicitudDx());
            map.put("fechaSolicitud",DateUtil.DateToString(examen.getSolicitudDx().getFechaHSolicitud(), "dd/MM/yyyy hh:mm:ss a"));
            map.put("nombreSolicitud", examen.getSolicitudDx().getCodDx().getNombre());
            map.put("codigoUnicoMx", examen.getSolicitudDx().getIdTomaMx().getCodigoUnicoMx());
            map.put("tipoMx", examen.getSolicitudDx().getIdTomaMx().getCodTipoMx().getNombre());
            map.put("tipoNotificacion", examen.getSolicitudDx().getIdTomaMx().getIdNotificacion().getCodTipoNotificacion().getValor());
            map.put("idOrdenExamen", examen.getIdOrdenExamen());
            map.put("NombreExamen", examen.getCodExamen().getNombre());

      //Si hay persona
            if (examen.getSolicitudDx().getIdTomaMx().getIdNotificacion().getPersona() !=null){
                /// se obtiene el nombre de la persona asociada a la ficha
                String nombreCompleto = "";
                nombreCompleto = examen.getSolicitudDx().getIdTomaMx().getIdNotificacion().getPersona().getPrimerNombre();
                if (examen.getSolicitudDx().getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre()!=null)
                    nombreCompleto = nombreCompleto +" "+ examen.getSolicitudDx().getIdTomaMx().getIdNotificacion().getPersona().getSegundoNombre();
                nombreCompleto = nombreCompleto+" "+ examen.getSolicitudDx().getIdTomaMx().getIdNotificacion().getPersona().getPrimerApellido();
                if (examen.getSolicitudDx().getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido()!=null)
                    nombreCompleto = nombreCompleto +" "+ examen.getSolicitudDx().getIdTomaMx().getIdNotificacion().getPersona().getSegundoApellido();
                map.put("persona",nombreCompleto);
            }else{
                map.put("persona"," ");
            }

            //detalle resultado examen
            List<DetalleResultado> resultList = resultadoFinalService.getResultDetailExaByIdOrden(examen.getIdOrdenExamen());
            Map<Integer, Object> mapResList = new HashMap<Integer, Object>();
            Map<String, String> mapRes = new HashMap<String, String>();
            int subIndice=0;
            for(DetalleResultado res: resultList){

                if (res.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                    Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(res.getValor());
                    mapRes.put("valor", cat_lista.getValor());
                } else {
                    mapRes.put("valor", res.getValor());
                }

                mapRes.put("fechaResultado", DateUtil.DateToString(res.getFechahRegistro(), "dd/MM/yyyy hh:mm:ss a"));
                subIndice++;
                mapResList.put(subIndice,mapRes);
                mapRes = new HashMap<String, String>();
            }

            map.put("resultado", new Gson().toJson(mapResList));

            mapResponse.put(indice, map);
            indice ++;
        }
        jsonResponse = new Gson().toJson(mapResponse);
        UnicodeEscaper escaper     = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }

    @RequestMapping(value = "getCatalogosListaConceptoByidDx", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    List<Catalogo_Lista> getCatalogoListaConceptoByIdDx(@RequestParam(value = "idDx", required = true) String idDx) throws Exception {
        logger.info("Obteniendo los valores para los conceptos tipo lista asociados a las respuesta del dx");
        return respuestasDxService.getCatalogoListaConceptoByIdDx(Integer.valueOf(idDx));
    }

    @RequestMapping(value = "getDetResFinalBySolicitud", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    List<DetalleResultadoFinal> getDetResFinalBySolicitud(@RequestParam(value = "idSolicitud", required = true) String idSolicitud) throws Exception {
        logger.info("Se obtienen los detalles de resultados activos para la solicitud");
        return  resultadoFinalService.getDetResActivosBySolicitud(idSolicitud);
    }

    @RequestMapping(value = "saveFinalResult", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void saveFinalResult(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        String strRespuestas="";
        String idSolicitud="";
        Integer cantRespuestas=0;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            strRespuestas = jsonpObject.get("strRespuestas").toString();
            idSolicitud = jsonpObject.get("idSolicitud").getAsString();
            cantRespuestas = jsonpObject.get("cantRespuestas").getAsInt();
            DaSolicitudDx solicitud = tomaMxService.getSolicitudDxByIdSolicitud(idSolicitud);
            long idUsuario = seguridadService.obtenerIdUsuario(request);
            Usuarios usuario = usuarioService.getUsuarioById((int) idUsuario);
            //se obtiene datos de los conceptos a registrar

            JsonObject jObjectRespuestas = new Gson().fromJson(strRespuestas, JsonObject.class);
            for(int i = 0; i< cantRespuestas;i++) {
                String respuesta = jObjectRespuestas.get(String.valueOf(i)).toString();
                JsonObject jsRespuestaObject = new Gson().fromJson(respuesta, JsonObject.class);
                Integer idRespuesta = jsRespuestaObject.get("idRespuesta").getAsInt();
                RespuestaDx conceptoTmp =  respuestasDxService.getRespuestaDxById(idRespuesta);
                String valor = jsRespuestaObject.get("valor").getAsString();
                DetalleResultadoFinal detResFinal = new DetalleResultadoFinal();
                detResFinal.setFechahRegistro(new Timestamp(new Date().getTime()));
                detResFinal.setValor(valor);
                detResFinal.setRespuesta(conceptoTmp);
                detResFinal.setSolicitudDx(solicitud);
                detResFinal.setUsuarioRegistro(usuario);
                DetalleResultadoFinal resFinalRegistrado = resultadoFinalService.getDetResBySolicitudAndRespuesta(idSolicitud,idRespuesta);
                if (resFinalRegistrado!=null){
                    detResFinal.setIdDetalle(resFinalRegistrado.getIdDetalle());
                    resultadoFinalService.updateDetResFinal(detResFinal);
                }else {
                    if (detResFinal.getValor() != null && !detResFinal.getValor().isEmpty()) {
                        resultadoFinalService.saveDetResFinal(detResFinal);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.result.error.added",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("idSolicitud",idSolicitud);
            map.put("strRespuestas",strRespuestas);
            map.put("mensaje",resultado);
            map.put("cantRespuestas",cantRespuestas.toString());
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }


    @RequestMapping(value = "overrideResult", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void overrideResult(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        String idSolicitud="";
        String causaAnulacion = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            idSolicitud = jsonpObject.get("idSolicitud").getAsString();
            causaAnulacion = jsonpObject.get("causaAnulacion").getAsString();
            DaSolicitudDx soli = tomaMxService.getSolicitudDxByIdSolicitud(idSolicitud);
            long idUsuario = seguridadService.obtenerIdUsuario(request);
            Usuarios usuario = usuarioService.getUsuarioById((int) idUsuario);
            //se obtiene datos de los resultados a registrar
            List<DetalleResultadoFinal> detResFinalActivos =  resultadoFinalService.getDetResActivosBySolicitud(idSolicitud);
            for(DetalleResultadoFinal detResFinal : detResFinalActivos) {
                detResFinal.setFechahAnulacion(new Timestamp(new Date().getTime()));
                detResFinal.setSolicitudDx(soli);
                detResFinal.setUsuarioAnulacion(usuario);
                detResFinal.setRazonAnulacion(causaAnulacion);
                detResFinal.setPasivo(true);
                resultadoFinalService.updateDetResFinal(detResFinal);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.result.error.canceled",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("idSolicitud",idSolicitud);
            map.put("causaAnulacion",causaAnulacion);
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

}
