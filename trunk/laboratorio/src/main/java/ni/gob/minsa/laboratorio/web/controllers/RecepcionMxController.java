package ni.gob.minsa.laboratorio.web.controllers;

import ni.gob.minsa.laboratorio.service.CatalogoService;
import ni.gob.minsa.laboratorio.service.SeguridadService;
import ni.gob.minsa.laboratorio.utilities.ConstantsSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

/**
 * Created by FIRSTICT on 12/10/2014.
 */
@Controller
@RequestMapping("recepcionMx")
public class RecepcionMxController {

    private static final Logger logger = LoggerFactory.getLogger(RecepcionMxController.class);
    @Autowired
    @Qualifier(value = "seguridadService")
    private SeguridadService seguridadService;

    @Autowired
    @Qualifier(value = "catalogosService")
    private CatalogoService catalogosService;

    @Autowired
    MessageSource messageSource;

    @RequestMapping(value = "search", method = RequestMethod.GET)
    public String initSearchForm(HttpServletRequest request) throws ParseException {
        logger.debug("buscar ordenes para recepcion");
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
        if (urlValidacion.isEmpty()) {
            return "recepcionMx/recepcionarOrders";
        }else
            return urlValidacion;
    }
}
