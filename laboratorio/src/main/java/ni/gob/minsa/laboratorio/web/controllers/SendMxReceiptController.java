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
import ni.gob.minsa.laboratorio.domain.seguridadlocal.User;
import ni.gob.minsa.laboratorio.service.*;
import ni.gob.minsa.laboratorio.utilities.ConstantsSecurity;
import ni.gob.minsa.laboratorio.utilities.DateUtil;
import ni.gob.minsa.laboratorio.utilities.reportes.DatosRecepcionMx;
import ni.gob.minsa.laboratorio.utilities.reportes.Solicitud;
import org.apache.commons.lang3.text.translate.UnicodeEscaper;
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
import java.io.*;
import java.sql.Timestamp;
import java.util.*;
import java.util.List;

/**
 * Created by Miguel Salinas on 12/10/2014.
 * V 1.0
 */
@Controller
@RequestMapping("sendMxReceipt")
public class SendMxReceiptController {

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

    @Resource(name = "parametrosService")
    private ParametrosService parametrosService;

    @Resource(name = "laboratoriosService")
    private LaboratoriosService laboratoriosService;

    @Resource(name = "usuarioService")
    private UsuarioService usuarioService;

    @Resource(name = "trasladosService")
    private TrasladosService trasladosService;

    @Autowired
    MessageSource messageSource;

    /**
     * M�todo que se llama al entrar a la opci�n de menu "Enviar Mx Recepcionadas". Se encarga de inicializar las listas para realizar la b�squeda de recepcionesMx
     * @param request para obtener informaci�n de la petici�n del cliente
     * @return ModelAndView
     * @throws Exception
     */
    @RequestMapping(value = "init", method = RequestMethod.GET)
    public ModelAndView initSearchForm(HttpServletRequest request) throws Exception {
        logger.debug("buscar ordenes para recepcion");
        String urlValidacion;
        try {
            urlValidacion = seguridadService.validarLogin(request);
            //si la url esta vacia significa que la validaci�n del login fue exitosa
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
            mav.addObject("area",areaList);
            mav.setViewName("recepcionMx/sendOrdersReceiptToLab");
        }else
            mav.setViewName(urlValidacion);

        return mav;
    }

