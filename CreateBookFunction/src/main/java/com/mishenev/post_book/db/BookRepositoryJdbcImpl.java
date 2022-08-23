package com.mishenev.post_book.db;

import com.mishenev.post_book.CreateBookRequest;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * BookRepositoryImpl.
 *
 * @author Dmitrii_Mishenev
 */
public class BookRepositoryJdbcImpl implements BookRepository, Closeable {

    private final Connection connection;

    public BookRepositoryJdbcImpl(Connection connection) {
        this.connection = connection;
    }

    public BookRepositoryJdbcImpl() {
        this.connection = new JdbcConnectionFactory().createConnection();
    }

    @Override
    public void createBook(CreateBookRequest createBookRequest) {
        final String sqlStatement = "INSERT INTO books (tittle, name, description) VALUES (?, ?, ?)";

        try(PreparedStatement insertBookStatement = connection.prepareStatement(sqlStatement)) {
            insertBookStatement.setString(1, createBookRequest.getTittle());
            insertBookStatement.setString(2, createBookRequest.getName());
            insertBookStatement.setString(3, createBookRequest.getDescription());
            insertBookStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }
}