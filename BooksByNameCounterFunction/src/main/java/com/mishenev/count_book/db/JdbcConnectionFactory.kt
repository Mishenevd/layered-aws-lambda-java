package com.mishenev.count_book.db

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*

data class DbConnectionDetails(val username: String, val password: String, val engine: String,
                               val host: String, val port: String,
                               val dbname: String)

class JdbcConnectionFactory {

    fun createConnection(): Connection {
        val dbConnectionDetails: DbConnectionDetails = retrieveConnectionDetails()
        return openConnection(dbConnectionDetails)
    }

    private fun retrieveConnectionDetails(): DbConnectionDetails {
        val environment = System.getenv("ENVIRONMENT")
        val secretName = "dev_library_postgres_creds"
        val region = Region.of("eu-central-1")
        if ((environment != null) and (environment == "LOCAL")) {
            return DbConnectionDetails(
                "library_db_user",
                "library_db_password",
                "postgres",
                "172.17.0.2",
                "5432",
                "library_db"
            )
        } else {

            // Create a Secrets Manager client
            val client = SecretsManagerClient.builder()
                .region(region)
                .build()
            val getSecretValueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build()
            client.use {
                val getSecretValueResponse = client.getSecretValue(getSecretValueRequest)

                val mapper = jacksonObjectMapper()

                // Decrypts secret using the associated KMS key.
                // Depending on whether the secret is a string or binary, one of these fields will be populated.
                return if (getSecretValueResponse.secretString() != null) {
                    val secretJson = getSecretValueResponse.secretString()
                    mapper.readValue(
                        secretJson,
                        DbConnectionDetails::class.java
                    )
                } else {
                    mapper.readValue(
                        String(
                            Base64.getDecoder()
                                .decode(getSecretValueResponse.secretBinary().asByteBuffer())
                                .array()
                        ),
                        DbConnectionDetails::class.java
                    )
                }
            }
        }
    }

    private fun openConnection(dbConnectionDetails: DbConnectionDetails): Connection {
        with(dbConnectionDetails) {
            val url = "jdbc:postgresql://$host:$port/$dbname"

            return try {
                DriverManager.getConnection(url, username, password)
            } catch (e: SQLException) {
                throw RuntimeException("Database connection error occurred", e)
            }
        }
    }
}