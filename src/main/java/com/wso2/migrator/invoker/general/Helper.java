package com.wso2.migrator.invoker.general;

import com.beust.jcommander.Parameter;
import com.wso2.migrator.exception.APIMigrationException;

public class Helper {

    @Parameter(names = {"--help", "-h"}, help = true)
    private boolean help = false;

    public boolean isHelp() {

        return help;
    }
    public void invoke() throws APIMigrationException {

    }
}
