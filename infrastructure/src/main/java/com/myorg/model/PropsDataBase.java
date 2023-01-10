package com.myorg.model;

public class PropsDataBase {
    private String id;

    private String tableName;

    public PropsDataBase(String id, String tableName) {
        this.id = id;
        this.tableName = tableName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
