package com.wso2.migrator.invoker.application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wso2.migrator.constants.Constants;
import com.wso2.migrator.exception.APIMigrationException;
import com.wso2.migrator.model.Configuration;
import com.wso2.migrator.util.DataHolder;
import com.wso2.migrator.util.SQLConstants;
import com.wso2.migrator.util.Util;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.model.Application;
import org.wso2.carbon.apimgt.api.model.OAuthApplicationInfo;
import org.wso2.carbon.apimgt.api.model.Subscriber;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ApplicationImportExportUtil {

    private static final Log LOG = LogFactory.getLog(ApplicationImportExportUtil.class.getName());

    private static Map<String, OAuthApplicationInfo> getOAuthApplications(Connection connection, int id)
            throws SQLException {

        Map<String, OAuthApplicationInfo> oAuthApplicationInfoMap = new HashMap<>();
        try (PreparedStatement preparedStatement = connection
                .prepareStatement(SQLConstants.GET_CONSUMER_KEYS_OF_APP)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String consumerKey = resultSet.getString("CONSUMER_KEY");
                    String type = resultSet.getString("KEY_TYPE");
                    OAuthApplicationInfo oAuthApplicationInfo = getOauthAppDetails(connection, consumerKey);
                    oAuthApplicationInfoMap.put(type, oAuthApplicationInfo);
                }
            }
        }

        return oAuthApplicationInfoMap;
    }

    private static OAuthApplicationInfo getOauthAppDetails(Connection connection, String consumerKey)
            throws SQLException {

        OAuthApplicationInfo oAuthApplicationInfo = new OAuthApplicationInfo();
        try (PreparedStatement preparedStatement = connection
                .prepareStatement(SQLConstants.GET_OAUTH_APP_DETAILS_SQL)) {
            preparedStatement.setString(1, consumerKey);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    oAuthApplicationInfo.setCallBackURL(resultSet.getString("CALLBACK_URL"));
                    oAuthApplicationInfo.setClientId(consumerKey);
                    byte[] consumerSecretBytes =
                            resultSet.getString("CONSUMER_SECRET").getBytes(Charset.defaultCharset());
                    oAuthApplicationInfo.setClientSecret(new String(Base64.encodeBase64(consumerSecretBytes)));
                    oAuthApplicationInfo.addParameter("grant_types", resultSet.getString("GRANT_TYPES"));
                    oAuthApplicationInfo.addParameter("redirect_uris", oAuthApplicationInfo.getCallBackURL());
                    oAuthApplicationInfo.addParameter("client_name", resultSet.getString("APP_NAME"));
                }
            }

        }
        return oAuthApplicationInfo;
    }

    private static List<Integer> getApplicationIds(String environmentName, int tenantId) throws APIMigrationException {

        HikariDataSource dataSource = DataHolder.getAPIMgtDataSource(environmentName);
        List<Integer> applicationIds = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement preparedStatement =
                         connection.prepareStatement(SQLConstants.GET_APPLICATION_IDS_BY_TENANT_ID)) {
                preparedStatement.setInt(1, tenantId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        applicationIds.add(resultSet.getInt("APPLICATION_ID"));
                    }
                }
            }
        } catch (SQLException e) {
            throw new APIMigrationException("Error while retrieving Applications from TENANT", e);
        }
        return applicationIds;
    }

    private static Application getApplicationDetails(String environmentName, int applicationId) throws SQLException {

        HikariDataSource dataSource = DataHolder.getAPIMgtDataSource(environmentName);
        Application application = null;
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection
                    .prepareStatement(SQLConstants.GET_APPLICATION_BY_ID_SQL)) {
                preparedStatement.setInt(1, applicationId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String applicationName = resultSet.getString("NAME");
                        String subscriberId = resultSet.getString("SUBSCRIBER_ID");
                        String subscriberName = resultSet.getString("USER_ID");

                        Subscriber subscriber = new Subscriber(subscriberName);
                        subscriber.setId(Integer.parseInt(subscriberId));
                        application = new Application(applicationName, subscriber);

                        application.setDescription(resultSet.getString("DESCRIPTION"));
                        application.setStatus(resultSet.getString("APPLICATION_STATUS"));
                        application.setCallbackUrl(resultSet.getString("CALLBACK_URL"));
                        application.setId(resultSet.getInt("APPLICATION_ID"));
                        application.setGroupId(resultSet.getString("GROUP_ID"));
                        application.setUUID(resultSet.getString("UUID"));
                        application.setTier(resultSet.getString("APPLICATION_TIER"));
                        subscriber.setId(resultSet.getInt("SUBSCRIBER_ID"));

                        Map<String, OAuthApplicationInfo> keyMap = getOAuthApplications(connection, applicationId);
                        for (Map.Entry<String, OAuthApplicationInfo> entry : keyMap.entrySet()) {
                            application.addOAuthApp(entry.getKey(), "Resident Key Manager", entry.getValue());
                        }
                    }
                }

            }
        }
        return application;
    }

    private static void writeApplicationDetailsToFileSystem(Application application, String path)
            throws IOException {

        String applicationFileLocation = path + File.separator + application.getName() + ".json";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileOutputStream fileOutputStream = new FileOutputStream(applicationFileLocation)) {
            try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream,
                                                                                StandardCharsets.UTF_8)) {
                gson.toJson(application, outputStreamWriter);
                LOG.info("Writing Application details into file in path: " + applicationFileLocation);
            }
        }
    }

    /**
     * Creates a zip archive from of a directory
     *
     * @param sourceDirectory directory to create zip archive from
     * @param archiveLocation path to the archive location, excluding archive name
     * @param archiveName     name of the archive to create
     * @throws IOException if an error occurs while creating the archive
     */
    private static void archiveDirectory(Path sourceDirectory, String archiveLocation, String archiveName)
            throws IOException {

        File directoryToZip = sourceDirectory.toFile();
        List<File> fileList = new ArrayList<>();
        getAllFiles(directoryToZip, fileList);
        writeArchiveFile(directoryToZip, fileList, archiveLocation, archiveName);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Archive generated successfully " + archiveName);
        }
    }

    /**
     * Queries all files under a directory recursively
     *
     * @param sourceDirectory full path to the root directory
     * @param fileList        list containing the files
     */
    private static void getAllFiles(File sourceDirectory, List<File> fileList) {

        File[] files = sourceDirectory.listFiles();
        if (files != null) {
            for (File file : files) {
                fileList.add(file);
                if (file.isDirectory()) {
                    getAllFiles(file, fileList);
                }
            }
        }
    }

    /**
     * @param directoryToZip  directory to create zip archive
     * @param fileList        list of files
     * @param archiveLocation path to the archive location, excluding archive name
     * @param archiveName     name of the archive to create
     * @throws IOException if an error occurs while writing to the archive file
     */
    private static void writeArchiveFile(File directoryToZip, List<File> fileList, String archiveLocation,
                                         String archiveName) throws IOException {

        try (FileOutputStream fileOutputStream = new FileOutputStream(archiveLocation + File.separator +
                                                                              archiveName + ".zip");
             ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream)) {
            for (File file : fileList) {
                if (!file.isDirectory()) {
                    addToArchive(directoryToZip, file, zipOutputStream);
                }
            }
        }
    }

    /**
     * @param directoryToZip  directory to create zip archive
     * @param file            file to archive
     * @param zipOutputStream output stream of the written archive file
     * @throws IOException if an error occurs while adding file to archive
     */
    private static void addToArchive(File directoryToZip, File file, ZipOutputStream zipOutputStream)
            throws IOException {
        // Add a file to archive
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            // Get relative path from archive directory to the specific file
            String zipFilePath = file.getCanonicalPath()
                    .substring(directoryToZip.getCanonicalPath().length() + 1, file.getCanonicalPath().length());
            if (File.separatorChar != '/') {
                zipFilePath = zipFilePath.replace(File.separatorChar, '/');
            }
            ZipEntry zipEntry = new ZipEntry(zipFilePath);
            zipOutputStream.putNextEntry(zipEntry);
            IOUtils.copy(fileInputStream, zipOutputStream);
            zipOutputStream.closeEntry();
        }
    }

    public static Map<String, Boolean> createApplicationArchives(String source, String target,
                                                                 Configuration configuration)
            throws APIMigrationException {

        Map<String, Boolean> archiveMap = new HashMap<>();
        int exportedTenantId = -1234;
        int importedTenantId = -1234;

        String tenantApplicationExPortPath =
                Paths.get(Util.getUserDirectoryPath(), source, Constants.EXPORTD_APPS_DIRECTORY,
                          String.valueOf(exportedTenantId)).toString();
        try {
            FileUtils.deleteDirectory(Paths.get(Util.getUserDirectoryPath(), source, Constants.EXPORTD_APPS_DIRECTORY,
                                                String.valueOf(exportedTenantId)).toFile());
            Files.createDirectories(Paths.get(Util.getUserDirectoryPath(), source, Constants.EXPORTD_APPS_DIRECTORY,
                                              String.valueOf(exportedTenantId)));
            List<Integer> applicationIds = getApplicationIds(source, exportedTenantId);

            for (Integer applicationId : applicationIds) {
                try {
                    LOG.info("Fetch application details on " + applicationId);
                    Application applicationDetails = getApplicationDetails(source, applicationId);
                    if (applicationDetails != null) {
                        applicationDetails.setTokenType("DEFAULT");
                        applicationDetails.setOwner(applicationDetails.getSubscriber().getName());
                        String achieveName =
                                applicationDetails.getOwner().concat("_").concat(applicationDetails.getName());
                        Path applicationDirectory = Paths.get(tenantApplicationExPortPath, achieveName,
                                                              applicationDetails.getName());
                        Files.createDirectories(applicationDirectory);
                        writeApplicationDetailsToFileSystem(applicationDetails, applicationDirectory.toString());
                        archiveDirectory(Paths.get(tenantApplicationExPortPath, achieveName),
                                         tenantApplicationExPortPath, achieveName);
                        FileUtils.deleteDirectory(Paths.get(tenantApplicationExPortPath, achieveName).toFile());
                        LOG.info("Application " + applicationDetails.getOwner().concat("--")
                                .concat(applicationDetails.getName()) + " subscriptions and keys Exported " +
                                         "Successfully");
                        if (!Util.isApplicationExistInTargetEnvironment(target,
                                                                        applicationDetails.getSubscriber().getName(),
                                                                        applicationDetails.getName(),
                                                                        importedTenantId)) {
                            archiveMap.put(achieveName, false);
                        } else {
                            archiveMap.put(achieveName, true);
                            LOG.info("Updating Application " + applicationDetails.getOwner().concat("--")
                                    .concat(applicationDetails.getName()) +
                                             " Subscriptions and keys due to already exist");
                        }
                    }
                } catch (SQLException | IOException e) {
                    LOG.error("Error while creating export achieve for applicationId:" + applicationId, e);
                }
            }
        } catch (APIMigrationException | IOException e) {
            throw new APIMigrationException("Error while exporting application details", e);
        }
        return archiveMap;
    }
}
