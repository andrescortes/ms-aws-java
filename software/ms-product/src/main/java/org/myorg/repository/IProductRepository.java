package org.myorg.repository;

import java.util.List;
import org.myorg.model.Product;

public interface IProductRepository {

    List<Product> getProducts();

    Product getProduct(String id);

    Product createProduct(Product product);

    Product updateProduct(String productId, Product product);

    Product deleteProduct(String id);
}
