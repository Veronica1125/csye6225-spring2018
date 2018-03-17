package com.csye6225.spring2018.controller;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.util.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;

public class S3BucketController {

    public String uploadFile(String keyName, MultipartFile multipartfile) {

        String bucketName = System.getProperty("bucket.name");
        System.out.println("bucket name is :" + System.getProperty("bucket.name"));

        /*Assigns Temporary credentials to IAM role
         * InstanceProfileCredentialsProvider : false does not refresh the credentials
         */
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new InstanceProfileCredentialsProvider(false))
                .build();
        try {

            System.out.println("Uploading file to s3 bucket");
            File filename = convertFromMultipartToProfilePic(multipartfile);
            s3Client.putObject(new PutObjectRequest(bucketName, keyName + filename.getName(), filename));
            return keyName + filename.getName();
        } catch (AmazonServiceException ase) {
            System.out.println("bucket name: " + bucketName);
            System.out.println("Request made to s3 bucket failed");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] getFile(String keyName) {

        String bucketName = System.getProperty("bucket.name");
        System.out.println("Get file from bucket, bucket name is :" + System.getProperty("bucket.name"));

        /*Assigns Temporary credentials to IAM role
         * InstanceProfileCredentialsProvider : false does not refresh the credentials
         */
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new InstanceProfileCredentialsProvider(false))
                .build();
        try {

            System.out.println("Getting file from S3 bucket!!");
            S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucketName, keyName));
            S3ObjectInputStream objectInputStream = s3Object.getObjectContent();
            byte[] bytes = IOUtils.toByteArray(objectInputStream);
            return bytes;
        } catch (AmazonServiceException ase) {
            System.out.println("bucket name: " + bucketName);
            System.out.println("Request made to s3 bucket failed");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String deleteFile(String keyName) {

        String bucketName = System.getProperty("bucket.name");
        System.out.println("Delete file from bucket, bucket name is :" + System.getProperty("bucket.name"));

        /*Assigns Temporary credentials to IAM role
         * InstanceProfileCredentialsProvider : false does not refresh the credentials
         */
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new InstanceProfileCredentialsProvider(false))
                .build();
        try {

            System.out.println("Deleting file from S3 bucket!!");
            s3Client.deleteObject(new DeleteObjectRequest(bucketName, keyName));
            return "deleted";
        } catch (AmazonServiceException ase) {
            System.out.println("bucket name: " + bucketName);
            System.out.println("Request made to s3 bucket failed");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method converts a multipart file to File format
     *
     * @param file : Attachment
     */
    public File convertFromMultipartToProfilePic(MultipartFile file) throws Exception {
        File convFile = new File("ProfilePic");
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
}
