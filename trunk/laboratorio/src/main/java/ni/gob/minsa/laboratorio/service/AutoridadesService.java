package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.examen.Departamento;
import ni.gob.minsa.laboratorio.domain.examen.Direccion;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.security.util.AuthResources;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by FIRSTICT on 6/29/2015.
 * V1.0
 */
@Service("autoridadesService")
@Transactional
public class AutoridadesService {
    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;

    public void addAuthorityLab(AutoridadLaboratorio auth) {
        Session session = sessionFactory.getCurrentSession();
        session.save(auth);
    }

    public void addAuthorityArea(AutoridadArea auth) {
        Session session = sessionFactory.getCurrentSession();
        session.save(auth);
    }

    public void addAuthorityExamen(AutoridadExamen auth) {
        Session session = sessionFactory.getCurrentSession();
        session.save(auth);
    }

    public void addAuthorityDireccion(AutoridadDireccion auth) {
        Session session = sessionFactory.getCurrentSession();
        session.save(auth);
    }

    public void addAuthorityDepartamento(AutoridadDepartamento auth) {
        Session session = sessionFactory.getCurrentSession();
        session.save(auth);
    }

    public void updateAuthorityArea(AutoridadArea auth) {
        Session session = sessionFactory.getCurrentSession();
        session.update(auth);
    }

    public void updateAuthorityExamen(AutoridadExamen auth) {
        Session session = sessionFactory.getCurrentSession();
        session.update(auth);
    }

    public void updateAuthorityDireccion(AutoridadDireccion auth) {
        Session session = sessionFactory.getCurrentSession();
        session.update(auth);
    }

    public void updateAuthorityDepartamento(AutoridadDepartamento auth) {
        Session session = sessionFactory.getCurrentSession();
        session.update(auth);
    }

    public AutoridadArea getAutoridadArea(Integer idAutoridadArea) {
        // Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM AutoridadArea as a where idAutoridadArea = :idAutoridadArea");
        query.setParameter("idAutoridadArea",idAutoridadArea);
        return (AutoridadArea)query.uniqueResult();
    }

    public AutoridadArea getAutoridadArea(Integer idArea, String userName) {
        // Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM AutoridadArea as a where a.area.idArea = :idArea and a.user.username = :userName and a.pasivo = false");
        query.setParameter("idArea",idArea);
        query.setParameter("userName",userName);
        return (AutoridadArea)query.uniqueResult();
    }

    public AutoridadExamen getAutoridadExamen(Integer idAutoridadExamen) {
        // Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM AutoridadExamen as a where idAutoridadExamen = :idAutoridadExamen");
        query.setParameter("idAutoridadExamen",idAutoridadExamen);
        return (AutoridadExamen)query.uniqueResult();
    }

    public AutoridadDireccion getAutoridadDireccion(Integer idAutoridadDirec) {
        // Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM AutoridadDireccion as a where idAutoridadDirec = :idAutoridadDirec");
        query.setParameter("idAutoridadDirec",idAutoridadDirec);
        return (AutoridadDireccion)query.uniqueResult();
    }

    public AutoridadDepartamento getAutoridadDepartamento(Integer idAutoridadDepa) {
        // Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM AutoridadDepartamento as a where idAutoridadDepa = :idAutoridadDepa");
        query.setParameter("idAutoridadDepa",idAutoridadDepa);
        return (AutoridadDepartamento)query.uniqueResult();
    }

    public List<AutoridadLaboratorio> getAutoridadesLab() {
        // Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM AutoridadLaboratorio as a");
        return query.list();
    }

    public List<AutoridadArea> getAutoridadesArea(String userName) {
        // Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM AutoridadArea as a where a.pasivo = false and a.user.username = :userName");
        query.setParameter("userName",userName);
        return query.list();
    }

    public List<AutoridadExamen> getAutoridadesExamen(String userName) {
        // Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM AutoridadExamen as a where a.pasivo = false and a.autoridadArea.user.username = :userName");
        query.setParameter("userName",userName);
        return query.list();
    }

