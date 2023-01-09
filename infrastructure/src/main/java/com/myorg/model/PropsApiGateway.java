package com.myorg.model;

import software.amazon.awscdk.services.lambda.Function;

public class PropsApiGateway {

    private String name;
    private String pathRoot;
    private Function handler;

    public PropsApiGateway(String name, String pathRoot, Function handler) {
        this.name = name;
        this.pathRoot = pathRoot;
        this.handler = handler;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPathRoot() {
        return pathRoot;
    }

    public void setPathRoot(String pathRoot) {
        this.pathRoot = pathRoot;
    }

    public Function getHandler() {
        return handler;
    }

    public void setHandler(Function handler) {
        this.handler = handler;
    }
}
