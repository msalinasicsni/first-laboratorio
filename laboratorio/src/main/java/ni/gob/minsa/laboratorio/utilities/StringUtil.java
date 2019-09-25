package ni.gob.minsa.laboratorio.utilities;

import java.util.Formatter;
import java.util.Random;

/**
 * Created by FIRSTICT on 1/6/2015.
 */
public class StringUtil {

    /**
     * Método que retorna un cadena alfanumerica compuesta por números del 0-9, y letras a-z,A-Z; según la longitud indicada
      * @param longitud tamaño de la cadena
     * @return String cadenaAleatoria
     */
    public static String getCadenaAlfanumAleatoria (int longitud){
        StringBuffer cadenaAleatoria = new StringBuffer();
        long milis = new java.util.GregorianCalendar().getTimeInMillis();
        Random r = new Random(milis);
        int i = 0;
        while ( i < longitud){
            char c = (char)r.nextInt(255);
            if ( (c >= '0' && c <='9') || (c >='a' && c <='z') || (c >='A' && c <='Z') ){
                cadenaAleatoria.append(c);
                i ++;
            }
        }
        return cadenaAleatoria.toString();
    }

    public static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    public static String completarCerosIzquierda(long numero, int longitud){
        Formatter obj = new Formatter();
        return String.valueOf(obj.format("%0"+longitud+"d", numero));
    }
}
