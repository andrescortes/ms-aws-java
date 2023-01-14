package org.myorg.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.myorg.db.DynamoDBFactory;
import org.myorg.model.Basket;
import org.myorg.model.transformer.Mapper;

public class IBasketRepositoryImpl implements IBasketRepository {

    private final DynamoDBFactory dynamoDB = new DynamoDBFactory();
    private final String PRIMARY_KEY = System.getenv("PRIMARY_KEY");
    private final String TABLE_NAME = System.getenv("TABLE_NAME");


    @Override
    public List<Basket> getBaskets() {

        ScanRequest scanRequest = new ScanRequest().withTableName(TABLE_NAME);
        ScanResult scanResult = dynamoDB.client().scan(scanRequest);

        return Mapper.mapToListEntity(scanResult);
    }

    @Override
    public Basket getBasket(String userName) {
        return dynamoDB.mapper().load(Basket.class, userName);
    }

    @Override
    public Basket createBasket(Basket basket) {
        //Set expected false for an attr - Save only if did not exist
        ExpectedAttributeValue expectedAttributeValue = new ExpectedAttributeValue();
        expectedAttributeValue.setExists(Boolean.FALSE);

        //Map the id field to the ExpectedAttributeValue
        Map<String, ExpectedAttributeValue> expectedAttributeValueMap = new HashMap<>();
        expectedAttributeValueMap.put("userName", expectedAttributeValue);

        //Use the attributes within a DynamoDBSaveExpression
        DynamoDBSaveExpression saveExpression = new DynamoDBSaveExpression();
        saveExpression.setExpected(expectedAttributeValueMap);

        dynamoDB.mapper().save(basket, saveExpression);
        return basket;

    }

    @Override
    public Basket deleteBasket(String userName) {
        Basket load = dynamoDB.mapper().load(Basket.class, userName);
        if (Objects.nonNull(load)) {
            dynamoDB.mapper().delete(load);
            return load;
        }
        return null;
    }

}
