package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.examen.*;
import ni.gob.minsa.laboratorio.domain.muestra.Laboratorio;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.User;
import ni.gob.minsa.laboratorio.service.AreaService;
import ni.gob.minsa.laboratorio.service.LaboratoriosService;
import ni.gob.minsa.laboratorio.service.OrganizationChartService;
import ni.gob.minsa.laboratorio.service.SeguridadService;
import ni.gob.minsa.laboratorio.utilities.ConstantsSecurity;
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
@RequestMapping("administracion/organizationChart")
public class OrganizationChartController {


    private static final Logger logger = LoggerFactory.getLogger(OrganizationChartController.class);

    @Autowired
    @Qualifier(value = "seguridadService")
    private SeguridadService seguridadService;

    @Autowired
    @Qualifier(value = "laboratoriosService")
    private LaboratoriosService laboratoriosService;

    @Autowired
    @Qualifier(value = "organizationChartService")
    private OrganizationChartService organizationChartService;

    @Autowired
    @Qualifier(value = "areaService")
    private AreaService areaService;

    @Autowired
    MessageSource messageSource;

    @RequestMapping(value = "init", method = RequestMethod.GET)
    public ModelAndView init(HttpServletRequest request) throws Exception {
        logger.debug("Cargando laboratorios");
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
            List<Direccion> managmentList =  laboratoriosService.getDireccionesActivas();
            List<Departamento> departmentList = laboratoriosService.getDepartamentosActivos();
            List<Area> areasList = laboratoriosService.getAreasActivas();

            mav.addObject("managmentList",managmentList);
            mav.addObject("departmentList",departmentList);
            mav.addObject("areasList",areasList);
            mav.setViewName("administracion/organizationChart");
        }else
            mav.setViewName(urlValidacion);

