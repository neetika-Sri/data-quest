
import org.apache.commons.io.FileUtils;
import org.javatuples.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;

public class FileSyncProcessor {
    private Map<String,FileEntry> fileEntries;

    public FileSyncProcessor(){
        fileEntries = new HashMap<String,FileEntry>();
    }

    public void process(String url, String destFolder, String bucket){
        // Delete files from local
        cleanDirectory(destFolder);
        // get file names from url
        AWSS3Service awss3Service = new AWSS3Service();
        parseSourceFilesInfo(url);
        // List s3 bucket files
        Set<String> s3FileNames = awss3Service.listFiles(bucket)
                .stream()
                .collect(Collectors.toSet());
        Set<String> sourceFileNames = fileEntries.keySet();
        Collection<String> filesToDelete = CollectionUtils.removeAll(s3FileNames, sourceFileNames);
        Collection<String> filesToAdd = CollectionUtils.removeAll(sourceFileNames, s3FileNames);

        // Download new files
        WebFiles webFiles = new WebFiles();
        filesToAdd.forEach(f ->
                webFiles.downloadFiles(fileEntries.get(f).getFileAbsUrl(),
                        fileEntries.get(f).getFileName(),destFolder )
        );
        // upload Directory
        if(!filesToAdd.isEmpty())
            awss3Service.uploadObject(bucket,destFolder);

        // Delete Directory

        awss3Service.deleteFiles(bucket,filesToDelete.toArray(new String[]{}));
        // Delete files from local
        cleanDirectory(destFolder);

    }

    private void cleanDirectory(String destFolder) {
        try {
            System.out.println("Cleaning the local folder");
            FileUtils.cleanDirectory(new File(destFolder));
        }
        catch(IOException io){
            io.printStackTrace();
        }
    }

    private void parseSourceFilesInfo(String url) {
        System.out.println("Parsing file list in "+url);
        try {
            Document doc = Jsoup.connect(url).get();
            Elements e = doc.select("A");
            Element preElement = doc.select("pre").first();
            List<Node> singleFileEntries = new ArrayList<Node>();
            for(Node node : preElement.childNodes()){
                if(node instanceof  Element){
                    String tagName = ((Element) node).tagName();
                    if(tagName.equalsIgnoreCase("br")){
                        addToFileEntries(singleFileEntries);
                        singleFileEntries = new ArrayList<>();
                    } else if(tagName.equalsIgnoreCase("a")){
                        singleFileEntries.add(node);
                    }
                } else if (node instanceof TextNode) {
                    singleFileEntries.add(node);
                }
            }

            printFileEntries();

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void addToFileEntries(List<Node> fileLineNodes){
        if(fileLineNodes == null || fileLineNodes.size() < 2)
            return;
        try {
            Pair<LocalDateTime, Long> dateTimeSize = parseDateTimeAndSize(fileLineNodes.get(0));
            Pair<String, String> nameAndUrl = ParseFileNameAndUrl(fileLineNodes.get(1));
            fileEntries.put(nameAndUrl.getValue0(), new FileEntry(nameAndUrl.getValue0(), nameAndUrl.getValue1(),
                    dateTimeSize.getValue0(), dateTimeSize.getValue1()));
        }
        catch (Exception e){
            System.out.println("Couldnot able to add file entry for file nodes: " + fileLineNodes.get(0) +
                    "     AND + "+ fileLineNodes.get(1) + "With Exception " + e.getMessage());
            //e.printStackTrace();
        }
    }
    private Pair<LocalDateTime, Long> parseDateTimeAndSize(Node fileDateTimeAndSizeNode){
        String text = ((TextNode) fileDateTimeAndSizeNode).text();
        String[] dtSizeArr = text.trim().split("\\s+");
        String dtTime = dtSizeArr[0]+" "+dtSizeArr[1]+" "+dtSizeArr[2];
        return new Pair<>(LocalDateTime.parse(
                dtTime,
                DateTimeFormatter.ofPattern("M/d/yyyy h:m a")),
                Long.parseLong(dtSizeArr[3])) ;
    }

    private Pair<String, String> ParseFileNameAndUrl(Node fileNameAndUrlNode){
        Element elem = (Element)fileNameAndUrlNode;
        return new Pair<>(elem.text(), elem.attr("abs:href"));
    }

    private void printFileEntries()
    {
        for(FileEntry fe : fileEntries.values())
            System.out.println(fe.toString());
    }


}
