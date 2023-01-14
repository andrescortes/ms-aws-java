package org.myorg;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.myorg.model.Basket;
import org.myorg.service.IBasketServiceImpl;

/**
 * Hello world!
 */
public class BasketApplication implements
    RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final IBasketServiceImpl service = new IBasketServiceImpl();

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
                    if (event.getPathParameters() != null) {
                        Map<String, String> pathParameters = event.getPathParameters();
                        logger.log(
                            "GET method Operation: getBasketByUserName" + pathParameters.get(
                                "userName"));
                        response.withBody(gson.toJson(
                            service.getBasketById(pathParameters.get("userName"))));
                    } else {
                        logger.log("GET method Operation: getAllBaskets");
                        response.withBody(gson.toJson(service.getAllBaskets()));
                    }
                    break;
                }
                case "POST": {
                    if (event.getPath() == "/basket/checkout") {
                        Basket product = gson.fromJson(event.getBody(), Basket.class);
                        product.setUserName(UUID.randomUUID().toString());
                        response.withStatusCode(201)
                            .withBody(gson.toJson(service.createBasket(product)));
                    } else {
                        response.withBody("").withStatusCode(401);
                    }
                    break;
                }

                case "DELETE": {
                    logger.log("DELETE method Operation");
                    String userName = event.getPathParameters().get("userName");
                    response.withStatusCode(204)
                        .withBody(gson.toJson(service.deleteBasket(userName)));
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
