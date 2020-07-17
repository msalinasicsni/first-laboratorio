package ni.gob.minsa.laboratorio.api;

import com.google.gson.Gson;
import ni.gob.minsa.laboratorio.domain.concepto.Catalogo_Lista;
import ni.gob.minsa.laboratorio.domain.estructura.CalendarioEpi;
import ni.gob.minsa.laboratorio.domain.estructura.Unidades;
import ni.gob.minsa.laboratorio.domain.examen.Area;
import ni.gob.minsa.laboratorio.domain.examen.CatalogoExamenes;
import ni.gob.minsa.laboratorio.domain.examen.Direccion;
import ni.gob.minsa.laboratorio.domain.muestra.*;
import ni.gob.minsa.laboratorio.domain.muestra.traslado.TrasladoMx;
import ni.gob.minsa.laboratorio.domain.poblacion.Comunidades;
import ni.gob.minsa.laboratorio.domain.poblacion.Divisionpolitica;
import ni.gob.minsa.laboratorio.domain.poblacion.Sectores;
import ni.gob.minsa.laboratorio.domain.resultados.DetalleResultado;
import ni.gob.minsa.laboratorio.service.*;
import ni.gob.minsa.laboratorio.utilities.ConstantsSecurity;
import ni.gob.minsa.laboratorio.utilities.DateUtil;
import ni.gob.minsa.laboratorio.utilities.enumeration.HealthUnitType;
import ni.gob.minsa.laboratorio.utilities.pdfUtils.GeneralUtils;
import ni.gob.minsa.laboratorio.utilities.reportes.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.text.translate.UnicodeEscaper;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Herrold on 08/06/14 22:13
 * <p/>
 * Clase para exponer datos generales a todas vistas y dispositivos moviles
 * que lo necesiten en una misma ruta.
 */
@Controller
@RequestMapping(value = "/api/v1/")
public class expose {

    private static final Logger logger = LoggerFactory.getLogger(expose.class);
    private static final String COD_NACIONAL_MUNI_MANAGUA = "5525";
    @Autowired(required = true)
    @Qualifier(value = "unidadesService")
    private UnidadesService unidadesService;

    @Autowired
    @Qualifier(value = "divisionPoliticaService")
    private DivisionPoliticaService divisionPoliticaService;

    @Autowired
    @Qualifier(value = "comunidadesService")
    private ComunidadesService comunidadesService;

    @Autowired
    @Qualifier(value = "catalogosService")
    private CatalogoService catalogosService;

    @Autowired
    @Qualifier(value = "calendarioEpiService")
    private CalendarioEpiService calendarioEpiService;

    @Autowired
    @Qualifier(value = "seguridadService")
    private SeguridadService seguridadService;

    @Autowired
    @Qualifier(value = "sectoresService")
    private SectoresService sectoresService;

    @Autowired
    @Qualifier(value = "tomaMxService")
    private TomaMxService tomaMxService;

    @Autowired
    @Qualifier(value = "examenesService")
    private ExamenesService examenesService;

    @Autowired
    @Qualifier(value = "trasladosService")
    private TrasladosService trasladosService;

    @Autowired
    @Qualifier(value = "solicitudService")
    private SolicitudService solicitudService;

    @Autowired
    @Qualifier(value = "resultadoFinalService")
    private ResultadoFinalService resultadoFinalService;

    @Resource(name = "laboratoriosService")
    private LaboratoriosService laboratoriosService;

    @Resource(name = "daNotificacionService")
    private DaNotificacionService notificacionService;

    @Resource(name = "resultadosService")
    private ResultadosService resultadosService;

    @Resource(name = "organizationChartService")
    private OrganizationChartService organizationChartService;

    @Resource(name = "ordenExamenMxService")
    private OrdenExamenMxService ordenExamenMxService;

    @Resource(name = "recepcionMxService")
    private RecepcionMxService recepcionMxService;

    @Autowired
    MessageSource messageSource;

