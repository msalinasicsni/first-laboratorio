package ni.gob.minsa.laboratorio;

import com.google.gson.Gson;
import ni.gob.minsa.laboratorio.domain.muestra.Laboratorio;
import ni.gob.minsa.laboratorio.service.HomeService;
import ni.gob.minsa.laboratorio.service.SeguridadService;
import org.apache.commons.lang3.text.translate.UnicodeEscaper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/")
public class HomeController {
	
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    @Autowired
    @Qualifier(value = "homeService")
    private HomeService homeService;

    @Autowired
    @Qualifier(value = "seguridadService")
    private SeguridadService seguridadService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String init() {
        logger.info("Starting project");
        return "home";
    }

    @RequestMapping(value="/login", method = RequestMethod.GET)
    public String login(ModelMap model) {

        return "login";

    }

    @RequestMapping(value="/loginfailed", method = RequestMethod.GET)
    public String loginerror(ModelMap model) {
        model.addAttribute("error", "true");
        return "login";

    }

    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        session.invalidate();
        return "login";
    }

    @RequestMapping(value="/403", method = RequestMethod.GET)
	public String noAcceso() {
		return "403"; 
	}
	
	@RequestMapping(value="/404", method = RequestMethod.GET)
	public String noEncontrado() { 
		return "404";
	}

    @RequestMapping(value = "getResumenMuestrasSILAIS", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    String getResumenMuestrasSILAIS() throws Exception {
        String jsonResponse = "";
        Laboratorio laboratorio = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
        if (laboratorio!=null) {
            List<Object[]> resumen = homeService.getResumenMuestrasSILAIS(laboratorio.getCodigo());
            Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
            Integer indice = 0;
            for (Object[] obj : resumen) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("total", obj[0] != null ? obj[0].toString() : "");
                map.put("SILAIS", obj[2] != null ? obj[2].toString() : "SIN SILAIS");
                mapResponse.put(indice, map);
                indice++;
            }
            jsonResponse = new Gson().toJson(mapResponse);
        }
        //escapar caracteres especiales, escape de los caracteres con valor numérico mayor a 127
        UnicodeEscaper escaper     = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }

    @RequestMapping(value = "getResumenMuestrasSolicitud", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    String getResumenMuestrasSolicitud() throws Exception {
        String jsonResponse = "";
        Laboratorio laboratorio = seguridadService.getLaboratorioUsuario(seguridadService.obtenerNombreUsuario());
        if (laboratorio!=null) {
            List<Object[]> resumen = homeService.getResumenMuestrasSolicitud(laboratorio.getCodigo());
            Map<Integer, Object> mapResponse = new HashMap<Integer, Object>();
            Integer indice = 0;
            for (Object[] obj : resumen) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("total", obj[0] != null ? obj[0].toString() : "");
                map.put("solicitud", obj[2] != null ? obj[2].toString() : "");
                mapResponse.put(indice, map);
                indice++;
            }
            jsonResponse = new Gson().toJson(mapResponse);
        }
        //escapar caracteres especiales, escape de los caracteres con valor numérico mayor a 127
        UnicodeEscaper escaper     = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }
}
