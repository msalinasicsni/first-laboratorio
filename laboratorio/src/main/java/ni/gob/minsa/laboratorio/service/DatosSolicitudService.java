package ni.gob.minsa.laboratorio.service;

import ni.gob.minsa.laboratorio.domain.concepto.Catalogo_Lista;
import ni.gob.minsa.laboratorio.domain.muestra.DatoSolicitud;
import ni.gob.minsa.laboratorio.domain.muestra.DatoSolicitudDetalle;
import ni.gob.minsa.laboratorio.domain.resultados.DetalleResultadoFinal;
import ni.gob.minsa.laboratorio.utilities.dto.DatosCovidViajeroDTO;
import ni.gob.minsa.laboratorio.utilities.reportes.DetalleDatosRecepcion;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by souyen-ics.
 */
@Service("datosSolicitudService")
@Transactional
public class DatosSolicitudService {

    private Logger logger = LoggerFactory.getLogger(DatosSolicitudService.class);

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;

    @Autowired
    @Qualifier(value = "resultadoFinalService")
    private ResultadoFinalService resultadoFinalService;

    public DatosSolicitudService() {
    }


    public List<DatoSolicitud> getDatosRecepcionDxByIdSolicitud(Integer idSolicitud){
        String query = "from DatoSolicitud as a where a.diagnostico.idDiagnostico = :idSolicitud order by orden asc";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idSolicitud", idSolicitud);
        return q.list();
    }

    public DatoSolicitud getDatoRecepcionSolicitudById(Integer idConceptoSol){
        String query = "from DatoSolicitud as a where idConceptoSol =:idConceptoSol";
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idConceptoSol", idConceptoSol);
        return (DatoSolicitud)q.uniqueResult();
    }

