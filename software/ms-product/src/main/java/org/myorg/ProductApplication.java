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
import java.util.Map;
import java.util.UUID;
import org.myorg.model.Product;
import org.myorg.service.IProductService;

/**
 * handler to aws function callback
 */
public class ProductApplication implements
    RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final IProductService service;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public ProductApplication(IProductService service) {
        this.service = service;
    }

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
        // TODO: 1/5/2023 Get Method product/1234?category=Phone queryParams
        switch (event.getHttpMethod()) {
            case "GET": {
                if (event.getPathParameters() != null) {
                    Map<String, String> pathParameters = event.getPathParameters();
                    logger.log("GET method Operation: getProductById" + pathParameters.get("id"));
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
                response.withStatusCode(201).withBody(gson.toJson(service.createProduct(product)));
                break;
            }
            case "PUT": {
                logger.log("PUT method Operation: createProduct = " + gson.toJson(event.getBody())
                    + ", id: " + event.getPathParameters().get("id"));
                Product product;
                String productId;
                try {
                    product = gson.fromJson(event.getBody(), Product.class);
                    productId = event.getPathParameters().get("id");
                    logger.log("productRequest: " + gson.toJson(product) + ", id: " + productId);
                } catch (GenericSignatureFormatError error) {
                    throw new AmazonClientException(
                        "Failed when unmarshall with reason: " + error.getMessage());
                }
                response
                    .withBody(gson.toJson(service.updateProduct(productId, product)));
                break;
            }

            case "DELETE": {
                logger.log("DELETE method Operation: deleteProductById" + event.getPathParameters()
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
    }
}
