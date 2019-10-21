package ni.gob.minsa.laboratorio.utilities;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
        // calcular la diferencia en dias
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


    //calcular edad
    public static int edad(String fecha_nac) {     //fecha_nac debe tener el formato dd/MM/yyyy

        Date fechaActual = new Date();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        String hoy = formato.format(fechaActual);
        String[] dat1 = fecha_nac.split("/");
        String[] dat2 = hoy.split("/");
        int anos = Integer.parseInt(dat2[2]) - Integer.parseInt(dat1[2]);
        int mes = Integer.parseInt(dat2[1]) - Integer.parseInt(dat1[1]);
        if (mes < 0) {
            anos = anos - 1;
        } else if (mes == 0) {
            int dia = Integer.parseInt(dat2[0]) - Integer.parseInt(dat1[0]);
            if (dia > 0) {
                anos = anos - 1;
            }
        }
        return anos;
    }

    public static int calcularEdadAnios(Date dFechaNac){
        Calendar today = Calendar.getInstance();
        Calendar fechaNac = Calendar.getInstance();
        fechaNac.setTime(dFechaNac);
        int diff_year = today.get(Calendar.YEAR) - fechaNac.get(Calendar.YEAR);
        int diff_month = today.get(Calendar.MONTH) - fechaNac.get(Calendar.MONTH);
        int diff_day = today.get(Calendar.DAY_OF_MONTH) - fechaNac.get(Calendar.DAY_OF_MONTH);

        //Si está en ese año pero todavía no los ha cumplido
        if( diff_month < 0 || (diff_month==0 && diff_day < 0)){
            diff_year--;
        }
        return diff_year;
    }

    public static String calcularEdad(Date fechaNac, Date fecha){
        if (fechaNac!=null) {
            Calendar calendarDOB = Calendar.getInstance();
            Calendar calendarToday = Calendar.getInstance();

            calendarToday.setTime(fecha);
            calendarDOB.setTime(fechaNac);
            Integer diaInicio = calendarDOB.get(Calendar.DAY_OF_MONTH);
            Integer mesInicio = calendarDOB.get(Calendar.MONTH)+1;
            Integer anioInicio = calendarDOB.get(Calendar.YEAR);

            Integer diaActual = calendarToday.get(Calendar.DAY_OF_MONTH);
            Integer mesActual = calendarToday.get(Calendar.MONTH)+1;
            Integer anioActual = calendarToday.get(Calendar.YEAR);

            int b = 0;
            Integer dias = 0;
            Integer anios = 0;
            Integer meses = 0;
            b = calendarDOB.getActualMaximum(Calendar.DAY_OF_MONTH);
            if ((anioInicio > anioActual) || (anioInicio.equals(anioActual) && mesInicio > mesActual)
                    || (anioInicio.equals(anioActual) && mesInicio.equals(mesActual) && diaInicio > diaActual)) {
                return "ND";
            } else {
                if (mesInicio <= mesActual) {
                    anios = anioActual - anioInicio;
                    if (diaInicio <= diaActual) {
                        meses = mesActual - mesInicio;
                        dias = (diaActual - diaInicio);
                    } else {
                        if (mesActual.equals(mesInicio)) {
                            anios = anios - 1;
                        }
                        meses = (mesActual - mesInicio - 1 + 12) % 12;
                        dias = b - (diaInicio - diaActual);
                    }
                } else {
                    anios = anioActual - anioInicio - 1;
                    if (diaInicio > diaActual) {
                        meses = mesActual - mesInicio - 1 + 12;
                        dias = b - (diaInicio - diaActual);
                    } else {
                        meses = mesActual - mesInicio + 12;
                        dias = diaActual - diaInicio;
                    }
                }
            }
            return anios.toString() + "/" + meses.toString() + "/" + dias.toString();
        }else{
            return "ND";
        }

    }

    public static Timestamp StringToTimestamp(String fechah) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = sdf.parse(fechah);
        return new Timestamp(date.getTime());
    }

    public static String getDiferenciaDiasHorasMinSegEntreFechas(Date fechaInicial, Date fechaFinal){
        if (fechaInicial != null && fechaFinal != null) {
            int diferencia = (int) ((fechaFinal.getTime() - fechaInicial.getTime()) / 1000);
            int dias = 0;
            int horas = 0;
            int minutos = 0;
            if (diferencia > 86400) {
                dias = (int) Math.floor(diferencia / 86400);
                diferencia = diferencia - (dias * 86400);
            }

            if (diferencia > 3600) {
                horas = (int) Math.floor(diferencia / 3600);
                diferencia = diferencia - (horas * 3600);
            }

            if (diferencia > 60) {
                minutos = (int) Math.floor(diferencia / 60);
                diferencia = diferencia - (minutos * 60);
            }
            return dias + " D, " + horas + " H, " + minutos + " M, " + diferencia + " S";
        }else return "SIN DATOS";
    }

    public static String getDiferenciaDiasHorasMinEntreFechas(Date fechaInicial, Date fechaFinal){
        int diferencia=(int) ((fechaFinal.getTime()-fechaInicial.getTime())/1000);
        int dias=0;
        int horas=0;
        int minutos=0;
        if(diferencia>86400) {
            dias=(int)Math.floor(diferencia/86400);
            diferencia=diferencia-(dias*86400);
        }

        if(diferencia>3600) {
            horas=(int)Math.floor(diferencia/3600);
            diferencia=diferencia-(horas*3600);
        }

        if(diferencia>60) {
            minutos=(int)Math.floor(diferencia/60);
            diferencia=diferencia-(minutos*60);
        }
        return dias+" D, "+horas+" H, "+minutos+" M";
    }

}
