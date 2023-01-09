package com.myorg.model;

public class PropsFunction {

    private String projectName;
    private String tableName;
    private String handler;

    public PropsFunction(String projectName, String tableName, String handler) {
        this.projectName = projectName;
        this.tableName = tableName;
        this.handler = handler;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
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
}
