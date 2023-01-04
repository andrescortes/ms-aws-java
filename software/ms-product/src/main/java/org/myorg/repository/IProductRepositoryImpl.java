package org.myorg.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import java.util.List;
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
    public Product getProduct(Integer id) {
        return amazonDynamoDB.mapper().load(Product.class, id);
    }
}