    /**
     * Agrega o Actualiza un Registro de DatoSolicitud
     * @param dto Objeto a actualizar
     * @throws Exception
     */
    public void saveOrUpdateDatoRecepcion(DatoSolicitud dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.saveOrUpdate(dto);
            }
            else
                throw new Exception("Objeto DatoSolicitud es NULL");
        }catch (Exception ex){
            logger.error("Error al actualizar o agregar DatoSolicitud",ex);
            throw ex;
        }
    }

    /**
     * Agrega o Actualiza un Registro de ConceptoSolicitud
     * @param dto Objeto a actualizar
     * @throws Exception
     */
    public void saveOrUpdateDetalleDatoRecepcion(DatoSolicitudDetalle dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.saveOrUpdate(dto);
            }
            else
                throw new Exception("Objeto DatoSolicitudDetalle es NULL");
        }catch (Exception ex){
            logger.error("Error al actualizar o agregar DatoSolicitudDetalle",ex);
            throw ex;
        }
    }

    public Integer deleteDetallesDatosRecepcionByTomaMx(String idTomaMx) {
        // Retrieve session from Hibernate
        Session s = sessionFactory.openSession();
        Transaction tx = s.beginTransaction();

        String hqlDelete = "delete DatoSolicitudDetalle dato where dato.solicitudDx in (from DaSolicitudDx where idTomaMx.idTomaMx = :idTomaMx)";
        int deletedEntities = s.createQuery( hqlDelete )
                .setString("idTomaMx", idTomaMx)
                .executeUpdate();
        tx.commit();
        s.close();
        return deletedEntities;
    }

    public List<Catalogo_Lista> getCatalogoListaConceptoByIdDx(Integer idDx) throws Exception {
        String query = "Select a from Catalogo_Lista as a inner join a.idConcepto tdl , DatoSolicitud as r inner join r.concepto tdc " +
                "where a.pasivo = false and tdl.idConcepto = tdc.idConcepto and r.diagnostico.idDiagnostico =:idDx" +
                " order by  a.valor";

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(query);
        q.setParameter("idDx",idDx);
        return q.list();
    }

    public List<DatoSolicitud> getDatosRecepcionActivosDxByIdSolicitud(Integer idSolicitud){
        String query = "from DatoSolicitud as a where a.diagnostico.idDiagnostico = :idSolicitud and pasivo = false order by orden asc";

        Session session = sessionFactory.getCurrentSession();

        Query q = session.createQuery(query);
        q.setParameter("idSolicitud", idSolicitud);
        return q.list();
    }

    public List<DatoSolicitudDetalle> getDatosSolicitudDetalleBySolicitud(String idSolicitud){
        Session session = sessionFactory.getCurrentSession();
        String query = "select a from DatoSolicitudDetalle as a inner join a.solicitudDx as r where r.idSolicitudDx = :idSolicitud ";
        Query q = session.createQuery(query);
        q.setParameter("idSolicitud", idSolicitud);
        return q.list();
    }

    public DatoSolicitudDetalle getDatoSolicitudDetalleById(String idDetalle){
        Session session = sessionFactory.getCurrentSession();
        String query = "select a from DatoSolicitudDetalle as a where a.idDetalle = :idDetalle ";
        Query q = session.createQuery(query);
        q.setParameter("idDetalle", idDetalle);
        return (DatoSolicitudDetalle)q.uniqueResult();
    }

    public List<DetalleDatosRecepcion> getDetalleDatosRecepcionByIdSolicitud(String idSolicitud){
        Session session = sessionFactory.getCurrentSession();
        String query = "select a.idDetalle as idDetalle, a.valor as valor, r.idSolicitudDx as solicitudDx, da.idConceptoSol as datoSolicitud, da.nombre as nombre, " +
                "da.concepto.tipo.codigo as tipoConcepto  " +
                "from DatoSolicitudDetalle as a inner join a.solicitudDx as r inner join a.datoSolicitud as da where r.idSolicitudDx = :idSolicitud ";
        Query q = session.createQuery(query);
        q.setParameter("idSolicitud", idSolicitud);
        q.setResultTransformer(Transformers.aliasToBean(DetalleDatosRecepcion.class));
        return q.list();
    }

    public List<DetalleDatosRecepcion> getDetalleDatosRecepcionByIdMx(String idTomaMx){
        Session session = sessionFactory.getCurrentSession();
        String query = "select a.idDetalle as idDetalle, a.valor as valor, da.nombre as nombre, da.concepto.tipo.codigo as tipoConcepto " +
                "from DatoSolicitudDetalle as a inner join a.solicitudDx as r inner join a.datoSolicitud as da inner join r.idTomaMx as mx where mx.idTomaMx = :idTomaMx " +
                "order by da.orden";
        Query q = session.createQuery(query);
        q.setParameter("idTomaMx", idTomaMx);
        q.setResultTransformer(Transformers.aliasToBean(DetalleDatosRecepcion.class));
        return q.list();
    }

    public DetalleDatosRecepcion getDetalleDatosRecepcionById(String idDetalle){
        Session session = sessionFactory.getCurrentSession();
        String query = "select a.idDetalle as idDetalle, a.valor as valor, r.idSolicitudDx as solicitudDx, da.idConceptoSol as datoSolicitud, da.nombre as nombre, da.requerido as requerido, " +
                "da.concepto.tipo.codigo as tipoConcepto, da.descripcion as  descripcion " +
                "from DatoSolicitudDetalle as a inner join a.solicitudDx as r inner join a.datoSolicitud as da where a.idDetalle = :idDetalle ";
        Query q = session.createQuery(query);
        q.setParameter("idDetalle", idDetalle);
        q.setResultTransformer(Transformers.aliasToBean(DetalleDatosRecepcion.class));
        return (DetalleDatosRecepcion)q.uniqueResult();
    }

    public List<DatoSolicitud> getDatosRecepcionActivosDxByIdSolicitudes(String idSolicitudes){
        String query = "from DatoSolicitud as a where a.diagnostico.idDiagnostico in ("+idSolicitudes+") and pasivo = false order by orden asc";

        Session session = sessionFactory.getCurrentSession();

        Query q = session.createQuery(query);
        return q.list();
    }

    public String getNumeroFactura(String idSolicitud){
        List<DetalleDatosRecepcion> resFinalList = this.getDetalleDatosRecepcionByIdSolicitud(idSolicitud);
        String numFactura="";
        for(DetalleDatosRecepcion res: resFinalList){
            if (res.getNombre().toLowerCase().contains("factura"))
                if (res.getTipoConcepto().equals("TPDATO|LIST")) {
                    Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(res.getValor());
                    numFactura+=cat_lista.getEtiqueta();
                }else if (res.getTipoConcepto().equals("TPDATO|LOG")) {
                    String valorBoleano = (Boolean.valueOf(res.getValor())?"lbl.yes":"lbl.no");
                    numFactura+=valorBoleano;
                } else {
                    numFactura+=res.getValor().toUpperCase();
                }
        }
        return numFactura;
    }

    public String getIdentificacionViajero(String idTomaMx, String identificacionPersona){
        List<DetalleDatosRecepcion> resFinalList = this.getDetalleDatosRecepcionByIdMx(idTomaMx);
        String identificacion="";
        for(DetalleDatosRecepcion res: resFinalList){
            if (res.getNombre().contains("ID"))
                if (res.getTipoConcepto().equals("TPDATO|LIST")) {
                    Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(res.getValor());
                    identificacion+=cat_lista.getEtiqueta();
                }else if (res.getTipoConcepto().equals("TPDATO|LOG")) {
                    String valorBoleano = (Boolean.valueOf(res.getValor())?"lbl.yes":"lbl.no");
                    identificacion+=valorBoleano;
                } else {
                    identificacion+=res.getValor().toUpperCase();
                }
        }
        if (identificacion.isEmpty()) identificacion = identificacionPersona;
        return identificacion;
    }

    public DatosCovidViajeroDTO getDatosCovidViajero(String idSolicitud, String identificacionPersona) {
        DatosCovidViajeroDTO datos = new DatosCovidViajeroDTO();
        List<DetalleDatosRecepcion> resFinalList = this.getDetalleDatosRecepcionByIdSolicitud(idSolicitud);
        for(DetalleDatosRecepcion res: resFinalList){
            if (res.getNombre().toLowerCase().contains("lugar")) {
                if (res.getTipoConcepto().equals("TPDATO|LIST")) {
                    Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(res.getValor());
                    datos.setLugarDondeViaja(cat_lista.getEtiqueta());
                } else if (res.getTipoConcepto().equals("TPDATO|LOG")) {
                    String valorBoleano = (Boolean.valueOf(res.getValor()) ? "lbl.yes" : "lbl.no");
                    datos.setLugarDondeViaja(valorBoleano);
                } else {
                    datos.setLugarDondeViaja(res.getValor().toUpperCase());
                }
            }else if (res.getNombre().toLowerCase().contains("factura")) {
                if (res.getTipoConcepto().equals("TPDATO|LIST")) {
                    Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(res.getValor());
                    datos.setNumeroFactura(cat_lista.getEtiqueta());
                } else if (res.getTipoConcepto().equals("TPDATO|LOG")) {
                    String valorBoleano = (Boolean.valueOf(res.getValor()) ? "lbl.yes" : "lbl.no");
                    datos.setNumeroFactura(valorBoleano);
                } else {
                    datos.setNumeroFactura(res.getValor().toUpperCase());
                }
            } else if (res.getNombre().contains("ID")) {
                if (res.getTipoConcepto().equals("TPDATO|LIST")) {
                    Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(res.getValor());
                    datos.setIdentificacion(cat_lista.getEtiqueta());
                } else if (res.getTipoConcepto().equals("TPDATO|LOG")) {
                    String valorBoleano = (Boolean.valueOf(res.getValor()) ? "lbl.yes" : "lbl.no");
                    datos.setIdentificacion(valorBoleano);
                } else {
                    datos.setIdentificacion(res.getValor().toUpperCase());
                }
            } else if (res.getNombre().toLowerCase().contains("idioma")) {
                if (res.getTipoConcepto().equals("TPDATO|LIST")) {
                    Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(res.getValor());
                    datos.setIdioma(cat_lista.getEtiqueta());
                }else {
                    datos.setIdioma(res.getValor().toUpperCase());
                }
            } else if (res.getNombre().toLowerCase().contains("modalidad")) {
                if (res.getTipoConcepto().equals("TPDATO|LIST")) {
                    Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(res.getValor());
                    datos.setModalidad(cat_lista.getEtiqueta());
                }else {
                    datos.setModalidad(res.getValor().toUpperCase());
                }
            }
        }
        if (datos.getIdentificacion() == null || datos.getIdentificacion().isEmpty()) datos.setIdentificacion(identificacionPersona);
        return datos;
    }

    public String getIdiomaResultadoViajero(String idTomaMx){
        List<DetalleDatosRecepcion> resFinalList = this.getDetalleDatosRecepcionByIdMx(idTomaMx);
        String idioma="";
        for(DetalleDatosRecepcion res: resFinalList){
            if (res.getNombre().toLowerCase().contains("idioma"))
                if (res.getTipoConcepto().equals("TPDATO|LIST")) {
                    Catalogo_Lista cat_lista = resultadoFinalService.getCatalogoLista(res.getValor());
                    idioma = cat_lista.getEtiqueta();
                }else {
                    idioma = res.getValor().toUpperCase();
                }
        }
        if (idioma.toLowerCase().contains("esp")) idioma = "ES";
        else if (idioma.toLowerCase().contains("ing")) idioma = "EN";
        else idioma = "ES"; //por defecto Español
        return idioma;
    }
}
