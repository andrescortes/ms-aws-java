package org.myorg;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.lang.reflect.GenericSignatureFormatError;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.myorg.model.Product;
import org.myorg.service.IProductServiceImpl;

/**
 * handler to aws function callback
 */
public class ProductApplication implements
    RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final IProductServiceImpl service = new IProductServiceImpl();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();


    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event,
        Context context) {
        LambdaLogger logger = context.getLogger();

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
            .withStatusCode(200)
            .withIsBase64Encoded(false)
            .withHeaders(headers);

        try {
            switch (event.getHttpMethod()) {
                case "GET": {
                    if (event.getQueryStringParameters() != null) {
                        // Get Method product/1234?category=Phone queryParams
                        Map<String, String> pathParameters = event.getPathParameters();
                        Map<String, String> queryStringParameters = event.getQueryStringParameters();
                        String productId = pathParameters.get("id");
                        String category = queryStringParameters.get("category");
                        Optional<List<Product>> products = service.getProductByCategory(productId,
                            category);
                        logger.log("\nGET method Operation with queryParams productId: " + productId
                            + ", category: " + category);
                        logger.log("\nResult of query is: " + gson.toJson(products.get()));
                        if (products.isPresent()) {
                            response.withBody(gson.toJson(products));
                        } else {
                            response
                                .withStatusCode(404)
                                .withBody("Not found with productId: " + productId + ", category: "
                                    + category);
                        }
                    } else if (event.getPathParameters() != null) {
                        Map<String, String> pathParameters = event.getPathParameters();
                        logger.log(
                            "GET method Operation: getProductById" + pathParameters.get("id"));
                        response.withBody(gson.toJson(
                            service.getProductById(pathParameters.get("id"))));
                    } else {
                        logger.log("GET method Operation: getAllProducts");
                        response.withBody(gson.toJson(service.getAllProducts()));
                    }
                    break;
                }
                case "POST": {
                    Product product;
                    logger.log(
                        "POST method Operation: createProduct = " + gson.toJson(event.getBody()));
                    try {
                        product = gson.fromJson(event.getBody(), Product.class);
                        product.setId(UUID.randomUUID().toString());
                        logger.log("productRequest: " + gson.toJson(product));
                    } catch (GenericSignatureFormatError error) {
                        throw new AmazonClientException(
                            "Failed when unmarshall with reason: " + error.getMessage());
                    }
                    response.withStatusCode(201)
                        .withBody(gson.toJson(service.createProduct(product)));
                    break;
                }
                case "PUT": {
                    logger.log(
                        "PUT method Operation: createProduct = " + gson.toJson(event.getBody())
                            + ", id: " + event.getPathParameters().get("id"));
                    Product product;
                    String productId;
                    try {
                        product = gson.fromJson(event.getBody(), Product.class);
                        productId = event.getPathParameters().get("id");
                        logger.log(
                            "productRequest: " + gson.toJson(product) + ", id: " + productId);
                    } catch (GenericSignatureFormatError error) {
                        throw new AmazonClientException(
                            "Failed when unmarshall with reason: " + error.getMessage());
                    }
                    response
                        .withBody(gson.toJson(service.updateProduct(productId, product)));
                    break;
                }

                case "DELETE": {
                    logger.log(
                        "DELETE method Operation: deleteProductById" + event.getPathParameters()
                            .get("id"));
                    Product product;
                    try {
                        product = gson.fromJson(event.getBody(), Product.class);
                        logger.log("productRequest: " + gson.toJson(product));
                    } catch (GenericSignatureFormatError error) {
                        throw new AmazonClientException(
                            "Failed when unmarshall with reason: " + error.getMessage());
                    }
                    response.withStatusCode(204)
                        .withBody(gson.toJson(service.deleteProduct(product.getId())));
                    break;
                }
                default: {
                    logger.log("Not found method, resource: " + event.getResource() + ", path: "
                        + event.getPath());
                    throw new AmazonClientException("Unsupported route: " + event.getHttpMethod());
                }
            }
            return response;
        } catch (AmazonClientException e) {
            Map<String, String> errors = new HashMap<>();
            errors.put("message", "Failed to perform operation.");
            errors.put("errMsg", e.getMessage());
            errors.put("errorStack", e.getLocalizedMessage());
            return response
                .withStatusCode(500)
                .withBody(gson.toJson(errors));
        }
    }
}
