package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.irag.DaIrag;
import ni.gob.minsa.laboratorio.utilities.reportes.DatosDaIrag;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by souyen-ics
 */
@Service("daIragService")
@Transactional
public class DaIragService {

    static final Logger logger = LoggerFactory.getLogger(DaIragService.class);

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;


    /**
     * Retorna Ficha de Vigilancia Integrada
     * @param id
     */
    public DaIrag getFormById(String id) {

        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery("FROM DaIrag vi where vi.idNotificacion.idNotificacion = '" + id + "'");
        return (DaIrag) query.uniqueResult();

    }

    /**
     * Guarda o actualiza una notificacion irag
     */
    public void saveOrUpdateIrag(DaIrag irag) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(irag.getIdNotificacion());
        session.saveOrUpdate(irag);
    }

    public DatosDaIrag getFormByIdV2(String id) {

        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery("select vi.idNotificacion.idNotificacion as idNotificacion, vi.id as id, vi.codExpediente as codExpediente, " +
                "coalesce((select c.valor from Captacion c where c.codigo = vi.codCaptacion.codigo), null) as codCaptacion, " +
                "coalesce((select c.valor from Clasificacion c where c.codigo = vi.codClasificacion.codigo), null) as codClasificacion, " +
                "coalesce((select c.valor from Procedencia c where c.codigo = vi.codProcedencia.codigo), null) as codProcedencia, " +
                "coalesce((select c.valor from CondicionEgreso c where c.codigo = vi.codCondEgreso.codigo), null) as codCondEgreso, " +
                "coalesce((select c.valor from ClasificacionFinal c where c.codigo = vi.codClasFCaso), null) as codClasFCaso, " +
                "coalesce((select c.nombreCie10 from Cie10 c where c.codigoCie10 = vi.diagnostico.codigoCie10), null) as diagnostico, " +
                " vi.fechaConsulta as fechaConsulta, vi.fechaPrimeraConsulta as  fechaPrimeraConsulta, vi.fechaEgreso as fechaEgreso, vi.uci as uci, vi.nombreMadreTutor as nombreMadreTutor, " +
                " vi.noDiasHospitalizado as noDiasHospitalizado, vi.ventilacionAsistida as ventilacionAsistida, vi.diagnostico1Egreso as diagnostico1Egreso, vi.diagnostico2Egreso as diagnostico2Egreso, " +
                " vi.condiciones as condiciones " +
                "FROM DaIrag vi where vi.idNotificacion.idNotificacion = '" + id + "'");

        query.setResultTransformer(Transformers.aliasToBean(DatosDaIrag.class));
        return (DatosDaIrag) query.uniqueResult();

    }

}
