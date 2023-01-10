package com.myorg.function;

import static java.util.Collections.singletonList;
import static software.amazon.awscdk.BundlingOutput.ARCHIVED;

import com.myorg.model.PropsFunction;
import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.BundlingOptions;
import software.amazon.awscdk.DockerVolume;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.services.lambda.AssetCode;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.FunctionProps;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.s3.assets.AssetOptions;
import software.constructs.Construct;

public class FunctionFactory extends Construct {

    public FunctionFactory(@NotNull Construct scope, @NotNull String id) {
        super(scope, id);
    }

    public Function createFunction(@NotNull PropsFunction propsFunction) {
        AssetOptions assetOptions = AssetOptions.builder()
            .bundling(bundlingOptions(packagingInstructionsToFunctionProduct(
                propsFunction.getProjectName()))
                .command(packagingInstructionsToFunctionProduct(propsFunction.getProjectName()))
                .build()
            )
            .build();

        AssetCode assetCode = Code.fromAsset("../software/", assetOptions);

        FunctionProps functionProps = FunctionProps.builder()
            .runtime(Runtime.JAVA_11)
            .code(assetCode)
            .handler(propsFunction.getHandler())
            .memorySize(512)
            .timeout(Duration.seconds(30))
            .environment(propsFunction.getEnvironment())
            .build();

        return new Function(this, "Function", functionProps);
    }

    private BundlingOptions.Builder bundlingOptions(List<String> packagingInstructions) {
        return BundlingOptions.builder()
            .command(packagingInstructions)
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
    }

    private List<String> packagingInstructionsToFunctionProduct(String projectName) {
        return Arrays.asList(
            "/bin/sh",
            "-c",
            "cd " + projectName +
                " && mvn clean install " +
                "&& cp /asset-input/" + projectName + "/target/" + projectName
                + ".jar /asset-output/"
        );
    }
}
