package org.myorg.db;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;

public class FactoryAmazonDynamoDB {

    private Regions REGION = Regions.US_EAST_1;

    private AmazonDynamoDB amazonDynamoDB() {
        return AmazonDynamoDBClientBuilder.standard()
            .withRegion(REGION)
            .build();
    }

    public DynamoDBMapper mapper() {
        try {
            return new DynamoDBMapper(this.amazonDynamoDB());
        } catch (Exception e) {
            throw new AmazonDynamoDBException(
                "No was created a instance of " + DynamoDBMapper.class);
        }
    }

}
