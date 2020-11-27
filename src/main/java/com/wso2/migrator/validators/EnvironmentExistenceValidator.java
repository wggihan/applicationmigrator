package com.wso2.migrator.validators;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;
import com.wso2.migrator.exception.APIMigrationException;
import com.wso2.migrator.model.Configuration;
import com.wso2.migrator.util.DataHolder;
import com.wso2.migrator.util.Util;

public class EnvironmentExistenceValidator implements IParameterValidator {

    @Override
    public void validate(String name, String value) throws ParameterException {

        Configuration configuration = DataHolder.getConfiguration();
        if (!configuration.getEnvironmentMap().containsKey(value)) {
            throw new ParameterException("Environment " + value + " Not configured in the client");
        }
    }
}