    /**
     * M�todo para realizar la b�squeda de recepcionesMx para enviar a recepci�n de Mx en laboratorio
     * @param filtro JSon con los datos de los filtros a aplicar en la b�squeda(Nombre Apellido, Rango Fec Toma Mx, Tipo Mx, SILAIS, unidad salud)
     * @return String con las recepcionesMx encontradas
     * @throws Exception
     */
    @RequestMapping(value = "searchOrders", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String fetchOrdersJson(@RequestParam(value = "strFilter", required = true) String filtro) throws Exception{
        logger.info("Obteniendo las ordenes de examen pendienetes seg�n filtros en JSON");
        FiltroMx filtroMx = jsonToFiltroMx(filtro);
        List<RecepcionMx> recepcionMxList = recepcionMxService.getRecepcionesByFiltro(filtroMx);
        return RecepcionMxToJson(recepcionMxList);
    }

    /**
     * M�todo para enviar una recepci�n de muestra de vigilancia a recepci�n en laboratorio que procesa. Modifica la Mx al estado ESTDMX|EPLAB
     * @param request para obtener informaci�n de la petici�n del cliente. Contiene en un par�metro la estructura json del registro a actualizar
     * @param response para notificar al cliente del resultado de la operaci�n
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "sendReceipt", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void sendReceiptLaboratory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        String strOrdenes="";
        String numeroHoja="";
        Integer cantRecepciones = 0;
        Integer cantRecepProc = 0;
        Date fechaHoraEnvio = new Date();
        String fechaEnvio = "";
        String horaEnvio = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            strOrdenes = jsonpObject.get("strOrdenes").toString();
            cantRecepciones = jsonpObject.get("cantRecepciones").getAsInt();
            if (jsonpObject.get("fechaEnvio") != null && !jsonpObject.get("fechaEnvio").getAsString().isEmpty())
                fechaEnvio = jsonpObject.get("fechaEnvio").getAsString();
            if (jsonpObject.get("horaEnvio") != null && !jsonpObject.get("horaEnvio").getAsString().isEmpty())
                horaEnvio = jsonpObject.get("horaEnvio").getAsString();

            if (!fechaEnvio.isEmpty()){
                if (horaEnvio.isEmpty())
                    fechaHoraEnvio = DateUtil.StringToDate(fechaEnvio, "dd/MM/yyyy");
                else
                    fechaHoraEnvio = DateUtil.StringToDate(fechaEnvio+ " "+horaEnvio, "dd/MM/yyyy hh:mm a");
            }

            //Se obtiene estado enviado a procesar en laboratorio
            EstadoMx estadoMx = catalogosService.getEstadoMx("ESTDMX|EPLAB");
            //se obtiene muestra a enviar a laboratorio
            DaTomaMx tomaMx;
            JsonObject jObjectTomasMx = new Gson().fromJson(strOrdenes, JsonObject.class);
            HojaTrabajo hojaTrabajo = new HojaTrabajo();
            Laboratorio labUser = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
            hojaTrabajo.setNumero(hojaTrabajoService.obtenerNumeroHoja(labUser.getCodigo()));
            hojaTrabajo.setUsuarioRegistro(seguridadService.getUsuario(seguridadService.obtenerNombreUsuario()));
            hojaTrabajo.setFechaRegistro(fechaHoraEnvio);
            hojaTrabajo.setLaboratorio(labUser);
            //se crea hoja de trabajo
            try {
                hojaTrabajoService.addHojaTrabajo(hojaTrabajo);
                numeroHoja = String.valueOf(hojaTrabajo.getNumero());
            }catch (Exception ex){
                throw new Exception(ex);
            }
            if (hojaTrabajo.getIdHojaTrabajo()!=null) {
                for (int i = 0; i < cantRecepciones; i++) {
                    String idTomaMx = jObjectTomasMx.get(String.valueOf(i)).getAsString();
                    tomaMx = tomaMxService.getTomaMxById(idTomaMx);
                    if (tomaMx != null) {
                        //se tiene que actualizar el estado de la muestra a ENVIADA PARA PROCESAR EN LABORATORIO
                        tomaMx.setEstadoMx(estadoMx);
                        try {
                            tomaMxService.updateTomaMx(tomaMx);
                            //se registra muestra en hoja de trabajo
                            Mx_HojaTrabajo mxHojaTrabajo = new Mx_HojaTrabajo();
                            mxHojaTrabajo.setHojaTrabajo(hojaTrabajo);
                            mxHojaTrabajo.setTomaMx(tomaMx);
                            mxHojaTrabajo.setFechaRegistro(new Date());
                            hojaTrabajoService.addDetalleHojaTrabajo(mxHojaTrabajo);

                            cantRecepProc++;
                        } catch (Exception ex) {
                            resultado = messageSource.getMessage("msg.update.order.error", null, null);
                            resultado = resultado + ". \n " + ex.getMessage();
                            ex.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.send.receipt.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("strOrdenes",strOrdenes);
            map.put("mensaje",resultado);
            map.put("cantRecepciones",cantRecepciones.toString());
            map.put("cantRecepProc",cantRecepProc.toString());
            map.put("numeroHoja",numeroHoja);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    /**
     * M�todo para convertir una lista de RecepcionMx a un string con estructura Json
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
            //map.put("idOrdenExamen", recepcion.getOrdenExamen().getIdOrdenExamen());
            map.put("idTomaMx", recepcion.getTomaMx().getIdTomaMx());
            //notificacion urgente
            if(recepcion.getTomaMx().getIdNotificacion().getUrgente()!= null){
                map.put("urgente", recepcion.getTomaMx().getIdNotificacion().getUrgente().getValor());
            }else{
                map.put("urgente", "--");
            }
            map.put("codigoUnicoMx", esEstudio?recepcion.getTomaMx().getCodigoUnicoMx():recepcion.getTomaMx().getCodigoLab());
            //map.put("fechaHoraOrden",DateUtil.DateToString(recepcion.getOrdenExamen().getFechaHOrden(), "dd/MM/yyyy hh:mm:ss a"));
            map.put("fechaTomaMx", DateUtil.DateToString(recepcion.getTomaMx().getFechaHTomaMx(), "dd/MM/yyyy")+
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
            //map.put("estadoOrden", recepcion.getOrdenExamen().getCodEstado().getValor());
            map.put("separadaMx", (recepcion.getTomaMx().getMxSeparada() != null ? (recepcion.getTomaMx().getMxSeparada() ? "Si" : "No") : ""));
            map.put("cantidadTubos", (recepcion.getTomaMx().getCanTubos() != null ? String.valueOf(recepcion.getTomaMx().getCanTubos()) : ""));
            map.put("tipoMuestra", recepcion.getTomaMx().getCodTipoMx().getNombre());
            //map.put("tipoExamen", recepcion.getOrdenExamen().getCodExamen().getNombre());
            //map.put("areaProcesa", recepcion.getOrdenExamen().getCodExamen().getArea().getNombre());
            //Si hay fecha de inicio de sintomas se muestra
            Date fechaInicioSintomas = recepcion.getTomaMx().getIdNotificacion().getFechaInicioSintomas();
            if (fechaInicioSintomas != null)
                map.put("fechaInicioSintomas", DateUtil.DateToString(fechaInicioSintomas, "dd/MM/yyyy"));
            else
                map.put("fechaInicioSintomas", " ");
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
            }else if (recepcion.getTomaMx().getIdNotificacion().getCodigoPacienteVIH() != null) {
            	map.put("persona", recepcion.getTomaMx().getIdNotificacion().getCodigoPacienteVIH());
            }else{
                map.put("persona", " ");
            }
            //se arma estructura de diagn�sticos o estudios
            Laboratorio labUser = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
            List<DaSolicitudDx> solicitudDxList = tomaMxService.getSolicitudesDxByIdToma(recepcion.getTomaMx().getIdTomaMx(), labUser.getCodigo());
            Map<Integer, Object> mapSolicitudesList = new HashMap<Integer, Object>();
            Map<String, String> mapSolicitud = new HashMap<String, String>();
                int subIndice = 0;
                for (DaSolicitudDx solicitudDx : solicitudDxList) {
                    mapSolicitud.put("nombre", solicitudDx.getCodDx().getNombre());
                    mapSolicitud.put("tipo", "Rutina");
                    mapSolicitud.put("fechaSolicitud", DateUtil.DateToString(solicitudDx.getFechaHSolicitud(), "dd/MM/yyyy hh:mm:ss a"));
                    subIndice++;
                    mapSolicitudesList.put(subIndice, mapSolicitud);
                    mapSolicitud = new HashMap<String, String>();
                }
                map.put("solicitudes", new Gson().toJson(mapSolicitudesList));
                List<DaSolicitudEstudio> solicitudEstudios = tomaMxService.getSolicitudesEstudioByIdTomaMx(recepcion.getTomaMx().getIdTomaMx());
                for (DaSolicitudEstudio solicitudEstudio : solicitudEstudios) {
                    mapSolicitud.put("nombre", solicitudEstudio.getTipoEstudio().getNombre());
                    mapSolicitud.put("tipo", "Estudio");
                    mapSolicitud.put("fechaSolicitud", DateUtil.DateToString(solicitudEstudio.getFechaHSolicitud(), "dd/MM/yyyy hh:mm:ss a"));
                    subIndice++;
                    mapSolicitudesList.put(subIndice, mapSolicitud);
                    mapSolicitud = new HashMap<String, String>();
                }
                map.put("solicitudes", new Gson().toJson(mapSolicitudesList));

            mapResponse.put(indice, map);
            indice++;
        }
        jsonResponse = new Gson().toJson(mapResponse);
        //escapar caracteres especiales, escape de los caracteres con valor num�rico mayor a 127
        UnicodeEscaper escaper     = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }

    /**
     * M�todo para convertir estructura Json que se recibe desde el cliente a FiltroMx para realizar b�squeda de Mx(Vigilancia) y Recepci�n Mx(Laboratorio)
     * @param strJson String con la informaci�n de los filtros
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
        String idAreaProcesa = null;
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
        if (jObjectFiltro.get("idArea") != null && !jObjectFiltro.get("idArea").getAsString().isEmpty())
            idAreaProcesa = jObjectFiltro.get("idArea").getAsString();
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
        filtroMx.setCodEstado("ESTDMX|RCP"); // s�lo las recepcionadas
        filtroMx.setIncluirMxInadecuada(true);
        filtroMx.setCodigoUnicoMx(codigoUnicoMx);
        filtroMx.setNombreUsuario(seguridadService.obtenerNombreUsuario());
        filtroMx.setIncluirTraslados(false);
        return filtroMx;
    }

    /*******MOVER MUESTRAS DE VIAJEROS DEL CNDR HACIA INIS******/
    /**
     * M�todo que se llama al entrar a la opci�n de menu "Trasladar Viajeros".
     * @param request para obtener informaci�n de la petici�n del cliente
     * @return ModelAndView
     * @throws Exception
     */
    @RequestMapping(value = "travelers", method = RequestMethod.GET)
    public ModelAndView sendTravelersHome(HttpServletRequest request) throws Exception {
        logger.debug("buscar ordenes para enviar a INIS");
        ModelAndView mav = new ModelAndView();
        Laboratorio laboratorio = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
        List<Laboratorio> laboratorioList = laboratoriosService.getLaboratoriosViajeros(laboratorio.getCodigo());
        if (laboratorioList != null) {
            mav.addObject("laboratorios", laboratorioList);
            mav.setViewName("viajeros/moveSamples");
        } else {
            mav.setViewName("403");
        }
        return mav;
    }

    /**
     * M�todo para realizar la b�squeda de recepcionesMx para enviar a recepci�n de Mx en laboratorio
     * @param filtro JSon con los datos de los filtros a aplicar en la b�squeda(Nombre Apellido, Rango Fec Toma Mx, Tipo Mx, SILAIS, unidad salud)
     * @return String con las recepcionesMx encontradas
     * @throws Exception
     */
    @RequestMapping(value = "searchTravelers", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String fetchReceptionsJson(@RequestParam(value = "strFilter", required = true) String filtro) throws Exception{
        logger.info("Obteniendo las recepciones de muestras seg�n filtros en JSON");
        FiltroMx filtroMx = jsonToFiltroDxViajero(filtro);
        List<DatosRecepcionMx> recepcionMxList = recepcionMxService.getRecepcionesByFiltros(filtroMx);
        return DatosRecepcionMxToJson(recepcionMxList);
    }

    @RequestMapping(value = "moveTravelers", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void moveTravelers(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        String idRecepcion="";
        String codigolab = "";
        Integer cantProcesadas = 0;
        String numeroHoja = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            String strSolicitudes = jsonpObject.get("strRecepciones").toString();
            Integer cantPublicaciones = jsonpObject.get("cantRecepciones").getAsInt();
            codigolab = jsonpObject.get("codigolab").getAsString();
            JsonObject jObjectSolicitudes = new Gson().fromJson(strSolicitudes, JsonObject.class);

            Integer idDxViajero = Integer.valueOf(parametrosService.getParametroByName("DX_VIAJERO_COVID19").getValor());
            EstadoMx estadoMx = catalogosService.getEstadoMx("ESTDMX|EPLAB"); //para que aparezca en recepci�n laboratorio
            Parametro pUsuarioDefecto = parametrosService.getParametroByName("USU_REGISTRO_TRASLADO");// para registrar traslado, pero ya recepcionado

            User usuarioLab = seguridadService.getUsuario(seguridadService.obtenerNombreUsuario());//usuario que realiza el traslado
            Laboratorio labDestino = laboratoriosService.getLaboratorioByCodigo(codigolab);//obtener laboratorio hacia d�nde se envia a procesar
            Laboratorio labUser = seguridadService.getLaboratorioUsuario(usuarioLab.getUsername());
            Usuarios usurioSis = null;
            if (pUsuarioDefecto!=null){
                usurioSis = usuarioService.getUsuarioById(Integer.valueOf(pUsuarioDefecto.getValor()));//usuario para alerta
            }

            try {
                HojaTrabajo hojaTrabajo = new HojaTrabajo();
                hojaTrabajo.setNumero(hojaTrabajoService.obtenerNumeroHoja(labUser.getCodigo()));
                hojaTrabajo.setUsuarioRegistro(seguridadService.getUsuario(seguridadService.obtenerNombreUsuario()));
                hojaTrabajo.setFechaRegistro(new Date());
                hojaTrabajo.setLaboratorio(labUser);
                //se crea hoja de trabajo

                hojaTrabajoService.addHojaTrabajo(hojaTrabajo);
                numeroHoja = String.valueOf(hojaTrabajo.getNumero());

                for (int i = 0; i < cantPublicaciones; i++) {
                    idRecepcion = jObjectSolicitudes.get(String.valueOf(i)).getAsString();
                    RecepcionMx recepcionMx = recepcionMxService.getRecepcionMx(idRecepcion);
                    if (recepcionMx == null) {
                        throw new Exception(messageSource.getMessage("msg.sample.to.move.not.found", null, null));
                    } else {
                        cantProcesadas += moveSample(recepcionMx, estadoMx, labDestino, usurioSis, usuarioLab, idDxViajero, hojaTrabajo);
                    }
                }
            }catch (Exception ex){
                throw new Exception(ex);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.error.move.sample",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("strRecepciones","-");
            map.put("cantRecepciones","-");
            map.put("codigolab","-");
            map.put("cantProcesadas", cantProcesadas.toString());
            map.put("mensaje",resultado);
            map.put("numeroHoja",numeroHoja);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    private int moveSample(RecepcionMx recepcionMx, EstadoMx estadoMx, Laboratorio labDestino, Usuarios usurioRegistro, User usuario, Integer idDxViajero, HojaTrabajo hojaTrabajo) {
        TrasladoMx trasladoMx = new TrasladoMx();
        DaEnvioMx envioMx = new DaEnvioMx();
        HistoricoEnvioMx historicoEnvioMx = new HistoricoEnvioMx();
        Laboratorio labOrigen = recepcionMx.getLabRecepcion();
        DaTomaMx tomaMx = recepcionMx.getTomaMx();
        DaSolicitudDx solicitudDx = null;
        EstadoMx estadoActual = tomaMx.getEstadoMx();
        DaEnvioMx envioActual = tomaMx.getEnvio();
        try {
            boolean esMxViajeroCovid = tomaMxService.esMxViajeroCovid(recepcionMx.getTomaMx().getCodigoLab());
            if (esMxViajeroCovid) {
                //registrar nueva recepci�n en el laboratorio destino, para obviar proceso de recepci�n general
                recepcionMx.setIdRecepcion(null);
                recepcionMx.setLabRecepcion(labDestino);
                recepcionMx.setUsuarioRecepcion(usuario);
                recepcionMx.setFechaHoraRecepcion(new Timestamp(new Date().getTime()));
                recepcionMx.setTrasladoViajero(true);
                recepcionMxService.addRecepcionMx(recepcionMx);

                //se recupera la solicitud de dx existente para la muestra y el dx Biologia molecular covid19
                solicitudDx = tomaMxService.getSolicitudesDxByMxDx(tomaMx.getIdTomaMx(), idDxViajero);
                if (solicitudDx != null) {
                    solicitudDx.setLabProcesa(labDestino);
                }
                tomaMxService.updateSolicitudDx(solicitudDx);//actualizar laboratorio que procesa solicitud dx

                //Se registra envio de mx hacia otro laboratorio
                envioMx.setLaboratorioDestino(labDestino);
                envioMx.setUsarioRegistro(usurioRegistro);
                envioMx.setFechaHoraEnvio(new Timestamp(new Date().getTime()));
                envioMx.setNombreTransporta("ES VIAJERO");
                envioMx.setTemperaturaTermo(null);
                try {
                    tomaMxService.addEnvioOrden(envioMx);
                    //se setea nuevo envio
                    tomaMx.setEnvio(envioMx);
                } catch (Exception ex) {
                    String resultado = messageSource.getMessage("msg.sending.error.add", null, null);
                    resultado = resultado + ". \n " + ex.getMessage();
                    ex.printStackTrace();
                    throw new Exception(resultado);
                }
                //antes enviar a historico relacion mx y enviomx
                historicoEnvioMx.setEnvioMx(envioActual);
                historicoEnvioMx.setTomaMx(tomaMx);
                historicoEnvioMx.setFechaHoraRegistro(new Timestamp(new Date().getTime()));
                historicoEnvioMx.setUsuarioRegistro(seguridadService.getUsuario(seguridadService.obtenerNombreUsuario()));
                trasladosService.saveHistoricoEnvioMx(historicoEnvioMx);

                //registramos traslado externo, pero ya recepcionado

                trasladoMx.setTomaMx(tomaMx);
                trasladoMx.setFechaHoraRegistro(new Timestamp(new Date().getTime()));
                trasladoMx.setRecepcionado(true);
                trasladoMx.setUsuarioRegistro(seguridadService.obtenerNombreUsuario());
                trasladoMx.setTrasladoExterno(true);
                trasladoMx.setLaboratorioDestino(labDestino);
                trasladoMx.setLaboratorioOrigen(labOrigen);
                trasladoMx.setPrioridad(1);
                trasladosService.saveTrasladoMx(trasladoMx);

                tomaMx.setEstadoMx(estadoMx);//cambiar a estado enviada para procesar en laboratorio
                tomaMxService.updateTomaMx(tomaMx);//actualizar muestra

                //se registra muestra en hoja de trabajo
                Mx_HojaTrabajo mxHojaTrabajo = new Mx_HojaTrabajo();
                mxHojaTrabajo.setHojaTrabajo(hojaTrabajo);
                mxHojaTrabajo.setTomaMx(tomaMx);
                mxHojaTrabajo.setFechaRegistro(new Date());
                hojaTrabajoService.addDetalleHojaTrabajo(mxHojaTrabajo);
            }
        }catch (Exception ex) {
            ex.printStackTrace();
            try {
                recepcionMxService.deleteRecepcionMx(recepcionMx);
                if (trasladoMx.getIdTraslado() != null)
                    trasladosService.deleteTrasladoMx(trasladoMx);
                if (envioMx.getIdEnvio() != null)
                    tomaMxService.deleteEnvioOrden(envioMx);
                if (historicoEnvioMx.getIdHistorico() != null)
                    trasladosService.deleteHistoricoEnvioMx(historicoEnvioMx);
                if (solicitudDx != null) {
                    solicitudDx.setLabProcesa(labOrigen);
                }
                tomaMxService.updateSolicitudDx(solicitudDx);//actualizar laboratorio que procesa al original

                tomaMx.setEnvio(envioActual);
                tomaMx.setEstadoMx(estadoActual);//cambiar a estado enviada para procesar en laboratorio
                tomaMxService.updateTomaMx(tomaMx);//actualizar muestra

            } catch (Exception ex2) {
                ex2.printStackTrace();
            }

            return 0;
        }
        return 1;
    }

    private FiltroMx jsonToFiltroDxViajero(String strJson) throws Exception {
        JsonObject jObjectFiltro = new Gson().fromJson(strJson, JsonObject.class);
        FiltroMx filtroMx = new FiltroMx();
        String codigoUnicoMx = null;
        Date fechaInicioRecep = null;
        Date fechaFinRecep = null;


        if (jObjectFiltro.get("codigoUnicoMx") != null && !jObjectFiltro.get("codigoUnicoMx").getAsString().isEmpty())
            codigoUnicoMx = jObjectFiltro.get("codigoUnicoMx").getAsString();
        if (jObjectFiltro.get("fechaInicioRecep") != null && !jObjectFiltro.get("fechaInicioRecep").getAsString().isEmpty())
            fechaInicioRecep = DateUtil.StringToDate(jObjectFiltro.get("fechaInicioRecep").getAsString());
        if (jObjectFiltro.get("fechaFinRecep") != null && !jObjectFiltro.get("fechaFinRecep").getAsString().isEmpty())
            fechaFinRecep = DateUtil.StringToDate(jObjectFiltro.get("fechaFinRecep").getAsString());

        filtroMx.setIdDx(Integer.valueOf(parametrosService.getParametroByName("DX_VIAJERO_COVID19").getValor()));
        filtroMx.setCodLaboratio(seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario()).getCodigo());
        filtroMx.setCodigoUnicoMx(codigoUnicoMx);
        filtroMx.setFechaInicioRecep(fechaInicioRecep);
        filtroMx.setFechaFinRecep(fechaFinRecep);
        filtroMx.setCodEstado("ESTDMX|RCP"); // s�lo las recepcionadas
        return filtroMx;
    }

    /**
     * M�todo para convertir una lista de RecepcionMx a un string con estructura Json
     * @param recepcionMxList lista con las Recepciones a convertir
     * @return String
     */
    private String DatosRecepcionMxToJson(List<DatosRecepcionMx> recepcionMxList){
        String jsonResponse;
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice=0;
        Laboratorio labUser = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
        for(DatosRecepcionMx recepcion : recepcionMxList){
            Map<String, String> map = new HashMap<String, String>();
            map.put("idRecepcion", recepcion.getIdRecepcion());
            map.put("idTomaMx", recepcion.getTomaMx());
            //notificacion urgente
            map.put("urgente", recepcion.getUrgente());
            map.put("codigoUnicoMx", recepcion.getCodigoMx());
            map.put("fechaTomaMx", DateUtil.DateToString(recepcion.getFechaTomaMx(), "dd/MM/yyyy"));
            map.put("fechaRecepcion", DateUtil.DateToString(recepcion.getFechaHoraRecepcion(), "dd/MM/yyyy hh:mm:ss a"));
            map.put("codSilais", recepcion.getNombreSilaisMx());
            map.put("codUnidadSalud", recepcion.getNombreUnidadMx());

            map.put("tipoMuestra", recepcion.getNombreTipoMx());
            /// se obtiene el nombre de la persona asociada a la ficha
            String nombreCompleto = recepcion.getPrimerNombre();
            if (recepcion.getSegundoNombre() != null) nombreCompleto = nombreCompleto + " " + recepcion.getSegundoNombre();
            if (recepcion.getPrimerApellido() != null) nombreCompleto = nombreCompleto + " " + recepcion.getPrimerApellido();
            if (recepcion.getSegundoApellido() != null) nombreCompleto = nombreCompleto + " " + recepcion.getSegundoApellido();
                map.put("persona", nombreCompleto);

            //se arma estructura de diagn�sticos
            List<Solicitud> solicitudDxList = tomaMxService.getSolicitudesDxByIdTomaV2(recepcion.getTomaMx(), labUser.getCodigo());
            Map<Integer, Object> mapSolicitudesList = new HashMap<Integer, Object>();
            Map<String, String> mapSolicitud = new HashMap<String, String>();
            int subIndice = 0;
            for (Solicitud solicitudDx : solicitudDxList) {
                mapSolicitud.put("nombre", solicitudDx.getNombre());
                mapSolicitud.put("tipo", "Rutina");
                mapSolicitud.put("fechaSolicitud", DateUtil.DateToString(solicitudDx.getFechaSolicitud(), "dd/MM/yyyy hh:mm:ss a"));
                subIndice++;
                mapSolicitudesList.put(subIndice, mapSolicitud);
                mapSolicitud = new HashMap<String, String>();
            }
            map.put("solicitudes", new Gson().toJson(mapSolicitudesList));

            mapResponse.put(indice, map);
            indice++;
        }
        jsonResponse = new Gson().toJson(mapResponse);
        //escapar caracteres especiales, escape de los caracteres con valor num�rico mayor a 127
        UnicodeEscaper escaper     = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }
}
