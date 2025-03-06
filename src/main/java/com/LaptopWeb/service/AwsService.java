//package com.LaptopWeb.service;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//import software.amazon.awssdk.core.sync.RequestBody;
//import software.amazon.awssdk.services.s3.S3Client;
//import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
//import software.amazon.awssdk.services.s3.model.PutObjectRequest;
//
//import java.io.IOException;
//import java.io.InputStream;
//
//@Slf4j
//@Service
//public class AwsService {
//
//    @Value("${cloud.aws.s3.bucket.name}")
//    private String BUCKET_NAME;
//
//    @Value("${cloud.aws.region}")
//    private String REGION;
//
//    @Autowired
//    private S3Client s3Client;
//
//    // save image
//    public String saveImageToS3(MultipartFile multipartFile, String folder) {
//        try {
//            String key = multipartFile.getOriginalFilename();
//
//            if(folder != null && !folder.isEmpty()) key = folder + "/" + key;
//
//            InputStream inputStream = multipartFile.getInputStream();
//
//            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//                    .bucket(BUCKET_NAME)
//                    .key(key)
//                    .contentType(multipartFile.getContentType())
//                    .build();
//
//            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, multipartFile.getSize()));
//            inputStream.close();
//
//            return "https://" + BUCKET_NAME + ".s3." + REGION + ".amazonaws.com/" + key;
//        } catch (IOException e) {
//            log.error("Unable to upload image to s3 bucket");
//        }
//
//        return null;
//    }
//
//
//    // delete image
//    public void deleteImageFromS3(String file) {
//        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
//                .bucket(BUCKET_NAME)
//                .key(file)
//                .build();
//
//        s3Client.deleteObject(deleteObjectRequest);
//
//        log.info(file + "removed");
//    }
//}
