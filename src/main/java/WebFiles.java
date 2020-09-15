import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;

public class WebFiles {

    public  void downloadFiles(String webUrl, String fileName, String DestFolderPath) {

        System.out.println("Downloading "+webUrl);

        try {
            URL url = new URL(webUrl);
            FileUtils.copyURLToFile(
                    url,
                    new File(DestFolderPath +  File.separator + fileName)
            );
        }
        catch(IOException e){

            System.out.println("Exception in downloading the file from URL " + e.getMessage());
        }

    }


/*
    public  static void main(String[] args){
        WebFiles wf = new WebFiles();
        wf.downloadFiles("https://download.bls.gov/pub/time.series/pr/pr.contacts", "./resources/");
    }*/




}
