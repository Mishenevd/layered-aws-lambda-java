package com.mishenev.post_book.db;

import com.amazonaws.util.json.Jackson;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Base64;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

/**
 * A simple factory-style class for the database interaction.
 * Could be useful for the following:
 * <ul>1. Encapsulate JDBC specific connection obtainment logic into the factory class.</ul>
 * <ul>2. Available to mock the DB related stuff out during the unit testing phase.</ul>
 *
 * @author Dmitrii_Mishenev
 */
public class JdbcConnectionFactory {

    /**
     * Factory constructor.
     * <li>Retrieves a DB connection properties from AWS Secrets Manager.</li>
     * <li>Initializes a DB connection.</li>
     */
    public JdbcConnectionFactory() {

    }

    private DbConnectionDetails retrieveConnectionDetails() {
        String environment = System.getenv("ENVIRONMENT");
        String secretName = "dev_library_postgres_creds";
        Region region = Region.of("eu-central-1");

        if (environment != null && environment.equals("LOCAL")) {
            final DbConnectionDetails localDetails = new DbConnectionDetails();
            localDetails.setHost("172.17.0.2");
            localDetails.setPort("5432");
            localDetails.setDbname("library_db");
            localDetails.setUsername("library_db_user");
            localDetails.setPassword("library_db_password");

            return localDetails;
        } else {

            // Create a Secrets Manager client
            final SecretsManagerClient client = SecretsManagerClient.builder()
                    .region(region)
                    .build();

            final GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                    .secretId(secretName)
                    .build();

            try (client) {
                final GetSecretValueResponse getSecretValueResponse = client.getSecretValue(getSecretValueRequest);

                // Decrypts secret using the associated KMS key.
                // Depending on whether the secret is a string or binary, one of these fields will be populated.
                if (getSecretValueResponse.secretString() != null) {
                    final String secretJson = getSecretValueResponse.secretString();
                    return Jackson.fromJsonString(secretJson, DbConnectionDetails.class);
                } else {
                    return Jackson.fromJsonString(
                            new String(Base64.getDecoder().decode(getSecretValueResponse.secretBinary().asByteBuffer()).array()),
                            DbConnectionDetails.class);
                }
            }
        }
    }

    private Connection openConnection(final DbConnectionDetails dbConnectionDetails) {
        final String url = "jdbc:postgresql://"
                + dbConnectionDetails.getHost()
                + ":"
                + dbConnectionDetails.getPort()
                + "/"
                + dbConnectionDetails.getDbname();

        try {
            return DriverManager.getConnection(url,
                    dbConnectionDetails.getUsername(),
                    dbConnectionDetails.getPassword());
        } catch (SQLException e) {
            throw new RuntimeException("Database connection error occurred", e);
        }
    }

    public Connection createConnection() {
        final DbConnectionDetails dbConnectionDetails = retrieveConnectionDetails();
        return openConnection(dbConnectionDetails);
    }
}