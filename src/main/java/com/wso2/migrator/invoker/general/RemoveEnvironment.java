package com.wso2.migrator.invoker.general;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.wso2.migrator.exception.APIMigrationException;
import com.wso2.migrator.model.Configuration;
import com.wso2.migrator.util.DataHolder;
import com.wso2.migrator.util.Util;
import com.wso2.migrator.validators.EnvironmentExistenceValidator;
import org.apache.log4j.Logger;

@Parameters(commandNames = "remove-env", commandDescription = "Remove Environment")
public class RemoveEnvironment extends Helper {

    public static final Logger log = Logger.getLogger(AddEnvironment.class);

    @Parameter(names = {"--environment"}, description = "Name of Environment", descriptionKey = "dev", validateWith = EnvironmentExistenceValidator.class, required = true)
    private String environmentName;

    public void invoke() throws APIMigrationException {

        Configuration configuration = DataHolder.getConfiguration();
        configuration.getEnvironmentMap().remove(environmentName);
        Util.writeConfigurationIntoFile(configuration);
        log.info("Environment " + environmentName + " Successfully removed");
    }

}