    @RequestMapping(value = "unidades", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    List<Unidades> getUnidadesBySilais(@RequestParam(value = "silaisId", required = true) int silaisId, HttpServletRequest request) throws Exception {
        logger.info("Obteniendo las unidades por municipio en JSON");
        long idUsuario = seguridadService.obtenerIdUsuario(request);
        //Si es usuario a nivel central se cargan todas las unidades del SILAIS
        if(seguridadService.esUsuarioNivelCentral(idUsuario, ConstantsSecurity.SYSTEM_CODE)) {
            return unidadesService.getUnidadesFromEntidades(silaisId);
        }else{//Sino se cargan las unidades a las que esta autorizado el usuario
            return seguridadService.obtenerUnidadesPorUsuarioEntidad((int)idUsuario,(long)silaisId, ConstantsSecurity.SYSTEM_CODE);
        }
    }

    @RequestMapping(value = "municipio", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    List<Divisionpolitica> getmunicipio(@RequestParam(value = "departamentoId", required = true) String departamentoId) throws Exception {
        logger.info("Obteniendo los silais por Departamento en JSON");
        return
                divisionPoliticaService.getMunicipiosFromDepartamento(departamentoId);
    }

    @RequestMapping(value = "municipiosbysilais", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    List<Divisionpolitica> getMunicipiosBySilas(@RequestParam(value = "idSilais", required = true) long idSilais, HttpServletRequest request) throws Exception {
        logger.info("Obteniendo los municipios por silais en JSON");
        long idUsuario = seguridadService.obtenerIdUsuario(request);
        //Si es usuario a nivel central se cargan todos los municipios asociados al SILAIS
        if(seguridadService.esUsuarioNivelCentral(idUsuario, ConstantsSecurity.SYSTEM_CODE)) {
            return  divisionPoliticaService.getMunicipiosBySilais(idSilais);
        }
        else{//sino sólo se cargan los municipios a los que esta autorizado el usuario
          return seguridadService.obtenerMunicipiosPorUsuarioEntidad((int)idUsuario,idSilais, ConstantsSecurity.SYSTEM_CODE);
        }
    }

    @RequestMapping(value = "unidadesPrimarias", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    List<Unidades> getPrimaryUnitsByMunicipioAndSilais(@RequestParam(value = "codMunicipio", required = true) String codMunicipio, @RequestParam(value = "codSilais", required = true) long codSilais, HttpServletRequest request) throws Exception {
        logger.info("Obteniendo las unidades por municipio y SILAIS en JSON");
        long idUsuario = seguridadService.obtenerIdUsuario(request);
        //Si es usuario a nivel central se cargan todas las unidades asociados al SILAIS y municipio
        if(seguridadService.esUsuarioNivelCentral(idUsuario, ConstantsSecurity.SYSTEM_CODE)) {
            return unidadesService.getPrimaryUnitsByMunicipio_Silais(codMunicipio, codSilais, HealthUnitType.UnidadesPrimarias.getDiscriminator().split(","));
        }else{ //sino sólo se cargarn las unidades autorizadas para el usuario según SILAIS y municipio
            return seguridadService.obtenerUnidadesPorUsuarioEntidadMunicipio((int)idUsuario ,codSilais,codMunicipio, ConstantsSecurity.SYSTEM_CODE,HealthUnitType.UnidadesPrimarias.getDiscriminator());
        }
    }

    @RequestMapping(value = "unidadesPrimHosp", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    List<Unidades> getPUnitsHospByMuniAndSilais(@RequestParam(value = "codMunicipio", required = true) String codMunicipio,@RequestParam(value = "codSilais", required = true) long codSilais, HttpServletRequest request) throws Exception {
        logger.info("Obteniendo las unidades primarias y Hospitales por municipio y Silais en JSON");
        long idUsuario = seguridadService.obtenerIdUsuario(request);
        //Si es usuario a nivel central se cargan todas las unidades asociados al SILAIS y municipio
        if(seguridadService.esUsuarioNivelCentral(idUsuario, ConstantsSecurity.SYSTEM_CODE)) {
            return  unidadesService.getPUnitsHospByMuniAndSilais(codMunicipio, HealthUnitType.UnidadesPrimHosp.getDiscriminator().split(","), codSilais);
        }else{ //sino sólo se cargarn las unidades autorizadas para el usuario según SILAIS y municipio
            return seguridadService.obtenerUnidadesPorUsuarioEntidadMunicipio((int) idUsuario, codSilais, codMunicipio, ConstantsSecurity.SYSTEM_CODE, HealthUnitType.UnidadesPrimHosp.getDiscriminator());
        }
    }

    @RequestMapping(value = "unidadesPrimariasSilais", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    List<Unidades> getPrimaryUnitsBySilais(@RequestParam(value = "codSilais", required = true) long codSilais, HttpServletRequest request) throws Exception {
        logger.info("Obteniendo las unidades por SILAIS en JSON");
        long idUsuario = seguridadService.obtenerIdUsuario(request);
        //Si es usuario a nivel central se cargan todas las unidades asociados al SILAIS
        if(seguridadService.esUsuarioNivelCentral(idUsuario, ConstantsSecurity.SYSTEM_CODE)) {
            return unidadesService.getPrimaryUnitsBySilais(codSilais, HealthUnitType.UnidadesPrimarias.getDiscriminator().split(","));
        }else{//sino sólo se cargarn las unidades autorizadas para el usuario según SILAIS
            return seguridadService.obtenerUnidadesPorUsuarioEntidad((int)idUsuario,codSilais, ConstantsSecurity.SYSTEM_CODE,HealthUnitType.UnidadesPrimarias.getDiscriminator());
        }
    }

    @RequestMapping(value = "unidadesPrimariasHospSilais", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    List<Unidades> getPrimaryUnitsAndHospBySilais(@RequestParam(value = "codSilais", required = true) long codSilais, HttpServletRequest request) throws Exception {
        logger.info("Obteniendo las unidades por SILAIS en JSON");
        long idUsuario = seguridadService.obtenerIdUsuario(request);
        //Si es usuario a nivel central se cargan todas las unidades asociados al SILAIS
        if(seguridadService.esUsuarioNivelCentral(idUsuario, ConstantsSecurity.SYSTEM_CODE)) {
            return unidadesService.getPrimaryUnitsBySilais(codSilais, HealthUnitType.UnidadesPrimHosp.getDiscriminator().split(","));
        }else{//sino sólo se cargarn las unidades autorizadas para el usuario según SILAIS
            return seguridadService.obtenerUnidadesPorUsuarioEntidad((int)idUsuario,codSilais, ConstantsSecurity.SYSTEM_CODE,HealthUnitType.UnidadesPrimHosp.getDiscriminator());
        }
    }

    @RequestMapping(value = "comunidad", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    List<Comunidades> getComunidad(@RequestParam(value = "municipioId", required = true) String municipioId) throws Exception {
        logger.info("Obteniendo las comunidaes por municipio en JSON");

        List<Comunidades> comunidades = comunidadesService.getComunidades(municipioId);
        return comunidades;
    }

    @RequestMapping(value = "comunidadesSector", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    List<Comunidades> getComunidadesBySector(@RequestParam(value = "codSector", required = true) String codSector) throws Exception {
        logger.info("Obteniendo las comunidaes por municipio en JSON");

        List<Comunidades> comunidades = comunidadesService.getComunidadesBySector(codSector);
        return comunidades;
    }


    @RequestMapping(value = "semanaEpidemiologica", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    CalendarioEpi getSemanaEpidemiologica(@RequestParam(value = "fechaValidar", required = true) String fechaValidar) throws Exception {
        logger.info("Obteniendo la semana epidemiológica de la fecha informada en JSON");
        CalendarioEpi semana;
        semana = calendarioEpiService.getCalendarioEpiByFecha(fechaValidar);
        return semana;
    }

    @RequestMapping(value = "sectoresMunicipio", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    List<Sectores> getSectoresByUnidad(@RequestParam(value = "codUnidad", required = true) long codUnidad) throws Exception {
        logger.info("Obteniendo los sectores por unidad de salud en JSON");
        List<Sectores> sectoresList = new ArrayList<Sectores>();
        sectoresList = sectoresService.getSectoresByUnidad(codUnidad);
        return sectoresList;
    }

    @RequestMapping(value = "getDiagnosticos", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    List<Dx_TipoMx_TipoNoti> getDiagnosticos(@RequestParam(value = "codMx", required = true) String codMx, @RequestParam(value = "tipoNoti", required = true) String tipoNoti,
                                             @RequestParam(value = "idTomaMx", required = true)String idTomaMx) throws Exception {
        logger.info("Obteniendo los dx por tipo mx en JSON");
        List<Dx_TipoMx_TipoNoti> dxTipoMxTipoNotis = new ArrayList<Dx_TipoMx_TipoNoti>();
        List<Dx_TipoMx_TipoNoti> dxTipoMxTipoNotisPermitidos = new ArrayList<Dx_TipoMx_TipoNoti>();
        dxTipoMxTipoNotis = tomaMxService.getDx(codMx,tipoNoti,seguridadService.obtenerNombreUsuario(), idTomaMx);
        TrasladoMx trasladoActivo = trasladosService.getTrasladoActivoMx(idTomaMx);

        if (trasladoActivo!=null && trasladoActivo.isTrasladoInterno()){
            for (Dx_TipoMx_TipoNoti dxTipoMxTipoNoti : dxTipoMxTipoNotis){
                if (dxTipoMxTipoNoti.getDiagnostico().getArea().getIdArea().equals(trasladoActivo.getAreaDestino().getIdArea()))
                    dxTipoMxTipoNotisPermitidos.add(dxTipoMxTipoNoti);
            }
            return dxTipoMxTipoNotisPermitidos;
        }else{
            return dxTipoMxTipoNotis;
        }
    }

    @RequestMapping(value = "getExamenes", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    List<CatalogoExamenes> getExamenes(@RequestParam(value = "idDx", required = true) int idDx) throws Exception {
        logger.info("Obteniendo los examenes por dx en JSON");
        List<CatalogoExamenes> catalogoExamenesList = new ArrayList<CatalogoExamenes>();
        catalogoExamenesList = examenesService.getExamenesByIdDx(idDx);
        return catalogoExamenesList;
    }

    @RequestMapping(value = "getDiagnosticosNoti", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    List<Catalogo_Dx> getDiagnosticosByNoti(@RequestParam(value = "codTipoNoti", required = true) String codTipoNoti) throws Exception {
        logger.info("Obteniendo los dx por tipo notificación en JSON");
        List<Catalogo_Dx> dxTipoMxTipoNotis = new ArrayList<Catalogo_Dx>();
        dxTipoMxTipoNotis = tomaMxService.getDxsByTipoNoti(codTipoNoti);
        return dxTipoMxTipoNotis;
    }

    @RequestMapping(value = "getEstudios", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    List<Estudio_TipoMx_TipoNoti> getEstudios(@RequestParam(value = "codMx", required = true) String codMx, @RequestParam(value = "tipoNoti", required = true) String tipoNoti, @RequestParam(value = "idTomaMx", required = true) String idTomaMx) throws Exception {
        logger.info("Obteniendo los estudios por mx y tipo de notitificación en JSON");
        List<Estudio_TipoMx_TipoNoti> estudiosByTipoMxTipoNoti = tomaMxService.getEstudiosByTipoMxTipoNoti(codMx,tipoNoti, idTomaMx);
        return estudiosByTipoMxTipoNoti;
    }

    @RequestMapping(value = "getExamenesEstudio", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    List<CatalogoExamenes> getExamenesEstudio(@RequestParam(value = "idEstudio", required = true) int idEstudio) throws Exception {
        logger.info("Obteniendo los examenes por estudio en JSON");
        List<CatalogoExamenes> catalogoExamenesList = new ArrayList<CatalogoExamenes>();
        catalogoExamenesList = examenesService.getExamenesByIdEstudio(idEstudio);
        return catalogoExamenesList;
    }

    @RequestMapping(value = "getEstudiosNoti", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    List<Catalogo_Estudio> getEstudiosByNoti(@RequestParam(value = "codTipoNoti", required = true) String codTipoNoti) throws Exception {
        logger.info("Obteniendo los estudios por tipo notificación en JSON");
        List<Catalogo_Estudio> estTipoMxTipoNotis = new ArrayList<Catalogo_Estudio>();
        estTipoMxTipoNotis = tomaMxService.getEstudiossByTipoNoti(codTipoNoti);
        return estTipoMxTipoNotis;
    }

    @RequestMapping(value = "getDiagnosticosEdicion", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    List<Dx_TipoMx_TipoNoti> getDiagnosticosEdicion(@RequestParam(value = "codMx", required = true) String codMx, @RequestParam(value = "tipoNoti", required = true) String tipoNoti,
                                             @RequestParam(value = "idTomaMx", required = true)String idTomaMx) throws Exception {
        logger.info("Obteniendo los dx por tipo mx en JSON");
        return tomaMxService.getDx(codMx,tipoNoti,seguridadService.obtenerNombreUsuario(), (idTomaMx.isEmpty()?null:idTomaMx));

    }

    @RequestMapping(value = "getCatDxCatEstPermitidos", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    String getCatDxCatEstPermitidos(@RequestParam(value = "codMx", required = true) String codMx, @RequestParam(value = "tipoNoti", required = true) String tipoNoti, @RequestParam(value = "idTomaMx", required = true) String idTomaMx) throws Exception {
        logger.info("Obteniendo los estudios por mx y tipo de notitificación en JSON");
        List<Estudio_TipoMx_TipoNoti> estudiosByTipoMxTipoNoti = this.getEstudios(codMx,tipoNoti, idTomaMx);
        List<Dx_TipoMx_TipoNoti> dxTipoMxTipoNotisPermitidos = this.getDiagnosticos(codMx,tipoNoti, idTomaMx);
        String jsonResponse="";
        Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
        Integer indice=0;
        for(Estudio_TipoMx_TipoNoti estActual : estudiosByTipoMxTipoNoti){
            Map<String, String> map = new HashMap<String, String>();
            map.put("id", estActual.getEstudio().getIdEstudio()+"-E");
            map.put("nombre", estActual.getEstudio().getNombre());
            mapResponse.put(indice, map);
            indice ++;
        }
        for (Dx_TipoMx_TipoNoti dxActual : dxTipoMxTipoNotisPermitidos) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("id", dxActual.getDiagnostico().getIdDiagnostico()+"-R");
            map.put("nombre", dxActual.getDiagnostico().getNombre());
            mapResponse.put(indice, map);
            indice ++;
        }
        jsonResponse = new Gson().toJson(mapResponse);
        //escapar caracteres especiales, escape de los caracteres con valor num�rico mayor a 127
        UnicodeEscaper escaper     = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }

    @RequestMapping(value = "getDxsVIHTBPersona/{idPersona}", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    List<ni.gob.minsa.laboratorio.utilities.reportes.DatosSolicitud> getDxsVIHTBPersona(@PathVariable(value = "idPersona") String idPersona) throws Exception {
        logger.info("Obteniendo los dx TB y VIH por persona en JSON");
        List<ni.gob.minsa.laboratorio.utilities.reportes.DatosSolicitud> datosSolicitudes = solicitudService.getSolicitudesVIHTB(true, true, idPersona);
        for(ni.gob.minsa.laboratorio.utilities.reportes.DatosSolicitud ds: datosSolicitudes){
            completarDatosSolicitud(ds);
        }
        return datosSolicitudes;
    }

    @RequestMapping(value = "getDxsPersonNoti/{idPersona}/{tipoNoti}", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    List<ni.gob.minsa.laboratorio.utilities.reportes.DatosSolicitud> getDxsVIHTBPersona(@PathVariable(value = "idPersona") String idPersona,
                                            @PathVariable(value = "tipoNoti") String tipoNoti) throws Exception {
        logger.info("Obteniendo los dx TB y VIH por persona en JSON");
        List<ni.gob.minsa.laboratorio.utilities.reportes.DatosSolicitud> datosSolicitudes = solicitudService.getSolicitudesByIdPersonTipoNoti(idPersona, tipoNoti);
        for(ni.gob.minsa.laboratorio.utilities.reportes.DatosSolicitud ds: datosSolicitudes){
            completarDatosSolicitud(ds);
        }
        return datosSolicitudes;
    }

    @RequestMapping(value = "getDxIdSolicitud/{idSolicitud}", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    ni.gob.minsa.laboratorio.utilities.reportes.DatosSolicitud getDxIdSolicitud(@PathVariable(value = "idSolicitud") String idSolicitud) throws Exception {
        logger.info("Obteniendo dx por idSolicitud en JSON");
        ni.gob.minsa.laboratorio.utilities.reportes.DatosSolicitud ds = solicitudService.getSolicitudesByIdSolicitud(idSolicitud);
        completarDatosSolicitud(ds);
        return ds;
    }

    private void completarDatosSolicitud(ni.gob.minsa.laboratorio.utilities.reportes.DatosSolicitud ds){
        List<ResultadoSolicitud> detRes = resultadoFinalService.getDetResActivosBySolicitudV2(ds.getIdSolicitud());
        ds.setResultado(new ArrayList<ValorResultado>());//por defecto vacio, solo se pondra resultado si esta aprobado
        if (ds.getAprobada()!= null){
            if (ds.getAprobada().equals(true)) {
                ds.setEstadoSolicitud(messageSource.getMessage("lbl.approval.result", null, null));
                for (ResultadoSolicitud res : detRes) {
                    ValorResultado resultado = new ValorResultado();
                    if (res.getRespuesta() != null) {
                        resultado.setVariable(res.getRespuesta().trim());
                        if (res.getTipo().equals("TPDATO|LIST")) {
                            Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(res.getValor());
                            resultado.setValor(cat_lista.getEtiqueta());
                        } else if (res.getTipo().equals("TPDATO|LOG")) {
                            String valorBoleano = (Boolean.valueOf(res.getValor()) ? "lbl.yes" : "lbl.no");
                            resultado.setValor(valorBoleano);
                        } else if (res.getValor().toLowerCase().contains("inadecuad")) {
                            resultado.setValor(res.getValor());
                        } else {
                            resultado.setValor(res.getValor());
                        }
                    } else if (res.getRespuestaExamen() != null) {
                        resultado.setVariable(res.getRespuestaExamen().trim());
                        if (res.getTipoExamen().equals("TPDATO|LIST")) {
                            Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(res.getValor());
                            resultado.setValor(cat_lista.getEtiqueta());
                        } else if (res.getTipoExamen().equals("TPDATO|LOG")) {
                            String valorBoleano = (Boolean.valueOf(res.getValor()) ? "lbl.yes" : "lbl.no");
                            resultado.setValor(valorBoleano);
                        } else {
                            resultado.setValor(res.getValor());
                        }
                    }
                    ds.getResultado().add(resultado);
                }
            } else {
                if (!detRes.isEmpty()) {
                    ds.setEstadoSolicitud(messageSource.getMessage("lbl.result.pending.approval", null, null));
                } else {
                    ds.setEstadoSolicitud(messageSource.getMessage("lbl.without.result", null, null));
                }
            }
        }else{
            if (!detRes.isEmpty()) {
                ds.setEstadoSolicitud(messageSource.getMessage("lbl.result.pending.approval", null, null));
            } else {
                ds.setEstadoSolicitud(messageSource.getMessage("lbl.without.result", null, null));
            }
        }
    }

    @RequestMapping(value = "getResultadosPDF/{codigomx}/{username}", method = RequestMethod.GET)
    public
    @ResponseBody
    String expToPDF(@PathVariable(value = "codigomx") String codigomx, @PathVariable(value = "username") String username) throws IOException, COSVisitorException, ParseException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PDDocument doc = new PDDocument();

        List<Laboratorio> laboratorios = laboratoriosService.getLaboratoriosRegionales();
        String response = null;
        String fechaImpresion = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").format(new Date());
        try {

            if (!codigomx.isEmpty()) {
                ResultadoVigilancia tomaMx = tomaMxService.getDatosMx(codigomx);
                //Prepare the document.
                if (tomaMx != null) {
                    for (Laboratorio labProcesa : laboratorios) {
                        if (tomaMxService.muestraTieneDxProcesadosEnLab(tomaMx.getIdTomaMx(), labProcesa.getCodigo())) {
                            List<Area> areaDxList = tomaMxService.getAreaSoliDxAprobByTomaAndLabProcesa(tomaMx.getIdTomaMx(), labProcesa.getCodigo());
                            float yPosicionExamen = 0;
                            for (Area area : areaDxList) {
                                if (seguridadService.usuarioAutorizadoArea(username, area.getIdArea())) {
                                    PDPage page = GeneralUtils.addNewPage(doc);
                                    PDPageContentStream stream = new PDPageContentStream(doc, page);

                                    String nombreDireccion = "";
                                    Direccion direccion = organizationChartService.getDireccionesByLab(labProcesa.getCodigo(), area.getIdArea());
                                    if (direccion != null) nombreDireccion = direccion.getNombre();
                                    //dibujar encabezado y pie de pagina
                                    GeneralUtils.drawHeaderAndFooter(stream, doc, 750, 590, 80, 600, 70);

                                    String pageNumber = String.valueOf(doc.getNumberOfPages());
                                    GeneralUtils.drawTEXT(pageNumber, 15, 550, stream, 10, PDType1Font.HELVETICA_BOLD);

                                    drawInfoLab(stream, page, labProcesa);

                                    float y = 648;
                                    //nombre del reporte
                                    float xCenter;
                                    xCenter = GeneralUtils.centerTextPositionX(page, PDType1Font.HELVETICA_BOLD_OBLIQUE, 12, nombreDireccion);
                                    GeneralUtils.drawTEXT(nombreDireccion, y, xCenter, stream, 12, PDType1Font.HELVETICA_BOLD_OBLIQUE);
                                    y = y - 15;
                                    xCenter = GeneralUtils.centerTextPositionX(page, PDType1Font.HELVETICA_BOLD_OBLIQUE, 11, messageSource.getMessage("lbl.lab.result", null, null).toUpperCase());
                                    GeneralUtils.drawTEXT(messageSource.getMessage("lbl.lab.result", null, null).toUpperCase(), y, xCenter, stream, 11, PDType1Font.HELVETICA_BOLD_OBLIQUE);
                                    y = y - 15;
                                    xCenter = GeneralUtils.centerTextPositionX(page, PDType1Font.HELVETICA_BOLD_OBLIQUE, 11, area.getNombre().toUpperCase());
                                    GeneralUtils.drawTEXT(area.getNombre().toUpperCase(), y, xCenter, stream, 11, PDType1Font.HELVETICA_BOLD_OBLIQUE);
                                    y = y - 20;

                                    String nombres = "";
                                    String apellidos = "";
                                    String edad = "";
                                    if (!tomaMx.getCodigoTipoNoti().equalsIgnoreCase("TPNOTI|VIH")) {
                                        if (tomaMx.getPrimerNombre() != null) {
                                            nombres = tomaMx.getPrimerNombre();
                                            if (tomaMx.getSegundoNombre() != null)
                                                nombres = nombres + " " + tomaMx.getSegundoNombre();

                                            apellidos = tomaMx.getPrimerApellido();
                                            if (tomaMx.getSegundoApellido() != null)
                                                apellidos = apellidos + " " + tomaMx.getSegundoApellido();
                                        } else if (tomaMx.getCodigoVIH() != null) {
                                            nombres = tomaMx.getCodigoVIH();
                                        } /*else {
                                            nombres = tomaMx.getIdNotificacion().getSolicitante().getNombre();
                                        }*/
                                    } else {
                                        nombres = tomaMx.getCodigoVIH();
                                    }

                                    String[] arrEdad = DateUtil.calcularEdad(tomaMx.getFechaNacimiento(), new Date()).split("/");
                                    if (arrEdad[0] != null) edad = arrEdad[0] + " A";
                                    if (arrEdad[1] != null) edad = edad + " " + arrEdad[1] + " M";

                                    //datos personales
                                    GeneralUtils.drawTEXT(messageSource.getMessage("lbl.code", null, null) + ": ", y, 60, stream, 11, PDType1Font.HELVETICA);
                                    GeneralUtils.drawTEXT(codigomx, y, 120, stream, 11, PDType1Font.HELVETICA_BOLD);
                                    GeneralUtils.drawTEXT(messageSource.getMessage("lbl.file.number", null, null) + ": ", y, 300, stream, 11, PDType1Font.HELVETICA);
                                    String numExpediente = (tomaMx.getExpediente() != null ? tomaMx.getExpediente() : notificacionService.getNumExpediente(tomaMx.getIdNotificacion()));
                                    GeneralUtils.drawTEXT(numExpediente, y, 420, stream, 11, PDType1Font.HELVETICA_BOLD);
                                    y = y - 15;
                                    GeneralUtils.drawTEXT(messageSource.getMessage("lbl.names", null, null) + ":", y, 60, stream, 11, PDType1Font.HELVETICA);
                                    GeneralUtils.drawTEXT(nombres, y, 120, stream, 11, PDType1Font.HELVETICA_BOLD);
                                    GeneralUtils.drawTEXT(messageSource.getMessage("lbl.lastnames", null, null) + ":", y, 300, stream, 11, PDType1Font.HELVETICA);
                                    GeneralUtils.drawTEXT(apellidos, y, 360, stream, 11, PDType1Font.HELVETICA_BOLD);
                                    y = y - 15;
                                    GeneralUtils.drawTEXT(messageSource.getMessage("lbl.age", null, null), y, 60, stream, 11, PDType1Font.HELVETICA);
                                    GeneralUtils.drawTEXT(edad, y, 100, stream, 11, PDType1Font.HELVETICA_BOLD);
                                    GeneralUtils.drawTEXT(messageSource.getMessage("lbl.silais1", null, null), y, 185, stream, 11, PDType1Font.HELVETICA);
                                    GeneralUtils.drawTEXT(tomaMx.getCodigoSilaisMx() != null ? tomaMx.getNombreSilaisMx() : "", y, 235, stream, 10, PDType1Font.HELVETICA_BOLD);
                                    GeneralUtils.drawTEXT(messageSource.getMessage("lbl.muni", null, null) + ":", y, 370, stream, 11, PDType1Font.HELVETICA);
                                    GeneralUtils.drawTEXT(tomaMx.getCodigoMuniMx() != null ? tomaMx.getNombreMuniMx() : "", y, 430, stream, 10, PDType1Font.HELVETICA_BOLD);
                                    y = y - 15;
                                    GeneralUtils.drawTEXT(messageSource.getMessage("lbl.health.unit1", null, null), y, 60, stream, 11, PDType1Font.HELVETICA);
                                    GeneralUtils.drawTEXT(tomaMx.getCodigoUnidadMx() != null ? tomaMx.getNombreUnidadMx() : "", y, 150, stream, 9, PDType1Font.HELVETICA_BOLD);
                                    GeneralUtils.drawTEXT(messageSource.getMessage("lbl.sampling.datetime1", null, null), y, 400, stream, 11, PDType1Font.HELVETICA);
                                    GeneralUtils.drawTEXT(DateUtil.DateToString(tomaMx.getFechaTomaMx(), "dd/MM/yyyy"), y, 490, stream, 11, PDType1Font.HELVETICA_BOLD);
                                    y = y - 15;
                                    GeneralUtils.drawTEXT(messageSource.getMessage("lbl.sample.type", null, null) + ":", y, 60, stream, 11, PDType1Font.HELVETICA);
                                    GeneralUtils.drawTEXT(tomaMx.getNombreTipoMx(), y, 150, stream, 11, PDType1Font.HELVETICA_BOLD);

                                    //resultados
                                    List<ni.gob.minsa.laboratorio.utilities.reportes.DatosSolicitud> listDx = tomaMxService.getSolicitudesAprobByToma_Lab_Area(tomaMx.getIdTomaMx(), labProcesa.getCodigo(), area.getIdArea());
                                    y = y - 4;
                                    DatosRecepcionMx recepcionMx = recepcionMxService.getRecepcionMxByCodUnicoMxV2(tomaMx.getCodUnicoMx(), labProcesa.getCodigo());
                                    String procesadoPor = "";
                                    String aprobadoPor = "";
                                    if (recepcionMx.getCalidadMx() != null && recepcionMx.getCalidadMx().equalsIgnoreCase("CALIDMX|IDC")) {
                                        y = y - 20;
                                        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.sample.inadequate2", null, null), y, 100, stream, 10, PDType1Font.HELVETICA);
                                    } else {
                                        for (ni.gob.minsa.laboratorio.utilities.reportes.DatosSolicitud dx : listDx) {
                                            aprobadoPor = dx.getUsuarioAprobacion();
                                            y = y - 20;
                                            List<DatosOrdenExamen> examenes = ordenExamenMxService.getOrdenesExamenByIdSolicitudV2(dx.getIdSolicitud());
                                            for (DatosOrdenExamen examen : examenes) {
                                                //salto de p�gina
                                                if ((y - 15) < 180) {
                                                    stream.close();
                                                    page = GeneralUtils.addNewPage(doc);
                                                    stream = new PDPageContentStream(doc, page);
                                                    //dibujar encabezado y pie de pagina
                                                    GeneralUtils.drawHeaderAndFooter(stream, doc, 750, 590, 80, 600, 70);
                                                    pageNumber = String.valueOf(doc.getNumberOfPages());
                                                    GeneralUtils.drawTEXT(pageNumber, 15, 550, stream, 10, PDType1Font.HELVETICA_BOLD);
                                                    drawInfoLab(stream, page, labProcesa);
                                                    y = 640;
                                                    //nombre del reporte
                                                    xCenter = GeneralUtils.centerTextPositionX(page, PDType1Font.HELVETICA_BOLD_OBLIQUE, 12, nombreDireccion);
                                                    GeneralUtils.drawTEXT(nombreDireccion, y, xCenter, stream, 12, PDType1Font.HELVETICA_BOLD_OBLIQUE);
                                                    y = y - 15;
                                                    xCenter = GeneralUtils.centerTextPositionX(page, PDType1Font.HELVETICA_BOLD_OBLIQUE, 11, messageSource.getMessage("lbl.lab.result", null, null).toUpperCase());
                                                    GeneralUtils.drawTEXT(messageSource.getMessage("lbl.lab.result", null, null).toUpperCase(), y, xCenter, stream, 11, PDType1Font.HELVETICA_BOLD_OBLIQUE);
                                                    y = y - 15;
                                                    xCenter = GeneralUtils.centerTextPositionX(page, PDType1Font.HELVETICA_BOLD_OBLIQUE, 11, area.getNombre().toUpperCase());
                                                    GeneralUtils.drawTEXT(area.getNombre().toUpperCase(), y, xCenter, stream, 11, PDType1Font.HELVETICA_BOLD_OBLIQUE);
                                                    y = y - 30;

                                                }
                                                List<DetalleResultado> resultados = resultadosService.getDetallesResultadoActivosByExamen(examen.getIdOrdenExamen());
                                                if (resultados.size() > 0) {
                                                    yPosicionExamen = y;
                                                    //GeneralUtils.drawTEXT(examen.getCodExamen().getNombre(), y, 100, stream, 10, PDType1Font.HELVETICA);
                                                    y = y - 15;
                                                }

                                                String fechaProcesamiento = "";
                                                for (DetalleResultado resultado : resultados) {
                                                    String detalleResultado = "";
                                                    if (resultado.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LIST")) {
                                                        Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(resultado.getValor());
                                                        detalleResultado = cat_lista.getValor();
                                                    } else if (resultado.getRespuesta().getConcepto().getTipo().getCodigo().equals("TPDATO|LOG")) {
                                                        detalleResultado = (Boolean.valueOf(resultado.getValor()) ? "lbl.yes" : "lbl.no");
                                                    } else {
                                                        detalleResultado = resultado.getValor();
                                                    }
                                                    procesadoPor = resultado.getUsuarioRegistro().getCompleteName();
                                                    fechaProcesamiento = DateUtil.DateToString(resultado.getFechahProcesa(), "dd/MM/yyyy");
                                                    GeneralUtils.drawTEXT(resultado.getRespuesta().getNombre() + ": " + detalleResultado, y, 150, stream, 12, PDType1Font.HELVETICA_BOLD);
                                                    y = y - 15;
                                                }
                                                if (resultados.size() > 0) {
                                                    GeneralUtils.drawTEXT(examen.getExamen() + " - " + messageSource.getMessage("lbl.processing.date", null, null) + ": " + fechaProcesamiento, yPosicionExamen, 100, stream, 10, PDType1Font.HELVETICA);
                                                    //y = y - 15;
                                                }
                                            }
                                        }
                                    }
                                    //fecha impresi?n
                                    GeneralUtils.drawTEXT(messageSource.getMessage("lbl.date.delivery.results", null, null) + ": ", 160, 60, stream, 11, PDType1Font.HELVETICA);
                                    GeneralUtils.drawTEXT(fechaImpresion, 160, 190, stream, 10, PDType1Font.HELVETICA);

                                    GeneralUtils.drawTEXT(messageSource.getMessage("lbl.bioanalyst", null, null) + ": ", 130, 60, stream, 11, PDType1Font.HELVETICA);
                                    GeneralUtils.drawTEXT(procesadoPor, 130, 122, stream, 10, PDType1Font.HELVETICA);

                                    GeneralUtils.drawTEXT(messageSource.getMessage("lbl.validated.by", null, null) + ": ", 130, 300, stream, 11, PDType1Font.HELVETICA);
                                    GeneralUtils.drawTEXT(aprobadoPor, 130, 370, stream, 10, PDType1Font.HELVETICA);

                                    GeneralUtils.drawTEXT(messageSource.getMessage("lbl.footer.note.results", null, null), 100, 60, stream, 11, PDType1Font.HELVETICA_BOLD);

                                    stream.close();
                                }
                            }
                        }
                    }
                }
                doc.save(output);
                doc.close();
                // generate the file
                response = Base64.encodeBase64String(output.toByteArray());

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return response;
    }

    private void drawInfoLab(PDPageContentStream stream, PDPage page, Laboratorio labProcesa) throws IOException {
        float xCenter;

        float inY = 720;
        float m = 18;

        xCenter = GeneralUtils.centerTextPositionX(page, PDType1Font.HELVETICA_BOLD, 14, messageSource.getMessage("lbl.minsa", null, null));
        GeneralUtils.drawTEXT(messageSource.getMessage("lbl.minsa", null, null), inY, xCenter, stream, 14, PDType1Font.HELVETICA_BOLD);
        inY -= m;

        if(labProcesa != null){

            if(labProcesa.getDescripcion()!= null){
                xCenter = GeneralUtils.centerTextPositionX(page, PDType1Font.HELVETICA_BOLD, 14, labProcesa.getNombre());
                GeneralUtils.drawTEXT(labProcesa.getNombre(), inY, xCenter, stream, 14, PDType1Font.HELVETICA_BOLD);
                inY -= m;
            }

            if(labProcesa.getDireccion() != null){
                xCenter = GeneralUtils.centerTextPositionX(page, PDType1Font.HELVETICA_BOLD, 11, labProcesa.getDireccion());
                GeneralUtils.drawTEXT(labProcesa.getDireccion(), inY, xCenter, stream, 11, PDType1Font.HELVETICA_BOLD);
                inY -= m;
            }

            if(labProcesa.getTelefono() != null){

                if(labProcesa.getTelefax() != null){
                    xCenter = GeneralUtils.centerTextPositionX(page, PDType1Font.HELVETICA_BOLD, 11, messageSource.getMessage("lbl.telephone", null, null)+": "+labProcesa.getTelefono() + " ," + messageSource.getMessage("person.fax", null, null)+": "+ labProcesa.getTelefax());
                    GeneralUtils.drawTEXT(messageSource.getMessage("lbl.telephone", null, null)+": "+labProcesa.getTelefono() + ", " + messageSource.getMessage("person.fax", null, null)+": "+ labProcesa.getTelefax(), inY, xCenter, stream, 11, PDType1Font.HELVETICA_BOLD);
                }else{
                    xCenter = GeneralUtils.centerTextPositionX(page, PDType1Font.HELVETICA_BOLD, 11, messageSource.getMessage("lbl.telephone", null, null)+": "+labProcesa.getTelefono());
                    GeneralUtils.drawTEXT(messageSource.getMessage("lbl.telephone", null, null)+": "+labProcesa.getTelefono(), inY, xCenter, stream, 11, PDType1Font.HELVETICA_BOLD);
                }
            }
        }
    }
}