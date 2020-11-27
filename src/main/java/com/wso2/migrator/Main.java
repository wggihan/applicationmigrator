package com.wso2.migrator;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.wso2.migrator.constants.Constants;
import com.wso2.migrator.exception.APIMigrationException;
import com.wso2.migrator.invoker.application.ImportExportAPP;
import com.wso2.migrator.invoker.general.AddEnvironment;
import com.wso2.migrator.invoker.general.AddTenantToEnvironment;
import com.wso2.migrator.invoker.general.Helper;
import com.wso2.migrator.invoker.general.ListEnvironment;
import com.wso2.migrator.invoker.general.ListTenants;
import com.wso2.migrator.invoker.general.RemoveEnvironment;
import com.wso2.migrator.invoker.general.RemoveTenant;
import com.wso2.migrator.model.Configuration;
import com.wso2.migrator.util.DataHolder;
import com.wso2.migrator.util.Util;
import com.zaxxer.hikari.HikariDataSource;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

@SuppressFBWarnings("DM_EXIT")

public class Main extends Helper {

    public static final Logger LOGGER = Logger.getLogger(Main.class);

    public static void main(String[] args) {

        try {
            Util.writeKeyStoreFileIntoHomeDirectory();
            Configuration configuration = Util.readConfigurations();
            DataHolder.setConfiguration(configuration);
            setupDataSources();
        } catch (APIMigrationException e) {
            LOGGER.error(e);
            System.out.println(e.getMessage());
            System.exit(1);
        }
        AddEnvironment addEnvironment = new AddEnvironment();
        RemoveEnvironment removeEnvironment = new RemoveEnvironment();
        ListEnvironment listEnvironment = new ListEnvironment();
        ImportExportAPP importExportAPP = new ImportExportAPP();
        AddTenantToEnvironment addTenantToEnvironment = new AddTenantToEnvironment();
        ListTenants listTenants = new ListTenants();
        RemoveTenant removeTenant = new RemoveTenant();
        JCommander jCommander = new JCommander();
        jCommander.addCommand(Constants.ADD_ENVIRONMENT, addEnvironment);
        jCommander.addCommand(Constants.REMOVE_ENVIRONMENT, removeEnvironment);
        jCommander.addCommand(Constants.LIST_ENVIRONMENT, listEnvironment);
        jCommander.addCommand(Constants.IMPORT_EXPORT_APP_ACTION, importExportAPP);
        jCommander.addCommand(Constants.ADD_TENANT, addTenantToEnvironment);
        jCommander.addCommand(Constants.LIST_TENANTS, listTenants);
        jCommander.addCommand(Constants.REMOVE_TENANT,removeTenant);
        try {
            jCommander.parse(args);
        } catch (ParameterException e) {
            LOGGER.error(e);
            System.out.println(e.getMessage());
            System.exit(1);
        }
        if (StringUtils.isEmpty(jCommander.getParsedCommand())) {
            jCommander.usage();
            return;
        }
        switch (jCommander.getParsedCommand()) {
            case Constants.ADD_ENVIRONMENT:
                invokeUsage(jCommander, addEnvironment);
                invokeAction(addEnvironment);
                break;
            case Constants.REMOVE_ENVIRONMENT:
                invokeUsage(jCommander, removeEnvironment);
                invokeAction(removeEnvironment);
                break;
            case Constants.LIST_ENVIRONMENT:
                invokeUsage(jCommander, listEnvironment);
                invokeAction(listEnvironment);
                break;
            case Constants.IMPORT_EXPORT_APP_ACTION:
                invokeUsage(jCommander, importExportAPP);
                invokeAction(importExportAPP);
                break;
            case Constants.ADD_TENANT:
                invokeUsage(jCommander, addTenantToEnvironment);
                invokeAction(addTenantToEnvironment);
                break;
            case Constants.LIST_TENANTS:
                invokeUsage(jCommander, listTenants);
                invokeAction(listTenants);
                break;
            case Constants.REMOVE_TENANT:
                invokeUsage(jCommander, removeTenant);
                invokeAction(removeTenant);
                break;
            default:
                break;
        }
    }

    private static void invokeUsage(JCommander commander, Helper object) {

        if (object.isHelp()) {
            commander.usage(commander.getParsedCommand());
            System.exit(0);
        }
    }

    private static void invokeAction(Helper helper) {

        try {
            helper.invoke();
        } catch (APIMigrationException e) {
            LOGGER.error(e);
            System.out.println("Error occurred.......");
            System.out.println(e);
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    private static void setupDataSources() {

        Configuration configuration = DataHolder.getConfiguration();
        configuration.getEnvironmentMap().forEach((name, environment) -> {
            if (StringUtils.isNotEmpty(environment.getApimgtDbUrl()) &&
                    StringUtils.isNotEmpty(environment.getApimgtDbUser()) &&
                    StringUtils.isNotEmpty(environment.getApimgtdbPassword())) {
                HikariDataSource hikariDataSource = Util.getDatasource(environment.getApimgtDbUrl(),
                        environment.getApimgtDbUser(), environment.getApimgtdbPassword());
                DataHolder.addAPIMgtDataSource(name, hikariDataSource);
            }

        });
    }
}
