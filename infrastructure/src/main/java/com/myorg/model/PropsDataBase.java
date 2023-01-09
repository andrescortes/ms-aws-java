package com.myorg.model;

public class PropsDataBase {

    private String tableName;

    public PropsDataBase(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
