package org.myorg;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Map;
import org.myorg.domain.Response;

/**
 * handler to aws function callback
 */
public class ProductApplication implements RequestHandler<Map<String, String>, Response> {

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public Response handleRequest(Map<String, String> event, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Event: " + gson.toJson(event));
        return Response.builder()
            .statusCode(200)
            .headers(Map.of("Content-Type", "application/json"))
            .body("Hello from Product! You've hit Here")
            .build();
    }
}
