public class S3FileSync {
    public static void main(String[] args){
/*
        String url = "https://download.bls.gov/pub/time.series/pr/";
        String destFolder = "./resources";
        String bucket = "testneetika";
        */
    if(args.length < 3){
        System.out.println("Please Provide all these arguments in order - URL , Destination Folder and s3 bucket name");
        System.exit(1);
    }
     System.out.println("Running s3 Data sync for :");
     System.out.println("Source URL :"+ args[0]);
     System.out.println("tmp folder on local :"+args[1]);
     System.out.println("Bucket :"+args[2]);
        String url = args[0];
        String destFolder = args[1];
        String bucket = args[2];


        FileSyncProcessor fileSyncProcessor = new FileSyncProcessor();
        fileSyncProcessor.process(url, destFolder, bucket);

    }
}
