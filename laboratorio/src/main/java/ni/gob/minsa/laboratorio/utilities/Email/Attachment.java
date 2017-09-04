package ni.gob.minsa.laboratorio.utilities.Email;

import javax.activation.DataSource;

/**
 * Created by Miguel Salinas on 8/3/2017.
 * V1.0
 */
public class Attachment {
    String content;
    String fileName;
    String type;
    DataSource fileDataSource;

    public Attachment() {
    }

    public Attachment(String fileName, String type, String content) {
        this.content = content;
        this.fileName = fileName;
        this.type = type;
    }

    public Attachment(String fileName, String type, DataSource fileDataSource) {
        this.fileDataSource = fileDataSource;
        this.fileName = fileName;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DataSource getFileDataSource() {
        return fileDataSource;
    }

    public void setFileDataSource(DataSource fileDataSource) {
        this.fileDataSource = fileDataSource;
    }
}
