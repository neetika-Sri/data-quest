import java.time.LocalDateTime;

public class FileEntry {
    private String fileName;
    private String fileAbsUrl;
    private LocalDateTime lastModifiedTime;
    private long fileSize;

    public String getFileName(){
        return this.fileName ;
    }

    public String getFileAbsUrl(){
        return this.fileAbsUrl ;
    }

    public LocalDateTime getLastModifiedTime() {
        return lastModifiedTime;
    }

    public long getFileSize() {
        return fileSize;
    }


    public FileEntry(String fileName, String fileAbsUrl, LocalDateTime lastModifiedTime, long fileSize) {
        this.fileName = fileName;
        this.fileAbsUrl = fileAbsUrl;
        this.lastModifiedTime = lastModifiedTime;
        this.fileSize = fileSize;
    }



    @Override
    public String toString() {
        return "FileEntry{" +
                "fileName='" + fileName + '\'' +
                ", fileAbsUrl='" + fileAbsUrl + '\'' +
                ", lastModifiedTime=" + lastModifiedTime +
                ", fileSize=" + fileSize +
                '}';
    }
}

