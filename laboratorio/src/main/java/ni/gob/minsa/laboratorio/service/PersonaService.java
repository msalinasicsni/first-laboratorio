package ni.gob.minsa.laboratorio.service;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import javax.annotation.Resource;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.sun.media.jfxmedia.logging.Logger;

import ni.gob.minsa.laboratorio.domain.persona.*;

import ni.gob.minsa.laboratorio.domain.poblacion.Comunidades;
import ni.gob.minsa.laboratorio.domain.poblacion.Divisionpolitica;
import ni.gob.minsa.laboratorio.domain.poblacion.Paises;
import ni.gob.minsa.laboratorio.utilities.ConstantsSecurity;
import ni.gob.minsa.ciportal.dto.InfoResultado;
import ni.gob.minsa.ejbPersona.dto.Persona;
import ni.gob.minsa.ejbPersona.servicios.PersonaBTMService;
import ni.gob.minsa.ejbPersona.servicios.PersonaUTMService;
import org.apache.commons.codec.language.Soundex;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("personaService")
@Transactional
public class PersonaService {

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(PersonaService.class);
    private PersonaUTMService personaUTMService;
    private InitialContext initialContext;

    @SuppressWarnings("unchecked")
    public List<SisPersona> getPersonas(String filtro){
        try {
            filtro = URLDecoder.decode(filtro, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Session session = sessionFactory.getCurrentSession();
        if(filtro.matches("[0-9]*")){
            return session.createCriteria(SisPersona.class)
                    .add( Restrictions.or(
                                    Restrictions.eq("telefonoResidencia", filtro),
                                    Restrictions.eq("telefonoMovil", filtro))
                    )
                    .list();
        }else if(filtro.matches("[a-zA-Zí\\s]*")){
            Soundex varSoundex = new Soundex();
            Criteria crit = session.createCriteria(SisPersona.class);
            String[] partes = filtro.split(" ");
            String[] partesSnd = filtro.split(" ");
            for(int i=0;i<partes.length;i++){
                try{
                    partesSnd[i] = varSoundex.encode(partes[i]);
                }
                catch (IllegalArgumentException e){
                    partesSnd[i] = "0000";
                    e.printStackTrace();
                }
            }
            for(int i=0;i<partes.length;i++){
                Junction conditionGroup = Restrictions.disjunction();
                conditionGroup.add(Restrictions.ilike("primerNombre" , "%"+partes[i]+"%" ))
                        .add(Restrictions.ilike( "primerApellido" , "%"+partes[i]+"%"  ))
                        .add(Restrictions.ilike( "segundoNombre" , "%"+partes[i]+"%"  ))
                        .add(Restrictions.ilike( "segundoApellido" , "%"+partes[i]+"%"  ))
                        .add(Restrictions.ilike("sndNombre", "%"+partesSnd[i]+"%"));
                crit.add(conditionGroup);
            }

            return crit.list();
        }
        else{
            return session.createCriteria(SisPersona.class)
                    .add( Restrictions.or(
                                    Restrictions.eq("identificacionHse", filtro).ignoreCase(),
                                    Restrictions.eq("identificacion", filtro).ignoreCase())
                    )
                    .list();
        }
    }

    public SisPersona getPersona(long idPerson){
        // Retrieve session from Hibernate
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM SisPersona p where p.personaId = :idPerson");
        query.setParameter("idPerson", idPerson);
        SisPersona persona = (SisPersona) query.uniqueResult();
        return persona;

    }

    /**
     * @param dto
     * @throws Exception
     */
    public void saveOrUpdatePerson(SisPersona dto) throws Exception {
        try {
            if (dto != null) {
                Session session = sessionFactory.getCurrentSession();
                session.saveOrUpdate(dto);
            }
            else
                throw new Exception("");
        }catch (Exception ex){
            throw ex;
        }
    }


    public void iniciarTransaccion() throws Exception {
        this.initialContext = new InitialContext();
        this.personaUTMService = (PersonaUTMService) initialContext.lookup(ConstantsSecurity.EJB_BIN_PERSON_UTM);
        this.personaUTMService.iniciarTransaccion();
        logger.info("Se inicia transacción personaUTMService");
    }

    public void commitTransaccion() throws Exception {
        this.personaUTMService.commitTransaccion();
    }

    public void rollbackTransaccion() throws Exception {
        this.personaUTMService.rollbackTransaccion();
        logger.info("Se hiso rollback personaUTMService");
    }

    public void remover() throws Exception {
        this.personaUTMService.remover();
        this.initialContext.close();
        logger.info("Se cierra conexión personaUTMService");
    }

    public InfoResultado guardarPersona(Persona pPersona, String pUsuarioRegistra) {
        logger.info("Se guardar persona mediante componente");
        return this.personaUTMService.guardarPersona(pPersona, pUsuarioRegistra);
    }

    public Persona buscarPorId(long pIdPersona) throws Exception {

        InfoResultado infoResultado;
        InitialContext ctx;
        Persona persona = null;
        try{
            ctx = new InitialContext();
            PersonaBTMService servicio = (PersonaBTMService) ctx.lookup(ConstantsSecurity.EJB_BIN_PERSON_BTM);

            infoResultado = servicio.obtenerPorId(pIdPersona);
            if(infoResultado.isOk() && infoResultado.getObjeto()!=null){
                persona = (Persona) infoResultado.getObjeto();
            }else{
                throw new Exception("No se encontro persona"+infoResultado.getMensaje()+infoResultado.getMensajeDetalle());
            }
            ctx.close();
        }catch(Exception e){
            e.printStackTrace();
            throw new Exception(e);
        }

        return persona;
    }

    public SisPersona ensamblarObjetoSisPersona(Persona pPersona){
        SisPersona persona = null;

        if(pPersona!=null){
            persona = new SisPersona();

            Sexo sexo = null;
            Etnia etnia = null;
            Ocupacion ocupacion = null;
            TipoAsegurado tipoAsegurado = null;
            Identificacion identificacion = null;
            Escolaridad escolaridad = null;
            EstadoCivil estadoCivil = null;
            Paises paisNac = null;
            Divisionpolitica municipioNac = null;
            Divisionpolitica municipioResidencia = null;
            Comunidades comunidadResidencia = null;

            if(pPersona.getSexoCodigo()!=null){
                sexo = new Sexo();
                sexo.setCatalogoId(pPersona.getSexoId());
                sexo.setCodigo(pPersona.getSexoCodigo());
                sexo.setValor(pPersona.getSexoValor());
            }

            if(pPersona.getEscolaridadCodigo()!=null){
                escolaridad = new Escolaridad();
                escolaridad.setCatalogoId(pPersona.getEscolaridadId());
                escolaridad.setCodigo(pPersona.getEscolaridadCodigo());
                escolaridad.setValor(pPersona.getEscolaridadValor());
            }

            if(pPersona.getEstadoCivilCodigo()!=null){
                estadoCivil = new EstadoCivil();
                estadoCivil.setCatalogoId(pPersona.getEstadoCivilId());
                estadoCivil.setCodigo(pPersona.getEstadoCivilCodigo());
                estadoCivil.setValor(pPersona.getEstadoCivilValor());
            }

            if(pPersona.getEtniaCodigo()!=null){
                etnia = new Etnia();
                etnia.setCodigo(pPersona.getEtniaCodigo());
                etnia.setValor(pPersona.getEtniaValor());
                etnia.setCatalogoId(etnia.getCatalogoId());
            }

            if(pPersona.getIdentCodigo()!=null){
                identificacion = new Identificacion();
                identificacion.setCatalogoId(pPersona.getIdentId());
                identificacion.setCodigo(pPersona.getIdentCodigo());
                identificacion.setValor(pPersona.getIdentValor());
            }

            if(pPersona.getOcupacionCodigo()!=null){
                ocupacion = new Ocupacion();
                ocupacion.setOcupacionId(pPersona.getOcupacionId());
                ocupacion.setCodigo(pPersona.getOcupacionCodigo());
                ocupacion.setNombre(pPersona.getOcupacionValor());
            }

            if(pPersona.getMuniNacCodigoNac()!=null){
                municipioNac = new Divisionpolitica();
                municipioNac.setDivisionpoliticaId(pPersona.getMuniNacId());
                municipioNac.setCodigoNacional(pPersona.getMuniNacCodigoNac());
                municipioNac.setNombre(pPersona.getMuniNacNombre());

            }

            if(pPersona.getMuniResiCodigoNac()!=null){
                municipioResidencia = new Divisionpolitica();
                municipioResidencia.setDivisionpoliticaId(pPersona.getMuniResiId());
                municipioResidencia.setCodigoNacional(pPersona.getMuniResiCodigoNac());
                municipioResidencia.setNombre(pPersona.getMuniResiNombre());
            }

            if(pPersona.getComuResiCodigo()!=null){
                comunidadResidencia = new Comunidades();
                comunidadResidencia.setComunidadId((int)pPersona.getComuResiId());
                comunidadResidencia.setCodigo(pPersona.getComuResiCodigo());
                comunidadResidencia.setNombre(pPersona.getComuResiNombre());
            }

            if(pPersona.getPaisNacCodigoAlfados()!=null){
                paisNac = new Paises();
                paisNac.setPaisId(pPersona.getPaisNacId());
                paisNac.setCodigoAlfados(pPersona.getPaisNacCodigoAlfados());
                paisNac.setNombre(pPersona.getPaisNacNombre());
            }

            persona.setPersonaId(pPersona.getPersonaId());
            persona.setFechaNacimiento(pPersona.getFechaNacimiento());
            persona.setPrimerNombre(pPersona.getPrimerNombre());
            persona.setPrimerApellido(pPersona.getPrimerApellido());
            persona.setSegundoNombre(pPersona.getSegundoNombre());
            persona.setSegundoApellido(pPersona.getSegundoApellido());
            persona.setDireccionResidencia(pPersona.getDireccionResi());
            persona.setIdentificacionHse(pPersona.getIdentHse());
            persona.setIdentificacion(pPersona.getIdentNumero());
            persona.setNumeroAsegurado(pPersona.getAseguradoNumero());
            persona.setTelefonoResidencia(pPersona.getTelefonoResi());
            persona.setTelefonoMovil(pPersona.getTelefonoMovil());
            persona.setSexo(sexo);
            persona.setEtnia(etnia);
            persona.setOcupacion(ocupacion);
            persona.setTipoAsegurado(tipoAsegurado);
            persona.setTipoIdentificacion(identificacion);
            persona.setEscolaridad(escolaridad);
            persona.setEstadoCivil(estadoCivil);
            persona.setPaisNacimiento(paisNac);
            persona.setMunicipioNacimiento(municipioNac);
            persona.setMunicipioResidencia(municipioResidencia);
            persona.setComunidadResidencia(comunidadResidencia);
            persona.setConfirmado(pPersona.getVerificados());
        }

        return persona;
    }

    public Persona ensamblarObjetoPersona(SisPersona pPersona){
        Persona persona = null;

        if(pPersona!=null){
            persona = new Persona();

            persona.setPersonaId(pPersona.getPersonaId());
            persona.setFechaNacimiento(pPersona.getFechaNacimiento());
            persona.setPrimerNombre(pPersona.getPrimerNombre());
            persona.setPrimerApellido(pPersona.getPrimerApellido());
            persona.setSegundoNombre(pPersona.getSegundoNombre());
            persona.setSegundoApellido(pPersona.getSegundoApellido());
            persona.setDireccionResi(pPersona.getDireccionResidencia());
            persona.setIdentHse(pPersona.getIdentificacionHse());
            persona.setIdentNumero(pPersona.getIdentificacion());
            persona.setAseguradoNumero(pPersona.getNumeroAsegurado());
            persona.setTelefonoResi(pPersona.getTelefonoResidencia());
            persona.setTelefonoMovil(pPersona.getTelefonoMovil());
            persona.setSexoCodigo(pPersona.getSexo()!=null ? pPersona.getSexo().getCodigo() : null);
            persona.setEtniaCodigo(pPersona.getEtnia()!=null ?pPersona.getEtnia().getCodigo() : null);
            persona.setOcupacionCodigo(pPersona.getOcupacion()!=null ? pPersona.getOcupacion().getCodigo() : null);
            persona.setTipoAsegCodigo(pPersona.getTipoAsegurado()!=null? pPersona.getTipoAsegurado().getCodigo() : null);
            persona.setIdentCodigo(pPersona.getTipoIdentificacion()!=null ? pPersona.getTipoIdentificacion().getCodigo() : null);
            persona.setEscolaridadCodigo(pPersona.getEscolaridad()!=null ? pPersona.getEscolaridad().getCodigo() : null);
            persona.setEstadoCivilCodigo(pPersona.getEstadoCivil()!=null ? pPersona.getEstadoCivil().getCodigo() : null);
            persona.setPaisNacCodigoAlfados(pPersona.getPaisNacimiento()!=null ? pPersona.getPaisNacimiento().getCodigoAlfados() : null);
            persona.setMuniNacCodigoNac(pPersona.getMunicipioNacimiento()!=null ? pPersona.getMunicipioNacimiento().getCodigoNacional() : null);
            persona.setMuniResiCodigoNac(pPersona.getMunicipioResidencia()!=null ? pPersona.getMunicipioResidencia().getCodigoNacional() : null);
            persona.setComuResiCodigo(pPersona.getComunidadResidencia()!=null ? pPersona.getComunidadResidencia().getCodigo() : null);
        }

        return persona;
    }
}