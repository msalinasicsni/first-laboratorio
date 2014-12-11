package ni.gob.minsa.laboratorio.domain.muestra;


import ni.gob.minsa.laboratorio.domain.estructura.Catalogo;
import ni.gob.minsa.laboratorio.domain.portal.Usuarios;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by souyen-ics on 11-27-14.
 */
@Entity
@Table(name = "CATALOGO_EXAMENES", schema = "ALERTA")
public class CatalogoExamenes {

    private Integer idExamen;
    private String direccion;
    private String departamento;
    private String area;
    private String codTipoMx;
    private NombreExamenes codExamen;
    private Float precio;
    private boolean pasivo;
    private String tipoNotificacion;
    private Timestamp fechaRegistro;
    private Usuarios usuarioRegistro;


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_EXAMEN", nullable = false, updatable = true, insertable = true, precision = 0)
    public Integer getIdExamen() {
        return idExamen;
    }

    public void setIdExamen(Integer idExamen) {
        this.idExamen = idExamen;
    }

    @Basic
    @Column(name = "DIRECCION", nullable = false, insertable = true, updatable = true, length = 100)
    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    @Basic
    @Column(name = "DEPARTAMENTO", nullable = false, insertable = true, updatable = true, length = 100)
    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    @Basic
    @Column(name = "AREA", nullable = false, insertable = true, updatable = true, length = 100)
    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }


    @Basic
    @Column(name = "COD_TIPOMX", nullable = false, insertable = true, updatable = true, length = 100)
    public String getCodTipoMx() {
        return codTipoMx;
    }

    public void setCodTipoMx(String codTipoMx) {
        this.codTipoMx = codTipoMx;
    }

    @ManyToOne(fetch=FetchType.LAZY,targetEntity=Catalogo.class)
    @JoinColumn(name="COD_EXAMEN", referencedColumnName = "CODIGO")
    @ForeignKey(name = "COD_EXA_FK")
    public NombreExamenes getCodExamen() {
        return codExamen;
    }

    public void setCodExamen(NombreExamenes codExamen) {
        this.codExamen = codExamen;
    }

    @Column(name = "PRECIO", nullable = true)
    public Float getPrecio() {
        return precio;
    }

    public void setPrecio(Float precio) {
        this.precio = precio;
    }

    @Basic
    @Column(name = "PASIVO", nullable = true, insertable = true, updatable = true)
    public boolean isPasivo() {
        return pasivo;
    }

    public void setPasivo(boolean pasivo) {
        this.pasivo = pasivo;
    }

    @Basic
    @Column(name = "TIPO_NOTIFICACION", nullable = false, insertable = true, updatable = true)
    public String getTipoNotificacion() {
        return tipoNotificacion;
    }

    public void setTipoNotificacion(String tipoNotificacion) {
        this.tipoNotificacion = tipoNotificacion;
    }


    @Basic
    @Column(name = "FECHA_REGISTRO", nullable = false, insertable = true, updatable = true)
    public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "USUARIO_REGISTRO", referencedColumnName = "USUARIO_ID")
    @ForeignKey(name = "USUARIO_REG_FK")

    public Usuarios getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(Usuarios usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }
}
