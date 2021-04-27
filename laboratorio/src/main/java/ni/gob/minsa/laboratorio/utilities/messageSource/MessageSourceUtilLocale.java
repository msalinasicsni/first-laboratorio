package ni.gob.minsa.laboratorio.utilities.messageSource;

import org.springframework.context.MessageSource;

import java.util.Locale;

/**
 * Created by miguel on 23/3/2021.
 */
public class MessageSourceUtilLocale {
    public static String getMessage(MessageSource messageSource, String code, Locale locale) {
        if (locale == null) locale = new Locale("es");
        return messageSource.getMessage(code, null, locale);
    }
}