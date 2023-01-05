package org.myorg.service;

import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.myorg.model.Product;
import org.myorg.repository.IProductRepository;

public class IProductServiceImpl implements IProductService {

    private final IProductRepository repository;

    public IProductServiceImpl(IProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Product> getAllProducts() {
        List<Product> products = repository.getProducts();
        return products.size() > 0 ? products : Collections.emptyList();
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
}
