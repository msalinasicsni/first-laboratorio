package ni.gob.minsa.laboratorio.restServices;

import ni.gob.minsa.laboratorio.utilities.HL7.TestOrder;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ResultadoHL7Services {
    public static final String API_URL = "http://localhost:8787"; //"http://prueba.minsa.gob.ni:8084/";
    @POST("/wsresultadossmil/ws/resultadossmil/nueva/solicitud")
    Call<ResponseBody> crearSolicitud(@Body TestOrder testOrder);
}
