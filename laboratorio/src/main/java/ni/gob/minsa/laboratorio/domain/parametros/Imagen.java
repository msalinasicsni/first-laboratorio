package ni.gob.minsa.laboratorio.domain.parametros;

import javax.persistence.*;

/**
 * Created by FIRSTICT on 12/16/2015.
 * V1.0
 */
@Entity
@Table(name = "imagen", schema = "laboratorio")
public class Imagen {

    private String nombre;
    private String descripcion;
    private byte[] bytes;
    private String type;
    private String nombreArchivo;

    @Id
    @Column(name = "NOMBRE", nullable = false, insertable = true, updatable = true, length = 50)
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Basic
    @Column(name = "DESCRIPCION", nullable = false, insertable = true, updatable = true, length = 200)
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Lob
    @Column(name = "ARCHIVO", nullable = true, insertable = true, updatable = true)
    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    @Basic
    @Column(name = "CONTENT_TYPE", nullable = true, insertable = true, updatable = true, length = 100)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Basic
    @Column(name = "NOMBRE_ARCHIVO", nullable = true, insertable = true, updatable = true, length = 100)
    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }
}
