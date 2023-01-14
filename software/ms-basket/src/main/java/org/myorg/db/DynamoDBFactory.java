package org.myorg.db;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class DynamoDBFactory {

    static String accessKey = "";
    static String secretKey = "";

    private AWSCredentialsProvider awsCredentialsProvider() {
        return new AWSStaticCredentialsProvider(
            new BasicAWSCredentials(accessKey, secretKey)
        );
    }

    public AmazonDynamoDB client() {
        return AmazonDynamoDBClientBuilder
            .standard()
            .withRegion(Regions.US_EAST_1)
            .withCredentials(
                this.awsCredentialsProvider()
            )
            .build();
    }

    public DynamoDBMapper mapper() {
        return new DynamoDBMapper(this.client());
    }
}
