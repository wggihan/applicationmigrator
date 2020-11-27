package com.wso2.migrator.util;

public interface SQLConstants {
    String GET_APPLICATION_BY_ID_SQL =
            " SELECT " +
                    "   APP.APPLICATION_ID," +
                    "   APP.NAME," +
                    "   APP.SUBSCRIBER_ID," +
                    "   APP.APPLICATION_TIER," +
                    "   APP.CALLBACK_URL," +
                    "   APP.DESCRIPTION, " +
                    "   APP.SUBSCRIBER_ID," +
                    "   APP.APPLICATION_STATUS, " +
                    "   SUB.USER_ID, " +
                    "   APP.GROUP_ID," +
                    "   APP.UUID " +
                    " FROM " +
                    "   AM_SUBSCRIBER SUB," +
                    "   AM_APPLICATION APP " +
                    " WHERE " +
                    "   APPLICATION_ID = ? " +
                    "   AND APP.SUBSCRIBER_ID = SUB.SUBSCRIBER_ID";
    String GET_CONSUMER_KEYS_OF_APP = "SELECT * FROM AM_APPLICATION_KEY_MAPPING WHERE APPLICATION_ID = ?";

    String GET_OAUTH_APP_DETAILS_SQL = "SELECT * FROM IDN_OAUTH_CONSUMER_APPS WHERE CONSUMER_KEY = ?";
    String GET_APPLICATION_FROM_APPLICATION_OWNER =
            "SELECT APPLICATION_ID FROM AM_APPLICATION WHERE SUBSCRIBER_ID = (SELECT SUBSCRIBER_ID FROM AM_SUBSCRIBER" +
                    " WHERE TENANT_ID= ? AND USER_ID= ?) AND NAME = ?";

    String GET_APPLICATION_IDS_BY_TENANT_ID = "SELECT APPLICATION_ID FROM AM_APPLICATION WHERE SUBSCRIBER_ID IN " +
            "(SELECT SUBSCRIBER_ID FROM AM_SUBSCRIBER WHERE TENANT_ID = ?)";
}
