package com.myorg;

import static java.util.Collections.singletonList;
import static software.amazon.awscdk.BundlingOutput.ARCHIVED;

import java.util.Arrays;
import java.util.List;
import software.amazon.awscdk.BundlingOptions;
import software.amazon.awscdk.DockerVolume;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigateway.LambdaRestApi;
import software.amazon.awscdk.services.apigateway.LambdaRestApiProps;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.FunctionProps;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.s3.assets.AssetOptions;
import software.constructs.Construct;

public class InfrastructureStack extends Stack {
    public InfrastructureStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public InfrastructureStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        List<String> functionPackagingInstructions = Arrays.asList(
            "/bin/sh",
            "-c",
            "cd ms-ecommerce " +
                "&& mvn clean" +
                "&& mvn clean install " +
                "&& cp /asset-input/ms-ecommerce/target/ms-ecommerce.jar /asset-output/"
        );

        BundlingOptions.Builder builderOptions = BundlingOptions.builder()
            .command(functionPackagingInstructions)
            .image(Runtime.JAVA_11.getBundlingImage())
            .volumes(singletonList(
                // Mount local .m2 repo to avoid download all the dependencies again inside the container
                DockerVolume.builder()
                    .hostPath(System.getProperty("user.home") + "/.m2/")
                    .containerPath("/root/.m2/")
                    .build()
            ))
            .user("root")
            .outputType(ARCHIVED);

        Function function = new Function(this, "FunctionTestJava", FunctionProps.builder()
            .runtime(Runtime.JAVA_11)
            .code(Code.fromAsset("../software/", AssetOptions.builder()
                .bundling(builderOptions
                    .command(functionPackagingInstructions)
                    .build())
                .build()))
            .handler("com.myorg.HandleRequest")
            .memorySize(1024)
            .timeout(Duration.seconds(10))
            .logRetention(RetentionDays.FIVE_DAYS)
            .build());

        new LambdaRestApi(this, "APIGatewayJava", LambdaRestApiProps.builder()
            .proxy(true)
            .handler(function)
            .build());
    }
}
