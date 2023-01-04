package org.myorg;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.HashMap;
import java.util.Map;
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
        logger.log("\nEvent: " + gson.toJson(event.getBody()));
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
            .withStatusCode(200)
            .withIsBase64Encoded(false)
            .withHeaders(headers);

        switch (event.getHttpMethod()) {
            case "GET": {
                if (event.getPathParameters() != null) {
                    Map<String, String> pathParameters = event.getPathParameters();
                    logger.log("pathParameters: " + gson.toJson(pathParameters));
                    return response.withBody(new Gson().toJson(
                        service.getProductById(Integer.valueOf(pathParameters.get("id")))));
                } else {
                    return response.withBody(new Gson().toJson(service.getAllProducts()));
                }
            }
            default: {
                return response.withBody("No found data!");
            }
        }
    }
}
