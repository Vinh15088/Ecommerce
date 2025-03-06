//package com.LaptopWeb.config;
//
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
//import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
//import software.amazon.awssdk.regions.Region;
//import software.amazon.awssdk.services.s3.S3Client;
//
//@Configuration
//public class StorageConfig {
//
//    @Value("${cloud.aws.credentials.access-key}")
//    private String ACCESS_KEY;
//
//    @Value("${cloud.aws.credentials.secret-key}")
//    private String SECRET_KEY;
//
//    @Value("${cloud.aws.region}")
//    private String REGION;
//
//    @Bean
//    public S3Client s3Client() {
//        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY);
//
//        return S3Client.builder()
//                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
//                .region(Region.of(REGION))
//                .build();
//    }
//
//}
