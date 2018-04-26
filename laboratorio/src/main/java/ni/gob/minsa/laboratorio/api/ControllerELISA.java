package ni.gob.minsa.laboratorio.api;

import ni.gob.minsa.laboratorio.domain.muestra.OrdenExamen;
import ni.gob.minsa.laboratorio.domain.resultados.DetalleResultado;
import ni.gob.minsa.laboratorio.domain.resultados.RespuestaExamen;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.User;
import ni.gob.minsa.laboratorio.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by FIRSTICT on 6/19/2015.
 * V1.0
 */
@Controller
@RequestMapping(value = "elisaResult")
public class ControllerELISA {

    @Autowired
    @Qualifier(value = "seguridadService")
    private SeguridadService seguridadService;

    @Autowired
    @Qualifier(value = "resultadosService")
    private ResultadosService resultadosService;

    @Autowired
    @Qualifier(value = "respuestasExamenService")
    private RespuestasExamenService respuestasExamenService;

    @Autowired
    @Qualifier(value = "ordenExamenMxService")
    private OrdenExamenMxService ordenExamenMxService;

    @RequestMapping(value = "save", method = RequestMethod.POST, consumes = "application/json")
    public @ResponseBody
    String sendMessageJson(@RequestBody ResultadoELISA resultado) {
        if (resultado == null){
            return "Fail. Resultado Null!";
        }
        else{
            User usuario = seguridadService.getUsuario(seguridadService.obtenerNombreUsuario());
            if (usuario ==null)
                return "Fail. Usuario enviado no se reconoce como usuario válido!";

            OrdenExamen ordenExamen = ordenExamenMxService.getOrdExamenNoAnulByCodLabMxIdDxIdExamen(resultado.getCodigoMx(), Integer.valueOf(resultado.getIdDx()), Integer.valueOf(resultado.getIdExamen()), seguridadService.obtenerNombreUsuario());
            if (ordenExamen ==null)
                return "Fail. No se encontró orden de exámen para la según codigo_mx="+resultado.getCodigoMx()+", dx="+resultado.getIdDx()+" y examen = "+resultado.getIdExamen()+" informado. ";

            RespuestaExamen respuestaExamen = respuestasExamenService.getRespuestaByNombre(resultado.getNombreRespuesta());
            if (respuestaExamen ==null)
                return "Fail. Id Respuesta enviada no se reconoce como respues válida!";

            DetalleResultado detalleResultado = new DetalleResultado();
            detalleResultado.setFechahProcesa(new Timestamp(new Date().getTime()));
            detalleResultado.setValor(resultado.getValor());
            detalleResultado.setRespuesta(respuestaExamen);
            detalleResultado.setExamen(ordenExamen);
            detalleResultado.setUsuarioRegistro(usuario);
            try {
                resultadosService.addDetalleResultado(detalleResultado);
            }catch (Exception ex){
                ex.printStackTrace();
                return "Fail. Sucedió un error al agregar el resultado. \n"+ex.getMessage();
            }
            return "Success";
        }
    }
}
