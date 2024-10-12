package com.LaptopWeb.service;

import com.LaptopWeb.exception.AppException;
import com.LaptopWeb.exception.ErrorApp;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class AwsS3Service {

    private final String BUCKET_NAME = "ecommerce-vinhseo";

    @Autowired
    private AmazonS3 amazonS3;

    // save imgage to s3
    public String saveImageToS3(MultipartFile photo, String folder) throws Exception {

        try {
            // get original filename
            String s3FileName = folder + "/" + photo.getOriginalFilename();

            // abtain an inputStream from photo file upload
            InputStream inputStream = photo.getInputStream();

            // create metadata for object -> jpeg image
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/jpeg");

            // create putObjectRequest
            PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, s3FileName, inputStream, metadata);

            // upload file to the s3
            amazonS3.putObject(putObjectRequest);

            // return url of uploaded image
            return "https://" + BUCKET_NAME + ".s3/amazonaws.com/" + s3FileName;
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new Exception("Unable to upload image to s3 bucket: " + e.getMessage());
        }
    }

    public String updateImageInS3(MultipartFile newPhoto, String folder, String oldFileName) throws Exception {
        try {
            // delete oldPhoto
            deleteImageFromS3(folder, oldFileName);

            // upload newPhoto
            return saveImageToS3(newPhoto, folder);
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new Exception("Unable to upload image to s3 bucket: " + e.getMessage());
        }
    }

    public void deleteImageFromS3(String folder, String fileName) throws Exception {
        try {
            String urlFile = folder + "/" + fileName;

            // delete obj from s3
            amazonS3.deleteObject(BUCKET_NAME, urlFile);

            System.out.println("delete success");
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new Exception("Unable to upload image to s3 bucket: " + e.getMessage());
        }
    }

}
