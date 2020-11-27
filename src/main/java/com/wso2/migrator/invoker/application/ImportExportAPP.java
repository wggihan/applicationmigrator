package com.wso2.migrator.invoker.application;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.wso2.migrator.exception.APIMigrationException;
import com.wso2.migrator.invoker.general.Helper;
import com.wso2.migrator.model.Configuration;
import com.wso2.migrator.model.Environment;
import com.wso2.migrator.util.DataHolder;
import com.wso2.migrator.util.Util;
import com.wso2.migrator.validators.EnvironmentExistenceValidator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.Map;

@Parameters(commandNames = "import-export-app")
public class ImportExportAPP extends Helper {

    public static final Logger LOGGER = Logger.getLogger(ImportExportAPP.class);
    @Parameter(names = "--source", description = "Source Environment", validateWith =
            EnvironmentExistenceValidator.class, required = true, order = 0)
    private String sourceEnvironment;
    @Parameter(names = "--target", description = "Target Environment", validateWith =
            EnvironmentExistenceValidator.class, required = true, order = 1)
    private String targetEnvironment;
    @Parameter(names = {"--insecure", "-k"}, description = "Insecure Connection")
    private boolean hostnameVerification;
    @Parameter(names = {"--verbose", "-v"}, description = "Verbose output of Log")
    private boolean enableVerboseOutput;

    public void invoke() throws APIMigrationException {

        if (enableVerboseOutput) {
            Logger.getRootLogger().setLevel(Level.DEBUG);
        }
        Configuration configuration = DataHolder.getConfiguration();
        Environment environment = configuration.getEnvironmentMap().get(targetEnvironment);
        String tokenForImportEnvironment =
                Util.generateTokenForEnvironment(targetEnvironment, configuration, environment.getUsername(),
                                                 environment.getPassword(), hostnameVerification, enableVerboseOutput);
        Map<String, Boolean> applicationArchives =
                ApplicationImportExportUtil.createApplicationArchives(
                        sourceEnvironment, targetEnvironment, configuration);
        Util.importApplications(sourceEnvironment, targetEnvironment, tokenForImportEnvironment, configuration,
                                applicationArchives, hostnameVerification, enableVerboseOutput);
    }

}
