package org.myorg.service;

import java.util.List;
import org.myorg.model.Product;

public interface IProductService {

    List<Product> getAllProducts();

    Product getProductById(String id);

    Product createProduct(Product product);

    Product updateProduct(String productId, Product product);

    Product deleteProduct(String productId);
}
