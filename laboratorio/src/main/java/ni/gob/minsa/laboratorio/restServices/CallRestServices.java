package ni.gob.minsa.laboratorio.restServices;

import ni.gob.minsa.laboratorio.utilities.HL7.TestOrder;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CallRestServices {
    public static void crearSolicitud(TestOrder testOrder) throws Exception {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ResultadoHL7Services.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ResultadoHL7Services servicios = retrofit.create(ResultadoHL7Services.class);

        Call<ResponseBody> call = servicios.crearSolicitud(testOrder);
        Response<ResponseBody> response = call.execute();
        System.out.println("Respuesta es exitosa desde "+ResultadoHL7Services.API_URL);
        System.out.println(response.isSuccessful());
        if (response.body()!=null) System.out.println(response.body().string());
    }
}
