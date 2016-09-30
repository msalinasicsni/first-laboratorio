package ni.gob.minsa.laboratorio.web.controllers;

import com.google.gson.Gson;
import ni.gob.minsa.laboratorio.service.SeguridadService;
import ni.gob.minsa.laboratorio.utilities.ConstantsSecurity;
import org.apache.commons.lang3.text.translate.UnicodeEscaper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by FIRSTICT on 9/19/2016.
 * V1.0
 */
@Controller
@RequestMapping("print")
public class ImprimirCodigoController {
    private static final Logger logger = LoggerFactory.getLogger(RecepcionMxController.class);
    @Autowired
    @Qualifier(value = "seguridadService")
    private SeguridadService seguridadService;

    @RequestMapping(value = "barcode", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String fetchOrdersJson(@RequestParam("strBarCodes")  String strBarCodes) throws Exception{
        strBarCodes = strBarCodes.replaceAll("\\*",".");
        URL url = new URL("http://localhost:13001/print?barcodes="+strBarCodes);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        String respuesta="";
        while((line = reader.readLine()) != null)  {
            respuesta += line;
        }
        reader.close();
        Map<String, String> mapResponse = new HashMap<String, String>();
        mapResponse.put("respuesta", respuesta);

        String jsonResponse = new Gson().toJson(mapResponse);
        //escapar caracteres especiales, escape de los caracteres con valor numérico mayor a 127
        UnicodeEscaper escaper     = UnicodeEscaper.above(127);
        return escaper.translate(jsonResponse);
    }


}
