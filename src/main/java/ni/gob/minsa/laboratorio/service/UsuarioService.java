package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Servicio para el objeto de Unidades
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
}