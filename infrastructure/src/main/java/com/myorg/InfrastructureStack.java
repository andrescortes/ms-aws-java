package com.myorg;

import static java.util.Collections.singletonList;
import static software.amazon.awscdk.BundlingOutput.ARCHIVED;

import java.util.Arrays;
import java.util.List;
import software.amazon.awscdk.BundlingOptions;
import software.amazon.awscdk.DockerVolume;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigateway.LambdaRestApi;
import software.amazon.awscdk.services.apigateway.LambdaRestApiProps;
import software.amazon.awscdk.services.apigateway.Resource;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.BillingMode;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.dynamodb.TableProps;
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
            "cd ms-product " +
                "&& mvn clean install " +
                "&& cp /asset-input/ms-product/target/ms-product.jar /asset-output/"
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

        Function productFunction = new Function(this, "FunctionTestJava", FunctionProps.builder()
            .runtime(Runtime.JAVA_11)
            .code(Code.fromAsset("../software/", AssetOptions.builder()
                .bundling(builderOptions
                    .command(functionPackagingInstructions)
                    .build())
                .build()))
            .handler("org.myorg.ProductApplication")
            .memorySize(1024)
            .timeout(Duration.seconds(10))
            .logRetention(RetentionDays.FIVE_DAYS)
            .build());

        Table productTable = new Table(this, "products", TableProps.builder()
            .partitionKey(Attribute.builder()
                .name("id")
                .type(AttributeType.STRING)
                .build())
            .tableName("products")
            .removalPolicy(RemovalPolicy.DESTROY)
            .billingMode(BillingMode.PAY_PER_REQUEST)
            .build());
        productTable.grantWriteData(productFunction);

        LambdaRestApi apiGatewayMS = new LambdaRestApi(this, "APIGatewayMS",
            LambdaRestApiProps.builder()
                .proxy(false)
                .handler(productFunction)
                .build());

        Resource product = apiGatewayMS.getRoot().addResource("product");
        product.addMethod("GET");
        product.addMethod("POST");

        Resource singleProduct = product.addResource("{id}");
        singleProduct.addMethod("GET");
        singleProduct.addMethod("PUT");
        singleProduct.addMethod("DELETE");
    }
}
