package org.myorg.repository;

import java.util.List;
import org.myorg.model.Product;

public interface IProductRepository {

    List<Product> getProducts();

    Product getProduct(Integer id);

}
