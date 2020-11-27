package com.wso2.migrator.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.wso2.migrator.constants.Constants;
import com.wso2.migrator.encorders.FormEncoder;
import com.wso2.migrator.exception.APIMigrationException;
import com.wso2.migrator.interceptors.BearerHeaderInterceptor;
import com.wso2.migrator.invoker.application.serviceproxy.ImportAPP;
import com.wso2.migrator.invoker.general.serviceproxy.DCRMService;
import com.wso2.migrator.invoker.general.serviceproxy.Oauth2Service;
import com.wso2.migrator.model.Configuration;
import com.wso2.migrator.model.DCRRequest;
import com.wso2.migrator.model.DCRResponse;
import com.wso2.migrator.model.Environment;
import com.wso2.migrator.model.OAuth2TokenInfo;
import com.wso2.migrator.model.TrustStore;
import com.zaxxer.hikari.HikariDataSource;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import feign.Client;
import feign.Feign;
import feign.Response;
import feign.auth.BasicAuthRequestInterceptor;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * Utility class for API Migrator
 */
public class Util {

    private static final Logger LOGGER = Logger.getLogger(Util.class);

    private static Configuration configuration;

    /**
     * This method used to read the configuration under migrator home.
     *
     * @return
     * @throws APIMigrationException
     */
    public static Configuration readConfigurations() throws APIMigrationException {

        String configYamlPath = Paths.get(getUserDirectoryPath(), Constants.CONFIGURATION_FILE).toString();
        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            configuration = mapper.readValue(new File(configYamlPath), Configuration.class);
            return configuration;
        } catch (FileNotFoundException e) {
            throw new APIMigrationException("Configuration file not found at: " + configYamlPath, e);
        } catch (JsonParseException e) {
            LOGGER.error("Error while parsing configuration", e);
            throw new APIMigrationException("Error while parsing configuration", e);
        } catch (JsonMappingException e) {
            LOGGER.error("Error while mapping configuration", e);
            throw new APIMigrationException("Error while mapping configuration", e);
        } catch (IOException e) {
            LOGGER.error("Error while reading configuration", e);
            throw new APIMigrationException("Error while reading configuration", e);
        }
    }

    public static boolean configurationExist() {

        File file = Paths.get(getUserDirectoryPath(), Constants.CONFIGURATION_FILE).toFile();
        return file.exists();
    }

    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
    public static void writeConfigurationIntoFile(Configuration configuration) throws APIMigrationException {

        File file = Paths.get(getUserDirectoryPath(), Constants.CONFIGURATION_FILE).toFile();
        file.getParentFile().mkdirs();
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            objectMapper.writeValue(file, configuration);
        } catch (IOException e) {
            throw new APIMigrationException("Error while Writing Configuration", e);
        }

    }

    public static String getUserDirectoryPath() {

        if (System.getProperty(Constants.MIGRATOR_HOME) != null) {
            return System.getProperty(Constants.MIGRATOR_HOME);
        }
        return Paths.get(System.getProperty("user.home"), Constants.MIGRATOR_TOOL_CONFIG_FOLDER).toString();
    }

    public static DCRResponse registerAndRetrieveApplication(String environmentName, Environment environment,
                                                             String username, String password,
                                                             boolean hostnameVerification, boolean verbose)
            throws APIMigrationException, KeyManagementException, NoSuchAlgorithmException {

        HostnameVerifier hostnameVerifier = new HOSTNameVerifierImpl(hostnameVerification);
        feign.Logger.Level logLevel = verbose ? feign.Logger.Level.FULL : feign.Logger.Level.NONE;

        DCRMService dcrmService = Feign.builder()
                .requestInterceptor(new BasicAuthRequestInterceptor(username, password)).encoder(new GsonEncoder())
                .client(new Client.Default(getClientSSLSocketFactory(configuration.getTrustStore()), hostnameVerifier))
                .logLevel(logLevel)
                .logger(new Slf4jLogger())
                .decoder(new GsonDecoder()).target(DCRMService.class,
                                                   environment.getDynamicClientRegistrationEndpoint());
        DCRRequest dcrRequest = new DCRRequest(Constants.API_MIGRATOR_CLIENT_NAME.concat("-").concat(environmentName)
                , username);
        Response response = dcrmService.registerApplication(dcrRequest);
        if (response.status() == Constants.HTTP_STATUS_OK) {  //201 - Success
            try {
                return (DCRResponse) new GsonDecoder().decode(response, DCRResponse.class);
            } catch (IOException e) {
                throw new APIMigrationException("Error while decoding response", e);
            }
        } else {
            try {
                InputStream inputStream = response.body().asInputStream();
                final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    LOGGER.error(line);
                }
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            LOGGER.error("Status Code:" + response.status() + "\nResponse:" + response.body().toString());
            throw new APIMigrationException("Error while Creating Application");
        }
    }

    public static OAuth2TokenInfo generateToken(Environment environment, String username, String password,
                                                boolean verification, boolean verbose) throws APIMigrationException {

        feign.Logger.Level logLevel = verbose ? feign.Logger.Level.FULL : feign.Logger.Level.NONE;

        HostnameVerifier hostnameVerifier = new HOSTNameVerifierImpl(verification);
        Oauth2Service oauth2Service = Feign.builder()
                .requestInterceptor(new BasicAuthRequestInterceptor(environment.getConsumerKey(),
                                                                    environment.getConsumerSecret())).encoder(
                        new GsonEncoder())
                .logLevel(logLevel)
                .logger(new Slf4jLogger())
                .client(new Client.Default(getClientSSLSocketFactory(configuration.getTrustStore()), hostnameVerifier))
                .encoder(new FormEncoder()).decoder(new GsonDecoder()).target(Oauth2Service.class,
                                                                              environment.getTokenEndpoint());
        Response response = oauth2Service.generateAccessToken(Constants.PASSWORD_GRANT_TYPE, username, password,
                                                              Constants.PUBLISHER_SCOPES);
        if (response.status() == Constants.HTTP_STATUS_OK) {  //200 - Success
            try {
                return (OAuth2TokenInfo) new GsonDecoder().decode(response, OAuth2TokenInfo.class);
            } catch (IOException e) {
                throw new APIMigrationException("Error while decoding response", e);
            }
        } else if (response.status() == 401) {
            throw new APIMigrationException("User " + username + " Not Authorized to Environment");
        } else {
            LOGGER.error("Status Code:" + response.status() + "\nResponse:" + response.body().toString());
            throw new APIMigrationException("Error while Generating Token");
        }
    }

    public static SSLSocketFactory getClientSSLSocketFactory(TrustStore trustStore)
            throws APIMigrationException {

        try {

            // This supports TLSv1.2
            SSLContext sslContext = SSLContext.getInstance("TLS");

            KeyStore kStore = KeyStore.getInstance(KeyStore.getDefaultType());

            try (FileInputStream file = new FileInputStream(trustStore.getLocation())) {
                kStore.load(file, trustStore.getPassword().toCharArray());
            }

            TrustManagerFactory tmf = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(kStore);

            sslContext.init(new KeyManager[]{}, tmf.getTrustManagers(), null);
            return sslContext.getSocketFactory();
        } catch (CertificateException | NoSuchAlgorithmException | IOException | KeyManagementException | KeyStoreException e) {
            throw new APIMigrationException("Error while initializing Truststore", e);
        }

    }

    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
    public static void writeKeyStoreFileIntoHomeDirectory() throws APIMigrationException {

        if (!configurationExist()) {
            Configuration configuration = new Configuration();
            TrustStore trustStore = new TrustStore();
            trustStore.setLocation(Paths.get(getUserDirectoryPath(), Constants.CLIENT_TRUST_STORE_JKS_FILE).toString());
            configuration.setTrustStore(trustStore);
            try (InputStream inputStream =
                         ClassLoader.getSystemClassLoader()
                                 .getResourceAsStream(Constants.CLIENT_TRUST_STORE_JKS_FILE)) {
                Paths.get(getUserDirectoryPath()).toFile().mkdirs();
                Files.copy(inputStream, Paths.get(trustStore.getLocation()), StandardCopyOption.REPLACE_EXISTING);
                Util.writeConfigurationIntoFile(configuration);
            } catch (IOException e) {
                throw new APIMigrationException("Error while copying the truststore File", e);
            }

        }
    }

    public static String generateTokenForEnvironment(String environmentName, Configuration configuration,
                                                     String username,
                                                     String password, boolean verification, boolean verboseOutput)
            throws APIMigrationException {

        Environment environment = configuration.getEnvironmentMap().get(environmentName);
        OAuth2TokenInfo oAuth2TokenInfo = generateToken(environment, username, password, verification,
                                                        verboseOutput);
        return oAuth2TokenInfo.getAccessToken();
    }

    public static void importApplications(String source, String target, String tokenForImportEnvironment,
                                          Configuration configuration,
                                          Map<String, Boolean> applicationArchives, boolean hostnameVerification,
                                          boolean enableVerboseOutput) throws APIMigrationException {

        Environment targetEnvironment = configuration.getEnvironmentMap().get(target);
        int exportedTenantId = -1234;
        String tenantApplicationExPortPath =
                Paths.get(Util.getUserDirectoryPath(), source, Constants.EXPORTD_APPS_DIRECTORY,
                          String.valueOf(exportedTenantId)).toString();
        for (Map.Entry<String, Boolean> applicationArchieveEntry : applicationArchives.entrySet()) {
            Path archievePath =
                    Paths.get(tenantApplicationExPortPath, applicationArchieveEntry.getKey().concat(".zip"));
            try {
                importAPPToEnvironment(targetEnvironment, tokenForImportEnvironment, archievePath.toFile(),
                                       applicationArchieveEntry.getValue().booleanValue(),
                                       applicationArchieveEntry.getValue().booleanValue(), hostnameVerification,
                                       enableVerboseOutput);
            } catch (APIMigrationException e) {
                LOGGER.error("Error while Exporting Application at archieve " + archievePath);
            }
        }
    }

    /**
     * This class used to disable Hostname Verification
     */
    private static class HOSTNameVerifierImpl implements HostnameVerifier {

        private boolean disableHostnameVerification;

        public HOSTNameVerifierImpl(boolean disableHostnameVerification) {

            this.disableHostnameVerification = disableHostnameVerification;
        }

        @Override
        public boolean verify(String s, SSLSession sslSession) {

            return disableHostnameVerification;
        }

    }

    public static boolean isApplicationExistInTargetEnvironment(String targetEnvironmentName, String username,
                                                                String applicationName, int tenantId)
            throws APIMigrationException {

        HikariDataSource dataSource = DataHolder.getAPIMgtDataSource(targetEnvironmentName);
        if (dataSource == null) {
            throw new APIMigrationException("API Manager Database configuration not given to target environment");
        }
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection
                    .prepareStatement(SQLConstants.GET_APPLICATION_FROM_APPLICATION_OWNER)) {
                preparedStatement.setInt(1, tenantId);
                preparedStatement.setString(2, username);
                preparedStatement.setString(3, applicationName);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            throw new APIMigrationException("Error while creating connection", e);
        }
    }

    public static void importAPPToEnvironment(Environment targetEnvironment, String accessToken, File importFile,
                                              boolean update, boolean skipApplicationKeys, boolean verification,
                                              boolean verbose)
            throws APIMigrationException {

        feign.Logger.Level logLevel = verbose ? feign.Logger.Level.FULL : feign.Logger.Level.NONE;

        HostnameVerifier hostnameVerifier = new HOSTNameVerifierImpl(verification);

        ImportAPP importExportAPI = Feign.builder()
                .requestInterceptor(new BearerHeaderInterceptor(accessToken))
                .logLevel(logLevel)
                .logger(new Slf4jLogger())
                .client(new Client.Default(getClientSSLSocketFactory(configuration.getTrustStore()), hostnameVerifier))
                .encoder(new feign.form.FormEncoder(new JacksonEncoder()))
                .target(ImportAPP.class, targetEnvironment.getAdminRestAPIEndpoint());
        Response response = importExportAPI.importApp(importFile, update, skipApplicationKeys);
        if (response.status() == Constants.HTTP_STATUS_CREATED) {
            LOGGER.info("Application IMPORTED SuccessFully");
        } else {
            throw new APIMigrationException("Error while Importing Application");
        }
    }

    public static HikariDataSource getDatasource(String url, String username, String password) {

        HikariDataSource ds = new HikariDataSource();

        ds.setJdbcUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setAutoCommit(true);
        ds.setMaxLifetime(30000);
        ds.setIdleTimeout(30000);
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        return ds;
    }

}
