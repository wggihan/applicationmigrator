package com.wso2.migrator.invoker.general;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.wso2.migrator.exception.APIMigrationException;
import com.wso2.migrator.model.Configuration;
import com.wso2.migrator.model.Environment;
import com.wso2.migrator.model.TenantDto;
import com.wso2.migrator.util.DataHolder;
import com.wso2.migrator.util.Util;
import com.wso2.migrator.validators.EnvironmentExistenceValidator;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Parameters(commandNames = "add-tenant", commandDescription = "Add Tenant To Environment")
public class AddTenantToEnvironment extends Helper {

    private static final Logger LOGGER = Logger.getLogger(AddTenantToEnvironment.class);

    @Parameter(names = {"--environment"}, description = "Name of Environment", descriptionKey = "dev", validateWith =
            EnvironmentExistenceValidator.class, required = true)
    private String environmentName;
    @Parameter(names = {"--tenant"}, description = "Name of tenant", descriptionKey = "dev", required = true)
    private String tenant;
    @Parameter(names = {"--tenant-admin-username"}, description = "Tenant Admin username", required = true)
    private String tenantAdminUsername;
    @Parameter(names = {"--tenant-admin-password"}, description = "Tenant Admin password", required = true)
    private String tenantAdminPassword;
    @Parameter(names = {"--userstores"}, description = "UserStore Names", variableArity = true)
    private List<String> userstores = new ArrayList<>();

    public void invoke() throws APIMigrationException {

        TenantDto tenantDto = new TenantDto();
        tenantDto.setTenantAdminUserName(tenantAdminUsername);
        tenantDto.setTenantAdminPassword(tenantAdminPassword);
        tenantDto.setUserstores(userstores);
        Configuration configuration = DataHolder.getConfiguration();
        Environment environment = configuration.getEnvironmentMap().get(environmentName);
        if (environment.getTenantDtoMap().containsKey(tenant)) {
            LOGGER.warn("Tenant " + tenant + " Already Exist in " + environmentName + " Environment,Updating the " +
                    "configuration");
            environment.getTenantDtoMap().remove(tenant);
        }
        environment.getTenantDtoMap().put(tenant, tenantDto);
        Util.writeConfigurationIntoFile(configuration);
        LOGGER.info("Tenant " + tenant + " Added to the " + environmentName + " Environment successfully.");
    }

}
