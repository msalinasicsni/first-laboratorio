package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.muestra.Laboratorio;
import ni.gob.minsa.laboratorio.domain.muestra.TecnicaxLaboratorio;
import ni.gob.minsa.laboratorio.domain.muestra.TipoTecnica;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by FIRSTICT on 12/11/2014.
 */
@Service("tecnicasProcesamientoService")
@Transactional
public class TecnicasProcesamientoService {
    @Resource(name = "sessionFactory")
    private SessionFactory sessionFactory;

    public List<TecnicaxLaboratorio> getTecnicaxLaboratoriosByLab(String codigoLab){
        String query = "from TecnicaxLaboratorio where laboratorio.codigo =:codigo order by tecnica.valor";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("codigo",codigoLab);
        return q.list();
    }

    public TecnicaxLaboratorio getTecnicaxLaboratoriosById(String idTecnicaLab){
        String query = "from TecnicaxLaboratorio where idTecnicaLab = :idTecnicaLab";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idTecnicaLab",idTecnicaLab);
        return (TecnicaxLaboratorio)q.uniqueResult();
    }
}
