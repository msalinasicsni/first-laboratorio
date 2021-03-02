package ni.gob.minsa.laboratorio.restServices.ServiciosEnLinea;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by miguel on 18/2/2021.
 */
public interface ServiciosEnLineaServices {
    public static final String TOKEN_PREFIX = "Q8R3C20R";
    public static final String TOKEN_KEY = "X5ZflsCYJ3RcmOJrlPVBKQ==";

    @POST("/wsserviciosenlinea/v1/serviciosenlinea/validacion")
    Call<ResponseBody> authenticate(@Body AuthenticationRequest authenticationRequest);

    @POST("wsserviciosenlinea/v1/serviciosenlinea/gestion/preregistro/obtenerlistapreregistro")
    Call<ResponseBody> obtenerListaPreRegistro(@Body ListaPreRegistroRequest listaPreRegistroRequest, @Header("Authorization") String auth);
}
