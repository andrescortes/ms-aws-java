package org.myorg.model.transformer;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.myorg.model.Product;

public class Mapper {

    private final Gson gson = new Gson();

    public static List<Product> mapToListEntity(ScanResult scanResult) {
        List<Product> products = new ArrayList<>();
        for (Map<String, AttributeValue> item : scanResult.getItems()) {
            System.out.println("Item: " + item);
            AttributeValue id = item.getOrDefault("id", new AttributeValue());
            AttributeValue name = item.getOrDefault("name", new AttributeValue());
            AttributeValue description = item.getOrDefault("description", new AttributeValue());
            AttributeValue price = item.getOrDefault("price", new AttributeValue());
            AttributeValue imageFile = item.getOrDefault("imageFile", new AttributeValue());
            AttributeValue category = item.getOrDefault("category", new AttributeValue());
            Product product = new Product();
            product.setId(id.getS());
            product.setName(name.getS());
            product.setDescription(description.getS());
            product.setPrice(Double.parseDouble(price.getN()));
            product.setImageFile(imageFile.getS());
            product.setCategory(category.getS());
            products.add(product);
        }
        return products;
    }
}
