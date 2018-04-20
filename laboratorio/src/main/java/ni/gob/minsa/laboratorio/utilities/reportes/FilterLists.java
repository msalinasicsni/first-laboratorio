package ni.gob.minsa.laboratorio.utilities.reportes;

import com.google.common.base.Predicate;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Miguel Salinas on 4/2/2018.
 * V1.0
 */
public class FilterLists {

    public static <T> Collection<T> filter(Collection<T> col, Predicate<T> predicate) {
        Collection<T> result = new ArrayList<T>();
        for (T element: col) {
            if (predicate.apply(element)) {
                result.add(element);
            }
        }
        return result;
    }
}
