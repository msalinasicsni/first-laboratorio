package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.estructura.EntidadesAdtvas;
import ni.gob.minsa.laboratorio.domain.examen.Area;
import ni.gob.minsa.laboratorio.domain.muestra.*;
import ni.gob.minsa.laboratorio.domain.muestra.traslado.HistoricoEnvioMx;
import ni.gob.minsa.laboratorio.domain.muestra.traslado.TrasladoMx;
import ni.gob.minsa.laboratorio.domain.parametros.Parametro;
import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
import ni.gob.minsa.laboratorio.domain.resultados.Catalogo_Lista;
import ni.gob.minsa.laboratorio.domain.resultados.Concepto;
import ni.gob.minsa.laboratorio.domain.resultados.DetalleResultadoFinal;
import ni.gob.minsa.laboratorio.service.*;
import ni.gob.minsa.laboratorio.utilities.DateUtil;
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
 * Created by FIRSTICT on 4/28/2015.
 * V1.0
 */
@Controller
@RequestMapping("trasladoMx")
public class TrasladoMxController {

    private static final Logger logger = LoggerFactory.getLogger(SendMxReceiptController.class);
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
    @Qualifier(value = "tomaMxService")
    private TomaMxService tomaMxService;

    @Autowired
    @Qualifier(value = "trasladosService")
    private TrasladosService trasladosService;

    @Autowired
    @Qualifier(value = "areaService")
    private AreaService areaService;

    @Autowired
    @Qualifier(value = "laboratoriosService")
    private LaboratoriosService laboratoriosService;

    @Autowired
    @Qualifier(value = "parametrosService")
    private ParametrosService parametrosService;

    @Autowired
    @Qualifier(value = "usuarioService")
    private UsuarioService usuarioService;

    @Autowired
    @Qualifier(value = "resultadoFinalService")
    private ResultadoFinalService resultadoFinalService;

    @Autowired
    @Qualifier(value = "respuestasExamenService")
    private RespuestasExamenService respuestasExamenService;

    @Autowired
    MessageSource messageSource;

    @RequestMapping(value = "init", method = RequestMethod.GET)
    public ModelAndView initSearchForm() throws Exception {
        logger.debug("buscar ordenes para recepcion");

        ModelAndView mav = new ModelAndView();
        logger.debug("List<DaSolicitudDx> solicitudDxList");
        List<EntidadesAdtvas> entidadesAdtvases =  entidadAdmonService.getAllEntidadesAdtvas();
        List<Catalogo_Dx> catalogoDxList = trasladosService.getRutinas();
        List<TipoMx> tipoMxList = catalogosService.getTipoMuestra();
        List<Area> areaList = areaService.getAreas();
        mav.addObject("entidades",entidadesAdtvases);
        mav.addObject("tipoMuestra", tipoMxList);
        mav.addObject("area",areaList);
        mav.addObject("rutinas", catalogoDxList);
        mav.setViewName("laboratorio/trasladoMx/trasladoMxInterno");
        return mav;
    }

    @RequestMapping(value = "initCC", method = RequestMethod.GET)
    public ModelAndView initSearchFormCC() throws Exception {
        logger.debug("buscar ordenes para recepcion");

        ModelAndView mav = new ModelAndView();
        List<EntidadesAdtvas> entidadesAdtvases =  entidadAdmonService.getAllEntidadesAdtvas();
        List<Catalogo_Dx> catalogoDxList = trasladosService.getRutinas();
        List<TipoMx> tipoMxList = catalogosService.getTipoMuestra();
        List<Area> areaList = areaService.getAreas();
        mav.addObject("entidades",entidadesAdtvases);
        mav.addObject("tipoMuestra", tipoMxList);
        mav.addObject("area",areaList);
        mav.addObject("rutinas",catalogoDxList);
        mav.setViewName("laboratorio/trasladoMx/trasladoMxCC");

        return mav;
    }

