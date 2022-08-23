package com.mishenev.create_author.db

import com.mishenev.create_author.AuthorCreatedEvent
import com.mishenev.create_author.CreateAuthorEvent
import java.io.Closeable
import java.io.IOException
import java.sql.Connection
import java.sql.SQLException

class AuthorRepositoryJdbcImpl : AuthorRepository, Closeable {
    private val connection: Connection

    constructor(connection: Connection) {
        this.connection = connection
    }

    constructor() {
        connection = JdbcConnectionFactory().createConnection()
    }

    override fun createNewAuthor(createAuthorEvent: CreateAuthorEvent): AuthorCreatedEvent {
        val sqlStatement = "INSERT INTO authors (name, surname) VALUES (?, ?)"

         try {
            connection.prepareStatement(sqlStatement)
                .use { insertBookStatement ->
                    insertBookStatement.setString(1, createAuthorEvent.name)
                    insertBookStatement.setString(2, createAuthorEvent.surname)
                    val resultSet = insertBookStatement.executeQuery()
                    resultSet.next()
                    with(resultSet) {
                        return AuthorCreatedEvent(
                            getLong("id"),
                            getString("name"),
                            getString("surname"))

                    }
                }
        } catch (e: SQLException) {
            throw RuntimeException(e)
        }
    }

    @Throws(IOException::class)
    override fun close() {
        try {
            connection.close()
        } catch (e: SQLException) {
            throw IOException(e)
        }
    }
}