package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.comunicacionResultados.RespuestaHL7;
import ni.gob.minsa.laboratorio.domain.comunicacionResultados.SolicitudHL7;
import ni.gob.minsa.laboratorio.utilities.DateUtil;
import ni.gob.minsa.laboratorio.utilities.StringUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

@Service("comunicacionResultadosService")
@Transactional
public class ComunicacionResultadosService {

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;

    public void saveOrUpdateSolicitudHL7(SolicitudHL7 obj){
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(obj);
    }

    public void saveOrUpdateRespuestaHL7(RespuestaHL7 obj){
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(obj);
    }

    public long contarSolicitudesDelDia(){
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("select count(s.idSolicitud) from SolicitudHL7 s where to_char(s.fechaRegistro, 'DDMMYYYY') = to_char(current_date, 'DDMMYYYY')");
        return (long)query.uniqueResult();
    }

    public long getMaxIdMuestraSecun(){
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("select coalesce(max(cast(s.idMuestraSecundario as long)), 1000000) " +
                "from SolicitudHL7 s where to_char(s.fechaRegistro, 'DDMMYYYY') = to_char(current_date, 'DDMMYYYY')");
        return (long)query.uniqueResult();
    }

    public String generarIdMuestra(){
        //long total = this.contarSolicitudesDelDia()+1;
        long total = this.getMaxIdMuestraSecun()+1;//el primer id debe ser 1000001 (un millon uno)
        return DateUtil.DateToString(new Date(), "yyMMdd")+StringUtil.completarCerosIzquierda(total, 5);
    }

}
