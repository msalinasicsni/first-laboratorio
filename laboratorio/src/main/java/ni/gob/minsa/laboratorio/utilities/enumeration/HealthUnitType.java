package ni.gob.minsa.laboratorio.utilities.enumeration;

/**
 * Created by FIRSTICT on 10/6/2014.
 */
public enum HealthUnitType {
    //códigos de las unidades primarias: Puesto de Salud, Centro de Salud, Policlínica.
    UnidadesPrimarias("3,4,5"),
    UnidadesPrimHosp("3,4,5,11,12,13,14,17,18,8,38,6,16,39"); // Se agregan 8,38,6,16 solicitados en capacitacion//29072019 se agrega tipo 39 según depuración MINSA (CMP del MINSA)

    String discriminator;

    private HealthUnitType(String discriminator){
        this.discriminator = discriminator;
    }

    public String getDiscriminator(){
        return discriminator;
    }

}
