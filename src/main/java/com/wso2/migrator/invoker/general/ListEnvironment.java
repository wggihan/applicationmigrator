package com.wso2.migrator.invoker.general;

import com.beust.jcommander.Parameters;
import com.wso2.migrator.exception.APIMigrationException;
import com.wso2.migrator.model.Configuration;
import com.wso2.migrator.model.Environment;
import com.wso2.migrator.util.DataHolder;
import com.wso2.migrator.util.TableView;
import com.wso2.migrator.util.Util;
import org.apache.log4j.Logger;

import java.util.Map;

@Parameters(commandNames = "list-env", commandDescription = "List Environment")
public class ListEnvironment extends Helper {

    public static final Logger LOGGER = Logger.getLogger(ListEnvironment.class);

    public void invoke() throws APIMigrationException {

        Configuration configuration = DataHolder.getConfiguration();
        Map<String, Environment> environmentMap = configuration.getEnvironmentMap();
        TableView tableView = new TableView();
        tableView.setHeaders("Name", "PUBLISHER ENDPOINT", "REGISTRATION ENDPOINT", "TOKEN ENDPOINT");
        environmentMap.forEach((name, environment) -> {
            tableView.addRow(name, environment.getApiImportExportEndpoint(),
                    environment.getDynamicClientRegistrationEndpoint(), environment.getTokenEndpoint());
        });
        if (environmentMap.isEmpty()) {
            System.out.println("No Environment configured");
        } else {
            System.out.println("Environments Available");
            tableView.print();
        }
    }

}
