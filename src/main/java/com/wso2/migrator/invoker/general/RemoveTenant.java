package com.wso2.migrator.invoker.general;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.wso2.migrator.exception.APIMigrationException;
import com.wso2.migrator.model.Configuration;
import com.wso2.migrator.model.Environment;
import com.wso2.migrator.model.TenantDto;
import com.wso2.migrator.util.DataHolder;
import com.wso2.migrator.util.TableView;
import com.wso2.migrator.util.Util;
import com.wso2.migrator.validators.EnvironmentExistenceValidator;
import org.apache.log4j.Logger;

import java.util.Map;

@Parameters(commandNames = "remove-tenant", commandDescription = "Remove Tenant from Environment")
public class RemoveTenant extends Helper {

    @Parameter(names = {
            "--environment"}, description = "Name of Environment", descriptionKey = "dev", validateWith =
            EnvironmentExistenceValidator.class, required = true)
    private String environmentName;

    @Parameter(names = {
            "--tenant"}, description = "Name of Tenant to Remove", descriptionKey = "dev", required = true)
    private String tenantDomain;
    public static final Logger LOGGER = Logger.getLogger(RemoveTenant.class);

    public void invoke() throws APIMigrationException {

        Configuration configuration = DataHolder.getConfiguration();
        Environment environment = configuration.getEnvironmentMap().get(environmentName);
        if (environment.getTenantDtoMap().containsKey(tenantDomain)) {
            environment.getTenantDtoMap().remove(tenantDomain);
            Util.writeConfigurationIntoFile(configuration);
            LOGGER.info(
                    "Tenant Domain " + tenantDomain + " Deleted Successfully from " + environmentName + " Environment");
        } else {
            LOGGER.info("Tenant Domain " + tenantDomain + " Not configured in " + environmentName + " Environment.");
        }

    }

}
