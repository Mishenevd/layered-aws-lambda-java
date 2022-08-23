package com.mishenev.post_book.db;


import com.mishenev.post_book.CreateBookRequest;

/**
 * Repository abstraction over the Book domain.
 * "Port" in terms of the Hexagonal architecture.
 *
 * @author Dmitrii_Mishenev
 */
public interface BookRepository {

    void createBook(CreateBookRequest createBookRequest);
}