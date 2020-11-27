package com.wso2.migrator.invoker.general;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.wso2.migrator.exception.APIMigrationException;
import com.wso2.migrator.model.Configuration;
import com.wso2.migrator.model.DCRResponse;
import com.wso2.migrator.model.Environment;
import com.wso2.migrator.util.DataHolder;
import com.wso2.migrator.util.Util;
import com.wso2.migrator.validators.EnvironmentNonExistenceValidator;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@Parameters(commandNames = "add-env", commandDescription = "Add Environment")
public class AddEnvironment extends Helper {

    private static final Logger LOGGER = Logger.getLogger(AddEnvironment.class);

    @Parameter(names = {"--environment"}, description = "Name of Environment", descriptionKey = "dev", validateWith =
            EnvironmentNonExistenceValidator.class, required = true)
    private String environmentName;
    @Parameter(names = {"--import-export"}, description = "Import-Export Url", descriptionKey = "v2")
    private String importExportUrl;
    @Parameter(names = {"--api_list"}, description = "APi listing url",required = true)
    private String apiListingUrl;
    @Parameter(names = {"--username"}, description = "Super Admin username",required = true)
    private String username;
    @Parameter(names = {"--password"}, description = "Super Admin password",required = true)
    private String password;
    @Parameter(names = {"--registration"}, description = "Registration endpoint",required = true)
    private String registrationEndpoint;
    @Parameter(names = {"--base-url"}, description = "Base url",required = true)
    private String baseUrl;
    @Parameter(names = {"--token"}, description = "Token endpoint",required = true)
    private String tokenEndpoint;
    @Parameter(names = {"--admin-endpoint"}, description = "Admin RestAPI Endpoint",required = true)
    private String adminRestAPIEndpoint;
    @Parameter(names = {"--apimgtdb-url"}, description = "APIM database url",required = true)
    private String apimgtDbUrl;
    @Parameter(names = {"--apimgtdb-user"}, description = "APIM database user",required = true)
    private String apimgtDbUser;
    @Parameter(names = {"--apimgtdb-password"}, description = "APIM database password",required = true)
    private String apimgtdbPassword;
    @Parameter(names = {"--insecure"}, description = "Insecure Connection")
    private boolean hostnameVerification;
    @Parameter(names = {"--verbose", "-v"}, description = "Verbose output of Log")
    private boolean enableVerboseOutput;
    public void invoke() throws APIMigrationException {

        Environment environment = new Environment();
        environment.setApiImportExportEndpoint(importExportUrl);
        environment.setDynamicClientRegistrationEndpoint(registrationEndpoint);
        environment.setTokenEndpoint(tokenEndpoint);
        environment.setUsername(username);
        environment.setPassword(password);
        environment.setBaseURl(baseUrl);
        environment.setPublisherRestAPIEndpoint(apiListingUrl);
        environment.setAdminRestAPIEndpoint(adminRestAPIEndpoint);
        environment.setApimgtDBURl(apimgtDbUrl);
        environment.setApimgtDBUsername(apimgtDbUser);
        environment.setApimgtDBPassword(apimgtdbPassword);
        Configuration configuration;
        if (Util.configurationExist()) {
            LOGGER.debug("Configuration Exist in Path : "+Util.getUserDirectoryPath());
            configuration = DataHolder.getConfiguration();
            Map<String, Environment> configurationMap = configuration.getEnvironmentMap();
            if (configurationMap.containsKey(environmentName)) {
                LOGGER.warn("Environment " + environmentName + " Data Exists in the Configuration. \n Ignoring the given" +
                        " Configs");
                return;
            }
        } else {
            configuration = new Configuration();
        }
        generateClientRegistrationAppIfNotPresent(environment,false, enableVerboseOutput);
        configuration.getEnvironmentMap().put(environmentName, environment);
        Util.writeConfigurationIntoFile(configuration);
        LOGGER.info("Environment " + environmentName + " Added Successfully.");
    }

    private void generateClientRegistrationAppIfNotPresent(Environment environment,
                                                                  boolean verification, boolean verboseOutput)
            throws APIMigrationException {

        if (StringUtils.isEmpty(environment.getConsumerKey()) || StringUtils.isEmpty(environment.getConsumerSecret())) {
            DCRResponse dcrResponse =
                    null;
            try {
                dcrResponse = Util.registerAndRetrieveApplication(environmentName, environment, environment.getUsername(),
                                                                  environment.getPassword(), verification, verboseOutput);
                environment.setConsumerKey(dcrResponse.getClientId());
                environment.setConsumerSecret(dcrResponse.getClientSecret());
            } catch (KeyManagementException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

        }
    }


}
