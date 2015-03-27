package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.Authority;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.Rol;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.User;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Servicio para administrar seguridad local
 *
 * @author Miguel Salinas
 */
@Service("usuarioService")
@Transactional
public class UsuarioService {

    @Resource(name="sessionFactory")
    public SessionFactory sessionFactory;

    public SessionFactory getSessionFactory(){
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory){
        if(this.sessionFactory == null){
            this.sessionFactory = sessionFactory;
        }
    }

    /**
     * @param idUsuario Id para obtener un objeto en especifico del tipo <code>Usuarios</code>
     * @return retorna un objeto filtrado del tipo <code>Usuarios</code>
     * @throws Exception
     */
    public Usuarios getUsuarioById(Integer idUsuario) throws Exception {
        Session session = sessionFactory.getCurrentSession();
        String query = "from Usuarios as a where usuarioId=:idUsuario";
        Query q = session.createQuery(query);
        q.setInteger("idUsuario", idUsuario);
        return  (Usuarios)q.uniqueResult();
    }

    /**
     * Regresa todos los usuarios
     *
     * @return una lista de <code>User</code>(s)
     */

    @SuppressWarnings("unchecked")
    public List<User> getUsers() {
        // Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        // Create a Hibernate query (HQL)
        Query query = session.createQuery("FROM User");
        // Retrieve all
        return  query.list();
    }


    /**
     * Regresa un User
     *
     * @return un <code>User</code>
     */

    public User getUser(String username) {
        // Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM User u where " +
                "u.username = '" + username + "'");
        return (User) query.uniqueResult();
    }

    /**
     * Verifica un User
     *
     * @return boolean
     */

    public Boolean checkUser(String username) {
        // Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM User u where " +
                "u.username = '" + username + "'");
        User user = (User) query.uniqueResult();
        return user!=null;
    }

    /**
     * Agrega un user
     *
     *
     */
    public void addUser(User user) {
        Session session = sessionFactory.getCurrentSession();
        /*UserLog uLog = new UserLog(new Date(), user.getUsername(), user.getPassword(),
                user.getCompleteName(), user.getEmail(), user.getEnabled(), user.getUsuario());*/
        session.save(user);
        //session.save(uLog);
    }

    /**
     * Agrega un rol
     *
     *
     */
    public void addAuthority(Authority auth) {
        Session session = sessionFactory.getCurrentSession();
        /*RolLog authLog = new RolLog(new Date(), auth.getAuthId().getUsername(),
                auth.getAuthId().getAuthority(), auth.getUser().getUsuario());*/
        session.save(auth);
        //session.save(authLog);
    }

    /**
     * Actualiza un user
     *
     *
     */
    public void updateUser(User user) {
        Session session = sessionFactory.getCurrentSession();
        //UserLog uLog = new UserLog(new Date(), user.getUsername(), user.getPassword(),
          //      user.getCompleteName(), user.getEmail(), user.getEnabled(), user.getUsuario());
        session.update(user);
        //session.save(uLog);
    }

    /**
     * Regresa todos los niveles usuarios
     *
     * @return una lista de <code>Nivel</code>(es)
     */

    @SuppressWarnings("unchecked")
    public List<Rol> getRoles() {
        // Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        // Create a Hibernate query (HQL)
        Query query = session.createQuery("FROM Rol");
        // Retrieve all
        return  query.list();
    }

    /**
     * Borra todos los roles
     *
     *
     */

    public Integer deleteRoles(String userName) {
        // Retrieve session from Hibernate
        Session s = sessionFactory.openSession();
        Transaction tx = s.beginTransaction();

        String hqlDelete = "delete Authority auth where auth.authId.username = :userName and" +
                " auth.authId.authority <> 'ROLE_ADMIN'";
        int deletedEntities = s.createQuery( hqlDelete )
                .setString( "userName", userName )
                .executeUpdate();
        tx.commit();
        s.close();
        return deletedEntities;
    }


    /**
     * Borra un rol asociado a un usuario
     *
     *
     */

    public Integer deleteRoleAdmin(String userName, String role) {
        // Retrieve session from Hibernate
        Session s = sessionFactory.openSession();
        Transaction tx = s.beginTransaction();

        String hqlDelete = "delete Authority auth where auth.authId.username = :userName and" +
                " auth.authId.authority = :role";
        int deletedEntities = s.createQuery( hqlDelete )
                .setString("userName", userName)
                .setString( "role", role )
                .executeUpdate();
        tx.commit();
        s.close();
        return deletedEntities;
    }
}