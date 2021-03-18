package ni.gob.minsa.laboratorio.restServices.ServiciosEnLinea;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import ni.gob.minsa.laboratorio.restServices.ServiciosEnLinea.entidades.PreRegistro;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CallServiciosEnLineaServices {
    public static ServiciosEnLineaResponse authenticate(String apiUrl) throws Exception {

        //OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        final ServiciosEnLineaResponse authenticationResponse = new ServiciosEnLineaResponse();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiUrl)
                //.client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServiciosEnLineaServices servicios = retrofit.create(ServiciosEnLineaServices.class);

        Call<ResponseBody> call = servicios.authenticate(authenticationRequest);
        Response<ResponseBody> response = call.execute();
        System.out.println("Respuesta es exitosa desde "+apiUrl);
        System.out.println(response.isSuccessful());
        if (response.body()!=null) {
            String strResponse = response.body().string();
            JsonObject jsonpObject = new Gson().fromJson(strResponse, JsonObject.class);
            if (jsonpObject.get("data") != null) {
                authenticationResponse.setData(jsonpObject.get("data").toString());
            }
            if (jsonpObject.get("status") != null) {
                authenticationResponse.setStatus(jsonpObject.get("status").getAsString());
            }
            if (jsonpObject.get("message") != null) {
                authenticationResponse.setMessage(jsonpObject.get("message").getAsString());
            }
        }
        return authenticationResponse;
    }

    public static List<PreRegistro> obtenerListaPreRegistro(String apiUrl, ListaPreRegistroRequest listaPreRegistroRequest, String token) throws Exception {
        //OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiUrl)
                //.client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServiciosEnLineaServices servicios = retrofit.create(ServiciosEnLineaServices.class);
        List<PreRegistro> preRegistros = new ArrayList<PreRegistro>();
        Type founderListType = founderListType = new TypeToken<ArrayList<PreRegistro>>(){}.getType();

        listaPreRegistroRequest.setHknhjx(ServiciosEnLineaServices.TOKEN_KEY);
        Call<ResponseBody> call = servicios.obtenerListaPreRegistro(listaPreRegistroRequest, ServiciosEnLineaServices.TOKEN_PREFIX+ " "+token);
        Response<ResponseBody> response = call.execute();
        System.out.println("Respuesta es exitosa desde "+apiUrl);
        System.out.println(response.isSuccessful());
        if (response.body()!=null) {
            String strResponse = response.body().string();
            JsonObject jsonpObject = new Gson().fromJson(strResponse, JsonObject.class);
            String data = null;
            if (jsonpObject.get("data") != null && !jsonpObject.get("data").toString().equalsIgnoreCase("null")) {
                data = jsonpObject.get("data").toString();
                preRegistros = new Gson().fromJson(data, founderListType);
            }
        }
        return preRegistros;
    }

    public static PreRegistro obtenerPreRegistro(String apiUrl,ListaPreRegistroRequest listaPreRegistroRequest, String token) throws Exception {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServiciosEnLineaServices servicios = retrofit.create(ServiciosEnLineaServices.class);
        List<PreRegistro> preRegistros = new ArrayList<PreRegistro>();
        Type founderListType = founderListType = new TypeToken<ArrayList<PreRegistro>>(){}.getType();

        listaPreRegistroRequest.setHknhjx(ServiciosEnLineaServices.TOKEN_KEY);
        Call<ResponseBody> call = servicios.obtenerListaPreRegistro(listaPreRegistroRequest, ServiciosEnLineaServices.TOKEN_PREFIX+ " "+token);
        Response<ResponseBody> response = call.execute();
        System.out.println("Respuesta es exitosa desde "+apiUrl);
        System.out.println(response.isSuccessful());
        if (response.body()!=null) {
            String strResponse = response.body().string();
            JsonObject jsonpObject = new Gson().fromJson(strResponse, JsonObject.class);
            String data = null;
            if (jsonpObject.get("data") != null && !jsonpObject.get("data").toString().equalsIgnoreCase("null")) {
                data = jsonpObject.get("data").toString();
                preRegistros = new Gson().fromJson(data, founderListType);
            }
        }
        return preRegistros.size() > 0 ? preRegistros.get(0) : null;
    }

    public static ServiciosEnLineaResponse actualizarPreregistro(String apiUrl, PreRegistro preRegistro, String token) throws Exception{
        ServiciosEnLineaResponse updateResponse = new ServiciosEnLineaResponse();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServiciosEnLineaServices servicios = retrofit.create(ServiciosEnLineaServices.class);

        Call<ResponseBody> call = servicios.actualizarPreregistro(preRegistro, ServiciosEnLineaServices.TOKEN_PREFIX+ " "+token);
        Response<ResponseBody> response = call.execute();
        System.out.println("Respuesta es exitosa desde "+apiUrl);
        System.out.println(response.isSuccessful());
        if (response.body()!=null) {
            String strResponse = response.body().string();
            JsonObject jsonpObject = new Gson().fromJson(strResponse, JsonObject.class);
            if (jsonpObject.get("data") != null) {
                updateResponse.setData(jsonpObject.get("data").toString());
            }
            if (jsonpObject.get("status") != null) {
                updateResponse.setStatus(jsonpObject.get("status").getAsString());
            }
            if (jsonpObject.get("message") != null) {
                updateResponse.setMessage(jsonpObject.get("message").getAsString());
            }
        }
        return updateResponse;
    }
}
