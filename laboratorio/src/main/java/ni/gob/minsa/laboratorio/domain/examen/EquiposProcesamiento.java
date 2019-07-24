package ni.gob.minsa.laboratorio.domain.examen;

import javax.persistence.*;

/**
 * Created by Miguel Salinas on 24/07/2019.
 * V1.0
 */
@Entity
@Table(name = "catalogo_area", schema = "laboratorio")
public class EquiposProcesamiento {

    private int idEquipo;
    private String nombre;
    private String marca;
    private String modelo;
    private String descripcion;

    @Id
    @GeneratedValue(strategy= GenerationType.TABLE)
    @Column(name = "ID_AREA", nullable = false, insertable = true, updatable = true)
    public int getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(int idEquipo) {
        this.idEquipo = idEquipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
