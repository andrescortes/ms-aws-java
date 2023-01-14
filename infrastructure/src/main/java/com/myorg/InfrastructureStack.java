package com.myorg;

import com.myorg.apigateway.ApiGatewayFactory;
import com.myorg.db.DataBaseFactory;
import com.myorg.function.FunctionFactory;
import com.myorg.model.PropsApiGateway;
import com.myorg.model.PropsDataBase;
import com.myorg.model.PropsFunction;
import java.util.Map;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigateway.LambdaRestApi;
import software.amazon.awscdk.services.apigateway.Resource;
import software.amazon.awscdk.services.dynamodb.ITable;
import software.amazon.awscdk.services.lambda.Function;
import software.constructs.Construct;

public class InfrastructureStack extends Stack {


    public InfrastructureStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public InfrastructureStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);
        // database constructor
        DataBaseFactory dataBaseFactory = new DataBaseFactory(this, "DatabaseFactory");

        // tables
        ITable productTable = dataBaseFactory.createTable(new PropsDataBase("id", "products"));
        ITable basketTable = dataBaseFactory.createTable(new PropsDataBase("userName", "basket"));

        // function lambda constructor
        FunctionFactory functionFactory = new FunctionFactory(this, "FunctionFactory");

        // functions
        Function productFunction = functionFactory.createFunction(
            new PropsFunction("ms-product", "FunctionProduct", "org.myorg.ProductApplication",
                productTable.getTableName(),
                Map.of("PRIMARY_KEY", "id", "TABLE_NAME", "products")));
        Function basketFunction = functionFactory.createFunction(
            new PropsFunction("ms-basket", "FunctionBasket", "org.myorg.BasketApplication",
                basketTable.getTableName(),
                Map.of("PRIMARY_KEY", "userName", "TABLE_NAME", "basket")));

        // giving permissions to function, CRUD
        productTable.grantWriteData(productFunction);
        basketTable.grantReadWriteData(basketFunction);

        // creating an apiGateway with function parameter
        ApiGatewayFactory apiGatewayFactory = new ApiGatewayFactory(this, "ApiGatewayFactory");

        // apiGateway to product
        LambdaRestApi apiGatewayMSProduct = apiGatewayFactory.createApiGateWay(
            new PropsApiGateway("APIGatewayMSProduct", productFunction));

        // resources and methods to product
        Resource product = apiGatewayMSProduct.getRoot().addResource("product");
        product.addMethod("GET");
        product.addMethod("POST");

        Resource singleProduct = product.addResource("{id}");
        singleProduct.addMethod("GET");
        singleProduct.addMethod("PUT");
        singleProduct.addMethod("DELETE");

        // apiGateway to basket
        LambdaRestApi apiGatewayMSBasket = apiGatewayFactory.createApiGateWay(
            new PropsApiGateway("APIGatewayMSBasket", basketFunction));
        // resources and methods to basket
        Resource basket = apiGatewayMSBasket.getRoot().addResource("basket");
        basket.addMethod("GET");//GET /basket
        basket.addMethod("POST");//POST /basket

        Resource singleBasket = basket.addResource("{userName}");
        singleBasket.addMethod("GET");
        singleBasket.addMethod("DELETE");

        Resource basketCheckout = basket.addResource("checkout");
        basketCheckout.addMethod("POST");// POST /basket/checkout
        // expected request payload : { userName: swn }
    }
}