    /**
     * Método para realizar la búsqueda de Mx para traslado interno
     * @param filtro JSon con los datos de los filtros a aplicar en la búsqueda(Nombre Apellido, Rango Fec Toma Mx, Tipo Mx, SILAIS, unidad salud)
     * @return String con las Recepciones encontradas
     * @throws Exception
     */
    @RequestMapping(value = "searchMx", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String fetchMxJson(@RequestParam(value = "strFilter", required = true) String filtro) throws Exception{
        logger.info("Obteniendo las mxs según filtros en JSON");
        FiltroMx filtroMx = jsonToFiltroMx(filtro);
        logger.info("antes trasladosService.getTomaMxByFiltro(filtroMx)");
        List<DaTomaMx> tomaMxList = trasladosService.getTomaMxByFiltro(filtroMx);
        logger.info("despues trasladosService.getTomaMxByFiltro(filtroMx)");
        return tomaMxToJson(tomaMxList);
    }

    /**
     * Método para realizar la búsqueda de Mx para traslado de control de calidad hacia CNDR
     * @param filtro JSon con los datos de los filtros a aplicar en la búsqueda(Nombre Apellido, Rango Fec Toma Mx, Tipo Mx, SILAIS, unidad salud)
     * @return String con las Recepciones encontradas
     * @throws Exception
     */
    @RequestMapping(value = "searchMxCC", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String fetchMxCCJson(@RequestParam(value = "strFilter", required = true) String filtro) throws Exception{
        logger.info("Obteniendo las mxs para cc según filtros en JSON");
        FiltroMx filtroMx = jsonToFiltroMx(filtro);
        List<DaTomaMx> tomaMxList = trasladosService.getTomaMxCCByFiltro(filtroMx);
        return tomaMxToJson(tomaMxList);
    }


    @RequestMapping(value = "realizarTrasladoMx", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void realizarTraslado(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        String strMuestras="";
        String codigosUnicosMx="";
        String tipoTraslado="";
        String idRutina = "";
        String nombreTransporta = "";
        Float temperaturaTermo = null;
        Integer cantMuestras = 0;
        Integer cantMxProc = 0;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            strMuestras = jsonpObject.get("strMuestras").toString();
            cantMuestras = jsonpObject.get("cantMuestras").getAsInt();
            tipoTraslado = jsonpObject.get("tipoTraslado").getAsString();

            if (jsonpObject.get("idRutina")!=null && !jsonpObject.get("idRutina").toString().isEmpty())
                idRutina = jsonpObject.get("idRutina").getAsString();

            if (jsonpObject.get("nombreTransporta")!=null && !jsonpObject.get("nombreTransporta").toString().isEmpty())
                nombreTransporta = jsonpObject.get("nombreTransporta").getAsString();

            if (jsonpObject.get("temperaturaTermo")!=null && !jsonpObject.get("temperaturaTermo").toString().isEmpty())
                temperaturaTermo = jsonpObject.get("temperaturaTermo").getAsFloat();

            //Se obtiene estado en que debe quedar la muestra (Trasladada)
            EstadoMx estadoMx = catalogosService.getEstadoMx("ESTDMX|TRAS");
            //EstadoMx estadoMx = catalogosService.getEstadoMx(tipoTraslado.equals("cc")?"ESTDMX|ENV":"ESTDMX|EPLAB");
            Parametro pUsuarioDefecto = parametrosService.getParametroByName("USU_REGISTRO_TRASLADO");
            Usuarios usurioRegistro = null;
            if (pUsuarioDefecto!=null){
                usurioRegistro = usuarioService.getUsuarioById(Integer.valueOf(pUsuarioDefecto.getValor()));
            }

            Laboratorio labDestino = null;//laboratoriosService.getLaboratorioByCodigo("LABCNDR");
            Laboratorio labOrigen = null;//seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
            Catalogo_Dx dxTraslado = null;
            if (!tipoTraslado.equals("cc")){
                dxTraslado = tomaMxService.getDxById(idRutina);
            }else{
                labDestino = laboratoriosService.getLaboratorioByCodigo("LABCNDR");
                if (labDestino==null){
                    throw new Exception("No se logró recuperar laboratio destino");
                }
                labOrigen = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
            }

            //se obtienen muestras a trasladar
            JsonObject jObjectRecepciones = new Gson().fromJson(strMuestras, JsonObject.class);
            for(int i = 0; i< cantMuestras;i++) {
                boolean crearSolicitud = true;
                Timestamp fhRegistro = new Timestamp(new Date().getTime());
                String idTomaMx = jObjectRecepciones.get(String.valueOf(i)).getAsString();
                List<DaSolicitudDx> solicitudDxList = tomaMxService.getSolicitudesDxPrioridadByIdToma(idTomaMx);
                List<DaSolicitudDx> solicitudDxPendTrasladoList = tomaMxService.getSolicitudesDxSinTrasladoByIdToma(idTomaMx);
                //determinar si existe una solicitud pendiente para el nuevo tipo de dx solicitado, en caso de existir no se va a registrar nueva solicitud
                for (DaSolicitudDx solicitudDx : solicitudDxPendTrasladoList){
                    if (solicitudDx.getCodDx().getIdDiagnostico().equals(dxTraslado.getIdDiagnostico())){
                        crearSolicitud = false;
                        break;
                    }
                }
                //se obtiene tomaMx a recepcionar
                DaTomaMx tomaMx = tomaMxService.getTomaMxById(idTomaMx);
                TrasladoMx trasladoMx = new TrasladoMx();
                trasladoMx.setTomaMx(tomaMx);
                trasladoMx.setFechaHoraRegistro(fhRegistro);
                trasladoMx.setRecepcionado(false);
                trasladoMx.setUsuarioRegistro(seguridadService.obtenerNombreUsuario());

                if (tipoTraslado.equals("cc")){
                    trasladoMx.setTrasladoExterno(true);
                    trasladoMx.setLaboratorioDestino(labDestino);
                    trasladoMx.setLaboratorioOrigen(labOrigen);
                    trasladoMx.setPrioridad(1);
                }else {
                    trasladoMx.setTrasladoExterno(false);
                    if (dxTraslado!=null){
                        trasladoMx.setAreaDestino(dxTraslado.getArea());
                        trasladoMx.setPrioridad(dxTraslado.getPrioridad());
                    }
                    //si tiene traslado activo tomar area destino
                    TrasladoMx trasladoActivo = trasladosService.getTrasladoActivoMx(idTomaMx);
                    if (trasladoActivo!=null) {
                        trasladoMx.setAreaOrigen(trasladoActivo.getAreaDestino());
                    }else{//si no tien traslados, tomar el area del dx con mayor prioridad
                        trasladoMx.setAreaOrigen(solicitudDxList.get(0).getCodDx().getArea());
                    }

                }
                try {

                    //crear solicitud dx, sólo si no existe solicitud pendiente de traslado para el nuevo dx solicitado
                    if (!tipoTraslado.equals("cc")){ //interno
                        if (crearSolicitud) {
                            //sólo si no existe solicitud pendiente de traslado
                            DaSolicitudDx solicitudDx = new DaSolicitudDx();
                            solicitudDx.setIdTomaMx(tomaMx);
                            solicitudDx.setAprobada(false);
                            solicitudDx.setCodDx(dxTraslado);
                            solicitudDx.setFechaHSolicitud(fhRegistro);
                            solicitudDx.setControlCalidad(false);
                            solicitudDx.setUsarioRegistro(usurioRegistro);
                            solicitudDx.setLabProcesa(seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario()));
                            tomaMxService.addSolicitudDx(solicitudDx);
                        }
                        tomaMx.setEstadoMx(estadoMx);//cambiar a estado trasladada
                    }else{// control de calidad //crear envio y solicitud dx
                        //sólo la solicitud positiva se envia a control de calidad
                        for(DaSolicitudDx solDx:solicitudDxList) {
                            boolean agregarDx = false;
                            List<DetalleResultadoFinal> resultadoFinalList = resultadoFinalService.getDetResActivosBySolicitud(solDx.getIdSolicitudDx());
                            for (DetalleResultadoFinal res:resultadoFinalList){
                                Concepto conceptoAsociado = null;
                                if (res.getRespuesta()!=null)
                                    conceptoAsociado = res.getRespuesta().getConcepto();
                                else
                                    conceptoAsociado = res.getRespuestaExamen().getConcepto();

                                if (conceptoAsociado.getTipo().getCodigo().equals("TPDATO|TXT")) {
                                    if (res.getValor().toLowerCase().equals("positivo")) {
                                        agregarDx = true;
                                        break;
                                    }

                                } else if (conceptoAsociado.getTipo().getCodigo().equals("TPDATO|LIST")) {
                                    Integer idLista = Integer.valueOf(res.getValor());
                                    Catalogo_Lista valor = respuestasExamenService.getCatalogoListaConceptoByIdLista(idLista);

                                    if (valor.getValor().toLowerCase().equals("positivo")) {
                                        agregarDx = true;
                                        break;
                                    }
                                }
                            }
                            if (agregarDx) {
                                DaSolicitudDx solicitudDx = new DaSolicitudDx();
                                solicitudDx.setIdTomaMx(tomaMx);
                                solicitudDx.setAprobada(false);
                                solicitudDx.setCodDx(solDx.getCodDx());
                                solicitudDx.setFechaHSolicitud(fhRegistro);
                                solicitudDx.setControlCalidad(true);
                                solicitudDx.setUsarioRegistro(usurioRegistro);
                                solicitudDx.setLabProcesa(labDestino);
                                tomaMxService.addSolicitudDx(solicitudDx);
                                tomaMx.setEstadoMx(estadoMx);//cambiar a estado trasladada
                            }

                        }
                        DaEnvioMx envioMx = new DaEnvioMx();
                        envioMx.setLaboratorioDestino(labDestino);
                        envioMx.setUsarioRegistro(usurioRegistro);
                        envioMx.setFechaHoraEnvio(fhRegistro);
                        envioMx.setNombreTransporta(nombreTransporta);
                        envioMx.setTemperaturaTermo(temperaturaTermo);

                        try {
                            tomaMxService.addEnvioOrden(envioMx);
                        }catch (Exception ex){
                            resultado = messageSource.getMessage("msg.sending.error.add",null,null);
                            resultado=resultado+". \n "+ex.getMessage();
                            ex.printStackTrace();
                            throw new Exception(ex);
                        }
                        //antes enviar a histórico relación mx y enviomx
                        HistoricoEnvioMx historicoEnvioMx = new HistoricoEnvioMx();
                        historicoEnvioMx.setEnvioMx(tomaMx.getEnvio());
                        historicoEnvioMx.setTomaMx(tomaMx);
                        historicoEnvioMx.setFechaHoraRegistro(fhRegistro);
                        historicoEnvioMx.setUsuarioRegistro(seguridadService.getUsuario(seguridadService.obtenerNombreUsuario()));
                        trasladosService.saveHistoricoEnvioMx(historicoEnvioMx);
                        //se setea nuevo envio
                        tomaMx.setEnvio(envioMx);
                    }

                } catch (Exception ex) {
                    resultado = messageSource.getMessage("msg.add.receipt.error", null, null);
                    resultado = resultado + ". \n " + ex.getMessage();
                    ex.printStackTrace();
                    throw new Exception(ex);
                }

                    //se tiene que actualizar la tomaMx (estado y Envio para cc)
                       try {
                        trasladosService.saveTrasladoMx(trasladoMx);
                        tomaMxService.updateTomaMx(tomaMx);
                        cantMxProc++;
                        if(cantMxProc==1)
                            codigosUnicosMx = tomaMx.getCodigoUnicoMx();
                        else
                            codigosUnicosMx += ","+ tomaMx.getCodigoUnicoMx();
                    } catch (Exception ex) {
                        resultado = messageSource.getMessage("msg.update.order.error", null, null);
                        resultado = resultado + ". \n " + ex.getMessage();
                        ex.printStackTrace();
                           throw new Exception(ex);
                    }
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.receipt.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("strMuestras",strMuestras);
            map.put("mensaje",resultado);
            map.put("cantMuestras", cantMuestras.toString());
            map.put("cantMxProc", cantMxProc.toString());
            map.put("codigosUnicosMx",codigosUnicosMx);
            map.put("tipoTraslado",tipoTraslado);
            map.put("idRutina",idRutina);
            map.put("nombreTransporta",nombreTransporta);
            map.put("temperaturaTermo",(temperaturaTermo!=null?String.valueOf(temperaturaTermo):""));
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    /**
     * Método para convertir estructura Json que se recibe desde el cliente a FiltroMx para realizar búsqueda de Mx
     * @param strJson String con la información de los filtros
     * @return FiltroMx
     * @throws Exception
     */
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
        String esLab = null;
        String codigoUnicoMx = null;
        String codTipoSolicitud = null;
        String nombreSolicitud = null;
        String tipoTraslado="";

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
        if (jObjectFiltro.get("esLab") !=null && !jObjectFiltro.get("esLab").getAsString().isEmpty())
            esLab = jObjectFiltro.get("esLab").getAsString();
        if (jObjectFiltro.get("codigoUnicoMx") != null && !jObjectFiltro.get("codigoUnicoMx").getAsString().isEmpty())
            codigoUnicoMx = jObjectFiltro.get("codigoUnicoMx").getAsString();
        if (jObjectFiltro.get("codTipoSolicitud") != null && !jObjectFiltro.get("codTipoSolicitud").getAsString().isEmpty())
            codTipoSolicitud = jObjectFiltro.get("codTipoSolicitud").getAsString();
        if (jObjectFiltro.get("nombreSolicitud") != null && !jObjectFiltro.get("nombreSolicitud").getAsString().isEmpty())
            nombreSolicitud = jObjectFiltro.get("nombreSolicitud").getAsString();
        if (jObjectFiltro.get("tipoTraslado") != null && !jObjectFiltro.get("tipoTraslado").getAsString().isEmpty())
            tipoTraslado = jObjectFiltro.get("tipoTraslado").getAsString();


        filtroMx.setCodSilais(codSilais);
        filtroMx.setCodUnidadSalud(codUnidadSalud);
        filtroMx.setFechaInicioTomaMx(fechaInicioTomaMx);
        filtroMx.setFechaFinTomaMx(fechaFinTomaMx);
        filtroMx.setFechaInicioRecep(fechaInicioRecep);
        filtroMx.setFechaFinRecep(fechaFinRecep);
        filtroMx.setNombreApellido(nombreApellido);
        filtroMx.setCodTipoMx(codTipoMx);
        filtroMx.setCodTipoSolicitud(codTipoSolicitud);
        filtroMx.setNombreSolicitud(nombreSolicitud);
        filtroMx.setCodEstado("ESTDMX|RCLAB"); // sólo las enviadas
        filtroMx.setCodigoUnicoMx(codigoUnicoMx);
        filtroMx.setNombreUsuario(seguridadService.obtenerNombreUsuario());
        if (tipoTraslado.equals("cc")){ //sólo para traslado al CNDR la solicitud tiene que estar aprobada
            filtroMx.setSolicitudAprobada(true);
        }
        return filtroMx;
    }

    /**
     * Método que convierte una lista de tomaMx a un string con estructura Json
     * @param tomaMxList lista con las tomaMx a convertir
     * @return String
     */
    private String tomaMxToJson(List<DaTomaMx> tomaMxList){
        String jsonResponse;
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice=0;
        for(DaTomaMx tomaMx : tomaMxList){
            Map<String, String> map = new HashMap<String, String>();
            //map.put("idOrdenExamen",tomaMx.getIdOrdenExamen());
            map.put("idTomaMx", tomaMx.getIdTomaMx());
            map.put("codigoUnicoMx", tomaMx.getCodigoUnicoMx());
            //map.put("fechaHoraOrden",DateUtil.DateToString(tomaMx.getFechaHOrden(), "dd/MM/yyyy hh:mm:ss a"));
            map.put("fechaTomaMx",DateUtil.DateToString(tomaMx.getFechaHTomaMx(), "dd/MM/yyyy hh:mm:ss a"));
            if (tomaMx.getIdNotificacion().getCodSilaisAtencion()!=null) {
                map.put("codSilais", tomaMx.getIdNotificacion().getCodSilaisAtencion().getNombre());
            }else{
                map.put("codSilais","");
            }
            if (tomaMx.getIdNotificacion().getCodUnidadAtencion()!=null) {
                map.put("codUnidadSalud", tomaMx.getIdNotificacion().getCodUnidadAtencion().getNombre());
            }else {
                map.put("codUnidadSalud","");
            }
            //map.put("estadoOrden", tomaMx.getCodEstado().getValor());
            map.put("separadaMx",(tomaMx.getMxSeparada()!=null?(tomaMx.getMxSeparada()?"Si":"No"):""));
            map.put("cantidadTubos", (tomaMx.getCanTubos()!=null?String.valueOf(tomaMx.getCanTubos()):""));
            map.put("tipoMuestra", tomaMx.getCodTipoMx().getNombre());
            //map.put("tipoExamen", tomaMx.getCodExamen().getNombre());
            //Si hay fecha de inicio de sintomas se muestra
            Date fechaInicioSintomas = tomaMx.getIdNotificacion().getFechaInicioSintomas();
            if (fechaInicioSintomas!=null)
                map.put("fechaInicioSintomas",DateUtil.DateToString(fechaInicioSintomas,"dd/MM/yyyy"));
            else
                map.put("fechaInicioSintomas"," ");
            //Si hay persona
            if (tomaMx.getIdNotificacion().getPersona()!=null){
                /// se obtiene el nombre de la persona asociada a la ficha
                String nombreCompleto = "";
                nombreCompleto = tomaMx.getIdNotificacion().getPersona().getPrimerNombre();
                if (tomaMx.getIdNotificacion().getPersona().getSegundoNombre()!=null)
                    nombreCompleto = nombreCompleto +" "+ tomaMx.getIdNotificacion().getPersona().getSegundoNombre();
                nombreCompleto = nombreCompleto+" "+ tomaMx.getIdNotificacion().getPersona().getPrimerApellido();
                if (tomaMx.getIdNotificacion().getPersona().getSegundoApellido()!=null)
                    nombreCompleto = nombreCompleto +" "+ tomaMx.getIdNotificacion().getPersona().getSegundoApellido();
                map.put("persona",nombreCompleto);
            }else{
                map.put("persona"," ");
            }

            //se arma estructura de solicitudes
            Laboratorio labUser = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
            List<DaSolicitudDx> solicitudDxList = tomaMxService.getSolicitudesDxByIdToma(tomaMx.getIdTomaMx(),labUser.getCodigo());
            Map<Integer, Object> mapSolicitudesList = new HashMap<Integer, Object>();
            Map<String, String> mapSolicitud = new HashMap<String, String>();
            if (solicitudDxList.size()>0) {
                int subIndice=0;
                for (DaSolicitudDx solicitudDx : solicitudDxList) {
                    mapSolicitud.put("nombre", solicitudDx.getCodDx().getNombre());
                    mapSolicitud.put("tipo", "Rutina");
                    mapSolicitud.put("fechaSolicitud", DateUtil.DateToString(solicitudDx.getFechaHSolicitud(), "dd/MM/yyyy hh:mm:ss a"));
                    subIndice++;
                    mapSolicitudesList.put(subIndice, mapSolicitud);
                    mapSolicitud = new HashMap<String, String>();
                }
                map.put("solicitudes", new Gson().toJson(mapSolicitudesList));
            }else{
                List<DaSolicitudEstudio> solicitudEstudios = tomaMxService.getSolicitudesEstudioByIdTomaMx(tomaMx.getIdTomaMx());
                int subIndice=0;
                for (DaSolicitudEstudio solicitudEstudio : solicitudEstudios) {
                    mapSolicitud.put("nombre", solicitudEstudio.getTipoEstudio().getNombre());
                    mapSolicitud.put("tipo", "Estudio");
                    mapSolicitud.put("fechaSolicitud", DateUtil.DateToString(solicitudEstudio.getFechaHSolicitud(), "dd/MM/yyyy hh:mm:ss a"));
                    subIndice++;
                    mapSolicitudesList.put(subIndice, mapSolicitud);
                    mapSolicitud = new HashMap<String, String>();
                }
                map.put("solicitudes", new Gson().toJson(mapSolicitudesList));
            }
            mapResponse.put(indice, map);
            indice ++;
        }

        jsonResponse = new Gson().toJson(mapResponse);
        UnicodeEscaper escaper     = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }

}
