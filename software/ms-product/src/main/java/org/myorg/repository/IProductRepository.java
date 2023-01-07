package org.myorg.repository;

import java.util.List;
import java.util.Optional;
import org.myorg.model.Product;

public interface IProductRepository {

    String getProducts();

    Product getProduct(String id);

    Product createProduct(Product product);

    Product updateProduct(String productId, Product product);

    Product deleteProduct(String id);

    Optional<List<Product>> getProductByCategory(String productId, String category);
}
