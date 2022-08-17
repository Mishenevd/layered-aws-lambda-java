package com.mishenev.count_book.db

import java.io.Closeable
import java.io.IOException
import java.sql.Connection
import java.sql.SQLException

class BookRepositoryJdbcImpl : BookRepository, Closeable {
    private val connection: Connection

    constructor(connection: Connection) {
        this.connection = connection
    }

    constructor() {
        connection = JdbcConnectionFactory().createConnection()
    }

    override fun countBooksByName(name: String): Long {
        val sqlStatement = "SELECT COUNT(*)  AS recordCount FROM books WHERE name = ?"

         try {
            connection.prepareStatement(sqlStatement)
                .use { insertBookStatement ->
                    insertBookStatement.setString(1, name)
                    val resultSet = insertBookStatement.executeQuery()
                    resultSet.next()
                    return resultSet.getLong("recordCount")
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