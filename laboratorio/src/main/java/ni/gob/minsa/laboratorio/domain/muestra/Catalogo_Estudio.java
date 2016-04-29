package ni.gob.minsa.laboratorio.domain.muestra;

import ni.gob.minsa.laboratorio.domain.audit.Auditable;
import ni.gob.minsa.laboratorio.domain.examen.Area;
import ni.gob.minsa.laboratorio.domain.seguridadlocal.User;
import org.hibernate.annotations.ForeignKey;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by FIRSTICT on 2/24/2015.
 * V1.0
 */
@Entity
@Table(name = "catalogo_estudio", schema = "laboratorio")
public class Catalogo_Estudio implements Serializable, Auditable {

    private static final long serialVersionUID = 5110985758665058146L;
    private Integer idEstudio;
    private String nombre;
    private boolean pasivo;
    private Area area;
    private String codigo;
    private Date fechaRegistro;
    private User usuarioRegistro;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "ID_ESTUDIO", nullable = false, insertable = true, updatable = true, length = 10)
    public Integer getIdEstudio() {
        return idEstudio;
    }

    public void setIdEstudio(Integer idEstudio) {
        this.idEstudio = idEstudio;
    }

    @Basic
    @Column(name = "NOMBRE", nullable = false, insertable = true, updatable = true, length = 100)
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Basic
    @Column(name = "PASIVO", nullable = true, insertable = true, updatable = true)
    public boolean isPasivo() {
        return pasivo;
    }

    public void setPasivo(boolean pasivo) {
        this.pasivo = pasivo;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name="ID_AREA", referencedColumnName = "ID_AREA", nullable = false)
    @ForeignKey(name = "ESTUDIO_AREA_FK")
    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    @Basic
    @Column(name = "CODIGO", nullable = false, insertable = true, updatable = true, length = 16)
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column(name = "FECHA_REGISTRO", nullable = true)
    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @ManyToOne()
    @JoinColumn(name="USUARIO_REGISTRO", referencedColumnName="username", nullable=true)
    @ForeignKey(name = "fk_dxTipoMxNoti_usuario")
    public User getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(User usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }

    @Override
    public String toString() {
        return String.valueOf(idEstudio);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Catalogo_Estudio)) return false;

        Catalogo_Estudio that = (Catalogo_Estudio) o;

        if (idEstudio != null ? !idEstudio.equals(that.idEstudio) : that.idEstudio != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return idEstudio != null ? idEstudio.hashCode() : 0;
    }

    @Override
    public boolean isFieldAuditable(String fieldname) {
        if (fieldname.matches("fechaRegistro") || fieldname.matches("usuarioRegistro")) return false;
        return  true;
    }
}
