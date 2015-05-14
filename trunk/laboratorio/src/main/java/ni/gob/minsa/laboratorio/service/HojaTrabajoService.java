package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.muestra.*;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.AutoridadLaboratorio;
import org.apache.commons.codec.language.Soundex;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by FIRSTICT on 4/17/2015.
 * V1.0
 */
@Service("hojaTrabajoService")
@Transactional
public class HojaTrabajoService {
    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;

    public void addHojaTrabajo(HojaTrabajo dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.save(dto);
            }
            else
                throw new Exception("Objeto hoja trabajo es NULL");
        }catch (Exception ex){
            ex.printStackTrace();
            throw ex;
        }
    }

    public void addDetalleHojaTrabajo(Mx_HojaTrabajo dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.save(dto);
            }
            else
                throw new Exception("Objeto mx_hoja trabajo es NULL");
        }catch (Exception ex){
            ex.printStackTrace();
            throw ex;
        }
    }

    public List<DaTomaMx> getTomaMxByHojaTrabajo(int numeroHoja, String codLaboratorio){
        String query = "select b from Mx_HojaTrabajo as a inner join a.tomaMx as b inner join a.hojaTrabajo as c " +
                "where c.numero =:numero and c.laboratorio.codigo = :codLaboratorio";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setInteger("numero", numeroHoja);
        q.setParameter("codLaboratorio",codLaboratorio);
        return q.list();
    }

    public HojaTrabajo getHojaTrabajo(int numeroHoja, String codLaboratorio){
        String query = "from HojaTrabajo as c " +
                "where c.numero =:numero and c.laboratorio.codigo = :codLaboratorio";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setInteger("numero", numeroHoja);
        q.setParameter("codLaboratorio",codLaboratorio);
        return (HojaTrabajo)q.uniqueResult();
    }

    public int obtenerNumeroHoja(){
        Random r = new Random();
        int numero = r.nextInt(9999999 - 1) + 1;
        String query = "from HojaTrabajo as a where a.numero =:numero";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setInteger("numero", numero);
        HojaTrabajo existeHoja  = (HojaTrabajo)q.uniqueResult();
        if (existeHoja!=null){
            return obtenerNumeroHoja();
        }
        return numero;
    }

    public Integer obtenerNumeroHoja(String codigoLaboratorio){
        Integer numero=null;
        String query = "select count(a.numero)+1 from HojaTrabajo as a where a.laboratorio.codigo = :codLab";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("codLab", codigoLaboratorio);
        Object oNumero  = q.uniqueResult();
        if (oNumero!=null){
            numero = Integer.valueOf(oNumero.toString());
        }
        return numero;
    }

    public List<HojaTrabajo> getTomaMxByFiltro(Integer numHoja, Date fechaInicio, Date fechaFin, String userName){
        Session session = sessionFactory.getCurrentSession();
        Soundex varSoundex = new Soundex();
        Criteria crit = session.createCriteria(HojaTrabajo.class, "hoja");
        //siempre se tomam las muestras que no estan anuladas
        crit.add(Subqueries.propertyIn("numero", DetachedCriteria.forClass(Mx_HojaTrabajo.class)
                .createAlias("hojaTrabajo","hojaTrabajo")
                .createAlias("hojaTrabajo.laboratorio","laboratorio")
                .createAlias("tomaMx","tomaMx")
                .add( Restrictions.and(
                                Restrictions.eq("tomaMx.anulada", false))
                )
                .setProjection(Property.forName("hojaTrabajo.numero"))));//y las ordenes en estado según filtro
        //se filtra por número de hoja
        if (numHoja!=null){
            crit.add( Restrictions.and(Restrictions.eq("hoja.numero", numHoja)));
        }
        //Se filtra por rango de fecha de registro hoja trabajo (se usan campos de filtro que hacen referencia a fecha toma mx)
        if (fechaInicio!=null && fechaFin!=null){
            crit.add( Restrictions.and(
                            Restrictions.between("hoja.fechaRegistro", fechaInicio,fechaFin))
            );
        }
        //se filtra que usuario tenga autorizado laboratorio al que se envio la muestra desde ALERTA
        if (userName!=null) {
            /*crit.add(Subqueries.propertyIn("laboratorio.codigo", DetachedCriteria.forClass(Mx_HojaTrabajo.class)
                    .createAlias("hojaTrabajo","hojaTrabajo")
                    .createAlias("hojaTrabajo.laboratorio","labo")
                    .createAlias("tomaMx","tomaMx")
                    .createAlias("tomaMx.envio","envioMx")
                    .add(Subqueries.propertyIn("envioMx.laboratorioDestino.codigo", DetachedCriteria.forClass(AutoridadLaboratorio.class)
                            .createAlias("laboratorio", "labautorizado")
                            .createAlias("user", "usuario")
                            .add(Restrictions.eq("pasivo",false)) //autoridad laboratorio activa
                            .add(Restrictions.and(Restrictions.eq("usuario.username",userName))) //usuario
                            .setProjection(Property.forName("labautorizado.codigo"))))
                    .setProjection(Property.forName("labo.codigo"))));*/
            crit.add(Subqueries.propertyIn("laboratorio.codigo", DetachedCriteria.forClass(AutoridadLaboratorio.class)
                            .createAlias("laboratorio", "labautorizado")
                            .createAlias("user", "usuario")
                            .add(Restrictions.eq("pasivo",false)) //autoridad laboratorio activa
                            .add(Restrictions.and(Restrictions.eq("usuario.username",userName))) //usuario
                            .setProjection(Property.forName("labautorizado.codigo"))));
        }

        return crit.list();
    }
}
