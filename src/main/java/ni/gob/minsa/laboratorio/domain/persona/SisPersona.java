package ni.gob.minsa.laboratorio.domain.persona;


import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.*;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;
import ni.gob.minsa.laboratorio.domain.poblacion.Comunidades;
import ni.gob.minsa.laboratorio.domain.poblacion.Divisionpolitica;
import ni.gob.minsa.laboratorio.domain.poblacion.Paises;


@NamedQueries({
	@NamedQuery(
			name = "personaPorId",
			query = "select per from SisPersona per where per.personaId = :pId"
	),
	@NamedQuery(
			name = "personaPorHse",
			query = "select per from SisPersona per where per.identificacionHse = :pIdentificacionHse"
	),
	@NamedQuery(
			name = "personaPorIdent",
			query = "select per from SisPersona per where per.identificacion = :pIdentificacion"
	),
	@NamedQuery(
			name = "personaPorNumAsegurado",
			query = "select per from SisPersona per where per.numeroAsegurado = :pNumAsegurado"
	)
})

@Entity  @Indexed
@Table(name="SIS_PERSONAS",schema="ALERTA")

public class SisPersona  implements java.io.Serializable {


    private static final long serialVersionUID = 1L;
	
    private long personaId;
    private String identificacionHse;
    private Identificacion tipoIdentificacion;
    private String identificacion;
    private Date fechaNacimiento;
    private String primerNombre;
    private String primerApellido;
    private String segundoNombre;
    private String segundoApellido;
    private String direccionResidencia;
    private String telefonoResidencia;
    private String telefonoMovil;
    private String numeroAsegurado;
    private Etnia etnia;
    private Escolaridad escolaridad;
    private EstadoCivil estadoCivil;
    private TipoAsegurado tipoAsegurado;
    private Sexo sexo;
    private Ocupacion ocupacion;
    private Divisionpolitica municipioResidencia;
    private Comunidades comunidadResidencia;
    private Paises paisNacimiento;
    private Divisionpolitica municipioNacimiento;
    private String sndNombre;
    private boolean confirmado;
    private Timestamp fechaRegistro;
    private String usuarioRegistro;
    private long fallecida;
    private char pasivo;
    private String idRegistroCentral;
    private String email;
    private String fax;
    
  
   
    public SisPersona() {
    }

	
    public SisPersona(long personaId, 
    		String primerNombre, 
    		String primerApellido, 
    		long comunidadResidenciaId, 
    		Timestamp fechaRegistro, 
    		String usuarioRegistro, 
    		boolean confirmado) {
    	
        this.personaId = personaId;
        this.primerNombre = primerNombre;
        this.primerApellido = primerApellido;
        this.fechaRegistro = fechaRegistro;
        this.usuarioRegistro = usuarioRegistro;
        this.confirmado = confirmado;
    }
    
    public SisPersona(
            long personaId,
    	     String identificacionHse,
    	     Identificacion tipoIdentificacion,
    	     String identificacion,
    	     Date fechaNacimiento,
    	     String primerNombre,
    	     String primerApellido,
    	     String segundoNombre,
    	     String segundoApellido,
    	     String direccionResidencia,
    	     String telefonoResidencia,
    	     String telefonoMovil,
    	     String numeroAsegurado,
    	     Etnia etnia,
    	     Escolaridad escolaridad,
    	     EstadoCivil estadocivil,
    	     TipoAsegurado tipoasegurado,
    	     Sexo sexo,
    	     Ocupacion ocupacion,
    	     Divisionpolitica municipioResidencia,
    	     Comunidades comunidadResidencia,
    	     Paises paisNacimiento,
    	     Divisionpolitica municipioNacimiento,
    	     boolean confirmado,
    	     Timestamp fechaRegistro,
    	     String usuarioRegistro) {
    	   	   	
    	 this.personaId=personaId;
	     this.identificacionHse=identificacionHse;
	     this.tipoIdentificacion=tipoIdentificacion;
	     this.identificacion=identificacion;
	     this.fechaNacimiento=fechaNacimiento;
	     this.primerNombre=primerNombre;
	     this.primerApellido=primerApellido;
	     this.segundoNombre=segundoNombre;
	     this.segundoApellido=segundoApellido;
	     this.direccionResidencia=direccionResidencia;
	     this.telefonoResidencia=telefonoResidencia;
	     this.telefonoMovil=telefonoMovil;
	     this.numeroAsegurado=numeroAsegurado;
	     this.etnia=etnia;
	     this.escolaridad=escolaridad;
	     this.estadoCivil=estadocivil;
	     this.tipoAsegurado=tipoasegurado;
	     this.sexo=sexo;
	     this.ocupacion=ocupacion;
	     this.municipioResidencia=municipioResidencia;
	     this.comunidadResidencia=comunidadResidencia;
	     this.paisNacimiento=paisNacimiento;
	     this.municipioNacimiento=municipioNacimiento;
	     this.confirmado=confirmado;
	     this.fechaRegistro=fechaRegistro;
	     this.usuarioRegistro=usuarioRegistro;  
    }
   