    public List<AutoridadDireccion> getAutoridadDireccion(String userName) {
        // Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM AutoridadDireccion as a where a.pasivo = false and a.user.username = :userName");
        query.setParameter("userName",userName);
        return query.list();
    }

    public List<AutoridadDepartamento> getAutoridadesDepart(String userName) {
        // Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM AutoridadDepartamento as a where a.pasivo = false and a.user.username = :userName");
        query.setParameter("userName",userName);
        return query.list();
    }

    public Integer bajaAutoridadAreas(String userName) {
        // Retrieve session from Hibernate
        Session s = sessionFactory.openSession();
        Transaction tx = s.beginTransaction();

        String hqlBaja = "update AutoridadArea set pasivo=true where user.username = :userName and pasivo = false ";
        int updateEntities = s.createQuery(hqlBaja)
                .setString("userName", userName)
                .executeUpdate();
        tx.commit();
        s.close();
        return updateEntities;
    }

    public Integer bajaAutoridadExamenesByUserName(String userName) {
        // Retrieve session from Hibernate
        Session s = sessionFactory.openSession();
        Transaction tx = s.beginTransaction();

        String hqlBaja = "update AutoridadExamen set pasivo=true where autoridadArea.user.username = :userName and pasivo = false ";
        int updateEntities = s.createQuery( hqlBaja )
                .setString("userName", userName)
                .executeUpdate();
        tx.commit();
        s.close();
        return updateEntities;
    }

    public Integer bajaAutoridadExamenesByAutoriArea(Integer idAutoridadArea) {
        // Retrieve session from Hibernate
        Session s = sessionFactory.openSession();
        Transaction tx = s.beginTransaction();

        String hqlBaja = "update AutoridadExamen set pasivo=true where autoridadArea.idAutoridadArea = :idAutoridadArea and pasivo = false ";
        int updateEntities = s.createQuery( hqlBaja )
                .setParameter("idAutoridadArea", idAutoridadArea)
                .executeUpdate();
        tx.commit();
        s.close();
        return updateEntities;
    }

    public Integer bajaAutoridadDireccion(String userName) {
        // Retrieve session from Hibernate
        Session s = sessionFactory.openSession();
        Transaction tx = s.beginTransaction();

        String hqlBaja = "update AutoridadDireccion set pasivo=true where user.username = :userName and pasivo = false ";
        int updateEntities = s.createQuery(hqlBaja)
                .setString("userName", userName)
                .executeUpdate();
        tx.commit();
        s.close();
        return updateEntities;
    }

    public Integer bajaAutoridadDepartamento(String userName) {
        // Retrieve session from Hibernate
        Session s = sessionFactory.openSession();
        Transaction tx = s.beginTransaction();

        String hqlBaja = "update AutoridadDepartamento set pasivo=true where user.username = :userName and pasivo = false ";
        int updateEntities = s.createQuery(hqlBaja)
                .setString("userName", userName)
                .executeUpdate();
        tx.commit();
        s.close();
        return updateEntities;
    }

    public List<Direccion> getDireccionesDisponiblesUsuario(String userName) {
        // Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("select d from Direccion d, AutoridadLaboratorio al where d.idDireccion not in " +
                                        "(select a.direccion.idDireccion FROM AutoridadDireccion as a where a.pasivo = false and a.user.username = :userName) " +
                "and d.laboratorio.codigo = al.laboratorio.codigo and al.user.username = :userName");

        query.setParameter("userName",userName);
        return query.list();
    }

    public List<Departamento> getDepartDisponiblesUsuario(String userName) {
        // Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("select d from Departamento d, AutoridadLaboratorio al where d.idDepartamento not in " +
                "(select a.departamento.idDepartamento FROM AutoridadDepartamento as a where a.pasivo = false and a.user.username = :userName) " +
                "and d.direccion.laboratorio.codigo = al.laboratorio.codigo and al.user.username = :userName");

        query.setParameter("userName",userName);
        return query.list();
    }
}