        return mav;
    }


    //Load laboratories list
    @RequestMapping(value = "getLabs", method = RequestMethod.GET,  produces = "application/json")
    public @ResponseBody
    List<Laboratorio> getLabs() throws Exception {
        logger.info("Obteniendo los laboratorios");
        List<Laboratorio> labList = null;
        labList = laboratoriosService.getAllLaboratories();
        return labList;
    }

    //Load Management Associated to Lab
    @RequestMapping(value = "getManagementAssociated", method = RequestMethod.GET)
    public @ResponseBody List<DireccionLaboratorio> getManagementAssociated(@RequestParam(value = "lab", required = true) String lab) {
        logger.info("Obteniendo las direcciones asociados a un laboratorio");
        List<DireccionLaboratorio> managementList = null;
        managementList = organizationChartService.getAssociatedManagement(lab);
        return managementList;
    }

    //Add or update association managment to lab
    @RequestMapping(value = "addUpdateManagment", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void addUpdateManagment(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String json = "";
        String resultado = "";
        String idLab = null;
        Integer management = 0;
        Integer idRecord = 0;

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);

            if(jsonpObject.get("idLab") != null && !jsonpObject.get("idLab").getAsString().isEmpty() ) {
                idLab = jsonpObject.get("idLab").getAsString();
            }

            if (jsonpObject.get("management") != null && !jsonpObject.get("management").getAsString().isEmpty()) {
                management = jsonpObject.get("management").getAsInt();
            }


            if (jsonpObject.get("idRecord") != null && !jsonpObject.get("idRecord").getAsString().isEmpty()) {
                idRecord = jsonpObject.get("idRecord").getAsInt();
            }

            User usuario = seguridadService.getUsuario(seguridadService.obtenerNombreUsuario());

            if (idRecord == 0) {
                if (management != 0 && idLab != null) {

                        //search record
                        DireccionLaboratorio record = organizationChartService.getManagmentLabRecord(idLab, management);

                        if (record == null) {
                            DireccionLaboratorio managLab = new DireccionLaboratorio();
                            managLab.setFechaRegistro(new Timestamp(new Date().getTime()));
                            managLab.setUsuarioRegistro(usuario);
                            managLab.setDireccion(laboratoriosService.getDireccionById(management));
                            managLab.setLaboratorio(laboratoriosService.getLaboratorioByCodigo(idLab));
                            managLab.setPasivo(false);
                            organizationChartService.addOrUpdateManagmentLab(managLab);
                        } else {
                            resultado = messageSource.getMessage("msg.existing.record.error", null, null);
                            throw new Exception(resultado);
                        }

                }
            } else {
                DireccionLaboratorio rec = organizationChartService.getManagmentLabById(idRecord);
                if (rec != null) {
                    rec.setPasivo(true);
                    organizationChartService.addOrUpdateManagmentLab(rec);
                }
            }




        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            ex.printStackTrace();
            resultado = messageSource.getMessage("msg.add.managLab.error", null, null);
            resultado = resultado + ". \n " + ex.getMessage();

        } finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("idLab", String.valueOf(idLab));
            map.put("mensaje", resultado);
            map.put("idRecord", "");
            map.put("management", "");
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    //Load Department Associated to ManagementLab
    @RequestMapping(value = "getDepartmentAssociated", method = RequestMethod.GET)
    public @ResponseBody List<DepartamentoDireccion> getDepartmentAssociated(@RequestParam(value = "idManagementLab", required = true) Integer idManagementLab) {
        logger.info("Obteniendo los departamentos asociados a una asociacion de direccion lab");
        List<DepartamentoDireccion> depaList = null;
        depaList = organizationChartService.getAssociatedDepartment(idManagementLab);
        return depaList;
    }

    //Add or update association managment to lab
    @RequestMapping(value = "addUpdateDepartment", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void addUpdateDepartment(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String json = "";
        String resultado = "";
        Integer idManagLab = 0;
        Integer department = 0;
        Integer idRecord = 0;


        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);

            if(jsonpObject.get("idManagLab") != null && !jsonpObject.get("idManagLab").getAsString().isEmpty() ) {
                idManagLab = jsonpObject.get("idManagLab").getAsInt();
            }

            if (jsonpObject.get("department") != null && !jsonpObject.get("department").getAsString().isEmpty()) {
                department = jsonpObject.get("department").getAsInt();
            }


            if (jsonpObject.get("idRecord") != null && !jsonpObject.get("idRecord").getAsString().isEmpty()) {
                idRecord = jsonpObject.get("idRecord").getAsInt();
            }




            User usuario = seguridadService.getUsuario(seguridadService.obtenerNombreUsuario());

            if (idRecord == 0) {
                if (department != 0 && idManagLab!= 0) {

                    //search record
                    DepartamentoDireccion record = organizationChartService.getDepManagementRecord(idManagLab, department);

                    if (record == null) {
                        DepartamentoDireccion dep = new DepartamentoDireccion();
                        dep.setFechaRegistro(new Timestamp(new Date().getTime()));
                        dep.setUsuarioRegistro(usuario);
                        dep.setDepartamento(laboratoriosService.getDepartamentoById(department));
                        dep.setDireccionLab(organizationChartService.getManagmentLabById(idManagLab));
                        dep.setPasivo(false);
                        organizationChartService.addOrUpdateDepManagement(dep);
                    } else {
                        resultado = messageSource.getMessage("msg.existing.record.error", null, null);
                        throw new Exception(resultado);
                    }

                }
            } else {
                DepartamentoDireccion rec = organizationChartService.getDepManagementById(idRecord);
                if (rec != null) {
                    rec.setPasivo(true);
                    organizationChartService.addOrUpdateDepManagement(rec);
                }
            }


        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            ex.printStackTrace();
            resultado = messageSource.getMessage("msg.add.depManag.error", null, null);
            resultado = resultado + ". \n " + ex.getMessage();

        } finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("idManagLab", idManagLab.toString());
            map.put("mensaje", resultado);
            map.put("idRecord", "");
            map.put("department", "");
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    //Load Areas Associated to Department-Management
    @RequestMapping(value = "getAreaDep", method = RequestMethod.GET)
    public @ResponseBody List<AreaDepartamento> getAreaDepAssociated(@RequestParam(value = "id", required = true) Integer id) {
        logger.info("Obteniendo las areas asociados a un departamento-direccion");
        List<AreaDepartamento> areasList = null;
        areasList = organizationChartService.getAssociatedAreas(id);
        return areasList;
    }

    //Add or update association area to department management
    @RequestMapping(value = "addUpdateArea", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void addUpdateArea(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String json = "";
        String resultado = "";
        Integer idDepManag = 0;
        Integer area = 0;
        Integer idRecord = 0;


        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);

            if(jsonpObject.get("idDepManag") != null && !jsonpObject.get("idDepManag").getAsString().isEmpty() ) {
                idDepManag = jsonpObject.get("idDepManag").getAsInt();
            }

            if (jsonpObject.get("area") != null && !jsonpObject.get("area").getAsString().isEmpty()) {
                area = jsonpObject.get("area").getAsInt();
            }

            if (jsonpObject.get("idRecord") != null && !jsonpObject.get("idRecord").getAsString().isEmpty()) {
                idRecord = jsonpObject.get("idRecord").getAsInt();
            }

            User usuario = seguridadService.getUsuario(seguridadService.obtenerNombreUsuario());

            if (idRecord == 0) {
                if (area != 0 && idDepManag!= 0) {

                    //search record
                    AreaDepartamento record = organizationChartService.getAreaDepRecord(idDepManag, area);

                    if (record == null) {
                        AreaDepartamento areaD = new AreaDepartamento();
                        areaD.setFechaRegistro(new Timestamp(new Date().getTime()));
                        areaD.setUsuarioRegistro(usuario);
                        areaD.setDepDireccion(organizationChartService.getDepManagementById(idDepManag));
                        areaD.setArea(areaService.getArea(area));
                        areaD.setPasivo(false);
                        organizationChartService.addOrUpdateArea(areaD);
                    } else {
                        resultado = messageSource.getMessage("msg.existing.record.error", null, null);
                        throw new Exception(resultado);
                    }
                }
            } else {
                AreaDepartamento rec = organizationChartService.getAreaDepById(idRecord);
                if (rec != null) {
                    rec.setPasivo(true);
                    organizationChartService.addOrUpdateArea(rec);
                }
            }


        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            ex.printStackTrace();
            resultado = messageSource.getMessage("msg.add.area.error", null, null);
            resultado = resultado + ". \n " + ex.getMessage();

        } finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("idDepManag", idDepManag.toString());
            map.put("mensaje", resultado);
            map.put("idRecord", "");
            map.put("area", "");
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }
}
