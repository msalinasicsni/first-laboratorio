package ni.gob.minsa.laboratorio;

import ni.gob.minsa.laboratorio.service.SeguridadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/*")
public class HomeController {
	
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    @Autowired
    @Qualifier(value = "seguridadService")
    private SeguridadService seguridadService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(HttpServletRequest request) {
        logger.info("Starting project...");
        String urlValidacion="";
        try {
            urlValidacion = seguridadService.validarLogin(request);
        }catch (Exception e){
            e.printStackTrace();
            urlValidacion = "404";
        }
        if (urlValidacion.isEmpty()) {
            return "home";
        }else{
            return urlValidacion;
        }

    }
    
    @RequestMapping(value="/403", method = RequestMethod.GET)
	public String noAcceso() {
		return "403"; 
	}
	
	@RequestMapping(value="/404", method = RequestMethod.GET)
	public String noEncontrado() { 
		return "404";
	}

    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public String salir(HttpSession session) {
        seguridadService.logOut(session);
        return "redirect:"+seguridadService.obtenerUrlPortal();
    }
    
}
