package com.myorg.apigateway;

import com.myorg.model.PropsApiGateway;
import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.services.apigateway.LambdaRestApi;
import software.amazon.awscdk.services.apigateway.LambdaRestApiProps;
import software.constructs.Construct;

public class ApiGatewayFactory extends Construct {

    public ApiGatewayFactory(@NotNull Construct scope,
        @NotNull String id) {
        super(scope, id);
    }

    public LambdaRestApi createApiGateWay(@NotNull PropsApiGateway propsApiGateway) {
        return new LambdaRestApi(this, propsApiGateway.getName(),
            LambdaRestApiProps.builder()
                .proxy(false)
                .handler(propsApiGateway.getHandler())
                .build());
    }

}
