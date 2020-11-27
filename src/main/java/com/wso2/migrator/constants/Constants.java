package com.wso2.migrator.constants;

public class Constants {

    public static final String CONFIGURATION_FILE = "config.yaml";
    public static final String MIGRATOR_TOOL_CONFIG_FOLDER = ".apimigrator";
    public static final String ADD_ENVIRONMENT = "add-env";
    public static final String API_MIGRATOR_CLIENT_NAME = "WSO2-API-Migrator";
    public static final int HTTP_STATUS_OK = 200;
    public static final String PASSWORD_GRANT_TYPE = "password";
    public static final String PUBLISHER_SCOPES = "apim:api_view apim:api_create apim:api_publish " +
            "apim:tier_view apim:tier_manage apim:subscription_view apim:subscription_block " +
            "apim:mediation_policy_view apim:api_workflow apim:app_import_export";
    public static final String CLIENT_TRUST_STORE_JKS_FILE = "client-truststore.jks";
    public static final String EXPORTD_APPS_DIRECTORY = "apps";
    public static final int HTTP_STATUS_CREATED = 201;
    public static final String REMOVE_ENVIRONMENT = "remove-env";
    public static final String LIST_ENVIRONMENT = "list-env";
    public static final char ZIP_FILE_SEPARATOR = '/';
    public static final String ARCHIVE_NAME = "exported.zip";
    public static final String MIGRATOR_HOME = "migrator.home";
    public static final String IMPORT_EXPORT_APP_ACTION = "import-export-app";
    public static final String ADD_TENANT = "add-tenant";
    public static final String LIST_TENANTS = "list-tenants";
    public static final String REMOVE_TENANT = "remove-tenant";
}
