package ni.gob.minsa.laboratorio.utilities;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
/**
 * Created by FIRSTICT on 11/5/2014.
 */
public class UtilityProperties {
    Properties prop;
    public UtilityProperties(){
        LoadFilePropertie();
    }

    private void LoadFilePropertie() {
         prop = new Properties();
        InputStream input = null;

        try {
            String filename = "messages_es.properties";
            input = UtilityProperties.class.getClassLoader().getResourceAsStream(filename);
            if (input == null) {
                System.out.println("Sorry, unable to find " + filename);
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getPropertie(String key){
        return prop.getProperty(key);
    }
}
