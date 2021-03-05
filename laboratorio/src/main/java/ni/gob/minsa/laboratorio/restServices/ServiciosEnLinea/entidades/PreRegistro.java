package ni.gob.minsa.laboratorio.restServices.ServiciosEnLinea.entidades;

import java.io.Serializable;

/**
 * Created by miguel on 18/2/2021.
 */
public class PreRegistro implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private Persona persona;
    private String fecharegistro;
    private DetallePago detallepago;
    private DocumentoViaje documentoviaje;
    private EstadoRegistro estadoregistro;

    public PreRegistro(long id, Persona persona, String fecharegistro, DetallePago detallepago, DocumentoViaje documentoviaje, EstadoRegistro estadoregistro) {
        this.id = id;
        this.persona = persona;
        this.fecharegistro = fecharegistro;
        this.detallepago = detallepago;
        this.documentoviaje = documentoviaje;
        this.estadoregistro = estadoregistro;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public String getFecharegistro() {
        return fecharegistro;
    }

    public void setFecharegistro(String fecharegistro) {
        this.fecharegistro = fecharegistro;
    }

    public DetallePago getDetallepago() {
        return detallepago;
    }

    public void setDetallepago(DetallePago detallepago) {
        this.detallepago = detallepago;
    }

    public DocumentoViaje getDocumentoviaje() {
        return documentoviaje;
    }

    public void setDocumentoviaje(DocumentoViaje documentoviaje) {
        this.documentoviaje = documentoviaje;
    }

    public EstadoRegistro getEstadoregistro() {
        return estadoregistro;
    }

    public void setEstadoregistro(EstadoRegistro estadoregistro) {
        this.estadoregistro = estadoregistro;
    }

    public class Persona implements Serializable{
        private static final long serialVersionUID = 1L;
        private long id;
        private Identificacion identificacion;
        private String primerNombre;
        private String primerApellido;
        private String segundoNombre;
        private String segundoApellido;
        private Sexo sexo;
        private Residencia residencia;
        private String telefono;
        private String edad;
        private String fechanacimiento;
        private Pais paisorigen;
        private String nombrecompleto;

        public Persona(long id, Identificacion identificacion, String primerNombre, String primerApellido, String segundoNombre, String segundoApellido, Sexo sexo, Residencia residencia, String telefono, String edad, String fechanacimiento, Pais paisorigen, String nombrecompleto) {
            this.id = id;
            this.identificacion = identificacion;
            this.primerNombre = primerNombre;
            this.primerApellido = primerApellido;
            this.segundoNombre = segundoNombre;
            this.segundoApellido = segundoApellido;
            this.sexo = sexo;
            this.residencia = residencia;
            this.telefono = telefono;
            this.edad = edad;
            this.fechanacimiento = fechanacimiento;
            this.paisorigen = paisorigen;
            this.nombrecompleto = nombrecompleto;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public Identificacion getIdentificacion() {
            return identificacion;
        }

        public void setIdentificacion(Identificacion identificacion) {
            this.identificacion = identificacion;
        }

        public String getPrimerNombre() {
            return primerNombre;
        }

        public void setPrimerNombre(String primerNombre) {
            this.primerNombre = primerNombre;
        }

        public String getPrimerApellido() {
            return primerApellido;
        }

        public void setPrimerApellido(String primerApellido) {
            this.primerApellido = primerApellido;
        }

        public String getSegundoNombre() {
            return segundoNombre;
        }

        public void setSegundoNombre(String segundoNombre) {
            this.segundoNombre = segundoNombre;
        }

        public String getSegundoApellido() {
            return segundoApellido;
        }

        public void setSegundoApellido(String segundoApellido) {
            this.segundoApellido = segundoApellido;
        }

        public Sexo getSexo() {
            return sexo;
        }

        public void setSexo(Sexo sexo) {
            this.sexo = sexo;
        }

        public Residencia getResidencia() {
            return residencia;
        }

        public void setResidencia(Residencia residencia) {
            this.residencia = residencia;
        }

        public String getTelefono() {
            return telefono;
        }

        public void setTelefono(String telefono) {
            this.telefono = telefono;
        }

        public String getEdad() {
            return edad;
        }

        public void setEdad(String edad) {
            this.edad = edad;
        }

        public String getFechanacimiento() {
            return fechanacimiento;
        }

        public void setFechanacimiento(String fechanacimiento) {
            this.fechanacimiento = fechanacimiento;
        }

        public Pais getPaisorigen() {
            return paisorigen;
        }

        public void setPaisorigen(Pais paisorigen) {
            this.paisorigen = paisorigen;
        }

        public String getNombrecompleto() {
            return nombrecompleto;
        }

        public void setNombrecompleto(String nombrecompleto) {
            this.nombrecompleto = nombrecompleto;
        }
    }

    public class Identificacion implements Serializable{
        private static final long serialVersionUID = 1L;
        private Long id;
        private TipoDocumento tipo;
        private String numeroIdentificacion;

        public Identificacion(Long id, TipoDocumento tipo, String numeroIdentificacion) {
            this.id = id;
            this.tipo = tipo;
            this.numeroIdentificacion = numeroIdentificacion;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public TipoDocumento getTipo() {
            return tipo;
        }

        public void setTipo(TipoDocumento tipo) {
            this.tipo = tipo;
        }

        public String getNumeroIdentificacion() {
            return numeroIdentificacion;
        }

        public void setNumeroIdentificacion(String numeroIdentificacion) {
            this.numeroIdentificacion = numeroIdentificacion;
        }
    }

    public class Sexo implements Serializable {
        private static final long serialVersionUID = 1L;
        private Long id;
        private String codigo;
        private String valor;

        public Sexo(Long id, String codigo, String valor) {
            this.id = id;
            this.codigo = codigo;
            this.valor = valor;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getCodigo() {
            return codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }

        public String getValor() {
            return valor;
        }

        public void setValor(String valor) {
            this.valor = valor;
        }
    }

    public class Residencia implements Serializable{
        private static final long serialVersionUID = 1L;
        private String direccionhabitual;
        private DivisionPolitica divisionpolitica;

        public Residencia(String direccionhabitual, DivisionPolitica divisionpolitica) {
            this.direccionhabitual = direccionhabitual;
            this.divisionpolitica = divisionpolitica;
        }

        public String getDireccionhabitual() {
            return direccionhabitual;
        }

        public void setDireccionhabitual(String direccionhabitual) {
            this.direccionhabitual = direccionhabitual;
        }

        public DivisionPolitica getDivisionpolitica() {
            return divisionpolitica;
        }

        public void setDivisionpolitica(DivisionPolitica divisionpolitica) {
            this.divisionpolitica = divisionpolitica;
        }
    }

    public class DivisionPolitica implements Serializable{
        private static final long serialVersionUID = 1L;
        private long id;
        private String region;
        private Departamento departamento;
        private Municipio municipio;

        public DivisionPolitica(long id, String region, Departamento departamento, Municipio municipio) {
            this.id = id;
            this.region = region;
            this.departamento = departamento;
            this.municipio = municipio;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public Departamento getDepartamento() {
            return departamento;
        }

        public void setDepartamento(Departamento departamento) {
            this.departamento = departamento;
        }

        public Municipio getMunicipio() {
            return municipio;
        }

        public void setMunicipio(Municipio municipio) {
            this.municipio = municipio;
        }
    }

    public class Departamento implements Serializable {
        private static final long serialVersionUID = 1L;
        private long id;
        private String codigo;
        private String nombre;

        public Departamento(long id, String codigo, String nombre) {
            this.id = id;
            this.codigo = codigo;
            this.nombre = nombre;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getCodigo() {
            return codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }
    }

    public class Municipio implements Serializable {
        private static final long serialVersionUID = 1L;
        private long id;
        private String codigo;
        private String nombre;

        public Municipio(long id, String codigo, String nombre) {
            this.id = id;
            this.codigo = codigo;
            this.nombre = nombre;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getCodigo() {
            return codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }
    }

    public class Pais implements Serializable {
        private static final long serialVersionUID = 1L;
        private long id;
        private String codigo;
        private String nombre;

        public Pais(long id, String codigo, String nombre) {
            this.id = id;
            this.codigo = codigo;
            this.nombre = nombre;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getCodigo() {
            return codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }
    }

    public class DetallePago implements Serializable {
        private static final long serialVersionUID = 1L;

        private String referencia;

        public DetallePago(String referencia) {
            this.referencia = referencia;
        }

        public String getReferencia() {
            return referencia;
        }

        public void setReferencia(String referencia) {
            this.referencia = referencia;
        }
    }

    public class DocumentoViaje implements Serializable{
        private static final long serialVersionUID = 1L;
        private Long id;
        private String numerodocumento;

        public DocumentoViaje(Long id, String numerodocumento) {
            this.id = id;
            this.numerodocumento = numerodocumento;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getNumerodocumento() {
            return numerodocumento;
        }

        public void setNumerodocumento(String numerodocumento) {
            this.numerodocumento = numerodocumento;
        }
    }

    public class TipoDocumento implements Serializable{
        private static final long serialVersionUID = 1L;
        private String codigo;

        public TipoDocumento(String codigo) {
            this.codigo = codigo;
        }

        public String getCodigo() {
            return codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }
    }

    public class EstadoRegistro implements Serializable{
        private static final long serialVersionUID = 1L;
        private String codigo;

        private EstadoRegistro(String codigo) {
            this.codigo = codigo;
        }

        public String getCodigo() {
            return codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }
    }

}

