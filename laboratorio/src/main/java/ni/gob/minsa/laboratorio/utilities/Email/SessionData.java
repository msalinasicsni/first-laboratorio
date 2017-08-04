package ni.gob.minsa.laboratorio.utilities.Email;

/**
 * Created by Miguel Salinas on 8/4/2017.
 * V1.0
 */
public class SessionData {
    String fromEmail;
    String password;
    String smtpHost;
    String smtpPort;
    String sslPort;

    public SessionData() {
    }

    public SessionData(String fromEmail, String password, String smtpHost, String smtpPort, String sslPort) {
        this.fromEmail = fromEmail;
        this.password = password;
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.sslPort = sslPort;
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    public String getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(String smtpPort) {
        this.smtpPort = smtpPort;
    }

    public String getSslPort() {
        return sslPort;
    }

    public void setSslPort(String sslPort) {
        this.sslPort = sslPort;
    }
}
