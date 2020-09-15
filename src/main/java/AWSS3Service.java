import java.io.File;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;


import java.util.ArrayList;
import java.util.List;

public class AWSS3Service {
    private Regions clientRegion = Regions.US_WEST_1;
    private AmazonS3 s3Client = null;

    public AWSS3Service(){
        s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(clientRegion)
                .build();
    }

  /*  public static void main(String[] args){
        AWSS3Service ser = new AWSS3Service();
        ser.uploadObject("rearcquest","./resources/");
        List<String> files = ser.listFiles("rearcquest");
        files.forEach(System.out::println);
        ser.deleteFiles("rearcquest", new String[]{"test1/newFile","test1/AddAFile"});
    }*/

    public void deleteFiles(String bucketName,String[] files){
        if(files == null || files.length < 1)
            return;

        System.out.println("Deleting files for s3 "+ String.join(",",files));

        try{
            DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName)
                    .withQuiet(false)
                    .withKeys(files);
            s3Client.deleteObjects(deleteObjectsRequest);

        } catch (AmazonServiceException e) {
            System.out.println("s3 could not process the delete request "+e.getErrorMessage());
        } catch (SdkClientException e) {
            System.out.println("s3 could not be contacted or Client cant parse the response" +
                    " for the delete request "+e.getLocalizedMessage());
        }
    }

    public List<String> listFiles(String bucketName) {
        ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName);
        ListObjectsV2Result result;
        List<String> fileNames = new ArrayList<>();
        try{
            do {
                result = s3Client.listObjectsV2(req);

                for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
                    System.out.printf(" - %s (size: %d)\n", objectSummary.getKey(), objectSummary.getSize());
                    fileNames.add(objectSummary.getKey());
                }
            } while (result.isTruncated());
        } catch (AmazonServiceException e) {
            System.out.println("s3 could not process the list files request "+e.getErrorMessage());
            System.exit(1);
        } catch (SdkClientException e) {
            System.out.println("s3 could not be contacted or Client cant parse the response" +
                    " for the list request "+e.getLocalizedMessage());
            System.exit(1);
        }
        return fileNames;
    }


    public void uploadObject(String bucketName, String fileName) {

        System.out.println("Uploading Directory "+fileName+ "s3 bucket "+bucketName);

        TransferManager transferManager = TransferManagerBuilder.standard().withS3Client(s3Client)
                .build();
        try {
            MultipleFileUpload multipleFileUpload = transferManager.uploadDirectory(bucketName,
                    "", new File(fileName), false);
            multipleFileUpload.waitForCompletion();
        } catch (AmazonServiceException e) {
            System.out.println("s3 could not process the upload directory request ");

            System.out.println(e.getErrorMessage());
            System.exit(1);
        }catch (InterruptedException ie) {
            System.out.println("Upload process got interrupted");
            System.out.println(ie.getLocalizedMessage());
            System.exit(1);
        }
        transferManager.shutdownNow(false);
    }
}


