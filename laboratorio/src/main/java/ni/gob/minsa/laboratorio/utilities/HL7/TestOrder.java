package ni.gob.minsa.laboratorio.utilities.HL7;

/**
 * Created by Miguel Salinas on 23/07/2019.
 * V1.0
 * Clase que describe los parámetros necesarios para crear una orden de laboratorio en el sistema COBAS Infinity de Roche *
 */
public class TestOrder {
    private String ipServer;
    private int puertoServer;
    private String messageId;
    private String codExpediente;
    private String personaId;
    private String nombre1;
    private String nombre2;
    private String apellido1;
    private String apellido2;
    private String fechaNac;      //YYYYMMdd
    private String sexo;    //M|F
    private String idMuestra;
    private String fechaHoraMx; //YYYYMMddHHMISS
    private String idUnidadSalud; //Se debe agregar una M al inicio del id. Ejem: 100 -> M100
    private String nombreUnidadSalud;
    private String idOrigen; //3=Componente (VIH), 2=Consulta externa, 1=Hospitalización
    private String nombreOrigen;
    private String idSilais; //Se debe agregar una M al inicio del id. Ejem: 10 -> M10
    private String nombreSilais;
    private String idExamenes; //si hay mas de uno se deben separar por coma. Ejem: 1,2,3
    private String trama;
    private String idMuestraLaboratorio;
    private String usuarioRegistro;
    private Integer equipo;
    private String codigoLab;

    public String getIpServer() {
        return ipServer;
    }

    public void setIpServer(String ipServer) {
        this.ipServer = ipServer;
    }

    public int getPuertoServer() {
        return puertoServer;
    }

    public void setPuertoServer(int puertoServer) {
        this.puertoServer = puertoServer;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getCodExpediente() {
        return codExpediente;
    }

    public void setCodExpediente(String codExpediente) {
        this.codExpediente = codExpediente;
    }

    public String getPersonaId() {
        return personaId;
    }

    public void setPersonaId(String personaId) {
        this.personaId = personaId;
    }

    public String getNombre1() {
        return nombre1;
    }

    public void setNombre1(String nombre1) {
        this.nombre1 = nombre1;
    }

    public String getNombre2() {
        return nombre2;
    }

    public void setNombre2(String nombre2) {
        this.nombre2 = nombre2;
    }

    public String getApellido1() {
        return apellido1;
    }

    public void setApellido1(String apellido1) {
        this.apellido1 = apellido1;
    }

    public String getApellido2() {
        return apellido2;
    }

    public void setApellido2(String apellido2) {
        this.apellido2 = apellido2;
    }

    public String getFechaNac() {
        return fechaNac;
    }

    public void setFechaNac(String fechaNac) {
        this.fechaNac = fechaNac;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getIdMuestra() {
        return idMuestra;
    }

    public void setIdMuestra(String idMuestra) {
        this.idMuestra = idMuestra;
    }

    public String getFechaHoraMx() {
        return fechaHoraMx;
    }

    public void setFechaHoraMx(String fechaHoraMx) {
        this.fechaHoraMx = fechaHoraMx;
    }

    public String getIdUnidadSalud() {
        return idUnidadSalud;
    }

    public void setIdUnidadSalud(String idUnidadSalud) {
        this.idUnidadSalud = idUnidadSalud;
    }

    public String getNombreUnidadSalud() {
        return nombreUnidadSalud;
    }

    public void setNombreUnidadSalud(String nombreUnidadSalud) {
        this.nombreUnidadSalud = nombreUnidadSalud;
    }

    public String getIdOrigen() {
        return idOrigen;
    }

    public void setIdOrigen(String idOrigen) {
        this.idOrigen = idOrigen;
    }

    public String getNombreOrigen() {
        return nombreOrigen;
    }

    public void setNombreOrigen(String nombreOrigen) {
        this.nombreOrigen = nombreOrigen;
    }

    public String getIdSilais() {
        return idSilais;
    }

    public void setIdSilais(String idSilais) {
        this.idSilais = idSilais;
    }

    public String getNombreSilais() {
        return nombreSilais;
    }

    public void setNombreSilais(String nombreSilais) {
        this.nombreSilais = nombreSilais;
    }

    public String getIdExamenes() {
        return idExamenes;
    }

    public void setIdExamenes(String idExamenes) {
        this.idExamenes = idExamenes;
    }

    public String getTrama() {
        return trama;
    }

    public void setTrama(String trama) {
        this.trama = trama;
    }

    public String getIdMuestraLaboratorio() {
        return idMuestraLaboratorio;
    }

    public void setIdMuestraLaboratorio(String idMuestraLaboratorio) {
        this.idMuestraLaboratorio = idMuestraLaboratorio;
    }

    public String getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(String usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }

    public Integer getEquipo() {
        return equipo;
    }

    public void setEquipo(Integer equipo) {
        this.equipo = equipo;
    }

    public String getCodigoLab() {
        return codigoLab;
    }

    public void setCodigoLab(String codigoLab) {
        this.codigoLab = codigoLab;
    }
}
