package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.examen.Area;
import ni.gob.minsa.laboratorio.domain.examen.CatalogoExamenes;
import ni.gob.minsa.laboratorio.domain.examen.Departamento;
import ni.gob.minsa.laboratorio.domain.examen.Direccion;
import ni.gob.minsa.laboratorio.domain.muestra.Laboratorio;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.*;

import ni.gob.minsa.laboratorio.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador web de peticiones relacionadas a usuarios
 *
 * @author William Avil�s
 */
@Controller
@RequestMapping("usuarios")
public class UsuariosController {
    @Resource(name = "usuarioService")
    private UsuarioService usuarioService;
    private static final Logger logger = LoggerFactory.getLogger(UsuariosController.class);

    @Autowired
    @Qualifier(value = "laboratoriosService")
    private LaboratoriosService laboratoriosService;

    @Autowired
    @Qualifier(value = "seguridadService")
    private SeguridadService seguridadService;

    @Autowired
    @Qualifier(value = "areaService")
    private AreaService areaService;

    @Autowired
    @Qualifier(value = "autoridadesService")
    private AutoridadesService autoridadesService;

    @Autowired
    @Qualifier(value = "examenesService")
    private ExamenesService examenesService;

    @Autowired
    MessageSource messageSource;

    @RequestMapping(value = "list", method = RequestMethod.GET)
    public String obtenerUsuarios(Model model, HttpServletRequest request) throws ParseException {
        logger.debug("Mostrando Usuarios en JSP");
        request.getSession().setAttribute("origen","lista");
        List<User> usuarios = usuarioService.getUsers();
        List<AutoridadLaboratorio> autoridadLaboratorios = autoridadesService.getAutoridadesLab();
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("authorities", this.usuarioService.getAuthorities());
        model.addAttribute("autoridadLaboratorios",autoridadLaboratorios);
        return "usuarios/seeUsers";
    }

    /**
     * Custom handler for displaying an user.
     *
     * @param username the ID of the user to display
     * @return a ModelMap with the model attributes for the view
     */
    @RequestMapping("/admin/{username}")
    public ModelAndView showUser(@PathVariable("username") String username, HttpServletRequest request) {
        request.getSession().setAttribute("origen","usuario");
        List<AutoridadLaboratorio> autoridadLaboratorios = autoridadesService.getAutoridadesLab();
        ModelAndView mav = new ModelAndView("usuarios/usuario");
        mav.addObject("user",this.usuarioService.getUser(username));
        mav.addObject("authorities",this.usuarioService.getAuthorities(username));
        mav.addObject("autoridadLaboratorios",autoridadLaboratorios);

        return mav;
    }

