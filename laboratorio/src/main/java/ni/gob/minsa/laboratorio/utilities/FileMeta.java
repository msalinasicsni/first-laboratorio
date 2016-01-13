package ni.gob.minsa.laboratorio.utilities;

import com.google.gson.annotations.Expose;

/**
 * Created by FIRSTICT on 12/14/2015.
 * V1.0
 */
public class FileMeta {

    private String fileName;
    private String fileSize;
    private String fileType;
    @Expose
    private byte[] bytes;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
