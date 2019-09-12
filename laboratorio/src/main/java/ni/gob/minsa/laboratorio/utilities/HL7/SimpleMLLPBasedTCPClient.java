package ni.gob.minsa.laboratorio.utilities.HL7;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Miguel Salinas on 23/07/2019.
 * V1.0
 */
public class SimpleMLLPBasedTCPClient {
    private static final char END_OF_BLOCK = '\u001c';
    private static final char START_OF_BLOCK = '\u000b';
    private static final char CARRIAGE_RETURN = 13;
    private static final String DEFAULT_IP_SERVER = "192.168.100.218";
    private static final int DEFAULT_PORT_SERVER = 50001;
    //ipServer
    //puertoServer
    //messageId
    //codExpediente
    //personaId
    //nombre1
    //nombre2
    //apellido1
    //apellido2
    //fechaNac
    //sexo
    //idMuestra
    //fechaHoraMx YYYYMMddHHMISS
    //idUnidadSalud
    //nombreUnidadSalud
    //idSilais
    //nombreSilais
    //idExamenes

    public static String sendHL7TestOrder(TestOrder testOrder) throws Exception {
        // Create a socket to connect to server running locally on port 1080
        String ip = testOrder.getIpServer()!=null?testOrder.getIpServer():DEFAULT_IP_SERVER;
        int port = testOrder.getPuertoServer()>0?testOrder.getPuertoServer():DEFAULT_PORT_SERVER;
        Socket socket = new Socket(ip, port);

        StringBuffer testHL7MessageToTransmit = new StringBuffer();

        testHL7MessageToTransmit.append(START_OF_BLOCK)
                .append("MSH|^~\\&|||||||OML^O21|MSGID").append(testOrder.getMessageId())  //OML^O21 agregar o quitar ordenes
                .append(CARRIAGE_RETURN)
                        //.append("PID|||401MASRM21128901|3254200|SALINAS^RODRIGUEZ^MIGUEL^AURELIO||19700312|M")
                .append("PID|||")
                .append(testOrder.getCodExpediente().toUpperCase())
                .append("|")
                .append(testOrder.getPersonaId())
                .append("|")
                .append(testOrder.getApellido1() != null ? testOrder.getApellido1().toUpperCase() : "")
                .append("^")
                .append(testOrder.getApellido2() != null ? testOrder.getApellido2().toUpperCase() : "")
                .append("^")
                .append(testOrder.getNombre1() != null ? testOrder.getNombre1().toUpperCase() : "")
                .append("^")
                .append(testOrder.getNombre2() != null ? testOrder.getNombre2().toUpperCase() : "")
                .append("||")
                .append(testOrder.getFechaNac())
                .append("|")
                .append(testOrder.getSexo().toUpperCase())
                .append(CARRIAGE_RETURN)
                        //.append("ORC|CA|1907220999|||||||20190722094500|||^|6^HOSP LENIN FONSECA|3^COMPONENTE|||1^SILAIS MANAGUA|")
                        //CA PARA ELIMINAR ORDEN
                        // 3^Componente (VIH), 2 Consulta externa, 1 Hospitalización | 6^HOSP LENIN FONSECA (agregar prefijo M antes del id de la unidad de salud|1^SILAIS MANAGUA.. agregar M antes del id del silais
                        //.append("ORC||1907220999|||||||20190722094500|||^|M2047^HOSPITAL INFANTIL MANUEL DE JESUS LA MASCOTA|3^COMPONENTE|||M10^SILAIS MANAGUA|")
                .append("ORC||")
                .append(testOrder.getIdMuestra())
                .append("|||||||")
                .append(testOrder.getFechaHoraMx())
                .append("|||^|M")
                .append(testOrder.getIdUnidadSalud())
                .append("^")
                .append(testOrder.getNombreUnidadSalud().toUpperCase())
                .append("|")
                .append(testOrder.getIdOrigen())
                .append("^")
                .append(testOrder.getNombreOrigen().toUpperCase())
                .append("|||M")
                .append(testOrder.getIdSilais())
                .append("^")
                .append(testOrder.getNombreSilais().toUpperCase())
                .append("|")
                .append(CARRIAGE_RETURN);

        for (String idExamen : testOrder.getIdExamenes().split(",")) {
            testHL7MessageToTransmit.append("OBR||||")
                    .append(idExamen)
                    .append("|")
                            //.append(CARRIAGE_RETURN)
                            //.append("OBR||||1001|X")
                            //.append(CARRIAGE_RETURN)
                            //.append("OBR||||1002|X") //SE agrega X para eliminar prueba
                    .append(CARRIAGE_RETURN);
        }

        testHL7MessageToTransmit.append(END_OF_BLOCK)
                .append(CARRIAGE_RETURN);

        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();

        System.out.println(testHL7MessageToTransmit.toString().replace("\r", "\\r"));
        testOrder.setTrama(testHL7MessageToTransmit.toString().replace("\r", "\\r"));
        // Send the MLLP-wrapped HL7 message to the server
        out.write(testHL7MessageToTransmit.toString().getBytes());


        byte[] byteBuffer = new byte[2000];
        in.read(byteBuffer);

        // Close the socket and its streams
        socket.close();

        return "Received from Server: " + new String(byteBuffer).replace("\r", "\\r");
    }
}
