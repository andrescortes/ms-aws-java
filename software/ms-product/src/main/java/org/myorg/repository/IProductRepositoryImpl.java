package org.myorg.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.SaveBehavior;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.myorg.db.FactoryAmazonDynamoDB;
import org.myorg.model.Product;
import org.myorg.model.transformer.Mapper;

public class IProductRepositoryImpl implements IProductRepository {

    private final FactoryAmazonDynamoDB dynamoDB = new FactoryAmazonDynamoDB();
    private final String PRIMARY_KEY = System.getenv("PRIMARY_KEY");
    private final String TABLE_NAME = System.getenv("TABLE_NAME");


    @Override
    public List<Product> getProducts() {

        ScanRequest scanRequest = new ScanRequest().withTableName(TABLE_NAME);
        ScanResult scanResult = dynamoDB.client().scan(scanRequest);

        return Mapper.mapToListEntity(scanResult);
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

        dynamoDB.mapper().save(product, saveExpression);
        return product;

    }

    @Override
    public Product updateProduct(String productId, Product product) {
        Product toSaved = getProduct(productId);
        if (Objects.nonNull(toSaved)) {
            toSaved.setName(product.getName());
            toSaved.setPrice(product.getPrice());
            toSaved.setCategory(product.getCategory());
            toSaved.setDescription(product.getDescription());
            toSaved.setImageFile(product.getImageFile());
            dynamoDB.mapper().save(toSaved, DynamoDBMapperConfig.builder()
                .withSaveBehavior(SaveBehavior.UPDATE_SKIP_NULL_ATTRIBUTES).build());
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
    public PaginatedScanList<Product> getProductByCategory(String category) {
        //values to filter as an attrs
        Map<String, AttributeValue> valueMap = new HashMap<>();
//        valueMap.put(":id", new AttributeValue().withS(productId));
        valueMap.put(":category", new AttributeValue().withS(category));
        //Expression to scan and execute query
        /*DynamoDBScanExpression scanExpression = new DynamoDBScanExpression().withFilterExpression(
            "id = :id and category = :category").withExpressionAttributeValues(valueMap);*/
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression().withFilterExpression(
            "category = :category").withExpressionAttributeValues(valueMap);

        return dynamoDB.mapper().scan(Product.class, scanExpression);
    }


}
