package ni.gob.minsa.laboratorio.domain.muestra;

import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by FIRSTICT on 12/9/2014.
 */
@Entity
@Table(name = "ETIQUETA_ANULADA_MX", schema = "LABORATORIO")
public class EtiquetaMxAnulada {
    String idEtiquetaAnul;
    Laboratorio laboratorio;
    String codMxAnulada;
    String estado;
    String justificacion;
    Date fechaAnulacion;
    Usuarios usuarioRegistro;

    @Id
    @Column(name = "ID_ETIQUETA_MX_ANUL", nullable = false, insertable = true, updatable = true, length = 10)
    public String getIdEtiquetaAnul() {
        return idEtiquetaAnul;
    }

    public void setIdEtiquetaAnul(String idEtiquetaAnul) {
        this.idEtiquetaAnul = idEtiquetaAnul;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "COD_LABORATORIO", referencedColumnName = "CODIGO", nullable = false)
    @ForeignKey(name = "ETIQTANUL_LABORATORIO_FK")
    public Laboratorio getLaboratorio() {
        return laboratorio;
    }

    public void setLaboratorio(Laboratorio laboratorio) {
        this.laboratorio = laboratorio;
    }


    @Basic
    @Column(name = "CODMX_ANULADA", nullable = false, insertable = true, updatable = true, length = 12)
    public String getCodMxAnulada() {
        return codMxAnulada;
    }

    public void setCodMxAnulada(String codMxAnulada) {
        this.codMxAnulada = codMxAnulada;
    }

    @Basic
    @Column(name = "FECHA_ANULACION", nullable = false, insertable = true, updatable = true, length = 12)
    public Date getFechaAnulacion() {
        return fechaAnulacion;
    }

    public void setFechaAnulacion(Date fechaAnulacion) {
        this.fechaAnulacion = fechaAnulacion;
    }

    @Basic
    @Column(name = "ESTADO", nullable = false, insertable = true, updatable = true, length = 10)
    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Basic
    @Column(name = "JUSTIFICACION", nullable = false, insertable = true, updatable = true, length = 200)
    public String getJustificacion() {
        return justificacion;
    }

    public void setJustificacion(String justificacion) {
        this.justificacion = justificacion;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "USUARIO_REGISTRO", referencedColumnName = "USUARIO_ID", nullable = false)
    @ForeignKey(name = "ETIQTA_USUARIO_FK")
    public Usuarios getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(Usuarios usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }
}
