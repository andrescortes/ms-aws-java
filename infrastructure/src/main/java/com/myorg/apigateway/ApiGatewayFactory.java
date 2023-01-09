package com.myorg.apigateway;

import com.myorg.model.PropsApiGateway;
import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.services.apigateway.LambdaRestApi;
import software.amazon.awscdk.services.apigateway.LambdaRestApiProps;
import software.amazon.awscdk.services.apigateway.Resource;
import software.constructs.Construct;

public class ApiGatewayFactory extends Construct {

    private final PropsApiGateway propsApiGateway;

    public ApiGatewayFactory(@NotNull Construct scope,
        @NotNull String id, @NotNull PropsApiGateway propsApiGateway) {
        super(scope, id);
        this.propsApiGateway = propsApiGateway;
    }

    public void ApiGateWayProduct() {
        LambdaRestApi apiGatewayMS = new LambdaRestApi(this, propsApiGateway.getName(),
            LambdaRestApiProps.builder()
                .proxy(false)
                .handler(propsApiGateway.getHandler())
                .build());

        Resource product = apiGatewayMS.getRoot().addResource(propsApiGateway.getPathRoot());
        product.addMethod("GET");
        product.addMethod("POST");

        Resource singleProduct = product.addResource("{id}");
        singleProduct.addMethod("GET");
        singleProduct.addMethod("PUT");
        singleProduct.addMethod("DELETE");
    }

}
