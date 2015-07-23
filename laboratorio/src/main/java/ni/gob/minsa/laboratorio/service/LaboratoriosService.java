package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.examen.*;
import ni.gob.minsa.laboratorio.domain.muestra.Laboratorio;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by FIRSTICT on 12/11/2014.
 */
@Service("laboratoriosService")
@Transactional
public class LaboratoriosService {
    @Resource(name = "sessionFactory")
    private SessionFactory sessionFactory;

    public void saveLaboratorio(Laboratorio laboratorio){
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(laboratorio);
    }

    public void saveDireccion(Direccion direccion) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(direccion);
    }

    public void saveDepartamento(Departamento departamento) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(departamento);
    }

    public void saveDireccionLaboratorio(DireccionLaboratorio direccionLaboratorio) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(direccionLaboratorio);
    }

    public void saveDepartamentoDireccion(DepartamentoDireccion departamentoDireccion) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(departamentoDireccion);
    }

    public List<Laboratorio> getAllLaboratories(){
        String query = "from Laboratorio order by nombre";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        return q.list();
    }

    public List<Laboratorio> getLaboratoriosInternos(){
        String query = "from Laboratorio where codTipo =:codTipo order by nombre";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("codTipo","INT");
        return q.list();
    }

    public List<Laboratorio> getLaboratoriosRegionales(){
        String query = "from Laboratorio where codTipo =:codTipo order by nombre";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("codTipo","REG");
        return q.list();
    }

    public Laboratorio getLaboratorioByCodigo(String codLaboratorio){
        String query = "from Laboratorio where codigo =:codLaboratorio order by nombre";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("codLaboratorio",codLaboratorio);
        return (Laboratorio)q.uniqueResult();
    }

    public Direccion getDireccionById(Integer idDireccion){
        String query = "from Direccion where idDireccion = :idDireccion";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idDireccion",idDireccion);
        return (Direccion)q.uniqueResult();
    }

    public Departamento getDepartamentoById(Integer idDepartamento){
        String query = "from Departamento where idDepartamento = :idDepartamento";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idDepartamento",idDepartamento);
        return (Departamento)q.uniqueResult();
    }

    public List<Direccion> getDirecciones(){
        String query = "select dir from Direccion dir order by dir.nombre";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        return q.list();
    }

    public List<Direccion> getDireccionesByLab(String codLaboratorio){
        String query = "select dir from Direccion dir, DireccionLaboratorio  dirLab " +
                "where dir.idDireccion = dirLab.direccion.idDireccion and " +
                "dirLab.pasivo = false and dir.pasivo = false and " +
                "dirLab.laboratorio.codigo = :codLaboratorio order by dir.nombre";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("codLaboratorio",codLaboratorio);
        return q.list();
    }

    public List<Departamento> getDepartamentos(){
        String query = "from Departamento dep order by dep.nombre";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        return q.list();
    }

    public List<Departamento> getDepartamentosByDireccion(Integer idDireccion){
        String query = "from Departamento dep, DepartamentoDireccion depDir " +
                "where dep.idDepartamento = depDir.departamento.idDepartamento and " +
                "depDir.pasivo = false and dep.pasivo = false and " +
                "depDir.direccion.idDireccion = :idDireccion order by dep.nombre";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idDireccion",idDireccion);
        return q.list();
    }

    public DireccionLaboratorio getDireccionLaboratorioById(Integer idDireccionLab){
        String query = "from DireccionLaboratorio where idDireccionLab = :idDireccionLab";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idDireccionLab",idDireccionLab);
        return (DireccionLaboratorio)q.uniqueResult();
    }

    public DepartamentoDireccion getDepartamentoLaboratorioById(Integer idDepartDireccion){
        String query = "from DepartamentoDireccion where idDepartDireccion = :idDepartDireccion";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        q.setParameter("idDepartDireccion",idDepartDireccion);
        return (DepartamentoDireccion)q.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<Direccion> getDireccionesActivas(){
        String query = "select dir from Direccion dir where dir.pasivo = false order by dir.nombre";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        return q.list();
    }

    @SuppressWarnings("unchecked")
    public List<Departamento> getDepartamentosActivos(){
        String query = "from Departamento dep where dep.pasivo = false order by dep.nombre";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        return q.list();
    }

    @SuppressWarnings("unchecked")
    public List<Area> getAreasActivas(){
        String query = "from Area area where area.pasivo = false order by area.nombre";
        Query q = sessionFactory.getCurrentSession().createQuery(query);
        return q.list();
    }
}
