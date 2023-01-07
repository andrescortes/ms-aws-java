package org.myorg.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.SaveBehavior;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.myorg.db.FactoryAmazonDynamoDB;
import org.myorg.model.Product;

public class IProductRepositoryImpl implements IProductRepository {

    private final FactoryAmazonDynamoDB dynamoDB = new FactoryAmazonDynamoDB();
    private final String tableName = dynamoDB.client().listTables().getTableNames().get(0);
    private final DynamoDBScanExpression dbScanExpression = new DynamoDBScanExpression();


    @Override
    public String getProducts() {
        System.out.println("table_name = " + tableName);
        ScanRequest scanRequest = new ScanRequest().withTableName(
            tableName.equals("") || tableName.isEmpty() || tableName.isBlank() ? "products" : tableName
        );
        ScanResult scanResult;
        try {
            scanResult = dynamoDB.client().scan(scanRequest);
        } catch (Exception e) {
            throw new AmazonDynamoDBException("Failed to perform action get data");
        }
        System.out.printf("scan results: " + scanResult.toString());
//        return client.mapper().scan(Product.class, dbScanExpression);
        return scanResult.toString();
    }

    @Override
    public Product getProduct(String id) {
        return dynamoDB.mapper().load(Product.class, id);
    }

    @Override
    public Product createProduct(Product product) {
        //Set expected false for an attr - Save only if did not exist
        ExpectedAttributeValue expectedAttributeValue = new ExpectedAttributeValue();
        expectedAttributeValue.setExists(Boolean.FALSE);

        //Map the id field to the ExpectedAttributeValue
        Map<String, ExpectedAttributeValue> expectedAttributeValueMap = new HashMap<>();
        expectedAttributeValueMap.put("id", expectedAttributeValue);

        //Use the attributes within a DynamoDBSaveExpression
        DynamoDBSaveExpression saveExpression = new DynamoDBSaveExpression();
        saveExpression.setExpected(expectedAttributeValueMap);

        try {
            //Save to mapper using the saveExpression
            dynamoDB.mapper().save(product, saveExpression);
            return product;
        } catch (ConditionalCheckFailedException e) {
            throw new AmazonDynamoDBException(
                "Failed to save Product with reason: " + e.getMessage());
        }
    }

    @Override
    public Product updateProduct(String productId, Product product) {
        Product toSaved = getProduct(productId);
        if (Objects.nonNull(toSaved)) {
            toSaved.setName(product.getName());
            toSaved.setPrice(product.getPrice());
            dynamoDB.mapper().save(toSaved, DynamoDBMapperConfig.builder()
                .withSaveBehavior(SaveBehavior.UPDATE_SKIP_NULL_ATTRIBUTES)
                .build());
            return toSaved;
        }
        return null;
    }

    @Override
    public Product deleteProduct(String id) {
        Product load = dynamoDB.mapper().load(Product.class, id);
        if (Objects.nonNull(load)) {
            dynamoDB.mapper().delete(load);
            return load;
        }
        return null;
    }

    @Override
    public Optional<List<Product>> getProductByCategory(String productId, String category) {
        //values to filter as an attrs
        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":id", new AttributeValue().withS(productId));
        valueMap.put(":category", new AttributeValue().withS(category));
        //Expression to scan and execute query
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
            .withFilterExpression("id = :id and category = :category")
            .withExpressionAttributeValues(valueMap);

        return Optional.ofNullable(dynamoDB.mapper()
            .scan(Product.class, scanExpression));
    }


}
