package com.wso2.migrator.invoker.general;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.wso2.migrator.exception.APIMigrationException;
import com.wso2.migrator.model.Configuration;
import com.wso2.migrator.model.Environment;
import com.wso2.migrator.model.TenantDto;
import com.wso2.migrator.util.DataHolder;
import com.wso2.migrator.util.TableView;
import com.wso2.migrator.validators.EnvironmentExistenceValidator;
import org.apache.log4j.Logger;

import java.util.Map;

@Parameters(commandNames = "list-tenants", commandDescription = "List Tenants")
public class ListTenants extends Helper {

    @Parameter(names = {
            "--environment"}, description = "Name of Environment", descriptionKey = "dev", validateWith =
            EnvironmentExistenceValidator.class, required = true)
    private String environmentName;

    public static final Logger LOGGER = Logger.getLogger(ListTenants.class);

    public void invoke() throws APIMigrationException {

        Configuration configuration = DataHolder.getConfiguration();
        Environment environment = configuration.getEnvironmentMap().get(environmentName);
        Map<String, TenantDto> tenantDtoMap = environment.getTenantDtoMap();
        TableView tableView = new TableView();
        tableView.setHeaders("Tenant Domain", "Tenant Admin UserName", "Tenant Admin Password", "UserStore Domains");
        tenantDtoMap.forEach((tenantDomain, tenantDto) -> {
            tableView.addRow(tenantDomain, tenantDto.getTenantAdminUserName(),
                    tenantDto.getTenantAdminPassword(), String.join(" , ", tenantDto.getUserstores()));
        });
        if (tenantDtoMap.isEmpty()) {
            LOGGER.info("No Tenant Domains Configured");
        } else {
            LOGGER.info("Tenant Domains Available in Environment : " + environmentName);
            tableView.print();
        }
    }

}
