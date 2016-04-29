package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.irag.DaVacunasIrag;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by souyen-ics.
 */
@Service("daVacunasIragService")
@Transactional
public class DaVacunasIragService {

    static final Logger logger = LoggerFactory.getLogger(DaVacunasIrag.class);

   @Resource(name ="sessionFactory")
    public SessionFactory sessionFactory;


      /**
       * Retorna lista de Vacunas
       * @param id
       */
      @SuppressWarnings("unchecked")
      public List<DaVacunasIrag> getAllVaccinesByIdIrag(String id){

          String query = "select vacu FROM DaVacunasIrag vacu where vacu.idNotificacion.idNotificacion.idNotificacion = :id and vacu.pasivo = :pasivo";
          org.hibernate.Session session = sessionFactory.getCurrentSession();
          Query q = session.createQuery(query);
          q.setParameter("pasivo", false);
          q.setString("id",id);
          return q.list();
      }



}
