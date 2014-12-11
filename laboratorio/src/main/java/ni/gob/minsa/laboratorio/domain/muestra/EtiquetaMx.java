package ni.gob.minsa.laboratorio.domain.muestra;

import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by FIRSTICT on 12/9/2014.
 */
@Entity
@Table(name = "ETIQUETA_MX", schema = "LABORATORIO")
public class EtiquetaMx {
    String idEtiquetaMx;
    Laboratorio laboratorio;
    String codMxInicial;
    String codMxFinal;
    Date fechaRegistro;
    Usuarios usuarioRegistro;

    @Id
    @Column(name = "ID_ETIQUETA_MX", nullable = false, insertable = true, updatable = true, length = 10)
    public String getIdEtiquetaMx() {
        return idEtiquetaMx;
    }

    public void setIdEtiquetaMx(String idEtiquetaMx) {
        this.idEtiquetaMx = idEtiquetaMx;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "COD_LABORATORIO", referencedColumnName = "CODIGO", nullable = false)
    @ForeignKey(name = "ETIQTA_LABORATORIO_FK")
    public Laboratorio getLaboratorio() {
        return laboratorio;
    }

    public void setLaboratorio(Laboratorio laboratorio) {
        this.laboratorio = laboratorio;
    }

    @Basic
    @Column(name = "CODMX_INICIAL", nullable = false, insertable = true, updatable = true, length = 12)
    public String getCodMxInicial() {
        return codMxInicial;
    }

    public void setCodMxInicial(String codMxInicial) {
        this.codMxInicial = codMxInicial;
    }

    @Basic
    @Column(name = "CODMX_FINAL", nullable = false, insertable = true, updatable = true, length = 12)
    public String getCodMxFinal() {
        return codMxFinal;
    }

    public void setCodMxFinal(String codMxFinal) {
        this.codMxFinal = codMxFinal;
    }

    @Basic
    @Column(name = "FECHA_REGISTRO", nullable = false, insertable = true, updatable = true, length = 12)
    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
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
