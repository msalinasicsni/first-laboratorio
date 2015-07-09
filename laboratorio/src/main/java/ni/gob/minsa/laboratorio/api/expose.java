package ni.gob.minsa.laboratorio.api;

import ni.gob.minsa.laboratorio.domain.estructura.CalendarioEpi;
import ni.gob.minsa.laboratorio.domain.estructura.Unidades;
import ni.gob.minsa.laboratorio.domain.examen.CatalogoExamenes;
import ni.gob.minsa.laboratorio.domain.muestra.Catalogo_Dx;
import ni.gob.minsa.laboratorio.domain.muestra.Catalogo_Estudio;
import ni.gob.minsa.laboratorio.domain.muestra.Dx_TipoMx_TipoNoti;
import ni.gob.minsa.laboratorio.domain.muestra.Estudio_TipoMx_TipoNoti;
import ni.gob.minsa.laboratorio.domain.muestra.traslado.TrasladoMx;
import ni.gob.minsa.laboratorio.domain.poblacion.Comunidades;
import ni.gob.minsa.laboratorio.domain.poblacion.Divisionpolitica;
import ni.gob.minsa.laboratorio.domain.poblacion.Sectores;
import ni.gob.minsa.laboratorio.service.*;
import ni.gob.minsa.laboratorio.utilities.ConstantsSecurity;
import ni.gob.minsa.laboratorio.utilities.enumeration.HealthUnitType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

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
        dxTipoMxTipoNotis = tomaMxService.getDx(codMx,tipoNoti,seguridadService.obtenerNombreUsuario());
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
        List<Estudio_TipoMx_TipoNoti> dxTipoMxTipoNotis = new ArrayList<Estudio_TipoMx_TipoNoti>();
        dxTipoMxTipoNotis = tomaMxService.getEstudiosByTipoMxTipoNoti(codMx,tipoNoti,idTomaMx);
        return dxTipoMxTipoNotis;
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

}