package com.absy.awss3;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class S3Application {

    private static final AWSCredentials credentials;
    private static String bucketName;

    static {
        //put your accesskey and secretkey here
        credentials = new BasicAWSCredentials(
                "ACCESS_KEY",
                "SECRET_KEY"
        );
    }

    public static void main(String[] args) throws IOException {
        //set-up the client
        AmazonS3 s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_2)
                .build();

        AWSS3Service awsService = new AWSS3Service(s3client);

        bucketName = "absy-sample-bucket-1";

        //creating a bucket
        if(awsService.doesBucketExist(bucketName)) {
            System.out.println("Bucket name is not available."
                    + " Try again with a different Bucket name.");
            return;
        }
        awsService.createBucket(bucketName);

        //list all the buckets
        for(Bucket s : awsService.listBuckets() ) {
            System.out.println(s.getName());
        }

        //deleting bucket
        //awsService.deleteBucket("baeldung-bucket-test2");

        //uploading object
        awsService.putObject(
                bucketName,
                "Document/aws.png",
                new File("C:\\Users\\smukherjee\\aws-upload\\aws.png")
        );

        //listing objects
        ObjectListing objectListing = awsService.listObjects(bucketName);
        for(S3ObjectSummary os : objectListing.getObjectSummaries()) {
            System.out.println(os.getKey());
        }

        //downloading an object
        S3Object s3object = awsService.getObject(bucketName, "Document/aws.png");
        S3ObjectInputStream inputStream = s3object.getObjectContent();
        FileUtils.copyInputStreamToFile(inputStream, new File("C:\\Users\\smukherjee\\aws-download\\aws.png"));

        //copying an object
        awsService.copyObject(
                "absy-sample-bucket-1",
                "Document/aws.png",
                "absy-sample-bucket-2",
                "Document/aws.png"
        );

        //deleting an object
        awsService.deleteObject(bucketName, "Document/aws.png");

        //deleting multiple objects
        String objkeyArr[] = {
                "Document/hello2.txt",
                "Document/picture.png"
        };

        DeleteObjectsRequest delObjReq = new DeleteObjectsRequest("baeldung-bucket")
                .withKeys(objkeyArr);
        //awsService.deleteObjects(delObjReq);
    }
}
