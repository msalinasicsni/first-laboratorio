/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ni.gob.minsa.laboratorio.domain.persona;


import java.sql.Timestamp;
import javax.persistence.*;

import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;

/**
 *
 * @author mdeltrus
 */
@Entity
@Table(name="sis_responsablepersonas",schema="alerta")
public class SisResponsablesPersona implements java.io.Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private long responsablePersonaId;
    private SisPersona personar;
    private SisPersona responsablePersona;
    private Parentesco parentesco;
    private long notificaEmergencia;
    private Timestamp fechaRegistro;
    //private String usuarioRegistro;

    public SisResponsablesPersona() {
    }

       
    public SisResponsablesPersona(long responsablePersonaId, SisPersona persona, SisPersona responsablePersona) {
        this.responsablePersonaId = responsablePersonaId;
        this.personar = persona;
        this.responsablePersona = responsablePersona;
    }

    public SisResponsablesPersona(long responsablePersonaId, SisPersona persona, SisPersona responsablePersona, Parentesco parentesco, Timestamp fechaRegistro, String usuarioRegistro, long notificaEmergencia) {
        this.responsablePersonaId = responsablePersonaId;
        this.personar = persona;
        this.responsablePersona = responsablePersona;
        this.parentesco = parentesco;
        this.fechaRegistro = fechaRegistro;
        //this.usuarioRegistro = usuarioRegistro;
        this.notificaEmergencia = notificaEmergencia;
    }

    @Column(name="FECHA_REGISTRO")
    public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @ManyToOne(targetEntity=Catalogo.class,fetch= FetchType.LAZY)
    @JoinColumn(name="CODIGO_PARENTESCO",referencedColumnName="CODIGO")
    public Parentesco getParentesco() {
        return parentesco;
    }

    public void setParentesco(Parentesco parentesco) {
        this.parentesco = parentesco;
    }

    
    @ManyToOne(targetEntity=SisPersona.class,fetch= FetchType.LAZY)
    @JoinColumn(name="PERSONA_ID",referencedColumnName="PERSONA_ID")
    public SisPersona getPersonar() {
        return personar;
    }

    public void setPersonar(SisPersona persona) {
        this.personar = persona;
    }

    @ManyToOne(targetEntity=SisPersona.class,fetch= FetchType.LAZY)
    @JoinColumn(name="RESPONSABLE_ID",referencedColumnName="PERSONA_ID")
    public SisPersona getResponsablePersona() {
        return responsablePersona;
    }

    public void setResponsablePersona(SisPersona responsablePersona) {
        this.responsablePersona = responsablePersona;
    }

    @Id
    @Column(name="RESPONSABLEPERSONA_ID", nullable=false, precision=10, scale=0)
    public long getResponsablePersonaId() {
        return responsablePersonaId;
    }

    public void setResponsablePersonaId(long responsablePersonaId) {
        this.responsablePersonaId = responsablePersonaId;
    }

    /*@Column(name="USUARIO_REGISTRO")
    public String getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(String usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }*/

    @Column(name="NOTIFICA_EMERGENCIA")
    public long getNotificaEmergencia() {
        return notificaEmergencia;
    }

    public void setNotificaEmergencia(long notificaEmergencia) {
        this.notificaEmergencia = notificaEmergencia;
    }
    
    
    
    
    
}

