package org.myorg.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.SaveBehavior;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.myorg.db.FactoryAmazonDynamoDB;
import org.myorg.model.Product;

public class IProductRepositoryImpl implements IProductRepository {

    private final FactoryAmazonDynamoDB amazonDynamoDB;
    private final DynamoDBScanExpression dbScanExpression;

    public IProductRepositoryImpl(FactoryAmazonDynamoDB amazonDynamoDB,
        DynamoDBScanExpression dbScanExpression) {
        this.amazonDynamoDB = amazonDynamoDB;
        this.dbScanExpression = dbScanExpression;
    }

    @Override
    public List<Product> getProducts() {
        return amazonDynamoDB.mapper().scan(Product.class, dbScanExpression);
    }

    @Override
    public Product getProduct(String id) {
        return amazonDynamoDB.mapper().load(Product.class, id);
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
            amazonDynamoDB.mapper().save(product, saveExpression);
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
            amazonDynamoDB.mapper().save(toSaved, DynamoDBMapperConfig.builder()
                .withSaveBehavior(SaveBehavior.UPDATE_SKIP_NULL_ATTRIBUTES)
                .build());
            return toSaved;
        }
        return null;
    }

    @Override
    public Product deleteProduct(String id) {
        Product load = amazonDynamoDB.mapper().load(Product.class, id);
        if (Objects.nonNull(load)) {
            amazonDynamoDB.mapper().delete(load);
            return load;
        }
        return null;
    }


}
