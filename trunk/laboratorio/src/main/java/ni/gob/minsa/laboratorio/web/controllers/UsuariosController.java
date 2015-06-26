package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ni.gob.minsa.laboratorio.domain.examen.Area;
import ni.gob.minsa.laboratorio.domain.muestra.DaTomaMx;
import ni.gob.minsa.laboratorio.domain.muestra.Laboratorio;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.*;

import ni.gob.minsa.laboratorio.service.AreaService;
import ni.gob.minsa.laboratorio.service.LaboratoriosService;
import ni.gob.minsa.laboratorio.service.SeguridadService;
import ni.gob.minsa.laboratorio.service.UsuarioService;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
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
    MessageSource messageSource;

    @RequestMapping(value = "list", method = RequestMethod.GET)
    public String obtenerUsuarios(Model model) throws ParseException {
        logger.debug("Mostrando Usuarios en JSP");
        List<User> usuarios = usuarioService.getUsers();
        List<AutoridadLaboratorio> autoridadLaboratorios = usuarioService.getAutoridadesLab();
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
    public ModelAndView showUser(@PathVariable("username") String username) {

        List<AutoridadLaboratorio> autoridadLaboratorios = usuarioService.getAutoridadesLab();
        List<Area> areas = areaService.getAreas();
        ModelAndView mav = new ModelAndView("usuarios/usuario");
        mav.addObject("user",this.usuarioService.getUser(username));
        mav.addObject("authorities",this.usuarioService.getAuthorities(username));
        mav.addObject("autoridadLaboratorios",autoridadLaboratorios);
        mav.addObject("areas",areas);

        return mav;
    }

    /**
     * Custom handler for enabling an user.
     *
     * @param username the ID of the user to enable
     * @return a String
     */
    @RequestMapping("/admin/{username}/enable")
    public String enableUser(@PathVariable("username") String username, RedirectAttributes redirectAttributes) {
        /*User user = this.usuarioService.getUser(username);
        user.setCreated(new Date());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        user.setUsuario(authentication.getName());
        user.setEnabled(true);
        this.usuarioService.updateUser(user);
        */
        redirectAttributes.addFlashAttribute("SUCCESS", "Usuario se encuentra activo!");
        return "redirect:/usuarios/admin/{username}";
    }

    /**
     * Custom handler for disabling an user.
     *
     * @param username the ID of the user to disable
     * @return a String
     */
    @RequestMapping("/admin/{username}/disable")
    public String disableUser(@PathVariable("username") String username, RedirectAttributes redirectAttributes) {
        /*User user = this.usuarioService.getUser(username);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        user.setUsuario(authentication.getName());
        user.setCreated(new Date());
        user.setEnabled(false);
        this.usuarioService.updateUser(user);*/
        redirectAttributes.addFlashAttribute("SUCCESS", "Usuario se encuentra inactivo!");
        return "redirect:/usuarios/admin/{username}";
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

            this.usuarioService.bajaAutoridadAreas(userName);

            this.usuarioService.bajaAutoridadExamenes(userName);

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
    public String initUpdateUserForm(@PathVariable("username") String username, Model model) {
        User user = this.usuarioService.getUser(username);
        List<Laboratorio> laboratorioList = laboratoriosService.getLaboratoriosRegionales();
        Laboratorio laboratorio = seguridadService.getLaboratorioUsuario(username);
        model.addAttribute("laboratorios",laboratorioList);
        model.addAttribute("user",user);
        model.addAttribute("labUser",laboratorio);
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
    public String initChgPassUserForm(@PathVariable("username") String username, Model model) {
        User user = this.usuarioService.getUser(username);
        model.addAttribute("user",user);
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

    /*@RequestMapping(value = "/mod/password", method = RequestMethod.PUT)
    public String processChgPassUser2Form(@Valid User user, BindingResult result, SessionStatus status, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "usuarios/ChgPassUser";
        } else {
            user.setCreated(new Date());
            user.setUsuario(user.getUsername());
            StandardPasswordEncoder encoder = new StandardPasswordEncoder();
            String encodedPass = encoder.encode(user.getPassword());
            user.setPassword(encodedPass);
            this.usuarioService.updateUser(user);
            status.setComplete();
            redirectAttributes.addFlashAttribute("SUCCESS", "Contrase�a cambiada correctamente!");
            return "redirect:/";
        }
    }*/

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
                    this.usuarioService.addAuthorityLab(autoridadLaboratorio);
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
        List<AutoridadArea> autoridadAreas = usuarioService.getAutoridadesArea(userName);
        return autoridadAreas;
    }

    @RequestMapping(value = "getAutoridadExamenUsuario", method = RequestMethod.GET, produces = "application/json")
    public   @ResponseBody
    List<AutoridadExamen> getAutoridadExamenUsuario(@RequestParam(value = "userName", required = true) String userName) throws Exception {
        logger.info("Realizando búsqueda de examenes sobre las que tiene autoridad el usuario.");
        List<AutoridadExamen> autoridadExamens =usuarioService.getAutoridadesExamen(userName);
        return autoridadExamens;
    }
}
