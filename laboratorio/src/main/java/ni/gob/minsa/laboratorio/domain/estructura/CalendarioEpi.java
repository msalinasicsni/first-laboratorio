package ni.gob.minsa.laboratorio.domain.estructura;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by FIRSTICT on 9/2/2014.
 */
@Entity
@Table(name = "CALENDARIO_EPI", schema = "ALERTA",  uniqueConstraints = @UniqueConstraint(columnNames = {"ANIO","NO_SEMANA"}))
public class CalendarioEpi {
    private int anio;
    private int noSemana;
    private Timestamp fechaInicial;
    private Timestamp fechaFinal;
    private int noMes;
    private Timestamp fechabaja;
    private String usuariobaja;

    @Id
    @Column(name = "FECHA_INICIAL")
    public Timestamp getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Timestamp fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    @Basic
    @Column(name = "ANIO")
    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    @Basic
    @Column(name = "NO_SEMANA")
    public int getNoSemana() {
        return noSemana;
    }

    public void setNoSemana(int noSemana) {
        this.noSemana = noSemana;
    }

    @Basic
    @Column(name = "FECHA_FINAL")
    public Timestamp getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Timestamp fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    @Basic
    @Column(name = "NO_MES")
    public int getNoMes() {
        return noMes;
    }

    public void setNoMes(int noMes) {
        this.noMes = noMes;
    }

    @Basic
    @Column(name = "FECHABAJA")
    public Timestamp getFechabaja() {
        return fechabaja;
    }

    public void setFechabaja(Timestamp fechabaja) {
        this.fechabaja = fechabaja;
    }

    @Basic
    @Column(name = "USUARIOBAJA")
    public String getUsuariobaja() {
        return usuariobaja;
    }

    public void setUsuariobaja(String usuariobaja) {
        this.usuariobaja = usuariobaja;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CalendarioEpi that = (CalendarioEpi) o;

        if (anio != that.anio) return false;
        if (noMes != that.noMes) return false;
        if (noSemana != that.noSemana) return false;
        if (fechaFinal != null ? !fechaFinal.equals(that.fechaFinal) : that.fechaFinal != null) return false;
        if (fechaInicial != null ? !fechaInicial.equals(that.fechaInicial) : that.fechaInicial != null) return false;
        if (fechabaja != null ? !fechabaja.equals(that.fechabaja) : that.fechabaja != null) return false;
        if (usuariobaja != null ? !usuariobaja.equals(that.usuariobaja) : that.usuariobaja != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = anio;
        result = 31 * result + noSemana;
        result = 31 * result + (fechaInicial != null ? fechaInicial.hashCode() : 0);
        result = 31 * result + (fechaFinal != null ? fechaFinal.hashCode() : 0);
        result = 31 * result + noMes;
        result = 31 * result + (fechabaja != null ? fechabaja.hashCode() : 0);
        result = 31 * result + (usuariobaja != null ? usuariobaja.hashCode() : 0);
        return result;
    }
}
