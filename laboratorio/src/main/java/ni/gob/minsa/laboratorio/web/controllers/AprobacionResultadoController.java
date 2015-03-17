package ni.gob.minsa.laboratorio.web.controllers;

import ni.gob.minsa.laboratorio.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * Created by souyen-ics.
 */
@Controller
@RequestMapping("aprobacion")
public class AprobacionResultadoController {

    private static final Logger logger = LoggerFactory.getLogger(AprobacionResultadoController.class);

    @Resource(name = "seguridadService")
    private SeguridadService seguridadService;

    @Resource(name = "catalogosService")
    private CatalogoService catalogoService;

    @Resource(name = "usuarioService")
    private UsuarioService usuarioService;

    @Resource(name = "entidadAdmonService")
    private EntidadAdmonService entidadAdmonService;

    @Resource(name = "resultadoFinalService")
    private ResultadoFinalService resultadoFinalService;

    @Resource(name= "tomaMxService")
    private TomaMxService tomaMxService;

    @Resource(name= "respuestasDxService")
    private RespuestasDxService respuestasDxService;

    @Autowired
    MessageSource messageSource;


}
