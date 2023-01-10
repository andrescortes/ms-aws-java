package com.myorg.model;

import software.amazon.awscdk.services.lambda.Function;

public class PropsApiGateway {

    private String name;
    private Function handler;

    public PropsApiGateway(String name, Function handler) {
        this.name = name;
        this.handler = handler;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Function getHandler() {
        return handler;
    }

    public void setHandler(Function handler) {
        this.handler = handler;
    }
}
