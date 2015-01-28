package ni.gob.minsa.laboratorio.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by FIRSTICT on 1/6/2015.
 */
public class DateUtil {


    /**
     * Convierte un string a Date con formato dd/MM/yyyy HH:mm:ss
     * @param strFecha cadena a convertir
     * @return Fecha
     * @throws java.text.ParseException
     */
    public static Date StringToDate(String strFecha) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return simpleDateFormat.parse(strFecha);
    }

    /**
     * Convierte un string a Date según el formato indicado
     * @param strFecha cadena a convertir
     * @param formato formato solicitado
     * @return Fecha
     * @throws java.text.ParseException
     */
    public static Date StringToDate(String strFecha, String formato) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formato);
        return simpleDateFormat.parse(strFecha);
    }

    /**
     * Convierte una Date a String, según el formato indicado
     * @param dtFecha Fecha a convertir
     * @param format formato solicitado
     * @return String
     */
    public static String DateToString(Date dtFecha, String format)  {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        if(dtFecha!=null)
            return simpleDateFormat.format(dtFecha);
        else
            return null;
    }

    public static int CalcularDiferenciaDiasFechas(Date fecha1, Date fecha2){
        // Crear 2 instancias de Calendar
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(fecha1);
        cal2.setTime(fecha2);
        // conseguir la representacion de la fecha en milisegundos
        long milis1 = cal1.getTimeInMillis();
        long milis2 = cal2.getTimeInMillis();
        // calcular la diferencia en milisengundos
        long diff = milis2 - milis1;
        // calcular la diferencia en horas
        Long diffHours = diff / (24 * 60 * 60 * 1000);
        return diffHours.intValue();
    }

    public static long CalcularDiferenciaHorasFechas(Date fecha1, Date fecha2){
        // Crear 2 instancias de Calendar
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(fecha1);
        cal2.setTime(fecha2);
        // conseguir la representacion de la fecha en milisegundos
        long milis1 = cal1.getTimeInMillis();
        long milis2 = cal2.getTimeInMillis();
        // calcular la diferencia en milisengundos
        long diff = milis2 - milis1;
        // calcular la diferencia en horas
        long diffHours = diff / (60 * 60 * 1000);
        return diffHours;
    }
}
