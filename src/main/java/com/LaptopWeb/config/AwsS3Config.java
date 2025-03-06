package com.LaptopWeb.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsS3Config {

    @Value("${cloud.aws.credentials.access-key}")
    private String ACCESS_KEY;

    @Value("${cloud.aws.credentials.secret-key}")
    private String SECRET_KEY;

    @Value("${cloud.aws.region}")
    private String REGION;

    @Bean
    public AmazonS3 amazonS3() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);

        // build an amazon s3 client with credential and region
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(Regions.AP_SOUTHEAST_2)
                .build();
    }

}
