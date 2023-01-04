package org.myorg.service;

import java.util.List;
import org.myorg.model.Product;

public interface IProductService {

    List<Product> getAllProducts();

    Product getProductById(Integer id);
}
