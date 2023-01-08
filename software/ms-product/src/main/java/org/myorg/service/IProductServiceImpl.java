package org.myorg.service;

import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.myorg.model.Product;
import org.myorg.repository.IProductRepositoryImpl;

public class IProductServiceImpl implements IProductService {

    private final IProductRepositoryImpl repository = new IProductRepositoryImpl();



    @Override
    public List<Product> getAllProducts() {
        return repository.getProducts();
    }

    @Override
    public Product getProductById(String id) {
        Product product = repository.getProduct(id);

        if (Objects.isNull(product)) {
            throw new IllegalArgumentException("No found product with id " + id);
        }
        return product;
    }

    @Override
    public Product createProduct(Product product) {
        return repository.createProduct(product);
    }

    @Override
    public Product updateProduct(String productId, Product product) {
        Product productToResponse = repository.updateProduct(productId, product);
        if (Objects.isNull(productToResponse)) {
            throw new AmazonDynamoDBException("Error at update operation");
        }
        return productToResponse;
    }

    @Override
    public Product deleteProduct(String productId) {
        return repository.deleteProduct(productId);
    }

    @Override
    public Optional<List<Product>> getProductByCategory(String productId, String category) {
        return repository.getProductByCategory(productId, category);
    }
}