    /**
     * Custom handler for enabling an user.     *
     *
     * @return a String
     */
    @RequestMapping(value = "enable", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void enableUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        String userName="";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            userName = jsonpObject.get("userName").getAsString();
            User user = this.usuarioService.getUser(userName);
            user.setCreated(new Date());
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            user.setUsuario(authentication.getName());
            user.setEnabled(true);
            this.usuarioService.updateUser(user);
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.user.enable.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("userName",userName);
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    /**
     * Custom handler for disabling an user.     *
     *
     * @return a String
     */
    @RequestMapping(value = "disable", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void disableUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        String userName="";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            userName = jsonpObject.get("userName").getAsString();
            User user = this.usuarioService.getUser(userName);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            user.setUsuario(authentication.getName());
            user.setCreated(new Date());
            user.setEnabled(false);
            this.usuarioService.updateUser(user);
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.user.disable.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("userName",userName);
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = "adminUser", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void adminUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        String userName="";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            userName = jsonpObject.get("userName").getAsString();
            User user = this.usuarioService.getUser(userName);
            user.setCreated(new Date());
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            user.setUsuario(authentication.getName());
            AuthorityId authId = new AuthorityId();
            authId.setUsername(user.getUsername());
            authId.setAuthority("ROLE_ADMIN");
            Authority auth = new Authority();
            auth.setAuthId(authId);
            auth.setUser(user);
            this.usuarioService.addAuthority(auth);

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.user.set.rol.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("userName",userName);
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = "noAdminUser", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void noAdminUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        String userName="";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            userName = jsonpObject.get("userName").getAsString();
            this.usuarioService.deleteRole(userName,"ROLE_ADMIN");

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.user.remove.rol.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("userName",userName);
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = "receptionistUser", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void receptionistUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        String userName="";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            userName = jsonpObject.get("userName").getAsString();
            User user = this.usuarioService.getUser(userName);
            user.setCreated(new Date());
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            user.setUsuario(authentication.getName());
            AuthorityId authId = new AuthorityId();
            authId.setUsername(user.getUsername());
            authId.setAuthority("ROLE_RECEPCION");
            Authority auth = new Authority();
            auth.setAuthId(authId);
            auth.setUser(user);
            this.usuarioService.addAuthority(auth);

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.user.set.rol.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("userName",userName);
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = "noReceptionistUser", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void noReceptionistUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        String userName="";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            userName = jsonpObject.get("userName").getAsString();
            this.usuarioService.deleteRole(userName,"ROLE_RECEPCION");

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.user.remove.rol.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("userName",userName);
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = "analystUser", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void analystUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        String userName="";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            userName = jsonpObject.get("userName").getAsString();
            User user = this.usuarioService.getUser(userName);
            user.setCreated(new Date());
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            user.setUsuario(authentication.getName());
            AuthorityId authId = new AuthorityId();
            authId.setUsername(user.getUsername());
            authId.setAuthority("ROLE_ANALISTA");
            Authority auth = new Authority();
            auth.setAuthId(authId);
            auth.setUser(user);
            this.usuarioService.addAuthority(auth);

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.user.set.rol.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("userName",userName);
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = "noAnalystUser", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void noAnalystUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        String userName="";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            userName = jsonpObject.get("userName").getAsString();
            this.usuarioService.deleteRole(userName,"ROLE_ANALISTA");

            this.autoridadesService.bajaAutoridadAreas(userName);

            this.autoridadesService.bajaAutoridadExamenesByUserName(userName);

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.user.remove.rol.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("userName",userName);
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = "directorUser", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void directorUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        String userName="";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            userName = jsonpObject.get("userName").getAsString();
            User user = this.usuarioService.getUser(userName);
            user.setCreated(new Date());
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            user.setUsuario(authentication.getName());
            AuthorityId authId = new AuthorityId();
            authId.setUsername(user.getUsername());
            authId.setAuthority("ROLE_DIR");
            Authority auth = new Authority();
            auth.setAuthId(authId);
            auth.setUser(user);
            this.usuarioService.addAuthority(auth);

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.user.set.rol.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("userName",userName);
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = "noDirectorUser", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void noDirectorUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        String userName="";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            userName = jsonpObject.get("userName").getAsString();
            this.usuarioService.deleteRole(userName,"ROLE_DIR");

            this.autoridadesService.bajaAutoridadDireccion(userName);

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.user.remove.rol.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("userName",userName);
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = "departmentHeadUser", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void departmentHeadUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        String userName="";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            userName = jsonpObject.get("userName").getAsString();
            User user = this.usuarioService.getUser(userName);
            user.setCreated(new Date());
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            user.setUsuario(authentication.getName());
            AuthorityId authId = new AuthorityId();
            authId.setUsername(user.getUsername());
            authId.setAuthority("ROLE_JEFE");
            Authority auth = new Authority();
            auth.setAuthId(authId);
            auth.setUser(user);
            this.usuarioService.addAuthority(auth);

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.user.set.rol.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("userName",userName);
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = "noDepartmentHeadUser", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void noDepartmentHeadUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        String userName="";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            userName = jsonpObject.get("userName").getAsString();
            this.usuarioService.deleteRole(userName,"ROLE_JEFE");

            this.autoridadesService.bajaAutoridadDepartamento(userName);

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.user.remove.rol.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("userName",userName);
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = "/admin/{username}/edit", method = RequestMethod.GET)
    public String initUpdateUserForm(@PathVariable("username") String username, Model model, HttpServletRequest request) {
        User user = this.usuarioService.getUser(username);
        List<Laboratorio> laboratorioList = laboratoriosService.getLaboratoriosRegionales();
        Laboratorio laboratorio = seguridadService.getLaboratorioUsuario(username);
        model.addAttribute("laboratorios",laboratorioList);
        model.addAttribute("user",user);
        model.addAttribute("labUser",laboratorio);
        model.addAttribute("origen",request.getSession().getAttribute("origen").toString());
        return "usuarios/UpdateUserForm";
    }

    @RequestMapping(value = "actualizarUsuario", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void actualizarUsuario(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        boolean habilitado=false;
        String userName="";
        String nombreCompleto="";
        String email=null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            userName = jsonpObject.get("userName").getAsString();
            nombreCompleto = jsonpObject.get("nombreCompleto").getAsString();
            if (jsonpObject.get("email")!=null && !jsonpObject.get("email").getAsString().isEmpty())
                email = jsonpObject.get("email").getAsString();
            habilitado = jsonpObject.get("habilitado").getAsBoolean();

            User user = usuarioService.getUser(userName);
            user.setCompleteName(nombreCompleto);
            user.setEmail(email);
            user.setEnabled(habilitado);

            this.usuarioService.updateUser(user);

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.user.updated.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("userName",userName);
            map.put("nombreCompleto", nombreCompleto);
            map.put("email", email);
            map.put("habilitado",String.valueOf(habilitado));
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = "/admin/{username}/chgpass", method = RequestMethod.GET)
    public String initChgPassUserForm(@PathVariable("username") String username, Model model, HttpServletRequest request) {
        User user = this.usuarioService.getUser(username);
        model.addAttribute("user",user);
        model.addAttribute("origen",request.getSession().getAttribute("origen").toString());
        return "usuarios/ChgPassForm";
    }

    @RequestMapping(value = "changePassword", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void modificarPass(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        String userName="";
        String password="";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            userName = jsonpObject.get("userName").getAsString();
            password = jsonpObject.get("password").getAsString();

            User user = usuarioService.getUser(userName);
            StandardPasswordEncoder encoder = new StandardPasswordEncoder();
            String encodedPass = encoder.encode(password);
            user.setPassword(encodedPass);

            this.usuarioService.updateUser(user);

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.user.changePass.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("userName",userName);
            map.put("password", password);
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = "/mod/password", method = RequestMethod.GET)
    public String initChgPassUser2Form(Model model) {
        /*Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = this.usuarioService.getUser(authentication.getName());
        model.addAttribute(user);*/
        return "usuarios/ChgPassUser";
    }

    @RequestMapping(value = "/admin/new", method = RequestMethod.GET)
    public String initCreationForm(Model model) {
        List<Laboratorio> laboratorioList = laboratoriosService.getLaboratoriosRegionales();
        model.addAttribute("laboratorios",laboratorioList);
        return "usuarios/CreateUserForm";
    }

    @RequestMapping(value = "agregarUsuario", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void agregarUsuario(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        boolean habilitado=false;
        String userName="";
        String nombreCompleto="";
        String email=null;
        String password="";
        String labAsignado = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            userName = jsonpObject.get("userName").getAsString();
            nombreCompleto = jsonpObject.get("nombreCompleto").getAsString();
            if (jsonpObject.get("email")!=null && !jsonpObject.get("email").getAsString().isEmpty())
                email = jsonpObject.get("email").getAsString();
            habilitado = jsonpObject.get("habilitado").getAsBoolean();
            password = jsonpObject.get("password").getAsString();
            labAsignado = jsonpObject.get("labAsignado").getAsString();

            User userExist = usuarioService.getUser(userName);
            if (userExist!=null){
                throw new Exception(messageSource.getMessage("msg.user.add.error2", null, null));
            }else {
                StandardPasswordEncoder encoder = new StandardPasswordEncoder();
                String encodedPass = encoder.encode(password);

                Laboratorio laboratorio = laboratoriosService.getLaboratorioByCodigo(labAsignado);

                User user = new User();
                user.setUsername(userName);
                user.setUsuario(seguridadService.obtenerNombreUsuario());
                user.setCreated(new Date());
                user.setCompleteName(nombreCompleto);
                user.setEmail(email);
                user.setEnabled(habilitado);
                user.setPassword(encodedPass);

                this.usuarioService.addUser(user);

                AutoridadLaboratorio autoridadLaboratorio = new AutoridadLaboratorio();
                autoridadLaboratorio.setFechaRegistro(new Date());
                autoridadLaboratorio.setLaboratorio(laboratorio);
                autoridadLaboratorio.setUser(user);
                autoridadLaboratorio.setUsuarioRegistro(seguridadService.getUsuario(seguridadService.obtenerNombreUsuario()));
                autoridadLaboratorio.setPasivo(false);
                try {
                    this.autoridadesService.addAuthorityLab(autoridadLaboratorio);
                } catch (Exception ex) {
                    this.usuarioService.deleteUser(user);
                    logger.error(ex.getMessage(), ex);
                    ex.printStackTrace();
                    resultado = messageSource.getMessage("msg.user.add.error1", null, null);
                }
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.user.add.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("userName",userName);
            map.put("nombreCompleto", nombreCompleto);
            map.put("email", email);
            map.put("habilitado",String.valueOf(habilitado));
            map.put("mensaje",resultado);
            map.put("labAsignado",labAsignado);
            map.put("password",password);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = "getAutoridadAreaUsuario", method = RequestMethod.GET, produces = "application/json")
    public   @ResponseBody
    List<AutoridadArea> getAutoridadAreaUsuario(@RequestParam(value = "userName", required = true) String userName) throws Exception {
        logger.info("Realizando búsqueda de areas sobre las que tiene autoridad el usuario.");
        List<AutoridadArea> autoridadAreas = autoridadesService.getAutoridadesArea(userName);
        return autoridadAreas;
    }

    @RequestMapping(value = "getAutoridadExamenUsuario", method = RequestMethod.GET, produces = "application/json")
    public   @ResponseBody
    List<AutoridadExamen> getAutoridadExamenUsuario(@RequestParam(value = "userName", required = true) String userName) throws Exception {
        logger.info("Realizando búsqueda de examenes sobre las que tiene autoridad el usuario.");
        List<AutoridadExamen> autoridadExamens =autoridadesService.getAutoridadesExamen(userName);
        return autoridadExamens;
    }

    @RequestMapping(value = "getAutoridadDireccionUsuario", method = RequestMethod.GET, produces = "application/json")
    public   @ResponseBody
    List<AutoridadDireccion> getAutoridadDireccionUsuario(@RequestParam(value = "userName", required = true) String userName) throws Exception {
        logger.info("Realizando búsqueda de direcciones sobre las que tiene autoridad el usuario.");
        List<AutoridadDireccion> autoridadDireccion=autoridadesService.getAutoridadDireccion(userName);
        return autoridadDireccion;
    }

    @RequestMapping(value = "getAutoridadDepartaUsuario", method = RequestMethod.GET, produces = "application/json")
    public   @ResponseBody
    List<AutoridadDepartamento> getAutoridadDepartaUsuario(@RequestParam(value = "userName", required = true) String userName) throws Exception {
        logger.info("Realizando búsqueda de departamentos sobre los que tiene autoridad el usuario.");
        List<AutoridadDepartamento> autoridadDepartamentos=autoridadesService.getAutoridadesDepart(userName);
        return autoridadDepartamentos;
    }

    @RequestMapping(value = "getAreasDispUsuario", method = RequestMethod.GET, produces = "application/json")
    public   @ResponseBody
    List<Area> getAreasDispUsuario(@RequestParam(value = "userName", required = true) String userName) throws Exception {
        logger.info("Realizando búsqueda de areas sobre las que puede tener autoridad el usuario.");
        List<Area> areas = areaService.getAreasDisponiblesUser(userName);
        return areas;
    }

    @RequestMapping(value = "getExamenesDisponiblesUsuario", method = RequestMethod.GET, produces = "application/json")
    public   @ResponseBody
    List<CatalogoExamenes> getExamenesDisponiblesUsuario(@RequestParam(value = "userName", required = true) String userName) throws Exception {
        logger.info("Realizando búsqueda de examenes sobre los que puede tener autoridad el usuario.");
        List<CatalogoExamenes> examenes = examenesService.getExamenesDisponiblesUser(userName);
        return examenes;
    }

    @RequestMapping(value = "getDireccionesDisponiblesUsuario", method = RequestMethod.GET, produces = "application/json")
    public   @ResponseBody
    List<Direccion> getDireccionesDisponiblesUsuario(@RequestParam(value = "userName", required = true) String userName) throws Exception {
        logger.info("Realizando búsqueda de direcciones sobre las que puede tener autoridad el usuario.");
        List<Direccion> direcciones = autoridadesService.getDireccionesDisponiblesUsuario(userName);
        return direcciones;
    }

    @RequestMapping(value = "getDepartaDisponiblesUsuario", method = RequestMethod.GET, produces = "application/json")
    public   @ResponseBody
    List<Departamento> getDepartaDisponiblesUsuario(@RequestParam(value = "userName", required = true) String userName) throws Exception {
        logger.info("Realizando búsqueda de departamentos sobre los que puede tener autoridad el usuario.");
        List<Departamento> departamentos = autoridadesService.getDepartDisponiblesUsuario(userName);
        return departamentos;
    }

    @RequestMapping(value = "asociarAreaUsuario", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void asociarAreaUsuario(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        String userName="";
        Integer idArea=0;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            userName = jsonpObject.get("userName").getAsString();
            idArea = jsonpObject.get("idArea").getAsInt();

            Area area = areaService.getArea(idArea);
            User user = usuarioService.getUser(userName);

            AutoridadArea autoridadArea = new AutoridadArea();
            autoridadArea.setPasivo(false);
            autoridadArea.setUsuarioRegistro(seguridadService.getUsuario(seguridadService.obtenerNombreUsuario()));
            autoridadArea.setArea(area);
            autoridadArea.setUser(user);
            autoridadArea.setFechaRegistro(new Date());

            autoridadesService.addAuthorityArea(autoridadArea);

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.user.add.authority.area.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("userName",userName);
            map.put("idArea",idArea.toString());
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = "overrideAreaUsuario", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void overrideAreaUsuario(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        Integer idAutoridadArea=0;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            idAutoridadArea = jsonpObject.get("idAutoridadArea").getAsInt();

            AutoridadArea autoridadArea = autoridadesService.getAutoridadArea(idAutoridadArea);
            autoridadArea.setPasivo(true);
            autoridadesService.updateAuthorityArea(autoridadArea);
            try {
                autoridadesService.bajaAutoridadExamenesByAutoriArea(idAutoridadArea);
            }catch (Exception ex) {
                autoridadArea.setPasivo(false);
                autoridadesService.updateAuthorityArea(autoridadArea);
                throw new Exception(messageSource.getMessage("msg.user.override.authority.area.error2",null,null));
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.user.override.authority.area.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("idAutoridadArea",idAutoridadArea.toString());
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = "asociarExamenUsuario", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void asociarExamenUsuario(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        String userName="";
        Integer idExamen=0;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            userName = jsonpObject.get("userName").getAsString();
            idExamen = jsonpObject.get("idExamen").getAsInt();

            CatalogoExamenes examen = examenesService.getExamenById(idExamen);
            AutoridadArea autoridadArea = autoridadesService.getAutoridadArea(examen.getArea().getIdArea(),userName);

            AutoridadExamen autoridadExamen = new AutoridadExamen();
            autoridadExamen.setPasivo(false);
            autoridadExamen.setUsuarioRegistro(seguridadService.getUsuario(seguridadService.obtenerNombreUsuario()));
            autoridadExamen.setExamen(examen);
            autoridadExamen.setAutoridadArea(autoridadArea);
            autoridadExamen.setFechaRegistro(new Date());

            autoridadesService.addAuthorityExamen(autoridadExamen);

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.user.add.authority.exam.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("userName",userName);
            map.put("idExamen",idExamen.toString());
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = "overrideExamenUsuario", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void overrideExamenUsuario(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        Integer idAutoridadExamen=0;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            idAutoridadExamen = jsonpObject.get("idAutoridadExamen").getAsInt();

            AutoridadExamen autoridadExamen = autoridadesService.getAutoridadExamen(idAutoridadExamen);
            autoridadExamen.setPasivo(true);
            autoridadesService.updateAuthorityExamen(autoridadExamen);

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.user.override.authority.area.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("idAutoridadExamen",idAutoridadExamen.toString());
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = "asociarDireccionUsuario", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void asociarDireccionUsuario(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        String userName="";
        Integer idDireccion=0;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            userName = jsonpObject.get("userName").getAsString();
            idDireccion = jsonpObject.get("idDireccion").getAsInt();


            List<AutoridadDireccion> autoridadesDireccion =autoridadesService.getAutoridadDireccion(userName);
            if (autoridadesDireccion.size()>0) {
                throw new Exception( messageSource.getMessage("msg.user.add.authority.direct.error2",null,null));
            }else {
                Direccion direccion = laboratoriosService.getDireccionById(idDireccion);
                User user = usuarioService.getUser(userName);

                AutoridadDireccion autoridadDireccion = new AutoridadDireccion();
                autoridadDireccion.setPasivo(false);
                autoridadDireccion.setUsuarioRegistro(seguridadService.getUsuario(seguridadService.obtenerNombreUsuario()));
                autoridadDireccion.setFechaRegistro(new Date());
                autoridadDireccion.setUser(user);
                autoridadDireccion.setDireccion(direccion);

                autoridadesService.addAuthorityDireccion(autoridadDireccion);
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.user.add.authority.direct.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("userName",userName);
            map.put("idDireccion",idDireccion.toString());
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = "overrideDireccionUsuario", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void overrideDireccionUsuario(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        Integer idAutoridadDirec=0;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            idAutoridadDirec = jsonpObject.get("idAutoridadDirec").getAsInt();

            AutoridadDireccion autoridadDireccion = autoridadesService.getAutoridadDireccion(idAutoridadDirec);
            autoridadDireccion.setPasivo(true);
            autoridadesService.updateAuthorityDireccion(autoridadDireccion);

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.user.override.authority.direct.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("idAutoridadDirec",idAutoridadDirec.toString());
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = "asociarDepartamentoUsuario", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void asociarDepartamentoUsuario(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        String userName="";
        Integer idDepartamento=0;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            userName = jsonpObject.get("userName").getAsString();
            idDepartamento = jsonpObject.get("idDepartamento").getAsInt();


            List<AutoridadDepartamento> autoridadesDireccion =autoridadesService.getAutoridadesDepart(userName);
            if (autoridadesDireccion.size()>0) {
                throw new Exception( messageSource.getMessage("msg.user.add.authority.depart.error2",null,null));
            }else {
                Departamento departamento = laboratoriosService.getDepartamentoById(idDepartamento);
                User user = usuarioService.getUser(userName);

                AutoridadDepartamento autoridadDepartamento = new AutoridadDepartamento();
                autoridadDepartamento.setPasivo(false);
                autoridadDepartamento.setUsuarioRegistro(seguridadService.getUsuario(seguridadService.obtenerNombreUsuario()));
                autoridadDepartamento.setFechaRegistro(new Date());
                autoridadDepartamento.setUser(user);
                autoridadDepartamento.setDepartamento(departamento);

                autoridadesService.addAuthorityDepartamento(autoridadDepartamento);
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.user.add.authority.depart.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("userName",userName);
            map.put("idDepartamento",idDepartamento.toString());
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = "overrideDepartaUsuario", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected void overrideDepartaUsuario(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json;
        String resultado = "";
        Integer idAutoridadDepa=0;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF8"));
            json = br.readLine();
            //Recuperando Json enviado desde el cliente
            JsonObject jsonpObject = new Gson().fromJson(json, JsonObject.class);
            idAutoridadDepa = jsonpObject.get("idAutoridadDepa").getAsInt();

            AutoridadDepartamento autoridadDepartamento = autoridadesService.getAutoridadDepartamento(idAutoridadDepa);
            autoridadDepartamento.setPasivo(true);
            autoridadesService.updateAuthorityDepartamento(autoridadDepartamento);

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            ex.printStackTrace();
            resultado =  messageSource.getMessage("msg.user.override.authority.depart.error",null,null);
            resultado=resultado+". \n "+ex.getMessage();

        }finally {
            Map<String, String> map = new HashMap<String, String>();
            map.put("idAutoridadDepa",idAutoridadDepa.toString());
            map.put("mensaje",resultado);
            String jsonResponse = new Gson().toJson(map);
            response.getOutputStream().write(jsonResponse.getBytes());
            response.getOutputStream().close();
        }
    }
}
