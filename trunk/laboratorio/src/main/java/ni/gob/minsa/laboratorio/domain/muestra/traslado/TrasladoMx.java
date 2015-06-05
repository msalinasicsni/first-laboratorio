package ni.gob.minsa.laboratorio.domain.muestra.traslado;

import ni.gob.minsa.laboratorio.domain.examen.Area;
import ni.gob.minsa.laboratorio.domain.muestra.DaTomaMx;
import ni.gob.minsa.laboratorio.domain.muestra.Laboratorio;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.User;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by FIRSTICT on 4/24/2015.
 * V1.0
 */
@Entity
@Table(name = "traslado_mx", schema = "laboratorio")
public class TrasladoMx {
    String idTraslado;
    DaTomaMx tomaMx;
    boolean trasladoExterno;
    Laboratorio laboratorioOrigen;
    Laboratorio laboratorioDestino;
    Area areaOrigen;
    Area areaDestino;
    Timestamp fechaHoraRegistro;
    String usuarioRegistro;
    Integer prioridad;
    boolean recepcionado;
    User usuarioRecepcion;
    Timestamp fechaHoraRecepcion;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "ID_TRASLADO", nullable = false, insertable = true, updatable = true, length = 36)
    public String getIdTraslado() {
        return idTraslado;
    }

    public void setIdTraslado(String idTraslado) {
        this.idTraslado = idTraslado;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_TOMAMX", referencedColumnName = "ID_TOMAMX")
    @ForeignKey(name = "ID_TOMAMX_TRASLADO_FK")
    public DaTomaMx getTomaMx() {
        return tomaMx;
    }

    public void setTomaMx(DaTomaMx tomaMx) {
        this.tomaMx = tomaMx;
    }

    @ManyToOne(optional = true)
    @JoinColumn(name="AREA_ORI", referencedColumnName = "ID_AREA", nullable = false)
    @ForeignKey(name = "TRASLADO_AREA_ORI_FK")
    public Area getAreaOrigen() {
        return areaOrigen;
    }

    public void setAreaOrigen(Area areaOrigen) {
        this.areaOrigen = areaOrigen;
    }

    @ManyToOne(optional = true)
    @JoinColumn(name="AREA_DEST", referencedColumnName = "ID_AREA", nullable = false)
    @ForeignKey(name = "TRASLADO_AREA_DEST_FK")
    public Area getAreaDestino() {
        return areaDestino;
    }

    public void setAreaDestino(Area areaDestino) {
        this.areaDestino = areaDestino;
    }

    @Basic
    @Column(name = "FECHAH_REGISTRO", nullable = true, insertable = true, updatable = true)
    public Timestamp getFechaHoraRegistro() {
        return fechaHoraRegistro;
    }

    public void setFechaHoraRegistro(Timestamp fechaHoraRegistro) {
        this.fechaHoraRegistro = fechaHoraRegistro;
    }

    @Basic
    @Column(name = "PRIORIDAD", nullable = false, insertable = true, updatable = true)
    public Integer getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Integer prioridad) {
        this.prioridad = prioridad;
    }

    @Basic
    @Column(name = "RECEPCIONADO", columnDefinition = "number(1,0) default 0", nullable = false, insertable = true, updatable = true)
    public boolean isRecepcionado() {
        return recepcionado;
    }

    public void setRecepcionado(boolean recepcionado) {
        this.recepcionado = recepcionado;
    }

    @ManyToOne(optional = true)
    @JoinColumn(name = "USUARIO_RECEPCIONA", referencedColumnName = "username")
    @ForeignKey(name = "TRASLADO_USUARIO_RECEPCION_FK")
    public User getUsuarioRecepcion() {
        return usuarioRecepcion;
    }

    public void setUsuarioRecepcion(User usuarioRecepcion) {
        this.usuarioRecepcion = usuarioRecepcion;
    }

    @Basic
    @Column(name = "FECHAH_RECEPCION", nullable = true, insertable = true, updatable = true)
    public Timestamp getFechaHoraRecepcion() {
        return fechaHoraRecepcion;
    }

    public void setFechaHoraRecepcion(Timestamp fechaHoraRecepcion) {
        this.fechaHoraRecepcion = fechaHoraRecepcion;
    }

    @Basic
    @Column(name = "TRASLADO_EXTERNO", columnDefinition = "number(1,0) default 0", nullable = false, insertable = true, updatable = true)
    public boolean isTrasladoExterno() {
        return trasladoExterno;
    }

    public void setTrasladoExterno(boolean trasladoExterno) {
        this.trasladoExterno = trasladoExterno;
    }

    @ManyToOne(optional = true)
    @JoinColumn(name = "LABORATORIO_ORI", referencedColumnName = "CODIGO")
    @ForeignKey(name = "TRASLADO_LABORATORIO_ORI_FK")
    public Laboratorio getLaboratorioOrigen() {
        return laboratorioOrigen;
    }

    public void setLaboratorioOrigen(Laboratorio laboratorioOrigen) {
        this.laboratorioOrigen = laboratorioOrigen;
    }

    @ManyToOne(optional = true)
    @JoinColumn(name = "LABORATORIO_DEST", referencedColumnName = "CODIGO")
    @ForeignKey(name = "TRASLADO_LABORATORIO_DEST_FK")
    public Laboratorio getLaboratorioDestino() {
        return laboratorioDestino;
    }

    public void setLaboratorioDestino(Laboratorio laboratorioDestino) {
        this.laboratorioDestino = laboratorioDestino;
    }

    @Basic
    @Column(name = "USUARIO_REGISTRO", nullable = false, insertable = true, updatable = true, length = 50)
    public String getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(String usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }
}
