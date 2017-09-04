package ni.gob.minsa.laboratorio.utilities.Email;

/**
 * Created by Miguel Salinas on 8/3/2017.
 * V1.0
 */

import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

public class EmailUtil {

    public static Session openSession(final SessionData sessionData){

        System.out.println("SSLEmail Start");
        Properties props = new Properties();
        props.put("mail.smtp.host", sessionData.getSmtpHost()); //SMTP Host
        props.put("mail.smtp.socketFactory.port", sessionData.getSslPort()); //SSL Port
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); //SSL Factory Class
        props.put("mail.smtp.auth", "true"); //Enabling SMTP Authentication
        props.put("mail.smtp.port", sessionData.getSmtpPort()); //SMTP Port

        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sessionData.getFromEmail(), sessionData.getPassword());
            }
        };

        return Session.getDefaultInstance(props, auth);
    }
    /**
     * Utility method to send simple HTML email
     * @param session Session abierta con el servidor de correo
     * @param toEmail destinatarios (separados por coma)
     * @param subject Asunto del correo
     * @param body Cuerpo del correo
     */
    public static void sendEmail(Session session, String toEmail, String subject, String body){
        try
        {
            MimeMessage msg = new MimeMessage(session);
            //set message headers
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            //msg.setFrom(new InternetAddress("msalinas@icsnicaragua.org", "NoReply-JD"));

            //msg.setReplyTo(InternetAddress.parse("msalinas@icsnicaragua.org", false));

            msg.setSubject(subject, "UTF-8");

            msg.setText(body, "UTF-8");

            msg.setSentDate(new Date());
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            System.out.println("Message is ready");
            Transport.send(msg);

            System.out.println("EMail Sent Successfully!!");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Utility method to send email with content
     * @param session Session abierta con el servidor de correo
     * @param toEmail destinatarios (separados por coma)
     * @param subject Asunto del correo
     * @param body Cuerpo del correo
     * @param attachment Adjunto(puede ser cualquier tipo de archivo)
     */
    public static void sendAttachmentEmail(Session session, String toEmail, String subject, String body, Attachment attachment) throws Exception{
        try{
            MimeMessage msg = new MimeMessage(session);
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            //msg.setFrom(new InternetAddress("no_reply@journaldev.com", "NoReply-JD"));

            //msg.setReplyTo(InternetAddress.parse("no_reply@journaldev.com", false));

            msg.setSubject(subject, "UTF-8");

            msg.setSentDate(new Date());

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));

            // Create the message body part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Fill the message
            messageBodyPart.setText(body);

            // Create a multipart message for content
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            // Second part is content
            messageBodyPart = new MimeBodyPart();
            //DataSource source = new FileDataSource(filename);
            if (attachment.getContent()!=null) {
                DataSource ds = new ByteArrayDataSource(attachment.getContent().getBytes("UTF-8"), attachment.getType());
                messageBodyPart.setDataHandler(new DataHandler(ds));
            }else{
                messageBodyPart.setDataHandler(new DataHandler(attachment.getFileDataSource()));
            }
            messageBodyPart.setFileName(attachment.getFileName());
            multipart.addBodyPart(messageBodyPart);

            // Send the complete message parts
            msg.setContent(multipart);

            // Send message
            Transport.send(msg);
            System.out.println("EMail Sent Successfully with content!!");
        }catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}
