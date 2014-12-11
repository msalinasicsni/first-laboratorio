package ni.gob.minsa.laboratorio.utilities;

/**
 * Created by Herrold on 11/06/14 13:41
 */
public enum typeCatalogs {

    EstadoCivil("ESTCV"),
    Ocupacion("Ocupacion"),
    Sexo("SEXO"),
    Escolaridad("ESCDA"),
    TipoIdentificacion("TPOID"),
    Etnia("ETNIA"),
    Procedencia("PROCDNCIA"),
    TipoSeguro("TPOAS"),
    TipoEncuesta("TIPOENCUESTA"),
    TipoOrdinal("ORDINALENCUESTA"),
    TipoTransporte("TPTRANSPORTE"),
    IdentAgente("IDENTAGNT"),
    TecnicaMuestra("TECNICAMX"),
    Ordinal("ORDINAL"), //MSalinas
    DistritosMng("DISTRIT"), //MSalinas
    AreasMng("AREAMNG"), //MSalinas
    ModeloEncuesta("TIPOMODENCU"), //MSalinas
    Clasificacion(""),//irag
    Captacion(""),//irag
    Respuesta(""),//irag
    Via(""), //irag
    ResRadiologia(""), //irag
    CondicionEgreso(""),//irag
    ClasifFinal(""), //irag
    Vacuna(""),//irag
    NombreVacuna(""), //irag
    CondicionPreexistente(""), //irag
    ManifestacionClinica(""); //irag

    String discriminator;

    private typeCatalogs(String discriminator){
        this.discriminator = discriminator;
    }

    public String getDiscriminator(){
        return discriminator;
    }
}