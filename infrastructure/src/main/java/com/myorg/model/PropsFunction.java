package com.myorg.model;

import java.util.Map;

public class PropsFunction {
    private String projectName;

    private String functionName;

    private String tableName;
    private String handler;
    private Map<String,String> environment;
    public PropsFunction(String projectName, String functionName, String handler, String tableName,
        Map<String, String> environment) {
        this.projectName = projectName;
        this.functionName = functionName;
        this.tableName = tableName;
        this.handler = handler;
        this.environment = environment;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getProjectName() {
        return functionName;
    }

    public void setProjectName(String projectName) {
        this.functionName = projectName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public Map<String, String> getEnvironment() {
        return environment;
    }

    public void setEnvironment(Map<String, String> environment) {
        this.environment = environment;
    }
}
