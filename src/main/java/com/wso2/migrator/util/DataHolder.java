package com.wso2.migrator.util;

import com.wso2.migrator.model.Configuration;
import com.zaxxer.hikari.HikariDataSource;

import java.util.HashMap;
import java.util.Map;

public class DataHolder {
    private static Configuration configuration;
    private static Map<String,HikariDataSource> apimgtDataSources = new HashMap<>();

    private DataHolder() {
    }

    public static Configuration getConfiguration() {
        return configuration;
    }

    public static void setConfiguration(Configuration configuration) {
        DataHolder.configuration = configuration;
    }
    public static void addAPIMgtDataSource(String name,HikariDataSource dataSource){
        apimgtDataSources.put(name,dataSource);
    }
    public static HikariDataSource getAPIMgtDataSource(String name){
        return apimgtDataSources.get(name);
    }

    public static Map<String, HikariDataSource> getApimgtDataSources() {

        return apimgtDataSources;
    }
}
