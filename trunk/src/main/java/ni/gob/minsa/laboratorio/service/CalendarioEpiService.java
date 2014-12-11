package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.estructura.CalendarioEpi;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by FIRSTICT on 9/2/2014.
 */
@Service("calendarioEpiService")
@Transactional

public class CalendarioEpiService {
    @Resource(name = "sessionFactory")
    public SessionFactory sessionFactory;

    public List<CalendarioEpi> getAllCalendariosEpi() throws Exception {
        String query = "from CalendarioEpi as a";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        return q.list();
    }

    public CalendarioEpi getCalendarioEpiByFecha(String strFecha) throws Exception {
        List<CalendarioEpi> result = null;
        CalendarioEpi aux = null;
        if (strFecha !=null && !strFecha.isEmpty()){
            try {
                //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                //Date fechaV = simpleDateFormat.parse(strFecha);
                //Calendar cal = Calendar.getInstance();
                //cal.setTime(fechaV);
                //int year = cal.get(Calendar.YEAR);
                //int month = cal.get(Calendar.MONTH)+1;
                Timestamp fechaB = new Timestamp(StringToDate(strFecha).getTime());
                //String query = "from CalendarioEpi where "+
                //        " anio = :anio and noMes-1 <= :mes and noMes+1 >= :mes";
                Session session = sessionFactory.getCurrentSession();
                String query2 = "from CalendarioEpi as a where a.fechaInicial <= :fecha and a.fechaFinal >= :fecha ";
                Query q = session.createQuery(query2);
                q.setTimestamp("fecha",fechaB);
                //q.setInteger("anio", year);
                //q.setInteger("mes",month);
                //result = q.list();
                aux = (CalendarioEpi)q.uniqueResult();
                /*for(CalendarioEpi calTem: result){
                    Date fechaIni = new Date(calTem.getFechaFinal().getTime());
                    Date fechaFin = new Date(calTem.getFechaFinal().getTime());
                    Date fechaIniFormat = StringToDate(DatetToString(fechaIni));
                    Date fechaFinFormat = StringToDate(DatetToString(fechaFin));
                    if (fechaIniFormat.compareTo(fechaV) > 0 && fechaFinFormat.compareTo(fechaV) > 0 ){
                        aux = calTem;
                        break;
                    }
                }*/
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return  aux;
    }

    private Date StringToDate(String strFecha) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return simpleDateFormat.parse(strFecha);
    }

    private String DatetToString(Date dtFecha) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return simpleDateFormat.format(dtFecha);
    }

}
