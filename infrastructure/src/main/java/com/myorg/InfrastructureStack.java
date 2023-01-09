package com.myorg;

import com.myorg.apigateway.ApiGatewayFactory;
import com.myorg.db.DataBaseFactory;
import com.myorg.function.FunctionFactory;
import com.myorg.model.PropsApiGateway;
import com.myorg.model.PropsDataBase;
import com.myorg.model.PropsFunction;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.dynamodb.ITable;
import software.amazon.awscdk.services.lambda.Function;
import software.constructs.Construct;

public class InfrastructureStack extends Stack {


    public InfrastructureStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public InfrastructureStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);
        //database constructor
        DataBaseFactory dataBaseFactory = new DataBaseFactory(this, "Database",
            new PropsDataBase("products"));

        ITable productTable = dataBaseFactory.createTable();

        //function lambda constructor
        FunctionFactory functionFactory = new FunctionFactory(this, "FunctionProduct",
            new PropsFunction("ms-product",
                "com.myorg.ProductApplication", productTable.getTableName()));

        Function productFunction = functionFactory.function();

        productTable.grantWriteData(productFunction);
        new ApiGatewayFactory(this, "ApiGateway", new PropsApiGateway("APIGatewayMS", "product",
            productFunction)).ApiGateWayProduct();
    }
}