    @Id 
    @Column(name="PERSONA_ID", nullable=false, precision=10, scale=0)
    public long getPersonaId() {
        return this.personaId;
    }
    
    public void setPersonaId(long personaId) {
        this.personaId = personaId;
    }
    
    @Column(name="IDENTIFICACION_HSE", length=50)
    public String getIdentificacionHse() {
        return this.identificacionHse;
    }
    
    public void setIdentificacionHse(String identificacionHse) {
        this.identificacionHse = identificacionHse;
    }
    
    @Column(name="IDENTIFICACION", length=20)
    public String getIdentificacion() {
        return this.identificacion;
    }
    
    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }
    @Temporal(TemporalType.DATE)
    @Column(name="FECHA_NACIMIENTO", length=7)
    public Date getFechaNacimiento() {
        return this.fechaNacimiento;
    }
    
    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
    
    @Field
    @Column(name="PRIMER_NOMBRE", nullable=false, length=50)
    public String getPrimerNombre() {
        return this.primerNombre;
    }
    
    public void setPrimerNombre(String primerNombre) {
        this.primerNombre = primerNombre;
    }
    
    @Field
    @Column(name="PRIMER_APELLIDO", nullable=false, length=50)
    public String getPrimerApellido() {
        return this.primerApellido;
    }
    
    public void setPrimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }
    
    @Field
    @Column(name="SEGUNDO_NOMBRE", length=50)
    public String getSegundoNombre() {
        return this.segundoNombre;
    }
    
    public void setSegundoNombre(String segundoNombre) {
        this.segundoNombre = segundoNombre;
    }
    
    @Field
    @Column(name="SEGUNDO_APELLIDO", length=50)
    public String getSegundoApellido() {
        return this.segundoApellido;
    }
    
    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }
    
    @Column(name="DIRECCION_RESIDENCIA", length=100)
    public String getDireccionResidencia() {
        return this.direccionResidencia;
    }
    
    public void setDireccionResidencia(String direccionResidencia) {
        this.direccionResidencia = direccionResidencia;
    }
    
    @Column(name="TELEFONO_RESIDENCIA", length=20)
    public String getTelefonoResidencia() {
        return this.telefonoResidencia;
    }
    
    public void setTelefonoResidencia(String telefonoResidencia) {
        this.telefonoResidencia = telefonoResidencia;
    }
    
    @Column(name="TELEFONO_MOVIL", length=20)
    public String getTelefonoMovil() {
        return this.telefonoMovil;
    }
    
    public void setTelefonoMovil(String telefonoMovil) {
        this.telefonoMovil = telefonoMovil;
    }
    
    @Column(name="NUMERO_ASEGURADO", length=20)
    public String getNumeroAsegurado() {
        return this.numeroAsegurado;
    }
    
    public void setNumeroAsegurado(String numeroAsegurado) {
        this.numeroAsegurado = numeroAsegurado;
    }
    
    @ManyToOne(fetch=FetchType.LAZY,targetEntity=Catalogo.class)
    @JoinColumn(name="CODIGO_TIPOIDENTIFICACION",referencedColumnName="CODIGO", nullable=true)
    public Identificacion getTipoIdentificacion() {
        return this.tipoIdentificacion;
    }
    
    public void setTipoIdentificacion(Identificacion tipoIdentificacion) {
        this.tipoIdentificacion = tipoIdentificacion;
    }
    
    @ManyToOne(fetch=FetchType.LAZY,targetEntity=Catalogo.class)
    @JoinColumn(name="CODIGO_ETNIA",referencedColumnName="CODIGO", nullable=true)
    public Etnia getEtnia() {
        return this.etnia;
    }
    
    public void setEtnia(Etnia etnia) {
        this.etnia = etnia;
    }
    
    @ManyToOne(fetch=FetchType.LAZY,targetEntity=Catalogo.class)
    @JoinColumn(name="CODIGO_ESCOLARIDAD",referencedColumnName="CODIGO", nullable=true)
    public Escolaridad getEscolaridad() {
        return this.escolaridad;
    }
    
    public void setEscolaridad(Escolaridad escolaridad) {
        this.escolaridad = escolaridad;
    }
       
    @ManyToOne(fetch=FetchType.LAZY,targetEntity=Catalogo.class)
    @JoinColumn(name="CODIGO_SEXO",referencedColumnName="CODIGO", nullable=true)
    public Sexo getSexo() {
        return this.sexo;
    }
    
    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }
    
    @ManyToOne(fetch=FetchType.LAZY,targetEntity=Divisionpolitica.class)
    @JoinColumn(name="CODIGO_MUNICIPIO_RESIDENCIA", referencedColumnName="CODIGO_NACIONAL",nullable=true)
    public Divisionpolitica getMunicipioResidencia() {
        return this.municipioResidencia;
    }
    
    public void setMunicipioResidencia(Divisionpolitica municipioResidencia) {
        this.municipioResidencia = municipioResidencia;
    }

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToOne(fetch=FetchType.LAZY,targetEntity=Ocupacion.class)
    @JoinColumn(name="CODIGO_OCUPACION",referencedColumnName="CODIGO", nullable=true)
    public Ocupacion getOcupacion() {
        return this.ocupacion;
    }
    
    public void setOcupacion(Ocupacion ocupacion) {
        this.ocupacion = ocupacion;
    }


    @ManyToOne(fetch=FetchType.LAZY,targetEntity=Comunidades.class)
    @JoinColumn(name="CODIGO_COMUNIDAD_RESIDENCIA", referencedColumnName="CODIGO", nullable=true)
    public Comunidades getComunidadResidencia() {
        return this.comunidadResidencia;
    }
    
    public void setComunidadResidencia(Comunidades comunidadResidencia) {
        this.comunidadResidencia = comunidadResidencia;
    }
        
    @ManyToOne(fetch=FetchType.LAZY,targetEntity=Divisionpolitica.class)
    @JoinColumn(name="CODIGO_MUNICIPIO_NACIMIENTO", referencedColumnName="CODIGO_NACIONAL",nullable=true)
    public Divisionpolitica getMunicipioNacimiento() {
        return this.municipioNacimiento;
    }
    
    public void setMunicipioNacimiento(Divisionpolitica municipioResidencia) {
        this.municipioNacimiento = municipioResidencia;
    }
    
    @ManyToOne(fetch=FetchType.LAZY,targetEntity=Paises.class)
    @JoinColumn(name="CODIGO_PAIS_NACIMIENTO",referencedColumnName="CODIGO_ALFADOS", nullable=true)
    public Paises getPaisNacimiento() {
        return this.paisNacimiento;
    }
    
    public void setPaisNacimiento(Paises paisNacimiento) {
        this.paisNacimiento = paisNacimiento;
    }
    
    @Column(name="SND_NOMBRE", length=200)
    public String getSndNombre() {
        return this.sndNombre;
    }
    
    public void setSndNombre(String sndNombre) {
        this.sndNombre = sndNombre;
    }
    
    @Column(name="CONFIRMADO", nullable=false, precision=1, scale=0)
    public boolean isConfirmado() {
        return this.confirmado;
    }
    
    public void setConfirmado(boolean confirmado) {
        this.confirmado = confirmado;
    }
    
    @Column(name="FECHA_REGISTRO", nullable=false)
    public Timestamp getFechaRegistro() {
        return this.fechaRegistro;
    }
    
    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
    
    @Column(name="USUARIO_REGISTRO", nullable=false, length=100)
    public String getUsuarioRegistro() {
        return this.usuarioRegistro;
    }
    
    public void setUsuarioRegistro(String usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }

    @ManyToOne(fetch=FetchType.LAZY,targetEntity=Catalogo.class)
    @JoinColumn(name="CODIGO_ESTADOCIVIL",referencedColumnName="CODIGO", nullable=true)
    public EstadoCivil getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(EstadoCivil estadocivil) {
        this.estadoCivil = estadocivil;
    }

    @Column(name="FALLECIDA",precision=1)
    public long getFallecida() {
        return fallecida;
    }

    public void setFallecida(long fallecida) {
        this.fallecida = fallecida;
    }

    @ManyToOne(fetch=FetchType.LAZY,targetEntity=Catalogo.class)
    @JoinColumn(name="CODIGO_TIPOASEGURADO",referencedColumnName="CODIGO", nullable=true)    
    public TipoAsegurado getTipoAsegurado() {
        return tipoAsegurado;
    }

    public void setTipoAsegurado(TipoAsegurado tipoasegurado) {
        this.tipoAsegurado = tipoasegurado;
    }
    
    

   /* @OneToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch=FetchType.LAZY,mappedBy = "personaFallecido")
    public Defuncion getDefuncion() {
        return defuncion;
    }

    public void setDefuncion(Defuncion defuncion) {
        this.defuncion = defuncion;
    }*/    
    @Column(name="ID_REGISTRO_CENTRAL", nullable = true, length=100)
    public String getIdRegistroCentral() {
		return idRegistroCentral;
	}


	public void setIdRegistroCentral(String idRegistroCentral) {
		this.idRegistroCentral = idRegistroCentral;
	}

	@Column(name="EMAIL", nullable = true, length=100)
	public String getEmail() {
		return email;
	}

	@Column(name="FAX", nullable = true, length=100)
	public void setEmail(String email) {
		this.email = email;
	}


	public String getFax() {
		return fax;
	}


	public void setFax(String fax) {
		this.fax = fax;
	}

	@Column(name = "PASIVO", nullable = false, length = 1)
	public char getPasivo() {
		return pasivo;
	}


	public void setPasivo(char pasivo) {
		this.pasivo = pasivo;
	}


	@Transient
    public boolean isSexoFemenino() {
        boolean resultado = false;
                
        if(this.sexo!=null){
            if(this.sexo.getCodigo().equals("SEXO|F")){
                resultado = true;
            }
        }
        
        return resultado;
    }
    
    @Transient
    public boolean isSexoMasculino() {
        boolean resultado = false;
                
        if(this.sexo!=null){
            if(this.sexo.getCodigo().equals("SEXO|M")){
                resultado = true;
            }
        }
        
        return resultado;
    }
           
    
}


