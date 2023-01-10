package com.myorg.db;

import com.myorg.model.PropsDataBase;
import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.BillingMode;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.dynamodb.TableProps;
import software.constructs.Construct;

public class DataBaseFactory extends Construct {

    public DataBaseFactory(@NotNull Construct scope, @NotNull String id) {
        super(scope, id);
    }

    public Table createTable(@NotNull PropsDataBase propsDataBase) {

        Attribute attribute = Attribute.builder()
            .name(propsDataBase.getId())
            .type(AttributeType.STRING)
            .build();

        TableProps tableProps = TableProps.builder()
            .partitionKey(attribute)
            .tableName(propsDataBase.getTableName())
            .removalPolicy(RemovalPolicy.DESTROY)
            .billingMode(BillingMode.PAY_PER_REQUEST)
            .build();

        return new Table(this, propsDataBase.getTableName(), tableProps);
    }


}
