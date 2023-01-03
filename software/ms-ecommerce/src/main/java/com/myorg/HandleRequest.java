package com.myorg;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Map;

public class HandleRequest implements RequestHandler<Map<String, String>, String> {

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public String handleRequest(Map<String, String> event, Context context) {

        LambdaLogger logger = context.getLogger();
        logger.log("Event: " + gson.toJson(event));
        logger.log("Event type: " + gson.toJson(event.getClass().toString()));
        return "200";
    }
}
